package com.tradesphere.ai.workflow.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain event: Workflow cancelled
 */
@Getter
public class WorkflowCancelledEvent extends BaseDomainEvent {

    private final UUID workflowId;
    private final UUID tenantId;
    private final String reason;

    public WorkflowCancelledEvent(
            UUID workflowId,
            UUID tenantId,
            String reason) {

        super(workflowId.toString(), "Workflow");
        this.workflowId = workflowId;
        this.tenantId = tenantId;
        this.reason = reason;
    }
}


