package com.example.demo.controller;

import com.example.demo.payload.request.KhoaRequest;
import com.example.demo.payload.response.KhoaResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.service.IKhoaService;
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

/**
 * Facade Pattern: Controller cung cấp API đơn giản cho Frontend,
 * che giấu toàn bộ phức tạp bên trong Service và Repository.
 *
 * Proxy Pattern (Spring Security):
 * - GET: PUBLIC (SV cũng cần biết danh sách Khoa khi đăng ký).
 * - POST/PUT/DELETE: Chỉ ADMIN.
 *
 * SRP: Controller chỉ hứng request, delegate sang service, trả response.
 *      Không chứa logic nghiệp vụ.
 */
@RestController
@RequestMapping("/api/khoa")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class KhoaController {

    private final IKhoaService khoaService;

    @GetMapping
    public ResponseEntity<List<KhoaResponse>> getAll() {
        return ResponseEntity.ok(khoaService.getAll());
    }

    @GetMapping("/paged")
    public ResponseEntity<PagedResponse<KhoaResponse>> getAllPaged(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<KhoaResponse> page = khoaService.getAllPaged(pageable);
        return ResponseEntity.ok(PagedResponse.of(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<KhoaResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(khoaService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KhoaResponse> create(@Valid @RequestBody KhoaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(khoaService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<KhoaResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody KhoaRequest request) {
        return ResponseEntity.ok(khoaService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        khoaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
