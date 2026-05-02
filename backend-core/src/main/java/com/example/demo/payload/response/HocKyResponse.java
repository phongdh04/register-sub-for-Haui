package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class HocKyResponse {
    private Long idHocKy;
    private String namHoc;
    private Integer kyThu;
    private Boolean trangThaiHienHanh;
    private String tenHocKy; // Computed: "Học kỳ 1 năm 2024-2025"

    /** Đăng ký trước giờ G — mốc bắt đầu / kết thúc (null = không cấu hình khóa). */
    private Instant preDangKyMoTu;
    private Instant preDangKyMoDen;

    /** Đăng ký chính thức — mốc bắt đầu / kết thúc (null = không cấu hình khóa). */
    private Instant dangKyChinhThucTu;
    private Instant dangKyChinhThucDen;

    /** Theo thời gian máy chủ hiện tại, có đang trong phiên pre-reg không. */
    private boolean preDangKyDangMo;
    /** Theo thời gian máy chủ hiện tại, có đang trong phiên đăng ký chính thức không. */
    private boolean dangKyChinhThucDangMo;
}
