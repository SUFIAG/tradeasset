package com.tradesphere.ai.compliance.adapter.out.persistence;

import com.tradesphere.ai.compliance.domain.model.*;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity for compliance checks
 */
@Entity
@Table(name = "compliance_checks", indexes = {
        @Index(name = "idx_compliance_tenant", columnList = "tenant_id"),
        @Index(name = "idx_compliance_entity", columnList = "entity_type, entity_id"),
        @Index(name = "idx_compliance_status", columnList = "status, tenant_id"),
        @Index(name = "idx_compliance_decision", columnList = "decision, tenant_id"),
        @Index(name = "idx_compliance_review", columnList = "requires_manual_review, tenant_id"),
        @Index(name = "idx_compliance_check_number", columnList = "check_number")
})
@Data
public class ComplianceCheckJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "check_number", nullable = false, unique = true)
    private String checkNumber;

    @Column(name = "entity_type", nullable = false)
    private String entityType;

    @Column(name = "entity_id", nullable = false)
    private UUID entityId;

    @Column(name = "entity_name", nullable = false)
    private String entityName;

    @Enumerated(EnumType.STRING)
    @Column(name = "check_type", nullable = false)
    private ComplianceCheckType checkType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ComplianceCheckStatus status;

    @Column(name = "initiated_at", nullable = false)
    private LocalDateTime initiatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "initiated_by", nullable = false)
    private UUID initiatedBy;

    // Store screening hits as JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "sanctions_hits", columnDefinition = "jsonb")
    private List<ScreeningHit> sanctionsHits = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "pep_hits", columnDefinition = "jsonb")
    private List<ScreeningHit> pepHits = new ArrayList<>();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "aml_hits", columnDefinition = "jsonb")
    private List<ScreeningHit> amlHits = new ArrayList<>();

    @Column(name = "ai_confidence_score")
    private Double aiConfidenceScore;

    @Column(name = "false_positive_probability")
    private Integer falsePositiveProbability;

    @Column(name = "ai_reasoning", length = 2000)
    private String aiReasoning;

    @Enumerated(EnumType.STRING)
    @Column(name = "decision")
    private ComplianceDecision decision;

    @Column(name = "decision_reason", length = 1000)
    private String decisionReason;

    @Column(name = "decided_by")
    private UUID decidedBy;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    @Column(name = "requires_manual_review", nullable = false)
    private boolean requiresManualReview = false;

    @Column(name = "review_comments", length = 2000)
    private String reviewComments;

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

