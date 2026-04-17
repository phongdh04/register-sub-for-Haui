package com.example.demo.repository;

import com.example.demo.domain.entity.GiaoDichVi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiaoDichViRepository extends JpaRepository<GiaoDichVi, Long> {

    boolean existsByGiaoDichThanhToan_IdGiaoDich(Long idGiaoDichThanhToan);

    @Query("""
            SELECT g FROM GiaoDichVi g
            LEFT JOIN FETCH g.giaoDichThanhToan gd
            WHERE g.viSinhVien.idVi = :idVi
            ORDER BY g.thoiGian DESC
            """)
    List<GiaoDichVi> findRecentForVi(@Param("idVi") Long idVi, Pageable pageable);

    @Query(value = "SELECT g FROM GiaoDichVi g ORDER BY g.thoiGian DESC",
            countQuery = "SELECT count(g) FROM GiaoDichVi g")
    Page<GiaoDichVi> findAllPagedOrderByThoiGianDesc(Pageable pageable);
}
