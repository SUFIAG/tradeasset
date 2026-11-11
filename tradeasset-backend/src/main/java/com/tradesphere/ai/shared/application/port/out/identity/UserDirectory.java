package com.tradesphere.ai.shared.application.port.out.identity;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Cross-module view of users, implemented by the identity module. Used for task assignment,
 * escalation and notifications.
 */
public interface UserDirectory {

    Optional<UserRef> findById(UUID tenantId, UUID userId);

    /** Active users of the tenant holding the role. */
    List<UserRef> activeUsersWithRole(UUID tenantId, String role);

    /**
     * @param supervisorId the user's line supervisor for SLA escalation (REQ-WFL-003); may be null
     */
    record UserRef(UUID id, String username, String displayName, String email, Set<String> roles, boolean active,
                   UUID supervisorId) {
    }
}
