-- =====================================================
-- Migration: V19 - Create Analytics Report Cache Schema
-- Description: Persistence layer for cached analytics reports
-- Author: AI System
-- Date: 2026-07-17
-- =====================================================

-- Analytics Reports Cache Table
CREATE TABLE analytics_reports (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    report_name VARCHAR(255) NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL,

    -- Time period
    start_date DATE,
    end_date DATE,

    -- Generation metadata
    generated_by VARCHAR(255),
    generated_at TIMESTAMP NOT NULL,
    computation_time_ms BIGINT,

    -- Cache control
    cache_expires_at TIMESTAMP,
    cached BOOLEAN DEFAULT FALSE,

    -- Audit fields
    created_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0,

    CONSTRAINT ck_analytics_report_type CHECK (report_type IN (
        'TRANSACTION_SUMMARY', 'RISK_ANALYSIS', 'COMPLIANCE_REPORT',
        'PERFORMANCE_METRICS', 'CUSTOMER_INSIGHTS', 'FINANCIAL_OVERVIEW',
        'OPERATIONAL_DASHBOARD', 'CUSTOM'
    )),
    CONSTRAINT ck_analytics_status CHECK (status IN (
        'GENERATING', 'COMPLETED', 'FAILED', 'CACHED'
    ))
);

-- Analytics Metrics Table (separate for efficient querying)
CREATE TABLE analytics_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id UUID NOT NULL,
    metric_name VARCHAR(255) NOT NULL,
    metric_value DECIMAL(19,4) NOT NULL,
    metric_unit VARCHAR(50),
    metric_type VARCHAR(50),
    timestamp TIMESTAMP NOT NULL,

    CONSTRAINT fk_metric_report FOREIGN KEY (report_id)
        REFERENCES analytics_reports(id) ON DELETE CASCADE,
    CONSTRAINT ck_metric_type CHECK (metric_type IN (
        'COUNTER', 'GAUGE', 'HISTOGRAM', 'SUMMARY'
    ))
);

-- Analytics Report Parameters Table
CREATE TABLE analytics_report_parameters (
    report_id UUID NOT NULL,
    param_key VARCHAR(255) NOT NULL,
    param_value TEXT,

    PRIMARY KEY (report_id, param_key),
    CONSTRAINT fk_param_report FOREIGN KEY (report_id)
        REFERENCES analytics_reports(id) ON DELETE CASCADE
);

-- Indexes for Performance
CREATE INDEX idx_analytics_tenant ON analytics_reports(tenant_id);
CREATE INDEX idx_analytics_type ON analytics_reports(report_type, tenant_id);
CREATE INDEX idx_analytics_status ON analytics_reports(status);
CREATE INDEX idx_analytics_cache ON analytics_reports(cached, cache_expires_at) WHERE cached = TRUE;
CREATE INDEX idx_analytics_generated ON analytics_reports(generated_at DESC);
CREATE INDEX idx_analytics_period ON analytics_reports(tenant_id, report_type, start_date, end_date);

CREATE INDEX idx_metric_report ON analytics_metrics(report_id);
CREATE INDEX idx_metric_name ON analytics_metrics(metric_name);
CREATE INDEX idx_metric_type ON analytics_metrics(metric_type);

-- Comments
COMMENT ON TABLE analytics_reports IS 'Cached analytics reports for performance optimization';
COMMENT ON TABLE analytics_metrics IS 'Individual metric data points for analytics reports';
COMMENT ON TABLE analytics_report_parameters IS 'Report generation parameters';

COMMENT ON COLUMN analytics_reports.cached IS 'Whether this report is cached for reuse';
COMMENT ON COLUMN analytics_reports.cache_expires_at IS 'When the cached report expires';
COMMENT ON COLUMN analytics_reports.computation_time_ms IS 'Time taken to generate the report in milliseconds';

