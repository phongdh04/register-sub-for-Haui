package com.example.demo.scheduling.solver;

/** BACK-TKB-025 — trạng thái job async cho endpoint /solver/run. */
public enum SolverJobStatus {
    QUEUED,
    RUNNING,
    COMPLETED,
    FAILED,
    TIMEOUT
}
