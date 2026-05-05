package com.example.demo.domain.entity;

import com.example.demo.domain.enums.GvBusyLoai;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;
import java.time.LocalDate;

/**
 * Khung giờ bận của giảng viên (HARD không được xếp lớp trùng tiết trong HK).
 */
@Entity
@Table(
        name = "gv_busy_slot",
        indexes = {
                @Index(name = "idx_gv_busy_gv_thu", columnList = "id_giang_vien,thu"),
                @Index(name = "idx_gv_busy_hk", columnList = "hoc_ky_id")
        })
@Check(constraints = "thu BETWEEN 2 AND 8 AND tiet_bd <= tiet_kt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GvBusySlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_giang_vien", nullable = false)
    private GiangVien giangVien;

    /** NULL = áp dụng mọi học kỳ (template global). */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_ky_id")
    private HocKy hocKy;

    /**
     * 2–7: Thứ Hai … Thứ Bảy; 8: Chủ nhật (policy HK đặc biệt).
     */
    @Column(name = "thu", nullable = false)
    private Short thu;

    @Column(name = "tiet_bd", nullable = false)
    private Short tietBd;

    @Column(name = "tiet_kt", nullable = false)
    private Short tietKt;

    @Enumerated(EnumType.STRING)
    @Column(name = "loai", nullable = false, length = 20)
    @Builder.Default
    private GvBusyLoai loai = GvBusyLoai.HARD;

    @Column(name = "ly_do", length = 500)
    private String lyDo;

    @Column(name = "ngay_bd")
    private LocalDate ngayBd;

    @Column(name = "ngay_kt")
    private LocalDate ngayKt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
