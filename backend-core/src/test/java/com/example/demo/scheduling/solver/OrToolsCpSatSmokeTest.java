package com.example.demo.scheduling.solver;

import com.google.ortools.Loader;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import com.google.ortools.sat.LinearArgument;
import com.google.ortools.sat.LinearExpr;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * BACK-TKB-018 — chứng minh classpath + native libs OR-Tools; model ràng buộc tầm thấp có lời giải trong CI/Dev.
 */
class OrToolsCpSatSmokeTest {

    @BeforeAll
    static void loadNative() {
        Loader.loadNativeLibraries();
    }

    /**
     * Một BoolVar có lời giải khả thi ({@code x+y==1,x,y∈{0,1}}) — không kích hoạt semantics TKB.
     */
    @Test
    void feasibilityXorChoice_solves() {
        CpModel model = new CpModel();
        var x = model.newBoolVar("x");
        var y = model.newBoolVar("y");
        model.addEquality(LinearExpr.sum(new LinearArgument[] { x, y }), 1);

        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);
        Assertions.assertTrue(
                status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE,
                () -> "Expected OPTIMAL or FEASIBLE, got=" + status);
        Assertions.assertEquals(1L, solver.value(x) + solver.value(y));
    }

    /** Objectives tối đa một biến trong miền hữu hạn ⇒ OPTIMAL. */
    @Test
    void maximizeTinyDomain_optimal() {
        CpModel model = new CpModel();
        var z = model.newIntVar(0L, 5L, "z");
        model.maximize(z);
        CpSolver solver = new CpSolver();
        CpSolverStatus status = solver.solve(model);
        Assertions.assertSame(CpSolverStatus.OPTIMAL, status);
        Assertions.assertEquals(5L, solver.value(z));
    }
}
