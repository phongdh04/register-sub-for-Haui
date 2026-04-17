package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Giỏ đăng ký trước giờ G — bản nháp theo học kỳ (Task 5).
 */
@Entity
@Table(name = "Gio_Hang_Dang_Ky", uniqueConstraints = {
        @UniqueConstraint(name = "uk_cart_sv_lop_hk", columnNames = {"id_sinh_vien", "id_lop_hp", "id_hoc_ky"})
}, indexes = {
        @Index(name = "idx_ghdk_sv_hk", columnList = "id_sinh_vien,id_hoc_ky")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GioHangDangKy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_gio_hang")
    private Long idGioHang;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sinh_vien", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lop_hp", nullable = false)
    private LopHocPhan lopHocPhan;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoc_ky", nullable = false)
    private HocKy hocKy;

    @Column(name = "ngay_them", nullable = false)
    private LocalDateTime ngayThem;

    @PrePersist
    public void prePersist() {
        if (ngayThem == null) {
            ngayThem = LocalDateTime.now();
        }
    }
}
