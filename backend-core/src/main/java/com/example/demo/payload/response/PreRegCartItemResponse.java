package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PreRegCartItemResponse {
    private Long idGioHang;
    private Long idLopHp;
    private String maLopHp;
    private Long idHocPhan;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer soTinChi;
    private BigDecimal hocPhi;
    private Long idHocKy;
    private String hocKyLabel;
}
