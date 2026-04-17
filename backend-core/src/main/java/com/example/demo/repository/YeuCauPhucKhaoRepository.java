package com.example.demo.repository;

import com.example.demo.domain.entity.YeuCauPhucKhao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface YeuCauPhucKhaoRepository extends JpaRepository<YeuCauPhucKhao, Long> {

    boolean existsByDangKy_IdDangKyAndTrangThai(Long idDangKy, String trangThai);

    @Query("""
            SELECT y FROM YeuCauPhucKhao y
            JOIN FETCH y.dangKy d
            JOIN FETCH d.sinhVien sv
            JOIN FETCH d.lopHocPhan lhp
            JOIN FETCH lhp.hocPhan hp
            JOIN FETCH lhp.giangVien gv
            JOIN FETCH d.hocKy hk
            LEFT JOIN FETCH d.bangDiemMon bdm
            WHERE d.sinhVien.idSinhVien = :svId
            ORDER BY y.ngayTao DESC
            """)
    List<YeuCauPhucKhao> findBySinhVienOrderByNgayTaoDesc(@Param("svId") Long svId);

    @Query("""
            SELECT y FROM YeuCauPhucKhao y
            JOIN FETCH y.dangKy d
            JOIN FETCH d.sinhVien sv
            JOIN FETCH sv.lop l
            JOIN FETCH d.lopHocPhan lhp
            JOIN FETCH lhp.hocPhan hp
            JOIN FETCH lhp.giangVien gv
            JOIN FETCH d.hocKy hk
            LEFT JOIN FETCH d.bangDiemMon bdm
            WHERE gv.idGiangVien = :idGv
              AND (:trangThai IS NULL OR y.trangThai = :trangThai)
            ORDER BY y.ngayTao DESC
            """)
    List<YeuCauPhucKhao> findForLecturer(@Param("idGv") Long idGv, @Param("trangThai") String trangThai);

    @Query("""
            SELECT y FROM YeuCauPhucKhao y
            JOIN FETCH y.dangKy d
            JOIN FETCH d.sinhVien sv
            JOIN FETCH sv.lop l
            JOIN FETCH d.lopHocPhan lhp
            JOIN FETCH lhp.hocPhan hp
            JOIN FETCH lhp.giangVien gv
            JOIN FETCH d.hocKy hk
            LEFT JOIN FETCH d.bangDiemMon bdm
            WHERE y.idYeuCau = :id
            """)
    Optional<YeuCauPhucKhao> findByIdWithGraph(@Param("id") Long id);
}
