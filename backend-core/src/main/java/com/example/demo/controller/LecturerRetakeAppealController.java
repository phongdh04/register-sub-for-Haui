package com.example.demo.controller;

import com.example.demo.payload.request.LecturerRetakeAppealDecisionRequest;
import com.example.demo.payload.response.RetakeAppealRowResponse;
import com.example.demo.service.IRetakeAppealService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Task 18 – Giảng viên xử lý phúc khảo (đồng ý / từ chối, cập nhật điểm).
 */
@RestController
@RequestMapping("/api/v1/lecturer/retake-appeals")
@RequiredArgsConstructor
public class LecturerRetakeAppealController {

    private final IRetakeAppealService retakeAppealService;

    @GetMapping
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<RetakeAppealRowResponse>> list(
            Authentication authentication,
            @RequestParam(required = false) String trangThai) {
        return ResponseEntity.ok(retakeAppealService.listForLecturer(authentication.getName(), trangThai));
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<RetakeAppealRowResponse> process(
            Authentication authentication,
            @PathVariable("id") Long idYeuCau,
            @Valid @RequestBody LecturerRetakeAppealDecisionRequest body) {
        return ResponseEntity.ok(retakeAppealService.processAppeal(authentication.getName(), idYeuCau, body));
    }
}
