package com.tradesphere.ai.customer.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import lombok.Getter;
import java.util.UUID;

@Getter
public class CustomerCreditLimitSet extends BaseDomainEvent {
    private final String customerNumber;
    private final Money creditLimit;

    private CustomerCreditLimitSet(String aggregateId, String customerNumber, Money creditLimit, UUID causedBy) {
        super(aggregateId, "Customer", causedBy);
        this.customerNumber = customerNumber;
        this.creditLimit = creditLimit;
    }

    public static CustomerCreditLimitSet of(String customerId, String customerNumber, Money creditLimit, UUID causedBy) {
        return new CustomerCreditLimitSet(customerId, customerNumber, creditLimit, causedBy);
    }
}

