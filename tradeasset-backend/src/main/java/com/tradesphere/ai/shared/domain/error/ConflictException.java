package com.tradesphere.ai.shared.domain.error;

import com.tradesphere.ai.shared.domain.DomainException;

import java.util.Map;

/** The request conflicts with existing data (duplicate, concurrent change). Rendered as 409. */
public class ConflictException extends DomainException {

    private final transient Map<String, Object> properties;

    public ConflictException(String code, String message) {
        this(code, message, null);
    }

    public ConflictException(String code, String message, Map<String, Object> properties) {
        super(message, code);
        this.properties = properties == null ? Map.of() : Map.copyOf(properties);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
