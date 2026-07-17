-- =====================================================
-- Migration: V12 - Create Analytics Schema
-- Description: Tables for analytics and reporting persistence
-- Author: AI System
-- Date: 2026-07-17
-- =====================================================

-- Create analytics schema
CREATE SCHEMA IF NOT EXISTS analytics;

-- =====================================================
-- Table: metric_snapshots
-- Description: Time-series snapshots of key metrics
-- =====================================================
CREATE TABLE analytics.metric_snapshots (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Metric Info
    metric_name VARCHAR(100) NOT NULL,
    metric_category VARCHAR(50) NOT NULL,

    -- Time
    snapshot_time TIMESTAMP NOT NULL,
    snapshot_date DATE NOT NULL,
    snapshot_hour INTEGER,

    -- Values
    metric_value DECIMAL(20,4),
    metric_count BIGINT,

    -- Dimensions
    dimension_1 VARCHAR(100),
    dimension_2 VARCHAR(100),
    dimension_3 VARCHAR(100),

    -- Additional Data
    metadata JSONB,

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT ck_metric_category CHECK (metric_category IN (
        'TRANSACTION', 'COMPLIANCE', 'RISK', 'PERFORMANCE',
        'CUSTOMER', 'DOCUMENT', 'WORKFLOW', 'SYSTEM'
    ))
);

-- =====================================================
-- Table: dashboard_metrics
-- Description: Pre-aggregated dashboard metrics
-- =====================================================
CREATE TABLE analytics.dashboard_metrics (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Time Range
    metric_date DATE NOT NULL,
    aggregation_level VARCHAR(20) NOT NULL,

    -- Transaction Metrics
    total_transactions BIGINT DEFAULT 0,
    active_transactions BIGINT DEFAULT 0,
    completed_transactions BIGINT DEFAULT 0,
    total_transaction_value DECIMAL(20,2) DEFAULT 0,

    -- LC Metrics
    lc_applications BIGINT DEFAULT 0,
    lc_issued BIGINT DEFAULT 0,
    lc_amendments BIGINT DEFAULT 0,
    lc_total_value DECIMAL(20,2) DEFAULT 0,

    -- Compliance Metrics
    compliance_checks BIGINT DEFAULT 0,
    compliance_passed BIGINT DEFAULT 0,
    compliance_failed BIGINT DEFAULT 0,
    compliance_pending BIGINT DEFAULT 0,
    high_risk_alerts BIGINT DEFAULT 0,

    -- Risk Metrics
    risk_assessments BIGINT DEFAULT 0,
    high_risk_count BIGINT DEFAULT 0,
    medium_risk_count BIGINT DEFAULT 0,
    low_risk_count BIGINT DEFAULT 0,
    avg_risk_score DECIMAL(5,2),

    -- Document Metrics
    documents_processed BIGINT DEFAULT 0,
    documents_verified BIGINT DEFAULT 0,
    documents_rejected BIGINT DEFAULT 0,
    avg_processing_time_ms BIGINT,

    -- Workflow Metrics
    workflows_created BIGINT DEFAULT 0,
    workflows_completed BIGINT DEFAULT 0,
    workflows_overdue BIGINT DEFAULT 0,
    avg_completion_time_hours DECIMAL(10,2),

    -- Customer Metrics
    new_customers BIGINT DEFAULT 0,
    active_customers BIGINT DEFAULT 0,
    kyc_completed BIGINT DEFAULT 0,

    -- Performance Metrics
    avg_response_time_ms BIGINT,
    error_count BIGINT DEFAULT 0,

    -- Audit
    calculated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT uq_dashboard_metrics UNIQUE (tenant_id, metric_date, aggregation_level),
    CONSTRAINT ck_aggregation_level CHECK (aggregation_level IN ('DAILY', 'WEEKLY', 'MONTHLY'))
);

-- =====================================================
-- Table: report_definitions
-- Description: Saved report configurations
-- =====================================================
CREATE TABLE analytics.report_definitions (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Report Info
    report_name VARCHAR(255) NOT NULL,
    report_type VARCHAR(100) NOT NULL,
    description TEXT,

    -- Configuration
    report_config JSONB NOT NULL,

    -- Filters
    default_filters JSONB,

    -- Scheduling
    is_scheduled BOOLEAN DEFAULT FALSE,
    schedule_cron VARCHAR(100),

    -- Recipients
    recipients TEXT[],

    -- Format
    output_format VARCHAR(50) DEFAULT 'PDF',

    -- Status
    enabled BOOLEAN DEFAULT TRUE,

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    -- Constraints
    CONSTRAINT ck_report_type CHECK (report_type IN (
        'TRANSACTION', 'COMPLIANCE', 'RISK', 'PERFORMANCE',
        'AUDIT', 'CUSTOMER', 'DASHBOARD', 'CUSTOM'
    )),
    CONSTRAINT ck_output_format CHECK (output_format IN ('PDF', 'EXCEL', 'CSV', 'JSON'))
);

-- =====================================================
-- Table: report_executions
-- Description: Report execution history
-- =====================================================
CREATE TABLE analytics.report_executions (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    report_definition_id UUID,

    -- Execution Info
    execution_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    execution_type VARCHAR(50) NOT NULL,

    -- Parameters
    parameters JSONB,

    -- Results
    status VARCHAR(50) NOT NULL,
    row_count BIGINT,
    file_path VARCHAR(500),
    file_size_bytes BIGINT,

    -- Performance
    execution_duration_ms BIGINT,

    -- Error Info
    error_message TEXT,

    -- User
    requested_by VARCHAR(100),

    -- Constraints
    CONSTRAINT fk_report_executions_definition FOREIGN KEY (report_definition_id)
        REFERENCES analytics.report_definitions(id) ON DELETE SET NULL,
    CONSTRAINT ck_execution_type CHECK (execution_type IN ('MANUAL', 'SCHEDULED', 'API')),
    CONSTRAINT ck_execution_status CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED'))
);

-- =====================================================
-- Table: kpi_targets
-- Description: KPI targets and thresholds
-- =====================================================
CREATE TABLE analytics.kpi_targets (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- KPI Info
    kpi_name VARCHAR(100) NOT NULL,
    kpi_category VARCHAR(50) NOT NULL,
    description TEXT,

    -- Targets
    target_value DECIMAL(20,4),
    warning_threshold DECIMAL(20,4),
    critical_threshold DECIMAL(20,4),

    -- Time Period
    effective_from DATE NOT NULL,
    effective_to DATE,

    -- Status
    active BOOLEAN DEFAULT TRUE,

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    -- Constraints
    CONSTRAINT uq_kpi_target UNIQUE (tenant_id, kpi_name, effective_from)
);

-- =====================================================
-- Table: transaction_analytics
-- Description: Detailed transaction analytics
-- =====================================================
CREATE TABLE analytics.transaction_analytics (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Reference
    transaction_id UUID NOT NULL,
    transaction_type VARCHAR(50) NOT NULL,

    -- Time Dimensions
    transaction_date DATE NOT NULL,
    transaction_month VARCHAR(7) NOT NULL,
    transaction_quarter VARCHAR(7) NOT NULL,
    transaction_year INTEGER NOT NULL,

    -- Financial
    transaction_value DECIMAL(20,2),
    transaction_currency VARCHAR(3),
    transaction_value_usd DECIMAL(20,2),

    -- Parties
    customer_id UUID,
    customer_segment VARCHAR(50),
    customer_country VARCHAR(2),

    -- Product
    product_type VARCHAR(50),
    product_category VARCHAR(50),

    -- Performance
    processing_time_hours DECIMAL(10,2),
    approval_time_hours DECIMAL(10,2),
    total_time_hours DECIMAL(10,2),

    -- Risk & Compliance
    risk_score DECIMAL(5,2),
    risk_level VARCHAR(20),
    compliance_score DECIMAL(5,2),
    compliance_status VARCHAR(20),

    -- Workflow
    workflow_id UUID,
    approval_levels_required INTEGER,
    approval_levels_completed INTEGER,

    -- Status
    current_status VARCHAR(50),
    is_completed BOOLEAN DEFAULT FALSE,
    is_cancelled BOOLEAN DEFAULT FALSE,

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- Metric Snapshots
CREATE INDEX idx_metric_snapshots_tenant ON analytics.metric_snapshots(tenant_id);
CREATE INDEX idx_metric_snapshots_time ON analytics.metric_snapshots(snapshot_time DESC);
CREATE INDEX idx_metric_snapshots_name ON analytics.metric_snapshots(metric_name, snapshot_time DESC);
CREATE INDEX idx_metric_snapshots_category ON analytics.metric_snapshots(metric_category, snapshot_time DESC);
CREATE INDEX idx_metric_snapshots_date ON analytics.metric_snapshots(snapshot_date DESC);

-- Dashboard Metrics
CREATE INDEX idx_dashboard_metrics_tenant ON analytics.dashboard_metrics(tenant_id);
CREATE INDEX idx_dashboard_metrics_date ON analytics.dashboard_metrics(metric_date DESC);
CREATE INDEX idx_dashboard_metrics_level ON analytics.dashboard_metrics(aggregation_level, metric_date DESC);

-- Report Definitions
CREATE INDEX idx_report_definitions_tenant ON analytics.report_definitions(tenant_id);
CREATE INDEX idx_report_definitions_enabled ON analytics.report_definitions(enabled, tenant_id) WHERE enabled = TRUE;

-- Report Executions
CREATE INDEX idx_report_executions_tenant ON analytics.report_executions(tenant_id);
CREATE INDEX idx_report_executions_definition ON analytics.report_executions(report_definition_id, execution_time DESC);
CREATE INDEX idx_report_executions_time ON analytics.report_executions(execution_time DESC);
CREATE INDEX idx_report_executions_status ON analytics.report_executions(status, tenant_id);

-- KPI Targets
CREATE INDEX idx_kpi_targets_tenant ON analytics.kpi_targets(tenant_id);
CREATE INDEX idx_kpi_targets_active ON analytics.kpi_targets(active, tenant_id) WHERE active = TRUE;

-- Transaction Analytics
CREATE INDEX idx_transaction_analytics_tenant ON analytics.transaction_analytics(tenant_id);
CREATE INDEX idx_transaction_analytics_date ON analytics.transaction_analytics(transaction_date DESC);
CREATE INDEX idx_transaction_analytics_month ON analytics.transaction_analytics(transaction_month DESC);
CREATE INDEX idx_transaction_analytics_customer ON analytics.transaction_analytics(customer_id, transaction_date DESC);
CREATE INDEX idx_transaction_analytics_type ON analytics.transaction_analytics(transaction_type, transaction_date DESC);

-- =====================================================
-- Comments
-- =====================================================

COMMENT ON TABLE analytics.metric_snapshots IS 'Time-series snapshots of key business metrics';
COMMENT ON TABLE analytics.dashboard_metrics IS 'Pre-aggregated metrics for dashboard performance';
COMMENT ON TABLE analytics.report_definitions IS 'Saved report configurations and templates';
COMMENT ON TABLE analytics.report_executions IS 'Report execution history and audit trail';
COMMENT ON TABLE analytics.kpi_targets IS 'KPI targets and threshold definitions';
COMMENT ON TABLE analytics.transaction_analytics IS 'Detailed transaction analytics for reporting';

COMMENT ON COLUMN analytics.dashboard_metrics.aggregation_level IS 'Aggregation level: DAILY, WEEKLY, MONTHLY';
COMMENT ON COLUMN analytics.report_definitions.schedule_cron IS 'Cron expression for scheduled report execution';

