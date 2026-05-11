package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Hotfix schema cho moi truong DB cu chua co cot status_publish.
 *
 * <p>Mot so may local da tao bang lop_hoc_phan truoc Sprint 3 nen thieu cot
 * status_publish/version, dan den loi SQLState 42703. Runner nay tu dong bo
 * sung cot/constraint/index theo huong idempotent (IF NOT EXISTS).
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SchemaHotfixRunner implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void run(ApplicationArguments args) {
        try {
            jdbcTemplate.execute("""
                    ALTER TABLE lop_hoc_phan
                        ADD COLUMN IF NOT EXISTS status_publish VARCHAR(16) NOT NULL DEFAULT 'PUBLISHED'
                    """);
            jdbcTemplate.execute("""
                    ALTER TABLE lop_hoc_phan
                        ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0
                    """);
            jdbcTemplate.execute("""
                    ALTER TABLE lop_hoc_phan
                        DROP CONSTRAINT IF EXISTS chk_lhp_status_publish
                    """);
            jdbcTemplate.execute("""
                    ALTER TABLE lop_hoc_phan
                        ADD CONSTRAINT chk_lhp_status_publish
                        CHECK (status_publish IN ('SHELL', 'SCHEDULED', 'PUBLISHED'))
                    """);
            jdbcTemplate.execute("""
                    CREATE INDEX IF NOT EXISTS idx_lhp_hk_status_publish
                    ON lop_hoc_phan (id_hoc_ky, status_publish)
                    """);
            jdbcTemplate.execute("""
                    UPDATE lop_hoc_phan
                    SET status_publish = 'PUBLISHED'
                    WHERE status_publish IS NULL
                    """);
            log.info("Schema hotfix done: lop_hoc_phan.status_publish/version");
        } catch (Exception ex) {
            log.warn("Schema hotfix skipped/failed: {}", ex.getMessage());
        }
    }
}
