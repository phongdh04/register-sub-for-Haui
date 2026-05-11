package com.example.demo.repository;

import com.example.demo.domain.entity.CtdtHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CtdtHocPhanRepository extends JpaRepository<CtdtHocPhan, Long> {

    boolean existsByChuongTrinhDaoTao_IdCtdtAndHocPhan_IdHocPhan(Long idCtdt, Long idHocPhan);

    @Query("""
            SELECT m FROM CtdtHocPhan m
            JOIN FETCH m.hocPhan hp
            WHERE m.chuongTrinhDaoTao.idCtdt = :ctdtId
            ORDER BY m.khoiKienThuc ASC, hp.tenHocPhan ASC
            """)
    List<CtdtHocPhan> findAllByCtdtId(@Param("ctdtId") Long ctdtId);
}

