package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class HocPhanResponse {
    private Long idHocPhan;
    private String maHocPhan;
    private String tenHocPhan;
    private String maIn;
    private Integer soTinChi;
    private String loaiMon;
    private Map<String, Object> thuocTinhJson;
    private Map<String, Object> dieuKienRangBuocJson;
}
