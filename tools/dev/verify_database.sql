-- Verify current state of admin user and database
-- Run this in your PostgreSQL tool

-- 1. Check admin user details
SELECT
    id,
    username,
    email,
    status,
    password_hash,
    created_at,
    updated_at
FROM users
WHERE username = 'admin';

-- 2. Check Flyway migration history
SELECT version, description, installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank DESC
LIMIT 10;

-- 3. Count total users
SELECT COUNT(*) as total_users FROM users;

