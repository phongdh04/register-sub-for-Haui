package com.example.demo.controller;

import com.example.demo.payload.request.CreateTuitionQrRequest;
import com.example.demo.payload.response.PaymentQrResponse;
import com.example.demo.service.IPaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Task 9 – Thanh toán học phí / QR (Adapter: MOCK, VNPay, MoMo stub).
 */
@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final IPaymentService paymentService;

    @PostMapping("/tuition-qr")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentQrResponse> createTuitionQr(
            Authentication authentication,
            @Valid @RequestBody CreateTuitionQrRequest request) {
        PaymentQrResponse body = paymentService.createTuitionQr(authentication.getName(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentQrResponse> getPayment(
            Authentication authentication,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.getMyPayment(authentication.getName(), id));
    }

    @PostMapping("/{id}/confirm-mock")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<PaymentQrResponse> confirmMock(
            Authentication authentication,
            @PathVariable Long id) {
        return ResponseEntity.ok(paymentService.confirmMockPayment(authentication.getName(), id));
    }
}
