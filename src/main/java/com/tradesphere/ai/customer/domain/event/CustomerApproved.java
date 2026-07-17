package com.tradesphere.ai.customer.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;
import java.util.UUID;

@Getter
public class CustomerApproved extends BaseDomainEvent {
    private final String customerNumber;

    private CustomerApproved(String aggregateId, String customerNumber, UUID causedBy) {
        super(aggregateId, "Customer", causedBy);
        this.customerNumber = customerNumber;
    }

    public static CustomerApproved of(String customerId, String customerNumber, UUID causedBy) {
        return new CustomerApproved(customerId, customerNumber, causedBy);
    }
}

