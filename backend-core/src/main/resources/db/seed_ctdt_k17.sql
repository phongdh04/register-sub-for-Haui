DO $$
DECLARE
    v_id_khoa bigint;
    v_id_nganh bigint;
    v_id_ctdt bigint;
BEGIN
    -- 1. TẠO KHOA 
    INSERT INTO khoa (ma_khoa, ten_khoa, mo_ta) 
    VALUES ('CNTT', 'Trường Công nghệ thông tin và Truyền thông', 'SOICT')
    ON CONFLICT (ma_khoa) DO UPDATE SET ten_khoa = EXCLUDED.ten_khoa
    RETURNING id_khoa INTO v_id_khoa;
    
    IF v_id_khoa IS NULL THEN
        SELECT id_khoa INTO v_id_khoa FROM khoa WHERE ma_khoa = 'CNTT';
    END IF;

    -- 2. TẠO NGÀNH ĐÀO TẠO
    INSERT INTO nganh_dao_tao (ma_nganh, ten_nganh, he_dao_tao, id_khoa)
    VALUES ('CT863', 'Công nghệ thông tin', 'Đại học', v_id_khoa)
    ON CONFLICT (ma_nganh) DO UPDATE SET ten_nganh = EXCLUDED.ten_nganh
    RETURNING id_nganh INTO v_id_nganh;
    
    IF v_id_nganh IS NULL THEN
        SELECT id_nganh INTO v_id_nganh FROM nganh_dao_tao WHERE ma_nganh = 'CT863';
    END IF;

    -- 3. TẠO CHƯƠNG TRÌNH ĐÀO TẠO
    INSERT INTO chuong_trinh_dao_tao (id_nganh, tong_so_tin_chi, thoi_gian_giang_day, doi_tuong_tuyen_sinh, muc_tieu, nam_ap_dung)
    VALUES (v_id_nganh, 140, '4 năm', 'Tốt nghiệp THPT hoặc tương đương', 'Trang bị kiến thức, kỹ năng, thái độ, năng lực ngoại ngữ/tin học đáp ứng vị trí việc làm sau tốt nghiệp.', 2023)
    RETURNING id_ctdt INTO v_id_ctdt;

    -- 4. INSERT HỌC PHẦN (COURSES)
    -- Group Đại cương: Bắt buộc
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES 
		('LP6010', 'Triết học Mác-Lênin', 3, 'DAI_CUONG', '{}', '{}'),
		('LP6011', 'Kinh tế chính trị Mác-Lênin', 2, 'DAI_CUONG', '{}', '{}'),
		('LP6012', 'Chủ nghĩa xã hội khoa học', 2, 'DAI_CUONG', '{}', '{}'),
		('LP6013', 'Lịch sử Đảng Cộng sản Việt Nam', 2, 'DAI_CUONG', '{}', '{}'),
		('LP6004', 'Tư tưởng Hồ Chí Minh', 2, 'DAI_CUONG', '{}', '{}'),
		('BS6018', 'Giao tiếp liên văn hóa', 2, 'DAI_CUONG', '{}', '{}'),
		('LP6003', 'Pháp luật đại cương', 2, 'DAI_CUONG', '{}', '{}'),
		('BS6002', 'Giải tích', 3, 'DAI_CUONG', '{}', '{}'),
		('BS6001', 'Đại số tuyến tính', 3, 'DAI_CUONG', '{}', '{}'),
		('IT6016', 'Kỹ thuật số', 3, 'DAI_CUONG', '{}', '{}'),
		('BS6027', 'Vật lý đại cương', 3, 'DAI_CUONG', '{}', '{}'),
		('IT6035', 'Toán rời rạc', 3, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- Group Đại Cương: Tự chọn Ngoại Ngữ
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES 
		('NN6001', 'Tiếng Hàn cơ bản 1', 5, 'DAI_CUONG', '{}', '{}'),
		('NN6005', 'Tiếng Hàn cơ bản 2', 5, 'DAI_CUONG', '{}', '{}'),
		('NN6002', 'Tiếng Trung cơ bản 1', 5, 'DAI_CUONG', '{}', '{}'),
		('NN6006', 'Tiếng Trung cơ bản 2', 5, 'DAI_CUONG', '{}', '{}'),
		('NN6003', 'Tiếng Nhật cơ bản 1', 5, 'DAI_CUONG', '{}', '{}'),
		('NN6007', 'Tiếng Nhật cơ bản 2', 5, 'DAI_CUONG', '{}', '{}'),
		('NN6004', 'Tiếng Anh CNTT 1', 5, 'DAI_CUONG', '{}', '{}'),
		('NN6008', 'Tiếng Anh CNTT 2', 5, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- Group Đại Cương: Tự chọn CNTT 1 & 2
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES 
		('TC6011', 'Con người và môi trường', 2, 'DAI_CUONG', '{}', '{}'),
		('TC6012', 'Nhập môn NCKH', 2, 'DAI_CUONG', '{}', '{}'),
		('TC6013', 'Quan hệ lao động và việc làm', 2, 'DAI_CUONG', '{}', '{}'),
		('TC6014', 'Quản lý dự án', 2, 'DAI_CUONG', '{}', '{}'),
		('TC6015', 'Âm nhạc ĐC', 2, 'DAI_CUONG', '{}', '{}'),
		('TC6016', 'Mỹ thuật ĐC', 2, 'DAI_CUONG', '{}', '{}'),
		('TC6017', 'Nghệ thuật học ĐC', 2, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- Group Cơ sở Tự chọn CNTT 3
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES 
		('TC6031', 'Phương pháp tính', 3, 'DAI_CUONG', '{}', '{}'),
		('TC6032', 'Tối ưu hóa', 3, 'DAI_CUONG', '{}', '{}'),
		('TC6033', 'Xác suất thống kê', 3, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- Group GDTC & QPAN
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES 
		('TC6001', 'Thể dục tự chọn Aerobic', 1, 'DAI_CUONG', '{}', '{}'),
		('TC6002', 'Thể dục tự chọn Bơi', 1, 'DAI_CUONG', '{}', '{}'),
		('TC6003', 'Thể dục tự chọn Bóng bàn', 1, 'DAI_CUONG', '{}', '{}'),
		('TC6004', 'Thể dục tự chọn Bóng đá', 1, 'DAI_CUONG', '{}', '{}'),
		('TC6005', 'Thể dục tự chọn Bóng chuyền', 1, 'DAI_CUONG', '{}', '{}'),
		('TC6006', 'Thể dục tự chọn Cầu lông', 1, 'DAI_CUONG', '{}', '{}'),
		('TC6007', 'Thể dục tự chọn Futsal', 1, 'DAI_CUONG', '{}', '{}'),
		('TC6008', 'Thể dục tự chọn Karate', 1, 'DAI_CUONG', '{}', '{}'),
		('QP6001', 'Công tác QPAN', 2, 'DAI_CUONG', '{}', '{}'),
		('QP6002', 'Đường lối QPAN', 3, 'DAI_CUONG', '{}', '{}'),
		('QP6003', 'Kỹ thuật chiến đấu', 2, 'DAI_CUONG', '{}', '{}'),
		('QP6004', 'Quân sự chung', 2, 'DAI_CUONG', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- Group Cơ sở chuyên ngành
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES 
		('IT6011', 'Nhập môn về kỹ thuật', 2, 'CO_SO_NGANH', '{}', '{}'),
		('IT6015', 'Kỹ thuật lập trình', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6011"]}'),
		('IT6126', 'Hệ thống cơ sở dữ liệu', 4, 'CO_SO_NGANH', '{}', '{}'),
		('IT6067', 'Kiến trúc máy tính và hệ điều hành', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6120', 'Lập trình hướng đối tượng', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6015"]}'),
		('IT6001', 'An toàn và bảo mật thông tin', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6002', 'Cấu trúc dữ liệu và giải thuật', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6015"]}'),
		('IT6083', 'Mạng máy tính', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6082', 'Nhập môn công nghệ phần mềm', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6120"]}'),
		('IT6066', 'Phân tích thiết kế phần mềm', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6071', 'Phát triển dự án công nghệ thông tin', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6100', 'Thiết kế đồ hoạ 2D', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6039', 'Thiết kế Web', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6121', 'Thực tập cơ sở ngành', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6071"]}'),
		('IT6094', 'Trí tuệ nhân tạo', 3, 'CO_SO_NGANH', '{}', '{"tien_quyet": ["IT6002"]}'),
		('IT6056', 'Quản trị mạng trên HĐH Windows', 3, 'CO_SO_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- Group Cơ sở chuyên ngành: Tự chọn CNTT 4
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES 
		('IT6050', 'An ninh mạng', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6051', 'Cơ sở lập trình nhúng', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6052', 'Học máy', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6053', 'Phát triển ứng dụng TMĐT', 3, 'CO_SO_NGANH', '{}', '{}'),
		('IT6054', 'Thiết kế web nâng cao', 3, 'CO_SO_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- Group Chuyên ngành Bắt Buộc
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES 
		('IT6123', 'Tương tác người máy', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6122', 'Đồ án chuyên ngành', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6013', 'Kiểm thử phần mềm', 3, 'CHUYEN_NGANH', '{}', '{"tien_quyet": ["IT6082"]}'),
		('IT6029', 'Phát triển ứng dụng trên thiết bị di động', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6034', 'Tích hợp hệ thống phần mềm', 3, 'CHUYEN_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- Group Chuyên ngành Định hướng: Tổ hợp môn
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES 
		('IT6060', 'Công nghệ đa phương tiện', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6061', 'Đảm bảo chất lượng PM', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6062', 'Hệ quản trị doanh nghiệp điện tử', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6063', 'Lập trình nhúng & IoT', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6064', 'Phần mềm mã nguồn mở', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6065', 'Phân tích dữ liệu lớn', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6068', 'Phát triển ứng dụng Game', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6069', 'Quản trị mạng trên HĐH mã nguồn mở', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6070', 'Ứng dụng thuật toán', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6075', 'Lập trình .NET', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6076', 'Lập trình Web bằng ASP.NET', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6077', 'Lập trình Java nâng cao', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6078', 'Lập trình Web bằng Java', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6079', 'Lập trình Web bằng PHP', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6080', 'Lập trình PHP nâng cao', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6084', 'Lập trình Python cơ bản', 3, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6085', 'Lập trình Python nâng cao', 3, 'CHUYEN_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;

    -- Group Đồ án
    INSERT INTO hoc_phan(ma_hoc_phan, ten_hoc_phan, so_tin_chi, loai_mon, thuoc_tinh_json, dieu_kien_rang_buoc_json) VALUES 
		('IT6129', 'Đồ án tốt nghiệp', 9, 'CHUYEN_NGANH', '{}', '{}'),
		('IT6128', 'Thực tập doanh nghiệp', 6, 'CHUYEN_NGANH', '{}', '{}')
    ON CONFLICT(ma_hoc_phan) DO NOTHING;


    -- 5. MAPPING HỌC PHẦN THUỘC VỀ CHƯƠNG TRÌNH ĐÀO TẠO NÀY (BẢNG CTDT_Hoc_Phan)
    -- Mapping group Đại cương Bắt buộc
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt, id_hoc_phan, 'DAI_CUONG', true, 1
    FROM hoc_phan WHERE ma_hoc_phan IN ('LP6010', 'LP6011', 'LP6012', 'LP6013', 'LP6004', 'BS6018', 'LP6003', 'BS6002', 'BS6001', 'IT6016', 'BS6027', 'IT6035');

    -- Mapping group Đại Cương Tự chọn (Ngoại ngữ, GDTC, QPAN, CNTT 1-2-3)
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt, id_hoc_phan, 'DAI_CUONG', false, 2
    FROM hoc_phan WHERE ma_hoc_phan IN (
        'NN6001', 'NN6005', 'NN6002', 'NN6006', 'NN6003', 'NN6007', 'NN6004', 'NN6008', 
        'TC6001', 'TC6002', 'TC6003', 'TC6004', 'TC6005', 'TC6006', 'TC6007', 'TC6008',
        'QP6001', 'QP6002', 'QP6003', 'QP6004',
        'TC6011', 'TC6012', 'TC6013', 'TC6014', 'TC6015', 'TC6016', 'TC6017',
        'TC6031', 'TC6032', 'TC6033'
    );

    -- Mapping group Cơ sở ngành Bắt buộc
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt, id_hoc_phan, 'CO_SO_NGANH', true, 3
    FROM hoc_phan WHERE ma_hoc_phan IN ('IT6011', 'IT6015', 'IT6126', 'IT6067', 'IT6120', 'IT6001', 'IT6002', 'IT6083', 'IT6082', 'IT6066', 'IT6071', 'IT6100', 'IT6039', 'IT6121', 'IT6094', 'IT6056');

    -- Mapping group Cơ sở ngành Tự chọn
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt, id_hoc_phan, 'CO_SO_NGANH', false, 5
    FROM hoc_phan WHERE ma_hoc_phan IN ('IT6050', 'IT6051', 'IT6052', 'IT6053', 'IT6054');

    -- Mapping group Chuyên ngành
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt, id_hoc_phan, 'CHUYEN_NGANH', true, 6
    FROM hoc_phan WHERE ma_hoc_phan IN ('IT6123', 'IT6122', 'IT6013', 'IT6029', 'IT6034');

    -- Mapping group Chuyên ngành Tự Chọn
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt, id_hoc_phan, 'CHUYEN_NGANH', false, 7
    FROM hoc_phan WHERE ma_hoc_phan IN (
        'IT6060', 'IT6061', 'IT6062', 'IT6063', 'IT6064', 'IT6065', 'IT6068', 'IT6069', 'IT6070',
        'IT6075', 'IT6076', 'IT6077', 'IT6078', 'IT6079', 'IT6080', 'IT6084', 'IT6085'
    );

    -- Mapping group Đồ án (Cuối khoá)
    INSERT INTO ctdt_hoc_phan (id_ctdt, id_hoc_phan, khoi_kien_thuc, bat_buoc, hoc_ky_goi_y)
    SELECT v_id_ctdt, id_hoc_phan, 'CHUYEN_NGANH', true, 8
    FROM hoc_phan WHERE ma_hoc_phan IN ('IT6129', 'IT6128');

END $$;
