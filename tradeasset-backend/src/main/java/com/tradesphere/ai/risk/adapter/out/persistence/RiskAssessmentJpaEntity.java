package com.tradesphere.ai.risk.adapter.out.persistence;

import com.tradesphere.ai.risk.domain.model.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity for risk assessments
 */
@Entity
@Table(name = "risk_assessments", indexes = {
        @Index(name = "idx_risk_tenant", columnList = "tenant_id"),
        @Index(name = "idx_risk_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_risk_status", columnList = "status, tenant_id"),
        @Index(name = "idx_risk_level", columnList = "overall_risk_level, tenant_id")
})
@Data
public class RiskAssessmentJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "assessment_number", nullable = false, unique = true)
    private String assessmentNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "risk_type", nullable = false)
    private RiskType riskType;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RiskAssessmentStatus status;

    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "initiated_by", nullable = false)
    private UUID initiatedBy;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "risk_factors", columnDefinition = "jsonb")
    private List<RiskFactor> riskFactors = new ArrayList<>();

    @Column(name = "overall_risk_score")
    private Integer overallRiskScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_risk_level")
    private RiskLevel overallRiskLevel;

    @Column(name = "risk_summary", length = 2000)
    private String riskSummary;

    @Column(name = "ai_reasoning", length = 2000)
    private String aiReasoning;

    @Column(name = "country_code", length = 3)
    private String countryCode;

    @Column(name = "country_risk_score")
    private Integer countryRiskScore;

    @Column(name = "credit_risk_score")
    private Integer creditRiskScore;

    @Column(name = "payment_history_score")
    private Integer paymentHistoryScore;

    @Column(name = "financial_health_score")
    private Integer financialHealthScore;

    @Column(name = "transaction_risk_score")
    private Integer transactionRiskScore;

    @Column(name = "documentary_risk_score")
    private Integer documentaryRiskScore;

    @Column(name = "recommendation", length = 1000)
    private String recommendation;

    @Column(name = "requires_approval", nullable = false)
    private boolean requiresApproval = false;

    @Column(name = "auto_rejected", nullable = false)
    private boolean autoRejected = false;

    @Version
    @Column(name = "version")
    private Long version;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

