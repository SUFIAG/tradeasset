package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event raised when payment is authorized for LC
 */
@Getter
public class LCPaymentAuthorized extends BaseDomainEvent {

    private final String lcId;
    private final String lcNumber;
    private final Money paymentAmount;
    private final String beneficiaryId;

    public LCPaymentAuthorized(
            String lcId,
            String lcNumber,
            Money paymentAmount,
            String beneficiaryId) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.lcNumber = lcNumber;
        this.paymentAmount = paymentAmount;
        this.beneficiaryId = beneficiaryId;
    }
}

