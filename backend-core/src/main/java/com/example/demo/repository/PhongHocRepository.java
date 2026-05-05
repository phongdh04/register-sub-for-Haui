package com.example.demo.repository;

import com.example.demo.domain.entity.PhongHoc;
import com.example.demo.domain.enums.TrangThaiPhong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PhongHocRepository extends JpaRepository<PhongHoc, Long> {

    List<PhongHoc> findByTrangThai(TrangThaiPhong trangThai);

    Optional<PhongHoc> findByMaPhong(String maPhong);

    Optional<PhongHoc> findByMaPhongIgnoreCase(String maPhong);

    boolean existsByMaPhong(String maPhong);

    Page<PhongHoc> findByMaCoSoIgnoreCase(String maCoSo, Pageable pageable);
}
