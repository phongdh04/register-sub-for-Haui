package com.example.demo.repository;

import com.example.demo.domain.entity.PhieuDuThi;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuDuThiRepository extends JpaRepository<PhieuDuThi, Long> {

    Optional<PhieuDuThi> findByDangKy_IdDangKy(Long idDangKy);

    List<PhieuDuThi> findByDangKy_IdDangKyIn(Collection<Long> idDangKys);
}
