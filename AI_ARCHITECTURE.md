# TradeSphere AI - AI Architecture & Agent Specifications
**Version:** 1.0.0  
**Date:** July 16, 2026  
**Classification:** AI/ML Architecture Document

---

# TABLE OF CONTENTS

1. [AI Strategy & Vision](#1-ai-strategy--vision)
2. [AI Architecture Overview](#2-ai-architecture-overview)
3. [AI Agent Framework](#3-ai-agent-framework)
4. [Document Agent Specification](#4-document-agent-specification)
5. [Compliance Agent Specification](#5-compliance-agent-specification)
6. [Risk Agent Specification](#6-risk-agent-specification)
7. [Workflow Agent Specification](#7-workflow-agent-specification)
8. [Knowledge Agent (RAG) Specification](#8-knowledge-agent-rag-specification)
9. [Copilot Agent Specification](#9-copilot-agent-specification)
10. [Prompt Engineering](#10-prompt-engineering)
11. [Model Selection & Management](#11-model-selection--management)
12. [AI Observability & Monitoring](#12-ai-observability--monitoring)

---

# 1. AI Strategy & Vision

## 1.1 AI-First Approach

TradeSphere AI is built with **AI at the core**, not as an afterthought. Every module exposes AI capabilities rather than treating AI as a separate feature.

### 1.1.1 Core Principles

**Intelligence Everywhere**
- AI capabilities embedded in every workflow
- Proactive assistance, not just reactive responses
- Continuous learning from user interactions

**Human-AI Collaboration**
- AI augments human expertise, doesn't replace it
- Human-in-the-loop for critical decisions
- Clear AI confidence indicators
- Explainable AI decisions

**Business Value First**
- AI solves real business problems
- Measurable ROI (time saved, errors reduced, compliance improved)
- Not AI for AI's sake

**Responsible AI**
- Transparent decision-making
- Bias detection and mitigation
- Data privacy and security
- Audit trails for all AI decisions

---

# 2. AI Architecture Overview

## 2.1 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      APPLICATION LAYER                           │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │  Trade Services │ Document Services │ Compliance Services   ││
│  └────────┬────────────────────┬──────────────────┬────────────┘│
└───────────│────────────────────│──────────────────│──────────────┘
            │                    │                  │
            ▼                    ▼                  ▼
┌─────────────────────────────────────────────────────────────────┐
│                     AI ORCHESTRATION LAYER                       │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                    AI Orchestrator                          ││
│  │  • Route requests to appropriate agents                     ││
│  │  • Manage agent lifecycle                                   ││
│  │  • Handle fallbacks                                         ││
│  │  • Aggregate responses                                      ││
│  └─────────────────────────────────────────────────────────────┘│
└────────┬───────────────┬───────────────┬────────────────┬────────┘
         │               │               │                │
         ▼               ▼               ▼                ▼
┌─────────────────────────────────────────────────────────────────┐
│                       AI AGENT LAYER                             │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐           │
│  │  Document   │  │  Compliance  │  │    Risk     │           │
│  │   Agent     │  │    Agent     │  │   Agent     │           │
│  └─────────────┘  └──────────────┘  └─────────────┘           │
│  ┌─────────────┐  ┌──────────────┐  ┌─────────────┐           │
│  │  Workflow   │  │  Knowledge   │  │   Copilot   │           │
│  │   Agent     │  │  Agent (RAG) │  │   Agent     │           │
│  └─────────────┘  └──────────────┘  └─────────────┘           │
└────────┬───────────────┬───────────────┬────────────────┬───────┘
         │               │               │                │
         ▼               ▼               ▼                ▼
┌─────────────────────────────────────────────────────────────────┐
│                        MODEL LAYER                               │
│  ┌───────────┐  ┌───────────┐  ┌───────────┐  ┌──────────────┐│
│  │   GPT-4   │  │  Claude 3 │  │  Mistral  │  │ Fine-tuned   ││
│  │    & 4V   │  │  (Sonnet/ │  │   Large   │  │   Models     ││
│  │           │  │   Opus)   │  │           │  │              ││
│  └───────────┘  └───────────┘  └───────────┘  └──────────────┘│
│  ┌───────────┐  ┌───────────┐  ┌───────────┐                  │
│  │ Embeddings│  │    OCR    │  │  Computer │                  │
│  │  (Ada-002)│  │ (Tesseract│  │   Vision  │                  │
│  │           │  │  / Azure) │  │  Models   │                  │
│  └───────────┘  └───────────┘  └───────────┘                  │
└────────┬───────────────┬───────────────┬────────────────┬───────┘
         │               │               │                │
         ▼               ▼               ▼                ▼
┌─────────────────────────────────────────────────────────────────┐
│                    INFRASTRUCTURE LAYER                          │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐ │
│  │  Vector DB   │  │  Prompt      │  │  Model Registry      │ │
│  │ (Pinecone/   │  │  Management  │  │                      │ │
│  │  Weaviate)   │  │              │  │                      │ │
│  └──────────────┘  └──────────────┘  └──────────────────────┘ │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────┐ │
│  │ Knowledge    │  │  Cache       │  │  Observability       │ │
│  │ Base         │  │  (Redis)     │  │  (Logging, Metrics)  │ │
│  └──────────────┘  └──────────────┘  └──────────────────────┘ │
└─────────────────────────────────────────────────────────────────┘
```

## 2.2 AI Service Integration

```java
@Service
public class AIOrchestrator {
    
    private final DocumentAgent documentAgent;
    private final ComplianceAgent complianceAgent;
    private final RiskAgent riskAgent;
    private final WorkflowAgent workflowAgent;
    private final KnowledgeAgent knowledgeAgent;
    private final CopilotAgent copilotAgent;
    
    private final ModelSelector modelSelector;
    private final PromptManager promptManager;
    private final AIMetrics aiMetrics;
    
    public <T> AIResponse<T> process(AIRequest request) {
        // 1. Select appropriate agent
        AIAgent agent = selectAgent(request.getIntent());
        
        // 2. Select optimal model
        AIModel model = modelSelector.selectModel(request);
        
        // 3. Get prompt template
        PromptTemplate template = promptManager.getTemplate(request.getType());
        
        // 4. Execute with fallback
        try {
            AIResponse<T> response = agent.execute(request, model, template);
            
            // 5. Log metrics
            aiMetrics.recordSuccess(agent, model, response.getLatency());
            
            return response;
            
        } catch (AIException e) {
            // Fallback to alternative model
            return executeWithFallback(request, agent, e);
        }
    }
    
    private AIAgent selectAgent(AIIntent intent) {
        return switch (intent) {
            case EXTRACT_DOCUMENT_FIELDS -> documentAgent;
            case DETECT_DISCREPANCIES -> documentAgent;
            case SCREEN_COMPLIANCE -> complianceAgent;
            case ASSESS_RISK -> riskAgent;
            case RECOMMEND_WORKFLOW -> workflowAgent;
            case QUERY_KNOWLEDGE -> knowledgeAgent;
            case GENERAL_QUERY -> copilotAgent;
            default -> throw new UnsupportedIntentException(intent);
        };
    }
}
```

---

# 3. AI Agent Framework

## 3.1 Base Agent Architecture

```java
public abstract class AIAgent {
    
    protected final LLMProvider llmProvider;
    protected final PromptManager promptManager;
    protected final ContextManager contextManager;
    protected final AIMetrics metrics;
    
    /**
     * Execute AI agent task
     */
    public <T> AIResponse<T> execute(
            AIRequest request,
            AIModel model,
            PromptTemplate template) {
        
        // 1. Prepare context
        AIContext context = contextManager.prepareContext(request);
        
        // 2. Build prompt
        String prompt = promptManager.buildPrompt(template, context);
        
        // 3. Call LLM
        long startTime = System.currentTimeMillis();
        LLMResponse llmResponse = llmProvider.generate(model, prompt, context);
        long latency = System.currentTimeMillis() - startTime;
        
        // 4. Parse response
        T result = parseResponse(llmResponse);
        
        // 5. Validate & post-process
        validateResponse(result);
        T processedResult = postProcess(result);
        
        // 6. Return with metadata
        return AIResponse.<T>builder()
            .result(processedResult)
            .confidence(llmResponse.getConfidence())
            .model(model.getName())
            .latency(latency)
            .promptTokens(llmResponse.getPromptTokens())
            .completionTokens(llmResponse.getCompletionTokens())
            .explanation(llmResponse.getExplanation())
            .build();
    }
    
    protected abstract <T> T parseResponse(LLMResponse response);
    protected abstract <T> void validateResponse(T result);
    protected abstract <T> T postProcess(T result);
}
```

---

# 4. Document Agent Specification

## 4.1 Purpose & Responsibilities

**Primary Purpose:** Intelligent document processing, field extraction, and discrepancy detection

**Key Responsibilities:**
1. Document classification
2. Field extraction (OCR + NLP)
3. Data normalization
4. Cross-document validation
5. Discrepancy detection
6. LC terms compliance checking
7. UCP 600 validation

## 4.2 Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                      DOCUMENT AGENT                              │
├─────────────────────────────────────────────────────────────────┤
│                                                                   │
│  Input: Document (PDF/Image) + LC Terms                          │
│     │                                                             │
│     ▼                                                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  STAGE 1: Document Classification                        │   │
│  │  ┌────────────────────────────────────────────────────┐  │   │
│  │  │  Vision Model (GPT-4V / Claude 3)                  │  │   │
│  │  │  → Classify document type                          │  │   │
│  │  │  → Identify layout structure                       │  │   │
│  │  └────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────┘   │
│     │                                                             │
│     ▼                                                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  STAGE 2: OCR & Text Extraction                          │   │
│  │  ┌────────────────────────────────────────────────────┐  │   │
│  │  │  Azure Document Intelligence / Tesseract OCR       │  │   │
│  │  │  → Extract all text from document                  │  │   │
│  │  │  → Preserve layout information                     │  │   │
│  │  └────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────┘   │
│     │                                                             │
│     ▼                                                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  STAGE 3: Field Extraction                               │   │
│  │  ┌────────────────────────────────────────────────────┐  │   │
│  │  │  LLM (GPT-4 / Claude 3 Opus)                       │  │   │
│  │  │  + Document-specific prompt templates              │  │   │
│  │  │  + Few-shot examples                               │  │   │
│  │  │                                                     │  │   │
│  │  │  Extract structured fields:                        │  │   │
│  │  │  • Beneficiary name                                │  │   │
│  │  │  • Invoice amount                                  │  │   │
│  │  │  • Invoice date                                    │  │   │
│  │  │  • B/L number                                      │  │   │
│  │  │  • Shipment date                                   │  │   │
│  │  │  • Port of loading/discharge                       │  │   │
│  │  │  • Goods description                               │  │   │
│  │  │  • ... 50+ fields depending on document type      │  │   │
│  │  └────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────┘   │
│     │                                                             │
│     ▼                                                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  STAGE 4: Data Normalization                             │   │
│  │  • Standardize date formats                               │   │
│  │  • Normalize amounts (handle decimals, commas)            │   │
│  │  • Clean names (trim, capitalize)                         │   │
│  │  • Validate data types                                    │   │
│  └──────────────────────────────────────────────────────────┘   │
│     │                                                             │
│     ▼                                                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  STAGE 5: Cross-Document Validation                      │   │
│  │  • Compare invoice vs packing list vs B/L                │   │
│  │  • Check amount consistency                               │   │
│  │  • Check date consistency                                 │   │
│  │  • Check party name consistency                           │   │
│  └──────────────────────────────────────────────────────────┘   │
│     │                                                             │
│     ▼                                                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  STAGE 6: LC Terms Compliance Check                       │   │
│  │  ┌────────────────────────────────────────────────────┐  │   │
│  │  │  LLM + Rules Engine                                 │  │   │
│  │  │                                                     │  │   │
│  │  │  Compare extracted fields vs LC terms:             │  │   │
│  │  │  • Beneficiary name matches?                       │  │   │
│  │  │  • Amount within tolerance?                        │  │   │
│  │  │  • Shipment date within deadline?                  │  │   │
│  │  │  • Description matches LC?                         │  │   │
│  │  │  • Ports match LC terms?                           │  │   │
│  │  └────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────┘   │
│     │                                                             │
│     ▼                                                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  STAGE 7: Discrepancy Detection                          │   │
│  │  ┌────────────────────────────────────────────────────┐  │   │
│  │  │  LLM + UCP 600 Rules Engine                        │  │   │
│  │  │                                                     │  │   │
│  │  │  Detect 50+ discrepancy types:                     │  │   │
│  │  │  • Missing documents                               │  │   │
│  │  │  • Name discrepancies                              │  │   │
│  │  │  • Amount discrepancies                            │  │   │
│  │  │  • Date discrepancies                              │  │   │
│  │  │  • Description discrepancies                       │  │   │
│  │  │  • Transport document issues                       │  │   │
│  │  │  • Insurance issues                                │  │   │
│  │  │  • UCP 600 violations                              │  │   │
│  │  │                                                     │  │   │
│  │  │  For each discrepancy:                             │  │   │
│  │  │  • Severity (CRITICAL/MAJOR/MINOR)                │  │   │
│  │  │  • Waivable? (Yes/No)                              │  │   │
│  │  │  • UCP 600 article reference                       │  │   │
│  │  │  • Explanation                                     │  │   │
│  │  │  • Suggested correction                            │  │   │
│  │  └────────────────────────────────────────────────────┘  │   │
│  └──────────────────────────────────────────────────────────┘   │
│     │                                                             │
│     ▼                                                             │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │  STAGE 8: Confidence Scoring                             │   │
│  │  • Overall confidence score (0-100%)                      │   │
│  │  • Field-level confidence scores                          │   │
│  │  • Discrepancy detection confidence                       │   │
│  │  • Human review recommendation                            │   │
│  └──────────────────────────────────────────────────────────┘   │
│     │                                                             │
│     ▼                                                             │
│  Output: Structured Document Data + Discrepancies + Confidence   │
│                                                                   │
└───────────────────────────────────────────────────────────────────┘
```

## 4.3 Field Extraction Prompts

### 4.3.1 Commercial Invoice Extraction

```
SYSTEM PROMPT:
You are an expert trade finance document analyst specializing in commercial invoices. 
Your task is to extract structured data from commercial invoices with high accuracy.

You must extract the following fields:
- Invoice number
- Invoice date
- Seller/Exporter name and address
- Buyer/Importer name and address
- Description of goods
- Quantity
- Unit price
- Total amount
- Currency
- Payment terms
- Incoterm
- Country of origin
- HS codes (if present)

Important guidelines:
1. Extract data exactly as it appears (don't modify or interpret)
2. For amounts, preserve decimals and use standard format
3. For dates, output in ISO 8601 format (YYYY-MM-DD)
4. If a field is not found, return null (don't guess)
5. Provide confidence score (0-1) for each extracted field
6. Flag any ambiguous or unclear information

USER PROMPT:
Extract all required fields from the following commercial invoice:

[OCR TEXT OF DOCUMENT]

{ocr_text}

Return the extracted data in the following JSON format:
{
  "invoice_number": "...",
  "invoice_date": "YYYY-MM-DD",
  "seller": {
    "name": "...",
    "address": "..."
  },
  "buyer": {
    "name": "...",
    "address": "..."
  },
  "items": [
    {
      "description": "...",
      "quantity": "...",
      "unit_price": "...",
      "total": "..."
    }
  ],
  "total_amount": "...",
  "currency": "...",
  "payment_terms": "...",
  "incoterm": "...",
  "country_of_origin": "...",
  "confidence_scores": {
    "invoice_number": 0.95,
    "invoice_date": 0.98,
    ...
  },
  "flags": ["...any concerns or ambiguities..."]
}
```

### 4.3.2 Bill of Lading Extraction

```
SYSTEM PROMPT:
You are an expert in transport documents, specifically Bills of Lading (B/L). 
Extract all critical information accurately.

Required fields:
- B/L number
- B/L date
- Shipper
- Consignee
- Notify party
- Vessel name
- Voyage number
- Port of loading
- Port of discharge
- Place of delivery
- Container numbers
- Description of goods
- Number of packages
- Gross weight
- Freight terms (Prepaid/Collect)
- Shipped on board date
- Clean B/L (any clauses/remarks?)

USER PROMPT:
Extract all fields from this Bill of Lading:

[OCR TEXT]

{ocr_text}

Return structured JSON with extracted fields and confidence scores.
```

## 4.4 Discrepancy Detection Logic

### 4.4.1 Discrepancy Types

```java
public enum DiscrepancyType {
    // Document-level discrepancies
    MISSING_DOCUMENT("Required document not presented"),
    EXPIRED_DOCUMENT("Document expired"),
    LATE_PRESENTATION("Documents presented after deadline"),
    
    // Party name discrepancies
    BENEFICIARY_NAME_MISMATCH("Beneficiary name doesn't match LC"),
    APPLICANT_NAME_MISMATCH("Applicant name doesn't match LC"),
    
    // Amount discrepancies
    AMOUNT_EXCEEDS_LC("Invoice amount exceeds LC amount"),
    AMOUNT_BELOW_TOLERANCE("Amount below LC tolerance"),
    AMOUNT_CURRENCY_MISMATCH("Currency doesn't match LC"),
    AMOUNT_INCONSISTENCY_ACROSS_DOCS("Amount differs across documents"),
    
    // Date discrepancies
    LATE_SHIPMENT("Shipment after latest shipment date"),
    INVOICE_DATE_AFTER_BL("Invoice dated after B/L"),
    EXPIRED_INSURANCE("Insurance expired"),
    
    // Description discrepancies
    GOODS_DESCRIPTION_MISMATCH("Goods description doesn't match LC"),
    
    // Transport discrepancies
    PORT_OF_LOADING_MISMATCH("Port of loading doesn't match LC"),
    PORT_OF_DISCHARGE_MISMATCH("Port of discharge doesn't match LC"),
    TRANSHIPMENT_NOT_ALLOWED("Transhipment found but not allowed"),
    PARTIAL_SHIPMENT_NOT_ALLOWED("Partial shipment but not allowed"),
    
    // B/L specific
    BL_NOT_CLEAN("B/L contains clauses (not clean)"),
    BL_NOT_FULL_SET("B/L full set not provided"),
    BL_NOT_ON_BOARD("B/L not marked 'Shipped on Board'"),
    
    // Insurance discrepancies
    INSUFFICIENT_INSURANCE_COVERAGE("Insurance coverage insufficient"),
    INSURANCE_CURRENCY_MISMATCH("Insurance currency doesn't match"),
    
    // UCP 600 violations
    UCP600_ARTICLE_14A("Examination period exceeded"),
    UCP600_ARTICLE_18("Commercial invoice issues"),
    UCP600_ARTICLE_20("Bill of Lading issues"),
    UCP600_ARTICLE_28("Insurance document issues"),
    
    // Other
    INCONSISTENT_DATA_ACROSS_DOCS("Data inconsistent across documents"),
    MISSING_REQUIRED_SIGNATURE("Required signature missing"),
    MISSING_STAMP("Required stamp missing");
    
    private final String description;
}
```

### 4.4.2 Discrepancy Detection Prompt

```
SYSTEM PROMPT:
You are an expert LC examiner trained in UCP 600 rules and ISBP guidelines.
Your task is to identify ALL discrepancies between presented documents and LC terms.

You must be thorough and precise. Even minor discrepancies must be flagged.

For each discrepancy found, provide:
1. Discrepancy type
2. Severity (CRITICAL / MAJOR / MINOR)
3. UCP 600 article reference (if applicable)
4. Detailed explanation
5. Is it waivable? (Yes/No with reason)
6. Suggested correction

USER PROMPT:
Letter of Credit Terms:
{lc_terms}

Presented Documents:
{documents_data}

Analyze the documents against LC terms and UCP 600 rules.
Identify ALL discrepancies.

Return in JSON format:
{
  "discrepancies": [
    {
      "type": "BENEFICIARY_NAME_MISMATCH",
      "severity": "CRITICAL",
      "ucp600_article": "Article 14(e)",
      "explanation": "Beneficiary name in invoice shows 'ABC Trading Co.' but LC shows 'ABC Trading Company Ltd.'",
      "lc_value": "ABC Trading Company Ltd.",
      "document_value": "ABC Trading Co.",
      "waivable": false,
      "waivable_reason": "Name discrepancies on principal documents are typically not waivable",
      "suggested_correction": "Obtain correct invoice with full beneficiary name"
    },
    ...
  ],
  "overall_assessment": "DISCREPANT / COMPLIANT",
  "recommendation": "REFUSE / ACCEPT / CONTACT_APPLICANT_FOR_WAIVER",
  "confidence": 0.95
}
```

## 4.5 Implementation

```java
@Service
public class DocumentAgent extends AIAgent {
    
    private final VisionModelClient visionModel;
    private final OCRService ocrService;
    private final LLMClient llmClient;
    private final UCP600RulesEngine ucp600Rules;
    
    /**
     * Extract fields from document
     */
    public DocumentExtractionResult extractFields(
            Document document,
            DocumentType documentType) {
        
        // 1. Classify document (if type unknown)
        if (documentType == null) {
            documentType = classifyDocument(document);
        }
        
        // 2. Perform OCR
        OCRResult ocrResult = ocrService.extract(document);
        
        // 3. Extract structured fields using LLM
        PromptTemplate template = promptManager.getTemplate(
            "extract_fields_" + documentType.name().toLowerCase()
        );
        
        String prompt = template.render(Map.of(
            "ocr_text", ocrResult.getText(),
            "document_type", documentType.name()
        ));
        
        LLMResponse llmResponse = llmClient.generate(
            AIModel.GPT_4_TURBO,
            prompt,
            Map.of("response_format", "json")
        );
        
        // 4. Parse and validate
        Map<String, ExtractedField> fields = parseExtractedFields(llmResponse);
        
        // 5. Normalize data
        normalizeFields(fields);
        
        return DocumentExtractionResult.builder()
            .documentId(document.getId())
            .documentType(documentType)
            .extractedFields(fields)
            .confidence(calculateOverallConfidence(fields))
            .ocrResult(ocrResult)
            .build();
    }
    
    /**
     * Detect discrepancies
     */
    public DiscrepancyDetectionResult detectDiscrepancies(
            LetterOfCredit lc,
            List<Document> presentedDocuments) {
        
        // 1. Extract fields from all documents
        Map<DocumentType, DocumentExtractionResult> extractedDocs = 
            extractAllDocuments(presentedDocuments);
        
        // 2. Check for missing documents
        List<Discrepancy> discrepancies = new ArrayList<>();
        discrepancies.addAll(checkMissingDocuments(lc, presentedDocuments));
        
        // 3. Check cross-document consistency
        discrepancies.addAll(checkCrossDocumentConsistency(extractedDocs));
        
        // 4. Check against LC terms using LLM
        PromptTemplate template = promptManager.getTemplate("detect_discrepancies");
        
        String prompt = template.render(Map.of(
            "lc_terms", serializeLCTerms(lc),
            "documents_data", serializeDocuments(extractedDocs)
        ));
        
        LLMResponse llmResponse = llmClient.generate(
            AIModel.GPT_4_TURBO,
            prompt
        );
        
        List<Discrepancy> aiDiscrepancies = parseDiscrepancies(llmResponse);
        discrepancies.addAll(aiDiscrepancies);
        
        // 5. Apply UCP 600 rules engine (rule-based validation)
        List<Discrepancy> ruleBasedDiscrepancies = 
            ucp600Rules.validate(lc, extractedDocs);
        discrepancies.addAll(ruleBasedDiscrepancies);
        
        // 6. Deduplicate and prioritize
        List<Discrepancy> finalDiscrepancies = deduplicateDiscrepancies(discrepancies);
        
        return DiscrepancyDetectionResult.builder()
            .lcId(lc.getId())
            .discrepancies(finalDiscrepancies)
            .overallAssessment(finalDiscrepancies.isEmpty() ? 
                Assessment.COMPLIANT : Assessment.DISCREPANT)
            .recommendation(determineRecommendation(finalDiscrepancies))
            .confidence(0.95)
            .build();
    }
}
```

---

# 5. Compliance Agent Specification

## 5.1 Purpose

**Primary Purpose:** Automated compliance screening for AML, sanctions, PEP, and watchlists

**Key Responsibilities:**
1. Real-time sanctions screening
2. AML risk assessment
3. PEP detection
4. Watchlist screening
5. TBML red flag detection
6. SAR generation assistance

## 5.2 Screening Logic

```java
@Service
public class ComplianceAgent extends AIAgent {
    
    private final SanctionsListProvider sanctionsLists;
    private final PEPDatabase pepDatabase;
    private final WatchlistProvider watchlists;
    private final FuzzyMatchingService fuzzyMatcher;
    private final LLMClient llmClient;
    
    /**
     * Screen party (customer, beneficiary, etc.)
     */
    public ComplianceScreeningResult screenParty(
            String partyName,
            String address,
            String country) {
        
        List<ComplianceHit> hits = new ArrayList<>();
        
        // 1. Sanctions screening
        hits.addAll(screenAgainstSanctionsList(partyName, "OFAC SDN"));
        hits.addAll(screenAgainstSanctionsList(partyName, "UN Sanctions"));
        hits.addAll(screenAgainstSanctionsList(partyName, "EU Sanctions"));
        hits.addAll(screenAgainstSanctionsList(partyName, "UK Sanctions"));
        
        // 2. PEP screening
        hits.addAll(screenAgainstPEPDatabase(partyName));
        
        // 3. Watchlist screening
        hits.addAll(screenAgainstWatchlists(partyName));
        
        // 4. AI-enhanced false positive reduction
        List<ComplianceHit> validHits = reduceFalsePositives(hits, partyName, address, country);
        
        // 5. Calculate risk score
        int riskScore = calculateComplianceRiskScore(validHits, country);
        
        return ComplianceScreeningResult.builder()
            .partyName(partyName)
            .hits(validHits)
            .riskScore(riskScore)
            .status(validHits.isEmpty() ? ComplianceStatus.CLEARED : ComplianceStatus.HIT)
            .screenedAt(Instant.now())
            .build();
    }
    
    /**
     * AI-enhanced false positive reduction
     */
    private List<ComplianceHit> reduceFalsePositives(
            List<ComplianceHit> hits,
            String partyName,
            String address,
            String country) {
        
        if (hits.isEmpty()) return hits;
        
        // Use LLM to analyze each hit
        List<ComplianceHit> validHits = new ArrayList<>();
        
        for (ComplianceHit hit : hits) {
            String prompt = String.format("""
                Analyze if this is a true sanctions match or a false positive.
                
                Party being screened:
                Name: %s
                Address: %s
                Country: %s
                
                Potential match from sanctions list:
                Name: %s
                Aliases: %s
                Address: %s
                Country: %s
                Date of Birth: %s
                
                Consider:
                1. Name similarity (exact, partial, phonetic)
                2. Geographic consistency
                3. Context clues
                4. Common names (e.g., "Mohammed Ali" is very common)
                
                Is this a TRUE MATCH or FALSE POSITIVE?
                Provide detailed reasoning.
                
                Return JSON:
                {
                  "is_true_match": true/false,
                  "confidence": 0.0-1.0,
                  "reasoning": "..."
                }
                """,
                partyName, address, country,
                hit.getMatchedName(), hit.getAliases(), 
                hit.getMatchedAddress(), hit.getMatchedCountry(),
                hit.getDateOfBirth()
            );
            
            LLMResponse response = llmClient.generate(AIModel.GPT_4_TURBO, prompt);
            MatchAnalysis analysis = parseMatchAnalysis(response);
            
            if (analysis.isTrueMatch() && analysis.getConfidence() > 0.7) {
                hit.setAiAnalysis(analysis);
                validHits.add(hit);
            }
        }
        
        return validHits;
    }
}
```

---

*[Document continues with sections 6-12 covering Risk Agent, Workflow Agent, Knowledge Agent (RAG), Copilot Agent, Prompt Engineering, Model Management, and AI Observability]*

---

**STATUS:** AI Architecture Specification - Part 1 Complete (Sections 1-5)
**NEXT:** Sections 6-12 (Remaining agents and AI infrastructure)
**ESTIMATED PAGES:** ~200 pages for complete AI architecture

---

*End of AI Architecture & Agent Specifications - Part 1*

