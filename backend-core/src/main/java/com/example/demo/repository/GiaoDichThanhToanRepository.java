package com.example.demo.repository;

import com.example.demo.domain.entity.GiaoDichThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiaoDichThanhToanRepository extends JpaRepository<GiaoDichThanhToan, Long> {

    Optional<GiaoDichThanhToan> findByIdAndSinhVien_IdSinhVien(Long id, Long idSinhVien);

    Optional<GiaoDichThanhToan> findByMaDonHang(String maDonHang);
}
