package com.example.demo.repository;

import com.example.demo.domain.entity.DanhGiaGiangVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface DanhGiaGiangVienRepository extends JpaRepository<DanhGiaGiangVien, Long> {

    Optional<DanhGiaGiangVien> findByDangKy_IdDangKy(Long idDangKy);

    List<DanhGiaGiangVien> findByDangKy_IdDangKyIn(Collection<Long> idDangKys);
}
