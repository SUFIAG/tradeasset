-- =====================================================
-- Migration: V11 - Create SSO and MFA Schema
-- Description: Tables for Single Sign-On and Multi-Factor Authentication
-- Author: AI System
-- Date: 2026-07-17
-- =====================================================

-- Create security schema
CREATE SCHEMA IF NOT EXISTS security;

-- =====================================================
-- Table: sso_providers
-- Description: SSO identity providers configuration
-- =====================================================
CREATE TABLE security.sso_providers (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Provider Info
    provider_name VARCHAR(100) NOT NULL,
    provider_type VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,

    -- Configuration
    issuer_url VARCHAR(500),
    authorization_endpoint VARCHAR(500),
    token_endpoint VARCHAR(500),
    userinfo_endpoint VARCHAR(500),
    jwks_uri VARCHAR(500),

    -- Client Credentials
    client_id VARCHAR(255),
    client_secret VARCHAR(500),

    -- SAML Specific
    saml_entity_id VARCHAR(500),
    saml_sso_url VARCHAR(500),
    saml_certificate TEXT,

    -- OIDC Specific
    oidc_scopes TEXT[],
    oidc_response_type VARCHAR(100),

    -- LDAP Specific
    ldap_url VARCHAR(500),
    ldap_base_dn VARCHAR(255),
    ldap_bind_dn VARCHAR(255),
    ldap_bind_password VARCHAR(500),
    ldap_user_search_filter VARCHAR(255),

    -- Metadata
    metadata JSONB,

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100),
    updated_at TIMESTAMP,
    updated_by VARCHAR(100),

    -- Constraints
    CONSTRAINT uq_sso_provider_name_tenant UNIQUE (tenant_id, provider_name),
    CONSTRAINT ck_provider_type CHECK (provider_type IN ('SAML', 'OAUTH2', 'OIDC', 'LDAP'))
);

-- =====================================================
-- Table: sso_sessions
-- Description: Active SSO sessions
-- =====================================================
CREATE TABLE security.sso_sessions (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Session Info
    session_id VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    provider_id UUID NOT NULL,

    -- Authentication
    subject VARCHAR(255) NOT NULL,
    email VARCHAR(255),

    -- Tokens
    access_token TEXT,
    refresh_token TEXT,
    id_token TEXT,

    -- Session Management
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_accessed_at TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,

    -- User Info
    user_attributes JSONB,

    -- Device Info
    ip_address VARCHAR(45),
    user_agent TEXT,

    -- Status
    active BOOLEAN DEFAULT TRUE,

    -- Foreign Keys
    CONSTRAINT fk_sso_sessions_provider FOREIGN KEY (provider_id)
        REFERENCES security.sso_providers(id) ON DELETE CASCADE
);

-- =====================================================
-- Table: mfa_configurations
-- Description: User MFA settings
-- =====================================================
CREATE TABLE security.mfa_configurations (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL,

    -- MFA Settings
    mfa_enabled BOOLEAN DEFAULT FALSE,
    mfa_method VARCHAR(50) NOT NULL,

    -- TOTP Settings
    totp_secret VARCHAR(255),
    totp_verified BOOLEAN DEFAULT FALSE,
    totp_verified_at TIMESTAMP,

    -- Phone Settings
    phone_number VARCHAR(50),
    phone_verified BOOLEAN DEFAULT FALSE,
    phone_verified_at TIMESTAMP,

    -- Email Settings
    email VARCHAR(255),
    email_verified BOOLEAN DEFAULT FALSE,
    email_verified_at TIMESTAMP,

    -- Backup Codes
    backup_codes TEXT[],
    backup_codes_used INTEGER DEFAULT 0,

    -- Recovery
    recovery_email VARCHAR(255),
    recovery_phone VARCHAR(50),

    -- Audit Fields
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    enabled_at TIMESTAMP,

    -- Constraints
    CONSTRAINT uq_mfa_user_tenant UNIQUE (tenant_id, user_id),
    CONSTRAINT ck_mfa_method CHECK (mfa_method IN ('TOTP', 'SMS', 'EMAIL', 'BACKUP_CODE'))
);

-- =====================================================
-- Table: mfa_attempts
-- Description: MFA verification attempts
-- =====================================================
CREATE TABLE security.mfa_attempts (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,
    user_id UUID NOT NULL,

    -- Attempt Info
    attempt_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    method VARCHAR(50) NOT NULL,

    -- Result
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(255),

    -- Code Info
    code_sent BOOLEAN DEFAULT FALSE,
    code_sent_at TIMESTAMP,
    code_expires_at TIMESTAMP,

    -- Device Info
    ip_address VARCHAR(45),
    user_agent TEXT,

    -- Rate Limiting
    attempts_count INTEGER DEFAULT 1,

    CONSTRAINT ck_mfa_attempt_method CHECK (method IN ('TOTP', 'SMS', 'EMAIL', 'BACKUP_CODE'))
);

-- =====================================================
-- Table: oauth_tokens
-- Description: OAuth2 tokens for API access
-- =====================================================
CREATE TABLE security.oauth_tokens (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- Token Info
    token_value VARCHAR(500) NOT NULL UNIQUE,
    token_type VARCHAR(50) NOT NULL,

    -- Association
    user_id UUID,
    client_id VARCHAR(255),

    -- Scopes
    scopes TEXT[],

    -- Validity
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN DEFAULT FALSE,
    revoked_at TIMESTAMP,

    -- Refresh Token
    refresh_token_value VARCHAR(500),

    -- Additional Data
    metadata JSONB,

    CONSTRAINT ck_token_type CHECK (token_type IN ('ACCESS_TOKEN', 'REFRESH_TOKEN', 'ID_TOKEN'))
);

-- =====================================================
-- Table: authentication_logs
-- Description: Authentication audit trail
-- =====================================================
CREATE TABLE security.authentication_logs (
    id UUID PRIMARY KEY,
    tenant_id VARCHAR(50) NOT NULL,

    -- User Info
    user_id UUID,
    username VARCHAR(255),
    email VARCHAR(255),

    -- Authentication Details
    auth_type VARCHAR(50) NOT NULL,
    auth_method VARCHAR(50),

    -- Result
    success BOOLEAN NOT NULL,
    failure_reason VARCHAR(255),

    -- MFA
    mfa_required BOOLEAN DEFAULT FALSE,
    mfa_completed BOOLEAN DEFAULT FALSE,
    mfa_method VARCHAR(50),

    -- SSO
    sso_provider_id UUID,
    sso_session_id VARCHAR(255),

    -- Device/Location
    ip_address VARCHAR(45),
    user_agent TEXT,
    location VARCHAR(255),

    -- Timestamp
    attempted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT ck_auth_type CHECK (auth_type IN ('PASSWORD', 'SSO', 'API_KEY', 'TOKEN'))
);

-- =====================================================
-- Indexes for Performance
-- =====================================================

-- SSO Providers
CREATE INDEX idx_sso_providers_tenant ON security.sso_providers(tenant_id);
CREATE INDEX idx_sso_providers_enabled ON security.sso_providers(enabled, tenant_id) WHERE enabled = TRUE;

-- SSO Sessions
CREATE INDEX idx_sso_sessions_tenant ON security.sso_sessions(tenant_id);
CREATE INDEX idx_sso_sessions_user ON security.sso_sessions(user_id);
CREATE INDEX idx_sso_sessions_provider ON security.sso_sessions(provider_id);
CREATE INDEX idx_sso_sessions_active ON security.sso_sessions(active, tenant_id) WHERE active = TRUE;
CREATE INDEX idx_sso_sessions_expires ON security.sso_sessions(expires_at);

-- MFA Configurations
CREATE INDEX idx_mfa_config_tenant ON security.mfa_configurations(tenant_id);
CREATE INDEX idx_mfa_config_user ON security.mfa_configurations(user_id);
CREATE INDEX idx_mfa_enabled ON security.mfa_configurations(mfa_enabled, tenant_id) WHERE mfa_enabled = TRUE;

-- MFA Attempts
CREATE INDEX idx_mfa_attempts_tenant ON security.mfa_attempts(tenant_id);
CREATE INDEX idx_mfa_attempts_user ON security.mfa_attempts(user_id, attempt_time DESC);
CREATE INDEX idx_mfa_attempts_time ON security.mfa_attempts(attempt_time DESC);

-- OAuth Tokens
CREATE INDEX idx_oauth_tokens_tenant ON security.oauth_tokens(tenant_id);
CREATE INDEX idx_oauth_tokens_user ON security.oauth_tokens(user_id);
CREATE INDEX idx_oauth_tokens_client ON security.oauth_tokens(client_id);
CREATE INDEX idx_oauth_tokens_expires ON security.oauth_tokens(expires_at);
CREATE INDEX idx_oauth_tokens_active ON security.oauth_tokens(revoked, expires_at) WHERE revoked = FALSE;

-- Authentication Logs
CREATE INDEX idx_auth_logs_tenant ON security.authentication_logs(tenant_id);
CREATE INDEX idx_auth_logs_user ON security.authentication_logs(user_id, attempted_at DESC);
CREATE INDEX idx_auth_logs_time ON security.authentication_logs(attempted_at DESC);
CREATE INDEX idx_auth_logs_failed ON security.authentication_logs(success, attempted_at DESC) WHERE success = FALSE;

-- =====================================================
-- Comments
-- =====================================================

COMMENT ON TABLE security.sso_providers IS 'SSO identity providers configuration (SAML, OAuth2, OIDC, LDAP)';
COMMENT ON TABLE security.sso_sessions IS 'Active SSO sessions for users';
COMMENT ON TABLE security.mfa_configurations IS 'Multi-factor authentication settings per user';
COMMENT ON TABLE security.mfa_attempts IS 'MFA verification attempts for rate limiting and auditing';
COMMENT ON TABLE security.oauth_tokens IS 'OAuth2 access and refresh tokens';
COMMENT ON TABLE security.authentication_logs IS 'Complete authentication audit trail';

COMMENT ON COLUMN security.mfa_configurations.mfa_method IS 'Primary MFA method: TOTP, SMS, EMAIL, or BACKUP_CODE';
COMMENT ON COLUMN security.mfa_configurations.backup_codes IS 'One-time use backup codes for account recovery';

