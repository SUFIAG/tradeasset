package com.tradesphere.ai.scf.application.port.in;

import com.tradesphere.ai.scf.application.port.in.dto.CreateSCFCommand;
import com.tradesphere.ai.scf.application.port.in.dto.SCFResponse;

/**
 * Use case for creating SCF transaction
 */
public interface CreateSCFUseCase {
    SCFResponse createSCF(CreateSCFCommand command);
}

