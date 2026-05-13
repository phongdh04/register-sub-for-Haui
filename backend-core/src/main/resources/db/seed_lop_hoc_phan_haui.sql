-- =====================================================
-- SEED DATA: LOP HOC PHAN (Course Classes)
-- =====================================================

DO $$
DECLARE
    v_id_hk1 bigint;
    v_id_gv1 bigint; v_id_gv2 bigint; v_id_gv3 bigint; v_id_gv4 bigint;
    v_id_gv5 bigint; v_id_gv6 bigint; v_id_gv7 bigint;
    v_id_hp_it6015 bigint; v_id_hp_it6120 bigint; v_id_hp_it6002 bigint;
    v_id_hp_it6126 bigint; v_id_hp_it6083 bigint; v_id_hp_it6067 bigint;
    v_id_hp_bs6002 bigint; v_id_hp_bs6001 bigint; v_id_hp_it6035 bigint;
    v_id_hp_lp6010 bigint; v_id_hp_nn6001 bigint; v_id_hp_tc6001 bigint;
    v_id_phong1 bigint; v_id_phong2 bigint; v_id_phong3 bigint;
BEGIN
    -- Get HocKy (nam_hoc now is varchar like '2025-2026')
    SELECT id_hoc_ky INTO v_id_hk1 FROM hoc_ky WHERE nam_hoc = '2025-2026' AND ky_thu = 1;
    
    -- Get GiangVien
    SELECT id_giang_vien INTO v_id_gv1 FROM giang_vien WHERE ma_giang_vien = 'GV001';
    SELECT id_giang_vien INTO v_id_gv2 FROM giang_vien WHERE ma_giang_vien = 'GV002';
    SELECT id_giang_vien INTO v_id_gv3 FROM giang_vien WHERE ma_giang_vien = 'GV003';
    SELECT id_giang_vien INTO v_id_gv4 FROM giang_vien WHERE ma_giang_vien = 'GV004';
    SELECT id_giang_vien INTO v_id_gv5 FROM giang_vien WHERE ma_giang_vien = 'GV005';
    SELECT id_giang_vien INTO v_id_gv6 FROM giang_vien WHERE ma_giang_vien = 'GV006';
    SELECT id_giang_vien INTO v_id_gv7 FROM giang_vien WHERE ma_giang_vien = 'GV007';
    
    -- Get HocPhan
    SELECT id_hoc_phan INTO v_id_hp_it6015 FROM hoc_phan WHERE ma_hoc_phan = 'IT6015';
    SELECT id_hoc_phan INTO v_id_hp_it6120 FROM hoc_phan WHERE ma_hoc_phan = 'IT6120';
    SELECT id_hoc_phan INTO v_id_hp_it6002 FROM hoc_phan WHERE ma_hoc_phan = 'IT6002';
    SELECT id_hoc_phan INTO v_id_hp_it6126 FROM hoc_phan WHERE ma_hoc_phan = 'IT6126';
    SELECT id_hoc_phan INTO v_id_hp_it6083 FROM hoc_phan WHERE ma_hoc_phan = 'IT6083';
    SELECT id_hoc_phan INTO v_id_hp_it6067 FROM hoc_phan WHERE ma_hoc_phan = 'IT6067';
    SELECT id_hoc_phan INTO v_id_hp_bs6002 FROM hoc_phan WHERE ma_hoc_phan = 'BS6002';
    SELECT id_hoc_phan INTO v_id_hp_bs6001 FROM hoc_phan WHERE ma_hoc_phan = 'BS6001';
    SELECT id_hoc_phan INTO v_id_hp_it6035 FROM hoc_phan WHERE ma_hoc_phan = 'IT6035';
    SELECT id_hoc_phan INTO v_id_hp_lp6010 FROM hoc_phan WHERE ma_hoc_phan = 'LP6010';
    SELECT id_hoc_phan INTO v_id_hp_nn6001 FROM hoc_phan WHERE ma_hoc_phan = 'NN6001';
    SELECT id_hoc_phan INTO v_id_hp_tc6001 FROM hoc_phan WHERE ma_hoc_phan = 'TC6001';
    
    -- Get Phong
    SELECT id_phong INTO v_id_phong1 FROM phong_hoc WHERE ma_phong = '101-A1';
    SELECT id_phong INTO v_id_phong2 FROM phong_hoc WHERE ma_phong = '103-A1';
    SELECT id_phong INTO v_id_phong3 FROM phong_hoc WHERE ma_phong = '201-A1';

    -- ===================== LOP HOC PHAN KY THUAT LAP TRINH =====================
    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-IT6015-25K1-A', v_id_hp_it6015, v_id_hk1, v_id_gv1, v_id_phong1,
           80, 75, 1500000, 'DANG_MO', true,
           2, '{"tiet_bd": 1, "tiet_kt": 4, "phong": "101-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6015-25K1-A');

    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-IT6015-25K1-B', v_id_hp_it6015, v_id_hk1, v_id_gv2, v_id_phong2,
           40, 38, 1500000, 'DANG_MO', true,
           3, '{"tiet_bd": 1, "tiet_kt": 4, "phong": "103-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6015-25K1-B');

    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-IT6015-25K1-C', v_id_hp_it6015, v_id_hk1, v_id_gv3, v_id_phong3,
           80, 65, 1500000, 'DANG_MO', true,
           4, '{"tiet_bd": 6, "tiet_kt": 9, "phong": "201-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6015-25K1-C');

    -- ===================== LOP HOC PHAN LAP TRINH OOP =====================
    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-IT6120-25K1-A', v_id_hp_it6120, v_id_hk1, v_id_gv2, v_id_phong1,
           80, 70, 1500000, 'DANG_MO', true,
           2, '{"tiet_bd": 6, "tiet_kt": 9, "phong": "101-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6120-25K1-A');

    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-IT6120-25K1-B', v_id_hp_it6120, v_id_hk1, v_id_gv4, v_id_phong2,
           40, 35, 1500000, 'DANG_MO', true,
           5, '{"tiet_bd": 1, "tiet_kt": 4, "phong": "103-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6120-25K1-B');

    -- ===================== LOP HOC PHAN CAU TRUC DU LIEU =====================
    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-IT6002-25K1-A', v_id_hp_it6002, v_id_hk1, v_id_gv3, v_id_phong1,
           80, 72, 1500000, 'DANG_MO', true,
           3, '{"tiet_bd": 1, "tiet_kt": 4, "phong": "101-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6002-25K1-A');

    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-IT6002-25K1-B', v_id_hp_it6002, v_id_hk1, v_id_gv5, v_id_phong3,
           80, 60, 1500000, 'DANG_MO', true,
           4, '{"tiet_bd": 6, "tiet_kt": 9, "phong": "201-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6002-25K1-B');

    -- ===================== LOP HOC PHAN HTTT CSDL =====================
    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-IT6126-25K1-A', v_id_hp_it6126, v_id_hk1, v_id_gv4, v_id_phong1,
           80, 65, 2000000, 'DANG_MO', true,
           5, '{"tiet_bd": 6, "tiet_kt": 10, "phong": "101-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6126-25K1-A');

    -- ===================== LOP HOC PHAN MANG MAY TINH =====================
    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-IT6083-25K1-A', v_id_hp_it6083, v_id_hk1, v_id_gv5, v_id_phong2,
           40, 38, 1500000, 'DANG_MO', true,
           6, '{"tiet_bd": 1, "tiet_kt": 4, "phong": "103-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6083-25K1-A');

    -- ===================== LOP HOC PHAN KIEN TRUC MT =====================
    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-IT6067-25K1-A', v_id_hp_it6067, v_id_hk1, v_id_gv6, v_id_phong1,
           80, 55, 1500000, 'DANG_MO', true,
           2, '{"tiet_bd": 1, "tiet_kt": 4, "phong": "101-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6067-25K1-A');

    -- ===================== LOP HOC PHAN GIAI TICH =====================
    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-BS6002-25K1-A', v_id_hp_bs6002, v_id_hk1, v_id_gv1, v_id_phong1,
           120, 110, 1500000, 'DANG_MO', true,
           2, '{"tiet_bd": 6, "tiet_kt": 9, "phong": "101-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-BS6002-25K1-A');

    -- ===================== LOP HOC PHAN DAI SO TUYEN TINH =====================
    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-BS6001-25K1-A', v_id_hp_bs6001, v_id_hk1, v_id_gv2, v_id_phong3,
           120, 100, 1500000, 'DANG_MO', true,
           3, '{"tiet_bd": 6, "tiet_kt": 9, "phong": "201-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-BS6001-25K1-A');

    -- ===================== LOP HOC PHAN TRIET HOC =====================
    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-LP6010-25K1-A', v_id_hp_lp6010, v_id_hk1, v_id_gv3, v_id_phong1,
           150, 140, 1000000, 'DANG_MO', true,
           6, '{"tiet_bd": 1, "tiet_kt": 4, "phong": "101-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-LP6010-25K1-A');

    -- ===================== LOP HOC PHAN ANH VAN 1 =====================
    INSERT INTO lop_hoc_phan (ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, id_phong_hoc, 
                               si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, status_publish,
                               thu_tkb, thoi_khoa_bieu_json)
    SELECT 'LHP-NN6001-25K1-A', v_id_hp_nn6001, v_id_hk1, v_id_gv7, v_id_phong2,
           40, 35, 2500000, 'DANG_MO', true,
           4, '{"tiet_bd": 1, "tiet_kt": 5, "phong": "103-A1"}'
    WHERE NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-NN6001-25K1-A');

    RAISE NOTICE 'Seed lop_hoc_phan completed: 15 lop hoc phan';

END $$;
