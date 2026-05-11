package com.example.demo.controller;

import com.example.demo.payload.response.ClassFillRateResponse;
import com.example.demo.payload.response.RegistrationOutcomeStatsResponse;
import com.example.demo.payload.response.RegistrationThroughputResponse;
import com.example.demo.service.IRegistrationMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

/**
 * Sprint 6 — Admin dashboard endpoints cho monitoring đăng ký live.
 *
 * <p>Tham số {@code from}/{@code to} optional — không truyền sẽ lấy 24h gần nhất.
 * Mọi response READ-only, backed bởi {@code registration_request_log}.
 */
@RestController
@RequestMapping("/api/v1/admin/registration-monitoring")
@RequiredArgsConstructor
public class AdminRegistrationMonitoringController {

    private final IRegistrationMonitoringService monitoringService;

    @GetMapping("/outcomes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegistrationOutcomeStatsResponse> outcomes(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return ResponseEntity.ok(monitoringService.outcomeStats(from, to));
    }

    @GetMapping("/throughput")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RegistrationThroughputResponse> throughput(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return ResponseEntity.ok(monitoringService.throughput(from, to));
    }

    @GetMapping("/fill-rate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ClassFillRateResponse> fillRate(
            @RequestParam Long hocKyId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return ResponseEntity.ok(monitoringService.fillRate(hocKyId, from, to));
    }
}
