package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Bảng Lớp Hành Chính (Administrative Class / Cohort).
 * SRP: Quản lý lớp hành chính, không kiêm logic lớp học phần.
 */
@Entity
@Table(name = "Lop")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Lop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lop")
    private Long idLop;

    @Column(name = "ma_lop", unique = true, nullable = false, length = 20)
    private String maLop;

    @Column(name = "ten_lop", nullable = false, length = 100)
    private String tenLop;

    @Column(name = "nam_nhap_hoc")
    private Integer namNhapHoc; // Vd: 2021

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nganh", nullable = false)
    private NganhDaoTao nganhDaoTao;

    @OneToMany(mappedBy = "lop", fetch = FetchType.LAZY)
    private List<SinhVien> sinhViens;
}
