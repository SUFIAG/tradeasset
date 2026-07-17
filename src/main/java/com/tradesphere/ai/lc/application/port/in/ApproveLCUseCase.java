package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import lombok.Builder;
import lombok.Value;

import java.util.UUID;

/**
 * Use case for approving LC application
 */
public interface ApproveLCUseCase {
    
    /**
     * Approve LC application
     */
    LetterOfCredit approveLC(ApproveLCCommand command);
    
    @Value
    @Builder
    class ApproveLCCommand {
        String lcId;
        UUID approvedBy;
        boolean creditLimitApproved;
        String comments;
    }
}

