package com.tradesphere.ai.workflow.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain event: Workflow task approved
 */
@Getter
public class WorkflowTaskApprovedEvent extends BaseDomainEvent {
    
    private final UUID workflowId;
    private final UUID tenantId;
    private final UUID taskId;
    private final UUID approvedBy;
    private final int approvalLevel;
    
    public WorkflowTaskApprovedEvent(
            UUID workflowId,
            UUID tenantId,
            UUID taskId,
            UUID approvedBy,
            int approvalLevel) {
        
        super(workflowId.toString(), "Workflow");
        this.workflowId = workflowId;
        this.tenantId = tenantId;
        this.taskId = taskId;
        this.approvedBy = approvedBy;
        this.approvalLevel = approvalLevel;
    }
}


