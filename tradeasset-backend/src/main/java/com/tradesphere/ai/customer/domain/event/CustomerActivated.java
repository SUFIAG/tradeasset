package com.tradesphere.ai.customer.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;
import java.util.UUID;

@Getter
public class CustomerActivated extends BaseDomainEvent {
    private final String customerNumber;
    private CustomerActivated(String aggregateId, String customerNumber, UUID causedBy) {
        super(aggregateId, "Customer", causedBy);
        this.customerNumber = customerNumber;
    }
    public static CustomerActivated of(String customerId, String customerNumber, UUID causedBy) {
        return new CustomerActivated(customerId, customerNumber, causedBy);
    }
}

