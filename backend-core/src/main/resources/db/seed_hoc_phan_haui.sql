-- =====================================================
-- SEED DATA: HOC PHAN (Courses)
-- Theo chuong trinh dao tao HaUI
-- =====================================================

DO $$
BEGIN
    -- ===================== DAI CUONG - BAT BUOC =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('LP6010', 'Triet hoc Mac-Lenin', 3, 'DAI_CUONG', '{}', '{}'),
        ('LP6011', 'Kinh te chinh tri Mac-Lenin', 2, 'DAI_CUONG', '{}', '{}'),
        ('LP6012', 'Chu nghia xa hoi khoa hoc', 2, 'DAI_CUONG', '{}', '{}'),
        ('LP6013', 'Lich su Dang Cong san VN', 2, 'DAI_CUONG', '{}', '{}'),
        ('LP6004', 'Tu tuong Ho Chi Minh', 2, 'DAI_CUONG', '{}', '{}'),
        ('LP6003', 'Phap luat dai cuong', 2, 'DAI_CUONG', '{}', '{}'),
        ('BS6002', 'Giai tich', 3, 'DAI_CUONG', '{}', '{}'),
        ('BS6001', 'Dai so tuyen tinh', 3, 'DAI_CUONG', '{}', '{}'),
        ('BS6027', 'Vat ly dai cuong', 3, 'DAI_CUONG', '{}', '{}'),
        ('IT6035', 'Toan roi rac', 3, 'DAI_CUONG', '{}', '{}'),
        ('IT6016', 'Ky thuat so', 3, 'DAI_CUONG', '{}', '{}'),
        ('BS6018', 'Giao tiep lien van hoa', 2, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== DAI CUONG - NGOAI NGU =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('NN6001', 'Anh van 1', 5, 'DAI_CUONG', '{}', '{}'),
        ('NN6002', 'Anh van 2', 5, 'DAI_CUONG', '{}', '{}'),
        ('NN6003', 'Anh van 3', 3, 'DAI_CUONG', '{}', '{}'),
        ('NN6004', 'Anh van 4', 3, 'DAI_CUONG', '{}', '{}'),
        ('NN6005', 'Trung van 1', 5, 'DAI_CUONG', '{}', '{}'),
        ('NN6006', 'Trung van 2', 5, 'DAI_CUONG', '{}', '{}'),
        ('NN6007', 'Han van 1', 5, 'DAI_CUONG', '{}', '{}'),
        ('NN6008', 'Han van 2', 5, 'DAI_CUONG', '{}', '{}'),
        ('NN6009', 'Nhat van 1', 5, 'DAI_CUONG', '{}', '{}'),
        ('NN6010', 'Nhat van 2', 5, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== DAI CUONG - GDTC =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('TC6001', 'The duc 1 - aerobic', 1, 'DAI_CUONG', '{}', '{}'),
        ('TC6002', 'The duc 2 - boi', 1, 'DAI_CUONG', '{}', '{}'),
        ('TC6003', 'The duc 3 - bong banh', 1, 'DAI_CUONG', '{}', '{}'),
        ('TC6004', 'The duc 4 - bong da', 1, 'DAI_CUONG', '{}', '{}'),
        ('TC6005', 'The duc 5 - bong chuyen', 1, 'DAI_CUONG', '{}', '{}'),
        ('TC6006', 'The duc 6 - cau long', 1, 'DAI_CUONG', '{}', '{}'),
        ('TC6007', 'The duc 7 - futal', 1, 'DAI_CUONG', '{}', '{}'),
        ('TC6008', 'The duc 8 - karate', 1, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== DAI CUONG - QPAN =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('QP6001', 'Cong tac QPAN 1', 2, 'DAI_CUONG', '{}', '{}'),
        ('QP6002', 'Duong loi QPAN', 3, 'DAI_CUONG', '{}', '{}'),
        ('QP6003', 'Ky thuat chien dau', 2, 'DAI_CUONG', '{}', '{}'),
        ('QP6004', 'Quan su chung', 2, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== DAI CUONG - TU CHON =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('TC6011', 'Con nguoi va moi truong', 2, 'DAI_CUONG', '{}', '{}'),
        ('TC6012', 'Nhap mon NCKH', 2, 'DAI_CUONG', '{}', '{}'),
        ('TC6013', 'Quan he lao dong va viec lam', 2, 'DAI_CUONG', '{}', '{}'),
        ('TC6014', 'Quan ly du an', 2, 'DAI_CUONG', '{}', '{}'),
        ('TC6031', 'Phuong phap tinh', 3, 'DAI_CUONG', '{}', '{}'),
        ('TC6032', 'Toi uu hoa', 3, 'DAI_CUONG', '{}', '{}'),
        ('TC6033', 'Xac suat thong ke', 3, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== CO SO NGANH - CNTT =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('IT6011', 'Nhap mon ky thuat', 2, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6015', 'Ky thuat lap trinh', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6011"]}'),
        ('IT6120', 'Lap trinh huong doi tuong', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6015"]}'),
        ('IT6002', 'Cau truc du lieu va giai thuat', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6015"]}'),
        ('IT6126', 'He thong co so du lieu', 4, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6067', 'Kien truc may tinh va HĐH', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6083', 'Mang may tinh', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6082', 'Nhap mon CNPM', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6120"]}'),
        ('IT6066', 'Phan tich thiet ke PM', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6071', 'Phat trien du an CNTT', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6121', 'Thuc tap co so nganh', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6071"]}'),
        ('IT6094', 'Tri tue nhan tao', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6002"]}'),
        ('IT6001', 'An toan va bao mat TT', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6039', 'Thiet ke Web', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6100', 'Thiet ke do hoa 2D', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6056', 'Quan tri mang tren Windows', 3, 'CO_SO_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== CO SO NGANH - TU CHON CNTT =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('IT6050', 'An ninh mang', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6051', 'Co so lap trinh nhung', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6052', 'Hoc may', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6053', 'Phat trien ung dung TMDT', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6054', 'Thiet ke web nang cao', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6055', 'Tich hop he thong PM', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6057', 'Cong nghe cloud computing', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('IT6058', 'DevOps', 3, 'CO_SO_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== CHUYEN NGANH - CNTT =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('IT6013', 'Kiem thu phan mem', 3, 'CHUYEN_NGANH', '{}', '{"tien_quyet": ["IT6082"]}'),
        ('IT6029', 'Phat trien UDTDDD', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6123', 'Tuong tac nguoi may', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6122', 'Do an chuyen nganh', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6034', 'Tich hop HT phan mem', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6084', 'Lap trinh Python', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6075', 'Lap trinh .NET', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6077', 'Lap trinh Java nang cao', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6079', 'Lap trinh PHP', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6065', 'Phan tich du lieu lon', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6063', 'Lap trinh nhung va IoT', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6068', 'Phat trien ung dung Game', 3, 'CHUYEN_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== DO AN =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('IT6129', 'Do an tot nghiep', 9, 'CHUYEN_NGANH', '{}', '{}'),
        ('IT6128', 'Thuc tap doanh nghiep', 6, 'CHUYEN_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== CO SO NGANH - CO KHI =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('CK6001', 'Co hoc va ky thuat co khi', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('CK6002', 'Ve ky thuat', 2, 'CO_SO_NGANH', '{}', '{}'),
        ('CK6003', 'Do dan do loi', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('CK6004', 'Vat lieu hoc', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('CK6005', 'Gia cong cat gon', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('CK6006', 'May cong cu', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('CK6007', 'CNC va Robotic', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('CK6008', 'Thiet ke may', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('CK6009', 'Thuy luc va khi nen', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('CK6010', 'Cong nghe chet tao', 3, 'CO_SO_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== CHUYEN NGANH - O TO =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('OTO6001', 'Ly luat o to', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('OTO6002', 'Cau tao o to', 4, 'CHUYEN_NGANH', '{}', '{}'),
        ('OTO6003', 'Dong co dot trong', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('OTO6004', 'He thong dien o to', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('OTO6005', 'He thong treo va la zuy', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('OTO6006', 'Ky thuat o to dien', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('OTO6007', 'Thi nghiem o to', 2, 'CHUYEN_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== CO SO NGANH - DIEN =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('ĐT6001', 'Ly thuyet mach', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('ĐT6002', 'Dien tu co ban', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('ĐT6003', 'Truyen dong dien', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('ĐT6004', 'May dien', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('ĐT6005', 'He thong dien', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('ĐT6006', 'Dien tu cong suat', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('ĐT6007', 'Ky thuat dien cao ap', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('ĐT6008', 'Tu dong hoa qua trinh', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('ĐT6009', 'Dien nang tac tao', 3, 'CO_SO_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== CO SO NGANH - KINH TE =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('KT6001', 'Nguyen ly kinh te vi mo', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6002', 'Toan kinh te', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6003', 'Xac suat thong ke kinh te', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6004', 'Tai chinh tien te', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6005', 'Ke toan co so', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6006', 'Marketing co so', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6007', 'Quan tri hoc co so', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6008', 'Luat kinh doanh', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('KT6009', 'Quan tri tai chinh', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('KT6010', 'Quan tri nhan su', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('KT6011', 'Marketing dich vu', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('KT6012', 'Logistics', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('KT6013', 'Ban hang va Quan trikenh phan phoi', 3, 'CHUYEN_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== CO SO NGANH - NGOAI NGU =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('NN6011', 'Nghe ngu Anh 1', 4, 'CO_SO_NGANH', '{}', '{}'),
        ('NN6012', 'Nghe ngu Anh 2', 4, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["NN6011"]}'),
        ('NN6013', 'Nghe ngu Anh 3', 4, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["NN6012"]}'),
        ('NN6014', 'Nghe ngu Anh 4', 4, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["NN6013"]}'),
        ('NN6015', 'Bieu dat tu vung Anh', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('NN6016', 'Ngon ngu hoc Anh', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('NN6017', 'Van hoc Anh', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('NN6018', 'Dich thuat Anh', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('NN6019', 'Giao tiep kinh doanh Anh', 3, 'CHUYEN_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== CO SO NGANH - HOA =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('HOA6001', 'Hoa hoc dai cuong', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('HOA6002', 'Hoa hoc phan tich', 4, 'CO_SO_NGANH', '{}', '{}'),
        ('HOA6003', 'Hoa hoc vo co', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('HOA6004', 'Hoa hoc huu co', 4, 'CO_SO_NGANH', '{}', '{}'),
        ('HOA6005', 'Hoa hoc ly', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('HOA6006', 'Cong nghe hoa than', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('HOA6007', 'Cong nghe polime', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('HOA6008', 'Cong nghe thuc pham', 3, 'CHUYEN_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- ===================== CO SO NGANH - MAY MAC =====================
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) 
    VALUES 
        ('MM6001', 'Vat lieu hoc day', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('MM6002', 'May mac', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('MM6003', 'Thiet ke thoi trang 1', 3, 'CO_SO_NGANH', '{}', '{}'),
        ('MM6004', 'Thiet ke thoi trang 2', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["MM6003"]}'),
        ('MM6005', 'Mau sach', 3, 'CHUYEN_NGANH', '{}', '{}'),
        ('MM6006', 'Cong nghe may mac', 3, 'CHUYEN_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    RAISE NOTICE 'Seed hoc_phan completed: 100+ hoc_phan';

END $$;
