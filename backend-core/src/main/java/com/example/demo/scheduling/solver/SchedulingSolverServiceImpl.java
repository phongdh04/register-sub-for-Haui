package com.example.demo.scheduling.solver;

import com.example.demo.repository.HocKyRepository;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.enums.TkbTrangThai;
import com.example.demo.metrics.TkbWorkflowMetrics;
import com.example.demo.scheduling.solver.config.SolverCpSatProperties;
import com.google.ortools.sat.CpModel;
import com.google.ortools.sat.CpSolver;
import com.google.ortools.sat.CpSolverStatus;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SchedulingSolverServiceImpl implements ISchedulingSolverService {

    private final HocKyRepository hocKyRepository;
    private final SolverDomainBuilderService solverDomainBuilderService;
    private final SolverCpSatProperties solverCpSatProperties;
    private final TkbWorkflowMetrics tkbWorkflowMetrics;

    private CpSolverStatus lastHeartbeatStatus = CpSolverStatus.UNKNOWN;

    @Override
    public SolverRunResult dryRunSynchronously(SolverRunRequest request) {
        String corr = java.util.UUID.randomUUID().toString();
        var sample = tkbWorkflowMetrics.start();
        long t0 = System.currentTimeMillis();
        HocKy hk = hocKyRepository.findById(request.hocKyId())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + request.hocKyId()));
        if (hk.getTkbTrangThai() == TkbTrangThai.CONG_BO) {
            throw new IllegalArgumentException("Học kỳ đã CONG_BO, không chạy solver trực tiếp.");
        }

        SolverDomainBundle domain = solverDomainBuilderService.build(request);
        SolverFeasibilityPrecheck.Outcome pre = SolverFeasibilityPrecheck.evaluate(domain);

        long seedApplied = request.seed() != null ? request.seed() : solverCpSatProperties.getRandomSeed();
        SolverRunStats partialStats = new SolverRunStats(
                domain.approximateTotalDecisionSpace(),
                domain.roomIdsHoatDong().size(),
                domain.baseAtomicSlots().size(),
                domain.lhpSubdomains().size(),
                System.currentTimeMillis() - t0,
                null,
                seedApplied);

        if (!pre.ok()) {
            String msg = pre.notes().isEmpty() ? "INFEASIBLE_EARLY" : pre.notes().getFirst();
            log.warn("Solver dry-run INFEASIBLE_EARLY hocKy={} {}", request.hocKyId(), pre.notes());
            tkbWorkflowMetrics.markSolverRun("dry-run", "infeasible-early");
            tkbWorkflowMetrics.stop(sample, "eduport.tkb.solver.dry_run.latency");
            return new SolverRunResult(SolverRunStatus.INFEASIBLE_EARLY, msg, pre.notes(), partialStats);
        }

        try {
            Integer wallSec = trivialCpSatPassWithTimeBudget(seedApplied);
            long elapsed = System.currentTimeMillis() - t0;
            SolverRunStats stats = new SolverRunStats(
                    domain.approximateTotalDecisionSpace(),
                    domain.roomIdsHoatDong().size(),
                    domain.baseAtomicSlots().size(),
                    domain.lhpSubdomains().size(),
                    elapsed,
                    wallSec,
                    seedApplied);
            log.atInfo().log(
                    "Solver dry-run PIPELINE_OK corrId={} hocKy={} approxSpace={} lhps={} cpWallSec={}",
                    corr,
                    request.hocKyId(),
                    domain.approximateTotalDecisionSpace(),
                    domain.lhpSubdomains().size(),
                    wallSec);
            tkbWorkflowMetrics.markSolverRun("dry-run", "ok");
            tkbWorkflowMetrics.stop(sample, "eduport.tkb.solver.dry_run.latency");
            return new SolverRunResult(
                    SolverRunStatus.FEASIBILITY_PIPELINE_OK,
                    "Pre-check §7.3 OK; CP-SAT heartbeat solve completed (" + lastHeartbeatStatus.name() + ").",
                    pre.notes(),
                    stats);
        } catch (RuntimeException ex) {
            log.error("Solver dry-run INTERNAL_ERROR hocKy={} {}", request.hocKyId(), ex.toString());
            tkbWorkflowMetrics.markSolverRun("dry-run", "error");
            tkbWorkflowMetrics.stop(sample, "eduport.tkb.solver.dry_run.latency");
            long elapsed = System.currentTimeMillis() - t0;
            SolverRunStats stats = new SolverRunStats(
                    domain.approximateTotalDecisionSpace(),
                    domain.roomIdsHoatDong().size(),
                    domain.baseAtomicSlots().size(),
                    domain.lhpSubdomains().size(),
                    elapsed,
                    null,
                    seedApplied);
            return new SolverRunResult(
                    SolverRunStatus.INTERNAL_ERROR,
                    ex.getMessage() != null ? ex.getMessage() : "solver error",
                    pre.notes(),
                    stats);
        }
    }

    /**
     * Chạy model tối thiểu để chứng minh timeout/seed/parameters — chưa ánh xạ domain TKB thật (BACK-TKB-022+).
     */
    private int trivialCpSatPassWithTimeBudget(long seed) {
        CpModel model = new CpModel();
        model.newBoolVar("solver_heartbeat");
        CpSolver solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds((double) solverCpSatProperties.getMaxTimeSeconds());
        solver.getParameters().setNumSearchWorkers(solverCpSatProperties.getNumSearchWorkers());
        solver.getParameters().setRandomSeed((int) (seed ^ (seed >>> 32)));
        lastHeartbeatStatus = solver.solve(model);
        if (lastHeartbeatStatus != CpSolverStatus.OPTIMAL
                && lastHeartbeatStatus != CpSolverStatus.FEASIBLE) {
            throw new IllegalStateException("CP-SAT heartbeat unexpected status=" + lastHeartbeatStatus);
        }
        return (int) Math.ceil(solver.wallTime());
    }
}
