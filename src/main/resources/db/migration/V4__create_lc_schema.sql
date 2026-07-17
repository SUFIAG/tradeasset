-- V3__create_lc_schema.sql
-- Letter of Credit Schema
-- Version: 1.0.0
-- Date: July 17, 2026

-- ===========================================
-- LETTER OF CREDIT SCHEMA
-- ===========================================

-- Main Letters of Credit table
CREATE TABLE trade.letters_of_credit (
    -- Primary Key
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    -- LC Identification
    lc_number VARCHAR(20) UNIQUE,  -- Format: LC-YYYY-NNNNNN
    lc_type VARCHAR(30) NOT NULL,  -- SIGHT, USANCE, CONFIRMED, etc.
    status VARCHAR(30) NOT NULL DEFAULT 'DRAFT',

    -- Multi-tenancy
    tenant_id UUID NOT NULL REFERENCES organization.tenants(id),

    -- Parties
    applicant_id UUID NOT NULL REFERENCES customer.customers(id),  -- The buyer/importer
    applicant_name VARCHAR(500) NOT NULL,
    applicant_address TEXT,

    beneficiary_id UUID NOT NULL REFERENCES customer.customers(id),  -- The seller/exporter
    beneficiary_name VARCHAR(500) NOT NULL,
    beneficiary_address TEXT,

    issuing_bank_id UUID,
    advising_bank_id UUID,
    confirming_bank_id UUID,

    -- Financial Terms
    lc_amount_value DECIMAL(20, 2) NOT NULL,
    lc_amount_currency VARCHAR(3) NOT NULL,
    tolerance DECIMAL(5, 2) DEFAULT 0.00,  -- Percentage (e.g., 5.00 for 5%)
    available_amount_value DECIMAL(20, 2),
    available_amount_currency VARCHAR(3),
    currency VARCHAR(3) NOT NULL,

    -- Dates
    application_date DATE NOT NULL,
    issue_date DATE,
    expiry_date DATE NOT NULL,
    last_shipment_date DATE,
    presentation_date DATE,

    -- Trade Terms
    incoterm VARCHAR(10),  -- EXW, FOB, CIF, etc.
    port_of_loading VARCHAR(200),
    port_of_discharge VARCHAR(200),
    place_of_destination VARCHAR(200),
    partial_shipment_allowed BOOLEAN DEFAULT FALSE,
    transshipment_allowed BOOLEAN DEFAULT FALSE,

    -- Goods Description
    goods_description TEXT,
    hs_code VARCHAR(20),  -- Harmonized System Code
    quantity DECIMAL(20, 2),
    quantity_unit VARCHAR(50),

    -- Credit Terms
    credit_limit_reserved_value DECIMAL(20, 2),
    credit_limit_reserved_currency VARCHAR(3),
    credit_limit_approved BOOLEAN DEFAULT FALSE,

    -- Document Requirements
    documents_required TEXT,
    commercial_invoice_required BOOLEAN DEFAULT TRUE,
    bill_of_lading_required BOOLEAN DEFAULT TRUE,
    packing_list_required BOOLEAN DEFAULT FALSE,
    certificate_of_origin_required BOOLEAN DEFAULT FALSE,
    insurance_certificate_required BOOLEAN DEFAULT FALSE,

    -- Special Instructions
    special_instructions TEXT,
    additional_conditions TEXT,

    -- Workflow
    submitted_by UUID,
    submitted_at TIMESTAMP,
    approved_by UUID,
    approved_at TIMESTAMP,
    rejected_by UUID,
    rejected_at TIMESTAMP,
    rejection_reason TEXT,

    -- SWIFT
    swift_message TEXT,  -- MT700 message
    swift_sent_at TIMESTAMP,

    -- Compliance & Risk
    compliance_check_passed BOOLEAN DEFAULT FALSE,
    sanctions_check_passed BOOLEAN DEFAULT FALSE,
    risk_rating VARCHAR(20),

    -- Audit Fields
    version BIGINT NOT NULL DEFAULT 0,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP,
    deleted_by UUID,
    deleted_at TIMESTAMP,

    -- Constraints
    CONSTRAINT check_lc_amount_positive CHECK (lc_amount_value > 0),
    CONSTRAINT check_dates CHECK (expiry_date >= application_date),
    CONSTRAINT check_different_parties CHECK (applicant_id != beneficiary_id)
);

-- LC Amendments table
CREATE TABLE trade.lc_amendments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lc_id UUID NOT NULL REFERENCES trade.letters_of_credit(id) ON DELETE CASCADE,
    amendment_number INT NOT NULL,
    amendment_date DATE NOT NULL,
    amendment_details TEXT NOT NULL,
    requested_by UUID NOT NULL,
    requested_at TIMESTAMP NOT NULL,
    approved_by UUID,
    approved_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',  -- PENDING, APPROVED, REJECTED

    -- Audit Fields
    version BIGINT NOT NULL DEFAULT 0,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP,

    CONSTRAINT unique_lc_amendment UNIQUE (lc_id, amendment_number)
);

-- LC Required Documents table
CREATE TABLE trade.lc_required_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lc_id UUID NOT NULL REFERENCES trade.letters_of_credit(id) ON DELETE CASCADE,
    document_type VARCHAR(100) NOT NULL,  -- e.g., COMMERCIAL_INVOICE, BILL_OF_LADING, etc.
    document_name VARCHAR(200) NOT NULL,
    number_of_originals INT DEFAULT 1,
    number_of_copies INT DEFAULT 0,
    is_mandatory BOOLEAN DEFAULT TRUE,
    special_requirements TEXT,

    -- Audit Fields
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP
);

-- LC Documents Presented table (when beneficiary presents documents)
CREATE TABLE trade.lc_documents_presented (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lc_id UUID NOT NULL REFERENCES trade.letters_of_credit(id) ON DELETE CASCADE,
    document_id UUID NOT NULL,  -- Reference to document management system
    document_type VARCHAR(100) NOT NULL,
    presented_date DATE NOT NULL,
    review_status VARCHAR(20) DEFAULT 'PENDING',  -- PENDING, ACCEPTED, REJECTED
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    review_notes TEXT,

    -- Audit Fields
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP
);

-- LC Discrepancies table
CREATE TABLE trade.lc_discrepancies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lc_id UUID NOT NULL REFERENCES trade.letters_of_credit(id) ON DELETE CASCADE,
    document_id UUID,
    discrepancy_type VARCHAR(100) NOT NULL,
    discrepancy_code VARCHAR(20),
    description TEXT NOT NULL,
    severity VARCHAR(20) NOT NULL,  -- MINOR, MAJOR, CRITICAL
    is_waivable BOOLEAN DEFAULT FALSE,
    detected_by VARCHAR(20),  -- MANUAL, AI
    detected_at TIMESTAMP NOT NULL,
    status VARCHAR(20) DEFAULT 'OPEN',  -- OPEN, WAIVED, RESOLVED

    -- Audit Fields
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP
);

-- LC Payments table
CREATE TABLE trade.lc_payments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    lc_id UUID NOT NULL REFERENCES trade.letters_of_credit(id) ON DELETE CASCADE,
    payment_number INT NOT NULL,
    payment_amount_value DECIMAL(20, 2) NOT NULL,
    payment_amount_currency VARCHAR(3) NOT NULL,
    payment_date DATE NOT NULL,
    value_date DATE,
    payment_reference VARCHAR(100),
    authorized_by UUID NOT NULL,
    authorized_at TIMESTAMP NOT NULL,
    executed_by UUID,
    executed_at TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'AUTHORIZED',  -- AUTHORIZED, EXECUTED, FAILED

    -- Audit Fields
    version BIGINT NOT NULL DEFAULT 0,
    created_by UUID NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP,

    CONSTRAINT unique_lc_payment UNIQUE (lc_id, payment_number),
    CONSTRAINT check_payment_amount_positive CHECK (payment_amount_value > 0)
);

-- ===========================================
-- INDEXES
-- ===========================================

-- Performance indexes for letters_of_credit
CREATE INDEX idx_lc_number ON trade.letters_of_credit(lc_number);
CREATE INDEX idx_lc_tenant ON trade.letters_of_credit(tenant_id);
CREATE INDEX idx_lc_applicant ON trade.letters_of_credit(applicant_id);
CREATE INDEX idx_lc_beneficiary ON trade.letters_of_credit(beneficiary_id);
CREATE INDEX idx_lc_status ON trade.letters_of_credit(status);
CREATE INDEX idx_lc_created_at ON trade.letters_of_credit(created_at);
CREATE INDEX idx_lc_expiry_date ON trade.letters_of_credit(expiry_date);
CREATE INDEX idx_lc_application_date ON trade.letters_of_credit(application_date);
CREATE INDEX idx_lc_type ON trade.letters_of_credit(lc_type);

-- Composite indexes
CREATE INDEX idx_lc_tenant_status ON trade.letters_of_credit(tenant_id, status);
CREATE INDEX idx_lc_applicant_status ON trade.letters_of_credit(applicant_id, status);

-- Indexes for amendments
CREATE INDEX idx_lc_amendments_lc_id ON trade.lc_amendments(lc_id);
CREATE INDEX idx_lc_amendments_status ON trade.lc_amendments(status);

-- Indexes for required documents
CREATE INDEX idx_lc_required_docs_lc_id ON trade.lc_required_documents(lc_id);

-- Indexes for presented documents
CREATE INDEX idx_lc_presented_docs_lc_id ON trade.lc_documents_presented(lc_id);
CREATE INDEX idx_lc_presented_docs_status ON trade.lc_documents_presented(review_status);

-- Indexes for discrepancies
CREATE INDEX idx_lc_discrepancies_lc_id ON trade.lc_discrepancies(lc_id);
CREATE INDEX idx_lc_discrepancies_status ON trade.lc_discrepancies(status);
CREATE INDEX idx_lc_discrepancies_severity ON trade.lc_discrepancies(severity);

-- Indexes for payments
CREATE INDEX idx_lc_payments_lc_id ON trade.lc_payments(lc_id);
CREATE INDEX idx_lc_payments_status ON trade.lc_payments(status);

-- ===========================================
-- COMMENTS
-- ===========================================

COMMENT ON TABLE trade.letters_of_credit IS 'Main table for Letters of Credit in trade finance';
COMMENT ON COLUMN trade.letters_of_credit.lc_number IS 'Unique LC number in format LC-YYYY-NNNNNN';
COMMENT ON COLUMN trade.letters_of_credit.applicant_id IS 'The buyer/importer - references customer';
COMMENT ON COLUMN trade.letters_of_credit.beneficiary_id IS 'The seller/exporter - references customer';
COMMENT ON COLUMN trade.letters_of_credit.tolerance IS 'Percentage tolerance for LC amount (e.g., 5% means +/-5%)';

COMMENT ON TABLE trade.lc_amendments IS 'Amendments to LCs after issuance';
COMMENT ON TABLE trade.lc_required_documents IS 'Documents required for LC presentation';
COMMENT ON TABLE trade.lc_documents_presented IS 'Documents presented by beneficiary for payment';
COMMENT ON TABLE trade.lc_discrepancies IS 'Discrepancies found in presented documents';
COMMENT ON TABLE trade.lc_payments IS 'Payment transactions for LCs';

