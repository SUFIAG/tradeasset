package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;

import java.util.Optional;

/**
 * Inbound port for retrieving a customer
 */
public interface GetCustomerUseCase {
    Optional<Customer> getCustomerById(String customerId);
    Optional<Customer> getCustomerByNumber(String customerNumber);
}

