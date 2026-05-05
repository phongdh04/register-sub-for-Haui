package com.example.demo.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

/**
 * Đếm trường hợp không gán được FK phòng từ JSON (BACK-TKB-008 / dual-write sau này).
 */
@Component
public class TkbPhongMappingMetrics {

    public static final String METER_FALLBACK = "eduport.tkb.fallback_json_phong";

    private final Counter unresolvedApplyCounter;

    public TkbPhongMappingMetrics(MeterRegistry registry) {
        this.unresolvedApplyCounter = Counter.builder(METER_FALLBACK)
                .description("LHP vẫn không map được id_phong_hoc sau batch/đọc JSON")
                .tag("reason", "unresolved_after_apply")
                .register(registry);
    }

    public void incrementFallbackJsonPhong() {
        unresolvedApplyCounter.increment();
    }
}
