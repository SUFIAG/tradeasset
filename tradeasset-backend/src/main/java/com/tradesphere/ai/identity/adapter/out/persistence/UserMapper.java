package com.tradesphere.ai.identity.adapter.out.persistence;

import com.tradesphere.ai.identity.domain.model.User;
import com.tradesphere.ai.identity.domain.model.UserId;
import org.springframework.stereotype.Component;

/**
 * Mapper between User domain model and JPA entity
 */
@Component
public class UserMapper {

    public UserJpaEntity toJpaEntity(User user) {
        return UserJpaEntity.builder()
                .id(user.getId().getValue())
                .tenantId(user.getTenantId())
                .username(user.getUsername())
                .email(user.getEmail())
                .passwordHash(user.getPasswordHash())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .status(user.getStatus())
                .emailVerified(user.isEmailVerified())
                .lastLoginAt(user.getLastLoginAt())
                .roleIds(user.getRoleIds())
                .failedLoginAttempts(user.getFailedLoginAttempts())
                .lockedUntil(user.getLockedUntil())
                .createdAt(user.getCreatedAt())
                .createdBy(user.getCreatedBy())
                .updatedAt(user.getUpdatedAt())
                .updatedBy(user.getUpdatedBy())
                .version(user.getVersion())
                .build();
    }

    public User toDomainModel(UserJpaEntity entity) {
        // Note: Using builder pattern for reconstruction
        // This bypasses domain validation as data is already validated in DB
        return User.builder()
                .id(UserId.of(entity.getId()))
                .tenantId(entity.getTenantId())
                .username(entity.getUsername())
                .email(entity.getEmail())
                .passwordHash(entity.getPasswordHash())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .status(entity.getStatus())
                .emailVerified(entity.isEmailVerified())
                .lastLoginAt(entity.getLastLoginAt())
                .roleIds(entity.getRoleIds())
                .failedLoginAttempts(entity.getFailedLoginAttempts())
                .lockedUntil(entity.getLockedUntil())
                .createdAt(entity.getCreatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedAt(entity.getUpdatedAt())
                .updatedBy(entity.getUpdatedBy())
                .version(entity.getVersion())
                .build();
    }
}

