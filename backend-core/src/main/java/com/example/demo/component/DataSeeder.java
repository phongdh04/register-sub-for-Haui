package com.example.demo.component;

import com.example.demo.domain.entity.*;
import com.example.demo.domain.enums.*;
import com.example.demo.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Data Seeder - Tự động tạo các tài khoản test khi Spring Boot khởi động.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final KhoaRepository khoaRepository;
    private final GiangVienRepository giangVienRepository;
    private final SinhVienRepository sinhVienRepository;
    private final HocKyRepository hocKyRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final LichThiRepository lichThiRepository;
    private final PhieuDuThiRepository phieuDuThiRepository;
    private final PhongHocRepository phongHocRepository;
    private final GvBusySlotRepository gvBusySlotRepository;
    private final PasswordEncoder passwordEncoder;
    private final NganhDaoTaoRepository nganhDaoTaoRepository;
    private final LopRepository lopRepository;
    private final HocPhanRepository hocPhanRepository;
    private final ChuongTrinhDaoTaoRepository chuongTrinhDaoTaoRepository;
    private final CtdtHocPhanRepository ctdtHocPhanRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Checking and seeding testing accounts...");

        // 1. Seed Admin
        if (!userRepository.existsByUsername("admin")) {
            userRepository.save(User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.ADMIN)
                    .status(Status.ACTIVE)
                    .email("admin@eduport.demo")
                    .mfaEnabled(false)
                    .build());
            log.info("Seeded Admin account: admin / 123456 (email MFA mặc định, MFA tắt)");
        }

        // 2. Seed Lecturer (Giảng viên)
        if (!userRepository.existsByUsername("gv01")) {
            userRepository.save(User.builder()
                    .username("gv01")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.LECTURER)
                    .status(Status.ACTIVE)
                    .build());
            log.info("Seeded Lecturer account: gv01 / 123456");
        }

        // 3. Seed Student
        if (!userRepository.existsByUsername("sv01")) {
            userRepository.save(User.builder()
                    .username("sv01")
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.STUDENT)
                    .status(Status.ACTIVE)
                    .build());
            log.info("Seeded Student account: sv01 / 123456");
        }
        
        // Tạo sẵn 1 Khoa CNTT nếu chưa có để test tiện hơn
        if (!khoaRepository.existsByMaKhoa("CNTT")) {
            khoaRepository.save(Khoa.builder()
                    .maKhoa("CNTT")
                    .tenKhoa("Công nghệ thông tin")
                    .moTa("Khoa dùng để Test API")
                    .build());
            log.info("Seeded Khoa test: CNTT");
        }

        // Liên kết gv01 ↔ hồ sơ Giảng viên (Task 16 – điểm danh / lớp phụ trách; cần Khoa CNTT)
        if (!giangVienRepository.existsByMaGiangVien("GV_SEED")) {
            khoaRepository.findByMaKhoa("CNTT").flatMap(k ->
                    userRepository.findByUsername("gv01").map(u ->
                            giangVienRepository.save(GiangVien.builder()
                                    .maGiangVien("GV_SEED")
                                    .tenGiangVien("Giảng viên Demo Seed")
                                    .khoa(k)
                                    .taiKhoan(u)
                                    .build())
                    )
            ).ifPresent(gv -> log.info("Seeded GiangVien {} linked to gv01", gv.getMaGiangVien()));
        }

        seedExamScheduleDemoIfEmpty();
        seedPhongHocBaselinesIfFew();
        seedGvBusySlotsForSeedGiangVien();

        // Seed đầy đủ dữ liệu cho sinh viên (Degree Audit)
        seedFullNganhData();

        // Đảm bảo sv01 cũng có profile sinh viên (liên kết với lớp CNTT-K17)
        seedSv01ProfileIfMissing();

        log.info("Database seeding completed.");
    }

    /**
     * Đảm bảo account sv01 có profile SinhVien (link với lớp CNTT-K17).
     */
    private void seedSv01ProfileIfMissing() {
        userRepository.findByUsername("sv01").ifPresent(user -> {
            if (sinhVienRepository.findByTaiKhoan_Id(user.getId()).isPresent()) {
                return;
            }
            // Tránh trùng mã sinh viên
            String baseMaSv = "SV000001";
            int i = 1;
            while (sinhVienRepository.existsByMaSinhVien(baseMaSv)) {
                baseMaSv = String.format("SV%06d", ++i);
            }
            final String finalMaSv = baseMaSv;
            lopRepository.findByMaLop("CT863-K17").ifPresent(lop -> {
                SinhVien sv = sinhVienRepository.save(SinhVien.builder()
                        .maSinhVien(finalMaSv)
                        .hoTen("Nguyen Van A - sv01")
                        .lop(lop)
                        .taiKhoan(user)
                        .build());
                log.info("Seeded SinhVien profile for sv01: {} ({})", sv.getMaSinhVien(), sv.getHoTen());
            });
        });
    }

    /** TKB P0: ≥20 phòng demo (nhiều loại, 2 cơ sở). */
    private void seedPhongHocBaselinesIfFew() {
        if (phongHocRepository.count() >= 20) {
            return;
        }
        record R(String ma, String ten, String coSo, LoaiPhong loai, int suc) {}
        R[] rows = new R[] {
                new R("A101", "Phòng lý thuyết A101", "CS1", LoaiPhong.LY_THUYET, 120),
                new R("A102", "Phòng lý thuyết A102", "CS1", LoaiPhong.LY_THUYET, 100),
                new R("A103", "Phòng lý thuyết A103", "CS1", LoaiPhong.LY_THUYET, 80),
                new R("A201", "Phòng lý thuyết A201", "CS1", LoaiPhong.LY_THUYET, 120),
                new R("A202", "Phòng lý thuyết A202", "CS1", LoaiPhong.LY_THUYET, 90),
                new R("B101", "Phòng lý thuyết B101", "CS1", LoaiPhong.LY_THUYET, 150),
                new R("B102", "Phòng lý thuyết B102", "CS1", LoaiPhong.LY_THUYET, 60),
                new R("C301", "Hội trường nhỏ C301", "CS1", LoaiPhong.LY_THUYET, 200),
                new R("LAB-IT-01", "Phòng máy tính 1", "CS1", LoaiPhong.MAY_TINH, 45),
                new R("LAB-IT-02", "Phòng máy tính 2", "CS1", LoaiPhong.MAY_TINH, 45),
                new R("LAB-IT-03", "Phòng máy tính 3", "CS1", LoaiPhong.MAY_TINH, 40),
                new R("LAB-NET-01", "Phòng lab mạng", "CS1", LoaiPhong.MAY_TINH, 35),
                new R("THN-HOA-01", "Thí nghiệm Hóa 1", "CS1", LoaiPhong.THI_NGHIEM_HOA, 30),
                new R("THN-HOA-02", "Thí nghiệm Hóa 2", "CS1", LoaiPhong.THI_NGHIEM_HOA, 28),
                new R("THN-VL-01", "Thí nghiệm Vật lý 1", "CS1", LoaiPhong.THI_NGHIEM_VAT_LY, 32),
                new R("THN-SINH-01", "Thí nghiệm Sinh 1", "CS1", LoaiPhong.THI_NGHIEM_SINH, 28),
                new R("D101", "Phòng đa năng D101", "CS2", LoaiPhong.HOC_TAT, 80),
                new R("D102", "Phòng đa năng D102", "CS2", LoaiPhong.HOC_TAT, 70),
                new R("D-LT-01", "Lý thuyết D-LT-01", "CS2", LoaiPhong.LY_THUYET, 100),
                new R("D-LT-02", "Lý thuyết D-LT-02", "CS2", LoaiPhong.LY_THUYET, 90),
                new R("D-LAB-01", "Máy tính D-LAB-01", "CS2", LoaiPhong.MAY_TINH, 40),
                new R("D-LAB-02", "Máy tính D-LAB-02", "CS2", LoaiPhong.MAY_TINH, 35),
                new R("ONLINE-ZOOM-A", "Lớp trực tuyến slot A", "ONLINE", LoaiPhong.KHAC, 300),
                new R("ONLINE-ZOOM-B", "Lớp trực tuyến slot B", "ONLINE", LoaiPhong.KHAC, 300),
        };
        int added = 0;
        for (R r : rows) {
            if (!phongHocRepository.existsByMaPhong(r.ma)) {
                phongHocRepository.save(PhongHoc.builder()
                        .maPhong(r.ma)
                        .tenPhong(r.ten)
                        .maCoSo(r.coSo)
                        .loaiPhong(r.loai)
                        .sucChua(r.suc)
                        .trangThai(TrangThaiPhong.HOAT_DONG)
                        .build());
                added++;
            }
        }
        if (added > 0) {
            log.info("Seeded {} baseline PhongHoc rows (TKB P0)", added);
        }
    }

    /** TKB P0: ≥5 pattern busy cho GV_SEED (gv01). */
    private void seedGvBusySlotsForSeedGiangVien() {
        giangVienRepository.findByMaGiangVien("GV_SEED").ifPresent(gv -> {
            List<GvBusySlot> existed = gvBusySlotRepository.findByGiangVien_IdGiangVienOrderByThuAscTietBdAsc(
                    gv.getIdGiangVien());
            if (existed.size() >= 5) {
                return;
            }
            LocalDate bd = LocalDate.now().minusMonths(1);
            LocalDate kt = bd.plusMonths(12);
            List<GvBusySlot> demos = List.of(
                    GvBusySlot.builder().giangVien(gv).hocKy(null).thu((short) 2).tietBd((short) 1).tietKt((short) 5)
                            .loai(GvBusyLoai.HARD).lyDo("Họp khoa sáng thứ 2").ngayBd(bd).ngayKt(kt).build(),
                    GvBusySlot.builder().giangVien(gv).hocKy(null).thu((short) 4).tietBd((short) 8).tietKt((short) 12)
                            .loai(GvBusyLoai.HARD).lyDo("Hướng dẫn NCKH").build(),
                    GvBusySlot.builder().giangVien(gv).hocKy(null).thu((short) 6).tietBd((short) 1).tietKt((short) 3)
                            .loai(GvBusyLoai.SOFT).lyDo("Ưu tiên không dạy sáng thứ 7").build(),
                    GvBusySlot.builder().giangVien(gv).hocKy(null).thu((short) 7).tietBd((short) 13).tietKt((short) 15)
                            .loai(GvBusyLoai.HARD).lyDo("Thí nghiệm cố định").build(),
                    GvBusySlot.builder().giangVien(gv).hocKy(null).thu((short) 8).tietBd((short) 9).tietKt((short) 12)
                            .loai(GvBusyLoai.HARD).lyDo("Chủ nhật seminar (HK đặc biệt)").build());
            gvBusySlotRepository.saveAll(demos);
            log.info("Seeded {} gv_busy_slot rows for GV_SEED (TKB)", demos.size());
        });
    }

    /**
     * Task 11: nếu DB đã có đăng ký của sv01 nhưng chưa có lịch thi, tạo một dòng demo (Lich_Thi + Phieu_Du_Thi).
     */
    private void seedExamScheduleDemoIfEmpty() {
        if (lichThiRepository.count() > 0) {
            return;
        }
        userRepository.findByUsername("sv01").flatMap(u -> sinhVienRepository.findByTaiKhoan_Id(u.getId()))
                .ifPresent(sv -> hocKyRepository.findTopByOrderByIdHocKyDesc().ifPresent(hk -> {
                    List<DangKyHocPhan> dks = dangKyHocPhanRepository.findRegisteredCoursesInSemester(
                            sv.getIdSinhVien(), hk.getIdHocKy());
                    if (dks.isEmpty()) {
                        return;
                    }
                    DangKyHocPhan dk = dks.get(0);
                    LopHocPhan lhp = dk.getLopHocPhan();
                    if (lichThiRepository.findByLopHocPhan_IdLopHp(lhp.getIdLopHp()).isPresent()) {
                        return;
                    }
                    LichThi lt = lichThiRepository.save(LichThi.builder()
                            .lopHocPhan(lhp)
                            .lanThi(1)
                            .ngayThi(LocalDate.now().plusWeeks(2))
                            .caThi("Ca 1")
                            .gioBatDau("07:30")
                            .phongThi("Phòng 101 (demo seed)")
                            .build());
                    if (phieuDuThiRepository.findByDangKy_IdDangKy(dk.getIdDangKy()).isEmpty()) {
                        phieuDuThiRepository.save(PhieuDuThi.builder()
                                .lichThi(lt)
                                .dangKy(dk)
                                .soBaoDanh("DEMO-" + sv.getMaSinhVien())
                                .trangThaiDuThi("DUOC_THI")
                                .build());
                        log.info("Seeded demo Lich_Thi + Phieu_Du_Thi for sv01 / lop {}", lhp.getMaLopHp());
                    }
                }));

        userRepository.findByUsername("admin").ifPresent(admin -> {
            if (admin.getEmail() == null || admin.getEmail().isBlank()) {
                admin.setEmail("admin@eduport.demo");
                userRepository.save(admin);
                log.info("Cập nhật email mặc định cho admin: admin@eduport.demo (Task 22 MFA)");
            }
        });
    }

    /**
     * Seed đầy đủ dữ liệu: Khoa, Ngành, Lớp, Học phần, CTĐT, Tài khoản SV cho mỗi ngành.
     */
    private void seedFullNganhData() {
        // Tạo các Khoa nếu chưa có
        Khoa khoaCntt = seedKhoaIfNotExists("CNTT", "Khoa Công nghệ thông tin");
        Khoa khoaCoKhi = seedKhoaIfNotExists("CK", "Khoa Cơ khí");
        Khoa khoaDien = seedKhoaIfNotExists("DIEN", "Khoa Điện");
        Khoa khoaKinhTe = seedKhoaIfNotExists("KT", "Khoa Kinh tế");
        Khoa khoaNgoaiNgu = seedKhoaIfNotExists("NN", "Khoa Ngoại ngữ");

        // Tạo Học phần chung (Đại cương)
        seedHocPhanDemo();

        // Seed mỗi ngành: [maNganh, tenNganh, khoa, username, maSv, hoTen]
        seedNganhWithStudent("CT863", "Công nghệ thông tin", khoaCntt, "sv_cntt", "20231001", "Nguyễn Văn A - CNTT");
        seedNganhWithStudent("CT201", "Cơ khí ô tô", khoaCoKhi, "sv_ckoto", "20232001", "Trần Thị B - Cơ khí");
        seedNganhWithStudent("CT301", "Kỹ thuật điện", khoaDien, "sv_dien", "20233001", "Lê Văn C - Điện");
        seedNganhWithStudent("CT501", "Quản trị kinh doanh", khoaKinhTe, "sv_kt", "20235001", "Phạm Thị D - Kinh tế");
        seedNganhWithStudent("CT601", "Ngôn ngữ Anh", khoaNgoaiNgu, "sv_nna", "20236001", "Hoàng Văn E - Ngoại ngữ");
    }

    private Khoa seedKhoaIfNotExists(String maKhoa, String tenKhoa) {
        return khoaRepository.findByMaKhoa(maKhoa).orElseGet(() -> {
            Khoa k = khoaRepository.save(Khoa.builder()
                    .maKhoa(maKhoa)
                    .tenKhoa(tenKhoa)
                    .build());
            log.info("Seeded Khoa: {}", tenKhoa);
            return k;
        });
    }

    private void seedNganhWithStudent(String maNganh, String tenNganh, Khoa khoa, String username, String maSv, String hoTen) {
        // Tạo Ngành nếu chưa có
        NganhDaoTao nganh = nganhDaoTaoRepository.findByMaNganh(maNganh)
                .orElseGet(() -> {
                    NganhDaoTao n = nganhDaoTaoRepository.save(NganhDaoTao.builder()
                            .maNganh(maNganh)
                            .tenNganh(tenNganh)
                            .heDaoTao("Đại học")
                            .khoa(khoa)
                            .build());
                    log.info("Seeded Ngành: {} ({})", tenNganh, maNganh);
                    return n;
                });

        // Tạo Lớp
        String maLop = maNganh + "-K17";
        String tenLop = tenNganh + " - K17";
        Lop lop = lopRepository.findByMaLop(maLop)
                .orElseGet(() -> {
                    Lop l = lopRepository.save(Lop.builder()
                            .maLop(maLop)
                            .tenLop(tenLop)
                            .namNhapHoc(2023)
                            .nganhDaoTao(nganh)
                            .build());
                    log.info("Seeded Lớp: {}", maLop);
                    return l;
                });

        // Tạo CTĐT + mapping học phần
        seedCtdtForNganh(nganh);

        // Tạo tài khoản SV + profile SinhVien
        seedStudentAccountAndProfile(username, maSv, hoTen, lop);
    }

    private void seedCtdtForNganh(NganhDaoTao nganh) {
        // Kiểm tra đã có CTĐT chưa (dùng native query để tránh lỗi nhiều kết quả)
        if (chuongTrinhDaoTaoRepository.findLatestByNganh(nganh.getIdNganh()).isPresent()) {
            return;
        }

        ChuongTrinhDaoTao ctdt = chuongTrinhDaoTaoRepository.save(ChuongTrinhDaoTao.builder()
                .nganhDaoTao(nganh)
                .tongSoTinChi(130)
                .mucTieu("Đào tạo cử nhân " + nganh.getTenNganh() + " có năng lực chuyên môn vững vàng.")
                .thoiGianGiangDay("4 năm")
                .doiTuongTuyenSinh("Tốt nghiệp THPT, đạt điểm chuẩn tuyển sinh")
                .namApDung(2024)
                .build());
        log.info("Seeded CTĐT {} 2024 (ID: {})", nganh.getTenNganh(), ctdt.getIdCtdt());

        // Mapping học phần vào CTĐT
        seedCtdtHocPhanMapping(ctdt, "BS6001", "DAI_CUONG", true, 1);
        seedCtdtHocPhanMapping(ctdt, "BS6002", "DAI_CUONG", true, 1);
        seedCtdtHocPhanMapping(ctdt, "BS6003", "DAI_CUONG", true, 1);
        seedCtdtHocPhanMapping(ctdt, "BS6004", "CO_SO_NGANH", true, 2);
        seedCtdtHocPhanMapping(ctdt, "IT6001", "CO_SO_NGANH", true, 2);
        seedCtdtHocPhanMapping(ctdt, "IT6002", "CO_SO_NGANH", true, 3);
        seedCtdtHocPhanMapping(ctdt, "IT6003", "CO_SO_NGANH", true, 3);
        seedCtdtHocPhanMapping(ctdt, "IT6004", "CHUYEN_NGANH", true, 5);
        seedCtdtHocPhanMapping(ctdt, "IT6005", "CHUYEN_NGANH", true, 6);
        seedCtdtHocPhanMapping(ctdt, "IT6006", "CHUYEN_NGANH", true, 6);
        seedCtdtHocPhanMapping(ctdt, "IT6007", "TU_CHON", false, 7);
        seedCtdtHocPhanMapping(ctdt, "IT6008", "TU_CHON", false, 8);
    }

    private void seedCtdtHocPhanMapping(ChuongTrinhDaoTao ctdt, String maHp, String khoi, boolean batBuoc, Integer hocKy) {
        hocPhanRepository.findByMaHocPhan(maHp).ifPresent(hp -> {
            if (!ctdtHocPhanRepository.existsByChuongTrinhDaoTao_IdCtdtAndHocPhan_IdHocPhan(ctdt.getIdCtdt(), hp.getIdHocPhan())) {
                ctdtHocPhanRepository.save(CtdtHocPhan.builder()
                        .chuongTrinhDaoTao(ctdt)
                        .hocPhan(hp)
                        .khoiKienThuc(khoi)
                        .batBuoc(batBuoc)
                        .hocKyGoiY(hocKy)
                        .build());
            }
        });
    }

    private void seedStudentAccountAndProfile(String username, String maSv, String hoTen, Lop lop) {
        // Tạo tài khoản nếu chưa có
        User user = userRepository.findByUsername(username).orElseGet(() -> {
            User u = userRepository.save(User.builder()
                    .username(username)
                    .password(passwordEncoder.encode("123456"))
                    .role(Role.STUDENT)
                    .status(Status.ACTIVE)
                    .build());
            log.info("Seeded Student account: {} / 123456", username);
            return u;
        });

        // Tạo profile SinhVien nếu chưa có
        if (sinhVienRepository.findByTaiKhoan_Id(user.getId()).isEmpty()) {
            SinhVien sv = sinhVienRepository.save(SinhVien.builder()
                    .maSinhVien(maSv)
                    .hoTen(hoTen)
                    .lop(lop)
                    .taiKhoan(user)
                    .build());
            log.info("Seeded SinhVien: {} ({})", sv.getMaSinhVien(), sv.getHoTen());
        }
    }

    /**
     * Seed một số Học Phần demo (thuộc 4 khối kiến thức).
     */
    private void seedHocPhanDemo() {
        seedHocPhanIfNotExists("BS6001", "Đại số tuyến tính", 3, "DAI_CUONG");
        seedHocPhanIfNotExists("BS6002", "Giải tích 1", 3, "DAI_CUONG");
        seedHocPhanIfNotExists("BS6003", "Vật lý đại cương 1", 3, "DAI_CUONG");
        seedHocPhanIfNotExists("BS6004", "Nhập môn lập trình", 3, "CO_SO_NGANH");
        seedHocPhanIfNotExists("IT6001", "Cấu trúc dữ liệu và giải thuật", 4, "CO_SO_NGANH");
        seedHocPhanIfNotExists("IT6002", "Lập trình hướng đối tượng", 3, "CO_SO_NGANH");
        seedHocPhanIfNotExists("IT6003", "Cơ sở dữ liệu", 3, "CO_SO_NGANH");
        seedHocPhanIfNotExists("IT6004", "Mạng máy tính", 3, "CHUYEN_NGANH");
        seedHocPhanIfNotExists("IT6005", "Trí tuệ nhân tạo", 3, "CHUYEN_NGANH");
        seedHocPhanIfNotExists("IT6006", "Phát triển ứng dụng Web", 3, "CHUYEN_NGANH");
        seedHocPhanIfNotExists("IT6007", "An toàn thông tin", 3, "TU_CHON");
        seedHocPhanIfNotExists("IT6008", "Thực tập tốt nghiệp", 5, "TU_CHON");
    }

    private void seedHocPhanIfNotExists(String ma, String ten, int tc, String loaiMon) {
        if (!hocPhanRepository.existsByMaHocPhan(ma)) {
            hocPhanRepository.save(HocPhan.builder()
                    .maHocPhan(ma)
                    .tenHocPhan(ten)
                    .soTinChi(tc)
                    .loaiMon(loaiMon)
                    .build());
            log.info("Seeded HocPhan: {} - {}", ma, ten);
        }
    }
}
