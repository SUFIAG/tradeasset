package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

@Getter
public class LCDocumentsAccepted extends BaseDomainEvent {
    private final String lcId;
    private final String lcNumber;
    private final String acceptedBy;

    public LCDocumentsAccepted(String lcId, String lcNumber, String acceptedBy) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.lcNumber = lcNumber;
        this.acceptedBy = acceptedBy;
    }
}

