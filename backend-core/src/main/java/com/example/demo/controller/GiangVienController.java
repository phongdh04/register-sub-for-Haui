package com.example.demo.controller;

import com.example.demo.payload.request.GiangVienRequest;
import com.example.demo.payload.response.GiangVienResponse;
import com.example.demo.service.IGiangVienService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Facade Pattern: Quản lý Giảng Viên.
 * Proxy (RBAC): Chỉ ADMIN quản lý. TEACHER/STUDENT chỉ GET.
 */
@RestController
@RequestMapping("/api/giang-vien")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class GiangVienController {

    private final IGiangVienService giangVienService;

    @GetMapping
    public ResponseEntity<List<GiangVienResponse>> getAll() {
        return ResponseEntity.ok(giangVienService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GiangVienResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(giangVienService.getById(id));
    }

    @GetMapping("/khoa/{idKhoa}")
    public ResponseEntity<List<GiangVienResponse>> getByKhoa(@PathVariable Long idKhoa) {
        return ResponseEntity.ok(giangVienService.getByKhoa(idKhoa));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiangVienResponse> create(@Valid @RequestBody GiangVienRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(giangVienService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<GiangVienResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody GiangVienRequest request) {
        return ResponseEntity.ok(giangVienService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        giangVienService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
