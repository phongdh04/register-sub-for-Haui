package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PaymentQrResponse {
    private Long idGiaoDich;
    private String maDonHang;
    private BigDecimal soTien;
    private String provider;
    private String trangThai;
    private String noiDung;
    /** Nội dung để sinh QR (MOCK / VietQR text). */
    private String qrContent;
    /** URL chuyển hướng cổng (VNPay). */
    private String redirectUrl;
    private String ghiChu;
}
