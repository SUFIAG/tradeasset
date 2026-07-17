-- =====================================================
-- TEST DATA GENERATION SCRIPT
-- Description: Generates large volume of realistic test data
-- for all tables in the TradeSphere AI system
-- Author: AI System
-- Date: 2026-07-17
-- Usage: Run after all migrations are complete
-- =====================================================

-- Set search path
SET search_path TO customer, lc, workflow, compliance, risk, document, audit, shipment, security, analytics, public;

-- =====================================================
-- CUSTOMERS DATA (100 customers)
-- =====================================================

INSERT INTO customer.customers (id, tenant_id, customer_number, name, customer_type, registration_number, country, address, email, phone, status, kyc_status, risk_rating, credit_limit, credit_currency, created_at, created_by)
SELECT
    gen_random_uuid(),
    'TENANT_001',
    'CUST-' || LPAD(i::TEXT, 6, '0'),
    CASE
        WHEN i % 5 = 0 THEN 'ABC Trading Company ' || i
        WHEN i % 5 = 1 THEN 'Global Exports Ltd ' || i
        WHEN i % 5 = 2 THEN 'International Imports Inc ' || i
        WHEN i % 5 = 3 THEN 'Premium Manufacturing Co ' || i
        ELSE 'Elite Commerce Corp ' || i
    END,
    CASE WHEN i % 3 = 0 THEN 'CORPORATE' ELSE 'SME' END,
    'REG-' || LPAD(i::TEXT, 8, '0'),
    CASE
        WHEN i % 10 = 0 THEN 'US'
        WHEN i % 10 = 1 THEN 'GB'
        WHEN i % 10 = 2 THEN 'DE'
        WHEN i % 10 = 3 THEN 'FR'
        WHEN i % 10 = 4 THEN 'CN'
        WHEN i % 10 = 5 THEN 'JP'
        WHEN i % 10 = 6 THEN 'IN'
        WHEN i % 10 = 7 THEN 'SG'
        WHEN i % 10 = 8 THEN 'AE'
        ELSE 'AU'
    END,
    i || ' Business Street, Suite ' || (i % 100),
    'contact' || i || '@company' || i || '.com',
    '+1-' || LPAD((i % 900 + 100)::TEXT, 3, '0') || '-' || LPAD((i % 9000 + 1000)::TEXT, 4, '0'),
    CASE
        WHEN i % 6 = 0 THEN 'ACTIVE'
        WHEN i % 6 = 1 THEN 'APPROVED'
        WHEN i % 6 = 2 THEN 'PENDING_APPROVAL'
        WHEN i % 6 = 3 THEN 'ACTIVE'
        WHEN i % 6 = 4 THEN 'ACTIVE'
        ELSE 'DRAFT'
    END,
    CASE
        WHEN i % 5 = 0 THEN 'COMPLETED'
        WHEN i % 5 = 1 THEN 'IN_PROGRESS'
        WHEN i % 5 = 2 THEN 'COMPLETED'
        WHEN i % 5 = 3 THEN 'COMPLETED'
        ELSE 'NOT_STARTED'
    END,
    CASE
        WHEN i % 7 = 0 THEN 'LOW'
        WHEN i % 7 IN (1,2,3) THEN 'MEDIUM'
        ELSE 'HIGH'
    END,
    (i * 100000 + (i % 10) * 50000),
    'USD',
    NOW() - (i || ' days')::INTERVAL,
    'system'
FROM generate_series(1, 100) AS i;

-- =====================================================
-- LETTER OF CREDIT DATA (500 LCs)
-- =====================================================

WITH customer_ids AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY created_at) as rn
    FROM customer.customers
    WHERE tenant_id = 'TENANT_001'
)
INSERT INTO lc.letters_of_credit (
    id, tenant_id, lc_number, applicant_id, beneficiary_id, amount, currency,
    status, application_date, expiry_date, issued_date, payment_date,
    incoterm, payment_terms, port_of_loading, port_of_discharge,
    created_at, created_by, version
)
SELECT
    gen_random_uuid(),
    'TENANT_001',
    CASE WHEN i > 100 THEN 'LC-2026-' || LPAD(i::TEXT, 8, '0') ELSE NULL END,
    (SELECT id FROM customer_ids WHERE rn = ((i % 50) + 1)),
    (SELECT id FROM customer_ids WHERE rn = ((i % 50) + 51)),
    (i * 10000 + (i % 100) * 500)::NUMERIC(15,2),
    CASE
        WHEN i % 5 = 0 THEN 'EUR'
        WHEN i % 5 = 1 THEN 'GBP'
        ELSE 'USD'
    END,
    CASE
        WHEN i % 10 = 0 THEN 'DRAFT'
        WHEN i % 10 = 1 THEN 'SUBMITTED'
        WHEN i % 10 = 2 THEN 'APPROVED'
        WHEN i % 10 IN (3,4,5) THEN 'ISSUED'
        WHEN i % 10 = 6 THEN 'DOCUMENTS_PRESENTED'
        WHEN i % 10 = 7 THEN 'DOCUMENTS_ACCEPTED'
        WHEN i % 10 = 8 THEN 'PAYMENT_AUTHORIZED'
        ELSE 'COMPLETED'
    END,
    NOW() - (i || ' days')::INTERVAL,
    NOW() + ((180 - i % 30) || ' days')::INTERVAL,
    CASE WHEN i % 10 > 2 THEN NOW() - ((i - 5) || ' days')::INTERVAL ELSE NULL END,
    CASE WHEN i % 10 = 9 THEN NOW() - ((i % 30) || ' days')::INTERVAL ELSE NULL END,
    CASE
        WHEN i % 4 = 0 THEN 'FOB'
        WHEN i % 4 = 1 THEN 'CIF'
        WHEN i % 4 = 2 THEN 'CFR'
        ELSE 'EXW'
    END,
    'Payment at sight upon presentation of compliant documents',
    CASE WHEN i % 3 = 0 THEN 'New York' WHEN i % 3 = 1 THEN 'Shanghai' ELSE 'Rotterdam' END,
    CASE WHEN i % 3 = 0 THEN 'London' WHEN i % 3 = 1 THEN 'Singapore' ELSE 'Dubai' END,
    NOW() - (i || ' days')::INTERVAL,
    'system',
    1
FROM generate_series(1, 500) AS i;

-- =====================================================
-- COMPLIANCE CHECKS DATA (1000 checks)
-- =====================================================

WITH customer_ids AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY created_at) as rn
    FROM customer.customers
    WHERE tenant_id = 'TENANT_001'
)
INSERT INTO compliance.compliance_checks (
    id, tenant_id, entity_type, entity_id, entity_name, check_type,
    status, risk_level, sanctions_score, aml_score, pep_score, overall_score,
    created_at, created_by, completed_at, reviewed_at, version
)
SELECT
    gen_random_uuid(),
    'TENANT_001',
    CASE WHEN i % 3 = 0 THEN 'CUSTOMER' ELSE 'TRANSACTION' END,
    (SELECT id FROM customer_ids WHERE rn = ((i % 100) + 1)),
    'Entity ' || i,
    CASE
        WHEN i % 4 = 0 THEN 'SANCTIONS'
        WHEN i % 4 = 1 THEN 'AML'
        WHEN i % 4 = 2 THEN 'PEP'
        ELSE 'COMPREHENSIVE'
    END,
    CASE
        WHEN i % 8 = 0 THEN 'PENDING'
        WHEN i % 8 IN (1,2) THEN 'IN_PROGRESS'
        WHEN i % 8 IN (3,4,5,6) THEN 'PASSED'
        ELSE 'MANUAL_REVIEW'
    END,
    CASE
        WHEN i % 10 < 7 THEN 'LOW'
        WHEN i % 10 < 9 THEN 'MEDIUM'
        ELSE 'HIGH'
    END,
    (RANDOM() * 100)::NUMERIC(5,2),
    (RANDOM() * 100)::NUMERIC(5,2),
    (RANDOM() * 100)::NUMERIC(5,2),
    (50 + RANDOM() * 50)::NUMERIC(5,2),
    NOW() - (i || ' hours')::INTERVAL,
    'system',
    CASE WHEN i % 8 >= 3 THEN NOW() - ((i % 48) || ' hours')::INTERVAL ELSE NULL END,
    CASE WHEN i % 8 = 7 THEN NOW() - ((i % 24) || ' hours')::INTERVAL ELSE NULL END,
    1
FROM generate_series(1, 1000) AS i;

-- =====================================================
-- RISK ASSESSMENTS DATA (800 assessments)
-- =====================================================

WITH customer_ids AS (
    SELECT id FROM customer.customers WHERE tenant_id = 'TENANT_001' LIMIT 100
)
INSERT INTO risk.risk_assessments (
    id, tenant_id, entity_type, entity_id, risk_type, risk_level, risk_score,
    country_risk_score, credit_risk_score, transaction_risk_score,
    compliance_risk_score, operational_risk_score, overall_score,
    status, assessed_at, created_at, created_by, version
)
SELECT
    gen_random_uuid(),
    'TENANT_001',
    CASE WHEN i % 2 = 0 THEN 'CUSTOMER' ELSE 'TRANSACTION' END,
    (SELECT id FROM customer_ids OFFSET (i % 100) LIMIT 1),
    CASE
        WHEN i % 5 = 0 THEN 'CREDIT_RISK'
        WHEN i % 5 = 1 THEN 'COUNTRY_RISK'
        WHEN i % 5 = 2 THEN 'TRANSACTION_RISK'
        WHEN i % 5 = 3 THEN 'COMPLIANCE_RISK'
        ELSE 'OPERATIONAL_RISK'
    END,
    CASE
        WHEN i % 10 < 5 THEN 'LOW'
        WHEN i % 10 < 8 THEN 'MEDIUM'
        ELSE 'HIGH'
    END,
    (20 + RANDOM() * 80)::NUMERIC(5,2),
    (RANDOM() * 100)::NUMERIC(5,2),
    (RANDOM() * 100)::NUMERIC(5,2),
    (RANDOM() * 100)::NUMERIC(5,2),
    (RANDOM() * 100)::NUMERIC(5,2),
    (RANDOM() * 100)::NUMERIC(5,2),
    (40 + RANDOM() * 50)::NUMERIC(5,2),
    'COMPLETED',
    NOW() - (i || ' hours')::INTERVAL,
    NOW() - (i || ' hours')::INTERVAL,
    'system',
    1
FROM generate_series(1, 800) AS i;

-- =====================================================
-- DOCUMENTS DATA (2000 documents)
-- =====================================================

WITH lc_ids AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY created_at) as rn
    FROM lc.letters_of_credit
    WHERE tenant_id = 'TENANT_001'
)
INSERT INTO document.documents (
    id, tenant_id, document_number, document_type, reference_type, reference_id,
    status, file_name, file_path, mime_type, file_size, page_count,
    uploaded_at, uploaded_by, processed_at, verified_at, version
)
SELECT
    gen_random_uuid(),
    'TENANT_001',
    'DOC-' || LPAD(i::TEXT, 8, '0'),
    CASE
        WHEN i % 8 = 0 THEN 'COMMERCIAL_INVOICE'
        WHEN i % 8 = 1 THEN 'BILL_OF_LADING'
        WHEN i % 8 = 2 THEN 'PACKING_LIST'
        WHEN i % 8 = 3 THEN 'CERTIFICATE_OF_ORIGIN'
        WHEN i % 8 = 4 THEN 'INSURANCE_CERTIFICATE'
        WHEN i % 8 = 5 THEN 'INSPECTION_CERTIFICATE'
        WHEN i % 8 = 6 THEN 'LETTER_OF_CREDIT'
        ELSE 'OTHER'
    END,
    'LETTER_OF_CREDIT',
    (SELECT id FROM lc_ids WHERE rn = ((i % 500) + 1)),
    CASE
        WHEN i % 6 = 0 THEN 'UPLOADED'
        WHEN i % 6 IN (1,2) THEN 'PROCESSING'
        WHEN i % 6 IN (3,4) THEN 'VERIFIED'
        ELSE 'REJECTED'
    END,
    'document_' || i || '.pdf',
    '/documents/2026/07/' || i || '/document_' || i || '.pdf',
    'application/pdf',
    (50000 + i * 1000)::BIGINT,
    (i % 10) + 1,
    NOW() - (i || ' hours')::INTERVAL,
    'user' || (i % 50),
    CASE WHEN i % 6 > 1 THEN NOW() - ((i - 10) || ' hours')::INTERVAL ELSE NULL END,
    CASE WHEN i % 6 IN (3,4) THEN NOW() - ((i - 20) || ' hours')::INTERVAL ELSE NULL END,
    1
FROM generate_series(1, 2000) AS i;

-- =====================================================
-- WORKFLOWS DATA (600 workflows)
-- =====================================================

WITH lc_ids AS (
    SELECT id FROM lc.letters_of_credit WHERE tenant_id = 'TENANT_001' LIMIT 500
)
INSERT INTO workflow.workflows (
    id, tenant_id, workflow_type, entity_type, entity_id, status,
    current_level, total_levels, priority, due_date, sla_breached,
    created_at, created_by, started_at, completed_at, version
)
SELECT
    gen_random_uuid(),
    'TENANT_001',
    CASE
        WHEN i % 5 = 0 THEN 'CUSTOMER_APPROVAL'
        WHEN i % 5 = 1 THEN 'LC_APPROVAL'
        WHEN i % 5 = 2 THEN 'DOCUMENT_VERIFICATION'
        WHEN i % 5 = 3 THEN 'COMPLIANCE_REVIEW'
        ELSE 'RISK_ASSESSMENT'
    END,
    CASE WHEN i % 2 = 0 THEN 'CUSTOMER' ELSE 'LETTER_OF_CREDIT' END,
    (SELECT id FROM lc_ids OFFSET (i % 500) LIMIT 1),
    CASE
        WHEN i % 7 = 0 THEN 'PENDING'
        WHEN i % 7 IN (1,2) THEN 'IN_PROGRESS'
        WHEN i % 7 IN (3,4,5) THEN 'COMPLETED'
        ELSE 'REJECTED'
    END,
    (i % 4) + 1,
    CASE WHEN i % 3 = 0 THEN 2 WHEN i % 3 = 1 THEN 3 ELSE 4 END,
    CASE
        WHEN i % 4 = 0 THEN 'LOW'
        WHEN i % 4 IN (1,2) THEN 'NORMAL'
        ELSE 'HIGH'
    END,
    NOW() + ((5 + i % 15) || ' days')::INTERVAL,
    CASE WHEN i % 20 = 0 THEN TRUE ELSE FALSE END,
    NOW() - (i || ' hours')::INTERVAL,
    'system',
    CASE WHEN i % 7 > 0 THEN NOW() - ((i - 2) || ' hours')::INTERVAL ELSE NULL END,
    CASE WHEN i % 7 IN (3,4,5) THEN NOW() - ((i % 48) || ' hours')::INTERVAL ELSE NULL END,
    1
FROM generate_series(1, 600) AS i;

-- =====================================================
-- SHIPMENT DATA (300 shipments)
-- =====================================================

WITH lc_ids AS (
    SELECT id, ROW_NUMBER() OVER (ORDER BY created_at) as rn
    FROM lc.letters_of_credit
    WHERE tenant_id = 'TENANT_001' AND status IN ('ISSUED', 'DOCUMENTS_PRESENTED', 'COMPLETED')
)
INSERT INTO shipment.shipments (
    id, tenant_id, shipment_number, lc_id, shipper_name, shipper_address,
    consignee_name, consignee_address, transport_mode, carrier_name, vessel_name,
    port_of_loading, port_of_discharge, expected_departure_date, expected_arrival_date,
    estimated_delivery_date, status, total_packages, total_weight_kg, is_delayed,
    created_at, created_by, version
)
SELECT
    gen_random_uuid(),
    'TENANT_001',
    'SHIP-2026-' || LPAD(i::TEXT, 6, '0'),
    (SELECT id FROM lc_ids WHERE rn = (i % 300) + 1),
    'Shipper Company ' || i,
    i || ' Export Plaza, Port City',
    'Consignee Corp ' || i,
    i || ' Import Boulevard, Destination City',
    CASE
        WHEN i % 4 = 0 THEN 'SEA'
        WHEN i % 4 = 1 THEN 'AIR'
        WHEN i % 4 = 2 THEN 'ROAD'
        ELSE 'RAIL'
    END,
    'Carrier ' || (i % 20),
    'Vessel-' || (i % 50),
    CASE WHEN i % 3 = 0 THEN 'Shanghai' WHEN i % 3 = 1 THEN 'Rotterdam' ELSE 'Singapore' END,
    CASE WHEN i % 3 = 0 THEN 'Los Angeles' WHEN i % 3 = 1 THEN 'Hamburg' ELSE 'Dubai' END,
    NOW() - ((i % 60) || ' days')::INTERVAL,
    NOW() + ((30 - i % 20) || ' days')::INTERVAL,
    NOW() + ((35 - i % 20) || ' days')::INTERVAL,
    CASE
        WHEN i % 8 = 0 THEN 'CREATED'
        WHEN i % 8 = 1 THEN 'READY'
        WHEN i % 8 IN (2,3) THEN 'IN_TRANSIT'
        WHEN i % 8 = 4 THEN 'ARRIVED'
        WHEN i % 8 = 5 THEN 'CUSTOMS_CLEARED'
        WHEN i % 8 = 6 THEN 'DELIVERED'
        ELSE 'CANCELLED'
    END,
    (i % 100) + 10,
    (i * 100 + (i % 1000))::NUMERIC(15,2),
    CASE WHEN i % 15 = 0 THEN TRUE ELSE FALSE END,
    NOW() - (i || ' days')::INTERVAL,
    'system',
    1
FROM generate_series(1, 300) AS i;

-- =====================================================
-- AUDIT LOGS DATA (5000 audit entries)
-- =====================================================

INSERT INTO audit.audit_logs (
    id, tenant_id, entity_type, entity_id, action, user_id, username,
    ip_address, user_agent, success, execution_time_ms, timestamp
)
SELECT
    gen_random_uuid(),
    'TENANT_001',
    CASE
        WHEN i % 6 = 0 THEN 'CUSTOMER'
        WHEN i % 6 = 1 THEN 'LETTER_OF_CREDIT'
        WHEN i % 6 = 2 THEN 'DOCUMENT'
        WHEN i % 6 = 3 THEN 'WORKFLOW'
        WHEN i % 6 = 4 THEN 'COMPLIANCE_CHECK'
        ELSE 'RISK_ASSESSMENT'
    END,
    gen_random_uuid(),
    CASE
        WHEN i % 10 = 0 THEN 'CREATE'
        WHEN i % 10 = 1 THEN 'UPDATE'
        WHEN i % 10 = 2 THEN 'DELETE'
        WHEN i % 10 = 3 THEN 'SUBMIT'
        WHEN i % 10 = 4 THEN 'APPROVE'
        WHEN i % 10 = 5 THEN 'REJECT'
        WHEN i % 10 = 6 THEN 'VERIFY'
        WHEN i % 10 = 7 THEN 'PROCESS'
        WHEN i % 10 = 8 THEN 'COMPLETE'
        ELSE 'VIEW'
    END,
    gen_random_uuid(),
    'user' || (i % 100),
    '192.168.' || (i % 255) || '.' || ((i * 3) % 255),
    'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36',
    CASE WHEN i % 50 = 0 THEN FALSE ELSE TRUE END,
    (i % 1000 + 50)::BIGINT,
    NOW() - (i || ' minutes')::INTERVAL
FROM generate_series(1, 5000) AS i;

-- =====================================================
-- ANALYTICS - Dashboard Metrics (180 days of daily data)
-- =====================================================

INSERT INTO analytics.dashboard_metrics (
    id, tenant_id, metric_date, aggregation_level,
    total_transactions, active_transactions, completed_transactions, total_transaction_value,
    lc_applications, lc_issued, lc_amendments, lc_total_value,
    compliance_checks, compliance_passed, compliance_failed, high_risk_alerts,
    risk_assessments, high_risk_count, medium_risk_count, low_risk_count,
    documents_processed, documents_verified, workflows_created, workflows_completed,
    calculated_at
)
SELECT
    gen_random_uuid(),
    'TENANT_001',
    CURRENT_DATE - i,
    'DAILY',
    (50 + (i % 30))::BIGINT,
    (20 + (i % 15))::BIGINT,
    (30 + (i % 20))::BIGINT,
    (1000000 + (i * 50000))::NUMERIC(20,2),
    (10 + (i % 8))::BIGINT,
    (8 + (i % 6))::BIGINT,
    (i % 4)::BIGINT,
    (500000 + (i * 25000))::NUMERIC(20,2),
    (25 + (i % 10))::BIGINT,
    (20 + (i % 8))::BIGINT,
    (i % 3)::BIGINT,
    (i % 2)::BIGINT,
    (15 + (i % 10))::BIGINT,
    (i % 3)::BIGINT,
    (8 + (i % 5))::BIGINT,
    (5 + (i % 4))::BIGINT,
    (40 + (i % 20))::BIGINT,
    (35 + (i % 15))::BIGINT,
    (12 + (i % 8))::BIGINT,
    (10 + (i % 6))::BIGINT,
    NOW()
FROM generate_series(0, 179) AS i;

-- =====================================================
-- Print Summary
-- =====================================================

DO $$
DECLARE
    customer_count INTEGER;
    lc_count INTEGER;
    compliance_count INTEGER;
    risk_count INTEGER;
    document_count INTEGER;
    workflow_count INTEGER;
    shipment_count INTEGER;
    audit_count INTEGER;
    analytics_count INTEGER;
BEGIN
    SELECT COUNT(*) INTO customer_count FROM customer.customers WHERE tenant_id = 'TENANT_001';
    SELECT COUNT(*) INTO lc_count FROM lc.letters_of_credit WHERE tenant_id = 'TENANT_001';
    SELECT COUNT(*) INTO compliance_count FROM compliance.compliance_checks WHERE tenant_id = 'TENANT_001';
    SELECT COUNT(*) INTO risk_count FROM risk.risk_assessments WHERE tenant_id = 'TENANT_001';
    SELECT COUNT(*) INTO document_count FROM document.documents WHERE tenant_id = 'TENANT_001';
    SELECT COUNT(*) INTO workflow_count FROM workflow.workflows WHERE tenant_id = 'TENANT_001';
    SELECT COUNT(*) INTO shipment_count FROM shipment.shipments WHERE tenant_id = 'TENANT_001';
    SELECT COUNT(*) INTO audit_count FROM audit.audit_logs WHERE tenant_id = 'TENANT_001';
    SELECT COUNT(*) INTO analytics_count FROM analytics.dashboard_metrics WHERE tenant_id = 'TENANT_001';

    RAISE NOTICE '=====================================================';
    RAISE NOTICE 'TEST DATA GENERATION COMPLETED';
    RAISE NOTICE '=====================================================';
    RAISE NOTICE 'Customers: %', customer_count;
    RAISE NOTICE 'Letters of Credit: %', lc_count;
    RAISE NOTICE 'Compliance Checks: %', compliance_count;
    RAISE NOTICE 'Risk Assessments: %', risk_count;
    RAISE NOTICE 'Documents: %', document_count;
    RAISE NOTICE 'Workflows: %', workflow_count;
    RAISE NOTICE 'Shipments: %', shipment_count;
    RAISE NOTICE 'Audit Logs: %', audit_count;
    RAISE NOTICE 'Analytics Metrics: %', analytics_count;
    RAISE NOTICE '=====================================================';
    RAISE NOTICE 'Total Records: %', (customer_count + lc_count + compliance_count + risk_count + document_count + workflow_count + shipment_count + audit_count + analytics_count);
    RAISE NOTICE '=====================================================';
END $$;

