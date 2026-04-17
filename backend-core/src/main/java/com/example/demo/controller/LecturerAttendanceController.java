package com.example.demo.controller;

import com.example.demo.payload.request.CreateAttendanceSessionRequest;
import com.example.demo.payload.request.PatchAttendanceRowRequest;
import com.example.demo.payload.response.AttendanceRowResponse;
import com.example.demo.payload.response.AttendanceSessionResponse;
import com.example.demo.payload.response.LecturerTeachingClassResponse;
import com.example.demo.service.IAttendanceService;
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

import java.util.List;

/**
 * Task 16 – Giảng viên: lớp phụ trách & buổi điểm danh.
 */
@RestController
@RequestMapping("/api/v1/lecturer/attendance")
@RequiredArgsConstructor
public class LecturerAttendanceController {

    private final IAttendanceService attendanceService;

    @GetMapping("/my-classes")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<List<LecturerTeachingClassResponse>> myClasses(Authentication authentication) {
        return ResponseEntity.ok(attendanceService.listMyTeachingClasses(authentication.getName()));
    }

    @PostMapping("/classes/{idLopHp}/sessions")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<AttendanceSessionResponse> createOrGetSession(
            Authentication authentication,
            @PathVariable Long idLopHp,
            @Valid @RequestBody(required = false) CreateAttendanceSessionRequest body) {
        CreateAttendanceSessionRequest req = body != null ? body : new CreateAttendanceSessionRequest();
        return ResponseEntity.ok(attendanceService.createOrGetSession(authentication.getName(), idLopHp, req));
    }

    @GetMapping("/sessions/{idBuoi}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<AttendanceSessionResponse> getSession(
            Authentication authentication,
            @PathVariable Long idBuoi) {
        return ResponseEntity.ok(attendanceService.getSessionForLecturer(authentication.getName(), idBuoi));
    }

    @PatchMapping("/rows/{idDiemDanh}")
    @PreAuthorize("hasRole('LECTURER')")
    public ResponseEntity<AttendanceRowResponse> patchRow(
            Authentication authentication,
            @PathVariable Long idDiemDanh,
            @Valid @RequestBody PatchAttendanceRowRequest body) {
        return ResponseEntity.ok(attendanceService.patchRowForLecturer(authentication.getName(), idDiemDanh, body));
    }
}
