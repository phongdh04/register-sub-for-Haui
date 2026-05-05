package com.example.demo.scheduling.solver;

import com.example.demo.scheduling.solver.config.SolverCpSatProperties;
import com.google.ortools.Loader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.List;

class SolverMultiScopeMergeRunnerTest {

    @BeforeAll
    static void loadNative() {
        Loader.loadNativeLibraries();
    }

    @Test
    void twoScopeConflicting_resolvesBySequentialMerge() {
        SolverCpSatProperties p = new SolverCpSatProperties();
        p.setMvpMaxTuplesPerClass(20);
        p.setMvpMultiScopeChunkSize(1);

        SolverLhpSubdomain a = new SolverLhpSubdomain(
                1L, "A", List.of(10L), List.of(100L), List.of(new SolverAtomicSlot(2, 1)), 1);
        SolverLhpSubdomain b = new SolverLhpSubdomain(
                2L, "B", List.of(10L, 11L), List.of(200L), List.of(new SolverAtomicSlot(2, 1)), 2);

        SolverMultiScopeMergeRunner.Outcome out =
                SolverMultiScopeMergeRunner.run(List.of(a, b), List.of(), p, 1L);

        Assertions.assertEquals(2, out.assignments().size());
        long distinctRooms = out.assignments().stream().map(SolverMiniChosenAssignment::roomId).distinct().count();
        Assertions.assertEquals(2L, distinctRooms);
        Assertions.assertTrue(out.unresolvedLopHpIds().isEmpty());
    }
}
