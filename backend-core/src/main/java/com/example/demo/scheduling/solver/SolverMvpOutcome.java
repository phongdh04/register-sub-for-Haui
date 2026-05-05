package com.example.demo.scheduling.solver;

/** Kết quả tổng thể endpoint MVP (pre-check + CP-SAT + optional persist). */
public enum SolverMvpOutcome {
    OK_ASSIGNED,
    INFEASIBLE_EARLY,
    SKIPPED_NO_LHP,
    SAT_INFEASIBLE,
    INTERNAL_ERROR
}
