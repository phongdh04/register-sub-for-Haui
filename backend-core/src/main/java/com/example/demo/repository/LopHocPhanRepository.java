package com.example.demo.repository;

import com.example.demo.domain.entity.LopHocPhan;
import com.example.demo.domain.enums.LopHocPhanPublishStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Repository cho bảng LopHocPhan - Bottleneck chính.
 *
 * Kế thừa JpaSpecificationExecutor để hỗ trợ Dynamic Query (Task 6 - Search).
 * Có query atomic update siSoThucTe để tránh Race Condition khi sync từ Redis.
 */
@Repository
public interface LopHocPhanRepository extends JpaRepository<LopHocPhan, Long>,
        JpaSpecificationExecutor<LopHocPhan> {  // ← Thêm cho Task 6 (Tìm kiếm động)

    Optional<LopHocPhan> findByMaLopHp(String maLopHp);

    List<LopHocPhan> findByHocKy_IdHocKy(Long idHocKy);

    /**
     * Danh sách lớp theo học kỳ kèm học phần/GV (tránh lazy ngoài session — dùng cho admin + public list).
     */
    @Query("""
            SELECT l FROM LopHocPhan l
            JOIN FETCH l.hocPhan hp
            JOIN FETCH l.hocKy hk
            LEFT JOIN FETCH l.giangVien gv
            LEFT JOIN FETCH l.phongHoc ph
            WHERE hk.idHocKy = :idHocKy
            ORDER BY l.maLopHp ASC
            """)
    List<LopHocPhan> findAllByHocKy_IdHocKyWithAssociations(@Param("idHocKy") Long idHocKy);
    List<LopHocPhan> findByTkbBlock_IdTkbBlock(Long idTkbBlock);

    /** Eager các FK cần cho snapshot admin (BK-TKB-011/012). */
    @Query("""
            SELECT DISTINCT l FROM LopHocPhan l
            LEFT JOIN FETCH l.phongHoc ph
            LEFT JOIN FETCH l.giangVien gv
            LEFT JOIN FETCH l.hocPhan hp
            LEFT JOIN FETCH l.tkbBlock tb
            WHERE l.hocKy.idHocKy = :hid
            """)
    List<LopHocPhan> findForSchedulingSnapshot(@Param("hid") Long idHocKy);

    List<LopHocPhan> findByHocKy_IdHocKyAndTrangThai(Long idHocKy, String trangThai);

    @Query("""
            SELECT DISTINCT l FROM LopHocPhan l
            JOIN FETCH l.hocPhan hp
            JOIN FETCH l.hocKy hk
            LEFT JOIN FETCH l.giangVien gv
            WHERE hk.idHocKy = :hkId
              AND hp.idHocPhan IN :ids
              AND l.trangThai = :tt
            ORDER BY hp.tenHocPhan ASC
            """)
    List<LopHocPhan> findOpenByHocKyAndHocPhanIds(
            @Param("hkId") Long idHocKy,
            @Param("ids") Collection<Long> idHocPhans,
            @Param("tt") String trangThai);

    List<LopHocPhan> findByGiangVien_IdGiangVien(Long idGiangVien);

    @Query("""
            SELECT DISTINCT l FROM LopHocPhan l
            JOIN FETCH l.hocPhan hp
            JOIN FETCH l.hocKy hk
            WHERE l.giangVien.idGiangVien = :gvId
            ORDER BY hk.idHocKy DESC, hp.tenHocPhan ASC
            """)
    List<LopHocPhan> findTeachingClassesForGiangVien(@Param("gvId") Long gvId);

    @Query("""
            SELECT l FROM LopHocPhan l
            JOIN FETCH l.giangVien gv
            LEFT JOIN FETCH gv.taiKhoan
            JOIN FETCH l.hocPhan
            JOIN FETCH l.hocKy
            WHERE l.idLopHp = :id
            """)
    Optional<LopHocPhan> findWithGiangVienForAttendance(@Param("id") Long id);

    boolean existsByMaLopHp(String maLopHp);

    boolean existsByPhongHoc_IdPhong(Long idPhong);

    /**
     * Atomic increment để sync kết quả từ Redis về DB.
     * Dùng khi Background Worker xử lý message từ Queue.
     * Đảm bảo không bị Lost Update khi nhiều Worker chạy song song.
     */
    @Modifying
    @Query("UPDATE LopHocPhan l SET l.siSoThucTe = l.siSoThucTe + 1 WHERE l.idLopHp = :id AND l.siSoThucTe < l.siSoToiDa")
    int incrementSiSoThucTe(@Param("id") Long id);

    /**
     * Atomic decrement khi SV rút môn.
     */
    @Modifying
    @Query("UPDATE LopHocPhan l SET l.siSoThucTe = l.siSoThucTe - 1 WHERE l.idLopHp = :id AND l.siSoThucTe > 0")
    int decrementSiSoThucTe(@Param("id") Long id);

    @Query("""
            SELECT lhp.maLopHp, hp.tenHocPhan, lhp.siSoToiDa, lhp.siSoThucTe
            FROM LopHocPhan lhp JOIN lhp.hocPhan hp
            WHERE lhp.hocKy.idHocKy = :hkId AND lhp.siSoToiDa > 0
            ORDER BY lhp.siSoThucTe DESC, lhp.maLopHp ASC
            """)
    List<Object[]> topClassesByHeadcountForHocKy(@Param("hkId") Long hkId, Pageable pageable);

    /**
     * Sprint 3 — list lớp theo học kỳ + status_publish (cho admin bulk-publish workflow).
     * Có thể lọc thêm theo cohort (nam_nhap_hoc của ngành đào tạo của các sinh viên đăng ký
     * không xác định trên LHP — cohort gắn vào LHP qua các quyết định mở lớp ở pha B).
     * Hiện tại bulk filter dựa trên hocKy + status; cohort filter dùng tham số JPA cho
     * tương lai.
     */
    @Query("""
            SELECT l FROM LopHocPhan l
            JOIN FETCH l.hocPhan hp
            LEFT JOIN FETCH l.giangVien gv
            WHERE l.hocKy.idHocKy = :hkId
              AND (:status IS NULL OR l.statusPublish = :status)
            ORDER BY hp.maHocPhan ASC, l.maLopHp ASC
            """)
    List<LopHocPhan> findByHocKyAndStatusPublish(
            @Param("hkId") Long hocKyId,
            @Param("status") LopHocPhanPublishStatus status);

    /**
     * Sprint 6 — fill-rate dashboard. Chỉ trả về lớp đã PUBLISHED và có siSoToiDa > 0.
     * {@code Object[] {idLopHp, maLopHp, maHocPhan, tenHocPhan, siSoToiDa, siSoThucTe}}.
     */
    @Query("""
            SELECT l.idLopHp, l.maLopHp, hp.maHocPhan, hp.tenHocPhan,
                   l.siSoToiDa, l.siSoThucTe
            FROM LopHocPhan l JOIN l.hocPhan hp
            WHERE l.hocKy.idHocKy = :hkId
              AND l.statusPublish = com.example.demo.domain.enums.LopHocPhanPublishStatus.PUBLISHED
              AND l.siSoToiDa > 0
            ORDER BY (1.0 * l.siSoThucTe / l.siSoToiDa) DESC, l.maLopHp ASC
            """)
    List<Object[]> findFillRateRowsForHocKy(@Param("hkId") Long hkId);
}
