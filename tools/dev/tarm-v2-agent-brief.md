# TARM v2 backend completion — shared brief for module builders

Repository root: `C:/Users/sufyan.abdulghani/Downloads/Softwares/work/Fintech Products/Tradeasset`
Backend: `tradeasset-backend/` (Spring Boot 3.3, Java 17). Base package `com.tradesphere.ai`.

Read FIRST, fully: `CLAUDE.md` (repo root) and `docs/BRD.md`. They are binding. Every requirement ID
assigned to your module must be implemented server-side.

## Situation

The backend is mid-rewrite on branch `revamp/tarm-v2`. A new shared kernel is finished and FROZEN.
Legacy module code was deleted (identity, security, customer services/adapters, LC, compliance,
risk, workflow, analytics) or is still legacy (BG, DC, SCF, shipment, document, knowledge, copilot,
integration). Legacy code uses `X-Tenant-ID` headers, `double`, `LocalDateTime.now()`, fake roles
such as `BG_CREATOR` — all forbidden. Rewrite to the v2 standard; delete legacy files in your own
packages freely (also their legacy tests).

Ten builders work IN PARALLEL in the same working tree. You own only the packages listed for you.

## Hard boundaries

- Edit ONLY files inside your owned packages (main and test), your migration file(s) and your
  devdata file. Do NOT edit `shared/**`, `config/**`, `pom.xml`, `application.yml`,
  `AssetApplication.java`, other modules, or migrations V1–V42.
- No new Maven dependencies. Available: Spring web/jdbc/jpa/security/validation/mail/cache,
  PostgreSQL, Flyway, jjwt 0.12.5, POI, PDFBox, Tika, tess4j, MinIO, Lombok, Jackson, springdoc,
  JUnit 5, Mockito, AssertJ, spring-security-test, ArchUnit.
- New config: read with `@Value("${tradesphere.<module>.<key>:<safe default>}")`. Never a secret default.
- If you truly need a shared-kernel change, do NOT make it: describe the exact change in your
  final report and work around it locally.
- Do not git commit, stash, reset or checkout.

## Shared kernel you must use (read the sources)

`src/main/java/com/tradesphere/ai/shared/`:

- `security/AuthenticatedUser` — the caller. Controllers take
  `@AuthenticationPrincipal AuthenticatedUser user`. Tenant, user, roles ONLY from here.
  `user.isCustomerUser()` + `user.customerId()` scope external users to their own customer's records.
- `security/Roles` — role codes. `@PreAuthorize("hasAnyRole('MAKER','CHECKER',...)")` on EVERY
  handler method (startup fails otherwise, `config/EndpointAuthorizationVerifier`).
  `@PublicEndpoint` only for auth endpoints already listed in `config/SecurityConfiguration`.
- `domain/valueobject/Money` — all money. `web/MoneyInput` (request, decimal string) and
  `web/MoneyView` (response: amount string + currency + formatted). Never float/double/JS number.
- `domain/error/*` — `NotFoundException` (404, also for other-tenant records, REQ-SEC-008),
  `ValidationException` (400), `ConflictException` (409), `ConcurrentModificationException`,
  `IllegalStateTransitionException` (409 with current state + legal actions, REQ-STA-001),
  `ForbiddenActionException` (403, e.g. four-eyes), `BusinessRuleViolationException` (422).
  Codes are UPPER_SNAKE, prefixed by module (e.g. `BG_CLAIM_EXCEEDS_OUTSTANDING`).
  `web/GlobalExceptionHandler` renders all of them as RFC 7807.
- `application/service/AuditTrail` — record EVERY state change (actor, action, from, to, comment,
  details) in the same transaction (REQ-REL-003). `timelineOf(...)` for detail views.
- `application/service/Ledger` + `domain/journal/LedgerAccounts` + `Movement` — the ONLY way to
  change balances (REQ-MON-006/007). Exposure on `CREDIT_EXPOSURE` with customerId set. Fees and
  VAT separate entries on `FEE` / `VAT`. Balances are derived; never store an authoritative balance
  column (a cached column is allowed only if rebuilt from the journal in the same tx).
- `application/service/MakerChecker` + `ApprovalRecord` — for REQ-MKR-001 actions: request by
  maker, approve/reject by a different checker. Self-approval throws.
- `application/service/EntityAccessRegistry` + `port/out/access/EntityAccessChecker` — every module
  owning records registers ONE `@Component` implementing `EntityAccessChecker` for its entity types.
- Cross-module ports in `application/port/out/**` (each implemented by exactly one module):
  `CustomerDirectory` (customer), `ScreeningGateway`, `TbmlGateway` (compliance),
  `CreditRiskGateway` (risk), `WorkflowGateway`, `NotificationGateway`, `MailSenderPort`
  (workflow), `UserDirectory` (identity), `DocumentDirectory` (document), `AiGateway` (knowledge).
  Consume other modules ONLY through these ports and `shared/application/event/IntegrationEvents`
  (publish with `ApplicationEventPublisher`, listen with `@TransactionalEventListener`).
  Never import another module's package.
- `domain/trade/EntityTypes` — entity type codes. `domain/time/BusinessCalendar` — Riyadh dates,
  banking days. Inject `java.time.Clock`; never call `now()` inline.
- `query/PageQuery` + `PageResult` — every list endpoint: `page`, `size`, `sort` params, server-side
  filters, whitelisted sort via `PageQuery.orderBy(map)` (REQ-UI-002).
- `adapter/in/rest/TabularExport` — CSV/XLSX export (REQ-ANL-004): instrument and register lists
  expose `GET .../export?format=csv|xlsx` with the same filters as the list.
- `web/idempotency/IdempotencyFilter` already handles `Idempotency-Key` for every mutating `/api`
  call. Nothing to do per endpoint, but make mutations safe under replay.
- Persistence examples: `shared/adapter/out/persistence/Jdbc*Adapter.java` and `JdbcRows`
  (NamedParameterJdbcTemplate + explicit SQL). Prefer this style. Use a `version BIGINT` column and
  `UPDATE ... WHERE id=? AND version=?` for optimistic locking; 0 rows -> `ConcurrentModificationException`.
  Always filter by `tenant_id`.

Look at `lc/domain/model/LcStateMachine.java` as the pattern for one explicit state machine per
instrument (table of (state, action) -> state; `legalActions(state)`).

## Standard shape of an instrument/record module

```
<module>/domain/model        aggregate, value objects, <X>Status, <X>Action, <X>StateMachine (pure Java)
<module>/application/port/in use-case interfaces + command records
<module>/application/port/out repository interfaces
<module>/application/service  @Service @Transactional implementations
<module>/adapter/in/rest      controller + request/response DTO records
<module>/adapter/out/persistence  Jdbc repository adapters
<module>/adapter/out/access   EntityAccessChecker implementation
```

- Domain: no Spring, no JPA, no Jackson, no Lombok-generated Spring stuff (ArchUnit enforces).
  Application must not import adapter. Controllers depend on port/in only.
- Detail response (REQ-UI-004, REQ-STA-002) includes: fields, MoneyView amounts and balances,
  `status`, `legalActions` (state machine legal actions filtered by the caller's roles and by
  maker-checker: hide approve from the maker of the pending request), `timeline`
  (AuditTrail.timelineOf), `approvals` (MakerChecker.historyOf), `documents`
  (DocumentDirectory.listFor), movements where relevant.
- Ownership on EVERY read and mutation: tenant scoping, and for CUSTOMER_USER only records where
  their customerId is a party. Not visible -> 404.
- New transactions require eligible parties: `CustomerDirectory.transactionBlockers(...)`.
- Submission opens a workflow task via `WorkflowGateway.openTask` (REQ-WFL-001); decisions close it
  via `completeTasks`.
- Issuance-type actions: screening clearance (`ScreeningGateway`), limit check
  (`CreditRiskGateway.checkHeadroom`), then post exposure through the Ledger.
- Outbound calls that time out leave the record in a PENDING sub-state resolved by a scheduled
  reconciliation job (REQ-REL-002).
- Scheduled jobs (`@Scheduled`, scheduling is enabled) run per tenant, use the Clock, and audit via
  `AuditTrail.recordSystem`.
- Never log national ID, IBAN, passport number, token, OTP, password or document content.

## Database

- One forward-only migration per module in `src/main/resources/db/migration`, version assigned
  below, file name `V<n>__<module>_v2.sql`. New v2 tables use the module prefix listed below; do
  not read or write legacy V1 tables (they are deprecated). `TIMESTAMPTZ` for instants, `DATE` for
  calendar dates, `NUMERIC(24,3)` + `CHAR(3)` for money, `tenant_id UUID NOT NULL` on every table,
  indexes on (tenant_id, status) and (tenant_id, created_at).
- Dev seed data (optional, only if your module needs it for a demo) goes in
  `src/main/resources/db/devdata/V9<nn>__dev_<module>.sql` (dev profile only). Dev tenant ID is
  `00000000-0000-0000-0000-000000000001`.

### Table contract (the analytics dashboard queries these; keep the names and these columns)

| Table | Required columns |
|---|---|
| `lc_letters_of_credit` (exists, V41) | tenant_id, status, amount, currency, applicant_id, created_at |
| `bg_guarantees` | id, tenant_id, reference, status, guarantee_type, applicant_id, amount, currency, expiry_date, created_at |
| `dc_collections` | id, tenant_id, reference, status, collection_type, principal_id, drawee_id, amount, currency, created_at |
| `scf_facilities` | id, tenant_id, reference, status, product_type, buyer_id, supplier_id, finance_amount, currency, maturity_date, created_at |
| `shp_shipments` | id, tenant_id, reference, status, estimated_arrival_date, actual_arrival_date, created_at |
| `cus_customers` | id, tenant_id, customer_number, name, status, kyc_status, risk_rating, country, created_at |
| `cmp_screening_hits` | id, tenant_id, status (`OPEN` until dispositioned), created_at |
| `cmp_tbml_alerts` | id, tenant_id, status (`OPEN` until reviewed), created_at |
| `rsk_alerts` | id, tenant_id, status (`OPEN` until acknowledged), alert_type, created_at |
| `wfl_tasks` | id, tenant_id, status (`OPEN`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`), assignee_id, due_at, sla_breached BOOLEAN, created_at |
| `idn_users` | id, tenant_id, username, status, created_at |

## Module assignment

| Builder | Owns packages | Migration | Table prefix | BRD |
|---|---|---|---|---|
| identity | `identity`, `security` (legacy, delete) | V43 | `idn_` | 2.1, 3.1, REQ-MKR for roles |
| customer | `customer` | V44 | `cus_` | 3.2 (credit limit itself lives in risk) |
| compliance | `compliance` | V45 | `cmp_` | 3.8, REQ-CUS-008 |
| risk | `risk` | V46 | `rsk_` | 3.9, REQ-CUS-005/006/007 scoring, FX rates |
| workflow | `workflow`, `notification` (new), `analytics`; legacy `shared/notification/**` is yours to delete when nothing else uses it | V47, V55 | `wfl_`, `ntf_`, `anl_` | 3.10, 3.12 |
| lc | `lc`, `integration` | V48 | `lc_` | 3.3 |
| bg-dc | `bankguarantee`, `collection` | V49, V50 | `bg_`, `dc_` | 3.4, 3.5 |
| scf-shipment | `scf`, `shipment` | V51, V52 | `scf_`, `shp_` | 3.6, 3.11 |
| document | `document` | V53 | `doc_` | 3.7 |
| knowledge | `knowledge`, `copilot`, new `ai` module (AiGateway impl); legacy `shared/ai/**` is yours to delete | V54 | `ai_`, `kn_`, `cop_` | 2.6, 3.13 |

## Build and verify

Use YOUR OWN build directory so parallel builders do not clobber each other:

```
cd tradeasset-backend
./mvnw -q -o compile -Dbuild.dir=target-<builder>
./mvnw -q -o test -Dbuild.dir=target-<builder> -Dtest='com.tradesphere.ai.<module>.**' -Dsurefire.failIfNoSpecifiedTests=false
```

Other builders are writing code at the same time, so compile errors in packages you do not own
are expected and are not yours to fix. Your packages must have ZERO errors. If others' errors
block test compilation, report that your main code compiles and list the tests you wrote.

Tests (no Docker, no Spring context): plain JUnit 5 + Mockito + AssertJ in
`src/test/java/com/tradesphere/ai/<module>/...`. Required: full legal/illegal transition matrix of
every state machine, money/limit/fee calculations including a 3-decimal currency (KWD) and JPY,
maker-checker self-approval rejection, and the key business rules of your BRD section.

## Final report (keep it short)

1. BRD requirement IDs implemented, and any NOT implemented with the reason.
2. Endpoints (method + path + roles).
3. Migration file(s).
4. Compile/test status of your packages.
5. Requested shared-kernel or config changes, if any (exact).
