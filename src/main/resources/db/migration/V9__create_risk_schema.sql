-- Risk Assessment Schema Migration
-- Creates tables for risk assessments and scoring

-- Create risk schema if not exists (might already exist from V1)
CREATE SCHEMA IF NOT EXISTS risk;

-- Risk Assessments table
CREATE TABLE risk.risk_assessments (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    assessment_number VARCHAR(50) NOT NULL UNIQUE,
    risk_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    entity_name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    initiated_at TIMESTAMP NOT NULL,
    completed_at TIMESTAMP,
    initiated_by UUID NOT NULL,

    -- Risk factors stored as JSON
    risk_factors JSONB,

    -- Overall risk
    overall_risk_score INTEGER,
    overall_risk_level VARCHAR(20),
    risk_summary TEXT,
    ai_reasoning TEXT,

    -- Country-specific
    country_code VARCHAR(3),
    country_risk_score INTEGER,

    -- Customer-specific
    credit_risk_score INTEGER,
    payment_history_score INTEGER,
    financial_health_score INTEGER,

    -- Transaction-specific
    transaction_risk_score INTEGER,
    documentary_risk_score INTEGER,

    -- Recommendation
    recommendation TEXT,
    requires_approval BOOLEAN NOT NULL DEFAULT false,
    auto_rejected BOOLEAN NOT NULL DEFAULT false,

    -- Audit fields
    version BIGINT DEFAULT 1,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_risk_tenant FOREIGN KEY (tenant_id) REFERENCES public.tenants(id)
);

-- Indexes for performance
CREATE INDEX idx_risk_tenant ON risk.risk_assessments(tenant_id);
CREATE INDEX idx_risk_entity ON risk.risk_assessments(entity_type, entity_id);
CREATE INDEX idx_risk_status ON risk.risk_assessments(status, tenant_id);
CREATE INDEX idx_risk_level ON risk.risk_assessments(overall_risk_level, tenant_id);
CREATE INDEX idx_risk_assessment_number ON risk.risk_assessments(assessment_number);
CREATE INDEX idx_risk_country ON risk.risk_assessments(country_code);
CREATE INDEX idx_risk_high_risk ON risk.risk_assessments(overall_risk_level, requires_approval, tenant_id);
CREATE INDEX idx_risk_initiated_at ON risk.risk_assessments(initiated_at DESC);

-- Comments
COMMENT ON TABLE risk.risk_assessments IS 'Risk assessments for customers, transactions, and counterparties';
COMMENT ON COLUMN risk.risk_assessments.risk_factors IS 'Individual risk factors stored as JSON array';
COMMENT ON COLUMN risk.risk_assessments.overall_risk_score IS 'Overall risk score from 0-100';
COMMENT ON COLUMN risk.risk_assessments.overall_risk_level IS 'Risk level: VERY_LOW, LOW, MEDIUM, HIGH, VERY_HIGH';
COMMENT ON COLUMN risk.risk_assessments.ai_reasoning IS 'AI-generated reasoning for risk assessment';

-- Grant permissions
GRANT USAGE ON SCHEMA risk TO tradesphere_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA risk TO tradesphere_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA risk TO tradesphere_app;

