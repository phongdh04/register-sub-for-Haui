package com.example.demo.payload.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Gợi ý lớp mở cho đăng ký theo khóa (năm nhập học) + học kỳ đăng ký và CTĐT (hoc_ky_goi_y).
 */
@Data
@Builder
public class RegistrationSuggestionResponse {

    private Integer namNhapHoc;
    private String maLop;
    private String tenLop;
    /** Nhãn khóa kiểu K21 từ năm nhập học. */
    private String nhanKhoa;

    private Long idHocKy;
    private String hocKyLabel;
    private Integer kyThuDangKy;
    private String namHocDangKy;

    /**
     * Thứ tự học kỳ trong CTĐT dùng để lọc {@code hoc_ky_goi_y} (ước lượng 2 kỳ/năm).
     */
    private int thuTuHocKyUocLuong;

    private String moTaCachTinh;

    private List<CourseSearchResponse> lopDeXuat;
}
