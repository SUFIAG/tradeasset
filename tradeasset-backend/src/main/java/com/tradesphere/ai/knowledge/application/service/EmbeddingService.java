package com.tradesphere.ai.knowledge.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * Embedding Service for converting text to vector embeddings
 * 
 * Production: Use OpenAI ada-002, Cohere, or sentence-transformers
 * Development: Stub implementation with random embeddings
 */
@Service
@Slf4j
public class EmbeddingService {

    @Value("${ai.embedding.dimension:1536}")
    private int embeddingDimension;
    
    @Value("${ai.embedding.enabled:false}")
    private boolean embeddingEnabled;
    
    private final Random random = new Random(42); // Fixed seed for consistency
    
    /**
     * Generate embedding for text
     * In production, this would call OpenAI API or local embedding model
     */
    public float[] generateEmbedding(String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException("Text cannot be empty");
        }
        
        if (!embeddingEnabled) {
            log.debug("Embedding service not configured, using stub implementation");
            return generateStubEmbedding(text);
        }
        
        // In production, call actual embedding API:
        // return openAIClient.createEmbedding(text, "text-embedding-ada-002");
        
        return generateStubEmbedding(text);
    }
    
    /**
     * Generate embeddings for multiple texts in batch
     */
    public float[][] generateBatchEmbeddings(String[] texts) {
        float[][] embeddings = new float[texts.length][];
        for (int i = 0; i < texts.length; i++) {
            embeddings[i] = generateEmbedding(texts[i]);
        }
        return embeddings;
    }
    
    /**
     * Stub implementation: Generate consistent pseudo-random embedding
     * based on text content (for testing/development)
     */
    private float[] generateStubEmbedding(String text) {
        // Use text hashcode as seed for consistency
        Random textRandom = new Random(text.hashCode());
        
        float[] embedding = new float[embeddingDimension];
        for (int i = 0; i < embeddingDimension; i++) {
            embedding[i] = textRandom.nextFloat() * 2 - 1; // Range: [-1, 1]
        }
        
        // Normalize vector
        float norm = 0;
        for (float v : embedding) {
            norm += v * v;
        }
        norm = (float) Math.sqrt(norm);
        
        for (int i = 0; i < embedding.length; i++) {
            embedding[i] /= norm;
        }
        
        return embedding;
    }
}

