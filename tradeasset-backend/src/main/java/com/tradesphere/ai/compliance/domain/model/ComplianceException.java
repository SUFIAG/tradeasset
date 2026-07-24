package com.tradesphere.ai.compliance.domain.model;

import com.tradesphere.ai.shared.domain.DomainException;

/**
 * Compliance Domain Exception
 * Thrown when compliance business rules are violated
 */
public class ComplianceException extends DomainException {

    public ComplianceException(String message) {
        super(message);
    }

    public ComplianceException(String message, Throwable cause) {
        super(message, cause);
    }
}

