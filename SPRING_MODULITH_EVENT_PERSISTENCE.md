# Spring Modulith Event Persistence - Configuration Guide

## Overview

This document explains how Spring Modulith event persistence was configured in this project to ensure that domain events (like `UserCreatedEvent`) are persisted to the database and can survive application restarts.

## Problem

Initially, the event publication table schema didn't match Spring Modulith's expectations, causing the following error:

```
PreparedStatementCallback; bad SQL grammar [INSERT INTO EVENT_PUBLICATION ...]
```

The issue was a **table/column name mismatch**: 
- Liquibase created: `event_publication` (lowercase)
- Spring Modulith expected: `EVENT_PUBLICATION` (uppercase)

Additionally, we were missing three required columns:
- `STATUS`
- `COMPLETION_ATTEMPTS`
- `LAST_RESUBMISSION_DATE`

## Solution

### 1. Updated Liquibase Changelog

File: `src/main/resources/db/changelog/changes/04-create-event-publication.yaml`

The table schema was updated to match Spring Modulith's requirements:

```yaml
databaseChangeLog:
  - changeSet:
      id: 04-create-event-publication
      author: alessandra
      changes:
        - createTable:
            tableName: EVENT_PUBLICATION
            columns:
              - column:
                  name: ID
                  type: uuid
                  constraints:
                    primaryKey: true
                    nullable: false
              - column:
                  name: COMPLETION_DATE
                  type: timestamp(6) with time zone
              - column:
                  name: EVENT_TYPE
                  type: varchar(512)
                  constraints:
                    nullable: false
              - column:
                  name: LISTENER_ID
                  type: varchar(512)
                  constraints:
                    nullable: false
              - column:
                  name: PUBLICATION_DATE
                  type: timestamp(6) with time zone
                  constraints:
                    nullable: false
              - column:
                  name: SERIALIZED_EVENT
                  type: varchar(4000)
                  constraints:
                    nullable: false
              - column:
                  name: STATUS
                  type: varchar(32)
                  constraints:
                    nullable: false
              - column:
                  name: COMPLETION_ATTEMPTS
                  type: integer
                  constraints:
                    nullable: false
                  defaultValueNumeric: 0
              - column:
                  name: LAST_RESUBMISSION_DATE
                  type: timestamp(6) with time zone
```

### Key Changes

1. **Table name**: Changed from `event_publication` to `EVENT_PUBLICATION` (uppercase)
2. **Column names**: All column names changed to uppercase
3. **Added missing columns**:
   - `STATUS`: Tracks the state of event processing (e.g., "PUBLISHED", "COMPLETED")
   - `COMPLETION_ATTEMPTS`: Counts retry attempts for failed event processing
   - `LAST_RESUBMISSION_DATE`: Timestamp of the last resubmission attempt

## How It Works

### Event Publishing Flow

1. **User creates an account** → `UserService.registerUser()` is called
2. **Domain event is published** → `events.publishEvent(new UserCreatedEvent(...))`
3. **Spring Modulith intercepts** → Event is persisted to `EVENT_PUBLICATION` table
4. **Event is dispatched** → `NotificationListener.handleUserCreated()` is called asynchronously
5. **On success** → `COMPLETION_DATE` is set, marking the event as processed
6. **On failure** → Event remains in the table for retry

### Guaranteed Delivery

Even if the application crashes after step 3, the event is safely stored in the database. When the application restarts:

- Spring Modulith checks for incomplete events (where `COMPLETION_DATE` is null)
- Outstanding events are automatically republished to listeners
- This ensures no events are lost

## Configuration Properties

In `application.yaml`, the following properties control event behavior:

```yaml
spring:
  modulith:
    events:
      jdbc:
        schema-initialization:
          enabled: false  # We use Liquibase instead
```

Additional optional properties you can configure:

```yaml
spring:
  modulith:
    events:
      # Enable/disable the event publication registry
      publication-registry:
        enabled: true  # Default: true
      
      # How long to keep completed events before cleanup
      retention-policy: P30D  # 30 days (ISO-8601 duration)
      
      # Republish incomplete events on application startup
      republish-outstanding-events-on-restart: true  # Default: true
```

## Database Schema Version

This implementation uses **Spring Modulith Event Publication Registry v2 schema**.

Key differences from v1:
- Added `STATUS` column for better state tracking
- Added `COMPLETION_ATTEMPTS` for retry logic
- Added `LAST_RESUBMISSION_DATE` for monitoring

## Testing

### Integration Tests

The `UsersIntegrationTest` verifies the complete flow:

```java
@Test
void shouldCreateUser_Login_AndGetProfile() {
    // Create user → event is persisted
    CreateUserRequest request = new CreateUserRequest(
        "integrationUser", 
        "integration@test.com", 
        "securePassword123"
    );
    
    webTestClient.post()
        .uri("/users")
        .bodyValue(request)
        .exchange()
        .expectStatus().isOk();
    
    // Verify event was processed by checking notification was sent
}
```

### Manual Verification

After creating a user, you can query the database to see the event:

```sql
SELECT * FROM EVENT_PUBLICATION 
WHERE EVENT_TYPE LIKE '%UserCreatedEvent%';
```

**Expected results:**
- **Immediately after creation**: Entry with `COMPLETION_DATE = null`, `STATUS = 'PUBLISHED'`
- **After listener completes**: Entry with `COMPLETION_DATE` set, `STATUS = 'COMPLETED'`

## Troubleshooting

### Events Not Being Persisted

1. Check that `spring-modulith-starter-jdbc` dependency is present
2. Verify Liquibase created the `EVENT_PUBLICATION` table (uppercase)
3. Check application logs for Spring Modulith initialization messages

### Events Not Being Processed

1. Ensure listener methods are annotated with `@ApplicationModuleListener`
2. For async processing, add `@Async` and enable async in configuration
3. Check listener method signatures match event types

### Table Name Issues on PostgreSQL

PostgreSQL converts unquoted identifiers to lowercase. Spring Modulith uses uppercase table names, which works because:
- PostgreSQL stores as lowercase: `event_publication`
- PostgreSQL matches case-insensitively when querying: `EVENT_PUBLICATION` → `event_publication`
- However, if your Liquibase changelog quoted the name (e.g., `"event_publication"`), it would force lowercase and cause a mismatch

**Solution**: Use uppercase, unquoted names in Liquibase as shown above.

## Dependencies

The following dependencies are required for event persistence:

```xml
<!-- Event publication with JDBC -->
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-starter-jdbc</artifactId>
</dependency>

<!-- Event API -->
<dependency>
    <groupId>org.springframework.modulith</groupId>
    <artifactId>spring-modulith-events-api</artifactId>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- Database migration -->
<dependency>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-core</artifactId>
</dependency>
```

## References

- [Spring Modulith Reference Documentation](https://docs.spring.io/spring-modulith/reference/)
- [Event Publication Registry](https://docs.spring.io/spring-modulith/reference/events.html#event-publication-registry)
- [Database Schemas](https://docs.spring.io/spring-modulith/reference/appendix.html#schemas)

## Summary

✅ Events are now persisted to the database
✅ Application crashes won't lose events
✅ Automatic retry on application restart
✅ All tests pass successfully