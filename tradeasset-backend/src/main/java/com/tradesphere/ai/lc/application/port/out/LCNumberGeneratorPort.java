package com.tradesphere.ai.lc.application.port.out;

import com.tradesphere.ai.lc.domain.model.LCNumber;

/**
 * Port for generating LC numbers
 */
public interface LCNumberGeneratorPort {

    /**
     * Generate next LC number for the current year
     */
    LCNumber generateNextLCNumber();
}

