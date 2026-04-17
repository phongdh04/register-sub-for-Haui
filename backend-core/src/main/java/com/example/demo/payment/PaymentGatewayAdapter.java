package com.example.demo.payment;

import com.example.demo.domain.entity.GiaoDichThanhToan;

/**
 * Adapter Pattern: tách tích hợp từng cổng thanh toán (MOCK / VNPay / MoMo).
 */
public interface PaymentGatewayAdapter {

    boolean supports(String providerCode);

    PaymentInitResult initiate(GiaoDichThanhToan order);
}
