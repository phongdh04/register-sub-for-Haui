package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class FinanceReceivableRowResponse {
    private Long idSinhVien;
    private String maSinhVien;
    private String hoTen;
    private String maLop;
    private BigDecimal soDuVi;
    private BigDecimal tongHocPhiDangKy;
    private BigDecimal conNoUocTinh;
    private boolean coNo;
}
