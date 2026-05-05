package com.example.demo.scheduling.solver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

class SolverFeasibilityPrecheckTest {

    @Test
    void fails_whenNoRooms() {
        SolverDomainBundle d = new SolverDomainBundle(
                1L,
                SolverScope.PER_HOC_KY,
                List.of(),
                List.of(),
                SolverSlotEnumeration.allAtomicSlots(2),
                List.of(),
                0);
        SolverFeasibilityPrecheck.Outcome o = SolverFeasibilityPrecheck.evaluate(d);
        Assertions.assertFalse(o.ok());
    }

    @Test
    void ok_whenLhpHasTuples() {
        SolverLhpSubdomain s = new SolverLhpSubdomain(
                10L,
                "X",
                List.of(1L),
                List.of(2L),
                List.of(new SolverAtomicSlot(3, 1)),
                1L * 2 * 1);
        SolverDomainBundle d = new SolverDomainBundle(
                5L,
                SolverScope.PER_HOC_KY,
                List.of(1L),
                List.of(2L),
                SolverSlotEnumeration.allAtomicSlots(12),
                List.of(s),
                12L);
        SolverFeasibilityPrecheck.Outcome o = SolverFeasibilityPrecheck.evaluate(d);
        Assertions.assertTrue(o.ok());
    }
}
