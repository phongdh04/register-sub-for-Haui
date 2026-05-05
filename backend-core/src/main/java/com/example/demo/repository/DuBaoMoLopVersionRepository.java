package com.example.demo.repository;

import com.example.demo.domain.entity.DuBaoMoLopVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DuBaoMoLopVersionRepository extends JpaRepository<DuBaoMoLopVersion, Long> {

    Optional<DuBaoMoLopVersion> findFirstByHocKy_IdHocKyOrderByCreatedAtDesc(Long hocKyId);
}
