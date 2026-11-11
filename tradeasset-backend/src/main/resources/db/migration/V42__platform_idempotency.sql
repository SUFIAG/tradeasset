-- V42: Platform infrastructure shared by every module.
--
-- idempotency_records : Idempotency-Key register (REQ-REL-001). Keys are scoped to the
--                       authenticated user and tenant. Rows expire after 24 hours and are purged;
--                       they hold API responses, never posted money.
--
-- Migration version ranges per module (forward only, never edit after merge):
--   V43 identity, V44 customer, V45 compliance, V46 risk, V47 workflow and notifications,
--   V48 letters of credit, V49 bank guarantees, V50 documentary collections,
--   V51 supply-chain finance, V52 shipments, V53 documents, V54 AI, knowledge and copilot,
--   V55 analytics.

CREATE TABLE idempotency_records (
    tenant_id             UUID         NOT NULL,
    user_id               UUID         NOT NULL,
    idempotency_key       VARCHAR(100) NOT NULL,
    request_method        VARCHAR(10)  NOT NULL,
    request_path          VARCHAR(500) NOT NULL,
    request_hash          CHAR(64)     NOT NULL,
    status                VARCHAR(12)  NOT NULL,
    response_status       INTEGER,
    response_content_type VARCHAR(200),
    response_body         BYTEA,
    created_at            TIMESTAMPTZ  NOT NULL,
    completed_at          TIMESTAMPTZ,
    expires_at            TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_idempotency_records PRIMARY KEY (tenant_id, user_id, idempotency_key),
    CONSTRAINT ck_idempotency_status CHECK (status IN ('IN_PROGRESS', 'COMPLETED'))
);
CREATE INDEX idx_idempotency_expires ON idempotency_records (expires_at);

-- Audit search by action and by time across the tenant (REQ-REL-003).
CREATE INDEX idx_trade_events_tenant_time ON trade_events (tenant_id, occurred_at DESC);
CREATE INDEX idx_trade_movements_tenant_account ON trade_movements (tenant_id, account, currency);
