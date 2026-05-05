-- BACK-TKB-026 — nguồn rule nhóm trùng tiết (cohort) theo học kỳ.
CREATE TABLE IF NOT EXISTS quy_tac_trung_tiet_cohort (
    id BIGSERIAL PRIMARY KEY,
    hoc_ky_id BIGINT NOT NULL REFERENCES hoc_ky(id_hoc_ky) ON DELETE CASCADE,
    nhom_ma VARCHAR(80) NOT NULL,
    id_lop_hp BIGINT NOT NULL REFERENCES lop_hoc_phan(id_lop_hp) ON DELETE CASCADE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    ghi_chu VARCHAR(500),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_cohort_hk_nhom_lhp UNIQUE (hoc_ky_id, nhom_ma, id_lop_hp)
);

CREATE INDEX IF NOT EXISTS idx_cohort_hk ON quy_tac_trung_tiet_cohort(hoc_ky_id);
CREATE INDEX IF NOT EXISTS idx_cohort_hk_nhom ON quy_tac_trung_tiet_cohort(hoc_ky_id, nhom_ma);
CREATE INDEX IF NOT EXISTS idx_cohort_lhp ON quy_tac_trung_tiet_cohort(id_lop_hp);
