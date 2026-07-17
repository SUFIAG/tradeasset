package com.tradesphere.ai.workflow.domain.model;

import com.tradesphere.ai.shared.domain.DomainException;

/**
 * Domain exception for Workflow operations
 */
public class WorkflowException extends DomainException {

    public WorkflowException(String message) {
        super("WORKFLOW_ERROR", message);
    }

    public WorkflowException(String code, String message) {
        super(code, message);
    }

    public static WorkflowException invalidStatus(String message) {
        return new WorkflowException("INVALID_STATUS", message);
    }

    public static WorkflowException taskNotFound(String message) {
        return new WorkflowException("TASK_NOT_FOUND", message);
    }

    public static WorkflowException unauthorizedAction(String message) {
        return new WorkflowException("UNAUTHORIZED", message);
    }

    public static WorkflowException slaBreached(String message) {
        return new WorkflowException("SLA_BREACHED", message);
    }

    public static WorkflowException invalidAssignment(String message) {
        return new WorkflowException("INVALID_ASSIGNMENT", message);
    }

    public static WorkflowException validationFailed(String message) {
        return new WorkflowException("VALIDATION_FAILED", message);
    }

    public static WorkflowException notFound(String message) {
        return new WorkflowException("NOT_FOUND", message);
    }
}

