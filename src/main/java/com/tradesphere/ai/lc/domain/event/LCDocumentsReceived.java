package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event raised when documents are received for LC
 */
@Getter
public class LCDocumentsReceived extends BaseDomainEvent {

    private final String lcId;
    private final String lcNumber;

    public LCDocumentsReceived(
            String lcId,
            String lcNumber) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.lcNumber = lcNumber;
    }
}

