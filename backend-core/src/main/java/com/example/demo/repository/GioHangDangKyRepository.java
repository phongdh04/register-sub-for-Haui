package com.example.demo.repository;

import com.example.demo.domain.entity.GioHangDangKy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GioHangDangKyRepository extends JpaRepository<GioHangDangKy, Long> {

    @Query("""
            SELECT g FROM GioHangDangKy g
            JOIN FETCH g.lopHocPhan lhp
            JOIN FETCH lhp.hocPhan hp
            JOIN FETCH g.hocKy hk
            WHERE g.sinhVien.idSinhVien = :svId AND hk.idHocKy = :hkId
            ORDER BY g.ngayThem DESC
            """)
    List<GioHangDangKy> findBySinhVienAndHocKyWithLop(@Param("svId") Long svId, @Param("hkId") Long hkId);

    boolean existsBySinhVien_IdSinhVienAndLopHocPhan_IdLopHpAndHocKy_IdHocKy(Long idSinhVien, Long idLopHp, Long idHocKy);

    Optional<GioHangDangKy> findByIdGioHangAndSinhVien_IdSinhVien(Long idGioHang, Long idSinhVien);
}
