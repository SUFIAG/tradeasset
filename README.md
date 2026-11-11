# TradeAsset — Trade Asset Risk Management

Trade finance and trade-asset risk management platform. Spring Boot backend, React frontend,
PostgreSQL, Kafka, Redis, MinIO.

> **Status: pre-MVP.** This platform is not production ready and must not be exposed to any
> network beyond localhost. See [`docs/MVP-GAP-ANALYSIS.md`](docs/MVP-GAP-ANALYSIS.md) for the
> open Sev-1 findings and the phased plan that closes them.

---

## Repository layout

```
tradeasset-backend/    Spring Boot 3.3 application (Java 17, hexagonal architecture)
tradeasset-frontend/   React 19 + Vite + TypeScript single-page application
docs/                  Architecture, gap analysis, decision records
tools/dev/             Local development utilities — never run against production
docker-compose.yml     Local infrastructure (PostgreSQL, Kafka, Redis, MinIO)
```

There is exactly one backend source tree. If you find a second copy, it is a mistake — delete it.

---

## Prerequisites

| Tool | Version |
|---|---|
| JDK | 17 |
| Node.js | 20 or later |
| Docker | with Compose v2 |

---

## Running locally

**1. Start infrastructure**

```bash
docker compose up -d
```

**2. Start the backend**

```bash
cd tradeasset-backend
./mvnw spring-boot:run
```

The API listens on `http://localhost:8080`. OpenAPI UI: `http://localhost:8080/swagger-ui.html`.

**3. Start the frontend**

```bash
cd tradeasset-frontend
cp .env.example .env.local
npm install
npm run dev
```

The application is served on `http://localhost:5173`.

---

## Build and test

```bash
# Backend
cd tradeasset-backend && ./mvnw clean verify

# Frontend
cd tradeasset-frontend && npm run build && npm run lint
```

---

## Configuration

No secret is ever committed. The backend reads all credentials from environment variables and
fails fast at startup when a required production secret is missing.

| Variable | Purpose |
|---|---|
| `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` | PostgreSQL connection |
| `JWT_SECRET` | Token signing key — minimum 256 bits, no default outside local |
| `MINIO_ACCESS_KEY`, `MINIO_SECRET_KEY` | Object storage credentials |
| `CORS_ALLOWED_ORIGINS` | Comma-separated allow-list |

Frontend variables are listed in `tradeasset-frontend/.env.example`.

---

## Documentation

| Document | Contents |
|---|---|
| [`docs/MVP-GAP-ANALYSIS.md`](docs/MVP-GAP-ANALYSIS.md) | Full audit, severity-ranked findings, phased implementation plan |
| `docs/adr/` | Architecture decision records |

---

## Contributing

Read `CLAUDE.md` before making changes. The non-negotiable rules: no floating-point money, no
in-place balance mutation, no secret in the repository, and no endpoint without an explicit
authorisation rule.
