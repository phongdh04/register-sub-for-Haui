package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class GradebookRowResponse {
    private Long idDangKy;
    private String maSinhVien;
    private String hoTen;
    private Long idBangDiem;
    private BigDecimal diemHe4;
    private String diemChu;
    /** CHO_CONG_BO | DA_CONG_BO hoặc null (legacy). */
    private String trangThaiBangDiem;
}
