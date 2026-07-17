package com.tradesphere.ai.customer.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;
import java.util.UUID;

@Getter
public class CustomerReactivated extends BaseDomainEvent {
    private final String customerNumber;
    private CustomerReactivated(String aggregateId, String customerNumber, UUID causedBy) {
        super(aggregateId, "Customer", causedBy);
        this.customerNumber = customerNumber;
    }
    public static CustomerReactivated of(String customerId, String customerNumber, UUID causedBy) {
        return new CustomerReactivated(customerId, customerNumber, causedBy);
    }
}

