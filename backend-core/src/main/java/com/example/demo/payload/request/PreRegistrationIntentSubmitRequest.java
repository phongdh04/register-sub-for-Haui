package com.example.demo.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Sinh viên gửi 1 nguyện vọng đăng ký dự kiến.
 * Dùng cho cả create (POST) và update priority/ghi chú (PUT).
 */
@Data
public class PreRegistrationIntentSubmitRequest {

    @NotNull
    private Long idHocKy;

    @NotNull
    private Long idHocPhan;

    /** 1 = ưu tiên cao nhất. Để null = mặc định 1. */
    @Min(1)
    private Integer priority;

    @Size(max = 500)
    private String ghiChu;
}
