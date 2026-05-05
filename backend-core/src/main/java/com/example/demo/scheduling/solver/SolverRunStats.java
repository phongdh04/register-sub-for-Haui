package com.example.demo.scheduling.solver;

/** Thống kê telemetry một lần chạy. */
public record SolverRunStats(
        long approximateTotalDecisionSpace,
        long countRooms,
        long countAtomicSlotsGlobal,
        int countLhpsInScope,
        long elapsedMillis,
        Integer cpSolverTimeUsedSecondsOrNull,
        Long randomSeedApplied) {

}
