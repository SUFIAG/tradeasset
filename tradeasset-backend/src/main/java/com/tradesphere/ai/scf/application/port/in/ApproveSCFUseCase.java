package com.tradesphere.ai.scf.application.port.in;

import com.tradesphere.ai.scf.application.port.in.dto.ApproveSCFCommand;
import com.tradesphere.ai.scf.application.port.in.dto.SCFResponse;

/**
 * Use case for approving SCF financing
 */
public interface ApproveSCFUseCase {
    SCFResponse approveSCF(ApproveSCFCommand command);
}

