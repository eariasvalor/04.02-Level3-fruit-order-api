# 📦 Fruit Order API - MongoDB

REST API for managing fruit orders using MongoDB with embedded documents.

**Level 3** | Spring Boot 3.x + MongoDB + Docker | TDD Outside-In

---

## 🎯 Overview

API to manage fruit orders with:
- Client name
- Delivery date (minimum tomorrow)
- List of fruit items with quantities

Orders stored as MongoDB documents with embedded items.

---

## 🌐 Endpoints

| Method | Endpoint | Description | Status |
|--------|----------|-------------|--------|
| POST | `/orders` | Create order | 201 |
| GET | `/orders` | Get all orders | 200 |
| GET | `/orders/{id}` | Get order by ID | 200/404 |
| PUT | `/orders/{id}` | Update order | 200/404 |
| DELETE | `/orders/{id}` | Delete order | 204/404 |
| GET | `/actuator/health` | Health check | 200 |

---

## 📝 Data Model

```json
{
  "id": "675ec8f9a1234567890abcde",
  "clientName": "John Doe",
  "deliveryDate": "2025-12-17",
  "items": [
    {
      "fruitName": "Apple",
      "quantityInKilos": 5
    }
  ]
}
```

---

## 🚀 Quick Start

### With Docker Compose (Recommended)

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down
```

API: http://localhost:8080

### Local Development

```bash
# Run tests
./mvnw test

# Run application (MongoDB required)
./mvnw spring-boot:run
```

---

## 🧪 Testing

**Total: 46 tests (100% passing)**
- 30 integration tests (controllers)
- 14 unit tests (services)
- 2 application tests

**TDD Outside-In approach:**
1. Integration tests define behavior
2. Unit tests verify logic
3. Implementation fulfills tests

```bash
# Run all tests
./mvnw test

# Specific tests
./mvnw test -Dtest=OrderControllerIntegrationTest

# Coverage report
./mvnw test jacoco:report
```

### Testcontainers: Real MongoDB in Tests

**Why Testcontainers?**
- Real MongoDB instance (not mocked or embedded)
- Ensures tests match production behavior
- Docker-based, isolated, and disposable
- Automatically starts/stops containers

**Configuration:**

```java
@TestConfiguration(proxyBeanMethods = false)
public class TestContainersConfiguration {
    
    @Bean
    @ServiceConnection
    MongoDBContainer mongoDBContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:7.0"))
                .withReuse(true); // Reuse container for faster tests
    }
}
```

**Usage in Tests:**

```java
@SpringBootTest
@AutoConfigureMockMvc
@Import(TestContainersConfiguration.class)
public abstract class BaseIntegrationTest {
    // All integration tests extend this class
}
```

**What Happens:**
1. Testcontainers starts MongoDB container before tests
2. Spring connects to containerized MongoDB automatically
3. Each test gets clean database (@BeforeEach cleans data)
4. Container stops after all tests complete

**Benefits:**
- ✅ Tests use real MongoDB (no mocks)
- ✅ Tests verify actual database operations
- ✅ Catches MongoDB-specific issues
- ✅ No need for local MongoDB installation

---

## 📋 API Examples

### Create Order

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "John Doe",
    "deliveryDate": "2025-12-17",
    "items": [
      {"fruitName": "Apple", "quantityInKilos": 5},
      {"fruitName": "Banana", "quantityInKilos": 3}
    ]
  }'
```

### Get All Orders

```bash
curl http://localhost:8080/orders
```

### Get Order by ID

```bash
curl http://localhost:8080/orders/{id}
```

### Update Order

```bash
curl -X PUT http://localhost:8080/orders/{id} \
  -H "Content-Type: application/json" \
  -d '{
    "clientName": "Jane Smith",
    "deliveryDate": "2025-12-20",
    "items": [
      {"fruitName": "Orange", "quantityInKilos": 10}
    ]
  }'
```

### Delete Order

```bash
curl -X DELETE http://localhost:8080/orders/{id}
```

---

## ⚠️ Validation Rules

- **clientName:** Required, not blank
- **deliveryDate:** Required, must be at least tomorrow (@FutureDate)
- **items:** Required, at least one item
- **fruitName:** Required, not blank
- **quantityInKilos:** Required, positive number

### Custom Validation: @FutureDate

Custom annotation that validates delivery date must be **at least tomorrow**.

**Implementation:**

```java
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FutureDateValidator.class)
public @interface FutureDate {
    String message() default "Delivery date must be at least tomorrow";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
```

**Validator Logic:**

```java
public class FutureDateValidator implements ConstraintValidator<FutureDate, LocalDate> {
    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        if (date == null) return true; // @NotNull handles null
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        return !date.isBefore(tomorrow);
    }
}
```

**Usage in DTO:**

```java
@FutureDate(message = "Delivery date must be at least tomorrow")
@NotNull(message = "Delivery date is required")
private LocalDate deliveryDate;
```

**Why Custom Validation?**
- `@Future` only checks if date is after now (allows today)
- `@FutureDate` enforces business rule: minimum tomorrow
- Reusable across multiple DTOs

### Error Response Example

```json
{
  "timestamp": "2025-12-15T12:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Delivery date must be at least tomorrow",
  "path": "/orders"
}
```

---

## 🏗️ Architecture

```
controllers/    → REST endpoints
services/       → Business logic
repository/     → MongoDB access
model/          → Entities (Order, OrderItem)
dto/            → Data transfer objects
exception/      → Custom exceptions & handler
validation/     → Custom validators (@FutureDate)
```

**Patterns:** MVC, TDD Outside-In, SOLID principles

---

## 🐳 Docker

### Multi-stage Dockerfile

- **Stage 1:** Build with Maven + JDK
- **Stage 2:** Run with JRE only (lightweight)

### Build & Run

```bash
# Build image
docker build -t fruit-order-api:latest .

# Run with docker-compose
docker-compose up -d
```

---

## 🔧 Technologies

- Spring Boot 3.4.1
- Spring Data MongoDB
- Bean Validation (JSR-380)
- Spring Boot Actuator
- Lombok
- Testcontainers
- JUnit 5 + Mockito
- Docker + Docker Compose
- Maven

---

## ⚙️ Configuration

### Environment Variables

```bash
MONGODB_URI=mongodb://localhost:27017/fruit_orders
MONGODB_DATABASE=fruit_orders
SERVER_PORT=8080
LOG_LEVEL=INFO
```

---

## 📊 Project Structure

```
src/
├── main/java/.../fruit/
│   ├── controllers/
│   ├── dto/
│   ├── model/
│   ├── services/
│   ├── mapper/
│   ├── repository/
│   ├── exception/
│   └── validation/
└── test/java/.../fruit/
    ├── config/
    ├── controllers/
    └── services/
```

---

## ✨ Key Features

✅ Full CRUD with MongoDB  
✅ Embedded documents (OrderItem in Order)  
✅ **Custom validation (@FutureDate with ConstraintValidator)**  
✅ Global exception handling  
✅ TDD Outside-In approach  
✅ **46 tests with Testcontainers (real MongoDB)**  
✅ Multi-stage Docker build  
✅ Health checks configured  
✅ DTOs with Bean Validation  
✅ Dedicated mapper for conversions  

---

## 📚 Requirements

- Java 21 (LTS)
- Maven 3.8+
- Docker 20.10+
- MongoDB 7.0 (optional with Docker)
