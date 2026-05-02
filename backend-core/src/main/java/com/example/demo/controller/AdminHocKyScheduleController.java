package com.example.demo.controller;

import com.example.demo.payload.request.HocKyLichDangKyRequest;
import com.example.demo.payload.response.HocKyResponse;
import com.example.demo.service.IHocKyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Admin — lịch đăng ký theo học kỳ (tách path {@code /api/v1/admin/...} để tránh xung đột routing với resource tĩnh).
 */
@RestController
@RequestMapping("/api/v1/admin/hoc-ky")
@RequiredArgsConstructor
public class AdminHocKyScheduleController {

    private final IHocKyService hocKyService;

    @PutMapping("/{id}/lich-dang-ky")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HocKyResponse> updateLichDangKy(
            @PathVariable Long id,
            @Valid @RequestBody HocKyLichDangKyRequest body) {
        return ResponseEntity.ok(hocKyService.updateLichDangKy(id, body));
    }
}
