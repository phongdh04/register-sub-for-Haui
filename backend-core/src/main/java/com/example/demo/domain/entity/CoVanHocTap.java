package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Bảng Cố Vấn Học Tập (Academic Advisor).
 * SRP: Quản lý thông tin cố vấn, không kiêm logic kiểm tra điểm SV.
 */
@Entity
@Table(name = "Co_Van_Hoc_Tap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoVanHocTap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_co_van")
    private Long idCoVan;

    @Column(name = "ten_co_van", nullable = false, length = 200)
    private String tenCoVan;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "email", length = 255)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khoa")
    private Khoa khoa;

    @OneToMany(mappedBy = "coVanHocTap", fetch = FetchType.LAZY)
    private List<SinhVien> sinhViens;
}
