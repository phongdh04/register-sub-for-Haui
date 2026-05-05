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
public class AdminPreRegistrationLinkCreateResponse {
    private Long id;
    private String token;
    private String intakeCode;
    private String campusCode;
    private LocalDateTime expiresAt;
    private Integer maxSubmissions;
    private String status;
}
