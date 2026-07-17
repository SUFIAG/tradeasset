-- Customer Management Schema Tables
-- Version: 1.0.0
-- Description: Create customer management tables

-- Customers Table
CREATE TABLE customer.customers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,

    -- Identification
    customer_number VARCHAR(50) NOT NULL,

    -- Basic Info
    name VARCHAR(200) NOT NULL,
    legal_name VARCHAR(300) NOT NULL,
    customer_type VARCHAR(50) NOT NULL,
    customer_category VARCHAR(50),

    -- Business Details
    industry VARCHAR(100),
    business_registration_number VARCHAR(100),
    tax_id VARCHAR(100),
    incorporation_date DATE,
    incorporation_country VARCHAR(2),

    -- Contact
    email VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    fax VARCHAR(20),
    website VARCHAR(255),

    -- Primary Address
    address_line1 VARCHAR(200),
    address_line2 VARCHAR(200),
    city VARCHAR(100),
    state VARCHAR(100),
    country VARCHAR(2),
    postal_code VARCHAR(20),

    -- Banking Details
    primary_bank_name VARCHAR(200),
    primary_account_number VARCHAR(50),
    swift_code VARCHAR(11),

    -- KYC
    kyc_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    kyc_completed_at TIMESTAMP WITH TIME ZONE,
    kyc_expiry_date DATE,
    kyc_next_review_date DATE,

    -- Compliance
    compliance_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    last_compliance_check_at TIMESTAMP WITH TIME ZONE,
    next_compliance_check_at TIMESTAMP WITH TIME ZONE,
    pep_status BOOLEAN DEFAULT FALSE,
    sanctions_hit BOOLEAN DEFAULT FALSE,
    high_risk_country BOOLEAN DEFAULT FALSE,

    -- Risk
    risk_rating VARCHAR(20),
    risk_score NUMERIC(5,2),
    risk_assessed_at TIMESTAMP WITH TIME ZONE,
    risk_next_review_date DATE,

    -- Credit
    credit_limit_amount NUMERIC(20,2),
    credit_limit_currency VARCHAR(3) DEFAULT 'USD',
    available_credit_amount NUMERIC(20,2),
    available_credit_currency VARCHAR(3),
    credit_approved_by UUID,
    credit_approved_at TIMESTAMP WITH TIME ZONE,

    -- Relationship
    relationship_manager_id UUID,
    customer_since DATE,

    -- Status
    status VARCHAR(20) NOT NULL DEFAULT 'DRAFT',
    approved_by UUID,
    approved_at TIMESTAMP WITH TIME ZONE,

    -- Notes
    notes TEXT,

    -- Standard Audit Fields
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE,
    updated_by UUID,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,

    CONSTRAINT uk_customers_customer_number UNIQUE (customer_number, tenant_id, deleted_at),
    CONSTRAINT fk_customers_tenant FOREIGN KEY (tenant_id) REFERENCES organization.tenants(id),
    CONSTRAINT fk_customers_relationship_manager FOREIGN KEY (relationship_manager_id) REFERENCES identity.users(id),
    CONSTRAINT fk_customers_approved_by FOREIGN KEY (approved_by) REFERENCES identity.users(id)
);

CREATE INDEX idx_customers_tenant_id ON customer.customers(tenant_id);
CREATE INDEX idx_customers_customer_number ON customer.customers(customer_number);
CREATE INDEX idx_customers_status ON customer.customers(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_customers_type ON customer.customers(type);
CREATE INDEX idx_customers_risk_rating ON customer.customers(risk_rating);
CREATE INDEX idx_customers_kyc_status ON customer.customers(kyc_status);
CREATE INDEX idx_customers_compliance_status ON customer.customers(compliance_status);
CREATE INDEX idx_customers_name ON customer.customers USING gin(to_tsvector('english', name));

COMMENT ON TABLE customer.customers IS 'Customer master data';
COMMENT ON COLUMN customer.customers.type IS 'IMPORTER, EXPORTER, BUYER, SELLER, BANK, BENEFICIARY, FREIGHT_FORWARDER';
COMMENT ON COLUMN customer.customers.status IS 'DRAFT, PENDING_APPROVAL, APPROVED, ACTIVE, SUSPENDED, INACTIVE, CLOSED';

-- KYC Documents Table
CREATE TABLE customer.kyc_documents (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    customer_id UUID NOT NULL,

    -- Document Info
    document_type VARCHAR(50) NOT NULL,
    document_number VARCHAR(100),
    issuing_authority VARCHAR(200),
    issuing_country VARCHAR(2),
    issue_date DATE,
    expiry_date DATE,

    -- Storage
    storage_key VARCHAR(500) NOT NULL,
    file_name VARCHAR(255) NOT NULL,
    file_size BIGINT,
    content_type VARCHAR(100),

    -- Verification
    verification_status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    verified_by UUID,
    verified_at TIMESTAMP WITH TIME ZONE,
    verification_notes TEXT,

    -- Standard Audit Fields
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,

    CONSTRAINT fk_kyc_documents_customer FOREIGN KEY (customer_id) REFERENCES customer.customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_kyc_documents_verified_by FOREIGN KEY (verified_by) REFERENCES identity.users(id)
);

CREATE INDEX idx_kyc_documents_customer_id ON customer.kyc_documents(customer_id);
CREATE INDEX idx_kyc_documents_document_type ON customer.kyc_documents(document_type);
CREATE INDEX idx_kyc_documents_status ON customer.kyc_documents(verification_status);

COMMENT ON TABLE customer.kyc_documents IS 'Customer KYC documents';
COMMENT ON COLUMN customer.kyc_documents.document_type IS 'TRADE_LICENSE, INCORPORATION_CERTIFICATE, BOARD_RESOLUTION, PASSPORT, etc.';

-- Customer Relationships Table
CREATE TABLE customer.customer_relationships (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,

    customer_id UUID NOT NULL,
    related_customer_id UUID NOT NULL,

    relationship_type VARCHAR(50) NOT NULL,
    relationship_nature TEXT,

    -- Validity
    valid_from DATE NOT NULL DEFAULT CURRENT_DATE,
    valid_until DATE,

    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Standard Audit Fields
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,

    CONSTRAINT uk_customer_relationships UNIQUE (customer_id, related_customer_id, relationship_type),
    CONSTRAINT fk_customer_relationships_customer FOREIGN KEY (customer_id) REFERENCES customer.customers(id) ON DELETE CASCADE,
    CONSTRAINT fk_customer_relationships_related FOREIGN KEY (related_customer_id) REFERENCES customer.customers(id) ON DELETE CASCADE,
    CONSTRAINT chk_customer_relationships_different CHECK (customer_id != related_customer_id)
);

CREATE INDEX idx_customer_relationships_customer_id ON customer.customer_relationships(customer_id);
CREATE INDEX idx_customer_relationships_related_id ON customer.customer_relationships(related_customer_id);
CREATE INDEX idx_customer_relationships_type ON customer.customer_relationships(relationship_type);

COMMENT ON TABLE customer.customer_relationships IS 'Customer-to-customer relationships';
COMMENT ON COLUMN customer.customer_relationships.relationship_type IS 'BUYER_SELLER, PARENT_SUBSIDIARY, AFFILIATED, CORRESPONDENT_BANK';

-- Customer Contacts Table
CREATE TABLE customer.customer_contacts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    customer_id UUID NOT NULL,

    -- Contact Info
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

    -- Standard Audit Fields
    version INTEGER NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,

    CONSTRAINT fk_customer_contacts_customer FOREIGN KEY (customer_id) REFERENCES customer.customers(id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_contacts_customer_id ON customer.customer_contacts(customer_id);
CREATE INDEX idx_customer_contacts_email ON customer.customer_contacts(email);
CREATE INDEX idx_customer_contacts_is_primary ON customer.customer_contacts(customer_id, is_primary) WHERE is_primary = TRUE;

COMMENT ON TABLE customer.customer_contacts IS 'Customer contact persons';
COMMENT ON COLUMN customer.customer_contacts.contact_type IS 'PRIMARY, BILLING, OPERATIONS, TECHNICAL';

-- Customer Addresses Table
CREATE TABLE customer.customer_addresses (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tenant_id UUID NOT NULL,
    customer_id UUID NOT NULL,

    -- Address Type
    address_type VARCHAR(20) NOT NULL,

    -- Address Details
    address_line1 VARCHAR(200) NOT NULL,
    address_line2 VARCHAR(200),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100),
    country VARCHAR(2) NOT NULL,
    postal_code VARCHAR(20),

    is_primary BOOLEAN NOT NULL DEFAULT FALSE,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,

    -- Standard Audit Fields
    version BIGINT NOT NULL DEFAULT 1,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by UUID NOT NULL,
    deleted_at TIMESTAMP WITH TIME ZONE,
    deleted_by UUID,

    CONSTRAINT fk_customer_addresses_customer FOREIGN KEY (customer_id) REFERENCES customer.customers(id) ON DELETE CASCADE
);

CREATE INDEX idx_customer_addresses_customer_id ON customer.customer_addresses(customer_id);
CREATE INDEX idx_customer_addresses_type ON customer.customer_addresses(address_type);
CREATE INDEX idx_customer_addresses_country ON customer.customer_addresses(country);

COMMENT ON TABLE customer.customer_addresses IS 'Customer multiple addresses';
COMMENT ON COLUMN customer.customer_addresses.address_type IS 'REGISTERED, BUSINESS, MAILING, SHIPPING';

COMMIT;

