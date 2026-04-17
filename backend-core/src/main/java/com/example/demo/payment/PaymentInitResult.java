package com.example.demo.payment;

/**
 * Kết quả khởi tạo thanh toán từ adapter (QR text hoặc URL redirect).
 */
public record PaymentInitResult(String qrContent, String redirectUrl, String clientHint) {
}
