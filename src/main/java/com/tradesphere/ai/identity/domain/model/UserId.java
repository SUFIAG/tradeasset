package com.tradesphere.ai.identity.domain.model;

import com.tradesphere.ai.shared.domain.ValueObject;
import lombok.Value;

import java.util.UUID;

@Value
public class UserId implements ValueObject {

    UUID value;

    private UserId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        this.value = value;
    }

    public static UserId of(UUID value) {
        return new UserId(value);
    }

    public static UserId of(String value) {
        return new UserId(UUID.fromString(value));
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}

