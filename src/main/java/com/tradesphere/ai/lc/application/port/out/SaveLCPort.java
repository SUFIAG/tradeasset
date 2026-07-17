package com.tradesphere.ai.lc.application.port.out;

import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

/**
 * Port for saving LC to persistence
 */
public interface SaveLCPort {
    
    LetterOfCredit saveLC(LetterOfCredit lc);
}

