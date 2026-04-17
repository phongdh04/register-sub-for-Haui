package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Yêu cầu phúc khảo / khiếu nại điểm (Task 18).
 */
@Entity
@Table(name = "Yeu_Cau_Phuc_Khao", indexes = {
        @Index(name = "idx_yc_pk_dk", columnList = "id_dang_ky"),
        @Index(name = "idx_yc_pk_tt", columnList = "trang_thai")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class YeuCauPhucKhao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_yeu_cau")
    private Long idYeuCau;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dang_ky", nullable = false)
    private DangKyHocPhan dangKy;

    /** Snapshot điểm hệ 4 lúc SV nộp (minh chứng). */
    @Column(name = "diem_he_4_luc_nop", precision = 4, scale = 2)
    private BigDecimal diemHe4LucNop;

    @Column(name = "ly_do_sinh_vien", nullable = false, length = 2000)
    private String lyDoSinhVien;

    /**
     * CHO_GV_XU_LY | DONG_Y | TU_CHOI
     */
    @Column(name = "trang_thai", nullable = false, length = 30)
    @Builder.Default
    private String trangThai = "CHO_GV_XU_LY";

    @Column(name = "ghi_chu_giang_vien", length = 2000)
    private String ghiChuGiangVien;

    @Column(name = "diem_sau_xu_ly", precision = 4, scale = 2)
    private BigDecimal diemSauXuLy;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "ngay_xu_ly")
    private LocalDateTime ngayXuLy;
}
