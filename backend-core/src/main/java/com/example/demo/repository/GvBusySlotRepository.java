package com.example.demo.repository;

import com.example.demo.domain.entity.GvBusySlot;
import com.example.demo.domain.enums.GvBusyLoai;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GvBusySlotRepository extends JpaRepository<GvBusySlot, Long> {

    List<GvBusySlot> findByGiangVien_IdGiangVienOrderByThuAscTietBdAsc(Long idGiangVien);

    /** Áp khi khảo sát HK: busy global (hocKy null) + busy riêng HK. */
    @Query("""
            SELECT b FROM GvBusySlot b
            WHERE b.giangVien.idGiangVien = :gvId
              AND (:hkId IS NULL OR b.hocKy IS NULL OR b.hocKy.idHocKy = :hkId)
            ORDER BY b.thu, b.tietBd
            """)
    List<GvBusySlot> findForSchedulingView(@Param("gvId") Long gvId, @Param("hkId") Long hkId);

    List<GvBusySlot> findByGiangVien_IdGiangVienAndLoaiOrderByThuAscTietBdAsc(Long idGiangVien, GvBusyLoai loai);
}
