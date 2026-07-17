package com.tradesphere.ai.analytics.adapter.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * JPA Entity for Metric data
 * Stored separately for efficient querying and aggregation
 */
@Entity
@Table(name = "analytics_metrics", indexes = {
    @Index(name = "idx_metric_report", columnList = "report_id"),
    @Index(name = "idx_metric_name", columnList = "metric_name"),
    @Index(name = "idx_metric_type", columnList = "metric_type")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetricJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private AnalyticsReportJpaEntity report;

    @Column(name = "metric_name", nullable = false)
    private String metricName;

    @Column(name = "metric_value", nullable = false, precision = 19, scale = 4)
    private BigDecimal metricValue;

    @Column(name = "metric_unit")
    private String metricUnit;

    @Column(name = "metric_type")
    private String metricType;

    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
}

