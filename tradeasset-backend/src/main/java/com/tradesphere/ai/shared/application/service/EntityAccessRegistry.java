package com.tradesphere.ai.shared.application.service;

import com.tradesphere.ai.shared.application.port.out.access.EntityAccessChecker;
import com.tradesphere.ai.shared.domain.error.NotFoundException;
import com.tradesphere.ai.shared.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Routes access questions to the module that owns the record type. An unknown type or a record
 * the user may not see is reported as not found (REQ-SEC-008).
 */
@Service
public class EntityAccessRegistry {

    private final Map<String, EntityAccessChecker> byType = new HashMap<>();

    public EntityAccessRegistry(List<EntityAccessChecker> checkers) {
        for (EntityAccessChecker checker : checkers) {
            for (String type : checker.entityTypes()) {
                EntityAccessChecker previous = byType.put(type, checker);
                if (previous != null) {
                    throw new IllegalStateException("Two access checkers registered for entity type " + type);
                }
            }
        }
    }

    public Set<String> supportedTypes() {
        return Set.copyOf(byType.keySet());
    }

    public boolean canRead(AuthenticatedUser user, String entityType, UUID entityId) {
        EntityAccessChecker checker = byType.get(entityType);
        return checker != null && checker.canRead(user, entityType, entityId);
    }

    public boolean canWrite(AuthenticatedUser user, String entityType, UUID entityId) {
        EntityAccessChecker checker = byType.get(entityType);
        return checker != null && checker.canWrite(user, entityType, entityId);
    }

    public void requireRead(AuthenticatedUser user, String entityType, UUID entityId) {
        if (!canRead(user, entityType, entityId)) {
            throw new NotFoundException(entityType, entityId);
        }
    }

    public void requireWrite(AuthenticatedUser user, String entityType, UUID entityId) {
        if (!canRead(user, entityType, entityId)) {
            throw new NotFoundException(entityType, entityId);
        }
        if (!canWrite(user, entityType, entityId)) {
            throw new com.tradesphere.ai.shared.domain.error.ForbiddenActionException("ENTITY_WRITE_FORBIDDEN",
                    "You may not change this record");
        }
    }

    public String labelOf(AuthenticatedUser user, String entityType, UUID entityId) {
        EntityAccessChecker checker = byType.get(entityType);
        return checker == null ? null : checker.labelOf(user, entityType, entityId);
    }
}
