package com.tradesphere.ai.identity.adapter.out.persistence;

import com.tradesphere.ai.identity.application.port.out.LoadUserRolesPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Adapter for loading user roles and permissions
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RolePersistenceAdapter implements LoadUserRolesPort {

    private final RoleJpaRepository roleRepository;
    private final PermissionJpaRepository permissionRepository;

    @Override
    public Set<String> loadRoleCodesByRoleIds(Set<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }

        List<RoleJpaEntity> roles = roleRepository.findByIdIn(roleIds);
        return roles.stream()
                .map(RoleJpaEntity::getCode)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<String> loadPermissionCodesByRoleIds(Set<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return new HashSet<>();
        }

        List<RoleJpaEntity> roles = roleRepository.findByIdIn(roleIds);
        Set<UUID> allPermissionIds = roles.stream()
                .flatMap(role -> role.getPermissionIds().stream())
                .collect(Collectors.toSet());

        if (allPermissionIds.isEmpty()) {
            return new HashSet<>();
        }

        List<PermissionJpaEntity> permissions = permissionRepository.findByIdIn(allPermissionIds);
        return permissions.stream()
                .map(PermissionJpaEntity::getCode)
                .collect(Collectors.toSet());
    }
}

