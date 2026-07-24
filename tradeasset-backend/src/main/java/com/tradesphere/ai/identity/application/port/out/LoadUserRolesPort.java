package com.tradesphere.ai.identity.application.port.out;

import java.util.Set;
import java.util.UUID;

/**
 * Port for loading user roles and permissions
 */
public interface LoadUserRolesPort {
    
    Set<String> loadRoleCodesByRoleIds(Set<UUID> roleIds);
    
    Set<String> loadPermissionCodesByRoleIds(Set<UUID> roleIds);
}

