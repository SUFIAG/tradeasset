# TradeSphere AI - AI-Native Trade Finance Platform

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/License-Proprietary-blue.svg)]()

> Next-generation AI-powered trade finance operations platform built on hexagonal architecture

---

## 🚀 Quick Start

### Prerequisites

- **Java 17+** (OpenJDK or Oracle JDK)
- **Maven 3.8+**
- **Docker Desktop** (for local infrastructure)
- **Git**
- **PostgreSQL 16** (or use Docker)

### Installation

```powershell
# 1. Clone the repository
git clone <repository-url>
cd tradeasset

# 2. Start infrastructure services
docker-compose up -d

# 3. Wait for services to be healthy (30-60 seconds)
docker-compose ps

# 4. Build the application
mvn clean install

# 5. Run the application
mvn spring-boot:run
```

### Access Points

Once the application is running:

- **API**: http://localhost:8080
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Actuator Health**: http://localhost:8080/actuator/health

### Infrastructure Services

- **PostgreSQL**: localhost:5432
  - Database: `tradesphere_platform`
  - Username: `tradesphere`
  - Password: `tradesphere_dev_2026`

- **Redis**: localhost:6379

- **Kafka**: localhost:9092
  - Kafka UI: http://localhost:8090

- **MinIO** (S3-compatible): http://localhost:9000
  - Console: http://localhost:9001
  - Access Key: `tradesphere`
  - Secret Key: `tradesphere_dev_2026`

- **Elasticsearch**: http://localhost:9200
  - Kibana: http://localhost:5601

- **Prometheus**: http://localhost:9090

- **Grafana**: http://localhost:3000
  - Username: `admin`
  - Password: `admin`

---

## 📋 Project Structure

```
tradesphere-ai/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── tradesphere/
│       │           └── ai/
│       │               ├── TradeSphereApplication.java
│       │               │
│       │               ├── shared/                    # Shared Kernel
│       │               │   ├── domain/
│       │               │   │   ├── Entity.java
│       │               │   │   ├── AggregateRoot.java
│       │               │   │   ├── ValueObject.java
│       │               │   │   ├── DomainEvent.java
│       │               │   │   └── valueobject/
│       │               │   │       ├── Money.java
│       │               │   │       └── Address.java
│       │               │   └── application/
│       │               │
│       │               ├── platform/                   # Platform Foundation
│       │               │   ├── identity/              # Identity BC
│       │               │   └── organization/          # Organization BC
│       │               │
│       │               ├── customer/                   # Customer Management BC
│       │               │   ├── domain/
│       │               │   ├── application/
│       │               │   └── adapter/
│       │               │
│       │               ├── trade/                      # Trade Finance BC
│       │               │   ├── domain/
│       │               │   ├── application/
│       │               │   └── adapter/
│       │               │
│       │               ├── document/                   # Document Intelligence BC
│       │               ├── compliance/                 # Compliance BC
│       │               ├── risk/                       # Risk Management BC
│       │               ├── workflow/                   # Workflow Engine BC
│       │               └── ai/                         # AI Services BC
│       │
│       └── resources/
│           ├── application.yml
│           └── db/
│               └── migration/
│                   ├── V1__init_schema.sql
│                   └── V2__create_customer_schema.sql
│
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## 🏗️ Architecture

### Hexagonal Architecture (Ports & Adapters)

```
┌──────────────────────────────────────────────┐
│           Inbound Adapters                   │
│   (REST, GraphQL, Kafka Consumers)           │
└──────────────┬───────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────┐
│          Application Layer                    │
│  (Use Cases, Application Services, DTOs)     │
└──────────────┬───────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────┐
│            Domain Layer                       │
│  (Aggregates, Entities, Value Objects,       │
│   Domain Services, Domain Events)            │
└──────────────┬───────────────────────────────┘
               │
               ▼
┌──────────────────────────────────────────────┐
│          Outbound Adapters                    │
│  (JPA Repositories, Kafka Producers,          │
│   External APIs, Storage)                     │
└──────────────────────────────────────────────┘
```

### Key Principles

1. **Domain-Driven Design (DDD)**
   - Bounded Contexts for each business domain
   - Ubiquitous Language
   - Aggregates for consistency boundaries

2. **Event-Driven Architecture**
   - Domain events for inter-context communication
   - Kafka for event streaming
   - Event sourcing where beneficial

3. **CQRS**
   - Command/Query separation
   - Optimized read models in Elasticsearch

4. **AI-First**
   - AI agents integrated at domain level
   - Spring AI for LLM integration
   - RAG for knowledge management

---

## 🔧 Configuration

### Environment Variables

Create a `.env` file in the project root:

```env
# Database
DATABASE_URL=jdbc:postgresql://localhost:5432/tradesphere_platform
DATABASE_USERNAME=tradesphere
DATABASE_PASSWORD=tradesphere_dev_2026

# JWT
JWT_SECRET=your-secret-key-min-256-bits

# AI APIs
OPENAI_API_KEY=sk-...
ANTHROPIC_API_KEY=sk-ant-...
MISTRAL_API_KEY=...

# Storage
MINIO_ENDPOINT=http://localhost:9000
MINIO_ACCESS_KEY=tradesphere
MINIO_SECRET_KEY=tradesphere_dev_2026

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:4200
```

---

## 🧪 Testing

```powershell
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=CustomerServiceTest

# Run integration tests
mvn verify

# Generate test coverage report
mvn jacoco:report
```

---

## 📚 API Documentation

### Swagger UI

Visit http://localhost:8080/swagger-ui.html for interactive API documentation.

### Key API Endpoints

#### Identity & Authentication
- `POST /api/v1/auth/login` - User login
- `POST /api/v1/auth/refresh` - Refresh token
- `POST /api/v1/users` - Create user
- `GET /api/v1/users/{id}` - Get user details

#### Customer Management
- `POST /api/v1/customers` - Create customer
- `GET /api/v1/customers/{id}` - Get customer details
- `PUT /api/v1/customers/{id}` - Update customer
- `POST /api/v1/customers/{id}/approve` - Approve customer

#### Trade Finance
- `POST /api/v1/trade/lc` - Create LC application
- `GET /api/v1/trade/lc/{id}` - Get LC details
- `POST /api/v1/trade/lc/{id}/issue` - Issue LC
- `POST /api/v1/trade/lc/{id}/amend` - Request LC amendment

---

## 🤖 AI Features

### Document Agent
- Automatic document classification
- Field extraction from trade documents
- Discrepancy detection
- UCP 600 compliance checking

### Compliance Agent
- Real-time sanctions screening
- AML risk assessment
- PEP detection
- Watchlist screening

### Risk Agent
- Country risk assessment
- Customer risk scoring
- Transaction risk analysis
- Fraud detection

### Knowledge Agent (RAG)
- UCP 600 / ISBP knowledge base
- Natural language queries
- Context-aware responses

### Copilot Agent
- Natural language interface
- Query assistance
- Report generation
- Decision support

---

## 🚀 Deployment

### Docker

```powershell
# Build Docker image
docker build -t tradesphere-ai:latest .

# Run container
docker run -p 8080:8080 -e SPRING_PROFILES_ACTIVE=prod tradesphere-ai:latest
```

### Kubernetes

```powershell
# Apply configurations
kubectl apply -f k8s/

# Check deployment
kubectl get pods -n tradesphere
```

---

## 📝 Development Guidelines

### Code Style
- Follow Java conventions
- Use Lombok to reduce boilerplate
- Document public APIs with Javadoc
- Write unit tests for business logic

### Git Workflow
1. Create feature branch from `develop`
2. Make changes with descriptive commits
3. Create pull request
4. Pass CI/CD checks
5. Code review approval
6. Merge to `develop`

### Commit Message Format
```
type(scope): subject

body

footer
```

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`

---

## 📊 Monitoring

### Health Checks
- Spring Boot Actuator: `/actuator/health`
- Database: `/actuator/health/db`
- Kafka: `/actuator/health/kafka`
- Redis: `/actuator/health/redis`

### Metrics
- Prometheus: `/actuator/prometheus`
- Grafana dashboards: http://localhost:3000

### Logging
- Application logs: `logs/application.log`
- Access logs: `logs/access.log`
- Error logs: `logs/error.log`

---

## 🛠️ Troubleshooting

### Common Issues

**1. Database connection failed**
```powershell
# Check if PostgreSQL is running
docker-compose ps postgres

# Restart PostgreSQL
docker-compose restart postgres
```

**2. Flyway migration failed**
```powershell
# Clean and re-migrate (development only!)
mvn flyway:clean
mvn flyway:migrate
```

**3. Kafka connection timeout**
```powershell
# Check Kafka status
docker-compose logs kafka

# Restart Kafka
docker-compose restart zookeeper kafka
```

---

## 📖 Documentation

- [Technical Architecture](TECHNICAL_ARCHITECTURE.md)
- [Database Schema](DATABASE_SCHEMA.md)
- [AI Architecture](AI_ARCHITECTURE.md)
- [Business Specification](COMPLETE_BUSINESS_SPECIFICATION.md)
- [API Documentation](http://localhost:8080/swagger-ui.html)

---

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

---

## 📄 License

Copyright © 2026 TradeSphere AI. All rights reserved.

---

## 📞 Support

- **Email**: support@tradesphere.ai
- **Documentation**: https://docs.tradesphere.ai
- **Issues**: https://github.com/tradesphere-ai/issues

---

## 🎯 Roadmap

### Phase 1 (Current) - Foundation ✅
- ✅ Project setup
- ✅ Shared kernel
- ✅ Database schema
- ✅ Infrastructure setup

### Phase 2 - Core Modules (In Progress) 🚧
- 🚧 Customer Management
- 🚧 Letter of Credit lifecycle
- 🚧 Document management
- 🚧 Basic AI agents

### Phase 3 - Advanced Features
- ⏳ Compliance screening
- ⏳ Risk assessment
- ⏳ Workflow engine
- ⏳ Analytics dashboard

### Phase 4 - Enterprise Features
- ⏳ SWIFT integration
- ⏳ ERP integration
- ⏳ Mobile apps
- ⏳ Advanced AI capabilities

---

**Built with ❤️ by the TradeSphere AI Team**

