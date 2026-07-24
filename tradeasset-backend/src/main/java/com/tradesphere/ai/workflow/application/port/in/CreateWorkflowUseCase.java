package com.tradesphere.ai.workflow.application.port.in;

import com.tradesphere.ai.workflow.domain.model.WorkflowId;

/**
 * Inbound port for creating workflows
 */
public interface CreateWorkflowUseCase {
    
    /**
     * Create and start a new workflow
     * 
     * @param command workflow creation command
     * @return workflow ID
     */
    WorkflowId createWorkflow(CreateWorkflowCommand command);
}


