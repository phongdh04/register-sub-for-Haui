package com.example.demo.repository;

import com.example.demo.domain.entity.ViSinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ViSinhVienRepository extends JpaRepository<ViSinhVien, Long> {

    Optional<ViSinhVien> findBySinhVien_IdSinhVien(Long idSinhVien);
}
