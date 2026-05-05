package com.example.demo.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DTO Request tạo/cập nhật Lớp Học Phần.
 * thoiKhoaBieuJson nhận List<Map> tương ứng JSONB trong DB.
 */
@Data
public class LopHocPhanRequest {
    @NotBlank(message = "Mã lớp học phần không được để trống")
    private String maLopHp;

    @NotNull(message = "ID học phần không được để trống")
    private Long idHocPhan;

    @NotNull(message = "ID học kỳ không được để trống")
    private Long idHocKy;

    private Long idGiangVien;

    @NotNull
    @Min(1)
    private Integer siSoToiDa;

    private BigDecimal hocPhi;

    /** FK master phòng (TKB Phase 1). Null = không gán hoặc (PUT) không đổi nếu thiết kế client gửi null. */
    private Long idPhongHoc;

    /** Gói TKB BACK-TKB-004 — null giữ/ghi nhận không gán như các FK khác. */
    private Long idTkbBlock;

    /**
     * Lịch học JSON array.
     * Vd: [{"thu": 2, "tiet": "1-3", "phong": "A.101", "ngay_bat_dau": "2024-09-10", "ngay_ket_thuc": "2024-12-10"}]
     */
    private List<Map<String, Object>> thoiKhoaBieuJson;
}
