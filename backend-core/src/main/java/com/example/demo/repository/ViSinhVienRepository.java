package com.example.demo.repository;

import com.example.demo.domain.entity.ViSinhVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ViSinhVienRepository extends JpaRepository<ViSinhVien, Long> {

    Optional<ViSinhVien> findBySinhVien_IdSinhVien(Long idSinhVien);

    @Query("SELECT COALESCE(SUM(v.soDu), 0) FROM ViSinhVien v")
    BigDecimal sumSoDuAll();

    @Query("SELECT v.sinhVien.idSinhVien, v.soDu FROM ViSinhVien v WHERE v.sinhVien.idSinhVien IN :ids")
    List<Object[]> findSoDuBySinhVienIds(@Param("ids") Collection<Long> ids);

    @Query("SELECT v.sinhVien.idSinhVien, v.soDu FROM ViSinhVien v")
    List<Object[]> listAllSinhVienSoDu();
}
