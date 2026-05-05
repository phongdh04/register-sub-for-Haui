package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Một học phần trong báo cáo dự báo (Demand plan line — §6.3).
 */
@Entity
@Table(
        name = "Du_Bao_Mo_Lop_Line",
        indexes = @Index(name = "idx_dbmll_version", columnList = "id_du_bao_version"),
        uniqueConstraints = @UniqueConstraint(name = "uk_dbmll_version_hp", columnNames = { "id_du_bao_version", "id_hoc_phan" }))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DuBaoMoLopLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_du_bao_line")
    private Long idDuBaoLine;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_du_bao_version", nullable = false)
    private DuBaoMoLopVersion duBaoMoLopVersion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoc_phan", nullable = false)
    private HocPhan hocPhan;

    /** Bản copy {@link CtdtHocPhan#getHocKyGoiY()} tại lúc tính. */
    @Column(name = "hoc_ky_goi_y_ctdt", nullable = false)
    private Integer hocKyGoiYCtdt;

    @Column(name = "so_sv_on_track", nullable = false)
    @Builder.Default
    private Integer soSvOnTrack = 0;

    @Column(name = "so_sv_hoc_lai", nullable = false)
    @Builder.Default
    private Integer soSvHocLai = 0;

    @Column(name = "so_sv_du_kien", nullable = false)
    @Builder.Default
    private Integer soSvDuKien = 0;

    @Column(name = "so_lop_de_xuat", nullable = false)
    @Builder.Default
    private Integer soLopDeXuat = 0;
}
