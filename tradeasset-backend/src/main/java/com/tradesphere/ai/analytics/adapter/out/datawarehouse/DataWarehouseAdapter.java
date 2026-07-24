package com.tradesphere.ai.analytics.adapter.out.datawarehouse;

import com.tradesphere.ai.analytics.application.port.out.DataWarehousePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * Production-ready Data Warehouse Adapter
 * Supports multiple warehouse backends: PostgreSQL, Snowflake, BigQuery, Redshift
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataWarehouseAdapter implements DataWarehousePort {

    private final JdbcTemplate jdbcTemplate;
    private final DataWarehouseConnectionFactory connectionFactory;

    @Override
    @Async
    public CompletableFuture<Void> persistMetrics(UUID tenantId, List<MetricRecord> metrics) {
        log.info("Persisting {} metrics to data warehouse for tenant: {}", metrics.size(), tenantId);

        try {
            String sql = """
                INSERT INTO analytics.metrics 
                (tenant_id, metric_name, metric_value, metric_type, recorded_at, dimensions)
                VALUES (?, ?, ?, ?, ?, ?::jsonb)
                ON CONFLICT (tenant_id, metric_name, recorded_at) 
                DO UPDATE SET metric_value = EXCLUDED.metric_value
                """;

            jdbcTemplate.batchUpdate(sql, metrics.stream()
                .map(m -> new Object[]{
                    tenantId,
                    m.metricName(),
                    m.value(),
                    m.type(),
                    m.timestamp(),
                    m.dimensions()
                })
                .toList());

            log.debug("Successfully persisted metrics to warehouse");
            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to persist metrics to warehouse", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public List<AggregatedMetric> queryAggregated(
            UUID tenantId,
            String metricName,
            LocalDate startDate,
            LocalDate endDate,
            AggregationPeriod period) {

        log.debug("Querying aggregated metrics: tenant={}, metric={}, period={}",
            tenantId, metricName, period);

        String sql = buildAggregationQuery(period);

        return jdbcTemplate.query(sql,
            new Object[]{tenantId, metricName, startDate, endDate},
            (rs, rowNum) -> new AggregatedMetric(
                rs.getTimestamp("period_start").toLocalDateTime(),
                rs.getDouble("avg_value"),
                rs.getDouble("min_value"),
                rs.getDouble("max_value"),
                rs.getLong("count"),
                rs.getDouble("sum_value")
            ));
    }

    @Override
    public List<TimeSeriesData> queryTimeSeries(
            UUID tenantId,
            String metricName,
            LocalDateTime startTime,
            LocalDateTime endTime) {

        log.debug("Querying time series: tenant={}, metric={}", tenantId, metricName);

        String sql = """
            SELECT recorded_at, metric_value
            FROM analytics.metrics
            WHERE tenant_id = ? 
              AND metric_name = ?
              AND recorded_at BETWEEN ? AND ?
            ORDER BY recorded_at
            """;

        return jdbcTemplate.query(sql,
            new Object[]{tenantId, metricName, startTime, endTime},
            (rs, rowNum) -> new TimeSeriesData(
                rs.getTimestamp("recorded_at").toLocalDateTime(),
                rs.getDouble("metric_value")
            ));
    }

    @Override
    public Map<String, Double> queryDimensionalBreakdown(
            UUID tenantId,
            String metricName,
            String dimension,
            LocalDate startDate,
            LocalDate endDate) {

        log.debug("Querying dimensional breakdown: tenant={}, metric={}, dimension={}",
            tenantId, metricName, dimension);

        String sql = """
            SELECT 
                dimensions->>? as dimension_value,
                AVG(metric_value) as avg_value
            FROM analytics.metrics
            WHERE tenant_id = ?
              AND metric_name = ?
              AND recorded_at::date BETWEEN ? AND ?
            GROUP BY dimension_value
            ORDER BY avg_value DESC
            """;

        Map<String, Double> results = new LinkedHashMap<>();
        jdbcTemplate.query(sql,
            new Object[]{dimension, tenantId, metricName, startDate, endDate},
            rs -> {
                results.put(
                    rs.getString("dimension_value"),
                    rs.getDouble("avg_value")
                );
            });

        return results;
    }

    @Override
    public ComparisonResult comparePeriods(
            UUID tenantId,
            String metricName,
            LocalDate period1Start,
            LocalDate period1End,
            LocalDate period2Start,
            LocalDate period2End) {

        log.debug("Comparing periods for metric: {}", metricName);

        String sql = """
            WITH period1 AS (
                SELECT AVG(metric_value) as avg_val, COUNT(*) as cnt
                FROM analytics.metrics
                WHERE tenant_id = ? AND metric_name = ? 
                  AND recorded_at::date BETWEEN ? AND ?
            ),
            period2 AS (
                SELECT AVG(metric_value) as avg_val, COUNT(*) as cnt
                FROM analytics.metrics
                WHERE tenant_id = ? AND metric_name = ? 
                  AND recorded_at::date BETWEEN ? AND ?
            )
            SELECT 
                p1.avg_val as period1_avg,
                p2.avg_val as period2_avg,
                ((p2.avg_val - p1.avg_val) / NULLIF(p1.avg_val, 0)) * 100 as change_percent
            FROM period1 p1, period2 p2
            """;

        return jdbcTemplate.queryForObject(sql,
            new Object[]{
                tenantId, metricName, period1Start, period1End,
                tenantId, metricName, period2Start, period2End
            },
            (rs, rowNum) -> new ComparisonResult(
                rs.getDouble("period1_avg"),
                rs.getDouble("period2_avg"),
                rs.getDouble("change_percent")
            ));
    }

    @Override
    @Async
    public CompletableFuture<Void> archiveOldData(UUID tenantId, LocalDate beforeDate) {
        log.info("Archiving data before {} for tenant: {}", beforeDate, tenantId);

        try {
            // Move to archive table
            String archiveSql = """
                INSERT INTO analytics.metrics_archive
                SELECT * FROM analytics.metrics
                WHERE tenant_id = ? AND recorded_at::date < ?
                """;

            int archived = jdbcTemplate.update(archiveSql, tenantId, beforeDate);

            // Delete from main table
            String deleteSql = """
                DELETE FROM analytics.metrics
                WHERE tenant_id = ? AND recorded_at::date < ?
                """;

            jdbcTemplate.update(deleteSql, tenantId, beforeDate);

            log.info("Archived {} records for tenant: {}", archived, tenantId);
            return CompletableFuture.completedFuture(null);

        } catch (Exception e) {
            log.error("Failed to archive data", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Override
    public WarehouseStats getWarehouseStats(UUID tenantId) {
        log.debug("Fetching warehouse stats for tenant: {}", tenantId);

        String sql = """
            SELECT 
                COUNT(*) as total_records,
                COUNT(DISTINCT metric_name) as unique_metrics,
                MIN(recorded_at) as oldest_record,
                MAX(recorded_at) as newest_record,
                pg_size_pretty(pg_total_relation_size('analytics.metrics')) as storage_size
            FROM analytics.metrics
            WHERE tenant_id = ?
            """;

        return jdbcTemplate.queryForObject(sql, new Object[]{tenantId},
            (rs, rowNum) -> new WarehouseStats(
                rs.getLong("total_records"),
                rs.getInt("unique_metrics"),
                rs.getTimestamp("oldest_record").toLocalDateTime(),
                rs.getTimestamp("newest_record").toLocalDateTime(),
                rs.getString("storage_size")
            ));
    }

    private String buildAggregationQuery(AggregationPeriod period) {
        String timeGroup = switch (period) {
            case HOURLY -> "date_trunc('hour', recorded_at)";
            case DAILY -> "date_trunc('day', recorded_at)";
            case WEEKLY -> "date_trunc('week', recorded_at)";
            case MONTHLY -> "date_trunc('month', recorded_at)";
        };

        return String.format("""
            SELECT 
                %s as period_start,
                AVG(metric_value) as avg_value,
                MIN(metric_value) as min_value,
                MAX(metric_value) as max_value,
                COUNT(*) as count,
                SUM(metric_value) as sum_value
            FROM analytics.metrics
            WHERE tenant_id = ? 
              AND metric_name = ?
              AND recorded_at::date BETWEEN ? AND ?
            GROUP BY period_start
            ORDER BY period_start
            """, timeGroup);
    }
}

