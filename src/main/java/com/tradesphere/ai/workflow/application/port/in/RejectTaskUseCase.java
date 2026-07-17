package com.tradesphere.ai.workflow.application.port.in;

/**
 * Inbound port for rejecting workflow tasks
 */
public interface RejectTaskUseCase {
    
    /**
     * Reject a workflow task (fails entire workflow)
     * 
     * @param command rejection command
     */
    void rejectTask(RejectTaskCommand command);
}


