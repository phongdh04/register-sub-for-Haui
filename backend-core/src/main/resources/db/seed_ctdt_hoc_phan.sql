-- =====================================================
-- SEED DATA: CTDT_HOC_PHAN - Gan hoc phan vao CTDT
-- Run sau seed_ctdt_haui.sql
-- =====================================================

DO $$
DECLARE
    -- CTDT IDs
    v_ctdt_cntt_2024 bigint;
    v_ctdt_khmt bigint;
    v_ctdt_ktpm bigint;
    v_ctdt_attt bigint;
    v_ctdt_httt bigint;
    v_ctdt_cndpt bigint;
    v_ctdt_qtkd bigint;
    v_ctdt_tcnh bigint;
    v_ctdt_marketing bigint;
    v_ctdt_logistics bigint;
    v_ctdt_dien bigint;
    v_ctdt_dientu bigint;
    v_ctdt_diekhi bigint;
    v_ctdt_ckoto bigint;
    v_ctdt_ckmay bigint;
    v_ctdt_nna bigint;
    v_ctdt_nntrung bigint;
    v_ctdt_cnhh bigint;
    v_ctdt_hh bigint;
    v_ctdt_cntp bigint;
    v_ctdt_maymac bigint;
    v_ctdt_tktt bigint;
BEGIN
    -- ===================== TIM CTDT =====================
    SELECT id_ctdt INTO v_ctdt_cntt_2024 FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT863' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_khmt FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT701' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_ktpm FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT702' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_attt FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT904' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_httt FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT703' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_cndpt FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT801' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_qtkd FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT501' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_tcnh FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT502' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_marketing FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT503' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_logistics FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT504' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_dien FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT301' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_dientu FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT302' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_diekhi FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT303' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_ckoto FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT201' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_ckmay FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT101' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_nna FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT601' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_nntrung FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT602' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_cnhh FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT7011' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_hh FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT7012' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_cntp FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT7013' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_maymac FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT8011' AND ctdt.nam_ap_dung = 2024;
    SELECT id_ctdt INTO v_ctdt_tktt FROM chuong_trinh_dao_tao ctdt
        JOIN nganh_dao_tao ndt ON ctdt.id_nganh = ndt.id_nganh
        WHERE ndt.ma_nganh = 'CT8012' AND ctdt.nam_ap_dung = 2024;

    RAISE NOTICE 'Tim thay CTDT IDs: CNTT=%', v_ctdt_cntt_2024;

    -- ===================== HOC PHAN DAI CUONG (ALL) =====================
    -- Triet, Chinh tri, Phap luat
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6003'
    ON CONFLICT DO NOTHING;

    -- QPAN
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6002'
    ON CONFLICT DO NOTHING;

    -- Toan, Ly
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6027'
    ON CONFLICT DO NOTHING;

    -- Ngoai ngu - Tieng Anh CNTT
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6008'
    ON CONFLICT DO NOTHING;

    -- Giao tiep lien van hoa
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6018'
    ON CONFLICT DO NOTHING;

    -- The duc
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6002'
    ON CONFLICT DO NOTHING;

    -- Con nguoi va moi truong
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6011'
    ON CONFLICT DO NOTHING;

    -- ===================== CNTT / KHMT / KTPM / ATTT / HTTT / CNDPT - CO SO NGANH =====================
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6016'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6035'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6015'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6067'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6120'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6083'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6126'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6066'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6082'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6056'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6039'
    ON CONFLICT DO NOTHING;

    -- ===================== CNTT - CHUYEN NGANH =====================
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6050'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6057'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6071'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6058'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6051'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6084'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6064'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6065'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6122'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6129'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cntt_2024, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6128'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert CNTT CTDT completed';

    -- ===================== KHMT - CHUYEN NGANH =====================
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6016'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6035'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6015'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6067'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6120'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6083'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6126'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6066'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6082'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6056'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6039'
    ON CONFLICT DO NOTHING;

    -- KHMT chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6094'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6053'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6070'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6077'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6068'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT1066'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6122'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6129'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_khmt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6128'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert KHMT CTDT completed';

    -- ===================== KTPM - CHUYEN NGANH =====================
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6016'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6035'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6015'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6067'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6120'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6083'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6126'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6066'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6082'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6056'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6039'
    ON CONFLICT DO NOTHING;

    -- KTPM chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6078'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6076'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6075'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6079'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT1080'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6061'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT1100'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6122'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6129'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ktpm, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6128'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert KTPM CTDT completed';

    -- ===================== ATTT - CO SO + CHUYEN NGANH =====================
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6016'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6035'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6015'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6067'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6120'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6083'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6126'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6066'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6082'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6056'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6039'
    ON CONFLICT DO NOTHING;

    -- ATTT chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6050'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6063'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6064'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6065'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6058'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6122'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6129'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_attt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6128'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert ATTT CTDT completed';

    -- ===================== HTTT - CO SO + CHUYEN NGANH =====================
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6016'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6035'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6015'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6067'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6120'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6083'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6126'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6066'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6082'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6056'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6039'
    ON CONFLICT DO NOTHING;

    -- HTTT chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6057'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6054'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6053'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6062'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6071'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6058'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6122'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6129'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_httt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6128'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert HTTT CTDT completed';

    -- ===================== CNDPT - CO SO + CHUYEN NGANH =====================
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6016'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6035'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6015'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6067'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6120'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6083'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6126'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6066'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6082'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6056'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6039'
    ON CONFLICT DO NOTHING;

    -- CNDPT chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6110'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6100'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6068'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6101'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6060'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6102'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6122'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6129'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cndpt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'IT6128'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert CNDPT CTDT completed';

    -- ===================== QTKd / TCNH / Marketing / Logistics - DAI CUONG + CO SO + CHUYEN NGANH =====================
    -- Dai cuong
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUUNG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6027'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6008'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6018'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6011'
    ON CONFLICT DO NOTHING;

    -- QTKd co so nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6005'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6006'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6007'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6008'
    ON CONFLICT DO NOTHING;

    -- QTKd chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6009'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_qtkd, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6013'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert QTKd CTDT completed';

    -- ===================== TCNH - CO SO + CHUYEN NGANH =====================
    -- Dai cuong
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6027'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6008'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6018'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6011'
    ON CONFLICT DO NOTHING;

    -- TCNh co so nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6005'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6006'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6007'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6008'
    ON CONFLICT DO NOTHING;

    -- TCNH chuyen nganh (tai chinh ngan hang)
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6009'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tcnh, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'KT6013'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert TCNH CTDT completed';

    -- ===================== DIEN - CO SO + CHUYEN NGANH =====================
    -- Dai cuong
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6027'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6008'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6018'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6011'
    ON CONFLICT DO NOTHING;

    -- Dien co so nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'DT6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'DT6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'DT6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'DT6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'DT6005'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'DT6006'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'DT6007'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'DT6008'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_dien, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'DT6009'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert Dien CTDT completed';

    -- ===================== CKOTO - CO SO + CHUYEN NGANH =====================
    -- Dai cuong
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6027'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6008'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6018'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6011'
    ON CONFLICT DO NOTHING;

    -- Co khi co so nganh (dung chung voi co khi may)
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'CK6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'CK6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'CK6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'CK6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'CK6005'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'CK6006'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'CK6007'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CO_SO_NGANH', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'CK6008'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'CK6009'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CO_SO_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'CK6010'
    ON CONFLICT DO NOTHING;

    -- CK Oto chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'OTO6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'OTO6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'OTO6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'OTO6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'OTO6005'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 7 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'OTO6006'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_ckoto, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 8 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'OTO6007'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert OTO CTDT completed';

    -- ===================== NNA - CO SO + CHUYEN NGANH =====================
    -- Dai cuong
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6027'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6008'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6018'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6011'
    ON CONFLICT DO NOTHING;

    -- NNA co so nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6014'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6015'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6016'
    ON CONFLICT DO NOTHING;

    -- NNA chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6017'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6018'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_nna, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6019'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert NNA CTDT completed';

    -- ===================== HOA - CNHH / HH / CNTP =====================
    -- Dai cuong
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6027'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6008'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6018'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6011'
    ON CONFLICT DO NOTHING;

    -- HOA co so nganh (chung)
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'HOA6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'HOA6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'HOA6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'HOA6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'CO_SO_NGANH', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'HOA6005'
    ON CONFLICT DO NOTHING;

    -- CNHH chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'HOA6006'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'HOA6007'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_cnhh, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'HOA6008'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert HOA CNHH CTDT completed';

    -- ===================== COMAY - MAYMAC / TKTT =====================
    -- Dai cuong
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6027'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6008'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6018'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6011'
    ON CONFLICT DO NOTHING;

    -- COMAY co so nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6004'
    ON CONFLICT DO NOTHING;

    -- MAYMAC chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6005'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_maymac, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6006'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert MAYMAC CTDT completed';

    -- ===================== TKTT - DAI CUONG + CO SO + CHUYEN NGANH =====================
    -- Dai cuong
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6010'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6011'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6012'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 4 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6013'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'LP6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 6 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'QP6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6027'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6004'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'NN6008'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'BS6018'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'DAI_CUONG', 3 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'TC6011'
    ON CONFLICT DO NOTHING;

    -- TKTT co so nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6001'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 1 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6002'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6003'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'CO_SO_NGANH', 2 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6004'
    ON CONFLICT DO NOTHING;

    -- TKTT chuyen nganh
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6005'
    ON CONFLICT DO NOTHING;
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, bat_buoc, khoi_kien_thuc, hoc_ky_goi_y)
    SELECT v_ctdt_tktt, hp.id_hoc_phan, true, 'CHUYEN_NGANH', 5 FROM hoc_phan hp WHERE hp.ma_hoc_phan = 'MM6006'
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Insert TKTT CTDT completed';

    RAISE NOTICE 'Seed ctdt_hoc_phan completed for all 22 CTDT';

END $$;
