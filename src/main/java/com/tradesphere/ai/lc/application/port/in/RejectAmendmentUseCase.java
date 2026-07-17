package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.UUID;

/**
 * Use case for rejecting LC amendments
 */
public interface RejectAmendmentUseCase {

    /**
     * Reject LC amendment request
     */
    LetterOfCredit rejectAmendment(String lcId, String rejectionReason, UUID rejectedBy);
}

