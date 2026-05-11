-- ============================================================================
-- SEED E2E REGISTRATION TEST DATA
-- Chạy SAU seed_ctdt_k17.sql và seed_logic_testing_data.sql
-- Yêu cầu: DataSeeder đã chạy (có admin, sv01, gv01, GV_SEED, Khoa CNTT)
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
    v_id_gv         bigint;
    v_id_hp         bigint;
    v_id_lhp        bigint;
    v_pw_hash       text;
    i               int;
    v_ma_hp         text;
    v_hp_ids        bigint[];
    v_gv_ids        bigint[];
BEGIN
    -- ── 0. Lấy reference data ─────────────────────────────────
    SELECT id_nganh INTO v_id_nganh FROM nganh_dao_tao WHERE ma_nganh = 'CT863' LIMIT 1;
    SELECT id_khoa INTO v_id_khoa FROM khoa WHERE ma_khoa = 'CNTT' LIMIT 1;
    SELECT id_hoc_ky INTO v_id_hk FROM hoc_ky WHERE trang_thai_hien_hanh = true LIMIT 1;

    IF v_id_nganh IS NULL OR v_id_khoa IS NULL OR v_id_hk IS NULL THEN
        RAISE NOTICE 'Thiếu dữ liệu nền (nganh/khoa/hocky). Chạy seed_ctdt_k17.sql + seed_logic_testing_data.sql trước.';
        RETURN;
    END IF;

    -- Lấy password hash từ sv01 (đã encode bởi DataSeeder)
    SELECT password_hash INTO v_pw_hash FROM tai_khoan WHERE username = 'sv01' LIMIT 1;
    IF v_pw_hash IS NULL THEN
        RAISE NOTICE 'Chưa có tài khoản sv01. Khởi động backend trước để DataSeeder chạy.';
        RETURN;
    END IF;

    -- ── 1. Lớp hành chính K17 + K18 ──────────────────────────
    SELECT id_lop INTO v_id_lop_k17 FROM lop WHERE ma_lop = 'CNTT-K17' LIMIT 1;
    IF v_id_lop_k17 IS NULL THEN
        INSERT INTO lop(ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
        VALUES ('CNTT-K17', 'Công nghệ thông tin - K17', 2023, v_id_nganh)
        RETURNING id_lop INTO v_id_lop_k17;
    END IF;

    INSERT INTO lop(ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    VALUES ('CNTT-K18', 'Công nghệ thông tin - K18', 2024, v_id_nganh)
    ON CONFLICT(ma_lop) DO UPDATE SET ten_lop = EXCLUDED.ten_lop
    RETURNING id_lop INTO v_id_lop_k18;
    IF v_id_lop_k18 IS NULL THEN
        SELECT id_lop INTO v_id_lop_k18 FROM lop WHERE ma_lop = 'CNTT-K18';
    END IF;

    -- ── 2. Tạo 9 tài khoản SV thêm (sv02..sv10) ─────────────
    FOR i IN 2..10 LOOP
        DECLARE
            v_username text := 'sv' || lpad(i::text, 2, '0');
            v_msv      text := '2023' || lpad(i::text, 4, '0');
            v_ten      text;
            v_lop_id   bigint;
        BEGIN
            -- Tên sinh viên
            v_ten := CASE
                WHEN i = 2  THEN 'Trần Thị Bích Ngọc'
                WHEN i = 3  THEN 'Lê Văn Cường'
                WHEN i = 4  THEN 'Phạm Minh Đức'
                WHEN i = 5  THEN 'Nguyễn Thị Hoa'
                WHEN i = 6  THEN 'Hoàng Văn Fong'
                WHEN i = 7  THEN 'Vũ Thị Giang'
                WHEN i = 8  THEN 'Đặng Hữu Hải'
                WHEN i = 9  THEN 'Bùi Thị Oanh'
                WHEN i = 10 THEN 'Cao Xuân Khánh'
            END;
            -- SV 2-7 thuộc K17, SV 8-10 thuộc K18
            v_lop_id := CASE WHEN i <= 7 THEN v_id_lop_k17 ELSE v_id_lop_k18 END;

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
                    '00120001' || lpad(i::text, 4, '0'),
                    v_username || '@student.edu.vn',
                    CASE WHEN i % 2 = 0 THEN 'NU' ELSE 'NAM' END,
                    v_id_sv
                );
            END IF;
        END;
    END LOOP;

    -- ── 3. Tạo thêm 4 giảng viên (GV02..GV05) ───────────────
    FOR i IN 2..5 LOOP
        DECLARE
            v_ma_gv  text := 'GV_' || lpad(i::text, 2, '0');
            v_ten_gv text;
        BEGIN
            v_ten_gv := CASE
                WHEN i = 2 THEN 'TS. Nguyễn Văn An'
                WHEN i = 3 THEN 'PGS.TS. Trần Thị Bình'
                WHEN i = 4 THEN 'ThS. Lê Hoàng Cảnh'
                WHEN i = 5 THEN 'TS. Phạm Đình Dũng'
            END;
            IF NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = v_ma_gv) THEN
                INSERT INTO giang_vien(ma_giang_vien, ten_giang_vien, email, id_khoa)
                VALUES (v_ma_gv, v_ten_gv, lower(replace(v_ma_gv, '_', '')) || '@haui.edu.vn', v_id_khoa);
            END IF;
        END;
    END LOOP;

    -- Thu thập GV IDs
    v_gv_ids := ARRAY(
        SELECT id_giang_vien FROM giang_vien
        WHERE ma_giang_vien IN ('GV_SEED', 'GV_02', 'GV_03', 'GV_04', 'GV_05')
        ORDER BY id_giang_vien
    );

    -- ── 4. Registration Windows (PRE + OFFICIAL) ──────────────
    -- PRE: mở rộng (tất cả cohort, tất cả ngành)
    IF NOT EXISTS (
        SELECT 1 FROM registration_window
        WHERE id_hoc_ky = v_id_hk AND phase = 'PRE' AND nam_nhap_hoc IS NULL AND id_nganh IS NULL
    ) THEN
        INSERT INTO registration_window(id_hoc_ky, phase, nam_nhap_hoc, id_nganh, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (
            v_id_hk, 'PRE', NULL, NULL,
            NOW() - INTERVAL '7 days',
            NOW() + INTERVAL '30 days',
            'Pha PRE mở cho tất cả cohort/ngành - seed E2E test',
            'seed_script', NOW(), NOW()
        );
    END IF;

    -- OFFICIAL: mở cho K17 CNTT
    IF NOT EXISTS (
        SELECT 1 FROM registration_window
        WHERE id_hoc_ky = v_id_hk AND phase = 'OFFICIAL' AND nam_nhap_hoc = 2023
    ) THEN
        INSERT INTO registration_window(id_hoc_ky, phase, nam_nhap_hoc, id_nganh, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (
            v_id_hk, 'OFFICIAL', 2023, v_id_nganh,
            NOW() - INTERVAL '3 days',
            NOW() + INTERVAL '30 days',
            'Pha OFFICIAL cho K17 CNTT - seed E2E test',
            'seed_script', NOW(), NOW()
        );
    END IF;

    -- OFFICIAL: mở cho K18 CNTT
    IF NOT EXISTS (
        SELECT 1 FROM registration_window
        WHERE id_hoc_ky = v_id_hk AND phase = 'OFFICIAL' AND nam_nhap_hoc = 2024
    ) THEN
        INSERT INTO registration_window(id_hoc_ky, phase, nam_nhap_hoc, id_nganh, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (
            v_id_hk, 'OFFICIAL', 2024, v_id_nganh,
            NOW() - INTERVAL '3 days',
            NOW() + INTERVAL '30 days',
            'Pha OFFICIAL cho K18 CNTT - seed E2E test',
            'seed_script', NOW(), NOW()
        );
    END IF;

    -- ── 5. Lớp học phần PUBLISHED (8 môn, mỗi môn 2-3 lớp) ──
    -- Thu thập HP IDs cho 8 môn chính
    DECLARE
        v_courses text[] := ARRAY[
            'BS6001', 'BS6002', 'IT6015', 'IT6002',
            'IT6016', 'IT6120', 'IT6126', 'IT6083'
        ];
        v_course_name text;
        v_si_so int;
        v_sections int;
        v_thu int;
        v_tiet text;
        v_phong text;
        v_gv_idx int;
    BEGIN
        FOR i IN 1..array_length(v_courses, 1) LOOP
            v_ma_hp := v_courses[i];
            SELECT id_hoc_phan INTO v_id_hp FROM hoc_phan WHERE ma_hoc_phan = v_ma_hp LIMIT 1;
            IF v_id_hp IS NULL THEN CONTINUE; END IF;

            -- 2-3 sections per course
            v_sections := CASE WHEN i <= 4 THEN 3 ELSE 2 END;

            FOR j IN 1..v_sections LOOP
                DECLARE
                    v_ma_lhp text := v_ma_hp || '_HK' || v_id_hk || '_' || j;
                BEGIN
                    IF NOT EXISTS (SELECT 1 FROM lop_hoc_phan WHERE ma_lop_hp = v_ma_lhp) THEN
                        v_si_so := 40 + (j * 5);
                        v_thu := 2 + ((i + j - 1) % 6);  -- Thứ 2-7
                        v_tiet := CASE
                            WHEN j = 1 THEN '1-3'
                            WHEN j = 2 THEN '4-6'
                            ELSE '7-9'
                        END;
                        v_phong := 'A' || (100 + i) || j;
                        v_gv_idx := ((i + j - 1) % array_length(v_gv_ids, 1)) + 1;

                        INSERT INTO lop_hoc_phan(
                            ma_lop_hp, id_hoc_phan, id_hoc_ky, id_giang_vien,
                            si_so_toi_da, si_so_thuc_te, hoc_phi, trang_thai,
                            thoi_khoa_bieu_json, status_publish, version
                        ) VALUES (
                            v_ma_lhp, v_id_hp, v_id_hk, v_gv_ids[v_gv_idx],
                            v_si_so, 0, 1500000 + (i * 100000), 'DANG_MO',
                            ('[{"thu": ' || v_thu || ', "tiet": "' || v_tiet || '", "phong": "' || v_phong || '"}]')::jsonb,
                            'PUBLISHED', 1
                        );
                    END IF;
                END;
            END LOOP;
        END LOOP;
    END;

    -- ── 6. Pre-Registration Intents (mỗi SV chọn 4-6 môn) ───
    DECLARE
        v_sv_rec   RECORD;
        v_hp_list  text[];
        v_priority int;
    BEGIN
        FOR v_sv_rec IN
            SELECT sv.id_sinh_vien, l.nam_nhap_hoc
            FROM sinh_vien sv JOIN lop l ON sv.id_lop = l.id_lop
            WHERE sv.ma_sinh_vien LIKE '2023%' OR sv.ma_sinh_vien LIKE '2024%'
            ORDER BY sv.id_sinh_vien
        LOOP
            -- K17 students chọn môn cơ sở, K18 chọn đại cương
            IF v_sv_rec.nam_nhap_hoc = 2023 THEN
                v_hp_list := ARRAY['IT6015', 'IT6002', 'IT6120', 'IT6126', 'IT6083'];
            ELSE
                v_hp_list := ARRAY['BS6001', 'BS6002', 'IT6016', 'IT6015'];
            END IF;

            v_priority := 1;
            FOR idx IN 1..array_length(v_hp_list, 1) LOOP
                SELECT id_hoc_phan INTO v_id_hp FROM hoc_phan WHERE ma_hoc_phan = v_hp_list[idx] LIMIT 1;
                IF v_id_hp IS NOT NULL THEN
                    INSERT INTO pre_registration_intent(id_sinh_vien, id_hoc_ky, id_hoc_phan, priority, created_at, updated_at)
                    VALUES (v_sv_rec.id_sinh_vien, v_id_hk, v_id_hp, v_priority, NOW(), NOW())
                    ON CONFLICT ON CONSTRAINT uk_prereg_intent_sv_hk_hp DO NOTHING;
                    v_priority := v_priority + 1;
                END IF;
            END LOOP;
        END LOOP;
    END;

    RAISE NOTICE '✅ Seed E2E registration test data completed!';
    RAISE NOTICE '   - 10 sinh viên (sv01..sv10, password: 123456)';
    RAISE NOTICE '   - 5 giảng viên (GV_SEED, GV_02..GV_05)';
    RAISE NOTICE '   - Registration windows PRE + OFFICIAL đang mở';
    RAISE NOTICE '   - 20 lớp học phần PUBLISHED (8 môn x 2-3 lớp)';
    RAISE NOTICE '   - ~50 pre-registration intents';

END $$;
