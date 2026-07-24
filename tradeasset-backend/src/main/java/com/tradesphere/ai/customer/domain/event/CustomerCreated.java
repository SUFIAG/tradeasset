package com.tradesphere.ai.customer.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

/**
 * Domain Event: Customer Created
 * Published when a new customer is created in the system
 */
@Getter
public class CustomerCreated extends BaseDomainEvent {

    private final String customerNumber;
    private final String name;
    private final String type;

    private CustomerCreated(
            String aggregateId,
            String customerNumber,
            String name,
            String type,
            UUID causedBy) {
        super(aggregateId, "Customer", causedBy);
        this.customerNumber = customerNumber;
        this.name = name;
        this.type = type;
    }

    public static CustomerCreated of(
            String customerId,
            String customerNumber,
            String name,
            String type,
            UUID causedBy) {
        return new CustomerCreated(customerId, customerNumber, name, type, causedBy);
    }
}

