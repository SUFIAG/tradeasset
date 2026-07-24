package com.tradesphere.ai.lc.application.port.in;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Use case for listing and searching LCs
 */
public interface ListLCsUseCase {

    /**
     * List all LCs with pagination
     */
    Page<LetterOfCredit> listLCs(Pageable pageable);

    /**
     * List LCs by applicant (customer)
     */
    Page<LetterOfCredit> listLCsByApplicant(String applicantId, Pageable pageable);

    /**
     * List LCs by status
     */
    Page<LetterOfCredit> listLCsByStatus(String status, Pageable pageable);
}

