-- BACK-TKB-035 — trạng thái vòng đời TKB học kỳ.
ALTER TABLE "Hoc_Ky"
    ADD COLUMN IF NOT EXISTS tkb_trang_thai VARCHAR(20) NOT NULL DEFAULT 'NHAP';

COMMENT ON COLUMN "Hoc_Ky".tkb_trang_thai IS 'Vòng đời TKB: NHAP -> CHO_DUYET -> CONG_BO';
