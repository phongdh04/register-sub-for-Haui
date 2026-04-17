package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Phiếu dự thi theo đăng ký (SBD, được thi / cấm thi).
 */
@Entity
@Table(name = "Phieu_Du_Thi", uniqueConstraints = {
        @UniqueConstraint(name = "uk_phieu_dk", columnNames = "id_dang_ky")
}, indexes = {
        @Index(name = "idx_phieu_lich", columnList = "id_lich_thi"),
        @Index(name = "idx_phieu_dk", columnList = "id_dang_ky")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhieuDuThi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_phieu")
    private Long idPhieu;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lich_thi", nullable = false)
    private LichThi lichThi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dang_ky", nullable = false)
    private DangKyHocPhan dangKy;

    @Column(name = "so_bao_danh", length = 40)
    private String soBaoDanh;

    /**
     * DUOC_THI | BI_CAM_THI | CHUA_CAP
     */
    @Column(name = "trang_thai_du_thi", nullable = false, length = 20)
    @Builder.Default
    private String trangThaiDuThi = "DUOC_THI";

    @Column(name = "ly_do", length = 500)
    private String lyDo;
}
