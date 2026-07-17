package com.tradesphere.ai.customer.domain.model;

import com.tradesphere.ai.shared.domain.DomainException;

/**
 * Customer Domain Exception
 * Thrown when business rules related to customers are violated
 */
public class CustomerException extends DomainException {

    public CustomerException(String message) {
        super(message, "CUSTOMER_ERROR");
    }

    public CustomerException(String message, String errorCode) {
        super(message, errorCode);
    }

    public CustomerException(String message, Throwable cause) {
        super(message, "CUSTOMER_ERROR", cause);
    }

    // Specific exception factory methods for common scenarios

    public static CustomerException customerNotFound(CustomerId customerId) {
        return new CustomerException(
            String.format("Customer not found: %s", customerId),
            "CUSTOMER_NOT_FOUND"
        );
    }

    public static CustomerException invalidStatus(CustomerStatus currentStatus, String operation) {
        return new CustomerException(
            String.format("Cannot %s customer in status: %s", operation, currentStatus),
            "INVALID_CUSTOMER_STATUS"
        );
    }

    public static CustomerException kycNotCompleted() {
        return new CustomerException(
            "KYC must be completed before this operation",
            "KYC_NOT_COMPLETED"
        );
    }

    public static CustomerException complianceNotCleared() {
        return new CustomerException(
            "Compliance screening must be cleared before this operation",
            "COMPLIANCE_NOT_CLEARED"
        );
    }

    public static CustomerException missingRequiredDocuments() {
        return new CustomerException(
            "Required KYC documents are missing",
            "MISSING_REQUIRED_DOCUMENTS"
        );
    }

    public static CustomerException creditLimitExceeded(String customerNumber) {
        return new CustomerException(
            String.format("Credit limit exceeded for customer: %s", customerNumber),
            "CREDIT_LIMIT_EXCEEDED"
        );
    }
}

