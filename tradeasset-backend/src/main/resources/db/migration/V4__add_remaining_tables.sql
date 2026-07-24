-- Add remaining missing tables
-- Version: 4.0.0
-- Description: Bank Guarantees, Documentary Collections, SCF, Shipments, Copilot, Knowledge, Security, and Workflow Tasks

-- ==============================================
-- BANK GUARANTEES
-- ==============================================
CREATE TABLE bank_guarantees (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    bg_number VARCHAR(255),
    reference_number VARCHAR(255) NOT NULL,
    applicant_id UUID NOT NULL,
    beneficiary_id UUID NOT NULL,
    issuing_bank VARCHAR(255),
    advising_bank VARCHAR(255),
    bg_type VARCHAR(50) NOT NULL,
    amount NUMERIC(20, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    application_date TIMESTAMP WITH TIME ZONE NOT NULL,
    issue_date TIMESTAMP WITH TIME ZONE,
    effective_date TIMESTAMP WITH TIME ZONE,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    claim_expiry_date TIMESTAMP WITH TIME ZONE,
    status VARCHAR(50) NOT NULL,
    purpose TEXT,
    governing_law VARCHAR(255),
    jurisdiction VARCHAR(255),
    claim_procedure TEXT,
    underlying_contract_number VARCHAR(255),
    underlying_contract_date DATE,
    underlying_contract_value NUMERIC(20, 2),
    reduction_allowed BOOLEAN DEFAULT FALSE,
    extension_allowed BOOLEAN DEFAULT FALSE,
    reduction_terms TEXT,
    extension_terms TEXT,
    claims_count INTEGER DEFAULT 0,
    claimed_amount NUMERIC(20, 2),
    paid_amount NUMERIC(20, 2),
    required_documents TEXT[],
    special_conditions TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by VARCHAR(255),
    approved_at TIMESTAMP WITH TIME ZONE,
    approved_by VARCHAR(255),
    issued_at TIMESTAMP WITH TIME ZONE,
    issued_by VARCHAR(255),
    version INTEGER DEFAULT 0,
    CONSTRAINT fk_bank_guarantees_applicant FOREIGN KEY (applicant_id) REFERENCES customers(id),
    CONSTRAINT fk_bank_guarantees_beneficiary FOREIGN KEY (beneficiary_id) REFERENCES customers(id)
);

CREATE INDEX idx_bank_guarantees_tenant ON bank_guarantees(tenant_id);
CREATE INDEX idx_bank_guarantees_status ON bank_guarantees(status);
CREATE INDEX idx_bank_guarantees_bg_number ON bank_guarantees(bg_number);

-- ==============================================
-- DOCUMENTARY COLLECTIONS
-- ==============================================
CREATE TABLE documentary_collections (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    collection_number VARCHAR(255),
    reference_number VARCHAR(255) NOT NULL,
    drawer_id UUID NOT NULL,
    drawee_id UUID NOT NULL,
    remitting_bank VARCHAR(255),
    collecting_bank VARCHAR(255),
    presenting_bank VARCHAR(255),
    collection_type VARCHAR(50) NOT NULL,
    amount NUMERIC(20, 2) NOT NULL,
    currency VARCHAR(3) NOT NULL,
    payment_terms VARCHAR(50) NOT NULL,
    tenor_days INTEGER,
    collection_date TIMESTAMP WITH TIME ZONE NOT NULL,
    presentation_date TIMESTAMP WITH TIME ZONE,
    acceptance_date TIMESTAMP WITH TIME ZONE,
    payment_due_date DATE,
    payment_date DATE,
    status VARCHAR(50) NOT NULL,
    required_documents TEXT[],
    documents_received BOOLEAN DEFAULT FALSE,
    documents_complete BOOLEAN DEFAULT FALSE,
    collection_instructions TEXT,
    protest_instructions TEXT,
    charges_instructions TEXT,
    bank_charges NUMERIC(10, 2),
    collection_charges NUMERIC(10, 2),
    charges_for_account VARCHAR(255),
    goods_description TEXT,
    invoice_number VARCHAR(255),
    invoice_date DATE,
    amount_received NUMERIC(20, 2),
    outstanding_amount NUMERIC(20, 2),
    protest_required BOOLEAN DEFAULT FALSE,
    protested BOOLEAN DEFAULT FALSE,
    protest_date TIMESTAMP WITH TIME ZONE,
    protest_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by VARCHAR(255),
    completed_at TIMESTAMP WITH TIME ZONE,
    completed_by VARCHAR(255),
    version INTEGER DEFAULT 0,
    CONSTRAINT fk_documentary_collections_drawer FOREIGN KEY (drawer_id) REFERENCES customers(id),
    CONSTRAINT fk_documentary_collections_drawee FOREIGN KEY (drawee_id) REFERENCES customers(id)
);

CREATE INDEX idx_documentary_collections_tenant ON documentary_collections(tenant_id);
CREATE INDEX idx_documentary_collections_status ON documentary_collections(status);
CREATE INDEX idx_documentary_collections_number ON documentary_collections(collection_number);

-- ==============================================
-- SUPPLY CHAIN FINANCE (SCF)
-- ==============================================
CREATE TABLE scf_facilities (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(255) NOT NULL,
    scf_reference VARCHAR(255) NOT NULL,
    scf_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    supplier_id UUID,
    supplier_name VARCHAR(255),
    buyer_id UUID,
    buyer_name VARCHAR(255),
    financier_id UUID,
    financier_name VARCHAR(255),
    invoice_amount NUMERIC(20, 2) NOT NULL,
    finance_amount NUMERIC(20, 2),
    advance_rate NUMERIC(5, 2),
    currency VARCHAR(3) NOT NULL,
    discount_rate NUMERIC(5, 2),
    fee_amount NUMERIC(20, 2),
    interest_rate NUMERIC(5, 2),
    invoice_number VARCHAR(255),
    purchase_order_number VARCHAR(255),
    invoice_date DATE,
    due_date DATE,
    payment_term_days INTEGER,
    finance_date DATE,
    maturity_date DATE,
    early_payment_date DATE,
    early_payment_discount NUMERIC(5, 2),
    approved BOOLEAN DEFAULT FALSE,
    approved_at TIMESTAMP WITH TIME ZONE,
    disbursed BOOLEAN DEFAULT FALSE,
    disbursed_at TIMESTAMP WITH TIME ZONE,
    repaid BOOLEAN DEFAULT FALSE,
    repaid_at TIMESTAMP WITH TIME ZONE,
    goods_description TEXT,
    shipment_reference VARCHAR(255),
    credit_score VARCHAR(50),
    risk_rating VARCHAR(50),
    credit_limit NUMERIC(20, 2),
    workflow_id UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by VARCHAR(255),
    version INTEGER DEFAULT 0
);

CREATE INDEX idx_scf_facilities_tenant ON scf_facilities(tenant_id);
CREATE INDEX idx_scf_facilities_status ON scf_facilities(status);
CREATE INDEX idx_scf_facilities_reference ON scf_facilities(scf_reference);
CREATE INDEX idx_scf_facilities_supplier ON scf_facilities(supplier_id);
CREATE INDEX idx_scf_facilities_buyer ON scf_facilities(buyer_id);

-- ==============================================
-- SHIPMENTS
-- ==============================================
CREATE TABLE shipments (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    shipment_number VARCHAR(255) NOT NULL UNIQUE,
    reference_number VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    estimated_arrival TIMESTAMP WITH TIME ZONE,
    actual_arrival TIMESTAMP WITH TIME ZONE,
    origin_port VARCHAR(255),
    origin_country VARCHAR(255),
    destination_port VARCHAR(255),
    destination_country VARCHAR(255),
    transport_mode VARCHAR(50),
    vessel_name VARCHAR(255),
    voyage_number VARCHAR(255),
    container_number VARCHAR(255),
    shipper_id UUID,
    consignee_id UUID,
    carrier_id UUID,
    bill_of_lading_number VARCHAR(255),
    documents_received BOOLEAN DEFAULT FALSE,
    milestone_flags SMALLINT,
    created_by UUID,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE,
    version BIGINT,
    CONSTRAINT fk_shipments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE INDEX idx_shipment_number ON shipments(shipment_number);
CREATE INDEX idx_shipment_tenant ON shipments(tenant_id);
CREATE INDEX idx_shipment_status ON shipments(status, tenant_id);
CREATE INDEX idx_shipment_reference ON shipments(reference_number, tenant_id);
CREATE INDEX idx_shipment_eta ON shipments(estimated_arrival, status);

-- Shipment Tracking Events (ElementCollection)
CREATE TABLE shipment_tracking_events (
    shipment_id UUID NOT NULL,
    event_timestamp TIMESTAMP WITH TIME ZONE,
    description TEXT,
    event_status VARCHAR(50),
    location VARCHAR(255),
    CONSTRAINT fk_shipment_tracking_events FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE
);

CREATE INDEX idx_shipment_tracking_shipment ON shipment_tracking_events(shipment_id);

-- ==============================================
-- AI COPILOT - CONVERSATIONS
-- ==============================================
CREATE TABLE conversations (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    tenant_id UUID NOT NULL,
    session_id VARCHAR(255) NOT NULL UNIQUE,
    status VARCHAR(50) NOT NULL,
    started_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_message_at TIMESTAMP WITH TIME ZONE NOT NULL,
    ended_at TIMESTAMP WITH TIME ZONE,
    created_by UUID,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE,
    version BIGINT,
    CONSTRAINT fk_conversations_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_conversations_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE INDEX idx_conversation_session ON conversations(session_id);
CREATE INDEX idx_conversation_user ON conversations(user_id);
CREATE INDEX idx_conversation_status ON conversations(status);

-- Conversation Context (ElementCollection)
CREATE TABLE conversation_context (
    conversation_id UUID NOT NULL,
    context_key VARCHAR(255) NOT NULL,
    context_value TEXT,
    PRIMARY KEY (conversation_id, context_key),
    CONSTRAINT fk_conversation_context FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE
);

-- Conversation Messages
CREATE TABLE conversation_messages (
    id UUID PRIMARY KEY,
    conversation_id UUID NOT NULL,
    role VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    intent VARCHAR(50),
    confidence REAL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_conversation_messages FOREIGN KEY (conversation_id) REFERENCES conversations(id) ON DELETE CASCADE
);

CREATE INDEX idx_message_conversation ON conversation_messages(conversation_id);
CREATE INDEX idx_message_timestamp ON conversation_messages(timestamp);

-- ==============================================
-- KNOWLEDGE DOCUMENTS
-- ==============================================
CREATE TABLE knowledge_documents (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    content TEXT NOT NULL,
    document_type VARCHAR(50),
    status VARCHAR(50) NOT NULL,
    category VARCHAR(255),
    chunk_count INTEGER,
    embedding_model VARCHAR(255),
    indexed_at TIMESTAMP WITH TIME ZONE,
    is_public BOOLEAN DEFAULT FALSE,
    allowed_roles TEXT[],
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID,
    version BIGINT,
    CONSTRAINT fk_knowledge_documents_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE INDEX idx_knowledge_documents_tenant ON knowledge_documents(tenant_id);
CREATE INDEX idx_knowledge_documents_status ON knowledge_documents(status);
CREATE INDEX idx_knowledge_documents_type ON knowledge_documents(document_type);

-- ==============================================
-- SECURITY - IDENTITY PROVIDERS
-- ==============================================
CREATE TABLE identity_providers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    provider_id VARCHAR(255) NOT NULL,
    provider_name VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    protocol VARCHAR(50) NOT NULL,
    auth_endpoint VARCHAR(500) NOT NULL,
    token_endpoint VARCHAR(500),
    user_info_endpoint VARCHAR(500),
    logout_endpoint VARCHAR(500),
    client_id VARCHAR(255) NOT NULL,
    client_secret VARCHAR(500) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE INDEX idx_provider_id ON identity_providers(provider_id);
CREATE INDEX idx_idp_tenant_id ON identity_providers(tenant_id);

-- Identity Provider Config (ElementCollection)
CREATE TABLE identity_provider_config (
    provider_id UUID NOT NULL,
    config_key VARCHAR(255) NOT NULL,
    config_value VARCHAR(500),
    PRIMARY KEY (provider_id, config_key),
    CONSTRAINT fk_identity_provider_config FOREIGN KEY (provider_id) REFERENCES identity_providers(id) ON DELETE CASCADE
);

-- ==============================================
-- SECURITY - MFA SETUPS
-- ==============================================
CREATE TABLE mfa_setups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    method VARCHAR(50) NOT NULL,
    secret VARCHAR(500) NOT NULL,
    enabled BOOLEAN DEFAULT FALSE,
    verified BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    enabled_at TIMESTAMP WITH TIME ZONE,
    last_used_at TIMESTAMP WITH TIME ZONE,
    phone_number VARCHAR(50),
    email VARCHAR(255)
);

CREATE INDEX idx_mfa_user_id ON mfa_setups(user_id);
CREATE INDEX idx_mfa_tenant_id ON mfa_setups(tenant_id);

-- MFA Backup Codes (ElementCollection)
CREATE TABLE mfa_backup_codes (
    mfa_setup_id UUID NOT NULL,
    backup_code VARCHAR(255),
    CONSTRAINT fk_mfa_backup_codes FOREIGN KEY (mfa_setup_id) REFERENCES mfa_setups(id) ON DELETE CASCADE
);

-- ==============================================
-- SECURITY - SSO SESSIONS
-- ==============================================
CREATE TABLE sso_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    session_id VARCHAR(255) NOT NULL UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    display_name VARCHAR(255),
    provider_id VARCHAR(255) NOT NULL,
    tenant_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_accessed_at TIMESTAMP WITH TIME ZONE,
    ip_address VARCHAR(50),
    user_agent VARCHAR(500),
    active BOOLEAN DEFAULT TRUE
);

CREATE INDEX idx_session_id ON sso_sessions(session_id);
CREATE INDEX idx_user_id ON sso_sessions(user_id);
CREATE INDEX idx_tenant_id ON sso_sessions(tenant_id);

-- SSO Session Attributes (ElementCollection)
CREATE TABLE sso_session_attributes (
    session_id UUID NOT NULL,
    attribute_key VARCHAR(255) NOT NULL,
    attribute_value VARCHAR(500),
    PRIMARY KEY (session_id, attribute_key),
    CONSTRAINT fk_sso_session_attributes FOREIGN KEY (session_id) REFERENCES sso_sessions(id) ON DELETE CASCADE
);

-- ==============================================
-- WORKFLOW TASKS
-- ==============================================
CREATE TABLE workflow_tasks (
    id UUID PRIMARY KEY,
    workflow_id UUID,
    task_name VARCHAR(255) NOT NULL,
    task_description VARCHAR(1000),
    status VARCHAR(50) NOT NULL,
    priority VARCHAR(50) NOT NULL,
    assigned_to UUID,
    assigned_to_name VARCHAR(255),
    assigned_by UUID,
    assigned_at TIMESTAMP WITH TIME ZONE,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    outcome VARCHAR(255),
    comments VARCHAR(2000),
    due_date TIMESTAMP WITH TIME ZONE NOT NULL,
    sla_breached BOOLEAN DEFAULT FALSE,
    escalated_at TIMESTAMP WITH TIME ZONE,
    escalated_to UUID,
    sequence_order INTEGER NOT NULL,
    version BIGINT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_workflow_tasks_workflow FOREIGN KEY (workflow_id) REFERENCES workflows(id) ON DELETE CASCADE,
    CONSTRAINT fk_workflow_tasks_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id)
);

CREATE INDEX idx_workflow_tasks_workflow ON workflow_tasks(workflow_id);
CREATE INDEX idx_workflow_tasks_assigned ON workflow_tasks(assigned_to);
CREATE INDEX idx_workflow_tasks_status ON workflow_tasks(status);

COMMIT;

