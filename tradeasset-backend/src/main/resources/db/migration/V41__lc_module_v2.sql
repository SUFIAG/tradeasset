-- V41: Letter of credit module (REQ-LC-001..013).
--
-- The legacy table letters_of_credit (V1/V13) is left untouched and is deprecated; it held only
-- synthetic seed rows whose states do not exist in the BRD state machine. It will be dropped in a
-- later two-phase migration once nothing reads it.

CREATE SEQUENCE lc_application_ref_seq START WITH 1000;
CREATE SEQUENCE lc_number_seq START WITH 5000;

CREATE TABLE lc_letters_of_credit (
    id                        UUID PRIMARY KEY,
    tenant_id                 UUID           NOT NULL,
    reference                 VARCHAR(30)    NOT NULL,
    lc_number                 VARCHAR(30),
    lc_type                   VARCHAR(30)    NOT NULL,
    status                    VARCHAR(30)    NOT NULL,
    applicant_id              UUID           NOT NULL,
    applicant_name            VARCHAR(300)   NOT NULL,
    beneficiary_id            UUID           NOT NULL,
    beneficiary_name          VARCHAR(300)   NOT NULL,
    beneficiary_address       TEXT,
    advising_bank_bic         VARCHAR(11),
    confirming_bank_bic       VARCHAR(11),
    confirmed                 BOOLEAN        NOT NULL DEFAULT FALSE,
    transferable              BOOLEAN        NOT NULL DEFAULT FALSE,
    amount                    NUMERIC(24, 3) NOT NULL,
    currency                  CHAR(3)        NOT NULL,
    tolerance_plus_pct        NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    tolerance_minus_pct       NUMERIC(5, 2)  NOT NULL DEFAULT 0,
    available_amount          NUMERIC(24, 3) NOT NULL DEFAULT 0,
    tenor_type                VARCHAR(20)    NOT NULL,
    tenor_days                INTEGER,
    expiry_date               DATE           NOT NULL,
    expiry_place              VARCHAR(200)   NOT NULL,
    latest_shipment_date      DATE,
    presentation_period_days  INTEGER        NOT NULL DEFAULT 21,
    partial_shipments_allowed BOOLEAN        NOT NULL DEFAULT FALSE,
    transshipment_allowed     BOOLEAN        NOT NULL DEFAULT FALSE,
    goods_description         TEXT           NOT NULL,
    incoterm                  VARCHAR(3),
    incoterm_place            VARCHAR(200),
    port_of_loading           VARCHAR(200),
    port_of_discharge         VARCHAR(200),
    required_documents        JSONB          NOT NULL DEFAULT '[]'::jsonb,
    additional_conditions     TEXT,
    issue_date                DATE,
    created_by                UUID           NOT NULL,
    created_at                TIMESTAMPTZ    NOT NULL,
    updated_by                UUID,
    updated_at                TIMESTAMPTZ,
    submitted_by              UUID,
    submitted_at              TIMESTAMPTZ,
    approved_by               UUID,
    approved_at               TIMESTAMPTZ,
    issued_by                 UUID,
    issued_at                 TIMESTAMPTZ,
    closed_at                 TIMESTAMPTZ,
    rejection_reason          TEXT,
    cancellation_reason       TEXT,
    version                   BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT uk_lc_reference UNIQUE (tenant_id, reference),
    CONSTRAINT uk_lc_number UNIQUE (tenant_id, lc_number),
    CONSTRAINT ck_lc_amount_positive CHECK (amount > 0),
    CONSTRAINT ck_lc_parties_differ CHECK (applicant_id <> beneficiary_id)
);
CREATE INDEX idx_lc_tenant_status ON lc_letters_of_credit (tenant_id, status);
CREATE INDEX idx_lc_tenant_applicant ON lc_letters_of_credit (tenant_id, applicant_id);
CREATE INDEX idx_lc_tenant_beneficiary ON lc_letters_of_credit (tenant_id, beneficiary_id);
CREATE INDEX idx_lc_tenant_expiry ON lc_letters_of_credit (tenant_id, expiry_date);
CREATE INDEX idx_lc_tenant_created ON lc_letters_of_credit (tenant_id, created_at DESC);

CREATE TABLE lc_amendments (
    id                       UUID PRIMARY KEY,
    lc_id                    UUID           NOT NULL REFERENCES lc_letters_of_credit (id),
    tenant_id                UUID           NOT NULL,
    amendment_number         INTEGER        NOT NULL,
    status                   VARCHAR(20)    NOT NULL,
    new_amount               NUMERIC(24, 3),
    new_expiry_date          DATE,
    new_latest_shipment_date DATE,
    new_goods_description    TEXT,
    other_changes            TEXT,
    previous_terms           JSONB          NOT NULL,
    lc_status_at_request     VARCHAR(30)    NOT NULL,
    requested_by             UUID           NOT NULL,
    requested_at             TIMESTAMPTZ    NOT NULL,
    applicant_consent        VARCHAR(10)    NOT NULL DEFAULT 'PENDING',
    applicant_consent_by     UUID,
    applicant_consent_at     TIMESTAMPTZ,
    issuing_bank_consent     VARCHAR(10)    NOT NULL DEFAULT 'PENDING',
    issuing_bank_consent_by  UUID,
    issuing_bank_consent_at  TIMESTAMPTZ,
    beneficiary_consent      VARCHAR(10)    NOT NULL DEFAULT 'PENDING',
    beneficiary_consent_by   UUID,
    beneficiary_consent_at   TIMESTAMPTZ,
    rejection_reason         TEXT,
    rejected_by              UUID,
    decided_at               TIMESTAMPTZ,
    version                  BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT uk_lc_amendment_number UNIQUE (lc_id, amendment_number)
);
CREATE INDEX idx_lc_amendments_lc ON lc_amendments (lc_id);

CREATE TABLE lc_presentations (
    id                     UUID PRIMARY KEY,
    lc_id                  UUID           NOT NULL REFERENCES lc_letters_of_credit (id),
    tenant_id              UUID           NOT NULL,
    presentation_number    INTEGER        NOT NULL,
    status                 VARCHAR(30)    NOT NULL,
    claimed_amount         NUMERIC(24, 3) NOT NULL,
    currency               CHAR(3)        NOT NULL,
    shipment_date          DATE,
    presentation_date      DATE           NOT NULL,
    presentation_deadline  DATE           NOT NULL,
    examination_deadline   DATE           NOT NULL,
    presenter_reference    VARCHAR(100),
    presented_documents    JSONB          NOT NULL DEFAULT '[]'::jsonb,
    received_by            UUID           NOT NULL,
    received_at            TIMESTAMPTZ    NOT NULL,
    examined_by            UUID,
    examined_at            TIMESTAMPTZ,
    decided_by             UUID,
    decided_at             TIMESTAMPTZ,
    refusal_notice_sent_at TIMESTAMPTZ,
    waiver_recorded_by     UUID,
    waiver_recorded_at     TIMESTAMPTZ,
    waiver_reference       VARCHAR(200),
    paid_amount            NUMERIC(24, 3),
    paid_at                TIMESTAMPTZ,
    payment_reference      VARCHAR(100),
    version                BIGINT         NOT NULL DEFAULT 0,
    CONSTRAINT uk_lc_presentation_number UNIQUE (lc_id, presentation_number),
    CONSTRAINT ck_lc_presentation_amount CHECK (claimed_amount > 0)
);
CREATE INDEX idx_lc_presentations_lc ON lc_presentations (lc_id);

CREATE TABLE lc_discrepancies (
    id              UUID PRIMARY KEY,
    presentation_id UUID         NOT NULL REFERENCES lc_presentations (id),
    lc_id           UUID         NOT NULL REFERENCES lc_letters_of_credit (id),
    tenant_id       UUID         NOT NULL,
    code            VARCHAR(60)  NOT NULL,
    document_type   VARCHAR(60)  NOT NULL,
    field           VARCHAR(100) NOT NULL,
    lc_term         VARCHAR(300) NOT NULL,
    isbp_reference  VARCHAR(100) NOT NULL,
    ucp_reference   VARCHAR(100),
    description     TEXT         NOT NULL,
    severity        VARCHAR(10)  NOT NULL,
    source          VARCHAR(10)  NOT NULL,
    status          VARCHAR(12)  NOT NULL,
    raised_by       UUID,
    raised_at       TIMESTAMPTZ  NOT NULL,
    resolved_by     UUID,
    resolved_at     TIMESTAMPTZ,
    resolution_note TEXT
);
CREATE INDEX idx_lc_discrepancies_presentation ON lc_discrepancies (presentation_id);

CREATE TABLE lc_swift_messages (
    id           UUID PRIMARY KEY,
    lc_id        UUID        NOT NULL REFERENCES lc_letters_of_credit (id),
    tenant_id    UUID        NOT NULL,
    message_type VARCHAR(10) NOT NULL,
    amendment_id UUID REFERENCES lc_amendments (id),
    sequence_no  INTEGER     NOT NULL,
    content      TEXT        NOT NULL,
    created_by   UUID,
    created_at   TIMESTAMPTZ NOT NULL
);
CREATE INDEX idx_lc_swift_lc ON lc_swift_messages (lc_id, created_at);

CREATE TRIGGER trg_lc_swift_messages_append_only
    BEFORE UPDATE OR DELETE ON lc_swift_messages
    FOR EACH ROW EXECUTE FUNCTION trade_forbid_mutation();
