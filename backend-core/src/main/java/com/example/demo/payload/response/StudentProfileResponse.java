package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class StudentProfileResponse {
    private Long idSinhVien;
    private String maSinhVien;
    private String hoTen;

    private Long idLop;
    private String maLop;
    private String tenLop;
    private Integer namNhapHoc;

    private Long idNganh;
    private String maNganh;
    private String tenNganh;
    private String heDaoTao;

    private Long idKhoa;
    private String maKhoa;
    private String tenKhoa;

    private Long idCoVan;
    private String tenCoVan;
    private String emailCoVan;
    private String sdtCoVan;

    private String email;
    private String sdt;
    private String diaChi;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String quocTich;
    private String danToc;
    private String tonGiao;
    private String soCccd;
    private LocalDate ngayCapCccd;
    private String noiCapCccd;
    private String maTheBhyt;
    private String tenNganHang;
    private String soTkNganHang;

    private List<ProfileProcedureResponse> thuTucTrucTuyen;
}
