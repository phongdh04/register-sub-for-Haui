package com.example.demo.domain.entity;

import com.example.demo.domain.enums.RegistrationOutcome;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Log idempotency cho luồng đăng ký học phần (Sprint 4).
 *
 * <p>Mỗi request đăng ký mang theo {@code idempotency_key} (mặc định = {@code traceId} từ Kafka message).
 * Service kiểm tra trước khi xử lý — trùng key → trả về kết quả cũ thay vì xử lý lại.
 *
 * <p>UNIQUE INDEX trên {@code idempotency_key} đảm bảo replay không tạo record trùng;
 * trong race condition, DB constraint là tuyến phòng thủ cuối.
 */
@Entity
@Table(
        name = "registration_request_log",
        indexes = {
                @Index(name = "idx_reqlog_sv_lhp_created",
                        columnList = "id_sinh_vien, id_lop_hp, created_at"),
                @Index(name = "idx_reqlog_outcome", columnList = "outcome")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationRequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_log")
    private Long id;

    @Column(name = "idempotency_key", nullable = false, unique = true, length = 128)
    private String idempotencyKey;

    @Column(name = "id_sinh_vien")
    private Long idSinhVien;

    @Column(name = "id_lop_hp")
    private Long idLopHp;

    @Column(name = "id_hoc_ky")
    private Long idHocKy;

    /** Loại request: REGISTER hoặc CANCEL. */
    @Column(name = "request_type", length = 16)
    private String requestType;

    @Enumerated(EnumType.STRING)
    @Column(name = "outcome", length = 32)
    private RegistrationOutcome outcome;

    @Column(name = "id_dang_ky")
    private Long idDangKy;

    @Column(name = "error_code", length = 64)
    private String errorCode;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
    }
}
