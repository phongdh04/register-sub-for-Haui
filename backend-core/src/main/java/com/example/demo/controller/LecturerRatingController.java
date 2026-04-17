package com.example.demo.controller;

import com.example.demo.payload.request.LecturerRatingSubmitRequest;
import com.example.demo.payload.response.LecturerRatingListResponse;
import com.example.demo.payload.response.LecturerRatingRowResponse;
import com.example.demo.service.ILecturerRatingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 11 – Đánh giá giảng viên (theo đăng ký học phần).
 */
@RestController
@RequestMapping("/api/v1/lecturer-ratings")
@RequiredArgsConstructor
public class LecturerRatingController {

    private final ILecturerRatingService lecturerRatingService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LecturerRatingListResponse> listMine(
            Authentication authentication,
            @RequestParam(required = false) Long hocKyId) {
        return ResponseEntity.ok(lecturerRatingService.listMine(authentication.getName(), hocKyId));
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<LecturerRatingRowResponse> submit(
            Authentication authentication,
            @Valid @RequestBody LecturerRatingSubmitRequest body) {
        return ResponseEntity.ok(lecturerRatingService.submit(authentication.getName(), body));
    }
}
