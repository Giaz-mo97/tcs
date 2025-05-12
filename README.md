# Customer Management API

## Project Overview

A Spring Boot RESTful service for managing customer records. Supports creating, retrieving, updating, and deleting customers, with automatic tier calculation (Silver, Gold, Platinum) based on annual spend and recency of last purchase.

## Prerequisites

- **Java 17** or higher installed
- **Maven** (or **Gradle**) for build management
- (Optional) IDE such as IntelliJ IDEA or Eclipse with annotation processing enabled for Lombok

## Build & Run Using Maven

```bash
mvn clean package
mvn spring-boot:run
```

Once started, the application runs on port **8080** by default.

## API Endpoints

- **Create Customer** (POST `/customers`)

  ```bash
  curl -X POST http://localhost:8080/customers \
    -H "Content-Type: application/json" \
    -d '{
      "name": "Alice",
      "email": "alice@example.com",
      "annualSpend": 1500,
      "lastPurchaseDate": "2025-04-01T12:00:00Z"
    }'
  ```

- **Get Customer by ID** (GET `/customers/{id}`)

  ```bash
  curl http://localhost:8080/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6
  ```

- **Get Customer by Query** (GET `/customers?name=` or `?email=`)

  ```bash
  curl http://localhost:8080/customers?email=alice@example.com
  ```

- **Update Customer** (PUT `/customers/{id}`)

  ```bash
  curl -X PUT http://localhost:8080/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6 \
    -H "Content-Type: application/json" \
    -d '{
      "name": "Alice Updated",
      "email": "alice.new@example.com",
      "annualSpend": 2000,
      "lastPurchaseDate": "2025-04-15T09:30:00Z"
    }'
  ```

- **Delete Customer** (DELETE `/customers/{id}`)

  ```bash
  curl -X DELETE http://localhost:8080/customers/3fa85f64-5717-4562-b3fc-2c963f66afa6
  ```

## H2 Console

Access the in-memory database console at:

```
http://localhost:8080/h2-console
```

Use JDBC URL:

```
jdbc:h2:mem:customers
```

## Swagger UI

Interactive API documentation available at:

```
http://localhost:8080/swagger-ui.html
```

## Testing

Execute all unit and integration tests:

```bash
mvn test
```

## Assumptions

- **ID Strategy:** UUIDs are used for primary keys
- **Database:** In-memory H2 database for development and testing
- **Tier Logic:**
    - **Platinum:** annualSpend ≥ 10000 AND lastPurchaseDate within last 6 months
    - **Gold:** annualSpend ≥ 1000 AND lastPurchaseDate within last 12 months
    - **Silver:** all other cases
- **Validation:** `email` must conform to a standard email format; `name` cannot be blank
- **Error Handling:** Validation errors return HTTP 400 with a JSON payload listing field errors

