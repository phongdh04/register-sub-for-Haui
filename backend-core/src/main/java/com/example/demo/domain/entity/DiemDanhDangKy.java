package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Trạng thái điểm danh của một đăng ký trong một buổi.
 */
@Entity
@Table(name = "Diem_Danh_Dang_Ky", uniqueConstraints = {
        @UniqueConstraint(name = "uk_diem_buoi_dk", columnNames = {"id_buoi", "id_dang_ky"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiemDanhDangKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_diem_danh")
    private Long idDiemDanh;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_buoi", nullable = false)
    private BuoiDiemDanh buoiDiemDanh;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_dang_ky", nullable = false)
    private DangKyHocPhan dangKyHocPhan;

    /** CO_MAT | VANG | PHEP */
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private String trangThai = "VANG";

    @Column(name = "thoi_gian_cap_nhat")
    private LocalDateTime thoiGianCapNhat;
}
