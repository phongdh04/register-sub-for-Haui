package com.example.demo.scheduling.solver;

import java.util.ArrayList;
import java.util.List;

/** §7.3 — pre-check trước khi dựng đầy đủ biến CP-SAT (BACK-TKB-021). */
public final class SolverFeasibilityPrecheck {

    public record Outcome(boolean ok, List<String> notes) {
    }

    private SolverFeasibilityPrecheck() {
    }

    public static Outcome evaluate(SolverDomainBundle domain) {
        List<String> notes = new ArrayList<>();
        if (domain.roomIdsHoatDong() == null || domain.roomIdsHoatDong().isEmpty()) {
            notes.add("INFEASIBLE_EARLY: không có phòng HOAT_DONG");
            return new Outcome(false, List.copyOf(notes));
        }
        if (domain.baseAtomicSlots() == null || domain.baseAtomicSlots().isEmpty()) {
            notes.add("INFEASIBLE_EARLY: lưới tiết nguyên thủy rỗng (kiểm tra cấu hình maxTietPerDay)");
            return new Outcome(false, List.copyOf(notes));
        }
        if (domain.lhpSubdomains() == null || domain.lhpSubdomains().isEmpty()) {
            notes.add("WARN: không có LHP trong phạm vi — pipeline OK nhưng không có gì để gán.");
            return new Outcome(true, List.copyOf(notes));
        }
        for (SolverLhpSubdomain s : domain.lhpSubdomains()) {
            if (s.candidateRoomIds().isEmpty()) {
                notes.add("INFEASIBLE_EARLY: LHP id=" + s.idLopHp() + " không có phòng đủ sức chứa.");
                return new Outcome(false, List.copyOf(notes));
            }
            if (s.candidateGvIds().isEmpty()) {
                notes.add("INFEASIBLE_EARLY: LHP id=" + s.idLopHp() + " không có GV candidate.");
                return new Outcome(false, List.copyOf(notes));
            }
            if (s.candidateAtomicSlots().isEmpty()) {
                notes.add("INFEASIBLE_EARLY: LHP id=" + s.idLopHp()
                        + " không còn slot sau khi trừ gv_busy_slot HARD.");
                return new Outcome(false, List.copyOf(notes));
            }
            if (s.approxTupleProduct() <= 0) {
                notes.add("INFEASIBLE_EARLY: miền Cartesian rỗng cho LHP id=" + s.idLopHp());
                return new Outcome(false, List.copyOf(notes));
            }
        }
        if (domain.approximateTotalDecisionSpace() <= 0) {
            notes.add("INFEASIBLE_EARLY: tổng không gian quyết định tiệm cận không dương.");
            return new Outcome(false, List.copyOf(notes));
        }
        notes.add("FEASIBILITY_OK: domain có đủ phòng/GV/slot cho tất cả LHP trong scope (MVP).");
        return new Outcome(true, List.copyOf(notes));
    }
}
