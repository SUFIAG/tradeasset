package com.tradesphere.ai.risk.domain.model;

import com.tradesphere.ai.shared.domain.DomainException;

/**
 * Risk Domain Exception
 * Thrown when risk business rules are violated
 */
public class RiskException extends DomainException {
    
    public RiskException(String message) {
        super(message);
    }
    
    public RiskException(String message, Throwable cause) {
        super(message, cause);
    }
}

