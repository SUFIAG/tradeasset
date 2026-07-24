package com.tradesphere.ai.analytics.adapter.out.persistence;

import com.tradesphere.ai.analytics.domain.model.ReportStatus;
import com.tradesphere.ai.analytics.domain.model.ReportType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * JPA Entity for Analytics Report persistence
 * Optimized for caching and quick retrieval
 */
@Entity
@Table(name = "analytics_reports", indexes = {
    @Index(name = "idx_analytics_tenant", columnList = "tenant_id"),
    @Index(name = "idx_analytics_type", columnList = "report_type, tenant_id"),
    @Index(name = "idx_analytics_status", columnList = "status"),
    @Index(name = "idx_analytics_cache", columnList = "cached, cache_expires_at"),
    @Index(name = "idx_analytics_generated", columnList = "generated_at DESC")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalyticsReportJpaEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "report_name", nullable = false)
    private String reportName;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false)
    private ReportType reportType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ReportStatus status;

    // Time period
    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    // Metrics stored as separate entities for better querying
    @OneToMany(mappedBy = "report", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<MetricJpaEntity> metrics = new ArrayList<>();

    // Parameters stored as JSON for flexibility
    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "analytics_report_parameters", joinColumns = @JoinColumn(name = "report_id"))
    @MapKeyColumn(name = "param_key")
    @Column(name = "param_value", columnDefinition = "TEXT")
    @Builder.Default
    private Map<String, String> parameters = new HashMap<>();

    // Generation metadata
    @Column(name = "generated_by")
    private String generatedBy;

    @Column(name = "generated_at")
    private Instant generatedAt;

    @Column(name = "computation_time_ms")
    private Long computationTimeMs;

    // Cache control
    @Column(name = "cache_expires_at")
    private Instant cacheExpiresAt;

    @Column(name = "cached")
    private boolean cached;

    // Audit fields
    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Version
    @Column(name = "version")
    private Long version;
}

