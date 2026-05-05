package com.example.demo.scheduling.solver;

import com.example.demo.payload.request.SolverMvpRunRequest;
import com.example.demo.payload.response.SolverMvpRunResponse;

/** BACK-TKB-022–024 — chạy CP-SAT mini trên subset LHP và (tuỳ chọn) persist. */
public interface ISolverMvpRunService {

    SolverMvpRunResponse run(Long hocKyId, SolverMvpRunRequest body);
}
