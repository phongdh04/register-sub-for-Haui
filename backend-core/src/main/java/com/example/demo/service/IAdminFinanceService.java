package com.example.demo.service;

import com.example.demo.payload.response.FinancePaymentRowResponse;
import com.example.demo.payload.response.FinanceReceivableRowResponse;
import com.example.demo.payload.response.FinanceSummaryResponse;
import com.example.demo.payload.response.FinanceWalletTxRowResponse;
import org.springframework.data.domain.Page;

public interface IAdminFinanceService {

    FinanceSummaryResponse getSummary();

    Page<FinanceReceivableRowResponse> pageReceivables(int page, int size);

    Page<FinancePaymentRowResponse> pagePayments(String trangThai, String provider, int page, int size);

    Page<FinanceWalletTxRowResponse> pageWalletTransactions(int page, int size);
}
