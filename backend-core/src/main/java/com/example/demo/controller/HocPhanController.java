package com.example.demo.controller;

import com.example.demo.payload.request.HocPhanRequest;
import com.example.demo.payload.response.HocPhanResponse;
import com.example.demo.service.IHocPhanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Facade Pattern: Quản lý Học Phần (Course Master Data).
 * Proxy (RBAC):
 *  - GET: Tất cả user (SV cần xem danh sách môn).
 *  - POST/PUT/DELETE: Chỉ ADMIN.
 */
@RestController
@RequestMapping("/api/hoc-phan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HocPhanController {

    private final IHocPhanService hocPhanService;

    @GetMapping
    public ResponseEntity<List<HocPhanResponse>> getAll() {
        return ResponseEntity.ok(hocPhanService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HocPhanResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(hocPhanService.getById(id));
    }

    @GetMapping("/ma/{maHocPhan}")
    public ResponseEntity<HocPhanResponse> getByMa(@PathVariable String maHocPhan) {
        return ResponseEntity.ok(hocPhanService.getByMa(maHocPhan));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HocPhanResponse> create(@Valid @RequestBody HocPhanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(hocPhanService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HocPhanResponse> update(@PathVariable Long id,
                                                   @Valid @RequestBody HocPhanRequest request) {
        return ResponseEntity.ok(hocPhanService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        hocPhanService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
