package com.example.demo.event;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

/**
 * Event phát ra khi sinh viên hủy đăng ký thành công (Sprint 4).
 */
@Getter
@Builder
public class RegistrationCancelledEvent {

    private final String idempotencyKey;
    private final String traceId;
    private final Long idDangKy;
    private final Long idSinhVien;
    private final Long idLopHp;
    private final Long idHocKy;
    private final Instant occurredAt;
}
