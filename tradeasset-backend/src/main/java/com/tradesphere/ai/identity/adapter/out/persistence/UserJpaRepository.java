package com.tradesphere.ai.identity.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for UserJpaEntity
 */
@Repository
public interface UserJpaRepository extends JpaRepository<UserJpaEntity, UUID> {
    
    Optional<UserJpaEntity> findByUsername(String username);
    
    Optional<UserJpaEntity> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
}

