-- Drop all old schemas and start fresh
-- Run this script directly in PostgreSQL to clean the database

-- Drop Flyway schema history to start fresh
DROP TABLE IF EXISTS flyway_schema_history CASCADE;

-- Drop all old schemas
DROP SCHEMA IF EXISTS identity CASCADE;
DROP SCHEMA IF EXISTS organization CASCADE;
DROP SCHEMA IF EXISTS customer CASCADE;
DROP SCHEMA IF EXISTS trade CASCADE;
DROP SCHEMA IF EXISTS document CASCADE;
DROP SCHEMA IF EXISTS compliance CASCADE;
DROP SCHEMA IF EXISTS risk CASCADE;
DROP SCHEMA IF EXISTS workflow CASCADE;
DROP SCHEMA IF EXISTS ai_services CASCADE;
DROP SCHEMA IF EXISTS audit CASCADE;
DROP SCHEMA IF EXISTS notification CASCADE;
DROP SCHEMA IF EXISTS bankguarantee CASCADE;
DROP SCHEMA IF EXISTS sso CASCADE;
DROP SCHEMA IF EXISTS mfa CASCADE;
DROP SCHEMA IF EXISTS analytics CASCADE;
DROP SCHEMA IF EXISTS collection CASCADE;
DROP SCHEMA IF EXISTS scf CASCADE;
DROP SCHEMA IF EXISTS knowledge CASCADE;
DROP SCHEMA IF EXISTS copilot CASCADE;
DROP SCHEMA IF EXISTS shipment CASCADE;

-- Drop all tables in public schema that might exist
DROP TABLE IF EXISTS public.user_roles CASCADE;
DROP TABLE IF EXISTS public.role_permissions CASCADE;
DROP TABLE IF EXISTS public.customer_contacts CASCADE;
DROP TABLE IF EXISTS public.letters_of_credit CASCADE;
DROP TABLE IF EXISTS public.documents CASCADE;
DROP TABLE IF EXISTS public.workflows CASCADE;
DROP TABLE IF EXISTS public.compliance_checks CASCADE;
DROP TABLE IF EXISTS public.risk_assessments CASCADE;
DROP TABLE IF EXISTS public.audit_logs CASCADE;
DROP TABLE IF EXISTS public.customers CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;
DROP TABLE IF EXISTS public.roles CASCADE;
DROP TABLE IF EXISTS public.permissions CASCADE;
DROP TABLE IF EXISTS public.branches CASCADE;
DROP TABLE IF EXISTS public.organizations CASCADE;
DROP TABLE IF EXISTS public.tenants CASCADE;

-- Now recreate the Flyway schema history table will be created automatically
-- The application will run the migrations fresh

COMMIT;

