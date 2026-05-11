package com.example.demo.service;

import com.example.demo.payload.response.ClassFillRateResponse;
import com.example.demo.payload.response.RegistrationOutcomeStatsResponse;
import com.example.demo.payload.response.RegistrationThroughputResponse;

import java.time.Instant;

/**
 * Sprint 6 — Tổng hợp số liệu monitoring cho dashboard admin.
 *
 * <p>Mọi truy vấn đều READ-only và backed bởi {@code registration_request_log} +
 * {@code lop_hoc_phan}, vì vậy có thể chạy song song với luồng đăng ký live mà
 * không cần lock.
 */
public interface IRegistrationMonitoringService {

    /** Tỷ lệ outcome trong window (mặc định null → 24h gần nhất). */
    RegistrationOutcomeStatsResponse outcomeStats(Instant from, Instant to);

    /** Throughput tách theo (request_type, outcome) trong window. */
    RegistrationThroughputResponse throughput(Instant from, Instant to);

    /** Fill-rate của mọi lớp PUBLISHED trong học kỳ + đính kèm số lần FULL trong window. */
    ClassFillRateResponse fillRate(Long hocKyId, Instant from, Instant to);
}
