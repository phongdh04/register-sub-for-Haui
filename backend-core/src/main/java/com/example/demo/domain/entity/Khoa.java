package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Bảng Khoa (Department).
 * SRP: Chỉ chứa thông tin về Khoa, không kiêm thêm logic nghiệp vụ.
 */
@Entity
@Table(name = "Khoa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Khoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_khoa")
    private Long idKhoa;

    @Column(name = "ma_khoa", unique = true, nullable = false, length = 20)
    private String maKhoa;

    @Column(name = "ten_khoa", nullable = false, length = 200)
    private String tenKhoa;

    @Column(name = "mo_ta", columnDefinition = "TEXT")
    private String moTa;

    // ISP: Mối quan hệ 1-N với Nganh, Giang Vien - chỉ dùng khi cần thiết
    @OneToMany(mappedBy = "khoa", fetch = FetchType.LAZY)
    private List<NganhDaoTao> nganhs;

    @OneToMany(mappedBy = "khoa", fetch = FetchType.LAZY)
    private List<GiangVien> giangViens;
}
