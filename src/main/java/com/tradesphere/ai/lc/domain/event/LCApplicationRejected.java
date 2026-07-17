package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event raised when LC application is rejected
 */
@Getter
public class LCApplicationRejected extends BaseDomainEvent {

    private final String lcId;
    private final String applicantId;
    private final String rejectedBy;
    private final String reason;

    public LCApplicationRejected(
            String lcId,
            String applicantId,
            String rejectedBy,
            String reason) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.applicantId = applicantId;
        this.rejectedBy = rejectedBy;
        this.reason = reason;
    }
}

