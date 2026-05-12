-- ============================================================================
-- SEED: TEST GÁN LỊCH & GÁN GIẢNG VIÊN CHO LỚP SHELL
-- Chạy SAU seed_e2e_registration_test.sql
--
-- Mục đích: Tạo ~30 SV đăng ký dự kiến (PRE intent) cho 1 môn cụ thể
--           (IT6015 - Kỹ thuật lập trình), sau đó tạo 4 lớp SHELL
--           để admin test workflow: assign-schedule → assign-gv → publish.
--
-- Kịch bản test:
--   1. Admin xem demand → thấy ~30 intent cho IT6015
--   2. Admin plan-sections hoặc dùng 4 lớp SHELL có sẵn
--   3. Admin gán GV (POST /{id}/assign-giang-vien) → auto-promote nếu có lịch
--   4. Admin gán lịch (POST /{id}/assign-schedule) → auto-promote nếu có GV
--   5. Admin publish (POST /{id}/publish) → DANG_MO
--   6. SV thấy lớp vừa publish, đăng ký chính thức
-- ============================================================================

DO $$
DECLARE
    v_id_nganh      bigint;
    v_id_khoa       bigint;
    v_id_hk         bigint;
    v_id_lop_k17    bigint;
    v_id_lop_k18    bigint;
    v_id_user       bigint;
    v_id_sv         bigint;
    v_id_hp_it6015  bigint;  -- Kỹ thuật lập trình
    v_id_hp_it6120  bigint;  -- Lập trình hướng đối tượng
    v_id_hp_it6002  bigint;  -- Cấu trúc dữ liệu và giải thuật
    v_pw_hash       text;
    i               int;
BEGIN
    -- ── 0. Lấy reference data ─────────────────────────────────
    SELECT id_nganh INTO v_id_nganh FROM nganh_dao_tao WHERE ma_nganh = 'CT863' LIMIT 1;
    SELECT id_khoa INTO v_id_khoa FROM khoa WHERE ma_khoa = 'CNTT' LIMIT 1;
    SELECT id_hoc_ky INTO v_id_hk FROM hoc_ky WHERE trang_thai_hien_hanh = true LIMIT 1;
    SELECT id_lop INTO v_id_lop_k17 FROM lop WHERE ma_lop = 'CNTT-K17' LIMIT 1;
    SELECT id_lop INTO v_id_lop_k18 FROM lop WHERE ma_lop = 'CNTT-K18' LIMIT 1;

    IF v_id_nganh IS NULL OR v_id_khoa IS NULL OR v_id_hk IS NULL THEN
        RAISE NOTICE '❌ Thiếu dữ liệu nền. Chạy seed_ctdt_k17.sql + seed_e2e_registration_test.sql trước.';
        RETURN;
    END IF;
    IF v_id_lop_k17 IS NULL OR v_id_lop_k18 IS NULL THEN
        RAISE NOTICE '❌ Thiếu lớp CNTT-K17/K18. Chạy seed_e2e_registration_test.sql trước.';
        RETURN;
    END IF;

    -- Lấy password hash chuẩn
    SELECT password_hash INTO v_pw_hash FROM tai_khoan WHERE username = 'sv01' LIMIT 1;
    IF v_pw_hash IS NULL THEN
        RAISE NOTICE '❌ Chưa có tài khoản sv01.';
        RETURN;
    END IF;

    -- Lấy học phần IDs
    SELECT id_hoc_phan INTO v_id_hp_it6015 FROM hoc_phan WHERE ma_hoc_phan = 'IT6015' LIMIT 1;
    SELECT id_hoc_phan INTO v_id_hp_it6120 FROM hoc_phan WHERE ma_hoc_phan = 'IT6120' LIMIT 1;
    SELECT id_hoc_phan INTO v_id_hp_it6002 FROM hoc_phan WHERE ma_hoc_phan = 'IT6002' LIMIT 1;

    IF v_id_hp_it6015 IS NULL THEN
        RAISE NOTICE '❌ Không tìm thấy học phần IT6015. Chạy seed_ctdt_k17.sql trước.';
        RETURN;
    END IF;

    -- ── 1. Tạo thêm lớp hành chính ──────────────────────────
    -- K17-B: lớp thứ 2 của K17
    IF NOT EXISTS (SELECT 1 FROM lop WHERE ma_lop = 'CNTT-K17-B') THEN
        INSERT INTO lop(ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
        VALUES ('CNTT-K17-B', 'Công nghệ thông tin - K17 - Lớp B', 2023, v_id_nganh);
    END IF;

    DECLARE
        v_id_lop_k17b bigint;
    BEGIN
        SELECT id_lop INTO v_id_lop_k17b FROM lop WHERE ma_lop = 'CNTT-K17-B';

        -- ── 2. Tạo 30 sinh viên (sv11..sv40) ────────────────
        FOR i IN 11..40 LOOP
            DECLARE
                v_username text := 'sv' || lpad(i::text, 2, '0');
                v_msv      text := '2023' || lpad(i::text, 4, '0');
                v_ten      text;
                v_lop_id   bigint;
            BEGIN
                -- Đặt tên đa dạng
                v_ten := CASE
                    WHEN i = 11 THEN 'Nguyễn Minh Quân'
                    WHEN i = 12 THEN 'Trần Thị Phương Anh'
                    WHEN i = 13 THEN 'Lê Hoàng Minh'
                    WHEN i = 14 THEN 'Phạm Thị Hằng'
                    WHEN i = 15 THEN 'Đỗ Văn Thắng'
                    WHEN i = 16 THEN 'Vũ Thị Mai'
                    WHEN i = 17 THEN 'Hoàng Đức Anh'
                    WHEN i = 18 THEN 'Nguyễn Thị Trang'
                    WHEN i = 19 THEN 'Trần Văn Long'
                    WHEN i = 20 THEN 'Bùi Thị Ngân'
                    WHEN i = 21 THEN 'Lê Công Vinh'
                    WHEN i = 22 THEN 'Phạm Thị Linh'
                    WHEN i = 23 THEN 'Đặng Văn Hùng'
                    WHEN i = 24 THEN 'Ngô Thị Hà'
                    WHEN i = 25 THEN 'Trịnh Văn Đạt'
                    WHEN i = 26 THEN 'Hồ Thị Yến'
                    WHEN i = 27 THEN 'Cao Minh Tuấn'
                    WHEN i = 28 THEN 'Dương Thị Thảo'
                    WHEN i = 29 THEN 'Lương Văn Bình'
                    WHEN i = 30 THEN 'Tạ Thị Cúc'
                    WHEN i = 31 THEN 'Phan Văn Sơn'
                    WHEN i = 32 THEN 'Đinh Thị Hiền'
                    WHEN i = 33 THEN 'Vương Đức Mạnh'
                    WHEN i = 34 THEN 'Nguyễn Thị Lan'
                    WHEN i = 35 THEN 'Lý Văn Phúc'
                    WHEN i = 36 THEN 'Mai Thị Hương'
                    WHEN i = 37 THEN 'Tô Văn Kiên'
                    WHEN i = 38 THEN 'Châu Thị Ngọc'
                    WHEN i = 39 THEN 'Lâm Quốc Huy'
                    WHEN i = 40 THEN 'Trương Thị Diệu'
                END;

                -- SV 11-25 → K17-A, SV 26-35 → K17-B, SV 36-40 → K18
                v_lop_id := CASE
                    WHEN i <= 25 THEN v_id_lop_k17
                    WHEN i <= 35 THEN v_id_lop_k17b
                    ELSE v_id_lop_k18
                END;

                -- Tạo tài khoản
                IF NOT EXISTS (SELECT 1 FROM tai_khoan WHERE username = v_username) THEN
                    INSERT INTO tai_khoan(username, password_hash, role, trang_thai)
                    VALUES (v_username, v_pw_hash, 'STUDENT', 'ACTIVE')
                    RETURNING id INTO v_id_user;
                ELSE
                    SELECT id INTO v_id_user FROM tai_khoan WHERE username = v_username;
                END IF;

                -- Tạo sinh viên
                INSERT INTO sinh_vien(ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
                VALUES (v_msv, v_ten, v_lop_id, v_id_user)
                ON CONFLICT(ma_sinh_vien) DO UPDATE SET ho_ten = EXCLUDED.ho_ten, id_lop = EXCLUDED.id_lop;

                SELECT id_sinh_vien INTO v_id_sv FROM sinh_vien WHERE ma_sinh_vien = v_msv;

                -- Hồ sơ
                IF NOT EXISTS (SELECT 1 FROM ho_so_sinh_vien WHERE id_sinh_vien = v_id_sv) THEN
                    INSERT INTO ho_so_sinh_vien(so_cccd, email, gioi_tinh, id_sinh_vien)
                    VALUES (
                        '00120002' || lpad(i::text, 4, '0'),
                        v_username || '@student.edu.vn',
                        CASE WHEN i % 2 = 0 THEN 'NU' ELSE 'NAM' END,
                        v_id_sv
                    );
                END IF;

                -- ── 3. PRE INTENT: Tất cả 30 SV đăng ký IT6015 ──
                INSERT INTO pre_registration_intent(id_sinh_vien, id_hoc_ky, id_hoc_phan, priority, created_at, updated_at)
                VALUES (v_id_sv, v_id_hk, v_id_hp_it6015, 1, NOW(), NOW())
                ON CONFLICT ON CONSTRAINT uk_prereg_intent_sv_hk_hp DO NOTHING;

                -- Thêm 1 số SV đăng ký thêm IT6120 và IT6002 để diversify
                IF i <= 20 AND v_id_hp_it6120 IS NOT NULL THEN
                    INSERT INTO pre_registration_intent(id_sinh_vien, id_hoc_ky, id_hoc_phan, priority, created_at, updated_at)
                    VALUES (v_id_sv, v_id_hk, v_id_hp_it6120, 2, NOW(), NOW())
                    ON CONFLICT ON CONSTRAINT uk_prereg_intent_sv_hk_hp DO NOTHING;
                END IF;

                IF i <= 15 AND v_id_hp_it6002 IS NOT NULL THEN
                    INSERT INTO pre_registration_intent(id_sinh_vien, id_hoc_ky, id_hoc_phan, priority, created_at, updated_at)
                    VALUES (v_id_sv, v_id_hk, v_id_hp_it6002, 3, NOW(), NOW())
                    ON CONFLICT ON CONSTRAINT uk_prereg_intent_sv_hk_hp DO NOTHING;
                END IF;
            END;
        END LOOP;
    END;

    -- ── 4. Tạo 4 lớp SHELL cho IT6015 (chưa gán GV, chưa gán lịch) ──
    -- Đây là lớp dùng để test workflow: assign-schedule → assign-gv → publish
    DECLARE
        v_shell_idx int;
        v_ma_lhp    text;
    BEGIN
        FOR v_shell_idx IN 1..4 LOOP
            v_ma_lhp := 'IT6015_HK' || v_id_hk || '_SHELL_' || v_shell_idx;
            IF NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = v_ma_lhp) THEN
                INSERT INTO lop_hoc_phan(
                    ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien,
                    si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai,
                    thoi_khoa_bieu_json, status_publish, version
                ) VALUES (
                    v_ma_lhp,
                    v_id_hp_it6015,
                    v_id_hk,
                    NULL,               -- ❌ Chưa gán GV
                    10 * v_shell_idx,   -- 10, 20, 30, 40 chỗ
                    0,
                    1500000,
                    'CHUA_MO',          -- sẽ đổi sang DANG_MO khi publish
                    NULL,               -- ❌ Chưa gán lịch
                    'SHELL',            -- trạng thái xuất bản: SHELL
                    1
                );
            END IF;
        END LOOP;
    END;

    -- ── 5. Tạo 2 lớp SCHEDULED cho IT6015 (đã gán GV + lịch, chờ publish) ──
    DECLARE
        v_sched_idx int;
        v_ma_lhp    text;
        v_gv_ids    bigint[];
        v_thu       int;
        v_tiet      text;
    BEGIN
        v_gv_ids := ARRAY(
            SELECT id_giang_vien FROM giang_vien
            WHERE ma_giang_vien IN ('GV_SEED', 'GV_02', 'GV_03')
            ORDER BY id_giang_vien
            LIMIT 3
        );

        FOR v_sched_idx IN 1..2 LOOP
            v_ma_lhp := 'IT6015_HK' || v_id_hk || '_SCHED_' || v_sched_idx;
            v_thu := CASE WHEN v_sched_idx = 1 THEN 3 ELSE 5 END;  -- Thứ 3, Thứ 5
            v_tiet := CASE WHEN v_sched_idx = 1 THEN '1-3' ELSE '4-6' END;

            IF NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = v_ma_lhp) THEN
                INSERT INTO lop_hoc_phan(
                    ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien,
                    si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai,
                    thoi_khoa_bieu_json, status_publish, version
                ) VALUES (
                    v_ma_lhp,
                    v_id_hp_it6015,
                    v_id_hk,
                    v_gv_ids[v_sched_idx],  -- ✅ Đã gán GV
                    40,
                    0,
                    1500000,
                    'CHUA_MO',
                    ('[{"thu": ' || v_thu || ', "tiet": "' || v_tiet || '", "phong": "B20' || v_sched_idx || '"}]')::jsonb,
                    'SCHEDULED',            -- ✅ Đã SCHEDULED, chờ publish
                    1
                );
            END IF;
        END LOOP;
    END;

    -- ── 6. Tạo 1 lớp PARTIAL (chỉ có GV, chưa có lịch) ──
    DECLARE
        v_ma_lhp text := 'IT6015_HK' || v_id_hk || '_PARTIAL_GV';
        v_gv_id  bigint;
    BEGIN
        SELECT id_giang_vien INTO v_gv_id FROM giang_vien WHERE ma_giang_vien = 'GV_04' LIMIT 1;
        IF v_gv_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = v_ma_lhp) THEN
            INSERT INTO lop_hoc_phan(
                ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien,
                si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai,
                thoi_khoa_bieu_json, status_publish, version
            ) VALUES (
                v_ma_lhp,
                v_id_hp_it6015,
                v_id_hk,
                v_gv_id,            -- ✅ Có GV
                35,
                0,
                1500000,
                'CHUA_MO',
                NULL,               -- ❌ Chưa có lịch
                'SHELL',            -- vẫn SHELL vì thiếu lịch
                1
            );
        END IF;
    END;

    -- ── 7. Tạo 1 lớp PARTIAL (chỉ có lịch, chưa có GV) ──
    DECLARE
        v_ma_lhp text := 'IT6015_HK' || v_id_hk || '_PARTIAL_TKB';
    BEGIN
        IF NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = v_ma_lhp) THEN
            INSERT INTO lop_hoc_phan(
                ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien,
                si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai,
                thoi_khoa_bieu_json, status_publish, version
            ) VALUES (
                v_ma_lhp,
                v_id_hp_it6015,
                v_id_hk,
                NULL,               -- ❌ Chưa có GV
                35,
                0,
                1500000,
                'CHUA_MO',
                '[{"thu": 4, "tiet": "7-9", "phong": "C301"}]'::jsonb,  -- ✅ Có lịch
                'SHELL',            -- vẫn SHELL vì thiếu GV
                1
            );
        END IF;
    END;

    RAISE NOTICE '✅ Seed assign-schedule test data completed!';
    RAISE NOTICE '   📚 Môn focus: IT6015 - Kỹ thuật lập trình';
    RAISE NOTICE '   👥 30 SV mới (sv11..sv40, password: 123456)';
    RAISE NOTICE '   📝 ~30 PRE intents cho IT6015 + thêm IT6120, IT6002';
    RAISE NOTICE '   🏗️  4 lớp SHELL (chưa GV + chưa lịch) → test assign đầy đủ';
    RAISE NOTICE '   📅 2 lớp SCHEDULED (đã GV + lịch) → test publish';
    RAISE NOTICE '   🔧 1 lớp PARTIAL (chỉ GV) → test assign-schedule auto-promote';
    RAISE NOTICE '   🔧 1 lớp PARTIAL (chỉ lịch) → test assign-gv auto-promote';
    RAISE NOTICE '';
    RAISE NOTICE '   Test workflow:';
    RAISE NOTICE '   1. GET  /api/v1/admin/pre-registrations/demand?hocKyId={hkId}';
    RAISE NOTICE '   2. POST /api/v1/admin/lop-hoc-phan/{id}/assign-giang-vien';
    RAISE NOTICE '   3. POST /api/v1/admin/lop-hoc-phan/{id}/assign-schedule';
    RAISE NOTICE '   4. POST /api/v1/admin/lop-hoc-phan/{id}/publish';
    RAISE NOTICE '   5. POST /api/v1/admin/lop-hoc-phan/bulk-publish?hocKyId={hkId}';

END $$;
