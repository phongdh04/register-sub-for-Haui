package com.example.demo.service;

import com.example.demo.payload.request.CreateTuitionQrRequest;
import com.example.demo.payload.response.PaymentQrResponse;

public interface IPaymentService {

    PaymentQrResponse createTuitionQr(String username, CreateTuitionQrRequest request);

    PaymentQrResponse getMyPayment(String username, Long idGiaoDich);

    /**
     * Demo: xác nhận thanh toán MOCK thành công và ghi có ví (Task 8 + Task 9).
     */
    PaymentQrResponse confirmMockPayment(String username, Long idGiaoDich);
}
