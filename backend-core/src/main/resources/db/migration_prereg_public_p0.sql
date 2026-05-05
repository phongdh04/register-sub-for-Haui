-- BACK-PREREG-001/002 — Public pre-registration link/request schema (P0)

CREATE TABLE IF NOT EXISTS pre_registration_link (
    id BIGSERIAL PRIMARY KEY,
    token_hash VARCHAR(128) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expires_at TIMESTAMP NOT NULL,
    max_submissions INT NOT NULL,
    submitted_count INT DEFAULT 0,
    intake_code VARCHAR(50) NOT NULL,
    campus_code VARCHAR(50),
    allow_domains TEXT,
    rate_limit_profile VARCHAR(50),
    created_by VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_prereg_link_expires ON pre_registration_link(expires_at);
CREATE INDEX IF NOT EXISTS idx_prereg_link_status ON pre_registration_link(status);

CREATE TABLE IF NOT EXISTS pre_registration_request (
    id BIGSERIAL PRIMARY KEY,
    request_id UUID NOT NULL UNIQUE,
    link_id BIGINT NOT NULL REFERENCES pre_registration_link(id),
    dedupe_key VARCHAR(128) NOT NULL UNIQUE,
    payload_json TEXT NOT NULL,
    source_ip_hash VARCHAR(128),
    source_ip_prefix VARCHAR(18),
    user_agent_hash VARCHAR(128),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    error_code VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    processed_at TIMESTAMP,
    trace_id VARCHAR(100),
    retry_count INT DEFAULT 0,
    last_error_detail TEXT,
    kafka_partition INT,
    kafka_offset BIGINT
);

CREATE INDEX IF NOT EXISTS idx_prereg_request_status_created
    ON pre_registration_request(status, created_at);
CREATE INDEX IF NOT EXISTS idx_prereg_request_link_created
    ON pre_registration_request(link_id, created_at);
