package com.example.demo.scheduling.solver;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * DoD Epic D BACK-TKB-020 — tổng metric không phải |R_global| × |S_global|
 * mà Σ_i |R_i|×|G_i|×|S_i| trong bundle (ít nhất với ví dụ tổng hợp).
 */
class SolverDomainApproxSpaceTest {

    @Test
    void totalDecisionSpace_matchesSumPerLhpProduct() {
        SolverLhpSubdomain a = new SolverLhpSubdomain(
                1L, "A", List.of(1L), List.of(1L, 2L), List.of(new SolverAtomicSlot(2, 1)), 2L);
        SolverLhpSubdomain b = new SolverLhpSubdomain(
                2L, "B", List.of(1L, 2L), List.of(1L), List.of(new SolverAtomicSlot(3, 4), new SolverAtomicSlot(3, 5)), 2 * 2L);
        long expected = 2L + (2L * 2L); // = 6
        SolverDomainBundle bundle = new SolverDomainBundle(
                9L,
                SolverScope.PER_HOC_KY,
                List.of(1L, 2L),
                List.of(1L),
                SolverSlotEnumeration.allAtomicSlots(1),
                List.of(a, b),
                expected);

        Assertions.assertEquals(expected, bundle.approximateTotalDecisionSpace());
        Assertions.assertNotEquals((long) 2 * 2 * 1, bundle.approximateTotalDecisionSpace());
    }
}
