package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChuongTrinhDaoTaoResponse {
    private Long idCtdt;
    private Long idNganh;
    private String tenNganh;
    private Integer tongSoTinChi;
    private String mucTieu;
    private String thoiGianGiangDay;
    private String doiTuongTuyenSinh;
    private Integer namApDung;
}
