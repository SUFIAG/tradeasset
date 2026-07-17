package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

@Getter
public class LCDocumentsRejected extends BaseDomainEvent {
    private final String lcId;
    private final String lcNumber;
    private final String discrepancies;
    private final String rejectedBy;

    public LCDocumentsRejected(String lcId, String lcNumber, String discrepancies, String rejectedBy) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.lcNumber = lcNumber;
        this.discrepancies = discrepancies;
        this.rejectedBy = rejectedBy;
    }
}

