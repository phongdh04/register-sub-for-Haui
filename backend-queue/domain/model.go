package domain

import "time"

// DangKyRequest là payload từ Frontend gửi lên khi SV nhấn "Đăng Ký".
// DIP: Go service không biết đến Java entity, chỉ biết contract interface này.
type DangKyRequest struct {
	IDSinhVien int64 `json:"id_sinh_vien" validate:"required,gt=0"`
	IDLopHp    int64 `json:"id_lop_hp" validate:"required,gt=0"`
	IDHocKy    int64 `json:"id_hoc_ky" validate:"required,gt=0"`
}

// DangKyMessage là cấu trúc message đẩy vào Kafka topic.
// Java consumer (backend-core) sẽ deserialize struct này để xử lý phía DB.
type DangKyMessage struct {
	IDSinhVien  int64     `json:"id_sinh_vien"`
	IDLopHp     int64     `json:"id_lop_hp"`
	IDHocKy     int64     `json:"id_hoc_ky"`
	ThoiGianGui time.Time `json:"thoi_gian_gui"`
	// TraceID để theo dõi luồng xử lý end-to-end (distributed tracing).
	TraceID string `json:"trace_id"`
}

// DangKyResponse là phản hồi trả về cho Frontend khi Go nhận request.
type DangKyResponse struct {
	Status  string `json:"status"`
	Message string `json:"message"`
	TraceID string `json:"trace_id,omitempty"`
}

// HuyCancelRequest là payload khi SV hủy đăng ký (xóa khỏi queue nếu chưa xử lý).
type HuyCancelRequest struct {
	IDSinhVien int64 `json:"id_sinh_vien" validate:"required,gt=0"`
	IDLopHp    int64 `json:"id_lop_hp" validate:"required,gt=0"`
	IDHocKy    int64 `json:"id_hoc_ky" validate:"required,gt=0"`
}

// SlotInfo là thông tin slot hiển thị trên UI.
type SlotInfo struct {
	IDLopHp    int64 `json:"id_lop_hp"`
	SlotConLai int64 `json:"slot_con_lai"`
	SiSoToiDa  int64 `json:"si_so_toi_da"`
}

// PreRegistrationQueueRequest là payload backend-core gửi sang Go ingress để enqueue pre-registration.
type PreRegistrationQueueRequest struct {
	RequestID    string `json:"requestId"`
	LinkID       int64  `json:"linkId"`
	DedupeKey    string `json:"dedupeKey"`
	TraceID      string `json:"traceId"`
	SubmittedAt  string `json:"submittedAt"`
	PayloadRefID int64  `json:"payloadRefId"`
	SchemaVersion int   `json:"schemaVersion"`
}

// PreRegistrationQueueMessage là message publish lên Kafka cho luồng pre-registration.
type PreRegistrationQueueMessage struct {
	RequestID     string `json:"requestId"`
	LinkID        int64  `json:"linkId"`
	DedupeKey     string `json:"dedupeKey"`
	TraceID       string `json:"traceId"`
	SubmittedAt   string `json:"submittedAt"`
	PayloadRefID  int64  `json:"payloadRefId"`
	SchemaVersion int    `json:"schema_version"`
}
