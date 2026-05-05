package com.example.demo.controller;

import com.example.demo.payload.request.SolverDryRunRequest;
import com.example.demo.payload.request.SolverMvpRunRequest;
import com.example.demo.payload.request.SolverRunJobRequest;
import com.example.demo.payload.response.SolverMvpRunResponse;
import com.example.demo.payload.response.SolverRunJobEnqueueResponse;
import com.example.demo.payload.response.SolverRunJobStatusResponse;
import com.example.demo.scheduling.solver.ISolverAsyncJobService;
import com.example.demo.scheduling.solver.ISchedulingSolverService;
import com.example.demo.scheduling.solver.ISolverMvpRunService;
import com.example.demo.scheduling.solver.SolverJobStatus;
import com.example.demo.scheduling.solver.SolverRunRequest;
import com.example.demo.scheduling.solver.SolverRunResult;
import com.example.demo.scheduling.solver.SolverRunJobSnapshot;
import com.example.demo.scheduling.solver.SolverScope;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * BACK-TKB-019+ — orchestrator solver đồng bộ “dry-run” (chưa job queue — chờ BACK-TKB-025).
 */
@RestController
@RequestMapping("/api/v1/admin/scheduling/hoc-ky/{hocKyId}/solver")
@RequiredArgsConstructor
public class AdminSchedulingSolverController {

    private final ISchedulingSolverService schedulingSolverService;
    private final ISolverMvpRunService solverMvpRunService;
    private final ISolverAsyncJobService solverAsyncJobService;

    @PostMapping("/dry-run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolverRunResult> dryRun(
            @PathVariable Long hocKyId,
            @RequestBody(required = false) SolverDryRunRequest body) {
        SolverDryRunRequest b = body == null ? new SolverDryRunRequest() : body;
        if (b.getScope() == null) {
            b.setScope(SolverScope.PER_HOC_KY);
        }
        SolverRunRequest req = new SolverRunRequest(hocKyId, b.getScope(), b.getSeed());
        return ResponseEntity.ok(schedulingSolverService.dryRunSynchronously(req));
    }

    /** BACK-TKB-022–024 — CP-SAT mini (subset) + optional persist. */
    @PostMapping("/mvp-run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolverMvpRunResponse> mvpRun(
            @PathVariable Long hocKyId,
            @RequestBody(required = false) SolverMvpRunRequest body) {
        SolverMvpRunRequest b = body == null ? new SolverMvpRunRequest() : body;
        if (b.getScope() == null) {
            b.setScope(SolverScope.PER_HOC_KY);
        }
        return ResponseEntity.ok(solverMvpRunService.run(hocKyId, b));
    }

    /** BACK-TKB-025 — async run: enqueue job rồi poll trạng thái theo jobId. */
    @PostMapping("/run")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolverRunJobEnqueueResponse> runAsync(
            @PathVariable Long hocKyId,
            @RequestBody(required = false) SolverRunJobRequest body) {
        SolverRunJobRequest b = body == null ? new SolverRunJobRequest() : body;
        SolverMvpRunRequest req = new SolverMvpRunRequest();
        req.setScope(b.getScope() == null ? SolverScope.PER_HOC_KY : b.getScope());
        req.setSeed(b.getSeed());
        req.setPersist(b.getPersist() == null ? Boolean.TRUE : b.getPersist());
        UUID jobId = solverAsyncJobService.enqueue(hocKyId, req);
        SolverRunJobSnapshot snap = solverAsyncJobService.getSnapshot(hocKyId, jobId);
        String pollUrl = "/api/v1/admin/scheduling/hoc-ky/" + hocKyId + "/solver/jobs/" + jobId;
        return ResponseEntity.ok(new SolverRunJobEnqueueResponse(
                jobId,
                SolverJobStatus.QUEUED,
                snap.submittedAt(),
                pollUrl));
    }

    @GetMapping("/jobs/{jobId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SolverRunJobStatusResponse> getJobStatus(
            @PathVariable Long hocKyId,
            @PathVariable UUID jobId) {
        SolverRunJobSnapshot s = solverAsyncJobService.getSnapshot(hocKyId, jobId);
        SolverRunJobStatusResponse resp = new SolverRunJobStatusResponse(
                s.jobId(),
                s.hocKyId(),
                s.scope(),
                s.status(),
                s.submittedAt(),
                s.startedAt(),
                s.finishedAt(),
                s.detailMessage(),
                s.result());
        return ResponseEntity.ok(resp);
    }
}
