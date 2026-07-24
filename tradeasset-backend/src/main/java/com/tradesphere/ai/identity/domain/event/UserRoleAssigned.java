package com.tradesphere.ai.identity.domain.event;

import com.tradesphere.ai.shared.domain.BaseDomainEvent;
import lombok.Getter;

import java.util.UUID;

@Getter
public class UserRoleAssigned extends BaseDomainEvent {

    private final String roleId;

    private UserRoleAssigned(String userId, String roleId, UUID causedBy) {
        super(userId, "User", causedBy);
        this.roleId = roleId;
    }

    public static UserRoleAssigned of(String userId, String roleId, UUID causedBy) {
        return new UserRoleAssigned(userId, roleId, causedBy);
    }
}

