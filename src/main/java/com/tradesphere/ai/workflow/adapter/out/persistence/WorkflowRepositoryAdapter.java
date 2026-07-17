package com.tradesphere.ai.workflow.adapter.out.persistence;

import com.tradesphere.ai.workflow.application.port.out.WorkflowRepository;
import com.tradesphere.ai.workflow.domain.model.Workflow;
import com.tradesphere.ai.workflow.domain.model.WorkflowId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA adapter implementation for workflow repository
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class WorkflowRepositoryAdapter implements WorkflowRepository {

    private final WorkflowJpaRepository jpaRepository;
    private final WorkflowMapper mapper;

    @Override
    public Workflow save(Workflow workflow) {
        log.debug("Saving workflow: id={}", workflow.getId());
        WorkflowJpaEntity entity = mapper.toJpaEntity(workflow);
        WorkflowJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Workflow> findById(WorkflowId workflowId, UUID tenantId) {
        log.debug("Finding workflow: id={}, tenantId={}", workflowId, tenantId);
        return jpaRepository.findByIdAndTenantId(workflowId.getValue(), tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Workflow> findByEntity(String entityType, UUID entityId, UUID tenantId) {
        log.debug("Finding workflow by entity: type={}, id={}, tenantId={}",
                entityType, entityId, tenantId);
        return jpaRepository.findByEntityTypeAndEntityIdAndTenantId(entityType, entityId, tenantId)
                .map(mapper::toDomain);
    }

    @Override
    public List<Workflow> findAllByEntity(String entityType, UUID entityId, UUID tenantId) {
        log.debug("Finding all workflows by entity: type={}, id={}, tenantId={}",
                entityType, entityId, tenantId);
        return jpaRepository.findAllByEntityTypeAndEntityIdAndTenantId(entityType, entityId, tenantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Workflow> findPendingWorkflows(UUID tenantId) {
        log.debug("Finding pending workflows: tenantId={}", tenantId);
        return jpaRepository.findPendingWorkflows(tenantId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<UUID> findActiveTenantIds() {
        log.debug("Finding all active tenant IDs");
        return jpaRepository.findActiveTenantIds();
    }

    @Override
    public boolean exists(WorkflowId workflowId, UUID tenantId) {
        return jpaRepository.existsByIdAndTenantId(workflowId.getValue(), tenantId);
    }
}


