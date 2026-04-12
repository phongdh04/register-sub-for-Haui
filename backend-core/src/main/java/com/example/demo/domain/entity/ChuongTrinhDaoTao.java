package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Bảng Chương Trình Đào Tạo (Curriculum).
 * SRP: Mô tả khung chương trình đào tạo, không chứa logic nghiệp vụ.
 */
@Entity
@Table(name = "Chuong_Trinh_Dao_Tao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChuongTrinhDaoTao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ctdt")
    private Long idCtdt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nganh", nullable = false)
    private NganhDaoTao nganhDaoTao;

    @Column(name = "tong_so_tin_chi", nullable = false)
    private Integer tongSoTinChi;

    @Column(name = "muc_tieu", columnDefinition = "TEXT")
    private String mucTieu;

    @Column(name = "thoi_gian_giang_day", length = 50) // Vd: "4 năm"
    private String thoiGianGiangDay;

    @Column(name = "doi_tuong_tuyen_sinh", columnDefinition = "TEXT")
    private String doiTuongTuyenSinh;

    @Column(name = "nam_ap_dung")
    private Integer namApDung; // Áp dụng từ năm học nào
}
