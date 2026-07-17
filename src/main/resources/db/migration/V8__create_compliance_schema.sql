-- Compliance Schema Migration
-- Creates tables for compliance checks, sanctions screening, AML, and PEP checks

-- Create compliance schema
CREATE SCHEMA IF NOT EXISTS compliance;

-- Compliance Checks table
CREATE TABLE compliance.compliance_checks (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    check_number VARCHAR(50) NOT NULL UNIQUE,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    entity_name VARCHAR(255) NOT NULL,
    check_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    initiated_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    initiated_by UUID NOT NULL,

    -- Screening results stored as JSON
    sanctions_hits JSONB,
    pep_hits JSONB,
    aml_hits JSONB,

    -- AI analysis
    ai_confidence_score DECIMAL(3,2),
    false_positive_probability INTEGER,
    ai_reasoning TEXT,

    -- Decision
    decision VARCHAR(50),
    decision_reason TEXT,
    decided_by UUID,
    decided_at TIMESTAMP,

    -- Review
    requires_manual_review BOOLEAN NOT NULL DEFAULT false,
    review_comments TEXT,

    -- Audit fields
    version BIGINT DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_compliance_tenant FOREIGN KEY (tenant_id) REFERENCES public.tenants(id)
);

-- Indexes for performance
CREATE INDEX idx_compliance_tenant ON compliance.compliance_checks(tenant_id);
CREATE INDEX idx_compliance_entity ON compliance.compliance_checks(entity_type, entity_id);
CREATE INDEX idx_compliance_status ON compliance.compliance_checks(status, tenant_id);
CREATE INDEX idx_compliance_decision ON compliance.compliance_checks(decision, tenant_id);
CREATE INDEX idx_compliance_review ON compliance.compliance_checks(requires_manual_review, tenant_id);
CREATE INDEX idx_compliance_check_number ON compliance.compliance_checks(check_number);
CREATE INDEX idx_compliance_initiated_at ON compliance.compliance_checks(initiated_at DESC);

-- Comments for documentation
COMMENT ON TABLE compliance.compliance_checks IS 'Compliance checks for sanctions, AML, and PEP screening with AI-powered false positive detection';
COMMENT ON COLUMN compliance.compliance_checks.sanctions_hits IS 'Sanctions screening hits stored as JSON array';
COMMENT ON COLUMN compliance.compliance_checks.pep_hits IS 'PEP (Politically Exposed Person) screening hits stored as JSON array';
COMMENT ON COLUMN compliance.compliance_checks.aml_hits IS 'AML (Anti-Money Laundering) screening hits stored as JSON array';
COMMENT ON COLUMN compliance.compliance_checks.ai_confidence_score IS 'AI confidence score from 0.0 to 1.0';
COMMENT ON COLUMN compliance.compliance_checks.false_positive_probability IS 'Probability that hits are false positives (0-100)';
COMMENT ON COLUMN compliance.compliance_checks.requires_manual_review IS 'Flag indicating if manual review is required';

-- Grant permissions (adjust based on your role structure)
GRANT USAGE ON SCHEMA compliance TO tradesphere_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA compliance TO tradesphere_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA compliance TO tradesphere_app;

