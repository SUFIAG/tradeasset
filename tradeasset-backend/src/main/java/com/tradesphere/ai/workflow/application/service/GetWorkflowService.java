package com.tradesphere.ai.workflow.application.service;

import com.tradesphere.ai.workflow.application.port.in.GetWorkflowUseCase;
import com.tradesphere.ai.workflow.application.port.out.WorkflowRepository;
import com.tradesphere.ai.workflow.domain.model.Workflow;
import com.tradesphere.ai.workflow.domain.model.WorkflowId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Use case implementation for querying workflows
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class GetWorkflowService implements GetWorkflowUseCase {
    
    private final WorkflowRepository workflowRepository;
    
    @Override
    public Optional<Workflow> getWorkflow(WorkflowId workflowId, UUID tenantId) {
        log.debug("Getting workflow: id={}, tenantId={}", workflowId, tenantId);
        return workflowRepository.findById(workflowId, tenantId);
    }
    
    @Override
    public Optional<Workflow> getWorkflowByEntity(String entityType, UUID entityId, UUID tenantId) {
        log.debug("Getting workflow by entity: type={}, id={}, tenantId={}", 
                entityType, entityId, tenantId);
        return workflowRepository.findByEntity(entityType, entityId, tenantId);
    }
}


