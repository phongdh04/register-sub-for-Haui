package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class PreRegCartResponse {
    private Long idHocKy;
    private String hocKyLabel;
    private int tongSoMon;
    private int tongTinChi;
    private BigDecimal tongHocPhi;
    /** Số cặp lớp trong giỏ có TKB trùng nhau. */
    private int soDoiTrungLichTrongGioHang;
    private boolean coTrungLichVoiDangKyChinhThuc;
    private List<PreRegCartItemResponse> items;

    /** Lịch đăng ký (theo học kỳ của giỏ) — tiện cho UI sinh viên. */
    private Instant preDangKyMoTu;
    private Instant preDangKyMoDen;
    private Instant dangKyChinhThucTu;
    private Instant dangKyChinhThucDen;
    private boolean preDangKyDangMo;
    private boolean dangKyChinhThucDangMo;
}
