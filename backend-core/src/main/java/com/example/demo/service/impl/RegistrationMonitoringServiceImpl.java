package com.example.demo.service.impl;

import com.example.demo.domain.enums.RegistrationOutcome;
import com.example.demo.payload.response.ClassFillRateResponse;
import com.example.demo.payload.response.RegistrationOutcomeStatsResponse;
import com.example.demo.payload.response.RegistrationThroughputResponse;
import com.example.demo.repository.LopHocPhanRepository;
import com.example.demo.repository.RegistrationRequestLogRepository;
import com.example.demo.service.IRegistrationMonitoringService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Sprint 6 — Tổng hợp metric registration cho admin dashboard.
 */
@Service
@RequiredArgsConstructor
public class RegistrationMonitoringServiceImpl implements IRegistrationMonitoringService {

    /** Mặc định 24h gần nhất nếu caller không truyền window. */
    private static final Duration DEFAULT_WINDOW = Duration.ofHours(24);

    /** Outcome dùng để tính successRate (loại CANCELLED vì không phải attempt đăng ký). */
    private static final Set<String> ATTEMPT_OUTCOMES = Set.of(
            RegistrationOutcome.SUCCESS.name(),
            RegistrationOutcome.FULL.name(),
            RegistrationOutcome.DUPLICATE.name(),
            RegistrationOutcome.VALIDATION_FAILED.name(),
            RegistrationOutcome.REJECTED.name()
    );

    private final RegistrationRequestLogRepository logRepository;
    private final LopHocPhanRepository lopHocPhanRepository;

    @Override
    @Transactional(readOnly = true)
    public RegistrationOutcomeStatsResponse outcomeStats(Instant from, Instant to) {
        Window w = resolveWindow(from, to);
        List<Object[]> rows = logRepository.aggregateByOutcome(w.from(), w.to());

        Map<String, Long> byOutcome = new HashMap<>();
        long total = 0L;
        long success = 0L;
        long attempts = 0L;
        for (Object[] row : rows) {
            String outcome = row[0] == null ? "UNKNOWN" : row[0].toString();
            long count = ((Number) row[1]).longValue();
            byOutcome.put(outcome, count);
            total += count;
            if (RegistrationOutcome.SUCCESS.name().equals(outcome)) {
                success = count;
            }
            if (ATTEMPT_OUTCOMES.contains(outcome)) {
                attempts += count;
            }
        }
        double successRate = attempts == 0 ? 0d : (double) success / attempts;

        return RegistrationOutcomeStatsResponse.builder()
                .fromAt(w.from())
                .toAt(w.to())
                .total(total)
                .byOutcome(byOutcome)
                .successRate(round4(successRate))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public RegistrationThroughputResponse throughput(Instant from, Instant to) {
        Window w = resolveWindow(from, to);
        List<Object[]> rows = logRepository.aggregateByTypeAndOutcome(w.from(), w.to());
        List<RegistrationThroughputResponse.Row> mapped = new ArrayList<>(rows.size());
        for (Object[] r : rows) {
            mapped.add(RegistrationThroughputResponse.Row.builder()
                    .requestType(r[0] == null ? null : r[0].toString())
                    .outcome(r[1] == null ? null : r[1].toString())
                    .count(((Number) r[2]).longValue())
                    .build());
        }
        return RegistrationThroughputResponse.builder()
                .fromAt(w.from())
                .toAt(w.to())
                .rows(mapped)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public ClassFillRateResponse fillRate(Long hocKyId, Instant from, Instant to) {
        Window w = resolveWindow(from, to);

        List<Object[]> classRows = lopHocPhanRepository.findFillRateRowsForHocKy(hocKyId);
        List<Object[]> fullRows = logRepository.topFullClasses(w.from(), w.to());
        Map<Long, Long> fullByLhp = new HashMap<>(fullRows.size());
        for (Object[] r : fullRows) {
            if (r[0] == null) continue;
            fullByLhp.put(((Number) r[0]).longValue(), ((Number) r[1]).longValue());
        }

        List<ClassFillRateResponse.Row> rows = new ArrayList<>(classRows.size());
        long totalSlots = 0L;
        long takenSlots = 0L;
        for (Object[] r : classRows) {
            Long idLopHp = r[0] == null ? null : ((Number) r[0]).longValue();
            int sito = ((Number) r[4]).intValue();
            int sit = r[5] == null ? 0 : ((Number) r[5]).intValue();
            totalSlots += sito;
            takenSlots += sit;
            rows.add(ClassFillRateResponse.Row.builder()
                    .idLopHp(idLopHp)
                    .maLopHp((String) r[1])
                    .maHocPhan((String) r[2])
                    .tenHocPhan((String) r[3])
                    .siSoToiDa(sito)
                    .siSoThucTe(sit)
                    .fillRate(sito == 0 ? 0d : round4((double) sit / sito))
                    .fullEvents(idLopHp == null ? 0L : fullByLhp.getOrDefault(idLopHp, 0L))
                    .build());
        }
        return ClassFillRateResponse.builder()
                .idHocKy(hocKyId)
                .totalClasses(rows.size())
                .totalSlots(totalSlots)
                .takenSlots(takenSlots)
                .overallFillRate(totalSlots == 0 ? 0d : round4((double) takenSlots / totalSlots))
                .rows(rows)
                .build();
    }

    private Window resolveWindow(Instant from, Instant to) {
        Instant t = to != null ? to : Instant.now();
        Instant f = from != null ? from : t.minus(DEFAULT_WINDOW);
        if (f.isAfter(t)) {
            throw new IllegalArgumentException("from > to không hợp lệ.");
        }
        return new Window(f, t);
    }

    private static double round4(double v) {
        return Math.round(v * 10000d) / 10000d;
    }

    private record Window(Instant from, Instant to) { }
}
