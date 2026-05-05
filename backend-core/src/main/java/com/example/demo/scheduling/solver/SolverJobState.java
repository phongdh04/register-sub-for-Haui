package com.example.demo.scheduling.solver;

import com.example.demo.payload.response.SolverMvpRunResponse;

import java.time.Instant;
import java.util.UUID;

/** Mutable in-memory state cho mỗi solver async job. */
final class SolverJobState {

    private final UUID jobId;
    private final Long hocKyId;
    private final SolverScope scope;
    private final Instant submittedAt;
    private volatile Instant startedAt;
    private volatile Instant finishedAt;
    private volatile SolverJobStatus status;
    private volatile String detailMessage;
    private volatile SolverMvpRunResponse result;

    SolverJobState(UUID jobId, Long hocKyId, SolverScope scope, Instant submittedAt) {
        this.jobId = jobId;
        this.hocKyId = hocKyId;
        this.scope = scope;
        this.submittedAt = submittedAt;
        this.status = SolverJobStatus.QUEUED;
        this.detailMessage = "QUEUED";
    }

    UUID jobId() {
        return jobId;
    }

    Long hocKyId() {
        return hocKyId;
    }

    SolverScope scope() {
        return scope;
    }

    synchronized void start() {
        this.startedAt = Instant.now();
        this.status = SolverJobStatus.RUNNING;
        this.detailMessage = "RUNNING";
    }

    synchronized void complete(SolverMvpRunResponse response) {
        this.finishedAt = Instant.now();
        this.status = SolverJobStatus.COMPLETED;
        this.result = response;
        this.detailMessage = response == null ? "COMPLETED" : response.message();
    }

    synchronized void fail(String message) {
        this.finishedAt = Instant.now();
        this.status = SolverJobStatus.FAILED;
        this.detailMessage = message;
    }

    synchronized void timeout(String message) {
        this.finishedAt = Instant.now();
        this.status = SolverJobStatus.TIMEOUT;
        this.detailMessage = message;
    }

    SolverRunJobSnapshot toSnapshot() {
        return new SolverRunJobSnapshot(
                jobId,
                hocKyId,
                scope,
                status,
                submittedAt,
                startedAt,
                finishedAt,
                detailMessage,
                result);
    }
}
