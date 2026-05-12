-- ============================================================================
-- SEED MULTI-NGÀNH: 3 ngành (CNTT đã có, thêm Kế toán + Ngôn ngữ Anh)
-- Mỗi ngành 1 SV test, CTDT riêng, lớp HP PUBLISHED, PRE intent, window
-- Chạy SAU seed_e2e_registration_test.sql
-- ============================================================================
DO $$
DECLARE
    v_id_hk       bigint;
    v_pw_hash     text;
    -- Kế toán
    v_id_khoa_kt  bigint;
    v_id_nganh_kt bigint;
    v_id_ctdt_kt  bigint;
    v_id_lop_kt   bigint;
    v_id_user_kt  bigint;
    v_id_sv_kt    bigint;
    -- Ngôn ngữ Anh
    v_id_khoa_nn  bigint;
    v_id_nganh_nn bigint;
    v_id_ctdt_nn  bigint;
    v_id_lop_nn   bigint;
    v_id_user_nn  bigint;
    v_id_sv_nn    bigint;
    -- GV
    v_id_gv       bigint;
    v_id_hp       bigint;
    v_ma          text;
BEGIN
    SELECT id_hoc_ky INTO v_id_hk FROM hoc_ky WHERE trang_thai_hien_hanh = true LIMIT 1;
    SELECT password_hash INTO v_pw_hash FROM tai_khoan WHERE username = 'sv01' LIMIT 1;
    IF v_id_hk IS NULL OR v_pw_hash IS NULL THEN
        RAISE NOTICE '❌ Thiếu học kỳ hoặc sv01. Chạy seed trước.';
        RETURN;
    END IF;
    SELECT id_giang_vien INTO v_id_gv FROM giang_vien WHERE ma_giang_vien = 'GV_SEED' LIMIT 1;

    -- ═══════════════════════════════════════════════════════════
    -- NGÀNH 1: KẾ TOÁN (Khoa Kế toán - Kiểm toán)
    -- ═══════════════════════════════════════════════════════════
    INSERT INTO khoa(ma_khoa, ten_khoa, mo_ta)
    VALUES ('KTKT', 'Khoa Kế toán - Kiểm toán', 'Kế toán kiểm toán HaUI')
    ON CONFLICT(ma_khoa) DO UPDATE SET ten_khoa = EXCLUDED.ten_khoa
    RETURNING id_khoa INTO v_id_khoa_kt;
    IF v_id_khoa_kt IS NULL THEN SELECT id_khoa INTO v_id_khoa_kt FROM khoa WHERE ma_khoa = 'KTKT'; END IF;

    INSERT INTO nganh_dao_tao(ma_nganh, ten_nganh, he_dao_tao, id_khoa)
    VALUES ('KT401', 'Kế toán', 'Đại học', v_id_khoa_kt)
    ON CONFLICT(ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_kt;
    IF v_id_nganh_kt IS NULL THEN SELECT id_nganh INTO v_id_nganh_kt FROM nganh_dao_tao WHERE ma_nganh = 'KT401'; END IF;

    -- Học phần Kế toán
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES
        ('KT6001', 'Nguyên lý kế toán', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6002', 'Kế toán tài chính 1', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6003', 'Kế toán tài chính 2', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["KT6002"]}'),
        ('KT6004', 'Kế toán quản trị', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6005', 'Kiểm toán căn bản', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6006', 'Thuế', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('KT6007', 'Phân tích báo cáo tài chính', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('KT6008', 'Kế toán ngân hàng', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('KT6009', 'Tin học ứng dụng kế toán', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('KT6010', 'Thực tập kế toán doanh nghiệp', 4, 'CHUYEN_NGANH', '{}', '{}'),
        ('KT6011', 'Kinh tế vi mô', 3, 'DAI_CUONG', '{}', '{}'),
        ('KT6012', 'Kinh tế vĩ mô', 3, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- CTDT Kế toán
    INSERT INTO chuong_trinh_dao_tao(id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    VALUES (v_id_nganh_kt, 130, '4 năm', 'Tốt nghiệp THPT', 'Đào tạo cử nhân Kế toán', 2023)
    RETURNING id_ctdt INTO v_id_ctdt_kt;

    -- Map HP vào CTDT KT (gồm một số môn đại cương chung)
    INSERT INTO ctdt_hoc_phan(id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt_kt, id_hoc_phan, 'DAI_CUONG', true, 1
    FROM hoc_phan WHERE ma_hoc_phan IN ('LP6010','LP6011','LP6004','LP6003','BS6002','KT6011','KT6012');

    INSERT INTO ctdt_hoc_phan(id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt_kt, id_hoc_phan, 'CO_SO_NGANH', true, 3
    FROM hoc_phan WHERE ma_hoc_phan IN ('KT6001','KT6002','KT6003','KT6004','KT6005');

    INSERT INTO ctdt_hoc_phan(id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt_kt, id_hoc_phan, 'CHUYEN_NGANH', true, 5
    FROM hoc_phan WHERE ma_hoc_phan IN ('KT6006','KT6007','KT6008','KT6009','KT6010');

    -- Lớp hành chính + SV Kế toán
    INSERT INTO lop(ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    VALUES ('KT-K17', 'Kế toán - K17', 2023, v_id_nganh_kt)
    ON CONFLICT(ma_lop) DO UPDATE SET ten_lop = EXCLUDED.ten_lop
    RETURNING id_lop INTO v_id_lop_kt;
    IF v_id_lop_kt IS NULL THEN SELECT id_lop INTO v_id_lop_kt FROM lop WHERE ma_lop = 'KT-K17'; END IF;

    IF NOT EXISTS (SELECT 1 FROM tai_khoan WHERE username = 'sv_kt01') THEN
        INSERT INTO tai_khoan(username, password_hash, role, trang_thai)
        VALUES ('sv_kt01', v_pw_hash, 'STUDENT', 'ACTIVE') RETURNING id INTO v_id_user_kt;
    ELSE SELECT id INTO v_id_user_kt FROM tai_khoan WHERE username = 'sv_kt01'; END IF;

    INSERT INTO sinh_vien(ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
    VALUES ('2023KT01', 'Trần Thị Kế Toán', v_id_lop_kt, v_id_user_kt)
    ON CONFLICT(ma_sinh_vien) DO UPDATE SET ho_ten = EXCLUDED.ho_ten;
    SELECT id_sinh_vien INTO v_id_sv_kt FROM sinh_vien WHERE ma_sinh_vien = '2023KT01';

    IF NOT EXISTS (SELECT 1 FROM ho_so_sinh_vien WHERE id_sinh_vien = v_id_sv_kt) THEN
        INSERT INTO ho_so_sinh_vien(so_cccd, email, gioi_tinh, id_sinh_vien)
        VALUES ('001200099001', 'sv_kt01@student.edu.vn', 'NU', v_id_sv_kt);
    END IF;

    -- LHP cho KT (PUBLISHED)
    FOR v_ma IN SELECT unnest(ARRAY['KT6001','KT6002','KT6004','KT6005','KT6011']) LOOP
        SELECT id_hoc_phan INTO v_id_hp FROM hoc_phan WHERE ma_hoc_phan = v_ma LIMIT 1;
        IF v_id_hp IS NOT NULL AND NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = v_ma || '_KT1') THEN
            INSERT INTO lop_hoc_phan(ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, thoi_khoa_bieu_json, status_publish, version)
            VALUES (v_ma || '_KT1', v_id_hp, v_id_hk, v_id_gv, 45, 0, 1500000, 'DANG_MO', '[{"thu":2,"tiet":"1-3","phong":"E101"}]'::jsonb, 'PUBLISHED', 1);
        END IF;
    END LOOP;

    -- PRE intent cho sv_kt01
    FOR v_ma IN SELECT unnest(ARRAY['KT6001','KT6002','KT6004','KT6005']) LOOP
        SELECT id_hoc_phan INTO v_id_hp FROM hoc_phan WHERE ma_hoc_phan = v_ma LIMIT 1;
        IF v_id_hp IS NOT NULL THEN
            INSERT INTO pre_registration_intent(id_sinh_vien, id_hoc_ky, id_hoc_phan, priority, created_at, updated_at)
            VALUES (v_id_sv_kt, v_id_hk, v_id_hp, 1, NOW(), NOW())
            ON CONFLICT ON CONSTRAINT uk_prereg_intent_sv_hk_hp DO NOTHING;
        END IF;
    END LOOP;

    -- Window cho KT
    IF NOT EXISTS (SELECT 1 FROM registration_window WHERE id_hoc_ky = v_id_hk AND phase = 'PRE' AND id_nganh = v_id_nganh_kt) THEN
        INSERT INTO registration_window(id_hoc_ky, phase, nam_nhap_hoc, id_nganh, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_id_hk, 'PRE', NULL, v_id_nganh_kt, NOW()-INTERVAL '7 days', NOW()+INTERVAL '30 days', 'PRE cho KT', 'seed', NOW(), NOW());
    END IF;
    IF NOT EXISTS (SELECT 1 FROM registration_window WHERE id_hoc_ky = v_id_hk AND phase = 'OFFICIAL' AND id_nganh = v_id_nganh_kt) THEN
        INSERT INTO registration_window(id_hoc_ky, phase, nam_nhap_hoc, id_nganh, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_id_hk, 'OFFICIAL', 2023, v_id_nganh_kt, NOW()-INTERVAL '3 days', NOW()+INTERVAL '30 days', 'OFFICIAL cho KT K17', 'seed', NOW(), NOW());
    END IF;

    -- ═══════════════════════════════════════════════════════════
    -- NGÀNH 2: NGÔN NGỮ ANH (Khoa Ngoại ngữ)
    -- ═══════════════════════════════════════════════════════════
    INSERT INTO khoa(ma_khoa, ten_khoa, mo_ta)
    VALUES ('NNgu', 'Khoa Ngoại ngữ', 'Ngoại ngữ HaUI')
    ON CONFLICT(ma_khoa) DO UPDATE SET ten_khoa = EXCLUDED.ten_khoa
    RETURNING id_khoa INTO v_id_khoa_nn;
    IF v_id_khoa_nn IS NULL THEN SELECT id_khoa INTO v_id_khoa_nn FROM khoa WHERE ma_khoa = 'NNgu'; END IF;

    INSERT INTO nganh_dao_tao(ma_nganh, ten_nganh, he_dao_tao, id_khoa)
    VALUES ('NNA01', 'Ngôn ngữ Anh', 'Đại học', v_id_khoa_nn)
    ON CONFLICT(ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_nn;
    IF v_id_nganh_nn IS NULL THEN SELECT id_nganh INTO v_id_nganh_nn FROM nganh_dao_tao WHERE ma_nganh = 'NNA01'; END IF;

    -- Học phần Ngôn ngữ Anh
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES
        ('NA6001', 'Ngữ pháp tiếng Anh 1', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('NA6002', 'Ngữ pháp tiếng Anh 2', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["NA6001"]}'),
        ('NA6003', 'Nghe hiểu 1', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('NA6004', 'Nói tiếng Anh 1', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('NA6005', 'Đọc hiểu 1', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('NA6006', 'Viết tiếng Anh 1', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('NA6007', 'Ngữ âm học', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('NA6008', 'Dịch thuật Anh-Việt', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('NA6009', 'Văn học Anh-Mỹ', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('NA6010', 'Tiếng Anh thương mại', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('NA6011', 'Ngôn ngữ học đại cương', 3, 'DAI_CUONG', '{}', '{}'),
        ('NA6012', 'Văn hóa các nước nói tiếng Anh', 2, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- CTDT Ngôn ngữ Anh
    INSERT INTO chuong_trinh_dao_tao(id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    VALUES (v_id_nganh_nn, 130, '4 năm', 'Tốt nghiệp THPT', 'Đào tạo cử nhân Ngôn ngữ Anh', 2023)
    RETURNING id_ctdt INTO v_id_ctdt_nn;

    INSERT INTO ctdt_hoc_phan(id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt_nn, id_hoc_phan, 'DAI_CUONG', true, 1
    FROM hoc_phan WHERE ma_hoc_phan IN ('LP6010','LP6011','LP6004','LP6003','NA6011','NA6012');

    INSERT INTO ctdt_hoc_phan(id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt_nn, id_hoc_phan, 'CO_SO_NGANH', true, 3
    FROM hoc_phan WHERE ma_hoc_phan IN ('NA6001','NA6002','NA6003','NA6004','NA6005','NA6006');

    INSERT INTO ctdt_hoc_phan(id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt_nn, id_hoc_phan, 'CHUYEN_NGANH', true, 5
    FROM hoc_phan WHERE ma_hoc_phan IN ('NA6007','NA6008','NA6009','NA6010');

    -- Lớp + SV Ngôn ngữ Anh
    INSERT INTO lop(ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    VALUES ('NNA-K17', 'Ngôn ngữ Anh - K17', 2023, v_id_nganh_nn)
    ON CONFLICT(ma_lop) DO UPDATE SET ten_lop = EXCLUDED.ten_lop
    RETURNING id_lop INTO v_id_lop_nn;
    IF v_id_lop_nn IS NULL THEN SELECT id_lop INTO v_id_lop_nn FROM lop WHERE ma_lop = 'NNA-K17'; END IF;

    IF NOT EXISTS (SELECT 1 FROM tai_khoan WHERE username = 'sv_nn01') THEN
        INSERT INTO tai_khoan(username, password_hash, role, trang_thai)
        VALUES ('sv_nn01', v_pw_hash, 'STUDENT', 'ACTIVE') RETURNING id INTO v_id_user_nn;
    ELSE SELECT id INTO v_id_user_nn FROM tai_khoan WHERE username = 'sv_nn01'; END IF;

    INSERT INTO sinh_vien(ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
    VALUES ('2023NN01', 'Lê Thị Ngôn Ngữ', v_id_lop_nn, v_id_user_nn)
    ON CONFLICT(ma_sinh_vien) DO UPDATE SET ho_ten = EXCLUDED.ho_ten;
    SELECT id_sinh_vien INTO v_id_sv_nn FROM sinh_vien WHERE ma_sinh_vien = '2023NN01';

    IF NOT EXISTS (SELECT 1 FROM ho_so_sinh_vien WHERE id_sinh_vien = v_id_sv_nn) THEN
        INSERT INTO ho_so_sinh_vien(so_cccd, email, gioi_tinh, id_sinh_vien)
        VALUES ('001200099002', 'sv_nn01@student.edu.vn', 'NU', v_id_sv_nn);
    END IF;

    -- LHP cho NNA
    FOR v_ma IN SELECT unnest(ARRAY['NA6001','NA6003','NA6004','NA6005','NA6011']) LOOP
        SELECT id_hoc_phan INTO v_id_hp FROM hoc_phan WHERE ma_hoc_phan = v_ma LIMIT 1;
        IF v_id_hp IS NOT NULL AND NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = v_ma || '_NN1') THEN
            INSERT INTO lop_hoc_phan(ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien, si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai, thoi_khoa_bieu_json, status_publish, version)
            VALUES (v_ma || '_NN1', v_id_hp, v_id_hk, v_id_gv, 40, 0, 1200000, 'DANG_MO', '[{"thu":3,"tiet":"1-3","phong":"F201"}]'::jsonb, 'PUBLISHED', 1);
        END IF;
    END LOOP;

    -- PRE intent cho sv_nn01
    FOR v_ma IN SELECT unnest(ARRAY['NA6001','NA6003','NA6004','NA6005']) LOOP
        SELECT id_hoc_phan INTO v_id_hp FROM hoc_phan WHERE ma_hoc_phan = v_ma LIMIT 1;
        IF v_id_hp IS NOT NULL THEN
            INSERT INTO pre_registration_intent(id_sinh_vien, id_hoc_ky, id_hoc_phan, priority, created_at, updated_at)
            VALUES (v_id_sv_nn, v_id_hk, v_id_hp, 1, NOW(), NOW())
            ON CONFLICT ON CONSTRAINT uk_prereg_intent_sv_hk_hp DO NOTHING;
        END IF;
    END LOOP;

    -- Window cho NNA
    IF NOT EXISTS (SELECT 1 FROM registration_window WHERE id_hoc_ky = v_id_hk AND phase = 'PRE' AND id_nganh = v_id_nganh_nn) THEN
        INSERT INTO registration_window(id_hoc_ky, phase, nam_nhap_hoc, id_nganh, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_id_hk, 'PRE', NULL, v_id_nganh_nn, NOW()-INTERVAL '7 days', NOW()+INTERVAL '30 days', 'PRE cho NNA', 'seed', NOW(), NOW());
    END IF;
    IF NOT EXISTS (SELECT 1 FROM registration_window WHERE id_hoc_ky = v_id_hk AND phase = 'OFFICIAL' AND id_nganh = v_id_nganh_nn) THEN
        INSERT INTO registration_window(id_hoc_ky, phase, nam_nhap_hoc, id_nganh, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_id_hk, 'OFFICIAL', 2023, v_id_nganh_nn, NOW()-INTERVAL '3 days', NOW()+INTERVAL '30 days', 'OFFICIAL cho NNA K17', 'seed', NOW(), NOW());
    END IF;

    RAISE NOTICE '✅ Seed multi-ngành completed!';
    RAISE NOTICE '  🏢 Ngành Kế toán (KT401): sv_kt01 / 123456 — 12 HP, 5 LHP';
    RAISE NOTICE '  🏢 Ngành Ngôn ngữ Anh (NNA01): sv_nn01 / 123456 — 12 HP, 5 LHP';
    RAISE NOTICE '  📌 Môn đại cương chung: LP6010, LP6011, LP6004, LP6003';
    RAISE NOTICE '  🔒 SV CNTT không thấy môn KT/NNA và ngược lại';
END $$;
