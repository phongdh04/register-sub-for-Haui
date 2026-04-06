package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class LopHocPhanResponse {
    private Long idLopHp;
    private String maLopHp;

    // Thông tin học phần nhúng vào (tránh client phải gọi thêm API)
    private Long idHocPhan;
    private String maHocPhan;
    private String tenHocPhan;
    private Integer soTinChi;

    // Học kỳ
    private Long idHocKy;
    private String tenHocKy;

    // Giảng viên
    private Long idGiangVien;
    private String tenGiangVien;

    // Sĩ số
    private Integer siSoToiDa;
    private Integer siSoThucTe;
    private Integer siSoConLai; // Computed: siSoToiDa - siSoThucTe

    private BigDecimal hocPhi;
    private String trangThai;
    private List<Map<String, Object>> thoiKhoaBieuJson;
}
