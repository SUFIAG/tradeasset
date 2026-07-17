package com.tradesphere.ai.scf.application.port.in;

import com.tradesphere.ai.scf.application.port.in.dto.DisburseSCFCommand;
import com.tradesphere.ai.scf.application.port.in.dto.SCFResponse;

/**
 * Use case for disbursing SCF funds
 */
public interface DisburseSCFUseCase {
    SCFResponse disburseSCF(DisburseSCFCommand command);
}

