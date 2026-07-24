package com.tradesphere.ai.workflow.application.port.out;

import com.tradesphere.ai.workflow.domain.model.Workflow;
import com.tradesphere.ai.workflow.domain.model.WorkflowId;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for workflow persistence
 */
public interface WorkflowRepository {
    
    /**
     * Save workflow
     */
    Workflow save(Workflow workflow);
    
    /**
     * Find workflow by ID
     */
    Optional<Workflow> findById(WorkflowId workflowId, UUID tenantId);
    
    /**
     * Find workflow by entity
     */
    Optional<Workflow> findByEntity(String entityType, UUID entityId, UUID tenantId);
    
    /**
     * Find all workflows for entity
     */
    List<Workflow> findAllByEntity(String entityType, UUID entityId, UUID tenantId);
    
    /**
     * Find pending workflows (for SLA monitoring)
     */
    List<Workflow> findPendingWorkflows(UUID tenantId);
    
    /**
     * Find all active tenant IDs (tenants with pending workflows)
     */
    List<UUID> findActiveTenantIds();

    /**
     * Check if workflow exists
     */
    boolean exists(WorkflowId workflowId, UUID tenantId);
}


