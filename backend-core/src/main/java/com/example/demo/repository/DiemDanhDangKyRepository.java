package com.example.demo.repository;

import com.example.demo.domain.entity.DiemDanhDangKy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DiemDanhDangKyRepository extends JpaRepository<DiemDanhDangKy, Long> {

    @Query("""
            SELECT d FROM DiemDanhDangKy d
            JOIN FETCH d.dangKyHocPhan dk
            JOIN FETCH dk.sinhVien sv
            WHERE d.buoiDiemDanh.idBuoi = :idBuoi
            ORDER BY sv.hoTen ASC
            """)
    List<DiemDanhDangKy> findByBuoiWithSinhVien(@Param("idBuoi") Long idBuoi);

    Optional<DiemDanhDangKy> findByBuoiDiemDanh_IdBuoiAndDangKyHocPhan_IdDangKy(Long idBuoi, Long idDangKy);

    @Query("""
            SELECT d FROM DiemDanhDangKy d
            JOIN FETCH d.buoiDiemDanh b
            JOIN FETCH b.lopHocPhan l
            JOIN FETCH l.giangVien gv
            LEFT JOIN FETCH gv.taiKhoan
            JOIN FETCH d.dangKyHocPhan dk
            JOIN FETCH dk.sinhVien sv
            WHERE d.idDiemDanh = :id
            """)
    Optional<DiemDanhDangKy> findWithBuoiAndLopForAuth(@Param("id") Long idDiemDanh);
}
