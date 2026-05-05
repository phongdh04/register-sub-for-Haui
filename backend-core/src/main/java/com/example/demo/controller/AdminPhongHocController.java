package com.example.demo.controller;

import com.example.demo.payload.request.PhongHocUpsertRequest;
import com.example.demo.payload.response.PhongHocResponse;
import com.example.demo.service.IPhongHocService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin — master phòng học (TKB Phase 1 / BACK-TKB-002).
 */
@RestController
@RequestMapping("/api/v1/admin/phong")
@RequiredArgsConstructor
public class AdminPhongHocController {

    private final IPhongHocService phongHocService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<PhongHocResponse>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String maCoSo) {
        var pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "maPhong"));
        return ResponseEntity.ok(phongHocService.page(pageable, maCoSo));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhongHocResponse> get(@PathVariable Long id) {
        return ResponseEntity.ok(phongHocService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhongHocResponse> create(@Valid @RequestBody PhongHocUpsertRequest body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(phongHocService.create(body));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PhongHocResponse> update(
            @PathVariable Long id, @Valid @RequestBody PhongHocUpsertRequest body) {
        return ResponseEntity.ok(phongHocService.update(id, body));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        phongHocService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
