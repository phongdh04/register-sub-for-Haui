package com.example.demo.payment;

import com.example.demo.domain.entity.GiaoDichThanhToan;
import org.springframework.stereotype.Component;

/**
 * Cổng MOCK: trả chuỗi QR tĩnh để demo không cần khóa merchant.
 */
@Component
public class MockQrPaymentAdapter implements PaymentGatewayAdapter {

    @Override
    public boolean supports(String providerCode) {
        return providerCode == null
                || providerCode.isBlank()
                || "MOCK".equalsIgnoreCase(providerCode);
    }

    @Override
    public PaymentInitResult initiate(GiaoDichThanhToan order) {
        String payload = "EDUPORT|MOCK|" + order.getMaDonHang() + "|" + order.getSoTien().toPlainString() + "|VND";
        return new PaymentInitResult(payload, null, "Chuỗi dùng để render QR demo (MOCK).");
    }
}
