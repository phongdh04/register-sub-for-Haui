package com.example.demo.scheduling.solver.config;

import com.google.ortools.Loader;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Nạp JNI OR-Tools một lần cho process (idempotent) + bind {@link SolverCpSatProperties}.
 */
@Configuration
@EnableConfigurationProperties(SolverCpSatProperties.class)
@Slf4j
public class SolverRuntimeConfig {

    @PostConstruct
    void loadOrTools() {
        try {
            Loader.loadNativeLibraries();
            log.debug("OR-Tools native libraries loaded for CP-SAT");
        } catch (Exception e) {
            log.error("OR-Tools JNI load failed — solver dry-run sẽ lỗi: {}", e.toString());
            throw e;
        }
    }
}
