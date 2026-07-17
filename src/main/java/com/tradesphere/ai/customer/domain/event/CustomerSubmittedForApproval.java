package com.tradesphere.ai.customer.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class CustomerSubmittedForApproval extends BaseDomainEvent {

    private final String customerNumber;

    private CustomerSubmittedForApproval(String aggregateId, String customerNumber, UUID causedBy) {
        super(aggregateId, "Customer", causedBy);
        this.customerNumber = customerNumber;
    }

    public static CustomerSubmittedForApproval of(String customerId, String customerNumber, UUID causedBy) {
        return new CustomerSubmittedForApproval(customerId, customerNumber, causedBy);
    }
}

