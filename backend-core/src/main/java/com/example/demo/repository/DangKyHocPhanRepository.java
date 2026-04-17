package com.example.demo.repository;

import com.example.demo.domain.entity.DangKyHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho bảng Dang_Ky_Hoc_Phan.
 *
 * SRP: Chỉ chứa query truy xuất dữ liệu, không chứa validation logic.
 * Các query được tối ưu để phục vụ Chain of Responsibility Validator.
 */
@Repository
public interface DangKyHocPhanRepository extends JpaRepository<DangKyHocPhan, Long> {

    List<DangKyHocPhan> findByLopHocPhan_IdLopHpAndTrangThaiDangKyIn(Long idLopHp, Collection<String> trangThais);

    Optional<DangKyHocPhan> findFirstByLopHocPhan_IdLopHpAndSinhVien_IdSinhVienAndTrangThaiDangKyIn(
            Long idLopHp, Long idSinhVien, Collection<String> trangThaiDangKys);

    /**
     * Kiểm tra SV đã đăng ký lớp này trong HK này chưa (chống ghi đè).
     * Dùng trong bước đầu tiên của Chain: KiemTraTrungLopHandler.
     */
    boolean existsBySinhVien_IdSinhVienAndLopHocPhan_IdLopHpAndHocKy_IdHocKy(
            Long idSinhVien, Long idLopHp, Long idHocKy);

    /**
     * Lấy tất cả các lớp SV đã đăng ký trong HK (để kiểm tra trùng lịch).
     * JOIN FETCH để tránh N+1 query khi load TKB JSON của từng lớp.
     */
    @Query("""
            SELECT d FROM DangKyHocPhan d
            JOIN FETCH d.lopHocPhan lhp
            JOIN FETCH lhp.hocPhan hp
            WHERE d.sinhVien.idSinhVien = :svId
              AND d.hocKy.idHocKy = :hkId
              AND d.trangThaiDangKy IN ('THANH_CONG', 'CHO_DUYET')
            """)
    List<DangKyHocPhan> findRegisteredCoursesInSemester(
            @Param("svId") Long svId,
            @Param("hkId") Long hkId);

    /**
     * Lấy danh sách lớp đã đăng ký để render TKB.
     * Nếu hkId null thì lấy theo mọi học kỳ (service sẽ ưu tiên học kỳ hiện hành trước).
     */
    @Query("""
            SELECT d FROM DangKyHocPhan d
            JOIN FETCH d.lopHocPhan lhp
            LEFT JOIN FETCH lhp.giangVien gv
            JOIN FETCH lhp.hocPhan hp
            JOIN FETCH d.hocKy hk
            WHERE d.sinhVien.idSinhVien = :svId
              AND d.trangThaiDangKy IN ('THANH_CONG', 'CHO_DUYET')
              AND (:hkId IS NULL OR hk.idHocKy = :hkId)
            ORDER BY hk.idHocKy DESC, hp.tenHocPhan ASC
            """)
    List<DangKyHocPhan> findTimetableRegistrations(
            @Param("svId") Long svId,
            @Param("hkId") Long hkId);

    /**
     * Lấy danh sách mã học phần SV đã đậu (điều kiện tiên quyết).
     * Dùng trong bước cuối Chain: KiemTraTienQuyetHandler.
     * Lưu ý: Bảng điểm sẽ được implement ở Task 4 (Transcript).
     *         Hiện tại trả về list rỗng nếu chưa có bảng Diem.
     */
    @Query("""
            SELECT hp.maHocPhan
            FROM DangKyHocPhan d
            JOIN d.lopHocPhan lhp
            JOIN lhp.hocPhan hp
            WHERE d.sinhVien.idSinhVien = :svId
              AND d.trangThaiDangKy = 'THANH_CONG'
              AND EXISTS (
                  SELECT 1 FROM BangDiemMon bdm
                  WHERE bdm.dangKyHocPhan = d
                    AND bdm.diemHe4 IS NOT NULL
                    AND bdm.diemHe4 >= 1.0
                    AND (bdm.trangThai IS NULL OR bdm.trangThai = 'DA_CONG_BO')
              )
            """)
    List<String> findCompletedCourseCodes(@Param("svId") Long svId);

    /**
     * Bảng điểm / transcript: đăng ký thành công + điểm (nếu đã nhập).
     */
    @Query("""
            SELECT d FROM DangKyHocPhan d
            JOIN FETCH d.lopHocPhan lhp
            JOIN FETCH lhp.hocPhan hp
            JOIN FETCH d.hocKy hk
            LEFT JOIN FETCH d.bangDiemMon bdm
            WHERE d.sinhVien.idSinhVien = :svId
              AND d.trangThaiDangKy = 'THANH_CONG'
              AND (:hkId IS NULL OR hk.idHocKy = :hkId)
            ORDER BY hk.idHocKy DESC, hp.tenHocPhan ASC
            """)
    List<DangKyHocPhan> findTranscriptRows(
            @Param("svId") Long svId,
            @Param("hkId") Long hkId);

    @Query("""
            SELECT d FROM DangKyHocPhan d
            JOIN FETCH d.sinhVien sv
            LEFT JOIN FETCH d.bangDiemMon bdm
            WHERE d.lopHocPhan.idLopHp = :idLopHp
              AND d.trangThaiDangKy IN ('THANH_CONG', 'CHO_DUYET')
            ORDER BY sv.hoTen ASC
            """)
    List<DangKyHocPhan> findGradebookRowsForLop(@Param("idLopHp") Long idLopHp);

    @Query("""
            SELECT d FROM DangKyHocPhan d
            JOIN FETCH d.sinhVien sv
            JOIN FETCH d.lopHocPhan lhp
            JOIN FETCH lhp.giangVien gv
            LEFT JOIN FETCH gv.taiKhoan
            LEFT JOIN FETCH d.bangDiemMon bdm
            WHERE d.idDangKy = :idDangKy
            """)
    Optional<DangKyHocPhan> findWithLopAndGiangVienForGrade(@Param("idDangKy") Long idDangKy);

    @Query("""
            SELECT COALESCE(SUM(lhp.hocPhi), 0) FROM DangKyHocPhan d
            JOIN d.lopHocPhan lhp
            WHERE d.sinhVien.idSinhVien = :svId
              AND d.trangThaiDangKy IN ('THANH_CONG', 'CHO_DUYET')
              AND lhp.hocPhi IS NOT NULL
            """)
    BigDecimal sumHocPhiDangKyBySinhVien(@Param("svId") Long svId);

    @Query("""
            SELECT d.sinhVien.idSinhVien, COALESCE(SUM(lhp.hocPhi), 0)
            FROM DangKyHocPhan d JOIN d.lopHocPhan lhp
            WHERE d.sinhVien.idSinhVien IN :ids
              AND d.trangThaiDangKy IN ('THANH_CONG', 'CHO_DUYET')
              AND lhp.hocPhi IS NOT NULL
            GROUP BY d.sinhVien.idSinhVien
            """)
    List<Object[]> sumHocPhiDangKyBySinhVienIds(@Param("ids") Collection<Long> ids);

    @Query("""
            SELECT d.sinhVien.idSinhVien, COALESCE(SUM(lhp.hocPhi), 0)
            FROM DangKyHocPhan d JOIN d.lopHocPhan lhp
            WHERE d.trangThaiDangKy IN ('THANH_CONG', 'CHO_DUYET')
              AND lhp.hocPhi IS NOT NULL
            GROUP BY d.sinhVien.idSinhVien
            """)
    List<Object[]> sumHocPhiDangKyGroupedBySinhVien();

    /**
     * Task 19 – Cố vấn: SV thuộc ngành của {@code idKhoa}, tổng tín chỉ môn rớt (điểm công bố dưới 1.0) ≥ {@code minFailedCredits}.
     */
    @Query("""
            SELECT sv.idSinhVien, sv.maSinhVien, sv.hoTen, l.maLop, l.tenLop,
                   SUM(hp.soTinChi), COUNT(d)
            FROM DangKyHocPhan d
            JOIN d.sinhVien sv
            JOIN sv.lop l
            JOIN l.nganhDaoTao n
            JOIN n.khoa k
            JOIN d.lopHocPhan lhp
            JOIN lhp.hocPhan hp
            JOIN d.bangDiemMon bdm
            WHERE d.trangThaiDangKy = 'THANH_CONG'
              AND k.idKhoa = :idKhoa
              AND hp.soTinChi IS NOT NULL
              AND (bdm.trangThai IS NULL OR bdm.trangThai = 'DA_CONG_BO')
              AND bdm.diemHe4 IS NOT NULL
              AND bdm.diemHe4 < 1.0
            GROUP BY sv.idSinhVien, sv.maSinhVien, sv.hoTen, l.maLop, l.tenLop
            HAVING SUM(hp.soTinChi) >= :minFailedCredits
            ORDER BY SUM(hp.soTinChi) DESC
            """)
    List<Object[]> findStudentsWithFailedCreditsAboveThreshold(
            @Param("idKhoa") Long idKhoa,
            @Param("minFailedCredits") int minFailedCredits);

    /**
     * GPA tích lũy (điểm hệ 4 đã công bố) — bổ sung cho danh sách Task 19.
     */
    @Query("""
            SELECT d.sinhVien.idSinhVien,
                   SUM(bdm.diemHe4 * hp.soTinChi),
                   SUM(hp.soTinChi)
            FROM DangKyHocPhan d
            JOIN d.lopHocPhan lhp
            JOIN lhp.hocPhan hp
            JOIN d.bangDiemMon bdm
            WHERE d.trangThaiDangKy = 'THANH_CONG'
              AND hp.soTinChi IS NOT NULL
              AND bdm.diemHe4 IS NOT NULL
              AND (bdm.trangThai IS NULL OR bdm.trangThai = 'DA_CONG_BO')
              AND d.sinhVien.idSinhVien IN :svIds
            GROUP BY d.sinhVien.idSinhVien
            """)
    List<Object[]> findCumulativeGpaFactorsBySinhVienIds(@Param("svIds") Collection<Long> svIds);
}
