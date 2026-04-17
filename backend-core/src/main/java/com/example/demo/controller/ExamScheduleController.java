package com.example.demo.controller;

import com.example.demo.payload.response.ExamScheduleResponse;
import com.example.demo.service.IExamScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 11 – Lịch thi theo đăng ký học phần.
 */
@RestController
@RequestMapping("/api/v1/exams")
@RequiredArgsConstructor
public class ExamScheduleController {

    private final IExamScheduleService examScheduleService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ExamScheduleResponse> getMyExams(
            Authentication authentication,
            @RequestParam(required = false) Long hocKyId) {
        return ResponseEntity.ok(examScheduleService.getMyExamSchedule(authentication.getName(), hocKyId));
    }
}
