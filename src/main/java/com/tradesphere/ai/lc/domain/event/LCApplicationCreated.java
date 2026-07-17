package com.tradesphere.ai.lc.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import com.tradesphere.ai.shared.domain.valueobject.Money;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event raised when a new LC application is created
 */
@Getter
public class LCApplicationCreated extends BaseDomainEvent {

    private final String lcId;
    private final String applicantId;
    private final String beneficiaryId;
    private final Money lcAmount;
    private final LocalDate expiryDate;

    public LCApplicationCreated(
            String lcId,
            String applicantId,
            String beneficiaryId,
            Money lcAmount,
            LocalDate expiryDate) {
        super(lcId, "LetterOfCredit");
        this.lcId = lcId;
        this.applicantId = applicantId;
        this.beneficiaryId = beneficiaryId;
        this.lcAmount = lcAmount;
        this.expiryDate = expiryDate;
    }
}

