package com.example.demo.support;

import com.example.demo.domain.entity.HocKy;
import org.springframework.stereotype.Component;

import java.time.Instant;

/**
 * Kiểm tra cửa sổ thời gian đăng ký theo học kỳ (admin cấu hình trên {@link HocKy}).
 * Cặp mốc null = không áp dụng khóa thời gian cho pha đó (tương thích dữ liệu cũ).
 */
@Component
public class RegistrationScheduleChecker {

    public boolean isPreRegistrationOpen(HocKy hk) {
        if (hk == null) {
            return true;
        }
        if (!hasWindowPair(hk.getPreDangKyMoTu(), hk.getPreDangKyMoDen())) {
            return true;
        }
        Instant now = Instant.now();
        return !now.isBefore(hk.getPreDangKyMoTu()) && !now.isAfter(hk.getPreDangKyMoDen());
    }

    public void requirePreRegistrationOpen(HocKy hk) {
        if (!isPreRegistrationOpen(hk)) {
            throw new IllegalArgumentException(
                    "Chưa đến hoặc đã hết phiên đăng ký trước (giỏ nháp) cho học kỳ này.");
        }
    }

    public boolean isOfficialRegistrationOpen(HocKy hk) {
        if (hk == null) {
            return true;
        }
        if (!hasWindowPair(hk.getDangKyChinhThucTu(), hk.getDangKyChinhThucDen())) {
            return true;
        }
        Instant now = Instant.now();
        return !now.isBefore(hk.getDangKyChinhThucTu()) && !now.isAfter(hk.getDangKyChinhThucDen());
    }

    private static boolean hasWindowPair(Instant from, Instant to) {
        return from != null && to != null;
    }
}
