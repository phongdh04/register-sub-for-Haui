package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GiangVienResponse {
    private Long idGiangVien;
    private String maGiangVien;
    private String tenGiangVien;
    private String email;
    private String sdt;
    private String hocHamHocVi;
    private Long idKhoa;
    private String tenKhoa;
}
