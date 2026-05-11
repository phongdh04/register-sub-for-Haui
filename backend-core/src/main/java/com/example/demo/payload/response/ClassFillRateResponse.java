package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Sprint 6 — Fill-rate dashboard. Mỗi row 1 lớp đã PUBLISHED.
 */
@Getter
@Builder
public class ClassFillRateResponse {

    private final Long idHocKy;
    private final int totalClasses;
    private final long totalSlots;
    private final long takenSlots;
    private final double overallFillRate;
    private final List<Row> rows;

    @Getter
    @Builder
    public static class Row {
        private final Long idLopHp;
        private final String maLopHp;
        private final String maHocPhan;
        private final String tenHocPhan;
        private final int siSoToiDa;
        private final int siSoThucTe;
        private final double fillRate;
        /** Đếm số lần FULL trong window monitoring (kèm trong query monitoring). */
        private final long fullEvents;
    }
}
