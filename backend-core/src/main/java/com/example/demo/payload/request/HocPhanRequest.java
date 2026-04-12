package com.example.demo.payload.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * DTO Request tạo Học Phần.
 * thuocTinhJson và dieuKienRangBuocJson nhận dạng Map<String, Object>
 * để giữ nguyên tính linh hoạt của JSONB.
 */
@Data
public class HocPhanRequest {
    @NotBlank(message = "Mã học phần không được để trống")
    private String maHocPhan;

    @NotBlank(message = "Tên học phần không được để trống")
    private String tenHocPhan;

    private String maIn;

    @NotNull
    @Min(1)
    private Integer soTinChi;

    private String loaiMon; // BAT_BUOC, TU_CHON

    /**
     * Các thuộc tính mở rộng (JSON).
     * Vd: {"bo_mon": "CNTT", "mo_ta": "...", "chuan_dau_ra": [...]}
     */
    private Map<String, Object> thuocTinhJson;

    /**
     * Điều kiện ràng buộc (JSON).
     * Vd: {"tien_quyet": ["CS101"], "song_hanh": [], "thay_the": [], "tuong_duong": []}
     */
    private Map<String, Object> dieuKienRangBuocJson;
}
