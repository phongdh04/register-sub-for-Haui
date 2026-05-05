package com.example.demo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

/**
 * BACK-TKB-026 — map mỗi LHP vào một nhóm “trùng tiết” trong học kỳ.
 * Các LHP cùng (hocKy, nhomMa) phải xếp cùng slot ở bước enforce.
 */
@Entity
@Table(
        name = "quy_tac_trung_tiet_cohort",
        indexes = {
                @Index(name = "idx_cohort_hk", columnList = "hoc_ky_id"),
                @Index(name = "idx_cohort_hk_nhom", columnList = "hoc_ky_id,nhom_ma"),
                @Index(name = "idx_cohort_lhp", columnList = "id_lop_hp")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CohortConflictRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hoc_ky_id", nullable = false)
    private HocKy hocKy;

    @Column(name = "nhom_ma", nullable = false, length = 80)
    private String nhomMa;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_lop_hp", nullable = false)
    private LopHocPhan lopHocPhan;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
