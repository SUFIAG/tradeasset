package com.tradesphere.ai.analytics.adapter.out.persistence;

import com.tradesphere.ai.analytics.application.port.out.AnalyticsReportRepository;
import com.tradesphere.ai.analytics.domain.model.AnalyticsReport;
import com.tradesphere.ai.analytics.domain.model.AnalyticsReportId;
import com.tradesphere.ai.analytics.domain.model.ReportType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter implementing AnalyticsReportRepository port
 * Provides caching and persistence for analytics reports
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsReportRepositoryAdapter implements AnalyticsReportRepository {

    private final AnalyticsReportJpaRepository jpaRepository;
    private final AnalyticsReportMapper mapper;

    @Override
    @Transactional
    public AnalyticsReport save(AnalyticsReport report) {
        log.debug("Saving analytics report: {} for tenant: {}",
            report.getId(), report.getTenantId());

        AnalyticsReportJpaEntity entity = mapper.toEntity(report);
        AnalyticsReportJpaEntity saved = jpaRepository.save(entity);

        log.info("Analytics report saved: {} with {} metrics",
            saved.getId(), saved.getMetrics().size());

        return mapper.toDomain(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<AnalyticsReport> findById(AnalyticsReportId id, UUID tenantId) {
        log.debug("Finding analytics report: {} for tenant: {}", id, tenantId);

        return jpaRepository.findByIdAndTenantId(id.value(), tenantId)
                .map(mapper::toDomain);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsReport> findByType(ReportType type, UUID tenantId) {
        log.debug("Finding analytics reports by type: {} for tenant: {}", type, tenantId);

        return jpaRepository.findByReportTypeAndTenantIdOrderByGeneratedAtDesc(type, tenantId)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyticsReport> findCached(UUID tenantId) {
        log.debug("Finding cached analytics reports for tenant: {}", tenantId);

        return jpaRepository.findValidCachedReports(tenantId, Instant.now())
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(AnalyticsReportId id) {
        log.debug("Deleting analytics report: {}", id);
        jpaRepository.deleteById(id.value());
    }

    /**
     * Find cached report by criteria (optimization for cache hits)
     */
    @Transactional(readOnly = true)
    public Optional<AnalyticsReport> findCachedReport(
            UUID tenantId,
            ReportType reportType,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate) {

        log.debug("Looking for cached report: type={}, period={} to {}",
            reportType, startDate, endDate);

        return jpaRepository.findCachedReport(tenantId, reportType, startDate, endDate, Instant.now())
                .map(entity -> {
                    log.info("Cache HIT for report: type={}, tenant={}", reportType, tenantId);
                    return mapper.toDomain(entity);
                });
    }

    /**
     * Cleanup expired cache entries (scheduled task)
     */
    @Transactional
    public void cleanupExpiredCache() {
        log.info("Cleaning up expired cached reports");
        jpaRepository.deleteExpiredCachedReports(Instant.now());
    }

    /**
     * Get recent reports for dashboard
     */
    @Transactional(readOnly = true)
    public List<AnalyticsReport> findRecentReports(UUID tenantId, int limit) {
        log.debug("Finding {} recent reports for tenant: {}", limit, tenantId);

        return jpaRepository.findRecentReports(tenantId)
                .stream()
                .limit(limit)
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }
}

