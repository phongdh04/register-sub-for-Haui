package com.example.demo.repository;

import com.example.demo.domain.entity.LopHocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository cho bảng LopHocPhan - Bottleneck chính.
 * Có query atomic update siSoThucTe để tránh Race Condition khi sync từ Redis.
 */
@Repository
public interface LopHocPhanRepository extends JpaRepository<LopHocPhan, Long> {

    Optional<LopHocPhan> findByMaLopHp(String maLopHp);

    List<LopHocPhan> findByHocKy_IdHocKy(Long idHocKy);

    List<LopHocPhan> findByHocKy_IdHocKyAndTrangThai(Long idHocKy, String trangThai);

    boolean existsByMaLopHp(String maLopHp);

    /**
     * Atomic increment để sync kết quả từ Redis về DB.
     * Dùng khi Background Worker xử lý message từ Queue.
     * Đảm bảo không bị Lost Update khi nhiều Worker chạy song song.
     */
    @Modifying
    @Query("UPDATE LopHocPhan l SET l.siSoThucTe = l.siSoThucTe + 1 WHERE l.idLopHp = :id AND l.siSoThucTe < l.siSoToiDa")
    int incrementSiSoThucTe(@Param("id") Long id);

    /**
     * Atomic decrement khi SV rút môn.
     */
    @Modifying
    @Query("UPDATE LopHocPhan l SET l.siSoThucTe = l.siSoThucTe - 1 WHERE l.idLopHp = :id AND l.siSoThucTe > 0")
    int decrementSiSoThucTe(@Param("id") Long id);
}
