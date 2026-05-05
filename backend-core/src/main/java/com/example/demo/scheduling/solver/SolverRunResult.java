package com.example.demo.scheduling.solver;

import java.util.List;

public record SolverRunResult(
        SolverRunStatus status,
        String detailMessage,
        List<String> feasibilityNotes,
        SolverRunStats stats) {

}
