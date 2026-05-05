package com.example.demo.domain.entity;

import com.example.demo.domain.enums.NotificationQueueStatus;
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
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.Map;

/** BACK-TKB-031 — hàng đợi thông báo stub cho contract SCHEDULE_CHANGED. */
@Entity
@Table(
        name = "notification_queue",
        indexes = {
                @Index(name = "idx_notification_queue_status", columnList = "trang_thai"),
                @Index(name = "idx_notification_queue_event", columnList = "event_type"),
                @Index(name = "idx_notification_queue_hk", columnList = "hoc_ky_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoc_ky_id")
    private HocKy hocKy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "change_set_id")
    private ScheduleChangeSet changeSet;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> payload;

    @Enumerated(EnumType.STRING)
    @Column(name = "trang_thai", nullable = false, length = 20)
    @Builder.Default
    private NotificationQueueStatus trangThai = NotificationQueueStatus.PENDING;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "sent_at")
    private Instant sentAt;
}
