package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event raised when LC application is approved
 */
@Getter
public class LCApplicationApproved extends BaseDomainEvent {
    
    private final String lcId;
    private final String applicantId;
    private final String approvedBy;
    private final Money lcAmount;
    
    public LCApplicationApproved(
            String lcId,
            String applicantId,
            String approvedBy,
            Money lcAmount) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.applicantId = applicantId;
        this.approvedBy = approvedBy;
        this.lcAmount = lcAmount;
    }
}

