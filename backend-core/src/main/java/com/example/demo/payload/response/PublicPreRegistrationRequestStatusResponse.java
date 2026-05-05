package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicPreRegistrationRequestStatusResponse {
    private UUID requestId;
    private String status;
    private String errorCode;
    private String studentCode;
    private Boolean accountProvisioned;
    private String nextStep;
    private LocalDateTime processedAt;
}
