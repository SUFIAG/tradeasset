package com.tradesphere.ai.customer.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;
import java.util.UUID;

@Getter
public class CustomerSuspended extends BaseDomainEvent {
    private final String customerNumber;
    private final String reason;

    private CustomerSuspended(String aggregateId, String customerNumber, String reason, UUID causedBy) {
        super(aggregateId, "Customer", causedBy);
        this.customerNumber = customerNumber;
        this.reason = reason;
    }

    public static CustomerSuspended of(String customerId, String customerNumber, String reason, UUID causedBy) {
        return new CustomerSuspended(customerId, customerNumber, reason, causedBy);
    }
}

