package com.tradesphere.ai.scf.adapter.out.persistence;

import com.tradesphere.ai.scf.application.port.out.SCFRepository;
import com.tradesphere.ai.scf.domain.model.SCFStatus;
import com.tradesphere.ai.scf.domain.model.SCFType;
import com.tradesphere.ai.scf.domain.model.SupplyChainFinance;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA Adapter for SCF Repository
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SCFRepositoryAdapter implements SCFRepository {

    private final SCFJpaRepository jpaRepository;
    private final SCFMapper mapper;

    @Override
    public SupplyChainFinance save(SupplyChainFinance scf) {
        log.debug("Saving SCF: id={}, reference={}", scf.getId(), scf.getScfReference());
        SCFJpaEntity entity = mapper.toEntity(scf);
        SCFJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<SupplyChainFinance> findById(UUID id, String tenantId) {
        log.debug("Finding SCF: id={}, tenant={}", id, tenantId);
        return jpaRepository.findByIdAndTenantId(id, tenantId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<SupplyChainFinance> findByReference(String scfReference, String tenantId) {
        return jpaRepository.findByScfReferenceAndTenantId(scfReference, tenantId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<SupplyChainFinance> findByInvoiceNumber(String invoiceNumber, String tenantId) {
        return jpaRepository.findByInvoiceNumber(tenantId, invoiceNumber)
            .map(mapper::toDomain);
    }

    @Override
    public List<SupplyChainFinance> findByTenantId(String tenantId) {
        return jpaRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SupplyChainFinance> findByStatus(String tenantId, SCFStatus status) {
        return jpaRepository.findByTenantIdAndStatus(tenantId, status).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SupplyChainFinance> findByType(String tenantId, SCFType type) {
        return jpaRepository.findByTenantIdAndType(tenantId, type).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SupplyChainFinance> findBySupplier(UUID supplierId, String tenantId) {
        return jpaRepository.findBySupplierIdAndTenantId(supplierId, tenantId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SupplyChainFinance> findByBuyer(UUID buyerId, String tenantId) {
        return jpaRepository.findByBuyerIdAndTenantId(buyerId, tenantId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SupplyChainFinance> findByFinancier(UUID financierId, String tenantId) {
        return jpaRepository.findByFinancierIdAndTenantId(financierId, tenantId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SupplyChainFinance> findDueSoon(String tenantId, int days) {
        LocalDate now = LocalDate.now();
        LocalDate futureDate = now.plusDays(days);
        return jpaRepository.findDueSoon(tenantId, now, futureDate).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<SupplyChainFinance> findOverdue(String tenantId) {
        return jpaRepository.findOverdue(tenantId, LocalDate.now()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        log.debug("Deleting SCF: id={}", id);
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean exists(UUID id, String tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).isPresent();
    }
}

