package com.tradesphere.ai.shared.domain.error;

import com.tradesphere.ai.shared.domain.DomainException;

import java.util.List;

/**
 * An action is not legal from the record's current state (REQ-STA-001). Rendered as 409 with code
 * ILLEGAL_STATE_TRANSITION and the current state plus the legal next actions.
 */
public class IllegalStateTransitionException extends DomainException {

    private final String resourceType;
    private final String currentState;
    private final String attemptedAction;
    private final List<String> legalNextActions;

    public IllegalStateTransitionException(String resourceType, String currentState, String attemptedAction,
                                           List<String> legalNextActions) {
        super(String.format("Cannot %s %s in state %s. Legal next actions: %s",
                attemptedAction, resourceType, currentState, legalNextActions), "ILLEGAL_STATE_TRANSITION");
        this.resourceType = resourceType;
        this.currentState = currentState;
        this.attemptedAction = attemptedAction;
        this.legalNextActions = List.copyOf(legalNextActions);
    }

    public String getResourceType() {
        return resourceType;
    }

    public String getCurrentState() {
        return currentState;
    }

    public String getAttemptedAction() {
        return attemptedAction;
    }

    public List<String> getLegalNextActions() {
        return legalNextActions;
    }
}
