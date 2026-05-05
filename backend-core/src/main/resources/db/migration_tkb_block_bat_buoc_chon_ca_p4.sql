-- BACK-TKB-038 — flag bắt buộc chọn cả block.
ALTER TABLE "Tkb_Block"
    ADD COLUMN IF NOT EXISTS bat_buoc_chon_ca_block BOOLEAN NOT NULL DEFAULT FALSE;

COMMENT ON COLUMN "Tkb_Block".bat_buoc_chon_ca_block IS 'Nếu true: SV không được chọn lẻ từng LHP thuộc block.';
