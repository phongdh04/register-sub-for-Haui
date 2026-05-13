-- =====================================================
-- SEED DATA: LOP (Classes) va SINH VIEN
-- =====================================================

DO $$
DECLARE
    v_id_nganh_cntt bigint;
    v_id_nganh_ck bigint;
    v_id_nganh_dien bigint;
    v_id_nganh_kt bigint;
    v_id_nganh_nn bigint;
    
    -- Lop variables
    v_lop1 bigint; v_lop2 bigint; v_lop3 bigint; v_lop4 bigint; v_lop5 bigint;
    v_lop6 bigint; v_lop7 bigint; v_lop8 bigint;
    
    -- TK variables
    v_sv1 bigint; v_sv2 bigint; v_sv3 bigint; v_sv4 bigint; v_sv5 bigint;
    v_sv6 bigint; v_sv7 bigint; v_sv8 bigint; v_sv9 bigint; v_sv10 bigint;
    
    -- Bien dem
    i integer;
    v_username text;
    v_ma_sv text;
    v_ho_ten text;
    v_email text;
    v_password_hash text;
    v_id_tk bigint;
    v_id_sv bigint;
    v_id_vi bigint;
BEGIN
    -- Get Nganh IDs
    SELECT id_nganh INTO v_id_nganh_cntt FROM nganh_dao_tao WHERE ma_nganh = 'CT863';
    SELECT id_nganh INTO v_id_nganh_ck FROM nganh_dao_tao WHERE ma_nganh = 'CT201';
    SELECT id_nganh INTO v_id_nganh_dien FROM nganh_dao_tao WHERE ma_nganh = 'CT301';
    SELECT id_nganh INTO v_id_nganh_kt FROM nganh_dao_tao WHERE ma_nganh = 'CT501';
    SELECT id_nganh INTO v_id_nganh_nn FROM nganh_dao_tao WHERE ma_nganh = 'CT601';

    -- Password hash for '123456'
    v_password_hash := '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O';

    -- ===================== LOP CNTT =====================
    INSERT INTO lop (ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    SELECT 'DHCNTT24A', 'Dai hoc CNTT K24A', 2024, v_id_nganh_cntt
    ON CONFLICT (ma_lop) DO NOTHING
    RETURNING id_lop INTO v_lop1;
    
    INSERT INTO lop (ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    SELECT 'DHCNTT24B', 'Dai hoc CNTT K24B', 2024, v_id_nganh_cntt
    ON CONFLICT (ma_lop) DO NOTHING
    RETURNING id_lop INTO v_lop2;
    
    INSERT INTO lop (ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    SELECT 'DHCNTT24C', 'Dai hoc CNTT K24C', 2024, v_id_nganh_cntt
    ON CONFLICT (ma_lop) DO NOTHING
    RETURNING id_lop INTO v_lop3;
    
    -- Lop nam 3
    INSERT INTO lop (ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    SELECT 'DHCNTT22A', 'Dai hoc CNTT K22A', 2022, v_id_nganh_cntt
    ON CONFLICT (ma_lop) DO NOTHING
    RETURNING id_lop INTO v_lop4;
    
    -- Lop nam 4
    INSERT INTO lop (ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    SELECT 'DHCNTT21A', 'Dai hoc CNTT K21A', 2021, v_id_nganh_cntt
    ON CONFLICT (ma_lop) DO NOTHING
    RETURNING id_lop INTO v_lop5;
    
    -- ===================== LOP CO KHI =====================
    INSERT INTO lop (ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    SELECT 'DHOTO24A', 'Dai hoc O to K24A', 2024, v_id_nganh_ck
    ON CONFLICT (ma_lop) DO NOTHING
    RETURNING id_lop INTO v_lop6;
    
    -- ===================== LOP DIEN =====================
    INSERT INTO lop (ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    SELECT 'DHDien24A', 'Dai hoc Dien K24A', 2024, v_id_nganh_dien
    ON CONFLICT (ma_lop) DO NOTHING
    RETURNING id_lop INTO v_lop7;
    
    -- ===================== LOP KINH TE =====================
    INSERT INTO lop (ma_lop, ten_lop, nam_nhap_hoc, id_nganh)
    SELECT 'DHKT24A', 'Dai hoc Kinh te K24A', 2024, v_id_nganh_kt
    ON CONFLICT (ma_lop) DO NOTHING
    RETURNING id_lop INTO v_lop8;

    -- ===================== SINH VIEN CNTT K24 (30 SV) =====================
    FOR i IN 1..30 LOOP
        v_ma_sv := 'SV24' || LPAD(i::text, 4, '0');
        v_username := v_ma_sv;
        
        -- Tao tai khoan
        INSERT INTO tai_khoan (username, password_hash, role, trang_thai)
        VALUES (v_username, v_password_hash, 'STUDENT', 'ACTIVE')
        ON CONFLICT (username) DO NOTHING
        RETURNING id INTO v_id_tk;
        
        IF v_id_tk IS NULL THEN
            SELECT id INTO v_id_tk FROM tai_khoan WHERE username = v_username;
        END IF;
        
        -- Tao sinh vien
        INSERT INTO sinh_vien (ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
        VALUES (v_ma_sv, 'Sinh vien ' || i, v_lop1, v_id_tk)
        ON CONFLICT (ma_sinh_vien) DO NOTHING
        RETURNING id_sinh_vien INTO v_id_sv;
        
        IF v_id_sv IS NULL THEN
            SELECT id_sinh_vien INTO v_id_sv FROM sinh_vien WHERE ma_sinh_vien = v_ma_sv;
        END IF;
        
        -- Tao vi
        INSERT INTO vi_sinh_vien (id_sinh_vien, so_du)
        VALUES (v_id_sv, 500000)
        ON CONFLICT (id_sinh_vien) DO NOTHING;
        
    END LOOP;

    -- ===================== SINH VIEN CNTT K24B (30 SV) =====================
    FOR i IN 1..30 LOOP
        v_ma_sv := 'SV24B' || LPAD(i::text, 4, '0');
        v_username := v_ma_sv;
        
        INSERT INTO tai_khoan (username, password_hash, role, trang_thai)
        VALUES (v_username, v_password_hash, 'STUDENT', 'ACTIVE')
        ON CONFLICT (username) DO NOTHING
        RETURNING id INTO v_id_tk;
        
        IF v_id_tk IS NULL THEN
            SELECT id INTO v_id_tk FROM tai_khoan WHERE username = v_username;
        END IF;
        
        INSERT INTO sinh_vien (ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
        VALUES (v_ma_sv, 'Sinh vien B' || i, v_lop2, v_id_tk)
        ON CONFLICT (ma_sinh_vien) DO NOTHING
        RETURNING id_sinh_vien INTO v_id_sv;
        
        IF v_id_sv IS NULL THEN
            SELECT id_sinh_vien INTO v_id_sv FROM sinh_vien WHERE ma_sinh_vien = v_ma_sv;
        END IF;
        
        INSERT INTO vi_sinh_vien (id_sinh_vien, so_du)
        VALUES (v_id_sv, 500000)
        ON CONFLICT (id_sinh_vien) DO NOTHING;
        
    END LOOP;

    -- ===================== SINH VIEN CNTT K24C (30 SV) =====================
    FOR i IN 1..30 LOOP
        v_ma_sv := 'SV24C' || LPAD(i::text, 4, '0');
        v_username := v_ma_sv;
        
        INSERT INTO tai_khoan (username, password_hash, role, trang_thai)
        VALUES (v_username, v_password_hash, 'STUDENT', 'ACTIVE')
        ON CONFLICT (username) DO NOTHING
        RETURNING id INTO v_id_tk;
        
        IF v_id_tk IS NULL THEN
            SELECT id INTO v_id_tk FROM tai_khoan WHERE username = v_username;
        END IF;
        
        INSERT INTO sinh_vien (ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
        VALUES (v_ma_sv, 'Sinh vien C' || i, v_lop3, v_id_tk)
        ON CONFLICT (ma_sinh_vien) DO NOTHING
        RETURNING id_sinh_vien INTO v_id_sv;
        
        IF v_id_sv IS NULL THEN
            SELECT id_sinh_vien INTO v_id_sv FROM sinh_vien WHERE ma_sinh_vien = v_ma_sv;
        END IF;
        
        INSERT INTO vi_sinh_vien (id_sinh_vien, so_du)
        VALUES (v_id_sv, 500000)
        ON CONFLICT (id_sinh_vien) DO NOTHING;
        
    END LOOP;

    -- ===================== SINH VIEN CNTT K22 (nam 3) =====================
    FOR i IN 1..25 LOOP
        v_ma_sv := 'SV22' || LPAD(i::text, 4, '0');
        v_username := v_ma_sv;
        
        INSERT INTO tai_khoan (username, password_hash, role, trang_thai)
        VALUES (v_username, v_password_hash, 'STUDENT', 'ACTIVE')
        ON CONFLICT (username) DO NOTHING
        RETURNING id INTO v_id_tk;
        
        IF v_id_tk IS NULL THEN
            SELECT id INTO v_id_tk FROM tai_khoan WHERE username = v_username;
        END IF;
        
        INSERT INTO sinh_vien (ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
        VALUES (v_ma_sv, 'Sinh vien K22 ' || i, v_lop4, v_id_tk)
        ON CONFLICT (ma_sinh_vien) DO NOTHING
        RETURNING id_sinh_vien INTO v_id_sv;
        
        IF v_id_sv IS NULL THEN
            SELECT id_sinh_vien INTO v_id_sv FROM sinh_vien WHERE ma_sinh_vien = v_ma_sv;
        END IF;
        
        INSERT INTO vi_sinh_vien (id_sinh_vien, so_du)
        VALUES (v_id_sv, 300000)
        ON CONFLICT (id_sinh_vien) DO NOTHING;
        
    END LOOP;

    -- ===================== SINH VIEN CNTT K21 (nam 4) =====================
    FOR i IN 1..20 LOOP
        v_ma_sv := 'SV21' || LPAD(i::text, 4, '0');
        v_username := v_ma_sv;
        
        INSERT INTO tai_khoan (username, password_hash, role, trang_thai)
        VALUES (v_username, v_password_hash, 'STUDENT', 'ACTIVE')
        ON CONFLICT (username) DO NOTHING
        RETURNING id INTO v_id_tk;
        
        IF v_id_tk IS NULL THEN
            SELECT id INTO v_id_tk FROM tai_khoan WHERE username = v_username;
        END IF;
        
        INSERT INTO sinh_vien (ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
        VALUES (v_ma_sv, 'Sinh vien K21 ' || i, v_lop5, v_id_tk)
        ON CONFLICT (ma_sinh_vien) DO NOTHING
        RETURNING id_sinh_vien INTO v_id_sv;
        
        IF v_id_sv IS NULL THEN
            SELECT id_sinh_vien INTO v_id_sv FROM sinh_vien WHERE ma_sinh_vien = v_ma_sv;
        END IF;
        
        INSERT INTO vi_sinh_vien (id_sinh_vien, so_du)
        VALUES (v_id_sv, 200000)
        ON CONFLICT (id_sinh_vien) DO NOTHING;
        
    END LOOP;

    -- ===================== SINH VIEN O TO K24 (20 SV) =====================
    FOR i IN 1..20 LOOP
        v_ma_sv := 'SV24O' || LPAD(i::text, 4, '0');
        v_username := v_ma_sv;
        
        INSERT INTO tai_khoan (username, password_hash, role, trang_thai)
        VALUES (v_username, v_password_hash, 'STUDENT', 'ACTIVE')
        ON CONFLICT (username) DO NOTHING
        RETURNING id INTO v_id_tk;
        
        IF v_id_tk IS NULL THEN
            SELECT id INTO v_id_tk FROM tai_khoan WHERE username = v_username;
        END IF;
        
        INSERT INTO sinh_vien (ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
        VALUES (v_ma_sv, 'Sinh vien O to ' || i, v_lop6, v_id_tk)
        ON CONFLICT (ma_sinh_vien) DO NOTHING
        RETURNING id_sinh_vien INTO v_id_sv;
        
        IF v_id_sv IS NULL THEN
            SELECT id_sinh_vien INTO v_id_sv FROM sinh_vien WHERE ma_sinh_vien = v_ma_sv;
        END IF;
        
        INSERT INTO vi_sinh_vien (id_sinh_vien, so_du)
        VALUES (v_id_sv, 500000)
        ON CONFLICT (id_sinh_vien) DO NOTHING;
        
    END LOOP;

    -- ===================== SINH VIEN DIEN K24 (20 SV) =====================
    FOR i IN 1..20 LOOP
        v_ma_sv := 'SV24D' || LPAD(i::text, 4, '0');
        v_username := v_ma_sv;
        
        INSERT INTO tai_khoan (username, password_hash, role, trang_thai)
        VALUES (v_username, v_password_hash, 'STUDENT', 'ACTIVE')
        ON CONFLICT (username) DO NOTHING
        RETURNING id INTO v_id_tk;
        
        IF v_id_tk IS NULL THEN
            SELECT id INTO v_id_tk FROM tai_khoan WHERE username = v_username;
        END IF;
        
        INSERT INTO sinh_vien (ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
        VALUES (v_ma_sv, 'Sinh vien Dien ' || i, v_lop7, v_id_tk)
        ON CONFLICT (ma_sinh_vien) DO NOTHING
        RETURNING id_sinh_vien INTO v_id_sv;
        
        IF v_id_sv IS NULL THEN
            SELECT id_sinh_vien INTO v_id_sv FROM sinh_vien WHERE ma_sinh_vien = v_ma_sv;
        END IF;
        
        INSERT INTO vi_sinh_vien (id_sinh_vien, so_du)
        VALUES (v_id_sv, 500000)
        ON CONFLICT (id_sinh_vien) DO NOTHING;
        
    END LOOP;

    -- ===================== SINH VIEN KINH TE K24 (25 SV) =====================
    FOR i IN 1..25 LOOP
        v_ma_sv := 'SV24K' || LPAD(i::text, 4, '0');
        v_username := v_ma_sv;
        
        INSERT INTO tai_khoan (username, password_hash, role, trang_thai)
        VALUES (v_username, v_password_hash, 'STUDENT', 'ACTIVE')
        ON CONFLICT (username) DO NOTHING
        RETURNING id INTO v_id_tk;
        
        IF v_id_tk IS NULL THEN
            SELECT id INTO v_id_tk FROM tai_khoan WHERE username = v_username;
        END IF;
        
        INSERT INTO sinh_vien (ma_sinh_vien, ho_ten, id_lop, tai_khoan_id)
        VALUES (v_ma_sv, 'Sinh vien KT ' || i, v_lop8, v_id_tk)
        ON CONFLICT (ma_sinh_vien) DO NOTHING
        RETURNING id_sinh_vien INTO v_id_sv;
        
        IF v_id_sv IS NULL THEN
            SELECT id_sinh_vien INTO v_id_sv FROM sinh_vien WHERE ma_sinh_vien = v_ma_sv;
        END IF;
        
        INSERT INTO vi_sinh_vien (id_sinh_vien, so_du)
        VALUES (v_id_sv, 500000)
        ON CONFLICT (id_sinh_vien) DO NOTHING;
        
    END LOOP;

    RAISE NOTICE 'Seed lop, sinh vien, vi completed: 8 lop, 200 sinh vien';

END $$;
