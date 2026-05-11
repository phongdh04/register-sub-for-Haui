package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

/**
 * Sprint 6 — Throughput response: tách REGISTER vs CANCEL, mỗi nhóm liệt kê outcome+count.
 */
@Getter
@Builder
public class RegistrationThroughputResponse {

    private final Instant fromAt;
    private final Instant toAt;
    private final List<Row> rows;

    @Getter
    @Builder
    public static class Row {
        private final String requestType;
        private final String outcome;
        private final long count;
    }
}
