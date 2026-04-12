package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Bảng Sinh Viên - Core (Student Core Profile).
 * SRP: Chỉ chứa thông tin cốt lõi phục vụ 90% truy vấn (TKB, điểm số, đăng ký).
 *      Tách phần hồ sơ chi tiết sang bảng HoSoSinhVien (Vertical Partitioning).
 * DIP: Phụ thuộc vào Lop, CoVanHocTap qua FK (interface/abstraction của JPA).
 */
@Entity
@Table(name = "Sinh_Vien", indexes = {
    @Index(name = "idx_sv_ma", columnList = "ma_sinh_vien"), // Truy vấn nhanh theo MSSV
    @Index(name = "idx_sv_lop", columnList = "id_lop")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SinhVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_sinh_vien")
    private Long idSinhVien;

    @Column(name = "ma_sinh_vien", unique = true, nullable = false, length = 20)
    private String maSinhVien; // MSSV - Indexed

    @Column(name = "ho_ten", nullable = false, length = 200)
    private String hoTen;

    // DIP: Inject mối quan hệ Lop, CoVan qua interface JPA Repository
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lop", nullable = false)
    private Lop lop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_co_van")
    private CoVanHocTap coVanHocTap;

    // Link sang tài khoản đăng nhập
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_khoan_id")
    private User taiKhoan;

    // Hồ sơ chi tiết (Vertical Partitioning - lazy load khi cần)
    @OneToOne(mappedBy = "sinhVien", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private HoSoSinhVien hoSoSinhVien;

    @OneToMany(mappedBy = "sinhVien", fetch = FetchType.LAZY)
    private List<DangKyHocPhan> dangKyHocPhans;
}
