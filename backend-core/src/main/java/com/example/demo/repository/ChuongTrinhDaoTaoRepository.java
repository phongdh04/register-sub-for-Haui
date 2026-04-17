package com.example.demo.repository;

import com.example.demo.domain.entity.ChuongTrinhDaoTao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChuongTrinhDaoTaoRepository extends JpaRepository<ChuongTrinhDaoTao, Long> {

    @Query("""
            SELECT c FROM ChuongTrinhDaoTao c
            WHERE c.nganhDaoTao.idNganh = :nganhId
            ORDER BY c.namApDung DESC
            """)
    Optional<ChuongTrinhDaoTao> findLatestByNganh(@Param("nganhId") Long nganhId);
}

