package com.example.demo.repository;

import com.example.demo.domain.entity.PreRegistrationIntent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho {@link PreRegistrationIntent}.
 *
 * <p>Cung cấp:
 * <ul>
 *   <li>Truy vấn list theo sinh viên + học kỳ.</li>
 *   <li>Truy vấn aggregation cho admin demand dashboard.</li>
 * </ul>
 */
@Repository
public interface PreRegistrationIntentRepository extends JpaRepository<PreRegistrationIntent, Long> {

    Optional<PreRegistrationIntent> findBySinhVien_IdSinhVienAndHocKy_IdHocKyAndHocPhan_IdHocPhan(
            Long idSinhVien, Long idHocKy, Long idHocPhan);

    @Query("""
            SELECT i FROM PreRegistrationIntent i
            JOIN FETCH i.hocPhan hp
            JOIN FETCH i.hocKy hk
            WHERE i.sinhVien.idSinhVien = :idSinhVien
              AND i.hocKy.idHocKy = :idHocKy
            ORDER BY i.priority ASC, i.createdAt ASC
            """)
    List<PreRegistrationIntent> findBySinhVienAndHocKy(
            @Param("idSinhVien") Long idSinhVien,
            @Param("idHocKy") Long idHocKy);

    /**
     * Các học phần có nguyện vọng PRE trong học kỳ — dùng hợp union CTĐT khi hiển thị lớp đăng ký chính thức.
     */
    @Query("""
            SELECT DISTINCT i.hocPhan.idHocPhan FROM PreRegistrationIntent i
            WHERE i.sinhVien.idSinhVien = :idSinhVien
              AND i.hocKy.idHocKy = :idHocKy
            """)
    List<Long> findDistinctHocPhanIdsBySinhVienAndHocKy(
            @Param("idSinhVien") Long idSinhVien,
            @Param("idHocKy") Long idHocKy);

    long countByHocKy_IdHocKy(Long idHocKy);

    /**
     * Aggregate demand theo (học phần × cohort × ngành) cho admin dashboard.
     *
     * <p>Cohort = {@code Lop.namNhapHoc} của sinh viên gửi intent.
     *
     * <p>Filter optional:
     * <ul>
     *   <li>{@code namNhapHoc IS NULL} → tổng hợp mọi cohort.</li>
     *   <li>{@code idNganh IS NULL} → tổng hợp mọi ngành.</li>
     * </ul>
     *
     * <p>Trả về Object[]: [idHocPhan, maHocPhan, tenHocPhan, soTinChi, namNhapHoc, idNganh, tenNganh, totalIntent].
     */
    @Query("""
            SELECT hp.idHocPhan, hp.maHocPhan, hp.tenHocPhan, hp.soTinChi,
                   l.namNhapHoc, n.idNganh, n.tenNganh,
                   COUNT(i.id)
            FROM PreRegistrationIntent i
            JOIN i.hocPhan hp
            JOIN i.sinhVien sv
            JOIN sv.lop l
            JOIN l.nganhDaoTao n
            WHERE i.hocKy.idHocKy = :idHocKy
              AND (:namNhapHoc IS NULL OR l.namNhapHoc = :namNhapHoc)
              AND (:idNganh IS NULL OR n.idNganh = :idNganh)
            GROUP BY hp.idHocPhan, hp.maHocPhan, hp.tenHocPhan, hp.soTinChi,
                     l.namNhapHoc, n.idNganh, n.tenNganh
            ORDER BY COUNT(i.id) DESC, hp.maHocPhan ASC
            """)
    List<Object[]> aggregateDemand(
            @Param("idHocKy") Long idHocKy,
            @Param("namNhapHoc") Integer namNhapHoc,
            @Param("idNganh") Long idNganh);
}
