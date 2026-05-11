-- Sprint 3 — Class publish lifecycle + optimistic lock (PostgreSQL).
-- Tài liệu: reports/personalized_course_recommendation_gap_report.md §4.2.4 + §14.4

ALTER TABLE lop_hoc_phan
    ADD COLUMN IF NOT EXISTS status_publish VARCHAR(16) NOT NULL DEFAULT 'PUBLISHED';

ALTER TABLE lop_hoc_phan
    ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;

ALTER TABLE lop_hoc_phan
    DROP CONSTRAINT IF EXISTS chk_lhp_status_publish;

ALTER TABLE lop_hoc_phan
    ADD CONSTRAINT chk_lhp_status_publish
        CHECK (status_publish IN ('SHELL', 'SCHEDULED', 'PUBLISHED'));

CREATE INDEX IF NOT EXISTS idx_lhp_hk_status_publish
    ON lop_hoc_phan (id_hoc_ky, status_publish);

-- Backfill: data hiện tại coi như đã PUBLISHED (back-compat với luồng đăng ký cũ).
-- Lớp shell sinh từ spawn-shell ở các đợt forecast sau Sprint 3 sẽ tự set SHELL ở entity layer.
UPDATE lop_hoc_phan
SET status_publish = 'PUBLISHED'
WHERE status_publish IS NULL;
