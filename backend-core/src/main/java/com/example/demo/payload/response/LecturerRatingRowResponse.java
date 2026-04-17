package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LecturerRatingRowResponse {
    private Long idDangKy;
    private Long idGiangVien;
    private String maGiangVien;
    private String tenGiangVien;
    private String maLopHp;
    private String maHocPhan;
    private String tenHocPhan;
    private boolean coGiangVien;
    private boolean daDanhGia;
    private Integer diemTong;
    private String binhLuan;
}
