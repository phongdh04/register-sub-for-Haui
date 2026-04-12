package com.example.demo.repository;

import com.example.demo.domain.entity.SinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository cho bảng Sinh_Vien.
 * SRP: Chỉ truy xuất dữ liệu SinhVien, không xử lý logic nghiệp vụ.
 */
@Repository
public interface SinhVienRepository extends JpaRepository<SinhVien, Long> {

    Optional<SinhVien> findByMaSinhVien(String maSinhVien);

    boolean existsByMaSinhVien(String maSinhVien);
}
