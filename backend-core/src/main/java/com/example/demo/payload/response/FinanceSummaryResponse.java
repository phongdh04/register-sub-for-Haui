package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FinanceSummaryResponse {
    private long tongSoSinhVien;
    private BigDecimal tongSoDuViTatCa;
    private BigDecimal tongNoHocPhiUocTinh;
    private long soSinhVienConNo;
    private long soGiaoDichChoThanhToan;
    private long soGiaoDichThanhCong;
    private long soGiaoDichThatBai;
    private long soGiaoDichHuy;
    private BigDecimal tongSoTienGiaoDichThanhCong;
    private BigDecimal tongSoTienGiaoDichChoThanhToan;
    private long soGiaoDichViGhiNhan;
}
