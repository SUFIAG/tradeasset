package com.tradesphere.ai.customer.application.port.in;

import com.tradesphere.ai.shared.domain.valueobject.Money;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * Command to set credit limit for a customer
 */
@Value
@Builder
public class SetCreditLimitCommand {
    String customerId;
    Money creditLimit;
    UUID approvedBy;
}

