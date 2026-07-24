package com.tradesphere.ai.customer.application.port.out;

import com.tradesphere.ai.customer.domain.model.Customer;

/**
 * Outbound port for saving customers to persistence
 */
public interface SaveCustomerPort {
    Customer saveCustomer(Customer customer);
}

