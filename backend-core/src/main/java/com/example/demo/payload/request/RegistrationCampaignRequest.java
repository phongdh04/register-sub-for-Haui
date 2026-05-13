package com.example.demo.payload.request;

import com.example.demo.domain.enums.RegistrationPhase;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

/**
 * Payload tạo chiến dịch đăng ký theo khóa.
 * Hệ thống sẽ tự tạo RegistrationWindow cho từng HocKy phù hợp với khóa.
 */
@Data
public class RegistrationCampaignRequest {

    @NotNull
    @Size(min = 3, max = 200)
    private String tenCampaign;

    /** Năm nhập học — cohort (vd 2021 = K17). Bắt buộc. */
    @NotNull
    private Integer namNhapHoc;

    @NotNull
    private RegistrationPhase phase;

    @NotNull
    private Instant openAt;

    @NotNull
    private Instant closeAt;

    @Size(max = 500)
    private String ghiChu;
}
