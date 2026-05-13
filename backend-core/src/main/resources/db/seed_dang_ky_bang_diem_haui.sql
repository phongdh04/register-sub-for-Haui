-- =====================================================
-- SEED DATA: DANG KY, BANG DIEM, GIO HANG
-- =====================================================

DO $$
DECLARE
    v_id_hk1 bigint;
    v_id_sv bigint;
    v_id_lhp1 bigint; v_id_lhp2 bigint; v_id_lhp3 bigint; v_id_lhp4 bigint;
    v_id_lhp5 bigint; v_id_lhp6 bigint; v_id_lhp7 bigint; v_id_lhp8 bigint;
    i integer;
    v_id_dk bigint;
    v_diem_he4 numeric(3,1);
    v_diem_chu text;
BEGIN
    -- Get HocKy
    SELECT id_hoc_ky INTO v_id_hk1 FROM hoc_ky WHERE nam_hoc = '2025-2026' AND ky_thu = 1;
    
    -- Get LopHocPhan
    SELECT id_lop_hp INTO v_id_lhp1 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6015-25K1-A';
    SELECT id_lop_hp INTO v_id_lhp2 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6015-25K1-B';
    SELECT id_lop_hp INTO v_id_lhp3 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6120-25K1-A';
    SELECT id_lop_hp INTO v_id_lhp4 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6002-25K1-A';
    SELECT id_lop_hp INTO v_id_lhp5 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-BS6002-25K1-A';
    SELECT id_lop_hp INTO v_id_lhp6 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-BS6001-25K1-A';
    SELECT id_lop_hp INTO v_id_lhp7 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-LP6010-25K1-A';
    SELECT id_lop_hp INTO v_id_lhp8 FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-NN6001-25K1-A';

    -- ===================== DANG KY HOC PHAN CHO SV K24 (30 SV dau) =====================
    FOR i IN 1..30 LOOP
        SELECT id_sinh_vien INTO v_id_sv 
        FROM sinh_vien 
        WHERE ma_sinh_vien = 'SV24' || LPAD(i::text, 4, '0');
        
        CONTINUE WHEN v_id_sv IS NULL;
        
        -- Dang ky Ky thuat lap trinh
        INSERT INTO dang_ky_hoc_phan (id_sinh_vien, id_lop_hp, id_hoc_ky, ngay_dang_ky, trang_thai_dang_ky)
        VALUES (v_id_sv, v_id_lhp1, v_id_hk1, NOW() - INTERVAL '10 days', 'DA_DUYET')
        ON CONFLICT DO NOTHING
        RETURNING id_dang_ky INTO v_id_dk;
        
        IF v_id_dk IS NOT NULL THEN
            v_diem_he4 := 7.0 + (random() * 3.0);
            v_diem_chu := CASE 
                WHEN v_diem_he4 >= 8.5 THEN 'A'
                WHEN v_diem_he4 >= 7.0 THEN 'B'
                WHEN v_diem_he4 >= 5.5 THEN 'C'
                WHEN v_diem_he4 >= 4.0 THEN 'D'
                ELSE 'F'
            END;
            
            INSERT INTO bang_diem_mon (id_dang_ky, diem_he_4, diem_chu, trang_thai)
            VALUES (v_id_dk, ROUND(v_diem_he4, 1), v_diem_chu, 'CHUA_NHAP')
            ON CONFLICT (id_dang_ky) DO NOTHING;
        END IF;
        
        -- Dang ky Cau truc du lieu
        INSERT INTO dang_ky_hoc_phan (id_sinh_vien, id_lop_hp, id_hoc_ky, ngay_dang_ky, trang_thai_dang_ky)
        VALUES (v_id_sv, v_id_lhp4, v_id_hk1, NOW() - INTERVAL '10 days', 'DA_DUYET')
        ON CONFLICT DO NOTHING
        RETURNING id_dang_ky INTO v_id_dk;
        
        IF v_id_dk IS NOT NULL THEN
            v_diem_he4 := 6.5 + (random() * 3.5);
            v_diem_chu := CASE 
                WHEN v_diem_he4 >= 8.5 THEN 'A'
                WHEN v_diem_he4 >= 7.0 THEN 'B'
                WHEN v_diem_he4 >= 5.5 THEN 'C'
                WHEN v_diem_he4 >= 4.0 THEN 'D'
                ELSE 'F'
            END;
            
            INSERT INTO bang_diem_mon (id_dang_ky, diem_he_4, diem_chu, trang_thai)
            VALUES (v_id_dk, ROUND(v_diem_he4, 1), v_diem_chu, 'CHUA_NHAP')
            ON CONFLICT (id_dang_ky) DO NOTHING;
        END IF;
        
        -- Dang ky Giai tich
        INSERT INTO dang_ky_hoc_phan (id_sinh_vien, id_lop_hp, id_hoc_ky, ngay_dang_ky, trang_thai_dang_ky)
        VALUES (v_id_sv, v_id_lhp5, v_id_hk1, NOW() - INTERVAL '12 days', 'DA_DUYET')
        ON CONFLICT DO NOTHING
        RETURNING id_dang_ky INTO v_id_dk;
        
        IF v_id_dk IS NOT NULL THEN
            v_diem_he4 := 5.5 + (random() * 4.5);
            v_diem_chu := CASE 
                WHEN v_diem_he4 >= 8.5 THEN 'A'
                WHEN v_diem_he4 >= 7.0 THEN 'B'
                WHEN v_diem_he4 >= 5.5 THEN 'C'
                WHEN v_diem_he4 >= 4.0 THEN 'D'
                ELSE 'F'
            END;
            
            INSERT INTO bang_diem_mon (id_dang_ky, diem_he_4, diem_chu, trang_thai)
            VALUES (v_id_dk, ROUND(v_diem_he4, 1), v_diem_chu, 'CHUA_NHAP')
            ON CONFLICT (id_dang_ky) DO NOTHING;
        END IF;
        
        -- Dang ky Triet hoc
        INSERT INTO dang_ky_hoc_phan (id_sinh_vien, id_lop_hp, id_hoc_ky, ngay_dang_ky, trang_thai_dang_ky)
        VALUES (v_id_sv, v_id_lhp7, v_id_hk1, NOW() - INTERVAL '15 days', 'DA_DUYET')
        ON CONFLICT DO NOTHING;

        -- Dang ky Anh van
        INSERT INTO dang_ky_hoc_phan (id_sinh_vien, id_lop_hp, id_hoc_ky, ngay_dang_ky, trang_thai_dang_ky)
        VALUES (v_id_sv, v_id_lhp8, v_id_hk1, NOW() - INTERVAL '15 days', 'DA_DUYET')
        ON CONFLICT DO NOTHING;

    END LOOP;

    -- ===================== DANG KY CHO SV K22 (nam 3) =====================
    FOR i IN 1..25 LOOP
        SELECT id_sinh_vien INTO v_id_sv 
        FROM sinh_vien 
        WHERE ma_sinh_vien = 'SV22' || LPAD(i::text, 4, '0');
        
        CONTINUE WHEN v_id_sv IS NULL;
        
        -- Dang ky OOP
        INSERT INTO dang_ky_hoc_phan (id_sinh_vien, id_lop_hp, id_hoc_ky, ngay_dang_ky, trang_thai_dang_ky)
        VALUES (v_id_sv, v_id_lhp3, v_id_hk1, NOW() - INTERVAL '20 days', 'DA_DUYET')
        ON CONFLICT DO NOTHING
        RETURNING id_dang_ky INTO v_id_dk;
        
        IF v_id_dk IS NOT NULL THEN
            v_diem_he4 := 6.0 + (random() * 4.0);
            v_diem_chu := CASE 
                WHEN v_diem_he4 >= 8.5 THEN 'A'
                WHEN v_diem_he4 >= 7.0 THEN 'B'
                WHEN v_diem_he4 >= 5.5 THEN 'C'
                WHEN v_diem_he4 >= 4.0 THEN 'D'
                ELSE 'F'
            END;
            
            INSERT INTO bang_diem_mon (id_dang_ky, diem_he_4, diem_chu, trang_thai)
            VALUES (v_id_dk, ROUND(v_diem_he4, 1), v_diem_chu, 'DA_NHAP')
            ON CONFLICT (id_dang_ky) DO NOTHING;
        END IF;
        
        -- Dang ky Mang may tinh
        INSERT INTO dang_ky_hoc_phan (id_sinh_vien, id_lop_hp, id_hoc_ky, ngay_dang_ky, trang_thai_dang_ky)
        VALUES (v_id_sv, v_id_lhp1, v_id_hk1, NOW() - INTERVAL '20 days', 'DA_DUYET')
        ON CONFLICT DO NOTHING;

    END LOOP;

    -- ===================== GIO HANG DANG KY =====================
    INSERT INTO gio_hang_dang_ky (id_sinh_vien, id_lop_hp, id_hoc_ky, ngay_them)
    SELECT sv.id_sinh_vien, v_id_lhp2, v_id_hk1, NOW() - INTERVAL '1 day'
    FROM sinh_vien sv
    WHERE sv.ma_sinh_vien IN ('SV2425', 'SV2426', 'SV2427', 'SV2428', 'SV2429', 'SV2430')
    ON CONFLICT DO NOTHING;

    INSERT INTO gio_hang_dang_ky (id_sinh_vien, id_lop_hp, id_hoc_ky, ngay_them)
    SELECT sv.id_sinh_vien, v_id_lhp6, v_id_hk1, NOW() - INTERVAL '2 days'
    FROM sinh_vien sv
    WHERE sv.ma_sinh_vien IN ('SV2425', 'SV2426', 'SV2427')
    ON CONFLICT DO NOTHING;

    RAISE NOTICE 'Seed dang ky, bang diem, gio hang completed';

END $$;
