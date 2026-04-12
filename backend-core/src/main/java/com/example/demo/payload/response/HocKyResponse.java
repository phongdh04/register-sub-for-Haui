package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HocKyResponse {
    private Long idHocKy;
    private String namHoc;
    private Integer kyThu;
    private Boolean trangThaiHienHanh;
    private String tenHocKy; // Computed: "Học kỳ 1 năm 2024-2025"
}
