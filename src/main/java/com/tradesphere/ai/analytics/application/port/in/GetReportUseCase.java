package com.tradesphere.ai.analytics.application.port.in;

import com.tradesphere.ai.analytics.domain.model.AnalyticsReport;
import com.tradesphere.ai.analytics.domain.model.AnalyticsReportId;

import java.util.Optional;
import java.util.UUID;

/**
 * Use case for retrieving analytics reports
 */
public interface GetReportUseCase {
    Optional<AnalyticsReport> getReport(AnalyticsReportId reportId, UUID tenantId);
}

