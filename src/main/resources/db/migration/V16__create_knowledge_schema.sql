-- =====================================================
-- Migration: V16 - Create Knowledge Agent (RAG) Schema
-- Description: Schema for Knowledge Agent with vector embeddings
-- Author: AI System
-- Date: 2026-07-17
-- =====================================================

CREATE SCHEMA IF NOT EXISTS knowledge;

-- Enable vector extension (if using pgvector)
-- CREATE EXTENSION IF NOT EXISTS vector;

-- =====================================================
-- Table: knowledge_documents
-- Description: Source documents for knowledge base
-- =====================================================
CREATE TABLE knowledge.knowledge_documents (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Document Details
    document_title VARCHAR(500) NOT NULL,
    document_type VARCHAR(100) NOT NULL,
    source_type VARCHAR(50) NOT NULL,
    source_reference VARCHAR(500),

    -- Content
    content TEXT NOT NULL,
    content_hash VARCHAR(64),

    -- Metadata
    metadata JSONB,
    tags TEXT[],
    category VARCHAR(100),
    language VARCHAR(10) DEFAULT 'en',

    -- File Info
    file_path VARCHAR(500),
    file_size BIGINT,
    mime_type VARCHAR(100),

    -- Processing Status
    processed BOOLEAN DEFAULT FALSE,
    processed_at TIMESTAMP,
    chunk_count INTEGER DEFAULT 0,
    embedding_count INTEGER DEFAULT 0,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0,

    -- Constraints
    CONSTRAINT ck_source_type CHECK (source_type IN (
        'UPLOADED', 'EMAIL', 'API', 'SCRAPE', 'IMPORT', 'SYSTEM'
    )),
    CONSTRAINT ck_document_type CHECK (document_type IN (
        'POLICY', 'PROCEDURE', 'REGULATION', 'FAQ', 'GUIDE',
        'REFERENCE', 'TEMPLATE', 'TRAINING', 'OTHER'
    ))
);

-- =====================================================
-- Table: knowledge_chunks
-- Description: Document chunks for embedding and retrieval
-- =====================================================
CREATE TABLE knowledge.knowledge_chunks (
    id UUID PRIMARY KEY,
    document_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Chunk Details
    chunk_index INTEGER NOT NULL,
    chunk_text TEXT NOT NULL,
    chunk_size INTEGER,

    -- Context
    preceding_text TEXT,
    following_text TEXT,

    -- Metadata
    metadata JSONB,

    -- Embedding
    embedding_model VARCHAR(100),
    embedding_dimensions INTEGER,
    -- embedding VECTOR(1536),  -- If using pgvector
    embedding_json TEXT,  -- Fallback: store as JSON array

    -- Processing
    embedded BOOLEAN DEFAULT FALSE,
    embedded_at TIMESTAMP,

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_knowledge_chunks_document FOREIGN KEY (document_id)
        REFERENCES knowledge.knowledge_documents(id) ON DELETE CASCADE
);

-- =====================================================
-- Table: knowledge_queries
-- Description: Query history and analytics
-- =====================================================
CREATE TABLE knowledge.knowledge_queries (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Query Details
    query_text TEXT NOT NULL,
    query_embedding_json TEXT,

    -- Context
    user_id VARCHAR(100),
    session_id VARCHAR(100),
    context JSONB,

    -- Results
    top_k INTEGER DEFAULT 5,
    similarity_threshold DECIMAL(5,4),
    results_count INTEGER,
    results JSONB,

    -- Retrieved Chunks
    retrieved_chunk_ids UUID[],

    -- Response
    response_generated BOOLEAN DEFAULT FALSE,
    response_text TEXT,
    response_model VARCHAR(100),

    -- Quality
    user_feedback VARCHAR(20),
    relevance_score DECIMAL(5,4),

    -- Performance
    search_time_ms BIGINT,
    total_time_ms BIGINT,

    -- Audit
    queried_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT ck_user_feedback CHECK (user_feedback IN (
        'HELPFUL', 'NOT_HELPFUL', 'PARTIALLY_HELPFUL', NULL
    ))
);

-- =====================================================
-- Table: knowledge_synonyms
-- Description: Synonyms and term mappings for better retrieval
-- =====================================================
CREATE TABLE knowledge.knowledge_synonyms (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Terms
    primary_term VARCHAR(255) NOT NULL,
    synonym_terms TEXT[] NOT NULL,

    -- Context
    category VARCHAR(100),
    domain VARCHAR(100),

    -- Status
    active BOOLEAN DEFAULT TRUE,

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),

    -- Constraints
    CONSTRAINT uq_knowledge_synonym UNIQUE (tenant_id, primary_term)
);

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- Documents
CREATE INDEX idx_knowledge_docs_tenant ON knowledge.knowledge_documents(tenant_id);
CREATE INDEX idx_knowledge_docs_type ON knowledge.knowledge_documents(document_type);
CREATE INDEX idx_knowledge_docs_source ON knowledge.knowledge_documents(source_type);
CREATE INDEX idx_knowledge_docs_processed ON knowledge.knowledge_documents(processed, tenant_id);
CREATE INDEX idx_knowledge_docs_hash ON knowledge.knowledge_documents(content_hash);
CREATE INDEX idx_knowledge_docs_category ON knowledge.knowledge_documents(category);

-- Chunks
CREATE INDEX idx_knowledge_chunks_document ON knowledge.knowledge_chunks(document_id);
CREATE INDEX idx_knowledge_chunks_tenant ON knowledge.knowledge_chunks(tenant_id);
CREATE INDEX idx_knowledge_chunks_embedded ON knowledge.knowledge_chunks(embedded, tenant_id);
-- CREATE INDEX idx_knowledge_chunks_embedding ON knowledge.knowledge_chunks
--   USING ivfflat (embedding vector_cosine_ops) WITH (lists = 100);  -- If using pgvector

-- Queries
CREATE INDEX idx_knowledge_queries_tenant ON knowledge.knowledge_queries(tenant_id);
CREATE INDEX idx_knowledge_queries_user ON knowledge.knowledge_queries(user_id, queried_at DESC);
CREATE INDEX idx_knowledge_queries_session ON knowledge.knowledge_queries(session_id);
CREATE INDEX idx_knowledge_queries_time ON knowledge.knowledge_queries(queried_at DESC);

-- Synonyms
CREATE INDEX idx_knowledge_synonyms_tenant ON knowledge.knowledge_synonyms(tenant_id);
CREATE INDEX idx_knowledge_synonyms_active ON knowledge.knowledge_synonyms(active, tenant_id) WHERE active = TRUE;

-- =====================================================
-- Comments
-- =====================================================

COMMENT ON TABLE knowledge.knowledge_documents IS 'Source documents for RAG knowledge base';
COMMENT ON TABLE knowledge.knowledge_chunks IS 'Document chunks with embeddings for semantic search';
COMMENT ON TABLE knowledge.knowledge_queries IS 'Query history and analytics for knowledge agent';
COMMENT ON TABLE knowledge.knowledge_synonyms IS 'Term synonyms for improved retrieval';

COMMENT ON COLUMN knowledge.knowledge_chunks.embedding_json IS 'Vector embedding stored as JSON array (fallback if pgvector not available)';

