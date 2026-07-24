package com.tradesphere.ai.customer.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;
import java.util.UUID;

@Getter
public class CustomerKYCCompleted extends BaseDomainEvent {
    private final String customerNumber;
    private CustomerKYCCompleted(String aggregateId, String customerNumber, UUID causedBy) {
        super(aggregateId, "Customer", causedBy);
        this.customerNumber = customerNumber;
    }
    public static CustomerKYCCompleted of(String customerId, String customerNumber, UUID causedBy) {
        return new CustomerKYCCompleted(customerId, customerNumber, causedBy);
    }
}

