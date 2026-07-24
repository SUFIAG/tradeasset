package com.tradesphere.ai.risk.domain.model;

import com.tradesphere.ai.shared.domain.ValueObject;
import lombok.Value;

import java.util.UUID;

/**
 * Risk Assessment ID Value Object
 */
@Value
public class RiskAssessmentId implements ValueObject {
    UUID value;

    private RiskAssessmentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("RiskAssessment ID cannot be null");
        }
        this.value = value;
    }

    public static RiskAssessmentId of(UUID value) {
        return new RiskAssessmentId(value);
    }

    public static RiskAssessmentId of(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("RiskAssessment ID string cannot be null or empty");
        }
        return new RiskAssessmentId(UUID.fromString(value));
    }

    public static RiskAssessmentId generate() {
        return new RiskAssessmentId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

