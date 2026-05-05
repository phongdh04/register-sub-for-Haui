package com.example.demo.controller;

import com.example.demo.payload.response.PhongJsonApplyResponse;
import com.example.demo.payload.response.PhongJsonAuditResponse;
import com.example.demo.service.IPhongJsonMigrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * TKB Phase 1 — BACK-TKB-007/008: đọc khảo sát và batch map {@code phong} trong JSON → {@code id_phong_hoc}.
 */
@RestController
@RequestMapping("/api/v1/admin/scheduling/hoc-ky/{hocKyId}/phong-from-json")
@RequiredArgsConstructor
public class AdminSchedulingPhongJsonController {

    private final IPhongJsonMigrationService phongJsonMigrationService;

    @GetMapping("/audit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhongJsonAuditResponse> audit(
            @PathVariable Long hocKyId,
            @RequestParam(defaultValue = "true") boolean includeDetails) {
        return ResponseEntity.ok(phongJsonMigrationService.audit(hocKyId, includeDetails));
    }

    @PostMapping("/apply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhongJsonApplyResponse> apply(
            @PathVariable Long hocKyId,
            @RequestParam(defaultValue = "false") boolean dryRun) {
        return ResponseEntity.ok(phongJsonMigrationService.apply(hocKyId, dryRun));
    }
}
