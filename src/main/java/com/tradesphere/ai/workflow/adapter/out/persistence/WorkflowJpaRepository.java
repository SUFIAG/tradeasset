package com.tradesphere.ai.workflow.adapter.out.persistence;

import com.tradesphere.ai.workflow.domain.model.WorkflowStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for workflows
 */
@Repository
public interface WorkflowJpaRepository extends JpaRepository<WorkflowJpaEntity, UUID> {
    
    /**
     * Find workflow by ID and tenant
     */
    Optional<WorkflowJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);
    
    /**
     * Find workflow by entity and tenant
     */
    Optional<WorkflowJpaEntity> findByEntityTypeAndEntityIdAndTenantId(
            String entityType, UUID entityId, UUID tenantId);
    
    /**
     * Find all workflows for entity
     */
    List<WorkflowJpaEntity> findAllByEntityTypeAndEntityIdAndTenantId(
            String entityType, UUID entityId, UUID tenantId);
    
    /**
     * Find pending workflows for SLA monitoring
     */
    @Query("SELECT w FROM WorkflowJpaEntity w WHERE w.tenantId = :tenantId " +
           "AND w.status IN ('IN_PROGRESS', 'WAITING_FOR_INPUT') " +
           "ORDER BY w.dueDate ASC")
    List<WorkflowJpaEntity> findPendingWorkflows(@Param("tenantId") UUID tenantId);
    
    /**
     * Find all active tenant IDs (tenants with pending workflows)
     */
    @Query("SELECT DISTINCT w.tenantId FROM WorkflowJpaEntity w " +
           "WHERE w.status IN ('IN_PROGRESS', 'WAITING_FOR_INPUT')")
    List<UUID> findActiveTenantIds();

    /**
     * Check if workflow exists
     */
    boolean existsByIdAndTenantId(UUID id, UUID tenantId);
}


