-- =====================================================
-- Migration: V15 - Create Supply Chain Finance Schema
-- Description: Complete schema for SCF (Invoice Factoring, Supplier Financing)
-- Author: AI System
-- Date: 2026-07-17
-- =====================================================

CREATE SCHEMA IF NOT EXISTS scf;

-- =====================================================
-- Table: scf_facilities
-- Description: Supply chain finance facilities
-- =====================================================
CREATE TABLE scf.scf_facilities (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Facility Identification
    facility_number VARCHAR(100) NOT NULL,
    facility_name VARCHAR(255) NOT NULL,

    -- Parties
    buyer_id UUID NOT NULL,
    supplier_id UUID,
    anchor_party_id UUID NOT NULL,

    -- Facility Type
    scf_type VARCHAR(50) NOT NULL,

    -- Financial Details
    facility_limit DECIMAL(20,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    utilized_amount DECIMAL(20,2) DEFAULT 0,
    available_amount DECIMAL(20,2),

    -- Terms
    discount_rate DECIMAL(5,4),
    payment_terms_days INTEGER,
    early_payment_discount DECIMAL(5,2),

    -- Dates
    effective_date TIMESTAMP NOT NULL,
    expiry_date TIMESTAMP NOT NULL,

    -- Status
    status VARCHAR(50) NOT NULL,

    -- Conditions
    eligible_suppliers TEXT[],
    eligible_categories TEXT[],
    minimum_invoice_amount DECIMAL(20,2),
    maximum_invoice_amount DECIMAL(20,2),

    -- Risk
    risk_rating VARCHAR(20),
    credit_insurance BOOLEAN DEFAULT FALSE,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    approved_at TIMESTAMP,
    approved_by VARCHAR(100),
    version INTEGER DEFAULT 0,

    -- Constraints
    CONSTRAINT ck_scf_type CHECK (scf_type IN (
        'INVOICE_FACTORING', 'REVERSE_FACTORING', 'DYNAMIC_DISCOUNTING',
        'SUPPLIER_FINANCING', 'PAYABLES_FINANCE', 'RECEIVABLES_PURCHASE'
    )),
    CONSTRAINT ck_scf_status CHECK (status IN (
        'DRAFT', 'PENDING_APPROVAL', 'APPROVED', 'ACTIVE',
        'SUSPENDED', 'EXPIRED', 'CANCELLED'
    )),
    CONSTRAINT uq_facility_number UNIQUE (tenant_id, facility_number)
);

-- =====================================================
-- Table: scf_transactions
-- Description: Individual SCF transactions
-- =====================================================
CREATE TABLE scf.scf_transactions (
    id UUID PRIMARY KEY,
    facility_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Transaction Identification
    transaction_number VARCHAR(100) NOT NULL,

    -- Invoice Details
    invoice_id UUID,
    invoice_number VARCHAR(100) NOT NULL,
    invoice_date DATE NOT NULL,
    invoice_amount DECIMAL(20,2) NOT NULL,
    invoice_currency VARCHAR(3) NOT NULL,

    -- Parties
    buyer_id UUID NOT NULL,
    supplier_id UUID NOT NULL,

    -- Financing Details
    financed_amount DECIMAL(20,2) NOT NULL,
    discount_amount DECIMAL(20,2),
    net_amount DECIMAL(20,2),
    discount_rate DECIMAL(5,4),

    -- Dates
    transaction_date TIMESTAMP NOT NULL,
    original_due_date DATE NOT NULL,
    early_payment_date DATE,
    actual_payment_date DATE,

    -- Status
    status VARCHAR(50) NOT NULL,

    -- Payment tracking
    amount_paid DECIMAL(20,2) DEFAULT 0,
    outstanding_amount DECIMAL(20,2),

    -- Fees
    service_fee DECIMAL(10,2) DEFAULT 0,
    processing_fee DECIMAL(10,2) DEFAULT 0,
    total_fees DECIMAL(10,2) DEFAULT 0,

    -- Settlement
    settlement_reference VARCHAR(100),
    settlement_date TIMESTAMP,

    -- Goods/Services
    goods_description TEXT,
    purchase_order_number VARCHAR(100),

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    approved_at TIMESTAMP,
    approved_by VARCHAR(100),
    paid_at TIMESTAMP,
    paid_by VARCHAR(100),
    version INTEGER DEFAULT 0,

    -- Foreign Keys
    CONSTRAINT fk_scf_transactions_facility FOREIGN KEY (facility_id)
        REFERENCES scf.scf_facilities(id) ON DELETE CASCADE,
    CONSTRAINT ck_scf_transaction_status CHECK (status IN (
        'DRAFT', 'SUBMITTED', 'UNDER_REVIEW', 'APPROVED', 'REJECTED',
        'FINANCED', 'PAYMENT_PENDING', 'PAID', 'SETTLED', 'CANCELLED'
    )),
    CONSTRAINT uq_transaction_number UNIQUE (tenant_id, transaction_number)
);

-- =====================================================
-- Table: scf_payments
-- Description: Payment tracking for SCF transactions
-- =====================================================
CREATE TABLE scf.scf_payments (
    id UUID PRIMARY KEY,
    transaction_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Payment Details
    payment_number VARCHAR(100) NOT NULL,
    payment_type VARCHAR(50) NOT NULL,
    payment_amount DECIMAL(20,2) NOT NULL,
    payment_currency VARCHAR(3) NOT NULL,
    payment_date TIMESTAMP NOT NULL,

    -- Payer/Payee
    payer_id UUID NOT NULL,
    payee_id UUID NOT NULL,

    -- Payment Method
    payment_method VARCHAR(50),
    payment_reference VARCHAR(100),

    -- Bank Details
    payment_bank VARCHAR(255),

    -- Status
    status VARCHAR(50) NOT NULL,

    -- Audit
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recorded_by VARCHAR(100),

    -- Foreign Keys
    CONSTRAINT fk_scf_payments_transaction FOREIGN KEY (transaction_id)
        REFERENCES scf.scf_transactions(id) ON DELETE CASCADE,
    CONSTRAINT ck_scf_payment_type CHECK (payment_type IN (
        'FINANCING_DISBURSEMENT', 'BUYER_PAYMENT', 'SUPPLIER_PAYMENT',
        'FEE_PAYMENT', 'SETTLEMENT'
    )),
    CONSTRAINT ck_scf_payment_status CHECK (status IN (
        'PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED'
    ))
);

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- Facilities
CREATE INDEX idx_scf_facilities_tenant ON scf.scf_facilities(tenant_id);
CREATE INDEX idx_scf_facilities_number ON scf.scf_facilities(facility_number);
CREATE INDEX idx_scf_facilities_buyer ON scf.scf_facilities(buyer_id);
CREATE INDEX idx_scf_facilities_supplier ON scf.scf_facilities(supplier_id);
CREATE INDEX idx_scf_facilities_status ON scf.scf_facilities(status, tenant_id);
CREATE INDEX idx_scf_facilities_type ON scf.scf_facilities(scf_type);

-- Transactions
CREATE INDEX idx_scf_transactions_tenant ON scf.scf_transactions(tenant_id);
CREATE INDEX idx_scf_transactions_facility ON scf.scf_transactions(facility_id);
CREATE INDEX idx_scf_transactions_number ON scf.scf_transactions(transaction_number);
CREATE INDEX idx_scf_transactions_invoice ON scf.scf_transactions(invoice_number);
CREATE INDEX idx_scf_transactions_buyer ON scf.scf_transactions(buyer_id);
CREATE INDEX idx_scf_transactions_supplier ON scf.scf_transactions(supplier_id);
CREATE INDEX idx_scf_transactions_status ON scf.scf_transactions(status, tenant_id);
CREATE INDEX idx_scf_transactions_date ON scf.scf_transactions(transaction_date DESC);

-- Payments
CREATE INDEX idx_scf_payments_transaction ON scf.scf_payments(transaction_id);
CREATE INDEX idx_scf_payments_tenant ON scf.scf_payments(tenant_id);
CREATE INDEX idx_scf_payments_payer ON scf.scf_payments(payer_id);
CREATE INDEX idx_scf_payments_payee ON scf.scf_payments(payee_id);
CREATE INDEX idx_scf_payments_date ON scf.scf_payments(payment_date DESC);

-- =====================================================
-- Comments
-- =====================================================

COMMENT ON TABLE scf.scf_facilities IS 'Supply chain finance facilities (factoring, supplier financing)';
COMMENT ON TABLE scf.scf_transactions IS 'Individual SCF financing transactions';
COMMENT ON TABLE scf.scf_payments IS 'Payment tracking for SCF transactions';

COMMENT ON COLUMN scf.scf_facilities.scf_type IS 'Type: INVOICE_FACTORING, REVERSE_FACTORING, DYNAMIC_DISCOUNTING, SUPPLIER_FINANCING, PAYABLES_FINANCE, RECEIVABLES_PURCHASE';

