-- =====================================================
-- Migration: V17 - Create Copilot Agent (NLP) Schema
-- Description: Schema for AI Copilot with conversation management
-- Author: AI System
-- Date: 2026-07-17
-- =====================================================

CREATE SCHEMA IF NOT EXISTS copilot;

-- =====================================================
-- Table: copilot_conversations
-- Description: Conversation sessions for copilot
-- =====================================================
CREATE TABLE copilot.copilot_conversations (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Session Details
    session_id VARCHAR(100) NOT NULL UNIQUE,
    user_id VARCHAR(100) NOT NULL,

    -- Context
    context_type VARCHAR(50),
    context_entity_type VARCHAR(50),
    context_entity_id UUID,

    -- Conversation State
    active BOOLEAN DEFAULT TRUE,
    message_count INTEGER DEFAULT 0,

    -- Metadata
    metadata JSONB,
    user_preferences JSONB,

    -- Timestamps
    started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_activity_at TIMESTAMP,
    ended_at TIMESTAMP,

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER DEFAULT 0,

    -- Constraints
    CONSTRAINT ck_context_type CHECK (context_type IN (
        'LC', 'BG', 'COLLECTION', 'SCF', 'CUSTOMER', 'GENERAL', 'HELP'
    ))
);

-- =====================================================
-- Table: copilot_messages
-- Description: Individual messages in conversations
-- =====================================================
CREATE TABLE copilot.copilot_messages (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Message Details
    message_index INTEGER NOT NULL,
    message_role VARCHAR(20) NOT NULL,
    message_text TEXT NOT NULL,

    -- Intent Recognition
    recognized_intent VARCHAR(100),
    intent_confidence DECIMAL(5,4),
    extracted_entities JSONB,

    -- Processing
    processed BOOLEAN DEFAULT FALSE,
    processing_time_ms BIGINT,

    -- Response
    response_generated BOOLEAN DEFAULT FALSE,
    response_text TEXT,
    response_actions JSONB,

    -- AI Model
    model_used VARCHAR(100),
    tokens_used INTEGER,

    -- Feedback
    user_feedback VARCHAR(20),
    feedback_comment TEXT,

    -- Timestamps
    sent_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_copilot_messages_conversation FOREIGN KEY (conversation_id)
        REFERENCES copilot.copilot_conversations(id) ON DELETE CASCADE,
    CONSTRAINT ck_message_role CHECK (message_role IN (
        'USER', 'ASSISTANT', 'SYSTEM'
    )),
    CONSTRAINT ck_copilot_feedback CHECK (user_feedback IN (
        'HELPFUL', 'NOT_HELPFUL', 'NEEDS_IMPROVEMENT', NULL
    ))
);

-- =====================================================
-- Table: copilot_intents
-- Description: Intent definitions and handlers
-- =====================================================
CREATE TABLE copilot.copilot_intents (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50),

    -- Intent Details
    intent_name VARCHAR(100) NOT NULL,
    intent_category VARCHAR(50) NOT NULL,
    description TEXT,

    -- Examples
    training_phrases TEXT[],
    example_responses TEXT[],

    -- Handler
    handler_class VARCHAR(255),
    handler_method VARCHAR(100),

    -- Parameters
    required_parameters JSONB,
    optional_parameters JSONB,

    -- Status
    active BOOLEAN DEFAULT TRUE,
    confidence_threshold DECIMAL(5,4) DEFAULT 0.70,

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),
    version INTEGER DEFAULT 0,

    -- Constraints
    CONSTRAINT uq_copilot_intent UNIQUE (tenant_id, intent_name),
    CONSTRAINT ck_intent_category CHECK (intent_category IN (
        'LC_OPERATION', 'BG_OPERATION', 'COLLECTION_OPERATION', 'SCF_OPERATION',
        'QUERY', 'HELP', 'NAVIGATION', 'REPORTING', 'APPROVAL', 'OTHER'
    ))
);

-- =====================================================
-- Table: copilot_actions
-- Description: Actions executed by copilot
-- =====================================================
CREATE TABLE copilot.copilot_actions (
    id UUID PRIMARY KEY,
    message_id UUID NOT NULL,
    conversation_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Action Details
    action_type VARCHAR(100) NOT NULL,
    action_name VARCHAR(255) NOT NULL,

    -- Parameters
    action_parameters JSONB,

    -- Execution
    executed BOOLEAN DEFAULT FALSE,
    execution_status VARCHAR(50),
    execution_result JSONB,
    error_message TEXT,

    -- Authorization
    requires_approval BOOLEAN DEFAULT FALSE,
    approved BOOLEAN,
    approved_by VARCHAR(100),
    approved_at TIMESTAMP,

    -- Timestamps
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    executed_at TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_copilot_actions_message FOREIGN KEY (message_id)
        REFERENCES copilot.copilot_messages(id) ON DELETE CASCADE,
    CONSTRAINT fk_copilot_actions_conversation FOREIGN KEY (conversation_id)
        REFERENCES copilot.copilot_conversations(id) ON DELETE CASCADE,
    CONSTRAINT ck_execution_status CHECK (execution_status IN (
        'PENDING', 'EXECUTING', 'SUCCESS', 'FAILED', 'CANCELLED'
    ))
);

-- =====================================================
-- Table: copilot_context
-- Description: Conversation context tracking
-- =====================================================
CREATE TABLE copilot.copilot_context (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL,
    tenant_id VARCHAR(50) NOT NULL,

    -- Context Key-Value
    context_key VARCHAR(100) NOT NULL,
    context_value TEXT,
    context_type VARCHAR(50),

    -- Lifecycle
    expires_at TIMESTAMP,

    -- Audit
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    -- Foreign Keys
    CONSTRAINT fk_copilot_context_conversation FOREIGN KEY (conversation_id)
        REFERENCES copilot.copilot_conversations(id) ON DELETE CASCADE,
    CONSTRAINT uq_copilot_context UNIQUE (conversation_id, context_key)
);

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- Conversations
CREATE INDEX idx_copilot_conversations_tenant ON copilot.copilot_conversations(tenant_id);
CREATE INDEX idx_copilot_conversations_user ON copilot.copilot_conversations(user_id, started_at DESC);
CREATE INDEX idx_copilot_conversations_session ON copilot.copilot_conversations(session_id);
CREATE INDEX idx_copilot_conversations_active ON copilot.copilot_conversations(active, tenant_id) WHERE active = TRUE;

-- Messages
CREATE INDEX idx_copilot_messages_conversation ON copilot.copilot_messages(conversation_id, message_index);
CREATE INDEX idx_copilot_messages_tenant ON copilot.copilot_messages(tenant_id);
CREATE INDEX idx_copilot_messages_intent ON copilot.copilot_messages(recognized_intent);
CREATE INDEX idx_copilot_messages_time ON copilot.copilot_messages(sent_at DESC);

-- Intents
CREATE INDEX idx_copilot_intents_tenant ON copilot.copilot_intents(tenant_id);
CREATE INDEX idx_copilot_intents_category ON copilot.copilot_intents(intent_category);
CREATE INDEX idx_copilot_intents_active ON copilot.copilot_intents(active, tenant_id) WHERE active = TRUE;

-- Actions
CREATE INDEX idx_copilot_actions_message ON copilot.copilot_actions(message_id);
CREATE INDEX idx_copilot_actions_conversation ON copilot.copilot_actions(conversation_id);
CREATE INDEX idx_copilot_actions_tenant ON copilot.copilot_actions(tenant_id);
CREATE INDEX idx_copilot_actions_executed ON copilot.copilot_actions(executed, tenant_id);
CREATE INDEX idx_copilot_actions_approval ON copilot.copilot_actions(requires_approval, approved)
    WHERE requires_approval = TRUE AND approved IS NULL;

-- Context
CREATE INDEX idx_copilot_context_conversation ON copilot.copilot_context(conversation_id);
CREATE INDEX idx_copilot_context_tenant ON copilot.copilot_context(tenant_id);

-- =====================================================
-- Comments
-- =====================================================

COMMENT ON TABLE copilot.copilot_conversations IS 'AI Copilot conversation sessions';
COMMENT ON TABLE copilot.copilot_messages IS 'Individual messages in copilot conversations';
COMMENT ON TABLE copilot.copilot_intents IS 'Intent definitions for natural language understanding';
COMMENT ON TABLE copilot.copilot_actions IS 'Actions executed by copilot on behalf of users';
COMMENT ON TABLE copilot.copilot_context IS 'Conversation context tracking for personalized responses';

COMMENT ON COLUMN copilot.copilot_messages.message_role IS 'Role: USER (from user), ASSISTANT (from AI), SYSTEM (system message)';
COMMENT ON COLUMN copilot.copilot_intents.intent_category IS 'Intent category for organization and routing';

