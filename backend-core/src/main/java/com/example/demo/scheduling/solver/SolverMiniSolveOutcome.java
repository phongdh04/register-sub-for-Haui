package com.example.demo.scheduling.solver;

import com.google.ortools.sat.CpSolverStatus;

import java.util.List;

/** Kết quả solve mini — phục vụ /solver/mvp-run (BACK-TKB-022). */
public record SolverMiniSolveOutcome(
        CpSolverStatus solverStatus,
        List<SolverMiniChosenAssignment> assignments,
        int wallSecs,
        int boolVarCount) {
}
