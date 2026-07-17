package com.tradesphere.ai.analytics.adapter.in.rest;

import com.tradesphere.ai.analytics.application.port.in.GenerateReportUseCase;
import com.tradesphere.ai.analytics.application.port.in.GetReportUseCase;
import com.tradesphere.ai.analytics.application.service.AnalyticsCacheService;
import com.tradesphere.ai.analytics.application.service.AnalyticsService;
import com.tradesphere.ai.analytics.application.service.AnalyticsService.*;
import com.tradesphere.ai.analytics.domain.model.AnalyticsReport;
import com.tradesphere.ai.analytics.domain.model.AnalyticsReportId;
import com.tradesphere.ai.analytics.domain.model.ReportType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Analytics REST API - High-Performance Endpoints
 * Optimized for low latency with aggressive caching
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Analytics", description = "Analytics and reporting APIs")
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final GenerateReportUseCase generateReportUseCase;
    private final GetReportUseCase getReportUseCase;
    private final AnalyticsCacheService cacheService;

    /**
     * Get dashboard metrics (cached, ultra-fast response)
     */
    @GetMapping("/dashboard")
    public ResponseEntity<DashboardMetrics> getDashboard(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        return ResponseEntity.ok(analyticsService.getDashboardMetrics(tenantId));
    }

    /**
     * Get transaction analytics (async for heavy computation)
     */
    @GetMapping("/transactions")
    public CompletableFuture<ResponseEntity<TransactionAnalytics>> getTransactionAnalytics(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        return analyticsService.getTransactionAnalytics(tenantId, startDate, endDate)
            .thenApply(ResponseEntity::ok);
    }

    /**
     * Get compliance analytics
     */
    @GetMapping("/compliance")
    public ResponseEntity<ComplianceAnalytics> getComplianceAnalytics(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        return ResponseEntity.ok(analyticsService.getComplianceAnalytics(tenantId));
    }

    /**
     * Get risk analytics
     */
    @GetMapping("/risk")
    public ResponseEntity<RiskAnalytics> getRiskAnalytics(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        return ResponseEntity.ok(analyticsService.getRiskAnalytics(tenantId));
    }

    /**
     * Get performance metrics
     */
    @GetMapping("/performance")
    public ResponseEntity<PerformanceMetrics> getPerformanceMetrics(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        return ResponseEntity.ok(analyticsService.getPerformanceMetrics(tenantId));
    }

    /**
     * Generate custom report (async)
     */
    @PostMapping("/reports")
    public CompletableFuture<ResponseEntity<CustomReport>> generateReport(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestBody ReportRequest request) {

        return analyticsService.generateCustomReport(tenantId, request)
            .thenApply(ResponseEntity::ok);
    }

    /**
     * Export report (async file download)
     */
    @GetMapping("/reports/{reportType}/export")
    public CompletableFuture<ResponseEntity<byte[]>> exportReport(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @PathVariable String reportType,
            @RequestParam(defaultValue = "pdf") String format) {

        return analyticsService.exportReport(tenantId, reportType, format)
            .thenApply(data -> ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=report." + format)
                .contentType(getMediaType(format))
                .body(data));
    }

    /**
     * Record custom metric (for real-time updates)
     */
    @PostMapping("/metrics")
    public ResponseEntity<Void> recordMetric(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestBody MetricRequest request) {

        analyticsService.recordMetric(tenantId, request.metricName(), request.value());
        return ResponseEntity.accepted().build();
    }

    /**
     * Generate cached analytics report
     */
    @PostMapping("/reports/generate")
    @Operation(summary = "Generate analytics report", description = "Generate and cache analytics report")
    public ResponseEntity<ReportResponse> generateCachedReport(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @RequestHeader("X-User-ID") UUID userId,
            @RequestBody GenerateReportRequest request) {

        log.info("Generating cached report: type={}, tenant={}", request.reportType(), tenantId);

        AnalyticsReport report = generateReportUseCase.generateReport(
            tenantId,
            request.reportType(),
            request.startDate(),
            request.endDate(),
            userId
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReportResponse.from(report));
    }

    /**
     * Get cached report by ID
     */
    @GetMapping("/reports/{reportId}")
    @Operation(summary = "Get report", description = "Retrieve cached analytics report")
    public ResponseEntity<ReportResponse> getReport(
            @RequestHeader("X-Tenant-ID") UUID tenantId,
            @PathVariable UUID reportId) {

        return getReportUseCase.getReport(AnalyticsReportId.of(reportId), tenantId)
                .map(ReportResponse::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get cache statistics
     */
    @GetMapping("/cache/stats")
    @Operation(summary = "Cache statistics", description = "Get analytics cache statistics")
    public ResponseEntity<AnalyticsCacheService.CacheStatistics> getCacheStats(
            @RequestHeader("X-Tenant-ID") UUID tenantId) {

        return ResponseEntity.ok(cacheService.getCacheStatistics(tenantId));
    }

    private MediaType getMediaType(String format) {
        return switch (format.toLowerCase()) {
            case "pdf" -> MediaType.APPLICATION_PDF;
            case "excel" -> MediaType.parseMediaType("application/vnd.ms-excel");
            case "csv" -> MediaType.parseMediaType("text/csv");
            default -> MediaType.APPLICATION_JSON;
        };
    }

    // DTOs
    public record MetricRequest(String metricName, double value) {}

    public record GenerateReportRequest(
        ReportType reportType,
        LocalDate startDate,
        LocalDate endDate
    ) {}

    public record ReportResponse(
        String reportId,
        String reportName,
        String reportType,
        String status,
        int metricCount,
        String generatedAt,
        Long computationTimeMs,
        boolean cached
    ) {
        public static ReportResponse from(AnalyticsReport report) {
            return new ReportResponse(
                report.getId().toString(),
                report.getReportName(),
                report.getReportType().name(),
                report.getStatus().name(),
                report.getMetrics().size(),
                report.getGeneratedAt().toString(),
                report.getComputationTimeMs(),
                report.isCached()
            );
        }
    }
}

