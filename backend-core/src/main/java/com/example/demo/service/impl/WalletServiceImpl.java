package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiaoDichThanhToan;
import com.example.demo.domain.entity.GiaoDichVi;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.domain.entity.ViSinhVien;
import com.example.demo.payload.response.WalletMeResponse;
import com.example.demo.payload.response.WalletTransactionResponse;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.GiaoDichThanhToanRepository;
import com.example.demo.repository.GiaoDichViRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ViSinhVienRepository;
import com.example.demo.service.IWalletService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements IWalletService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final ViSinhVienRepository viSinhVienRepository;
    private final GiaoDichViRepository giaoDichViRepository;
    private final GiaoDichThanhToanRepository giaoDichThanhToanRepository;
    private final DangKyHocPhanRepository dangKyHocPhanRepository;

    @Override
    @Transactional
    public WalletMeResponse getMyWallet(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));
        SinhVien sv = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        ViSinhVien vi = ensureWalletRow(sv);
        BigDecimal tongHocPhi = dangKyHocPhanRepository.sumHocPhiDangKyBySinhVien(sv.getIdSinhVien());
        if (tongHocPhi == null) {
            tongHocPhi = BigDecimal.ZERO;
        }
        BigDecimal soDu = vi.getSoDu() != null ? vi.getSoDu() : BigDecimal.ZERO;
        BigDecimal no = tongHocPhi.subtract(soDu);
        if (no.compareTo(BigDecimal.ZERO) < 0) {
            no = BigDecimal.ZERO;
        }

        List<GiaoDichVi> recent = giaoDichViRepository.findRecentForVi(vi.getIdVi(), PageRequest.of(0, 40));

        return WalletMeResponse.builder()
                .idVi(vi.getIdVi())
                .soDu(soDu)
                .tongHocPhiDangKyUocTinh(tongHocPhi)
                .noHocPhiUocTinh(no)
                .giaoDichGanDay(recent.stream().map(this::toTx).collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional
    public void applyCreditForSuccessfulPayment(GiaoDichThanhToan gd) {
        if (gd == null || gd.getIdGiaoDich() == null) {
            throw new IllegalArgumentException("Giao dịch thanh toán không hợp lệ.");
        }
        GiaoDichThanhToan fresh = giaoDichThanhToanRepository.findById(gd.getIdGiaoDich())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch thanh toán."));
        if (!"THANH_CONG".equals(fresh.getTrangThai())) {
            throw new IllegalStateException("Chỉ ghi có ví khi thanh toán đã THANH_CONG.");
        }
        if (giaoDichViRepository.existsByGiaoDichThanhToan_IdGiaoDich(fresh.getIdGiaoDich())) {
            return;
        }

        SinhVien sv = fresh.getSinhVien();
        ViSinhVien vi = ensureWalletRow(sv);
        BigDecimal soDuHienTai = vi.getSoDu() != null ? vi.getSoDu() : BigDecimal.ZERO;
        BigDecimal soTien = fresh.getSoTien() != null ? fresh.getSoTien() : BigDecimal.ZERO;
        BigDecimal soDuMoi = soDuHienTai.add(soTien);
        vi.setSoDu(soDuMoi);
        viSinhVienRepository.save(vi);

        String moTa = "Nạp từ thanh toán " + fresh.getMaDonHang()
                + (fresh.getNoiDung() != null && !fresh.getNoiDung().isBlank() ? " — " + fresh.getNoiDung() : "");

        giaoDichViRepository.save(GiaoDichVi.builder()
                .viSinhVien(vi)
                .loai("NAP_TU_THANH_TOAN")
                .soTien(soTien)
                .soDuSau(soDuMoi)
                .giaoDichThanhToan(fresh)
                .moTa(moTa)
                .build());
    }

    private ViSinhVien ensureWalletRow(SinhVien sv) {
        return viSinhVienRepository.findBySinhVien_IdSinhVien(sv.getIdSinhVien())
                .orElseGet(() -> viSinhVienRepository.save(ViSinhVien.builder()
                        .sinhVien(sv)
                        .soDu(BigDecimal.ZERO)
                        .build()));
    }

    private WalletTransactionResponse toTx(GiaoDichVi g) {
        String maDon = null;
        if (g.getGiaoDichThanhToan() != null) {
            maDon = g.getGiaoDichThanhToan().getMaDonHang();
        }
        return WalletTransactionResponse.builder()
                .idGiaoDichVi(g.getIdGiaoDichVi())
                .loai(g.getLoai())
                .soTien(g.getSoTien())
                .soDuSau(g.getSoDuSau())
                .moTa(g.getMoTa())
                .thoiGian(g.getThoiGian())
                .maDonHangThanhToan(maDon)
                .build();
    }
}
