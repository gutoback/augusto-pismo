# Augusto-Pismo

**Version:** 0.0.1-SNAPSHOT  
**Java:** 17  
**Spring Boot:** 4.0.5  
**Build Tool:** Gradle

---

## 📋 Table of Contents

- [Quick Start](#quick-start)
- [Project Overview](#project-overview)
- [API Endpoints](#api-endpoints)
- [Setup & Installation](#setup--installation)
- [Running the Application](#running-the-application)
- [Testing](#testing)
- [Database Schema](#database-schema)
- [Docker](#docker)
- [Architecture](#architecture)
- [Test Coverage](#test-coverage)

---

## 🚀 Quick Start

### Using Docker Compose (Recommended)

```bash
# Clone and navigate to the project directory
cd augusto-pismo

# Start the application with PostgreSQL
docker-compose up --build

# The API will be available at http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Local Development

```bash
# Prerequisites
# - Java 17+
# - PostgreSQL running on localhost:5432
# - Gradle (or use ./gradlew)

# Build the project
./gradlew clean build

# Run the application
./gradlew bootRun

# API available at http://localhost:8080
```

---

## 📖 Project Overview

**Augusto-Pismo** is a fintech microservice that provides:

- ✅ **Account Management** - Create and retrieve accounts with unique document numbers
- ✅ **Transaction Processing** - Record transactions with automatic amount negation logic
- ✅ **Operation Types** - 4 pre-configured transaction types (Purchase, Installments, Withdrawal, Credit)
- ✅ **Automatic Timestamps** - Events automatically timestamped on creation
- ✅ **Database Versioning** - Flyway migrations for schema management
- ✅ **Modular Architecture** - Spring Modulith for scalable design
- ✅ **Comprehensive Testing** - 45+ unit and integration tests
- ✅ **OpenAPI Documentation** - Swagger UI for interactive API exploration

---

## 🔌 API Endpoints

### Accounts API (`/api/v1/accounts`)

#### Create Account
```http
POST /api/v1/accounts
Content-Type: application/json

{
  "document_number": "12345678901"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "document_number": "12345678901"
}
```

**Error (400 Bad Request):**
- Empty document number
- Document number exceeds max length

---

#### Get Account by ID
```http
GET /api/v1/accounts/{id}
```

**Response (200 OK):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "document_number": "12345678901"
}
```

**Error (404 Not Found):**
- Account with given ID does not exist

---

### Transactions API (`/api/v1/transaction`)

#### Create Transaction
```http
POST /api/v1/transaction
Content-Type: application/json

{
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "operationTypeId": "550e8400-e29b-41d4-a716-446655440001",
  "amount": "100.50"
}
```

**Response (201 Created):**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440002",
  "accountId": "550e8400-e29b-41d4-a716-446655440000",
  "type": {
    "id": "550e8400-e29b-41d4-a716-446655440001",
    "description": "Normal Purchase"
  },
  "amount": "-100.50",
  "event_date": "2026-04-02T00:32:36Z"
}
```

**Notes:**
- Amount is **negated** for operation types with `should_negate_amount = true`
- Operation types with negation: Purchase, Installments, Withdrawal
- Operation types without negation: Credit Voucher
- `event_date` is automatically set on creation

**Error (404 Not Found):**
- Account or OperationType does not exist

---

### Operation Types

Pre-configured operation types available in the system:

| ID | Description | Negate Amount |
|----|-------------|---------------|
| 1 | Normal Purchase | ✅ Yes |
| 2 | Purchase with installments | ✅ Yes |
| 3 | Withdrawal | ✅ Yes |
| 4 | Credit Voucher | ❌ No |

---

## 🛠️ Setup & Installation

### Prerequisites

- **Java 17+**
- **PostgreSQL 12+** (if running locally)
- **Gradle 8.0+** (or use bundled `./gradlew`)
- **Docker & Docker Compose** (optional, for containerized setup)

### 1. Clone the Repository

```bash
git clone <repository-url>
cd augusto-pismo
```

### 2. Database Configuration

#### Option A: Using Docker Compose (Recommended)
```bash
docker-compose up -d postgres
# Wait for PostgreSQL to be ready
docker-compose up
```

#### Option B: Local PostgreSQL
Create a PostgreSQL database:
```sql
CREATE DATABASE augusto_pismo;
CREATE USER app_user WITH PASSWORD 'app_password';
GRANT ALL PRIVILEGES ON DATABASE augusto_pismo TO app_user;
```

Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/augusto_pismo
spring.datasource.username=app_user
spring.datasource.password=app_password
```

### 3. Build the Project

```bash
# Clean and build
./gradlew clean build

# Run tests during build
./gradlew clean build --info
```

---

## ▶️ Running the Application

### Via Gradle

```bash
./gradlew bootRun
```

The application starts on `http://localhost:8080`

### Via Docker Compose

```bash
docker-compose up --build
```

Services:
- **PostgreSQL:** `localhost:5432`
- **Application:** `http://localhost:8080`
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`

### Via Docker (Manual)

```bash
# Build Docker image
docker build -t augusto-pismo:latest .

# Run container
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/mydb \
  -e SPRING_DATASOURCE_USERNAME=myuser \
  -e SPRING_DATASOURCE_PASSWORD=secret \
  augusto-pismo:latest
```

---

## 🧪 Testing

### Run All Tests

```bash
./gradlew test
```

### Run Unit Tests Only

```bash
./gradlew unit-test
```

### Run Integration Tests Only

```bash
./gradlew integration-test
```

### Run Tests with Coverage

```bash
./gradlew test jacocoTestReport
# Coverage report: build/reports/jacoco/test/html/index.html
```

### Test Categories

**Tests are tagged with:**
- `test-unit` - Unit tests (mocked dependencies)
- `test-integration` - Integration tests (real Spring context)

---

## 📊 Test Coverage

**Total Tests:** 45+ test methods across 9 test files

### Unit Tests (23 tests)

| Component | Tests | Coverage |
|-----------|-------|----------|
| **GlobalExceptionHandler** | 6 | Exception handling, error formatting |
| **AccountService** | 5 | Account CRUD, error handling |
| **TransactionService** | 5 | Transaction creation, amount negation |
| **OperationTypeService** | 7 | Operation type lookup, negation logic |

### Integration Tests (22+ tests)

| Component | Tests | Coverage |
|-----------|-------|----------|
| **Application Context** | 3 | Spring boot startup, module validation |
| **AccountController** | 7 | REST endpoint CRUD operations |
| **TransactionController** | 7 | Transaction creation, validation |
| **End-to-End** | 4 | Complete workflows from account to transaction |

### Key Test Scenarios

✅ Account creation and retrieval  
✅ Transaction creation with amount negation  
✅ Credit transactions without negation  
✅ Invalid operation types (404 errors)  
✅ Non-existent accounts (404 errors)  
✅ Invalid document numbers (400 errors)  
✅ Automatic timestamp generation  
✅ Multiple transactions per account  
✅ Large amounts (999999.99)  
✅ Zero amounts  
✅ Exception handling and error details  

### Running Tests in IDE

**IntelliJ IDEA:**
- Right-click test file → "Run" or "Run with Coverage"
- Use test tags: right-click → "Run Tests with Tags" → `test-unit` or `test-integration`

**VS Code with Extension Pack for Java:**
```bash
# Command palette → Test: Run All Tests
# Command palette → Test: Run Test at Cursor
```

---

## 💾 Database Schema

### Accounts Table
```sql
CREATE TABLE accounts (
  id UUID PRIMARY KEY,
  document_number VARCHAR(255) UNIQUE NOT NULL
);
```

### Operation Types Table
```sql
CREATE TABLE operation_type (
  id UUID PRIMARY KEY,
  description VARCHAR(50) NOT NULL,
  should_negate_amount BOOLEAN DEFAULT FALSE
);

-- Pre-populated with 4 types:
-- 1. Normal Purchase (negate=true)
-- 2. Purchase with installments (negate=true)
-- 3. Withdrawal (negate=true)
-- 4. Credit Voucher (negate=false)
```

### Transactions Table
```sql
CREATE TABLE transactions (
  id UUID PRIMARY KEY,
  account_id UUID NOT NULL,
  operation_id UUID NOT NULL,
  amount NUMERIC(19,2) NOT NULL,
  event_date TIMESTAMP NOT NULL,
  FOREIGN KEY (account_id) REFERENCES accounts(id),
  FOREIGN KEY (operation_id) REFERENCES operation_type(id)
);

CREATE INDEX idx_transaction_account 
  ON transactions(account_id, operation_id);
```

### Entity Relationships
```
Accounts (1) ──────────────┐
                            │
                       (Many)
                            │
Transactions (Many) ────────┼──── OperationTypes (1)
                            │
                       (One)
```

---

## 🐳 Docker

### Docker Compose Services

**PostgreSQL (postgres)**
- Image: `postgres:18.3`
- Port: 5432
- Database: `mydatabase`
- Username: `myuser`
- Password: `secret`
- Health Check: `pg_isready`

**Application (app)**
- Image: Built from `Dockerfile`
- Port: 8080
- Depends on: PostgreSQL (health check)
- Network: `app-network`

### Building Docker Image

```bash
docker build -t augusto-pismo:1.0 .
```

**Multi-stage Build:**
1. **Build Stage** - Uses `gradle:9.4-jdk17-alpine` to compile
2. **Runtime Stage** - Uses `eclipse-temurin:17-jre-alpine` (lightweight)

### Environment Variables

Configure via `docker-compose.yml` or runtime:

```env
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/mydb
SPRING_DATASOURCE_USERNAME=myuser
SPRING_DATASOURCE_PASSWORD=secret
SPRING_JPA_HIBERNATE_DDL_AUTO=none
SPRING_JPA_SHOW_SQL=true
```

---

## 🏗️ Architecture

### Layered Architecture

```
┌─────────────────────────────┐
│   REST Controllers          │  (Account, Transaction)
├─────────────────────────────┤
│   Services (Business Logic) │  (AccountService, TransactionService)
├─────────────────────────────┤
│   Repositories (Data Access)│  (AccountRepository, TransactionRepository)
├─────────────────────────────┤
│   Entities & DTOs           │  (Account, Transaction, DTOs)
├─────────────────────────────┤
│   Database (PostgreSQL)     │  (Flyway Migrations)
└─────────────────────────────┘
```

### Key Components

**Controllers** (`account.controller`, `transaction.controller`)
- Handle HTTP requests/responses
- Validate input with Jakarta validation
- Return appropriate HTTP status codes

**Services** (`account.service`, `transaction.service`)
- Implement business logic
- Handle transactions with `@Transactional`
- Apply domain rules (e.g., amount negation)

**Repositories** (`account.repository`, `transaction.repository`)
- Extend Spring Data `CrudRepository`
- Provide database access methods
- Auto-generate queries

**DTOs** (Data Transfer Objects)
- `AccountDto`, `CreateAccountRequestDto`
- `TransactionDto`, `RequestTransactionDto`
- Separated from entities for API contracts

**Exception Handling** (`common.config.GlobalExceptionHandler`)
- Centralized exception handling
- Returns standardized `ErrorDetail` responses
- HTTP status mapping (404, 400, 500)

**Configuration** (`common.config.ModelMapperConfig`)
- Automatic entity-to-DTO mapping
- Configures ModelMapper bean

### Modular Structure (Spring Modulith)

Organized by business domain:
- `account/` - Account module
- `transaction/` - Transaction module
- `common/` - Shared utilities

---

## 📚 Additional Resources

### Swagger/OpenAPI Documentation
```
http://localhost:8080/swagger-ui.html
http://localhost:8080/v3/api-docs
```

### Health Check
```http
GET /actuator/health
```

Response:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    }
  }
}
```

### Application Properties

**Key configurations in `application.properties`:**
```properties
spring.application.name=augusto-pismo
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
```

---

## 🔧 Development Tips

### Common Tasks

**Add a new migration:**
```bash
# Create file: src/main/resources/db/migration/v1/V3__MY_MIGRATION.sql
touch src/main/resources/db/migration/v1/V3__MY_CHANGE.sql
```

**Run formatter:**
```bash
./gradlew spotlessApply  # If configured
```

**View dependencies:**
```bash
./gradlew dependencies
```

**Debug mode:**
```bash
./gradlew bootRun --debug
# App listens on port 5005 for debugger
```

---

## 📝 License

[Add license information here]

---

## 👤 Contact & Support

For issues, questions, or contributions, please reach out or create an issue in the repository.

---

**Last Updated:** April 2026  
**Maintainers:** Augusto-Pismo Team
