package com.example.demo.controller;

import com.example.demo.service.IStudentTimetableProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Sprint 5 — Admin tool: rebuild read-model TKB cho 1 sinh viên trong 1 học kỳ.
 * Dùng khi event AFTER_COMMIT bị miss (rare race) hoặc sau khi sửa lịch lớp hàng loạt.
 */
@RestController
@RequestMapping("/api/v1/admin/timetable-projection")
@RequiredArgsConstructor
public class AdminTimetableProjectionController {

    private final IStudentTimetableProjection projection;

    @PostMapping("/rebuild")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> rebuild(
            @RequestParam Long sinhVienId,
            @RequestParam Long hocKyId) {
        int slots = projection.rebuildForStudent(sinhVienId, hocKyId);
        return ResponseEntity.ok(Map.of(
                "sinhVienId", sinhVienId,
                "hocKyId", hocKyId,
                "rebuiltSlots", slots
        ));
    }
}
