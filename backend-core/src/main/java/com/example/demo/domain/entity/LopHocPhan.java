package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Bảng Lớp Học Phần (Course Class - Opened for Registration).
 * Đây là Bottleneck chính trong toàn hệ thống: chịu 90% tải lúc ĐKHP.
 *
 * SRP: Chỉ quản lý thông tin lớp, slot, lịch học - không xử lý logic đăng ký.
 * OCP: Lịch học lưu trong JSONB để mở rộng cấu trúc mà không ALTER TABLE.
 * Singleton (trong logic): `si_so_thuc_te` được cập nhật atomic qua Redis DECR (không thay đổi ở entity).
 */
@Entity
@Table(name = "Lop_Hoc_Phan", indexes = {
    @Index(name = "idx_lhp_hoc_ky", columnList = "id_hoc_ky"),
    @Index(name = "idx_lhp_hoc_phan", columnList = "id_hoc_phan"),
    @Index(name = "idx_lhp_giang_vien", columnList = "id_giang_vien")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LopHocPhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lop_hp")
    private Long idLopHp;

    @Column(name = "ma_lop_hp", unique = true, nullable = false, length = 30)
    private String maLopHp; // Vd: "INT2204_1"

    // DIP: Inject qua FK thay vì tự tạo đối tượng
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoc_phan", nullable = false)
    private HocPhan hocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoc_ky", nullable = false)
    private HocKy hocKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_giang_vien")
    private GiangVien giangVien;

    /**
     * Pre-calculated field: Tránh COUNT(*) tốn kém.
     * Số thực tế được cập nhật atomic qua Redis DECR rồi sync về DB.
     */
    @Column(name = "si_so_toi_da", nullable = false)
    private Integer siSoToiDa;

    @Column(name = "si_so_thuc_te")
    @Builder.Default
    private Integer siSoThucTe = 0;

    @Column(name = "hoc_phi", precision = 15, scale = 2)
    private BigDecimal hocPhi;

    /**
     * Trạng thái lớp: DANG_MO, HET_CHO, KHOA, DA_HUY
     */
    @Column(name = "trang_thai", length = 20)
    @Builder.Default
    private String trangThai = "DANG_MO";

    /**
     * JSONB: Lịch học pre-calculated.
     * Format: [{"thu": 2, "tiet": "1-3", "phong": "A.101", "ngay_bat_dau": "2024-09-10", "ngay_ket_thuc": "2024-12-10"}]
     * Cho phép render TKB siêu tốc mà không cần JOIN bảng lịch chi tiết.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "thoi_khoa_bieu_json", columnDefinition = "jsonb")
    private List<Map<String, Object>> thoiKhoaBieuJson;

    @OneToMany(mappedBy = "lopHocPhan", fetch = FetchType.LAZY)
    private List<DangKyHocPhan> dangKys;
}
