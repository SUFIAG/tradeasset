package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event raised when LC application is submitted for approval
 */
@Getter
public class LCApplicationSubmitted extends BaseDomainEvent {
    
    private final String lcId;
    private final String applicantId;
    private final Money lcAmount;
    
    public LCApplicationSubmitted(
            String lcId,
            String applicantId,
            Money lcAmount) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.applicantId = applicantId;
        this.lcAmount = lcAmount;
    }
}

