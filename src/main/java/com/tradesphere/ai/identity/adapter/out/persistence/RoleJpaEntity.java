package com.tradesphere.ai.identity.adapter.out.persistence;

import com.tradesphere.ai.identity.domain.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * JPA Entity for Role persistence
 */
@Entity
@Table(name = "roles", schema = "identity")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleJpaEntity {

    @Id
    private UUID id;

    @Column(name = "tenant_id", nullable = false)
    private UUID tenantId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "code", unique = true, nullable = false, length = 50)
    private String code;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_system_role", nullable = false)
    private boolean systemRole;

    @ElementCollection
    @CollectionTable(name = "role_permissions", schema = "identity",
                     joinColumns = @JoinColumn(name = "role_id"))
    @Column(name = "permission_id")
    @Builder.Default
    private Set<UUID> permissionIds = new HashSet<>();

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Long version;
}

