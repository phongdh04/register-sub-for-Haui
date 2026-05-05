-- BACK-TKB-011 — Version snapshot TKB cho học kỳ.

ALTER TABLE "Hoc_Ky" ADD COLUMN IF NOT EXISTS tkb_revision BIGINT NOT NULL DEFAULT 0;
COMMENT ON COLUMN "Hoc_Ky".tkb_revision IS 'Tăng khi có thay đổi LHP/TKB học kỳ; invalidate cache snapshot';
