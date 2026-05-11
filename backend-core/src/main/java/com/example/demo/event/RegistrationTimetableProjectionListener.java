package com.example.demo.event;

import com.example.demo.service.IStudentTimetableProjection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Sprint 5 — Listener đồng bộ read-model TKB sinh viên ngay sau khi đăng ký commit.
 *
 * <p>Chạy {@link TransactionPhase#AFTER_COMMIT} → bảo đảm projection luôn nhất quán với DB.
 * Mỗi method gọi tới projection service ({@code REQUIRES_NEW}) — failure ở đây
 * KHÔNG roll back transaction nghiệp vụ (đăng ký đã commit), chỉ log để retry tay/cron.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationTimetableProjectionListener {

    private final IStudentTimetableProjection projection;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onConfirmed(RegistrationConfirmedEvent event) {
        try {
            projection.upsertForRegistration(event.getIdDangKy());
        } catch (Exception ex) {
            log.error("❌ [TKB-Projection] upsert thất bại cho idDangKy={}, idempotencyKey={} — cần rebuild thủ công.",
                    event.getIdDangKy(), event.getIdempotencyKey(), ex);
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCancelled(RegistrationCancelledEvent event) {
        try {
            projection.removeForRegistration(event.getIdDangKy());
        } catch (Exception ex) {
            log.error("❌ [TKB-Projection] remove thất bại cho idDangKy={}, idempotencyKey={} — cần rebuild thủ công.",
                    event.getIdDangKy(), event.getIdempotencyKey(), ex);
        }
    }
}
