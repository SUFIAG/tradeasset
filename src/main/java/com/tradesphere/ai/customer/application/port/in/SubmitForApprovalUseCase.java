package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.customer.domain.model.Customer;

import java.util.UUID;

/**
 * Inbound port for submitting customer for approval
 */
public interface SubmitForApprovalUseCase {
    Customer submitForApproval(String customerId, UUID submittedBy);
}

