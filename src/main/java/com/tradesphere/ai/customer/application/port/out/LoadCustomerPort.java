package com.tradesphere.ai.customer.application.port.out;

import com.tradesphere.ai.customer.domain.model.Customer;
import com.tradesphere.ai.customer.domain.model.CustomerId;

import java.util.Optional;

/**
 * Outbound port for loading customers from persistence
 */
public interface LoadCustomerPort {
    Optional<Customer> loadCustomerById(CustomerId customerId);
    Optional<Customer> loadCustomerByNumber(String customerNumber);
}

