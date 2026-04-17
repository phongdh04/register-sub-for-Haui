package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class WalletMeResponse {
    private Long idVi;
    private BigDecimal soDu;
    /** Tổng học phí lớp đã đăng ký (theo `LopHocPhan.hocPhi`), minh họa công nợ. */
    private BigDecimal tongHocPhiDangKyUocTinh;
    /** max(0, tongHocPhi - soDu) — ước tính phần chưa đủ trong ví. */
    private BigDecimal noHocPhiUocTinh;
    private List<WalletTransactionResponse> giaoDichGanDay;
}
