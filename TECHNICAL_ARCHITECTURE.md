# TradeSphere AI - Technical Architecture Specification
**Version:** 1.0.0  
**Date:** July 16, 2026  
**Classification:** Enterprise Architecture Document

---

# TABLE OF CONTENTS

1. [Architecture Overview](#1-architecture-overview)
2. [Hexagonal Architecture](#2-hexagonal-architecture)
3. [Bounded Contexts](#3-bounded-contexts)
4. [Domain-Driven Design](#4-domain-driven-design)
5. [Event-Driven Architecture](#5-event-driven-architecture)
6. [API Architecture](#6-api-architecture)
7. [Data Architecture](#7-data-architecture)
8. [Integration Architecture](#8-integration-architecture)
9. [Security Architecture](#9-security-architecture)
10. [AI Architecture](#10-ai-architecture)
11. [Deployment Architecture](#11-deployment-architecture)
12. [Observability Architecture](#12-observability-architecture)

---

# 1. Architecture Overview

## 1.1 Architectural Principles

### 1.1.1 Core Principles

**Separation of Concerns**
- Domain logic isolated from infrastructure
- Business rules independent of frameworks
- Clear boundaries between layers

**Dependency Inversion**
- High-level modules independent of low-level modules
- Both depend on abstractions (ports)
- Infrastructure depends on domain, not vice versa

**Single Responsibility**
- Each component has one reason to change
- Bounded contexts own their domain
- Clear ownership boundaries

**Open-Closed Principle**
- Open for extension via ports
- Closed for modification of core domain
- Pluggable adapters

**Technology Agnostic**
- Domain model technology-independent
- Easy to swap infrastructure components
- Future-proof architecture

### 1.1.2 Architectural Patterns

```
┌─────────────────────────────────────────────────────────────┐
│                    HEXAGONAL ARCHITECTURE                    │
│                      (Ports & Adapters)                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  ┌───────────────┐    ┌──────────────┐    ┌──────────────┐ │
│  │   Inbound     │    │              │    │   Outbound   │ │
│  │   Adapters    │───▶│    DOMAIN    │───▶│   Adapters   │ │
│  │   (REST,      │    │     CORE     │    │   (DB, APIs, │ │
│  │   GraphQL,    │    │              │    │   Messaging) │ │
│  │   Messaging)  │    │              │    │              │ │
│  └───────────────┘    └──────────────┘    └──────────────┘ │
│         │                     │                    │         │
│         │                     │                    │         │
│    ┌────▼─────┐         ┌────▼────┐         ┌────▼─────┐  │
│    │ Inbound  │         │ Domain  │         │ Outbound │  │
│    │  Ports   │         │ Services│         │  Ports   │  │
│    └──────────┘         └─────────┘         └──────────┘  │
│                                                              │
└──────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│              DOMAIN-DRIVEN DESIGN (DDD)                      │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  Bounded Contexts ──▶ Ubiquitous Language ──▶ Aggregates   │
│        │                     │                      │        │
│        ▼                     ▼                      ▼        │
│  Context Maps          Entities              Repositories   │
│                        Value Objects          Domain Events  │
│                        Domain Services                       │
│                                                              │
└──────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│           EVENT-DRIVEN ARCHITECTURE (EDA)                    │
├─────────────────────────────────────────────────────────────┤
│                                                               │
│  Event Producers ──▶ Kafka Topics ──▶ Event Consumers       │
│         │                  │                   │             │
│         ▼                  ▼                   ▼             │
│  Domain Events      Event Schemas       Event Handlers      │
│  Integration Events Schema Registry     Saga Coordinators   │
│                                                              │
└──────────────────────────────────────────────────────────────┘
```

## 1.2 High-Level Architecture

```
┌────────────────────────────────────────────────────────────────────┐
│                         PRESENTATION LAYER                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐ │
│  │ Web Frontend │  │    Mobile    │  │   External Systems       │ │
│  │  (React/     │  │  (React      │  │   (SWIFT, ERP, Core     │ │
│  │   Angular)   │  │   Native)    │  │    Banking)              │ │
│  └──────────────┘  └──────────────┘  └──────────────────────────┘ │
└────────────┬──────────────────┬─────────────────┬──────────────────┘
             │                  │                 │
             ▼                  ▼                 ▼
┌────────────────────────────────────────────────────────────────────┐
│                          API GATEWAY                                │
│  ┌──────────────────────────────────────────────────────────────┐ │
│  │  Authentication │ Rate Limiting │ Routing │ Load Balancing   │ │
│  └──────────────────────────────────────────────────────────────┘ │
└────────────┬───────────────────────────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────────────────────────┐
│                      APPLICATION LAYER                              │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │          Bounded Context Services (Microservices)           │  │
│  ├─────────────────────────────────────────────────────────────┤  │
│  │ Identity │ Customer │ Trade │ Document │ Compliance │ Risk  │  │
│  │ Workflow │ Shipment │ AI    │ Analytics│ Integration│ Audit │  │
│  └─────────────────────────────────────────────────────────────┘  │
└────────────┬───────────────────────────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────────────────────────┐
│                       DOMAIN LAYER                                  │
│  ┌─────────────────────────────────────────────────────────────┐  │
│  │ Domain Models │ Business Rules │ Domain Services │ Events   │  │
│  │ Aggregates    │ Value Objects  │ Repositories    │ Factories│  │
│  └─────────────────────────────────────────────────────────────┘  │
└────────────┬───────────────────────────────────────────────────────┘
             │
             ▼
┌────────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐ │
│  │  PostgreSQL  │  │  Redis Cache │  │   Kafka Messaging        │ │
│  │  (Primary DB)│  │              │  │                          │ │
│  └──────────────┘  └──────────────┘  └──────────────────────────┘ │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐ │
│  │ Elasticsearch│  │  MinIO/S3    │  │   External APIs          │ │
│  │  (Search)    │  │  (Storage)   │  │   (AI, SWIFT, Banks)     │ │
│  └──────────────┘  └──────────────┘  └──────────────────────────┘ │
└────────────────────────────────────────────────────────────────────┘

┌────────────────────────────────────────────────────────────────────┐
│                    CROSS-CUTTING CONCERNS                           │
│  Security │ Logging │ Monitoring │ Tracing │ Configuration        │
└────────────────────────────────────────────────────────────────────┘
```

---

# 2. Hexagonal Architecture

## 2.1 Hexagonal Architecture Pattern

### 2.1.1 Core Concepts

**The Hexagon (Domain Core)**
- Contains business logic only
- No dependencies on external frameworks
- Pure Java/domain code
- Technology-agnostic

**Ports (Interfaces)**
- Inbound ports: Use cases exposed to outside world
- Outbound ports: Dependencies needed by domain
- Contract definitions
- Technology-neutral

**Adapters (Implementations)**
- Inbound adapters: Receive requests (REST, GraphQL, Messaging)
- Outbound adapters: Call external systems (DB, APIs, Queues)
- Technology-specific implementations
- Pluggable components

### 2.1.2 Package Structure

```
com.tradesphere.ai/
│
├── shared/                          # Shared kernel
│   ├── domain/
│   │   ├── ValueObject.java
│   │   ├── Entity.java
│   │   ├── AggregateRoot.java
│   │   ├── DomainEvent.java
│   │   └── DomainException.java
│   ├── application/
│   │   ├── UseCase.java
│   │   └── Command.java
│   └── infrastructure/
│       ├── events/
│       ├── config/
│       └── utils/
│
├── platform/                        # Platform Foundation
│   ├── identity/                    # Identity BC
│   ├── organization/                # Organization BC
│   ├── notification/                # Notification BC
│   └── audit/                       # Audit BC
│
├── customer/                        # Customer Management BC
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Customer.java        # Aggregate Root
│   │   │   ├── CustomerId.java      # Value Object
│   │   │   ├── CustomerType.java    # Enum
│   │   │   ├── KYCDocument.java     # Entity
│   │   │   └── CreditProfile.java   # Value Object
│   │   ├── repository/
│   │   │   └── CustomerRepository.java  # Port (Interface)
│   │   ├── service/
│   │   │   └── CustomerDomainService.java
│   │   └── event/
│   │       ├── CustomerCreated.java
│   │       ├── CustomerApproved.java
│   │       └── CustomerRiskRated.java
│   │
│   ├── application/
│   │   ├── usecase/
│   │   │   ├── CreateCustomerUseCase.java
│   │   │   ├── ApproveCustomerUseCase.java
│   │   │   └── UpdateCustomerUseCase.java
│   │   ├── service/
│   │   │   └── CustomerApplicationService.java
│   │   ├── dto/
│   │   │   ├── CreateCustomerRequest.java
│   │   │   ├── CustomerResponse.java
│   │   │   └── CustomerDetailDTO.java
│   │   └── port/
│   │       ├── in/
│   │       │   ├── CreateCustomerPort.java      # Inbound Port
│   │       │   ├── GetCustomerPort.java
│   │       │   └── UpdateCustomerPort.java
│   │       └── out/
│   │           ├── LoadCustomerPort.java        # Outbound Port
│   │           ├── SaveCustomerPort.java
│   │           └── NotifyCustomerPort.java
│   │
│   ├── adapter/
│   │   ├── in/
│   │   │   ├── rest/
│   │   │   │   ├── CustomerController.java      # REST Adapter
│   │   │   │   └── CustomerMapper.java
│   │   │   ├── graphql/
│   │   │   │   └── CustomerResolver.java        # GraphQL Adapter
│   │   │   └── messaging/
│   │   │       └── CustomerEventConsumer.java   # Kafka Consumer
│   │   │
│   │   └── out/
│   │       ├── persistence/
│   │       │   ├── CustomerJpaAdapter.java      # JPA Adapter
│   │       │   ├── CustomerEntity.java
│   │       │   ├── CustomerJpaRepository.java
│   │       │   └── CustomerRepositoryImpl.java
│   │       ├── messaging/
│   │       │   └── CustomerEventPublisher.java  # Kafka Producer
│   │       └── external/
│   │           ├── RiskScoringAdapter.java      # External API
│   │           └── ComplianceAdapter.java
│   │
│   └── config/
│       └── CustomerConfiguration.java           # Spring Configuration
│
├── trade/                           # Trade Finance BC
│   ├── domain/
│   │   ├── model/
│   │   │   ├── letterofcredit/
│   │   │   │   ├── LetterOfCredit.java          # Aggregate Root
│   │   │   │   ├── LCApplication.java           # Entity
│   │   │   │   ├── LCAmendment.java             # Entity
│   │   │   │   ├── LCTerms.java                 # Value Object
│   │   │   │   ├── LCStatus.java                # Enum
│   │   │   │   ├── LCType.java                  # Enum
│   │   │   │   └── LCNumber.java                # Value Object
│   │   │   ├── guarantee/
│   │   │   │   ├── BankGuarantee.java
│   │   │   │   └── GuaranteeType.java
│   │   │   ├── collection/
│   │   │   │   └── DocumentaryCollection.java
│   │   │   └── scf/
│   │   │       └── SupplyChainFinance.java
│   │   ├── repository/
│   │   │   ├── LCRepository.java
│   │   │   └── GuaranteeRepository.java
│   │   ├── service/
│   │   │   ├── LCDomainService.java
│   │   │   ├── LCValidationService.java
│   │   │   └── LCAmendmentService.java
│   │   └── event/
│   │       ├── LCApplicationSubmitted.java
│   │       ├── LCIssued.java
│   │       ├── LCAmended.java
│   │       └── LCClosed.java
│   │
│   ├── application/
│   │   ├── usecase/
│   │   │   ├── lc/
│   │   │   │   ├── CreateLCApplicationUseCase.java
│   │   │   │   ├── IssueLCUseCase.java
│   │   │   │   ├── AmendLCUseCase.java
│   │   │   │   └── CloseLCUseCase.java
│   │   │   ├── guarantee/
│   │   │   └── collection/
│   │   ├── service/
│   │   ├── dto/
│   │   └── port/
│   │       ├── in/
│   │       │   ├── CreateLCPort.java
│   │       │   ├── IssueLCPort.java
│   │       │   └── AmendLCPort.java
│   │       └── out/
│   │           ├── LoadLCPort.java
│   │           ├── SaveLCPort.java
│   │           ├── SwiftMessagePort.java
│   │           └── ComplianceCheckPort.java
│   │
│   ├── adapter/
│   │   ├── in/
│   │   │   └── rest/
│   │   │       ├── LCController.java
│   │   │       └── GuaranteeController.java
│   │   └── out/
│   │       ├── persistence/
│   │       │   ├── LCJpaAdapter.java
│   │       │   └── LCEntity.java
│   │       ├── messaging/
│   │       │   └── TradeEventPublisher.java
│   │       └── external/
│   │           ├── SwiftAdapter.java
│   │           └── CoreBankingAdapter.java
│   │
│   └── config/
│       └── TradeConfiguration.java
│
├── document/                        # Document Intelligence BC
│   ├── domain/
│   │   ├── model/
│   │   │   ├── Document.java                    # Aggregate Root
│   │   │   ├── DocumentType.java                # Enum
│   │   │   ├── DocumentStatus.java              # Enum
│   │   │   ├── ExtractedField.java              # Value Object
│   │   │   ├── DocumentVersion.java             # Entity
│   │   │   └── Discrepancy.java                 # Value Object
│   │   ├── repository/
│   │   │   └── DocumentRepository.java
│   │   ├── service/
│   │   │   ├── DocumentValidationService.java
│   │   │   ├── DocumentComparisonService.java
│   │   │   └── DiscrepancyDetectionService.java
│   │   └── event/
│   │       ├── DocumentUploaded.java
│   │       ├── DocumentProcessed.java
│   │       ├── DiscrepancyDetected.java
│   │       └── DocumentApproved.java
│   │
│   ├── application/
│   │   ├── usecase/
│   │   │   ├── UploadDocumentUseCase.java
│   │   │   ├── ProcessDocumentUseCase.java
│   │   │   ├── ReviewDocumentUseCase.java
│   │   │   └── CompareDocumentsUseCase.java
│   │   ├── port/
│   │   │   ├── in/
│   │   │   │   ├── UploadDocumentPort.java
│   │   │   │   └── ProcessDocumentPort.java
│   │   │   └── out/
│   │   │       ├── SaveDocumentPort.java
│   │   │       ├── OCRPort.java
│   │   │       ├── DocumentStoragePort.java
│   │   │       └── AIExtractionPort.java
│   │
│   ├── adapter/
│   │   ├── in/
│   │   │   └── rest/
│   │   │       └── DocumentController.java
│   │   └── out/
│   │       ├── persistence/
│   │       │   └── DocumentJpaAdapter.java
│   │       ├── storage/
│   │       │   └── MinIOAdapter.java
│   │       └── ai/
│   │           ├── OpenAIOCRAdapter.java
│   │           └── DocumentAIAdapter.java
│
├── compliance/                      # Compliance BC
│   ├── domain/
│   │   ├── model/
│   │   │   ├── ComplianceCheck.java
│   │   │   ├── SanctionsScreening.java
│   │   │   ├── AMLCheck.java
│   │   │   ├── PEPCheck.java
│   │   │   └── WatchlistMatch.java
│   │   ├── repository/
│   │   └── service/
│   │       ├── SanctionsScreeningService.java
│   │       ├── AMLService.java
│   │       └── RiskScoringService.java
│   │
│   ├── application/
│   │   ├── usecase/
│   │   │   ├── ScreenPartyUseCase.java
│   │   │   ├── PerformAMLCheckUseCase.java
│   │   │   └── GenerateSARUseCase.java
│   │   └── port/
│   │       └── out/
│   │           ├── SanctionsListPort.java
│   │           ├── PEPDatabasePort.java
│   │           └── WatchlistPort.java
│   │
│   └── adapter/
│       └── out/
│           └── external/
│               ├── DowJonesAdapter.java
│               ├── WorldCheckAdapter.java
│               └── OFACAdapter.java
│
├── risk/                            # Risk Management BC
│   ├── domain/
│   │   ├── model/
│   │   │   ├── RiskAssessment.java
│   │   │   ├── RiskScore.java
│   │   │   ├── CountryRisk.java
│   │   │   ├── CustomerRisk.java
│   │   │   └── TransactionRisk.java
│   │   ├── repository/
│   │   └── service/
│   │       └── RiskScoringService.java
│   │
│   ├── application/
│   │   └── usecase/
│   │       ├── AssessRiskUseCase.java
│   │       └── CalculateExposureUseCase.java
│   │
│   └── adapter/
│
├── workflow/                        # Workflow Engine BC
│   ├── domain/
│   │   ├── model/
│   │   │   ├── WorkflowDefinition.java
│   │   │   ├── WorkflowInstance.java
│   │   │   ├── WorkflowTask.java
│   │   │   ├── ApprovalNode.java
│   │   │   └── WorkflowStatus.java
│   │   ├── repository/
│   │   └── service/
│   │       └── WorkflowExecutionService.java
│   │
│   ├── application/
│   └── adapter/
│
├── ai/                              # AI Services BC
│   ├── domain/
│   │   ├── model/
│   │   │   ├── AIRequest.java
│   │   │   ├── AIResponse.java
│   │   │   ├── Prompt.java
│   │   │   └── ModelSelection.java
│   │   └── service/
│   │       ├── DocumentAgent.java
│   │       ├── ComplianceAgent.java
│   │       ├── RiskAgent.java
│   │       ├── WorkflowAgent.java
│   │       ├── KnowledgeAgent.java
│   │       └── CopilotAgent.java
│   │
│   ├── application/
│   │   ├── usecase/
│   │   │   ├── ExtractDocumentFieldsUseCase.java
│   │   │   ├── DetectDiscrepanciesUseCase.java
│   │   │   ├── AssessRiskUseCase.java
│   │   │   └── QueryCopilotUseCase.java
│   │   └── port/
│   │       └── out/
│   │           ├── LLMPort.java
│   │           ├── EmbeddingPort.java
│   │           ├── VectorDBPort.java
│   │           └── OCRPort.java
│   │
│   └── adapter/
│       └── out/
│           ├── openai/
│           │   ├── OpenAIAdapter.java
│           │   └── GPT4VisionAdapter.java
│           ├── anthropic/
│           │   └── ClaudeAdapter.java
│           ├── mistral/
│           │   └── MistralAdapter.java
│           └── vectordb/
│               └── PineconeAdapter.java
│
├── analytics/                       # Analytics BC
│   ├── domain/
│   ├── application/
│   └── adapter/
│       └── out/
│           └── elasticsearch/
│               └── AnalyticsRepository.java
│
├── integration/                     # Integration Hub BC
│   ├── domain/
│   ├── application/
│   └── adapter/
│       └── out/
│           ├── swift/
│           │   └── SwiftAdapter.java
│           ├── erp/
│           │   ├── SAPAdapter.java
│           │   └── OracleAdapter.java
│           └── corebanking/
│               └── FinacleAdapter.java
│
└── shipment/                        # Shipment Tracking BC
    ├── domain/
    ├── application/
    └── adapter/
```

### 2.1.3 Dependency Rules

```
┌─────────────────────────────────────────────────────────┐
│                   DEPENDENCY FLOW                        │
├─────────────────────────────────────────────────────────┤
│                                                           │
│  Adapters (In) ──▶ Application ──▶ Domain ◀── Adapters (Out)
│                         │              │                 │
│                         │              │                 │
│                         ▼              ▼                 │
│                    Ports (In)     Ports (Out)           │
│                                                          │
│  ✅ Allowed:                                            │
│  - Adapters depend on Ports                             │
│  - Application depends on Domain                        │
│  - Ports defined by Domain                              │
│                                                          │
│  ❌ NOT Allowed:                                        │
│  - Domain depends on Adapters                           │
│  - Domain depends on Frameworks                         │
│  - Ports depend on Adapters                             │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

**Enforced by ArchUnit:**
```java
@ArchTest
static final ArchRule domainShouldNotDependOnAdapters = 
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAPackage("..adapter..");

@ArchTest
static final ArchRule domainShouldNotDependOnInfrastructure = 
    noClasses()
        .that().resideInAPackage("..domain..")
        .should().dependOnClassesThat()
        .resideInAnyPackage("org.springframework..", "jakarta.persistence..");
```

---

# 3. Bounded Contexts

## 3.1 Bounded Context Map

```
┌─────────────────────────────────────────────────────────────────────┐
│                    BOUNDED CONTEXT MAP                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ┌──────────────┐                                                   │
│  │   Identity   │ ◀───── ACL ──────┐                               │
│  │   Context    │                   │                               │
│  └──────┬───────┘                   │                               │
│         │                           │                               │
│         │ (Customer-Supplier)       │                               │
│         ▼                           │                               │
│  ┌──────────────┐          ┌───────┴────────┐                      │
│  │   Customer   │◀─────────│     Trade      │                      │
│  │   Context    │          │    Context     │                      │
│  └──────┬───────┘          └────────┬───────┘                      │
│         │                           │                               │
│         │ (Partnership)             │ (Partnership)                │
│         ▼                           ▼                               │
│  ┌──────────────┐          ┌──────────────┐                        │
│  │  Compliance  │          │   Document   │                        │
│  │   Context    │◀─────────│   Context    │                        │
│  └──────┬───────┘          └──────┬───────┘                        │
│         │                         │                                 │
│         │                         │                                 │
│         ▼                         ▼                                 │
│  ┌──────────────┐          ┌──────────────┐                        │
│  │     Risk     │          │      AI      │                        │
│  │   Context    │◀─────────│   Context    │                        │
│  └──────────────┘          └──────────────┘                        │
│                                    │                                │
│                                    │ (Shared Kernel)               │
│                                    ▼                                │
│                            ┌──────────────┐                         │
│                            │  Analytics   │                         │
│                            │   Context    │                         │
│                            └──────────────┘                         │
│                                                                      │
│  ┌──────────────┐          ┌──────────────┐                        │
│  │  Workflow    │          │  Shipment    │                        │
│  │   Context    │          │   Context    │                        │
│  └──────┬───────┘          └──────────────┘                        │
│         │                                                            │
│         │                                                            │
│         ▼                                                            │
│  ┌──────────────┐          ┌──────────────┐                        │
│  │ Integration  │          │ Notification │                        │
│  │   Context    │          │   Context    │                        │
│  └──────────────┘          └──────────────┘                        │
│                                                                      │
└──────────────────────────────────────────────────────────────────────┘

Legend:
◀──── : Upstream/Downstream relationship
ACL   : Anti-Corruption Layer
(Partnership) : Shared understanding, mutual dependency
(Customer-Supplier) : One-way dependency
(Shared Kernel) : Shared domain model
```

## 3.2 Context Relationships

### 3.2.1 Identity Context (Core)
**Upstream to:** All contexts
**Relationship:** Customer-Supplier
**Integration:** Via JWT tokens, User IDs
**Responsibility:** Authentication, Authorization, User Management

### 3.2.2 Trade Context (Core)
**Upstream to:** Document, Workflow, Compliance
**Downstream from:** Customer, Identity
**Relationship:** Partnership with Document & Workflow
**Integration:** Domain events, REST APIs
**Responsibility:** Trade finance lifecycle, LC/BG/Collections

### 3.2.3 Customer Context
**Upstream to:** Trade, Compliance, Risk
**Downstream from:** Identity
**Relationship:** Customer-Supplier
**Integration:** Customer IDs, REST APIs, Events
**Responsibility:** Customer lifecycle, KYC, Relationships

### 3.2.4 Document Context
**Upstream to:** Trade (providing document status)
**Partnership with:** Trade, AI
**Integration:** Document IDs, Events, REST APIs
**Responsibility:** Document management, OCR, Validation

### 3.2.5 AI Context
**Shared Kernel with:** Document, Compliance, Risk
**Services:** All contexts
**Integration:** Internal APIs, Synchronous calls
**Responsibility:** AI capabilities for all domains

### 3.2.6 Compliance Context
**Services:** Trade, Customer
**Partnership with:** Risk
**Integration:** Compliance results via events
**Responsibility:** AML, Sanctions, Watchlist screening

### 3.2.7 Risk Context
**Services:** Trade, Customer
**Partnership with:** Compliance
**Integration:** Risk scores via events/APIs
**Responsibility:** Risk assessment, Exposure calculation

### 3.2.8 Workflow Context
**Services:** Trade, Customer, Document
**Integration:** Workflow state via events
**Responsibility:** Approval routing, Task management, SLA

### 3.2.9 Analytics Context (Supporting)
**Downstream from:** All contexts
**Shared Kernel with:** AI
**Integration:** Event sourcing, Data replication
**Responsibility:** Reporting, Dashboards, Insights

### 3.2.10 Integration Context (Supporting)
**Services:** All contexts
**Integration:** Message translation
**Responsibility:** External system integration (SWIFT, ERP, Banks)

## 3.3 Context Integration Patterns

### 3.3.1 Synchronous Integration (REST APIs)
**Use When:**
- Immediate response needed
- Strong consistency required
- Query operations

**Example:**
```
Trade Context ──[REST GET]──▶ Customer Context
                              "Get Customer Details"
```

### 3.3.2 Asynchronous Integration (Events)
**Use When:**
- Eventual consistency acceptable
- Decoupling desired
- Command operations

**Example:**
```
Trade Context ──[Event: LCIssued]──▶ Kafka Topic ──▶ Document Context
                                                   └─▶ Compliance Context
                                                   └─▶ Analytics Context
```

### 3.3.3 Anti-Corruption Layer (ACL)
**Use When:**
- Protecting domain from external models
- Legacy system integration
- Third-party API integration

**Example:**
```
Trade Context ──▶ ACL ──▶ SWIFT Adapter ──▶ SWIFT Network
              (Domain     (Translation)
               Model)
```

---

# 4. Domain-Driven Design

## 4.1 Ubiquitous Language

### 4.1.1 Trade Finance Domain Terms

| Term | Definition | Context |
|------|------------|---------|
| **Letter of Credit (LC)** | A payment instrument issued by a bank guaranteeing payment to a seller | Trade |
| **Applicant** | The party (usually buyer/importer) requesting the LC | Trade |
| **Beneficiary** | The party (usually seller/exporter) receiving payment | Trade |
| **Issuing Bank** | Bank that issues the LC on behalf of the applicant | Trade |
| **Advising Bank** | Bank that advises the LC to the beneficiary | Trade |
| **Confirming Bank** | Bank that adds its confirmation to the LC | Trade |
| **Discrepancy** | Any deviation between documents and LC terms | Document |
| **UCP 600** | Uniform Customs and Practice for Documentary Credits (ICC rules) | Trade |
| **ISBP** | International Standard Banking Practice | Trade |
| **Documentary Collection** | Payment method where bank acts as intermediary for documents | Trade |
| **D/P** | Documents against Payment | Trade |
| **D/A** | Documents against Acceptance | Trade |
| **Bank Guarantee** | Written commitment by bank to pay if principal fails obligation | Trade |
| **Performance Guarantee** | Guarantee ensuring contract performance | Trade |
| **Standby LC (SBLC)** | LC serving as backup payment mechanism | Trade |
| **Amendment** | Change to original LC terms | Trade |
| **Sight LC** | LC payable upon presentation of compliant documents | Trade |
| **Usance LC** | LC with deferred payment | Trade |
| **Transferable LC** | LC that can be transferred to second beneficiary | Trade |
| **Back-to-Back LC** | Two LCs with related transactions | Trade |
| **Reimbursement** | Payment from issuing bank to paying bank | Trade |
| **Negotiation** | Purchase of documents under LC by bank | Trade |
| **Acceptance** | Bank's commitment to pay at future date | Trade |
| **Discounting** | Advance payment with discount | Trade |
| **SWIFT MT700** | SWIFT message type for LC issuance | Integration |
| **SWIFT MT707** | SWIFT message for LC amendment | Integration |
| **Bill of Lading (B/L)** | Transport document proving shipment | Document |
| **Commercial Invoice** | Document detailing goods and prices | Document |
| **Packing List** | Document listing package contents | Document |
| **Certificate of Origin** | Document certifying goods' origin country | Document |
| **Insurance Certificate** | Document proving cargo insurance | Document |
| **Inspection Certificate** | Document certifying goods inspection | Document |
| **Shipment Advice** | Notice of goods dispatch | Shipment |
| **AML** | Anti-Money Laundering | Compliance |
| **KYC** | Know Your Customer | Compliance |
| **KYB** | Know Your Business | Compliance |
| **PEP** | Politically Exposed Person | Compliance |
| **Sanctions Screening** | Checking parties against sanctions lists | Compliance |
| **TBML** | Trade-Based Money Laundering | Compliance |
| **SAR** | Suspicious Activity Report | Compliance |
| **Incoterms** | International Commercial Terms (shipping terms) | Trade |
| **FOB** | Free On Board | Trade |
| **CIF** | Cost, Insurance, Freight | Trade |
| **Murabaha** | Islamic cost-plus financing | Trade |
| **Wakala** | Islamic agency arrangement | Trade |

### 4.1.2 Platform Domain Terms

| Term | Definition | Context |
|------|------------|---------|
| **Tenant** | Organization using the platform | Platform |
| **Organization** | Legal entity (bank, corporate) | Platform |
| **Branch** | Physical/logical location of organization | Platform |
| **User** | Person accessing the system | Identity |
| **Role** | Set of permissions | Identity |
| **Permission** | Authorization to perform action | Identity |
| **RBAC** | Role-Based Access Control | Identity |
| **ABAC** | Attribute-Based Access Control | Identity |
| **Trade Case** | Instance of trade transaction | Trade |
| **Workflow** | Sequence of tasks and approvals | Workflow |
| **Task** | Work item assigned to user | Workflow |
| **SLA** | Service Level Agreement | Workflow |
| **Audit Log** | Immutable record of system events | Audit |
| **Notification** | Alert sent to user | Notification |
| **Dashboard** | Visual display of metrics | Analytics |

## 4.2 Aggregates

### 4.2.1 Trade Context Aggregates

#### LetterOfCredit Aggregate
```java
@Entity
@Table(name = "letters_of_credit")
public class LetterOfCredit extends AggregateRoot<LCId> {
    
    // Identity
    @EmbeddedId
    private LCId id;
    
    private LCNumber lcNumber;
    private LCType type;
    private LCStatus status;
    
    // Parties
    private CustomerId applicant;
    private CustomerId beneficiary;
    private BankId issuingBank;
    private BankId advisingBank;
    private BankId confirmingBank;
    
    // Terms
    @Embedded
    private LCTerms terms;
    
    @Embedded
    private LCAmount amount;
    
    @Embedded
    private LCDates dates;
    
    // Documents
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RequiredDocument> requiredDocuments = new ArrayList<>();
    
    // Amendments
    @OneToMany(cascade = CascadeType.ALL)
    private List<LCAmendment> amendments = new ArrayList<>();
    
    // Lifecycle
    private LocalDateTime issuedAt;
    private LocalDateTime expiryDate;
    private UserId issuedBy;
    
    // Business Methods (enforcing invariants)
    public void issue(UserId userId) {
        if (this.status != LCStatus.APPROVED) {
            throw new LCCannotBeIssuedException("LC must be approved before issuance");
        }
        this.status = LCStatus.ISSUED;
        this.issuedAt = LocalDateTime.now();
        this.issuedBy = userId;
        
        // Raise domain event
        registerEvent(new LCIssued(this.id, this.lcNumber, this.applicant, this.beneficiary));
    }
    
    public void amend(LCAmendment amendment, UserId userId) {
        if (this.status != LCStatus.ISSUED) {
            throw new LCCannotBeAmendedException("Only issued LC can be amended");
        }
        amendment.validate(this);
        this.amendments.add(amendment);
        
        // Apply amendment to terms
        amendment.applyTo(this.terms);
        
        registerEvent(new LCAmended(this.id, amendment.getId(), userId));
    }
    
    public void receiveDocuments(List<Document> documents, LocalDateTime presentationDate) {
        if (this.status != LCStatus.ISSUED) {
            throw new IllegalStateException("Cannot receive documents for non-issued LC");
        }
        
        // Check if within presentation period
        if (presentationDate.isAfter(this.dates.getLatestPresentationDate())) {
            throw new DocumentsPresentedLateException();
        }
        
        this.status = LCStatus.UNDER_REVIEW;
        registerEvent(new DocumentsReceived(this.id, documents.size(), presentationDate));
    }
    
    public void approvePayment(Money amount, UserId approvedBy) {
        if (this.status != LCStatus.UNDER_REVIEW) {
            throw new IllegalStateException("LC must be under review to approve payment");
        }
        
        if (amount.isGreaterThan(this.amount.getAmount())) {
            throw new PaymentExceedsLCAmountException();
        }
        
        this.status = LCStatus.APPROVED_FOR_PAYMENT;
        registerEvent(new PaymentApproved(this.id, amount, approvedBy));
    }
    
    public void close(UserId userId) {
        if (!canBeClosed()) {
            throw new LCCannotBeClosedException("LC cannot be closed in current state");
        }
        this.status = LCStatus.CLOSED;
        registerEvent(new LCClosed(this.id, userId));
    }
    
    private boolean canBeClosed() {
        return this.status == LCStatus.PAID || 
               this.status == LCStatus.EXPIRED ||
               this.status == LCStatus.CANCELLED;
    }
    
    // Getters
    public LCId getId() { return id; }
    public LCNumber getLcNumber() { return lcNumber; }
    public LCStatus getStatus() { return status; }
    // ... other getters
}
```

#### Value Objects
```java
@Embeddable
public class LCTerms {
    private String description;
    private String placeOfExpiry;
    private String portOfLoading;
    private String portOfDischarge;
    private String incoterm;
    private boolean partialShipmentAllowed;
    private boolean transhipmentAllowed;
    private String specialConditions;
    
    // Constructor, getters, equals, hashCode (immutable)
}

@Embeddable
public class LCAmount {
    private BigDecimal amount;
    private Currency currency;
    private BigDecimal tolerance; // +/- percentage
    
    public Money getAmount() {
        return new Money(amount, currency);
    }
    
    public boolean isWithinTolerance(Money actualAmount) {
        BigDecimal minAmount = amount.multiply(BigDecimal.ONE.subtract(tolerance.divide(BigDecimal.valueOf(100))));
        BigDecimal maxAmount = amount.multiply(BigDecimal.ONE.add(tolerance.divide(BigDecimal.valueOf(100))));
        return actualAmount.getAmount().compareTo(minAmount) >= 0 && 
               actualAmount.getAmount().compareTo(maxAmount) <= 0;
    }
}

@Embeddable
public class LCDates {
    private LocalDate issueDate;
    private LocalDate expiryDate;
    private LocalDate latestShipmentDate;
    private Integer presentationDays; // Days after shipment for document presentation
    
    public LocalDateTime getLatestPresentationDate() {
        return latestShipmentDate.plusDays(presentationDays).atTime(LocalTime.MAX);
    }
    
    public boolean isExpired() {
        return LocalDate.now().isAfter(expiryDate);
    }
}
```

### 4.2.2 Document Context Aggregates

#### Document Aggregate
```java
@Entity
@Table(name = "documents")
public class Document extends AggregateRoot<DocumentId> {
    
    @EmbeddedId
    private DocumentId id;
    
    private DocumentType type;
    private DocumentStatus status;
    
    // Ownership
    private LCId letterOfCreditId;
    private CustomerId uploadedBy;
    
    // Storage
    private String storageKey; // S3/MinIO key
    private String fileName;
    private String contentType;
    private Long fileSize;
    
    // Processing
    @Embedded
    private OCRResult ocrResult;
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<ExtractedField> extractedFields = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<Discrepancy> discrepancies = new ArrayList<>();
    
    // AI Metadata
    private BigDecimal confidenceScore;
    private String aiModel;
    private LocalDateTime processedAt;
    
    // Versions
    @OneToMany(cascade = CascadeType.ALL)
    private List<DocumentVersion> versions = new ArrayList<>();
    
    // Business Methods
    public void process(OCRResult ocrResult, List<ExtractedField> fields) {
        this.ocrResult = ocrResult;
        this.extractedFields = fields;
        this.status = DocumentStatus.PROCESSED;
        this.processedAt = LocalDateTime.now();
        
        registerEvent(new DocumentProcessed(this.id, fields.size()));
    }
    
    public void detectDiscrepancies(List<Discrepancy> discrepancies) {
        this.discrepancies = discrepancies;
        if (!discrepancies.isEmpty()) {
            this.status = DocumentStatus.DISCREPANT;
            registerEvent(new DiscrepanciesDetected(this.id, discrepancies.size()));
        }
    }
    
    public void approve(UserId approvedBy) {
        if (this.status == DocumentStatus.DISCREPANT) {
            throw new DiscrepantDocumentCannotBeApprovedException();
        }
        this.status = DocumentStatus.APPROVED;
        registerEvent(new DocumentApproved(this.id, approvedBy));
    }
    
    public void reject(String reason, UserId rejectedBy) {
        this.status = DocumentStatus.REJECTED;
        registerEvent(new DocumentRejected(this.id, reason, rejectedBy));
    }
}
```

### 4.2.3 Customer Context Aggregates

#### Customer Aggregate
```java
@Entity
@Table(name = "customers")
public class Customer extends AggregateRoot<CustomerId> {
    
    @EmbeddedId
    private CustomerId id;
    
    private String name;
    private CustomerType type; // IMPORTER, EXPORTER, BANK, etc.
    private CustomerStatus status;
    
    // Business Details
    @Embedded
    private BusinessDetails businessDetails;
    
    @Embedded
    private ContactDetails contactDetails;
    
    // KYC
    @OneToMany(cascade = CascadeType.ALL)
    private List<KYCDocument> kycDocuments = new ArrayList<>();
    
    private KYCStatus kycStatus;
    private LocalDateTime kycApprovedAt;
    
    // Risk & Credit
    @Embedded
    private CreditProfile creditProfile;
    
    private RiskRating riskRating;
    private BigDecimal creditLimit;
    
    // Compliance
    private ComplianceStatus complianceStatus;
    private LocalDateTime lastComplianceCheck;
    
    // Relationships
    @OneToMany(cascade = CascadeType.ALL)
    private List<CustomerRelationship> relationships = new ArrayList<>();
    
    // Business Methods
    public void submitForApproval(UserId submittedBy) {
        if (!hasRequiredKYCDocuments()) {
            throw new InsufficientKYCDocumentsException();
        }
        this.status = CustomerStatus.PENDING_APPROVAL;
        registerEvent(new CustomerSubmittedForApproval(this.id, submittedBy));
    }
    
    public void approve(UserId approvedBy) {
        if (this.status != CustomerStatus.PENDING_APPROVAL) {
            throw new CustomerNotPendingException();
        }
        if (this.kycStatus != KYCStatus.APPROVED) {
            throw new KYCNotApprovedException();
        }
        if (this.complianceStatus != ComplianceStatus.CLEARED) {
            throw new ComplianceNotClearedException();
        }
        
        this.status = CustomerStatus.ACTIVE;
        registerEvent(new CustomerApproved(this.id, approvedBy));
    }
    
    public void updateRiskRating(RiskRating newRating, String reason) {
        RiskRating oldRating = this.riskRating;
        this.riskRating = newRating;
        registerEvent(new CustomerRiskRatingChanged(this.id, oldRating, newRating, reason));
    }
    
    public void setCreditLimit(BigDecimal limit, UserId approvedBy) {
        if (limit.compareTo(BigDecimal.ZERO) < 0) {
            throw new InvalidCreditLimitException();
        }
        this.creditLimit = limit;
        registerEvent(new CreditLimitSet(this.id, limit, approvedBy));
    }
    
    private boolean hasRequiredKYCDocuments() {
        // Business logic to check required documents
        return kycDocuments.stream()
            .anyMatch(doc -> doc.getType() == KYCDocumentType.TRADE_LICENSE) &&
            kycDocuments.stream()
            .anyMatch(doc -> doc.getType() == KYCDocumentType.BOARD_RESOLUTION);
    }
}
```

## 4.3 Domain Services

### 4.3.1 LC Validation Service
```java
@Service
public class LCValidationService {
    
    public ValidationResult validateLCTerms(LCTerms terms, LCAmount amount) {
        List<ValidationError> errors = new ArrayList<>();
        
        // Validate amount
        if (amount.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add(new ValidationError("LC amount must be positive"));
        }
        
        // Validate dates
        if (terms.getExpiryDate().isBefore(terms.getLatestShipmentDate())) {
            errors.add(new ValidationError("Expiry date cannot be before shipment date"));
        }
        
        // Validate Incoterm
        if (!Incoterms.isValid(terms.getIncoterm())) {
            errors.add(new ValidationError("Invalid Incoterm: " + terms.getIncoterm()));
        }
        
        // UCP 600 validations
        validateUCP600Compliance(terms, errors);
        
        return errors.isEmpty() ? 
            ValidationResult.success() : 
            ValidationResult.failure(errors);
    }
    
    private void validateUCP600Compliance(LCTerms terms, List<ValidationError> errors) {
        // UCP 600 Article 6c: Ambiguous terms
        if (containsAmbiguousTerms(terms.getDescription())) {
            errors.add(new ValidationError("UCP 600 Article 6c: Description contains ambiguous terms like 'prompt', 'immediately'"));
        }
        
        // UCP 600 Article 14b: Document examination period (5 banking days)
        // ... more UCP 600 rules
    }
    
    public boolean isAmendmentConsistent(LetterOfCredit lc, LCAmendment amendment) {
        // Validate amendment doesn't create inconsistencies
        return true;
    }
}
```

### 4.3.2 Discrepancy Detection Service
```java
@Service
public class DiscrepancyDetectionService {
    
    private final DocumentComparisonService comparisonService;
    private final UCP600RulesEngine ucp600Rules;
    
    public List<Discrepancy> detectDiscrepancies(
            LetterOfCredit lc, 
            List<Document> presentedDocuments) {
        
        List<Discrepancy> discrepancies = new ArrayList<>();
        
        // Check if all required documents are presented
        discrepancies.addAll(checkMissingDocuments(lc, presentedDocuments));
        
        // Check each document against LC terms
        for (Document doc : presentedDocuments) {
            discrepancies.addAll(checkDocumentAgainstLCTerms(lc, doc));
        }
        
        // Check cross-document consistency
        discrepancies.addAll(checkCrossDocumentConsistency(presentedDocuments));
        
        // UCP 600 compliance
        discrepancies.addAll(checkUCP600Compliance(lc, presentedDocuments));
        
        // ISBP compliance
        discrepancies.addAll(checkISBPCompliance(lc, presentedDocuments));
        
        return discrepancies;
    }
    
    private List<Discrepancy> checkMissingDocuments(
            LetterOfCredit lc, 
            List<Document> presentedDocuments) {
        
        List<Discrepancy> discrepancies = new ArrayList<>();
        
        for (RequiredDocument required : lc.getRequiredDocuments()) {
            boolean found = presentedDocuments.stream()
                .anyMatch(doc -> doc.getType() == required.getType());
            
            if (!found) {
                discrepancies.add(Discrepancy.builder()
                    .type(DiscrepancyType.MISSING_DOCUMENT)
                    .severity(DiscrepancySeverity.CRITICAL)
                    .description("Required document not presented: " + required.getType())
                    .ucp600Article("UCP 600 Article 14")
                    .waivable(false)
                    .build());
            }
        }
        
        return discrepancies;
    }
    
    private List<Discrepancy> checkDocumentAgainstLCTerms(
            LetterOfCredit lc, 
            Document doc) {
        
        List<Discrepancy> discrepancies = new ArrayList<>();
        
        // Extract key fields from document
        Map<String, String> extractedFields = doc.getExtractedFieldsAsMap();
        
        // Check beneficiary name
        String docBeneficiary = extractedFields.get("beneficiary");
        if (!comparisonService.namesMatch(docBeneficiary, lc.getBeneficiary().getName())) {
            discrepancies.add(Discrepancy.builder()
                .type(DiscrepancyType.BENEFICIARY_NAME_MISMATCH)
                .severity(DiscrepancySeverity.MAJOR)
                .description("Beneficiary name in document does not match LC")
                .lcValue(lc.getBeneficiary().getName())
                .documentValue(docBeneficiary)
                .waivable(false)
                .build());
        }
        
        // Check amount
        // Check dates
        // Check descriptions
        // ... more checks
        
        return discrepancies;
    }
}
```

---

# 5. Event-Driven Architecture

## 5.1 Event Types

### 5.1.1 Domain Events
**Definition:** Events that capture state changes within the domain  
**Scope:** Within bounded context  
**Purpose:** Maintain consistency, trigger domain logic

### 5.1.2 Integration Events
**Definition:** Events published to other bounded contexts  
**Scope:** Cross-context communication  
**Purpose:** Decouple contexts, enable event-driven integration

### 5.1.3 Event Schema

```json
{
  "eventId": "uuid",
  "eventType": "com.tradesphere.ai.trade.LCIssued",
  "aggregateId": "LC-2026-001",
  "aggregateType": "LetterOfCredit",
  "occurredAt": "2026-07-16T10:30:00Z",
  "version": 1,
  "causationId": "user-action-id",
  "correlationId": "trace-id",
  "metadata": {
    "userId": "user-123",
    "tenantId": "tenant-456",
    "source": "trade-service"
  },
  "payload": {
    "lcId": "LC-2026-001",
    "lcNumber": "LC2026000001",
    "applicantId": "CUST-001",
    "beneficiaryId": "CUST-002",
    "amount": {
      "value": 100000.00,
      "currency": "USD"
    },
    "issuedBy": "user-123",
    "issuedAt": "2026-07-16T10:30:00Z"
  }
}
```

## 5.2 Event Catalog

### 5.2.1 Trade Context Events

```java
// LC Lifecycle Events
public class LCApplicationSubmitted extends DomainEvent {
    private LCId lcId;
    private CustomerId applicantId;
    private CustomerId beneficiaryId;
    private Money amount;
}

public class LCApplicationApproved extends DomainEvent {
    private LCId lcId;
    private UserId approvedBy;
}

public class LCIssued extends DomainEvent {
    private LCId lcId;
    private LCNumber lcNumber;
    private CustomerId applicantId;
    private CustomerId beneficiaryId;
    private Money amount;
    private LocalDateTime issuedAt;
}

public class LCAdvised extends DomainEvent {
    private LCId lcId;
    private BankId advisingBank;
    private LocalDateTime advisedAt;
}

public class LCAmendmentRequested extends DomainEvent {
    private LCId lcId;
    private LCAmendmentId amendmentId;
    private String reason;
}

public class LCAmended extends DomainEvent {
    private LCId lcId;
    private LCAmendmentId amendmentId;
    private Map<String, Object> changes;
}

public class DocumentsReceived extends DomainEvent {
    private LCId lcId;
    private List<DocumentId> documentIds;
    private LocalDateTime receivedAt;
}

public class DocumentsUnderReview extends DomainEvent {
    private LCId lcId;
    private UserId reviewerId;
}

public class DiscrepanciesDetected extends DomainEvent {
    private LCId lcId;
    private List<DiscrepancyId> discrepancyIds;
    private DiscrepancySeverity highestSeverity;
}

public class PaymentApproved extends DomainEvent {
    private LCId lcId;
    private Money amount;
    private UserId approvedBy;
}

public class PaymentExecuted extends DomainEvent {
    private LCId lcId;
    private Money amount;
    private String referenceNumber;
    private LocalDateTime executedAt;
}

public class LCClosed extends DomainEvent {
    private LCId lcId;
    private String reason;
    private UserId closedBy;
}
```

### 5.2.2 Document Context Events

```java
public class DocumentUploaded extends DomainEvent {
    private DocumentId documentId;
    private DocumentType type;
    private String fileName;
    private Long fileSize;
    private CustomerId uploadedBy;
}

public class DocumentProcessed extends DomainEvent {
    private DocumentId documentId;
    private int fieldsExtracted;
    private BigDecimal confidenceScore;
}

public class DocumentApproved extends DomainEvent {
    private DocumentId documentId;
    private UserId approvedBy;
}

public class DocumentRejected extends DomainEvent {
    private DocumentId documentId;
    private String reason;
    private UserId rejectedBy;
}
```

### 5.2.3 Customer Context Events

```java
public class CustomerCreated extends DomainEvent {
    private CustomerId customerId;
    private String name;
    private CustomerType type;
}

public class CustomerApproved extends DomainEvent {
    private CustomerId customerId;
    private UserId approvedBy;
}

public class CustomerRiskRatingChanged extends DomainEvent {
    private CustomerId customerId;
    private RiskRating oldRating;
    private RiskRating newRating;
    private String reason;
}
```

### 5.2.4 Compliance Context Events

```java
public class ComplianceCheckCompleted extends DomainEvent {
    private EntityId entityId;
    private ComplianceCheckType type;
    private ComplianceResult result;
}

public class SanctionsMatchFound extends DomainEvent {
    private EntityId entityId;
    private String listName;
    private BigDecimal matchScore;
}

public class SARGenerated extends DomainEvent {
    private SARId sarId;
    private EntityId relatedEntityId;
    private String reason;
}
```

## 5.3 Kafka Topics

### 5.3.1 Topic Naming Convention
```
<domain>.<entity>.<event-type>

Examples:
- trade.lc.issued
- trade.lc.amended
- document.document.uploaded
- customer.customer.approved
- compliance.screening.completed
```

### 5.3.2 Topic Configuration

```yaml
trade.lc.issued:
  partitions: 10
  replication-factor: 3
  retention-ms: 604800000  # 7 days
  cleanup-policy: delete
  
trade.lc.amended:
  partitions: 10
  replication-factor: 3
  retention-ms: 604800000
  
document.document.uploaded:
  partitions: 20
  replication-factor: 3
  retention-ms: 2592000000  # 30 days
  
compliance.screening.completed:
  partitions: 10
  replication-factor: 3
  retention-ms: 31536000000  # 365 days
```

### 5.3.3 Event Publishing

```java
@Service
public class DomainEventPublisher {
    
    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;
    private final SchemaRegistry schemaRegistry;
    
    public void publish(DomainEvent event) {
        String topic = deriveTopicName(event);
        String key = event.getAggregateId().toString();
        
        // Add metadata
        event.setEventId(UUID.randomUUID());
        event.setOccurredAt(Instant.now());
        
        // Validate against schema
        schemaRegistry.validate(event);
        
        // Publish
        kafkaTemplate.send(topic, key, event)
            .whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Failed to publish event: {}", event, ex);
                    // Handle failure (retry, dead letter, etc.)
                } else {
                    log.info("Event published: {} to topic: {}", event.getEventType(), topic);
                }
            });
    }
    
    private String deriveTopicName(DomainEvent event) {
        // Extract from event class package and name
        return event.getClass().getPackageName() + "." + 
               event.getClass().getSimpleName();
    }
}
```

### 5.3.4 Event Consumption

```java
@Service
public class LCEventConsumer {
    
    private final DocumentService documentService;
    private final ComplianceService complianceService;
    
    @KafkaListener(
        topics = "trade.lc.issued",
        groupId = "document-service",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleLCIssued(LCIssued event) {
        log.info("Received LCIssued event: {}", event.getLcId());
        
        try {
            // Create document checklist for this LC
            documentService.createDocumentChecklist(event.getLcId(), event.getRequiredDocuments());
        } catch (Exception e) {
            log.error("Error handling LCIssued event", e);
            throw e; // Will be retried or sent to DLQ
        }
    }
    
    @KafkaListener(
        topics = "trade.lc.issued",
        groupId = "compliance-service"
    )
    public void screenLCParties(LCIssued event) {
        log.info("Screening parties for LC: {}", event.getLcId());
        
        // Screen applicant and beneficiary
        complianceService.screenParty(event.getApplicantId());
        complianceService.screenParty(event.getBeneficiaryId());
    }
}
```

## 5.4 Saga Pattern

### 5.4.1 LC Issuance Saga

```
┌─────────────────────────────────────────────────────────────────┐
│                    LC ISSUANCE SAGA                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  1. Submit LC Application                                        │
│     ├──▶ Validate Customer (Customer Service)                   │
│     │    └──▶ Success ──▶ Continue                              │
│     │    └──▶ Failure ──▶ Rollback                              │
│     │                                                             │
│  2. Screen Parties (Compliance Service)                         │
│     ├──▶ Screen Applicant                                       │
│     ├──▶ Screen Beneficiary                                     │
│     │    └──▶ Match Found ──▶ Reject Application               │
│     │    └──▶ Clear ──▶ Continue                               │
│     │                                                             │
│  3. Assess Risk (Risk Service)                                  │
│     ├──▶ Calculate Risk Score                                   │
│     │    └──▶ High Risk ──▶ Flag for Review                    │
│     │    └──▶ Accept able ──▶ Continue                         │
│     │                                                             │
│  4. Check Credit Limit (Customer Service)                       │
│     ├──▶ Check Available Limit                                  │
│     │    └──▶ Insufficient ──▶ Reject                          │
│     │    └──▶ Sufficient ──▶ Reserve Limit ──▶ Continue       │
│     │                                                             │
│  5. Route for Approval (Workflow Service)                       │
│     ├──▶ Determine Approval Path                               │
│     ├──▶ Assign Tasks                                           │
│     │    └──▶ Wait for Approvals                               │
│     │                                                             │
│  6. Issue LC (Trade Service)                                    │
│     ├──▶ Generate LC Number                                     │
│     ├──▶ Create LC Record                                       │
│     ├──▶ Generate SWIFT MT700                                   │
│     └──▶ Publish LCIssued Event                                │
│                                                                   │
│  Compensation (if failure):                                      │
│     ├──▶ Release Credit Limit                                   │
│     ├──▶ Cancel Workflow                                        │
│     └──▶ Notify Applicant                                       │
│                                                                   │
└─────────────────────────────────────────────────────────────────┘
```

```java
@Service
public class LCIssuanceSaga {
    
    private final CustomerService customerService;
    private final ComplianceService complianceService;
    private final RiskService riskService;
    private final WorkflowService workflowService;
    private final TradeService tradeService;
    private final SagaExecutor sagaExecutor;
    
    public LCId execute(CreateLCCommand command) {
        SagaDefinition saga = SagaDefinition.builder()
            .step("validate-customer")
                .invoke(() -> customerService.validate(command.getApplicantId()))
                .compensate(() -> { /* No compensation needed */ })
            .step("screen-parties")
                .invoke(() -> complianceService.screenParties(command))
                .compensate(() -> { /* No compensation needed */ })
            .step("assess-risk")
                .invoke(() -> riskService.assessLC(command))
                .compensate(() -> { /* No compensation needed */ })
            .step("reserve-credit-limit")
                .invoke(() -> customerService.reserveCreditLimit(
                    command.getApplicantId(), 
                    command.getAmount()))
                .compensate(() -> customerService.releaseCreditLimit(
                    command.getApplicantId(), 
                    command.getAmount()))
            .step("route-approval")
                .invoke(() -> workflowService.initiateApproval(command))
                .compensate(() -> workflowService.cancelWorkflow(command.getWorkflowId()))
            .step("issue-lc")
                .invoke(() -> tradeService.issueLetter OfCredit(command))
                .compensate(() -> tradeService.cancelLC(command.getLcId()))
            .build();
        
        return sagaExecutor.execute(saga);
    }
}
```

---

*[Document continues with sections 6-12: API Architecture, Data Architecture, Integration Architecture, Security Architecture, AI Architecture, Deployment Architecture, and Observability Architecture]*

---

**STATUS:** Part 1 of Technical Architecture Complete (Sections 1-5)
**NEXT:** Sections 6-12 (API, Data, Integration, Security, AI, Deployment, Observability)
**PAGE COUNT:** ~150 pages (estimated for complete document)

---

*End of Technical Architecture Specification - Part 1*

