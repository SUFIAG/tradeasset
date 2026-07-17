-- V7: Create workflow schema and tables
-- Complete workflow and approval management

-- Create workflow tables in workflow schema
CREATE TABLE workflow.workflows (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    type VARCHAR(50) NOT NULL,
    workflow_name VARCHAR(200) NOT NULL,
    description TEXT,

    -- Entity reference
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,

    -- Status & lifecycle
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    current_level INTEGER NOT NULL DEFAULT 0,
    total_levels INTEGER NOT NULL DEFAULT 0,

    -- SLA management
    due_date TIMESTAMP,
    sla_breached BOOLEAN NOT NULL DEFAULT false,
    sla_hours INTEGER NOT NULL,

    -- Initiator
    initiated_by UUID NOT NULL,
    initiator_name VARCHAR(200) NOT NULL,

    -- Completion tracking
    completed_tasks INTEGER NOT NULL DEFAULT 0,
    rejected_tasks INTEGER NOT NULL DEFAULT 0,

    -- Outcome
    final_outcome VARCHAR(50),
    final_comments TEXT,

    -- Audit fields
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_by VARCHAR(100),

    CONSTRAINT workflows_pk PRIMARY KEY (id),
    CONSTRAINT workflows_tenant_fk FOREIGN KEY (tenant_id) REFERENCES organization.tenants(id)
);

-- Create workflow tasks table
CREATE TABLE workflow.workflow_tasks (
    id UUID PRIMARY KEY,
    workflow_id UUID NOT NULL,

    -- Task details
    task_name VARCHAR(200) NOT NULL,
    task_description TEXT,
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(20) NOT NULL,

    -- Assignment
    assigned_to UUID,
    assigned_to_name VARCHAR(200),
    assigned_by UUID,
    assigned_at TIMESTAMP,

    -- Execution
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    outcome VARCHAR(50),
    comments TEXT,

    -- SLA tracking
    due_date TIMESTAMP NOT NULL,
    sla_breached BOOLEAN NOT NULL DEFAULT false,
    escalated_at TIMESTAMP,
    escalated_to UUID,

    -- Ordering
    sequence_order INTEGER NOT NULL,

    -- Audit fields
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT workflow_tasks_pk PRIMARY KEY (id),
    CONSTRAINT workflow_tasks_workflow_fk FOREIGN KEY (workflow_id)
        REFERENCES workflow.workflows(id) ON DELETE CASCADE
);

-- Indexes for workflows table
CREATE INDEX idx_workflows_tenant ON workflow.workflows(tenant_id);
CREATE INDEX idx_workflows_entity ON workflow.workflows(entity_type, entity_id);
CREATE INDEX idx_workflows_status ON workflow.workflows(status, tenant_id);
CREATE INDEX idx_workflows_due_date ON workflow.workflows(due_date, status);
CREATE INDEX idx_workflows_type ON workflow.workflows(type, tenant_id);

-- Indexes for workflow_tasks table
CREATE INDEX idx_workflow_tasks_workflow ON workflow.workflow_tasks(workflow_id);
CREATE INDEX idx_workflow_tasks_assigned ON workflow.workflow_tasks(assigned_to, status);
CREATE INDEX idx_workflow_tasks_status ON workflow.workflow_tasks(status);
CREATE INDEX idx_workflow_tasks_due_date ON workflow.workflow_tasks(due_date, status);
CREATE INDEX idx_workflow_tasks_sequence ON workflow.workflow_tasks(workflow_id, sequence_order);

-- Comments
COMMENT ON TABLE workflow.workflows IS 'Approval workflows for various business processes';
COMMENT ON TABLE workflow.workflow_tasks IS 'Individual tasks within a workflow';

COMMENT ON COLUMN workflow.workflows.type IS 'Type of workflow (CUSTOMER_APPROVAL, LC_APPROVAL, etc.)';
COMMENT ON COLUMN workflow.workflows.entity_type IS 'Type of entity being approved (Customer, LC, etc.)';
COMMENT ON COLUMN workflow.workflows.entity_id IS 'ID of the entity being approved';
COMMENT ON COLUMN workflow.workflows.current_level IS 'Current approval level (1-based)';
COMMENT ON COLUMN workflow.workflows.total_levels IS 'Total number of approval levels';
COMMENT ON COLUMN workflow.workflows.sla_breached IS 'Whether SLA has been breached';

COMMENT ON COLUMN workflow.workflow_tasks.sequence_order IS 'Order in which task should be executed';
COMMENT ON COLUMN workflow.workflow_tasks.sla_breached IS 'Whether task SLA has been breached';
COMMENT ON COLUMN workflow.workflow_tasks.escalated_to IS 'User ID task was escalated to';


