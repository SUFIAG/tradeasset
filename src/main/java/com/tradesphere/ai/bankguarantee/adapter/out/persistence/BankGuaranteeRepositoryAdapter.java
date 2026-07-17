package com.tradesphere.ai.bankguarantee.adapter.out.persistence;

import com.tradesphere.ai.bankguarantee.application.port.out.BankGuaranteeRepository;
import com.tradesphere.ai.bankguarantee.domain.model.BankGuarantee;
import com.tradesphere.ai.bankguarantee.domain.model.BGStatus;
import com.tradesphere.ai.bankguarantee.domain.model.BGType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * JPA Adapter for Bank Guarantee Repository
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BankGuaranteeRepositoryAdapter implements BankGuaranteeRepository {

    private final BankGuaranteeJpaRepository jpaRepository;
    private final BankGuaranteeMapper mapper;

    @Override
    public BankGuarantee save(BankGuarantee bankGuarantee) {
        log.debug("Saving bank guarantee: id={}", bankGuarantee.getId());
        BankGuaranteeJpaEntity entity = mapper.toEntity(bankGuarantee);
        BankGuaranteeJpaEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<BankGuarantee> findById(UUID id, String tenantId) {
        log.debug("Finding bank guarantee: id={}, tenant={}", id, tenantId);
        return jpaRepository.findByIdAndTenantId(id, tenantId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<BankGuarantee> findByBgNumber(String bgNumber, String tenantId) {
        return jpaRepository.findByBgNumberAndTenantId(bgNumber, tenantId)
            .map(mapper::toDomain);
    }

    @Override
    public Optional<BankGuarantee> findByReferenceNumber(String referenceNumber, String tenantId) {
        return jpaRepository.findByReferenceNumberAndTenantId(referenceNumber, tenantId)
            .map(mapper::toDomain);
    }

    @Override
    public List<BankGuarantee> findByTenantId(String tenantId) {
        return jpaRepository.findByTenantIdOrderByCreatedAtDesc(tenantId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<BankGuarantee> findByStatus(String tenantId, BGStatus status) {
        return jpaRepository.findByTenantIdAndStatus(tenantId, status).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<BankGuarantee> findByType(String tenantId, BGType bgType) {
        return jpaRepository.findByTenantIdAndBgType(tenantId, bgType).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<BankGuarantee> findByApplicant(UUID applicantId, String tenantId) {
        return jpaRepository.findByApplicantIdAndTenantId(applicantId, tenantId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<BankGuarantee> findByBeneficiary(UUID beneficiaryId, String tenantId) {
        return jpaRepository.findByBeneficiaryIdAndTenantId(beneficiaryId, tenantId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<BankGuarantee> findExpiringSoon(String tenantId, int days) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime futureDate = now.plusDays(days);
        return jpaRepository.findExpiringGuarantees(tenantId, now, futureDate).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public List<BankGuarantee> findExpired(String tenantId) {
        return jpaRepository.findExpiredGuarantees(tenantId, LocalDateTime.now()).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID id) {
        log.debug("Deleting bank guarantee: id={}", id);
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean exists(UUID id, String tenantId) {
        return jpaRepository.findByIdAndTenantId(id, tenantId).isPresent();
    }
}

