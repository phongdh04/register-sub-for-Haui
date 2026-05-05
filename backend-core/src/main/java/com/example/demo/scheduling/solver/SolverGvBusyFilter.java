package com.example.demo.scheduling.solver;

import com.example.demo.domain.entity.GvBusySlot;
import com.example.demo.domain.enums.GvBusyLoai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Loại bỏ slot atomic trùng tiết với gv_busy_slot HARD §7.2. */
public final class SolverGvBusyFilter {

    private SolverGvBusyFilter() {
    }

    public static List<SolverAtomicSlot> filterHardBusy(List<SolverAtomicSlot> base, List<GvBusySlot> busySlots) {
        if (busySlots == null || busySlots.isEmpty()) {
            return List.copyOf(base);
        }
        List<SolverAtomicSlot> out = new ArrayList<>(base.size());
        nextSlot:
        for (SolverAtomicSlot s : base) {
            for (GvBusySlot b : busySlots) {
                if (b == null || b.getLoai() != GvBusyLoai.HARD) {
                    continue;
                }
                Short th = b.getThu();
                Short bd = b.getTietBd();
                Short kt = b.getTietKt();
                if (th == null || bd == null || kt == null) {
                    continue;
                }
                if (th.intValue() != s.thu()) {
                    continue;
                }
                if (s.tiet() >= bd && s.tiet() <= kt) {
                    continue nextSlot;
                }
            }
            out.add(s);
        }
        return Collections.unmodifiableList(out);
    }
}
