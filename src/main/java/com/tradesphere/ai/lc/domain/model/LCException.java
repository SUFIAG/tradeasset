package com.tradesphere.ai.lc.domain.model;

import com.tradesphere.ai.shared.domain.DomainException;

/**
 * Exception thrown when Letter of Credit business rules are violated
 */
public class LCException extends DomainException {

    public LCException(String message) {
        super(message);
    }

    public LCException(String message, String errorCode) {
        super(message, errorCode);
    }

    public LCException(String message, Throwable cause) {
        super(message, cause);
    }

    public static LCException invalidStatus(String message) {
        return new LCException(message, "LC_INVALID_STATUS");
    }

    public static LCException creditLimitExceeded(String message) {
        return new LCException(message, "LC_CREDIT_LIMIT_EXCEEDED");
    }

    public static LCException invalidParty(String message) {
        return new LCException(message, "LC_INVALID_PARTY");
    }

    public static LCException invalidAmount(String message) {
        return new LCException(message, "LC_INVALID_AMOUNT");
    }

    public static LCException invalidDates(String message) {
        return new LCException(message, "LC_INVALID_DATES");
    }

    public static LCException customerNotActive(String message) {
        return new LCException(message, "LC_CUSTOMER_NOT_ACTIVE");
    }

    public static LCException validationFailed(String message) {
        return new LCException(message, "LC_VALIDATION_FAILED");
    }
}

