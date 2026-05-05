package com.example.demo.scheduling.solver;

import java.util.List;

/**
 * Miền rút gọn cho một LHP sau filter §7.2 (R_i, G_i, S_i thô).
 */
public record SolverLhpSubdomain(
        Long idLopHp,
        String maLopHp,
        List<Long> candidateRoomIds,
        List<Long> candidateGvIds,
        List<SolverAtomicSlot> candidateAtomicSlots,
        /** |R_i|×|G_i|×|S_i| — proxy kích thước không gian booleans CP-SAT. */
        long approxTupleProduct) {

    public SolverLhpSubdomain {
        if (approxTupleProduct < 0) {
            throw new IllegalArgumentException("approxTupleProduct");
        }
    }
}
