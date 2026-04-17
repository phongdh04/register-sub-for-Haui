package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Ví nội bộ sinh viên (Task 8) — một ví / một sinh viên.
 */
@Entity
@Table(name = "Vi_Sinh_Vien", indexes = {
        @Index(name = "idx_vi_sv", columnList = "id_sinh_vien", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ViSinhVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vi")
    private Long idVi;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sinh_vien", nullable = false, unique = true)
    private SinhVien sinhVien;

    @Column(name = "so_du", precision = 18, scale = 2, nullable = false)
    @Builder.Default
    private BigDecimal soDu = BigDecimal.ZERO;
}
