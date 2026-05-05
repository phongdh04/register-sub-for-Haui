package com.example.demo.scheduling.solver;

import com.example.demo.payload.request.SolverMvpRunRequest;

import java.util.UUID;

/** BACK-TKB-025 — enqueue chạy solver async và poll trạng thái. */
public interface ISolverAsyncJobService {

    UUID enqueue(Long hocKyId, SolverMvpRunRequest request);

    SolverRunJobSnapshot getSnapshot(Long hocKyId, UUID jobId);
}
