package com.example.demo.controller;

import com.example.demo.payload.request.LopHocPhanRequest;
import com.example.demo.payload.response.LopHocPhanResponse;
import com.example.demo.payload.response.PagedResponse;
import com.example.demo.service.ILopHocPhanService;
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
 * Facade Pattern: Quản lý Lớp Học Phần.
 *
 * Proxy (RBAC):
 *  - GET by hocky: SV và TEACHER đều cần xem.
 *  - POST/PUT/DELETE: Chỉ ADMIN.
 *  - publish/close: Chỉ ADMIN (Kill Switch).
 */
@RestController
@RequestMapping("/api/lop-hoc-phan")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LopHocPhanController {

    private final ILopHocPhanService lopHocPhanService;

    /**
     * Lấy tất cả lớp của một Học Kỳ cụ thể.
     * SV dùng để xem danh sách lớp mở trong kỳ hiện tại khi đăng ký môn.
     */
    @GetMapping("/hoc-ky/{idHocKy}")
    public ResponseEntity<List<LopHocPhanResponse>> getAllByHocKy(@PathVariable Long idHocKy) {
        return ResponseEntity.ok(lopHocPhanService.getAllByHocKy(idHocKy));
    }

    @GetMapping("/hoc-ky/{idHocKy}/paged")
    public ResponseEntity<PagedResponse<LopHocPhanResponse>> getAllByHocKyPaged(
            @PathVariable Long idHocKy,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<LopHocPhanResponse> page = lopHocPhanService.getAllByHocKyPaged(idHocKy, pageable);
        return ResponseEntity.ok(PagedResponse.of(page));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LopHocPhanResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(lopHocPhanService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopHocPhanResponse> create(@Valid @RequestBody LopHocPhanRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(lopHocPhanService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopHocPhanResponse> update(@PathVariable Long id,
                                                     @Valid @RequestBody LopHocPhanRequest request) {
        return ResponseEntity.ok(lopHocPhanService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        lopHocPhanService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Phát hành lớp → chuyển trạng thái CHUA_MO → DANG_MO.
     * Đây là nút "Launch Semester" theo UI_Data_Mapping.
     * Trong tương lai sẽ trigger Redis warm-up slot.
     */
    @PatchMapping("/{id}/phat-hanh")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopHocPhanResponse> publishLop(@PathVariable Long id) {
        return ResponseEntity.ok(lopHocPhanService.publishLop(id));
    }

    /**
     * Đóng lớp → trạng thái KHOA.
     * "Kill Switch" - Admin dùng khi hệ thống bảo trì hoặc lớp hết chỗ thủ công.
     */
    @PatchMapping("/{id}/dong-lop")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopHocPhanResponse> closeLop(@PathVariable Long id) {
        return ResponseEntity.ok(lopHocPhanService.closeLop(id));
    }
}
