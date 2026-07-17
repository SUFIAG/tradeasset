package com.tradesphere.ai.customer.application.port.in;

import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * Command to approve a customer
 */
@Value
@Builder
public class ApproveCustomerCommand {
    String customerId;
    UUID approvedBy;
}

