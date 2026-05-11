package com.example.demo.event;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Event phát ra sau khi 1 lần đăng ký học phần được commit thành công (Sprint 4).
 *
 * <p>Sử dụng Spring {@code ApplicationEventPublisher} (in-process) để giữ luồng đơn giản;
 * có thể swap sang Kafka producer trong tương lai mà không phá interface listener.
 *
 * <p>Đăng ký kèm chế độ {@code @TransactionalEventListener(phase = AFTER_COMMIT)} để bảo đảm
 * read-model (timetable, dashboard) chỉ cập nhật khi DB đã chốt.
 */
@Getter
@Builder
public class RegistrationConfirmedEvent {

    private final String idempotencyKey;
    private final String traceId;
    private final Long idDangKy;
    private final Long idSinhVien;
    private final Long idLopHp;
    private final Long idHocKy;
    private final Instant occurredAt;
}
