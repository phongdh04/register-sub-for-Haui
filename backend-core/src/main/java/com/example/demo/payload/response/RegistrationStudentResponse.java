package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Ket qua sau khi sinh vien dang ky / huy 1 lop, tra ve toan bo danh sach
 * dang dang ky trong hoc ky de UI render lai khong can goi them GET.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationStudentResponse {

    private Long idHocKy;
    private String tenHocKy;
    private Integer tongSoMon;
    private Integer tongTinChi;
    private BigDecimal tongHocPhi;
    private List<RegisteredItem> items;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RegisteredItem {
        private Long idDangKy;
        private Long idLopHp;
        private String maLopHp;
        private Long idHocPhan;
        private String maHocPhan;
        private String tenHocPhan;
        private Integer soTinChi;
        private String tenGiangVien;
        private Integer siSoToiDa;
        private Integer siSoThucTe;
        private BigDecimal hocPhi;
        private String trangThaiDangKy;
        private LocalDateTime ngayDangKy;
    }
}
