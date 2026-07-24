package com.tradesphere.ai.document.adapter.out.persistence;

import com.tradesphere.ai.document.domain.model.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for DocumentJpaEntity
 * Tenant-aware queries for security
 */
@Repository
public interface DocumentJpaRepository extends JpaRepository<DocumentJpaEntity, UUID> {

    // Tenant-aware queries
    Optional<DocumentJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<DocumentJpaEntity> findByStorageKeyAndTenantId(String storageKey, UUID tenantId);

    Optional<DocumentJpaEntity> findByStorageKey(String storageKey);

    Page<DocumentJpaEntity> findByTenantId(UUID tenantId, Pageable pageable);

    Page<DocumentJpaEntity> findByTenantIdAndRelatedEntityTypeAndRelatedEntityId(
        UUID tenantId, String entityType, String entityId, Pageable pageable);

    Page<DocumentJpaEntity> findByTenantIdAndStatus(UUID tenantId, DocumentStatus status, Pageable pageable);

    @Query("SELECT d FROM DocumentJpaEntity d WHERE d.tenantId = :tenantId AND " +
           "(d.fileName LIKE %:searchTerm% OR d.originalFileName LIKE %:searchTerm%)")
    Page<DocumentJpaEntity> searchByFileName(@Param("tenantId") UUID tenantId,
                                            @Param("searchTerm") String searchTerm,
                                            Pageable pageable);
}

