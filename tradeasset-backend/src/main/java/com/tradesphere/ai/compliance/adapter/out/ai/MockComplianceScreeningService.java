package com.tradesphere.ai.compliance.adapter.out.ai;

import com.tradesphere.ai.compliance.application.port.out.ComplianceScreeningPort;
import com.tradesphere.ai.compliance.domain.model.ScreeningHit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Mock implementation of compliance screening service
 * In production, this would integrate with:
 * - OFAC (Office of Foreign Assets Control)
 * - UN Sanctions Lists
 * - EU Sanctions Lists
 * - World-Check (Refinitiv)
 * - Dow Jones Risk & Compliance
 * - LexisNexis Bridger
 * - OpenAI/Claude for AI analysis
 */
@Service
@Slf4j
public class MockComplianceScreeningService implements ComplianceScreeningPort {

    private final Random random = new Random();

    @Override
    public List<ScreeningHit> screenSanctions(String name, String country, String dateOfBirth) {
        log.info("Screening sanctions: name={}, country={}", name, country);

        List<ScreeningHit> hits = new ArrayList<>();

        // Mock: Simulate some hits based on name patterns
        if (name != null && (name.toLowerCase().contains("sanctioned") ||
                             name.toLowerCase().contains("blocked"))) {
            hits.add(ScreeningHit.builder()
                    .hitType("SANCTIONS")
                    .matchedName(name + " (Sanctioned Entity)")
                    .matchScore(85 + random.nextInt(10))
                    .sourceList("OFAC SDN List")
                    .listProvider("U.S. Treasury")
                    .countryCodes(country)
                    .additionalInfo("Possible match on OFAC list")
                    .aiConfidence(0.7 + random.nextDouble() * 0.2)
                    .assessedAsFalsePositive(false)
                    .build());
        }

        log.info("Sanctions screening completed: {} hits found", hits.size());
        return hits;
    }

    @Override
    public List<ScreeningHit> screenPEP(String name, String country, String position) {
        log.info("Screening PEP: name={}, country={}, position={}", name, country, position);

        List<ScreeningHit> hits = new ArrayList<>();

        // Mock: Simulate PEP hits for certain positions
        if (position != null && (position.toLowerCase().contains("minister") ||
                                position.toLowerCase().contains("president") ||
                                position.toLowerCase().contains("director"))) {
            hits.add(ScreeningHit.builder()
                    .hitType("PEP")
                    .matchedName(name)
                    .matchScore(90 + random.nextInt(10))
                    .sourceList("World-Check PEP Database")
                    .listProvider("Refinitiv")
                    .countryCodes(country)
                    .additionalInfo("Politically Exposed Person - " + position)
                    .aiConfidence(0.8 + random.nextDouble() * 0.15)
                    .assessedAsFalsePositive(false)
                    .build());
        }

        log.info("PEP screening completed: {} hits found", hits.size());
        return hits;
    }

    @Override
    public List<ScreeningHit> screenAML(String name, String country, String transactionDetails) {
        log.info("Screening AML: name={}, country={}", name, country);

        List<ScreeningHit> hits = new ArrayList<>();

        // Mock: Simulate AML hits for high-risk countries
        if (country != null && isHighRiskCountry(country)) {
            hits.add(ScreeningHit.builder()
                    .hitType("AML")
                    .matchedName(name)
                    .matchScore(75 + random.nextInt(15))
                    .sourceList("High-Risk Jurisdiction List")
                    .listProvider("FATF")
                    .countryCodes(country)
                    .additionalInfo("Entity from high-risk AML jurisdiction")
                    .aiConfidence(0.6 + random.nextDouble() * 0.3)
                    .assessedAsFalsePositive(false)
                    .build());
        }

        log.info("AML screening completed: {} hits found", hits.size());
        return hits;
    }

    @Override
    public ScreeningAnalysisResult analyzeWithAI(
            String entityName,
            List<ScreeningHit> sanctionsHits,
            List<ScreeningHit> pepHits,
            List<ScreeningHit> amlHits) {

        log.info("Analyzing screening results with AI: entityName={}, totalHits={}",
                entityName, sanctionsHits.size() + pepHits.size() + amlHits.size());

        int totalHits = sanctionsHits.size() + pepHits.size() + amlHits.size();

        if (totalHits == 0) {
            return new ScreeningAnalysisResult(
                    1.0,
                    0,
                    "No hits found. Entity appears clean.",
                    List.of(),
                    List.of()
            );
        }

        // Mock AI analysis
        List<ScreeningHit> confirmedHits = new ArrayList<>();
        List<ScreeningHit> likelyFalsePositives = new ArrayList<>();

        // Analyze sanctions hits
        for (ScreeningHit hit : sanctionsHits) {
            if (hit.getMatchScore() >= 90) {
                confirmedHits.add(hit);
            } else {
                likelyFalsePositives.add(hit);
            }
        }

        // Analyze PEP hits
        for (ScreeningHit hit : pepHits) {
            if (hit.getMatchScore() >= 85) {
                confirmedHits.add(hit);
            } else {
                likelyFalsePositives.add(hit);
            }
        }

        // Analyze AML hits
        amlHits.forEach(likelyFalsePositives::add); // Most AML hits are informational

        double aiConfidence = confirmedHits.isEmpty() ? 0.9 : 0.7;
        int falsePositiveProbability = confirmedHits.isEmpty() ? 80 : 20;

        String reasoning = String.format(
                "AI Analysis: Found %d total hits. %d confirmed high-confidence hits, " +
                "%d likely false positives. Confidence: %.2f",
                totalHits, confirmedHits.size(), likelyFalsePositives.size(), aiConfidence
        );

        log.info("AI analysis completed: confirmedHits={}, falsePositives={}, confidence={}",
                confirmedHits.size(), likelyFalsePositives.size(), aiConfidence);

        return new ScreeningAnalysisResult(
                aiConfidence,
                falsePositiveProbability,
                reasoning,
                confirmedHits,
                likelyFalsePositives
        );
    }

    private boolean isHighRiskCountry(String country) {
        // Mock implementation
        List<String> highRiskCountries = List.of("Iran", "North Korea", "Syria", "Myanmar");
        return highRiskCountries.stream()
                .anyMatch(c -> country != null && country.toLowerCase().contains(c.toLowerCase()));
    }
}

