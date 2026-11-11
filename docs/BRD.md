# Business Requirements Document — TradeAsset

**Product:** TradeAsset — Trade Asset Risk Management (TARM)
**Version:** 1.0
**Date:** 2026-10-09
**Status:** Approved baseline. This document is the single source of truth for business rules.

---

## 0. How to read this document

Every requirement has an ID (`REQ-<MODULE>-<NNN>`), a rule, and the reason the rule exists.
The reason matters: when a rule is ambiguous in a future situation, the reason decides the answer.

A requirement is **done** when it is implemented in the backend, reachable from the frontend,
covered by a test, and enforced server-side. Client-side validation is a convenience, never a
control.

---

## 1. What the product is

TradeAsset is a trade-finance operations and risk platform for a bank or a corporate treasury
in the GCC. It handles the full life of four trade instruments — letters of credit, bank
guarantees, documentary collections and supply-chain finance — and wraps them in the controls a
regulated institution needs: customer onboarding and KYC, sanctions screening, credit limits and
exposure, document handling with AI extraction, maker-checker approvals, and an audit trail.

**What it is not.** It is not a core banking system and it does not move money. It instructs,
records and controls. Actual settlement happens in the bank's payment rails; TradeAsset records
the instruction and reconciles the outcome.

### 1.1 Users

| Role | What they do |
|---|---|
| `MAKER` | Creates and edits transactions. Cannot approve their own work. |
| `CHECKER` | Reviews and approves what a maker created. |
| `APPROVER` | Authorises issuance and payment above a maker-checker pair. |
| `COMPLIANCE_OFFICER` | Reviews screening hits, clears false positives, escalates. |
| `RISK_OFFICER` | Sets limits and ratings, reviews exposure and concentration. |
| `OPERATIONS` | Handles documents, shipments, settlement follow-up. |
| `AUDITOR` | Read-only access to everything, including the audit trail. |
| `SYSTEM_ADMIN` | Manages users, roles and configuration. Cannot approve transactions. |
| `CUSTOMER_USER` | External applicant. Sees only their own transactions. |

### 1.2 Governing rules

Letters of credit follow **UCP 600** with **ISBP 821**. Demand guarantees follow **URDG 758**.
Documentary collections follow **URC 522**. Trade terms are **Incoterms 2020**. Where this
document and an ICC rule disagree, this document wins and the difference is deliberate.

---

## 2. Cross-cutting requirements

These apply to every module. They are listed once and are not repeated per module.

### 2.1 Identity and access

| ID | Requirement | Reason |
|---|---|---|
| REQ-SEC-001 | Every endpoint except login, registration, password reset, CAPTCHA, health and the API documentation requires a valid access token. Anything unlisted is denied. | A default-allow API grows holes faster than anyone can patch them. |
| REQ-SEC-002 | The caller's user ID, tenant ID and roles come from the verified token. A request header carrying an identity is ignored. | A header is client input. Treating it as identity lets any caller become any user in any tenant. |
| REQ-SEC-003 | Access tokens expire in 15 minutes. Refresh tokens expire in 7 days and are rotated on each use. A used refresh token is invalid. | Short access tokens limit the damage of a leak. Rotation makes a stolen refresh token detectable. |
| REQ-SEC-004 | Logout revokes the presented refresh token immediately. | A logout that leaves a working token is not a logout. |
| REQ-SEC-005 | Five failed logins lock the account for 30 minutes. CAPTCHA is required after three failures from one address. | Blunts credential stuffing without locking out honest users permanently. |
| REQ-SEC-006 | Passwords: minimum 12 characters, with upper case, lower case, digit and symbol. Stored with BCrypt, cost 12. | Trade finance credentials are high value. |
| REQ-SEC-007 | MFA is available to every user and mandatory for `APPROVER`, `COMPLIANCE_OFFICER` and `SYSTEM_ADMIN`. | These roles can release money or change who can. |
| REQ-SEC-008 | A read or a write against a record outside the caller's tenant returns 404, never 403. | 403 confirms the record exists. That is an information leak across tenants. |
| REQ-SEC-009 | No secret is committed. The application refuses to start in the `prod` profile when `JWT_SECRET`, `DB_PASSWORD` or the storage credentials are absent. | A default credential that reaches production is a breach waiting for a date. |

### 2.2 Money

| ID | Requirement | Reason |
|---|---|---|
| REQ-MON-001 | Every monetary value is an amount plus an ISO 4217 currency, never an amount alone. | An amount without a currency is not a number, it is a guess. |
| REQ-MON-002 | Scale comes from the currency. KWD, BHD, JOD and OMR have 3 minor units; JPY has 0; most others have 2. | A GCC product sees all of these. A hardcoded scale of 2 silently corrupts KWD. |
| REQ-MON-003 | Arithmetic between different currencies throws. Conversion is explicit, with the rate and its source recorded. | Implicit conversion produces numbers nobody can explain afterwards. |
| REQ-MON-004 | No monetary value is ever held in `float`, `double` or a JavaScript `number` used for arithmetic. | Binary floating point cannot represent 0.10. Trade finance cannot absorb that. |
| REQ-MON-005 | Amounts are formatted by the server and rendered by the client as given. | One formatting implementation, one place to fix, consistent across every screen and export. |
| REQ-MON-006 | Every change to an instrument's utilised or available amount is recorded as a movement with a reason, actor and timestamp. The current balance is derived from the movements. | "Why is this balance what it is" must be answerable years later. |
| REQ-MON-007 | A posted movement is never edited or deleted. A mistake is corrected by a reversing movement. | An editable history is not a history. |

### 2.3 Idempotency and reliability

| ID | Requirement | Reason |
|---|---|---|
| REQ-REL-001 | Every mutating endpoint accepts an `Idempotency-Key` header. A repeated key with an identical request body replays the stored response and performs no new work. A repeated key with a different body returns 409. | A user double-click or a network retry must not issue two LCs. |
| REQ-REL-002 | An outbound call that times out leaves the transaction in `PENDING`, never `FAILED`. A reconciliation job resolves it by querying the counterparty. | A timeout means "unknown", and treating unknown as failure is how money goes missing. |
| REQ-REL-003 | Every state change is written to the audit trail with actor, action, before, after, timestamp and source address. | This is the evidence in a dispute or an inspection. |

### 2.4 State and time

| ID | Requirement | Reason |
|---|---|---|
| REQ-STA-001 | Each instrument has one explicit state machine. An illegal transition is rejected with a typed error naming the current state and the legal next states. | Scattered `if (status != X) throw` guards cannot be audited or exhaustively tested. |
| REQ-STA-002 | The API exposes the legal next actions for a record, so the UI enables exactly the buttons the server would accept. | A button that fails on click is a defect the user has to discover. |
| REQ-TIM-001 | Instants are stored in UTC as `TIMESTAMPTZ` and rendered in `Asia/Riyadh`. Calendar dates — expiry, value date, shipment date — are plain dates with no zone. | An LC that expires "on the 30th" expires on the 30th in the beneficiary's calendar, not at a UTC instant. |
| REQ-TIM-002 | Time is read from an injected clock, never from an inline `now()`. | Expiry, tenor and ageing logic that cannot be tested at a chosen date cannot be trusted. |

### 2.5 Maker-checker

| ID | Requirement | Reason |
|---|---|---|
| REQ-MKR-001 | These actions need a second person: issuing an instrument, authorising a payment, setting or changing a credit limit, clearing a sanctions hit, and changing a user's roles. | Every one of them can move money or remove a control. |
| REQ-MKR-002 | The checker cannot be the maker. The system rejects it; it does not merely warn. | Self-approval defeats the entire control. |
| REQ-MKR-003 | The approval record stores both identities, both timestamps and the checker's comment. | Accountability must survive staff turnover. |

### 2.6 AI

| ID | Requirement | Reason |
|---|---|---|
| REQ-AI-001 | AI drafts, extracts, scores, classifies and recommends. AI never approves a payment, grants credit, clears a sanctions hit or changes a role. | These are regulated decisions requiring a responsible human. |
| REQ-AI-002 | Every AI output carries a confidence score, the model identifier, the prompt version and the source it drew from. | A suggestion a reviewer cannot trace is a suggestion they cannot responsibly accept. |
| REQ-AI-003 | Below the configured confidence threshold, the suggestion is withheld and the manual path is used. The threshold is configurable per document type and defaults to 0.85 for auto-fill and 0.50 for show-with-warning. | A low-confidence guess presented confidently is worse than no guess. |
| REQ-AI-004 | Personal identifiers — national ID, passport number, date of birth, IBAN — are masked before any text leaves the platform for a model provider. | PDPL exposure, and it is unnecessary: the model does not need the real number to find the field. |
| REQ-AI-005 | Every AI call is logged with the model, prompt version, token counts, latency, cost and the decision it influenced. A user's accept, edit or reject is stored. | Cost control, and the feedback is the only honest measure of whether the AI helps. |
| REQ-AI-006 | If the AI service fails or times out, the flow continues on the manual path. No user is blocked by an AI outage. | AI is an accelerator, never a dependency on the critical path. |
| REQ-AI-007 | Model output is validated against a schema before use. Malformed output is discarded and treated as a failure under REQ-AI-006. | Casting unvalidated model output into an enum or a UUID is a crash, or worse, a wrong assignment. |

### 2.7 Frontend

| ID | Requirement | Reason |
|---|---|---|
| REQ-UI-001 | Every screen handles five states: loading, empty, success, error, and partial. | "Partial" is the one that gets skipped, and it is the one that happens in production. |
| REQ-UI-002 | A list is paginated, sortable and filterable **on the server**. The client never downloads everything to filter it. | A tenant with 50,000 LCs would otherwise download 50,000 LCs. |
| REQ-UI-003 | A submit button disables while in flight and sends an `Idempotency-Key`. A page refresh mid-submit does not duplicate the record. | The most common way a user creates a duplicate. |
| REQ-UI-004 | Every list row opens a detail view showing the state timeline, the legal next actions, linked documents and the audit history. | A table that cannot be opened is a report, not an application. |
| REQ-UI-005 | All user-facing text goes through i18n. English and Arabic, with full RTL. | The market is the GCC. |
| REQ-UI-006 | WCAG 2.1 AA: keyboard operable, labelled controls, visible focus, sufficient contrast. | Accessibility is a procurement requirement for institutional buyers. |
| REQ-UI-007 | No `console.log` of request or response payloads on a path that runs in production. | Customer data in a browser console is a data leak. |

---

## 3. Module requirements

### 3.1 Identity — `REQ-IDN`

| ID | Requirement |
|---|---|
| REQ-IDN-001 | A user registers with username, email, password, first and last name, and is created in `PENDING` until an administrator activates them. |
| REQ-IDN-002 | Login returns an access token, a refresh token and the user's profile with roles and permissions. |
| REQ-IDN-003 | `GET /auth/me` returns the current user, their roles, their permissions and their tenant. The frontend uses it to restore a session on reload. |
| REQ-IDN-004 | Password reset is requested by email and completed with a single-use token valid for 30 minutes. The request response is identical whether or not the email exists. |
| REQ-IDN-005 | A device fingerprint is recorded at login. A login from an unrecognised device triggers MFA even when MFA is otherwise optional. |
| REQ-IDN-006 | Roles are assigned by a `SYSTEM_ADMIN` and the change requires maker-checker under REQ-MKR-001. |

### 3.2 Customer — `REQ-CUS`

| ID | Requirement |
|---|---|
| REQ-CUS-001 | A customer is an importer, exporter, bank or other counterparty, with a unique customer number within the tenant. |
| REQ-CUS-002 | Lifecycle: `DRAFT` → `PENDING_APPROVAL` → `APPROVED` → `ACTIVE`, with `REJECTED` and `SUSPENDED` as terminal or reversible side states. Only an `ACTIVE` customer can be party to a new transaction. |
| REQ-CUS-003 | KYC must be complete before a customer is approved. Required documents are configurable; the default set is trade licence, board resolution, passport of signatories and proof of address. |
| REQ-CUS-004 | KYC expires 12 months after completion. An expired KYC blocks new transactions but does not affect transactions already issued. |
| REQ-CUS-005 | A credit limit has an amount, a currency, an expiry date and an approver. Setting or changing it requires maker-checker. |
| REQ-CUS-006 | Available credit equals the limit minus committed exposure. It is derived, never stored as the authoritative value. |
| REQ-CUS-007 | Each customer has a risk rating from `LOW` to `CRITICAL`, with the score, the factors behind it and the full override history. |
| REQ-CUS-008 | A customer is screened against sanctions and PEP lists at onboarding and re-screened whenever a list updates. |

### 3.3 Letter of credit — `REQ-LC`

| ID | Requirement |
|---|---|
| REQ-LC-001 | An LC application captures applicant, beneficiary, amount and currency, tenor, expiry date and place, latest shipment date, goods description, Incoterm, required documents, and whether it is confirmed and transferable. |
| REQ-LC-002 | Lifecycle: `DRAFT` → `SUBMITTED` → `APPROVED` → `ISSUED` → `DOCUMENTS_RECEIVED` → `DOCUMENTS_ACCEPTED` → `PAYMENT_AUTHORIZED` → `PAID` → `CLOSED`, with `REJECTED`, `DOCUMENTS_REJECTED`, `AMENDED`, `EXPIRED` and `CANCELLED` as branches. Every transition is validated against REQ-STA-001. |
| REQ-LC-003 | Issuance is blocked unless the applicant is `ACTIVE`, KYC is valid, compliance screening has passed, and the amount fits within available credit. The limit check is a real calculation, not a flag. |
| REQ-LC-004 | Issuance consumes the applicant's available credit and records the movement under REQ-MON-006. |
| REQ-LC-005 | The presentation period defaults to 21 days after shipment and never runs past the LC expiry date, per UCP 600 article 14(c). |
| REQ-LC-006 | Document examination produces a discrepancy list. Each discrepancy names the document, the field, the LC term it breaches and the ISBP reference. |
| REQ-LC-007 | Documents are accepted, refused with the discrepancy list, or accepted with discrepancies waived by the applicant. A refusal notice must go out within five banking days, per UCP 600 article 16(d). |
| REQ-LC-008 | Payment authorisation requires accepted documents and maker-checker. The authorised amount cannot exceed the available amount. |
| REQ-LC-009 | A partial drawing reduces the available amount by the drawn amount and leaves the LC open until it is exhausted or expires. |
| REQ-LC-010 | An amendment requires consent from the applicant, the issuing bank and the beneficiary. Until all consent is recorded, the original terms stand, per UCP 600 article 10(a). |
| REQ-LC-011 | A rejected amendment is recorded with its reason and the LC returns to its previous state. The rejection stays in the history. |
| REQ-LC-012 | An LC expires automatically at its expiry date if it has not been drawn. Expiry releases the reserved credit. |
| REQ-LC-013 | SWIFT MT700 is generated on issuance and MT707 on amendment, and both are stored against the LC. |

### 3.4 Bank guarantee — `REQ-BG`

| ID | Requirement |
|---|---|
| REQ-BG-001 | Types: performance, advance payment, bid bond, payment, retention and customs. |
| REQ-BG-002 | Lifecycle: `DRAFT` → `SUBMITTED` → `APPROVED` → `ISSUED` → `ACTIVE`, then `CLAIMED`, `EXPIRED`, `RELEASED` or `CANCELLED`. |
| REQ-BG-003 | Issuance consumes the applicant's credit for the full guarantee amount as contingent exposure. |
| REQ-BG-004 | A claim records the amount, the date, the reason and the supporting documents, and is checked against the guarantee terms before approval. A claim cannot exceed the outstanding amount. |
| REQ-BG-005 | An approved claim reduces the outstanding amount and records the movement. |
| REQ-BG-006 | Extension and reduction are amendments and require the beneficiary's consent. |
| REQ-BG-007 | Release — by the beneficiary or by expiry — frees the remaining contingent exposure. |
| REQ-BG-008 | Guarantees expiring within 30 days appear on a monitoring list with the number of days remaining. |

### 3.5 Documentary collection — `REQ-DC`

| ID | Requirement |
|---|---|
| REQ-DC-001 | Both types are supported: documents against payment (D/P) and documents against acceptance (D/A). |
| REQ-DC-002 | Lifecycle: `DRAFT` → `SUBMITTED` → `PRESENTED` → `ACCEPTED` or `PAID` → `SETTLED`, with `DISHONOURED` as a branch. |
| REQ-DC-003 | The collection instruction is recorded verbatim and governs what the presenting bank may do, per URC 522 article 4. |
| REQ-DC-004 | Documents are released to the drawee only on payment (D/P) or on acceptance of the draft (D/A). |
| REQ-DC-005 | A dishonour is recorded with its reason and triggers a notice to the principal within the period in the instruction. |
| REQ-DC-006 | Bank charges and collection charges are recorded separately from the principal amount and are never netted into it. |

### 3.6 Supply chain finance — `REQ-SCF`

| ID | Requirement |
|---|---|
| REQ-SCF-001 | Supported products: invoice discounting, reverse factoring and payables finance. |
| REQ-SCF-002 | Lifecycle: `REQUESTED` → `APPROVED` → `DISBURSED` → `REPAID`, with `REJECTED` and `OVERDUE` as branches. |
| REQ-SCF-003 | A financing request is rejected if the same invoice is already financed. Duplicate detection matches on invoice number, supplier and amount. |
| REQ-SCF-004 | The finance amount equals the invoice amount multiplied by the advance rate, and cannot exceed the buyer's available limit. |
| REQ-SCF-005 | The discount is calculated from the rate and the days to maturity, and is recorded as a separate fee, never deducted silently. |
| REQ-SCF-006 | Facilities due within 7 days and facilities past due appear on their own monitoring lists. |
| REQ-SCF-007 | Repayment settles the facility and releases the limit. A partial repayment reduces the outstanding amount proportionally. |

### 3.7 Document — `REQ-DOC`

| ID | Requirement |
|---|---|
| REQ-DOC-001 | A document is uploaded against an entity — customer, LC, BG, collection, SCF facility or shipment — with a type and a version. Replacing a document creates a new version; nothing is overwritten. |
| REQ-DOC-002 | Maximum file size is 50 MB. Accepted types: PDF, JPEG, PNG, TIFF, DOCX, XLSX. The content type is verified from the file's own bytes, not from the declared header. |
| REQ-DOC-003 | Every upload is virus scanned before it is stored. A scan failure quarantines the file. |
| REQ-DOC-004 | Documents are stored in object storage; the database holds only metadata and the storage key. |
| REQ-DOC-005 | A download is authorised against the parent entity. A document ID is not an access grant. |
| REQ-DOC-006 | AI classifies the document type and extracts its fields. The extraction is shown field by field with its confidence and the region it came from, and a human confirms it. |
| REQ-DOC-007 | A document is `PENDING`, `VERIFIED` or `REJECTED`. Only a human sets `VERIFIED`. |

### 3.8 Compliance — `REQ-CMP`

| ID | Requirement |
|---|---|
| REQ-CMP-001 | Screening runs against sanctions, PEP and adverse-media lists for every customer at onboarding and for every counterparty on every transaction. |
| REQ-CMP-002 | Matching is fuzzy and the score is recorded. The threshold is configurable per list. |
| REQ-CMP-003 | Any hit blocks the transaction until a `COMPLIANCE_OFFICER` dispositions it. There is no automatic clearance. |
| REQ-CMP-004 | A hit is approved, rejected or cleared as a false positive, always with a written reason. Clearing requires maker-checker. |
| REQ-CMP-005 | AI may rank hits by likely false positive and draft the case narrative. It may never dispose of a hit. |
| REQ-CMP-006 | TBML red flags are detected and raised for review: price far from market, unusual routing, a counterparty in a high-risk jurisdiction, goods inconsistent with the customer's business, and round-amount invoices. |
| REQ-CMP-007 | Screening results are retained for 10 years and are immutable. |

### 3.9 Risk — `REQ-RSK`

| ID | Requirement |
|---|---|
| REQ-RSK-001 | Limits exist per counterparty, per group, per country and per product, arranged in a hierarchy where the tightest limit binds. |
| REQ-RSK-002 | Exposure is calculated as committed plus utilised plus contingent, net of collateral, and is recomputed on every transaction. |
| REQ-RSK-003 | The pre-transaction limit check runs before issuance and blocks it when headroom is insufficient. A temporary excess requires `RISK_OFFICER` approval and is recorded. |
| REQ-RSK-004 | A risk assessment records its inputs, its factors, its weights, its score and its model version, and is reproducible from stored inputs. |
| REQ-RSK-005 | A deterministic rules engine produces the score. AI contributes an advisory signal that is shown separately and never overwrites the rules-based result. |
| REQ-RSK-006 | Concentration against configurable thresholds is monitored by single name, by country and by sector, and a breach raises an alert. |
| REQ-RSK-007 | High-risk entities appear on a watchlist with an owner and a review date. |

### 3.10 Workflow — `REQ-WFL`

| ID | Requirement |
|---|---|
| REQ-WFL-001 | An approval workflow is created automatically when a transaction is submitted and routed by type, amount and risk. |
| REQ-WFL-002 | A task has an assignee, a due date, a priority and an SLA. The maker is never a valid assignee for their own task. |
| REQ-WFL-003 | An SLA breach escalates to the assignee's supervisor and raises an alert. |
| REQ-WFL-004 | AI may suggest an assignee and a priority. The suggestion is shown with its confidence and can be overridden; it is never applied silently. |
| REQ-WFL-005 | A user sees their own task queue with SLA countdowns, sorted by urgency. |

### 3.11 Shipment — `REQ-SHP`

| ID | Requirement |
|---|---|
| REQ-SHP-001 | A shipment records the carrier, vessel or flight, origin, destination, estimated and actual dates, and is linked to its LC, collection or SCF facility. |
| REQ-SHP-002 | Milestones recorded: departure, arrival, customs clearance, delivery. Each has a timestamp and a source. |
| REQ-SHP-003 | A shipment is delayed when the actual date passes the estimate, and delayed shipments appear on a monitoring list. |
| REQ-SHP-004 | AI cross-checks transport documents against the shipment record and flags inconsistent vessel, port or route data, and dual-use goods. |

### 3.12 Analytics — `REQ-ANL`

| ID | Requirement |
|---|---|
| REQ-ANL-001 | The dashboard shows total exposure, counts of active instruments, pending approvals, open compliance alerts and SLA breaches, all scoped to the caller's tenant and role. |
| REQ-ANL-002 | Every figure on a dashboard comes from the database. No figure is hardcoded and no placeholder is shown as if it were data. |
| REQ-ANL-003 | Monetary aggregates are computed in decimal and are grouped by currency. Totals across currencies are shown only with an explicit conversion rate and its date. |
| REQ-ANL-004 | Reports export to CSV and XLSX, generated server-side, reflecting the filters that are on screen. |

### 3.13 Knowledge and copilot — `REQ-KNW`

| ID | Requirement |
|---|---|
| REQ-KNW-001 | The knowledge base answers questions about ICC rules and internal policy, and every answer cites its source passage. |
| REQ-KNW-002 | An answer with no supporting source says so. It does not improvise. |
| REQ-KNW-003 | The copilot can explain a transaction's status, summarise its risk, navigate the user, and draft text. Any write it proposes is confirmed by the user before it happens. |
| REQ-KNW-004 | The copilot sees only data the signed-in user is authorised to see. |

---

## 4. Non-functional requirements

| ID | Requirement |
|---|---|
| REQ-NFR-001 | A list endpoint responds within 500 ms at the 95th percentile for a tenant holding 100,000 records. |
| REQ-NFR-002 | An AI extraction returns within 30 seconds or falls back to the manual path. |
| REQ-NFR-003 | The system supports 500 concurrent users and 10,000 transactions per day. |
| REQ-NFR-004 | Availability target 99.5% during GCC business hours. |
| REQ-NFR-005 | RPO 15 minutes, RTO 4 hours. Backup restore is rehearsed and the date recorded. |
| REQ-NFR-006 | All data is stored in-region. No personal data leaves the region except masked text sent to an AI provider under REQ-AI-004. |
| REQ-NFR-007 | Audit records are retained for 10 years, transaction records for 7 years after closure. |

---

## 5. Out of scope for version 1.0

Standby letters of credit under ISP98, bank-to-bank reimbursement under URR 725, electronic
presentation under eUCP, ZATCA e-invoicing, a public API for third-party developers, and a
customer-facing mobile application. Each is a deliberate exclusion, not an oversight.

---

## 6. Acceptance

Version 1.0 is accepted when:

1. A user registers, logs in, and their session survives a page refresh and a token expiry.
2. A maker creates a customer, completes KYC, and a checker approves it; the maker cannot.
3. A risk officer sets a credit limit; a second person approves it.
4. A maker applies for an LC, screening and the limit check run, a checker approves, and the LC
   is issued with an MT700 stored against it.
5. Documents are uploaded, AI extracts the fields, a human confirms them, a discrepancy is
   raised and waived, and payment is authorised by a second person.
6. The applicant's available credit, exposure and dashboard figures all change by the right
   amount and reconcile to the recorded movements.
7. Every one of the above is in the audit trail with its actor, and no step was reachable without
   authentication.
