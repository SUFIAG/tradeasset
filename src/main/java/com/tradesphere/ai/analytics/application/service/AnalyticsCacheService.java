package com.tradesphere.ai.analytics.application.service;

import com.tradesphere.ai.analytics.adapter.out.persistence.AnalyticsReportRepositoryAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Analytics Cache Management Service
 * Handles cache cleanup and optimization
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsCacheService {

    private final AnalyticsReportRepositoryAdapter reportRepository;

    /**
     * Cleanup expired cache entries
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void cleanupExpiredCache() {
        log.info("Starting analytics cache cleanup");

        try {
            reportRepository.cleanupExpiredCache();
            log.info("Analytics cache cleanup completed successfully");
        } catch (Exception e) {
            log.error("Error during cache cleanup", e);
        }
    }

    /**
     * Get cache statistics
     */
    public CacheStatistics getCacheStatistics(java.util.UUID tenantId) {
        var cachedReports = reportRepository.findCached(tenantId);

        return new CacheStatistics(
            cachedReports.size(),
            cachedReports.stream()
                .filter(r -> !r.isCacheExpired())
                .count(),
            cachedReports.stream()
                .filter(com.tradesphere.ai.analytics.domain.model.AnalyticsReport::isCacheExpired)
                .count()
        );
    }

    public record CacheStatistics(
        long totalCached,
        long validCached,
        long expiredCached
    ) {}
}

