package com.tradesphere.ai.workflow.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User workload tracking service
 * Tracks current task assignments per user
 */
@Service
@Slf4j
public class UserWorkloadService {

    private final Map<UUID, Integer> currentWorkloads = new ConcurrentHashMap<>();
    // Organizational hierarchy: user -> supervisor
    private final Map<UUID, UUID> organizationalHierarchy = new ConcurrentHashMap<>();
    // Manager/Supervisor role tracking
    private final Set<UUID> managerRoles = ConcurrentHashMap.newKeySet();

    /**
     * Get current workloads for multiple users
     */
    public Map<UUID, Integer> getCurrentWorkloads(List<UUID> userIds) {
        Map<UUID, Integer> workloads = new HashMap<>();

        for (UUID userId : userIds) {
            workloads.put(userId, getCurrentWorkload(userId));
        }

        return workloads;
    }

    /**
     * Get current workload for a single user
     */
    public int getCurrentWorkload(UUID userId) {
        return currentWorkloads.getOrDefault(userId, 0);
    }

    /**
     * Increment user workload (when task assigned)
     */
    public void incrementWorkload(UUID userId) {
        currentWorkloads.compute(userId, (k, v) -> (v == null ? 1 : v + 1));
        log.debug("Incremented workload for user: {} -> {}", userId, currentWorkloads.get(userId));
    }

    /**
     * Decrement user workload (when task completed)
     */
    public void decrementWorkload(UUID userId) {
        currentWorkloads.compute(userId, (k, v) -> (v == null || v <= 0) ? 0 : v - 1);
        log.debug("Decremented workload for user: {} -> {}", userId, currentWorkloads.get(userId));
    }

    /**
     * Find user with lowest workload
     */
    public UUID findUserWithLowestWorkload(List<UUID> userIds) {
        return userIds.stream()
            .min(Comparator.comparingInt(this::getCurrentWorkload))
            .orElse(null);
    }

    /**
     * Check if user is overloaded
     */
    public boolean isOverloaded(UUID userId, int threshold) {
        return getCurrentWorkload(userId) > threshold;
    }

    /**
     * Get users below workload threshold
     */
    public List<UUID> getUsersBelowThreshold(List<UUID> userIds, int threshold) {
        return userIds.stream()
            .filter(userId -> getCurrentWorkload(userId) < threshold)
            .toList();
    }

    /**
     * Reset workload for user (admin function)
     */
    public void resetWorkload(UUID userId) {
        currentWorkloads.remove(userId);
        log.info("Reset workload for user: {}", userId);
    }

    /**
     * Get workload statistics
     */
    public WorkloadStatistics getStatistics() {
        if (currentWorkloads.isEmpty()) {
            return new WorkloadStatistics(0, 0.0, 0, 0);
        }

        Collection<Integer> workloads = currentWorkloads.values();
        int total = workloads.stream().mapToInt(Integer::intValue).sum();
        double average = total / (double) currentWorkloads.size();
        int max = Collections.max(workloads);
        int min = Collections.min(workloads);

        return new WorkloadStatistics(total, average, max, min);
    }

    public record WorkloadStatistics(
        int total,
        double average,
        int max,
        int min
    ) {}

    /**
     * Get supervisor for a user
     * Returns the user's direct supervisor from organizational hierarchy
     */
    public UUID getSupervisor(UUID userId) {
        UUID supervisor = organizationalHierarchy.get(userId);
        log.debug("Supervisor for user {}: {}", userId, supervisor);
        return supervisor;
    }

    /**
     * Set supervisor for a user
     * Used to maintain organizational hierarchy
     */
    public void setSupervisor(UUID userId, UUID supervisorId) {
        if (userId.equals(supervisorId)) {
            log.warn("Cannot set user as their own supervisor: {}", userId);
            return;
        }
        organizationalHierarchy.put(userId, supervisorId);
        // Supervisors are also managers
        if (supervisorId != null) {
            managerRoles.add(supervisorId);
        }
        log.info("Set supervisor for user {}: {}", userId, supervisorId);
    }

    /**
     * Mark user as a manager/supervisor
     */
    public void addManager(UUID userId) {
        managerRoles.add(userId);
        log.info("Added manager role for user: {}", userId);
    }

    /**
     * Remove manager role from user
     */
    public void removeManager(UUID userId) {
        managerRoles.remove(userId);
        log.info("Removed manager role for user: {}", userId);
    }

    /**
     * Get all available managers (for escalation fallback)
     */
    public List<UUID> getAvailableManagers() {
        return new ArrayList<>(managerRoles);
    }

    /**
     * Check if user is a manager
     */
    public boolean isManager(UUID userId) {
        return managerRoles.contains(userId);
    }

    /**
     * Get all subordinates for a manager
     */
    public List<UUID> getSubordinates(UUID managerId) {
        return organizationalHierarchy.entrySet().stream()
            .filter(entry -> managerId.equals(entry.getValue()))
            .map(Map.Entry::getKey)
            .toList();
    }

    /**
     * Clear organizational hierarchy (admin function)
     */
    public void clearHierarchy() {
        organizationalHierarchy.clear();
        managerRoles.clear();
        log.info("Cleared organizational hierarchy");
    }
}

