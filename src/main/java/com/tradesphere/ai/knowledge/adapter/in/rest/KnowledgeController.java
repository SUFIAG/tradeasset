package com.tradesphere.ai.knowledge.adapter.in.rest;

import com.tradesphere.ai.knowledge.application.service.KnowledgeAgentService;
import com.tradesphere.ai.knowledge.application.service.KnowledgeAgentService.KnowledgeResponse;
import com.tradesphere.ai.knowledge.application.service.KnowledgeAgentService.SearchResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * REST Controller for Knowledge Agent (RAG)
 */
@RestController
@RequestMapping("/api/v1/knowledge")
@RequiredArgsConstructor
@Slf4j
public class KnowledgeController {

    private final KnowledgeAgentService knowledgeService;

    /**
     * Ask a question using RAG
     */
    @PostMapping("/ask")
    public ResponseEntity<KnowledgeResponse> askQuestion(
            @RequestBody QuestionRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Question received: {}", request.question());

        KnowledgeResponse response = knowledgeService.answerQuestion(
            request.question(),
            UUID.fromString(tenantId),
            request.context()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Ingest a document into knowledge base
     */
    @PostMapping("/documents")
    public ResponseEntity<DocumentIngestResponse> ingestDocument(
            @RequestBody DocumentRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Ingesting document: {}", request.title());

        try {
            knowledgeService.ingestDocument(
                UUID.randomUUID().toString(),
                request.title(),
                request.content(),
                UUID.fromString(tenantId),
                request.metadata() != null ? request.metadata() : Map.of()
            );

            return ResponseEntity.ok(new DocumentIngestResponse(
                true,
                "Document ingested successfully"
            ));
        } catch (Exception e) {
            log.error("Document ingestion failed", e);
            return ResponseEntity.badRequest().body(new DocumentIngestResponse(
                false,
                "Failed: " + e.getMessage()
            ));
        }
    }

    /**
     * Search knowledge base
     */
    @PostMapping("/search")
    public ResponseEntity<List<SearchResult>> search(
            @RequestBody SearchRequest request,
            @RequestHeader("X-Tenant-Id") String tenantId) {

        log.info("Searching knowledge base: {}", request.query());

        List<SearchResult> results = knowledgeService.search(
            request.query(),
            UUID.fromString(tenantId),
            request.limit() != null ? request.limit() : 10
        );

        return ResponseEntity.ok(results);
    }

    /**
     * Get regulation information
     */
    @GetMapping("/regulations/{topic}")
    public ResponseEntity<KnowledgeResponse> getRegulations(
            @PathVariable String topic,
            @RequestParam String jurisdiction) {

        log.info("Getting regulations for: {} in {}", topic, jurisdiction);

        KnowledgeResponse response = knowledgeService.getRegulationInfo(topic, jurisdiction);
        return ResponseEntity.ok(response);
    }

    /**
     * Get best practices
     */
    @GetMapping("/best-practices/{operation}")
    public ResponseEntity<KnowledgeResponse> getBestPractices(
            @PathVariable String operation) {

        log.info("Getting best practices for: {}", operation);

        KnowledgeResponse response = knowledgeService.getBestPractices(operation);
        return ResponseEntity.ok(response);
    }

    // DTOs
    public record QuestionRequest(String question, String context) {}

    public record DocumentRequest(
        String title,
        String content,
        String documentType,
        Map<String, String> metadata
    ) {}

    public record SearchRequest(String query, Integer limit) {}

    public record DocumentIngestResponse(boolean success, String message) {}
}

