package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class LopHocPhanBulkPublishResponse {

    private Long idHocKy;
    private int totalRequested;
    private int publishedCount;
    private int skippedCount;
    private List<Long> publishedIds;
    private List<SkippedLopHocPhan> skipped;

    @Data
    @Builder
    public static class SkippedLopHocPhan {
        private Long idLopHp;
        private String maLopHp;
        private String reason;
    }
}
