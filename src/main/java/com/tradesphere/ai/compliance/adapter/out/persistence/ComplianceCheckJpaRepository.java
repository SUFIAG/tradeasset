package com.tradesphere.ai.compliance.adapter.out.persistence;

import com.tradesphere.ai.compliance.domain.model.ComplianceCheckStatus;
import com.tradesphere.ai.compliance.domain.model.ComplianceDecision;
import com.tradesphere.ai.shared.multitenancy.TenantAwareJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA repository for compliance checks
 */
@Repository
public interface ComplianceCheckJpaRepository extends TenantAwareJpaRepository<ComplianceCheckJpaEntity, UUID> {
    
    Optional<ComplianceCheckJpaEntity> findByEntityTypeAndEntityIdAndTenantId(
            String entityType, UUID entityId, UUID tenantId);
    
    List<ComplianceCheckJpaEntity> findAllByEntityTypeAndEntityIdAndTenantId(
            String entityType, UUID entityId, UUID tenantId);
    
    @Query("SELECT c FROM ComplianceCheckJpaEntity c WHERE c.requiresManualReview = true " +
           "AND c.tenantId = :tenantId ORDER BY c.initiatedAt DESC")
    List<ComplianceCheckJpaEntity> findChecksRequiringReview(@Param("tenantId") UUID tenantId);
    
    List<ComplianceCheckJpaEntity> findByStatusAndTenantId(ComplianceCheckStatus status, UUID tenantId);
    
    List<ComplianceCheckJpaEntity> findByDecisionAndTenantId(ComplianceDecision decision, UUID tenantId);
    
    @Query("SELECT c FROM ComplianceCheckJpaEntity c WHERE c.initiatedAt >= :since " +
           "AND c.tenantId = :tenantId ORDER BY c.initiatedAt DESC")
    List<ComplianceCheckJpaEntity> findRecentChecks(
            @Param("since") LocalDateTime since, 
            @Param("tenantId") UUID tenantId);
    
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
           "FROM ComplianceCheckJpaEntity c WHERE c.entityType = :entityType " +
           "AND c.entityId = :entityId AND c.tenantId = :tenantId " +
           "AND (c.decision = 'APPROVED' OR c.decision = 'AUTO_APPROVED' " +
           "OR c.decision = 'FALSE_POSITIVE')")
    boolean hasPassedCompliance(
            @Param("entityType") String entityType, 
            @Param("entityId") UUID entityId, 
            @Param("tenantId") UUID tenantId);
}

