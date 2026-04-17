package com.example.demo.controller;

import com.example.demo.payload.request.AttendanceCheckInRequest;
import com.example.demo.payload.response.AttendanceCheckInResponse;
import com.example.demo.service.IAttendanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 16 – Sinh viên điểm danh bằng mã buổi học (token từ QR giảng viên).
 */
@RestController
@RequestMapping("/api/v1/attendance")
@RequiredArgsConstructor
public class StudentAttendanceController {

    private final IAttendanceService attendanceService;

    @PostMapping("/me/check-in")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<AttendanceCheckInResponse> checkIn(
            Authentication authentication,
            @Valid @RequestBody AttendanceCheckInRequest body) {
        return ResponseEntity.ok(attendanceService.studentCheckIn(authentication.getName(), body));
    }
}
