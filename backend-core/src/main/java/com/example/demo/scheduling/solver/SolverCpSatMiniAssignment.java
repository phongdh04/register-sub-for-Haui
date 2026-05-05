package com.example.demo.scheduling.solver;

import com.example.demo.scheduling.solver.config.SolverCpSatProperties;
import com.google.ortools.sat.BoolVar;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BACK-TKB-022 — CP-SAT rút gọn: mỗi LHP chọn đúng một bộ (phòng, GV, ô tiết nguyên thủy);
 * HARD: không trùng (phòng,thứ,tiết), không trùng (GV,thứ,tiết); SOFT: tối đa hệ số tổ hợp dương.
 */
public final class SolverCpSatMiniAssignment {

    private static final Comparator<SolverAtomicSlot> SLOT_ORDER =
            Comparator.comparingInt(SolverAtomicSlot::thu).thenComparingInt(SolverAtomicSlot::tiet);

    private SolverCpSatMiniAssignment() {
    }

    public static SolverMiniSolveOutcome solve(
            List<SolverLhpSubdomain> lhps,
            List<SolverCohortPairRule> cohortPairs,
            List<SolverMiniChosenAssignment> fixedAssignments,
            SolverCpSatProperties props,
            long seed) {
        List<SolverLhpSubdomain> sorted = lhps.stream()
                .sorted(Comparator.comparing(SolverLhpSubdomain::idLopHp))
                .toList();
        if (sorted.isEmpty()) {
            return new SolverMiniSolveOutcome(CpSolverStatus.OPTIMAL, List.of(), 0, 0);
        }

        int cap = Math.max(1, props.getMvpMaxTuplesPerClass());
        CpModel model = new CpModel();
        List<TupleLeaf> allLeaves = new ArrayList<>();

        Map<RoomSlot, List<BoolVar>> roomUse = new HashMap<>();
        Map<GvSlot, List<BoolVar>> gvUse = new HashMap<>();
        java.util.Set<RoomSlot> occupiedRoom = new java.util.HashSet<>();
        java.util.Set<GvSlot> occupiedGv = new java.util.HashSet<>();
        if (fixedAssignments != null) {
            for (SolverMiniChosenAssignment a : fixedAssignments) {
                occupiedRoom.add(new RoomSlot(a.roomId(), a.thu(), a.tiet()));
                occupiedGv.add(new GvSlot(a.gvId(), a.thu(), a.tiet()));
            }
        }

        Map<Long, Map<SlotKey, List<BoolVar>>> classSlotVars = new HashMap<>();
        for (SolverLhpSubdomain sd : sorted) {
            List<TupleSeed> tuples = enumerateTuples(sd, cap);
            if (tuples.isEmpty()) {
                return new SolverMiniSolveOutcome(CpSolverStatus.INFEASIBLE, List.of(), 0, 0);
            }
            List<LinearArgument> classVars = new ArrayList<>(tuples.size());
            int i = 0;
            for (TupleSeed t : tuples) {
                if (occupiedRoom.contains(new RoomSlot(t.roomId(), t.thu(), t.tiet()))
                        || occupiedGv.contains(new GvSlot(t.gvId(), t.thu(), t.tiet()))) {
                    continue;
                }
                BoolVar v = model.newBoolVar(
                        "lhp_" + sd.idLopHp() + "_" + i++);
                long coef = softWeight(t.roomId(), t.thu(), t.tiet());
                TupleLeaf leaf = new TupleLeaf(v, sd.idLopHp(), sd.maLopHp(), t.roomId(), t.gvId(), t.thu(), t.tiet(),
                        coef);
                allLeaves.add(leaf);
                classVars.add(v);
                classSlotVars
                        .computeIfAbsent(sd.idLopHp(), k -> new HashMap<>())
                        .computeIfAbsent(new SlotKey(t.thu(), t.tiet()), k -> new ArrayList<>())
                        .add(v);
                roomUse.computeIfAbsent(new RoomSlot(t.roomId(), t.thu(), t.tiet()), k -> new ArrayList<>()).add(v);
                gvUse.computeIfAbsent(new GvSlot(t.gvId(), t.thu(), t.tiet()), k -> new ArrayList<>()).add(v);
            }
            if (classVars.isEmpty()) {
                return new SolverMiniSolveOutcome(CpSolverStatus.INFEASIBLE, List.of(), 0, allLeaves.size());
            }
            model.addEquality(LinearExpr.sum(classVars.toArray(LinearArgument[]::new)), 1L);
        }

        for (List<BoolVar> g : roomUse.values()) {
            if (g.size() > 1) {
                model.addLessOrEqual(LinearExpr.sum(g.toArray(LinearArgument[]::new)), 1L);
            }
        }
        for (List<BoolVar> g : gvUse.values()) {
            if (g.size() > 1) {
                model.addLessOrEqual(LinearExpr.sum(g.toArray(LinearArgument[]::new)), 1L);
            }
        }
        // BACK-TKB-027: cohort pair phải chọn cùng slot (thu,tiet).
        for (SolverCohortPairRule pair : cohortPairs) {
            Map<SlotKey, List<BoolVar>> left = classSlotVars.get(pair.leftLopHpId());
            Map<SlotKey, List<BoolVar>> right = classSlotVars.get(pair.rightLopHpId());
            if (left == null || right == null) {
                continue;
            }
            java.util.Set<SlotKey> all = new java.util.LinkedHashSet<>();
            all.addAll(left.keySet());
            all.addAll(right.keySet());
            for (SlotKey slot : all) {
                model.addEquality(sumVars(left.get(slot)), sumVars(right.get(slot)));
            }
        }

        LinearArgument[] objVars = new LinearArgument[allLeaves.size()];
        long[] objCoef = new long[allLeaves.size()];
        for (int k = 0; k < allLeaves.size(); k++) {
            TupleLeaf L = allLeaves.get(k);
            objVars[k] = L.var();
            objCoef[k] = L.coef();
        }
        model.maximize(LinearExpr.weightedSum(objVars, objCoef));

        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds((double) props.getMaxTimeSeconds());
        solver.getParameters().setNumSearchWorkers(props.getNumSearchWorkers());
        solver.getParameters().setRandomSeed((int) (seed ^ (seed >>> 32)));

        CpSolverStatus st = solver.solve(model);
        int wall = (int) Math.ceil(solver.wallTime());

        if (st != CpSolverStatus.OPTIMAL && st != CpSolverStatus.FEASIBLE) {
            return new SolverMiniSolveOutcome(st, List.of(), wall, allLeaves.size());
        }

        List<SolverMiniChosenAssignment> chosen = new ArrayList<>();
        for (TupleLeaf L : allLeaves) {
            if (Boolean.TRUE.equals(solver.booleanValue(L.var()))) {
                chosen.add(new SolverMiniChosenAssignment(
                        L.idLopHp(), L.maLopHp(), L.roomId(), L.gvId(), L.thu(), L.tiet()));
            }
        }
        return new SolverMiniSolveOutcome(st, List.copyOf(chosen), wall, allLeaves.size());
    }

    private static List<TupleSeed> enumerateTuples(SolverLhpSubdomain sd, int cap) {
        List<Long> rooms = sd.candidateRoomIds().stream().sorted().toList();
        List<Long> gvs = sd.candidateGvIds().stream().sorted().toList();
        List<SolverAtomicSlot> slots = sd.candidateAtomicSlots().stream().sorted(SLOT_ORDER).toList();
        List<TupleSeed> out = new ArrayList<>();
        outer:
        for (long r : rooms) {
            for (long g : gvs) {
                for (SolverAtomicSlot s : slots) {
                    if (out.size() >= cap) {
                        break outer;
                    }
                    out.add(new TupleSeed(r, g, s.thu(), s.tiet()));
                }
            }
        }
        return out;
    }

    /** Ưu tiên thứ sớm, tiết sớm, phòng id nhỏ — hệ số lớn hơn khi “tốt hơn” theo thứ tự đó. */
    private static long softWeight(long roomId, int thu, int tiet) {
        return 10_000_000L - (long) thu * 500_000L - (long) tiet * 10_000L - (roomId % 50_000L);
    }

    private static LinearExpr sumVars(List<BoolVar> vars) {
        if (vars == null || vars.isEmpty()) {
            return LinearExpr.constant(0);
        }
        return LinearExpr.sum(vars.toArray(LinearArgument[]::new));
    }

    private record RoomSlot(long roomId, int thu, int tiet) {
    }

    private record GvSlot(long gvId, int thu, int tiet) {
    }

    private record SlotKey(int thu, int tiet) {
    }

    private record TupleSeed(long roomId, long gvId, int thu, int tiet) {
    }

    private record TupleLeaf(
            BoolVar var,
            Long idLopHp,
            String maLopHp,
            long roomId,
            long gvId,
            int thu,
            int tiet,
            long coef) {
    }
}
