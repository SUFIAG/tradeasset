package com.tradesphere.ai.lc.application.port.out;

import com.tradesphere.ai.lc.domain.model.LCId;
import com.tradesphere.ai.lc.domain.model.LetterOfCredit;

import java.util.Optional;

/**
 * Port for loading LC from persistence
 */
public interface LoadLCPort {
    
    Optional<LetterOfCredit> loadLCById(LCId lcId);
    
    Optional<LetterOfCredit> loadLCByNumber(String lcNumber);
}

