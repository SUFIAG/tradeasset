package com.tradesphere.ai.shared.domain.error;

import com.tradesphere.ai.shared.domain.DomainException;

/**
 * The caller is authenticated but this specific action is not allowed for them, for example a maker
 * approving their own work (REQ-MKR-002). Rendered as 403 with the given stable code.
 */
public class ForbiddenActionException extends DomainException {

    public ForbiddenActionException(String code, String message) {
        super(message, code);
    }
}
