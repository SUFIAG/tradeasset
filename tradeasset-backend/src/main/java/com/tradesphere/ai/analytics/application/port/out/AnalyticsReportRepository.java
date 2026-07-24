package com.tradesphere.ai.analytics.application.port.out;

import com.tradesphere.ai.analytics.domain.model.AnalyticsReport;
import com.tradesphere.ai.analytics.domain.model.AnalyticsReportId;
import com.tradesphere.ai.analytics.domain.model.ReportType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port for Analytics Report persistence
 */
public interface AnalyticsReportRepository {
    AnalyticsReport save(AnalyticsReport report);
    Optional<AnalyticsReport> findById(AnalyticsReportId id, UUID tenantId);
    List<AnalyticsReport> findByType(ReportType type, UUID tenantId);
    List<AnalyticsReport> findCached(UUID tenantId);
    void delete(AnalyticsReportId id);
}

