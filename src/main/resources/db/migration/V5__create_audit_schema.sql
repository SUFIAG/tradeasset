-- V5: Create audit schema and tables
-- High-performance audit logging with JSONB support

-- Create audit schema
CREATE SCHEMA IF NOT EXISTS audit;

-- Create audit_logs table
CREATE TABLE audit.audit_logs (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    user_id VARCHAR(100),
    user_name VARCHAR(200),
    timestamp TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    changes JSONB,
    description TEXT,
    success BOOLEAN NOT NULL DEFAULT true,
    error_message TEXT,
    execution_time_ms BIGINT,

    -- Partition key hint for future partitioning by timestamp
    CONSTRAINT audit_logs_timestamp_check CHECK (timestamp IS NOT NULL)
);

-- Indexes optimized for common query patterns
-- Index for tenant-based queries with time range
CREATE INDEX idx_audit_tenant_timestamp ON audit.audit_logs(tenant_id, timestamp DESC);

-- Index for entity-based lookups (audit trail)
CREATE INDEX idx_audit_entity ON audit.audit_logs(entity_type, entity_id, timestamp DESC);

-- Index for user activity tracking
CREATE INDEX idx_audit_user ON audit.audit_logs(user_id, timestamp DESC);

-- Index for time-based queries
CREATE INDEX idx_audit_timestamp ON audit.audit_logs(timestamp DESC);

-- Index for action-based queries
CREATE INDEX idx_audit_action ON audit.audit_logs(action, timestamp DESC);

-- GIN index for JSONB changes column for efficient JSON queries
CREATE INDEX idx_audit_changes ON audit.audit_logs USING GIN (changes);

-- Index for monitoring failed actions
CREATE INDEX idx_audit_failures ON audit.audit_logs(tenant_id, success, timestamp DESC)
WHERE success = false;

-- Comment on table
COMMENT ON TABLE audit.audit_logs IS 'Audit log for tracking all actions in the system. Optimized for high-throughput writes and efficient reads.';

-- Comments on columns
COMMENT ON COLUMN audit.audit_logs.entity_type IS 'Type of entity (Customer, LetterOfCredit, etc.)';
COMMENT ON COLUMN audit.audit_logs.entity_id IS 'UUID of the affected entity';
COMMENT ON COLUMN audit.audit_logs.action IS 'Action performed (CREATE, UPDATE, DELETE, APPROVE, etc.)';
COMMENT ON COLUMN audit.audit_logs.changes IS 'JSONB field containing old and new values for updates';
COMMENT ON COLUMN audit.audit_logs.execution_time_ms IS 'Execution time in milliseconds for performance monitoring';

-- Grant permissions (adjust as needed for your security model)
-- GRANT SELECT ON audit.audit_logs TO audit_reader_role;
-- GRANT INSERT ON audit.audit_logs TO application_role;

