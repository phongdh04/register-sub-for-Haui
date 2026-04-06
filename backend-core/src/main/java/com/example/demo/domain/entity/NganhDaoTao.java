package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * Bảng Ngành Đào Tạo (Major / Program).
 * SRP: Chỉ mô tả ngành đào tạo, không có logic tính toán.
 * DIP: Phụ thuộc vào Khoa thông qua FK (Abstraction - quan hệ ORM), không tự tạo Khoa mới.
 */
@Entity
@Table(name = "Nganh_Dao_Tao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NganhDaoTao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nganh")
    private Long idNganh;

    @Column(name = "ma_nganh", unique = true, nullable = false, length = 20)
    private String maNganh;

    @Column(name = "ten_nganh", nullable = false, length = 200)
    private String tenNganh;

    @Column(name = "he_dao_tao", length = 50)
    private String heDaoTao; // Đại Trà, CLC, Tài Năng

    // DIP: Inject mối quan hệ qua FK (không new Khoa() trực tiếp)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khoa", nullable = false)
    private Khoa khoa;

    // LSP: Link sang ChuongTrinhDaoTao để tra cứu khung chương trình
    @OneToMany(mappedBy = "nganhDaoTao", fetch = FetchType.LAZY)
    private List<ChuongTrinhDaoTao> chuongTrinhs;
}
