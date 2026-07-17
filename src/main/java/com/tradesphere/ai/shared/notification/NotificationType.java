package com.tradesphere.ai.shared.notification;

/**
 * Notification types for different events
 */
public enum NotificationType {
    // SLA Related
    SLA_WARNING,
    SLA_BREACH,
    TASK_OVERDUE,
    WORKFLOW_OVERDUE,

    // Task Related
    TASK_ASSIGNED,
    TASK_REASSIGNED,
    TASK_ESCALATED,
    TASK_COMPLETED,
    TASK_REJECTED,

    // Approval Related
    APPROVAL_REQUIRED,
    APPROVAL_APPROVED,
    APPROVAL_REJECTED,
    APPROVAL_DELEGATED,

    // Workflow Related
    WORKFLOW_STARTED,
    WORKFLOW_COMPLETED,
    WORKFLOW_CANCELLED,

    // Compliance Related
    COMPLIANCE_CHECK_REQUIRED,
    COMPLIANCE_ALERT,
    HIGH_RISK_DETECTED,

    // Document Related
    DOCUMENT_UPLOADED,
    DOCUMENT_VERIFIED,
    DOCUMENT_REJECTED,

    // LC Related
    LC_APPLICATION_SUBMITTED,
    LC_APPROVED,
    LC_REJECTED,
    LC_ISSUED,
    LC_DOCUMENTS_RECEIVED,

    // System Related
    SYSTEM_ALERT,
    MAINTENANCE_NOTICE,
    SECURITY_ALERT
}

