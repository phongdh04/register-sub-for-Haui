package com.example.demo.controller;

import com.example.demo.payload.response.TimetableResponse;
import com.example.demo.service.ITimetableService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final ITimetableService timetableService;

    /**
     * Task 10 - Dịch vụ Thời Khóa Biểu thông minh.
     * Hỗ trợ:
     * - Không truyền hocKyId: lấy học kỳ hiện hành (nếu có).
     * - Có truyền hocKyId: lấy TKB theo học kỳ chỉ định.
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<TimetableResponse> getMyTimetable(
            Authentication authentication,
            @RequestParam(required = false) Long hocKyId) {
        TimetableResponse response = timetableService.getMyTimetable(authentication.getName(), hocKyId);
        return ResponseEntity.ok(response);
    }
}
