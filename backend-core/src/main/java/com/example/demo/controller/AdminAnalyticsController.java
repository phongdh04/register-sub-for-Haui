package com.example.demo.controller;

import com.example.demo.payload.response.AnalyticsDashboardResponse;
import com.example.demo.service.IAdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 15 – Báo cáo phân tích (dashboard tổng hợp cho Admin).
 */
@RestController
@RequestMapping("/api/v1/admin/analytics")
@RequiredArgsConstructor
public class AdminAnalyticsController {

    private final IAdminAnalyticsService adminAnalyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AnalyticsDashboardResponse> dashboard() {
        return ResponseEntity.ok(adminAnalyticsService.getDashboard());
    }
}
