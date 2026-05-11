package com.example.demo.service.impl;

import com.example.demo.domain.entity.RegistrationRequestLog;
import com.example.demo.domain.enums.RegistrationOutcome;
import com.example.demo.repository.RegistrationRequestLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Idempotency log writer (Sprint 4).
 *
 * <p>Tách thành service riêng để có thể chạy với {@link Propagation#REQUIRES_NEW},
 * bảo đảm record audit luôn được commit độc lập (kể cả khi transaction nghiệp vụ rollback).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RegistrationIdempotencyService {

    private final RegistrationRequestLogRepository repo;

    /**
     * Lấy outcome đã ghi (nếu có) cho 1 idempotency key.
     * Đọc trong transaction READ-only riêng để tránh phantom data từ TX hiện tại.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public Optional<RegistrationRequestLog> findByKey(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            return Optional.empty();
        }
        return repo.findByIdempotencyKey(idempotencyKey);
    }

    /**
     * Ghi log đăng ký. Chạy ở TX riêng → audit luôn commit dù caller rollback.
     * Trùng key → bỏ qua (UNIQUE đã chặn ở DB).
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void writeLog(String idempotencyKey,
                         Long idSinhVien,
                         Long idLopHp,
                         Long idHocKy,
                         String requestType,
                         RegistrationOutcome outcome,
                         Long idDangKy,
                         String errorCode,
                         String errorMessage) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            log.debug("Bỏ qua ghi log idempotency vì key rỗng (svId={}, lhpId={})", idSinhVien, idLopHp);
            return;
        }
        try {
            RegistrationRequestLog row = RegistrationRequestLog.builder()
                    .idempotencyKey(idempotencyKey)
                    .idSinhVien(idSinhVien)
                    .idLopHp(idLopHp)
                    .idHocKy(idHocKy)
                    .requestType(requestType)
                    .outcome(outcome)
                    .idDangKy(idDangKy)
                    .errorCode(errorCode)
                    .errorMessage(truncate(errorMessage, 1000))
                    .build();
            repo.save(row);
        } catch (DataIntegrityViolationException ex) {
            log.warn("⚠️ Idempotency key đã tồn tại khi ghi log (key={}). Bỏ qua.", idempotencyKey);
        }
    }

    private static String truncate(String s, int max) {
        if (s == null) {
            return null;
        }
        return s.length() <= max ? s : s.substring(0, max);
    }
}
