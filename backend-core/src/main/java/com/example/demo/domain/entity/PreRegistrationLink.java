package com.example.demo.domain.entity;

import com.example.demo.domain.enums.PreRegistrationLinkStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pre_registration_link", indexes = {
        @Index(name = "idx_prereg_link_expires", columnList = "expires_at"),
        @Index(name = "idx_prereg_link_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreRegistrationLink {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private PreRegistrationLinkStatus status = PreRegistrationLinkStatus.ACTIVE;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "max_submissions", nullable = false)
    private Integer maxSubmissions;

    @Column(name = "submitted_count")
    @Builder.Default
    private Integer submittedCount = 0;

    @Column(name = "intake_code", nullable = false, length = 50)
    private String intakeCode;

    @Column(name = "campus_code", length = 50)
    private String campusCode;

    @Column(name = "allow_domains", columnDefinition = "TEXT")
    private String allowDomains;

    @Column(name = "rate_limit_profile", length = 50)
    private String rateLimitProfile;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
