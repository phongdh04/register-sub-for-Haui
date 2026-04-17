package com.example.demo.repository;

import com.example.demo.domain.entity.NhatKyHanhDong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NhatKyHanhDongRepository extends JpaRepository<NhatKyHanhDong, Long> {

    @Query("""
            SELECT n FROM NhatKyHanhDong n
            WHERE (:ma IS NULL OR :ma = '' OR n.maHanhDong = :ma)
            ORDER BY n.thoiGian DESC
            """)
    Page<NhatKyHanhDong> pageByOptionalAction(@Param("ma") String maHanhDong, Pageable pageable);
}
