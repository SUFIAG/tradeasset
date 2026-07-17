package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import com.tradesphere.ai.shared.domain.valueobject.Money;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Use case for approving LC amendments
 */
public interface ApproveAmendmentUseCase {

    /**
     * Approve LC amendment with new terms
     */
    LetterOfCredit approveAmendment(
        String lcId,
        Money newAmount,
        LocalDate newExpiryDate,
        LocalDate newLastShipmentDate,
        UUID approvedBy
    );
}

