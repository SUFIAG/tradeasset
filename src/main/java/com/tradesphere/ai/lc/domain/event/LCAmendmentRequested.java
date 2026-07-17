package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event raised when LC amendment is requested
 */
@Getter
public class LCAmendmentRequested extends BaseDomainEvent {
    
    private final String lcId;
    private final String lcNumber;
    private final String amendmentDetails;
    private final String requestedBy;
    
    public LCAmendmentRequested(
            String lcId,
            String lcNumber,
            String amendmentDetails,
            String requestedBy) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.lcNumber = lcNumber;
        this.amendmentDetails = amendmentDetails;
        this.requestedBy = requestedBy;
    }
}

