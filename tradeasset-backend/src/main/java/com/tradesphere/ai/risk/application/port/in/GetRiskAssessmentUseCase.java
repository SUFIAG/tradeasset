package com.tradesphere.ai.risk.application.port.in;

import com.tradesphere.ai.risk.domain.model.RiskAssessment;

import java.util.List;
import java.util.UUID;

/**
 * Inbound port for querying risk assessments
 */
public interface GetRiskAssessmentUseCase {
    
    /**
     * Get risk assessment by ID
     */
    RiskAssessment getById(UUID assessmentId, UUID tenantId);
    
    /**
     * Get risk assessments for entity
     */
    List<RiskAssessment> getByEntity(String entityType, UUID entityId, UUID tenantId);
    
    /**
     * Get high risk assessments requiring review
     */
    List<RiskAssessment> getHighRiskAssessments(UUID tenantId);
}

