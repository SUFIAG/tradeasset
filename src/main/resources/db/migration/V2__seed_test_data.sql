-- TradeSphere AI - Comprehensive Test Data
-- Version: 2.0.0
-- Description: Substantial test data for all tables
-- Date: 2026-07-18

-- ==============================================
-- TENANTS & ORGANIZATION DATA
-- ==============================================

-- Insert default tenant
INSERT INTO tenants (
    id, name, code, type, legal_name, email, phone, website,
    city, country, subscription_tier, subscription_status, status,
    max_users, max_branches, onboarded_at, created_at
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    'Global Trade Bank',
    'GTB',
    'BANK',
    'Global Trade Bank International Ltd.',
    'admin@globaltrade.bank',
    '+971-4-1234567',
    'https://www.globaltrade.bank',
    'Dubai',
    'AE',
    'ENTERPRISE',
    'ACTIVE',
    'ACTIVE',
    500,
    50,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
);

-- Insert Organizations
INSERT INTO organizations (
    id, tenant_id, name, code, type, legal_name,
    email, phone, city, country, status, level,
    created_at, created_by, updated_at, updated_by
) VALUES
('10000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001',
 'GTB Headquarters', 'GTB-HQ', 'HEADQUARTERS', 'Global Trade Bank HQ',
 'hq@globaltrade.bank', '+971-4-1234567', 'Dubai', 'AE', 'ACTIVE', 1,
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001'),

('10000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001',
 'GTB Middle East Division', 'GTB-ME', 'DIVISION', 'Global Trade Bank Middle East',
 'me@globaltrade.bank', '+971-4-1234568', 'Dubai', 'AE', 'ACTIVE', 1,
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001'),

('10000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001',
 'GTB Asia Division', 'GTB-ASIA', 'DIVISION', 'Global Trade Bank Asia Pacific',
 'asia@globaltrade.bank', '+65-6123-4567', 'Singapore', 'SG', 'ACTIVE', 1,
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001');

-- Insert Branches
INSERT INTO branches (
    id, tenant_id, organization_id, name, code, branch_type,
    swift_code, email, phone, city, country, status, opened_at,
    timezone, created_at, created_by, updated_at, updated_by
) VALUES
('20000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001',
 'Dubai Main Branch', 'DXB-MAIN', 'MAIN', 'GTBDAEADXXX', 'dubai@globaltrade.bank', '+971-4-1234567',
 'Dubai', 'AE', 'ACTIVE', '2020-01-01', 'Asia/Dubai', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001'),

('20000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000001',
 'Abu Dhabi Branch', 'AUH-BR', 'BRANCH', 'GTBDAEAAXXX', 'abudhabi@globaltrade.bank', '+971-2-1234567',
 'Abu Dhabi', 'AE', 'ACTIVE', '2020-03-01', 'Asia/Dubai', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001'),

('20000000-0000-0000-0000-000000000003', '00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000003',
 'Singapore Branch', 'SIN-BR', 'BRANCH', 'GTBSSGSGXXX', 'singapore@globaltrade.bank', '+65-6123-4567',
 'Singapore', 'SG', 'ACTIVE', '2021-01-01', 'Asia/Singapore', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001'),

('20000000-0000-0000-0000-000000000004', '00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000003',
 'Hong Kong Branch', 'HKG-BR', 'BRANCH', 'GTBHKHKHXXX', 'hongkong@globaltrade.bank', '+852-2123-4567',
 'Hong Kong', 'HK', 'ACTIVE', '2021-06-01', 'Asia/Hong_Kong', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001'),

('20000000-0000-0000-0000-000000000005', '00000000-0000-0000-0000-000000000001', '10000000-0000-0000-0000-000000000002',
 'Riyadh Branch', 'RUH-BR', 'BRANCH', 'GTBSARIXXX', 'riyadh@globaltrade.bank', '+966-11-1234567',
 'Riyadh', 'SA', 'ACTIVE', '2022-01-01', 'Asia/Riyadh', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001');

-- ==============================================
-- PERMISSIONS DATA
-- ==============================================

INSERT INTO permissions (code, name, module, resource, action, description) VALUES
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
('lc.approve', 'Approve LC', 'trade', 'lc', 'approve', 'Approve LC applications'),
('document.upload', 'Upload Document', 'document', 'document', 'upload', 'Upload documents'),
('document.read', 'View Document', 'document', 'document', 'read', 'View documents'),
('document.verify', 'Verify Document', 'document', 'document', 'verify', 'Verify documents'),
('compliance.check', 'Run Compliance Check', 'compliance', 'check', 'execute', 'Execute compliance checks'),
('risk.assess', 'Assess Risk', 'risk', 'assessment', 'assess', 'Perform risk assessments'),
('workflow.manage', 'Manage Workflow', 'workflow', 'workflow', 'manage', 'Manage workflows'),
('audit.view', 'View Audit Logs', 'audit', 'log', 'read', 'View audit trails');

-- ==============================================
-- ROLES DATA
-- ==============================================

INSERT INTO roles (tenant_id, name, code, role_type, description, level) VALUES
('00000000-0000-0000-0000-000000000001', 'System Administrator', 'SYSTEM_ADMIN', 'SYSTEM', 'Full system access', 1),
('00000000-0000-0000-0000-000000000001', 'Trade Officer', 'TRADE_OFFICER', 'SYSTEM', 'Trade finance operations', 2),
('00000000-0000-0000-0000-000000000001', 'Trade Manager', 'TRADE_MANAGER', 'SYSTEM', 'Trade finance manager', 2),
('00000000-0000-0000-0000-000000000001', 'Approver Level 1', 'APPROVER_L1', 'SYSTEM', 'First level approver', 2),
('00000000-0000-0000-0000-000000000001', 'Approver Level 2', 'APPROVER_L2', 'SYSTEM', 'Second level approver', 2),
('00000000-0000-0000-0000-000000000001', 'Compliance Officer', 'COMPLIANCE_OFFICER', 'SYSTEM', 'Compliance and screening', 2),
('00000000-0000-0000-0000-000000000001', 'Risk Analyst', 'RISK_ANALYST', 'SYSTEM', 'Risk assessment', 2),
('00000000-0000-0000-0000-000000000001', 'Auditor', 'AUDITOR', 'SYSTEM', 'Audit access (read-only)', 2),
('00000000-0000-0000-0000-000000000001', 'Customer Service', 'CUSTOMER_SERVICE', 'SYSTEM', 'Customer support', 2),
('00000000-0000-0000-0000-000000000001', 'Document Processor', 'DOC_PROCESSOR', 'SYSTEM', 'Document processing', 2);

-- ==============================================
-- USERS DATA (100+ users)
-- ==============================================

-- Admin user
INSERT INTO users (
    id, tenant_id, username, email, password_hash, first_name, last_name,
    status, email_verified, language, timezone, created_at, created_by, updated_at, updated_by
) VALUES (
    '00000000-0000-0000-0000-000000000001',
    '00000000-0000-0000-0000-000000000001',
    'admin',
    'admin@globaltrade.bank',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',  -- password: admin123
    'System',
    'Administrator',
    'ACTIVE',
    TRUE,
    'en',
    'UTC',
    CURRENT_TIMESTAMP,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP,
    '00000000-0000-0000-0000-000000000001'
);

-- Generate 100 additional users
INSERT INTO users (tenant_id, username, email, password_hash, first_name, last_name, status, email_verified, phone_number, created_at, created_by, updated_at, updated_by)
SELECT
    '00000000-0000-0000-0000-000000000001',
    'user' || generate_series,
    'user' || generate_series || '@globaltrade.bank',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    CASE (generate_series % 10)
        WHEN 0 THEN 'Ahmed'
        WHEN 1 THEN 'Fatima'
        WHEN 2 THEN 'Mohammed'
        WHEN 3 THEN 'Sarah'
        WHEN 4 THEN 'Ali'
        WHEN 5 THEN 'Mariam'
        WHEN 6 THEN 'Omar'
        WHEN 7 THEN 'Layla'
        WHEN 8 THEN 'Hassan'
        ELSE 'Aisha'
    END,
    CASE (generate_series % 10)
        WHEN 0 THEN 'Al-Mansoori'
        WHEN 1 THEN 'Al-Mazrouei'
        WHEN 2 THEN 'Al-Ketbi'
        WHEN 3 THEN 'Al-Suwaidi'
        WHEN 4 THEN 'Al-Shamsi'
        WHEN 5 THEN 'Al-Qasimi'
        WHEN 6 THEN 'Al-Falasi'
        WHEN 7 THEN 'Al-Nuaimi'
        WHEN 8 THEN 'Al-Dhaheri'
        ELSE 'Al-Hosani'
    END,
    'ACTIVE',
    TRUE,
    '+971-50-' || LPAD(generate_series::text, 7, '0'),
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    '00000000-0000-0000-0000-000000000001'
FROM generate_series(1, 100);

-- ==============================================
-- CUSTOMERS DATA (500+ customers)
-- ==============================================

INSERT INTO customers (
    tenant_id, customer_number, name, legal_name, customer_type, customer_category,
    industry, email, phone, city, country, kyc_status, compliance_status,
    risk_rating, risk_score, credit_limit_amount, credit_limit_currency,
    available_credit_amount, status, customer_since, created_at, created_by, updated_at, updated_by
)
SELECT
    '00000000-0000-0000-0000-000000000001',
    'CUST' || LPAD(generate_series::text, 6, '0'),
    CASE (generate_series % 20)
        WHEN 0 THEN 'Emirates Trading LLC'
        WHEN 1 THEN 'Gulf Imports & Exports'
        WHEN 2 THEN 'Middle East Commodities'
        WHEN 3 THEN 'Asian Textiles Company'
        WHEN 4 THEN 'European Electronics GmbH'
        WHEN 5 THEN 'American Machinery Inc'
        WHEN 6 THEN 'Dubai Trading House'
        WHEN 7 THEN 'Singapore Shipping Co'
        WHEN 8 THEN 'Hong Kong Logistics Ltd'
        WHEN 9 THEN 'Saudi Petrochemicals'
        WHEN 10 THEN 'Indian Spices Export'
        WHEN 11 THEN 'Chinese Manufacturing Co'
        WHEN 12 THEN 'Turkish Textiles Ltd'
        WHEN 13 THEN 'German Auto Parts'
        WHEN 14 THEN 'Japanese Electronics'
        WHEN 15 THEN 'Korean Steel Industries'
        WHEN 16 THEN 'Italian Fashion House'
        WHEN 17 THEN 'French Cosmetics SA'
        WHEN 18 THEN 'British Pharma Ltd'
        ELSE 'Australian Mining Corp'
    END || ' ' || generate_series,
    CASE (generate_series % 20)
        WHEN 0 THEN 'Emirates Trading LLC'
        WHEN 1 THEN 'Gulf Imports & Exports LLC'
        WHEN 2 THEN 'Middle East Commodities FZ'
        WHEN 3 THEN 'Asian Textiles Company Pvt Ltd'
        WHEN 4 THEN 'European Electronics GmbH'
        WHEN 5 THEN 'American Machinery Incorporated'
        WHEN 6 THEN 'Dubai Trading House DMCC'
        WHEN 7 THEN 'Singapore Shipping Company Pte Ltd'
        WHEN 8 THEN 'Hong Kong Logistics Limited'
        WHEN 9 THEN 'Saudi Petrochemicals Company'
        WHEN 10 THEN 'Indian Spices Export Pvt Ltd'
        WHEN 11 THEN 'Chinese Manufacturing Company Ltd'
        WHEN 12 THEN 'Turkish Textiles Limited'
        WHEN 13 THEN 'German Auto Parts GmbH'
        WHEN 14 THEN 'Japanese Electronics Corporation'
        WHEN 15 THEN 'Korean Steel Industries Inc'
        WHEN 16 THEN 'Italian Fashion House SRL'
        WHEN 17 THEN 'French Cosmetics SA'
        WHEN 18 THEN 'British Pharmaceutical Ltd'
        ELSE 'Australian Mining Corporation'
    END || ' ' || generate_series,
    CASE (generate_series % 5)
        WHEN 0 THEN 'IMPORTER'
        WHEN 1 THEN 'EXPORTER'
        WHEN 2 THEN 'BUYER'
        WHEN 3 THEN 'SELLER'
        ELSE 'FREIGHT_FORWARDER'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'CORPORATE'
        WHEN 1 THEN 'SME'
        ELSE 'ENTERPRISE'
    END,
    CASE (generate_series % 15)
        WHEN 0 THEN 'Manufacturing'
        WHEN 1 THEN 'Trading'
        WHEN 2 THEN 'Logistics'
        WHEN 3 THEN 'Textiles'
        WHEN 4 THEN 'Electronics'
        WHEN 5 THEN 'Automotive'
        WHEN 6 THEN 'Food & Beverage'
        WHEN 7 THEN 'Petrochemicals'
        WHEN 8 THEN 'Pharmaceuticals'
        WHEN 9 THEN 'Construction'
        WHEN 10 THEN 'Agriculture'
        WHEN 11 THEN 'Mining'
        WHEN 12 THEN 'Technology'
        WHEN 13 THEN 'Fashion'
        ELSE 'Cosmetics'
    END,
    'customer' || generate_series || '@company.com',
    '+971-4-' || (3000000 + generate_series),
    CASE (generate_series % 10)
        WHEN 0 THEN 'Dubai'
        WHEN 1 THEN 'Abu Dhabi'
        WHEN 2 THEN 'Singapore'
        WHEN 3 THEN 'Hong Kong'
        WHEN 4 THEN 'Shanghai'
        WHEN 5 THEN 'Mumbai'
        WHEN 6 THEN 'London'
        WHEN 7 THEN 'New York'
        WHEN 8 THEN 'Tokyo'
        ELSE 'Frankfurt'
    END,
    CASE (generate_series % 10)
        WHEN 0 THEN 'AE'
        WHEN 1 THEN 'AE'
        WHEN 2 THEN 'SG'
        WHEN 3 THEN 'HK'
        WHEN 4 THEN 'CN'
        WHEN 5 THEN 'IN'
        WHEN 6 THEN 'GB'
        WHEN 7 THEN 'US'
        WHEN 8 THEN 'JP'
        ELSE 'DE'
    END,
    CASE (generate_series % 4)
        WHEN 0 THEN 'COMPLETED'
        WHEN 1 THEN 'PENDING'
        WHEN 2 THEN 'IN_REVIEW'
        ELSE 'APPROVED'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'CLEAR'
        WHEN 1 THEN 'PENDING'
        ELSE 'APPROVED'
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'LOW'
        WHEN 1 THEN 'MEDIUM'
        WHEN 2 THEN 'MEDIUM_HIGH'
        WHEN 3 THEN 'HIGH'
        ELSE 'VERY_HIGH'
    END,
    (30 + (generate_series % 70))::numeric(5,2),
    (1000000 + (generate_series * 50000))::numeric(20,2),
    'USD',
    (800000 + (generate_series * 40000))::numeric(20,2),
    CASE (generate_series % 6)
        WHEN 0 THEN 'ACTIVE'
        WHEN 1 THEN 'ACTIVE'
        WHEN 2 THEN 'ACTIVE'
        WHEN 3 THEN 'ACTIVE'
        WHEN 4 THEN 'PENDING_APPROVAL'
        ELSE 'DRAFT'
    END,
    CURRENT_DATE - (generate_series * 3 || ' days')::interval,
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    '00000000-0000-0000-0000-000000000001'
FROM generate_series(1, 500);

-- ==============================================
-- CUSTOMER CONTACTS DATA (1000+ contacts)
-- ==============================================

INSERT INTO customer_contacts (
    tenant_id, customer_id, contact_type, first_name, last_name, title,
    department, email, phone, mobile, is_primary, is_active,
    created_at, created_by, updated_at, updated_by
)
SELECT
    '00000000-0000-0000-0000-000000000001',
    c.id,
    CASE (ROW_NUMBER() OVER (PARTITION BY c.id) % 4)
        WHEN 1 THEN 'PRIMARY'
        WHEN 2 THEN 'BILLING'
        WHEN 3 THEN 'OPERATIONS'
        ELSE 'TECHNICAL'
    END,
    CASE ((ROW_NUMBER() OVER ()) % 10)
        WHEN 0 THEN 'John'
        WHEN 1 THEN 'Sarah'
        WHEN 2 THEN 'Michael'
        WHEN 3 THEN 'Emily'
        WHEN 4 THEN 'David'
        WHEN 5 THEN 'Jennifer'
        WHEN 6 THEN 'Robert'
        WHEN 7 THEN 'Maria'
        WHEN 8 THEN 'William'
        ELSE 'Lisa'
    END,
    CASE ((ROW_NUMBER() OVER ()) % 10)
        WHEN 0 THEN 'Smith'
        WHEN 1 THEN 'Johnson'
        WHEN 2 THEN 'Williams'
        WHEN 3 THEN 'Brown'
        WHEN 4 THEN 'Jones'
        WHEN 5 THEN 'Garcia'
        WHEN 6 THEN 'Miller'
        WHEN 7 THEN 'Davis'
        WHEN 8 THEN 'Rodriguez'
        ELSE 'Martinez'
    END,
    CASE ((ROW_NUMBER() OVER ()) % 5)
        WHEN 0 THEN 'CEO'
        WHEN 1 THEN 'CFO'
        WHEN 2 THEN 'Trade Manager'
        WHEN 3 THEN 'Finance Director'
        ELSE 'Operations Manager'
    END,
    CASE ((ROW_NUMBER() OVER ()) % 4)
        WHEN 0 THEN 'Finance'
        WHEN 1 THEN 'Operations'
        WHEN 2 THEN 'Trade'
        ELSE 'Administration'
    END,
    LOWER(CASE ((ROW_NUMBER() OVER ()) % 10)
        WHEN 0 THEN 'john.smith'
        WHEN 1 THEN 'sarah.johnson'
        WHEN 2 THEN 'michael.williams'
        WHEN 3 THEN 'emily.brown'
        WHEN 4 THEN 'david.jones'
        WHEN 5 THEN 'jennifer.garcia'
        WHEN 6 THEN 'robert.miller'
        WHEN 7 THEN 'maria.davis'
        WHEN 8 THEN 'william.rodriguez'
        ELSE 'lisa.martinez'
    END) || ROW_NUMBER() OVER () || '@company.com',
    '+971-4-' || (4000000 + (ROW_NUMBER() OVER ())),
    '+971-50-' || (1000000 + (ROW_NUMBER() OVER ())),
    (ROW_NUMBER() OVER (PARTITION BY c.id)) = 1,
    TRUE,
    CURRENT_TIMESTAMP,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP,
    '00000000-0000-0000-0000-000000000001'
FROM customers c
CROSS JOIN generate_series(1, 2) gs
LIMIT 1000;

-- ==============================================
-- LETTERS OF CREDIT DATA (300+ LCs)
-- ==============================================

INSERT INTO letters_of_credit (
    tenant_id, lc_number, lc_type, lc_category,
    applicant_id, beneficiary_id,
    lc_amount, lc_currency, issue_date, expiry_date, latest_shipment_date,
    payment_terms, incoterms, port_of_loading, port_of_discharge,
    goods_description, status, workflow_state,
    created_at, created_by, updated_at, updated_by
)
SELECT
    '00000000-0000-0000-0000-000000000001',
    'LC' || TO_CHAR(CURRENT_DATE, 'YYYY') || LPAD(generate_series::text, 6, '0'),
    CASE (generate_series % 5)
        WHEN 0 THEN 'SIGHT'
        WHEN 1 THEN 'USANCE'
        WHEN 2 THEN 'DEFERRED_PAYMENT'
        WHEN 3 THEN 'ACCEPTANCE'
        ELSE 'NEGOTIATION'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'DOCUMENTARY'
        WHEN 1 THEN 'STANDBY'
        ELSE 'REVOLVING'
    END,
    (SELECT id FROM customers WHERE customer_type IN ('IMPORTER', 'BUYER') ORDER BY RANDOM() LIMIT 1),
    (SELECT id FROM customers WHERE customer_type IN ('EXPORTER', 'SELLER') ORDER BY RANDOM() LIMIT 1),
    (100000 + (generate_series * 25000))::numeric(20,2),
    CASE (generate_series % 4)
        WHEN 0 THEN 'USD'
        WHEN 1 THEN 'EUR'
        WHEN 2 THEN 'GBP'
        ELSE 'AED'
    END,
    CURRENT_DATE - (365 - generate_series || ' days')::interval,
    CURRENT_DATE + (generate_series * 2 || ' days')::interval,
    CURRENT_DATE + ((generate_series * 2) - 21 || ' days')::interval,
    CASE (generate_series % 4)
        WHEN 0 THEN 'SIGHT'
        WHEN 1 THEN 'USANCE_30'
        WHEN 2 THEN 'USANCE_60'
        ELSE 'USANCE_90'
    END,
    CASE (generate_series % 8)
        WHEN 0 THEN 'FOB'
        WHEN 1 THEN 'CIF'
        WHEN 2 THEN 'CFR'
        WHEN 3 THEN 'EXW'
        WHEN 4 THEN 'FCA'
        WHEN 5 THEN 'CPT'
        WHEN 6 THEN 'CIP'
        ELSE 'DAP'
    END,
    CASE (generate_series % 10)
        WHEN 0 THEN 'Shanghai Port'
        WHEN 1 THEN 'Singapore Port'
        WHEN 2 THEN 'Hong Kong Port'
        WHEN 3 THEN 'Mumbai Port'
        WHEN 4 THEN 'Rotterdam Port'
        WHEN 5 THEN 'Hamburg Port'
        WHEN 6 THEN 'Los Angeles Port'
        WHEN 7 THEN 'Dubai Port'
        WHEN 8 THEN 'Abu Dhabi Port'
        ELSE 'Tokyo Port'
    END,
    CASE (generate_series % 10)
        WHEN 0 THEN 'Jebel Ali Port, Dubai'
        WHEN 1 THEN 'Port Rashid, Dubai'
        WHEN 2 THEN 'Abu Dhabi Port'
        WHEN 3 THEN 'Singapore Port'
        WHEN 4 THEN 'Hong Kong Port'
        WHEN 5 THEN 'Rotterdam Port'
        WHEN 6 THEN 'Hamburg Port'
        WHEN 7 THEN 'New York Port'
        WHEN 8 THEN 'Mumbai Port'
        ELSE 'Shanghai Port'
    END,
    CASE (generate_series % 15)
        WHEN 0 THEN 'Electronics goods - laptops, mobile phones, accessories'
        WHEN 1 THEN 'Textile products - cotton fabrics, garments'
        WHEN 2 THEN 'Machinery parts - industrial equipment'
        WHEN 3 THEN 'Automotive spare parts'
        WHEN 4 THEN 'Construction materials - steel, cement'
        WHEN 5 THEN 'Food products - dates, spices, rice'
        WHEN 6 THEN 'Petrochemical products'
        WHEN 7 THEN 'Pharmaceutical products'
        WHEN 8 THEN 'Fashion accessories and jewelry'
        WHEN 9 THEN 'Furniture and home decor'
        WHEN 10 THEN 'Agricultural products'
        WHEN 11 THEN 'Mining equipment'
        WHEN 12 THEN 'Technology hardware'
        WHEN 13 THEN 'Cosmetics and beauty products'
        ELSE 'General trading goods'
    END,
    CASE (generate_series % 7)
        WHEN 0 THEN 'DRAFT'
        WHEN 1 THEN 'PENDING_APPROVAL'
        WHEN 2 THEN 'APPROVED'
        WHEN 3 THEN 'ISSUED'
        WHEN 4 THEN 'ACTIVE'
        WHEN 5 THEN 'DOCUMENTS_SUBMITTED'
        ELSE 'COMPLETED'
    END,
    CASE (generate_series % 7)
        WHEN 0 THEN 'DRAFT'
        WHEN 1 THEN 'PENDING_APPROVAL'
        WHEN 2 THEN 'APPROVED'
        WHEN 3 THEN 'ISSUED'
        WHEN 4 THEN 'ACTIVE'
        WHEN 5 THEN 'UNDER_REVIEW'
        ELSE 'COMPLETED'
    END,
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    '00000000-0000-0000-0000-000000000001'
FROM generate_series(1, 300);

-- ==============================================
-- DOCUMENTS DATA (1000+ documents)
-- ==============================================

INSERT INTO documents (
    tenant_id, document_number, document_type, document_category,
    entity_type, entity_id, title, file_name, file_size, content_type,
    storage_provider, storage_bucket, storage_key,
    extraction_status, classification, status,
    created_at, created_by, updated_at, updated_by
)
SELECT
    '00000000-0000-0000-0000-000000000001',
    'DOC' || TO_CHAR(CURRENT_DATE, 'YYYYMMDD') || LPAD(generate_series::text, 6, '0'),
    CASE (generate_series % 12)
        WHEN 0 THEN 'COMMERCIAL_INVOICE'
        WHEN 1 THEN 'PACKING_LIST'
        WHEN 2 THEN 'BILL_OF_LADING'
        WHEN 3 THEN 'CERTIFICATE_OF_ORIGIN'
        WHEN 4 THEN 'INSURANCE_CERTIFICATE'
        WHEN 5 THEN 'INSPECTION_CERTIFICATE'
        WHEN 6 THEN 'TRADE_LICENSE'
        WHEN 7 THEN 'PASSPORT'
        WHEN 8 THEN 'BANK_STATEMENT'
        WHEN 9 THEN 'LC_APPLICATION'
        WHEN 10 THEN 'AMENDMENT_REQUEST'
        ELSE 'SHIPPING_INSTRUCTION'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'TRADE_DOCUMENT'
        WHEN 1 THEN 'KYC_DOCUMENT'
        ELSE 'COMPLIANCE_DOCUMENT'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'CUSTOMER'
        WHEN 1 THEN 'LC'
        ELSE 'COMPLIANCE_CHECK'
    END,
    (SELECT id FROM customers ORDER BY RANDOM() LIMIT 1),
    CASE (generate_series % 12)
        WHEN 0 THEN 'Commercial Invoice #'
        WHEN 1 THEN 'Packing List #'
        WHEN 2 THEN 'Bill of Lading #'
        WHEN 3 THEN 'Certificate of Origin #'
        WHEN 4 THEN 'Insurance Certificate #'
        WHEN 5 THEN 'Inspection Certificate #'
        WHEN 6 THEN 'Trade License #'
        WHEN 7 THEN 'Passport Copy #'
        WHEN 8 THEN 'Bank Statement #'
        WHEN 9 THEN 'LC Application #'
        WHEN 10 THEN 'Amendment Request #'
        ELSE 'Shipping Instruction #'
    END || generate_series,
    CASE (generate_series % 12)
        WHEN 0 THEN 'commercial_invoice_'
        WHEN 1 THEN 'packing_list_'
        WHEN 2 THEN 'bill_of_lading_'
        WHEN 3 THEN 'certificate_origin_'
        WHEN 4 THEN 'insurance_cert_'
        WHEN 5 THEN 'inspection_cert_'
        WHEN 6 THEN 'trade_license_'
        WHEN 7 THEN 'passport_'
        WHEN 8 THEN 'bank_statement_'
        WHEN 9 THEN 'lc_application_'
        WHEN 10 THEN 'amendment_request_'
        ELSE 'shipping_instruction_'
    END || generate_series || '.pdf',
    (512000 + (generate_series * 1000))::bigint,
    'application/pdf',
    'MINIO',
    'tradesphere-documents',
    'documents/' || TO_CHAR(CURRENT_DATE, 'YYYY/MM/DD') || '/' || gen_random_uuid() || '.pdf',
    CASE (generate_series % 4)
        WHEN 0 THEN 'COMPLETED'
        WHEN 1 THEN 'PENDING'
        WHEN 2 THEN 'IN_PROGRESS'
        ELSE 'FAILED'
    END,
    CASE (generate_series % 12)
        WHEN 0 THEN 'INVOICE'
        WHEN 1 THEN 'PACKING_LIST'
        WHEN 2 THEN 'TRANSPORT'
        WHEN 3 THEN 'ORIGIN'
        WHEN 4 THEN 'INSURANCE'
        WHEN 5 THEN 'INSPECTION'
        WHEN 6 THEN 'LICENSE'
        WHEN 7 THEN 'IDENTITY'
        WHEN 8 THEN 'FINANCIAL'
        WHEN 9 THEN 'APPLICATION'
        WHEN 10 THEN 'AMENDMENT'
        ELSE 'INSTRUCTION'
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'ACTIVE'
        WHEN 1 THEN 'ACTIVE'
        WHEN 2 THEN 'ACTIVE'
        WHEN 3 THEN 'PENDING_REVIEW'
        ELSE 'ARCHIVED'
    END,
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    '00000000-0000-0000-0000-000000000001'
FROM generate_series(1, 1000);

-- ==============================================
-- COMPLIANCE CHECKS DATA (500+ checks)
-- ==============================================

INSERT INTO compliance_checks (
    tenant_id, check_number, entity_type, entity_id,
    check_type, screening_provider, check_status, risk_level,
    sanctions_hit, pep_hit, adverse_media_hit, watchlist_hits, score,
    checked_at, checked_by, created_at, created_by, updated_at, updated_by
)
SELECT
    '00000000-0000-0000-0000-000000000001',
    'CHK' || TO_CHAR(CURRENT_DATE, 'YYYYMMDD') || LPAD(generate_series::text, 6, '0'),
    CASE (generate_series % 2)
        WHEN 0 THEN 'CUSTOMER'
        ELSE 'LC'
    END,
    CASE (generate_series % 2)
        WHEN 0 THEN (SELECT id FROM customers ORDER BY RANDOM() LIMIT 1)
        ELSE (SELECT id FROM letters_of_credit ORDER BY RANDOM() LIMIT 1)
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'SANCTIONS_SCREENING'
        WHEN 1 THEN 'PEP_SCREENING'
        WHEN 2 THEN 'ADVERSE_MEDIA'
        WHEN 3 THEN 'WATCHLIST_CHECK'
        ELSE 'FULL_COMPLIANCE'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'WORLD_CHECK'
        WHEN 1 THEN 'DOW_JONES'
        ELSE 'REFINITIV'
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'COMPLETED'
        WHEN 1 THEN 'COMPLETED'
        WHEN 2 THEN 'COMPLETED'
        WHEN 3 THEN 'PENDING_REVIEW'
        ELSE 'IN_PROGRESS'
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'LOW'
        WHEN 1 THEN 'LOW'
        WHEN 2 THEN 'MEDIUM'
        WHEN 3 THEN 'HIGH'
        ELSE 'VERY_HIGH'
    END,
    (generate_series % 20) = 0,
    (generate_series % 30) = 0,
    (generate_series % 15) = 0,
    CASE
        WHEN (generate_series % 20) = 0 THEN (1 + (generate_series % 3))
        ELSE 0
    END,
    (20 + (generate_series % 80))::numeric(5,2),
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    (SELECT id FROM users ORDER BY RANDOM() LIMIT 1),
    CURRENT_TIMESTAMP - ((generate_series + 1) || ' hours')::interval,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    '00000000-0000-0000-0000-000000000001'
FROM generate_series(1, 500);

-- ==============================================
-- RISK ASSESSMENTS DATA (500+ assessments)
-- ==============================================

INSERT INTO risk_assessments (
    tenant_id, assessment_number, entity_type, entity_id,
    assessment_type, assessment_model, risk_score, risk_rating, risk_category,
    assessment_status, assessed_by, assessed_at,
    created_at, created_by, updated_at, updated_by
)
SELECT
    '00000000-0000-0000-0000-000000000001',
    'RISK' || TO_CHAR(CURRENT_DATE, 'YYYYMMDD') || LPAD(generate_series::text, 6, '0'),
    CASE (generate_series % 2)
        WHEN 0 THEN 'CUSTOMER'
        ELSE 'LC'
    END,
    CASE (generate_series % 2)
        WHEN 0 THEN (SELECT id FROM customers ORDER BY RANDOM() LIMIT 1)
        ELSE (SELECT id FROM letters_of_credit ORDER BY RANDOM() LIMIT 1)
    END,
    CASE (generate_series % 4)
        WHEN 0 THEN 'CREDIT_RISK'
        WHEN 1 THEN 'COUNTRY_RISK'
        WHEN 2 THEN 'OPERATIONAL_RISK'
        ELSE 'COMPREHENSIVE'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'STANDARD'
        WHEN 1 THEN 'ADVANCED'
        ELSE 'AI_ENHANCED'
    END,
    (25 + (generate_series % 75))::numeric(5,2),
    CASE
        WHEN (25 + (generate_series % 75)) < 40 THEN 'LOW'
        WHEN (25 + (generate_series % 75)) < 60 THEN 'MEDIUM'
        WHEN (25 + (generate_series % 75)) < 80 THEN 'HIGH'
        ELSE 'VERY_HIGH'
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'CREDIT'
        WHEN 1 THEN 'COUNTRY'
        WHEN 2 THEN 'OPERATIONAL'
        WHEN 3 THEN 'COMPLIANCE'
        ELSE 'MARKET'
    END,
    CASE (generate_series % 4)
        WHEN 0 THEN 'COMPLETED'
        WHEN 1 THEN 'COMPLETED'
        WHEN 2 THEN 'PENDING_APPROVAL'
        ELSE 'DRAFT'
    END,
    (SELECT id FROM users ORDER BY RANDOM() LIMIT 1),
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    CURRENT_TIMESTAMP - ((generate_series + 2) || ' hours')::interval,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    '00000000-0000-0000-0000-000000000001'
FROM generate_series(1, 500);

-- ==============================================
-- WORKFLOWS DATA (400+ workflows)
-- ==============================================

INSERT INTO workflows (
    tenant_id, workflow_name, workflow_code, workflow_type, entity_type, entity_id,
    current_step, current_step_index, total_steps, workflow_status, priority,
    assigned_to, started_at, due_date,
    created_at, created_by, updated_at, updated_by
)
SELECT
    '00000000-0000-0000-0000-000000000001',
    CASE (generate_series % 5)
        WHEN 0 THEN 'LC Approval Workflow'
        WHEN 1 THEN 'Customer Onboarding'
        WHEN 2 THEN 'Document Verification'
        WHEN 3 THEN 'Compliance Review'
        ELSE 'Risk Assessment'
    END,
    'WF' || LPAD(generate_series::text, 6, '0'),
    CASE (generate_series % 5)
        WHEN 0 THEN 'LC_APPROVAL'
        WHEN 1 THEN 'CUSTOMER_ONBOARDING'
        WHEN 2 THEN 'DOCUMENT_VERIFICATION'
        WHEN 3 THEN 'COMPLIANCE_REVIEW'
        ELSE 'RISK_ASSESSMENT'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'LC'
        WHEN 1 THEN 'CUSTOMER'
        ELSE 'DOCUMENT'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN (SELECT id FROM letters_of_credit ORDER BY RANDOM() LIMIT 1)
        WHEN 1 THEN (SELECT id FROM customers ORDER BY RANDOM() LIMIT 1)
        ELSE (SELECT id FROM documents ORDER BY RANDOM() LIMIT 1)
    END,
    CASE (generate_series % 4)
        WHEN 0 THEN 'Maker Review'
        WHEN 1 THEN 'Checker Review'
        WHEN 2 THEN 'Approver Review'
        ELSE 'Final Approval'
    END,
    (1 + (generate_series % 3)),
    4,
    CASE (generate_series % 5)
        WHEN 0 THEN 'PENDING'
        WHEN 1 THEN 'IN_PROGRESS'
        WHEN 2 THEN 'IN_PROGRESS'
        WHEN 3 THEN 'COMPLETED'
        ELSE 'ON_HOLD'
    END,
    CASE (generate_series % 4)
        WHEN 0 THEN 'NORMAL'
        WHEN 1 THEN 'NORMAL'
        WHEN 2 THEN 'HIGH'
        ELSE 'URGENT'
    END,
    (SELECT id FROM users ORDER BY RANDOM() LIMIT 1),
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    CURRENT_TIMESTAMP + ((72 - generate_series) || ' hours')::interval,
    CURRENT_TIMESTAMP - ((generate_series + 1) || ' hours')::interval,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    '00000000-0000-0000-0000-000000000001'
FROM generate_series(1, 400);

-- ==============================================
-- AUDIT LOGS DATA (2000+ audit entries)
-- ==============================================

INSERT INTO audit_logs (
    tenant_id, entity_type, entity_id, action, user_id, username,
    occurred_at
)
SELECT
    '00000000-0000-0000-0000-000000000001',
    CASE (generate_series % 6)
        WHEN 0 THEN 'CUSTOMER'
        WHEN 1 THEN 'LC'
        WHEN 2 THEN 'DOCUMENT'
        WHEN 3 THEN 'USER'
        WHEN 4 THEN 'COMPLIANCE_CHECK'
        ELSE 'RISK_ASSESSMENT'
    END,
    gen_random_uuid()::text,
    CASE (generate_series % 10)
        WHEN 0 THEN 'CREATE'
        WHEN 1 THEN 'UPDATE'
        WHEN 2 THEN 'UPDATE'
        WHEN 3 THEN 'UPDATE'
        WHEN 4 THEN 'READ'
        WHEN 5 THEN 'READ'
        WHEN 6 THEN 'APPROVE'
        WHEN 7 THEN 'REJECT'
        WHEN 8 THEN 'SUBMIT'
        ELSE 'DELETE'
    END,
    (SELECT id FROM users ORDER BY RANDOM() LIMIT 1),
    (SELECT username FROM users ORDER BY RANDOM() LIMIT 1),
    CURRENT_TIMESTAMP - (generate_series || ' minutes')::interval
FROM generate_series(1, 2000);

COMMIT;

