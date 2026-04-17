package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiaoDichThanhToan;
import com.example.demo.domain.entity.GiaoDichVi;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.payload.response.FinancePaymentRowResponse;
import com.example.demo.payload.response.FinanceReceivableRowResponse;
import com.example.demo.payload.response.FinanceSummaryResponse;
import com.example.demo.payload.response.FinanceWalletTxRowResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.GiaoDichThanhToanRepository;
import com.example.demo.repository.GiaoDichViRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.ViSinhVienRepository;
import com.example.demo.service.IAdminFinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminFinanceServiceImpl implements IAdminFinanceService {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private final SinhVienRepository sinhVienRepository;
    private final ViSinhVienRepository viSinhVienRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;
    private final GiaoDichThanhToanRepository giaoDichThanhToanRepository;
    private final GiaoDichViRepository giaoDichViRepository;

    @Override
    @Transactional(readOnly = true)
    public FinanceSummaryResponse getSummary() {
        BigDecimal tongVi = zeroIfNull(viSinhVienRepository.sumSoDuAll());
        long tongSv = sinhVienRepository.count();

        List<Object[]> phiRows = dangKyHocPhanRepository.sumHocPhiDangKyGroupedBySinhVien();
        List<Object[]> duRows = viSinhVienRepository.listAllSinhVienSoDu();
        Map<Long, BigDecimal> phiMap = toLongBigDecimalMap(phiRows);
        Map<Long, BigDecimal> duMap = toLongBigDecimalMap(duRows);

        Set<Long> ids = new HashSet<>();
        ids.addAll(phiMap.keySet());
        ids.addAll(duMap.keySet());

        BigDecimal tongNo = ZERO;
        long soSvConNo = 0;
        for (Long id : ids) {
            BigDecimal phi = phiMap.getOrDefault(id, ZERO);
            BigDecimal du = duMap.getOrDefault(id, ZERO);
            BigDecimal no = phi.subtract(du);
            if (no.compareTo(ZERO) > 0) {
                tongNo = tongNo.add(no);
                soSvConNo++;
            }
        }

        long cho = giaoDichThanhToanRepository.countByTrangThai("CHO_THANH_TOAN");
        long ok = giaoDichThanhToanRepository.countByTrangThai("THANH_CONG");
        long fail = giaoDichThanhToanRepository.countByTrangThai("THAT_BAI");
        long huy = giaoDichThanhToanRepository.countByTrangThai("HUY");

        BigDecimal tienOk = zeroIfNull(giaoDichThanhToanRepository.sumSoTienByTrangThai("THANH_CONG"));
        BigDecimal tienCho = zeroIfNull(giaoDichThanhToanRepository.sumSoTienByTrangThai("CHO_THANH_TOAN"));

        long soGdv = giaoDichViRepository.count();

        return FinanceSummaryResponse.builder()
                .tongSoSinhVien(tongSv)
                .tongSoDuViTatCa(tongVi)
                .tongNoHocPhiUocTinh(tongNo)
                .soSinhVienConNo(soSvConNo)
                .soGiaoDichChoThanhToan(cho)
                .soGiaoDichThanhCong(ok)
                .soGiaoDichThatBai(fail)
                .soGiaoDichHuy(huy)
                .tongSoTienGiaoDichThanhCong(tienOk)
                .tongSoTienGiaoDichChoThanhToan(tienCho)
                .soGiaoDichViGhiNhan(soGdv)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinanceReceivableRowResponse> pageReceivables(int page, int size) {
        Page<SinhVien> p = sinhVienRepository.findAll(
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "maSinhVien")));
        List<SinhVien> list = p.getContent();
        List<Long> ids = list.stream().map(SinhVien::getIdSinhVien).toList();

        Map<Long, BigDecimal> phiMap = ids.isEmpty()
                ? Map.of()
                : toLongBigDecimalMap(dangKyHocPhanRepository.sumHocPhiDangKyBySinhVienIds(ids));
        Map<Long, BigDecimal> duMap = ids.isEmpty()
                ? Map.of()
                : toLongBigDecimalMap(viSinhVienRepository.findSoDuBySinhVienIds(ids));

        List<FinanceReceivableRowResponse> rows = list.stream()
                .map(sv -> toReceivableRow(sv, phiMap, duMap))
                .toList();

        return new PageImpl<>(rows, p.getPageable(), p.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinancePaymentRowResponse> pagePayments(String trangThai, String provider, int page, int size) {
        String st = blankToNull(trangThai);
        String pv = blankToNull(provider);
        return giaoDichThanhToanRepository
                .pageForAdmin(st, pv, PageRequest.of(page, size))
                .map(this::toPaymentRow);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FinanceWalletTxRowResponse> pageWalletTransactions(int page, int size) {
        return giaoDichViRepository
                .findAllPagedOrderByThoiGianDesc(PageRequest.of(page, size))
                .map(this::toWalletRow);
    }

    private FinanceReceivableRowResponse toReceivableRow(SinhVien sv, Map<Long, BigDecimal> phiMap, Map<Long, BigDecimal> duMap) {
        Long id = sv.getIdSinhVien();
        BigDecimal phi = zeroIfNull(phiMap.get(id));
        BigDecimal du = zeroIfNull(duMap.get(id));
        BigDecimal raw = phi.subtract(du);
        BigDecimal conNo = raw.compareTo(ZERO) > 0 ? raw : ZERO;
        return FinanceReceivableRowResponse.builder()
                .idSinhVien(id)
                .maSinhVien(sv.getMaSinhVien())
                .hoTen(sv.getHoTen())
                .maLop(sv.getLop() != null ? sv.getLop().getMaLop() : null)
                .soDuVi(du)
                .tongHocPhiDangKy(phi)
                .conNoUocTinh(conNo)
                .coNo(conNo.compareTo(ZERO) > 0)
                .build();
    }

    private FinancePaymentRowResponse toPaymentRow(GiaoDichThanhToan g) {
        SinhVien sv = g.getSinhVien();
        return FinancePaymentRowResponse.builder()
                .idGiaoDich(g.getIdGiaoDich())
                .maSinhVien(sv.getMaSinhVien())
                .hoTenSinhVien(sv.getHoTen())
                .soTien(g.getSoTien())
                .provider(g.getProvider())
                .trangThai(g.getTrangThai())
                .maDonHang(g.getMaDonHang())
                .taoLuc(g.getTaoLuc())
                .noiDung(g.getNoiDung())
                .build();
    }

    private FinanceWalletTxRowResponse toWalletRow(GiaoDichVi g) {
        SinhVien sv = g.getViSinhVien().getSinhVien();
        String maDon = null;
        if (g.getGiaoDichThanhToan() != null) {
            maDon = g.getGiaoDichThanhToan().getMaDonHang();
        }
        return FinanceWalletTxRowResponse.builder()
                .idGiaoDichVi(g.getIdGiaoDichVi())
                .maSinhVien(sv.getMaSinhVien())
                .hoTenSinhVien(sv.getHoTen())
                .loai(g.getLoai())
                .soTien(g.getSoTien())
                .soDuSau(g.getSoDuSau())
                .thoiGian(g.getThoiGian())
                .maDonHangThanhToan(maDon)
                .build();
    }

    private static Map<Long, BigDecimal> toLongBigDecimalMap(List<Object[]> rows) {
        Map<Long, BigDecimal> m = new HashMap<>();
        if (rows == null) {
            return m;
        }
        for (Object[] r : rows) {
            if (r == null || r.length < 2 || r[0] == null) {
                continue;
            }
            m.put((Long) r[0], zeroIfNull((BigDecimal) r[1]));
        }
        return m;
    }

    private static BigDecimal zeroIfNull(BigDecimal v) {
        return v == null ? ZERO : v;
    }

    private static String blankToNull(String s) {
        if (s == null) {
            return null;
        }
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
