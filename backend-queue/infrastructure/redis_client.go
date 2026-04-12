package infrastructure

import (
	"context"
	"eduport-queue/config"
	"fmt"
	"log"
	"time"

	"github.com/redis/go-redis/v9"
)

// NewRedisClient khởi tạo kết nối Redis và kiểm tra ping.
// OCP: Phụ thuộc vào RedisConfig, không hardcode giá trị.
func NewRedisClient(cfg config.RedisConfig) *redis.Client {
	rdb := redis.NewClient(&redis.Options{
		Addr:         cfg.Addr,
		Password:     cfg.Password,
		DB:           cfg.DB,
		DialTimeout:  5 * time.Second,
		ReadTimeout:  3 * time.Second,
		WriteTimeout: 3 * time.Second,
		PoolSize:     20,
		MinIdleConns: 5,
	})

	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()

	if err := rdb.Ping(ctx).Err(); err != nil {
		log.Fatalf("❌ Không thể kết nối Redis [%s]: %v", cfg.Addr, err)
	}

	log.Printf("✅ Kết nối Redis thành công! Addr=%s DB=%d", cfg.Addr, cfg.DB)
	return rdb
}

// ============================================================
// REDIS KEY NAMING CONVENTION (Giờ Vàng)
// ============================================================
// slot:{lopHpId}        → DECR nguyên tử, đếm slot còn lại
// lock:{lopHpId}:{svId} → SET NX EX 30s, chống double-click
// queue:dkhp            → LIST (LPUSH/RPOP) – hàng đợi chính
// cooldown:{svId}       → SET NX EX 5s, chống spam request
// ============================================================

const (
	// KeySlot Redis key đếm số chỗ còn lại của một LopHocPhan.
	// Format: slot:{id_lop_hp}
	KeySlot = "slot:%d"

	// KeyLock Redis key chống double-click của một SinhVien trên một LopHocPhan.
	// Format: lock:{id_lop_hp}:{id_sinh_vien}
	KeyLock = "lock:%d:%d"

	// KeyQueue Redis LIST – hàng đợi đăng ký chính.
	KeyQueue = "queue:dkhp"

	// KeyCooldown Redis key chống spam request của một SinhVien.
	// Format: cooldown:{id_sinh_vien}
	KeyCooldown = "cooldown:%d"
)

// SlotKey trả về Redis key của slot cho một LopHocPhan.
func SlotKey(lopHpID int64) string {
	return fmt.Sprintf(KeySlot, lopHpID)
}

// LockKey trả về Redis key chống double-click.
func LockKey(lopHpID, svID int64) string {
	return fmt.Sprintf(KeyLock, lopHpID, svID)
}

// CooldownKey trả về Redis key chống spam.
func CooldownKey(svID int64) string {
	return fmt.Sprintf(KeyCooldown, svID)
}
