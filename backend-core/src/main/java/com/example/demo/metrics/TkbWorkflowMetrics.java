package com.example.demo.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

/** BACK-TKB-036 — metrics cho conflict-check / solver / change-set apply. */
@Component
public class TkbWorkflowMetrics {

    private final MeterRegistry registry;
    private final Counter changeSetApplyCounter;

    public TkbWorkflowMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.changeSetApplyCounter = Counter.builder("eduport.tkb.change_set.apply.count")
                .description("Số lần apply change-set thành công")
                .register(registry);
    }

    public Timer.Sample start() {
        return Timer.start(registry);
    }

    public void stop(Timer.Sample sample, String timerName) {
        sample.stop(Timer.builder(timerName).register(registry));
    }

    public void markConflictCheck(String result) {
        Counter.builder("eduport.tkb.conflict_check.count")
                .tag("result", result)
                .register(registry)
                .increment();
    }

    public void markSolverRun(String mode, String result) {
        Counter.builder("eduport.tkb.solver.run.count")
                .tag("mode", mode)
                .tag("result", result)
                .register(registry)
                .increment();
    }

    public void markChangeSetApply() {
        changeSetApplyCounter.increment();
    }
}
