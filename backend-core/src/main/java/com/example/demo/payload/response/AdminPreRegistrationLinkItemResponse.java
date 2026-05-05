package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPreRegistrationLinkItemResponse {
    private Long id;
    private String intakeCode;
    private String campusCode;
    private Integer maxSubmissions;
    private Integer submittedCount;
    private String status;
    private LocalDateTime expiresAt;
    private LocalDateTime createdAt;
}
