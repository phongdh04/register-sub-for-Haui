-- ====================================================================
-- Sprint 4 — Idempotency log cho luồng đăng ký học phần
-- ====================================================================
-- Mỗi request từ Kafka mang `trace_id` (default idempotency key).
-- Service kiểm tra trước khi xử lý → trùng key thì trả về kết quả cũ.
-- UNIQUE INDEX trên `idempotency_key` là tuyến phòng thủ cuối khi
-- 2 consumer cùng nhận message replay đồng thời.
-- ====================================================================

CREATE TABLE IF NOT EXISTS registration_request_log (
    id_log              BIGSERIAL PRIMARY KEY,
    idempotency_key     VARCHAR(128) NOT NULL,
    id_sinh_vien        BIGINT,
    id_lop_hp           BIGINT,
    id_hoc_ky           BIGINT,
    request_type        VARCHAR(16),
    outcome             VARCHAR(32),
    id_dang_ky          BIGINT,
    error_code          VARCHAR(64),
    error_message       VARCHAR(1000),
    created_at          TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

-- UNIQUE: chặn replay tạo bản ghi trùng.
CREATE UNIQUE INDEX IF NOT EXISTS uq_reqlog_idempotency_key
    ON registration_request_log (idempotency_key);

-- Tra cứu theo (sv, lop, time) cho audit + dashboard sự cố.
CREATE INDEX IF NOT EXISTS idx_reqlog_sv_lhp_created
    ON registration_request_log (id_sinh_vien, id_lop_hp, created_at);

-- Lọc theo outcome cho dashboard tỷ lệ thành công.
CREATE INDEX IF NOT EXISTS idx_reqlog_outcome
    ON registration_request_log (outcome);

-- Sanity guard — outcome chỉ thuộc tập enum đã định nghĩa.
ALTER TABLE registration_request_log
    DROP CONSTRAINT IF EXISTS chk_reqlog_outcome;
ALTER TABLE registration_request_log
    ADD CONSTRAINT chk_reqlog_outcome CHECK (
        outcome IS NULL OR outcome IN (
            'SUCCESS', 'DUPLICATE', 'FULL', 'VALIDATION_FAILED', 'REJECTED', 'CANCELLED'
        )
    );

ALTER TABLE registration_request_log
    DROP CONSTRAINT IF EXISTS chk_reqlog_request_type;
ALTER TABLE registration_request_log
    ADD CONSTRAINT chk_reqlog_request_type CHECK (
        request_type IS NULL OR request_type IN ('REGISTER', 'CANCEL')
    );

COMMENT ON TABLE registration_request_log IS
    'Sprint 4 — idempotency + audit log cho luồng đăng ký/hủy học phần.';
COMMENT ON COLUMN registration_request_log.idempotency_key IS
    'Mặc định = trace_id từ Kafka message; UNIQUE để chặn replay.';
COMMENT ON COLUMN registration_request_log.outcome IS
    'SUCCESS | DUPLICATE | FULL | VALIDATION_FAILED | REJECTED | CANCELLED';
COMMENT ON COLUMN registration_request_log.request_type IS
    'REGISTER | CANCEL';
