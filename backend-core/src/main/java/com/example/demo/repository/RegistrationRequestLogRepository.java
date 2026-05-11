package com.example.demo.repository;

import com.example.demo.domain.entity.RegistrationRequestLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Repository idempotency log — Sprint 4 + monitoring queries Sprint 6.
 */
@Repository
public interface RegistrationRequestLogRepository extends JpaRepository<RegistrationRequestLog, Long> {

    Optional<RegistrationRequestLog> findByIdempotencyKey(String idempotencyKey);

    boolean existsByIdempotencyKey(String idempotencyKey);

    /**
     * Sprint 6 — gộp số lần xuất hiện theo outcome trong khoảng thời gian.
     * Trả về {@code Object[] {outcome (String), count (Long)}}.
     */
    @Query("""
            SELECT r.outcome, COUNT(r)
            FROM RegistrationRequestLog r
            WHERE r.createdAt BETWEEN :from AND :to
            GROUP BY r.outcome
            """)
    List<Object[]> aggregateByOutcome(@Param("from") Instant from,
                                      @Param("to") Instant to);

    /**
     * Sprint 6 — top lớp bị FULL nhiều nhất trong khoảng thời gian.
     * Trả về {@code Object[] {idLopHp (Long), count (Long)}}.
     */
    @Query("""
            SELECT r.idLopHp, COUNT(r)
            FROM RegistrationRequestLog r
            WHERE r.outcome = com.example.demo.domain.enums.RegistrationOutcome.FULL
              AND r.createdAt BETWEEN :from AND :to
              AND r.idLopHp IS NOT NULL
            GROUP BY r.idLopHp
            ORDER BY COUNT(r) DESC
            """)
    List<Object[]> topFullClasses(@Param("from") Instant from,
                                  @Param("to") Instant to);

    /**
     * Sprint 6 — throughput tổng theo outcome trong window (đếm REGISTER + CANCEL riêng).
     * {@code Object[] {requestType (String), outcome (String), count (Long)}}.
     */
    @Query("""
            SELECT r.requestType, r.outcome, COUNT(r)
            FROM RegistrationRequestLog r
            WHERE r.createdAt BETWEEN :from AND :to
            GROUP BY r.requestType, r.outcome
            """)
    List<Object[]> aggregateByTypeAndOutcome(@Param("from") Instant from,
                                             @Param("to") Instant to);
}
