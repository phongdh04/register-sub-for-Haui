package com.example.demo.payload.response;

import com.example.demo.domain.entity.StudentTimetableEntry;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Sprint 5 — Response cho 1 slot TKB từ read-model.
 */
@Getter
@Builder
public class StudentTimetableEntryResponse {

    private final Long idDangKy;
    private final Long idLopHp;
    private final String maLopHp;
    private final String maHocPhan;
    private final String tenHocPhan;
    private final String tenGiangVien;
    private final Short thu;
    private final String tiet;
    private final String phong;
    private final LocalDate ngayBatDau;
    private final LocalDate ngayKetThuc;
    private final Short slotIndex;

    public static StudentTimetableEntryResponse from(StudentTimetableEntry e) {
        return StudentTimetableEntryResponse.builder()
                .idDangKy(e.getIdDangKy())
                .idLopHp(e.getIdLopHp())
                .maLopHp(e.getMaLopHp())
                .maHocPhan(e.getMaHocPhan())
                .tenHocPhan(e.getTenHocPhan())
                .tenGiangVien(e.getTenGiangVien())
                .thu(e.getThu())
                .tiet(e.getTiet())
                .phong(e.getPhong())
                .ngayBatDau(e.getNgayBatDau())
                .ngayKetThuc(e.getNgayKetThuc())
                .slotIndex(e.getSlotIndex())
                .build();
    }
}
