package com.example.demo.payload.response;

import com.example.demo.domain.enums.ScheduleChangeSetStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record ScheduleChangeSetResponse(
        Long id,
        Long hocKyId,
        ScheduleChangeSetStatus trangThai,
        Map<String, Object> payloadDelta,
        String ghiChu,
        String requestedBy,
        String reviewedBy,
        Long effectiveVersionNo,
        Integer affectedSvCount,
        List<Long> affectedSvIds,
        Instant approvedAt,
        Instant appliedAt,
        Instant createdAt,
        Instant updatedAt) {
}
