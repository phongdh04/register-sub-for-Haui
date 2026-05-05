-- BACK-TKB-004 — Gói TKB (`Tkb_Block`) + FK nullable trên `Lop_Hoc_Phan` (chuẩn P0 §7.6).
-- Lifecycle API đầy đủ hoãn BACK-TKB-037 (P4); P0 chỉ DDL + khóa ngoại để không nợ schema.

CREATE TABLE IF NOT EXISTS "Tkb_Block" (
    id_tkb_block BIGSERIAL PRIMARY KEY,
    id_hoc_ky    BIGINT NOT NULL REFERENCES "Hoc_Ky"(id_hoc_ky),
    ma_block     VARCHAR(64)  NOT NULL,
    ten_block    VARCHAR(300) NOT NULL,
    json_slots   JSONB,
    danh_sach_id_hoc_phan_json JSONB,
    UNIQUE (id_hoc_ky, ma_block)
);

COMMENT ON TABLE "Tkb_Block" IS 'Gói lớp TKB trong học kỳ (bundle đăng ký năm 1–2); P4 bổ sung API.';
COMMENT ON COLUMN "Tkb_Block".json_slots IS 'Optional preview slots / metadata (solver P2+)';
COMMENT ON COLUMN "Tkb_Block".danh_sach_id_hoc_phan_json IS 'Mảng id_hoc_phân học phần thuộc block';

CREATE INDEX IF NOT EXISTS idx_tkb_block_hk ON "Tkb_Block"(id_hoc_ky);

ALTER TABLE "Lop_Hoc_Phan"
    ADD COLUMN IF NOT EXISTS id_tkb_block BIGINT REFERENCES "Tkb_Block"(id_tkb_block) ON DELETE SET NULL;
CREATE INDEX IF NOT EXISTS idx_lhp_tkb_block ON "Lop_Hoc_Phan"(id_tkb_block);
