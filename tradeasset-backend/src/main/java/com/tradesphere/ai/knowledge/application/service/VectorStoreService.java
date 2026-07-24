package com.tradesphere.ai.knowledge.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Vector Store Service for RAG
 * Stores and retrieves document embeddings for semantic search
 *
 * Production: Use Pinecone, Weaviate, Qdrant, or Chroma
 * Development: In-memory implementation
 */
@Service
@Slf4j
public class VectorStoreService {

    // In-memory storage for development (replace with actual vector DB in production)
    private final Map<String, VectorEntry> vectorStore = new ConcurrentHashMap<>();

    /**
     * Store document embedding
     */
    public void store(String id, float[] embedding, String content, String title,
                     UUID tenantId, Map<String, String> metadata) {
        VectorEntry entry = new VectorEntry(
            id,
            embedding,
            content,
            title,
            tenantId,
            metadata != null ? metadata : new HashMap<>()
        );

        vectorStore.put(id, entry);
        log.debug("Stored vector: {} (dimension: {})", id, embedding.length);
    }

    /**
     * Similarity search using cosine similarity
     */
    public List<KnowledgeAgentService.RetrievedDocument> similaritySearch(
            float[] queryEmbedding,
            UUID tenantId,
            int limit) {

        log.debug("Performing similarity search (limit: {})", limit);

        List<ScoredDocument> scoredDocs = new ArrayList<>();

        for (VectorEntry entry : vectorStore.values()) {
            // Filter by tenant if specified
            if (tenantId != null && !tenantId.equals(entry.tenantId)) {
                continue;
            }

            float similarity = cosineSimilarity(queryEmbedding, entry.embedding);
            scoredDocs.add(new ScoredDocument(entry, similarity));
        }

        // Sort by similarity (descending) and take top N
        return scoredDocs.stream()
            .sorted(Comparator.comparing(ScoredDocument::score).reversed())
            .limit(limit)
            .map(sd -> new KnowledgeAgentService.RetrievedDocument(
                sd.entry.id,
                sd.entry.title,
                sd.entry.content,
                sd.score,
                sd.entry.metadata
            ))
            .toList();
    }

    /**
     * Delete document embeddings
     */
    public void delete(String id) {
        vectorStore.remove(id);
        log.debug("Deleted vector: {}", id);
    }

    /**
     * Clear all vectors for a tenant
     */
    public void clearTenant(UUID tenantId) {
        vectorStore.entrySet().removeIf(entry ->
            tenantId.equals(entry.getValue().tenantId)
        );
        log.info("Cleared all vectors for tenant: {}", tenantId);
    }

    /**
     * Calculate cosine similarity between two vectors
     */
    private float cosineSimilarity(float[] a, float[] b) {
        if (a.length != b.length) {
            throw new IllegalArgumentException("Vectors must have same dimension");
        }

        float dotProduct = 0.0f;
        float normA = 0.0f;
        float normB = 0.0f;

        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }

        return dotProduct / (float) (Math.sqrt(normA) * Math.sqrt(normB));
    }

    private record VectorEntry(
        String id,
        float[] embedding,
        String content,
        String title,
        UUID tenantId,
        Map<String, String> metadata
    ) {}

    private record ScoredDocument(VectorEntry entry, float score) {}
}

