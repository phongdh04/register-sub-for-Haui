package com.example.demo.repository;

import com.example.demo.domain.entity.StudentTimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository cho read-model TKB sinh viên (Sprint 5).
 */
@Repository
public interface StudentTimetableEntryRepository extends JpaRepository<StudentTimetableEntry, Long> {

    /** Read all slots của 1 sinh viên trong 1 học kỳ, sort theo (thu, tiet) cho UI render. */
    @Query("""
            SELECT e FROM StudentTimetableEntry e
            WHERE e.idSinhVien = :svId
              AND e.idHocKy = :hkId
            ORDER BY e.thu ASC, e.tiet ASC, e.maLopHp ASC
            """)
    List<StudentTimetableEntry> findBySinhVienAndHocKy(@Param("svId") Long svId,
                                                       @Param("hkId") Long hkId);

    /** Toàn bộ slots của 1 đăng ký — dùng để xoá khi cancel. */
    List<StudentTimetableEntry> findByIdDangKy(Long idDangKy);

    @Modifying
    @Query("DELETE FROM StudentTimetableEntry e WHERE e.idDangKy = :idDangKy")
    int deleteByIdDangKy(@Param("idDangKy") Long idDangKy);

    @Modifying
    @Query("DELETE FROM StudentTimetableEntry e WHERE e.idSinhVien = :svId AND e.idHocKy = :hkId")
    int deleteBySinhVienAndHocKy(@Param("svId") Long svId, @Param("hkId") Long hkId);
}
