package com.example.demo.scheduling.solver;

/**
 * Một lựa chọn gán từ CP-SAT mini (ô tiết đơn + phòng + GV) — BACK-TKB-022/024.
 */
public record SolverMiniChosenAssignment(
        Long idLopHp, String maLopHp, long roomId, long gvId, int thu, int tiet) {
}
