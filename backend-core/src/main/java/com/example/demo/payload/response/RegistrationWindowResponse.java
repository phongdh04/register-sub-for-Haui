package com.example.demo.payload.response;

import com.example.demo.domain.enums.RegistrationPhase;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

/**
 * Response cho cấu hình {@code registration_window}.
 *
 * <p>{@code dangMo} là tính toán theo thời gian server hiện tại (so với {@code openAt}/{@code closeAt}),
 * tiện cho UI admin hiển thị trạng thái nhanh.
 */
@Data
@Builder
public class RegistrationWindowResponse {

    private Long id;
    private Long idHocKy;
    private String tenHocKy;
    private RegistrationPhase phase;

    private Integer namNhapHoc;
    private Long idNganh;
    private String tenNganh;

    private Instant openAt;
    private Instant closeAt;
    private boolean dangMo;

    private String ghiChu;
    private String createdBy;
    private Instant createdAt;
    private Instant updatedAt;
}
