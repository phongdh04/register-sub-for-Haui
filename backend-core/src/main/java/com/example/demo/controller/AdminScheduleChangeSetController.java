package com.example.demo.controller;

import com.example.demo.domain.entity.ScheduleChangeSet;
import com.example.demo.payload.request.ScheduleChangeSetApplyRequest;
import com.example.demo.payload.request.ScheduleChangeSetReviewRequest;
import com.example.demo.payload.request.ScheduleChangeSetSubmitRequest;
import com.example.demo.payload.response.ScheduleChangeSetResponse;
import com.example.demo.service.IScheduleChangeSetService;
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

import java.util.List;

/** BACK-TKB-032 — submit/review/apply workflow cho schedule change set. */
@RestController
@RequestMapping("/api/v1/admin/scheduling/hoc-ky/{hocKyId}/change-sets")
@RequiredArgsConstructor
public class AdminScheduleChangeSetController {

    private final IScheduleChangeSetService scheduleChangeSetService;

    @PostMapping("/submit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleChangeSetResponse> submit(
            @PathVariable Long hocKyId,
            @Valid @RequestBody ScheduleChangeSetSubmitRequest body) {
        ScheduleChangeSet saved = scheduleChangeSetService.createPending(
                hocKyId, body.getPayloadDelta(), body.getRequestedBy(), body.getNote());
        return ResponseEntity.ok(toResponse(saved));
    }

    @PostMapping("/{changeSetId}/review")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleChangeSetResponse> review(
            @PathVariable Long hocKyId,
            @PathVariable Long changeSetId,
            @Valid @RequestBody ScheduleChangeSetReviewRequest body) {
        ScheduleChangeSet saved = scheduleChangeSetService.review(
                hocKyId,
                changeSetId,
                Boolean.TRUE.equals(body.getApprove()),
                body.getReviewedBy(),
                body.getLyDoThayDoi(),
                body.getReviewNote());
        return ResponseEntity.ok(toResponse(saved));
    }

    @PostMapping("/{changeSetId}/apply")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ScheduleChangeSetResponse> apply(
            @PathVariable Long hocKyId,
            @PathVariable Long changeSetId,
            @Valid @RequestBody ScheduleChangeSetApplyRequest body) {
        ScheduleChangeSet saved = scheduleChangeSetService.apply(
                hocKyId, changeSetId, body.getAppliedBy(), body.getLyDoThayDoi());
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ScheduleChangeSetResponse>> list(@PathVariable Long hocKyId) {
        List<ScheduleChangeSetResponse> out = scheduleChangeSetService.listByHocKy(hocKyId).stream()
                .map(AdminScheduleChangeSetController::toResponse)
                .toList();
        return ResponseEntity.ok(out);
    }

    private static ScheduleChangeSetResponse toResponse(ScheduleChangeSet x) {
        return new ScheduleChangeSetResponse(
                x.getId(),
                x.getHocKy() != null ? x.getHocKy().getIdHocKy() : null,
                x.getTrangThai(),
                x.getPayloadDelta(),
                x.getGhiChu(),
                x.getRequestedBy(),
                x.getReviewedBy(),
                x.getEffectiveVersionNo(),
                x.getAffectedSvCount(),
                x.getAffectedSvIds(),
                x.getApprovedAt(),
                x.getAppliedAt(),
                x.getCreatedAt(),
                x.getUpdatedAt());
    }
}
