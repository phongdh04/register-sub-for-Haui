package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Đánh giá giảng viên theo đăng ký học phần (một lần / một học phần đã đăng ký).
 */
@Entity
@Table(name = "Danh_Gia_Giang_Vien", uniqueConstraints = {
        @UniqueConstraint(name = "uk_dggv_dang_ky", columnNames = "id_dang_ky")
}, indexes = {
        @Index(name = "idx_dggv_dk", columnList = "id_dang_ky")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhGiaGiangVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_danh_gia")
    private Long idDanhGia;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dang_ky", nullable = false)
    private DangKyHocPhan dangKy;

    @Column(name = "diem_tong", nullable = false)
    private Integer diemTong;

    @Column(name = "binh_luan", length = 2000)
    private String binhLuan;

    @Column(name = "ngay_tao", nullable = false)
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
    }
}
