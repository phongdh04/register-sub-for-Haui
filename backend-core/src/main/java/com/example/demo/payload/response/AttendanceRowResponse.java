package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AttendanceRowResponse {
    private Long idDiemDanh;
    private Long idDangKy;
    private String maSinhVien;
    private String hoTen;
    private String trangThai;
    private LocalDateTime thoiGianCapNhat;
}
