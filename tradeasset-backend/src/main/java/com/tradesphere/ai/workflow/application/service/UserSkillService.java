package com.tradesphere.ai.workflow.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User skill management service
 * Tracks user skills and competencies for intelligent task assignment
 */
@Service
@Slf4j
public class UserSkillService {

    private final Map<UUID, Set<String>> userSkills = new ConcurrentHashMap<>();
    private final Map<UUID, Map<String, SkillLevel>> userSkillLevels = new ConcurrentHashMap<>();

    /**
     * Get skills for multiple users
     */
    public Map<UUID, Set<String>> getUserSkills(List<UUID> userIds) {
        Map<UUID, Set<String>> skills = new HashMap<>();

        for (UUID userId : userIds) {
            skills.put(userId, getSkills(userId));
        }

        return skills;
    }

    /**
     * Get skills for a single user
     */
    public Set<String> getSkills(UUID userId) {
        return userSkills.getOrDefault(userId, Collections.emptySet());
    }

    /**
     * Add skill to user
     */
    public void addSkill(UUID userId, String skill, SkillLevel level) {
        userSkills.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(skill);
        userSkillLevels.computeIfAbsent(userId, k -> new ConcurrentHashMap<>()).put(skill, level);

        log.info("Added skill to user: {} -> {} ({})", userId, skill, level);
    }

    /**
     * Remove skill from user
     */
    public void removeSkill(UUID userId, String skill) {
        Set<String> skills = userSkills.get(userId);
        if (skills != null) {
            skills.remove(skill);
        }

        Map<String, SkillLevel> levels = userSkillLevels.get(userId);
        if (levels != null) {
            levels.remove(skill);
        }

        log.info("Removed skill from user: {} -> {}", userId, skill);
    }

    /**
     * Get skill level for user
     */
    public SkillLevel getSkillLevel(UUID userId, String skill) {
        Map<String, SkillLevel> levels = userSkillLevels.get(userId);
        return levels != null ? levels.getOrDefault(skill, SkillLevel.BASIC) : SkillLevel.BASIC;
    }

    /**
     * Find users with specific skill
     */
    public List<UUID> findUsersWithSkill(String skill) {
        return userSkills.entrySet().stream()
            .filter(entry -> entry.getValue().contains(skill))
            .map(Map.Entry::getKey)
            .toList();
    }

    /**
     * Find users with all required skills
     */
    public List<UUID> findUsersWithAllSkills(Set<String> requiredSkills) {
        return userSkills.entrySet().stream()
            .filter(entry -> entry.getValue().containsAll(requiredSkills))
            .map(Map.Entry::getKey)
            .toList();
    }

    /**
     * Find users with any of the required skills
     */
    public List<UUID> findUsersWithAnySkill(Set<String> requiredSkills) {
        return userSkills.entrySet().stream()
            .filter(entry -> entry.getValue().stream().anyMatch(requiredSkills::contains))
            .map(Map.Entry::getKey)
            .toList();
    }

    /**
     * Calculate skill match score for user
     */
    public double calculateSkillMatch(UUID userId, Set<String> requiredSkills) {
        if (requiredSkills.isEmpty()) {
            return 1.0;
        }

        Set<String> userSkills = getSkills(userId);
        if (userSkills.isEmpty()) {
            return 0.0;
        }

        long matchCount = requiredSkills.stream()
            .filter(userSkills::contains)
            .count();

        return (double) matchCount / requiredSkills.size();
    }

    /**
     * Find best match for required skills
     */
    public UUID findBestMatch(List<UUID> candidates, Set<String> requiredSkills) {
        return candidates.stream()
            .max(Comparator.comparingDouble(userId -> calculateSkillMatch(userId, requiredSkills)))
            .orElse(null);
    }

    /**
     * Initialize default skills for common roles
     */
    public void initializeDefaultSkills() {
        // This would typically be loaded from database or configuration
        log.info("Initializing default skill sets");

        // Example skills
        Set<String> tradeFinanceSkills = Set.of(
            "LETTER_OF_CREDIT",
            "DOCUMENTARY_CREDIT",
            "TRADE_FINANCE",
            "UCP_600",
            "ISBP",
            "SWIFT_MT700"
        );

        Set<String> complianceSkills = Set.of(
            "AML_KYC",
            "SANCTIONS_SCREENING",
            "REGULATORY_COMPLIANCE",
            "RISK_ASSESSMENT",
            "DUE_DILIGENCE"
        );

        Set<String> operationsSkills = Set.of(
            "DOCUMENT_VERIFICATION",
            "DATA_ENTRY",
            "CUSTOMER_SERVICE",
            "PROCESS_MANAGEMENT"
        );
    }

    /**
     * Skill proficiency levels
     */
    public enum SkillLevel {
        BASIC(1),
        INTERMEDIATE(2),
        ADVANCED(3),
        EXPERT(4);

        private final int level;

        SkillLevel(int level) {
            this.level = level;
        }

        public int getLevel() {
            return level;
        }

        public boolean isAtLeast(SkillLevel other) {
            return this.level >= other.level;
        }
    }
}

