-- TKB P0 — khung giờ bận giảng viên (ADR kế hoạch §5.2).

CREATE TABLE IF NOT EXISTS gv_busy_slot (
    id               BIGSERIAL PRIMARY KEY,
    id_giang_vien    BIGINT NOT NULL REFERENCES "Giang_Vien"(id_giang_vien),
    hoc_ky_id        BIGINT REFERENCES "Hoc_Ky"(id_hoc_ky),
    thu              SMALLINT NOT NULL CHECK (thu BETWEEN 2 AND 8),
                                     -- 2=Thứ Hai … 7=Thứ Bảy, 8=Chủ Nhật
    tiet_bd          SMALLINT NOT NULL,
    tiet_kt          SMALLINT NOT NULL,
    loai             VARCHAR(20) NOT NULL DEFAULT 'HARD',
    ly_do            VARCHAR(500),
    ngay_bd          DATE,
    ngay_kt          DATE,
    created_at       TIMESTAMPTZ NOT NULL DEFAULT now(),
    CHECK (tiet_bd <= tiet_kt)
);
CREATE INDEX IF NOT EXISTS idx_gv_busy_gv_thu ON gv_busy_slot(id_giang_vien, thu);
CREATE INDEX IF NOT EXISTS idx_gv_busy_hk ON gv_busy_slot(hoc_ky_id);
