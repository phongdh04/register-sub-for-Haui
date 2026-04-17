package com.example.demo.repository;

import com.example.demo.domain.entity.GiaoDichThanhToan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiaoDichThanhToanRepository extends JpaRepository<GiaoDichThanhToan, Long> {

    Optional<GiaoDichThanhToan> findByIdAndSinhVien_IdSinhVien(Long id, Long idSinhVien);

    Optional<GiaoDichThanhToan> findByMaDonHang(String maDonHang);

    long countByTrangThai(String trangThai);

    @Query("SELECT COALESCE(SUM(g.soTien), 0) FROM GiaoDichThanhToan g WHERE g.trangThai = :trangThai")
    BigDecimal sumSoTienByTrangThai(@Param("trangThai") String trangThai);

    @Query("""
            SELECT g FROM GiaoDichThanhToan g
            WHERE (:trangThai IS NULL OR g.trangThai = :trangThai)
              AND (:provider IS NULL OR g.provider = :provider)
            ORDER BY g.taoLuc DESC
            """)
    Page<GiaoDichThanhToan> pageForAdmin(
            @Param("trangThai") String trangThai,
            @Param("provider") String provider,
            Pageable pageable);

    @Query("""
            SELECT g.trangThai, COUNT(g), COALESCE(SUM(g.soTien), 0)
            FROM GiaoDichThanhToan g
            GROUP BY g.trangThai
            ORDER BY g.trangThai ASC
            """)
    List<Object[]> aggregatePaymentsByStatus();
}
