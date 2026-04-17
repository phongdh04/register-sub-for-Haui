package com.example.demo.controller;

import com.example.demo.payload.response.WalletMeResponse;
import com.example.demo.service.IWalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Task 8 – Ví sinh viên (số dư + lịch sử + ước tính học phí đăng ký).
 */
@RestController
@RequestMapping("/api/v1/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final IWalletService walletService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<WalletMeResponse> getMyWallet(Authentication authentication) {
        return ResponseEntity.ok(walletService.getMyWallet(authentication.getName()));
    }
}
