package com.tradesphere.ai.shared.domain.error;

import com.tradesphere.ai.shared.domain.DomainException;

/** Input is malformed. Rendered as 400 with code VALIDATION_FAILED and the offending field. */
public class ValidationException extends DomainException {

    private final String field;

    public ValidationException(String field, String message) {
        super(message, "VALIDATION_FAILED");
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
