package service

import (
	"context"
	"eduport-queue/domain"
	"encoding/json"
	"errors"
	"fmt"
	"log"
	"time"

	"github.com/IBM/sarama"
	"github.com/redis/go-redis/v9"
)

// ============================================================
//  INTERFACE (ISP + DIP)
// ============================================================

// IQueueService định nghĩa contract cho luồng Giờ Vàng.
// Mọi handler chỉ phụ thuộc interface này, không phụ thuộc concrete struct.
type IQueueService interface {
	// DangKyHocPhan nhận request đăng ký, kiểm tra slot Redis, đẩy vào queue Kafka.
	DangKyHocPhan(ctx context.Context, req domain.DangKyRequest) (*domain.DangKyResponse, error)

	// HuyDangKy hủy đăng ký (trả slot Redis nếu chưa Kafka consume).
	HuyDangKy(ctx context.Context, req domain.HuyCancelRequest) (*domain.DangKyResponse, error)

	// KhoiTaoSlot nạp số slot còn lại từ DB vào Redis khi admin mở đợt đăng ký.
	KhoiTaoSlot(ctx context.Context, lopHpID int64, siSoToiDa int64) error

	// LayThongTinSlot trả về số slot còn lại cho một lớp học phần.
	LayThongTinSlot(ctx context.Context, lopHpID int64) (int64, error)

	// SubmitPreRegistration enqueue pre-registration message sau khi pass anti-spike checks.
	SubmitPreRegistration(ctx context.Context, req domain.PreRegistrationQueueRequest) (*domain.DangKyResponse, error)
}

// ============================================================
//  CONCRETE IMPLEMENTATION
// ============================================================

// QueueService là lõi xử lý logic Giờ Vàng.
// SRP: Chỉ điều phối Redis (slot/lock) + Kafka (message broker).
type QueueService struct {
	rdb             *redis.Client
	producer        sarama.SyncProducer
	kafkaTopic      string
	preRegKafkaTopic string
}

// NewQueueService factory constructor – DIP: nhận interface/dependency, không tự new.
func NewQueueService(rdb *redis.Client, producer sarama.SyncProducer, kafkaTopic string, preRegKafkaTopic string) IQueueService {
	return &QueueService{
		rdb:              rdb,
		producer:         producer,
		kafkaTopic:       kafkaTopic,
		preRegKafkaTopic: preRegKafkaTopic,
	}
}

// ============================================================
//  LUA SCRIPTS – Atomic Redis Operations
// ============================================================

// luaDecrSlot là Lua script giảm slot nguyên tử.
// Trả về -1 nếu slot đã hết (không bao giờ giảm xuống dưới 0).
// Trả về số slot còn lại sau khi giảm nếu thành công.
var luaDecrSlot = redis.NewScript(`
local key = KEYS[1]
local current = redis.call('GET', key)
if current == false then
    return -2  -- key không tồn tại (chưa KhoiTaoSlot)
end
if tonumber(current) <= 0 then
    return -1  -- Hết chỗ
end
return redis.call('DECR', key)
`)

// luaIncrSlot là Lua script tăng slot nguyên tử khi hủy đăng ký.
var luaIncrSlot = redis.NewScript(`
local key    = KEYS[1]
local maxKey = KEYS[2]
local current = tonumber(redis.call('GET', key) or 0)
local max     = tonumber(redis.call('GET', maxKey) or 9999)
if current >= max then
    return -1  -- không tăng quá si_so_toi_da
end
return redis.call('INCR', key)
`)

// luaAcquireLock SET NX EX để chống double-click trong cùng user+lớp.
var luaAcquireLock = redis.NewScript(`
local key = KEYS[1]
local ttl = ARGV[1]
local ok = redis.call('SET', key, '1', 'NX', 'EX', ttl)
if ok then return 1 end
return 0
`)

// ============================================================
//  PUBLIC METHODS
// ============================================================

// KhoiTaoSlot nạp slot từ DB vào Redis trước khi mở đợt đăng ký.
// Được gọi từ Admin API (Java) hoặc từ script seeder.
func (s *QueueService) KhoiTaoSlot(ctx context.Context, lopHpID int64, siSoToiDa int64) error {
	slotKey := fmt.Sprintf("slot:%d", lopHpID)
	maxKey := fmt.Sprintf("slot:max:%d", lopHpID)

	pipe := s.rdb.TxPipeline()
	// Chỉ SET nếu key chưa tồn tại (tránh reset giữa chừng khi admin gọi lại API).
	pipe.SetNX(ctx, slotKey, siSoToiDa, 0)
	pipe.SetNX(ctx, maxKey, siSoToiDa, 0)
	_, err := pipe.Exec(ctx)
	if err != nil {
		return fmt.Errorf("KhoiTaoSlot: pipeline thất bại lopHpID=%d: %w", lopHpID, err)
	}
	log.Printf("✅ Đã khởi tạo slot Redis: lopHpID=%d siSoToiDa=%d", lopHpID, siSoToiDa)
	return nil
}

// LayThongTinSlot trả về số slot còn lại.
func (s *QueueService) LayThongTinSlot(ctx context.Context, lopHpID int64) (int64, error) {
	key := fmt.Sprintf("slot:%d", lopHpID)
	val, err := s.rdb.Get(ctx, key).Int64()
	if errors.Is(err, redis.Nil) {
		return 0, fmt.Errorf("slot chưa được khởi tạo cho lopHpID=%d", lopHpID)
	}
	return val, err
}

// DangKyHocPhan – Trái Tim Giờ Vàng.
// Thứ tự xử lý:
//  1. Cooldown check (chống spam)
//  2. Lock check (chống double-click)
//  3. DECR slot nguyên tử (Lua)
//  4. Push message vào Kafka topic
func (s *QueueService) DangKyHocPhan(ctx context.Context, req domain.DangKyRequest) (*domain.DangKyResponse, error) {
	traceID := generateTraceID(req.IDSinhVien, req.IDLopHp)

	// ── Bước 1: Cooldown (chống spam 5 giây) ──────────────────
	cooldownKey := fmt.Sprintf("cooldown:%d", req.IDSinhVien)
	acquired, err := s.rdb.SetNX(ctx, cooldownKey, "1", 5*time.Second).Result()
	if err != nil {
		return nil, fmt.Errorf("lỗi kiểm tra cooldown: %w", err)
	}
	if !acquired {
		return &domain.DangKyResponse{
			Status:  "REJECTED",
			Message: "Bạn đang gửi quá nhanh. Vui lòng chờ vài giây.",
			TraceID: traceID,
		}, nil
	}

	// ── Bước 2: Distributed Lock (chống double-click 30 giây) ──
	lockKey := fmt.Sprintf("lock:%d:%d", req.IDLopHp, req.IDSinhVien)
	lockResult, err := luaAcquireLock.Run(ctx, s.rdb, []string{lockKey}, "30").Int()
	if err != nil {
		return nil, fmt.Errorf("lỗi acquire lock: %w", err)
	}
	if lockResult == 0 {
		return &domain.DangKyResponse{
			Status:  "DUPLICATE",
			Message: "Yêu cầu đăng ký đang được xử lý. Không gửi lại.",
			TraceID: traceID,
		}, nil
	}

	// ── Bước 3: DECR slot nguyên tử (Lua script) ──────────────
	slotKey := fmt.Sprintf("slot:%d", req.IDLopHp)
	slotResult, err := luaDecrSlot.Run(ctx, s.rdb, []string{slotKey}).Int64()
	if err != nil {
		// Rollback lock để SV có thể thử lại
		s.rdb.Del(ctx, lockKey)
		return nil, fmt.Errorf("lỗi DECR slot Redis: %w", err)
	}

	switch slotResult {
	case -2:
		s.rdb.Del(ctx, lockKey)
		return &domain.DangKyResponse{
			Status:  "ERROR",
			Message: "Lớp học phần chưa được khởi tạo slot. Vui lòng liên hệ Admin.",
			TraceID: traceID,
		}, nil
	case -1:
		s.rdb.Del(ctx, lockKey)
		return &domain.DangKyResponse{
			Status:  "FULL",
			Message: "Lớp học phần đã hết chỗ. Bạn không thể đăng ký.",
			TraceID: traceID,
		}, nil
	}

	log.Printf("📌 Slot DECR thành công: lopHpID=%d slot_con_lai=%d traceID=%s",
		req.IDLopHp, slotResult, traceID)

	// ── Bước 4: Đẩy message vào Kafka ─────────────────────────
	msg := domain.DangKyMessage{
		IDSinhVien:  req.IDSinhVien,
		IDLopHp:     req.IDLopHp,
		IDHocKy:     req.IDHocKy,
		ThoiGianGui: time.Now(),
		TraceID:     traceID,
	}

	if err := s.publishToKafka(ctx, msg); err != nil {
		// Compensating transaction: INCR lại slot vì message không gửi được
		s.rdb.Incr(ctx, slotKey)
		s.rdb.Del(ctx, lockKey)
		log.Printf("⚠️ Kafka thất bại, đã INCR slot bù trừ: lopHpID=%d traceID=%s", req.IDLopHp, traceID)
		return nil, fmt.Errorf("không thể đẩy message vào Kafka: %w", err)
	}

	return &domain.DangKyResponse{
		Status:  "QUEUED",
		Message: "Yêu cầu đăng ký đã được ghi nhận. Hệ thống đang xử lý.",
		TraceID: traceID,
	}, nil
}

// HuyDangKy hủy đăng ký, trả lại slot nếu chưa được Kafka consumer xử lý.
func (s *QueueService) HuyDangKy(ctx context.Context, req domain.HuyCancelRequest) (*domain.DangKyResponse, error) {
	traceID := generateTraceID(req.IDSinhVien, req.IDLopHp)

	slotKey := fmt.Sprintf("slot:%d", req.IDLopHp)
	maxKey := fmt.Sprintf("slot:max:%d", req.IDLopHp)

	// INCR slot nguyên tử (Lua), không vượt quá siSoToiDa
	incrResult, err := luaIncrSlot.Run(ctx, s.rdb, []string{slotKey, maxKey}).Int64()
	if err != nil {
		return nil, fmt.Errorf("lỗi INCR slot Redis khi hủy: %w", err)
	}
	if incrResult == -1 {
		log.Printf("⚠️ INCR slot bị chặn (đã đầy): lopHpID=%d", req.IDLopHp)
	}

	// Xóa lock để SV có thể đăng ký lại lớp khác
	lockKey := fmt.Sprintf("lock:%d:%d", req.IDLopHp, req.IDSinhVien)
	s.rdb.Del(ctx, lockKey)

	// Đẩy message hủy vào Kafka để Java xóa record trong DB
	cancelMsg := domain.DangKyMessage{
		IDSinhVien:  req.IDSinhVien,
		IDLopHp:     req.IDLopHp,
		IDHocKy:     req.IDHocKy,
		ThoiGianGui: time.Now(),
		TraceID:     "CANCEL-" + traceID,
	}
	if err := s.publishToKafka(ctx, cancelMsg); err != nil {
		log.Printf("⚠️ Kafka không nhận được message hủy: traceID=%s err=%v", traceID, err)
	}

	log.Printf("🗑️ Đã hủy đăng ký: svID=%d lopHpID=%d traceID=%s",
		req.IDSinhVien, req.IDLopHp, traceID)

	return &domain.DangKyResponse{
		Status:  "CANCELLED",
		Message: "Đã hủy đăng ký học phần thành công.",
		TraceID: traceID,
	}, nil
}

// SubmitPreRegistration nhận request enqueue từ backend-core cho luồng pre-registration.
func (s *QueueService) SubmitPreRegistration(ctx context.Context, req domain.PreRegistrationQueueRequest) (*domain.DangKyResponse, error) {
	if req.RequestID == "" || req.LinkID <= 0 || req.DedupeKey == "" {
		return &domain.DangKyResponse{
			Status:  "REJECTED",
			Message: "Thiếu thông tin bắt buộc của pre-registration request.",
			TraceID: req.TraceID,
		}, nil
	}

	// Cooldown theo requestId để chặn spam retry sát nhau.
	cooldownKey := fmt.Sprintf("prereg:cooldown:%s", req.RequestID)
	acquired, err := s.rdb.SetNX(ctx, cooldownKey, "1", 3*time.Second).Result()
	if err != nil {
		return nil, fmt.Errorf("lỗi kiểm tra cooldown pre-reg: %w", err)
	}
	if !acquired {
		return &domain.DangKyResponse{
			Status:  "DUPLICATE",
			Message: "Yêu cầu đang được xử lý, vui lòng không gửi lại ngay.",
			TraceID: req.TraceID,
		}, nil
	}

	// Lock theo dedupeKey để đảm bảo một người một đợt không enqueue trùng.
	lockKey := fmt.Sprintf("prereg:lock:%s", req.DedupeKey)
	lockResult, err := luaAcquireLock.Run(ctx, s.rdb, []string{lockKey}, "30").Int()
	if err != nil {
		return nil, fmt.Errorf("lỗi acquire lock pre-reg: %w", err)
	}
	if lockResult == 0 {
		return &domain.DangKyResponse{
			Status:  "DUPLICATE",
			Message: "Yêu cầu pre-registration đang được xử lý.",
			TraceID: req.TraceID,
		}, nil
	}

	msg := domain.PreRegistrationQueueMessage{
		RequestID:     req.RequestID,
		LinkID:        req.LinkID,
		DedupeKey:     req.DedupeKey,
		TraceID:       req.TraceID,
		SubmittedAt:   req.SubmittedAt,
		PayloadRefID:  req.PayloadRefID,
		SchemaVersion: req.SchemaVersion,
	}
	if err := s.publishPreRegToKafka(ctx, msg); err != nil {
		s.rdb.Del(ctx, lockKey)
		return nil, fmt.Errorf("không thể đẩy pre-reg message vào Kafka: %w", err)
	}
	return &domain.DangKyResponse{
		Status:  "QUEUED",
		Message: "Pre-registration request đã được enqueue.",
		TraceID: req.TraceID,
	}, nil
}

// ============================================================
//  PRIVATE HELPERS
// ============================================================

// publishToKafka serialize message và gửi lên Kafka topic.
func (s *QueueService) publishToKafka(ctx context.Context, msg domain.DangKyMessage) error {
	if s.producer == nil {
		// Kafka chưa kết nối – fallback: ghi log và tiếp tục (dev mode).
		log.Printf("⚠️  [Dev Mode] Kafka producer nil – bỏ qua gửi message traceID=%s", msg.TraceID)
		return nil
	}

	data, err := json.Marshal(msg)
	if err != nil {
		return fmt.Errorf("marshal message thất bại: %w", err)
	}

	kafkaMsg := &sarama.ProducerMessage{
		Topic: s.kafkaTopic,
		// Partition key = IDLopHp để đảm bảo message cùng lớp vào cùng partition (thứ tự)
		Key:   sarama.StringEncoder(fmt.Sprintf("%d", msg.IDLopHp)),
		Value: sarama.StringEncoder(data),
	}

	partition, offset, err := s.producer.SendMessage(kafkaMsg)
	if err != nil {
		return fmt.Errorf("gửi Kafka thất bại: %w", err)
	}

	log.Printf("📨 Kafka OK: topic=%s partition=%d offset=%d traceID=%s",
		s.kafkaTopic, partition, offset, msg.TraceID)
	return nil
}

func (s *QueueService) publishPreRegToKafka(ctx context.Context, msg domain.PreRegistrationQueueMessage) error {
	if s.producer == nil {
		log.Printf("⚠️  [Dev Mode] Kafka producer nil – bỏ qua gửi pre-reg message traceID=%s", msg.TraceID)
		return nil
	}

	data, err := json.Marshal(msg)
	if err != nil {
		return fmt.Errorf("marshal pre-reg message thất bại: %w", err)
	}

	kafkaMsg := &sarama.ProducerMessage{
		Topic: s.preRegKafkaTopic,
		Key:   sarama.StringEncoder(msg.RequestID),
		Value: sarama.StringEncoder(data),
	}

	partition, offset, err := s.producer.SendMessage(kafkaMsg)
	if err != nil {
		return fmt.Errorf("gửi Kafka pre-reg thất bại: %w", err)
	}

	log.Printf("📨 Kafka pre-reg OK: topic=%s partition=%d offset=%d traceID=%s",
		s.preRegKafkaTopic, partition, offset, msg.TraceID)
	return nil
}

// generateTraceID tạo ID theo dõi đơn giản từ svID + lopHpID + timestamp.
func generateTraceID(svID, lopHpID int64) string {
	return fmt.Sprintf("DKHP-%d-%d-%d", svID, lopHpID, time.Now().UnixMilli())
}
