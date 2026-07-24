package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;

/**
 * Inbound port for updating a customer
 */
public interface UpdateCustomerUseCase {
    Customer updateCustomer(UpdateCustomerCommand command);
}

