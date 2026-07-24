package com.tradesphere.ai.analytics.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.analytics.domain.model.AnalyticsReport;
import com.tradesphere.ai.analytics.domain.model.Metric;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Mapper between AnalyticsReport domain model and JPA entities
 * Handles complex metric and parameter serialization
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AnalyticsReportMapper {

    private final ObjectMapper objectMapper;

    /**
     * Convert domain model to JPA entity
     */
    public AnalyticsReportJpaEntity toEntity(AnalyticsReport report) {
        AnalyticsReportJpaEntity entity = AnalyticsReportJpaEntity.builder()
                .id(report.getId().value())
                .tenantId(report.getTenantId())
                .reportName(report.getReportName())
                .reportType(report.getReportType())
                .status(report.getStatus())
                .startDate(report.getStartDate())
                .endDate(report.getEndDate())
                .parameters(serializeParameters(report.getParameters()))
                .generatedBy(report.getGeneratedBy())
                .generatedAt(report.getGeneratedAt())
                .computationTimeMs(report.getComputationTimeMs())
                .cacheExpiresAt(report.getCacheExpiresAt())
                .cached(report.isCached())
                .createdBy(report.getCreatedBy())
                .createdAt(report.getCreatedAt())
                .updatedBy(report.getUpdatedBy())
                .updatedAt(report.getUpdatedAt())
                .version(report.getVersion())
                .build();

        // Map metrics to separate entities
        List<MetricJpaEntity> metricEntities = report.getMetrics().stream()
                .map(metric -> toMetricEntity(metric, entity))
                .collect(Collectors.toList());
        entity.setMetrics(metricEntities);

        return entity;
    }

    /**
     * Convert JPA entity to domain model
     */
    public AnalyticsReport toDomain(AnalyticsReportJpaEntity entity) {
        // Build metrics
        List<Metric> metrics = entity.getMetrics().stream()
                .map(this::toMetricDomain)
                .collect(Collectors.toList());

        return AnalyticsReport.builder()
                .id(entity.getId())
                .tenantId(entity.getTenantId())
                .reportName(entity.getReportName())
                .reportType(entity.getReportType())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .metrics(metrics)
                .parameters(deserializeParameters(entity.getParameters()))
                .generatedBy(entity.getGeneratedBy())
                .generatedAt(entity.getGeneratedAt())
                .computationTimeMs(entity.getComputationTimeMs())
                .cacheExpiresAt(entity.getCacheExpiresAt())
                .cached(entity.isCached())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedBy(entity.getUpdatedBy())
                .updatedAt(entity.getUpdatedAt())
                .version(entity.getVersion())
                .build();
    }

    /**
     * Convert domain Metric to JPA entity
     */
    private MetricJpaEntity toMetricEntity(Metric metric, AnalyticsReportJpaEntity report) {
        return MetricJpaEntity.builder()
                .report(report)
                .metricName(metric.getName())
                .metricValue(metric.getValue())
                .metricUnit(metric.getUnit())
                .metricType(metric.getType() != null ? metric.getType().name() : null)
                .timestamp(metric.getTimestamp())
                .build();
    }

    /**
     * Convert JPA entity to domain Metric
     */
    private Metric toMetricDomain(MetricJpaEntity entity) {
        Metric.MetricType type = null;
        if (entity.getMetricType() != null) {
            try {
                type = Metric.MetricType.valueOf(entity.getMetricType());
            } catch (IllegalArgumentException e) {
                log.warn("Invalid metric type: {}", entity.getMetricType());
            }
        }

        return Metric.create(
            entity.getMetricName(),
            entity.getMetricValue(),
            entity.getMetricUnit(),
            type
        );
    }

    /**
     * Serialize parameters map to string map for JPA
     */
    private Map<String, String> serializeParameters(Map<String, Object> parameters) {
        Map<String, String> serialized = new HashMap<>();
        if (parameters != null) {
            parameters.forEach((key, value) -> {
                try {
                    serialized.put(key, objectMapper.writeValueAsString(value));
                } catch (JsonProcessingException e) {
                    log.warn("Failed to serialize parameter {}: {}", key, e.getMessage());
                    serialized.put(key, value != null ? value.toString() : null);
                }
            });
        }
        return serialized;
    }

    /**
     * Deserialize parameters from string map
     */
    private Map<String, Object> deserializeParameters(Map<String, String> parameters) {
        Map<String, Object> deserialized = new HashMap<>();
        if (parameters != null) {
            parameters.forEach((key, value) -> {
                try {
                    deserialized.put(key, objectMapper.readValue(value, Object.class));
                } catch (JsonProcessingException e) {
                    log.warn("Failed to deserialize parameter {}: {}", key, e.getMessage());
                    deserialized.put(key, value);
                }
            });
        }
        return deserialized;
    }
}

