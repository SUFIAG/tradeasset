package com.tradesphere.ai.risk.adapter.out.persistence;

import com.tradesphere.ai.risk.domain.model.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Mapper between domain model and JPA entity for risk assessments
 */
@Component
public class RiskAssessmentMapper {

    public RiskAssessmentJpaEntity toJpaEntity(RiskAssessment domain) {
        RiskAssessmentJpaEntity entity = new RiskAssessmentJpaEntity();

        entity.setId(domain.getId().getValue());
        entity.setTenantId(domain.getTenantId());
        entity.setAssessmentNumber(domain.getAssessmentNumber());
        entity.setRiskType(domain.getRiskType());
        entity.setEntityType(domain.getEntityType());
        entity.setEntityId(domain.getEntityId());
        entity.setEntityName(domain.getEntityName());
        entity.setStatus(domain.getStatus());
        entity.setInitiatedAt(toLocalDateTime(domain.getInitiatedAt()));
        entity.setCompletedAt(toLocalDateTime(domain.getCompletedAt()));
        entity.setInitiatedBy(domain.getInitiatedBy());
        entity.setRiskFactors(domain.getRiskFactors());
        entity.setOverallRiskScore(domain.getOverallRiskScore());
        entity.setOverallRiskLevel(domain.getOverallRiskLevel());
        entity.setRiskSummary(domain.getRiskSummary());
        entity.setAiReasoning(domain.getAiReasoning());
        entity.setCountryCode(domain.getCountryCode());
        entity.setCountryRiskScore(domain.getCountryRiskScore());
        entity.setCreditRiskScore(domain.getCreditRiskScore());
        entity.setPaymentHistoryScore(domain.getPaymentHistoryScore());
        entity.setFinancialHealthScore(domain.getFinancialHealthScore());
        entity.setTransactionRiskScore(domain.getTransactionRiskScore());
        entity.setDocumentaryRiskScore(domain.getDocumentaryRiskScore());
        entity.setRecommendation(domain.getRecommendation());
        entity.setRequiresApproval(domain.isRequiresApproval());
        entity.setAutoRejected(domain.isAutoRejected());
        entity.setVersion(domain.getVersion());

        return entity;
    }

    public RiskAssessment toDomainModel(RiskAssessmentJpaEntity entity) {
        try {
            var constructor = RiskAssessment.class.getDeclaredConstructor(
                    RiskAssessmentId.class, java.util.UUID.class);
            constructor.setAccessible(true);

            RiskAssessment domain = constructor.newInstance(
                    RiskAssessmentId.of(entity.getId()),
                    entity.getTenantId()
            );

            // Use reflection to set private fields
            setField(domain, "assessmentNumber", entity.getAssessmentNumber());
            setField(domain, "riskType", entity.getRiskType());
            setField(domain, "entityType", entity.getEntityType());
            setField(domain, "entityId", entity.getEntityId());
            setField(domain, "entityName", entity.getEntityName());
            setField(domain, "status", entity.getStatus());
            setField(domain, "initiatedAt", toInstant(entity.getInitiatedAt()));
            setField(domain, "completedAt", toInstant(entity.getCompletedAt()));
            setField(domain, "initiatedBy", entity.getInitiatedBy());
            setField(domain, "riskFactors", entity.getRiskFactors());
            setField(domain, "overallRiskScore", entity.getOverallRiskScore());
            setField(domain, "overallRiskLevel", entity.getOverallRiskLevel());
            setField(domain, "riskSummary", entity.getRiskSummary());
            setField(domain, "aiReasoning", entity.getAiReasoning());
            setField(domain, "countryCode", entity.getCountryCode());
            setField(domain, "countryRiskScore", entity.getCountryRiskScore());
            setField(domain, "creditRiskScore", entity.getCreditRiskScore());
            setField(domain, "paymentHistoryScore", entity.getPaymentHistoryScore());
            setField(domain, "financialHealthScore", entity.getFinancialHealthScore());
            setField(domain, "transactionRiskScore", entity.getTransactionRiskScore());
            setField(domain, "documentaryRiskScore", entity.getDocumentaryRiskScore());
            setField(domain, "recommendation", entity.getRecommendation());
            setField(domain, "requiresApproval", entity.isRequiresApproval());
            setField(domain, "autoRejected", entity.isAutoRejected());
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

