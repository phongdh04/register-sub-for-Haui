package com.example.demo.repository;

import com.example.demo.domain.entity.BangDiemMon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BangDiemMonRepository extends JpaRepository<BangDiemMon, Long> {

    Optional<BangDiemMon> findByDangKyHocPhan_IdDangKy(Long idDangKy);

    /**
     * Task 19 – SV có tổng tín chỉ các môn rớt (điểm hệ 4 &lt; 1.0, đã công bố) vượt ngưỡng, trong cùng khoa.
     */
    @Query("""
            SELECT sv.idSinhVien, sv.maSinhVien, sv.hoTen, l.maLop,
                   COALESCE(SUM(hp.soTinChi), 0), COUNT(bdm.idBangDiem)
            FROM BangDiemMon bdm
            JOIN bdm.dangKyHocPhan dk
            JOIN dk.sinhVien sv
            JOIN sv.lop l
            JOIN l.nganhDaoTao n
            JOIN n.khoa k
            JOIN dk.lopHocPhan lhp
            JOIN lhp.hocPhan hp
            WHERE dk.trangThaiDangKy = 'THANH_CONG'
              AND (bdm.trangThai IS NULL OR bdm.trangThai = 'DA_CONG_BO')
              AND bdm.diemHe4 IS NOT NULL AND bdm.diemHe4 < 1.0
              AND hp.soTinChi IS NOT NULL
              AND k.idKhoa = :khoaId
            GROUP BY sv.idSinhVien, sv.maSinhVien, sv.hoTen, l.maLop
            HAVING COALESCE(SUM(hp.soTinChi), 0) > :minFailTc
            ORDER BY sv.maSinhVien ASC
            """)
    List<Object[]> findStudentsWithFailedCreditsAboveThreshold(
            @Param("khoaId") Long khoaId,
            @Param("minFailTc") int minFailTc);
}
