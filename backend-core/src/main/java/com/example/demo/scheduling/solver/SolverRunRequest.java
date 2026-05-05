package com.example.demo.scheduling.solver;

import java.util.Objects;

/**
 * Đầu vào một lần chạy solver (sync / dry-run — BACK-TKB-019).
 *
 * @param hocKyId    học kỳ xếp lịch
 * @param scope      phạm vi dữ liệu
 * @param seed       optional; ghi đè {@code eduport.solver.cpsat.random-seed}
 */
public record SolverRunRequest(Long hocKyId, SolverScope scope, Long seed) {

    public SolverRunRequest {
        Objects.requireNonNull(hocKyId, "hocKyId");
        Objects.requireNonNull(scope, "scope");
    }
}
