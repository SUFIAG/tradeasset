package com.tradesphere.ai.shared.domain;

import lombok.Getter;

/**
 * Base exception for all domain-level exceptions
 * Domain exceptions represent business rule violations
 */
@Getter
public class DomainException extends RuntimeException {

    private final String errorCode;

    public DomainException(String message) {
        super(message);
        this.errorCode = this.getClass().getSimpleName();
    }

    public DomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public DomainException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = this.getClass().getSimpleName();
    }

    public DomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}

