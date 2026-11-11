package com.tradesphere.ai.shared.domain.error;

import com.tradesphere.ai.shared.domain.DomainException;

import java.util.Map;

/** A business rule rejects the request. Rendered as 422 with the given stable code. */
public class BusinessRuleViolationException extends DomainException {

    private final transient Map<String, Object> properties;

    public BusinessRuleViolationException(String code, String message) {
        this(code, message, null);
    }

    /**
     * @param properties extra machine-readable context rendered into the problem detail, for
     *                   example the list of blocking reasons. Never put PII or secrets here.
     */
    public BusinessRuleViolationException(String code, String message, Map<String, Object> properties) {
        super(message, code);
        this.properties = properties == null ? Map.of() : Map.copyOf(properties);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}
