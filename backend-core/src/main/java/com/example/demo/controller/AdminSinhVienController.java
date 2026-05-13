package com.example.demo.controller;

import com.example.demo.payload.request.SinhVienRequest;
import com.example.demo.payload.response.SinhVienResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.service.ISinhVienAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sinh-vien")
@RequiredArgsConstructor
public class AdminSinhVienController {

    private final ISinhVienAdminService sinhVienAdminService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SinhVienResponse>> getAll() {
        return ResponseEntity.ok(sinhVienAdminService.getAll());
    }

    @GetMapping("/paged")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PagedResponse<SinhVienResponse>> getAllPaged(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<SinhVienResponse> page = sinhVienAdminService.getAllPaged(pageable);
        return ResponseEntity.ok(PagedResponse.of(page));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SinhVienResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(sinhVienAdminService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SinhVienResponse> create(@Valid @RequestBody SinhVienRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sinhVienAdminService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SinhVienResponse> update(@PathVariable Long id, @Valid @RequestBody SinhVienRequest request) {
        return ResponseEntity.ok(sinhVienAdminService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        sinhVienAdminService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
