-- V11: Fix admin password - FINAL FIX
-- This uses the EXACT hash from V2__seed_test_data.sql that originally worked

-- Update admin password to the correct hash for "admin123"
UPDATE users
SET
    password_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    updated_at = CURRENT_TIMESTAMP,
    updated_by = id
WHERE username = 'admin'
  AND id = '00000000-0000-0000-0000-000000000001';

-- Verify the update worked
DO $$
DECLARE
    v_hash TEXT;
BEGIN
    SELECT password_hash INTO v_hash
    FROM users
    WHERE username = 'admin';

    IF v_hash = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy' THEN
        RAISE NOTICE 'SUCCESS: Admin password hash updated correctly';
    ELSE
        RAISE EXCEPTION 'FAILED: Password hash was not updated. Current: %', v_hash;
    END IF;
END $$;

