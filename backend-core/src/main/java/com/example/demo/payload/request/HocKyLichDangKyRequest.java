package com.example.demo.payload.request;

import lombok.Data;

import java.time.Instant;

/**
 * Admin cập nhật lịch đăng ký theo học kỳ.
 * Mỗi pha: cả hai mốc null (không khóa) hoặc cả hai có giá trị (bắt buộc from ≤ to).
 */
@Data
public class HocKyLichDangKyRequest {

    private Instant preDangKyMoTu;
    private Instant preDangKyMoDen;

    private Instant dangKyChinhThucTu;
    private Instant dangKyChinhThucDen;
}
