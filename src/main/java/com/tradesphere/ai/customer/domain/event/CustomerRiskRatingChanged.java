package com.tradesphere.ai.customer.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CustomerRiskRatingChanged extends BaseDomainEvent {
    private final String customerNumber;
    private final String oldRating;
    private final String newRating;
    private final BigDecimal riskScore;
    private final String reason;

    private CustomerRiskRatingChanged(String aggregateId, String customerNumber, String oldRating,
                                     String newRating, BigDecimal riskScore, String reason, UUID causedBy) {
        super(aggregateId, "Customer", causedBy);
        this.customerNumber = customerNumber;
        this.oldRating = oldRating;
        this.newRating = newRating;
        this.riskScore = riskScore;
        this.reason = reason;
    }

    public static CustomerRiskRatingChanged of(String customerId, String customerNumber, String oldRating,
                                              String newRating, BigDecimal riskScore, String reason, UUID causedBy) {
        return new CustomerRiskRatingChanged(customerId, customerNumber, oldRating, newRating, riskScore, reason, causedBy);
    }
}

