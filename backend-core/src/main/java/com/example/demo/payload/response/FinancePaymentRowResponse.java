package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class FinancePaymentRowResponse {
    private Long idGiaoDich;
    private String maSinhVien;
    private String hoTenSinhVien;
    private BigDecimal soTien;
    private String provider;
    private String trangThai;
    private String maDonHang;
    private LocalDateTime taoLuc;
    private String noiDung;
}
