package com.example.demo.repository;

import com.example.demo.domain.entity.GiangVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiangVienRepository extends JpaRepository<GiangVien, Long> {
    Optional<GiangVien> findByMaGiangVien(String maGiangVien);
    List<GiangVien> findByKhoa_IdKhoa(Long idKhoa);
    boolean existsByMaGiangVien(String maGiangVien);
}
