-- Document Management Schema Migration
-- Version: V6
-- Description: Create document schema and tables

-- Create document schema
CREATE SCHEMA IF NOT EXISTS document;

-- Create documents table
CREATE TABLE document.documents (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL,

    -- File information
    file_name VARCHAR(255) NOT NULL,
    original_file_name VARCHAR(255),
    file_size BIGINT NOT NULL,
    mime_type VARCHAR(100),
    file_extension VARCHAR(10),

    -- Storage information
    storage_key VARCHAR(500) UNIQUE,
    storage_bucket VARCHAR(100),
    storage_url VARCHAR(1000),
    checksum VARCHAR(64),

    -- Relationships
    related_entity_type VARCHAR(50),
    related_entity_id VARCHAR(100),
    uploaded_by UUID,
    uploaded_at TIMESTAMP,

    -- Virus scanning
    scan_started_at TIMESTAMP,
    scan_completed_at TIMESTAMP,
    virus_scan_passed BOOLEAN,
    scan_result VARCHAR(500),

    -- AI extraction
    extraction_started_at TIMESTAMP,
    extraction_completed_at TIMESTAMP,
    extraction_successful BOOLEAN,
    extracted_data TEXT, -- JSON
    extraction_confidence REAL,
    extraction_model VARCHAR(50),

    -- Review & verification
    reviewed_by UUID,
    reviewed_at TIMESTAMP,
    verified BOOLEAN,
    rejection_reason VARCHAR(1000),

    -- Metadata
    metadata TEXT, -- JSON

    -- Audit fields
    version BIGINT NOT NULL DEFAULT 0,
    created_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP,

    -- Constraints
    CONSTRAINT check_file_size_positive CHECK (file_size > 0),
    CONSTRAINT check_confidence_range CHECK (extraction_confidence IS NULL OR (extraction_confidence >= 0 AND extraction_confidence <= 1))
);

-- Create indexes for performance
CREATE INDEX idx_document_tenant ON document.documents(tenant_id);
CREATE INDEX idx_document_status ON document.documents(status);
CREATE INDEX idx_document_storage_key ON document.documents(storage_key);
CREATE INDEX idx_document_related_entity ON document.documents(related_entity_type, related_entity_id);
CREATE INDEX idx_document_uploaded_at ON document.documents(uploaded_at DESC);
CREATE INDEX idx_document_uploaded_by ON document.documents(uploaded_by);
CREATE INDEX idx_document_type ON document.documents(document_type);
CREATE INDEX idx_document_verified ON document.documents(verified) WHERE verified IS TRUE;

-- Create composite index for common queries
CREATE INDEX idx_document_tenant_status ON document.documents(tenant_id, status);
CREATE INDEX idx_document_tenant_type ON document.documents(tenant_id, document_type);
CREATE INDEX idx_document_tenant_entity ON document.documents(tenant_id, related_entity_type, related_entity_id);

-- Add comments for documentation
COMMENT ON TABLE document.documents IS 'Document management table storing metadata for uploaded files';
COMMENT ON COLUMN document.documents.tenant_id IS 'Tenant ID for multi-tenancy isolation';
COMMENT ON COLUMN document.documents.document_type IS 'Type of document (COMMERCIAL_INVOICE, BILL_OF_LADING, etc.)';
COMMENT ON COLUMN document.documents.status IS 'Document processing status (UPLOADED, SCANNING, EXTRACTED, VERIFIED, etc.)';
COMMENT ON COLUMN document.documents.storage_key IS 'Unique key in object storage (MinIO/S3)';
COMMENT ON COLUMN document.documents.extracted_data IS 'AI-extracted data stored as JSON';
COMMENT ON COLUMN document.documents.extraction_confidence IS 'AI extraction confidence score (0.0 to 1.0)';
COMMENT ON COLUMN document.documents.verified IS 'Whether document has been manually verified';

-- Grant permissions (adjust based on your security model)
-- GRANT SELECT, INSERT, UPDATE, DELETE ON document.documents TO tradesphere_app;

