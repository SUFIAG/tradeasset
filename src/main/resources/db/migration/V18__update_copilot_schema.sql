-- =====================================================
-- Migration: V18 - Update Copilot Schema for Domain Model
-- Description: Update copilot schema to match domain-driven design
-- Author: AI System
-- Date: 2026-07-17
-- =====================================================

-- Update conversations table to match domain model
ALTER TABLE copilot.copilot_conversations RENAME TO conversations_old;

CREATE TABLE copilot.conversations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    started_at TIMESTAMP NOT NULL,
    last_message_at TIMESTAMP NOT NULL,
    ended_at TIMESTAMP,
    created_by UUID,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    updated_at TIMESTAMP,
    version BIGINT DEFAULT 0,

    CONSTRAINT ck_conversation_status CHECK (status IN ('ACTIVE', 'ENDED', 'ARCHIVED'))
);

-- Update messages table to match domain model
ALTER TABLE copilot.copilot_messages RENAME TO messages_old;

CREATE TABLE copilot.conversation_messages (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL,
    role VARCHAR(20) NOT NULL,
    content TEXT NOT NULL,
    intent VARCHAR(50),
    confidence DECIMAL(5,4),
    timestamp TIMESTAMP NOT NULL,

    CONSTRAINT fk_message_conversation FOREIGN KEY (conversation_id)
        REFERENCES copilot.conversations(id) ON DELETE CASCADE,
    CONSTRAINT ck_message_role CHECK (role IN ('USER', 'ASSISTANT', 'SYSTEM')),
    CONSTRAINT ck_message_intent CHECK (intent IN (
        'QUERY', 'CREATE', 'UPDATE', 'SEARCH', 'DELETE', 'APPROVE', 'REJECT',
        'HELP', 'GREETING', 'FAREWELL', 'NAVIGATION', 'REPORT', 'EXPORT',
        'SYSTEM', 'ERROR', 'UNKNOWN'
    ))
);

-- Context storage table
CREATE TABLE copilot.conversation_context (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL,
    context_key VARCHAR(255) NOT NULL,
    context_value TEXT,

    CONSTRAINT fk_context_conversation FOREIGN KEY (conversation_id)
        REFERENCES copilot.conversations(id) ON DELETE CASCADE,
    CONSTRAINT uq_conversation_context UNIQUE (conversation_id, context_key)
);

-- Create indexes
CREATE INDEX idx_conversation_session ON copilot.conversations(session_id);
CREATE INDEX idx_conversation_user ON copilot.conversations(user_id);
CREATE INDEX idx_conversation_status ON copilot.conversations(status);
CREATE INDEX idx_conversation_tenant ON copilot.conversations(tenant_id);

CREATE INDEX idx_message_conversation ON copilot.conversation_messages(conversation_id);
CREATE INDEX idx_message_timestamp ON copilot.conversation_messages(timestamp);

CREATE INDEX idx_context_conversation ON copilot.conversation_context(conversation_id);

-- Migrate data from old tables (if any)
-- This is a placeholder - adjust based on actual data migration needs
INSERT INTO copilot.conversations (id, user_id, tenant_id, session_id, status, started_at, last_message_at, ended_at, created_at, version)
SELECT id, user_id::UUID, tenant_id::UUID, session_id,
       CASE WHEN active THEN 'ACTIVE' ELSE 'ENDED' END,
       started_at, COALESCE(last_activity_at, started_at), ended_at, created_at, version
FROM copilot.conversations_old
WHERE EXISTS (SELECT 1 FROM copilot.conversations_old);

-- Drop old tables
DROP TABLE IF EXISTS copilot.messages_old CASCADE;
DROP TABLE IF EXISTS copilot.conversations_old CASCADE;

-- Comments
COMMENT ON TABLE copilot.conversations IS 'AI Copilot conversation sessions';
COMMENT ON TABLE copilot.conversation_messages IS 'Individual messages in conversations';
COMMENT ON TABLE copilot.conversation_context IS 'Conversation context key-value storage';

