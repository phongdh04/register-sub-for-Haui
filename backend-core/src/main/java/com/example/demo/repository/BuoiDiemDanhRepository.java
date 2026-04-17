package com.example.demo.repository;

import com.example.demo.domain.entity.BuoiDiemDanh;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface BuoiDiemDanhRepository extends JpaRepository<BuoiDiemDanh, Long> {

    Optional<BuoiDiemDanh> findByLopHocPhan_IdLopHpAndNgayBuoi(Long idLopHp, LocalDate ngayBuoi);

    Optional<BuoiDiemDanh> findByPublicToken(String publicToken);

    @EntityGraph(attributePaths = {"lopHocPhan", "lopHocPhan.hocPhan", "lopHocPhan.hocKy"})
    @Query("SELECT b FROM BuoiDiemDanh b WHERE b.publicToken = :token")
    Optional<BuoiDiemDanh> findByPublicTokenWithLop(@Param("token") String token);

    @EntityGraph(attributePaths = {"lopHocPhan", "lopHocPhan.hocPhan", "lopHocPhan.hocKy", "lopHocPhan.giangVien",
            "lopHocPhan.giangVien.taiKhoan"})
    @Query("SELECT b FROM BuoiDiemDanh b WHERE b.idBuoi = :id")
    Optional<BuoiDiemDanh> findWithLopById(@Param("id") Long id);
}
