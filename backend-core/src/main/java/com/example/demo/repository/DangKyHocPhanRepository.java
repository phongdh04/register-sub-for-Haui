package com.example.demo.repository;

import com.example.demo.domain.entity.DangKyHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho bảng Dang_Ky_Hoc_Phan.
 *
 * SRP: Chỉ chứa query truy xuất dữ liệu, không chứa validation logic.
 * Các query được tối ưu để phục vụ Chain of Responsibility Validator.
 */
@Repository
public interface DangKyHocPhanRepository extends JpaRepository<DangKyHocPhan, Long> {

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
            SELECT d.lopHocPhan.hocPhan.maHocPhan
            FROM DangKyHocPhan d
            WHERE d.sinhVien.idSinhVien = :svId
              AND d.trangThaiDangKy = 'THANH_CONG'
            """)
    List<String> findCompletedCourseCodes(@Param("svId") Long svId);
}
