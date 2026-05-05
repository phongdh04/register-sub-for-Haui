package com.example.demo.controller;

import com.example.demo.payload.request.ForecastMoLopRunRequest;
import com.example.demo.payload.response.ForecastMoLopRunResponse;
import com.example.demo.payload.response.ForecastMoLopVersionStatusResponse;
import com.example.demo.payload.response.ForecastShellSpawnResponse;
import com.example.demo.service.IForecastMoLopService;
import com.example.demo.service.IForecastMoLopWorkflowService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BACK-TKB-015–017 — dự báo, duyệt phiên, sinh LHP shell (§6).
 */
@RestController
@RequestMapping("/api/v1/admin/scheduling/hoc-ky/{hocKyId}")
@RequiredArgsConstructor
public class AdminSchedulingForecastController {

    private final IForecastMoLopService forecastMoLopService;
    private final IForecastMoLopWorkflowService forecastMoLopWorkflowService;

    @PostMapping("/forecast")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ForecastMoLopRunResponse> runForecast(
            @PathVariable Long hocKyId,
            @Valid @RequestBody ForecastMoLopRunRequest body) {
        return ResponseEntity.ok(forecastMoLopService.runForecast(hocKyId, body));
    }

    @PostMapping("/forecast-versions/{versionId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ForecastMoLopVersionStatusResponse> approveForecast(
            @PathVariable Long hocKyId,
            @PathVariable Long versionId) {
        return ResponseEntity.ok(forecastMoLopWorkflowService.approve(hocKyId, versionId));
    }

    @PostMapping("/forecast-versions/{versionId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ForecastMoLopVersionStatusResponse> rejectForecast(
            @PathVariable Long hocKyId,
            @PathVariable Long versionId) {
        return ResponseEntity.ok(forecastMoLopWorkflowService.reject(hocKyId, versionId));
    }

    @PostMapping("/forecast-versions/{versionId}/spawn-shell")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ForecastShellSpawnResponse> spawnShell(
            @PathVariable Long hocKyId,
            @PathVariable Long versionId) {
        return ResponseEntity.ok(forecastMoLopWorkflowService.spawnShellLhps(hocKyId, versionId));
    }
}
