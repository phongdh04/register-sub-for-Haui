-- Registration Campaign: mo phien dang ky theo khoa (cohort-first).
-- Bang parent chua cau hinh mac dinh, cac RegistrationWindow co the thuoc campaign nay.

CREATE TABLE IF NOT EXISTS registration_campaign (
    id_registration_campaign  BIGSERIAL PRIMARY KEY,
    ten_campaign             VARCHAR(200) NOT NULL,
    nam_nhap_hoc            INT          NOT NULL,
    phase                   VARCHAR(16)  NOT NULL,
    open_at                 TIMESTAMPTZ  NOT NULL,
    close_at                TIMESTAMPTZ  NOT NULL,
    ghi_chu                 VARCHAR(500),
    created_by               VARCHAR(100),
    created_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),
    updated_at              TIMESTAMPTZ  NOT NULL DEFAULT NOW(),

    CONSTRAINT chk_campaign_phase       CHECK (phase IN ('PRE', 'OFFICIAL')),
    CONSTRAINT chk_campaign_open_before_close CHECK (open_at < close_at),
    CONSTRAINT chk_campaign_namnhaphoc  CHECK (nam_nhap_hoc BETWEEN 2000 AND 2100),
    CONSTRAINT uk_campaign_scope         UNIQUE (nam_nhap_hoc, phase)
);

CREATE INDEX IF NOT EXISTS idx_campaign_nam_phase
    ON registration_campaign (nam_nhap_hoc, phase);

CREATE INDEX IF NOT EXISTS idx_campaign_open_close
    ON registration_campaign (open_at, close_at);

-- Them campaignId vao registration_window (backward compat: nullable).
-- Window co campaignId = thuoc mot chiendich; null = tao thu cong bang admin (cach cu).
ALTER TABLE registration_window
    ADD COLUMN IF NOT EXISTS id_campaign BIGINT
        REFERENCES registration_campaign(id_registration_campaign) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_regwin_campaign
    ON registration_window (id_campaign)
    WHERE id_campaign IS NOT NULL;
