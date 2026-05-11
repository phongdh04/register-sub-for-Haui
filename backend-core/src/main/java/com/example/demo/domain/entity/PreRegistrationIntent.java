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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Nguyện vọng đăng ký dự kiến của sinh viên (pha A — pre-registration intent).
 *
 * <p>Khác với {@code GioHangDangKy} (giỏ trong phiên đăng ký chính thức),
 * {@code PreRegistrationIntent} thu thập nhu cầu trước khi mở lớp,
 * dùng để tổng hợp demand và quyết định số lớp cần mở.
 *
 * <p>Quy ước:
 * <ul>
 *   <li>1 sinh viên chỉ có 1 intent / 1 học phần / 1 học kỳ (unique constraint).</li>
 *   <li>{@code priority} 1..n cho phép xếp thứ tự ưu tiên giữa các môn (1 = cao nhất).</li>
 *   <li>Không cấp slot lớp ở pha này.</li>
 * </ul>
 */
@Entity
@Table(
        name = "pre_registration_intent",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_prereg_intent_sv_hk_hp",
                        columnNames = {"id_sinh_vien", "id_hoc_ky", "id_hoc_phan"}
                )
        },
        indexes = {
                @Index(name = "idx_prereg_intent_hk_hp", columnList = "id_hoc_ky, id_hoc_phan"),
                @Index(name = "idx_prereg_intent_sv_hk", columnList = "id_sinh_vien, id_hoc_ky")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreRegistrationIntent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_intent")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_sinh_vien", nullable = false)
    private SinhVien sinhVien;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoc_ky", nullable = false)
    private HocKy hocKy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoc_phan", nullable = false)
    private HocPhan hocPhan;

    /** 1 = ưu tiên cao nhất. Mặc định 1 nếu sinh viên không chỉ định. */
    @Column(name = "priority", nullable = false)
    @Builder.Default
    private Integer priority = 1;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    void onCreate() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = Instant.now();
    }
}
