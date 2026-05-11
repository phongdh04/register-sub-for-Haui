package com.example.demo.payload.request;

import com.example.demo.domain.enums.RegistrationPhase;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Payload "mo dang ky ngay" (quick-action cho admin):
 * tao window mo bat dau tu thoi diem hien tai, keo dai {@code durationDays}.
 */
@Data
public class RegistrationWindowOpenNowRequest {

    @NotNull
    private Long idHocKy;

    @NotNull
    private RegistrationPhase phase;

    /** null = ap dung cho moi cohort. */
    private Integer namNhapHoc;

    /** null = ap dung cho moi nganh trong cohort. */
    private Long idNganh;

    /** Mac dinh 30 ngay neu khong gui. */
    @Min(1)
    private Integer durationDays;

    @Size(max = 500)
    private String ghiChu;
}
