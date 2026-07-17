-- V3: Create Identity Schema for IAM (Identity & Access Management)
-- Date: 2026-07-17
-- Description: Users, Roles, Permissions, and Authentication

-- Create identity schema
CREATE SCHEMA IF NOT EXISTS identity;

-- =============================================
-- USERS TABLE
-- =============================================
CREATE TABLE identity.users (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    last_login_at TIMESTAMP,
    failed_login_attempts INTEGER NOT NULL DEFAULT 0,
    locked_until TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 1,
    CONSTRAINT chk_user_status CHECK (status IN ('PENDING', 'ACTIVE', 'INACTIVE', 'LOCKED', 'SUSPENDED'))
);

-- Indexes for users
CREATE INDEX idx_users_tenant_id ON identity.users(tenant_id);
CREATE INDEX idx_users_username ON identity.users(username);
CREATE INDEX idx_users_email ON identity.users(email);
CREATE INDEX idx_users_status ON identity.users(status);

-- =============================================
-- USER ROLES (Many-to-Many Junction Table)
-- =============================================
CREATE TABLE identity.user_roles (
    user_id UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL,
    PRIMARY KEY (user_id, role_id)
);

CREATE INDEX idx_user_roles_user_id ON identity.user_roles(user_id);
CREATE INDEX idx_user_roles_role_id ON identity.user_roles(role_id);

-- =============================================
-- ROLES TABLE
-- =============================================
CREATE TABLE identity.roles (
    id UUID PRIMARY KEY,
    tenant_id UUID NOT NULL,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(50) UNIQUE NOT NULL,
    description TEXT,
    is_system_role BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 1
);

CREATE INDEX idx_roles_tenant_id ON identity.roles(tenant_id);
CREATE INDEX idx_roles_code ON identity.roles(code);

-- =============================================
-- PERMISSIONS TABLE
-- =============================================
CREATE TABLE identity.permissions (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(100) UNIQUE NOT NULL,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID,
    updated_at TIMESTAMP,
    updated_by UUID,
    version BIGINT NOT NULL DEFAULT 1
);

CREATE INDEX idx_permissions_code ON identity.permissions(code);
CREATE INDEX idx_permissions_resource ON identity.permissions(resource);

-- =============================================
-- ROLE PERMISSIONS (Many-to-Many Junction Table)
-- =============================================
CREATE TABLE identity.role_permissions (
    role_id UUID NOT NULL REFERENCES identity.roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES identity.permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

CREATE INDEX idx_role_permissions_role_id ON identity.role_permissions(role_id);
CREATE INDEX idx_role_permissions_permission_id ON identity.role_permissions(permission_id);

-- =============================================
-- REFRESH TOKENS TABLE (for JWT refresh tokens)
-- =============================================
CREATE TABLE identity.refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES identity.users(id) ON DELETE CASCADE,
    token VARCHAR(500) UNIQUE NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    revoked BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at TIMESTAMP
);

CREATE INDEX idx_refresh_tokens_user_id ON identity.refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_token ON identity.refresh_tokens(token);
CREATE INDEX idx_refresh_tokens_expires_at ON identity.refresh_tokens(expires_at);

-- =============================================
-- SEED DATA: Default Roles
-- =============================================
INSERT INTO identity.roles (id, tenant_id, name, code, description, is_system_role, created_at)
VALUES
    ('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', 'Super Admin', 'SUPER_ADMIN', 'Full system access', true, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', 'Admin', 'ADMIN', 'Administrative access', true, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', 'Customer Manager', 'CUSTOMER_MANAGER', 'Manage customers', true, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', 'LC Manager', 'LC_MANAGER', 'Manage letters of credit', true, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', 'Compliance Officer', 'COMPLIANCE_OFFICER', 'Compliance and risk management', true, CURRENT_TIMESTAMP),
    ('10000000-0000-0000-0000-000000000006', '00000000-0000-0000-0000-000000000001', 'Viewer', 'VIEWER', 'Read-only access', true, CURRENT_TIMESTAMP);

-- =============================================
-- SEED DATA: Default Permissions
-- =============================================
INSERT INTO identity.permissions (id, name, code, resource, action, description, created_at)
VALUES
    -- Customer Permissions
    ('20000000-0000-0000-0000-000000000001', 'Create Customer', 'CUSTOMER_CREATE', 'CUSTOMER', 'CREATE', 'Create new customers', CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000002', 'Read Customer', 'CUSTOMER_READ', 'CUSTOMER', 'READ', 'View customer details', CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000003', 'Update Customer', 'CUSTOMER_UPDATE', 'CUSTOMER', 'UPDATE', 'Update customer information', CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000004', 'Delete Customer', 'CUSTOMER_DELETE', 'CUSTOMER', 'DELETE', 'Delete customers', CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000005', 'Approve Customer', 'CUSTOMER_APPROVE', 'CUSTOMER', 'APPROVE', 'Approve customer applications', CURRENT_TIMESTAMP),

    -- LC Permissions
    ('20000000-0000-0000-0000-000000000011', 'Create LC', 'LC_CREATE', 'LC', 'CREATE', 'Create letter of credit', CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000012', 'Read LC', 'LC_READ', 'LC', 'READ', 'View LC details', CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000013', 'Update LC', 'LC_UPDATE', 'LC', 'UPDATE', 'Update LC information', CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000014', 'Approve LC', 'LC_APPROVE', 'LC', 'APPROVE', 'Approve LC applications', CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000015', 'Issue LC', 'LC_ISSUE', 'LC', 'ISSUE', 'Issue approved LCs', CURRENT_TIMESTAMP),

    -- User Management Permissions
    ('20000000-0000-0000-0000-000000000021', 'Manage Users', 'USER_MANAGE', 'USER', 'MANAGE', 'Full user management', CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000022', 'Manage Roles', 'ROLE_MANAGE', 'ROLE', 'MANAGE', 'Manage roles and permissions', CURRENT_TIMESTAMP),

    -- System Permissions
    ('20000000-0000-0000-0000-000000000031', 'View Reports', 'REPORT_VIEW', 'REPORT', 'VIEW', 'View analytics and reports', CURRENT_TIMESTAMP),
    ('20000000-0000-0000-0000-000000000032', 'System Config', 'SYSTEM_CONFIG', 'SYSTEM', 'CONFIG', 'Configure system settings', CURRENT_TIMESTAMP);

-- =============================================
-- SEED DATA: Assign Permissions to Roles
-- =============================================

-- Super Admin: All permissions
INSERT INTO identity.role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000001', id FROM identity.permissions;

-- Admin: Most permissions except system config
INSERT INTO identity.role_permissions (role_id, permission_id)
SELECT '10000000-0000-0000-0000-000000000002', id
FROM identity.permissions
WHERE code != 'SYSTEM_CONFIG';

-- Customer Manager: Customer-related permissions
INSERT INTO identity.role_permissions (role_id, permission_id)
VALUES
    ('10000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000001'), -- CREATE
    ('10000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000002'), -- READ
    ('10000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000003'), -- UPDATE
    ('10000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000005'); -- APPROVE

-- LC Manager: LC-related permissions
INSERT INTO identity.role_permissions (role_id, permission_id)
VALUES
    ('10000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000011'),
    ('10000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000012'),
    ('10000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000013'),
    ('10000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000014'),
    ('10000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000015');

-- Viewer: Read-only permissions
INSERT INTO identity.role_permissions (role_id, permission_id)
VALUES
    ('10000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000002'),
    ('10000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000012'),
    ('10000000-0000-0000-0000-000000000006', '20000000-0000-0000-0000-000000000031');

-- =============================================
-- COMMENTS
-- =============================================
COMMENT ON SCHEMA identity IS 'Identity and Access Management schema';
COMMENT ON TABLE identity.users IS 'System users with authentication credentials';
COMMENT ON TABLE identity.roles IS 'User roles for authorization';
COMMENT ON TABLE identity.permissions IS 'Granular permissions for role-based access control';
COMMENT ON TABLE identity.refresh_tokens IS 'JWT refresh tokens for authentication';

