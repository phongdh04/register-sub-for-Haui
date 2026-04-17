package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Giao dịch thanh toán học phí / nạp tiền (Task 9 – cổng thanh toán).
 */
@Entity
@Table(name = "Giao_Dich_Thanh_Toan", indexes = {
        @Index(name = "idx_gdtt_sv", columnList = "id_sinh_vien")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaoDichThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_giao_dich")
    private Long idGiaoDich;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sinh_vien", nullable = false)
    private SinhVien sinhVien;

    @Column(name = "so_tien", precision = 15, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Column(name = "noi_dung", length = 500)
    private String noiDung;

    /**
     * MOCK | VNPAY | MOMO
     */
    @Column(name = "provider", length = 20, nullable = false)
    private String provider;

    /**
     * CHO_THANH_TOAN | THANH_CONG | THAT_BAI | HUY
     */
    @Column(name = "trang_thai", length = 30, nullable = false)
    @Builder.Default
    private String trangThai = "CHO_THANH_TOAN";

    @Column(name = "ma_don_hang", nullable = false, unique = true, length = 64)
    private String maDonHang;

    @Column(name = "qr_content", columnDefinition = "TEXT")
    private String qrContent;

    @Column(name = "redirect_url", columnDefinition = "TEXT")
    private String redirectUrl;

    @Column(name = "tao_luc")
    private LocalDateTime taoLuc;

    @PrePersist
    public void prePersist() {
        if (taoLuc == null) {
            taoLuc = LocalDateTime.now();
        }
    }
}
