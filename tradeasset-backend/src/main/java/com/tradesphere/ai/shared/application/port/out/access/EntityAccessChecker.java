package com.tradesphere.ai.shared.application.port.out.access;

import com.tradesphere.ai.shared.security.AuthenticatedUser;

import java.util.Set;
import java.util.UUID;

/**
 * Answers "may this user see or change this record" for the record types a module owns. Every
 * module that owns records registers one bean. Used by modules that attach things to other
 * modules' records: documents (REQ-DOC-005), the generic timeline, the copilot (REQ-KNW-004).
 *
 * <p>A record outside the user's tenant must answer false for both methods, and callers report it
 * as 404 (REQ-SEC-008).</p>
 */
public interface EntityAccessChecker {

    /** {@code EntityTypes} codes handled by this checker. */
    Set<String> entityTypes();

    boolean canRead(AuthenticatedUser user, String entityType, UUID entityId);

    boolean canWrite(AuthenticatedUser user, String entityType, UUID entityId);

    /** Short human label of the record, for example the LC reference. Null when not found or not readable. */
    default String labelOf(AuthenticatedUser user, String entityType, UUID entityId) {
        return null;
    }
}
