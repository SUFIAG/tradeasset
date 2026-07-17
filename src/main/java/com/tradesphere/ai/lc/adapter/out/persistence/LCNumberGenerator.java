package com.tradesphere.ai.lc.adapter.out.persistence;

import com.tradesphere.ai.lc.application.port.out.LCNumberGeneratorPort;
import com.tradesphere.ai.lc.domain.model.LCNumber;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

/**
 * LC Number Generator Service
 * Generates unique LC numbers in format: LC-YYYY-NNNNNN
 */
@Component
@RequiredArgsConstructor
public class LCNumberGenerator implements LCNumberGeneratorPort {
    
    private final LCJpaRepository lcRepository;
    
    @Override
    @Transactional
    public synchronized LCNumber generateNextLCNumber() {
        int currentYear = Year.now().getValue();
        String yearStr = String.valueOf(currentYear);
        
        // Get the max sequence for the current year
        String maxSequence = lcRepository.findMaxSequenceForYear(yearStr);
        
        long nextSequence = 1;
        if (maxSequence != null) {
            try {
                nextSequence = Long.parseLong(maxSequence) + 1;
            } catch (NumberFormatException e) {
                // If parsing fails, start from 1
                nextSequence = 1;
            }
        }
        
        return LCNumber.generate(currentYear, nextSequence);
    }
}

