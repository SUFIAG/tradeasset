-- TradeSphere AI Database Initialization
-- Version: 1.0.0
-- Description: Create initial schemas and core tables

-- Enable required extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create schemas for bounded contexts
CREATE SCHEMA IF NOT EXISTS identity;
CREATE SCHEMA IF NOT EXISTS organization;
CREATE SCHEMA IF NOT EXISTS customer;
CREATE SCHEMA IF NOT EXISTS trade;
CREATE SCHEMA IF NOT EXISTS document;
CREATE SCHEMA IF NOT EXISTS compliance;
CREATE SCHEMA IF NOT EXISTS risk;
CREATE SCHEMA IF NOT EXISTS workflow;
CREATE SCHEMA IF NOT EXISTS ai_services;
CREATE SCHEMA IF NOT EXISTS audit;
CREATE SCHEMA IF NOT EXISTS notification;

-- ==============================================
-- IDENTITY SCHEMA
-- ==============================================

-- Users Table
CREATE TABLE identity.users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    username VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL,
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    phone_number VARCHAR(20),
    phone_verified BOOLEAN NOT NULL DEFAULT FALSE,

    -- Authentication
    password_hash VARCHAR(255),
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_secret VARCHAR(255),

    -- Profile
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    display_name VARCHAR(200),
    avatar_url VARCHAR(500),
    language VARCHAR(10) DEFAULT 'en',
    timezone VARCHAR(50) DEFAULT 'UTC',

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at TIMESTAMP WITH TIME ZONE,
    last_login_ip INET,
    failed_login_attempts INTEGER DEFAULT 0,
    locked_until TIMESTAMP WITH TIME ZONE,

    -- Password Management
    password_changed_at TIMESTAMP WITH TIME ZONE,
    password_expires_at TIMESTAMP WITH TIME ZONE,

    -- Standard Audit Fields
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,

    CONSTRAINT uk_users_username_tenant UNIQUE (username, tenant_id, deleted_at),
    CONSTRAINT uk_users_email_tenant UNIQUE (email, tenant_id, deleted_at)
);

CREATE INDEX idx_users_tenant_id ON identity.users(tenant_id);
CREATE INDEX idx_users_email ON identity.users(email);
CREATE INDEX idx_users_status ON identity.users(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_users_username ON identity.users(username);

COMMENT ON TABLE identity.users IS 'User accounts in the system';
COMMENT ON COLUMN identity.users.status IS 'User status: ACTIVE, INACTIVE, SUSPENDED, LOCKED, PENDING_ACTIVATION';

-- Roles Table
CREATE TABLE identity.roles (
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
    CONSTRAINT fk_roles_parent FOREIGN KEY (parent_role_id) REFERENCES identity.roles(id)
);

CREATE INDEX idx_roles_tenant_id ON identity.roles(tenant_id);
CREATE INDEX idx_roles_code ON identity.roles(code);
CREATE INDEX idx_roles_type ON identity.roles(role_type);

COMMENT ON TABLE identity.roles IS 'User roles for RBAC';
COMMENT ON COLUMN identity.roles.role_type IS 'SYSTEM or CUSTOM';

-- Permissions Table
CREATE TABLE identity.permissions (
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

CREATE INDEX idx_permissions_module ON identity.permissions(module);
CREATE INDEX idx_permissions_code ON identity.permissions(code);

COMMENT ON TABLE identity.permissions IS 'System permissions';

-- User-Role Mapping
CREATE TABLE identity.user_roles (
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
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES identity.users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES identity.roles(id)
);

CREATE INDEX idx_user_roles_user_id ON identity.user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON identity.user_roles(role_id);

-- Role-Permission Mapping
CREATE TABLE identity.role_permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

    role_id UUID NOT NULL,
    permission_id UUID NOT NULL,

    conditions JSONB,

    granted_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    granted_by UUID,

    CONSTRAINT uk_role_permissions UNIQUE (role_id, permission_id),
    CONSTRAINT fk_role_permissions_role FOREIGN KEY (role_id) REFERENCES identity.roles(id) ON DELETE CASCADE,
    CONSTRAINT fk_role_permissions_permission FOREIGN KEY (permission_id) REFERENCES identity.permissions(id) ON DELETE CASCADE
);

CREATE INDEX idx_role_permissions_role_id ON identity.role_permissions(role_id);

-- ==============================================
-- ORGANIZATION SCHEMA
-- ==============================================

-- Tenants Table
CREATE TABLE organization.tenants (
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

CREATE INDEX idx_tenants_code ON organization.tenants(code);
CREATE INDEX idx_tenants_type ON organization.tenants(type);
CREATE INDEX idx_tenants_status ON organization.tenants(status);

COMMENT ON TABLE organization.tenants IS 'Multi-tenant organizations';

-- Organizations Table
CREATE TABLE organization.organizations (
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
    CONSTRAINT fk_organizations_tenant FOREIGN KEY (tenant_id) REFERENCES organization.tenants(id),
    CONSTRAINT fk_organizations_parent FOREIGN KEY (parent_organization_id) REFERENCES organization.organizations(id)
);

CREATE INDEX idx_organizations_tenant_id ON organization.organizations(tenant_id);
CREATE INDEX idx_organizations_parent_id ON organization.organizations(parent_organization_id);
CREATE INDEX idx_organizations_path ON organization.organizations(path);

-- Branches Table
CREATE TABLE organization.branches (
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
    CONSTRAINT fk_branches_tenant FOREIGN KEY (tenant_id) REFERENCES organization.tenants(id),
    CONSTRAINT fk_branches_organization FOREIGN KEY (organization_id) REFERENCES organization.organizations(id),
    CONSTRAINT fk_branches_manager FOREIGN KEY (branch_manager_id) REFERENCES identity.users(id)
);

CREATE INDEX idx_branches_tenant_id ON organization.branches(tenant_id);
CREATE INDEX idx_branches_organization_id ON organization.branches(organization_id);
CREATE INDEX idx_branches_swift_code ON organization.branches(swift_code);

-- ==============================================
-- AUDIT SCHEMA
-- ==============================================

-- Audit Log Table
CREATE TABLE audit.audit_logs (
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

    CONSTRAINT fk_audit_logs_user FOREIGN KEY (user_id) REFERENCES identity.users(id)
);

CREATE INDEX idx_audit_logs_tenant_id ON audit.audit_logs(tenant_id);
CREATE INDEX idx_audit_logs_entity_type_id ON audit.audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_logs_user_id ON audit.audit_logs(user_id);
CREATE INDEX idx_audit_logs_occurred_at ON audit.audit_logs(occurred_at);

COMMENT ON TABLE audit.audit_logs IS 'Immutable audit trail of all system changes';

-- ==============================================
-- SEED DATA
-- ==============================================

-- Insert default tenant
INSERT INTO organization.tenants (
    id, name, code, type, legal_name, email,
    subscription_tier, subscription_status, status
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Default Tenant',
    'DEFAULT',
    'BANK',
    'Default Tenant Ltd.',
    'admin@tradesphere.ai',
    'ENTERPRISE',
    'ACTIVE',
    'ACTIVE'
);

-- Insert system permissions
INSERT INTO identity.permissions (code, name, module, resource, action, description) VALUES
('user.create', 'Create User', 'identity', 'user', 'create', 'Create new user accounts'),
('user.read', 'View User', 'identity', 'user', 'read', 'View user details'),
('user.update', 'Update User', 'identity', 'user', 'update', 'Update user information'),
('user.delete', 'Delete User', 'identity', 'user', 'delete', 'Delete user accounts'),
('user.approve', 'Approve User', 'identity', 'user', 'approve', 'Approve user registration'),
('role.manage', 'Manage Roles', 'identity', 'role', 'manage', 'Manage roles and permissions'),
('customer.create', 'Create Customer', 'customer', 'customer', 'create', 'Create new customers'),
('customer.read', 'View Customer', 'customer', 'customer', 'read', 'View customer details'),
('customer.update', 'Update Customer', 'customer', 'customer', 'update', 'Update customer information'),
('customer.delete', 'Delete Customer', 'customer', 'customer', 'delete', 'Delete customers'),
('customer.approve', 'Approve Customer', 'customer', 'customer', 'approve', 'Approve customer onboarding'),
('lc.create', 'Create LC', 'trade', 'lc', 'create', 'Create letters of credit'),
('lc.read', 'View LC', 'trade', 'lc', 'read', 'View LC details'),
('lc.update', 'Update LC', 'trade', 'lc', 'update', 'Update LC information'),
('lc.delete', 'Delete LC', 'trade', 'lc', 'delete', 'Delete LCs'),
('lc.issue', 'Issue LC', 'trade', 'lc', 'issue', 'Issue letters of credit'),
('lc.approve', 'Approve LC', 'trade', 'lc', 'approve', 'Approve LC applications');

-- Insert system roles
INSERT INTO identity.roles (tenant_id, name, code, role_type, description, level) VALUES
('00000000-0000-0000-0000-000000000001', 'System Administrator', 'SYSTEM_ADMIN', 'SYSTEM', 'Full system access', 1),
('00000000-0000-0000-0000-000000000001', 'Trade Officer', 'TRADE_OFFICER', 'SYSTEM', 'Trade finance operations', 2),
('00000000-0000-0000-0000-000000000001', 'Approver Level 1', 'APPROVER_L1', 'SYSTEM', 'First level approver', 2),
('00000000-0000-0000-0000-000000000001', 'Approver Level 2', 'APPROVER_L2', 'SYSTEM', 'Second level approver', 2),
('00000000-0000-0000-0000-000000000001', 'Compliance Officer', 'COMPLIANCE_OFFICER', 'SYSTEM', 'Compliance and screening', 2),
('00000000-0000-0000-0000-000000000001', 'Risk Analyst', 'RISK_ANALYST', 'SYSTEM', 'Risk assessment', 2),
('00000000-0000-0000-0000-000000000001', 'Auditor', 'AUDITOR', 'SYSTEM', 'Audit access (read-only)', 2),
('00000000-0000-0000-0000-000000000001', 'Customer Service', 'CUSTOMER_SERVICE', 'SYSTEM', 'Customer support', 2);

COMMIT;

