package com.tradesphere.ai.lc.application.service;

import com.tradesphere.ai.lc.application.port.out.ListLCsPort;
import com.tradesphere.ai.lc.application.port.out.SaveLCPort;
import com.tradesphere.ai.lc.domain.model.LCStatus;
import com.tradesphere.ai.lc.domain.model.LetterOfCredit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Scheduled job to check and expire overdue LCs
 * Runs daily at 2 AM for high-performance batch processing
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LCExpiryScheduler {

    private final ListLCsPort listLCsPort;
    private final SaveLCPort saveLCPort;

    /**
     * Check and expire overdue LCs
     * Optimized for memory efficiency with batch processing
     */
    @Scheduled(cron = "0 0 2 * * *") // Daily at 2 AM
    @Transactional
    public void checkAndExpireLCs() {
        log.info("Starting scheduled LC expiry check");

        int page = 0;
        int batchSize = 100; // Process in batches for memory efficiency
        int totalExpired = 0;

        while (true) {
            Page<LetterOfCredit> activeLCs = listLCsPort.findByStatus(
                    LCStatus.ISSUED,
                    PageRequest.of(page, batchSize));

            if (activeLCs.isEmpty()) {
                break;
            }

            for (LetterOfCredit lc : activeLCs) {
                try {
                    if (lc.isExpired()) {
                        log.info("Expiring LC: {}", lc.getLcNumber().value());
                        lc.expire();
                        saveLCPort.saveLC(lc);
                        totalExpired++;
                    }
                } catch (Exception e) {
                    log.error("Failed to expire LC: {}", lc.getId().asString(), e);
                    // Continue processing other LCs
                }
            }

            if (activeLCs.isLast()) {
                break;
            }
            page++;
        }

        log.info("LC expiry check completed. Total expired: {}", totalExpired);
    }
}

