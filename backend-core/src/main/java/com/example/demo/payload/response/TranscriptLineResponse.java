package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TranscriptLineResponse {
    private Long idDangKy;
    private String maLopHp;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer soTinChi;
    private BigDecimal diemHe4;
    private String diemChu;
    private boolean daCoDiem;
}
