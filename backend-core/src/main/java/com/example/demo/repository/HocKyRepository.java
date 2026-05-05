package com.example.demo.repository;

import com.example.demo.domain.entity.HocKy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HocKyRepository extends JpaRepository<HocKy, Long> {
    Optional<HocKy> findByTrangThaiHienHanhTrue();

    Optional<HocKy> findTopByOrderByIdHocKyDesc();

    boolean existsByNamHocAndKyThu(String namHoc, Integer kyThu);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE HocKy hk SET hk.tkbRevision = COALESCE(hk.tkbRevision, 0L) + 1 WHERE hk.idHocKy = :id")
    int bumpTkbRevision(@Param("id") Long hocKyId);
}
