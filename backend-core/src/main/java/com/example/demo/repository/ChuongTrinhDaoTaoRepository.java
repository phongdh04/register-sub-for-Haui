package com.example.demo.repository;

import com.example.demo.domain.entity.ChuongTrinhDaoTao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChuongTrinhDaoTaoRepository extends JpaRepository<ChuongTrinhDaoTao, Long> {

    @Query(value = """
            SELECT * FROM chuong_trinh_dao_tao
            WHERE id_nganh = :nganhId
            ORDER BY nam_ap_dung DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<ChuongTrinhDaoTao> findLatestByNganh(@Param("nganhId") Long nganhId);
}

