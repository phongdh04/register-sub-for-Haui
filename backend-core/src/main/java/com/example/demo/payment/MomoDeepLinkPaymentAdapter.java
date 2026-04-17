package com.example.demo.payment;

import com.example.demo.domain.entity.GiaoDichThanhToan;
import org.springframework.stereotype.Component;

/**
 * Stub MoMo: deep link giả lập (không gọi API thật).
 */
@Component
public class MomoDeepLinkPaymentAdapter implements PaymentGatewayAdapter {

    @Override
    public boolean supports(String providerCode) {
        return "MOMO".equalsIgnoreCase(providerCode);
    }

    @Override
    public PaymentInitResult initiate(GiaoDichThanhToan order) {
        String deep = "momo://pay?partnerCode=EDUPORT&orderId=" + order.getMaDonHang()
                + "&amount=" + order.getSoTien().toPlainString();
        return new PaymentInitResult(null, deep, "Mở ứng dụng MoMo (stub deep link).");
    }
}
