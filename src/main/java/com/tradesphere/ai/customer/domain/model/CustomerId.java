package com.tradesphere.ai.customer.domain.model;

import com.tradesphere.ai.shared.domain.ValueObject;
import lombok.Value;

import java.util.UUID;

/**
 * Customer ID Value Object
 * Strongly-typed identifier for Customer aggregate
 * Ensures type safety and prevents mixing IDs from different aggregates
 */
@Value
public class CustomerId implements ValueObject {

    UUID value;

    private CustomerId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("Customer ID cannot be null");
        }
        this.value = value;
    }

    /**
     * Create CustomerId from existing UUID
     */
    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }

    /**
     * Create CustomerId from string representation
     */
    public static CustomerId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer ID string cannot be null or empty");
        }
        return new CustomerId(UUID.fromString(value));
    }

    /**
     * Generate new CustomerId
     */
    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }

    /**
     * Get string representation
     */
    @Override
    public String toString() {
        return value.toString();
    }
}

