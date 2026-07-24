-- Fix workflows table structure to match Workflow entity
-- Version: 6.0.0
-- Description: Add missing columns and align with domain model

-- Add all missing columns that the Workflow entity expects
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS completed_tasks INTEGER DEFAULT 0;
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS rejected_tasks INTEGER DEFAULT 0;
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS type VARCHAR(50);
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS description TEXT;
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS initiator_name VARCHAR(255);
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS sla_hours INTEGER DEFAULT 24;
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS final_outcome VARCHAR(50);
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS final_comments TEXT;
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS status VARCHAR(20);
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS current_level INTEGER DEFAULT 0;
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS total_levels INTEGER;
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS sla_breached BOOLEAN DEFAULT FALSE;
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS started_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE workflows ADD COLUMN IF NOT EXISTS due_date TIMESTAMP WITH TIME ZONE;

-- Use PL/pgSQL to safely rename columns
DO $$
BEGIN
    -- Copy workflow_status to status if workflow_status exists
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'workflows' AND column_name = 'workflow_status') THEN
        EXECUTE 'UPDATE workflows SET status = workflow_status WHERE status IS NULL';
    END IF;

    -- Copy current_step_index to current_level if it exists
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'workflows' AND column_name = 'current_step_index') THEN
        EXECUTE 'UPDATE workflows SET current_level = current_step_index WHERE current_level = 0 OR current_level IS NULL';
    END IF;

    -- Copy total_steps to total_levels if it exists
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'workflows' AND column_name = 'total_steps') THEN
        EXECUTE 'UPDATE workflows SET total_levels = total_steps WHERE total_levels IS NULL';
    END IF;

    -- Copy assigned_to to initiated_by if needed
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'workflows' AND column_name = 'assigned_to') THEN
        IF NOT EXISTS (SELECT 1 FROM information_schema.columns
                      WHERE table_name = 'workflows' AND column_name = 'initiated_by') THEN
            EXECUTE 'ALTER TABLE workflows RENAME COLUMN assigned_to TO initiated_by';
        ELSE
            EXECUTE 'UPDATE workflows SET initiated_by = COALESCE(initiated_by, assigned_to)';
        END IF;
    END IF;

    -- Copy sla_breach to sla_breached if it exists
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'workflows' AND column_name = 'sla_breach') THEN
        EXECUTE 'UPDATE workflows SET sla_breached = COALESCE(sla_breached, sla_breach, FALSE)';
    END IF;

    -- Copy assigned_at to started_at if needed
    IF EXISTS (SELECT 1 FROM information_schema.columns
               WHERE table_name = 'workflows' AND column_name = 'assigned_at') THEN
        EXECUTE 'UPDATE workflows SET started_at = COALESCE(started_at, assigned_at)';
    END IF;
END $$;

-- Copy workflow_type to type
UPDATE workflows SET type = workflow_type WHERE type IS NULL AND workflow_type IS NOT NULL;

-- Set NOT NULL and defaults for required columns
UPDATE workflows SET
    completed_tasks = COALESCE(completed_tasks, 0),
    rejected_tasks = COALESCE(rejected_tasks, 0),
    sla_hours = COALESCE(sla_hours, 24),
    sla_breached = COALESCE(sla_breached, FALSE),
    current_level = COALESCE(current_level, 0),
    total_levels = COALESCE(total_levels, 1),
    status = COALESCE(status, 'PENDING');

-- Create indexes for performance
CREATE INDEX IF NOT EXISTS idx_workflows_status ON workflows(status);
CREATE INDEX IF NOT EXISTS idx_workflows_type ON workflows(type);
CREATE INDEX IF NOT EXISTS idx_workflows_initiated_by ON workflows(initiated_by);
CREATE INDEX IF NOT EXISTS idx_workflows_due_date ON workflows(due_date);
CREATE INDEX IF NOT EXISTS idx_workflows_tenant_status ON workflows(tenant_id, status);

