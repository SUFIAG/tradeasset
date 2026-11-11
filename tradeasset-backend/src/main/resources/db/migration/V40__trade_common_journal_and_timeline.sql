-- V40: Shared trade-instrument infrastructure used by the LC, BG, collection, SCF and shipment modules.
--
-- trade_events     : append-only state/timeline/audit trail per instrument (REQ-REL-003, REQ-UI-004)
-- trade_movements  : append-only balance journal (REQ-MON-006, REQ-MON-007). Balances are SUM(amount)
--                    per (instrument, account). Corrections are reversing rows, never edits.
-- trade_approvals  : maker-checker records (REQ-MKR-001..003). The checker can never be the maker.
--
-- Amounts use NUMERIC(24,3) so that 3-decimal currencies (KWD, BHD, JOD, OMR) are never truncated.

CREATE OR REPLACE FUNCTION trade_forbid_mutation() RETURNS trigger AS $$
BEGIN
    RAISE EXCEPTION 'Table % is append-only: % is not permitted', TG_TABLE_NAME, TG_OP
        USING ERRCODE = 'integrity_constraint_violation';
END;
$$ LANGUAGE plpgsql;

CREATE TABLE trade_events (
    id              UUID PRIMARY KEY,
    tenant_id       UUID         NOT NULL,
    instrument_type VARCHAR(20)  NOT NULL,
    instrument_id   UUID         NOT NULL,
    action          VARCHAR(60)  NOT NULL,
    from_state      VARCHAR(40),
    to_state        VARCHAR(40),
    actor_id        UUID,
    actor_name      VARCHAR(200),
    comment         TEXT,
    details         JSONB,
    source_address  VARCHAR(64),
    occurred_at     TIMESTAMPTZ  NOT NULL
);
CREATE INDEX idx_trade_events_instrument ON trade_events (tenant_id, instrument_type, instrument_id, occurred_at);
CREATE INDEX idx_trade_events_actor ON trade_events (tenant_id, actor_id, occurred_at);

CREATE TRIGGER trg_trade_events_append_only
    BEFORE UPDATE OR DELETE ON trade_events
    FOR EACH ROW EXECUTE FUNCTION trade_forbid_mutation();

CREATE TABLE trade_movements (
    id                   UUID PRIMARY KEY,
    tenant_id            UUID           NOT NULL,
    instrument_type      VARCHAR(20)    NOT NULL,
    instrument_id        UUID           NOT NULL,
    customer_id          UUID,
    account              VARCHAR(30)    NOT NULL,
    movement_type        VARCHAR(40)    NOT NULL,
    amount               NUMERIC(24, 3) NOT NULL,
    currency             CHAR(3)        NOT NULL,
    reason               TEXT           NOT NULL,
    reference            VARCHAR(100),
    actor_id             UUID,
    occurred_at          TIMESTAMPTZ    NOT NULL,
    reverses_movement_id UUID REFERENCES trade_movements (id)
);
CREATE INDEX idx_trade_movements_instrument ON trade_movements (tenant_id, instrument_type, instrument_id, account);
CREATE INDEX idx_trade_movements_customer ON trade_movements (tenant_id, customer_id, account, currency);
CREATE UNIQUE INDEX uk_trade_movements_single_reversal ON trade_movements (reverses_movement_id)
    WHERE reverses_movement_id IS NOT NULL;

CREATE TRIGGER trg_trade_movements_append_only
    BEFORE UPDATE OR DELETE ON trade_movements
    FOR EACH ROW EXECUTE FUNCTION trade_forbid_mutation();

CREATE TABLE trade_approvals (
    id              UUID PRIMARY KEY,
    tenant_id       UUID         NOT NULL,
    instrument_type VARCHAR(20)  NOT NULL,
    instrument_id   UUID         NOT NULL,
    action          VARCHAR(60)  NOT NULL,
    reference       VARCHAR(100),
    status          VARCHAR(20)  NOT NULL,
    amount          NUMERIC(24, 3),
    currency        CHAR(3),
    maker_id        UUID         NOT NULL,
    maker_name      VARCHAR(200),
    maker_at        TIMESTAMPTZ  NOT NULL,
    maker_comment   TEXT,
    checker_id      UUID,
    checker_name    VARCHAR(200),
    checker_at      TIMESTAMPTZ,
    checker_comment TEXT,
    CONSTRAINT ck_trade_approvals_status CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'CANCELLED')),
    CONSTRAINT ck_trade_approvals_four_eyes CHECK (checker_id IS NULL OR checker_id <> maker_id)
);
CREATE INDEX idx_trade_approvals_instrument ON trade_approvals (tenant_id, instrument_type, instrument_id);
CREATE INDEX idx_trade_approvals_pending ON trade_approvals (tenant_id, status) WHERE status = 'PENDING';
