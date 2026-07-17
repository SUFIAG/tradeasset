-- =====================================================
-- Migration: V14 - Create Documentary Collection Schema
-- Description: Complete schema for Documentary Collections
-- Author: AI System
-- Date: 2026-07-17
-- =====================================================

CREATE SCHEMA IF NOT EXISTS collection;

-- =====================================================
-- Table: documentary_collections
-- Description: Main documentary collection table
-- =====================================================
CREATE TABLE collection.documentary_collections (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Collection Identification
    collection_number VARCHAR(100),
    reference_number VARCHAR(100) NOT NULL,

    -- Parties
    drawer_id UUID NOT NULL,
    drawee_id UUID NOT NULL,
    remitting_bank VARCHAR(255),
    collecting_bank VARCHAR(255),
    presenting_bank VARCHAR(255),

    -- Collection Type
    collection_type VARCHAR(50) NOT NULL,

    -- Financial Details
    amount DECIMAL(20,2) NOT NULL,
    currency VARCHAR(3) NOT NULL,

    -- Terms
    payment_terms VARCHAR(50) NOT NULL,
    tenor_days INTEGER,

    -- Dates
    collection_date TIMESTAMP NOT NULL,
    presentation_date TIMESTAMP,
    acceptance_date TIMESTAMP,
    payment_due_date TIMESTAMP,
    payment_date TIMESTAMP,

    -- Status
    status VARCHAR(50) NOT NULL,

    -- Documents
    required_documents TEXT[],
    documents_received BOOLEAN DEFAULT FALSE,
    documents_complete BOOLEAN DEFAULT FALSE,

    -- Instructions
    collection_instructions TEXT,
    protest_instructions TEXT,
    charges_instructions TEXT,

    -- Charges
    bank_charges DECIMAL(10,2) DEFAULT 0,
    collection_charges DECIMAL(10,2) DEFAULT 0,
    charges_for_account VARCHAR(20),

    -- Trade Details
    goods_description TEXT,
    invoice_number VARCHAR(100),
    invoice_date DATE,
    shipment_details TEXT,

    -- Payment tracking
    amount_received DECIMAL(20,2) DEFAULT 0,
    outstanding_amount DECIMAL(20,2),

    -- Protest handling
    protest_required BOOLEAN DEFAULT FALSE,
    protested BOOLEAN DEFAULT FALSE,
    protest_date TIMESTAMP,
    protest_reason TEXT,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    completed_at TIMESTAMP,
    completed_by VARCHAR(100),
    version INTEGER DEFAULT 0,

    -- Constraints
    CONSTRAINT ck_collection_type CHECK (collection_type IN (
        'D_P', 'D_A', 'CLEAN', 'DOCUMENTARY'
    )),
    CONSTRAINT ck_payment_terms CHECK (payment_terms IN (
        'AT_SIGHT', 'DEFERRED', 'USANCE', 'ACCEPTANCE'
    )),
    CONSTRAINT ck_collection_status CHECK (status IN (
        'DRAFT', 'SUBMITTED', 'DOCUMENTS_SENT', 'DOCUMENTS_PRESENTED',
        'ACCEPTED', 'PAYMENT_PENDING', 'PAID', 'PROTESTED',
        'RETURNED', 'CANCELLED', 'COMPLETED'
    )),
    CONSTRAINT ck_charges_account CHECK (charges_for_account IN (
        'DRAWER', 'DRAWEE', 'SPLIT'
    )),
    CONSTRAINT uq_collection_reference UNIQUE (tenant_id, reference_number)
);

-- =====================================================
-- Table: collection_documents
-- Description: Documents in the collection
-- =====================================================
CREATE TABLE collection.collection_documents (
    id UUID PRIMARY KEY,
    collection_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Document Details
    document_type VARCHAR(100) NOT NULL,
    document_number VARCHAR(100),
    document_date DATE,

    -- Status
    received BOOLEAN DEFAULT FALSE,
    verified BOOLEAN DEFAULT FALSE,
    discrepancy_found BOOLEAN DEFAULT FALSE,
    discrepancy_details TEXT,

    -- File reference
    file_id UUID,
    file_path VARCHAR(500),

    -- Audit
    received_at TIMESTAMP,
    verified_at TIMESTAMP,
    verified_by VARCHAR(100),

    -- Foreign Keys
    CONSTRAINT fk_collection_documents_collection FOREIGN KEY (collection_id)
        REFERENCES collection.documentary_collections(id) ON DELETE CASCADE
);

-- =====================================================
-- Table: collection_payments
-- Description: Payment tracking for collections
-- =====================================================
CREATE TABLE collection.collection_payments (
    id UUID PRIMARY KEY,
    collection_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Payment Details
    payment_number VARCHAR(100),
    payment_amount DECIMAL(20,2) NOT NULL,
    payment_currency VARCHAR(3) NOT NULL,
    payment_date TIMESTAMP NOT NULL,

    -- Payment Method
    payment_method VARCHAR(50),
    payment_reference VARCHAR(100),

    -- Bank Details
    paying_bank VARCHAR(255),
    payment_instruction TEXT,

    -- Status
    status VARCHAR(50) NOT NULL,

    -- Charges
    charges_deducted DECIMAL(10,2) DEFAULT 0,
    net_amount DECIMAL(20,2),

    -- Audit
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    recorded_by VARCHAR(100),

    -- Foreign Keys
    CONSTRAINT fk_collection_payments_collection FOREIGN KEY (collection_id)
        REFERENCES collection.documentary_collections(id) ON DELETE CASCADE,
    CONSTRAINT ck_payment_status CHECK (status IN (
        'PENDING', 'RECEIVED', 'CLEARED', 'RETURNED', 'CANCELLED'
    ))
);

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- Collections
CREATE INDEX idx_collections_tenant ON collection.documentary_collections(tenant_id);
CREATE INDEX idx_collections_number ON collection.documentary_collections(collection_number);
CREATE INDEX idx_collections_reference ON collection.documentary_collections(reference_number);
CREATE INDEX idx_collections_drawer ON collection.documentary_collections(drawer_id);
CREATE INDEX idx_collections_drawee ON collection.documentary_collections(drawee_id);
CREATE INDEX idx_collections_status ON collection.documentary_collections(status, tenant_id);
CREATE INDEX idx_collections_type ON collection.documentary_collections(collection_type);
CREATE INDEX idx_collections_payment_due ON collection.documentary_collections(payment_due_date);

-- Documents
CREATE INDEX idx_collection_docs_collection ON collection.collection_documents(collection_id);
CREATE INDEX idx_collection_docs_tenant ON collection.collection_documents(tenant_id);

-- Payments
CREATE INDEX idx_collection_payments_collection ON collection.collection_payments(collection_id);
CREATE INDEX idx_collection_payments_tenant ON collection.collection_payments(tenant_id);
CREATE INDEX idx_collection_payments_date ON collection.collection_payments(payment_date DESC);

-- =====================================================
-- Comments
-- =====================================================

COMMENT ON TABLE collection.documentary_collections IS 'Documentary collections for trade finance';
COMMENT ON TABLE collection.collection_documents IS 'Documents associated with collections';
COMMENT ON TABLE collection.collection_payments IS 'Payment tracking for collections';

COMMENT ON COLUMN collection.documentary_collections.collection_type IS 'Type: D/P (Documents against Payment), D/A (Documents against Acceptance), CLEAN, DOCUMENTARY';
COMMENT ON COLUMN collection.documentary_collections.payment_terms IS 'Payment terms: AT_SIGHT, DEFERRED, USANCE, ACCEPTANCE';

