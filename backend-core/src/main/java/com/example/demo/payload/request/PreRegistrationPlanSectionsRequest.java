package com.example.demo.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Admin — sinh các {@code lop_hoc_phan} shell từ nhu cầu PRE hoặc số lớp chỉ định (F17 BL-01).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PreRegistrationPlanSectionsRequest {

    @NotNull
    private Long hocKyId;

    @NotNull
    private Long idHocPhan;

    /** Lọc cohort — khớp aggregate demand (nullable = mọi cohort trong aggregate). */
    private Integer namNhapHoc;

    /** Lọc ngành — khớp aggregate (nullable = mọi ngành). */
    private Long idNganh;

    /**
     * Số section tạo thủ công. Khi có giá trị, bỏ qua {@link #useRecommendedFromDemand}.
     */
    @Min(1)
    @Max(200)
    private Integer sectionCount;

    /**
     * Khi {@code sectionCount} null: nếu true (mặc định) thì dùng công thức giống F04 trên tổng intent học phần trong phạm vi lọc.
     */
    @Builder.Default
    private Boolean useRecommendedFromDemand = Boolean.TRUE;

    /** Sĩ số mục tiêu cho công thức ceil — null/≤0 dùng default server. */
    @Min(1)
    @Max(500)
    private Integer targetClassSize;

    /** Sĩ số tối đa ghi vào shell — null thì dùng cùng giá trị class size đã chọn. */
    @Min(1)
    @Max(500)
    private Integer siSoToiDa;
}
