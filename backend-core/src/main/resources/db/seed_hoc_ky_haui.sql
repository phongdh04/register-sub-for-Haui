-- =====================================================
-- SEED DATA: HOC KY (Semesters)
-- =====================================================

DO $$
DECLARE
    v_id_hk1 bigint;
    v_id_hk2 bigint;
BEGIN
    -- Clear existing hoc_ky to avoid conflicts
    DELETE FROM hoc_ky;
    
    -- ===================== HOC KY 2024-2025 =====================
    INSERT INTO hoc_ky (nam_hoc, ky_thu, trang_thai_hien_hanh, tkb_trang_thai, 
                        pre_dk_mo_tu, pre_dk_mo_den, dk_chinh_thuc_tu, dk_chinh_thuc_den, tkb_revision)
    VALUES 
        ('2024-2025', 1, true, 'CONG_BO', 
         '2025-01-15 00:00:00', '2025-02-01 23:59:59',
         '2025-02-10 00:00:00', '2025-03-15 23:59:59', 1)
    RETURNING id_hoc_ky INTO v_id_hk1;

    INSERT INTO hoc_ky (nam_hoc, ky_thu, trang_thai_hien_hanh, tkb_trang_thai, tkb_revision)
    VALUES 
        ('2024-2025', 2, false, 'NHAP', 0)
    RETURNING id_hoc_ky INTO v_id_hk2;

    -- ===================== HOC KY 2025-2026 (hien tai) =====================
    INSERT INTO hoc_ky (nam_hoc, ky_thu, trang_thai_hien_hanh, tkb_trang_thai, 
                        pre_dk_mo_tu, pre_dk_mo_den, dk_chinh_thuc_tu, dk_chinh_thuc_den, tkb_revision)
    VALUES 
        ('2025-2026', 1, true, 'CONG_BO', 
         '2026-01-10 00:00:00', '2026-02-01 23:59:59',
         '2026-02-15 00:00:00', '2026-03-15 23:59:59', 2);

    INSERT INTO hoc_ky (nam_hoc, ky_thu, trang_thai_hien_hanh, tkb_trang_thai, tkb_revision)
    VALUES 
        ('2025-2026', 2, false, 'NHAP', 0);

    RAISE NOTICE 'Seed hoc_ky completed: 4 hoc_ky';

END $$;
