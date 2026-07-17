package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;

import java.util.UUID;

/**
 * Inbound port for approving a customer
 */
public interface ApproveCustomerUseCase {
    Customer approveCustomer(ApproveCustomerCommand command);
    Customer activateCustomer(String customerId, UUID activatedBy);
}

