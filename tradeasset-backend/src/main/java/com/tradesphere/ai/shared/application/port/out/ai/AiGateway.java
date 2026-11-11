package com.tradesphere.ai.shared.application.port.out.ai;

import java.util.UUID;

/**
 * The single exit point for model calls (REQ-AI-001..007). Implemented by the knowledge module.
 *
 * <p>The implementation masks personal identifiers before text leaves the platform (REQ-AI-004),
 * logs every call with model, prompt version, tokens, latency and cost (REQ-AI-005), and never
 * throws on a provider failure or timeout: it returns {@code success=false} and the caller takes
 * the manual path (REQ-AI-006). Callers validate {@link Completion#text()} against their own
 * schema before use and treat malformed output as a failure (REQ-AI-007).</p>
 *
 * <p>AI drafts, extracts, scores, classifies and recommends. It never approves a payment, grants
 * credit, clears a sanctions hit or changes a role (REQ-AI-001).</p>
 */
public interface AiGateway {

    /** False when no provider is configured; callers go straight to the manual path. */
    boolean isAvailable();

    Completion complete(Prompt prompt);

    /** Stores the user's accept, edit or reject decision for an earlier call (REQ-AI-005). */
    void recordFeedback(UUID tenantId, UUID callId, UUID userId, String decision, String comment);

    /**
     * @param purpose       stable code such as DOC_CLASSIFY, DOC_EXTRACT, KNOWLEDGE_ANSWER,
     *                      COPILOT_CHAT, HIT_TRIAGE, CASE_NARRATIVE, RISK_ADVISORY, TASK_ROUTING
     * @param promptVersion version of the prompt template, for example "doc-extract/v3"
     * @param expectJson    true when the caller will parse the text as JSON
     */
    record Prompt(UUID tenantId, UUID userId, String purpose, String promptVersion, String systemText,
                  String userText, String entityType, UUID entityId, boolean expectJson, int maxOutputTokens) {
    }

    /**
     * @param callId        ID of the logged call, used for feedback
     * @param failureReason stable code when {@code success} is false: AI_DISABLED, AI_TIMEOUT,
     *                      AI_PROVIDER_ERROR
     */
    record Completion(boolean success, String text, String modelId, String promptVersion, int inputTokens,
                      int outputTokens, long latencyMs, UUID callId, String failureReason) {

        public static Completion failed(String promptVersion, UUID callId, String reason, long latencyMs) {
            return new Completion(false, null, null, promptVersion, 0, 0, latencyMs, callId, reason);
        }
    }
}
