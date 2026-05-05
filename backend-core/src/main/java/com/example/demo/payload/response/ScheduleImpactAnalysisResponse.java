package com.example.demo.payload.response;

import java.util.List;

/** BACK-TKB-030 — impact analysis trên delta slot của change set. */
public record ScheduleImpactAnalysisResponse(
        int affectedSvCount,
        List<Long> affectedSvIds,
        List<Long> targetLopHpIds) {
}
