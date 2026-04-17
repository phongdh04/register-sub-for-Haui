package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AnalyticsPaymentStatusItem {
    private String trangThai;
    private long soLuong;
    private BigDecimal tongSoTien;
}
