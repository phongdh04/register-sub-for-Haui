package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminPreRegistrationLinkStatsResponse {
    private Long linkId;
    private Long totalRequests;
    private Long pendingRequests;
    private Long processingRequests;
    private Long successRequests;
    private Long failedRequests;
}
