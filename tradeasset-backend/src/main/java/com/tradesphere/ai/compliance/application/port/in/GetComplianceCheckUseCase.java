package com.tradesphere.ai.compliance.application.port.in;

import com.tradesphere.ai.compliance.domain.model.ComplianceCheck;
import com.tradesphere.ai.compliance.domain.model.ComplianceCheckId;

import java.util.List;
import java.util.UUID;

/**
 * Inbound port for querying compliance checks
 */
public interface GetComplianceCheckUseCase {

    /**
     * Get compliance check by ID
     */
    ComplianceCheck getById(UUID complianceCheckId, UUID tenantId);

    /**
     * Get compliance checks for entity
     */
    List<ComplianceCheck> getByEntity(String entityType, UUID entityId, UUID tenantId);

    /**
     * Get checks requiring manual review
     */
    List<ComplianceCheck> getChecksRequiringReview(UUID tenantId);

    /**
     * Check if entity has passed compliance
     */
    boolean hasPassedCompliance(String entityType, UUID entityId, UUID tenantId);
}

