package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;

/**
 * Inbound port for completing KYC
 */
public interface CompleteKYCUseCase {
    Customer completeKYC(CompleteKYCCommand command);
}

