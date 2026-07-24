-- Migration to align letters_of_credit table schema with LCJpaEntity
-- This migration renames columns and adds missing fields to match the JPA entity structure

-- Add missing address columns
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS applicant_name VARCHAR(500),
ADD COLUMN IF NOT EXISTS applicant_address TEXT,
ADD COLUMN IF NOT EXISTS beneficiary_name VARCHAR(500),
ADD COLUMN IF NOT EXISTS beneficiary_address TEXT;

-- Rename amount columns to match JPA entity
ALTER TABLE letters_of_credit
RENAME COLUMN lc_amount TO lc_amount_value;

ALTER TABLE letters_of_credit
RENAME COLUMN lc_currency TO lc_amount_currency;

-- Rename tolerance column
ALTER TABLE letters_of_credit
RENAME COLUMN tolerance_percentage TO tolerance;

-- Rename shipment date column
ALTER TABLE letters_of_credit
RENAME COLUMN latest_shipment_date TO last_shipment_date;

-- Add available amount columns
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS available_amount_value NUMERIC(20,2),
ADD COLUMN IF NOT EXISTS available_amount_currency VARCHAR(3);

-- Rename incoterms to incoterm
ALTER TABLE letters_of_credit
RENAME COLUMN incoterms TO incoterm;

-- Add application date
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS application_date DATE;

-- Rename issue_date column (it's already there, but might need to match exactly)
-- issue_date already exists, no change needed

-- Add presentation_date (different from presentation_period_days)
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS presentation_date DATE;

-- Add place_of_destination
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS place_of_destination VARCHAR(500);

-- Add goods-related columns
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS hs_code VARCHAR(20),
ADD COLUMN IF NOT EXISTS quantity NUMERIC(20,2),
ADD COLUMN IF NOT EXISTS quantity_unit VARCHAR(50);

-- Add credit limit columns
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS credit_limit_reserved_value NUMERIC(20,2),
ADD COLUMN IF NOT EXISTS credit_limit_reserved_currency VARCHAR(3),
ADD COLUMN IF NOT EXISTS credit_limit_approved BOOLEAN DEFAULT FALSE;

-- Add document requirement boolean flags
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS documents_required TEXT,
ADD COLUMN IF NOT EXISTS commercial_invoice_required BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS bill_of_lading_required BOOLEAN DEFAULT TRUE,
ADD COLUMN IF NOT EXISTS packing_list_required BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS certificate_of_origin_required BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS insurance_certificate_required BOOLEAN DEFAULT FALSE;

-- Rename special_conditions to special_instructions
ALTER TABLE letters_of_credit
RENAME COLUMN special_conditions TO special_instructions;

-- Add workflow columns
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS submitted_by UUID,
ADD COLUMN IF NOT EXISTS submitted_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS rejected_by UUID,
ADD COLUMN IF NOT EXISTS rejected_at TIMESTAMP,
ADD COLUMN IF NOT EXISTS rejection_reason TEXT;

-- Add SWIFT columns
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS swift_message TEXT,
ADD COLUMN IF NOT EXISTS swift_sent_at TIMESTAMP;

-- Add compliance and risk columns
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS compliance_check_passed BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS sanctions_check_passed BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS risk_rating VARCHAR(50);

-- Add currency column (separate from lc_amount_currency for base currency)
ALTER TABLE letters_of_credit
ADD COLUMN IF NOT EXISTS currency VARCHAR(3);

-- Update existing records to populate new columns with sensible defaults
UPDATE letters_of_credit
SET
    applicant_name = COALESCE(applicant_name, 'Unknown Applicant'),
    beneficiary_name = COALESCE(beneficiary_name, 'Unknown Beneficiary'),
    currency = COALESCE(currency, lc_amount_currency),
    application_date = COALESCE(application_date, issue_date, CURRENT_DATE),
    commercial_invoice_required = COALESCE(commercial_invoice_required, TRUE),
    bill_of_lading_required = COALESCE(bill_of_lading_required, TRUE),
    credit_limit_approved = COALESCE(credit_limit_approved, FALSE),
    compliance_check_passed = COALESCE(compliance_check_passed, FALSE),
    sanctions_check_passed = COALESCE(sanctions_check_passed, FALSE)
WHERE applicant_name IS NULL
   OR beneficiary_name IS NULL
   OR currency IS NULL
   OR application_date IS NULL;

-- Update available_amount columns from the old available_amount column if it exists
UPDATE letters_of_credit
SET
    available_amount_value = COALESCE(available_amount_value, available_amount),
    available_amount_currency = COALESCE(available_amount_currency, lc_amount_currency)
WHERE available_amount IS NOT NULL;

-- Drop old available_amount column if it exists
ALTER TABLE letters_of_credit
DROP COLUMN IF EXISTS available_amount;

-- Drop columns that are no longer needed
ALTER TABLE letters_of_credit
DROP COLUMN IF EXISTS lc_category,
DROP COLUMN IF EXISTS presentation_period_days,
DROP COLUMN IF EXISTS payment_terms,
DROP COLUMN IF EXISTS workflow_state,
DROP COLUMN IF EXISTS approval_level,
DROP COLUMN IF EXISTS issued_by,
DROP COLUMN IF EXISTS issued_at,
DROP COLUMN IF EXISTS amended_count,
DROP COLUMN IF EXISTS last_amended_at,
DROP COLUMN IF EXISTS utilized_amount,
DROP COLUMN IF EXISTS notes;

-- Update NOT NULL constraints to match JPA entity
ALTER TABLE letters_of_credit
ALTER COLUMN applicant_name SET NOT NULL,
ALTER COLUMN beneficiary_name SET NOT NULL;

-- Make issue_date nullable (it's set after approval, not required initially)
ALTER TABLE letters_of_credit
ALTER COLUMN issue_date DROP NOT NULL;

-- Make expiry_date nullable initially (can be set during creation or later)
-- Actually, keep it NOT NULL as per business logic

-- Comment on the table
COMMENT ON TABLE letters_of_credit IS 'Letters of Credit - Schema aligned with LCJpaEntity as of V13';

