package com.example.demo.scheduling.solver;

import java.util.List;

/** Toàn miền build được cho một học kỳ (BACK-TKB-020). */
public record SolverDomainBundle(
        Long hocKyId,
        SolverScope scope,
        List<Long> roomIdsHoatDong,
        List<Long> gvIdsDistinctInHocKy,
        List<SolverAtomicSlot> baseAtomicSlots,
        List<SolverLhpSubdomain> lhpSubdomains,
        /** Σ_i |R_i||G_i||S_i| — log metric DoD Epic D độ “nổ” domain. */
        long approximateTotalDecisionSpace) {

}
