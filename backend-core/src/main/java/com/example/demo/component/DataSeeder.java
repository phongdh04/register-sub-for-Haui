package com.example.demo.component;

import com.example.demo.domain.entity.DangKyHocPhan;
import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.Khoa;
import com.example.demo.domain.entity.LichThi;
import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.entity.PhieuDuThi;
import com.example.demo.domain.entity.GvBusySlot;
import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.GvBusyLoai;
import com.example.demo.domain.enums.LoaiPhong;
import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.Status;
import com.example.demo.domain.enums.TrangThaiPhong;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.GvBusySlotRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.KhoaRepository;
import com.example.demo.repository.LichThiRepository;
import com.example.demo.repository.PhieuDuThiRepository;
import com.example.demo.repository.PhongHocRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

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

        log.info("Database seeding completed.");
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
}
