-- Populate All Missing Tables with Sample Data
-- Version: 12.0.0
-- Description: Add comprehensive test data for all tables
-- Date: 2026-07-24

-- ==============================================
-- ANALYTICS REPORTS DATA
-- ==============================================

INSERT INTO analytics_reports (
    id, tenant_id, report_name, report_type, status,
    start_date, end_date, generated_by, generated_at,
    computation_time_ms, cache_expires_at, cached,
    created_by, created_at, updated_by, updated_at, version
)
SELECT
    gen_random_uuid(),
    '00000000-0000-0000-0000-000000000001',
    CASE (generate_series % 10)
        WHEN 0 THEN 'Daily Trade Volume Report'
        WHEN 1 THEN 'Monthly LC Performance'
        WHEN 2 THEN 'Customer Risk Analysis'
        WHEN 3 THEN 'Compliance Dashboard'
        WHEN 4 THEN 'Revenue Analysis'
        WHEN 5 THEN 'Document Processing Stats'
        WHEN 6 THEN 'Workflow Efficiency Report'
        WHEN 7 THEN 'Branch Performance'
        WHEN 8 THEN 'Risk Assessment Summary'
        ELSE 'Quarterly Business Review'
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'TRADE_VOLUME'
        WHEN 1 THEN 'PERFORMANCE'
        WHEN 2 THEN 'RISK_ANALYSIS'
        WHEN 3 THEN 'COMPLIANCE'
        ELSE 'FINANCIAL'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'COMPLETED'
        WHEN 1 THEN 'IN_PROGRESS'
        ELSE 'CACHED'
    END,
    CURRENT_DATE - (generate_series * 7 || ' days')::interval,
    CURRENT_DATE - ((generate_series * 7) - 7 || ' days')::interval,
    'admin',
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    (500 + (generate_series * 100))::bigint,
    CURRENT_TIMESTAMP + ((24 - generate_series) || ' hours')::interval,
    (generate_series % 3) = 0,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    1
FROM generate_series(1, 50);

-- ==============================================
-- ANALYTICS REPORT PARAMETERS DATA
-- ==============================================

INSERT INTO analytics_report_parameters (report_id, param_key, param_value)
SELECT
    r.id,
    CASE (gs % 5)
        WHEN 0 THEN 'currency'
        WHEN 1 THEN 'branch_id'
        WHEN 2 THEN 'customer_type'
        WHEN 3 THEN 'min_amount'
        ELSE 'max_amount'
    END,
    CASE (gs % 5)
        WHEN 0 THEN 'USD'
        WHEN 1 THEN '20000000-0000-0000-0000-000000000001'
        WHEN 2 THEN 'CORPORATE'
        WHEN 3 THEN '10000'
        ELSE '1000000'
    END
FROM analytics_reports r
CROSS JOIN generate_series(1, 3) gs;

-- ==============================================
-- ANALYTICS METRICS DATA
-- ==============================================

INSERT INTO analytics_metrics (
    id, report_id, metric_name, metric_value, metric_unit,
    metric_type, timestamp
)
SELECT
    gen_random_uuid(),
    r.id,
    CASE (gs % 12)
        WHEN 0 THEN 'total_lc_count'
        WHEN 1 THEN 'total_lc_amount'
        WHEN 2 THEN 'average_lc_value'
        WHEN 3 THEN 'active_customers'
        WHEN 4 THEN 'pending_approvals'
        WHEN 5 THEN 'compliance_pass_rate'
        WHEN 6 THEN 'document_processing_time'
        WHEN 7 THEN 'workflow_completion_rate'
        WHEN 8 THEN 'risk_score_average'
        WHEN 9 THEN 'revenue_total'
        WHEN 10 THEN 'fee_income'
        ELSE 'transaction_count'
    END,
    (100 + (gs * 1000.50))::numeric(19,4),
    CASE (gs % 12)
        WHEN 0 THEN 'count'
        WHEN 1 THEN 'USD'
        WHEN 2 THEN 'USD'
        WHEN 3 THEN 'count'
        WHEN 4 THEN 'count'
        WHEN 5 THEN 'percentage'
        WHEN 6 THEN 'hours'
        WHEN 7 THEN 'percentage'
        WHEN 8 THEN 'score'
        WHEN 9 THEN 'USD'
        WHEN 10 THEN 'USD'
        ELSE 'count'
    END,
    CASE (gs % 4)
        WHEN 0 THEN 'COUNTER'
        WHEN 1 THEN 'GAUGE'
        WHEN 2 THEN 'AVERAGE'
        ELSE 'SUM'
    END,
    CURRENT_TIMESTAMP - (gs || ' hours')::interval
FROM analytics_reports r
CROSS JOIN generate_series(1, 10) gs;

-- ==============================================
-- BANK GUARANTEES DATA
-- ==============================================

INSERT INTO bank_guarantees (
    id, tenant_id, bg_number, reference_number,
    applicant_id, beneficiary_id, issuing_bank, advising_bank,
    bg_type, amount, currency, application_date, issue_date,
    effective_date, expiry_date, status, purpose,
    governing_law, jurisdiction, underlying_contract_number,
    reduction_allowed, extension_allowed, claims_count,
    created_at, created_by, updated_at, updated_by, version
)
SELECT
    gen_random_uuid(),
    '00000000-0000-0000-0000-000000000001',
    'BG' || TO_CHAR(CURRENT_DATE, 'YYYY') || LPAD(generate_series::text, 6, '0'),
    'REF-BG-' || generate_series,
    (SELECT id FROM customers WHERE customer_type = 'BUYER' ORDER BY RANDOM() LIMIT 1),
    (SELECT id FROM customers WHERE customer_type = 'SELLER' ORDER BY RANDOM() LIMIT 1),
    'Global Trade Bank',
    CASE (generate_series % 3)
        WHEN 0 THEN 'HSBC Bank'
        WHEN 1 THEN 'Standard Chartered'
        ELSE 'Citibank'
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'PERFORMANCE'
        WHEN 1 THEN 'ADVANCE_PAYMENT'
        WHEN 2 THEN 'BID_BOND'
        WHEN 3 THEN 'RETENTION'
        ELSE 'MAINTENANCE'
    END,
    (50000 + (generate_series * 10000))::numeric(20,2),
    CASE (generate_series % 3)
        WHEN 0 THEN 'USD'
        WHEN 1 THEN 'EUR'
        ELSE 'AED'
    END,
    CURRENT_TIMESTAMP - (generate_series * 10 || ' days')::interval,
    CURRENT_TIMESTAMP - ((generate_series * 10) - 5 || ' days')::interval,
    CURRENT_TIMESTAMP - ((generate_series * 10) - 3 || ' days')::interval,
    CURRENT_TIMESTAMP + (generate_series * 30 || ' days')::interval,
    CASE (generate_series % 5)
        WHEN 0 THEN 'DRAFT'
        WHEN 1 THEN 'PENDING_APPROVAL'
        WHEN 2 THEN 'APPROVED'
        WHEN 3 THEN 'ISSUED'
        ELSE 'ACTIVE'
    END,
    'Bank Guarantee for Contract Performance',
    CASE (generate_series % 3)
        WHEN 0 THEN 'UAE Law'
        WHEN 1 THEN 'English Law'
        ELSE 'Singapore Law'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'Dubai Courts'
        WHEN 1 THEN 'London Arbitration'
        ELSE 'Singapore Arbitration'
    END,
    'CONTRACT-' || generate_series,
    (generate_series % 2) = 0,
    (generate_series % 2) = 1,
    0,
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    'admin',
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    'admin',
    0
FROM generate_series(1, 100);

-- ==============================================
-- DOCUMENTARY COLLECTIONS DATA
-- ==============================================

INSERT INTO documentary_collections (
    id, tenant_id, collection_number, reference_number,
    drawer_id, drawee_id, remitting_bank, collecting_bank,
    collection_type, amount, currency, payment_terms,
    tenor_days, collection_date, status, required_documents,
    documents_received, documents_complete, goods_description,
    invoice_number, charges_for_account, protest_required,
    created_at, created_by, updated_at, updated_by, version
)
SELECT
    gen_random_uuid(),
    '00000000-0000-0000-0000-000000000001',
    'DC' || TO_CHAR(CURRENT_DATE, 'YYYY') || LPAD(generate_series::text, 6, '0'),
    'REF-DC-' || generate_series,
    (SELECT id FROM customers WHERE customer_type = 'EXPORTER' ORDER BY RANDOM() LIMIT 1),
    (SELECT id FROM customers WHERE customer_type = 'IMPORTER' ORDER BY RANDOM() LIMIT 1),
    'Global Trade Bank',
    CASE (generate_series % 4)
        WHEN 0 THEN 'HSBC Bank'
        WHEN 1 THEN 'Standard Chartered'
        WHEN 2 THEN 'Citibank'
        ELSE 'Deutsche Bank'
    END,
    CASE (generate_series % 2)
        WHEN 0 THEN 'D/P'
        ELSE 'D/A'
    END,
    (25000 + (generate_series * 5000))::numeric(20,2),
    CASE (generate_series % 3)
        WHEN 0 THEN 'USD'
        WHEN 1 THEN 'EUR'
        ELSE 'GBP'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 'SIGHT'
        WHEN 1 THEN 'USANCE_30'
        ELSE 'USANCE_60'
    END,
    CASE (generate_series % 3)
        WHEN 0 THEN 0
        WHEN 1 THEN 30
        ELSE 60
    END,
    CURRENT_TIMESTAMP - (generate_series * 5 || ' days')::interval,
    CASE (generate_series % 5)
        WHEN 0 THEN 'DRAFT'
        WHEN 1 THEN 'SUBMITTED'
        WHEN 2 THEN 'DOCUMENTS_PRESENTED'
        WHEN 3 THEN 'ACCEPTED'
        ELSE 'PAID'
    END,
    ARRAY['Commercial Invoice', 'Packing List', 'Bill of Lading', 'Certificate of Origin'],
    (generate_series % 2) = 0,
    (generate_series % 3) = 0,
    CASE (generate_series % 5)
        WHEN 0 THEN 'Electronics goods'
        WHEN 1 THEN 'Textile products'
        WHEN 2 THEN 'Machinery parts'
        WHEN 3 THEN 'Automotive parts'
        ELSE 'General merchandise'
    END,
    'INV-' || TO_CHAR(CURRENT_DATE, 'YYYY') || '-' || generate_series,
    CASE (generate_series % 2)
        WHEN 0 THEN 'DRAWER'
        ELSE 'DRAWEE'
    END,
    (generate_series % 10) = 0,
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    'admin',
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    'admin',
    0
FROM generate_series(1, 150);

-- ==============================================
-- SCF FACILITIES DATA
-- ==============================================

INSERT INTO scf_facilities (
    id, tenant_id, scf_reference, scf_type, status,
    supplier_id, supplier_name, buyer_id, buyer_name,
    invoice_amount, finance_amount, advance_rate, currency,
    discount_rate, fee_amount, interest_rate,
    invoice_number, purchase_order_number, invoice_date, due_date,
    payment_term_days, finance_date, maturity_date,
    approved, disbursed, repaid, goods_description,
    risk_rating, created_at, created_by, updated_at, updated_by, version
)
SELECT
    gen_random_uuid(),
    '00000000-0000-0000-0000-000000000001',
    'SCF' || TO_CHAR(CURRENT_DATE, 'YYYY') || LPAD(generate_series::text, 6, '0'),
    CASE (generate_series % 4)
        WHEN 0 THEN 'INVOICE_FINANCING'
        WHEN 1 THEN 'PAYABLES_FINANCING'
        WHEN 2 THEN 'REVERSE_FACTORING'
        ELSE 'DYNAMIC_DISCOUNTING'
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'PENDING'
        WHEN 1 THEN 'APPROVED'
        WHEN 2 THEN 'DISBURSED'
        WHEN 3 THEN 'ACTIVE'
        ELSE 'REPAID'
    END,
    (SELECT id FROM customers WHERE customer_type = 'SELLER' ORDER BY RANDOM() LIMIT 1),
    (SELECT name FROM customers WHERE customer_type = 'SELLER' ORDER BY RANDOM() LIMIT 1),
    (SELECT id FROM customers WHERE customer_type = 'BUYER' ORDER BY RANDOM() LIMIT 1),
    (SELECT name FROM customers WHERE customer_type = 'BUYER' ORDER BY RANDOM() LIMIT 1),
    (50000 + (generate_series * 5000))::numeric(20,2),
    (40000 + (generate_series * 4000))::numeric(20,2),
    80.00::numeric(5,2),
    'USD',
    2.50::numeric(5,2),
    (500 + (generate_series * 50))::numeric(20,2),
    3.75::numeric(5,2),
    'INV-' || TO_CHAR(CURRENT_DATE, 'YYYY') || '-SCF-' || generate_series,
    'PO-' || generate_series,
    CURRENT_DATE - (generate_series * 30 || ' days')::interval,
    CURRENT_DATE + ((90 - generate_series) || ' days')::interval,
    90,
    CURRENT_DATE - ((generate_series * 30) - 7 || ' days')::interval,
    CURRENT_DATE + ((90 - generate_series) || ' days')::interval,
    (generate_series % 5) != 0,
    (generate_series % 5) > 1,
    (generate_series % 5) = 4,
    'General trading goods',
    CASE (generate_series % 4)
        WHEN 0 THEN 'LOW'
        WHEN 1 THEN 'MEDIUM'
        WHEN 2 THEN 'MEDIUM_HIGH'
        ELSE 'HIGH'
    END,
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    'admin',
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    'admin',
    0
FROM generate_series(1, 200);

-- ==============================================
-- SHIPMENTS DATA
-- ==============================================

INSERT INTO shipments (
    id, tenant_id, shipment_number, reference_number, status,
    estimated_arrival, actual_arrival, origin_port, origin_country,
    destination_port, destination_country, transport_mode,
    vessel_name, voyage_number, container_number,
    shipper_id, consignee_id, bill_of_lading_number,
    documents_received, milestone_flags,
    created_by, created_at, updated_by, updated_at, version
)
SELECT
    gen_random_uuid(),
    '00000000-0000-0000-0000-000000000001',
    'SHP' || TO_CHAR(CURRENT_DATE, 'YYYY') || LPAD(generate_series::text, 6, '0'),
    'REF-SHP-' || generate_series,
    CASE (generate_series % 6)
        WHEN 0 THEN 'BOOKED'
        WHEN 1 THEN 'IN_TRANSIT'
        WHEN 2 THEN 'AT_PORT'
        WHEN 3 THEN 'CUSTOMS_CLEARANCE'
        WHEN 4 THEN 'DELIVERED'
        ELSE 'COMPLETED'
    END,
    CURRENT_TIMESTAMP + ((30 - generate_series) || ' days')::interval,
    CASE WHEN (generate_series % 6) >= 4
        THEN CURRENT_TIMESTAMP - (generate_series || ' days')::interval
        ELSE NULL
    END,
    CASE (generate_series % 8)
        WHEN 0 THEN 'Shanghai Port'
        WHEN 1 THEN 'Singapore Port'
        WHEN 2 THEN 'Hong Kong Port'
        WHEN 3 THEN 'Mumbai Port'
        WHEN 4 THEN 'Rotterdam Port'
        WHEN 5 THEN 'Hamburg Port'
        WHEN 6 THEN 'Dubai Port'
        ELSE 'Tokyo Port'
    END,
    CASE (generate_series % 8)
        WHEN 0 THEN 'CN'
        WHEN 1 THEN 'SG'
        WHEN 2 THEN 'HK'
        WHEN 3 THEN 'IN'
        WHEN 4 THEN 'NL'
        WHEN 5 THEN 'DE'
        WHEN 6 THEN 'AE'
        ELSE 'JP'
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'Jebel Ali Port, Dubai'
        WHEN 1 THEN 'Port Rashid, Dubai'
        WHEN 2 THEN 'Abu Dhabi Port'
        WHEN 3 THEN 'Singapore Port'
        ELSE 'Hong Kong Port'
    END,
    CASE (generate_series % 5)
        WHEN 0 THEN 'AE'
        WHEN 1 THEN 'AE'
        WHEN 2 THEN 'AE'
        WHEN 3 THEN 'SG'
        ELSE 'HK'
    END,
    CASE (generate_series % 4)
        WHEN 0 THEN 'SEA'
        WHEN 1 THEN 'AIR'
        WHEN 2 THEN 'ROAD'
        ELSE 'RAIL'
    END,
    'VESSEL-' || (1000 + generate_series),
    'V' || TO_CHAR(CURRENT_DATE, 'YY') || '-' || generate_series,
    'CONT' || LPAD(generate_series::text, 10, '0'),
    (SELECT id FROM customers WHERE customer_type = 'EXPORTER' ORDER BY RANDOM() LIMIT 1),
    (SELECT id FROM customers WHERE customer_type = 'IMPORTER' ORDER BY RANDOM() LIMIT 1),
    'BL' || TO_CHAR(CURRENT_DATE, 'YYYY') || LPAD(generate_series::text, 8, '0'),
    (generate_series % 3) = 0,
    (generate_series % 8)::smallint,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' days')::interval,
    1
FROM generate_series(1, 250);

-- ==============================================
-- SHIPMENT TRACKING EVENTS DATA
-- ==============================================

INSERT INTO shipment_tracking_events (
    shipment_id, event_timestamp, description, event_status, location
)
SELECT
    s.id,
    s.created_at + ((gs * 2) || ' hours')::interval,
    CASE (gs % 8)
        WHEN 0 THEN 'Shipment booked'
        WHEN 1 THEN 'Container loaded'
        WHEN 2 THEN 'Vessel departed'
        WHEN 3 THEN 'In transit'
        WHEN 4 THEN 'Arrived at port'
        WHEN 5 THEN 'Customs clearance started'
        WHEN 6 THEN 'Customs cleared'
        ELSE 'Delivered'
    END,
    CASE (gs % 8)
        WHEN 0 THEN 'BOOKED'
        WHEN 1 THEN 'LOADED'
        WHEN 2 THEN 'DEPARTED'
        WHEN 3 THEN 'IN_TRANSIT'
        WHEN 4 THEN 'ARRIVED'
        WHEN 5 THEN 'CUSTOMS'
        WHEN 6 THEN 'CLEARED'
        ELSE 'DELIVERED'
    END,
    CASE (gs % 8)
        WHEN 0 THEN s.origin_port
        WHEN 1 THEN s.origin_port
        WHEN 2 THEN s.origin_port
        WHEN 3 THEN 'At Sea'
        WHEN 4 THEN s.destination_port
        WHEN 5 THEN s.destination_port
        WHEN 6 THEN s.destination_port
        ELSE s.destination_port
    END
FROM shipments s
CROSS JOIN generate_series(0, 4) gs;

-- ==============================================
-- CONVERSATIONS DATA
-- ==============================================

INSERT INTO conversations (
    id, user_id, tenant_id, session_id, status,
    started_at, last_message_at, ended_at,
    created_by, created_at, updated_by, updated_at, version
)
SELECT
    gen_random_uuid(),
    (SELECT id FROM users ORDER BY RANDOM() LIMIT 1),
    '00000000-0000-0000-0000-000000000001',
    'SESSION-' || gen_random_uuid()::text,
    CASE (generate_series % 3)
        WHEN 0 THEN 'ACTIVE'
        WHEN 1 THEN 'ENDED'
        ELSE 'INACTIVE'
    END,
    CURRENT_TIMESTAMP - (generate_series * 2 || ' hours')::interval,
    CURRENT_TIMESTAMP - ((generate_series * 2) - 1 || ' hours')::interval,
    CASE WHEN (generate_series % 3) = 1
        THEN CURRENT_TIMESTAMP - ((generate_series * 2) - 1 || ' hours')::interval
        ELSE NULL
    END,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series * 2 || ' hours')::interval,
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - ((generate_series * 2) - 1 || ' hours')::interval,
    1
FROM generate_series(1, 100);

-- ==============================================
-- CONVERSATION CONTEXT DATA
-- ==============================================

INSERT INTO conversation_context (conversation_id, context_key, context_value)
SELECT
    c.id,
    CASE (gs % 5)
        WHEN 0 THEN 'current_module'
        WHEN 1 THEN 'last_entity_type'
        WHEN 2 THEN 'last_entity_id'
        WHEN 3 THEN 'language'
        ELSE 'timezone'
    END,
    CASE (gs % 5)
        WHEN 0 THEN 'TRADE_FINANCE'
        WHEN 1 THEN 'LC'
        WHEN 2 THEN gen_random_uuid()::text
        WHEN 3 THEN 'en'
        ELSE 'UTC'
    END
FROM conversations c
CROSS JOIN generate_series(1, 3) gs;

-- ==============================================
-- CONVERSATION MESSAGES DATA
-- ==============================================

INSERT INTO conversation_messages (
    id, conversation_id, role, content, intent, confidence, timestamp
)
SELECT
    gen_random_uuid(),
    c.id,
    CASE (gs % 2)
        WHEN 0 THEN 'user'
        ELSE 'assistant'
    END,
    CASE (gs % 10)
        WHEN 0 THEN 'What is the status of my LC application?'
        WHEN 1 THEN 'Your LC application LC202600123 is currently pending approval.'
        WHEN 2 THEN 'Show me all pending workflows'
        WHEN 3 THEN 'Here are your pending workflows...'
        WHEN 4 THEN 'What is the risk rating for customer CUST000050?'
        WHEN 5 THEN 'The risk rating is MEDIUM with a score of 55.5'
        WHEN 6 THEN 'How many documents are pending verification?'
        WHEN 7 THEN 'There are 15 documents pending verification.'
        WHEN 8 THEN 'Generate a trade volume report'
        ELSE 'Report generation started...'
    END,
    CASE (gs % 6)
        WHEN 0 THEN 'QUERY_STATUS'
        WHEN 1 THEN 'PROVIDE_INFO'
        WHEN 2 THEN 'LIST_ITEMS'
        WHEN 3 THEN 'PROVIDE_LIST'
        WHEN 4 THEN 'GENERATE_REPORT'
        ELSE 'CONFIRM_ACTION'
    END,
    (0.75 + (gs % 25) * 0.01)::real,
    c.started_at + ((gs * 30) || ' seconds')::interval
FROM conversations c
CROSS JOIN generate_series(1, 6) gs;

-- ==============================================
-- KNOWLEDGE DOCUMENTS DATA
-- ==============================================

INSERT INTO knowledge_documents (
    id, tenant_id, title, content, document_type, status,
    category, chunk_count, embedding_model, indexed_at,
    is_public, allowed_roles,
    created_at, created_by, updated_at, updated_by, version
)
VALUES
(gen_random_uuid(), '00000000-0000-0000-0000-000000000001',
 'Letter of Credit Process Guide',
 'Complete guide on LC processing including application, approval, issuance, amendment, and settlement procedures.',
 'GUIDE', 'ACTIVE', 'TRADE_FINANCE', 15, 'text-embedding-ada-002',
 CURRENT_TIMESTAMP, TRUE, ARRAY['TRADE_OFFICER', 'TRADE_MANAGER'],
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001',
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', 1),

(gen_random_uuid(), '00000000-0000-0000-0000-000000000001',
 'KYC Requirements and Procedures',
 'Comprehensive guide on Know Your Customer requirements, document verification, and compliance procedures.',
 'POLICY', 'ACTIVE', 'COMPLIANCE', 20, 'text-embedding-ada-002',
 CURRENT_TIMESTAMP, FALSE, ARRAY['COMPLIANCE_OFFICER', 'SYSTEM_ADMIN'],
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001',
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', 1),

(gen_random_uuid(), '00000000-0000-0000-0000-000000000001',
 'Risk Assessment Framework',
 'Framework for assessing customer, transaction, and country risk in trade finance operations.',
 'FRAMEWORK', 'ACTIVE', 'RISK_MANAGEMENT', 25, 'text-embedding-ada-002',
 CURRENT_TIMESTAMP, FALSE, ARRAY['RISK_ANALYST', 'COMPLIANCE_OFFICER'],
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001',
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', 1),

(gen_random_uuid(), '00000000-0000-0000-0000-000000000001',
 'Document Verification Standards',
 'Standards and procedures for verifying trade documents including invoices, bills of lading, and certificates.',
 'STANDARD', 'ACTIVE', 'DOCUMENTATION', 18, 'text-embedding-ada-002',
 CURRENT_TIMESTAMP, TRUE, ARRAY['DOC_PROCESSOR', 'TRADE_OFFICER'],
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001',
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', 1),

(gen_random_uuid(), '00000000-0000-0000-0000-000000000001',
 'Trade Finance Regulations UAE',
 'Overview of UAE trade finance regulations, central bank guidelines, and compliance requirements.',
 'REGULATION', 'ACTIVE', 'COMPLIANCE', 30, 'text-embedding-ada-002',
 CURRENT_TIMESTAMP, FALSE, ARRAY['COMPLIANCE_OFFICER', 'TRADE_MANAGER'],
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001',
 CURRENT_TIMESTAMP, '00000000-0000-0000-0000-000000000001', 1);

-- ==============================================
-- IDENTITY PROVIDERS DATA
-- ==============================================

INSERT INTO identity_providers (
    id, provider_id, provider_name, tenant_id, protocol,
    auth_endpoint, token_endpoint, user_info_endpoint, logout_endpoint,
    client_id, client_secret, active, created_at, updated_at
)
VALUES
(gen_random_uuid(), 'azure-ad-gtb', 'Azure Active Directory',
 '00000000-0000-0000-0000-000000000001', 'OAUTH2',
 'https://login.microsoftonline.com/common/oauth2/v2.0/authorize',
 'https://login.microsoftonline.com/common/oauth2/v2.0/token',
 'https://graph.microsoft.com/v1.0/me',
 'https://login.microsoftonline.com/common/oauth2/v2.0/logout',
 'gtb-client-id-12345', 'encrypted-secret-key-67890',
 TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), 'google-oauth-gtb', 'Google OAuth',
 '00000000-0000-0000-0000-000000000001', 'OAUTH2',
 'https://accounts.google.com/o/oauth2/v2/auth',
 'https://oauth2.googleapis.com/token',
 'https://www.googleapis.com/oauth2/v2/userinfo',
 'https://accounts.google.com/logout',
 'google-client-id-abcde', 'encrypted-google-secret-fghij',
 TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

(gen_random_uuid(), 'okta-gtb', 'Okta SSO',
 '00000000-0000-0000-0000-000000000001', 'SAML2',
 'https://gtb.okta.com/app/saml/sso',
 'https://gtb.okta.com/api/v1/sessions',
 'https://gtb.okta.com/api/v1/users/me',
 'https://gtb.okta.com/login/signout',
 'okta-client-id-xyz', 'encrypted-okta-secret-123',
 FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ==============================================
-- IDENTITY PROVIDER CONFIG DATA
-- ==============================================

INSERT INTO identity_provider_config (provider_id, config_key, config_value)
SELECT
    ip.id,
    unnest(ARRAY['scope', 'response_type', 'redirect_uri', 'grant_type']),
    unnest(ARRAY['openid profile email', 'code', 'https://app.globaltrade.bank/auth/callback', 'authorization_code'])
FROM identity_providers ip
WHERE ip.protocol = 'OAUTH2';

-- ==============================================
-- MFA SETUPS DATA
-- ==============================================

INSERT INTO mfa_setups (
    id, user_id, tenant_id, method, secret, enabled, verified,
    created_at, enabled_at, last_used_at, phone_number, email
)
SELECT
    gen_random_uuid(),
    u.id::text,
    '00000000-0000-0000-0000-000000000001',
    CASE (ROW_NUMBER() OVER (PARTITION BY u.id) % 3)
        WHEN 0 THEN 'TOTP'
        WHEN 1 THEN 'SMS'
        ELSE 'EMAIL'
    END,
    'SECRET-' || encode(gen_random_bytes(20), 'base64'),
    (ROW_NUMBER() OVER (PARTITION BY u.id)) = 1,
    (ROW_NUMBER() OVER (PARTITION BY u.id)) = 1,
    CURRENT_TIMESTAMP - (ROW_NUMBER() OVER () || ' days')::interval,
    CASE WHEN (ROW_NUMBER() OVER (PARTITION BY u.id)) = 1
        THEN CURRENT_TIMESTAMP - ((ROW_NUMBER() OVER () - 1) || ' days')::interval
        ELSE NULL
    END,
    CASE WHEN (ROW_NUMBER() OVER (PARTITION BY u.id)) = 1
        THEN CURRENT_TIMESTAMP - (ROW_NUMBER() OVER () % 5 || ' hours')::interval
        ELSE NULL
    END,
    u.phone_number,
    u.email
FROM users u
WHERE u.id != '00000000-0000-0000-0000-000000000001'
LIMIT 50;

-- ==============================================
-- MFA BACKUP CODES DATA
-- ==============================================

INSERT INTO mfa_backup_codes (mfa_setup_id, backup_code)
SELECT
    m.id,
    UPPER(substring(md5(random()::text || m.id::text || gs::text), 1, 8))
FROM mfa_setups m
CROSS JOIN generate_series(1, 10) gs
WHERE m.method = 'TOTP';

-- ==============================================
-- SSO SESSIONS DATA
-- ==============================================

INSERT INTO sso_sessions (
    id, session_id, user_id, email, display_name, provider_id, tenant_id,
    created_at, expires_at, last_accessed_at, ip_address, user_agent, active
)
SELECT
    gen_random_uuid(),
    'SSO-' || gen_random_uuid()::text,
    u.id::text,
    u.email,
    u.first_name || ' ' || u.last_name,
    (SELECT provider_id FROM identity_providers ORDER BY RANDOM() LIMIT 1),
    '00000000-0000-0000-0000-000000000001',
    CURRENT_TIMESTAMP - (generate_series || ' hours')::interval,
    CURRENT_TIMESTAMP + ((24 - generate_series) || ' hours')::interval,
    CURRENT_TIMESTAMP - ((generate_series % 12) || ' minutes')::interval,
    '192.168.1.' || (1 + (generate_series % 254)),
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    generate_series <= 20
FROM users u
CROSS JOIN generate_series(1, 2) generate_series
WHERE u.id != '00000000-0000-0000-0000-000000000001'
LIMIT 60;

-- ==============================================
-- SSO SESSION ATTRIBUTES DATA
-- ==============================================

INSERT INTO sso_session_attributes (session_id, attribute_key, attribute_value)
SELECT
    s.id,
    unnest(ARRAY['idp_token', 'refresh_token', 'token_type', 'expires_in']),
    unnest(ARRAY[
        'encrypted-token-' || encode(gen_random_bytes(32), 'base64'),
        'encrypted-refresh-' || encode(gen_random_bytes(32), 'base64'),
        'Bearer',
        '3600'
    ])
FROM sso_sessions s;

-- ==============================================
-- WORKFLOW TASKS DATA
-- ==============================================

INSERT INTO workflow_tasks (
    id, workflow_id, task_name, task_description, status, priority,
    assigned_to, assigned_to_name, assigned_by, assigned_at,
    started_at, completed_at, outcome, due_date, sla_breached,
    sequence_order, version, created_at, updated_at
)
SELECT
    gen_random_uuid(),
    w.id,
    CASE (gs % 4)
        WHEN 0 THEN 'Initial Review'
        WHEN 1 THEN 'Risk Assessment'
        WHEN 2 THEN 'Compliance Check'
        ELSE 'Final Approval'
    END,
    CASE (gs % 4)
        WHEN 0 THEN 'Review application details and verify completeness'
        WHEN 1 THEN 'Assess risk factors and assign risk rating'
        WHEN 2 THEN 'Perform compliance screening and AML checks'
        ELSE 'Final approval from authorized signatory'
    END,
    CASE
        WHEN w.status = 'COMPLETED' THEN 'COMPLETED'
        WHEN gs < w.current_level THEN 'COMPLETED'
        WHEN gs = w.current_level THEN 'IN_PROGRESS'
        ELSE 'PENDING'
    END,
    COALESCE(w.priority, 'NORMAL'),
    (SELECT id FROM users ORDER BY RANDOM() LIMIT 1),
    (SELECT first_name || ' ' || last_name FROM users ORDER BY RANDOM() LIMIT 1),
    '00000000-0000-0000-0000-000000000001',
    w.created_at + ((gs * 2) || ' hours')::interval,
    CASE
        WHEN gs <= w.current_level
        THEN w.created_at + ((gs * 2 + 1) || ' hours')::interval
        ELSE NULL
    END,
    CASE
        WHEN w.status = 'COMPLETED' OR gs < w.current_level
        THEN w.created_at + ((gs * 2 + 3) || ' hours')::interval
        ELSE NULL
    END,
    CASE
        WHEN w.status = 'COMPLETED' OR gs < w.current_level
        THEN CASE (gs % 3) WHEN 0 THEN 'APPROVED' WHEN 1 THEN 'APPROVED' ELSE 'APPROVED' END
        ELSE NULL
    END,
    COALESCE(w.due_date, w.created_at + '72 hours'::interval) - ((4 - gs) * 8 || ' hours')::interval,
    FALSE,
    gs,
    1,
    w.created_at + ((gs * 2) || ' hours')::interval,
    COALESCE(
        CASE WHEN gs <= w.current_level
        THEN w.created_at + ((gs * 2 + 3) || ' hours')::interval
        ELSE NULL END,
        w.created_at + ((gs * 2) || ' hours')::interval
    )
FROM workflows w
CROSS JOIN generate_series(1, 4) gs
LIMIT 1600;

COMMIT;

