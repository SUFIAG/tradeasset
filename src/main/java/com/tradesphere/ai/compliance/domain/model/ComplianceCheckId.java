package com.tradesphere.ai.compliance.domain.model;

import com.tradesphere.ai.shared.domain.ValueObject;
import lombok.Value;

import java.util.UUID;

/**
 * ComplianceCheck ID Value Object
 * Strongly-typed identifier for ComplianceCheck aggregate
 */
@Value
public class ComplianceCheckId implements ValueObject {

    UUID value;

    private ComplianceCheckId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ComplianceCheck ID cannot be null");
        }
        this.value = value;
    }

    /**
     * Create ComplianceCheckId from existing UUID
     */
    public static ComplianceCheckId of(UUID value) {
        return new ComplianceCheckId(value);
    }

    /**
     * Create ComplianceCheckId from string representation
     */
    public static ComplianceCheckId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("ComplianceCheck ID string cannot be null or empty");
        }
        return new ComplianceCheckId(UUID.fromString(value));
    }

    /**
     * Generate new ComplianceCheckId
     */
    public static ComplianceCheckId generate() {
        return new ComplianceCheckId(UUID.randomUUID());
    }

    /**
     * Get string representation
     */
    @Override
    public String toString() {
        return value.toString();
    }
}

