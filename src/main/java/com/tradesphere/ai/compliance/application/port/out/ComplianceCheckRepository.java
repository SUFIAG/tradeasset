package com.tradesphere.ai.compliance.application.port.out;

import com.tradesphere.ai.compliance.domain.model.*;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Outbound port for compliance check persistence
 */
public interface ComplianceCheckRepository {

    /**
     * Save compliance check
     */
    ComplianceCheck save(ComplianceCheck complianceCheck);

    /**
     * Find compliance check by ID
     */
    Optional<ComplianceCheck> findById(ComplianceCheckId checkId, UUID tenantId);

    /**
     * Find compliance check by entity
     */
    Optional<ComplianceCheck> findByEntity(String entityType, UUID entityId, UUID tenantId);

    /**
     * Find all compliance checks for entity
     */
    List<ComplianceCheck> findAllByEntity(String entityType, UUID entityId, UUID tenantId);

    /**
     * Find checks requiring manual review
     */
    List<ComplianceCheck> findChecksRequiringReview(UUID tenantId);

    /**
     * Find checks by status
     */
    List<ComplianceCheck> findByStatus(ComplianceCheckStatus status, UUID tenantId);

    /**
     * Find checks by decision
     */
    List<ComplianceCheck> findByDecision(ComplianceDecision decision, UUID tenantId);

    /**
     * Find recent checks (for monitoring)
     */
    List<ComplianceCheck> findRecentChecks(UUID tenantId, Instant since);

    /**
     * Check if entity has passed compliance
     */
    boolean hasPassedCompliance(String entityType, UUID entityId, UUID tenantId);

    /**
     * Check if exists
     */
    boolean exists(ComplianceCheckId checkId, UUID tenantId);
}

