package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class AttendanceCheckInResponse {
    private String maLopHp;
    private String tenHocPhan;
    private LocalDate ngayBuoi;
    private String trangThai;
}
