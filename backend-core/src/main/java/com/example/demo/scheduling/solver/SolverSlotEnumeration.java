package com.example.demo.scheduling.solver;

import com.example.demo.domain.support.TkbThuSurrogate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Lưới tiết nguyên thủy cố định (MVP Epic D — chưa tách buổi sáng/chiều). */
public final class SolverSlotEnumeration {

    private SolverSlotEnumeration() {
    }

    public static List<SolverAtomicSlot> allAtomicSlots(int maxTietPerDayInclusive) {
        if (maxTietPerDayInclusive < 1) {
            return List.of();
        }
        List<SolverAtomicSlot> out = new ArrayList<>(
                (TkbThuSurrogate.THU_MAX - TkbThuSurrogate.THU_MIN + 1) * maxTietPerDayInclusive);
        for (int thu = TkbThuSurrogate.THU_MIN; thu <= TkbThuSurrogate.THU_MAX; thu++) {
            for (int t = 1; t <= maxTietPerDayInclusive; t++) {
                out.add(new SolverAtomicSlot(thu, t));
            }
        }
        return Collections.unmodifiableList(out);
    }
}
