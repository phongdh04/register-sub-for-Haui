package com.example.demo.scheduling.solver;

import com.example.demo.payload.request.SolverMvpRunRequest;
import com.example.demo.payload.response.SolverMvpChosenSlotDto;
import com.example.demo.payload.response.SolverMvpRunResponse;
import com.example.demo.domain.entity.HocKy;
import com.example.demo.domain.enums.TkbTrangThai;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.PhongHocRepository;
import com.example.demo.scheduling.solver.config.SolverCpSatProperties;
import com.example.demo.metrics.TkbWorkflowMetrics;
import com.example.demo.service.ICohortConflictRuleService;
import com.example.demo.service.SolverAssignmentApplyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolverMvpRunServiceImpl implements ISolverMvpRunService {

    private final HocKyRepository hocKyRepository;
    private final PhongHocRepository phongHocRepository;
    private final SolverDomainBuilderService solverDomainBuilderService;
    private final SolverCpSatProperties solverCpSatProperties;
    private final SolverAssignmentApplyService solverAssignmentApplyService;
    private final ICohortConflictRuleService cohortConflictRuleService;
    private final TkbWorkflowMetrics tkbWorkflowMetrics;

    @Override
    public SolverMvpRunResponse run(Long hocKyId, SolverMvpRunRequest body) {
        String corr = java.util.UUID.randomUUID().toString();
        var sample = tkbWorkflowMetrics.start();
        long t0 = System.currentTimeMillis();
        HocKy hk = hocKyRepository.findById(hocKyId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy học kỳ: " + hocKyId));
        if (hk.getTkbTrangThai() == TkbTrangThai.CONG_BO) {
            throw new IllegalArgumentException("Học kỳ đã CONG_BO, không chạy solver trực tiếp.");
        }

        SolverMvpRunRequest b = body == null ? new SolverMvpRunRequest() : body;
        SolverScope scope = b.getScope() != null ? b.getScope() : SolverScope.PER_HOC_KY;
        long seedApplied = b.getSeed() != null ? b.getSeed() : solverCpSatProperties.getRandomSeed();

        SolverRunRequest buildReq = new SolverRunRequest(hocKyId, scope, b.getSeed());
        SolverDomainBundle full = solverDomainBuilderService.build(buildReq);

        List<SolverLhpSubdomain> subset = full.lhpSubdomains().stream()
                .sorted(Comparator.comparing(SolverLhpSubdomain::idLopHp))
                .limit(solverCpSatProperties.getMvpMaxClasses())
                .toList();
        java.util.Set<Long> subsetIds = subset.stream().map(SolverLhpSubdomain::idLopHp).collect(java.util.stream.Collectors.toSet());
        List<SolverCohortPairRule> cohortPairs = cohortConflictRuleService.loadPairRulesByHocKy(hocKyId).stream()
                .filter(p -> subsetIds.contains(p.leftLopHpId()) && subsetIds.contains(p.rightLopHpId()))
                .map(p -> new SolverCohortPairRule(p.leftLopHpId(), p.rightLopHpId(), p.nhomMa()))
                .toList();
        long approxSubset = subset.stream().mapToLong(SolverLhpSubdomain::approxTupleProduct).sum();
        SolverDomainBundle subsetBundle = new SolverDomainBundle(
                full.hocKyId(),
                full.scope(),
                full.roomIdsHoatDong(),
                full.gvIdsDistinctInHocKy(),
                full.baseAtomicSlots(),
                subset,
                approxSubset);

        SolverFeasibilityPrecheck.Outcome pre = SolverFeasibilityPrecheck.evaluate(subsetBundle);
        SolverRunStats partialStats = new SolverRunStats(
                approxSubset,
                full.roomIdsHoatDong().size(),
                full.baseAtomicSlots().size(),
                subset.size(),
                System.currentTimeMillis() - t0,
                null,
                seedApplied);

        if (!pre.ok()) {
            String msg = pre.notes().isEmpty() ? "INFEASIBLE_EARLY" : pre.notes().getFirst();
            log.warn("MVP solver INFEASIBLE_EARLY hocKy={} {}", hocKyId, pre.notes());
            tkbWorkflowMetrics.markSolverRun("mvp", "infeasible-early");
            tkbWorkflowMetrics.stop(sample, "eduport.tkb.solver.mvp.latency");
            return new SolverMvpRunResponse(
                    SolverMvpOutcome.INFEASIBLE_EARLY, msg, pre.notes(), List.of(), partialStats, false, null);
        }

        if (subset.isEmpty()) {
            tkbWorkflowMetrics.markSolverRun("mvp", "empty");
            tkbWorkflowMetrics.stop(sample, "eduport.tkb.solver.mvp.latency");
            return new SolverMvpRunResponse(
                    SolverMvpOutcome.SKIPPED_NO_LHP,
                    "Subset rỗng — không có LHP trong phạm vi.",
                    pre.notes(),
                    List.of(),
                    partialStats,
                    false,
                    null);
        }

        try {
            SolverMultiScopeMergeRunner.Outcome multi =
                    SolverMultiScopeMergeRunner.run(subset, cohortPairs, solverCpSatProperties, seedApplied);
            long elapsed = System.currentTimeMillis() - t0;
            SolverRunStats stats = new SolverRunStats(
                    approxSubset,
                    full.roomIdsHoatDong().size(),
                    full.baseAtomicSlots().size(),
                    subset.size(),
                    elapsed,
                    null,
                    seedApplied);
            List<SolverMiniChosenAssignment> chosen = multi.assignments();
            if (chosen.isEmpty()) {
                tkbWorkflowMetrics.markSolverRun("mvp", "infeasible");
                tkbWorkflowMetrics.stop(sample, "eduport.tkb.solver.mvp.latency");
                return new SolverMvpRunResponse(
                        SolverMvpOutcome.SAT_INFEASIBLE,
                        "Không tìm được assignment sau merge-pass multi-scope.",
                        pre.notes(),
                        List.of(),
                        stats,
                        false,
                        "MULTI_SCOPE_EMPTY");
            }
            List<String> notes = new ArrayList<>(pre.notes());
            if (!multi.unresolvedLopHpIds().isEmpty()) {
                notes.add("scope_conflict_ids=" + multi.unresolvedLopHpIds());
            }
            notes.add("retry_micro_scope_count=" + multi.retryMicroScopeCount());
            List<SolverMvpChosenSlotDto> dtos = toDtos(chosen);
            boolean persisted = Boolean.TRUE.equals(b.getPersist());
            if (persisted) {
                int n = solverAssignmentApplyService.applyAssignments(hocKyId, chosen);
                log.atInfo().log("MVP solver persisted corrId={} hocKy={} rows={}", corr, hocKyId, n);
            }
            tkbWorkflowMetrics.markSolverRun("mvp", "ok");
            tkbWorkflowMetrics.stop(sample, "eduport.tkb.solver.mvp.latency");

            return new SolverMvpRunResponse(
                    SolverMvpOutcome.OK_ASSIGNED,
                    persisted ? "Đã gán/merge multi-scope và ghi nhận." : "Đã gán/merge multi-scope (dry).",
                    notes,
                    dtos,
                    stats,
                    persisted,
                    "MULTI_SCOPE_MERGED");

        } catch (RuntimeException ex) {
            log.error("MVP solver INTERNAL_ERROR hocKy={} {}", hocKyId, ex.toString(), ex);
            tkbWorkflowMetrics.markSolverRun("mvp", "error");
            tkbWorkflowMetrics.stop(sample, "eduport.tkb.solver.mvp.latency");
            long elapsed = System.currentTimeMillis() - t0;
            SolverRunStats stats = new SolverRunStats(
                    approxSubset,
                    full.roomIdsHoatDong().size(),
                    full.baseAtomicSlots().size(),
                    subset.size(),
                    elapsed,
                    null,
                    seedApplied);
            String msg = ex.getMessage() != null ? ex.getMessage() : "solver error";
            return new SolverMvpRunResponse(
                    SolverMvpOutcome.INTERNAL_ERROR, msg, pre.notes(), List.of(), stats, false, null);
        }
    }

    private List<SolverMvpChosenSlotDto> toDtos(List<SolverMiniChosenAssignment> assigns) {
        List<SolverMvpChosenSlotDto> out = new ArrayList<>(assigns.size());
        for (SolverMiniChosenAssignment a : assigns) {
            String maPhong = phongHocRepository.findById(a.roomId())
                    .map(p -> p.getMaPhong())
                    .orElse("");
            out.add(new SolverMvpChosenSlotDto(
                    a.idLopHp(),
                    a.maLopHp(),
                    a.roomId(),
                    maPhong,
                    a.gvId(),
                    a.thu(),
                    a.tiet()));
        }
        return out;
    }
}
