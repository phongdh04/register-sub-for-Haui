package com.example.demo.controller;

import com.example.demo.payload.request.LecturerUpdateGradeRequest;
import com.example.demo.payload.response.GradebookRowResponse;
import com.example.demo.payload.response.LecturerGradebookResponse;
import com.example.demo.service.IGradingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 17 – Giảng viên nhập điểm (BangDiemMon), nháp / công bố.
 */
@RestController
@RequestMapping("/api/v1/lecturer/grades")
@RequiredArgsConstructor
public class LecturerGradingController {

    private final IGradingService gradingService;

    @GetMapping("/classes/{idLopHp}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<LecturerGradebookResponse> getGradebook(
            Authentication authentication,
            @PathVariable Long idLopHp) {
        return ResponseEntity.ok(gradingService.getGradebook(authentication.getName(), idLopHp));
    }

    @PatchMapping("/by-dang-ky/{idDangKy}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<GradebookRowResponse> saveDraft(
            Authentication authentication,
            @PathVariable Long idDangKy,
            @Valid @RequestBody LecturerUpdateGradeRequest body) {
        return ResponseEntity.ok(gradingService.saveDraftGrade(authentication.getName(), idDangKy, body));
    }

    @PostMapping("/by-dang-ky/{idDangKy}/publish")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<GradebookRowResponse> publish(
            Authentication authentication,
            @PathVariable Long idDangKy) {
        return ResponseEntity.ok(gradingService.publishGrade(authentication.getName(), idDangKy));
    }
}
