# 📊 Level 2 vs Level 3 Comparison

## Spring Boot REST API: MySQL vs MongoDB

---

## 🎯 Project Overview

| Aspect | Level 2 (MySQL) | Level 3 (MongoDB) |
|--------|----------------|-------------------|
| **Database** | Relational (MySQL) | NoSQL (MongoDB) |
| **Data Model** | Tables with relations | Documents with embedded data |
| **Main Entity** | Fruit inventory | Fruit orders |
| **Relationships** | @ManyToOne (Fruit → Provider) | Embedded documents (Order → OrderItems) |
| **Business Logic** | Inventory management | Order management |

---

## 🗄️ Database Architecture

### Level 2: Relational Model (MySQL)

```
┌─────────────┐         ┌──────────────┐
│   Fruit     │         │   Provider   │
├─────────────┤         ├──────────────┤
│ id (PK)     │────┐    │ id (PK)      │
│ name        │    └───>│ name         │
│ weightKg    │         │ country      │
│ provider_id │         │ email        │
└─────────────┘         └──────────────┘
```

**Characteristics:**
- Normalized data (3NF)
- Foreign key relationships
- JOINs to retrieve related data
- Referential integrity enforced by DB
- Provider exists independently

**SQL Query Example:**
```sql
SELECT f.*, p.name as provider_name 
FROM fruit f 
JOIN provider p ON f.provider_id = p.id 
WHERE f.id = 1;
```

---

### Level 3: Document Model (MongoDB)

```json
{
  "_id": "675ec8f9a1234567890abcde",
  "clientName": "John Doe",
  "deliveryDate": "2025-12-17",
  "items": [
    {
      "fruitName": "Apple",
      "quantityInKilos": 5
    },
    {
      "fruitName": "Banana",
      "quantityInKilos": 3
    }
  ]
}
```

**Characteristics:**
- Denormalized (embedded documents)
- No JOINs needed
- All data in one document
- No foreign keys
- Items don't exist independently

**MongoDB Query Example:**
```javascript
db.orders.findOne({ "_id": ObjectId("675ec8f9a1234567890abcde") })
// Returns complete order with all items
```

---

## 📐 Entity Design

### Level 2: Separate Entities with Relations

**Fruit Entity:**
```java
@Entity
@Table(name = "fruit")
public class Fruit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private int weightInKilos;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id")
    private Provider provider;
}
```

**Provider Entity:**
```java
@Entity
@Table(name = "provider")
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String country;
    private String email;
    
    @OneToMany(mappedBy = "provider")
    private List<Fruit> fruits;
}
```

**Key Points:**
- Two separate entities
- Bidirectional relationship
- LazyInitializationException risks
- Foreign key in database

---

### Level 3: Single Document with Embedded Data

**Order Entity:**
```java
@Document(collection = "orders")
public class Order {
    @Id
    private String id; // MongoDB ObjectId
    
    private String clientName;
    private LocalDate deliveryDate;
    private List<OrderItem> items; // Embedded
}
```

**OrderItem (Embedded):**
```java
public class OrderItem {
    private String fruitName;
    private int quantityInKilos;
    // No @Entity, no @Id
}
```

**Key Points:**
- Single document
- No separate collection for items
- No relationships
- No LazyInitializationException

---

## 🔍 Data Retrieval Comparison

### Level 2: JOINs Required

**Repository:**
```java
public interface FruitRepository extends JpaRepository<Fruit, Long> {
    // Returns Fruit with Provider loaded (EAGER fetch)
    Optional<Fruit> findById(Long id);
    
    // Custom query with JOIN
    @Query("SELECT f FROM Fruit f JOIN FETCH f.provider WHERE f.name = :name")
    List<Fruit> findByNameWithProvider(@Param("name") String name);
}
```

**Issues to Handle:**
- N+1 query problem (LAZY fetch)
- LazyInitializationException
- JOIN performance on large tables
- FetchType strategy decisions

---

### Level 3: Single Document Query

**Repository:**
```java
public interface OrderRepository extends MongoRepository<Order, String> {
    // Returns complete Order with all items
    Optional<Order> findById(String id);
    
    // No JOINs needed!
    List<Order> findByClientName(String clientName);
}
```

**Advantages:**
- No N+1 problem
- No LazyInitializationException
- Single query retrieves all data
- Simpler code

---

## 🔑 Primary Keys

| Aspect | Level 2 (MySQL) | Level 3 (MongoDB) |
|--------|-----------------|-------------------|
| **Type** | `Long` | `String` |
| **Generation** | `@GeneratedValue(AUTO_INCREMENT)` | MongoDB generates ObjectId |
| **Format** | 1, 2, 3, ... | "675ec8f9a1234567890abcde" |
| **Size** | 8 bytes | 12 bytes |
| **Predictable** | ✅ Sequential | ❌ Random |
| **URL-safe** | ✅ Yes | ✅ Yes |

---

## ✅ Validation Differences

### Common Validations (Both Levels)

```java
@NotBlank(message = "Name is required")
private String name;

@Positive(message = "Quantity must be positive")
private int quantity;
```

### Level 2 Specific

```java
@ManyToOne
@NotNull(message = "Provider is required")
private Provider provider;
```

### Level 3 Specific

```java
@FutureDate(message = "Delivery date must be at least tomorrow")
@NotNull(message = "Delivery date is required")
private LocalDate deliveryDate;

@NotEmpty(message = "At least one item is required")
@Valid // Validates nested items
private List<OrderItem> items;
```

**Custom Validator:** @FutureDate (Level 3 only)

---

## 🧪 Testing Strategy

### Level 2: H2 In-Memory Database

**Configuration:**
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
```

**Advantages:**
- Fast tests
- No external dependencies
- Auto schema creation

**Disadvantages:**
- H2 ≠ MySQL (dialect differences)
- Some MySQL features not supported
- False positives possible

---

### Level 3: Testcontainers (Real MongoDB)

**Configuration:**
```java
@Bean
@ServiceConnection
MongoDBContainer mongoDBContainer() {
    return new MongoDBContainer(DockerImageName.parse("mongo:7.0"))
            .withReuse(true);
}
```

**Advantages:**
- Real MongoDB instance
- 100% production-like tests
- Catches MongoDB-specific issues
- No dialect differences

**Disadvantages:**
- Requires Docker
- Slightly slower tests
- More resource intensive

**Winner:** Level 3 (more realistic testing)

---

## 🔄 CRUD Operations Comparison

### CREATE

**Level 2:**
```java
// Must create Provider first
Provider provider = providerRepository.save(new Provider(...));

// Then create Fruit with provider reference
Fruit fruit = new Fruit();
fruit.setProvider(provider);
fruitRepository.save(fruit);
```

**Level 3:**
```java
// Everything in one object
Order order = new Order();
order.setItems(List.of(new OrderItem(...), new OrderItem(...)));
orderRepository.save(order);
```

**Winner:** Level 3 (simpler, atomic)

---

### READ

**Level 2:**
```java
// May trigger additional query for Provider (if LAZY)
Fruit fruit = fruitRepository.findById(1L)
    .orElseThrow(...);
String providerName = fruit.getProvider().getName(); // Possible LazyInitializationException
```

**Level 3:**
```java
// Single query, all data included
Order order = orderRepository.findById(id)
    .orElseThrow(...);
List<OrderItem> items = order.getItems(); // No extra query
```

**Winner:** Level 3 (no lazy loading issues)

---

### UPDATE

**Level 2:**
```java
// Update Fruit
Fruit fruit = fruitRepository.findById(1L).orElseThrow(...);
fruit.setWeightInKilos(10);

// Change Provider (must exist)
Provider newProvider = providerRepository.findById(2L).orElseThrow(...);
fruit.setProvider(newProvider);

fruitRepository.save(fruit);
```

**Level 3:**
```java
// Update Order (replace entire document)
Order order = orderRepository.findById(id).orElseThrow(...);
order.setClientName("New Name");
order.setItems(newItemsList); // Replace all items

orderRepository.save(order); // MongoDB replaces document
```

**Winner:** Depends on use case
- Level 2: Better for partial updates, shared entities
- Level 3: Better for full document replacement

---

### DELETE

**Level 2:**
```java
// Must handle foreign key constraints
fruitRepository.deleteById(1L); 
// Provider remains (independent entity)
```

**Level 3:**
```java
// Deletes order and all embedded items
orderRepository.deleteById(id);
// Items are deleted automatically (not independent)
```

**Winner:** Level 3 (simpler, atomic)

---

## 🏗️ Architecture Patterns

### Similarities (Both Levels)

✅ MVC pattern  
✅ Service layer with business logic  
✅ Repository pattern  
✅ DTOs for input/output  
✅ Dedicated mapper  
✅ Global exception handling  
✅ Bean Validation  
✅ TDD Outside-In  

### Differences

| Pattern | Level 2 | Level 3 |
|---------|---------|---------|
| **Fetch Strategy** | EAGER/LAZY decisions | Not applicable |
| **Cascade Operations** | @Cascade configuration | Not applicable |
| **Transaction Management** | @Transactional required | Optional (atomic by default) |
| **Schema Management** | Hibernate DDL / Flyway | No schema needed |

---

## ⚡ Performance Considerations

### Level 2: MySQL

**Strengths:**
- ACID transactions
- Complex queries with JOINs
- Aggregations (GROUP BY, etc.)
- Referential integrity
- Mature optimization tools

**Challenges:**
- N+1 query problem
- JOIN overhead on large tables
- Schema migrations
- Vertical scaling limits

---

### Level 3: MongoDB

**Strengths:**
- Fast reads (no JOINs)
- Horizontal scaling (sharding)
- Flexible schema
- Atomic document updates
- Better for hierarchical data

**Challenges:**
- No ACID across documents
- Data duplication
- Large documents can impact performance
- Limited JOIN support (lookup)

---

## 🔐 Data Integrity

### Level 2: Database-Enforced

```sql
-- Foreign key constraint
ALTER TABLE fruit 
ADD CONSTRAINT fk_provider 
FOREIGN KEY (provider_id) 
REFERENCES provider(id);
```

**Advantages:**
- Database prevents invalid data
- Cascade deletes/updates
- Referential integrity guaranteed

**Disadvantages:**
- Can complicate deletions
- Migration complexity

---

### Level 3: Application-Enforced

```java
// No database constraints
// Validation in application layer only
@Valid
private List<OrderItem> items;
```

**Advantages:**
- Flexible schema changes
- No foreign key constraints
- Simpler deletions

**Disadvantages:**
- Must validate in code
- No database-level integrity
- Orphaned data possible (if not careful)

---

## 🐳 Docker Configuration

### Level 2: MySQL

```yaml
services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: fruitdb
    ports:
      - "3306:3306"
```

**Considerations:**
- Schema initialization scripts
- Character set configuration
- Connection pool tuning

---

### Level 3: MongoDB

```yaml
services:
  mongodb:
    image: mongo:7.0
    environment:
      MONGO_INITDB_DATABASE: fruit_orders
    ports:
      - "27017:27017"
```

**Considerations:**
- No schema needed
- Simpler configuration
- Replica sets for production

---

## 📊 When to Use Each

### Choose MySQL (Level 2) When:

✅ Data has clear relationships  
✅ Need ACID transactions across tables  
✅ Complex queries with JOINs are common  
✅ Data structure is stable  
✅ Referential integrity is critical  
✅ Team is familiar with SQL  
✅ Reporting/analytics are priority  

**Example Use Cases:**
- E-commerce (orders, products, users)
- Financial systems
- HR management systems
- Inventory with suppliers

---

### Choose MongoDB (Level 3) When:

✅ Hierarchical/nested data  
✅ Schema flexibility needed  
✅ Fast reads are priority  
✅ Horizontal scaling required  
✅ Data is document-oriented  
✅ Agile development with changing requirements  
✅ Write-heavy workloads  

**Example Use Cases:**
- Content management systems
- Real-time analytics
- IoT data storage
- Social media feeds
- Product catalogs

---

## 🎓 Learning Outcomes

### Level 2 (MySQL) Taught:

📚 JPA relationships (@ManyToOne, @OneToMany)  
📚 Fetch strategies (EAGER vs LAZY)  
📚 Foreign keys and constraints  
📚 N+1 problem and solutions  
📚 Transaction management  
📚 Schema migrations  
📚 H2 for testing  

---

### Level 3 (MongoDB) Taught:

📚 NoSQL concepts  
📚 Document-oriented design  
📚 Embedded vs referenced documents  
📚 Custom validators (ConstraintValidator)  
📚 Testcontainers for realistic testing  
📚 ObjectId vs auto-increment  
📚 Schema-less flexibility  

---

## 📈 Complexity Comparison

| Aspect | Level 2 | Level 3 |
|--------|---------|---------|
| **Entity Design** | 🔴 Complex (relationships) | 🟢 Simple (documents) |
| **Queries** | 🔴 Complex (JOINs) | 🟢 Simple (no JOINs) |
| **Testing** | 🟡 Medium (H2 mock) | 🟢 Simple (Testcontainers) |
| **Validation** | 🟢 Standard | 🟡 Custom validator needed |
| **Data Modeling** | 🟢 Well-established (normalization) | 🟡 Requires understanding of embedding |
| **Migrations** | 🔴 Required (Flyway/Liquibase) | 🟢 Not needed |

---

## 🎯 Final Verdict

### Level 2 (MySQL) is Better For:
- Traditional business applications
- Strong consistency requirements
- Complex relationships between entities
- Teams familiar with SQL
- Reporting and analytics

### Level 3 (MongoDB) is Better For:
- Fast development cycles
- Flexible/evolving schemas
- Hierarchical data structures
- High read/write throughput
- Horizontal scaling needs

**Both are valid choices** - the "best" depends on your specific requirements!

---

## 🔄 Migration Path

### From Level 2 to Level 3

**What Changes:**
- Remove JPA annotations (@Entity, @ManyToOne, etc.)
- Add @Document annotation
- Remove foreign keys, add embedded documents
- Change Long id → String id
- Replace H2 tests with Testcontainers
- Remove JOIN queries

**What Stays:**
- MVC architecture
- Service layer pattern
- DTOs and validation
- Exception handling
- Controller structure
- TDD approach

**Effort:** Medium (architectural changes needed)

---

## 📝 Code Comparison Summary

### Level 2 Entity (14 annotations)
```java
@Entity
@Table(name = "fruit")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fruit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    private String name;
    
    @Positive
    private int weightInKilos;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id")
    @NotNull
    private Provider provider;
}
```

### Level 3 Entity (5 annotations)
```java
@Document(collection = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    private String id;
    
    private String clientName;
    private LocalDate deliveryDate;
    private List<OrderItem> items;
}
```

**Winner:** Level 3 (simpler, less boilerplate)

---

**Conclusion:** Both approaches are valuable. Level 2 teaches relational database best practices, while Level 3 introduces modern NoSQL concepts. Understanding both makes you a more versatile developer! 🚀