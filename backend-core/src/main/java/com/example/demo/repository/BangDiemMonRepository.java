package com.example.demo.repository;

import com.example.demo.domain.entity.BangDiemMon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BangDiemMonRepository extends JpaRepository<BangDiemMon, Long> {

    Optional<BangDiemMon> findByDangKyHocPhan_IdDangKy(Long idDangKy);
}
