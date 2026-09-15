# Backend Codebase Audit

**Scope:** `propertypilot-backend/`  
**Audit date:** 2026-09-01  
**Java files inspected:** 82 of 82

## Executive Result

The directory is not a Spring Boot codebase. It is a generated file-tree prototype in which planning documentation was saved under Java, XML, YAML, properties, SQL, shell, and batch filenames.

Java classification:

| Classification | Count | Result |
| --- | ---: | --- |
| Valid Java code | 0 | None |
| Generated placeholder text | 82 | Every Java file |
| Documentation instead of code | 82 | Every Java file contains a variation of “Backend Foundation Generation Plan” |
| Files with a Java `package` declaration | 0 | Compilation blocker |
| Files with Java imports | 0 | Confirms absence of implementation |
| Files with Markdown headings | 82 | Usually 6–7 headings per file |

Every Java file contains the phrases `Backend Foundation Generation Plan`, `Overview`, `Architecture`, `Implementation Steps`, and `Conclusion`. Apparent words such as `class`, `interface`, or `enum` found in a few files occur in English prose and are not Java declarations.

## Inspection Criteria

Each `.java` file was read and checked for:

- A syntactically valid `package ...;` declaration
- Import statements
- A class, interface, enum, or record matching the filename
- Java syntax rather than Markdown headings and lists
- Spring/JPA/test annotations where the filename implies them
- Whether the contents could be compiled independently

A file is classified as generated documentation when it starts with `# Backend_Foundation_Generation_Plan.md` or contains the repeated backend-plan sections instead of Java syntax.

## Complete Java File Classification

All files in the following inventory have the same classification: **generated placeholder documentation; invalid Java; replace or delete**.

### Application entry point

| File | Classification |
| --- | --- |
| `src/main/java/com/propertypilot/PropertyPilotApplication.java` | Generated documentation; no application class |

### API controllers

| File | Classification |
| --- | --- |
| `api/controller/AdminController.java` | Generated documentation; no controller |
| `api/controller/AgentController.java` | Generated documentation; no controller |
| `api/controller/AuthController.java` | Generated documentation; no controller |
| `api/controller/CustomerController.java` | Generated documentation; no controller |
| `api/controller/OperationsController.java` | Generated documentation; no controller |

### Request and response DTOs

| File | Classification |
| --- | --- |
| `api/dto/request/LoginRequest.java` | Generated documentation; no DTO |
| `api/dto/request/RegisterRequest.java` | Generated documentation; no DTO |
| `api/dto/request/UpdateProfileRequest.java` | Generated documentation; no DTO |
| `api/dto/response/AuthResponse.java` | Generated documentation; no DTO |
| `api/dto/response/BookingResponse.java` | Generated documentation; no DTO |
| `api/dto/response/PropertyResponse.java` | Generated documentation; no DTO |

### API mappers and validation

| File | Classification |
| --- | --- |
| `api/mapper/BookingMapper.java` | Generated documentation; no mapper |
| `api/mapper/CustomerMapper.java` | Generated documentation; no mapper |
| `api/mapper/PropertyMapper.java` | Generated documentation; no mapper |
| `api/validation/ValidRequestValidator.java` | Generated documentation; no validator |

### Application exceptions

| File | Classification |
| --- | --- |
| `application/exception/ApiException.java` | Generated documentation; no exception type |
| `application/exception/GlobalExceptionHandler.java` | Generated documentation; no exception handler |

### Application services

| File | Classification |
| --- | --- |
| `application/service/AuditService.java` | Generated documentation; no service |
| `application/service/AuthService.java` | Generated documentation; no service |
| `application/service/BookingService.java` | Generated documentation; no service |
| `application/service/NotificationService.java` | Generated documentation; no service |
| `application/service/PaymentService.java` | Generated documentation; no service |
| `application/service/PropertyService.java` | Generated documentation; no service |
| `application/service/UserService.java` | Generated documentation; no service |

### Application use cases

| File | Classification |
| --- | --- |
| `application/usecase/CreateBookingUseCase.java` | Generated documentation; no use case |
| `application/usecase/ProcessPaymentUseCase.java` | Generated documentation; no use case |
| `application/usecase/RegisterCustomerUseCase.java` | Generated documentation; no use case |

### Common response and pagination types

| File | Classification |
| --- | --- |
| `common/pagination/PageRequest.java` | Generated documentation; no pagination type |
| `common/pagination/PaginatedResponse.java` | Generated documentation; no response type |
| `common/response/ApiResponse.java` | Generated documentation; no response type |

### Configuration

| File | Classification |
| --- | --- |
| `config/ApplicationProperties.java` | Generated documentation; no configuration-properties type |
| `config/ClockConfig.java` | Generated documentation; no Spring configuration |
| `config/JpaAuditingConfig.java` | Generated documentation; no JPA auditing configuration |
| `config/OpenApiConfig.java` | Generated documentation; no OpenAPI configuration |

### Domain enums

| File | Classification |
| --- | --- |
| `domain/enum/BookingStatus.java` | Generated documentation; no enum |
| `domain/enum/PaymentStatus.java` | Generated documentation; no enum |
| `domain/enum/ServiceStatus.java` | Generated documentation; no enum |
| `domain/enum/UserRole.java` | Generated documentation; no enum |

### Domain models

| File | Classification |
| --- | --- |
| `domain/model/AuditLog.java` | Generated documentation; no model |
| `domain/model/Booking.java` | Generated documentation; no model |
| `domain/model/Notification.java` | Generated documentation; no model |
| `domain/model/Payment.java` | Generated documentation; no model |
| `domain/model/Property.java` | Generated documentation; no model |
| `domain/model/Role.java` | Generated documentation; no model |
| `domain/model/ServiceRequest.java` | Generated documentation; no model |
| `domain/model/User.java` | Generated documentation; no model |

### Domain repositories

| File | Classification |
| --- | --- |
| `domain/repository/AuditLogRepository.java` | Generated documentation; no repository interface |
| `domain/repository/BookingRepository.java` | Generated documentation; no repository interface |
| `domain/repository/PaymentRepository.java` | Generated documentation; no repository interface |
| `domain/repository/PropertyRepository.java` | Generated documentation; no repository interface |
| `domain/repository/ServiceRequestRepository.java` | Generated documentation; no repository interface |
| `domain/repository/UserRepository.java` | Generated documentation; no repository interface |

### Cache infrastructure

| File | Classification |
| --- | --- |
| `infrastructure/cache/CacheService.java` | Generated documentation; no cache service |
| `infrastructure/cache/RedisConfig.java` | Generated documentation; no Redis configuration |
| `infrastructure/cache/RedisRepository.java` | Generated documentation; no Redis repository |

### Messaging infrastructure

| File | Classification |
| --- | --- |
| `infrastructure/messaging/EventConsumer.java` | Generated documentation; no consumer |
| `infrastructure/messaging/EventPublisher.java` | Generated documentation; no publisher |
| `infrastructure/messaging/KafkaConfig.java` | Generated documentation; no Kafka configuration |

### Persistence entities

| File | Classification |
| --- | --- |
| `infrastructure/persistence/entity/BookingEntity.java` | Generated documentation; no entity |
| `infrastructure/persistence/entity/PaymentEntity.java` | Generated documentation; no entity |
| `infrastructure/persistence/entity/PropertyEntity.java` | Generated documentation; no entity |
| `infrastructure/persistence/entity/UserEntity.java` | Generated documentation; no entity |

### JPA repositories

| File | Classification |
| --- | --- |
| `infrastructure/persistence/jpa/JpaBookingRepository.java` | Generated documentation; no JPA repository |
| `infrastructure/persistence/jpa/JpaPropertyRepository.java` | Generated documentation; no JPA repository |
| `infrastructure/persistence/jpa/JpaUserRepository.java` | Generated documentation; no JPA repository |

### Security infrastructure

| File | Classification |
| --- | --- |
| `infrastructure/security/CustomUserDetailsService.java` | Generated documentation; no user-details service |
| `infrastructure/security/JwtAuthenticationEntryPoint.java` | Generated documentation; no authentication entry point |
| `infrastructure/security/JwtAuthenticationFilter.java` | Generated documentation; no authentication filter |
| `infrastructure/security/JwtService.java` | Generated documentation; no JWT service |
| `infrastructure/security/SecurityConfig.java` | Generated documentation; no Spring Security configuration |
| `infrastructure/security/UserPrincipal.java` | Generated documentation; no principal type |

### Shared constants, utilities, and validation

| File | Classification |
| --- | --- |
| `shared/constants/ApiConstants.java` | Generated documentation; no constants type |
| `shared/constants/SecurityConstants.java` | Generated documentation; no constants type |
| `shared/util/DateTimeUtils.java` | Generated documentation; no utility type |
| `shared/util/PaginationUtils.java` | Generated documentation; no utility type |
| `shared/util/PasswordUtils.java` | Generated documentation; no utility type |
| `shared/validation/EnumValueValidator.java` | Generated documentation; no validator |

### Tests

| File | Classification |
| --- | --- |
| `src/test/java/com/propertypilot/api/controller/AuthControllerTest.java` | Generated documentation; no test class |
| `src/test/java/com/propertypilot/application/service/PropertyServiceTest.java` | Generated documentation; no test class |
| `src/test/java/com/propertypilot/integration/ApiIntegrationTest.java` | Generated documentation; no integration test |
| `src/test/java/com/propertypilot/security/JwtServiceTest.java` | Generated documentation; no test class |

## Files To Keep

### Java files

**None can be kept as Java implementation.** There is no executable statement, declaration, API signature, test, or business rule to preserve.

The filenames may be retained temporarily as a requirements inventory, but the files themselves must not survive into the clean source tree merely to preserve apparent progress.

### Repository artifacts worth retaining for review

These are outside the Java classification and may remain until their replacements are verified:

- `.editorconfig` as an intended formatting-policy location, after validating its actual content.
- `.env.example` as the intended environment-variable reference, after removing generated prose.
- `README.md` and `Backend_Foundation_Generation_Plan.md` as planning inputs, consolidated with the canonical documentation rather than treated as application files.
- `docker-compose.yml` as a desired local-stack location, but only after it becomes valid YAML.
- The conventional `src/main/java`, `src/main/resources`, and `src/test/java` directory roots.

## Files To Replace

Replace rather than delete the filenames that are required for the first executable Spring Boot foundation. Replacement means write a valid implementation from approved requirements; it does not mean trying to edit the Markdown into Java.

### Build and application foundation

- `pom.xml`
- Maven wrapper scripts and `.mvn/wrapper/maven-wrapper.properties`
- `PropertyPilotApplication.java`
- `application.yml`, environment profile configuration, and `logback-spring.xml`
- Valid Flyway migrations after database reconciliation

### Initial MVP backend slice

- Authentication/security configuration and services
- User and role persistence required by authentication
- Customer, property, and service-request domain/persistence/API layers
- Common API error response and global exception handling
- Pagination types actually required by list endpoints
- Auditing configuration and minimal audit persistence
- Unit and integration tests for the implemented slice

Some current filenames can be reused where they match the approved model, including `AuthController`, `PropertyService`, `Property`, `ServiceRequest`, `User`, `Role`, `UserRepository`, `PropertyRepository`, `ServiceRequestRepository`, `SecurityConfig`, `JwtService`, `GlobalExceptionHandler`, and their corresponding DTO/test concepts. Their current contents must be fully replaced.

## Files To Delete

Delete placeholder Java files that are outside the approved first MVP slice instead of replacing them speculatively:

- Booking types, mapper, service, repository, persistence entity, response, and use case unless “booking” is formally reconciled with the documented service-request/visit workflow.
- Payment service, status, model, repository, entity, and processing use case until the payments phase begins.
- Notification model/service until the notification delivery contract and provider strategy are approved.
- Kafka configuration, publisher, and consumer until an actual asynchronous use case requires Kafka.
- Redis service/config/repository until a measured cache or session requirement exists.
- Admin, agent, and operations controllers until their implemented use cases and contracts are scheduled.
- Generic utilities and constants with no immediate caller.
- Duplicate domain-model versus persistence-entity abstractions unless the team explicitly adopts that mapping boundary.
- All four placeholder test files; add real tests alongside real behavior rather than preserving their names as coverage evidence.

Deletion should occur in the same reviewed change that installs the valid project foundation, so the repository never presents placeholder files as active code.

## Package Structure Problems

### No packages exist in source

The directory path suggests `com.propertypilot...`, but none of the 82 files declares `package com.propertypilot...;`. Java packages are determined by declarations, not directory names.

### Reserved keyword used as a package segment

`domain/enum/` implies `com.propertypilot.domain.enum`. `enum` is a Java keyword and cannot be used as an identifier in a package declaration. Use `domain.enums`, or keep enums within their owning feature package.

### Mixed architectural styles

The tree mixes:

- Layered packages (`controller`, `service`, `repository`)
- Clean/hexagonal terminology (`domain`, `application`, `infrastructure`)
- Use-case classes plus broad service classes
- Domain repositories plus JPA repositories
- Domain models plus separate persistence entities
- Global technical packages plus feature concepts

None is implemented, so the duplication currently adds navigation cost without providing separation. Choose one structure before generating real files.

### Missing core feature consistency

The proposed packages include booking and payment but omit a complete customer aggregate, visit/evidence workflow, and consistent service-request API. This does not align cleanly with the documented initial vertical slice.

### Package-to-test mismatch

`src/test/java/com/propertypilot/security/JwtServiceTest.java` does not mirror the proposed production package `com.propertypilot.infrastructure.security`. Tests should normally mirror the package of the class under test unless intentionally testing through a public boundary.

### Generic shared package risk

`common` and `shared` overlap. Both would become catch-all packages. Use a single small cross-cutting package only for truly shared stable types; keep feature-specific validation, pagination decisions, and constants with their owning feature.

## Compilation Problems

Compilation cannot start. The problems occur before dependency resolution:

1. `pom.xml` is Markdown, not Maven XML, and has no `<project>` element.
2. `mvnw`, `mvnw.cmd`, and `maven-wrapper.properties` contain Markdown rather than wrapper scripts/properties.
3. All 82 `.java` files begin with `#`, which is an illegal Java token in this context.
4. No Java file contains a package declaration.
5. No usable public class, interface, enum, or record exists.
6. `PropertyPilotApplication.java` has no `main` method and no `@SpringBootApplication` class.
7. Test files contain no JUnit test classes or methods.
8. YAML configuration files contain Markdown headings and lists rather than Spring configuration.
9. `logback-spring.xml` and other XML-named files are not XML.
10. SQL migration files contain planning prose rather than executable SQL.
11. The Docker Compose file cannot be trusted as runtime configuration until validated separately.
12. Java and Maven were not available on the audit PATH; after repairing the source, the development toolchain must also be installed or supplied through a container/CI environment.

Because the POM and wrapper are invalid, running Maven would not produce a meaningful list of ordinary Java compiler errors. The source tree must first be replaced with a valid minimal project.

## Recommended Clean Spring Boot Structure

Use a single deployable Spring Boot application organized by feature. Keep the initial structure small and add packages only when real code requires them.

```text
propertypilot-backend/
|-- pom.xml
|-- mvnw
|-- mvnw.cmd
|-- .mvn/wrapper/
|-- src/
|   |-- main/
|   |   |-- java/com/propertypilot/
|   |   |   |-- PropertyPilotApplication.java
|   |   |   |-- config/
|   |   |   |-- common/
|   |   |   |   `-- error/
|   |   |   |-- auth/
|   |   |   |   |-- api/
|   |   |   |   |-- application/
|   |   |   |   `-- persistence/
|   |   |   |-- customer/
|   |   |   |   |-- api/
|   |   |   |   |-- application/
|   |   |   |   |-- domain/
|   |   |   |   `-- persistence/
|   |   |   |-- property/
|   |   |   |   |-- api/
|   |   |   |   |-- application/
|   |   |   |   |-- domain/
|   |   |   |   `-- persistence/
|   |   |   `-- servicerequest/
|   |   |       |-- api/
|   |   |       |-- application/
|   |   |       |-- domain/
|   |   |       `-- persistence/
|   |   `-- resources/
|   |       |-- application.yml
|   |       |-- application-dev.yml
|   |       |-- application-prod.yml
|   |       `-- db/migration/
|   `-- test/java/com/propertypilot/
|       |-- auth/
|       |-- customer/
|       |-- property/
|       |-- servicerequest/
|       `-- integration/
`-- README.md
```

Structure rules:

- Package by business feature first and technical layer second.
- Keep controllers, request/response DTOs, and API mappers inside the feature `api` package.
- Keep transaction orchestration in the feature `application` package.
- Keep entities/value objects and state rules in the feature `domain` package where that separation adds value.
- Keep Spring Data repositories and JPA mappings in the feature `persistence` package.
- Do not introduce Redis, Kafka, payment, notification, booking, admin, or operations packages until scheduled behavior requires them.
- Mirror production packages under `src/test/java` and maintain a separate integration-test area for application boundaries.
- Keep one authoritative model per concept unless a documented mapping boundary justifies separate domain and persistence types.

## Recommended Recovery Sequence

1. Preserve this audit and the backend folder path.
2. Confirm the approved Sprint 1 scope and database/API authorities.
3. Remove the placeholder source/configuration/build files in one reviewed replacement change.
4. Establish a valid minimal Maven/Spring Boot application and wrapper.
5. Install the reconciled PostgreSQL Flyway migration series.
6. Add only authentication, customer, property, and service-request packages.
7. Add real tests with each behavior; do not recreate the entire placeholder inventory.
8. Add later features only when their contracts and dependencies are approved.

## Final Decision

- **Files containing valid Java:** none.
- **Files containing generated placeholder text:** all 82 Java files.
- **Files containing documentation instead of code:** all 82 Java files.
- **Java files to keep unchanged:** none.
- **Recovery approach:** replace the backend foundation, not repair individual placeholder documents as Java.
