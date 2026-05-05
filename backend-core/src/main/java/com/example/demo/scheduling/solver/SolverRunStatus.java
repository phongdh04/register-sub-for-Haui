package com.example.demo.scheduling.solver;

public enum SolverRunStatus {
    /** §7.3 — dừng trước CP-SAT khi domain rỗng / không có tuple. */
    INFEASIBLE_EARLY,
    /** Pre-check đạt; pipeline OR-Tools chạy được (có thể kèm solve trival). */
    FEASIBILITY_PIPELINE_OK,
    /** Lỗi runtime (JNI, bug, v.v.). */
    INTERNAL_ERROR
}
