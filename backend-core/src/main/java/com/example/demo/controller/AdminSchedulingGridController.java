package com.example.demo.controller;

import com.example.demo.payload.request.SchedulingConflictCheckRequest;
import com.example.demo.payload.response.SchedulingConflictCheckResponse;
import com.example.demo.payload.response.SchedulingSnapshotResponse;
import com.example.demo.service.ISchedulingGridAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BK-TKB-011 / 012 — snapshot grid + conflict-check (phòng / GV) theo học kỳ.
 */
@RestController
@RequestMapping("/api/v1/admin/scheduling/hoc-ky/{hocKyId}")
@RequiredArgsConstructor
public class AdminSchedulingGridController {

    private final ISchedulingGridAdminService schedulingGridAdminService;

    @GetMapping("/snapshot")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchedulingSnapshotResponse> snapshot(@PathVariable Long hocKyId) {
        SchedulingSnapshotResponse body = schedulingGridAdminService.getSnapshot(hocKyId);
        return ResponseEntity.ok()
                .eTag("W/\"" + body.getRevisionVersion() + "\"")
                .body(body);
    }

    @PostMapping("/conflict-check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SchedulingConflictCheckResponse> conflictCheck(
            @PathVariable Long hocKyId,
            @Valid @RequestBody SchedulingConflictCheckRequest request) {
        return ResponseEntity.ok(schedulingGridAdminService.conflictCheck(hocKyId, request));
    }
}
