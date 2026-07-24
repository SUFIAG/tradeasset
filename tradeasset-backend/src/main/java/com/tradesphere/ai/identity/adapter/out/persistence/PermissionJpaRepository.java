package com.tradesphere.ai.identity.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface PermissionJpaRepository extends JpaRepository<PermissionJpaEntity, UUID> {
    
    List<PermissionJpaEntity> findByIdIn(Set<UUID> permissionIds);
}

