package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Lịch thi theo lớp học phần (một lớp mở — một dòng lịch thi cuối kỳ demo).
 */
@Entity
@Table(name = "Lich_Thi", uniqueConstraints = {
        @UniqueConstraint(name = "uk_lich_thi_lop", columnNames = "id_lop_hp")
}, indexes = {
        @Index(name = "idx_lich_thi_lop", columnList = "id_lop_hp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LichThi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_lich_thi")
    private Long idLichThi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lop_hp", nullable = false)
    private LopHocPhan lopHocPhan;

    @Column(name = "lan_thi", nullable = false)
    @Builder.Default
    private Integer lanThi = 1;

    @Column(name = "ngay_thi", nullable = false)
    private LocalDate ngayThi;

    @Column(name = "ca_thi", nullable = false, length = 40)
    private String caThi;

    @Column(name = "gio_bat_dau", length = 10)
    private String gioBatDau;

    @Column(name = "phong_thi", length = 200)
    private String phongThi;
}
