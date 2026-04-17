package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FinanceWalletTxRowResponse {
    private Long idGiaoDichVi;
    private String maSinhVien;
    private String hoTenSinhVien;
    private String loai;
    private BigDecimal soTien;
    private BigDecimal soDuSau;
    private LocalDateTime thoiGian;
    private String maDonHangThanhToan;
}
