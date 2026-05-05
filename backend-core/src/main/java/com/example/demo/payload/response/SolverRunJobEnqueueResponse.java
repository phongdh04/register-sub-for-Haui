package com.example.demo.payload.response;

import com.example.demo.scheduling.solver.SolverJobStatus;

import java.time.Instant;
import java.util.UUID;

public record SolverRunJobEnqueueResponse(
        UUID jobId,
        SolverJobStatus status,
        Instant submittedAt,
        String pollUrl) {
}
