package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Bảng điểm theo từng lần đăng ký học phần (Task 4 – Transcript).
 * Một dòng đăng ký thành công tối đa một bản ghi điểm.
 */
@Entity
@Table(name = "Bang_Diem_Mon", indexes = {
        @Index(name = "idx_bdm_dang_ky", columnList = "id_dang_ky", unique = true)
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BangDiemMon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_bang_diem")
    private Long idBangDiem;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dang_ky", nullable = false, unique = true)
    private DangKyHocPhan dangKyHocPhan;

    /**
     * Điểm hệ 4 (0–4), dùng tính GPA.
     */
    @Column(name = "diem_he_4", precision = 4, scale = 2)
    private BigDecimal diemHe4;

    @Column(name = "diem_chu", length = 5)
    private String diemChu;

    /**
     * CHO_CONG_BO | DA_CONG_BO
     */
    @Column(name = "trang_thai", length = 20)
    @Builder.Default
    private String trangThai = "DA_CONG_BO";
}
