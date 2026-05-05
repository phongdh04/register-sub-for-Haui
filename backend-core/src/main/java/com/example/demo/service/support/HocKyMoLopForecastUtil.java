package com.example.demo.service.support;

import com.example.demo.domain.entity.HocKy;

/** Quy chiếu năm học mục tiêu → “kỳ logic” trong CTĐT ({@link com.example.demo.domain.entity.CtdtHocPhan#getHocKyGoiY()}) §6.2. */
public final class HocKyMoLopForecastUtil {

    private HocKyMoLopForecastUtil() {
    }

    /** Năm bắt đầu chuỗi {@code namHoc}, ví dụ {@code "2024-2025"} → {@code 2024}. */
    public static int parseNamHocStartYear(String namHoc) {
        if (namHoc == null || namHoc.isBlank()) {
            throw new IllegalArgumentException("namHoc không hợp lệ");
        }
        String[] parts = namHoc.trim().split("[\\-_/]");
        try {
            return Integer.parseInt(parts[0].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Không đọc được năm từ namHoc='" + namHoc + "'");
        }
    }

    public static int kyThuSanitized(HocKy hk) {
        Integer k = hk.getKyThu();
        if (k == null || k < 1) {
            return 1;
        }
        return k;
    }

    /**
     * Kỳ logic hiện tại của SV nếu vào học đúng niên khóa: {@code 2 * (năm_đầu_HK − nam_nhap) + ky_thu}.
     */
    public static int kyLogicSinhVien(int namNhapHocLopHanChinh, int namDauNamHocMucTieu, int kyThuMucTieu) {
        return 2 * (namDauNamHocMucTieu - namNhapHocLopHanChinh) + kyThuMucTieu;
    }
}
