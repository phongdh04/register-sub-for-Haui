package com.example.demo.domain.entity;

import com.example.demo.domain.enums.ScheduleChangeSetStatus;
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
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

/**
 * BACK-TKB-029 — payload chỉnh sửa TKB ở cấp học kỳ trước khi apply.
 */
@Entity
@Table(
        name = "schedule_change_set",
        indexes = {
                @Index(name = "idx_schedule_change_set_hk", columnList = "hoc_ky_id"),
                @Index(name = "idx_schedule_change_set_status", columnList = "trang_thai")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleChangeSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hoc_ky_id", nullable = false)
    private HocKy hocKy;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private ScheduleChangeSetStatus trangThai = ScheduleChangeSetStatus.PENDING;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload_delta", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payloadDelta;

    @Column(name = "ghi_chu", length = 1000)
    private String ghiChu;

    @Column(name = "requested_by", length = 120)
    private String requestedBy;

    @Column(name = "reviewed_by", length = 120)
    private String reviewedBy;

    @Column(name = "approved_at")
    private Instant approvedAt;

    @Column(name = "applied_at")
    private Instant appliedAt;

    @Column(name = "effective_version_no")
    private Long effectiveVersionNo;

    @Column(name = "affected_sv_count", nullable = false)
    @Builder.Default
    private Integer affectedSvCount = 0;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "affected_sv_ids", columnDefinition = "jsonb")
    private java.util.List<Long> affectedSvIds;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}
