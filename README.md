# TNC Analyzer

> AI-powered Terms & Conditions risk analysis built as a modern Spring Boot microservice stack.
>
> This project combines secure authentication, gateway routing, AI evaluation, and persisted analysis history.

---

## ✨ Project summary

`tnc-analyzer` is a developer-focused microservice application that demonstrates how to:

- build a secure gateway-based architecture,
- authenticate users with JWT,
- integrate AI-driven policy analysis using Google Gemini,
- persist analysis results for audit and user history,
- expose filtering, sorting, and pagination on analysis data.

This README is written for developers, maintainers, and reviewers who want a clear view of architecture, startup, features, and future direction.

---

## 🏛 Architecture at a glance

```text
 Client / Frontend
   │
   ▼
 API Gateway (8080)
   │ authentication + authorization + header enrichment
   ├─> Analysis Service (8081)
   │       • AI risk analysis
   │       • history storage
   │
   └─> Auth Service (8082)
           • login / register
           • JWT issuance

 PostgreSQL
   • users
   • analysis history
```

### Why this architecture?

- **Gateway first**: all client traffic enters through `api-gateway`, so routing and auth are centralized.
- **Service isolation**: `auth-service` owns identity, `analysis-service` owns AI analysis and history.
- **AI integration layer**: analysis service delegates Gemini calls to a dedicated AI client, making the model backend replaceable.
- **History persistence**: analysis results are saved and exposed through a filtered, pageable API.

---

## 📌 Core modules

### `api-gateway`

- Spring Cloud Gateway
- routes `/analyses/**` to analysis service
- validates JWT tokens before forwarding
- adds `X-Authenticated-User` to requests for audit context

### `auth-service`

- Spring Boot Web + Security
- user registration and login
- BCrypt password hashing
- JWT token generation for stateless access

### `analysis-service`

- Spring Boot Web + JPA + WebFlux
- receives analysis requests and validates payload
- calls Gemini AI for structured risk analysis
- stores results to PostgreSQL
- exposes history, filtering, sorting, and paging

---

## 🚀 What the app does today

### Authentication & gateway flow

- Users register and login through `auth-service`
- Login returns a JWT token
- `api-gateway` validates JWT before forwarding requests
- authenticated username is propagated to analysis service

### AI analysis workflow

- `POST /analyses` accepts raw Terms & Conditions text
- `analysis-service` sends a structured prompt to Gemini
- the response is converted into a DTO and persisted
- the client receives a safety summary and analysis payload

### History & analytics

- `GET /analyses` returns user-specific history
- supports filters like `riskLevel` and `keyword`
- supports pagination and dynamic sorting
- history is built from persisted analysis results, not volatile state

---

## 🔥 Feature highlights

- AI-driven T&C analysis using Google Gemini
- secure JWT-based auth and gateway validation
- request enrichment with authenticated user header
- persistent analysis history in PostgreSQL
- dynamic query support with pagination and sorting
- clean separation between auth, gateway, and analysis service functionality

---

## ⚡ Startup guide

### 1. prerequisites

- Java 21
- Docker
- Gradle wrapper

> Secrets are managed via configuration or environment variables. Do not store credentials directly in this README.

### 2. start infrastructure

```bash
docker-compose up -d
```

### 3. start services

From the repo root:

```powershell
.\gradlew.bat :auth-service:bootRun
.\gradlew.bat :analysis-service:bootRun
.\gradlew.bat :api-gateway:bootRun
```

### 4. run from IDE

Run these main classes:

- `com.tnc.AuthServiceApplication`
- `com.tnc.AnalysisServiceApplication`
- `com.tnc.ApiGatewayApplication`

---

## 🧠 Scope and future planning

### Completed scope

- multi-module Spring Boot architecture
- PostgreSQL persistence for users and analysis history
- JWT auth and gateway-based validation
- AI integration with Gemini
- history API with filtering, sorting, and pagination

### Next-phase focus

- externalize secrets and configuration safely
- improve API error responses and validation handling
- migrate Gemini integration to a reactive or async flow
- add refresh token support and stronger session handling
- introduce integration tests and CI pipelines
- split persistence to service-specific databases for stronger isolation
- add caching or async processing with Redis/Kafka when load grows

---

## 📁 Useful references

- `docker-compose.yml`
- `tncAnalyzerAPIs.postman_collection.json`
- `WorkNotes.txt`

---

## 📌 Notes for maintainers

This project is intended as both a working proof of concept and a learning platform. The README focuses on architecture, startup, features, and planning, while the detailed notes are captured in `WorkNotes.txt`.
