package com.example.demo.payload.request;

import com.example.demo.domain.enums.RegistrationPhase;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.Instant;

/**
 * Payload tạo/sửa cấu hình window theo cohort/ngành.
 *
 * <p>Quy ước null:
 * <ul>
 *   <li>{@code namNhapHoc == null} → áp dụng cho mọi cohort.</li>
 *   <li>{@code idNganh == null} → áp dụng cho mọi ngành trong cohort đó.</li>
 * </ul>
 */
@Data
public class RegistrationWindowUpsertRequest {

    @NotNull
    private Long idHocKy;

    @NotNull
    private RegistrationPhase phase;

    private Integer namNhapHoc;
    private Long idNganh;

    @NotNull
    private Instant openAt;

    @NotNull
    private Instant closeAt;

    @Size(max = 500)
    private String ghiChu;
}
