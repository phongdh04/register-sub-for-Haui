package com.example.demo.domain.entity;

import com.example.demo.domain.enums.RegistrationPhase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Chiến dịch đăng ký theo khóa (cohort).
 *
 * <p>Admin tạo 1 campaign cho (K17, OFFICIAL) → hệ thống tự tạo
 * {@link RegistrationWindow} cho từng {@link HocKy} phù hợp với khóa đó.
 * Nếu khóa K17 nhập 2021 (năm thứ 1), hệ thống sẽ tạo windows cho HK1, HK2
 * trong năm học hiện tại.
 *
 * <p>Backward-compatible: campaign là optional. Admin vẫn có thể tạo
 * {@code RegistrationWindow} thủ công không qua campaign (cách cũ).
 */
@Entity
@Table(name = "registration_campaign")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationCampaign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_registration_campaign")
    private Long id;

    @Column(name = "ten_campaign", nullable = false, length = 200)
    private String tenCampaign;

    /** Năm nhập học — cohort identifier (vd 2021 = K17). */
    @Column(name = "nam_nhap_hoc", nullable = false)
    private Integer namNhapHoc;

    @Enumerated(EnumType.STRING)
    @Column(name = "phase", nullable = false, length = 16)
    private RegistrationPhase phase;

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

    @OneToMany(mappedBy = "campaign", orphanRemoval = true)
    @Builder.Default
    private List<RegistrationWindow> windows = new ArrayList<>();

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
