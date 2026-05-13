-- =====================================================
-- SEED DATA: CO VAN HOC TAP, TKB BLOCK, GV BUSY SLOT
-- =====================================================

DO $$
DECLARE
    v_id_khoa_cntt bigint;
    v_id_hk1 bigint;
    v_id_gv1 bigint; v_id_gv2 bigint; v_id_gv3 bigint;
    v_id_cv1 bigint; v_id_cv2 bigint; v_id_cv3 bigint;
BEGIN
    -- Get IDs
    SELECT id_khoa INTO v_id_khoa_cntt FROM khoa WHERE ma_khoa = 'CNTT';
    SELECT id_hoc_ky INTO v_id_hk1 FROM hoc_ky WHERE nam_hoc = '2025-2026' AND ky_thu = 1;
    SELECT id_giang_vien INTO v_id_gv1 FROM giang_vien WHERE ma_giang_vien = 'GV001';
    SELECT id_giang_vien INTO v_id_gv2 FROM giang_vien WHERE ma_giang_vien = 'GV002';
    SELECT id_giang_vien INTO v_id_gv3 FROM giang_vien WHERE ma_giang_vien = 'GV003';

    -- ===================== CO VAN HOC TAP =====================
    INSERT INTO co_van_hoc_tap (ten_co_van, sdt, email, id_khoa)
    SELECT 'Nguyen Van Co Van 1', '0981234567', 'covan1@haui.edu.vn', v_id_khoa_cntt
    ON CONFLICT DO NOTHING
    RETURNING id_co_van INTO v_id_cv1;
    
    INSERT INTO co_van_hoc_tap (ten_co_van, sdt, email, id_khoa)
    SELECT 'Tran Thi Co Van 2', '0981234568', 'covan2@haui.edu.vn', v_id_khoa_cntt
    ON CONFLICT DO NOTHING
    RETURNING id_co_van INTO v_id_cv2;
    
    INSERT INTO co_van_hoc_tap (ten_co_van, sdt, email, id_khoa)
    SELECT 'Le Van Co Van 3', '0981234569', 'covan3@haui.edu.vn', v_id_khoa_cntt
    ON CONFLICT DO NOTHING
    RETURNING id_co_van INTO v_id_cv3;

    -- Cap nhat co van cho sinh vien
    UPDATE sinh_vien SET id_co_van = v_id_cv1 WHERE ma_sinh_vien LIKE 'SV24%' AND ma_sinh_vien <= 'SV2415';
    UPDATE sinh_vien SET id_co_van = v_id_cv2 WHERE ma_sinh_vien LIKE 'SV24%' AND ma_sinh_vien > 'SV2415' AND ma_sinh_vien <= 'SV2430';
    UPDATE sinh_vien SET id_co_van = v_id_cv3 WHERE ma_sinh_vien LIKE 'SV24C%';

    -- ===================== TKB BLOCK =====================
    INSERT INTO tkb_block (id_hoc_ky, ma_block, ten_block, json_slots, danh_sach_id_hoc_phan_json, bat_buoc_chon_ca_block)
    SELECT v_id_hk1, 'BLOCK_SANG_T234', 'Block sang Thu 2,3,4',
           '[{"thu":2,"tiet_bd":1,"tiet_kt":5},{"thu":3,"tiet_bd":1,"tiet_kt":5},{"thu":4,"tiet_bd":1,"tiet_kt":5}]',
           '[]', false
    WHERE NOT EXISTS (SELECT 1 FROM tkb_block WHERE ma_block = 'BLOCK_SANG_T234');
    
    INSERT INTO tkb_block (id_hoc_ky, ma_block, ten_block, json_slots, danh_sach_id_hoc_phan_json, bat_buoc_chon_ca_block)
    SELECT v_id_hk1, 'BLOCK_CHIEU_T234', 'Block chieu Thu 2,3,4',
           '[{"thu":2,"tiet_bd":6,"tiet_kt":10},{"thu":3,"tiet_bd":6,"tiet_kt":10},{"thu":4,"tiet_bd":6,"tiet_kt":10}]',
           '[]', false
    WHERE NOT EXISTS (SELECT 1 FROM tkb_block WHERE ma_block = 'BLOCK_CHIEU_T234');

    -- ===================== GV BUSY SLOT =====================
    INSERT INTO gv_busy_slot (id_giang_vien, hoc_ky_id, thu, tiet_bd, tiet_kt, loai, ly_do)
    SELECT v_id_gv1, v_id_hk1, 6, 1, 5, 'NGHI', 'Lich hop khoa'
    WHERE NOT EXISTS (SELECT 1 FROM gv_busy_slot WHERE id_giang_vien = v_id_gv1 AND thu = 6 AND tiet_bd = 1);
    
    INSERT INTO gv_busy_slot (id_giang_vien, hoc_ky_id, thu, tiet_bd, tiet_kt, loai, ly_do)
    SELECT v_id_gv2, v_id_hk1, 5, 6, 10, 'LICH_KHAC', 'Day lop khac'
    WHERE NOT EXISTS (SELECT 1 FROM gv_busy_slot WHERE id_giang_vien = v_id_gv2 AND thu = 5 AND tiet_bd = 6);
    
    INSERT INTO gv_busy_slot (id_giang_vien, hoc_ky_id, thu, tiet_bd, tiet_kt, loai, ly_do)
    SELECT v_id_gv3, v_id_hk1, 7, 1, 5, 'NGHI', 'Nghi phep'
    WHERE NOT EXISTS (SELECT 1 FROM gv_busy_slot WHERE id_giang_vien = v_id_gv3 AND thu = 7 AND tiet_bd = 1);

    RAISE NOTICE 'Seed co van, tkb block, gv busy slot completed';

END $$;
