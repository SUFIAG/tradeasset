package com.tradesphere.ai.copilot.application.service;

import com.tradesphere.ai.copilot.domain.model.ConversationContext;
import com.tradesphere.ai.copilot.domain.model.Intent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

import static com.tradesphere.ai.copilot.application.service.CopilotAgentService.RecognizedIntent;

/**
 * Intent Recognition Service
 * Analyzes user input to determine intent and extract entities
 */
@Service
@Slf4j
public class IntentRecognitionService {

    // Intent patterns
    private static final Map<Pattern, Intent> INTENT_PATTERNS = new HashMap<>();

    static {
        // Query patterns
        INTENT_PATTERNS.put(Pattern.compile("(?i).*(what|how|why|when|where|explain|tell me about).*"),
            Intent.QUERY);

        // Create patterns
        INTENT_PATTERNS.put(Pattern.compile("(?i).*(create|new|add|open|issue|initiate).*"),
            Intent.CREATE);

        // Update patterns
        INTENT_PATTERNS.put(Pattern.compile("(?i).*(update|change|modify|edit|amend).*"),
            Intent.UPDATE);

        // Search patterns
        INTENT_PATTERNS.put(Pattern.compile("(?i).*(search|find|show|list|get|display).*"),
            Intent.SEARCH);

        // Help patterns
        INTENT_PATTERNS.put(Pattern.compile("(?i).*(help|assist|guide|support).*"),
            Intent.HELP);

        // Greeting patterns
        INTENT_PATTERNS.put(Pattern.compile("(?i).*(hello|hi|hey|good morning|good afternoon).*"),
            Intent.GREETING);
    }

    // Entity patterns
    private static final Map<String, Pattern> ENTITY_PATTERNS = new HashMap<>();

    static {
        ENTITY_PATTERNS.put("letter_of_credit", Pattern.compile("(?i).*(letter of credit|LC|l/c).*"));
        ENTITY_PATTERNS.put("bank_guarantee", Pattern.compile("(?i).*(bank guarantee|BG|guarantee).*"));
        ENTITY_PATTERNS.put("customer", Pattern.compile("(?i).*(customer|client|buyer|seller).*"));
        ENTITY_PATTERNS.put("document", Pattern.compile("(?i).*(document|file|invoice|bill of lading).*"));
        ENTITY_PATTERNS.put("collection", Pattern.compile("(?i).*(collection|D/P|D/A|documentary collection).*"));
        ENTITY_PATTERNS.put("scf", Pattern.compile("(?i).*(supply chain finance|financing|factoring).*"));
    }

    /**
     * Recognize intent from user message
     */
    public RecognizedIntent recognizeIntent(String message, ConversationContext context) {
        log.debug("Recognizing intent for message: {}", message);

        // Determine intent type
        Intent intentType = determineIntentType(message);

        // Extract entity
        String entity = extractEntity(message);

        // Extract parameters
        Map<String, Object> parameters = extractParameters(message);

        // Calculate confidence
        float confidence = calculateConfidence(message, intentType, entity);

        return new RecognizedIntent(
            intentType,
            entity,
            parameters,
            confidence
        );
    }

    private Intent determineIntentType(String message) {
        for (Map.Entry<Pattern, Intent> entry : INTENT_PATTERNS.entrySet()) {
            if (entry.getKey().matcher(message).matches()) {
                return entry.getValue();
            }
        }
        return Intent.UNKNOWN;
    }

    private String extractEntity(String message) {
        for (Map.Entry<String, Pattern> entry : ENTITY_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(message).matches()) {
                return entry.getKey();
            }
        }
        return "general";
    }

    private Map<String, Object> extractParameters(String message) {
        Map<String, Object> parameters = new HashMap<>();

        // Extract simple parameters (extend as needed)
        if (message.matches(".*\\d+.*")) {
            // Contains numbers - might be amount, ID, etc.
            parameters.put("hasNumbers", true);
        }

        parameters.put("query", message);

        return parameters;
    }

    private float calculateConfidence(String message, Intent intent, String entity) {
        // Simple confidence calculation
        if (intent == Intent.UNKNOWN) {
            return 0.3f;
        }
        if (entity.equals("general")) {
            return 0.6f;
        }
        return 0.85f;
    }
}

