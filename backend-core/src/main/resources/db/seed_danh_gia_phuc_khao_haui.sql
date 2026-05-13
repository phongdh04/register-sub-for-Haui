-- =====================================================
-- SEED DATA: DANH GIA GIANG VIEN, PHUC KHAO, LICH THI
-- =====================================================

DO $$
DECLARE
    v_id_dk_arr bigint[];
    v_id_dk bigint;
    v_id_lhp bigint;
    v_diem_tong integer;
    v_binh_luan text;
    v_ly_do text;
    i integer;
    j integer;
BEGIN
    -- ===================== DANH GIA GIANG VIEN =====================
    -- Lay cac dang ky co diem
    FOR i IN 1..50 LOOP
        SELECT id_dang_ky INTO v_id_dk 
        FROM dang_ky_hoc_phan 
        WHERE id_dang_ky IN (
            SELECT id_dang_ky FROM bang_diem_mon 
            WHERE trang_thai = 'DA_NHAP'
        )
        ORDER BY RANDOM() 
        LIMIT 1;
        
        CONTINUE WHEN v_id_dk IS NULL;
        
        v_diem_tong := 60 + (random() * 40)::integer;
        v_binh_luan := CASE (random() * 3)::integer
            WHEN 0 THEN 'Giang vien giao duc tot, noi dung bay ro'
            WHEN 1 THEN 'Bai giang huu ich, co bai tap thuc hanh'
            WHEN 2 THEN 'Giao vien tan tinh, co the cai thien them'
            ELSE 'Noi dung kho hieu, can them vi du'
        END;
        
        INSERT INTO danh_gia_giang_vien (id_dang_ky, diem_tong, binh_luan, ngay_tao)
        VALUES (v_id_dk, v_diem_tong, v_binh_luan, NOW() - INTERVAL '5 days')
        ON CONFLICT DO NOTHING;
        
    END LOOP;

    -- ===================== YEU CAU PHUC KHAO =====================
    -- Mot so yeu cau phuc khao ngau nhien
    FOR i IN 1..10 LOOP
        SELECT id_dang_ky INTO v_id_dk 
        FROM dang_ky_hoc_phan 
        WHERE id_dang_ky IN (
            SELECT id_dang_ky FROM bang_diem_mon 
            WHERE trang_thai = 'DA_NHAP'
        )
        ORDER BY RANDOM() 
        LIMIT 1;
        
        CONTINUE WHEN v_id_dk IS NULL;
        
        v_ly_do := CASE (random() * 4)::integer
            WHEN 0 THEN 'Em binh ong diem nay chua chinh xac voi bai lam cua em'
            WHEN 1 THEN 'Em co minh chung bai thi khac voi diem cong thuc cua giao vien'
            WHEN 2 THEN 'Em thay co loi tinh trong viec cham diem'
            ELSE 'Em khang cach diem va muon phuc khao de biet ro hon'
        END;
        
        INSERT INTO yeu_cau_phuc_khao (id_dang_ky, diem_he_4_luc_nop, ly_do_sinh_vien, trang_thai)
        VALUES (v_id_dk, 5.5 + (random() * 2.0), v_ly_do, 'CHO_XU_LY')
        ON CONFLICT DO NOTHING;
        
    END LOOP;

    -- ===================== LICH THI =====================
    SELECT id_lop_hp INTO v_id_lhp FROM lop_hoc_phan WHERE ma_lop_hp = 'LHP-IT6015-25K1-A';
    
    -- Thi lan 1
    INSERT INTO lich_thi (id_lop_hp, lan_thi, ngay_thi, ca_thi, gio_bat_dau, phong_thi)
    SELECT v_id_lhp, 1, '2026-06-15', 'CA_SANG', '08:00', '101-A1'
    WHERE NOT EXISTS (SELECT 1 FROM lich_thi WHERE id_lop_hp = v_id_lhp AND lan_thi = 1);
    
    -- Thi lan 2
    INSERT INTO lich_thi (id_lop_hp, lan_thi, ngay_thi, ca_thi, gio_bat_dau, phong_thi)
    SELECT v_id_lhp, 2, '2026-07-01', 'CA_CHIEU', '14:00', '101-A1'
    WHERE NOT EXISTS (SELECT 1 FROM lich_thi WHERE id_lop_hp = v_id_lhp AND lan_thi = 2);

    RAISE NOTICE 'Seed danh gia, phuc khao, lich thi completed';

END $$;
