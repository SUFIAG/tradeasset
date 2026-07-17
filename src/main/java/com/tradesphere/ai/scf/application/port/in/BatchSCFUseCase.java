package com.tradesphere.ai.scf.application.port.in;

import com.tradesphere.ai.scf.application.port.in.dto.BatchSCFCommand;
import com.tradesphere.ai.scf.application.port.in.dto.BatchSCFResponse;

/**
 * Use case for batch SCF operations
 */
public interface BatchSCFUseCase {
    BatchSCFResponse processBatch(BatchSCFCommand command);
}

