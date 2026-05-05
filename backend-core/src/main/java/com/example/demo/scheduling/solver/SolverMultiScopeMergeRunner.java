package com.example.demo.scheduling.solver;

import com.example.demo.scheduling.solver.config.SolverCpSatProperties;
import com.google.ortools.sat.CpSolverStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/** BACK-TKB-028 — chạy scope con tuần tự + merge pass + 1 lần retry micro-scope. */
public final class SolverMultiScopeMergeRunner {

    private SolverMultiScopeMergeRunner() {
    }

    public static Outcome run(
            List<SolverLhpSubdomain> subset,
            List<SolverCohortPairRule> cohortPairs,
            SolverCpSatProperties props,
            long seed) {
        int chunk = Math.max(1, props.getMvpMultiScopeChunkSize());
        List<SolverMiniChosenAssignment> merged = new ArrayList<>();
        List<Long> unresolved = new ArrayList<>();
        int retries = 0;

        for (int i = 0; i < subset.size(); i += chunk) {
            int to = Math.min(subset.size(), i + chunk);
            List<SolverLhpSubdomain> part = subset.subList(i, to);
            Set<Long> ids = part.stream().map(SolverLhpSubdomain::idLopHp).collect(Collectors.toSet());
            List<SolverCohortPairRule> pairPart = cohortPairs.stream()
                    .filter(p -> ids.contains(p.leftLopHpId()) && ids.contains(p.rightLopHpId()))
                    .toList();
            SolverMiniSolveOutcome solved = SolverCpSatMiniAssignment.solve(part, pairPart, merged, props, seed + i);
            if (solved.solverStatus() == CpSolverStatus.OPTIMAL || solved.solverStatus() == CpSolverStatus.FEASIBLE) {
                merged.addAll(solved.assignments());
                continue;
            }
            retries++;
            for (SolverLhpSubdomain one : part) {
                SolverMiniSolveOutcome retry = SolverCpSatMiniAssignment.solve(
                        List.of(one), List.of(), merged, props, seed + i + 1000L + one.idLopHp());
                if (retry.solverStatus() == CpSolverStatus.OPTIMAL || retry.solverStatus() == CpSolverStatus.FEASIBLE) {
                    merged.addAll(retry.assignments());
                } else {
                    unresolved.add(one.idLopHp());
                }
            }
        }
        return new Outcome(List.copyOf(merged), List.copyOf(unresolved), retries);
    }

    public record Outcome(
            List<SolverMiniChosenAssignment> assignments,
            List<Long> unresolvedLopHpIds,
            int retryMicroScopeCount) {
    }
}
