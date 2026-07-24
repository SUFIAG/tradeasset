package com.tradesphere.ai.customer.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;
import java.util.UUID;

@Getter
public class CustomerComplianceStatusChanged extends BaseDomainEvent {
    private final String customerNumber;
    private final String newStatus;

    private CustomerComplianceStatusChanged(String aggregateId, String customerNumber, String newStatus, UUID causedBy) {
        super(aggregateId, "Customer", causedBy);
        this.customerNumber = customerNumber;
        this.newStatus = newStatus;
    }

    public static CustomerComplianceStatusChanged of(String customerId, String customerNumber, String newStatus, UUID causedBy) {
        return new CustomerComplianceStatusChanged(customerId, customerNumber, newStatus, causedBy);
    }
}

