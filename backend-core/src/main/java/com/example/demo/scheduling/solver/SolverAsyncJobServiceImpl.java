package com.example.demo.scheduling.solver;

import com.example.demo.payload.request.SolverMvpRunRequest;
import com.example.demo.payload.response.SolverMvpRunResponse;
import com.example.demo.scheduling.solver.config.SolverCpSatProperties;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolverAsyncJobServiceImpl implements ISolverAsyncJobService {

    private final ISolverMvpRunService solverMvpRunService;
    private final SolverCpSatProperties solverCpSatProperties;

    private final ConcurrentHashMap<UUID, SolverJobState> jobs = new ConcurrentHashMap<>();

    @Override
    public UUID enqueue(Long hocKyId, SolverMvpRunRequest request) {
        SolverMvpRunRequest body = request == null ? new SolverMvpRunRequest() : request;
        if (body.getScope() == null) {
            body.setScope(SolverScope.PER_HOC_KY);
        }
        UUID jobId = UUID.randomUUID();
        SolverJobState state = new SolverJobState(jobId, hocKyId, body.getScope(), Instant.now());
        jobs.put(jobId, state);

        CompletableFuture
                .supplyAsync(() -> runJob(state, body))
                .orTimeout(Math.max(1, solverCpSatProperties.getMaxTimeSeconds()), TimeUnit.SECONDS)
                .whenComplete((resp, ex) -> {
                    if (ex == null) {
                        state.complete(resp);
                        return;
                    }
                    Throwable root = ex.getCause() != null ? ex.getCause() : ex;
                    if (root instanceof TimeoutException) {
                        state.timeout("TIMEOUT: vượt " + solverCpSatProperties.getMaxTimeSeconds() + " giây.");
                    } else {
                        state.fail(root.getMessage() != null ? root.getMessage() : "solver async failed");
                    }
                });
        return jobId;
    }

    @Override
    public SolverRunJobSnapshot getSnapshot(Long hocKyId, UUID jobId) {
        SolverJobState state = jobs.get(jobId);
        if (state == null || !state.hocKyId().equals(hocKyId)) {
            throw new EntityNotFoundException("Không tìm thấy jobId=" + jobId + " cho học kỳ " + hocKyId);
        }
        return state.toSnapshot();
    }

    private SolverMvpRunResponse runJob(SolverJobState state, SolverMvpRunRequest body) {
        state.start();
        log.atInfo().log("Solver async job start id={} hocKy={} scope={}", state.jobId(), state.hocKyId(), state.scope());
        return solverMvpRunService.run(state.hocKyId(), body);
    }
}
