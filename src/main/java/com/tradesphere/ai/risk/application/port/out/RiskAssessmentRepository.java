package com.tradesphere.ai.risk.application.port.out;

import com.tradesphere.ai.risk.domain.model.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for risk assessment persistence
 */
public interface RiskAssessmentRepository {

    /**
     * Save risk assessment
     */
    RiskAssessment save(RiskAssessment assessment);

    /**
     * Find by ID
     */
    Optional<RiskAssessment> findById(RiskAssessmentId assessmentId, UUID tenantId);

    /**
     * Find by entity
     */
    List<RiskAssessment> findByEntity(String entityType, UUID entityId, UUID tenantId);

    /**
     * Find high risk assessments
     */
    List<RiskAssessment> findHighRiskAssessments(UUID tenantId);

    /**
     * Find by status
     */
    List<RiskAssessment> findByStatus(RiskAssessmentStatus status, UUID tenantId);
}

