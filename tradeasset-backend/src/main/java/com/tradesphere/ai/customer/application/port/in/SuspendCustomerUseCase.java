package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;

import java.util.UUID;

/**
 * Inbound port for suspending a customer
 */
public interface SuspendCustomerUseCase {
    Customer suspendCustomer(String customerId, String reason, UUID suspendedBy);
    Customer reactivateCustomer(String customerId, UUID reactivatedBy);
}

