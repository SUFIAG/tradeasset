package com.tradesphere.ai.analytics.application.port.out;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Port for Data Warehouse integration
 * Supports long-term analytics storage and complex queries
 */
public interface DataWarehousePort {

    /**
     * Persist metrics to data warehouse for long-term storage
     */
    CompletableFuture<Void> persistMetrics(UUID tenantId, List<MetricRecord> metrics);

    /**
     * Query aggregated metrics over a time period
     */
    List<AggregatedMetric> queryAggregated(
        UUID tenantId,
        String metricName,
        LocalDate startDate,
        LocalDate endDate,
        AggregationPeriod period
    );

    /**
     * Query time series data
     */
    List<TimeSeriesData> queryTimeSeries(
        UUID tenantId,
        String metricName,
        LocalDateTime startTime,
        LocalDateTime endTime
    );

    /**
     * Query dimensional breakdown (e.g., by country, product, customer)
     */
    Map<String, Double> queryDimensionalBreakdown(
        UUID tenantId,
        String metricName,
        String dimension,
        LocalDate startDate,
        LocalDate endDate
    );

    /**
     * Compare two time periods
     */
    ComparisonResult comparePeriods(
        UUID tenantId,
        String metricName,
        LocalDate period1Start,
        LocalDate period1End,
        LocalDate period2Start,
        LocalDate period2End
    );

    /**
     * Archive old data to cold storage
     */
    CompletableFuture<Void> archiveOldData(UUID tenantId, LocalDate beforeDate);

    /**
     * Get warehouse statistics
     */
    WarehouseStats getWarehouseStats(UUID tenantId);

    // DTOs
    record MetricRecord(
        String metricName,
        Double value,
        String type,
        LocalDateTime timestamp,
        String dimensions
    ) {}

    record AggregatedMetric(
        LocalDateTime periodStart,
        Double avgValue,
        Double minValue,
        Double maxValue,
        Long count,
        Double sumValue
    ) {}

    record TimeSeriesData(
        LocalDateTime timestamp,
        Double value
    ) {}

    record ComparisonResult(
        Double period1Value,
        Double period2Value,
        Double changePercent
    ) {}

    record WarehouseStats(
        Long totalRecords,
        Integer uniqueMetrics,
        LocalDateTime oldestRecord,
        LocalDateTime newestRecord,
        String storageSize
    ) {}

    enum AggregationPeriod {
        HOURLY, DAILY, WEEKLY, MONTHLY
    }
}

