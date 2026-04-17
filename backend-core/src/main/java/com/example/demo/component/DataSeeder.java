package com.example.demo.component;

import com.example.demo.domain.entity.GiangVien;
import com.example.demo.domain.entity.Khoa;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.Status;
import com.example.demo.repository.GiangVienRepository;
import com.example.demo.repository.KhoaRepository;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
                    .build());
            log.info("Seeded Admin account: admin / 123456");
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

        log.info("Database seeding completed.");
    }
}
