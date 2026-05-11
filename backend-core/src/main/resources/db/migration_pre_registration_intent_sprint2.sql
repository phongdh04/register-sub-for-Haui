-- Sprint 2 — Pre-registration intent (PostgreSQL).
-- Tài liệu: reports/personalized_course_recommendation_gap_report.md §4.2.2

CREATE TABLE IF NOT EXISTS pre_registration_intent (
    id_intent     BIGSERIAL PRIMARY KEY,
    id_sinh_vien  BIGINT      NOT NULL REFERENCES sinh_vien(id_sinh_vien) ON DELETE CASCADE,
    id_hoc_ky     BIGINT      NOT NULL REFERENCES hoc_ky(id_hoc_ky)       ON DELETE CASCADE,
    id_hoc_phan   BIGINT      NOT NULL REFERENCES hoc_phan(id_hoc_phan)   ON DELETE CASCADE,
    priority      INT         NOT NULL DEFAULT 1,
    ghi_chu       VARCHAR(500),
    created_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_prereg_intent_priority CHECK (priority >= 1),
    CONSTRAINT uk_prereg_intent_sv_hk_hp UNIQUE (id_sinh_vien, id_hoc_ky, id_hoc_phan)
);

CREATE INDEX IF NOT EXISTS idx_prereg_intent_hk_hp
    ON pre_registration_intent (id_hoc_ky, id_hoc_phan);

CREATE INDEX IF NOT EXISTS idx_prereg_intent_sv_hk
    ON pre_registration_intent (id_sinh_vien, id_hoc_ky);
