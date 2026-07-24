-- Analytics Module Tables
-- Add missing analytics_reports, analytics_report_parameters, and analytics_metrics tables

-- Analytics Reports Table
CREATE TABLE analytics_reports (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    report_name VARCHAR(255) NOT NULL,
    report_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    start_date DATE,
    end_date DATE,
    generated_by VARCHAR(255),
    generated_at TIMESTAMP WITH TIME ZONE,
    computation_time_ms BIGINT,
    cache_expires_at TIMESTAMP WITH TIME ZONE,
    cached BOOLEAN DEFAULT FALSE,
    created_by UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE,
    version BIGINT,
    CONSTRAINT fk_analytics_reports_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE INDEX idx_analytics_tenant ON analytics_reports(tenant_id);
CREATE INDEX idx_analytics_type ON analytics_reports(report_type, tenant_id);
CREATE INDEX idx_analytics_status ON analytics_reports(status);
CREATE INDEX idx_analytics_cache ON analytics_reports(cached, cache_expires_at);
CREATE INDEX idx_analytics_generated ON analytics_reports(generated_at DESC);

-- Analytics Report Parameters Table (ElementCollection)
CREATE TABLE analytics_report_parameters (
    report_id UUID NOT NULL,
    param_key VARCHAR(255) NOT NULL,
    param_value TEXT,
    PRIMARY KEY (report_id, param_key),
    CONSTRAINT fk_analytics_params_report FOREIGN KEY (report_id) REFERENCES analytics_reports(id) ON DELETE CASCADE
);

-- Analytics Metrics Table
CREATE TABLE analytics_metrics (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    report_id UUID NOT NULL,
    metric_name VARCHAR(255) NOT NULL,
    metric_value NUMERIC(19, 4) NOT NULL,
    metric_unit VARCHAR(50),
    metric_type VARCHAR(50),
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_analytics_metrics_report FOREIGN KEY (report_id) REFERENCES analytics_reports(id) ON DELETE CASCADE
);

CREATE INDEX idx_metric_report ON analytics_metrics(report_id);
CREATE INDEX idx_metric_name ON analytics_metrics(metric_name);
CREATE INDEX idx_metric_type ON analytics_metrics(metric_type);

COMMIT;

