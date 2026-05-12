package com.example.demo.payload.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChuongTrinhDaoTaoRequest {
    @NotNull(message = "ID Ngành không được để trống")
    private Long idNganh;

    @NotNull(message = "Tổng số tín chỉ không được để trống")
    private Integer tongSoTinChi;

    private String mucTieu;
    private String thoiGianGiangDay;
    private String doiTuongTuyenSinh;
    private Integer namApDung;
}
