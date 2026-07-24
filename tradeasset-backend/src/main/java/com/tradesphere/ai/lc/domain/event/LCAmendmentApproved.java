package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import lombok.Getter;

import java.time.LocalDate;

/**
 * Domain event raised when an LC amendment is approved
 */
@Getter
public class LCAmendmentApproved extends BaseDomainEvent {

    private final String lcId;
    private final String lcNumber;
    private final Money newAmount;
    private final LocalDate newExpiryDate;
    private final String approvedBy;

    public LCAmendmentApproved(String lcId, String lcNumber, Money newAmount,
                              LocalDate newExpiryDate, String approvedBy) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.lcNumber = lcNumber;
        this.newAmount = newAmount;
        this.newExpiryDate = newExpiryDate;
        this.approvedBy = approvedBy;
    }
}

