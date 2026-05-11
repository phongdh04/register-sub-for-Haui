package com.example.demo.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Listener mặc định ghi log audit cho event đăng ký (Sprint 4).
 *
 * <p>Lưu ý: chỉ kích hoạt sau {@link TransactionPhase#AFTER_COMMIT}. Các listener khác
 * (cập nhật cache TKB, push notification, dashboard demand) có thể đăng ký theo cùng pattern.
 */
@Slf4j
@Component
public class RegistrationEventLogger {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onConfirmed(RegistrationConfirmedEvent event) {
        log.info("📣 REGISTRATION_CONFIRMED idempotencyKey={} idDangKy={} svId={} lhpId={} hkId={}",
                event.getIdempotencyKey(), event.getIdDangKy(),
                event.getIdSinhVien(), event.getIdLopHp(), event.getIdHocKy());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCancelled(RegistrationCancelledEvent event) {
        log.info("🗑️ REGISTRATION_CANCELLED idempotencyKey={} idDangKy={} svId={} lhpId={} hkId={}",
                event.getIdempotencyKey(), event.getIdDangKy(),
                event.getIdSinhVien(), event.getIdLopHp(), event.getIdHocKy());
    }
}
