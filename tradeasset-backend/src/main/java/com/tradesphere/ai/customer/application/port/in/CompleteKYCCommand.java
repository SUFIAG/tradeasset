package com.tradesphere.ai.customer.application.port.in;

import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Command to complete KYC for a customer
 */
@Value
@Builder
public class CompleteKYCCommand {
    String customerId;
    LocalDate expiryDate;
    UUID completedBy;
}

