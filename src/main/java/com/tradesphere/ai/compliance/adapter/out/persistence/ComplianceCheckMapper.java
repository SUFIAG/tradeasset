package com.tradesphere.ai.compliance.adapter.out.persistence;

import com.tradesphere.ai.compliance.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Mapper between domain model and JPA entity for compliance checks
 */
@Component
public class ComplianceCheckMapper {

    public ComplianceCheckJpaEntity toJpaEntity(ComplianceCheck domain) {
        ComplianceCheckJpaEntity entity = new ComplianceCheckJpaEntity();

        entity.setId(domain.getId().getValue());
        entity.setTenantId(domain.getTenantId());
        entity.setCheckNumber(domain.getCheckNumber());
        entity.setEntityType(domain.getEntityType());
        entity.setEntityId(domain.getEntityId());
        entity.setEntityName(domain.getEntityName());
        entity.setCheckType(domain.getCheckType());
        entity.setStatus(domain.getStatus());
        entity.setInitiatedAt(toLocalDateTime(domain.getInitiatedAt()));
        entity.setCompletedAt(toLocalDateTime(domain.getCompletedAt()));
        entity.setInitiatedBy(domain.getInitiatedBy());
        entity.setSanctionsHits(domain.getSanctionsHits());
        entity.setPepHits(domain.getPepHits());
        entity.setAmlHits(domain.getAmlHits());
        entity.setAiConfidenceScore(domain.getAiConfidenceScore());
        entity.setFalsePositiveProbability(domain.getFalsePositiveProbability());
        entity.setAiReasoning(domain.getAiReasoning());
        entity.setDecision(domain.getDecision());
        entity.setDecisionReason(domain.getDecisionReason());
        entity.setDecidedBy(domain.getDecidedBy());
        entity.setDecidedAt(toLocalDateTime(domain.getDecidedAt()));
        entity.setRequiresManualReview(domain.isRequiresManualReview());
        entity.setReviewComments(domain.getReviewComments());
        entity.setVersion(domain.getVersion());

        return entity;
    }

    public ComplianceCheck toDomainModel(ComplianceCheckJpaEntity entity) {
        // Use reflection to create domain object (since constructor is private)
        try {
            var constructor = ComplianceCheck.class.getDeclaredConstructor(
                    ComplianceCheckId.class, java.util.UUID.class);
            constructor.setAccessible(true);

            ComplianceCheck domain = constructor.newInstance(
                    ComplianceCheckId.of(entity.getId()),
                    entity.getTenantId()
            );

            // Use reflection to set private fields
            setField(domain, "checkNumber", entity.getCheckNumber());
            setField(domain, "entityType", entity.getEntityType());
            setField(domain, "entityId", entity.getEntityId());
            setField(domain, "entityName", entity.getEntityName());
            setField(domain, "checkType", entity.getCheckType());
            setField(domain, "status", entity.getStatus());
            setField(domain, "initiatedAt", toInstant(entity.getInitiatedAt()));
            setField(domain, "completedAt", toInstant(entity.getCompletedAt()));
            setField(domain, "initiatedBy", entity.getInitiatedBy());
            setField(domain, "sanctionsHits", entity.getSanctionsHits());
            setField(domain, "pepHits", entity.getPepHits());
            setField(domain, "amlHits", entity.getAmlHits());
            setField(domain, "aiConfidenceScore", entity.getAiConfidenceScore());
            setField(domain, "falsePositiveProbability", entity.getFalsePositiveProbability());
            setField(domain, "aiReasoning", entity.getAiReasoning());
            setField(domain, "decision", entity.getDecision());
            setField(domain, "decisionReason", entity.getDecisionReason());
            setField(domain, "decidedBy", entity.getDecidedBy());
            setField(domain, "decidedAt", toInstant(entity.getDecidedAt()));
            setField(domain, "requiresManualReview", entity.isRequiresManualReview());
            setField(domain, "reviewComments", entity.getReviewComments());
            setField(domain, "version", entity.getVersion());

            return domain;
        } catch (Exception e) {
            throw new RuntimeException("Failed to map JPA entity to domain model", e);
        }
    }

    private LocalDateTime toLocalDateTime(Instant instant) {
        return instant != null ? LocalDateTime.ofInstant(instant, ZoneId.systemDefault()) : null;
    }

    private Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atZone(ZoneId.systemDefault()).toInstant() : null;
    }

    private void setField(Object target, String fieldName, Object value) {
        try {
            var field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set field: " + fieldName, e);
        }
    }
}

