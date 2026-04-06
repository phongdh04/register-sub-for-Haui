package com.example.demo.repository;

import com.example.demo.domain.entity.Khoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ISP: Interface chỉ khai báo các method cần thiết cho Khoa.
 * Không nhồi nhét method của NganhDaoTao hay GiangVien vào đây.
 */
@Repository
public interface KhoaRepository extends JpaRepository<Khoa, Long> {
    Optional<Khoa> findByMaKhoa(String maKhoa);
    boolean existsByMaKhoa(String maKhoa);
}
