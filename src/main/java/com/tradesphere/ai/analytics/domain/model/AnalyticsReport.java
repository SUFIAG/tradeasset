package com.tradesphere.ai.analytics.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import lombok.Getter;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

/**
 * AnalyticsReport Aggregate Root
 * Represents generated analytics reports with metrics
 */
@Getter
public class AnalyticsReport extends AggregateRoot<AnalyticsReportId> {

    private UUID tenantId;
    private String reportName;
    private ReportType reportType;
    private ReportStatus status;

    // Time period
    private LocalDate startDate;
    private LocalDate endDate;

    // Metrics
    private List<Metric> metrics;

    // Metadata
    private Map<String, Object> parameters;
    private String generatedBy;
    private Instant generatedAt;
    private Long computationTimeMs;

    // Cache
    private Instant cacheExpiresAt;
    private boolean cached;

    private AnalyticsReport(AnalyticsReportId id, UUID tenantId) {
        super(id);
        this.tenantId = tenantId;
        this.metrics = new ArrayList<>();
        this.parameters = new HashMap<>();
        this.status = ReportStatus.GENERATING;
    }

    /**
     * Create new analytics report
     */
    public static AnalyticsReport create(
            UUID tenantId,
            String reportName,
            ReportType reportType,
            LocalDate startDate,
            LocalDate endDate,
            UUID createdBy) {

        if (tenantId == null) {
            throw new IllegalArgumentException("Tenant ID required");
        }
        if (reportName == null || reportName.trim().isEmpty()) {
            throw new IllegalArgumentException("Report name required");
        }

        AnalyticsReport report = new AnalyticsReport(AnalyticsReportId.generate(), tenantId);
        report.reportName = reportName;
        report.reportType = reportType;
        report.startDate = startDate;
        report.endDate = endDate;
        report.generatedBy = createdBy != null ? createdBy.toString() : null;
        report.generatedAt = Instant.now();
        report.setCreatedBy(createdBy);
        report.setCreatedAt(Instant.now());

        return report;
    }

    /**
     * Add metric to report
     */
    public void addMetric(Metric metric) {
        if (metric == null) {
            throw new IllegalArgumentException("Metric cannot be null");
        }
        this.metrics.add(metric);
    }

    /**
     * Add multiple metrics
     */
    public void addMetrics(List<Metric> metrics) {
        if (metrics != null) {
            this.metrics.addAll(metrics);
        }
    }

    /**
     * Set parameter
     */
    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    /**
     * Mark as completed
     */
    public void markCompleted(long computationTimeMs) {
        this.status = ReportStatus.COMPLETED;
        this.computationTimeMs = computationTimeMs;
    }

    /**
     * Mark as failed
     */
    public void markFailed() {
        this.status = ReportStatus.FAILED;
    }

    /**
     * Enable caching
     */
    public void enableCache(int ttlSeconds) {
        this.cached = true;
        this.cacheExpiresAt = Instant.now().plusSeconds(ttlSeconds);
    }

    /**
     * Check if cache is expired
     */
    public boolean isCacheExpired() {
        return cacheExpiresAt != null && Instant.now().isAfter(cacheExpiresAt);
    }

    /**
     * Get metric by name
     */
    public Optional<Metric> getMetric(String name) {
        return metrics.stream()
                .filter(m -> m.getName().equals(name))
                .findFirst();
    }

    // Builder for persistence reconstruction
    public static AnalyticsReportBuilder builder() {
        return new AnalyticsReportBuilder();
    }

    public static class AnalyticsReportBuilder {
        private AnalyticsReport report;

        public AnalyticsReportBuilder id(UUID id) {
            this.report = new AnalyticsReport(AnalyticsReportId.of(id), null);
            return this;
        }

        public AnalyticsReportBuilder tenantId(UUID tenantId) {
            report.tenantId = tenantId;
            return this;
        }

        public AnalyticsReportBuilder reportName(String reportName) {
            report.reportName = reportName;
            return this;
        }

        public AnalyticsReportBuilder reportType(ReportType reportType) {
            report.reportType = reportType;
            return this;
        }

        public AnalyticsReportBuilder status(ReportStatus status) {
            report.status = status;
            return this;
        }

        public AnalyticsReportBuilder startDate(LocalDate startDate) {
            report.startDate = startDate;
            return this;
        }

        public AnalyticsReportBuilder endDate(LocalDate endDate) {
            report.endDate = endDate;
            return this;
        }

        public AnalyticsReportBuilder metrics(List<Metric> metrics) {
            report.metrics = metrics != null ? new ArrayList<>(metrics) : new ArrayList<>();
            return this;
        }

        public AnalyticsReportBuilder parameters(Map<String, Object> parameters) {
            report.parameters = parameters != null ? new HashMap<>(parameters) : new HashMap<>();
            return this;
        }

        public AnalyticsReportBuilder generatedBy(String generatedBy) {
            report.generatedBy = generatedBy;
            return this;
        }

        public AnalyticsReportBuilder generatedAt(Instant generatedAt) {
            report.generatedAt = generatedAt;
            return this;
        }

        public AnalyticsReportBuilder computationTimeMs(Long computationTimeMs) {
            report.computationTimeMs = computationTimeMs;
            return this;
        }

        public AnalyticsReportBuilder cacheExpiresAt(Instant cacheExpiresAt) {
            report.cacheExpiresAt = cacheExpiresAt;
            return this;
        }

        public AnalyticsReportBuilder cached(boolean cached) {
            report.cached = cached;
            return this;
        }

        public AnalyticsReportBuilder createdBy(UUID createdBy) {
            report.createdBy = createdBy;
            return this;
        }

        public AnalyticsReportBuilder createdAt(Instant createdAt) {
            report.createdAt = createdAt;
            return this;
        }

        public AnalyticsReportBuilder updatedBy(UUID updatedBy) {
            report.updatedBy = updatedBy;
            return this;
        }

        public AnalyticsReportBuilder updatedAt(Instant updatedAt) {
            report.updatedAt = updatedAt;
            return this;
        }

        public AnalyticsReportBuilder version(Long version) {
            report.version = version;
            return this;
        }

        public AnalyticsReport build() {
            return report;
        }
    }
}

