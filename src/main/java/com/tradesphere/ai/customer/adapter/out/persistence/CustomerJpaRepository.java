package com.tradesphere.ai.customer.adapter.out.persistence;

import com.tradesphere.ai.customer.domain.model.CustomerStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for CustomerJpaEntity
 * High-performance with tenant-aware queries
 */
@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerJpaEntity, UUID> {
    
    // Tenant-aware queries for security and performance
    Optional<CustomerJpaEntity> findByIdAndTenantId(UUID id, UUID tenantId);

    Optional<CustomerJpaEntity> findByCustomerNumberAndTenantId(String customerNumber, UUID tenantId);

    Optional<CustomerJpaEntity> findByCustomerNumber(String customerNumber);
    
    boolean existsByIdAndTenantId(UUID id, UUID tenantId);

    boolean existsByEmailAndTenantId(String email, UUID tenantId);

    boolean existsByEmail(String email);
    
    boolean existsByBusinessRegistrationNumberAndTenantId(String businessRegistrationNumber, UUID tenantId);

    boolean existsByBusinessRegistrationNumber(String businessRegistrationNumber);

    Page<CustomerJpaEntity> findByTenantIdAndStatus(UUID tenantId, CustomerStatus status, Pageable pageable);

    Page<CustomerJpaEntity> findByStatus(CustomerStatus status, Pageable pageable);

    @Query("SELECT c FROM CustomerJpaEntity c WHERE c.tenantId = :tenantId AND (" +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.customerNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<CustomerJpaEntity> searchByNameOrEmailOrNumberAndTenantId(
        @Param("tenantId") UUID tenantId,
        @Param("searchTerm") String searchTerm,
        Pageable pageable);

    @Query("SELECT c FROM CustomerJpaEntity c WHERE " +
           "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(c.customerNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<CustomerJpaEntity> searchByNameOrEmailOrNumber(@Param("searchTerm") String searchTerm, Pageable pageable);
}
