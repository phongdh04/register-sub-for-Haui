package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CtdtHocPhanResponse {
    private Long idCtdtHp;
    private Long idCtdt;
    private Long idHocPhan;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer soTinChi;
    private String khoiKienThuc;
    private Boolean batBuoc;
    private Integer hocKyGoiY;
}
