package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRowResponse {

    private Long idNhatKy;
    private LocalDateTime thoiGian;
    private String tenDangNhap;
    private String vaiTro;
    private String maHanhDong;
    private String moTaNgan;
    private String chiTietJson;
}
