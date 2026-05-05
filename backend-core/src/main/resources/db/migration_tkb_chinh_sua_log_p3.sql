-- BACK-TKB-033 — audit log chỉnh sửa TKB theo change-set workflow.

CREATE TABLE IF NOT EXISTS tkb_chinh_sua_log (
    id BIGSERIAL PRIMARY KEY,
    hoc_ky_id BIGINT NOT NULL REFERENCES "Hoc_Ky"(id_hoc_ky) ON DELETE CASCADE,
    change_set_id BIGINT REFERENCES schedule_change_set(id) ON DELETE SET NULL,
    id_lop_hp BIGINT REFERENCES lop_hoc_phan(id_lop_hp) ON DELETE SET NULL,
    hanh_dong VARCHAR(30) NOT NULL,
    nguoi_thuc_hien VARCHAR(120),
    ly_do_thay_doi VARCHAR(1000) NOT NULL,
    payload_cu JSONB,
    payload_moi JSONB,
    effective_version_no BIGINT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_tkb_chinh_sua_log_hk ON tkb_chinh_sua_log(hoc_ky_id);
CREATE INDEX IF NOT EXISTS idx_tkb_chinh_sua_log_cs ON tkb_chinh_sua_log(change_set_id);
