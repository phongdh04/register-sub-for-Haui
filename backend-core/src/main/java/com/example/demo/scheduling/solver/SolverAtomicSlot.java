package com.example.demo.scheduling.solver;

/** Một “ô” tiết đơn trong tuần — dùng lọc gv_busy_slot HARD. */
public record SolverAtomicSlot(int thu, int tiet) {
}
