package com.example.demo.payload.response;

import com.example.demo.scheduling.solver.SolverMvpOutcome;
import com.example.demo.scheduling.solver.SolverRunStats;

import java.util.List;

public record SolverMvpRunResponse(
        SolverMvpOutcome outcome,
        String message,
        List<String> feasibilityNotes,
        List<SolverMvpChosenSlotDto> assignments,
        SolverRunStats stats,
        boolean persisted,
        String cpSolverStatus) {
}
