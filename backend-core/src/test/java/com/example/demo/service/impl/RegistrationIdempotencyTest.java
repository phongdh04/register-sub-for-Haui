package com.example.demo.service.impl;

import com.example.demo.domain.entity.RegistrationRequestLog;
import com.example.demo.domain.enums.RegistrationOutcome;
import com.example.demo.payload.request.RegistrationMessageDto;
import com.example.demo.repository.DangKyHocPhanRepository;
import com.example.demo.repository.HocKyRepository;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.SinhVienRepository;
import com.example.demo.service.validation.handler.DuplicateRegistrationHandler;
import com.example.demo.service.validation.handler.PrerequisiteCourseHandler;
import com.example.demo.service.validation.handler.ScheduleConflictHandler;
import com.example.demo.support.RegistrationScheduleChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Sprint 4 — Idempotency cho luồng đăng ký.
 * Tập trung vào behaviour: replay với cùng idempotency key được short-circuit ngay đầu hàm,
 * không gọi tới validation chain hay repository nghiệp vụ.
 */
@ExtendWith(MockitoExtension.class)
class RegistrationIdempotencyTest {

    @Mock private DangKyHocPhanRepository dangKyHocPhanRepository;
    @Mock private LopHocPhanRepository    lopHocPhanRepository;
    @Mock private HocKyRepository         hocKyRepository;
    @Mock private SinhVienRepository      sinhVienRepository;
    @Mock private DuplicateRegistrationHandler duplicateRegistrationHandler;
    @Mock private ScheduleConflictHandler      scheduleConflictHandler;
    @Mock private PrerequisiteCourseHandler    prerequisiteCourseHandler;
    @Mock private RegistrationScheduleChecker  registrationScheduleChecker;
    @Mock private RegistrationIdempotencyService idempotencyService;
    @Mock private ApplicationEventPublisher    eventPublisher;

    @InjectMocks
    private DangKyHocPhanServiceImpl service;

    @BeforeEach
    void setUp() {
        when(duplicateRegistrationHandler.setNext(scheduleConflictHandler))
                .thenReturn(scheduleConflictHandler);
        when(scheduleConflictHandler.setNext(prerequisiteCourseHandler))
                .thenReturn(prerequisiteCourseHandler);
        service.buildValidationChain();
    }

    @Test
    void replay_with_success_returnsTrueWithoutReprocessing() {
        RegistrationMessageDto msg = sample();
        RegistrationRequestLog prior = RegistrationRequestLog.builder()
                .idempotencyKey(msg.getTraceId())
                .outcome(RegistrationOutcome.SUCCESS)
                .build();
        when(idempotencyService.findByKey(msg.getTraceId())).thenReturn(Optional.of(prior));

        boolean ok = service.processRegistration(msg);

        assertThat(ok).isTrue();
        verify(hocKyRepository, never()).findById(anyLong());
        verify(sinhVienRepository, never()).findById(anyLong());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void replay_with_failedOutcome_returnsFalseWithoutReprocessing() {
        RegistrationMessageDto msg = sample();
        RegistrationRequestLog prior = RegistrationRequestLog.builder()
                .idempotencyKey(msg.getTraceId())
                .outcome(RegistrationOutcome.FULL)
                .build();
        when(idempotencyService.findByKey(msg.getTraceId())).thenReturn(Optional.of(prior));

        boolean ok = service.processRegistration(msg);

        assertThat(ok).isFalse();
        verify(hocKyRepository, never()).findById(anyLong());
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void rejected_when_hocKyMissing_writesLogAndShortCircuits() {
        RegistrationMessageDto msg = sample();
        when(idempotencyService.findByKey(msg.getTraceId())).thenReturn(Optional.empty());
        when(hocKyRepository.findById(msg.getIdHocKy())).thenReturn(Optional.empty());

        boolean ok = service.processRegistration(msg);

        assertThat(ok).isFalse();
        verify(idempotencyService).writeLog(
                msg.getTraceId(), msg.getIdSinhVien(), msg.getIdLopHp(),
                msg.getIdHocKy(), "REGISTER",
                RegistrationOutcome.REJECTED, null,
                "HOC_KY_NOT_FOUND", "Học kỳ không tồn tại");
        verify(eventPublisher, never()).publishEvent(any());
    }

    private RegistrationMessageDto sample() {
        return RegistrationMessageDto.builder()
                .traceId("DKHP-1-2-12345")
                .idSinhVien(1L)
                .idLopHp(2L)
                .idHocKy(3L)
                .build();
    }
}
