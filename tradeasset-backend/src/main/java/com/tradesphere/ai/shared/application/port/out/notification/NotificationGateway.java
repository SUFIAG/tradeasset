package com.tradesphere.ai.shared.application.port.out.notification;

import java.util.UUID;

/**
 * Cross-module in-app (and, when configured, e-mail) notifications. Implemented by the workflow
 * module. Never put a national ID, IBAN, passport number, token, OTP or password in a title or body.
 */
public interface NotificationGateway {

    void notifyUser(UUID tenantId, UUID userId, Notice notice);

    /** Notifies every active user of the tenant holding the role. */
    void notifyRole(UUID tenantId, String role, Notice notice);

    /**
     * @param type       stable code, for example TASK_ASSIGNED, SLA_BREACHED, SCREENING_HIT, LC_ISSUED
     * @param severity   INFO, WARNING or CRITICAL
     * @param entityType optional link target
     * @param entityId   optional link target
     */
    record Notice(String type, String severity, String title, String body, String entityType, UUID entityId) {
    }
}
