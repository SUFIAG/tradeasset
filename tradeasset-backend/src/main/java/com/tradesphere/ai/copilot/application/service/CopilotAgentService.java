package com.tradesphere.ai.copilot.application.service;

import com.tradesphere.ai.copilot.domain.model.*;
import com.tradesphere.ai.knowledge.application.service.KnowledgeAgentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Copilot Agent - NLP Interface for Trade Finance
 * Provides conversational AI interface for users to interact with the platform
 * 
 * Capabilities:
 * - Natural language queries about transactions
 * - Intent recognition and command execution
 * - Contextual assistance and guidance
 * - Multi-turn conversations with memory
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CopilotAgentService {

    private final KnowledgeAgentService knowledgeAgent;
    private final IntentRecognitionService intentRecognition;
    private final ConversationMemoryService conversationMemory;
    
    /**
     * Process user message and generate response
     */
    public CopilotResponse chat(String message, UUID userId, UUID tenantId, String sessionId) {
        log.info("Copilot processing message from user: {}", userId);
        
        try {
            // Get conversation context
            ConversationContext context = conversationMemory.getContext(sessionId);
            
            // Recognize user intent
            RecognizedIntent intent = intentRecognition.recognizeIntent(message, context);
            
            // Process based on intent
            CopilotResponse response = switch (intent.type()) {
                case QUERY -> handleQuery(message, intent, tenantId, context);
                case CREATE -> handleCreate(intent, userId, tenantId, context);
                case UPDATE -> handleUpdate(intent, userId, tenantId, context);
                case SEARCH -> handleSearch(intent, tenantId, context);
                case HELP -> handleHelp(message, tenantId);
                case GREETING -> handleGreeting();
                default -> handleUnknown(message, tenantId);
            };
            
            // Update conversation memory
            conversationMemory.addMessage(sessionId, message, response.getMessage());

            return response;
            
        } catch (Exception e) {
            log.error("Copilot error processing message", e);
            return CopilotResponse.error("I encountered an error processing your request. Please try again.");
        }
    }
    
    /**
     * Handle informational queries
     */
    private CopilotResponse handleQuery(String message, RecognizedIntent intent, 
                                       UUID tenantId, ConversationContext context) {
        log.debug("Handling query intent: {}", message);
        
        // Use knowledge agent for informational queries
        var knowledge = knowledgeAgent.answerQuestion(message, tenantId, context.toString());
        
        return CopilotResponse.builder()
            .message(knowledge.answer())
            .intent(IntentType.QUERY)
            .confidence(knowledge.confidence())
            .suggestions(generateSuggestions(intent))
            .citations(knowledge.citations())
            .build();
    }
    
    /**
     * Handle create operations (new LC, customer, etc.)
     */
    private CopilotResponse handleCreate(RecognizedIntent intent, UUID userId,
                                        UUID tenantId, ConversationContext context) {
        log.debug("Handling create intent: {}", intent.entity());
        
        String entityType = intent.entity();
        Map<String, Object> params = intent.parameters();
        
        String message = switch (entityType) {
            case "letter_of_credit", "lc" -> 
                "I can help you create a Letter of Credit. I'll need some information:\n" +
                "- Applicant (buyer)\n" +
                "- Beneficiary (seller)\n" +
                "- Amount and currency\n" +
                "- Expiry date\n" +
                "- Goods description\n\n" +
                "Would you like to proceed with creating an LC?";
            case "customer" -> 
                "I'll help you onboard a new customer. Please provide:\n" +
                "- Customer name\n" +
                "- Business type\n" +
                "- Country\n" +
                "- Contact information";
            case "bank_guarantee" ->
                "I can assist with creating a bank guarantee. What type do you need?\n" +
                "- Bid Bond\n" +
                "- Performance Guarantee\n" +
                "- Advance Payment Guarantee\n" +
                "- Other";
            default -> 
                "I can help create a new " + entityType + ". What details would you like to provide?";
        };
        
        return CopilotResponse.builder()
            .message(message)
            .intent(IntentType.CREATE)
            .entity(entityType)
            .requiresInput(true)
            .suggestions(Arrays.asList("Yes, proceed", "Show example", "Cancel"))
            .build();
    }
    
    /**
     * Handle update operations
     */
    private CopilotResponse handleUpdate(RecognizedIntent intent, UUID userId, 
                                        UUID tenantId, ConversationContext context) {
        log.debug("Handling update intent: {}", intent.entity());
        
        return CopilotResponse.builder()
            .message("I can help you update " + intent.entity() + ". What would you like to change?")
            .intent(IntentType.UPDATE)
            .entity(intent.entity())
            .suggestions(Arrays.asList("Status", "Amount", "Dates", "Documents"))
            .build();
    }
    
    /**
     * Handle search operations
     */
    private CopilotResponse handleSearch(RecognizedIntent intent, UUID tenantId, 
                                        ConversationContext context) {
        log.debug("Handling search intent");
        
        String query = intent.parameters().getOrDefault("query", "").toString();
        String entityType = intent.entity();
        
        String message = String.format(
            "Searching for %s matching '%s'...\n\n" +
            "Found recent items:\n" +
            "1. [Item 1 details]\n" +
            "2. [Item 2 details]\n" +
            "3. [Item 3 details]\n\n" +
            "Would you like to see more details about any of these?",
            entityType, query
        );
        
        return CopilotResponse.builder()
            .message(message)
            .intent(IntentType.SEARCH)
            .entity(entityType)
            .suggestions(Arrays.asList("Show item 1", "Refine search", "Export results"))
            .build();
    }
    
    /**
     * Handle help requests
     */
    private CopilotResponse handleHelp(String message, UUID tenantId) {
        String helpMessage = """
            I'm your Trade Finance AI Assistant. I can help you with:
            
            📋 Information & Queries
            - Ask about trade finance processes
            - Get regulatory information
            - Learn about best practices
            
            ✅ Operations
            - Create Letters of Credit
            - Process Bank Guarantees
            - Manage Documentary Collections
            - Handle Supply Chain Finance
            
            🔍 Search & Find
            - Find transactions
            - Search documents
            - Check statuses
            
            💡 Suggestions
            - Workflow optimization
            - Risk analysis
            - Compliance checks
            
            Just ask me anything in natural language!
            """;
        
        return CopilotResponse.builder()
            .message(helpMessage)
            .intent(IntentType.HELP)
            .suggestions(Arrays.asList(
                "Create new LC",
                "Check LC status",
                "What is a performance guarantee?",
                "Show my pending tasks"
            ))
            .build();
    }
    
    /**
     * Handle greetings
     */
    private CopilotResponse handleGreeting() {
        String[] greetings = {
            "Hello! I'm your Trade Finance AI Assistant. How can I help you today?",
            "Hi there! Ready to assist with your trade finance needs.",
            "Welcome! What would you like to work on today?"
        };
        
        String greeting = greetings[new Random().nextInt(greetings.length)];
        
        return CopilotResponse.builder()
            .message(greeting)
            .intent(IntentType.GREETING)
            .suggestions(Arrays.asList(
                "Create a new LC",
                "Check transaction status",
                "Help with documentation",
                "Show analytics"
            ))
            .build();
    }
    
    /**
     * Handle unknown/unclear intents
     */
    private CopilotResponse handleUnknown(String message, UUID tenantId) {
        return CopilotResponse.builder()
            .message("I'm not sure I understood that. Could you rephrase or ask for help?")
            .intent(IntentType.UNKNOWN)
            .suggestions(Arrays.asList("Help", "Show examples", "Start over"))
            .build();
    }
    
    /**
     * Generate contextual suggestions
     */
    private List<String> generateSuggestions(RecognizedIntent intent) {
        return Arrays.asList(
            "Tell me more",
            "Show related topics",
            "Create new transaction",
            "Search documents"
        );
    }
    
    public record RecognizedIntent(
        Intent type,
        String entity,
        Map<String, Object> parameters,
        float confidence
    ) {}
}

