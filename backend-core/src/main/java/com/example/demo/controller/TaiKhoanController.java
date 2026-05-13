package com.example.demo.controller;

import com.example.demo.domain.enums.Role;
import com.example.demo.domain.enums.Status;
import com.example.demo.payload.request.TaiKhoanRequest;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.payload.response.TaiKhoanResponse;
import com.example.demo.service.ITaiKhoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/tai-khoan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class TaiKhoanController {

    private final ITaiKhoanService taiKhoanService;

    @GetMapping("/sinh-vien")
    public ResponseEntity<PagedResponse<TaiKhoanResponse>> getSinhVienAccounts(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        PagedResponse<TaiKhoanResponse> result;
        if (q != null && !q.isBlank()) {
            result = taiKhoanService.searchByRole(Role.STUDENT, q, pageable);
        } else {
            result = taiKhoanService.getByRole(Role.STUDENT, pageable);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/giang-vien")
    public ResponseEntity<PagedResponse<TaiKhoanResponse>> getGiangVienAccounts(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 20) Pageable pageable) {
        PagedResponse<TaiKhoanResponse> result;
        if (q != null && !q.isBlank()) {
            result = taiKhoanService.searchByRole(Role.LECTURER, q, pageable);
        } else {
            result = taiKhoanService.getByRole(Role.LECTURER, pageable);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaiKhoanResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(taiKhoanService.getById(id));
    }

    @PostMapping
    public ResponseEntity<TaiKhoanResponse> create(@Valid @RequestBody TaiKhoanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taiKhoanService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaiKhoanResponse> update(@PathVariable Long id,
                                                    @Valid @RequestBody TaiKhoanRequest request) {
        return ResponseEntity.ok(taiKhoanService.update(id, request));
    }

    @PatchMapping("/{id}/trang-thai")
    public ResponseEntity<Void> updateTrangThai(@PathVariable Long id,
                                                @RequestBody TrangThaiRequest req) {
        taiKhoanService.updateTrangThai(id, Status.valueOf(req.trangThai()));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taiKhoanService.delete(id);
        return ResponseEntity.noContent().build();
    }

    public record TrangThaiRequest(String trangThai) {}
}
