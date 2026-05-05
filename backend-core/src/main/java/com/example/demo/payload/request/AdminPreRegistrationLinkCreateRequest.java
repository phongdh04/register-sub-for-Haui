package com.example.demo.payload.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AdminPreRegistrationLinkCreateRequest {
    @NotBlank
    @Size(max = 50)
    private String intakeCode;

    @Size(max = 50)
    private String campusCode;

    @NotNull
    @Future
    private LocalDateTime expiresAt;

    @NotNull
    @Min(1)
    private Integer maxSubmissions;

    @Size(max = 50)
    private String rateLimitProfile;
}
