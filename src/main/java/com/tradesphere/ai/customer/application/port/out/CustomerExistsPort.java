package com.tradesphere.ai.customer.application.port.out;

/**
 * Outbound port for checking if customer exists
 */
public interface CustomerExistsPort {
    boolean existsByEmail(String email);
    boolean existsByBusinessRegistrationNumber(String registrationNumber);
}

