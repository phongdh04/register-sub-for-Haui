package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicPreRegistrationLinkResponse {
    private String linkStatus;
    private String intakeCode;
    private List<String> requiredFields;
    private boolean captchaRequired;
    private LocalDateTime expiresAt;
}
