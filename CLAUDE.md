# Contributor and agent guide — TradeAsset

Read this before changing code. It encodes the rules that keep a money-handling product
auditable. Violating one of them is a blocking review comment, not a preference.

---

## Repository layout

```
tradeasset-backend/    Spring Boot 3.3, Java 17, hexagonal architecture
tradeasset-frontend/   React 19, Vite, TypeScript
docs/                  Gap analysis and architecture decision records
tools/dev/             Local-only utilities. Never point these at a shared environment.
```

There is one backend source tree. If a second copy appears, delete it — divergence between
duplicated trees silently loses work.

---

## Commands

| Task | Command |
|---|---|
| Backend build and test | `cd tradeasset-backend && ./mvnw clean verify` |
| Backend run | `cd tradeasset-backend && ./mvnw spring-boot:run` |
| Frontend build | `cd tradeasset-frontend && npm run build` |
| Frontend lint | `cd tradeasset-frontend && npm run lint` |
| Local infrastructure | `docker compose up -d` |

---

## Architecture

The backend is hexagonal and this is enforced by `HexagonalArchitectureTest` (ArchUnit).
Keep the layering intact:

```
adapter/in/rest      HTTP controllers, request and response DTOs
adapter/out/*        Persistence, messaging, external system clients
application/port/in  Use case interfaces and commands
application/port/out Interfaces the domain needs from the outside
application/service  Use case implementations
domain/model         Aggregates, value objects, domain exceptions
domain/event         Domain events
```

Rules:

- The `domain` package depends on nothing outside itself. No Spring, no JPA, no Jackson.
- Controllers depend on `port/in` only. They never touch repositories or JPA entities.
- Persistence adapters map between JPA entities and domain models. Never expose a JPA entity
  through an API.
- One aggregate per transaction. Cross-aggregate consistency goes through events.

---

## Money rules — non-negotiable

1. **Never use `float` or `double` for a monetary value.** Use the `Money` value object.
2. **Never mutate a balance in place.** Every movement is a balanced set of ledger entries.
   Balances are derived from the journal, or materialised with a rebuild-from-journal proof.
3. **Never delete or edit a posted row.** Correct with a compensating entry.
4. **Never assume scale 2.** Currency drives the number of minor units. KWD, BHD, JOD and OMR
   use 3. JPY uses 0. A GCC product will see all of these.
5. **Never mix currencies implicitly.** Arithmetic across currencies throws.
6. **Every mutating endpoint accepts `Idempotency-Key`** and replays the stored response for a
   repeated key with a matching request hash.
7. **A timeout is not a failure.** Unknown outcomes enter a pending state and are resolved by a
   status-query reconciliation loop.
8. **Fees, VAT and FX margin are separate ledger entries.** Never fold them into a net amount.
9. **Never compute money in the browser.** The server returns a formatted amount and a currency.

---

## Time rules

- Inject `java.time.Clock`. Never call `LocalDate.now()`, `LocalDateTime.now()` or
  `Instant.now()` inline — it makes the behaviour untestable.
- Store every instant as `TIMESTAMPTZ` in UTC. Render in `Asia/Riyadh`.
- Use `Instant` or `OffsetDateTime` for instants. `LocalDate` is only for genuine calendar dates
  such as a value date or an expiry date.

---

## Security rules

- **Deny by default.** Every endpoint carries an explicit authorisation rule. A new controller
  without one fails review.
- **Identity comes from the authenticated principal, never from a request header.** Tenant ID,
  user ID and roles are read from the security context. A client-supplied `X-Tenant-ID` or
  `X-User-Id` is untrusted input.
- **Check resource ownership on every read and every mutation.** An ID in a path is not
  authorisation.
- **No secret in the repository.** No default credential that could reach a deployed
  environment. Production configuration fails fast when a secret is absent.
- **Never log** a national ID, IBAN, passport number, token, OTP, password or full document
  content.
- **Money-out, limit changes and role changes require maker-checker.** The maker cannot be the
  checker.

---

## Error handling

- All errors leave the API as RFC 7807 problem details with a stable machine-readable code.
- Never return a stack trace, a SQL fragment, an internal class name, or raw text from an
  external system.
- Error codes are a versioned contract. The frontend maps them to localised, actionable
  messages. Adding a code is additive; changing the meaning of one is breaking.

---

## Frontend rules

- Every screen implements five states: loading, empty, success, error, partial or degraded.
- Submit buttons are idempotent: disabled while in flight, sending an `Idempotency-Key`, and
  safe across a double tap and a page refresh.
- Amounts are rendered from the server-provided formatted value and currency. No client-side
  arithmetic or `toFixed`.
- Every string goes through i18n. English and Arabic, with full RTL support. Use CSS logical
  properties (`margin-inline-start`, not `margin-left`).
- Data fetching goes through React Query. No raw `useEffect` fetch loops.
- Drag-and-drop is keyboard operable and announced to screen readers.
- Target WCAG 2.1 AA.
- No `console.log` on a code path that can run in production.

---

## Testing expectations

| Area | Requirement |
|---|---|
| Money maths, fees, FX, rounding | Exhaustive unit coverage, including the property test that every journal sums to zero |
| State machines | Full transition matrix, legal and illegal |
| Idempotency | Concurrent duplicate requests produce exactly one effect |
| Concurrency | Parallel debits produce no lost update and no negative balance |
| Persistence and messaging | Testcontainers with real PostgreSQL and real Kafka |
| External systems | Contract tests against recorded fixtures, covering declines, timeouts and duplicates |

---

## Database migrations

- Flyway, forward only, versioned, never edited after merge.
- Column drops are two-phase and backward compatible.
- No manual change to a shared database. Break-glass access is logged.

---

## Definition of done

A change is done when it builds, its tests pass, it carries an explicit authorisation rule, it
emits no secret or PII into logs, its errors are typed, and its user-facing strings are
translated. Not before.
