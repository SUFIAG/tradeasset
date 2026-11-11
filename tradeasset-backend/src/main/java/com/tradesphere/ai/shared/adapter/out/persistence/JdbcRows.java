package com.tradesphere.ai.shared.adapter.out.persistence;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tradesphere.ai.shared.domain.valueobject.Money;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Small helpers shared by every JDBC persistence adapter: UTC instants for TIMESTAMPTZ columns,
 * calendar dates, UUIDs, money and JSONB. Bind JSON with {@code CAST(:param AS jsonb)}.
 */
public final class JdbcRows {

    private static final ObjectMapper JSON = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private JdbcRows() {
    }

    /** Value to bind for a TIMESTAMPTZ column. Always UTC (REQ-TIM-001). */
    public static OffsetDateTime ts(Instant instant) {
        return instant == null ? null : instant.atOffset(ZoneOffset.UTC);
    }

    public static Instant instant(ResultSet rs, String column) throws SQLException {
        OffsetDateTime value = rs.getObject(column, OffsetDateTime.class);
        return value == null ? null : value.toInstant();
    }

    public static LocalDate date(ResultSet rs, String column) throws SQLException {
        return rs.getObject(column, LocalDate.class);
    }

    public static UUID uuid(ResultSet rs, String column) throws SQLException {
        return rs.getObject(column, UUID.class);
    }

    public static Integer integer(ResultSet rs, String column) throws SQLException {
        int value = rs.getInt(column);
        return rs.wasNull() ? null : value;
    }

    public static Long longValue(ResultSet rs, String column) throws SQLException {
        long value = rs.getLong(column);
        return rs.wasNull() ? null : value;
    }

    /** Reads an amount column and its currency column into Money; null when the amount is null. */
    public static Money money(ResultSet rs, String amountColumn, String currencyColumn) throws SQLException {
        BigDecimal amount = rs.getBigDecimal(amountColumn);
        String currency = rs.getString(currencyColumn);
        if (amount == null || currency == null) {
            return null;
        }
        return Money.of(amount, currency.trim());
    }

    public static String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return JSON.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Value cannot be serialised to JSON", e);
        }
    }

    public static Map<String, Object> jsonMap(ResultSet rs, String column) throws SQLException {
        String raw = rs.getString(column);
        if (raw == null || raw.isBlank()) {
            return Map.of();
        }
        try {
            return JSON.readValue(raw, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            throw new SQLException("Column " + column + " does not hold a JSON object", e);
        }
    }

    public static List<String> jsonStringList(ResultSet rs, String column) throws SQLException {
        String raw = rs.getString(column);
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        try {
            return JSON.readValue(raw, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException e) {
            throw new SQLException("Column " + column + " does not hold a JSON string array", e);
        }
    }

    public static <T> T fromJson(String raw, TypeReference<T> type) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        try {
            return JSON.readValue(raw, type);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Stored JSON cannot be read", e);
        }
    }
}
