package com.tradesphere.ai.identity.domain.model;

import com.tradesphere.ai.shared.domain.AggregateRoot;
import com.tradesphere.ai.identity.domain.event.UserCreated;
import com.tradesphere.ai.identity.domain.event.UserLoggedIn;
import com.tradesphere.ai.identity.domain.event.UserRoleAssigned;
import lombok.Getter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * User Aggregate Root
 * Represents a user in the system with authentication and authorization
 */
@Getter
public class User extends AggregateRoot<UserId> {

    private UUID tenantId;
    private String username;
    private String email;
    private String passwordHash;
    private String firstName;
    private String lastName;
    private UserStatus status;
    private boolean emailVerified;
    private Instant lastLoginAt;
    private Set<UUID> roleIds;
    private int failedLoginAttempts;
    private Instant lockedUntil;

    private User(UserId id, UUID tenantId) {
        super(id);
        this.tenantId = tenantId;
        this.status = UserStatus.PENDING;
        this.emailVerified = false;
        this.roleIds = new HashSet<>();
        this.failedLoginAttempts = 0;
    }

    // Private constructor for builder (used by persistence layer)
    private User(UserId id) {
        super(id);
    }

    /**
     * Builder for reconstructing User from persistence
     */
    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private User user;

        public UserBuilder() {}

        public UserBuilder id(UserId id) {
            this.user = new User(id);
            return this;
        }

        public UserBuilder tenantId(UUID tenantId) {
            user.tenantId = tenantId;
            return this;
        }

        public UserBuilder username(String username) {
            user.username = username;
            return this;
        }

        public UserBuilder email(String email) {
            user.email = email;
            return this;
        }

        public UserBuilder passwordHash(String passwordHash) {
            user.passwordHash = passwordHash;
            return this;
        }

        public UserBuilder firstName(String firstName) {
            user.firstName = firstName;
            return this;
        }

        public UserBuilder lastName(String lastName) {
            user.lastName = lastName;
            return this;
        }

        public UserBuilder status(UserStatus status) {
            user.status = status;
            return this;
        }

        public UserBuilder emailVerified(boolean emailVerified) {
            user.emailVerified = emailVerified;
            return this;
        }

        public UserBuilder lastLoginAt(Instant lastLoginAt) {
            user.lastLoginAt = lastLoginAt;
            return this;
        }

        public UserBuilder roleIds(Set<UUID> roleIds) {
            user.roleIds = roleIds != null ? roleIds : new HashSet<>();
            return this;
        }

        public UserBuilder failedLoginAttempts(int failedLoginAttempts) {
            user.failedLoginAttempts = failedLoginAttempts;
            return this;
        }

        public UserBuilder lockedUntil(Instant lockedUntil) {
            user.lockedUntil = lockedUntil;
            return this;
        }

        public UserBuilder createdAt(Instant createdAt) {
            user.createdAt = createdAt;
            return this;
        }

        public UserBuilder createdBy(UUID createdBy) {
            user.createdBy = createdBy;
            return this;
        }

        public UserBuilder updatedAt(Instant updatedAt) {
            user.updatedAt = updatedAt;
            return this;
        }

        public UserBuilder updatedBy(UUID updatedBy) {
            user.updatedBy = updatedBy;
            return this;
        }

        public UserBuilder version(Long version) {
            user.version = version;
            return this;
        }

        public User build() {
            return user;
        }
    }

    public static User create(
            UUID tenantId,
            String username,
            String email,
            String passwordHash,
            String firstName,
            String lastName,
            UUID createdBy) {

        validateNotNull(tenantId, "Tenant ID");
        validateNotEmpty(username, "Username");
        validateEmail(email);
        validateNotEmpty(passwordHash, "Password hash");

        User user = new User(UserId.generate(), tenantId);
        user.username = username;
        user.email = email;
        user.passwordHash = passwordHash;
        user.firstName = firstName;
        user.lastName = lastName;
        user.createdBy = createdBy;
        user.createdAt = Instant.now();
        user.status = UserStatus.ACTIVE;

        user.registerEvent(UserCreated.of(
                user.getId().toString(),
                username,
                email,
                createdBy
        ));

        return user;
    }

    public void assignRole(UUID roleId, UUID assignedBy) {
        if (roleIds.contains(roleId)) {
            throw new UserException("Role already assigned to user");
        }

        roleIds.add(roleId);
        markAsUpdated(assignedBy);

        registerEvent(UserRoleAssigned.of(
                getId().toString(),
                roleId.toString(),
                assignedBy
        ));
    }

    public void removeRole(UUID roleId) {
        roleIds.remove(roleId);
    }

    public void recordSuccessfulLogin() {
        this.lastLoginAt = Instant.now();
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;

        registerEvent(UserLoggedIn.of(
                getId().toString(),
                username,
                Instant.now()
        ));
    }

    public void recordFailedLogin() {
        this.failedLoginAttempts++;

        if (failedLoginAttempts >= 5) {
            lockAccount(30); // Lock for 30 minutes
        }
    }

    public void lockAccount(int minutes) {
        this.status = UserStatus.LOCKED;
        this.lockedUntil = Instant.now().plusSeconds(minutes * 60L);
    }

    public void unlockAccount() {
        this.status = UserStatus.ACTIVE;
        this.lockedUntil = null;
        this.failedLoginAttempts = 0;
    }

    public boolean isLocked() {
        if (status == UserStatus.LOCKED && lockedUntil != null) {
            if (Instant.now().isAfter(lockedUntil)) {
                unlockAccount();
                return false;
            }
            return true;
        }
        return false;
    }

    public void deactivate(UUID deactivatedBy) {
        this.status = UserStatus.INACTIVE;
        markAsUpdated(deactivatedBy);
    }

    public void activate(UUID activatedBy) {
        this.status = UserStatus.ACTIVE;
        markAsUpdated(activatedBy);
    }

    public void verifyEmail() {
        this.emailVerified = true;
    }

    public void updatePassword(String newPasswordHash, UUID updatedBy) {
        validateNotEmpty(newPasswordHash, "Password hash");
        this.passwordHash = newPasswordHash;
        markAsUpdated(updatedBy);
    }

    // Validation helpers
    private static void validateNotNull(Object value, String fieldName) {
        if (value == null) {
            throw new UserException(fieldName + " cannot be null");
        }
    }

    private static void validateNotEmpty(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new UserException(fieldName + " cannot be empty");
        }
    }

    private static void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new UserException("Invalid email format");
        }
    }
}
