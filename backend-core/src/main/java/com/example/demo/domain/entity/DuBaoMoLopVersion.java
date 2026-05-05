package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Một lần “chạy dự báo” mở lớp (BACK-TKB-013) — gom các dòng {@link DuBaoMoLopLine}.
 */
@Entity
@Table(name = "Du_Bao_Mo_Lop_Version", indexes = {
        @Index(name = "idx_dbmlv_hk_created", columnList = "id_hoc_ky,created_at")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DuBaoMoLopVersion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_du_bao_version")
    private Long idDuBaoVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoc_ky", nullable = false)
    private HocKy hocKy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_ctdt", nullable = false)
    private ChuongTrinhDaoTao chuongTrinhDaoTao;

    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private String trangThai = "DRAFT";

    @Column(name = "si_so_mac_dinh", nullable = false)
    private Integer siSoMacDinh;

    @Column(name = "he_so_du_phong", nullable = false, precision = 6, scale = 3)
    private BigDecimal heSoDuPhong;

    @Column(name = "ty_le_sv_hoc_lai", nullable = false, precision = 6, scale = 3)
    private BigDecimal tyLeSvHocLai;

    /** Năm bắt đầu của chuỗi {@link HocKy#getNamHoc()} tại thời điểm chạy (§6.2 quy chiếu kỳ logic). */
    @Column(name = "nam_hoc_nam_ke", nullable = false)
    private Short namHocNamKe;

    @Column(name = "ky_thu_muc_tieu", nullable = false)
    private Integer kyThuMucTieu;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "duBaoMoLopVersion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DuBaoMoLopLine> lines;

    @PrePersist
    void prePersist() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (trangThai == null) {
            trangThai = "DRAFT";
        }
    }
}
