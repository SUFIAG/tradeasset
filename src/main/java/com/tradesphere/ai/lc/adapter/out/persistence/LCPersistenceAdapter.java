package com.tradesphere.ai.lc.adapter.out.persistence;

import com.tradesphere.ai.lc.application.port.out.ListLCsPort;
import com.tradesphere.ai.lc.application.port.out.LoadLCPort;
import com.tradesphere.ai.lc.application.port.out.SaveLCPort;
import com.tradesphere.ai.lc.domain.model.LCId;
import com.tradesphere.ai.lc.domain.model.LCStatus;
import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Persistence Adapter for Letter of Credit
 * Implements outbound ports using Spring Data JPA
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class LCPersistenceAdapter implements LoadLCPort, SaveLCPort, ListLCsPort {

    private final LCJpaRepository lcRepository;
    private final LCMapper lcMapper;

    @Override
    public Optional<LetterOfCredit> loadLCById(LCId lcId) {
        log.debug("Loading LC by ID: {}", lcId.asString());
        return lcRepository.findById(lcId.value())
                .map(lcMapper::toDomainModel);
    }

    @Override
    public Optional<LetterOfCredit> loadLCByNumber(String lcNumber) {
        log.debug("Loading LC by number: {}", lcNumber);
        return lcRepository.findByLcNumber(lcNumber)
                .map(lcMapper::toDomainModel);
    }

    @Override
    public LetterOfCredit saveLC(LetterOfCredit lc) {
        log.debug("Saving LC: {}", lc.getId().asString());
        LCJpaEntity entity = lcMapper.toJpaEntity(lc);
        LCJpaEntity savedEntity = lcRepository.save(entity);
        return lcMapper.toDomainModel(savedEntity);
    }

    @Override
    public Page<LetterOfCredit> findAll(Pageable pageable) {
        log.debug("Finding all LCs with pagination");
        return lcRepository.findAll(pageable)
                .map(lcMapper::toDomainModel);
    }

    @Override
    public Page<LetterOfCredit> findByApplicantId(UUID applicantId, Pageable pageable) {
        log.debug("Finding LCs by applicant: {}", applicantId);
        return lcRepository.findByApplicantId(applicantId, pageable)
                .map(lcMapper::toDomainModel);
    }

    @Override
    public Page<LetterOfCredit> findByStatus(LCStatus status, Pageable pageable) {
        log.debug("Finding LCs by status: {}", status);
        return lcRepository.findByStatus(status, pageable)
                .map(lcMapper::toDomainModel);
    }
}

