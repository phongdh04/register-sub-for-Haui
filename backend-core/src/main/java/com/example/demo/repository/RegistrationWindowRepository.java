package com.example.demo.repository;

import com.example.demo.domain.entity.RegistrationWindow;
import com.example.demo.domain.enums.RegistrationPhase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho {@link RegistrationWindow}.
 *
 * <p>Truy vấn chính được tối ưu cho 2 use case:
 * <ul>
 *   <li>Admin liệt kê windows theo bộ lọc.</li>
 *   <li>Resolver runtime tìm window phù hợp nhất với 1 sinh viên (cohort, ngành).</li>
 * </ul>
 */
@Repository
public interface RegistrationWindowRepository extends JpaRepository<RegistrationWindow, Long> {

    /**
     * Liệt kê tất cả window của 1 học kỳ + phase, sắp xếp theo specificity giảm dần
     * (exact match xuất hiện trước window general).
     */
    @Query("""
            SELECT w FROM RegistrationWindow w
            LEFT JOIN FETCH w.nganhDaoTao
            WHERE w.hocKy.idHocKy = :hocKyId AND w.phase = :phase
            ORDER BY
              CASE WHEN w.namNhapHoc IS NULL THEN 1 ELSE 0 END,
              CASE WHEN w.nganhDaoTao IS NULL THEN 1 ELSE 0 END,
              w.openAt
            """)
    List<RegistrationWindow> findByHocKyAndPhaseOrdered(
            @Param("hocKyId") Long hocKyId,
            @Param("phase") RegistrationPhase phase);

    /**
     * Liệt kê windows theo bộ lọc admin (hocKy bắt buộc, phase optional).
     */
    @Query("""
            SELECT w FROM RegistrationWindow w
            LEFT JOIN FETCH w.nganhDaoTao
            LEFT JOIN FETCH w.hocKy
            LEFT JOIN FETCH w.campaign
            WHERE w.hocKy.idHocKy = :hocKyId
              AND (:phase IS NULL OR w.phase = :phase)
            ORDER BY w.phase, w.namNhapHoc NULLS LAST, w.openAt
            """)
    List<RegistrationWindow> findForAdminListing(
            @Param("hocKyId") Long hocKyId,
            @Param("phase") RegistrationPhase phase);

    /**
     * Resolve window phù hợp nhất theo cohort + ngành (specific → general).
     * Trả về Optional rỗng nếu không có window nào áp dụng.
     */
    @Query("""
            SELECT w FROM RegistrationWindow w
            WHERE w.hocKy.idHocKy = :hocKyId
              AND w.phase = :phase
              AND (w.namNhapHoc IS NULL OR w.namNhapHoc = :namNhapHoc)
              AND (w.nganhDaoTao IS NULL OR w.nganhDaoTao.idNganh = :nganhId)
            ORDER BY
              CASE WHEN w.namNhapHoc IS NULL THEN 1 ELSE 0 END,
              CASE WHEN w.nganhDaoTao IS NULL THEN 1 ELSE 0 END,
              w.openAt DESC
            """)
    List<RegistrationWindow> findApplicableForResolver(
            @Param("hocKyId") Long hocKyId,
            @Param("phase") RegistrationPhase phase,
            @Param("namNhapHoc") Integer namNhapHoc,
            @Param("nganhId") Long nganhId);

    /**
     * Kiểm tra trùng cấu hình theo bộ khóa logic (hocKy, phase, namNhapHoc, nganhId).
     * Dùng khi tạo/sửa để báo lỗi 409 thay vì để DB throw constraint exception.
     */
    @Query("""
            SELECT w FROM RegistrationWindow w
            WHERE w.hocKy.idHocKy = :hocKyId
              AND w.phase = :phase
              AND ((w.namNhapHoc IS NULL AND :namNhapHoc IS NULL) OR w.namNhapHoc = :namNhapHoc)
              AND (
                    (w.nganhDaoTao IS NULL AND :nganhId IS NULL)
                    OR w.nganhDaoTao.idNganh = :nganhId
                  )
            """)
    Optional<RegistrationWindow> findExistingScope(
            @Param("hocKyId") Long hocKyId,
            @Param("phase") RegistrationPhase phase,
            @Param("namNhapHoc") Integer namNhapHoc,
            @Param("nganhId") Long nganhId);

    /**
     * Lay tat ca window thuoc mot campaign.
     */
    @Query("""
            SELECT w FROM RegistrationWindow w
            LEFT JOIN FETCH w.hocKy
            LEFT JOIN FETCH w.campaign
            WHERE w.campaign.id = :campaignId
            ORDER BY w.hocKy.namHoc, w.hocKy.kyThu
            """)
    List<RegistrationWindow> findByCampaignId(@Param("campaignId") Long campaignId);

    /**
     * Lay tat ca windows thuoc nhieu campaign (cho listAll/Active).
     */
    @Query("""
            SELECT w FROM RegistrationWindow w
            LEFT JOIN FETCH w.hocKy
            LEFT JOIN FETCH w.campaign
            WHERE w.campaign.id IN :campaignIds
            ORDER BY w.campaign.id, w.hocKy.namHoc, w.hocKy.kyThu
            """)
    List<RegistrationWindow> findAllByCampaignIds(@Param("campaignIds") List<Long> campaignIds);

    /**
     * Xoa tat ca windows thuoc mot campaign.
     */
    @Modifying
    @Query("DELETE FROM RegistrationWindow w WHERE w.campaign.id = :campaignId")
    void deleteByCampaignId(@Param("campaignId") Long campaignId);
}
