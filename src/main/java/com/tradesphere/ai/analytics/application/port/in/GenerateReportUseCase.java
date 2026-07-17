package com.tradesphere.ai.analytics.application.port.in;

import com.tradesphere.ai.analytics.domain.model.AnalyticsReport;
import com.tradesphere.ai.analytics.domain.model.ReportType;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Use case for generating analytics reports
 */
public interface GenerateReportUseCase {
    AnalyticsReport generateReport(
        UUID tenantId,
        ReportType reportType,
        LocalDate startDate,
        LocalDate endDate,
        UUID userId
    );
}

