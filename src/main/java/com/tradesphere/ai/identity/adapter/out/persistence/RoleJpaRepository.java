package com.tradesphere.ai.identity.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface RoleJpaRepository extends JpaRepository<RoleJpaEntity, UUID> {
    
    List<RoleJpaEntity> findByIdIn(Set<UUID> roleIds);
    
    @Query("SELECT r FROM RoleJpaEntity r WHERE r.id IN :roleIds")
    List<RoleJpaEntity> findRolesByIds(@Param("roleIds") Set<UUID> roleIds);
}

