package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RetakeAppealRowResponse {

    private Long idYeuCau;
    private String trangThai;
    private LocalDateTime ngayTao;
    private LocalDateTime ngayXuLy;
    private String lyDoSinhVien;
    private String ghiChuGiangVien;
    private BigDecimal diemHe4LucNop;
    private BigDecimal diemSauXuLy;

    private Long idDangKy;
    private String maSinhVien;
    private String hoTenSinhVien;
    private String maLopHanhChinh;
    private String tenHocPhan;
    private String maHocPhan;
    private String maLopHp;
    private String hocKyLabel;
    private BigDecimal diemHe4HienTai;

    private String ngayThi;
    private String caThi;
    private String phongThi;
}
