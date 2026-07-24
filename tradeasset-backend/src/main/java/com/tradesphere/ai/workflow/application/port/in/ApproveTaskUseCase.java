package com.tradesphere.ai.workflow.application.port.in;

/**
 * Inbound port for approving workflow tasks
 */
public interface ApproveTaskUseCase {
    
    /**
     * Approve a workflow task
     * 
     * @param command approval command
     */
    void approveTask(ApproveTaskCommand command);
}


