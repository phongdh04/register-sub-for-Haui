-- BACK-TKB-029 — schedule_change_set + FK pending_change_set_id trên Hoc_Ky.

CREATE TABLE IF NOT EXISTS schedule_change_set (
    id BIGSERIAL PRIMARY KEY,
    hoc_ky_id BIGINT NOT NULL REFERENCES "Hoc_Ky"(id_hoc_ky) ON DELETE CASCADE,
    trang_thai VARCHAR(20) NOT NULL,
    payload_delta JSONB NOT NULL,
    ghi_chu VARCHAR(1000),
    requested_by VARCHAR(120),
    reviewed_by VARCHAR(120),
    approved_at TIMESTAMPTZ,
    applied_at TIMESTAMPTZ,
    effective_version_no BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_schedule_change_set_hk ON schedule_change_set(hoc_ky_id);
CREATE INDEX IF NOT EXISTS idx_schedule_change_set_status ON schedule_change_set(trang_thai);

ALTER TABLE "Hoc_Ky"
    ADD COLUMN IF NOT EXISTS pending_change_set_id BIGINT NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints
        WHERE table_name = 'Hoc_Ky'
          AND constraint_name = 'fk_hoc_ky_pending_change_set'
    ) THEN
        ALTER TABLE "Hoc_Ky"
            ADD CONSTRAINT fk_hoc_ky_pending_change_set
            FOREIGN KEY (pending_change_set_id)
            REFERENCES schedule_change_set(id)
            ON DELETE SET NULL;
    END IF;
END $$;
