package com.example.demo.scheduling.solver;

import com.example.demo.payload.response.SolverMvpRunResponse;

import java.time.Instant;
import java.util.UUID;

/** Snapshot immutable trả ra API poll trạng thái job solver. */
public record SolverRunJobSnapshot(
        UUID jobId,
        Long hocKyId,
        SolverScope scope,
        SolverJobStatus status,
        Instant submittedAt,
        Instant startedAt,
        Instant finishedAt,
        String detailMessage,
        SolverMvpRunResponse result) {
}
