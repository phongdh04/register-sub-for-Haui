package com.example.demo.scheduling.solver;

import com.example.demo.scheduling.solver.config.SolverCpSatProperties;
import com.google.ortools.Loader;
import com.google.ortools.sat.CpSolverStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

/** BACK-TKB-022 — ràng buộc phòng + GV không trùng ô tiết. */
class SolverCpSatMiniAssignmentTest {

    @BeforeAll
    static void loadNative() {
        Loader.loadNativeLibraries();
    }

    @Test
    void twoIndependentClasses_feasible() {
        SolverCpSatProperties p = tunedProps();
        List<SolverLhpSubdomain> lhps = List.of(
                new SolverLhpSubdomain(
                        1L,
                        "HP1",
                        List.of(10L),
                        List.of(100L),
                        List.of(new SolverAtomicSlot(2, 1), new SolverAtomicSlot(2, 2)),
                        4L),
                new SolverLhpSubdomain(
                        2L,
                        "HP2",
                        List.of(20L),
                        List.of(200L),
                        List.of(new SolverAtomicSlot(2, 1), new SolverAtomicSlot(2, 2)),
                        4L));
        SolverMiniSolveOutcome out = SolverCpSatMiniAssignment.solve(lhps, List.of(), List.of(), p, 42L);
        Assertions.assertSame(CpSolverStatus.OPTIMAL, out.solverStatus());
        Assertions.assertEquals(2, out.assignments().size());
    }

    @Test
    void sameRoomSameSlot_hardConflict_infeasible() {
        SolverCpSatProperties p = tunedProps();
        List<SolverLhpSubdomain> lhps = List.of(
                new SolverLhpSubdomain(
                        1L,
                        "X",
                        List.of(10L),
                        List.of(100L),
                        List.of(new SolverAtomicSlot(2, 1)),
                        1L),
                new SolverLhpSubdomain(
                        2L,
                        "Y",
                        List.of(10L),
                        List.of(200L),
                        List.of(new SolverAtomicSlot(2, 1)),
                        1L));
        SolverMiniSolveOutcome out = SolverCpSatMiniAssignment.solve(lhps, List.of(), List.of(), p, 1L);
        Assertions.assertSame(CpSolverStatus.INFEASIBLE, out.solverStatus());
        Assertions.assertTrue(out.assignments().isEmpty());
    }

    @Test
    void cohortPair_requiresSameSlot_and_can_be_infeasible() {
        SolverCpSatProperties p = tunedProps();
        List<SolverLhpSubdomain> lhps = List.of(
                new SolverLhpSubdomain(
                        11L,
                        "A",
                        List.of(10L),
                        List.of(101L),
                        List.of(new SolverAtomicSlot(2, 1)),
                        1L),
                new SolverLhpSubdomain(
                        12L,
                        "B",
                        List.of(20L),
                        List.of(202L),
                        List.of(new SolverAtomicSlot(2, 2)),
                        1L));
        List<SolverCohortPairRule> pairs = List.of(new SolverCohortPairRule(11L, 12L, "N1"));
        SolverMiniSolveOutcome out = SolverCpSatMiniAssignment.solve(lhps, pairs, List.of(), p, 7L);
        Assertions.assertSame(CpSolverStatus.INFEASIBLE, out.solverStatus());
    }

    private static SolverCpSatProperties tunedProps() {
        SolverCpSatProperties p = new SolverCpSatProperties();
        p.setMaxTimeSeconds(30);
        p.setNumSearchWorkers(4);
        p.setMvpMaxTuplesPerClass(50);
        return p;
    }
}
