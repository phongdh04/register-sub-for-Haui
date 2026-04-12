package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Bảng Hồ Sơ Sinh Viên Chi Tiết (Student Profile).
 *
 * Vertical Partitioning: Tách riêng thông tin chi tiết ít truy xuất (CCCD, Ngân hàng, BHYT)
 * ra khỏi bảng Sinh_Vien lõi để tăng tốc độ quét khi chỉ cần thông tin căn bản.
 *
 * SRP: Chỉ chứa thông tin nhân thân, không có logic nghiệp vụ.
 */
@Entity
@Table(name = "Ho_So_Sinh_Vien")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HoSoSinhVien {

    @Id
    @Column(name = "id_sinh_vien") // Shared PK với Sinh_Vien (1-1 mapping)
    private Long idSinhVien;

    // Liên kết 1-1 với Sinh Viên (PK = FK)
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "id_sinh_vien")
    private SinhVien sinhVien;

    // Thông tin liên lạc
    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "sdt", length = 20)
    private String sdt;

    @Column(name = "dia_chi", length = 500)
    private String diaChi;

    @Column(name = "ngay_sinh")
    private java.time.LocalDate ngaySinh;

    @Column(name = "gioi_tinh", length = 10)
    private String gioiTinh;

    @Column(name = "quoc_tich", length = 100)
    private String quocTich;

    @Column(name = "dan_toc", length = 100)
    private String danToc;

    @Column(name = "ton_giao", length = 100)
    private String tonGiao;

    // Giấy tờ tùy thân
    @Column(name = "so_cccd", length = 20)
    private String soCccd;

    @Column(name = "ngay_cap_cccd")
    private java.time.LocalDate ngayCapCccd;

    @Column(name = "noi_cap_cccd", length = 200)
    private String noiCapCccd;

    @Column(name = "url_anh_cccd_truoc", length = 500)
    private String urlAnhCccdTruoc;

    @Column(name = "url_anh_cccd_sau", length = 500)
    private String urlAnhCccdSau;

    @Column(name = "ma_the_bhyt", length = 50)
    private String maTheBhyt;

    // Thông tin ngân hàng
    @Column(name = "ten_ngan_hang", length = 100)
    private String tenNganHang;

    @Column(name = "so_tk_ngan_hang", length = 30)
    private String soTkNganHang;
}
