package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/** BACK-TKB-017 — kết quả sinh LHP nháy từ phiên APPROVED. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForecastShellSpawnResponse {

    private Long hocKyId;
    private Long forecastVersionId;
    /** Số {@link com.example.demo.domain.entity.LopHocPhan} mới được insert trong lần gọi. */
    private int createdCount;
    /** Đã tồn tại deterministích (chạy lại idempotent). */
    private int skippedExistingMaCount;

    /** Tối đa một số ID đầu để FE debug (không tràn payload). */
    private List<Long> createdLopHpIdsPreview;
}
