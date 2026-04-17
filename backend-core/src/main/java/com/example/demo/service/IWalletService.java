package com.example.demo.service;

import com.example.demo.domain.entity.GiaoDichThanhToan;
import com.example.demo.payload.response.WalletMeResponse;

public interface IWalletService {

    WalletMeResponse getMyWallet(String username);

    /**
     * Ghi có ví khi giao dịch thanh toán chuyển sang THANH_CONG (idempotent theo mã giao dịch thanh toán).
     */
    void applyCreditForSuccessfulPayment(GiaoDichThanhToan giaoDichThanhToan);
}
