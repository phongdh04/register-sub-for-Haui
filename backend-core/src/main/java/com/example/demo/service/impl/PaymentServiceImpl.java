package com.example.demo.service.impl;

import com.example.demo.domain.entity.GiaoDichThanhToan;
import com.example.demo.domain.entity.SinhVien;
import com.example.demo.domain.entity.User;
import com.example.demo.payment.PaymentGatewayAdapter;
import com.example.demo.payment.PaymentInitResult;
import com.example.demo.payload.request.CreateTuitionQrRequest;
import com.example.demo.payload.response.PaymentQrResponse;
import com.example.demo.repository.GiaoDichThanhToanRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.IPaymentService;
import com.example.demo.service.IWalletService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements IPaymentService {

    private final UserRepository userRepository;
    private final SinhVienRepository sinhVienRepository;
    private final GiaoDichThanhToanRepository giaoDichThanhToanRepository;
    private final IWalletService walletService;
    private final List<PaymentGatewayAdapter> paymentGatewayAdapters;

    @Override
    @Transactional
    public PaymentQrResponse createTuitionQr(String username, CreateTuitionQrRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));

        SinhVien sinhVien = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        String provider = request.getProvider() == null || request.getProvider().isBlank()
                ? "MOCK"
                : request.getProvider().trim().toUpperCase();

        String maDonHang = "EP-" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);

        GiaoDichThanhToan gd = GiaoDichThanhToan.builder()
                .sinhVien(sinhVien)
                .soTien(request.getSoTien())
                .noiDung(request.getNoiDung())
                .provider(provider)
                .trangThai("CHO_THANH_TOAN")
                .maDonHang(maDonHang)
                .build();
        gd = giaoDichThanhToanRepository.save(gd);

        PaymentGatewayAdapter adapter = resolveAdapter(provider);
        PaymentInitResult init = adapter.initiate(gd);

        gd.setQrContent(init.qrContent());
        gd.setRedirectUrl(init.redirectUrl());
        gd = giaoDichThanhToanRepository.save(gd);

        return toResponse(gd, init.clientHint());
    }

    @Override
    @Transactional(readOnly = true)
    public PaymentQrResponse getMyPayment(String username, Long idGiaoDich) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));

        SinhVien sinhVien = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        GiaoDichThanhToan gd = giaoDichThanhToanRepository.findByIdAndSinhVien_IdSinhVien(idGiaoDich, sinhVien.getIdSinhVien())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch."));

        return toResponse(gd, null);
    }

    @Override
    @Transactional
    public PaymentQrResponse confirmMockPayment(String username, Long idGiaoDich) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy tài khoản: " + username));

        SinhVien sinhVien = sinhVienRepository.findByTaiKhoan_Id(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Tài khoản chưa liên kết hồ sơ sinh viên."));

        GiaoDichThanhToan gd = giaoDichThanhToanRepository.findByIdAndSinhVien_IdSinhVien(idGiaoDich, sinhVien.getIdSinhVien())
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy giao dịch."));

        if ("THANH_CONG".equals(gd.getTrangThai())) {
            return toResponse(gd, "Giao dịch đã hoàn tất trước đó.");
        }
        if (!"CHO_THANH_TOAN".equals(gd.getTrangThai())) {
            throw new IllegalArgumentException("Trạng thái giao dịch không cho phép xác nhận.");
        }
        if (!"MOCK".equalsIgnoreCase(gd.getProvider())) {
            throw new IllegalArgumentException("Chỉ giao dịch MOCK mới xác nhận được qua API demo này.");
        }

        gd.setTrangThai("THANH_CONG");
        gd = giaoDichThanhToanRepository.save(gd);

        walletService.applyCreditForSuccessfulPayment(gd);

        return toResponse(gd, "Đã xác nhận MOCK — số tiền đã ghi có vào ví.");
    }

    private PaymentGatewayAdapter resolveAdapter(String provider) {
        return paymentGatewayAdapters.stream()
                .filter(a -> !(a instanceof com.example.demo.payment.MockQrPaymentAdapter))
                .filter(a -> a.supports(provider))
                .findFirst()
                .orElseGet(() -> paymentGatewayAdapters.stream()
                        .filter(a -> a instanceof com.example.demo.payment.MockQrPaymentAdapter)
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Thiếu adapter MOCK.")));
    }

    private static PaymentQrResponse toResponse(GiaoDichThanhToan gd, String extraHint) {
        return PaymentQrResponse.builder()
                .idGiaoDich(gd.getIdGiaoDich())
                .maDonHang(gd.getMaDonHang())
                .soTien(gd.getSoTien())
                .provider(gd.getProvider())
                .trangThai(gd.getTrangThai())
                .noiDung(gd.getNoiDung())
                .qrContent(gd.getQrContent())
                .redirectUrl(gd.getRedirectUrl())
                .ghiChu(extraHint)
                .build();
    }
}
