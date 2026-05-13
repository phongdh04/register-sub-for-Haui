-- =====================================================
-- SEED DATA: KHOA, NGANH, CHUONG TRINH DAO TAO
-- Theo cau truc ĐH Cong nghiep Ha Noi (HaUI) - 2024
-- =====================================================

DO $$
DECLARE
    -- Khoa IDs
    v_id_khoa_cntt bigint;
    v_id_khoa_cokhi bigint;
    v_id_khoa_dien bigint;
    v_id_khoa_kinhte bigint;
    v_id_khoa_ngoaingu bigint;
    v_id_khoa_hoa bigint;
    v_id_khoa_comay bigint;
    
    -- Nganh IDs
    v_id_nganh_cntt bigint;
    v_id_nganh_khmt bigint;
    v_id_nganh_ktpm bigint;
    v_id_nganh_attt bigint;
    v_id_nganh_httt bigint;
    v_id_nganh_cndpt bigint;
    v_id_nganh_ckoto bigint;
    v_id_nganh_ckmay bigint;
    v_id_nganh_dien bigint;
    v_id_nganh_dientu bigint;
    v_id_nganh_diekhi bigint;
    v_id_nganh_qtkd bigint;
    v_id_nganh_tcnh bigint;
    v_id_nganh_marketing bigint;
    v_id_nganh_logistics bigint;
    v_id_nganh_nna bigint;
    v_id_nganh_nntrung bigint;
    v_id_nganh_cnhh bigint;
    v_id_nganh_hh bigint;
    v_id_nganh_cntp bigint;
    v_id_nganh_maymac bigint;
    v_id_nganh_tktt bigint;
BEGIN
    -- ===================== KHOA =====================
    INSERT INTO khoa (ma_khoa, ten_khoa, mo_ta) VALUES
        ('CNTT', 'Truong Cong nghe Thong tin va Truyen thong', 'SOICT')
    ON CONFLICT (ma_khoa) DO UPDATE SET ten_khoa = EXCLUDED.ten_khoa, mo_ta = EXCLUDED.mo_ta
    RETURNING id_khoa INTO v_id_khoa_cntt;
    
    INSERT INTO khoa (ma_khoa, ten_khoa, mo_ta) VALUES
        ('COKHI', 'Truong Co khi - O to', 'SME')
    ON CONFLICT (ma_khoa) DO UPDATE SET ten_khoa = EXCLUDED.ten_khoa, mo_ta = EXCLUDED.mo_ta
    RETURNING id_khoa INTO v_id_khoa_cokhi;
    
    INSERT INTO khoa (ma_khoa, ten_khoa, mo_ta) VALUES
        ('DIEN', 'Truong Dien - Dien tu', 'SEE')
    ON CONFLICT (ma_khoa) DO UPDATE SET ten_khoa = EXCLUDED.ten_khoa, mo_ta = EXCLUDED.mo_ta
    RETURNING id_khoa INTO v_id_khoa_dien;
    
    INSERT INTO khoa (ma_khoa, ten_khoa, mo_ta) VALUES
        ('KINHTE', 'Truong Kinh te', 'SBE')
    ON CONFLICT (ma_khoa) DO UPDATE SET ten_khoa = EXCLUDED.ten_khoa, mo_ta = EXCLUDED.mo_ta
    RETURNING id_khoa INTO v_id_khoa_kinhte;
    
    INSERT INTO khoa (ma_khoa, ten_khoa, mo_ta) VALUES
        ('NNDL', 'Truong Ngoai ngu - Du lich', 'SFLT')
    ON CONFLICT (ma_khoa) DO UPDATE SET ten_khoa = EXCLUDED.ten_khoa, mo_ta = EXCLUDED.mo_ta
    RETURNING id_khoa INTO v_id_khoa_ngoaingu;
    
    INSERT INTO khoa (ma_khoa, ten_khoa, mo_ta) VALUES
        ('HOA', 'Khoa Cong nghe Hoa', 'SCT')
    ON CONFLICT (ma_khoa) DO UPDATE SET ten_khoa = EXCLUDED.ten_khoa, mo_ta = EXCLUDED.mo_ta
    RETURNING id_khoa INTO v_id_khoa_hoa;
    
    INSERT INTO khoa (ma_khoa, ten_khoa, mo_ta) VALUES
        ('COMAY', 'Khoa Cong may va Thiet ke Thoi trang', 'STF')
    ON CONFLICT (ma_khoa) DO UPDATE SET ten_khoa = EXCLUDED.ten_khoa, mo_ta = EXCLUDED.mo_ta
    RETURNING id_khoa INTO v_id_khoa_comay;

    -- ===================== NGANH DAO TAO =====================
    -- CNTT
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT863', 'Cong nghe thong tin', 'Dai hoc', v_id_khoa_cntt)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_cntt;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT701', 'Khoa hoc may tinh', 'Dai hoc', v_id_khoa_cntt)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_khmt;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT702', 'Ky thuat phan mem', 'Dai hoc', v_id_khoa_cntt)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_ktpm;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT904', 'An toan thong tin', 'Dai hoc', v_id_khoa_cntt)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_attt;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT703', 'He thong thong tin', 'Dai hoc', v_id_khoa_cntt)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_httt;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT801', 'Cong nghe da phuong tien', 'Dai hoc', v_id_khoa_cntt)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_cndpt;
    
    -- Co khi - O to
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT201', 'Cong nghe ky thuat o to', 'Dai hoc', v_id_khoa_cokhi)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_ckoto;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT101', 'Cong nghe may tinh', 'Dai hoc', v_id_khoa_cokhi)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_ckmay;
    
    -- Dien - Dien tu
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT301', 'Ky thuat dien', 'Dai hoc', v_id_khoa_dien)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_dien;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT302', 'Ky thuat dien tu', 'Dai hoc', v_id_khoa_dien)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_dientu;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT303', 'Ky thuat dieu khien va tu dong hoa', 'Dai hoc', v_id_khoa_dien)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_diekhi;
    
    -- Kinh te
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT501', 'Quan tri kinh doanh', 'Dai hoc', v_id_khoa_kinhte)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_qtkd;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT502', 'Tai chinh - Ngan hang', 'Dai hoc', v_id_khoa_kinhte)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_tcnh;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT503', 'Marketing', 'Dai hoc', v_id_khoa_kinhte)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_marketing;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT504', 'Logistics', 'Dai hoc', v_id_khoa_kinhte)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_logistics;
    
    -- Ngoai ngu
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT601', 'Ngon ngu Anh', 'Dai hoc', v_id_khoa_ngoaingu)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_nna;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT602', 'Ngon ngu Trung', 'Dai hoc', v_id_khoa_ngoaingu)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_nntrung;
    
    -- Hoa
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT7011', 'Cong nghe hoa hoc', 'Dai hoc', v_id_khoa_hoa)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_cnhh;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT7012', 'Hoa hoc', 'Dai hoc', v_id_khoa_hoa)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_hh;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT7013', 'Cong nghe thuc pham', 'Dai hoc', v_id_khoa_hoa)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_cntp;
    
    -- Co may
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT8011', 'Cong nghe may mac', 'Dai hoc', v_id_khoa_comay)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_maymac;
    
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa) VALUES
        ('CT8012', 'Thiet ke thoi trang', 'Dai hoc', v_id_khoa_comay)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh_tktt;

    -- ===================== CHUONG TRINH DAO TAO =====================
    -- CNTT
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_cntt, 140, '4 nam', 'Tot nghiep THPT', 'Trang bi kien thuc va ky nang cong nghe thong tin de phat trien ung dung phan mem, ha tang mang, an toan thong tin, dam bao chat luong va nang suat he thong', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_cntt AND nam_ap_dung = 2024);

    -- Khoa hoc may tinh
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_khmt, 140, '4 nam', 'Tot nghiep THPT', 'Dao tao nhung chuyen gia co kien thuc chuyen sau ve khoa hoc may tinh: tri tue nhan tao, hoc may, xu ly ngon ngu tu nhien, nhan dien mau, thị giac may', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_khmt AND nam_ap_dung = 2024);

    -- Ky thuat phan mem
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_ktpm, 140, '4 nam', 'Tot nghiep THPT', 'Dao tao ky su co kha nang phan tich, thiet ke, xay dung, kiem thu va bao tri cac he thong phan mem theo quy trinh chuan', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_ktpm AND nam_ap_dung = 2024);

    -- An toan thong tin
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_attt, 140, '4 nam', 'Tot nghiep THPT', 'Trang bi kien thuc ve an toan mang, ma hoa, kiem thu xam nhap, phat hien lang phieng, phong chong mat ma va bao mat he thong', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_attt AND nam_ap_dung = 2024);

    -- He thong thong tin
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_httt, 140, '4 nam', 'Tot nghiep THPT', 'Dao tao ky su thiet ke, trien khai va van hanh cac he thong thong tin quan ly, ho tro ra quyet dinh trong to chuc', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_httt AND nam_ap_dung = 2024);

    -- Cong nghe da phuong tien
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_cndpt, 140, '4 nam', 'Tot nghiep THPT', 'Trang bi ky nang san xuat, xu ly noi dung da phuong tien: video, am thanh, do hoa, thuc tai ao, thuc tai tang cuong', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_cndpt AND nam_ap_dung = 2024);

    -- Quan tri kinh doanh
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_qtkd, 120, '4 nam', 'Tot nghiep THPT', 'Dao tao nhung chuyen gia quan ly co kien thuc toan dien ve kinh te, tai chinh, marketing, nhan su, van hanh doanh nghiep', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_qtkd AND nam_ap_dung = 2024);

    -- Tai chinh - Ngan hang
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_tcnh, 120, '4 nam', 'Tot nghiep THPT', 'Trang bi kien thuc ve tai chinh doanh nghiep, ngan hang, bao hiem, dau tu, dinh gia, quan ly rui ro tai chinh', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_tcnh AND nam_ap_dung = 2024);

    -- Marketing
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_marketing, 120, '4 nam', 'Tot nghiep THPT', 'Dao tao chuyen gia marketing co kha nang phan tich thi truong, xay dung chien luoc thuong hieu, quan ly truyen thong so, thuong mai dien tu', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_marketing AND nam_ap_dung = 2024);

    -- Logistics
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_logistics, 120, '4 nam', 'Tot nghiep THPT', 'Trang bi kien thuc ve quan ly chuoi cung ung, kho vận, van chuyen, logistics thuong mai dien tu, quan ly ha tang', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_logistics AND nam_ap_dung = 2024);

    -- Ky thuat dien
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_dien, 150, '4 nam', 'Tot nghiep THPT', 'Dao tao ky su co kien thuc ve he thong dien, may phat dien, truyen tai dien nang, dien cong nghiep, dien dan dung, lap dat van hanh thiet bi dien', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_dien AND nam_ap_dung = 2024);

    -- Ky thuat dien tu
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_dientu, 150, '4 nam', 'Tot nghiep THPT', 'Trang bi kien thuc ve thiet ke, che tao, van hanh cac he thong dien tu: vi mach, dieu khien tu dong, ky thuat RF, vien thong', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_dientu AND nam_ap_dung = 2024);

    -- Ky thuat dieu khien va tu dong hoa
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_diekhi, 150, '4 nam', 'Tot nghiep THPT', 'Dao tao ky su dieu khien va tu dong hoa co kha nang thiet ke, lap rap, van hanh he thong SCADA, PLC, robot công nghiep', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_diekhi AND nam_ap_dung = 2024);

    -- Cong nghe ky thuat o to
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_ckoto, 150, '4 nam', 'Tot nghiep THPT', 'Trang bi kien thuc ve thiet ke, che tao, lap rap, kiem thu o to; ky thuat dien - dien tu o to; cong nghe giao thong van tai', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_ckoto AND nam_ap_dung = 2024);

    -- Cong nghe may tinh (Co khi)
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_ckmay, 150, '4 nam', 'Tot nghiep THPT', 'Dao tao ky su co kien thuc ve thiet ke, che tao, van hanh may cong cu CNC, robot, he thong san xuat linh hoat, CAD/CAM/CNM', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_ckmay AND nam_ap_dung = 2024);

    -- Ngon ngu Anh
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_nna, 120, '4 nam', 'Tot nghiep THPT', 'Dao tao chuyen gia ngon ngu Anh co kha nang su dung Anh ngũ trong giao tiep, bien dich, tam ly ngu, lich su van hoa Anh-My', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_nna AND nam_ap_dung = 2024);

    -- Ngon ngu Trung
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_nntrung, 120, '4 nam', 'Tot nghiep THPT', 'Dao tao chuyen gia ngon ngu Trung co kha nang su dung Trung van nghiep trong giao tiep, kinh doanh, bien dich, tam ly ngu, lich su van hoa Trung Quoc', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_nntrung AND nam_ap_dung = 2024);

    -- Cong nghe hoa hoc
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_cnhh, 145, '4 nam', 'Tot nghiep THPT', 'Trang bi kien thuc ve cong nghe hoa hoc, vat lieu polime, hoa moi truong, phan tich chat, san xuat hoa pham, chat luong san pham', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_cnhh AND nam_ap_dung = 2024);

    -- Hoa hoc
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_hh, 145, '4 nam', 'Tot nghiep THPT', 'Dao tao ky su hoa hoc co kien thuc sau ve hoa ly, hoa huu co, hoa vo co, sinh hoa hoc, cac phuong phap phan tich hien dai', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_hh AND nam_ap_dung = 2024);

    -- Cong nghe thuc pham
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_cntp, 145, '4 nam', 'Tot nghiep THPT', 'Trang bi ky nang ve cong nghe che bien, bao quan thuc pham; an toan ve sinh thuc pham; phat trien san pham moi; qua ly chat luong', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_cntp AND nam_ap_dung = 2024);

    -- Cong nghe may mac
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_maymac, 140, '4 nam', 'Tot nghiep THPT', 'Dao tao ky su co kha nang thiet ke, san xuat, quan ly chat luong san pham det nhua, det vai, cac loai vải ky thuat', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_maymac AND nam_ap_dung = 2024);

    -- Thiet ke thoi trang
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    SELECT v_id_nganh_tktt, 140, '4 nam', 'Tot nghiep THPT', 'Trang bi ky nang thiet ke thoi trang, mau sac, hoa tiet; ky thuat cat may, gia cong thanh pham; quan ly san xuat dong phuc', 2024
    WHERE NOT EXISTS (SELECT 1 FROM chuong_trinh_dao_tao WHERE id_nganh = v_id_nganh_tktt AND nam_ap_dung = 2024);

    RAISE NOTICE 'Seed khoa, nganh, ctdt completed: 7 khoa, 20+ nganh, 22 ctdt';

END $$;
