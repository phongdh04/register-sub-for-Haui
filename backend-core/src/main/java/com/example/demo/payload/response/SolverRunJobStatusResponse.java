package com.example.demo.payload.response;

import com.example.demo.scheduling.solver.SolverJobStatus;
import com.example.demo.scheduling.solver.SolverScope;

import java.time.Instant;
import java.util.UUID;

public record SolverRunJobStatusResponse(
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
