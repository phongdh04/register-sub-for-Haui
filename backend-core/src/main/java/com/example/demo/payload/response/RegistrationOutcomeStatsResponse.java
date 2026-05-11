package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

/**
 * Sprint 6 — Tổng hợp số request đăng ký theo outcome trong cửa sổ thời gian.
 */
@Getter
@Builder
public class RegistrationOutcomeStatsResponse {

    private final Instant fromAt;
    private final Instant toAt;

    /** Tổng request trong window. */
    private final long total;

    /** outcome (SUCCESS / FULL / DUPLICATE / ...) → count. */
    private final Map<String, Long> byOutcome;

    /** Tỷ lệ thành công = SUCCESS / (SUCCESS + FULL + VALIDATION_FAILED + REJECTED + DUPLICATE). */
    private final double successRate;
}
