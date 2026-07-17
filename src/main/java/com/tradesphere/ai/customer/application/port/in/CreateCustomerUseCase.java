package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;

/**
 * Inbound port for creating a customer
 */
public interface CreateCustomerUseCase {
    Customer createCustomer(CreateCustomerCommand command);
}

