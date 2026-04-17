package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Sổ giao dịch ví (nạp từ thanh toán, sau này có thể trừ học phí…).
 */
@Entity
@Table(name = "Giao_Dich_Vi", indexes = {
        @Index(name = "idx_gdv_vi", columnList = "id_vi"),
        @Index(name = "idx_gdv_gdtt", columnList = "id_giao_dich_thanh_toan", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaoDichVi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giao_dich_vi")
    private Long idGiaoDichVi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_vi", nullable = false)
    private ViSinhVien viSinhVien;

    /** Ví dụ: NAP_TU_THANH_TOAN */
    @Column(name = "loai", nullable = false, length = 40)
    private String loai;

    @Column(name = "so_tien", precision = 18, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Column(name = "so_du_sau", precision = 18, scale = 2, nullable = false)
    private BigDecimal soDuSau;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_giao_dich_thanh_toan", unique = true)
    private GiaoDichThanhToan giaoDichThanhToan;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "thoi_gian", nullable = false)
    private LocalDateTime thoiGian;

    @PrePersist
    public void prePersist() {
        if (thoiGian == null) {
            thoiGian = LocalDateTime.now();
        }
    }
}
