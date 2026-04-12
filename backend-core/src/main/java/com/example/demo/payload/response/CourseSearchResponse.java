package com.example.demo.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Response DTO trả về kết quả tìm kiếm lớp học phần.
 * Nhúng đầy đủ thông tin để Frontend không phải gọi thêm API (BFF style).
 *
 * SRP: Chỉ là view model trả về Client, không chứa logic nghiệp vụ.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseSearchResponse {

    // ── Thông tin Lớp Học Phần ─────────────────────────────────
    private Long idLopHp;
    private String maLopHp;
    private String trangThai;

    // ── Thông tin Học Phần (môn học) ───────────────────────────
    private Long idHocPhan;
    private String maHocPhan;
    private String tenHocPhan;
    private String maIn;         // Mã in trên TKB
    private Integer soTinChi;
    private String loaiMon;      // BAT_BUOC, TU_CHON, DAI_CUONG, CHUYEN_NGANH

    // ── Thông tin Học Kỳ ───────────────────────────────────────
    private Long idHocKy;
    private String tenHocKy;

    // ── Thông tin Giảng Viên ────────────────────────────────────
    private Long idGiangVien;
    private String tenGiangVien;
    private String emailGiangVien;

    // ── Sĩ số (frontend render progress bar) ────────────────────
    private Integer siSoToiDa;
    private Integer siSoThucTe;
    private Integer siSoConLai;   // Computed: siSoToiDa - siSoThucTe
    private Double phanTramDay;   // Computed: (siSoThucTe * 100.0 / siSoToiDa) để render fill %

    // ── Học phí & Lịch học ─────────────────────────────────────
    private BigDecimal hocPhi;
    private List<Map<String, Object>> thoiKhoaBieuJson;  // JSONB pre-calculated

    // ── Điều kiện ràng buộc (SV tự kiểm tra trước khi bấm Đăng Ký) ──
    private Map<String, Object> dieuKienRangBuocJson;    // {"tien_quyet": [...], ...}

    /**
     * Helper: true nếu lớp còn chỗ đăng ký (dùng để disable button Đăng Ký phía Frontend).
     */
    public boolean isConCho() {
        return siSoConLai != null && siSoConLai > 0;
    }
}
