package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;

import java.util.UUID;

/**
 * Inbound port for rejecting a customer
 */
public interface RejectCustomerUseCase {
    Customer rejectCustomer(String customerId, String reason, UUID rejectedBy);
}

