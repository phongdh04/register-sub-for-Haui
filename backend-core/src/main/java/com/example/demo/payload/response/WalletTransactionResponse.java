package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class WalletTransactionResponse {
    private Long idGiaoDichVi;
    private String loai;
    private BigDecimal soTien;
    private BigDecimal soDuSau;
    private String moTa;
    private LocalDateTime thoiGian;
    private String maDonHangThanhToan;
}
