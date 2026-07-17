-- =====================================================
-- Migration: V13 - Create Bank Guarantee Schema
-- Description: Complete schema for Bank Guarantee product
-- Author: AI System
-- Date: 2026-07-17
-- =====================================================

CREATE SCHEMA IF NOT EXISTS bankguarantee;

-- =====================================================
-- Table: bank_guarantees
-- Description: Main bank guarantee table
-- =====================================================
CREATE TABLE bankguarantee.bank_guarantees (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- BG Identification
    bg_number VARCHAR(100),
    reference_number VARCHAR(100) NOT NULL,

    -- Parties
    applicant_id UUID NOT NULL,
    beneficiary_id UUID NOT NULL,
    issuing_bank VARCHAR(255),
    advising_bank VARCHAR(255),

    -- BG Type
    bg_type VARCHAR(50) NOT NULL,

    -- Financial Details
    amount DECIMAL(20,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,

    -- Dates
    application_date TIMESTAMP NOT NULL,
    issue_date TIMESTAMP,
    effective_date TIMESTAMP,
    expiry_date TIMESTAMP NOT NULL,
    claim_expiry_date TIMESTAMP,

    -- Status
    status VARCHAR(50) NOT NULL,

    -- Terms & Conditions
    purpose TEXT,
    governing_law VARCHAR(100),
    jurisdiction VARCHAR(100),
    claim_procedure TEXT,

    -- Contract Details
    underlying_contract_number VARCHAR(100),
    underlying_contract_date DATE,
    underlying_contract_value DECIMAL(20,2),

    -- Reduction/Extension
    reduction_allowed BOOLEAN DEFAULT FALSE,
    extension_allowed BOOLEAN DEFAULT FALSE,
    reduction_terms TEXT,
    extension_terms TEXT,

    -- Claims
    claims_count INTEGER DEFAULT 0,
    claimed_amount DECIMAL(20,2) DEFAULT 0,
    paid_amount DECIMAL(20,2) DEFAULT 0,

    -- Documents
    required_documents TEXT[],

    -- Special Conditions
    special_conditions TEXT,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    approved_at TIMESTAMP,
    approved_by VARCHAR(100),
    issued_at TIMESTAMP,
    issued_by VARCHAR(100),
    version INTEGER DEFAULT 0,

    -- Constraints
    CONSTRAINT ck_bg_status CHECK (status IN (
        'DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED',
        'ISSUED', 'ACTIVE', 'CLAIMED', 'PARTIALLY_PAID', 'FULLY_PAID',
        'EXPIRED', 'CANCELLED', 'RELEASED'
    )),
    CONSTRAINT ck_bg_type CHECK (bg_type IN (
        'PERFORMANCE', 'ADVANCE_PAYMENT', 'RETENTION', 'BID_BOND',
        'PAYMENT', 'FINANCIAL', 'CUSTOMS', 'WARRANTY'
    )),
    CONSTRAINT ck_bg_amount CHECK (amount > 0),
    CONSTRAINT uq_bg_reference UNIQUE (tenant_id, reference_number)
);

-- =====================================================
-- Table: bg_amendments
-- Description: BG amendments and modifications
-- =====================================================
CREATE TABLE bankguarantee.bg_amendments (
    id UUID PRIMARY KEY,
    bg_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Amendment Details
    amendment_number INTEGER NOT NULL,
    amendment_type VARCHAR(50) NOT NULL,

    -- Changes
    old_amount DECIMAL(20,2),
    new_amount DECIMAL(20,2),
    old_expiry_date TIMESTAMP,
    new_expiry_date TIMESTAMP,
    changes_description TEXT NOT NULL,

    -- Status
    status VARCHAR(50) NOT NULL,

    -- Approval
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    requested_by VARCHAR(100),
    approved_at TIMESTAMP,
    approved_by VARCHAR(100),
    rejected_at TIMESTAMP,
    rejected_by VARCHAR(100),
    rejection_reason TEXT,

    -- Foreign Keys
    CONSTRAINT fk_bg_amendments_bg FOREIGN KEY (bg_id)
        REFERENCES bankguarantee.bank_guarantees(id) ON DELETE CASCADE,
    CONSTRAINT ck_amendment_type CHECK (amendment_type IN (
        'AMOUNT_INCREASE', 'AMOUNT_DECREASE', 'EXTENSION', 'REDUCTION',
        'TERMS_MODIFICATION', 'BENEFICIARY_CHANGE', 'OTHER'
    )),
    CONSTRAINT ck_amendment_status CHECK (status IN (
        'PENDING', 'APPROVED', 'REJECTED', 'IMPLEMENTED'
    ))
);

-- =====================================================
-- Table: bg_claims
-- Description: Claims against bank guarantees
-- =====================================================
CREATE TABLE bankguarantee.bg_claims (
    id UUID PRIMARY KEY,
    bg_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Claim Details
    claim_number VARCHAR(100) NOT NULL,
    claim_amount DECIMAL(20,2) NOT NULL,
    claim_currency VARCHAR(3) NOT NULL,

    -- Claim Reason
    claim_reason TEXT NOT NULL,
    claim_type VARCHAR(50) NOT NULL,

    -- Documents
    supporting_documents UUID[],

    -- Status
    status VARCHAR(50) NOT NULL,

    -- Processing
    received_date TIMESTAMP NOT NULL,
    review_deadline TIMESTAMP,
    decision_date TIMESTAMP,
    payment_date TIMESTAMP,

    -- Decision
    approved_amount DECIMAL(20,2),
    rejection_reason TEXT,

    -- Payments
    payment_reference VARCHAR(100),
    paid_amount DECIMAL(20,2) DEFAULT 0,

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    reviewed_by VARCHAR(100),
    decided_by VARCHAR(100),

    -- Foreign Keys
    CONSTRAINT fk_bg_claims_bg FOREIGN KEY (bg_id)
        REFERENCES bankguarantee.bank_guarantees(id) ON DELETE CASCADE,
    CONSTRAINT ck_claim_status CHECK (status IN (
        'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED',
        'PAYMENT_PENDING', 'PARTIALLY_PAID', 'FULLY_PAID'
    )),
    CONSTRAINT ck_claim_type CHECK (claim_type IN (
        'PERFORMANCE_DEFAULT', 'NON_PAYMENT', 'CONTRACT_BREACH',
        'ADVANCE_PAYMENT_DEFAULT', 'OTHER'
    )),
    CONSTRAINT ck_claim_amount CHECK (claim_amount > 0)
);

-- =====================================================
-- Table: bg_reductions
-- Description: BG amount reductions
-- =====================================================
CREATE TABLE bankguarantee.bg_reductions (
    id UUID PRIMARY KEY,
    bg_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Reduction Details
    reduction_number INTEGER NOT NULL,
    reduction_amount DECIMAL(20,2) NOT NULL,
    new_total_amount DECIMAL(20,2) NOT NULL,
    reduction_percentage DECIMAL(5,2),

    -- Reason
    reduction_reason TEXT NOT NULL,
    supporting_evidence TEXT,

    -- Status
    status VARCHAR(50) NOT NULL,

    -- Processing
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    requested_by VARCHAR(100),
    approved_at TIMESTAMP,
    approved_by VARCHAR(100),
    effective_date TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_bg_reductions_bg FOREIGN KEY (bg_id)
        REFERENCES bankguarantee.bank_guarantees(id) ON DELETE CASCADE,
    CONSTRAINT ck_reduction_status CHECK (status IN (
        'PENDING', 'APPROVED', 'REJECTED', 'IMPLEMENTED'
    ))
);

-- =====================================================
-- Table: bg_extensions
-- Description: BG validity extensions
-- =====================================================
CREATE TABLE bankguarantee.bg_extensions (
    id UUID PRIMARY KEY,
    bg_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Extension Details
    extension_number INTEGER NOT NULL,
    old_expiry_date TIMESTAMP NOT NULL,
    new_expiry_date TIMESTAMP NOT NULL,
    extension_days INTEGER,

    -- Reason
    extension_reason TEXT NOT NULL,
    justification TEXT,

    -- Status
    status VARCHAR(50) NOT NULL,

    -- Processing
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    requested_by VARCHAR(100),
    approved_at TIMESTAMP,
    approved_by VARCHAR(100),
    effective_date TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_bg_extensions_bg FOREIGN KEY (bg_id)
        REFERENCES bankguarantee.bank_guarantees(id) ON DELETE CASCADE,
    CONSTRAINT ck_extension_status CHECK (status IN (
        'PENDING', 'APPROVED', 'REJECTED', 'IMPLEMENTED'
    ))
);

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- Bank Guarantees
CREATE INDEX idx_bg_tenant ON bankguarantee.bank_guarantees(tenant_id);
CREATE INDEX idx_bg_number ON bankguarantee.bank_guarantees(bg_number);
CREATE INDEX idx_bg_reference ON bankguarantee.bank_guarantees(reference_number);
CREATE INDEX idx_bg_applicant ON bankguarantee.bank_guarantees(applicant_id);
CREATE INDEX idx_bg_beneficiary ON bankguarantee.bank_guarantees(beneficiary_id);
CREATE INDEX idx_bg_status ON bankguarantee.bank_guarantees(status, tenant_id);
CREATE INDEX idx_bg_type ON bankguarantee.bank_guarantees(bg_type);
CREATE INDEX idx_bg_expiry ON bankguarantee.bank_guarantees(expiry_date);
CREATE INDEX idx_bg_issue_date ON bankguarantee.bank_guarantees(issue_date DESC);

-- Amendments
CREATE INDEX idx_bg_amendments_bg ON bankguarantee.bg_amendments(bg_id);
CREATE INDEX idx_bg_amendments_status ON bankguarantee.bg_amendments(status);

-- Claims
CREATE INDEX idx_bg_claims_bg ON bankguarantee.bg_claims(bg_id);
CREATE INDEX idx_bg_claims_status ON bankguarantee.bg_claims(status);
CREATE INDEX idx_bg_claims_received ON bankguarantee.bg_claims(received_date DESC);

-- Reductions
CREATE INDEX idx_bg_reductions_bg ON bankguarantee.bg_reductions(bg_id);
CREATE INDEX idx_bg_reductions_status ON bankguarantee.bg_reductions(status);

-- Extensions
CREATE INDEX idx_bg_extensions_bg ON bankguarantee.bg_extensions(bg_id);
CREATE INDEX idx_bg_extensions_status ON bankguarantee.bg_extensions(status);

-- =====================================================
-- Comments
-- =====================================================

COMMENT ON TABLE bankguarantee.bank_guarantees IS 'Main bank guarantee table for all BG types';
COMMENT ON TABLE bankguarantee.bg_amendments IS 'BG amendments and modifications';
COMMENT ON TABLE bankguarantee.bg_claims IS 'Claims against bank guarantees';
COMMENT ON TABLE bankguarantee.bg_reductions IS 'BG amount reductions';
COMMENT ON TABLE bankguarantee.bg_extensions IS 'BG validity period extensions';

COMMENT ON COLUMN bankguarantee.bank_guarantees.bg_type IS 'Type: PERFORMANCE, ADVANCE_PAYMENT, RETENTION, BID_BOND, PAYMENT, FINANCIAL, CUSTOMS, WARRANTY';
COMMENT ON COLUMN bankguarantee.bank_guarantees.status IS 'BG lifecycle status';

