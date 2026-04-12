package com.example.demo.controller;

import com.example.demo.payload.request.NganhDaoTaoRequest;
import com.example.demo.payload.response.NganhDaoTaoResponse;
import com.example.demo.service.INganhDaoTaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Facade Pattern: Quản lý Ngành Đào Tạo.
 * Proxy (RBAC): GET public, CUD chỉ ADMIN.
 */
@RestController
@RequestMapping("/api/nganh-dao-tao")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class NganhDaoTaoController {

    private final INganhDaoTaoService nganhService;

    @GetMapping
    public ResponseEntity<List<NganhDaoTaoResponse>> getAll() {
        return ResponseEntity.ok(nganhService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<NganhDaoTaoResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(nganhService.getById(id));
    }

    @GetMapping("/khoa/{idKhoa}")
    public ResponseEntity<List<NganhDaoTaoResponse>> getByKhoa(@PathVariable Long idKhoa) {
        return ResponseEntity.ok(nganhService.getByKhoa(idKhoa));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NganhDaoTaoResponse> create(@Valid @RequestBody NganhDaoTaoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(nganhService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NganhDaoTaoResponse> update(@PathVariable Long id,
                                                       @Valid @RequestBody NganhDaoTaoRequest request) {
        return ResponseEntity.ok(nganhService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        nganhService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
