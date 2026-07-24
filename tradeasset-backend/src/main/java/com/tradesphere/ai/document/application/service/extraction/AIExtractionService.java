package com.tradesphere.ai.document.application.service.extraction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradesphere.ai.document.config.ExtractionConfiguration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AI Service for document extraction using OpenAI, Anthropic Claude, or Azure OpenAI
 * Provides integration with various LLM providers
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AIExtractionService {

    private final ExtractionConfiguration config;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Extract structured data using AI
     */
    public Map<String, Object> extractWithAI(String prompt) {
        if (!config.getAi().isEnabled()) {
            log.warn("AI extraction is disabled, using fallback");
            return new HashMap<>();
        }

        String provider = config.getAi().getProvider();

        try {
            return switch (provider.toLowerCase()) {
                case "openai" -> extractWithOpenAI(prompt);
                case "anthropic" -> extractWithClaude(prompt);
                case "azure" -> extractWithAzureOpenAI(prompt);
                default -> {
                    log.warn("Unknown AI provider: {}, using OpenAI", provider);
                    yield extractWithOpenAI(prompt);
                }
            };
        } catch (Exception e) {
            log.error("AI extraction failed with provider: {}", provider, e);
            throw new RuntimeException("AI extraction failed", e);
        }
    }

    /**
     * Extract using OpenAI GPT-4
     */
    private Map<String, Object> extractWithOpenAI(String prompt) {
        log.info("Extracting with OpenAI GPT-4");

        String apiKey = config.getAi().getApiKey();
        if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("${")) {
            log.warn("OpenAI API key not configured, returning empty result");
            return new HashMap<>();
        }

        try {
            // Prepare request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getAi().getModel());
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a document extraction assistant. Extract information and respond with valid JSON only."),
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", config.getAi().getTemperature());
            requestBody.put("max_tokens", config.getAi().getMaxTokens());
            requestBody.put("response_format", Map.of("type", "json_object"));

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Make API call
            String url = config.getAi().getApiUrl() + "/chat/completions";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // Extract JSON from response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");

                    // Parse JSON response
                    return objectMapper.readValue(content, Map.class);
                }
            }

            log.warn("OpenAI API returned unexpected response");
            return new HashMap<>();

        } catch (Exception e) {
            log.error("OpenAI extraction failed", e);
            return new HashMap<>();
        }
    }

    /**
     * Extract using Anthropic Claude
     */
    private Map<String, Object> extractWithClaude(String prompt) {
        log.info("Extracting with Anthropic Claude");

        String apiKey = config.getAi().getApiKey();
        if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("${")) {
            log.warn("Anthropic API key not configured, returning empty result");
            return new HashMap<>();
        }

        try {
            // Prepare request for Claude API
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", config.getAi().getModel());
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("max_tokens", config.getAi().getMaxTokens());
            requestBody.put("temperature", config.getAi().getTemperature());

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("x-api-key", apiKey);
            headers.set("anthropic-version", "2023-06-01");

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Make API call
            String url = "https://api.anthropic.com/v1/messages";
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // Extract JSON from response
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> content = (List<Map<String, Object>>) responseBody.get("content");

                if (content != null && !content.isEmpty()) {
                    String text = (String) content.get(0).get("text");

                    // Parse JSON response
                    return objectMapper.readValue(text, Map.class);
                }
            }

            log.warn("Claude API returned unexpected response");
            return new HashMap<>();

        } catch (Exception e) {
            log.error("Claude extraction failed", e);
            return new HashMap<>();
        }
    }

    /**
     * Extract using Azure OpenAI
     */
    private Map<String, Object> extractWithAzureOpenAI(String prompt) {
        log.info("Extracting with Azure OpenAI");

        String apiKey = config.getAi().getApiKey();
        if (apiKey == null || apiKey.isBlank() || apiKey.startsWith("${")) {
            log.warn("Azure OpenAI API key not configured, returning empty result");
            return new HashMap<>();
        }

        try {
            // Azure OpenAI uses similar API to OpenAI but with different authentication
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messages", List.of(
                Map.of("role", "system", "content", "You are a document extraction assistant. Extract information and respond with valid JSON only."),
                Map.of("role", "user", "content", prompt)
            ));
            requestBody.put("temperature", config.getAi().getTemperature());
            requestBody.put("max_tokens", config.getAi().getMaxTokens());

            // Set headers for Azure
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("api-key", apiKey);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Azure OpenAI endpoint format: https://{resource-name}.openai.azure.com/openai/deployments/{deployment-id}/chat/completions?api-version={api-version}
            String url = config.getAi().getApiUrl();
            ResponseEntity<Map> response = restTemplate.postForEntity(url, request, Map.class);

            // Extract JSON from response (same format as OpenAI)
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");

                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    String content = (String) message.get("content");

                    // Parse JSON response
                    return objectMapper.readValue(content, Map.class);
                }
            }

            log.warn("Azure OpenAI API returned unexpected response");
            return new HashMap<>();

        } catch (Exception e) {
            log.error("Azure OpenAI extraction failed", e);
            return new HashMap<>();
        }
    }

    /**
     * Check if AI service is properly configured
     */
    public boolean isConfigured() {
        if (!config.getAi().isEnabled()) {
            return false;
        }

        String apiKey = config.getAi().getApiKey();
        return apiKey != null && !apiKey.isBlank() && !apiKey.startsWith("${");
    }
}

