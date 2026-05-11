package com.example.demo.controller;

import com.example.demo.payload.request.LopHocPhanAssignGiangVienRequest;
import com.example.demo.payload.response.LopHocPhanBulkPublishResponse;
import com.example.demo.payload.response.LopHocPhanPublishResponse;
import com.example.demo.payload.response.LopHocPhanResponse;
import com.example.demo.service.IClassPublishService;
import com.example.demo.service.ILopHocPhanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Admin — workflow công bố lớp học phần (Sprint 3).
 *
 * <p>Endpoint:
 * <ul>
 *   <li>{@code POST .../{id}/assign-giang-vien}: gán GV.</li>
 *   <li>{@code POST .../{id}/publish}: publish 1 lớp (yêu cầu đã SCHEDULED).</li>
 *   <li>{@code POST .../bulk-publish}: publish hàng loạt theo học kỳ.</li>
 *   <li>{@code GET .../hoc-ky/{hocKyId}}: danh sách tất cả lớp (gồm {@code SHELL}) — dùng màn xuất bản.</li>
 * </ul>
 */
@RestController
@RequestMapping("/api/v1/admin/lop-hoc-phan")
@RequiredArgsConstructor
public class AdminClassPublishController {

    private final IClassPublishService classPublishService;
    private final ILopHocPhanService lopHocPhanService;

    @GetMapping("/hoc-ky/{hocKyId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<LopHocPhanResponse>> listByHocKy(@PathVariable Long hocKyId) {
        return ResponseEntity.ok(lopHocPhanService.getAllByHocKy(hocKyId));
    }

    @PostMapping("/{idLopHp}/assign-giang-vien")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopHocPhanPublishResponse> assignGiangVien(
            @PathVariable Long idLopHp,
            @Valid @RequestBody LopHocPhanAssignGiangVienRequest body) {
        return ResponseEntity.ok(classPublishService.assignGiangVien(idLopHp, body));
    }

    @PostMapping("/{idLopHp}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopHocPhanPublishResponse> publish(@PathVariable Long idLopHp) {
        return ResponseEntity.ok(classPublishService.publish(idLopHp));
    }

    @PostMapping("/bulk-publish")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopHocPhanBulkPublishResponse> bulkPublish(@RequestParam Long hocKyId) {
        return ResponseEntity.ok(classPublishService.bulkPublish(hocKyId));
    }

    /**
     * Force mở tất cả lớp của học kỳ — chuyển PUBLISHED + DANG_MO bất kể GV/lịch.
     * Dùng để mở nhanh đăng ký cho demo / đồ án.
     */
    @PostMapping("/force-publish-all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LopHocPhanBulkPublishResponse> forcePublishAll(@RequestParam Long hocKyId) {
        return ResponseEntity.ok(classPublishService.forcePublishAll(hocKyId));
    }
}
