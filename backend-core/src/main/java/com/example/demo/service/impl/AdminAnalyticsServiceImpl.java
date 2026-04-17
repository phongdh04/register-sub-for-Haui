package com.example.demo.service.impl;

import com.example.demo.domain.entity.HocKy;
import com.example.demo.payload.response.AnalyticsDangKyByHocKyItem;
import com.example.demo.payload.response.AnalyticsDashboardResponse;
import com.example.demo.payload.response.AnalyticsKhoaCountItem;
import com.example.demo.payload.response.AnalyticsPaymentStatusItem;
import com.example.demo.payload.response.AnalyticsTopClassItem;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.GiaoDichThanhToanRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.service.IAdminAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminAnalyticsServiceImpl implements IAdminAnalyticsService {

    private final SinhVienRepository sinhVienRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final GiaoDichThanhToanRepository giaoDichThanhToanRepository;
    private final HocKyRepository hocKyRepository;
    private final LopHocPhanRepository lopHocPhanRepository;

    @Override
    @Transactional(readOnly = true)
    public AnalyticsDashboardResponse getDashboard() {
        long tongSv = sinhVienRepository.count();
        long tongDk = dangKyHocPhanRepository.countActiveRegistrations();
        long tongGd = giaoDichThanhToanRepository.count();

        List<AnalyticsDangKyByHocKyItem> dkItems = new ArrayList<>();
        for (Object[] r : dangKyHocPhanRepository.countActiveRegistrationsByHocKy()) {
            Long idHk = (Long) r[0];
            String nam = (String) r[1];
            Integer ky = r[2] != null ? ((Number) r[2]).intValue() : null;
            long cnt = r[3] != null ? ((Number) r[3]).longValue() : 0L;
            dkItems.add(AnalyticsDangKyByHocKyItem.builder()
                    .idHocKy(idHk)
                    .namHoc(nam)
                    .kyThu(ky)
                    .hocKyLabel(formatHocKyLabel(ky, nam))
                    .soDangKyHieuLuc(cnt)
                    .build());
        }

        List<AnalyticsPaymentStatusItem> payItems = new ArrayList<>();
        for (Object[] r : giaoDichThanhToanRepository.aggregatePaymentsByStatus()) {
            payItems.add(AnalyticsPaymentStatusItem.builder()
                    .trangThai((String) r[0])
                    .soLuong(r[1] != null ? ((Number) r[1]).longValue() : 0L)
                    .tongSoTien(r[2] != null ? (BigDecimal) r[2] : BigDecimal.ZERO)
                    .build());
        }

        List<AnalyticsKhoaCountItem> khoaItems = new ArrayList<>();
        for (Object[] r : sinhVienRepository.countStudentsByKhoa()) {
            khoaItems.add(AnalyticsKhoaCountItem.builder()
                    .maKhoa((String) r[1])
                    .tenKhoa((String) r[2])
                    .soSinhVien(r[3] != null ? ((Number) r[3]).longValue() : 0L)
                    .build());
        }

        HocKy hkTop = hocKyRepository.findTopByOrderByIdHocKyDesc().orElse(null);
        List<AnalyticsTopClassItem> top = new ArrayList<>();
        Long hkId = null;
        String hkLabel = null;
        if (hkTop != null) {
            hkId = hkTop.getIdHocKy();
            hkLabel = formatHocKyLabel(hkTop.getKyThu(), hkTop.getNamHoc());
            for (Object[] row : lopHocPhanRepository.topClassesByHeadcountForHocKy(
                    hkTop.getIdHocKy(), PageRequest.of(0, 8))) {
                String maLop = (String) row[0];
                String tenHp = (String) row[1];
                int toiDa = row[2] != null ? ((Number) row[2]).intValue() : 0;
                int thuc = row[3] != null ? ((Number) row[3]).intValue() : 0;
                int pct = 0;
                if (toiDa > 0) {
                    pct = BigDecimal.valueOf(thuc)
                            .multiply(BigDecimal.valueOf(100))
                            .divide(BigDecimal.valueOf(toiDa), 0, RoundingMode.HALF_UP)
                            .intValue();
                }
                top.add(AnalyticsTopClassItem.builder()
                        .maLopHp(maLop)
                        .tenHocPhan(tenHp)
                        .siSoToiDa(toiDa)
                        .siSoThucTe(thuc)
                        .tyLePhanTram(pct)
                        .build());
            }
        }

        return AnalyticsDashboardResponse.builder()
                .tongSinhVien(tongSv)
                .tongDangKyHieuLuc(tongDk)
                .tongGiaoDichThanhToan(tongGd)
                .idHocKyTopClasses(hkId)
                .hocKyLabelTopClasses(hkLabel)
                .dangKyTheoHocKy(dkItems)
                .thanhToanTheoTrangThai(payItems)
                .sinhVienTheoKhoa(khoaItems)
                .topLopTheoSiSo(top)
                .build();
    }

    private static String formatHocKyLabel(Integer kyThu, String namHoc) {
        if (kyThu == null || namHoc == null) {
            return namHoc != null ? namHoc : "—";
        }
        return "HK" + kyThu + " - " + namHoc;
    }
}
