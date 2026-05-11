DO $$
DECLARE
    v_id_nganh bigint;
    v_id_lop bigint;
    v_id_user_sv01 bigint;
    v_id_user_gv01 bigint;
    v_id_gv01 bigint;
    v_id_sv01 bigint;
    v_id_hk bigint;
    v_id_hp_dsltt bigint;
    v_id_hp_ktlt bigint;
BEGIN
    -- Lấy ngành CT863
    SELECT id_nganh INTO v_id_nganh FROM nganh_dao_tao WHERE ma_nganh = 'CT863' LIMIT 1;
    
    -- Insert Lớp
    IF v_id_nganh IS NOT NULL THEN
        INSERT INTO lop(ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
        VALUES ('CNTT-K17', 'Công nghệ thông tin - K17', 2023, v_id_nganh)
        ON CONFLICT(ma_lop) DO UPDATE SET ten_lop = EXCLUDED.ten_lop
        RETURNING id_lop INTO v_id_lop;
    END IF;

    IF v_id_lop IS NULL THEN
        SELECT id_lop INTO v_id_lop FROM lop WHERE ma_lop = 'CNTT-K17';
    END IF;

    -- Lấy user sv01
    SELECT id INTO v_id_user_sv01 FROM tai_khoan WHERE username = 'sv01' LIMIT 1;

    -- Insert Sinh Viên sv01 liên kết với tài khoản
    IF v_id_user_sv01 IS NOT NULL AND v_id_lop IS NOT NULL THEN
        INSERT INTO sinh_vien(ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
        VALUES ('20231001', 'Nguyễn Văn Sinh Viên Demo', v_id_lop, v_id_user_sv01)
        ON CONFLICT(ma_sinh_vien) DO UPDATE SET ho_ten = EXCLUDED.ho_ten
        RETURNING id_sinh_vien INTO v_id_sv01;
    END IF;
    
    IF v_id_sv01 IS NULL THEN
        SELECT id_sinh_vien INTO v_id_sv01 FROM sinh_vien WHERE ma_sinh_vien = '20231001';
    END IF;

    -- Insert Hồ sơ sinh viên chi tiết cho SinhVien
    IF v_id_sv01 IS NOT NULL THEN
        IF NOT EXISTS (SELECT 1 FROM ho_so_sinh_vien WHERE id_sinh_vien = v_id_sv01) THEN
            INSERT INTO ho_so_sinh_vien(so_cccd, email, gioi_tinh, id_sinh_vien)
            VALUES ('001200012345', 'sv01@student.edu.vn', 'NAM', v_id_sv01);
        END IF;
    END IF;

    -- Lấy user gv01 / giang_vien
    SELECT id_giang_vien INTO v_id_gv01 FROM giang_vien WHERE ma_giang_vien = 'GV_SEED' LIMIT 1;

    -- Insert Học kỳ hiện hành
    SELECT id_hoc_ky INTO v_id_hk FROM hoc_ky WHERE nam_hoc = '2024-2025' AND ky_thu = 1 LIMIT 1;
    IF v_id_hk IS NULL THEN
        INSERT INTO hoc_ky(ky_thu, nam_hoc, trang_thai_hien_hanh) VALUES (1, '2024-2025', true) RETURNING id_hoc_ky INTO v_id_hk;
    END IF;

    -- Cập nhật tất cả học kỳ khác thành false để bảo vệ cờ duy nhất
    UPDATE hoc_ky SET trang_thai_hien_hanh = false WHERE id_hoc_ky != v_id_hk;

    -- Insert Lớp Học Phần cho Sinh viên chọn Giờ vàng!
    -- Lấy HP Đại số tuyến tính (BS6001), Kỹ thuật lập trình (IT6015), Cấu trúc dữ liệu và giải thuật (IT6002)
    SELECT id_hoc_phan INTO v_id_hp_dsltt FROM hoc_phan WHERE ma_hoc_phan = 'BS6001' LIMIT 1;
    IF v_id_hp_dsltt IS NOT NULL THEN
        INSERT INTO lop_hoc_phan(ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, si_so_toi_da, hoc_phi, trang_thai, thoi_khoa_bieu_json, status_publish, version)
        VALUES ('BS6001_1', v_id_hp_dsltt, v_id_hk, v_id_gv01, 50, 1500000, 'DANG_MO', '[{"thu": 2, "tiet": "1-3", "phong": "D3-501"}]'::jsonb, 'PUBLISHED', 1)
        ON CONFLICT(ma_lop_hp) DO UPDATE SET trang_thai = 'DANG_MO', status_publish = 'PUBLISHED';
    END IF;

    SELECT id_hoc_phan INTO v_id_hp_ktlt FROM hoc_phan WHERE ma_hoc_phan = 'IT6015' LIMIT 1;
    IF v_id_hp_ktlt IS NOT NULL THEN
        INSERT INTO lop_hoc_phan(ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, si_so_toi_da, hoc_phi, trang_thai, thoi_khoa_bieu_json, status_publish, version)
        VALUES ('IT6015_1', v_id_hp_ktlt, v_id_hk, v_id_gv01, 40, 2000000, 'DANG_MO', '[{"thu": 4, "tiet": "4-6", "phong": "D5-202"}]'::jsonb, 'PUBLISHED', 1)
        ON CONFLICT(ma_lop_hp) DO UPDATE SET trang_thai = 'DANG_MO', status_publish = 'PUBLISHED';
    END IF;

END $$;
