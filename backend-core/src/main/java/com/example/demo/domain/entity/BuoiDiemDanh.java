package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Một buổi điểm danh theo lớp học phần và ngày (Task 16).
 */
@Entity
@Table(name = "Buoi_Diem_Danh", uniqueConstraints = {
        @UniqueConstraint(name = "uk_buoi_lop_ngay", columnNames = {"id_lop_hp", "ngay_buoi"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuoiDiemDanh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_buoi")
    private Long idBuoi;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lop_hp", nullable = false)
    private LopHocPhan lopHocPhan;

    @Column(name = "ngay_buoi", nullable = false)
    private LocalDate ngayBuoi;

    /** Token công khai (QR) để sinh viên gửi check-in; không chứa JWT. */
    @Column(name = "public_token", nullable = false, unique = true, length = 64)
    private String publicToken;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "buoiDiemDanh", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DiemDanhDangKy> chiTiets;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
