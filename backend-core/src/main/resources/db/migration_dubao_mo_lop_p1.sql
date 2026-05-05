-- BACK-TKB-013 — Dự báo mở lớp: phiên bản báo cáo + dòng chi tiết theo học phần.

CREATE TABLE IF NOT EXISTS "Du_Bao_Mo_Lop_Version" (
    id_du_bao_version BIGSERIAL PRIMARY KEY,
    id_hoc_ky         BIGINT NOT NULL REFERENCES "Hoc_Ky"(id_hoc_ky),
    id_ctdt           BIGINT NOT NULL REFERENCES "Chuong_Trinh_Dao_Tao"(id_ctdt),
    trang_thai        VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    si_so_mac_dinh    INTEGER NOT NULL,
    he_so_du_phong    NUMERIC(6, 3) NOT NULL,
    ty_le_sv_hoc_lai  NUMERIC(6, 3) NOT NULL,
    nam_hoc_nam_ke    SMALLINT NOT NULL,
    ky_thu_muc_tieu   INTEGER NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT ck_dbmlv_trang_thai CHECK (trang_thai IN ('DRAFT', 'APPROVED', 'REJECTED'))
);

CREATE INDEX IF NOT EXISTS idx_dbmlv_hk_created ON "Du_Bao_Mo_Lop_Version"(id_hoc_ky, created_at DESC);

CREATE TABLE IF NOT EXISTS "Du_Bao_Mo_Lop_Line" (
    id_du_bao_line    BIGSERIAL PRIMARY KEY,
    id_du_bao_version BIGINT NOT NULL REFERENCES "Du_Bao_Mo_Lop_Version"(id_du_bao_version) ON DELETE CASCADE,
    id_hoc_phan       BIGINT NOT NULL REFERENCES "Hoc_Phan"(id_hoc_phan),
    hoc_ky_goi_y_ctdt INTEGER NOT NULL,
    so_sv_on_track    INTEGER NOT NULL DEFAULT 0,
    so_sv_hoc_lai     INTEGER NOT NULL DEFAULT 0,
    so_sv_du_kien    INTEGER NOT NULL DEFAULT 0,
    so_lop_de_xuat   INTEGER NOT NULL DEFAULT 0,
    UNIQUE (id_du_bao_version, id_hoc_phan)
);

CREATE INDEX IF NOT EXISTS idx_dbmll_version ON "Du_Bao_Mo_Lop_Line"(id_du_bao_version);
