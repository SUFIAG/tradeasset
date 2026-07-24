package com.tradesphere.ai.collection.adapter.out.persistence;

import com.tradesphere.ai.collection.domain.model.CollectionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentaryCollectionJpaRepository extends JpaRepository<DocumentaryCollectionJpaEntity, UUID> {
    
    Optional<DocumentaryCollectionJpaEntity> findByIdAndTenantId(UUID id, String tenantId);

    Optional<DocumentaryCollectionJpaEntity> findByReferenceNumberAndTenantId(String referenceNumber, String tenantId);

    List<DocumentaryCollectionJpaEntity> findByTenantId(String tenantId);

    List<DocumentaryCollectionJpaEntity> findByTenantIdOrderByCreatedAtDesc(String tenantId);

    List<DocumentaryCollectionJpaEntity> findByStatusAndTenantId(CollectionStatus status, String tenantId);

    List<DocumentaryCollectionJpaEntity> findByDrawerIdAndTenantId(UUID drawerId, String tenantId);

    List<DocumentaryCollectionJpaEntity> findByDraweeIdAndTenantId(UUID draweeId, String tenantId);
}



