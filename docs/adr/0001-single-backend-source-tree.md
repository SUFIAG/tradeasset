# ADR 0001 — Single backend source tree

- **Status:** Accepted
- **Date:** 2026-09-09
- **Deciders:** Engineering

## Context

The repository contained two backend source trees: `src/` at the repository root and
`tradeasset-backend/src/`. A file-by-file SHA-256 comparison showed 642 files on each side with
zero differences, and the two `pom.xml` files were byte identical. Every loose file at the
repository root — `mvnw`, `mvnw.cmd`, `Dockerfile.ocr`, `CheckSchema.java`, the database
scripts and three committed `.class` files — was also duplicated inside `tradeasset-backend/`.

Only `tradeasset-backend/target/` existed, which means the root tree had never been compiled.
The root `.idea/asset.iml` described a generic Java module rather than a Maven project,
indicating the IDE project was opened on `tradeasset-backend`.

Two trees for one application is a correctness hazard: an edit applied to one side is invisible
on the other, and there is no build step that would detect the divergence.

## Decision

`tradeasset-backend/` is the single backend source tree.

- The root `src/`, `pom.xml`, `.mvn/`, `mvnw`, `mvnw.cmd` and `Dockerfile.ocr` were removed.
- Committed build artefacts (`*.class`) were removed and are now ignored.
- Local development utilities were moved to `tools/dev/` rather than deleted.
- `docker-compose.yml` stays at the repository root because it orchestrates infrastructure for
  both the backend and the frontend.

The naming is now symmetric: `tradeasset-backend/` and `tradeasset-frontend/`.

## Consequences

- Build and run commands are executed from `tradeasset-backend/`.
- IDE projects opened against the repository root need one re-import.
- Nothing was lost. Both trees were tracked in commit `fb665d4`, so the removed copy remains
  recoverable from git history.
- Verified after the change: `./mvnw clean compile` completes with exit code 0.
