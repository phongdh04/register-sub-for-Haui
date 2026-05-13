package com.example.demo.repository;

import com.example.demo.domain.entity.GiangVien;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GiangVienRepository extends JpaRepository<GiangVien, Long> {
    Optional<GiangVien> findByTaiKhoan_Id(Long taiKhoanId);

    Optional<GiangVien> findByMaGiangVien(String maGiangVien);
    List<GiangVien> findByKhoa_IdKhoa(Long idKhoa);
    Page<GiangVien> findByKhoa_IdKhoa(Long idKhoa, Pageable pageable);
    boolean existsByMaGiangVien(String maGiangVien);
}
