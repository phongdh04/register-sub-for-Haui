-- BACK-TKB-030/031 — impact analysis fields + notification_queue stub.

ALTER TABLE schedule_change_set
    ADD COLUMN IF NOT EXISTS affected_sv_count INTEGER NOT NULL DEFAULT 0;

ALTER TABLE schedule_change_set
    ADD COLUMN IF NOT EXISTS affected_sv_ids JSONB;

CREATE TABLE IF NOT EXISTS notification_queue (
    id BIGSERIAL PRIMARY KEY,
    event_type VARCHAR(60) NOT NULL,
    hoc_ky_id BIGINT REFERENCES "Hoc_Ky"(id_hoc_ky) ON DELETE SET NULL,
    change_set_id BIGINT REFERENCES schedule_change_set(id) ON DELETE SET NULL,
    payload JSONB NOT NULL,
    trang_thai VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_message VARCHAR(1000),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    sent_at TIMESTAMPTZ
);

CREATE INDEX IF NOT EXISTS idx_notification_queue_status ON notification_queue(trang_thai);
CREATE INDEX IF NOT EXISTS idx_notification_queue_event ON notification_queue(event_type);
CREATE INDEX IF NOT EXISTS idx_notification_queue_hk ON notification_queue(hoc_ky_id);
