package com.example.demo.repository;

import com.example.demo.domain.entity.HocKy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HocKyRepository extends JpaRepository<HocKy, Long> {
    Optional<HocKy> findByTrangThaiHienHanhTrue();
    boolean existsByNamHocAndKyThu(String namHoc, Integer kyThu);
}
