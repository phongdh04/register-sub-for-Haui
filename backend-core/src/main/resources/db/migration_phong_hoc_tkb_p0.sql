-- TKB Phase 1 (P0): master phòng + FK nullable trên Lớp học phần.
-- Chạy thủ công nếu không dùng spring.jpa.hibernate.ddl-auto=update.

CREATE TABLE IF NOT EXISTS "Phong_Hoc" (
    id_phong     BIGSERIAL PRIMARY KEY,
    ma_phong     VARCHAR(30)  NOT NULL UNIQUE,
    ten_phong    VARCHAR(200) NOT NULL,
    ma_co_so     VARCHAR(50)  NOT NULL,
    loai_phong   VARCHAR(40)  NOT NULL,
    suc_chua     INTEGER      NOT NULL,
    trang_thai   VARCHAR(30)  NOT NULL DEFAULT 'HOAT_DONG',
    ghi_chu      TEXT
);
CREATE INDEX IF NOT EXISTS idx_phong_ma_co_so ON "Phong_Hoc" (ma_co_so);
CREATE INDEX IF NOT EXISTS idx_phong_loai ON "Phong_Hoc" (loai_phong);

ALTER TABLE "Lop_Hoc_Phan"
    ADD COLUMN IF NOT EXISTS id_phong_hoc BIGINT REFERENCES "Phong_Hoc"(id_phong);
CREATE INDEX IF NOT EXISTS idx_lhp_phong_hoc ON "Lop_Hoc_Phan"(id_phong_hoc);
