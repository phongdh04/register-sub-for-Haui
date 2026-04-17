package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AnalyticsDashboardResponse {
    private long tongSinhVien;
    private long tongDangKyHieuLuc;
    private long tongGiaoDichThanhToan;
    private Long idHocKyTopClasses;
    private String hocKyLabelTopClasses;
    private List<AnalyticsDangKyByHocKyItem> dangKyTheoHocKy;
    private List<AnalyticsPaymentStatusItem> thanhToanTheoTrangThai;
    private List<AnalyticsKhoaCountItem> sinhVienTheoKhoa;
    private List<AnalyticsTopClassItem> topLopTheoSiSo;
}
