package com.tradesphere.ai.lc.application.port.out;

import com.tradesphere.ai.lc.domain.model.LCStatus;
import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Port for listing and searching LCs
 */
public interface ListLCsPort {

    Page<LetterOfCredit> findAll(Pageable pageable);

    Page<LetterOfCredit> findByApplicantId(UUID applicantId, Pageable pageable);

    Page<LetterOfCredit> findByStatus(LCStatus status, Pageable pageable);
}

