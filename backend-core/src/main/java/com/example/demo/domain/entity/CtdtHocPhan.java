package com.example.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Bảng mapping CTĐT ↔ Học phần (Curriculum Courses).
 * SRP: Chỉ mô tả danh sách học phần thuộc một chương trình đào tạo và nhóm kiến thức.
 */
@Entity
@Table(name = "CTDT_Hoc_Phan", indexes = {
        @Index(name = "idx_ctdt_hp_ctdt", columnList = "id_ctdt"),
        @Index(name = "idx_ctdt_hp_hoc_phan", columnList = "id_hoc_phan"),
        @Index(name = "idx_ctdt_hp_khoi", columnList = "khoi_kien_thuc")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CtdtHocPhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_ctdt_hp")
    private Long idCtdtHp;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ctdt", nullable = false)
    private ChuongTrinhDaoTao chuongTrinhDaoTao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoc_phan", nullable = false)
    private HocPhan hocPhan;

    /**
     * Nhóm kiến thức hiển thị trên UI:
     * DAI_CUONG | CO_SO_NGANH | CHUYEN_NGANH | TU_CHON
     */
    @Column(name = "khoi_kien_thuc", length = 40, nullable = false)
    private String khoiKienThuc;

    /**
     * true: bắt buộc; false: tự chọn.
     */
    @Column(name = "bat_buoc", nullable = false)
    @Builder.Default
    private Boolean batBuoc = true;

    /**
     * Học kỳ gợi ý trong lộ trình (1..8). Nullable nếu không áp.
     */
    @Column(name = "hoc_ky_goi_y")
    private Integer hocKyGoiY;
}

