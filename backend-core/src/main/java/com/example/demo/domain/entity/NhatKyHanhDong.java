package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Nhật ký hành động (Task 23 – Audit trail tối thiểu).
 */
@Entity
@Table(name = "Nhat_Ky_Hanh_Dong", indexes = {
        @Index(name = "idx_nkhd_thoi_gian", columnList = "thoi_gian"),
        @Index(name = "idx_nkhd_tk", columnList = "ten_dang_nhap"),
        @Index(name = "idx_nkhd_ma", columnList = "ma_hanh_dong")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhatKyHanhDong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nhat_ky")
    private Long idNhatKy;

    @Column(name = "thoi_gian", nullable = false)
    private LocalDateTime thoiGian;

    @Column(name = "ten_dang_nhap", nullable = false, length = 100)
    private String tenDangNhap;

    @Column(name = "vai_tro", length = 30)
    private String vaiTro;

    @Column(name = "ma_hanh_dong", nullable = false, length = 60)
    private String maHanhDong;

    @Column(name = "mo_ta_ngan", length = 500)
    private String moTaNgan;

    @Column(name = "chi_tiet_json", length = 4000)
    private String chiTietJson;
}
