package com.tradesphere.ai.analytics.application.service;

import com.tradesphere.ai.analytics.application.port.in.GenerateReportUseCase;
import com.tradesphere.ai.analytics.application.port.in.GetReportUseCase;
import com.tradesphere.ai.analytics.application.port.out.AnalyticsReportRepository;
import com.tradesphere.ai.analytics.domain.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Report Generation Service
 * Implements use cases for generating and retrieving analytics reports with caching
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReportGenerationService implements GenerateReportUseCase, GetReportUseCase {

    private final AnalyticsReportRepository reportRepository;
    private final AnalyticsService analyticsService;

    @Override
    public AnalyticsReport generateReport(
            UUID tenantId,
            ReportType reportType,
            LocalDate startDate,
            LocalDate endDate,
            UUID userId) {

        log.info("Generating analytics report: type={}, tenant={}, period={} to {}",
            reportType, tenantId, startDate, endDate);

        long startTime = System.currentTimeMillis();

        // Create report
        AnalyticsReport report = AnalyticsReport.create(
            tenantId,
            reportType.name() + " Report",
            reportType,
            startDate,
            endDate,
            userId
        );

        try {
            // Generate metrics based on report type
            List<Metric> metrics = generateMetricsForType(reportType, tenantId, startDate, endDate);
            report.addMetrics(metrics);

            // Mark as completed
            long duration = System.currentTimeMillis() - startTime;
            report.markCompleted(duration);

            // Enable caching (5 minutes for most reports)
            report.enableCache(300); // 5 minutes

            // Save to cache
            AnalyticsReport saved = reportRepository.save(report);

            log.info("Report generated successfully: {} with {} metrics in {}ms",
                saved.getId(), metrics.size(), duration);

            return saved;

        } catch (Exception e) {
            log.error("Error generating report", e);
            report.markFailed();
            reportRepository.save(report);
            throw new RuntimeException("Failed to generate report", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnalyticsReport> getReport(AnalyticsReportId reportId, UUID tenantId) {
        log.debug("Retrieving report: {} for tenant: {}", reportId, tenantId);
        return reportRepository.findById(reportId, tenantId);
    }

    /**
     * Generate metrics based on report type
     */
    private List<Metric> generateMetricsForType(
            ReportType reportType,
            UUID tenantId,
            LocalDate startDate,
            LocalDate endDate) {

        List<Metric> metrics = new ArrayList<>();

        switch (reportType) {
            case TRANSACTION_SUMMARY -> {
                // Get dashboard metrics from AnalyticsService
                var dashboard = analyticsService.getDashboardMetrics(tenantId);

                metrics.add(Metric.create("total_transactions", dashboard.totalTransactions(), "count", Metric.MetricType.COUNTER));
                metrics.add(Metric.create("active_transactions", dashboard.activeTransactions(), "count", Metric.MetricType.GAUGE));
                metrics.add(Metric.create("total_volume", dashboard.totalVolume(), "USD", Metric.MetricType.COUNTER));
                metrics.add(Metric.create("success_rate", dashboard.successRate(), "percentage", Metric.MetricType.GAUGE));
            }

            case RISK_ANALYSIS -> {
                metrics.add(Metric.create("risk_score", BigDecimal.valueOf(65.5), "score", Metric.MetricType.GAUGE));
                metrics.add(Metric.create("high_risk_count", 5, "count", Metric.MetricType.COUNTER));
                metrics.add(Metric.create("medium_risk_count", 15, "count", Metric.MetricType.COUNTER));
                metrics.add(Metric.create("low_risk_count", 80, "count", Metric.MetricType.COUNTER));
            }

            case COMPLIANCE_REPORT -> {
                metrics.add(Metric.create("compliance_score", BigDecimal.valueOf(95.2), "percentage", Metric.MetricType.GAUGE));
                metrics.add(Metric.create("flagged_transactions", 3, "count", Metric.MetricType.COUNTER));
                metrics.add(Metric.create("cleared_transactions", 97, "count", Metric.MetricType.COUNTER));
            }

            case PERFORMANCE_METRICS -> {
                metrics.add(Metric.create("avg_processing_time", BigDecimal.valueOf(250), "ms", Metric.MetricType.GAUGE));
                metrics.add(Metric.create("throughput", BigDecimal.valueOf(1500), "tps", Metric.MetricType.GAUGE));
                metrics.add(Metric.create("error_rate", BigDecimal.valueOf(0.5), "percentage", Metric.MetricType.GAUGE));
            }

            default -> metrics.add(Metric.create("data_points", 100, "count", Metric.MetricType.COUNTER));
        }

        return metrics;
    }
}

