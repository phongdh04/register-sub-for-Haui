-- =====================================================
-- SEED DATA: TAI KHOAN, VI, GIANG VIEN
-- =====================================================

DO $$
DECLARE
    v_id_khoa_cntt bigint;
    v_id_khoa_ck bigint;
    v_id_khoa_dien bigint;
    v_id_khoa_kt bigint;
    v_id_khoa_nn bigint;
    v_id_hk1 bigint;
    v_id_hk2 bigint;
    
    -- GV variables
    v_gv1 bigint; v_gv2 bigint; v_gv3 bigint; v_gv4 bigint; v_gv5 bigint;
    v_gv6 bigint; v_gv7 bigint; v_gv8 bigint; v_gv9 bigint; v_gv10 bigint;
    v_gv11 bigint; v_gv12 bigint;
BEGIN
    -- Get Khoa IDs
    SELECT id_khoa INTO v_id_khoa_cntt FROM khoa WHERE ma_khoa = 'CNTT';
    SELECT id_khoa INTO v_id_khoa_ck FROM khoa WHERE ma_khoa = 'COKHI';
    SELECT id_khoa INTO v_id_khoa_dien FROM khoa WHERE ma_khoa = 'DIEN';
    SELECT id_khoa INTO v_id_khoa_kt FROM khoa WHERE ma_khoa = 'KINHTE';
    SELECT id_khoa INTO v_id_khoa_nn FROM khoa WHERE ma_khoa = 'NNDL';
    
    -- Get HocKy ID
    SELECT id_hoc_ky INTO v_id_hk1 FROM hoc_ky WHERE nam_hoc = '2025-2026' AND ky_thu = 1;
    SELECT id_hoc_ky INTO v_id_hk2 FROM hoc_ky WHERE nam_hoc = '2024-2025' AND ky_thu = 1;

    -- ===================== TAI KHOAN GIANG VIEN =====================
    -- GV CNTT
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv001', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv1;
    
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv002', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv2;
    
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv003', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv3;
    
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv004', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv4;
    
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv005', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv5;
    
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv006', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv6;
    
    -- GV Co khi
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv007', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv7;
    
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv008', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv8;
    
    -- GV Dien
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv009', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv9;
    
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv010', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv10;
    
    -- GV Kinh te
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv011', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv11;
    
    -- GV Ngoai ngu
    INSERT INTO tai_khoan (username, password_hash, role, trang_thai) VALUES
        ('gv012', '$2a$10$N9qo8uLOickgx2ZMRZoMy.Mrq4L4p.Z8d8M2l8p.sP9c6cX6gC5O', 'LECTURER', 'ACTIVE')
    ON CONFLICT (username) DO NOTHING
    RETURNING id INTO v_gv12;

    -- ===================== GIANG VIEN =====================
    -- GV CNTT (password: 123456)
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV001', 'Nguyen Van A', 'nvana@haui.edu.vn', '0912345678', 'Giao su', v_id_khoa_cntt, v_gv1
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV001');
    
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV002', 'Tran Thi B', 'ttb@haui.edu.vn', '0912345679', 'Pho giao su', v_id_khoa_cntt, v_gv2
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV002');
    
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV003', 'Le Van C', 'lvc@haui.edu.vn', '0912345680', 'TS', v_id_khoa_cntt, v_gv3
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV003');
    
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV004', 'Pham Thi D', 'ptd@haui.edu.vn', '0912345681', 'ThS', v_id_khoa_cntt, v_gv4
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV004');
    
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV005', 'Hoang Van E', 'hve@haui.edu.vn', '0912345682', 'TS', v_id_khoa_cntt, v_gv5
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV005');
    
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV006', 'Nguyen Thi F', 'ntf@haui.edu.vn', '0912345683', 'ThS', v_id_khoa_cntt, v_gv6
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV006');
    
    -- GV Co khi
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV007', 'Tran Van G', 'tvg@haui.edu.vn', '0912345684', 'Giao su', v_id_khoa_ck, v_gv7
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV007');
    
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV008', 'Le Thi H', 'lth@haui.edu.vn', '0912345685', 'TS', v_id_khoa_ck, v_gv8
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV008');
    
    -- GV Dien
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV009', 'Pham Van I', 'pvi@haui.edu.vn', '0912345686', 'Pho giao su', v_id_khoa_dien, v_gv9
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV009');
    
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV010', 'Nguyen Van K', 'nvk@haui.edu.vn', '0912345687', 'TS', v_id_khoa_dien, v_gv10
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV010');
    
    -- GV Kinh te
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV011', 'Tran Thi L', 'ttl@haui.edu.vn', '0912345688', 'ThS', v_id_khoa_kt, v_gv11
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV011');
    
    -- GV Ngoai ngu
    INSERT INTO giang_vien (ma_giang_vien, ten_giang_vien, email, sdt, hoc_ham_hoc_vi, id_khoa, tai_khoan_id)
    SELECT 'GV012', 'John Smith', 'jsmith@haui.edu.vn', '0912345689', 'ThS', v_id_khoa_nn, v_gv12
    WHERE NOT EXISTS (SELECT 1 FROM giang_vien WHERE ma_giang_vien = 'GV012');

    RAISE NOTICE 'Seed tai khoan, giang vien completed: 12 giang vien';

END $$;
