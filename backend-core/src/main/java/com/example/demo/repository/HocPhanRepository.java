package com.example.demo.repository;

import com.example.demo.domain.entity.HocPhan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * ISP: JpaSpecificationExecutor cho phép Builder Pattern (CourseQueryBuilder)
 * ghép nối Specification động mà không phải sửa interface này.
 * Đây là nền tảng cho Task 6 - Lọc Môn Đỉnh Cao (Builder Pattern).
 */
@Repository
public interface HocPhanRepository extends JpaRepository<HocPhan, Long>,
        JpaSpecificationExecutor<HocPhan> {
    Optional<HocPhan> findByMaHocPhan(String maHocPhan);
    boolean existsByMaHocPhan(String maHocPhan);
}
