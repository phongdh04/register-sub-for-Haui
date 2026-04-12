package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Bảng Đăng Ký Học Phần (Course Registration).
 * SRP: Chỉ ghi nhận kết quả đăng ký, không xử lý logic validate hay thanh toán.
 * Partition gợi ý: Cần Partition by id_hoc_ky khi bảng lớn (>1 triệu records).
 */
@Entity
@Table(name = "Dang_Ky_Hoc_Phan", indexes = {
    @Index(name = "idx_dkhp_sv", columnList = "id_sinh_vien"),
    @Index(name = "idx_dkhp_hoc_ky", columnList = "id_hoc_ky"),
    @Index(name = "idx_dkhp_sv_hp", columnList = "id_sinh_vien,id_lop_hp", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DangKyHocPhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dang_ky")
    private Long idDangKy;

    // DIP: Liên kết qua FK
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_sinh_vien", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lop_hp", nullable = false)
    private LopHocPhan lopHocPhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoc_ky", nullable = false)
    private HocKy hocKy;

    @Column(name = "ngay_dang_ky")
    private LocalDateTime ngayDangKy;

    /**
     * Trạng thái đăng ký:
     * THANH_CONG, CHO_DUYET, RUT_MON, HUY_BO
     */
    @Column(name = "trang_thai_dang_ky", length = 30)
    @Builder.Default
    private String trangThaiDangKy = "THANH_CONG";

    @PrePersist
    public void prePersist() {
        if (ngayDangKy == null) {
            ngayDangKy = LocalDateTime.now();
        }
    }
}
