package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Bảng Học Kỳ (Semester/Term).
 * SRP: Quản lý thời gian học kỳ, trạng thái hiện hành duy nhất.
 */
@Entity
@Table(name = "Hoc_Ky")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HocKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hoc_ky")
    private Long idHocKy;

    @Column(name = "nam_hoc", nullable = false, length = 20)
    private String namHoc; // Vd: "2024-2025"

    @Column(name = "ky_thu", nullable = false)
    private Integer kyThu; // 1, 2, 3 (kỳ hè)

    @Column(name = "trang_thai_hien_hanh")
    private Boolean trangThaiHienHanh; // Học kỳ đang diễn ra hiện tại

    @OneToMany(mappedBy = "hocKy", fetch = FetchType.LAZY)
    private List<LopHocPhan> lopHocPhans;
}
