package com.tradesphere.ai.lc.domain.model;

import com.tradesphere.ai.shared.domain.ValueObject;

import java.io.Serializable;

/**
 * Value Object representing LC Number
 * Format: LC-YYYY-NNNNNN (e.g., LC-2026-000001)
 */
public record LCNumber(String value) implements ValueObject, Serializable {
    
    public LCNumber {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("LC Number cannot be null or empty");
        }
        if (!value.matches("^LC-\\d{4}-\\d{6}$")) {
            throw new IllegalArgumentException("Invalid LC Number format. Expected: LC-YYYY-NNNNNN");
        }
    }
    
    public static LCNumber from(String value) {
        return new LCNumber(value);
    }
    
    /**
     * Generate LC Number for a given year and sequence
     */
    public static LCNumber generate(int year, long sequence) {
        String formatted = String.format("LC-%04d-%06d", year, sequence);
        return new LCNumber(formatted);
    }
}

