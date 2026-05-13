-- =====================================================
-- SEED DATA: PHONG HOC (Classrooms)
-- =====================================================

DO $$
BEGIN
    -- ===================== CO SO 1 - HAUI CHINH =====================
    -- Tang 1 - A1 (LOAI_PHANH = LY_THUYET hoac MAY_TINH)
    INSERT INTO phong_hoc (ma_phong, ten_phong, ma_co_so, loai_phong, suc_chua, trang_thai, ghi_chu)
    VALUES 
        ('101-A1', '101 - A1', 'CS1', 'LY_THUYET', 120, 'HOAT_DONG', 'Tang 1, Toa A'),
        ('102-A1', '102 - A1', 'CS1', 'LY_THUYET', 100, 'HOAT_DONG', 'Tang 1, Toa A'),
        ('103-A1', '103 - A1', 'CS1', 'MAY_TINH', 40, 'HOAT_DONG', 'Tang 1, Toa A - Phong may'),
        ('104-A1', '104 - A1', 'CS1', 'MAY_TINH', 40, 'HOAT_DONG', 'Tang 1, Toa A - Phong may'),
        ('105-A1', '105 - A1', 'CS1', 'MAY_TINH', 40, 'BAO_TRI', 'Tang 1, Toa A - Dang sua chua'),
        ('106-A1', '106 - A1', 'CS1', 'LY_THUYET', 80, 'HOAT_DONG', 'Tang 1, Toa A'),
        ('107-A1', '107 - A1', 'CS1', 'LY_THUYET', 80, 'HOAT_DONG', 'Tang 1, Toa A'),
        ('108-A1', '108 - A1', 'CS1', 'MAY_TINH', 35, 'HOAT_DONG', 'Tang 1, Toa A - Phong internet'),
        ('109-A1', '109 - A1', 'CS1', 'MAY_TINH', 35, 'HOAT_DONG', 'Tang 1, Toa A - Phong internet'),
        ('110-A1', '110 - A1', 'CS1', 'LY_THUYET', 60, 'HOAT_DONG', 'Tang 1, Toa A')
    ON CONFLICT (ma_phong) DO UPDATE SET 
        ten_phong = EXCLUDED.ten_phong, 
        suc_chua = EXCLUDED.suc_chua,
        trang_thai = EXCLUDED.trang_thai;

    -- Tang 2 - A1
    INSERT INTO phong_hoc (ma_phong, ten_phong, ma_co_so, loai_phong, suc_chua, trang_thai, ghi_chu)
    VALUES 
        ('201-A1', '201 - A1', 'CS1', 'LY_THUYET', 120, 'HOAT_DONG', 'Tang 2, Toa A'),
        ('202-A1', '202 - A1', 'CS1', 'LY_THUYET', 100, 'HOAT_DONG', 'Tang 2, Toa A'),
        ('203-A1', '203 - A1', 'CS1', 'MAY_TINH', 40, 'HOAT_DONG', 'Tang 2, Toa A - Phong may'),
        ('204-A1', '204 - A1', 'CS1', 'MAY_TINH', 40, 'HOAT_DONG', 'Tang 2, Toa A - Phong may'),
        ('205-A1', '205 - A1', 'CS1', 'LY_THUYET', 80, 'HOAT_DONG', 'Tang 2, Toa A'),
        ('206-A1', '206 - A1', 'CS1', 'LY_THUYET', 80, 'HOAT_DONG', 'Tang 2, Toa A'),
        ('207-A1', '207 - A1', 'CS1', 'LY_THUYET', 60, 'HOAT_DONG', 'Tang 2, Toa A'),
        ('208-A1', '208 - A1', 'CS1', 'KHAC', 30, 'HOAT_DONG', 'Tang 2, Toa A - Phong nghe nghiep')
    ON CONFLICT (ma_phong) DO UPDATE SET 
        ten_phong = EXCLUDED.ten_phong, 
        suc_chua = EXCLUDED.suc_chua;

    -- Tang 3 - A1
    INSERT INTO phong_hoc (ma_phong, ten_phong, ma_co_so, loai_phong, suc_chua, trang_thai, ghi_chu)
    VALUES 
        ('301-A1', '301 - A1', 'CS1', 'LY_THUYET', 120, 'HOAT_DONG', 'Tang 3, Toa A'),
        ('302-A1', '302 - A1', 'CS1', 'LY_THUYET', 100, 'HOAT_DONG', 'Tang 3, Toa A'),
        ('303-A1', '303 - A1', 'CS1', 'MAY_TINH', 40, 'HOAT_DONG', 'Tang 3, Toa A - Phong may'),
        ('304-A1', '304 - A1', 'CS1', 'MAY_TINH', 40, 'HOAT_DONG', 'Tang 3, Toa A - Phong may'),
        ('305-A1', '305 - A1', 'CS1', 'LY_THUYET', 80, 'HOAT_DONG', 'Tang 3, Toa A'),
        ('306-A1', '306 - A1', 'CS1', 'LY_THUYET', 80, 'HOAT_DONG', 'Tang 3, Toa A')
    ON CONFLICT (ma_phong) DO UPDATE SET 
        ten_phong = EXCLUDED.ten_phong, 
        suc_chua = EXCLUDED.suc_chua;

    -- ===================== TOA B - KY THUAT =====================
    INSERT INTO phong_hoc (ma_phong, ten_phong, ma_co_so, loai_phong, suc_chua, trang_thai, ghi_chu)
    VALUES 
        ('101-B1', '101 - B1', 'CS1', 'KHAC', 30, 'HOAT_DONG', 'Tang 1, Toa B - Phong dien'),
        ('102-B1', '102 - B1', 'CS1', 'KHAC', 30, 'HOAT_DONG', 'Tang 1, Toa B - Phong dien'),
        ('103-B1', '103 - B1', 'CS1', 'KHAC', 35, 'HOAT_DONG', 'Tang 1, Toa B - Phong co khi'),
        ('104-B1', '104 - B1', 'CS1', 'KHAC', 35, 'HOAT_DONG', 'Tang 1, Toa B - Phong co khi'),
        ('105-B1', '105 - B1', 'CS1', 'LY_THUYET', 100, 'HOAT_DONG', 'Tang 1, Toa B'),
        ('201-B1', '201 - B1', 'CS1', 'LY_THUYET', 120, 'HOAT_DONG', 'Tang 2, Toa B'),
        ('202-B1', '202 - B1', 'CS1', 'LY_THUYET', 100, 'HOAT_DONG', 'Tang 2, Toa B'),
        ('203-B1', '203 - B1', 'CS1', 'MAY_TINH', 40, 'HOAT_DONG', 'Tang 2, Toa B - Phong may')
    ON CONFLICT (ma_phong) DO UPDATE SET 
        ten_phong = EXCLUDED.ten_phong, 
        suc_chua = EXCLUDED.suc_chua;

    -- ===================== TOA C - NGOAI NGU =====================
    INSERT INTO phong_hoc (ma_phong, ten_phong, ma_co_so, loai_phong, suc_chua, trang_thai, ghi_chu)
    VALUES 
        ('101-C1', '101 - C1', 'CS1', 'KHAC', 30, 'HOAT_DONG', 'Tang 1, Toa C - Phong nghe'),
        ('102-C1', '102 - C1', 'CS1', 'KHAC', 30, 'HOAT_DONG', 'Tang 1, Toa C - Phong nghe'),
        ('103-C1', '103 - C1', 'CS1', 'KHAC', 30, 'HOAT_DONG', 'Tang 1, Toa C - Phong nghe'),
        ('104-C1', '104 - C1', 'CS1', 'KHAC', 30, 'HOAT_DONG', 'Tang 1, Toa C - Phong nghe'),
        ('105-C1', '105 - C1', 'CS1', 'LY_THUYET', 80, 'HOAT_DONG', 'Tang 1, Toa C'),
        ('201-C1', '201 - C1', 'CS1', 'LY_THUYET', 80, 'HOAT_DONG', 'Tang 2, Toa C'),
        ('202-C1', '202 - C1', 'CS1', 'LY_THUYET', 80, 'HOAT_DONG', 'Tang 2, Toa C')
    ON CONFLICT (ma_phong) DO UPDATE SET 
        ten_phong = EXCLUDED.ten_phong, 
        suc_chua = EXCLUDED.suc_chua;

    -- ===================== TOA D - THUC NGHIEM =====================
    INSERT INTO phong_hoc (ma_phong, ten_phong, ma_co_so, loai_phong, suc_chua, trang_thai, ghi_chu)
    VALUES 
        ('LAB101', 'LAB 101 - Hoa', 'CS1', 'THI_NGHIEM_HOA', 25, 'HOAT_DONG', 'Phong thi nghiem Hoa'),
        ('LAB102', 'LAB 102 - Ly', 'CS1', 'THI_NGHIEM_VAT_LY', 25, 'HOAT_DONG', 'Phong thi nghiem Ly'),
        ('LAB103', 'LAB 103 - Sinh', 'CS1', 'THI_NGHIEM_SINH', 25, 'HOAT_DONG', 'Phong thi nghiem Sinh hoc'),
        ('LAB104', 'LAB 104 - May mac', 'CS1', 'KHAC', 20, 'HOAT_DONG', 'Phong thi nghiem May mac')
    ON CONFLICT (ma_phong) DO UPDATE SET 
        ten_phong = EXCLUDED.ten_phong, 
        suc_chua = EXCLUDED.suc_chua;

    -- ===================== CO SO 2 =====================
    INSERT INTO phong_hoc (ma_phong, ten_phong, ma_co_so, loai_phong, suc_chua, trang_thai, ghi_chu)
    VALUES 
        ('CS2-101', '101 - CS2', 'CS2', 'LY_THUYET', 100, 'HOAT_DONG', 'Co so 2'),
        ('CS2-102', '102 - CS2', 'CS2', 'LY_THUYET', 80, 'HOAT_DONG', 'Co so 2'),
        ('CS2-103', '103 - CS2', 'CS2', 'MAY_TINH', 40, 'HOAT_DONG', 'Co so 2 - Phong may'),
        ('CS2-104', '104 - CS2', 'CS2', 'MAY_TINH', 40, 'HOAT_DONG', 'Co so 2 - Phong may'),
        ('CS2-201', '201 - CS2', 'CS2', 'LY_THUYET', 100, 'HOAT_DONG', 'Co so 2'),
        ('CS2-202', '202 - CS2', 'CS2', 'LY_THUYET', 80, 'HOAT_DONG', 'Co so 2')
    ON CONFLICT (ma_phong) DO UPDATE SET 
        ten_phong = EXCLUDED.ten_phong, 
        suc_chua = EXCLUDED.suc_chua;

    RAISE NOTICE 'Seed phong_hoc completed: 43 phong hoc';

END $$;
