package com.example.demo.controller;

import com.example.demo.payload.response.FinancePaymentRowResponse;
import com.example.demo.payload.response.FinanceReceivableRowResponse;
import com.example.demo.payload.response.FinanceSummaryResponse;
import com.example.demo.payload.response.FinanceWalletTxRowResponse;
import com.example.demo.service.IAdminFinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 14 – Giám sát tài chính (kế toán / admin).
 */
@RestController
@RequestMapping("/api/v1/admin/finance")
@RequiredArgsConstructor
public class AdminFinanceController {

    private final IAdminFinanceService adminFinanceService;

    @GetMapping("/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FinanceSummaryResponse> summary() {
        return ResponseEntity.ok(adminFinanceService.getSummary());
    }

    @GetMapping("/receivables")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FinanceReceivableRowResponse>> receivables(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminFinanceService.pageReceivables(page, size));
    }

    @GetMapping("/payments")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FinancePaymentRowResponse>> payments(
            @RequestParam(required = false) String trangThai,
            @RequestParam(required = false) String provider,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminFinanceService.pagePayments(trangThai, provider, page, size));
    }

    @GetMapping("/wallet-transactions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<FinanceWalletTxRowResponse>> walletTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminFinanceService.pageWalletTransactions(page, size));
    }
}
