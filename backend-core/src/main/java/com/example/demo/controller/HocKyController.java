package com.example.demo.controller;

import com.example.demo.payload.request.HocKyRequest;
import com.example.demo.payload.response.HocKyResponse;
import com.example.demo.service.IHocKyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Facade Pattern: Controller quản lý Học Kỳ cho Admin.
 * Proxy (RBAC): Chỉ ADMIN mới được tạo/cập nhật học kỳ.
 * GET current: Public - SV cần biết học kỳ hiện tại để đăng ký.
 */
@RestController
@RequestMapping("/api/hoc-ky")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HocKyController {

    private final IHocKyService hocKyService;

    @GetMapping
    public ResponseEntity<List<HocKyResponse>> getAll() {
        return ResponseEntity.ok(hocKyService.getAll());
    }

    @GetMapping("/hien-hanh")
    public ResponseEntity<HocKyResponse> getCurrent() {
        return ResponseEntity.ok(hocKyService.getCurrent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HocKyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hocKyService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HocKyResponse> create(@Valid @RequestBody HocKyRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hocKyService.create(request));
    }

    /**
     * Kích hoạt học kỳ này làm học kỳ hiện hành.
     * Tự động tắt học kỳ cũ đang active.
     */
    @PatchMapping("/{id}/kich-hoat")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HocKyResponse> setActive(@PathVariable Long id) {
        return ResponseEntity.ok(hocKyService.setActive(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hocKyService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
