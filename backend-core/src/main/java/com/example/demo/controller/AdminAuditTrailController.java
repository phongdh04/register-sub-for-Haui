package com.example.demo.controller;

import com.example.demo.payload.response.AuditLogRowResponse;
import com.example.demo.service.IAuditTrailService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 23 – Admin xem nhật ký hành động (điểm, phúc khảo, …).
 */
@RestController
@RequestMapping("/api/v1/admin/audit-logs")
@RequiredArgsConstructor
public class AdminAuditTrailController {

    private final IAuditTrailService auditTrailService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<AuditLogRowResponse>> page(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String maHanhDong) {
        return ResponseEntity.ok(auditTrailService.pageLogs(page, size, maHanhDong));
    }
}
