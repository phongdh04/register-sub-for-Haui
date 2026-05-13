-- =====================================================
-- SEED DATA: Registration Campaign (cohort-first) + update MSSV format
-- =====================================================

DO $$
DECLARE
    v_campaign_k21_pre bigint;
    v_campaign_k22_pre bigint;
    v_campaign_k24_pre bigint;
    v_campaign_k21_off bigint;
    v_campaign_k22_off bigint;
    v_campaign_k24_off bigint;
    v_hk_id bigint;
    v_hk2_id bigint;
    v_now timestamptz;
BEGIN
    v_now := NOW();

    -- =====================================================
    -- CAP NHAT MSSV SANG FORMAT MOI: YYNNSSSS
    -- YY = nam nhap hoc (2 so cuoi), NN = ma nganh, SSSS = stt
    -- Ma nganh: 01=CNTT(CT863), 02=CoKhi(CT201), 03=Dien(CT301), 04=KinhTe(CT501), 05=NNDL(CT601)
    -- =====================================================

    -- K21 (namNhapHoc=2021) -> 21xxxx
    UPDATE sinh_vien sv
    SET ma_sinh_vien = '21' || LPAD(
            (SELECT COALESCE(
                (SELECT id_nganh FROM nganh_dao_tao WHERE id_nganh = (
                    SELECT id_nganh FROM lop WHERE id_lop = sv.id_lop
                )),
                1
            )::text - 1),  -- offset: CT863=1 -> 01, CT201=2 -> 02, ...
            2, '0')
        || LPAD(
            (SELECT COUNT(*)::text FROM sinh_vien sv2
             JOIN lop l2 ON sv2.id_lop = l2.id_lop
             WHERE l2.nam_nhap_hoc = 2021
               AND sv2.id_sinh_vien <= sv.id_sinh_vien
               AND (SELECT id_nganh FROM lop WHERE id_lop = sv2.id_lop) =
                   (SELECT id_nganh FROM lop WHERE id_lop = sv.id_lop)
            ), 4, '0')
    FROM lop l
    WHERE sv.id_lop = l.id_lop
      AND l.nam_nhap_hoc = 2021;

    -- K22 (namNhapHoc=2022) -> 22xxxx
    UPDATE sinh_vien sv
    SET ma_sinh_vien = '22' || LPAD(
            (SELECT COALESCE(
                (SELECT id_nganh FROM nganh_dao_tao WHERE id_nganh = (
                    SELECT id_nganh FROM lop WHERE id_lop = sv.id_lop
                )),
                1
            )::text - 1), 2, '0')
        || LPAD(
            (SELECT COUNT(*)::text FROM sinh_vien sv2
             JOIN lop l2 ON sv2.id_lop = l2.id_lop
             WHERE l2.nam_nhap_hoc = 2022
               AND sv2.id_sinh_vien <= sv.id_sinh_vien
               AND (SELECT id_nganh FROM lop WHERE id_lop = sv2.id_lop) =
                   (SELECT id_nganh FROM lop WHERE id_lop = sv.id_lop)
            ), 4, '0')
    FROM lop l
    WHERE sv.id_lop = l.id_lop
      AND l.nam_nhap_hoc = 2022;

    -- K24 (namNhapHoc=2024) -> 24xxxx
    UPDATE sinh_vien sv
    SET ma_sinh_vien = '24' || LPAD(
            (SELECT COALESCE(
                (SELECT id_nganh FROM nganh_dao_tao WHERE id_nganh = (
                    SELECT id_nganh FROM lop WHERE id_lop = sv.id_lop
                )),
                1
            )::text - 1), 2, '0')
        || LPAD(
            (SELECT COUNT(*)::text FROM sinh_vien sv2
             JOIN lop l2 ON sv2.id_lop = l2.id_lop
             WHERE l2.nam_nhap_hoc = 2024
               AND sv2.id_sinh_vien <= sv.id_sinh_vien
               AND (SELECT id_nganh FROM lop WHERE id_lop = sv2.id_lop) =
                   (SELECT id_nganh FROM lop WHERE id_lop = sv.id_lop)
            ), 4, '0')
    FROM lop l
    WHERE sv.id_lop = l.id_lop
      AND l.nam_nhap_hoc = 2024;

    RAISE NOTICE 'Seed: updated MSSV to YYNNSSSS format';

    -- =====================================================
    -- TAO REGISTRATION CAMPAIGN
    -- =====================================================

    -- PRE campaigns
    INSERT INTO registration_campaign (ten_campaign, nam_nhap_hoc, phase, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
    VALUES
        ('Dang ky truoc K21 - 2025-2026', 2021, 'PRE',
         '2026-02-01 08:00:00+07', '2026-02-14 23:59:59+07',
         'Dang ky gio hang truoc cho K21 (nam 4)', 'system', v_now, v_now)
    RETURNING id_registration_campaign INTO v_campaign_k21_pre;

    INSERT INTO registration_campaign (ten_campaign, nam_nhap_hoc, phase, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
    VALUES
        ('Dang ky truoc K22 - 2025-2026', 2022, 'PRE',
         '2026-02-01 08:00:00+07', '2026-02-14 23:59:59+07',
         'Dang ky gio hang truoc cho K22 (nam 3)', 'system', v_now, v_now)
    RETURNING id_registration_campaign INTO v_campaign_k22_pre;

    INSERT INTO registration_campaign (ten_campaign, nam_nhap_hoc, phase, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
    VALUES
        ('Dang ky truoc K24 - 2025-2026', 2024, 'PRE',
         '2026-02-01 08:00:00+07', '2026-02-14 23:59:59+07',
         'Dang ky gio hang truoc cho K24 (nam 1)', 'system', v_now, v_now)
    RETURNING id_registration_campaign INTO v_campaign_k24_pre;

    -- OFFICIAL campaigns
    INSERT INTO registration_campaign (ten_campaign, nam_nhap_hoc, phase, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
    VALUES
        ('Dang ky chinh thuc K21 - 2025-2026', 2021, 'OFFICIAL',
         '2026-02-15 08:00:00+07', '2026-03-15 23:59:59+07',
         'Dang ky chinh thuc cho K21 (nam 4)', 'system', v_now, v_now)
    RETURNING id_registration_campaign INTO v_campaign_k21_off;

    INSERT INTO registration_campaign (ten_campaign, nam_nhap_hoc, phase, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
    VALUES
        ('Dang ky chinh thuc K22 - 2025-2026', 2022, 'OFFICIAL',
         '2026-02-15 08:00:00+07', '2026-03-15 23:59:59+07',
         'Dang ky chinh thuc cho K22 (nam 3)', 'system', v_now, v_now)
    RETURNING id_registration_campaign INTO v_campaign_k22_off;

    INSERT INTO registration_campaign (ten_campaign, nam_nhap_hoc, phase, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
    VALUES
        ('Dang ky chinh thuc K24 - 2025-2026', 2024, 'OFFICIAL',
         '2026-02-15 08:00:00+07', '2026-03-15 23:59:59+07',
         'Dang ky chinh thuc cho K24 (nam 1)', 'system', v_now, v_now)
    RETURNING id_registration_campaign INTO v_campaign_k24_off;

    RAISE NOTICE 'Seed: created 6 registration campaigns';

    -- =====================================================
    -- TU DONG TAO REGISTRATION WINDOW CHO CAC CAMPAIGN
    -- (Hien tai backend lam viec nay, o day chi la backup seed)
    -- =====================================================

    -- Lay hoc ky HK1 2025-2026 lam vi du
    SELECT id_hoc_ky INTO v_hk_id FROM hoc_ky WHERE nam_hoc = '2025-2026' AND ky_thu = 1;
    SELECT id_hoc_ky INTO v_hk2_id FROM hoc_ky WHERE nam_hoc = '2025-2026' AND ky_thu = 2;

    IF v_hk_id IS NOT NULL THEN
        -- K21 -> HK1 2025-2026 (nam 4 HK1)
        INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_campaign, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_hk_id, 'PRE', 2021, v_campaign_k21_pre,
                '2026-02-01 08:00:00+07', '2026-02-14 23:59:59+07',
                'Tu dong tao boi campaign', 'system', v_now, v_now);

        INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_campaign, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_hk_id, 'OFFICIAL', 2021, v_campaign_k21_off,
                '2026-02-15 08:00:00+07', '2026-03-15 23:59:59+07',
                'Tu dong tao boi campaign', 'system', v_now, v_now);

        -- K22 -> HK1 2025-2026 (nam 3 HK1)
        INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_campaign, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_hk_id, 'PRE', 2022, v_campaign_k22_pre,
                '2026-02-01 08:00:00+07', '2026-02-14 23:59:59+07',
                'Tu dong tao boi campaign', 'system', v_now, v_now);

        INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_campaign, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_hk_id, 'OFFICIAL', 2022, v_campaign_k22_off,
                '2026-02-15 08:00:00+07', '2026-03-15 23:59:59+07',
                'Tu dong tao boi campaign', 'system', v_now, v_now);

        -- K24 -> HK1 2025-2026 (nam 1 HK1)
        INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_campaign, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_hk_id, 'PRE', 2024, v_campaign_k24_pre,
                '2026-02-01 08:00:00+07', '2026-02-14 23:59:59+07',
                'Tu dong tao boi campaign', 'system', v_now, v_now);

        INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_campaign, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_hk_id, 'OFFICIAL', 2024, v_campaign_k24_off,
                '2026-02-15 08:00:00+07', '2026-03-15 23:59:59+07',
                'Tu dong tao boi campaign', 'system', v_now, v_now);

        RAISE NOTICE 'Seed: created 6 registration windows for HK1 2025-2026';
    END IF;

    IF v_hk2_id IS NOT NULL THEN
        -- K21 -> HK2 2025-2026 (nam 4 HK2)
        INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_campaign, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_hk2_id, 'PRE', 2021, v_campaign_k21_pre,
                '2026-06-01 08:00:00+07', '2026-06-14 23:59:59+07',
                'Tu dong tao boi campaign', 'system', v_now, v_now);

        INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_campaign, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_hk2_id, 'OFFICIAL', 2021, v_campaign_k21_off,
                '2026-06-15 08:00:00+07', '2026-07-15 23:59:59+07',
                'Tu dong tao boi campaign', 'system', v_now, v_now);

        -- K22 -> HK2 2025-2026 (nam 3 HK2)
        INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_campaign, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_hk2_id, 'PRE', 2022, v_campaign_k22_pre,
                '2026-06-01 08:00:00+07', '2026-06-14 23:59:59+07',
                'Tu dong tao boi campaign', 'system', v_now, v_now);

        INSERT INTO registration_window (id_hoc_ky, phase, nam_nhap_hoc, id_campaign, open_at, close_at, ghi_chu, created_by, created_at, updated_at)
        VALUES (v_hk2_id, 'OFFICIAL', 2022, v_campaign_k22_off,
                '2026-06-15 08:00:00+07', '2026-07-15 23:59:59+07',
                'Tu dong tao boi campaign', 'system', v_now, v_now);

        RAISE NOTICE 'Seed: created 4 registration windows for HK2 2025-2026';
    END IF;

    RAISE NOTICE 'Seed registration_campaign completed';

END $$;
