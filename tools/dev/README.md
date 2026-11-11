# Local development utilities

These scripts are **local-only**. They are destructive or diagnostic and must never be pointed at
a shared, staging, or production environment.

| File | Purpose |
|---|---|
| `clean_database.ps1` | Drops and recreates the local Postgres schema so Flyway can replay from V1. |
| `clean_database.sql` | The SQL executed by `clean_database.ps1`. |
| `verify_database.sql` | Read-only sanity queries against the local schema. |
| `test-login.ps1` | Smoke-tests the local `/api/v1/auth/login` endpoint. |
| `CheckSchema.java` | Throwaway JDBC main class used to print the local schema. Not part of the build. |

Schema changes belong in Flyway migrations under
`tradeasset-backend/src/main/resources/db/migration`, never in these scripts.
