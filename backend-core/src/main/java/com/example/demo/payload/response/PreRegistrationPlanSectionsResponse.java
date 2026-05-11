package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PreRegistrationPlanSectionsResponse {

    private Long hocKyId;
    private Long idHocPhan;
    private String maHocPhan;

    /** Số section thực tế trong kế hoạch (N). */
    private int sectionCountPlanned;

    /** Tổng intent PRE gom theo học phần + phạm vi (0 nếu chỉ dùng sectionCount tay). */
    private long totalIntentSnapshot;

    /** Số lớp đề xuất từ công thức F04 (chỉ khi dùng recommended; 0 nếu không áp dụng). */
    private int recommendedFromDemand;

    private int createdCount;
    private int skippedExistingMaCount;

    /** Thứ tự theo section 1..N. */
    private List<Long> createdLopHpIds;

    /** true khi toàn bộ mã đã tồn tại (replay an toàn, không insert thêm). */
    private boolean idempotentReplay;
}
