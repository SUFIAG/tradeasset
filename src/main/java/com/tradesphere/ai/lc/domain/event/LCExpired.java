package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class LCExpired extends BaseDomainEvent {
    private final String lcId;
    private final String lcNumber;
    private final LocalDate expiryDate;

    public LCExpired(String lcId, String lcNumber, LocalDate expiryDate) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.lcNumber = lcNumber;
        this.expiryDate = expiryDate;
    }
}

