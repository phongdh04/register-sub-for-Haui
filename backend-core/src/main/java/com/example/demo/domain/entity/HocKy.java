package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;

/**
 * Bảng Học Kỳ (Semester/Term).
 * SRP: Quản lý thời gian học kỳ, trạng thái hiện hành duy nhất.
 */
@Entity
@Table(name = "Hoc_Ky")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HocKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoc_ky")
    private Long idHocKy;

    @Column(name = "nam_hoc", nullable = false, length = 20)
    private String namHoc; // Vd: "2024-2025"

    @Column(name = "ky_thu", nullable = false)
    private Integer kyThu; // 1, 2, 3 (kỳ hè)

    @Column(name = "trang_thai_hien_hanh")
    private Boolean trangThaiHienHanh; // Học kỳ đang diễn ra hiện tại

    /**
     * Phiên đăng ký trước giờ G (giỏ nháp): cả hai null = không khóa theo thời gian.
     */
    @Column(name = "pre_dk_mo_tu")
    private Instant preDangKyMoTu;

    @Column(name = "pre_dk_mo_den")
    private Instant preDangKyMoDen;

    /**
     * Đăng ký chính thức (ghi nhận qua Kafka / giờ vàng): cả hai null = không khóa theo thời gian.
     */
    @Column(name = "dk_chinh_thuc_tu")
    private Instant dangKyChinhThucTu;

    @Column(name = "dk_chinh_thuc_den")
    private Instant dangKyChinhThucDen;

    @OneToMany(mappedBy = "hocKy", fetch = FetchType.LAZY)
    private List<LopHocPhan> lopHocPhans;
}
