package com.tradesphere.ai.analytics.domain.model;

import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Metric Value Object
 * Represents a single metric data point
 */
@Getter
public class Metric {
    private final String name;
    private final BigDecimal value;
    private final String unit;
    private final Instant timestamp;
    private final MetricType type;

    public Metric(String name, BigDecimal value, String unit, MetricType type) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Metric name cannot be empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("Metric value cannot be null");
        }
        
        this.name = name;
        this.value = value;
        this.unit = unit;
        this.type = type;
        this.timestamp = Instant.now();
    }

    public static Metric create(String name, BigDecimal value, String unit, MetricType type) {
        return new Metric(name, value, unit, type);
    }

    public static Metric create(String name, double value, String unit, MetricType type) {
        return new Metric(name, BigDecimal.valueOf(value), unit, type);
    }

    public enum MetricType {
        COUNTER,
        GAUGE,
        HISTOGRAM,
        SUMMARY
    }
}

