package com.example.demo.controller;

import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.StudentTimetableEntry;
import com.example.demo.domain.entity.User;
import com.example.demo.payload.response.StudentTimetableEntryResponse;
import com.example.demo.payload.response.StudentTimetableSnapshotResponse;
import com.example.demo.payload.response.TimetableResponse;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IStudentTimetableProjection;
import com.example.demo.service.ITimetableService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/timetable")
@RequiredArgsConstructor
public class TimetableController {

    private final ITimetableService timetableService;

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final IStudentTimetableProjection projection;

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

    /**
     * Sprint 5 — Snapshot TKB từ read-model {@code student_timetable_entry}.
     * Khác {@code /me}: đọc trực tiếp từ projection (không parse JSON), phản ánh ngay
     * sau khi sự kiện {@code REGISTRATION_CONFIRMED} commit (eventual ms).
     */
    @GetMapping("/me/snapshot")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentTimetableSnapshotResponse> getMySnapshot(
            Authentication authentication,
            @RequestParam Long hocKyId) {
        SinhVien sv = resolveSinhVien(authentication.getName());
        List<StudentTimetableEntry> rows = projection.readForStudent(sv.getIdSinhVien(), hocKyId);
        List<StudentTimetableEntryResponse> entries = rows.stream()
                .map(StudentTimetableEntryResponse::from)
                .toList();
        return ResponseEntity.ok(StudentTimetableSnapshotResponse.builder()
                .idSinhVien(sv.getIdSinhVien())
                .idHocKy(hocKyId)
                .totalSlots(entries.size())
                .entries(entries)
                .build());
    }

    private SinhVien resolveSinhVien(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        return sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));
    }
}
