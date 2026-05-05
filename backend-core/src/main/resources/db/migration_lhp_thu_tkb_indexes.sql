-- BACK-TKB-010 — Cột surrogate thứ trong tuần + index composite (warm path §8.2.1).
-- Hibernate ddl-auto có thể đã tạo schema; SQL này cho môi trường chạy script tay.

ALTER TABLE "Lop_Hoc_Phan" ADD COLUMN IF NOT EXISTS thu_tkb SMALLINT;

COMMENT ON COLUMN "Lop_Hoc_Phan".thu_tkb IS 'Surrogate từ thoi_khoa_bieu_json[0].thu (2–8); index với hoc_kỳ + phòng/GV';

CREATE INDEX IF NOT EXISTS idx_lhp_hk_phong_thu ON "Lop_Hoc_Phan"(id_hoc_ky, id_phong_hoc, thu_tkb);
CREATE INDEX IF NOT EXISTS idx_lhp_hk_gv_thu ON "Lop_Hoc_Phan"(id_hoc_ky, id_giang_vien, thu_tkb);
