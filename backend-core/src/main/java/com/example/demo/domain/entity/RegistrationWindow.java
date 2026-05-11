package com.example.demo.domain.entity;

import com.example.demo.domain.enums.RegistrationPhase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Cấu hình cửa sổ thời gian đăng ký theo cohort/ngành cho từng học kỳ.
 *
 * <p>Resolution priority (specific → general):
 * <ol>
 *   <li>(hocKy, phase, namNhapHoc, idNganh) — exact match cho 1 cohort + 1 ngành.</li>
 *   <li>(hocKy, phase, namNhapHoc, NULL) — cohort đó, mọi ngành.</li>
 *   <li>(hocKy, phase, NULL, NULL) — mọi cohort, mọi ngành (override level học kỳ).</li>
 * </ol>
 *
 * <p>Mở/đăng ký chỉ được tính khi có cửa sổ khớp bảng này; mốc thời gian trên {@code HocKy} không thay cửa sổ.</p>
 *
 * <p>{@code namNhapHoc} là cohort identifier (vd 2021 → "K17"). Khớp với {@code Lop.namNhapHoc}.
 * <p>{@code idNganh} null = áp dụng mọi ngành trong cohort.
 */
@Entity
@Table(
        name = "registration_window",
        indexes = {
                @Index(name = "idx_regwin_hk_phase", columnList = "id_hoc_ky, phase"),
                @Index(name = "idx_regwin_open_close", columnList = "open_at, close_at")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationWindow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registration_window")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_hoc_ky", nullable = false)
    private HocKy hocKy;

    @Enumerated(EnumType.STRING)
    @Column(name = "phase", nullable = false, length = 16)
    private RegistrationPhase phase;

    /** Cohort identifier — null nghĩa là áp dụng cho mọi cohort trong học kỳ. */
    @Column(name = "nam_nhap_hoc")
    private Integer namNhapHoc;

    /** Ngành cụ thể — null nghĩa là áp dụng cho mọi ngành trong cohort. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nganh")
    private NganhDaoTao nganhDaoTao;

    @Column(name = "open_at", nullable = false)
    private Instant openAt;

    @Column(name = "close_at", nullable = false)
    private Instant closeAt;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "created_by", length = 100)
    private String createdBy;

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
