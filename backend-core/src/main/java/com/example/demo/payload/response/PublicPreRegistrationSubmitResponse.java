package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicPreRegistrationSubmitResponse {
    private UUID requestId;
    private String status;
    private Integer estimatedProcessingSeconds;
}
