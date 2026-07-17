package com.tradesphere.ai.knowledge.application.service;

import com.tradesphere.ai.shared.ai.AIRequest;
import com.tradesphere.ai.shared.ai.AIResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Knowledge Agent using RAG (Retrieval-Augmented Generation)
 * Provides intelligent answers based on trade finance knowledge base
 *
 * RAG Architecture:
 * 1. Document Ingestion - Index trade finance documents
 * 2. Vector Embedding - Convert documents to embeddings
 * 3. Semantic Search - Find relevant context
 * 4. LLM Generation - Generate answers with context
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class KnowledgeAgentService {

    private final VectorStoreService vectorStore;
    private final EmbeddingService embeddingService;
    private final LLMService llmService;

    /**
     * Answer question using RAG
     */
    public KnowledgeResponse answerQuestion(String question, UUID tenantId, String context) {
        log.info("Knowledge Agent processing question: {}", question);

        try {
            // Step 1: Generate embedding for question
            float[] questionEmbedding = embeddingService.generateEmbedding(question);

            // Step 2: Retrieve relevant documents from vector store
            List<RetrievedDocument> relevantDocs = vectorStore.similaritySearch(
                questionEmbedding,
                tenantId,
                5  // Top 5 relevant documents
            );

            // Step 3: Build context from retrieved documents
            String augmentedContext = buildContext(relevantDocs, context);

            // Step 4: Generate answer using LLM with context
            String answer = llmService.generateAnswer(question, augmentedContext);

            // Step 5: Extract citations
            List<String> citations = extractCitations(relevantDocs);

            log.info("Knowledge Agent answered question successfully");

            return KnowledgeResponse.builder()
                .question(question)
                .answer(answer)
                .relevantDocuments(relevantDocs.stream().map(RetrievedDocument::title).toList())
                .citations(citations)
                .confidence(calculateConfidence(relevantDocs))
                .build();

        } catch (Exception e) {
            log.error("Knowledge Agent error", e);
            return KnowledgeResponse.builder()
                .question(question)
                .answer("I apologize, but I encountered an error processing your question.")
                .confidence(0.0f)
                .build();
        }
    }

    /**
     * Ingest document into knowledge base
     */
    public void ingestDocument(String documentId, String title, String content,
                              UUID tenantId, Map<String, String> metadata) {
        log.info("Ingesting document into knowledge base: {}", title);

        try {
            // Split document into chunks
            List<DocumentChunk> chunks = chunkDocument(content, 500); // 500 token chunks

            // Generate embeddings for each chunk
            for (DocumentChunk chunk : chunks) {
                float[] embedding = embeddingService.generateEmbedding(chunk.text());

                // Store in vector database
                vectorStore.store(
                    documentId + "_" + chunk.index(),
                    embedding,
                    chunk.text(),
                    title,
                    tenantId,
                    metadata
                );
            }

            log.info("Document ingested successfully: {} chunks", chunks.size());

        } catch (Exception e) {
            log.error("Document ingestion failed", e);
            throw new RuntimeException("Failed to ingest document", e);
        }
    }

    /**
     * Search knowledge base
     */
    public List<SearchResult> search(String query, UUID tenantId, int limit) {
        log.debug("Searching knowledge base: {}", query);

        try {
            float[] queryEmbedding = embeddingService.generateEmbedding(query);
            List<RetrievedDocument> docs = vectorStore.similaritySearch(queryEmbedding, tenantId, limit);

            return docs.stream()
                .map(doc -> new SearchResult(
                    doc.title(),
                    doc.content(),
                    doc.score(),
                    doc.metadata()
                ))
                .toList();

        } catch (Exception e) {
            log.error("Search failed", e);
            return Collections.emptyList();
        }
    }

    /**
     * Get trade finance regulations and compliance info
     */
    public KnowledgeResponse getRegulationInfo(String topic, String jurisdiction) {
        String question = String.format(
            "What are the regulations for %s in %s?",
            topic,
            jurisdiction
        );
        return answerQuestion(question, null, "compliance");
    }

    /**
     * Get best practices for trade finance operations
     */
    public KnowledgeResponse getBestPractices(String operation) {
        String question = String.format(
            "What are the best practices for %s in trade finance?",
            operation
        );
        return answerQuestion(question, null, "best-practices");
    }

    // Helper methods

    private String buildContext(List<RetrievedDocument> docs, String additionalContext) {
        StringBuilder context = new StringBuilder();

        if (additionalContext != null) {
            context.append("Additional Context: ").append(additionalContext).append("\n\n");
        }

        context.append("Relevant Information:\n\n");
        for (int i = 0; i < docs.size(); i++) {
            RetrievedDocument doc = docs.get(i);
            context.append(String.format("[%d] %s\n%s\n\n",
                i + 1, doc.title(), doc.content()));
        }

        return context.toString();
    }

    private List<String> extractCitations(List<RetrievedDocument> docs) {
        return docs.stream()
            .map(doc -> String.format("%s (Relevance: %.2f)", doc.title(), doc.score()))
            .toList();
    }

    private float calculateConfidence(List<RetrievedDocument> docs) {
        if (docs.isEmpty()) return 0.0f;

        // Average relevance score of top documents
        return (float) docs.stream()
            .mapToDouble(RetrievedDocument::score)
            .average()
            .orElse(0.0);
    }

    private List<DocumentChunk> chunkDocument(String content, int chunkSize) {
        List<DocumentChunk> chunks = new ArrayList<>();
        String[] sentences = content.split("\\. ");

        StringBuilder currentChunk = new StringBuilder();
        int chunkIndex = 0;

        for (String sentence : sentences) {
            if (currentChunk.length() + sentence.length() > chunkSize && currentChunk.length() > 0) {
                chunks.add(new DocumentChunk(chunkIndex++, currentChunk.toString()));
                currentChunk = new StringBuilder();
            }
            currentChunk.append(sentence).append(". ");
        }

        if (currentChunk.length() > 0) {
            chunks.add(new DocumentChunk(chunkIndex, currentChunk.toString()));
        }

        return chunks;
    }

    // DTOs

    public record KnowledgeResponse(
        String question,
        String answer,
        List<String> relevantDocuments,
        List<String> citations,
        float confidence
    ) {
        public static KnowledgeResponseBuilder builder() {
            return new KnowledgeResponseBuilder();
        }
    }

    public static class KnowledgeResponseBuilder {
        private String question;
        private String answer;
        private List<String> relevantDocuments = new ArrayList<>();
        private List<String> citations = new ArrayList<>();
        private float confidence;

        public KnowledgeResponseBuilder question(String question) {
            this.question = question;
            return this;
        }

        public KnowledgeResponseBuilder answer(String answer) {
            this.answer = answer;
            return this;
        }

        public KnowledgeResponseBuilder relevantDocuments(List<String> docs) {
            this.relevantDocuments = docs;
            return this;
        }

        public KnowledgeResponseBuilder citations(List<String> citations) {
            this.citations = citations;
            return this;
        }

        public KnowledgeResponseBuilder confidence(float confidence) {
            this.confidence = confidence;
            return this;
        }

        public KnowledgeResponse build() {
            return new KnowledgeResponse(question, answer, relevantDocuments, citations, confidence);
        }
    }

    public record RetrievedDocument(
        String id,
        String title,
        String content,
        float score,
        Map<String, String> metadata
    ) {}

    public record DocumentChunk(int index, String text) {}

    public record SearchResult(
        String title,
        String content,
        float relevance,
        Map<String, String> metadata
    ) {}
}

