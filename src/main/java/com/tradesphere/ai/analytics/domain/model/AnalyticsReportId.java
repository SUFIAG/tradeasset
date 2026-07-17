package com.tradesphere.ai.analytics.domain.model;

import java.util.UUID;

/**
 * Analytics Report ID Value Object
 */
public record AnalyticsReportId(UUID value) {

    public AnalyticsReportId {
        if (value == null) {
            throw new IllegalArgumentException("AnalyticsReportId cannot be null");
        }
    }

    public static AnalyticsReportId of(UUID value) {
        return new AnalyticsReportId(value);
    }

    public static AnalyticsReportId of(String value) {
        return new AnalyticsReportId(UUID.fromString(value));
    }

    public static AnalyticsReportId generate() {
        return new AnalyticsReportId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

