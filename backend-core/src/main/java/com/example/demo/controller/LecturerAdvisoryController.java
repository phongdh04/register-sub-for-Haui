package com.example.demo.controller;

import com.example.demo.payload.response.AtRiskStudentListResponse;
import com.example.demo.service.ILecturerAdvisoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 19 – Cố vấn học tập: lọc SV có tổng tín chỉ môn rớt vượt ngưỡng (theo khoa của GV).
 */
@RestController
@RequestMapping("/api/v1/lecturer/advisory")
@RequiredArgsConstructor
public class LecturerAdvisoryController {

    private final ILecturerAdvisoryService lecturerAdvisoryService;

    @GetMapping("/at-risk")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<AtRiskStudentListResponse> listAtRisk(
            Authentication authentication,
            @RequestParam(required = false) Integer minFailTc) {
        return ResponseEntity.ok(lecturerAdvisoryService.listAtRiskStudents(authentication.getName(), minFailTc));
    }
}
