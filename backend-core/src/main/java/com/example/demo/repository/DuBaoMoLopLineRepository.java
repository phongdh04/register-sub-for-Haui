package com.example.demo.repository;

import com.example.demo.domain.entity.DuBaoMoLopLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DuBaoMoLopLineRepository extends JpaRepository<DuBaoMoLopLine, Long> {

    @Query("""
            SELECT l FROM DuBaoMoLopLine l
            JOIN FETCH l.hocPhan hp
            WHERE l.duBaoMoLopVersion.idDuBaoVersion = :vid
            ORDER BY hp.maHocPhan ASC
            """)
    List<DuBaoMoLopLine> findLinesWithHocPhanByVersion(@Param("vid") Long versionId);

    long countByDuBaoMoLopVersion_IdDuBaoVersion(Long versionId);
}
