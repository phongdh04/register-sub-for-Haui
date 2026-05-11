package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Sprint 5 — Snapshot TKB sinh viên đọc từ read-model {@code student_timetable_entry}.
 */
@Getter
@Builder
public class StudentTimetableSnapshotResponse {
    private final Long idSinhVien;
    private final Long idHocKy;
    private final int totalSlots;
    private final List<StudentTimetableEntryResponse> entries;
}
