-- TradeSphere AI Database - Consolidated Schema
-- Version: 2.0.0
-- Description: All tables in public schema with comprehensive test data
-- Date: 2026-07-18

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ==============================================
-- TENANTS & ORGANIZATION TABLES
-- ==============================================

-- Tenants Table
CREATE TABLE tenants (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    legal_name VARCHAR(300) NOT NULL,
    registration_number VARCHAR(100),
    tax_id VARCHAR(100),
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    website VARCHAR(255),
    address_line1 VARCHAR(200),
    address_line2 VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(2),
    postal_code VARCHAR(20),
    settings JSONB,
    features JSONB,
    subscription_tier VARCHAR(50),
    subscription_status VARCHAR(20),
    subscription_starts_at TIMESTAMP WITH TIME ZONE,
    subscription_ends_at TIMESTAMP WITH TIME ZONE,
    max_users INTEGER DEFAULT 50,
    max_branches INTEGER DEFAULT 10,
    max_api_calls_per_day INTEGER DEFAULT 10000,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    onboarded_at TIMESTAMP WITH TIME ZONE,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID
);

CREATE INDEX idx_tenants_code ON tenants(code);
CREATE INDEX idx_tenants_type ON tenants(type);
CREATE INDEX idx_tenants_status ON tenants(status);

-- Organizations Table
CREATE TABLE organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    parent_organization_id UUID,
    level INTEGER NOT NULL DEFAULT 1,
    path VARCHAR(500),
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NOT NULL,
    type VARCHAR(50),
    legal_name VARCHAR(300),
    registration_number VARCHAR(100),
    email VARCHAR(255),
    phone VARCHAR(20),
    address_line1 VARCHAR(200),
    address_line2 VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(2),
    postal_code VARCHAR(20),
    settings JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,
    CONSTRAINT uk_organizations_code_tenant UNIQUE (code, tenant_id, deleted_at),
    CONSTRAINT fk_organizations_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_organizations_parent FOREIGN KEY (parent_organization_id) REFERENCES organizations(id)
);

CREATE INDEX idx_organizations_tenant_id ON organizations(tenant_id);
CREATE INDEX idx_organizations_parent_id ON organizations(parent_organization_id);

-- Branches Table
CREATE TABLE branches (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    organization_id UUID NOT NULL,
    name VARCHAR(200) NOT NULL,
    code VARCHAR(50) NOT NULL,
    branch_type VARCHAR(50),
    swift_code VARCHAR(11),
    email VARCHAR(255),
    phone VARCHAR(20),
    address_line1 VARCHAR(200),
    address_line2 VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(2),
    postal_code VARCHAR(20),
    opening_hours JSONB,
    timezone VARCHAR(50) DEFAULT 'UTC',
    branch_manager_id UUID,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    opened_at DATE,
    closed_at DATE,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,
    CONSTRAINT uk_branches_code_tenant UNIQUE (code, tenant_id, deleted_at),
    CONSTRAINT uk_branches_swift_code UNIQUE (swift_code),
    CONSTRAINT fk_branches_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_branches_organization FOREIGN KEY (organization_id) REFERENCES organizations(id)
);

CREATE INDEX idx_branches_tenant_id ON branches(tenant_id);
CREATE INDEX idx_branches_organization_id ON branches(organization_id);
CREATE INDEX idx_branches_swift_code ON branches(swift_code);

-- ==============================================
-- USERS & IDENTITY TABLES
-- ==============================================

-- Users Table
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_number VARCHAR(20),
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,
    password_hash VARCHAR(255),
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    display_name VARCHAR(200),
    avatar_url VARCHAR(500),
    language VARCHAR(10) DEFAULT 'en',
    timezone VARCHAR(50) DEFAULT 'UTC',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at TIMESTAMP WITH TIME ZONE,
    last_login_ip INET,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP WITH TIME ZONE,
    password_changed_at TIMESTAMP WITH TIME ZONE,
    password_expires_at TIMESTAMP WITH TIME ZONE,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,
    CONSTRAINT uk_users_username_tenant UNIQUE (username, tenant_id, deleted_at),
    CONSTRAINT uk_users_email_tenant UNIQUE (email, tenant_id, deleted_at),
    CONSTRAINT fk_users_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE INDEX idx_users_tenant_id ON users(tenant_id);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_status ON users(status) WHERE deleted_at IS NULL;

-- Roles Table
CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) NOT NULL,
    description TEXT,
    role_type VARCHAR(20) NOT NULL DEFAULT 'CUSTOM',
    parent_role_id UUID,
    level INTEGER NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,
    CONSTRAINT uk_roles_code_tenant UNIQUE (code, tenant_id, deleted_at),
    CONSTRAINT fk_roles_parent FOREIGN KEY (parent_role_id) REFERENCES roles(id),
    CONSTRAINT fk_roles_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE INDEX idx_roles_tenant_id ON roles(tenant_id);
CREATE INDEX idx_roles_code ON roles(code);

-- Permissions Table
CREATE TABLE permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) NOT NULL UNIQUE,
    description TEXT,
    module VARCHAR(50) NOT NULL,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_permissions_module ON permissions(module);
CREATE INDEX idx_permissions_code ON permissions(code);

-- User-Role Mapping
CREATE TABLE user_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,
    organization_id UUID,
    branch_id UUID,
    valid_from TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    valid_until TIMESTAMP WITH TIME ZONE,
    assigned_by UUID NOT NULL,
    assigned_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_user_roles UNIQUE (user_id, role_id, organization_id, branch_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id),
    CONSTRAINT fk_user_roles_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE INDEX idx_user_roles_user_id ON user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON user_roles(role_id);

-- Role-Permission Mapping
CREATE TABLE role_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,
    conditions JSONB,
    granted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    granted_by UUID,
    CONSTRAINT uk_role_permissions UNIQUE (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE
);

CREATE INDEX idx_role_permissions_role_id ON role_permissions(role_id);

-- ==============================================
-- CUSTOMER TABLES
-- ==============================================

-- Customers Table
CREATE TABLE customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    customer_number VARCHAR(50) NOT NULL,
    name VARCHAR(200) NOT NULL,
    legal_name VARCHAR(300) NOT NULL,
    customer_type VARCHAR(50) NOT NULL,
    customer_category VARCHAR(50),
    industry VARCHAR(100),
    business_registration_number VARCHAR(100),
    tax_id VARCHAR(100),
    incorporation_date DATE,
    incorporation_country VARCHAR(2),
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    fax VARCHAR(20),
    website VARCHAR(255),
    address_line1 VARCHAR(200),
    address_line2 VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(2),
    postal_code VARCHAR(20),
    primary_bank_name VARCHAR(200),
    primary_account_number VARCHAR(50),
    swift_code VARCHAR(11),
    kyc_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    kyc_completed_at TIMESTAMP WITH TIME ZONE,
    kyc_expiry_date DATE,
    kyc_next_review_date DATE,
    compliance_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    last_compliance_check_at TIMESTAMP WITH TIME ZONE,
    next_compliance_check_at TIMESTAMP WITH TIME ZONE,
    pep_status BOOLEAN DEFAULT FALSE,
    sanctions_hit BOOLEAN DEFAULT FALSE,
    high_risk_country BOOLEAN DEFAULT FALSE,
    risk_rating VARCHAR(20),
    risk_score NUMERIC(5,2),
    risk_assessed_at TIMESTAMP WITH TIME ZONE,
    risk_next_review_date DATE,
    credit_limit_amount NUMERIC(20,2),
    credit_limit_currency VARCHAR(3) DEFAULT 'USD',
    available_credit_amount NUMERIC(20,2),
    available_credit_currency VARCHAR(3),
    credit_approved_by UUID,
    credit_approved_at TIMESTAMP WITH TIME ZONE,
    relationship_manager_id UUID,
    customer_since DATE,
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    approved_by UUID,
    approved_at TIMESTAMP WITH TIME ZONE,
    notes TEXT,
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,
    CONSTRAINT uk_customers_customer_number UNIQUE (customer_number, tenant_id, deleted_at),
    CONSTRAINT fk_customers_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_customers_relationship_manager FOREIGN KEY (relationship_manager_id) REFERENCES users(id),
    CONSTRAINT fk_customers_approved_by FOREIGN KEY (approved_by) REFERENCES users(id)
);

CREATE INDEX idx_customers_tenant_id ON customers(tenant_id);
CREATE INDEX idx_customers_customer_number ON customers(customer_number);
CREATE INDEX idx_customers_status ON customers(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_customers_type ON customers(customer_type);
CREATE INDEX idx_customers_risk_rating ON customers(risk_rating);
CREATE INDEX idx_customers_kyc_status ON customers(kyc_status);
CREATE INDEX idx_customers_name ON customers USING gin(to_tsvector('english', name));

-- Customer Contacts Table
CREATE TABLE customer_contacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    customer_id UUID NOT NULL,
    contact_type VARCHAR(20) NOT NULL DEFAULT 'PRIMARY',
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    title VARCHAR(100),
    department VARCHAR(100),
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    mobile VARCHAR(20),
    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    notes TEXT,
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,
    CONSTRAINT fk_customer_contacts_customer FOREIGN KEY (customer_id) REFERENCES customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_customer_contacts_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
);

CREATE INDEX idx_customer_contacts_customer_id ON customer_contacts(customer_id);
CREATE INDEX idx_customer_contacts_email ON customer_contacts(email);

-- ==============================================
-- LETTER OF CREDIT (LC) TABLES
-- ==============================================

-- Letters of Credit Table
CREATE TABLE letters_of_credit (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    lc_number VARCHAR(50) NOT NULL,
    lc_type VARCHAR(50) NOT NULL,
    lc_category VARCHAR(50),
    applicant_id UUID NOT NULL,
    beneficiary_id UUID NOT NULL,
    issuing_bank_id UUID,
    advising_bank_id UUID,
    confirming_bank_id UUID,
    lc_amount NUMERIC(20,2) NOT NULL,
    lc_currency VARCHAR(3) NOT NULL DEFAULT 'USD',
    tolerance_percentage NUMERIC(5,2) DEFAULT 0,
    partial_shipment_allowed BOOLEAN DEFAULT TRUE,
    transshipment_allowed BOOLEAN DEFAULT TRUE,
    issue_date DATE NOT NULL,
    expiry_date DATE NOT NULL,
    latest_shipment_date DATE,
    presentation_period_days INTEGER DEFAULT 21,
    payment_terms VARCHAR(50) NOT NULL,
    incoterms VARCHAR(10),
    port_of_loading VARCHAR(200),
    port_of_discharge VARCHAR(200),
    goods_description TEXT NOT NULL,
    required_documents JSONB,
    special_conditions TEXT,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    workflow_state VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    approval_level INTEGER DEFAULT 0,
    approved_by UUID,
    approved_at TIMESTAMP WITH TIME ZONE,
    issued_by UUID,
    issued_at TIMESTAMP WITH TIME ZONE,
    amended_count INTEGER DEFAULT 0,
    last_amended_at TIMESTAMP WITH TIME ZONE,
    utilized_amount NUMERIC(20,2) DEFAULT 0,
    available_amount NUMERIC(20,2),
    notes TEXT,
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,
    CONSTRAINT uk_lc_number UNIQUE (lc_number, tenant_id, deleted_at),
    CONSTRAINT fk_lc_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_lc_applicant FOREIGN KEY (applicant_id) REFERENCES customers(id),
    CONSTRAINT fk_lc_beneficiary FOREIGN KEY (beneficiary_id) REFERENCES customers(id)
);

CREATE INDEX idx_lc_tenant_id ON letters_of_credit(tenant_id);
CREATE INDEX idx_lc_number ON letters_of_credit(lc_number);
CREATE INDEX idx_lc_applicant_id ON letters_of_credit(applicant_id);
CREATE INDEX idx_lc_beneficiary_id ON letters_of_credit(beneficiary_id);
CREATE INDEX idx_lc_status ON letters_of_credit(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_lc_expiry_date ON letters_of_credit(expiry_date);

-- ==============================================
-- DOCUMENT TABLES
-- ==============================================

-- Documents Table
CREATE TABLE documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    document_number VARCHAR(50) NOT NULL,
    document_type VARCHAR(50) NOT NULL,
    document_category VARCHAR(50),
    entity_type VARCHAR(50),
    entity_id UUID,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    file_name VARCHAR(500) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),
    storage_provider VARCHAR(20) DEFAULT 'MINIO',
    storage_bucket VARCHAR(100),
    storage_key VARCHAR(500) NOT NULL,
    storage_url VARCHAR(1000),
    checksum VARCHAR(100),
    version_number INTEGER DEFAULT 1,
    parent_document_id UUID,
    is_latest_version BOOLEAN DEFAULT TRUE,
    extraction_status VARCHAR(20) DEFAULT 'PENDING',
    extracted_data JSONB,
    extraction_confidence NUMERIC(5,2),
    extracted_at TIMESTAMP WITH TIME ZONE,
    ocr_text TEXT,
    classification VARCHAR(50),
    classification_confidence NUMERIC(5,2),
    tags TEXT[],
    metadata JSONB,
    verification_status VARCHAR(20) DEFAULT 'PENDING',
    verified_by UUID,
    verified_at TIMESTAMP WITH TIME ZONE,
    verification_notes TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    expires_at TIMESTAMP WITH TIME ZONE,
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,
    CONSTRAINT uk_documents_number UNIQUE (document_number, tenant_id, deleted_at),
    CONSTRAINT fk_documents_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_documents_parent FOREIGN KEY (parent_document_id) REFERENCES documents(id)
);

CREATE INDEX idx_documents_tenant_id ON documents(tenant_id);
CREATE INDEX idx_documents_document_number ON documents(document_number);
CREATE INDEX idx_documents_entity ON documents(entity_type, entity_id);
CREATE INDEX idx_documents_type ON documents(document_type);
CREATE INDEX idx_documents_status ON documents(status) WHERE deleted_at IS NULL;

-- ==============================================
-- WORKFLOW TABLES
-- ==============================================

-- Workflows Table
CREATE TABLE workflows (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    workflow_name VARCHAR(100) NOT NULL,
    workflow_code VARCHAR(50) NOT NULL,
    workflow_type VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    current_step VARCHAR(100),
    current_step_index INTEGER DEFAULT 0,
    total_steps INTEGER,
    workflow_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    priority VARCHAR(20) DEFAULT 'NORMAL',
    assigned_to UUID,
    assigned_at TIMESTAMP WITH TIME ZONE,
    started_at TIMESTAMP WITH TIME ZONE,
    completed_at TIMESTAMP WITH TIME ZONE,
    due_date TIMESTAMP WITH TIME ZONE,
    workflow_data JSONB,
    approval_chain JSONB,
    escalation_rules JSONB,
    sla_breach BOOLEAN DEFAULT FALSE,
    notes TEXT,
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID,
    CONSTRAINT fk_workflows_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_workflows_assigned_to FOREIGN KEY (assigned_to) REFERENCES users(id)
);

CREATE INDEX idx_workflows_tenant_id ON workflows(tenant_id);
CREATE INDEX idx_workflows_entity ON workflows(entity_type, entity_id);
CREATE INDEX idx_workflows_status ON workflows(workflow_status);
CREATE INDEX idx_workflows_assigned_to ON workflows(assigned_to);

-- ==============================================
-- COMPLIANCE TABLES
-- ==============================================

-- Compliance Checks Table
CREATE TABLE compliance_checks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    check_number VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    check_type VARCHAR(50) NOT NULL,
    screening_provider VARCHAR(50),
    screening_reference VARCHAR(100),
    check_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    risk_level VARCHAR(20),
    findings JSONB,
    sanctions_hit BOOLEAN DEFAULT FALSE,
    pep_hit BOOLEAN DEFAULT FALSE,
    adverse_media_hit BOOLEAN DEFAULT FALSE,
    watchlist_hits INTEGER DEFAULT 0,
    score NUMERIC(5,2),
    screening_result TEXT,
    checked_at TIMESTAMP WITH TIME ZONE,
    checked_by UUID,
    reviewed_by UUID,
    reviewed_at TIMESTAMP WITH TIME ZONE,
    review_notes TEXT,
    next_check_date DATE,
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID,
    CONSTRAINT uk_compliance_check_number UNIQUE (check_number, tenant_id),
    CONSTRAINT fk_compliance_checks_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_compliance_checks_checked_by FOREIGN KEY (checked_by) REFERENCES users(id)
);

CREATE INDEX idx_compliance_checks_tenant_id ON compliance_checks(tenant_id);
CREATE INDEX idx_compliance_checks_entity ON compliance_checks(entity_type, entity_id);
CREATE INDEX idx_compliance_checks_status ON compliance_checks(check_status);

-- ==============================================
-- RISK ASSESSMENT TABLES
-- ==============================================

-- Risk Assessments Table
CREATE TABLE risk_assessments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    assessment_number VARCHAR(50) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID NOT NULL,
    assessment_type VARCHAR(50) NOT NULL,
    assessment_model VARCHAR(50) DEFAULT 'STANDARD',
    risk_score NUMERIC(5,2),
    risk_rating VARCHAR(20),
    risk_category VARCHAR(50),
    risk_factors JSONB,
    mitigation_measures TEXT,
    assessment_status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    assessed_by UUID,
    assessed_at TIMESTAMP WITH TIME ZONE,
    approved_by UUID,
    approved_at TIMESTAMP WITH TIME ZONE,
    valid_until DATE,
    next_review_date DATE,
    notes TEXT,
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID,
    CONSTRAINT uk_risk_assessment_number UNIQUE (assessment_number, tenant_id),
    CONSTRAINT fk_risk_assessments_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_risk_assessments_assessed_by FOREIGN KEY (assessed_by) REFERENCES users(id)
);

CREATE INDEX idx_risk_assessments_tenant_id ON risk_assessments(tenant_id);
CREATE INDEX idx_risk_assessments_entity ON risk_assessments(entity_type, entity_id);
CREATE INDEX idx_risk_assessments_status ON risk_assessments(assessment_status);

-- ==============================================
-- AUDIT LOG TABLE
-- ==============================================

-- Audit Log Table
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id VARCHAR(100) NOT NULL,
    action VARCHAR(50) NOT NULL,
    user_id UUID,
    username VARCHAR(100),
    ip_address INET,
    user_agent TEXT,
    old_values JSONB,
    new_values JSONB,
    changes JSONB,
    occurred_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_audit_logs_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE INDEX idx_audit_logs_tenant_id ON audit_logs(tenant_id);
CREATE INDEX idx_audit_logs_entity_type_id ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_user_id ON audit_logs(user_id);
CREATE INDEX idx_audit_logs_occurred_at ON audit_logs(occurred_at);

COMMIT;

