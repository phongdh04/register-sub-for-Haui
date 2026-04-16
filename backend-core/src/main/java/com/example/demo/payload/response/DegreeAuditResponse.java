package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DegreeAuditResponse {
    private Long idSinhVien;
    private String maSinhVien;
    private String hoTenSinhVien;

    private Long idNganh;
    private String maNganh;
    private String tenNganh;
    private String heDaoTao;
    private String tenKhoa;

    private Long idCtdt;
    private Integer namApDung;
    private Integer tongSoTinChiToanKhoa;
    private String mucTieu;
    private String thoiGianGiangDay;

    private int tongTinChiDaHoanThanh;
    private List<DegreeAuditBlockResponse> khois;
}

