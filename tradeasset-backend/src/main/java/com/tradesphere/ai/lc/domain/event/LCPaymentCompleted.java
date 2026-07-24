package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import lombok.Getter;

@Getter
public class LCPaymentCompleted extends BaseDomainEvent {
    private final String lcId;
    private final String lcNumber;
    private final Money paymentAmount;

    public LCPaymentCompleted(String lcId, String lcNumber, Money paymentAmount) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.lcNumber = lcNumber;
        this.paymentAmount = paymentAmount;
    }
}

