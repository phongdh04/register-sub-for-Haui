package com.example.demo.service.impl;

import com.example.demo.payload.response.ClassFillRateResponse;
import com.example.demo.payload.response.RegistrationOutcomeStatsResponse;
import com.example.demo.payload.response.RegistrationThroughputResponse;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.RegistrationRequestLogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationMonitoringServiceImplTest {

    @Mock private RegistrationRequestLogRepository logRepository;
    @Mock private LopHocPhanRepository lopHocPhanRepository;

    @InjectMocks
    private RegistrationMonitoringServiceImpl service;

    @Test
    void outcomeStats_computesSuccessRateExcludingCancelled() {
        when(logRepository.aggregateByOutcome(any(), any())).thenReturn(List.<Object[]>of(
                new Object[]{"SUCCESS", 80L},
                new Object[]{"FULL", 10L},
                new Object[]{"VALIDATION_FAILED", 6L},
                new Object[]{"DUPLICATE", 4L},
                new Object[]{"REJECTED", 0L},
                new Object[]{"CANCELLED", 1000L}
        ));

        RegistrationOutcomeStatsResponse res = service.outcomeStats(null, null);

        assertThat(res.getTotal()).isEqualTo(80 + 10 + 6 + 4 + 0 + 1000);
        assertThat(res.getByOutcome()).containsEntry("SUCCESS", 80L)
                .containsEntry("CANCELLED", 1000L);
        assertThat(res.getSuccessRate()).isEqualTo(0.8d);
    }

    @Test
    void outcomeStats_zeroAttemptsReturnsZeroRate() {
        when(logRepository.aggregateByOutcome(any(), any())).thenReturn(List.of());
        RegistrationOutcomeStatsResponse res = service.outcomeStats(null, null);
        assertThat(res.getTotal()).isZero();
        assertThat(res.getSuccessRate()).isZero();
    }

    @Test
    void throughput_mapsRowsAndPropagatesWindow() {
        Instant from = Instant.parse("2026-01-01T00:00:00Z");
        Instant to = Instant.parse("2026-01-01T01:00:00Z");
        when(logRepository.aggregateByTypeAndOutcome(from, to)).thenReturn(List.<Object[]>of(
                new Object[]{"REGISTER", "SUCCESS", 50L},
                new Object[]{"REGISTER", "FULL", 5L},
                new Object[]{"CANCEL", "CANCELLED", 8L}
        ));

        RegistrationThroughputResponse res = service.throughput(from, to);

        assertThat(res.getFromAt()).isEqualTo(from);
        assertThat(res.getToAt()).isEqualTo(to);
        assertThat(res.getRows()).hasSize(3);
    }

    @Test
    void fillRate_mergesFullEventsByLop() {
        when(lopHocPhanRepository.findFillRateRowsForHocKy(99L)).thenReturn(List.<Object[]>of(
                new Object[]{1L, "INT2204_1", "INT2204", "OOP", 50, 50},
                new Object[]{2L, "INT2204_2", "INT2204", "OOP", 50, 30}
        ));
        when(logRepository.topFullClasses(any(), any())).thenReturn(List.<Object[]>of(
                new Object[]{1L, 7L}
        ));

        ClassFillRateResponse res = service.fillRate(99L, null, null);

        assertThat(res.getTotalClasses()).isEqualTo(2);
        assertThat(res.getTotalSlots()).isEqualTo(100);
        assertThat(res.getTakenSlots()).isEqualTo(80);
        assertThat(res.getOverallFillRate()).isEqualTo(0.8d);
        assertThat(res.getRows()).extracting(ClassFillRateResponse.Row::getFullEvents)
                .containsExactly(7L, 0L);
        assertThat(res.getRows().get(0).getFillRate()).isEqualTo(1.0d);
        assertThat(res.getRows().get(1).getFillRate()).isEqualTo(0.6d);
    }

    @Test
    void resolveWindow_rejectsFromAfterTo() {
        Instant from = Instant.parse("2026-01-01T02:00:00Z");
        Instant to = Instant.parse("2026-01-01T01:00:00Z");

        assertThatThrownBy(() -> service.outcomeStats(from, to))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("from > to");
    }
}
