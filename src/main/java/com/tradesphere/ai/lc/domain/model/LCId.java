package com.tradesphere.ai.lc.domain.model;

import com.tradesphere.ai.shared.domain.ValueObject;

import java.io.Serializable;
import java.util.UUID;

/**
 * Value Object representing Letter of Credit ID
 */
public record LCId(UUID value) implements ValueObject, Serializable {

    public LCId {
        if (value == null) {
            throw new IllegalArgumentException("LC ID cannot be null");
        }
    }

    public static LCId generate() {
        return new LCId(UUID.randomUUID());
    }

    public static LCId from(String value) {
        return new LCId(UUID.fromString(value));
    }

    public static LCId from(UUID value) {
        return new LCId(value);
    }

    public String asString() {
        return value.toString();
    }
}

