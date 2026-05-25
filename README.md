# Bookstore

Java Spring Boot implementation of Bookstore-3. This repository contains a microservice-based bookstore system with backend services, Backend-for-Frontend (BFF) services, Kafka-based customer registration events, CRM email handling, Docker Compose support, Kubernetes manifests, and AWS CloudFormation deployment files.

## Services

The system contains five Spring Boot services:

- `book-service`
- `customer-service`
- `crm-service`
- `web-bff`
- `mobile-bff`

The backend services expose book and customer APIs. The BFF services sit in front of the backend services and enforce JWT-based authorization. The customer service publishes customer registration events to Kafka, and the CRM service consumes those events.

---

## Table of Contents

1. [Overview](#1-overview)
2. [Architecture](#2-architecture)
3. [Services](#3-services)
4. [Tech Stack](#4-tech-stack)
5. [Implemented Features](#5-implemented-features)
6. [Project Structure](#6-project-structure)
7. [API Overview](#7-api-overview)
8. [JWT Rules](#8-jwt-rules)
9. [BFF Behavior](#9-bff-behavior)
10. [Kafka and CRM Flow](#10-kafka-and-crm-flow)
11. [Environment Variables](#11-environment-variables)
12. [Local Development Setup](#12-local-development-setup)
13. [Local Run With Docker Compose](#13-local-run-with-docker-compose)
14. [Useful Test Commands](#14-useful-test-commands)
15. [Database](#15-database)
16. [Kubernetes Deployment Notes](#16-kubernetes-deployment-notes)
17. [AWS Deployment Notes](#17-aws-deployment-notes)
18. [Error Handling](#18-error-handling)
19. [Important Notes](#19-important-notes)

---

## 1. Overview

This project is a Spring Boot microservice bookstore application. It separates responsibilities across services:

- `book-service` handles book data and book-related APIs.
- `customer-service` handles customer data and customer-related APIs.
- `crm-service` consumes customer registration events and sends/logs customer emails.
- `web-bff` exposes APIs for web clients.
- `mobile-bff` exposes APIs for mobile clients and applies mobile-specific response transformations.

The system uses MySQL for persistence, Kafka for asynchronous customer registration events, and BFF services for request forwarding and JWT validation.

---

## 2. Architecture

```text
Client
  |
  |--- Web requests ---> web-bff
  |
  |--- Mobile requests -> mobile-bff
                         |
                         |--- /books/* -----> book-service -----> book_db
                         |
                         |--- /customers/* -> customer-service -> customer_db
                                                   |
                                                   | publishes customer event
                                                   v
                                                Kafka topic
                                                   |
                                                   v
                                              crm-service
```

The BFF services route requests based on the path:

- `/books/**` routes to `book-service`
- `/customers/**` routes to `customer-service`

Customer creation also triggers an event to Kafka. The CRM service listens to the Kafka topic and handles the customer registration event.

---

## 3. Services

### 3.1 book-service

Responsible for book-related functionality.

**Default port:** `3000`

**Implemented endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| POST | `/books` | Create a new book |
| PUT | `/books/{isbn}` | Update an existing book |
| GET | `/books` | Get all books |
| GET | `/books/{isbn}` | Get book details by ISBN |
| GET | `/books/isbn/{isbn}` | Alternative path to get book details by ISBN |
| GET | `/books/{isbn}/related-books` | Get related/recommended books |
| GET | `/status` | Health check |

Book service also includes LLM summary support. When a book is created or fetched, the service may generate and store a summary if one is missing.

The `related-books` endpoint calls an external recommendation service using:

```
/recommended-titles/isbn/{isbn}
```

If the recommendation service times out, the endpoint may return `504 Gateway Timeout`. If the circuit breaker is open, it may return `503 Service Unavailable`.

### 3.2 customer-service

Responsible for customer-related functionality.

**Default port:** `3000`

**Implemented endpoints:**

| Method | Path | Description |
|--------|------|-------------|
| POST | `/customers` | Create a new customer |
| GET | `/customers/{id}` | Get customer by numeric ID |
| GET | `/customers?userId=...` | Get customer by userId |
| GET | `/status` | Health check |

When a customer is created successfully, `customer-service` publishes a customer registration event to Kafka.

### 3.3 crm-service

Responsible for consuming customer registration events from Kafka.

**Default port:** `4000`

**Main responsibilities:**

- Consume customer registration events from Kafka
- Use the configured Andrew ID
- Use SMTP settings for email handling
- Log or send email notification behavior depending on configuration

This service does not expose the main book/customer REST APIs. It is an event-driven service.

### 3.4 web-bff

Frontend-facing BFF service for web clients.

**Default port:** `80`  
**Docker Compose host port:** `8081`

**Main behavior:**

- Validates `Authorization: Bearer <token>`
- Requires `X-Client-Type` header to be present
- Forwards `/books/**` requests to `book-service`
- Forwards `/customers/**` requests to `customer-service`
- Preserves selected response headers such as `Location` and `Content-Type`
- Does not modify successful backend responses

**Implemented forwarded endpoints:**

| Method | Path |
|--------|------|
| POST | `/books` |
| PUT | `/books/{isbn}` |
| GET | `/books` |
| GET | `/books/{isbn}` |
| GET | `/books/isbn/{isbn}` |
| GET | `/books/{isbn}/related-books` |
| POST | `/customers` |
| GET | `/customers/{id}` |
| GET | `/customers?userId=...` |
| GET | `/status` |

### 3.5 mobile-bff

Frontend-facing BFF service for mobile clients.

**Default port:** `80`  
**Docker Compose host port:** `8082`

**Main behavior:**

- Validates `Authorization: Bearer <token>`
- Requires `X-Client-Type` header to be present
- Forwards `/books/**` requests to `book-service`
- Forwards `/customers/**` requests to `customer-service`
- Applies mobile-specific transformations to selected successful responses

**Implemented forwarded endpoints:**

| Method | Path |
|--------|------|
| POST | `/books` |
| PUT | `/books/{isbn}` |
| GET | `/books` |
| GET | `/books/{isbn}` |
| GET | `/books/isbn/{isbn}` |
| GET | `/books/{isbn}/related-books` |
| POST | `/customers` |
| GET | `/customers/{id}` |
| GET | `/customers?userId=...` |
| GET | `/status` |

---

## 4. Tech Stack

- Java
- Spring Boot
- Maven
- MySQL 8
- Spring JDBC
- Spring Kafka
- Kafka
- Docker
- Docker Compose
- Kubernetes
- AWS CloudFormation
- AWS EC2 / EKS-style deployment files
- SMTP email configuration
- Optional LLM summary support

---

## 5. Implemented Features

This repository implements:

- Multiple Spring Boot microservices
- Separate book and customer services
- Web and mobile BFF services
- JWT validation in both BFFs
- Request forwarding from BFFs to backend services
- Mobile-specific response shaping
- Customer registration event publishing through Kafka
- CRM Kafka consumer service
- MySQL database separation for books and customers
- Dockerfiles for each service
- Docker Compose local setup
- Kubernetes YAML manifests
- AWS CloudFormation deployment file
- `/status` endpoint on the main HTTP services

---

## 6. Project Structure

```
bookstore-3/
├── aws/
├── database/
├── k8s/
│   ├── book-service.yaml
│   ├── crm-service.yaml
│   ├── customer-service.yaml
│   ├── mobile-bff.yaml
│   ├── namespace.yaml
│   └── web-bff.yaml
├── services/
│   ├── book-service/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── src/
│   ├── customer-service/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── src/
│   ├── crm-service/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── src/
│   ├── mobile-bff/
│   │   ├── Dockerfile
│   │   ├── pom.xml
│   │   └── src/
│   └── web-bff/
│       ├── Dockerfile
│       ├── pom.xml
│       └── src/
├── CF-A3-cmu.yml
├── db-init.sql
├── docker-compose.yml
├── local_test.sh
├── setup_local_db.sql
├── README.md
└── url.txt
```

---

## 7. API Overview

### 7.1 Health Check

```
GET /status
```

**Response:**
```
OK
```

### 7.2 Book APIs

Book APIs are exposed directly by `book-service` and through both BFF services.

| Method | Path | Description |
|--------|------|-------------|
| POST | `/books` | Create a new book |
| PUT | `/books/{isbn}` | Update an existing book |
| GET | `/books` | Get all books |
| GET | `/books/{isbn}` | Get book by ISBN |
| GET | `/books/isbn/{isbn}` | Get book by ISBN using alternative path |
| GET | `/books/{isbn}/related-books` | Get related books from recommendation service |

**Example create book body:**

```json
{
  "isbn": "9780134685991",
  "title": "Effective Java",
  "author": "Joshua Bloch",
  "description": "A practical guide to best practices in Java programming.",
  "genre": "non-fiction",
  "price": 45.99,
  "quantity": 10
}
```

### 7.3 Customer APIs

Customer APIs are exposed directly by `customer-service` and through both BFF services.

| Method | Path | Description |
|--------|------|-------------|
| POST | `/customers` | Create a new customer |
| GET | `/customers/{id}` | Get customer by numeric ID |
| GET | `/customers?userId=...` | Get customer by userId |

**Example create customer body:**

```json
{
  "userId": "sampleuser@gmail.com",
  "name": "Sample User",
  "phone": "+14125550111",
  "address": "5000 Forbes Ave",
  "address2": "Apt 1",
  "city": "Pittsburgh",
  "state": "PA",
  "zipcode": "15213"
}
```

---

## 8. JWT Rules

Both BFF services require:

```
Authorization: Bearer <token>
```

The JWT validation checks the decoded payload. It does not verify the cryptographic signature.

**The token must:**

- Have exactly three dot-separated JWT sections
- Contain a Base64 URL encoded JSON payload
- Contain `iss`
- Contain `sub`
- Contain numeric `exp`
- Have `exp` in the future

**Allowed `sub` values:**

- `starlord`
- `gamora`
- `drax`
- `rocket`
- `groot`

**Required issuer:** `cmu.edu`

If the `Authorization` header is missing, malformed, expired, or invalid, the BFF returns:

```
401 Unauthorized
```

**Example local unsigned JWT generator:**

```bash
JWT=$(python3 - <<'PY'
import base64
import json
import time

def b64url(obj):
    return base64.urlsafe_b64encode(json.dumps(obj).encode()).decode().rstrip("=")

header = b64url({"alg": "none", "typ": "JWT"})
payload = b64url({
    "iss": "cmu.edu",
    "sub": "starlord",
    "exp": int(time.time()) + 3600
})
signature = "dummy_sig"

print(f"{header}.{payload}.{signature}")
PY
)

echo "$JWT"
```

---

## 9. BFF Behavior

### 9.1 X-Client-Type Header

Both BFF services require the `X-Client-Type` header to be present and non-empty.

```
X-Client-Type: Web
```
or
```
X-Client-Type: Android
```

> **Note:** The current implementation checks only that the header exists and is not blank. It does not currently enforce exact values such as `Web`, `iOS`, or `Android`.

If the header is missing or blank, the BFF returns:

```
400 Bad Request
```

### 9.2 Web BFF Response Behavior

`web-bff` forwards responses without changing successful response bodies. It routes:

- `/books/**` to `book-service`
- `/customers/**` to `customer-service`

### 9.3 Mobile BFF Response Behavior

`mobile-bff` transforms selected successful responses.

**Book transformation**

For:
- `GET /books`
- `GET /books/{isbn}`
- `GET /books/isbn/{isbn}`

If a book has:
```json
{ "genre": "non-fiction" }
```
the mobile BFF changes it to:
```json
{ "genre": 3 }
```

This applies to both single-book responses and book-list responses.

**Customer transformation**

For:
- `GET /customers/{id}`
- `GET /customers?userId=...`

The mobile BFF removes the following fields:

- `address`
- `address2`
- `city`
- `state`
- `zipcode`

---

## 10. Kafka and CRM Flow

When a customer is created via `POST /customers`, the `customer-service`:

1. Validates the request
2. Checks whether the `userId` already exists
3. Inserts the customer into `customer_db`
4. Publishes the created customer event to Kafka

**Kafka topic default:** `rdhurand.customer.evt`

The `crm-service` consumes this topic and handles customer registration email behavior.

---

## 11. Environment Variables

### 11.1 book-service

| Variable | Default |
|----------|---------|
| `SERVER_PORT` | `3000` |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `book_db` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | `rootpassword` |
| `LLM_PROVIDER` | `gemini` |
| `LLM_API_KEY` | *(empty)* |
| `LLM_MODEL` | `gemini-1.5-flash` |
| `LLM_TIMEOUT_MS` | `15000` |
| `RECOMMENDATION_SERVICE_URL` | `http://100.51.187.149` |

> **Note:** `docker-compose.yml` currently sets `APP_RECOMMENDATION_URL`, but the service code reads `RECOMMENDATION_SERVICE_URL`. To configure the recommendation service through environment variables, use `RECOMMENDATION_SERVICE_URL`.

### 11.2 customer-service

| Variable | Default |
|----------|---------|
| `SERVER_PORT` | `3000` |
| `DB_HOST` | `localhost` |
| `DB_PORT` | `3306` |
| `DB_NAME` | `customer_db` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | `rootpassword` |
| `KAFKA_BROKERS` | `52.73.13.84:9092,100.51.187.149:9092,52.54.87.18:9092` |
| `KAFKA_TOPIC` | `rdhurand.customer.evt` |

### 11.3 crm-service

| Variable | Default |
|----------|---------|
| `SERVER_PORT` | `4000` |
| `KAFKA_BROKERS` | `52.73.13.84:9092,100.51.187.149:9092,52.54.87.18:9092` |
| `KAFKA_TOPIC` | `rdhurand.customer.evt` |
| `ANDREW_ID` | `rdhurand` |
| `SMTP_HOST` | `smtp.gmail.com` |
| `SMTP_PORT` | `587` |
| `SMTP_USER` | *(required for real email sending)* |
| `SMTP_PASS` | *(required for real email sending)* |

### 11.4 web-bff

| Variable | Default |
|----------|---------|
| `SERVER_PORT` | `80` |
| `BOOK_SERVICE_URL` | `http://localhost:3000` |
| `CUSTOMER_SERVICE_URL` | `http://localhost:3000` |

### 11.5 mobile-bff

| Variable | Default |
|----------|---------|
| `SERVER_PORT` | `80` |
| `BOOK_SERVICE_URL` | `http://localhost:3000` |
| `CUSTOMER_SERVICE_URL` | `http://localhost:3000` |

---

## 12. Local Development Setup

### Prerequisites

- Java
- Maven
- Docker
- Docker Compose
- MySQL 8 (if running without Docker)
- Kafka access (if testing customer registration events and CRM behavior)

### Database Setup Without Docker

If running MySQL locally, create the databases and tables using:

```bash
mysql -u root -p < setup_local_db.sql
```

Or use the SQL files under the `database/` directory for service-specific setup.

The Docker Compose setup uses `db-init.sql`, which creates:

- `book_db`
- `customer_db`

---

## 13. Local Run With Docker Compose

From the repository root:

```bash
docker-compose up --build
```

**Port mappings:**

| Service | Container Port | Host Port |
|---------|---------------|-----------|
| MySQL | 3306 | 3307 |
| book-service | 3000 | 3001 |
| customer-service | 3000 | 3002 |
| crm-service | 4000 | *(not exposed by default)* |
| web-bff | 3000 | 8081 |
| mobile-bff | 3000 | 8082 |

**To stop the system:**

```bash
docker-compose down
```

**To remove containers and volumes:**

```bash
docker-compose down -v
```

---

## 14. Useful Test Commands

### 14.1 Generate JWT

```bash
JWT=$(python3 - <<'PY'
import base64
import json
import time

def b64url(obj):
    return base64.urlsafe_b64encode(json.dumps(obj).encode()).decode().rstrip("=")

header = b64url({"alg": "none", "typ": "JWT"})
payload = b64url({
    "iss": "cmu.edu",
    "sub": "starlord",
    "exp": int(time.time()) + 3600
})
signature = "dummy_sig"

print(f"{header}.{payload}.{signature}")
PY
)
```

### 14.2 Health Checks

```bash
curl http://localhost:8081/status
curl http://localhost:8082/status
curl http://localhost:3001/status
curl http://localhost:3002/status
```

**Expected response:** `OK`

### 14.3 Get Book Through Web BFF

```bash
curl -i http://localhost:8081/books/9780134685991 \
  -H "X-Client-Type: Web" \
  -H "Authorization: Bearer $JWT"
```

### 14.4 Get All Books Through Mobile BFF

```bash
curl -i http://localhost:8082/books \
  -H "X-Client-Type: Android" \
  -H "Authorization: Bearer $JWT"
```

For books with `"genre": "non-fiction"`, the mobile BFF response should return `"genre": 3`.

### 14.5 Get Related Books Through Web BFF

```bash
curl -i http://localhost:8081/books/9780134685991/related-books \
  -H "X-Client-Type: Web" \
  -H "Authorization: Bearer $JWT"
```

Possible responses:

- `200 OK` — related books returned
- `204 No Content` — no related books available
- `503 Service Unavailable` — circuit breaker is open
- `504 Gateway Timeout` — recommendation service timed out

### 14.6 Create Customer Through Web BFF

```bash
curl -i -X POST http://localhost:8081/customers \
  -H "Content-Type: application/json" \
  -H "X-Client-Type: Web" \
  -H "Authorization: Bearer $JWT" \
  -d '{
    "userId": "testuser@example.com",
    "name": "Test User",
    "phone": "555-1234",
    "address": "123 Test St",
    "address2": "Apt 1",
    "city": "Pittsburgh",
    "state": "PA",
    "zipcode": "15213"
  }'
```

A successful request returns `201 Created` and publishes a customer registration event to Kafka.

### 14.7 Get Customer Through Mobile BFF

```bash
curl -i "http://localhost:8082/customers?userId=testuser%40example.com" \
  -H "X-Client-Type: Android" \
  -H "Authorization: Bearer $JWT"
```

The mobile BFF removes address-related fields from successful customer responses.

### 14.8 Check CRM Logs

```bash
docker-compose logs crm-service
```

Use this after creating a customer to verify whether the CRM service consumed the customer registration event.

---

## 15. Database

The Docker Compose database initialization file is `db-init.sql`. It creates two databases:

### book_db

**Table:** `books`

**Important columns:** `isbn`, `title`, `author`, `description`, `genre`, `price`, `quantity`, `summary`

### customer_db

**Table:** `customers`

**Important columns:** `id`, `user_id`, `name`, `phone`, `address`, `address2`, `city`, `state`, `zipcode`

---

## 16. Kubernetes Deployment Notes

The repository includes Kubernetes manifests under `k8s/`:

- `namespace.yaml`
- `book-service.yaml`
- `customer-service.yaml`
- `crm-service.yaml`
- `web-bff.yaml`
- `mobile-bff.yaml`

**Deploy all manifests:**

```bash
kubectl apply -f k8s/
```

**Check pods:**

```bash
kubectl get pods -n bookstore
```

**Check services:**

```bash
kubectl get svc -n bookstore
```

> The exact namespace depends on the contents of `k8s/namespace.yaml`.

---

## 17. AWS Deployment Notes

The repository includes `CF-A3-cmu.yml` for AWS deployment setup, as well as an `aws/` directory.

Depending on the deployment setup, the architecture may include:

- EC2 instances or Kubernetes worker nodes
- Load balancers
- MySQL-compatible database configuration
- Service-specific deployment configuration
- Public endpoints for BFF services
- Internal routing for backend services

Check `url.txt` for recorded deployed service URLs, if available.

---

## 18. Error Handling

| Status Code | Meaning |
|-------------|---------|
| `200 OK` | Successful GET or PUT |
| `201 Created` | Successful resource creation |
| `204 No Content` | No related books found |
| `400 Bad Request` | Invalid input, missing query parameter, missing `X-Client-Type` |
| `401 Unauthorized` | Missing or invalid JWT |
| `404 Not Found` | Book or customer not found |
| `409 Conflict` | Duplicate ISBN or duplicate customer `userId` |
| `503 Service Unavailable` | Recommendation circuit breaker is open |
| `504 Gateway Timeout` | Recommendation service timeout |
| `500 Internal Server Error` | Unexpected server-side error |

---

## 19. Important Notes

- The current BFF code requires `X-Client-Type` to be present, but does not validate exact client type values.
- JWT signature verification is not implemented; the BFF validates only token structure and payload claims.
- `book-service` and `customer-service` both default to port `3000` — when running outside Docker, assign different `SERVER_PORT` values.
- Docker Compose maps `book-service` to host port `3001` and `customer-service` to host port `3002`.
- Docker Compose maps `web-bff` to host port `8081` and `mobile-bff` to host port `8082`.
- The Docker Compose MySQL service maps container port `3306` to host port `3307`.
- `customer-service` publishes Kafka events to `rdhurand.customer.evt` by default.
- `crm-service` consumes the same Kafka topic by default.
- For real CRM email sending, configure SMTP credentials using `SMTP_USER` and `SMTP_PASS`.
- For recommendation-service configuration, prefer `RECOMMENDATION_SERVICE_URL` — that is the environment variable read by the current `book-service` code.
