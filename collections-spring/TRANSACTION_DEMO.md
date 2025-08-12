# Spring Data Transaction Propagation and Isolation Demo

This project demonstrates Spring Data transaction propagation and isolation levels with practical examples.

## Overview

Spring Framework provides powerful transaction management capabilities through the `@Transactional` annotation. The two key concepts are:

1. **Propagation**: How transactions relate to each other when multiple transactional methods are called
2. **Isolation**: How transactions see data modified by other concurrent transactions

## Implementation Files

- `BookEntity.java` - JPA entity with version field for optimistic locking
- `BookRepository.java` - Repository with different locking strategies
- `TransactionDemoService.java` - Service demonstrating all propagation and isolation levels
- `TransactionDemoController.java` - REST endpoints to test the features

## Transaction Propagation Levels

### 1. REQUIRED (Default)
- **Behavior**: Join existing transaction or create new one
- **Use Case**: Most common pattern for business methods
- **Example**: `addBookWithRequired()` method

### 2. REQUIRES_NEW
- **Behavior**: Always create a new transaction, suspending current one
- **Use Case**: Audit logging, independent operations
- **Example**: `updateBookCountInNewTransaction()` method

### 3. SUPPORTS
- **Behavior**: Join existing transaction if present, otherwise execute non-transactionally
- **Use Case**: Read-only operations that can work with or without transactions
- **Example**: `getAllBooks()` method

### 4. NOT_SUPPORTED
- **Behavior**: Execute non-transactionally, suspending current transaction
- **Use Case**: Operations that must not run in a transaction
- **Example**: `logBookOperation()` method

### 5. MANDATORY
- **Behavior**: Must have an existing transaction, throw exception if none
- **Use Case**: Operations that require transactional context
- **Example**: `validateBookInMandatoryTransaction()` method

### 6. NEVER
- **Behavior**: Execute non-transactionally, throw exception if transaction exists
- **Use Case**: Operations that must never run in a transaction
- **Example**: `performNonTransactionalOperation()` method

### 7. NESTED
- **Behavior**: Execute within nested transaction if supported
- **Use Case**: Partial rollback scenarios
- **Example**: `updateBookInNestedTransaction()` method

## Transaction Isolation Levels

### 1. READ_UNCOMMITTED
- **Allows**: Dirty reads, non-repeatable reads, phantom reads
- **Performance**: Highest
- **Use Case**: When performance is critical and dirty reads are acceptable

### 2. READ_COMMITTED
- **Prevents**: Dirty reads
- **Allows**: Non-repeatable reads, phantom reads
- **Use Case**: Most common isolation level

### 3. REPEATABLE_READ
- **Prevents**: Dirty reads, non-repeatable reads
- **Allows**: Phantom reads
- **Use Case**: When consistent reads within transaction are required

### 4. SERIALIZABLE
- **Prevents**: All phenomena (highest isolation)
- **Performance**: Lowest
- **Use Case**: When data consistency is critical

## Locking Strategies

### Pessimistic Locking
- **When**: Lock data during read to prevent other transactions from modifying it
- **Implementation**: `@Lock(LockModeType.PESSIMISTIC_WRITE)`
- **Use Case**: High contention scenarios where conflicts are likely

### Optimistic Locking
- **When**: Check for conflicts before committing
- **Implementation**: `@Version` field in entity
- **Use Case**: Low contention scenarios where conflicts are rare

## Testing the Implementation

### 1. Start the Application
```bash
mvn spring-boot:run
```

### 2. Access H2 Console
- URL: http://localhost:8081/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- Username: sa
- Password: password

### 3. Test Propagation
```bash
curl -X POST "http://localhost:8081/api/transactions/demo/propagation?title=Test%20Book&author=Test%20Author"
```

### 4. Add Sample Data and Test Isolation
```bash
# Add a sample book
curl -X POST "http://localhost:8081/api/transactions/demo/add-sample-book?title=Isolation%20Test&author=Demo%20Author"

# Test isolation
curl -X POST "http://localhost:8081/api/transactions/demo/isolation?title=Isolation%20Test"
```

### 5. Test Locking
```bash
# Test pessimistic locking
curl -X POST "http://localhost:8081/api/transactions/demo/pessimistic-lock?title=Isolation%20Test"

# Test optimistic locking
curl -X POST "http://localhost:8081/api/transactions/demo/optimistic-lock?title=Isolation%20Test"
```

### 6. Test Edge Cases
```bash
# Test MANDATORY propagation (should fail when called directly)
curl -X POST "http://localhost:8081/api/transactions/demo/mandatory-fail?bookId=1"

# Test NEVER propagation (should succeed when called directly)
curl -X POST "http://localhost:8081/api/transactions/demo/never-success"
```

## Key Concepts Demonstrated

### 1. Self-Injection for Proxy Methods
To ensure `@Transactional` annotations work on internal method calls, we use self-injection through `ApplicationContext`.

### 2. Transaction Status Monitoring
Using `TransactionSynchronizationManager.isActualTransactionActive()` to check transaction status.

### 3. Database Configuration
H2 in-memory database with detailed SQL logging and transaction debugging enabled.

### 4. Practical Examples
Real-world scenarios like book borrowing with concurrent access control.

## Configuration Properties

```properties
# JPA Configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Transaction Configuration
spring.jpa.properties.hibernate.connection.isolation=2

# Logging Configuration
logging.level.org.springframework.transaction=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

## Best Practices

1. **Use REQUIRED for most business methods**
2. **Use REQUIRES_NEW for independent operations**
3. **Prefer optimistic locking unless high contention expected**
4. **Choose appropriate isolation level based on consistency requirements**
5. **Monitor transaction boundaries with logging**
6. **Test concurrent scenarios thoroughly**

## Common Pitfalls

1. **Self-invocation**: Internal method calls bypass proxy, use self-injection
2. **Checked exceptions**: Don't trigger rollback by default, use `rollbackFor`
3. **Transaction boundaries**: Keep transactions as short as possible
4. **Isolation levels**: Higher isolation = lower performance
5. **Lock scope**: Minimize pessimistic lock duration
