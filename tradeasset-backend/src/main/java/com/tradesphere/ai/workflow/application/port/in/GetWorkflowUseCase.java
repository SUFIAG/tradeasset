package com.tradesphere.ai.workflow.application.port.in;

import com.tradesphere.ai.workflow.domain.model.Workflow;
import com.tradesphere.ai.workflow.domain.model.WorkflowId;

import java.util.Optional;
import java.util.UUID;

/**
 * Inbound port for querying workflows
 */
public interface GetWorkflowUseCase {

    /**
     * Get workflow by ID
     *
     * @param workflowId workflow ID
     * @param tenantId tenant ID
     * @return workflow if found
     */
    Optional<Workflow> getWorkflow(WorkflowId workflowId, UUID tenantId);

    /**
     * Get workflow by entity
     *
     * @param entityType entity type
     * @param entityId entity ID
     * @param tenantId tenant ID
     * @return workflow if found
     */
    Optional<Workflow> getWorkflowByEntity(String entityType, UUID entityId, UUID tenantId);
}


