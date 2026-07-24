package com.tradesphere.ai.risk.adapter.out.persistence;

import com.tradesphere.ai.risk.domain.model.RiskAssessmentStatus;
import com.tradesphere.ai.risk.domain.model.RiskLevel;
import com.tradesphere.ai.shared.multitenancy.TenantAwareJpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * JPA repository for risk assessments
 */
@Repository
public interface RiskAssessmentJpaRepository extends TenantAwareJpaRepository<RiskAssessmentJpaEntity, UUID> {
    
    List<RiskAssessmentJpaEntity> findAllByEntityTypeAndEntityIdAndTenantId(
            String entityType, UUID entityId, UUID tenantId);
    
    @Query("SELECT r FROM RiskAssessmentJpaEntity r WHERE r.overallRiskLevel IN ('HIGH', 'VERY_HIGH') " +
           "AND r.tenantId = :tenantId ORDER BY r.initiatedAt DESC")
    List<RiskAssessmentJpaEntity> findHighRiskAssessments(@Param("tenantId") UUID tenantId);
    
    List<RiskAssessmentJpaEntity> findByStatusAndTenantId(RiskAssessmentStatus status, UUID tenantId);
    
    List<RiskAssessmentJpaEntity> findByOverallRiskLevelAndTenantId(RiskLevel riskLevel, UUID tenantId);
}

