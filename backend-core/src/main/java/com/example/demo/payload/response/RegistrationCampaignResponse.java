package com.example.demo.payload.response;

import com.example.demo.domain.enums.RegistrationPhase;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

/**
 * Response trả về chi tiết chiến dịch đăng ký.
 */
@Data
@Builder
public class RegistrationCampaignResponse {

    private Long id;
    private String tenCampaign;
    private Integer namNhapHoc;
    private RegistrationPhase phase;
    private Instant openAt;
    private Instant closeAt;
    private String ghiChu;
    private boolean dangMo;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;

    /** Danh sách các RegistrationWindow được tạo tự động bởi campaign này. */
    private List<WindowSummary> windows;

    @Data
    @Builder
    public static class WindowSummary {
        private Long id;
        private Long idHocKy;
        private String tenHocKy;
        private Instant openAt;
        private Instant closeAt;
        private boolean dangMo;
    }
}
