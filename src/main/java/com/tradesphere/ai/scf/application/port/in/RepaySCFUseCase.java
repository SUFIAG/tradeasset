package com.tradesphere.ai.scf.application.port.in;

import com.tradesphere.ai.scf.application.port.in.dto.RepaySCFCommand;
import com.tradesphere.ai.scf.application.port.in.dto.SCFResponse;

/**
 * Use case for repaying SCF financing
 */
public interface RepaySCFUseCase {
    SCFResponse repaySCF(RepaySCFCommand command);
}

