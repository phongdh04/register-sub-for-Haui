-- Sprint 1 — Cohort-first registration window (PostgreSQL).
-- Tài liệu: reports/personalized_course_recommendation_gap_report.md §4.2.1
--
-- Khi dùng spring.jpa.hibernate.ddl-auto=update, Hibernate có thể đã tạo bảng tương ứng;
-- file này dùng để áp thủ công hoặc làm tài liệu cho DBA.

CREATE TABLE IF NOT EXISTS registration_window (
    id_registration_window BIGSERIAL PRIMARY KEY,
    id_hoc_ky              BIGINT      NOT NULL REFERENCES hoc_ky(id_hoc_ky) ON DELETE CASCADE,
    phase                  VARCHAR(16) NOT NULL,
    nam_nhap_hoc           INT,
    id_nganh               BIGINT      REFERENCES nganh_dao_tao(id_nganh) ON DELETE SET NULL,
    open_at                TIMESTAMPTZ NOT NULL,
    close_at               TIMESTAMPTZ NOT NULL,
    ghi_chu                VARCHAR(500),
    created_by             VARCHAR(100),
    created_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at             TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    CONSTRAINT chk_regwin_phase CHECK (phase IN ('PRE', 'OFFICIAL')),
    CONSTRAINT chk_regwin_open_before_close CHECK (open_at < close_at),
    CONSTRAINT chk_regwin_nganh_requires_cohort CHECK (id_nganh IS NULL OR nam_nhap_hoc IS NOT NULL)
);

CREATE INDEX IF NOT EXISTS idx_regwin_hk_phase
    ON registration_window (id_hoc_ky, phase);

CREATE INDEX IF NOT EXISTS idx_regwin_open_close
    ON registration_window (open_at, close_at);

-- Unique theo bộ khoá logic (hocKy, phase, namNhapHoc, idNganh) — coi NULL như giá trị riêng.
-- PostgreSQL: dùng COALESCE-based functional unique index để bảo vệ trùng cấu hình.
CREATE UNIQUE INDEX IF NOT EXISTS uk_regwin_scope
    ON registration_window (
        id_hoc_ky,
        phase,
        COALESCE(nam_nhap_hoc, -1),
        COALESCE(id_nganh, -1)
    );
