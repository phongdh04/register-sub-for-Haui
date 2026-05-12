package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SinhVienResponse {
    private Long idSinhVien;
    private String maSinhVien;
    private String hoTen;
    private Long idLop;
    private String maLop;
    private String tenLop;
    private Long idNganh;
    private String tenNganh;
    private Integer namNhapHoc;
}
