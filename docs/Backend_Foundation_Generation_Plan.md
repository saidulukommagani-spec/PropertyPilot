````markdown
# Backend Foundation Generation Plan

Document Type: Backend Engineering Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Backend Architecture / Platform Engineering

---

# Purpose

This document defines the backend foundation for PropertyPilot using Java 21 and Spring Boot 3. It translates the product requirements, canonical data model, API specification, and domain workflows into an implementation-ready backend architecture.

The plan establishes:
- the service architecture
- major domain modules
- persistence and migration strategy
- API implementation alignment
- authentication and authorization model
- event-driven integration and outbox design
- caching strategy
- security hardening
- observability model
- sprint-based delivery plan
- implementation effort estimates and risk assessment

This blueprint is intended to be the source of truth for backend implementation, contract alignment, and delivery sequencing.

---

# Architecture Overview

## Target Architecture

PropertyPilot backend will be implemented as a modular Spring Boot 3 monolith with clear domain boundaries and future readiness for decomposition into services if scaling demands require it.

Core components:
- Spring Boot 3 application runtime
- Java 21
- Spring Security with JWT
- PostgreSQL as primary transactional database
- Redis for distributed cache and short-lived session state
- Flyway for schema versioning and migrations
- Maven for build and dependency management
- Docker for local and CI/CD environment consistency
- OpenAPI-aligned REST controllers
- asynchronous processing via eventing and background jobs
- centralized validation and exception handling
- audit logging and observability instrumentation

## Architectural Principles

- Domain-first modularity
- Explicit API contract alignment with OpenAPI
- Security and least privilege by default
- Database integrity as a first-class concern
- Event-driven integration where asynchronous workflows require it
- Clear separation between web layer, application services, domain model, persistence, and infrastructure
- CI-enforced quality gates for testing and security
- Observability built into all critical services

## High-Level Layers

1. API Layer
   - controllers
   - request/response DTOs
   - validation
   - security filters

2. Application Layer
   - use cases
   - orchestration
   - business logic
   - domain services
   - transaction boundaries

3. Domain Layer
   - entities
   - value objects
   - domain enums
   - state definitions
   - core rules

4. Persistence Layer
   - repositories
   - JPA entities
   - Flyway migrations
   - database access

5. Infrastructure Layer
   - security configuration
   - JWT provider
   - Redis config
   - event publishers
   - file storage integration
   - external integration clients

---

# Recommended Package Structure

```text
com.propertypilot
├── PropertyPilotApplication.java
├── api
│   ├── controller
│   │   ├── auth
│   │   │   ├── AuthController.java
│   │   │   └── TokenController.java
│   │   ├── customer
│   │   │   ├── CustomerController.java
│   │   │   └── CustomerProfileController.java
│   │   ├── property
│   │   │   ├── PropertyController.java
│   │   │   ├── PropertyVerificationController.java
│   │   │   └── PropertyOwnershipController.java
│   │   ├── service
│   │   │   ├── ServiceCatalogController.java
│   │   │   ├── ServiceRequestController.java
│   │   │   └── ServiceStatusController.java
│   │   ├── subscription
│   │   │   ├── SubscriptionController.java
│   │   │   └── BillingController.java
│   │   ├── payment
│   │   │   ├── PaymentController.java
│   │   │   ├── InvoiceController.java
│   │   │   └── RefundController.java
│   │   ├── report
│   │   │   ├── ReportController.java
│   │   │   └── AnalyticsController.java
│   │   ├── notification
│   │   │   ├── NotificationController.java
│   │   │   └── NotificationPreferenceController.java
│   │   ├── complaint
│   │   │   ├── ComplaintController.java
│   │   │   └── ComplaintResolutionController.java
│   │   ├── marketplace
│   │   │   ├── MarketplaceController.java
│   │   │   └── ListingController.java
│   │   ├── agent
│   │   │   ├── AgentController.java
│   │   │   └── AgentAssignmentController.java
│   │   ├── operations
│   │   │   ├── OperationsController.java
│   │   │   ├── QueueController.java
│   │   │   └── EscalationController.java
│   │   └── admin
│   │       ├── AdminController.java
│   │       ├── UserManagementController.java
│   │       ├── RoleController.java
│   │       └── ConfigController.java
│   ├── dto
│   │   ├── auth
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── RefreshTokenRequest.java
│   │   │   └── AuthResponse.java
│   │   ├── customer
│   │   │   ├── CustomerCreateRequest.java
│   │   │   ├── CustomerUpdateRequest.java
│   │   │   └── CustomerResponse.java
│   │   ├── property
│   │   │   ├── PropertyCreateRequest.java
│   │   │   ├── PropertyUpdateRequest.java
│   │   │   └── PropertyResponse.java
│   │   ├── service
│   │   │   ├── ServiceRequestCreateRequest.java
│   │   │   ├── ServiceRequestUpdateRequest.java
│   │   │   └── ServiceRequestResponse.java
│   │   ├── payment
│   │   │   ├── PaymentRequest.java
│   │   │   ├── InvoiceResponse.java
│   │   │   └── RefundRequest.java
│   │   ├── notification
│   │   │   ├── NotificationRequest.java
│   │   │   └── NotificationResponse.java
│   │   └── common
│   │       ├── ApiResponse.java
│   │       ├── PaginationRequest.java
│   │       └── PagedResponse.java
│   ├── mapper
│   │   ├── CustomerMapper.java
│   │   ├── PropertyMapper.java
│   │   ├── ServiceRequestMapper.java
│   │   ├── PaymentMapper.java
│   │   └── NotificationMapper.java
│   └── validation
│       ├── ValidPhoneNumber.java
│       ├── ValidOtpCode.java
│       ├── ValidPropertyState.java
│       └── GlobalValidator.java
├── application
│   ├── service
│   │   ├── auth
│   │   │   ├── AuthService.java
│   │   │   ├── TokenService.java
│   │   │   └── UserSessionService.java
│   │   ├── customer
│   │   │   ├── CustomerService.java
│   │   │   └── CustomerProfileService.java
│   │   ├── property
│   │   │   ├── PropertyService.java
│   │   │   ├── PropertyOwnershipService.java
│   │   │   └── PropertyVerificationService.java
│   │   ├── service
│   │   │   ├── ServiceCatalogService.java
│   │   │   ├── ServiceRequestService.java
│   │   │   └── ServiceWorkflowService.java
│   │   ├── subscription
│   │   │   ├── SubscriptionService.java
│   │   │   └── BillingCycleService.java
│   │   ├── payment
│   │   │   ├── PaymentService.java
│   │   │   ├── InvoiceService.java
│   │   │   └── RefundService.java
│   │   ├── report
│   │   │   ├── ReportService.java
│   │   │   └── AnalyticsService.java
│   │   ├── notification
│   │   │   ├── NotificationService.java
│   │   │   └── NotificationTemplateService.java
│   │   ├── complaint
│   │   │   ├── ComplaintService.java
│   │   │   └── ComplaintResolutionService.java
│   │   ├── marketplace
│   │   │   ├── MarketplaceService.java
│   │   │   └── ListingService.java
│   │   ├── agent
│   │   │   ├── AgentService.java
│   │   │   └── AgentAssignmentService.java
│   │   ├── operations
│   │   │   ├── OperationsDashboardService.java
│   │   │   ├── QueueService.java
│   │   │   └── EscalationService.java
│   │   └── admin
│   │       ├── AdminUserService.java
│   │       ├── RoleService.java
│   │       ├── ConfigurationService.java
│   │       └── AuditService.java
│   ├── event
│   │   ├── publisher
│   │   │   ├── DomainEventPublisher.java
│   │   │   └── OutboxEventPublisher.java
│   │   ├── consumer
│   │   │   ├── NotificationEventConsumer.java
│   │   │   ├── PaymentEventConsumer.java
│   │   │   └── SubscriptionEventConsumer.java
│   │   └── model
│   │       ├── DomainEvent.java
│   │       ├── NotificationEvent.java
│   │       ├── PaymentEvent.java
│   │       └── SubscriptionEvent.java
│   └── usecase
│       ├── auth
│       │   ├── RegisterCustomerUseCase.java
│       │   └── LoginUseCase.java
│       ├── property
│       │   ├── CreatePropertyUseCase.java
│       │   └── VerifyPropertyOwnershipUseCase.java
│       ├── service
│       │   ├── CreateServiceRequestUseCase.java
│       │   └── AdvanceServiceRequestUseCase.java
│       ├── payment
│       │   ├── ProcessPaymentUseCase.java
│       │   └── CreateInvoiceUseCase.java
│       └── admin
│           ├── CreateUserUseCase.java
│           └── UpdateRoleUseCase.java
├── domain
│   ├── model
│   │   ├── auth
│   │   │   ├── User.java
│   │   │   ├── Role.java
│   │   │   └── Permission.java
│   │   ├── customer
│   │   │   ├── Customer.java
│   │   │   └── CustomerProfile.java
│   │   ├── property
│   │   │   ├── Property.java
│   │   │   ├── PropertyOwnership.java
│   │   │   └── PropertyVerification.java
│   │   ├── service
│   │   │   ├── ServiceCatalogItem.java
│   │   │   ├── ServiceRequest.java
│   │   │   └── ServiceRequestHistory.java
│   │   ├── subscription
│   │   │   ├── Subscription.java
│   │   │   ├── SubscriptionPlan.java
│   │   │   └── BillingCycle.java
│   │   ├── payment
│   │   │   ├── Payment.java
│   │   │   ├── Invoice.java
│   │   │   └── Refund.java
│   │   ├── report
│   │   │   ├── Report.java
│   │   │   └── ReportMetric.java
│   │   ├── notification
│   │   │   ├── Notification.java
│   │   │   └── NotificationTemplate.java
│   │   ├── complaint
│   │   │   ├── Complaint.java
│   │   │   └── ComplaintResolution.java
│   │   ├── marketplace
│   │   │   ├── MarketplaceListing.java
│   │   │   └── MarketplaceOffer.java
│   │   ├── agent
│   │   │   ├── Agent.java
│   │   │   ├── AgentAssignment.java
│   │   │   └── AgentAvailability.java
│   │   ├── operations
│   │   │   ├── OperationsQueue.java
│   │   │   └── EscalationRecord.java
│   │   └── admin
│   │       ├── SystemConfiguration.java
│   │       └── AuditLog.java
│   ├── repository
│   │   ├── auth
│   │   │   ├── UserRepository.java
│   │   │   └── RefreshTokenRepository.java
│   │   ├── customer
│   │   │   ├── CustomerRepository.java
│   │   │   └── CustomerProfileRepository.java
│   │   ├── property
│   │   │   ├── PropertyRepository.java
│   │   │   └── PropertyVerificationRepository.java
│   │   ├── service
│   │   │   ├── ServiceCatalogItemRepository.java
│   │   │   └── ServiceRequestRepository.java
│   │   ├── subscription
│   │   │   ├── SubscriptionRepository.java
│   │   │   └── BillingCycleRepository.java
│   │   ├── payment
│   │   │   ├── PaymentRepository.java
│   │   │   ├── InvoiceRepository.java
│   │   │   └── RefundRepository.java
│   │   ├── report
│   │   │   ├── ReportRepository.java
│   │   │   └── ReportMetricRepository.java
│   │   ├── notification
│   │   │   ├── NotificationRepository.java
│   │   │   └── NotificationTemplateRepository.java
│   │   ├── complaint
│   │   │   ├── ComplaintRepository.java
│   │   │   └── ComplaintResolutionRepository.java
│   │   ├── agent
│   │   │   ├── AgentRepository.java
│   │   │   └── AgentAssignmentRepository.java
│   │   ├── operations
│   │   │   ├── QueueRepository.java
│   │   │   └── EscalationRepository.java
│   │   └── admin
│   │       ├── AuditLogRepository.java
│   │       └── SystemConfigurationRepository.java
│   ├── enums
│   │   ├── UserRole.java
│   │   ├── UserStatus.java
│   │   ├── PropertyStatus.java
│   │   ├── ServiceRequestStatus.java
│   │   ├── PaymentStatus.java
│   │   ├── NotificationType.java
│   │   ├── ComplaintStatus.java
│   │   ├── SubscriptionStatus.java
│   │   └── SystemActionType.java
│   └── state
│       ├── ServiceRequestStateMachine.java
│       ├── PaymentStateMachine.java
│       ├── SubscriptionStateMachine.java
│       ├── ComplaintStateMachine.java
│       └── NotificationStateMachine.java
├── infrastructure
│   ├── persistence
│   │   ├── entity
│   │   │   ├── UserEntity.java
│   │   │   ├── CustomerEntity.java
│   │   │   ├── PropertyEntity.java
│   │   │   ├── ServiceRequestEntity.java
│   │   │   ├── PaymentEntity.java
│   │   │   └── NotificationEntity.java
│   │   ├── jpa
│   │   │   ├── JpaUserRepository.java
│   │   │   └── JpaServiceRequestRepository.java
│   │   └── migration
│   │       ├── FlywayConfig.java
│   │       └── DataMigrationRunner.java
│   ├── security
│   │   ├── SecurityConfig.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   ├── UserPrincipal.java
│   │   ├── AuthorizationService.java
│   │   └── PermissionEvaluator.java
│   ├── cache
│   │   ├── RedisConfig.java
│   │   ├── RedisCacheService.java
│   │   ├── CacheKeys.java
│   │   └── CacheManager.java
│   ├── message
│   │   ├── EventPublisher.java
│   │   ├── OutboxPublisher.java
│   │   ├── WebhookClient.java
│   │   └── MessagingConfig.java
│   ├── external
│   │   ├── PaymentGatewayClient.java
│   │   ├── SmsGatewayClient.java
│   │   ├── EmailGatewayClient.java
│   │   └── PushNotificationClient.java
│   └── filesystem
│       ├── StorageService.java
│       └── FileUploadService.java
├── config
│   ├── OpenApiConfig.java
│   ├── AppProperties.java
│   ├── ClockConfig.java
│   ├── AsyncConfig.java
│   ├── JacksonConfig.java
│   └── RetryConfig.java
├── common
│   ├── constants
│   │   ├── ApiErrorCodes.java
│   │   ├── SecurityConstants.java
│   │   └── CommonConstants.java
│   ├── exception
│   │   ├── ApiException.java
│   │   ├── ValidationException.java
│   │   ├── NotFoundException.java
│   │   ├── UnauthorizedException.java
│   │   └── GlobalExceptionHandler.java
│   ├── util
│   │   ├── DateUtil.java
│   │   ├── PaginationUtil.java
│   │   ├── StringNormalizer.java
│   │   └── UUIDUtil.java
│   ├── audit
│   │   ├── AuditContext.java
│   │   ├── AuditAspect.java
│   │   └── AuditLogWriter.java
│   └── pagination
│       ├── PageRequest.java
│       └── PageResponse.java
├── resources
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-qa.yml
│   ├── application-uat.yml
│   ├── application-prod.yml
│   ├── db
│   │   └── migration
│   │       ├── V1__init_schema.sql
│   │       ├── V2__seed_reference_data.sql
│   │       ├── V3__seed_roles_and_permissions.sql
│   │       └── V4__seed_admin_user.sql
│   ├── messages
│   │   ├── validation.properties
│   │   └── exceptions.properties
│   └── logback-spring.xml
└── test
    ├── java
    │   ├── integration
    │   │   ├── AuthIntegrationTest.java
    │   │   ├── ServiceRequestIntegrationTest.java
    │   │   └── PaymentIntegrationTest.java
    │   ├── unit
    │   │   ├── auth
    │   │   │   └── JwtTokenProviderTest.java
    │   │   ├── service
    │   │   │   └── PropertyServiceTest.java
    │   │   └── validation
    │   │       └── RequestValidatorTest.java
    │   └── security
    │       └── AuthorizationTest.java
    └── resources
        └── application-test.yml
```

---

# Module-by-Module Implementation Plan

## 1. Auth Module

Package:
- com.propertypilot.auth

### Entities
- User
- Role
- Permission
- RefreshToken
- UserSession

### Repositories
- UserRepository
- RoleRepository
- PermissionRepository
- RefreshTokenRepository
- UserSessionRepository

### Services
- AuthService
- UserService
- TokenService
- SessionService
- PasswordPolicyService

### Controllers
- AuthController
- TokenController
- UserController

### DTOs
- LoginRequest
- RegisterRequest
- RefreshTokenRequest
- AuthResponse
- UserResponse

### Validators
- LoginRequestValidator
- PasswordValidator
- OTPValidator
- RoleAssignmentValidator

### Mappers
- UserMapper
- SessionMapper

### Events
- UserRegisteredEvent
- LoginSucceededEvent
- SessionInvalidatedEvent
- PasswordChangedEvent

### State Machines
- UserStatusStateMachine
- SessionStatusStateMachine

### Security Requirements
- JWT access tokens
- refresh tokens stored securely
- password hashing with bcrypt or PBKDF2
- MFA/OTP support for sensitive actions
- session invalidation on logout and suspicious activity
- role-based access control

---

## 2. Customer Module

Package:
- com.propertypilot.customer

### Entities
- Customer
- CustomerProfile
- CustomerPreference
- CustomerAddress
- CustomerVerification

### Repositories
- CustomerRepository
- CustomerProfileRepository
- CustomerPreferenceRepository

### Services
- CustomerService
- CustomerProfileService
- CustomerVerificationService
- CustomerPreferenceService

### Controllers
- CustomerController
- CustomerProfileController
- CustomerPreferenceController

### DTOs
- CustomerCreateRequest
- CustomerUpdateRequest
- CustomerResponse
- CustomerProfileResponse

### Validators
- CustomerRegistrationValidator
- CustomerProfileValidator
- ConsentValidator

### Mappers
- CustomerMapper
- CustomerProfileMapper

### Events
- CustomerCreatedEvent
- CustomerUpdatedEvent
- CustomerVerificationRequiredEvent

### State Machines
- CustomerStatusStateMachine

### Security Requirements
- customers can access only own data
- customer admin access limited to managed roles
- PII masked in logs and error responses

---

## 3. Property Module

Package:
- com.propertypilot.property

### Entities
- Property
- PropertyAddress
- PropertyVerification
- PropertyOwnership
- PropertyDocument
- PropertyMedia

### Repositories
- PropertyRepository
- PropertyAddressRepository
- PropertyVerificationRepository
- PropertyOwnershipRepository

### Services
- PropertyService
- PropertyVerificationService
- PropertyOwnershipService
- PropertyMediaService

### Controllers
- PropertyController
- PropertyVerificationController
- PropertyOwnershipController

### DTOs
- PropertyCreateRequest
- PropertyUpdateRequest
- PropertyResponse
- PropertyVerificationRequest

### Validators
- PropertyInputValidator
- OwnershipValidator
- GPSCoordinateValidator
- DocumentTypeValidator

### Mappers
- PropertyMapper
- PropertyOwnershipMapper

### Events
- PropertyCreatedEvent
- PropertyVerifiedEvent
- PropertyRejectedEvent

### State Machines
- PropertyStatusStateMachine
- OwnershipVerificationStateMachine

### Security Requirements
- customer can manage own property records
- admin and operations may view verification information
- ownership verification auditable

---

## 4. Service Module

Package:
- com.propertypilot.service

### Entities
- ServiceCatalogItem
- ServiceRequest
- ServiceRequestHistory
- ServiceRequirement
- ServiceEvidence
- ServiceAssignment

### Repositories
- ServiceCatalogItemRepository
- ServiceRequestRepository
- ServiceRequestHistoryRepository
- ServiceEvidenceRepository
- ServiceAssignmentRepository

### Services
- ServiceCatalogService
- ServiceRequestService
- ServiceWorkflowService
- ServiceEvidenceService
- ServiceAssignmentService

### Controllers
- ServiceCatalogController
- ServiceRequestController
- ServiceStatusController
- ServiceEvidenceController

### DTOs
- ServiceCatalogResponse
- ServiceRequestCreateRequest
- ServiceRequestUpdateRequest
- ServiceRequestResponse
- ServiceStatusUpdateRequest

### Validators
- ServiceRequestValidator
- ServiceDateValidator
- ServiceStatusTransitionValidator
- EvidenceValidator

### Mappers
- ServiceCatalogMapper
- ServiceRequestMapper
- ServiceEvidenceMapper

### Events
- ServiceRequestedEvent
- ServiceAssignedEvent
- ServiceStatusChangedEvent
- ServiceCompletedEvent

### State Machines
- ServiceRequestStateMachine
- ServiceEvidenceStateMachine

### Security Requirements
- customer can create and view own service requests
- agent can only act on assigned tasks
- operations can override or escalate where policy allows

---

## 5. Subscription Module

Package:
- com.propertypilot.subscription

### Entities
- Subscription
- SubscriptionPlan
- BillingCycle
- RenewalSchedule
- PlanBenefit

### Repositories
- SubscriptionRepository
- SubscriptionPlanRepository
- BillingCycleRepository

### Services
- SubscriptionService
- SubscriptionPlanService
- RenewalService
- BillingLifecycleService

### Controllers
- SubscriptionController
- SubscriptionPlanController
- BillingController

### DTOs
- SubscriptionResponse
- SubscriptionCreateRequest
- SubscriptionPlanResponse
- BillingCycleResponse

### Validators
- SubscriptionValidator
- PlanSelectionValidator
- RenewalEligibilityValidator

### Mappers
- SubscriptionMapper
- SubscriptionPlanMapper

### Events
- SubscriptionCreatedEvent
- SubscriptionActivatedEvent
- SubscriptionCancelledEvent
- SubscriptionExpiredEvent

### State Machines
- SubscriptionStateMachine
- BillingCycleStateMachine

### Security Requirements
- only customer or admin can modify subscription where permitted
- sensitive billing transitions logged and auditable
- no bypass of plan validation

---

## 6. Payment Module

Package:
- com.propertypilot.payment

### Entities
- Payment
- Invoice
- Refund
- PaymentProviderTransaction
- PaymentAttempt

### Repositories
- PaymentRepository
- InvoiceRepository
- RefundRepository
- PaymentAttemptRepository

### Services
- PaymentService
- InvoiceService
- RefundService
- PaymentProviderAdapter
- PaymentLedgerService

### Controllers
- PaymentController
- InvoiceController
- RefundController

### DTOs
- PaymentRequest
- PaymentResponse
- RefundRequest
- InvoiceResponse

### Validators
- PaymentValidator
- RefundValidator
- AmountValidator
- CurrencyValidator

### Mappers
- PaymentMapper
- InvoiceMapper

### Events
- PaymentInitiatedEvent
- PaymentSucceededEvent
- PaymentFailedEvent
- PaymentRefundedEvent

### State Machines
- PaymentStateMachine
- InvoiceStateMachine
- RefundStateMachine

### Security Requirements
- PCI-safe handling of payment data
- gateway integration through provider abstraction
- idempotency keys for retries
- all payment events logged and auditable

---

## 7. Report Module

Package:
- com.propertypilot.report

### Entities
- Report
- ReportDefinition
- ReportMetric
- ReportJob
- ReportExport

### Repositories
- ReportRepository
- ReportMetricRepository
- ReportJobRepository
- ReportExportRepository

### Services
- ReportService
- AnalyticsService
- ReportExportService
- MetricAggregationService

### Controllers
- ReportController
- AnalyticsController

### DTOs
- ReportRequest
- ReportResponse
- ReportExportResponse

### Validators
- ReportQueryValidator
- DateRangeValidator
- ExportFormatValidator

### Mappers
- ReportMapper
- MetricMapper

### Events
- ReportGeneratedEvent
- ReportExportRequestedEvent
- ReportExportCompletedEvent

### State Machines
- ReportJobStateMachine
- ReportExportStateMachine

### Security Requirements
- role-based access to operational and admin dashboards
- export authorization with audit logging

---

## 8. Notification Module

Package:
- com.propertypilot.notification

### Entities
- Notification
- NotificationPreference
- NotificationTemplate
- NotificationChannel
- NotificationDelivery

### Repositories
- NotificationRepository
- NotificationTemplateRepository
- NotificationPreferenceRepository

### Services
- NotificationService
- NotificationTemplateService
- NotificationChannelService
- NotificationDeliveryService

### Controllers
- NotificationController
- NotificationPreferenceController

### DTOs
- NotificationRequest
- NotificationResponse
- NotificationPreferenceRequest

### Validators
- NotificationMessageValidator
- ChannelValidator
- PreferenceValidator

### Mappers
- NotificationMapper
- PreferenceMapper

### Events
- NotificationCreatedEvent
- NotificationSentEvent
- NotificationFailedEvent

### State Machines
- NotificationStateMachine
- NotificationDeliveryStateMachine

### Security Requirements
- no sensitive data in notification payloads
- user preference enforcement
- audit logging for message sends and failures

---

## 9. Complaint Module

Package:
- com.propertypilot.complaint

### Entities
- Complaint
- ComplaintThread
- ComplaintResolution
- ComplaintEscalation

### Repositories
- ComplaintRepository
- ComplaintThreadRepository
- ComplaintResolutionRepository

### Services
- ComplaintService
- ComplaintResolutionService
- ComplaintEscalationService

### Controllers
- ComplaintController
- ComplaintResolutionController

### DTOs
- ComplaintCreateRequest
- ComplaintUpdateRequest
- ComplaintResponse

### Validators
- ComplaintValidator
- EscalationValidator

### Mappers
- ComplaintMapper
- ComplaintResolutionMapper

### Events
- ComplaintCreatedEvent
- ComplaintUpdatedEvent
- ComplaintResolvedEvent
- ComplaintEscalatedEvent

### State Machines
- ComplaintStateMachine

### Security Requirements
- complaint visibility restricted by subject and role
- audit logging for actions and resolution

---

## 10. Marketplace Module

Package:
- com.propertypilot.marketplace

### Entities
- MarketplaceListing
- ListingCategory
- MarketplaceOffer
- ListingVisibility

### Repositories
- MarketplaceListingRepository
- MarketplaceOfferRepository

### Services
- MarketplaceService
- ListingService
- OfferService

### Controllers
- MarketplaceController
- ListingController

### DTOs
- ListingCreateRequest
- ListingResponse
- OfferRequest

### Validators
- ListingValidator
- OfferValidator

### Mappers
- MarketplaceMapper

### Events
- ListingCreatedEvent
- ListingUpdatedEvent
- OfferCreatedEvent

### State Machines
- ListingStateMachine

### Security Requirements
- listings visible based on role and access
- admin moderation required if marketplace is used in MVP beyond minimal exposure

---

## 11. Agent Module

Package:
- com.propertypilot.agent

### Entities
- Agent
- AgentAssignment
- AgentAvailability
- AgentPerformance

### Repositories
- AgentRepository
- AgentAssignmentRepository
- AgentAvailabilityRepository

### Services
- AgentService
- AgentAssignmentService
- AgentAvailabilityService

### Controllers
- AgentController
- AgentAssignmentController

### DTOs
- AgentResponse
- AgentAssignmentRequest
- AgentAssignmentResponse

### Validators
- AgentValidator
- AssignmentValidator
- AvailabilityValidator

### Mappers
- AgentMapper
- AgentAssignmentMapper

### Events
- AgentAssignedEvent
- AgentTaskStartedEvent
- AgentTaskCompletedEvent

### State Machines
- AgentAssignmentStateMachine

### Security Requirements
- agent access to assigned tasks only
- role restrictions for updates and assignments
- verification of task ownership before status change

---

## 12. Operations Module

Package:
- com.propertypilot.operations

### Entities
- OperationsQueue
- QueueItem
- EscalationRecord
- OperationalAlert

### Repositories
- QueueRepository
- EscalationRepository
- OperationalAlertRepository

### Services
- QueueService
- OperationsService
- EscalationService
- AlertService

### Controllers
- OperationsController
- QueueController
- EscalationController

### DTOs
- QueueItemResponse
- EscalationRequest
- OperationsDashboardResponse

### Validators
- QueueActionValidator
- EscalationValidator

### Mappers
- QueueMapper
- EscalationMapper

### Events
- QueueItemCreatedEvent
- EscalationRaisedEvent
- EscalationResolvedEvent

### State Machines
- OperationsQueueStateMachine
- EscalationStateMachine

### Security Requirements
- ops users can view only allowed queue and service activity
- escalation actions logged
- read-only access for low-permission roles

---

## 13. Admin Module

Package:
- com.propertypilot.admin

### Entities
- SystemConfiguration
- AdminUser
- AuditLog
- Role
- Permission
- FeatureFlag

### Repositories
- AuditLogRepository
- SystemConfigurationRepository
- FeatureFlagRepository

### Services
- AdminUserService
- RoleService
- ConfigurationService
- AuditService
- FeatureFlagService

### Controllers
- AdminController
- UserManagementController
- RoleController
- ConfigController

### DTOs
- AdminUserResponse
- RoleAssignmentRequest
- SystemConfigurationRequest
- AuditLogResponse

### Validators
- RoleAssignmentValidator
- ConfigurationValidator
- FeatureFlagValidator

### Mappers
- AdminUserMapper
- AuditMapper

### Events
- AdminActionLoggedEvent
- RoleUpdatedEvent
- ConfigurationChangedEvent

### State Machines
- FeatureFlagStateMachine
- SystemConfigurationStateMachine

### Security Requirements
- admin-level authorization only
- all admin actions logged
- config changes must include approval path if required
- emergency access controlled and auditable

---

## 14. Common Module

Package:
- com.propertypilot.common

### Purpose
- shared code, constants, policies, common DTOs, cross-cutting concerns

### Contents
- ApiResponse
- PagedResponse
- ApiErrorCodes
- DomainEvent
- Auditing
- GlobalExceptionHandler
- Validation utilities
- Date utilities
- pagination utilities
- security constants
- generic repository patterns

---

# Database Migration Roadmap

PropertyPilot should use Flyway for schema management. Every domain module should have a versioned migration associated with it.

## Migration Strategy

- Each migration is versioned and immutable
- Database changes must be backward compatible where possible
- Failed migration execution blocks the application bootstrap
- All application startup must validate migration status
- Use incremental SQL migration files with explicit naming conventions

## Recommended Naming Convention

```text
V<major>__<short_description>.sql
```

Examples:
- V1__create_auth_schema.sql
- V2__create_customer_schema.sql
- V3__create_property_schema.sql
- V4__create_service_schema.sql
- V5__create_subscription_schema.sql
- V6__create_payment_schema.sql
- V7__create_notification_schema.sql
- V8__create_complaint_schema.sql
- V9__create_agent_schema.sql
- V10__create_operations_schema.sql
- V11__create_admin_schema.sql
- V12__seed_reference_data.sql
- V13__seed_roles_and_permissions.sql
- V14__insert_admin_users.sql
- V15__create_outbox_tables.sql

## Reference Migration Mapping

### Auth
- users
- roles
- permissions
- refresh_tokens
- user_sessions

### Customer
- customers
- customer_profiles
- customer_preferences
- customer_addresses

### Property
- properties
- property_addresses
- property_verification
- property_documents
- property_media

### Service
- service_catalog
- service_requests
- service_history
- service_evidence
- assignments

### Subscription and Billing
- subscriptions
- plans
- billing_cycles
- renewal_records

### Payment
- payments
- invoices
- refunds
- payment_attempts

### Notification
- notifications
- notification_templates
- notification_preferences
- delivery_records

### Complaint
- complaints
- complaint_threads
- complaint_resolutions

### Agent
- agents
- agent_assignments
- agent_availability

### Operations
- queue_items
- escalation_records
- operational_alerts

### Admin
- system_configuration
- audit_logs
- feature_flags

### Outbox & Eventing
- outbox_events
- webhook_events
- async_job_queue

## Migration Validation
- Validate migrations in test and UAT before production
- Run dry-run or schema diff in CI
- Verify rollback path for each major migration set
- Ensure migration scripts are idempotent where necessary
- Ensure database constraints match the physical model

---

# API Implementation Roadmap

API implementation must align with the OpenAPI contract and domain model. Each controller should map to one or more domain-specific operations.

## API Group Mapping

### Auth
- POST /api/v1/auth/register
- POST /api/v1/auth/login
- POST /api/v1/auth/refresh
- POST /api/v1/auth/logout
- GET /api/v1/auth/me

### Customer
- GET /api/v1/customers/{id}
- PUT /api/v1/customers/{id}
- GET /api/v1/customers/{id}/properties
- POST /api/v1/customers/{id}/preferences

### Property
- GET /api/v1/properties
- POST /api/v1/properties
- GET /api/v1/properties/{id}
- PUT /api/v1/properties/{id}
- POST /api/v1/properties/{id}/verify

### Service
- GET /api/v1/services
- GET /api/v1/services/{id}
- POST /api/v1/service-requests
- GET /api/v1/service-requests/{id}
- PATCH /api/v1/service-requests/{id}/status
- GET /api/v1/service-requests/{id}/history

### Subscription
- GET /api/v1/subscriptions
- POST /api/v1/subscriptions
- PATCH /api/v1/subscriptions/{id}
- DELETE /api/v1/subscriptions/{id}

### Payment
- POST /api/v1/payments
- GET /api/v1/payments/{id}
- POST /api/v1/payments/{id}/refund
- GET /api/v1/invoices/{id}

### Report
- GET /api/v1/reports
- GET /api/v1/reports/{id}
- POST /api/v1/reports/export

### Notification
- GET /api/v1/notifications
- PATCH /api/v1/notifications/{id}/read
- PUT /api/v1/notifications/preferences

### Complaint
- POST /api/v1/complaints
- GET /api/v1/complaints/{id}
- PATCH /api/v1/complaints/{id}/status

### Agent
- GET /api/v1/agents/{id}/assignments
- PATCH /api/v1/assignments/{id}/status
- POST /api/v1/assignments/{id}/complete

### Operations
- GET /api/v1/operations/queue
- POST /api/v1/operations/assignments
- POST /api/v1/operations/escalations

### Admin
- GET /api/v1/admin/users
- POST /api/v1/admin/roles
- PUT /api/v1/admin/configurations/{id}
- GET /api/v1/admin/audit-logs

## API Standards
- versioning via /api/v1
- consistent error response model
- pagination and sorting support
- strict validation for request DTOs
- standardized HTTP status mapping
- clear distinction between 4xx and 5xx errors
- support for idempotency keys on payment and mutation endpoints

---

# Authentication & Authorization Design

## JWT Design

Use JWT access tokens and refresh tokens with standard claims.

Claims:
- sub: user id
- role: primary role
- permissions: list of granted permissions
- aud: audience
- iss: issuer
- exp: expiry
- iat: issued at

## Token Strategy
- access token short-lived, e.g., 15 minutes
- refresh token long-lived, e.g., 7 to 30 days
- rotate refresh tokens on use
- revoke tokens on logout and suspicious activity
- store refresh token metadata in database and Redis for validation

## Roles
- CUSTOMER
- AGENT
- OPERATIONS
- ADMIN
- SYSTEM

## Permissions
Organize permissions by domain:
- customer:read_own_profile, customer:update_own_profile, property:create, property:read_own
- agent:read_assignment, assignment:update_own, property:verify
- operations:queue:view, queue:assign, escalation:create
- admin:user:manage, role:manage, config:update, audit:read

## RBAC Design
- assign roles to users
- map roles to permissions
- enforce permission checks at controller/service level
- use method security annotations such as:
  - @PreAuthorize
  - @Secured
- enforce role granularity and domain ownership checks

## Security Filters
- JWT authentication filter
- exception handling filter
- rate limiting filter
- request logging filter
- CSRF disabled for stateless API
- secure cookie strategy only if applicable to web UI

---

# Event-Driven Components

## Outbox Pattern

Use outbox pattern for reliable event publishing:
- events written to outbox table in same transaction as business data
- outbox relay publishes to brokers or external systems asynchronously
- ensures no lost events during transaction failures

### Outbox Tables
- outbox_events
- webhook_events
- async_job_queue

## Event Types

### Notification Events
- UserRegisteredEvent
- PropertyCreatedEvent
- ServiceRequestedEvent
- ServiceCompletedEvent
- PaymentSucceededEvent
- ComplaintCreatedEvent

### Subscription Events
- SubscriptionCreatedEvent
- SubscriptionCancelledEvent
- RenewalTriggeredEvent
- ExpiredSubscriptionEvent

### Payment Events
- PaymentInitiatedEvent
- PaymentSucceededEvent
- PaymentFailedEvent
- RefundRequestedEvent
- RefundProcessedEvent

### Webhook Processing
- webhook events created from payment or external integration notifications
- signature validation
- retries with exponential backoff
- dead-letter handling for permanent failures

### Event Consumers
- notification dispatcher
- payment outcome processor
- subscription state updater
- report generator
- audit writer

---

# Caching Strategy

## Redis Usage
Redis should be used for:
- JWT blacklist / token invalidation metadata
- user session lookups
- short-lived API response cache
- service catalog caching
- hot report and dashboard metrics
- lock / deduplication keys

## Cache Keys
Use namespaced keys such as:
- user:profile:{userId}
- property:details:{propertyId}
- service:catalog:{categoryId}
- queue:operations:{date}
- report:summary:{userId}:{period}

## TTL Strategy
- user profile: 5-15 min
- service catalog: 15-60 min
- queue summaries: 1-5 min
- report metrics: 5-15 min
- token invalidation metadata: short TTL aligned to token expiry
- hot API responses: short TTL with cache-aside pattern

## Cache Invalidation
- update cache on create/update/delete events
- invalidate on domain mutation
- avoid stale data on payment and subscription transitions

---

# Error Handling Strategy

## Global Exception Handling

Use centralized exception handling with:
- ApiException base class
- validation exceptions
- not found exceptions
- unauthorized exceptions
- business rule exceptions
- internal server errors masked appropriately

## Error Codes
Define structured codes such as:
- AUTH_001 invalid credentials
- AUTH_002 token expired
- AUTH_003 refresh token invalid
- USER_001 user not found
- PROPERTY_001 property not found
- SERVICE_001 invalid status transition
- PAYMENT_001 payment failed
- SUBSCRIPTION_002 plan invalid
- COMPLAINT_001 complaint not found

## Validation Errors
- standard field-level validation messages
- consistent names and required constraints
- validation exceptions for required fields and domain rules
- request validation in DTO annotations and custom validators

## Error Response Structure
```json
{
  "code": "SERVICE_001",
  "message": "Invalid status transition for service request",
  "details": [
    "Current status: IN_PROGRESS",
    "Requested status: CANCELLED"
  ],
  "timestamp": "2026-08-31T12:00:00Z"
}
```

---

# Audit Strategy

## Audit Logs
Capture:
- user login and logout
- role assignments
- admin config changes
- payment actions
- subscription lifecycle updates
- complaint resolution changes
- service status changes
- ownership verification events
- sensitive data access attempts

## Change Tracking
- store before and after values for admin changes
- log actor id, entity type, action, timestamp, and reason
- include correlation id for request tracing

## User Activity
- record user activity at principal/service boundaries
- include endpoint metadata, request id, and actor context
- separate audit trail from app logs

## Audit Storage
- store structured audit entries in PostgreSQL
- include retention policies and access restrictions
- restrict audit read access to authorized roles

---

# Observability

## Logging
- structured JSON logging
- correlation IDs in each request
- log levels based on environment
- log sensitive operations with controlled access
- avoid logging raw tokens, payment data, or PII

## Metrics
Track:
- API latency
- request rate
- error rate
- DB query performance
- Redis hit/miss rate
- queue backlog
- payment processing time
- worker processing duration

## Tracing
- enable distributed tracing across controller, service, DB, Redis, and external integrations
- trace id propagation across async tasks
- include service version and environment in trace metadata

## Health Checks
- readiness endpoint
- liveness endpoint
- DB connectivity health
- Redis health
- external service health
- queue health

## Monitoring & Alerting
- error spike detection
- queue backlog alerts
- latency threshold alerts
- payment failure alerts
- login and token failure alerts
- DB resource saturation alerts

---

# CI/CD Requirements

## Build
- Maven build with Java 21
- dependency vulnerability checks
- static analysis and formatting validation
- unit test execution as build gate
- compile and package checks

## Test
- unit test suite
- integration test suite
- API contract validation
- security scans
- database migration validation
- regression suite pre-merge

## Deploy
- environment-specific deployment pipelines
- immutable build artifact promotion
- deployment approvals for QA, UAT, and production
- blue/green or rolling deployments for production
- health verification gate after deployment

## Rollback
- provide rollback to previous stable artifact
- rollback plan for DB schema and application version
- validate rollback path in CI/CD and cutover readiness
- maintain artifact retention and deployment metadata

---

# Security Controls

## OWASP Alignment
- protect against injection attacks
- validate input and payload shapes
- prevent broken access control
- sanitize outputs and logs
- protect against SSRF if external integrations are used
- enforce secure session and token management
- protect against mass assignment and insecure deserialization

## Input Validation
- Bean Validation for DTOs
- custom validators for business rules
- IP validation, phone validation, OTP validation, date transition validation
- reject invalid enums and malformed payloads

## Rate Limiting
- login rate limiting
- OTP resend rate limiting
- payment retry protection
- public API abuse protection
- operation-specific quotas for report generation

## Secrets Management
- externalize all secrets
- use environment variables or secret manager
- rotate tokens, certs, DB credentials regularly
- do not store secrets in source control

---

# Sprint-wise Backend Delivery Plan

## Sprint 1
Focus:
- foundation setup
- project skeleton
- security setup
- DB and migration baseline
- auth domain
- base common modules

Deliverables:
- Spring Boot project initialized
- PostgreSQL and Redis configured
- Flyway migration baseline
- JWT security and RBAC skeleton
- basic health, logging, and config
- user and role domain model

---

## Sprint 2
Focus:
- customer and property domain
- service catalog and service-request workflows
- controller and service scaffolding
- validation framework

Deliverables:
- customer onboarding APIs
- property create/read/update APIs
- service catalog and booking flow
- request state machine implementation
- migration and repository layer for core entities

---

## Sprint 3
Focus:
- agent workflow and operations queue
- payments and subscriptions
- notification foundations
- event publishing and outbox

Deliverables:
- assignment management APIs
- service workflow progression
- payment and invoice flows
- subscription lifecycle APIs
- notification templates and dispatch logic
- outbox integration

---

## Sprint 4
Focus:
- complaints, reports, and admin controls
- audit logging
- performance and security validation
- report generation APIs

Deliverables:
- complaint lifecycle APIs
- reporting and analytics APIs
- admin user and config management
- audit log implementation
- security scanning and hardening

---

## Sprint 5
Focus:
- stabilization, performance tuning, UAT support
- production readiness and deployment validation

Deliverables:
- end-to-end API validation
- regression suite
- production deployment pipeline operationalization
- rollback script validation
- observability tuning and alert readiness
- UAT support and defect closure

---

# Effort Estimates

## Developer Count
Recommended team shape:
- 1 backend architect
- 3 to 5 backend engineers
- 1 QA automation engineer
- 1 DevOps/Platform engineer
- 1 security reviewer or security-minded engineer
- optional 1 integration engineer for payment/notifications/external services

## Timeline
- Foundation and core modules: 4 to 6 weeks
- End-to-end domain implementation: 6 to 10 weeks
- Hardening, security, performance, and PROD readiness: 3 to 6 weeks

Total realistic MVP backend implementation window:
- 10 to 16 weeks depending on delivery maturity and team size

## Dependencies
- UI and screen finalized
- API contract agreement finalized
- DB design approved
- payment gateway integration spec available
- notification provider integration spec available
- security model approved by product and architecture
- environment provisioning completed

## Risks
- unclear ownership of integration contracts
- late changes in business rules
- insufficient validation of payment and notification flows
- DB migration complexity during production deployment
- excessive scope creep beyond MVP
- missing test automation for critical flows
- weak operational ownership for production readiness

---

# Implementation Recommendation

The backend is best implemented as:
- a modular Spring Boot 3 monolith with clearly separated packages and bounded contexts
- PostgreSQL-backed transactional domain model
- Redis-adjacent cache and short-lived session metadata
- Flyway-managed migrations
- event-driven integration using outbox and asynchronous workers
- security-first design with JWT and RBAC
- observability-first instrumentation from day one

This approach gives PropertyPilot a strong foundation for MVP delivery while also enabling later decomposition if product scale or team scaling requires service isolation.

---

# Exit Criteria for Backend Foundation Completion

The backend foundation is ready for MVP delivery when:
- all required modules are implemented and tested
- OpenAPI contract aligns with actual controllers
- Flyway migration pipeline is stable and validated
- auth and RBAC are tested
- payment, subscription, property, and service workflows pass end-to-end validation
- Redis and PostgreSQL are operational in all environments
- outbox-based async processing is validated
- monitoring, logs, and health checks are active
- production deployment and rollback are validated
- security scans and dependency checks are passing
- operations and admin workflows are validated in UAT

---

# Final Summary

This backend foundation plan provides a production-grade blueprint for building the PropertyPilot platform using Java 21, Spring Boot 3, PostgreSQL, Redis, JWT, Flyway, Maven, and Docker. It aligns the implementation with the product vision, the canonical data model, and the API contract while establishing robust security, auditing, observability, and deployment practices.

By structuring the backend around clear domain modules, enforcing a migration-driven database model, and designing for operational maturity from the start, the team can deliver a secure, testable, and scalable MVP foundation.
```// filepath: c:\PropertyPilot\docs\Backend_Foundation_Generation_Plan.md
# Backend Foundation Generation Plan

Document Type: Backend Engineering Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Backend Architecture / Platform Engineering

---

# Purpose

This document defines the backend foundation for PropertyPilot using Java 21 and Spring Boot 3. It translates the product requirements, canonical data model, API specification, and domain workflows into an implementation-ready backend architecture.

The plan establishes:
- the service architecture
- major domain modules
- persistence and migration strategy
- API implementation alignment
- authentication and authorization model
- event-driven integration and outbox design
- caching strategy
- security hardening
- observability model
- sprint-based delivery plan
- implementation effort estimates and risk assessment

This blueprint is intended to be the source of truth for backend implementation, contract alignment, and delivery sequencing.

---

# Architecture Overview

## Target Architecture

PropertyPilot backend will be implemented as a modular Spring Boot 3 monolith with clear domain boundaries and future readiness for decomposition into services if scaling demands require it.

Core components:
- Spring Boot 3 application runtime
- Java 21
- Spring Security with JWT
- PostgreSQL as primary transactional database
- Redis for distributed cache and short-lived session state
- Flyway for schema versioning and migrations
- Maven for build and dependency management
- Docker for local and CI/CD environment consistency
- OpenAPI-aligned REST controllers
- asynchronous processing via eventing and background jobs
- centralized validation and exception handling
- audit logging and observability instrumentation

## Architectural Principles

- Domain-first modularity
- Explicit API contract alignment with OpenAPI
- Security and least privilege by default
- Database integrity as a first-class concern
- Event-driven integration where asynchronous workflows require it
- Clear separation between web layer, application services, domain model, persistence, and infrastructure
- CI-enforced quality gates for testing and security
- Observability built into all critical services

## High-Level Layers

1. API Layer
   - controllers
   - request/response DTOs
   - validation
   - security filters

2. Application Layer
   - use cases
   - orchestration
   - business logic
   - domain services
   - transaction boundaries

3. Domain Layer
   - entities
   - value objects
   - domain enums
   - state definitions
   - core rules

4. Persistence Layer
   - repositories
   - JPA entities
   - Flyway migrations
   - database access

5. Infrastructure Layer
   - security configuration
   - JWT provider
   - Redis config
   - event publishers
   - file storage integration
   - external integration clients

---

# Recommended Package Structure

```text
com.propertypilot
├── PropertyPilotApplication.java
├── api
│   ├── controller
│   │   ├── auth
│   │   │   ├── AuthController.java
│   │   │   └── TokenController.java
│   │   ├── customer
│   │   │   ├── CustomerController.java
│   │   │   └── CustomerProfileController.java
│   │   ├── property
│   │   │   ├── PropertyController.java
│   │   │   ├── PropertyVerificationController.java
│   │   │   └── PropertyOwnershipController.java
│   │   ├── service
│   │   │   ├── ServiceCatalogController.java
│   │   │   ├── ServiceRequestController.java
│   │   │   └── ServiceStatusController.java
│   │   ├── subscription
│   │   │   ├── SubscriptionController.java
│   │   │   └── BillingController.java
│   │   ├── payment
│   │   │   ├── PaymentController.java
│   │   │   ├── InvoiceController.java
│   │   │   └── RefundController.java
│   │   ├── report
│   │   │   ├── ReportController.java
│   │   │   └── AnalyticsController.java
│   │   ├── notification
│   │   │   ├── NotificationController.java
│   │   │   └── NotificationPreferenceController.java
│   │   ├── complaint
│   │   │   ├── ComplaintController.java
│   │   │   └── ComplaintResolutionController.java
│   │   ├── marketplace
│   │   │   ├── MarketplaceController.java
│   │   │   └── ListingController.java
│   │   ├── agent
│   │   │   ├── AgentController.java
│   │   │   └── AgentAssignmentController.java
│   │   ├── operations
│   │   │   ├── OperationsController.java
│   │   │   ├── QueueController.java
│   │   │   └── EscalationController.java
│   │   └── admin
│   │       ├── AdminController.java
│   │       ├── UserManagementController.java
│   │       ├── RoleController.java
│   │       └── ConfigController.java
│   ├── dto
│   │   ├── auth
│   │   │   ├── LoginRequest.java
│   │   │   ├── RegisterRequest.java
│   │   │   ├── RefreshTokenRequest.java
│   │   │   └── AuthResponse.java
│   │   ├── customer
│   │   │   ├── CustomerCreateRequest.java
│   │   │   ├── CustomerUpdateRequest.java
│   │   │   └── CustomerResponse.java
│   │   ├── property
│   │   │   ├── PropertyCreateRequest.java
│   │   │   ├── PropertyUpdateRequest.java
│   │   │   └── PropertyResponse.java
│   │   ├── service
│   │   │   ├── ServiceRequestCreateRequest.java
│   │   │   ├── ServiceRequestUpdateRequest.java
│   │   │   └── ServiceRequestResponse.java
│   │   ├── payment
│   │   │   ├── PaymentRequest.java
│   │   │   ├── InvoiceResponse.java
│   │   │   └── RefundRequest.java
│   │   ├── notification
│   │   │   ├── NotificationRequest.java
│   │   │   └── NotificationResponse.java
│   │   └── common
│   │       ├── ApiResponse.java
│   │       ├── PaginationRequest.java
│   │       └── PagedResponse.java
│   ├── mapper
│   │   ├── CustomerMapper.java
│   │   ├── PropertyMapper.java
│   │   ├── ServiceRequestMapper.java
│   │   ├── PaymentMapper.java
│   │   └── NotificationMapper.java
│   └── validation
│       ├── ValidPhoneNumber.java
│       ├── ValidOtpCode.java
│       ├── ValidPropertyState.java
│       └── GlobalValidator.java
├── application
│   ├── service
│   │   ├── auth
│   │   │   ├── AuthService.java
│   │   │   ├── TokenService.java
│   │   │   └── UserSessionService.java
│   │   ├── customer
│   │   │   ├── CustomerService.java
│   │   │   └── CustomerProfileService.java
│   │   ├── property
│   │   │   ├── PropertyService.java
│   │   │   ├── PropertyOwnershipService.java
│   │   │   └── PropertyVerificationService.java
│   │   ├── service
│   │   │   ├── ServiceCatalogService.java
│   │   │   ├── ServiceRequestService.java
│   │   │   └── ServiceWorkflowService.java
│   │   ├── subscription
│   │   │   ├── SubscriptionService.java
│   │   │   └── BillingCycleService.java
│   │   ├── payment
│   │   │   ├── PaymentService.java
│   │   │   ├── InvoiceService.java
│   │   │   └── RefundService.java
│   │   ├── report
│   │   │   ├── ReportService.java
│   │   │   └── AnalyticsService.java
│   │   ├── notification
│   │   │   ├── NotificationService.java
│   │   │   └── NotificationTemplateService.java
│   │   ├── complaint
│   │   │   ├── ComplaintService.java
│   │   │   └── ComplaintResolutionService.java
│   │   ├── marketplace
│   │   │   ├── MarketplaceService.java
│   │   │   └── ListingService.java
│   │   ├── agent
│   │   │   ├── AgentService.java
│   │   │   └── AgentAssignmentService.java
│   │   ├── operations
│   │   │   ├── OperationsDashboardService.java
│   │   │   ├── QueueService.java
│   │   │   └── EscalationService.java
│   │   └── admin
│   │       ├── AdminUserService.java
│   │       ├── RoleService.java
│   │       ├── ConfigurationService.java
│   │       └── AuditService.java
│   ├── event
│   │   ├── publisher
│   │   │   ├── DomainEventPublisher.java
│   │   │   └── OutboxEventPublisher.java
│   │   ├── consumer
│   │   │   ├── NotificationEventConsumer.java
│   │   │   ├── PaymentEventConsumer.java
│   │   │   └── SubscriptionEventConsumer.java
│   │   └── model
│   │       ├── DomainEvent.java
│   │       ├── NotificationEvent.java
│   │       ├── PaymentEvent.java
│   │       └── SubscriptionEvent.java
│   └── usecase
│       ├── auth
│       │   ├── RegisterCustomerUseCase.java
│       │   └── LoginUseCase.java
│       ├── property
│       │   ├── CreatePropertyUseCase.java
│       │   └── VerifyPropertyOwnershipUseCase.java
│       ├── service
│       │   ├── CreateServiceRequestUseCase.java
│       │   └── AdvanceServiceRequestUseCase.java
│       ├── payment
│       │   ├── ProcessPaymentUseCase.java
│       │   └── CreateInvoiceUseCase.java
│       └── admin
│           ├── CreateUserUseCase.java
│           └── UpdateRoleUseCase.java
├── domain
│   ├── model
│   │   ├── auth
│   │   │   ├── User.java
│   │   │   ├── Role.java
│   │   │   └── Permission.java
│   │   ├── customer
│   │   │   ├── Customer.java
│   │   │   └── CustomerProfile.java
│   │   ├── property
│   │   │   ├── Property.java
│   │   │   ├── PropertyOwnership.java
│   │   │   └── PropertyVerification.java
│   │   ├── service
│   │   │   ├── ServiceCatalogItem.java
│   │   │   ├── ServiceRequest.java
│   │   │   └── ServiceRequestHistory.java
│   │   ├── subscription
│   │   │   ├── Subscription.java
│   │   │   ├── SubscriptionPlan.java
│   │   │   └── BillingCycle.java
│   │   ├── payment
│   │   │   ├── Payment.java
│   │   │   ├── Invoice.java
│   │   │   └── Refund.java
│   │   ├── report
│   │   │   ├── Report.java
│   │   │   └── ReportMetric.java
│   │   ├── notification
│   │   │   ├── Notification.java
│   │   │   └── NotificationTemplate.java
│   │   ├── complaint
│   │   │   ├── Complaint.java
│   │   │   └── ComplaintResolution.java
│   │   ├── marketplace
│   │   │   ├── MarketplaceListing.java
│   │   │   └── MarketplaceOffer.java
│   │   ├── agent
│   │   │   ├── Agent.java
│   │   │   ├── AgentAssignment.java
│   │   │   └── AgentAvailability.java
│   │   ├── operations
│   │   │   ├── OperationsQueue.java
│   │   │   └── EscalationRecord.java
│   │   └── admin
│   │       ├── SystemConfiguration.java
│   │       └── AuditLog.java
│   ├── repository
│   │   ├── auth
│   │   │   ├── UserRepository.java
│   │   │   └── RefreshTokenRepository.java
│   │   ├── customer
│   │   │   ├── CustomerRepository.java
│   │   │   └── CustomerProfileRepository.java
│   │   ├── property
│   │   │   ├── PropertyRepository.java
│   │   │   └── PropertyVerificationRepository.java
│   │   ├── service
│   │   │   ├── ServiceCatalogItemRepository.java
│   │   │   └── ServiceRequestRepository.java
│   │   ├── subscription
│   │   │   ├── SubscriptionRepository.java
│   │   │   └── BillingCycleRepository.java
│   │   ├── payment
│   │   │   ├── PaymentRepository.java
│   │   │   ├── InvoiceRepository.java
│   │   │   └── RefundRepository.java
│   │   ├── report
│   │   │   ├── ReportRepository.java
│   │   │   └── ReportMetricRepository.java
│   │   ├── notification
│   │   │   ├── NotificationRepository.java
│   │   │   └── NotificationTemplateRepository.java
│   │   ├── complaint
│   │   │   ├── ComplaintRepository.java
│   │   │   └── ComplaintResolutionRepository.java
│   │   ├── agent
│   │   │   ├── AgentRepository.java
│   │   │   └── AgentAssignmentRepository.java
│   │   ├── operations
│   │   │   ├── QueueRepository.java
│   │   │   └── EscalationRepository.java
│   │   └── admin
│   │       ├── AuditLogRepository.java
│   │       └── SystemConfigurationRepository.java
│   ├── enums
│   │   ├── UserRole.java
│   │   ├── UserStatus.java
│   │   ├── PropertyStatus.java
│   │   ├── ServiceRequestStatus.java
│   │   ├── PaymentStatus.java
│   │   ├── NotificationType.java
│   │   ├── ComplaintStatus.java
│   │   ├── SubscriptionStatus.java
│   │   └── SystemActionType.java
│   └── state
│       ├── ServiceRequestStateMachine.java
│       ├── PaymentStateMachine.java
│       ├── SubscriptionStateMachine.java
│       ├── ComplaintStateMachine.java
│       └── NotificationStateMachine.java
├── infrastructure
│   ├── persistence
│   │   ├── entity
│   │   │   ├── UserEntity.java
│   │   │   ├── CustomerEntity.java
│   │   │   ├── PropertyEntity.java
│   │   │   ├── ServiceRequestEntity.java
│   │   │   ├── PaymentEntity.java
│   │   │   └── NotificationEntity.java
│   │   ├── jpa
│   │   │   ├── JpaUserRepository.java
│   │   │   └── JpaServiceRequestRepository.java
│   │   └── migration
│   │       ├── FlywayConfig.java
│   │       └── DataMigrationRunner.java
│   ├── security
│   │   ├── SecurityConfig.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationEntryPoint.java
│   │   ├── UserPrincipal.java
│   │   ├── AuthorizationService.java
│   │   └── PermissionEvaluator.java
│   ├── cache
│   │   ├── RedisConfig.java
│   │   ├── RedisCacheService.java
│   │   ├── CacheKeys.java
│   │   └── CacheManager.java
│   ├── message
│   │   ├── EventPublisher.java
│   │   ├── OutboxPublisher.java
│   │   ├── WebhookClient.java
│   │   └── MessagingConfig.java
│   ├── external
│   │   ├── PaymentGatewayClient.java
│   │   ├── SmsGatewayClient.java
│   │   ├── EmailGatewayClient.java
│   │   └── PushNotificationClient.java
│   └── filesystem
│       ├── StorageService.java
│       └── FileUploadService.java
├── config
│   ├── OpenApiConfig.java
│   ├── AppProperties.java
│   ├── ClockConfig.java
│   ├── AsyncConfig.java
│   ├── JacksonConfig.java
│   └── RetryConfig.java
├── common
│   ├── constants
│   │   ├── ApiErrorCodes.java
│   │   ├── SecurityConstants.java
│   │   └── CommonConstants.java
│   ├── exception
│   │   ├── ApiException.java
│   │   ├── ValidationException.java
│   │   ├── NotFoundException.java
│   │   ├── UnauthorizedException.java
│   │   └── GlobalExceptionHandler.java
│   ├── util
│   │   ├── DateUtil.java
│   │   ├── PaginationUtil.java
│   │   ├── StringNormalizer.java
│   │   └── UUIDUtil.java
│   ├── audit
│   │   ├── AuditContext.java
│   │   ├── AuditAspect.java
│   │   └── AuditLogWriter.java
│   └── pagination
│       ├── PageRequest.java
│       └── PageResponse.java
├── resources
│   ├── application.yml
│   ├── application-dev.yml
│   ├── application-qa.yml
│   ├── application-uat.yml
│   ├── application-prod.yml
│   ├── db
│   │   └── migration
│   │       ├── V1__init_schema.sql
│   │       ├── V2__seed_reference_data.sql
│   │       ├── V3__seed_roles_and_permissions.sql
│   │       └── V4__seed_admin_user.sql
│   ├── messages
│   │   ├── validation.properties
│   │   └── exceptions.properties
│   └── logback-spring.xml
└── test
    ├── java
    │   ├── integration
    │   │   ├── AuthIntegrationTest.java
    │   │   ├── ServiceRequestIntegrationTest.java
    │   │   └── PaymentIntegrationTest.java
    │   ├── unit
    │   │   ├── auth
    │   │   │   └── JwtTokenProviderTest.java
    │   │   ├── service
    │   │   │   └── PropertyServiceTest.java
    │   │   └── validation
    │   │       └── RequestValidatorTest.java
    │   └── security
    │       └── AuthorizationTest.java
    └── resources
        └── application-test.yml
```

---

# Module-by-Module Implementation Plan

## 1. Auth Module

Package:
- com.propertypilot.auth

### Entities
- User
- Role
- Permission
- RefreshToken
- UserSession

### Repositories
- UserRepository
- RoleRepository
- PermissionRepository
- RefreshTokenRepository
- UserSessionRepository

### Services
- AuthService
- UserService
- TokenService
- SessionService
- PasswordPolicyService

### Controllers
- AuthController
- TokenController
- UserController

### DTOs
- LoginRequest
- RegisterRequest
- RefreshTokenRequest
- AuthResponse
- UserResponse

### Validators
- LoginRequestValidator
- PasswordValidator
- OTPValidator
- RoleAssignmentValidator

### Mappers
- UserMapper
- SessionMapper

### Events
- UserRegisteredEvent
- LoginSucceededEvent
- SessionInvalidatedEvent
- PasswordChangedEvent

### State Machines
- UserStatusStateMachine
- SessionStatusStateMachine

### Security Requirements
- JWT access tokens
- refresh tokens stored securely
- password hashing with bcrypt or PBKDF2
- MFA/OTP support for sensitive actions
- session invalidation on logout and suspicious activity
- role-based access control

---

## 2. Customer Module

Package:
- com.propertypilot.customer

### Entities
- Customer
- CustomerProfile
- CustomerPreference
- CustomerAddress
- CustomerVerification

### Repositories
- CustomerRepository
- CustomerProfileRepository
- CustomerPreferenceRepository

### Services
- CustomerService
- CustomerProfileService
- CustomerVerificationService
- CustomerPreferenceService

### Controllers
- CustomerController
- CustomerProfileController
- CustomerPreferenceController

### DTOs
- CustomerCreateRequest
- CustomerUpdateRequest
- CustomerResponse
- CustomerProfileResponse

### Validators
- CustomerRegistrationValidator
- CustomerProfileValidator
- ConsentValidator

### Mappers
- CustomerMapper
- CustomerProfileMapper

### Events
- CustomerCreatedEvent
- CustomerUpdatedEvent
- CustomerVerificationRequiredEvent

### State Machines
- CustomerStatusStateMachine

### Security Requirements
- customers can access only own data
- customer admin access limited to managed roles
- PII masked in logs and error responses

---

## 3. Property Module

Package:
- com.propertypilot.property

### Entities
- Property
- PropertyAddress
- PropertyVerification
- PropertyOwnership
- PropertyDocument
- PropertyMedia

### Repositories
- PropertyRepository
- PropertyAddressRepository
- PropertyVerificationRepository
- PropertyOwnershipRepository

### Services
- PropertyService
- PropertyVerificationService
- PropertyOwnershipService
- PropertyMediaService

### Controllers
- PropertyController
- PropertyVerificationController
- PropertyOwnershipController

### DTOs
- PropertyCreateRequest
- PropertyUpdateRequest
- PropertyResponse
- PropertyVerificationRequest

### Validators
- PropertyInputValidator
- OwnershipValidator
- GPSCoordinateValidator
- DocumentTypeValidator

### Mappers
- PropertyMapper
- PropertyOwnershipMapper

### Events
- PropertyCreatedEvent
- PropertyVerifiedEvent
- PropertyRejectedEvent

### State Machines
- PropertyStatusStateMachine
- OwnershipVerificationStateMachine

### Security Requirements
- customer can manage own property records
- admin and operations may view verification information
- ownership verification auditable

---

## 4. Service Module

Package:
- com.propertypilot.service

### Entities
- ServiceCatalogItem
- ServiceRequest
- ServiceRequestHistory
- ServiceRequirement
- ServiceEvidence
- ServiceAssignment

### Repositories
- ServiceCatalogItemRepository
- ServiceRequestRepository
- ServiceRequestHistoryRepository
- ServiceEvidenceRepository
- ServiceAssignmentRepository

### Services
- ServiceCatalogService
- ServiceRequestService
- ServiceWorkflowService
- ServiceEvidenceService
- ServiceAssignmentService

### Controllers
- ServiceCatalogController
- ServiceRequestController
- ServiceStatusController
- ServiceEvidenceController

### DTOs
- ServiceCatalogResponse
- ServiceRequestCreateRequest
- ServiceRequestUpdateRequest
- ServiceRequestResponse
- ServiceStatusUpdateRequest

### Validators
- ServiceRequestValidator
- ServiceDateValidator
- ServiceStatusTransitionValidator
- EvidenceValidator

### Mappers
- ServiceCatalogMapper
- ServiceRequestMapper
- ServiceEvidenceMapper

### Events
- ServiceRequestedEvent
- ServiceAssignedEvent
- ServiceStatusChangedEvent
- ServiceCompletedEvent

### State Machines
- ServiceRequestStateMachine
- ServiceEvidenceStateMachine

### Security Requirements
- customer can create and view own service requests
- agent can only act on assigned tasks
- operations can override or escalate where policy allows

---

## 5. Subscription Module

Package:
- com.propertypilot.subscription

### Entities
- Subscription
- SubscriptionPlan
- BillingCycle
- RenewalSchedule
- PlanBenefit

### Repositories
- SubscriptionRepository
- SubscriptionPlanRepository
- BillingCycleRepository

### Services
- SubscriptionService
- SubscriptionPlanService
- RenewalService
- BillingLifecycleService

### Controllers
- SubscriptionController
- SubscriptionPlanController
- BillingController

### DTOs
- SubscriptionResponse
- SubscriptionCreateRequest
- SubscriptionPlanResponse
- BillingCycleResponse

### Validators
- SubscriptionValidator
- PlanSelectionValidator
- RenewalEligibilityValidator

### Mappers
- SubscriptionMapper
- SubscriptionPlanMapper

### Events
- SubscriptionCreatedEvent
- SubscriptionActivatedEvent
- SubscriptionCancelledEvent
- SubscriptionExpiredEvent

### State Machines
- SubscriptionStateMachine
- BillingCycleStateMachine

### Security Requirements
- only customer or admin can modify subscription where permitted
- sensitive billing transitions logged and auditable
- no bypass of plan validation

---

## 6. Payment Module

Package:
- com.propertypilot.payment

### Entities
- Payment
- Invoice
- Refund
- PaymentProviderTransaction
- PaymentAttempt

### Repositories
- PaymentRepository
- InvoiceRepository
- RefundRepository
- PaymentAttemptRepository

### Services
- PaymentService
- InvoiceService
- RefundService
- PaymentProviderAdapter
- PaymentLedgerService

### Controllers
- PaymentController
- InvoiceController
- RefundController

### DTOs
- PaymentRequest
- PaymentResponse
- RefundRequest
- InvoiceResponse

### Validators
- PaymentValidator
- RefundValidator
- AmountValidator
- CurrencyValidator

### Mappers
- PaymentMapper
- InvoiceMapper

### Events
- PaymentInitiatedEvent
- PaymentSucceededEvent
- PaymentFailedEvent
- PaymentRefundedEvent

### State Machines
- PaymentStateMachine
- InvoiceStateMachine
- RefundStateMachine

### Security Requirements
- PCI-safe handling of payment data
- gateway integration through provider abstraction
- idempotency keys for retries
- all payment events logged and auditable

---

## 7. Report Module

Package:
- com.propertypilot.report

### Entities
- Report
- ReportDefinition
- ReportMetric
- ReportJob
- ReportExport

### Repositories
- ReportRepository
- ReportMetricRepository
- ReportJobRepository
- ReportExportRepository

### Services
- ReportService
- AnalyticsService
- ReportExportService
- MetricAggregationService

### Controllers
- ReportController
- AnalyticsController

### DTOs
- ReportRequest
- ReportResponse
- ReportExportResponse

### Validators
- ReportQueryValidator
- DateRangeValidator
- ExportFormatValidator

### Mappers
- ReportMapper
- MetricMapper

### Events
- ReportGeneratedEvent
- ReportExportRequestedEvent
- ReportExportCompletedEvent

### State Machines
- ReportJobStateMachine
- ReportExportStateMachine

### Security Requirements
- role-based access to operational and admin dashboards
- export authorization with audit logging

---

## 8. Notification Module

Package:
- com.propertypilot.notification

### Entities
- Notification
- NotificationPreference
- NotificationTemplate
- NotificationChannel
- NotificationDelivery

### Repositories
- NotificationRepository
- NotificationTemplateRepository
- NotificationPreferenceRepository

### Services
- NotificationService
- NotificationTemplateService
- NotificationChannelService
- NotificationDeliveryService

### Controllers
- NotificationController
- NotificationPreferenceController

### DTOs
- NotificationRequest
- NotificationResponse
- NotificationPreferenceRequest

### Validators
- NotificationMessageValidator
- ChannelValidator
- PreferenceValidator

### Mappers
- NotificationMapper
- PreferenceMapper

### Events
- NotificationCreatedEvent
- NotificationSentEvent
- NotificationFailedEvent

### State Machines
- NotificationStateMachine
- NotificationDeliveryStateMachine

### Security Requirements
- no sensitive data in notification payloads
- user preference enforcement
- audit logging for message sends and failures

---

## 9. Complaint Module

Package:
- com.propertypilot.complaint

### Entities
- Complaint
- ComplaintThread
- ComplaintResolution
- ComplaintEscalation

### Repositories
- ComplaintRepository
- ComplaintThreadRepository
- ComplaintResolutionRepository

### Services
- ComplaintService
- ComplaintResolutionService
- ComplaintEscalationService

### Controllers
- ComplaintController
- ComplaintResolutionController

### DTOs
- ComplaintCreateRequest
- ComplaintUpdateRequest
- ComplaintResponse

### Validators
- ComplaintValidator
- EscalationValidator

### Mappers
- ComplaintMapper
- ComplaintResolutionMapper

### Events
- ComplaintCreatedEvent
- ComplaintUpdatedEvent
- ComplaintResolvedEvent
- ComplaintEscalatedEvent

### State Machines
- ComplaintStateMachine

### Security Requirements
- complaint visibility restricted by subject and role
- audit logging for actions and resolution

---

## 10. Marketplace Module

Package:
- com.propertypilot.marketplace

### Entities
- MarketplaceListing
- ListingCategory
- MarketplaceOffer
- ListingVisibility

### Repositories
- MarketplaceListingRepository
- MarketplaceOfferRepository

### Services
- MarketplaceService
- ListingService
- OfferService

### Controllers
- MarketplaceController
- ListingController

### DTOs
- ListingCreateRequest
- ListingResponse
- OfferRequest

### Validators
- ListingValidator
- OfferValidator

### Mappers
- MarketplaceMapper

### Events
- ListingCreatedEvent
- ListingUpdatedEvent
- OfferCreatedEvent

### State Machines
- ListingStateMachine

### Security Requirements
- listings visible based on role and access
- admin moderation required if marketplace is used in MVP beyond minimal exposure

---

## 11. Agent Module

Package:
- com.propertypilot.agent

### Entities
- Agent
- AgentAssignment
- AgentAvailability
- AgentPerformance

### Repositories
- AgentRepository
- AgentAssignmentRepository
- AgentAvailabilityRepository

### Services
- AgentService
- AgentAssignmentService
- AgentAvailabilityService

### Controllers
- AgentController
- AgentAssignmentController

### DTOs
- AgentResponse
- AgentAssignmentRequest
- AgentAssignmentResponse

### Validators
- AgentValidator
- AssignmentValidator
- AvailabilityValidator

### Mappers
- AgentMapper
- AgentAssignmentMapper

### Events
- AgentAssignedEvent
- AgentTaskStartedEvent
- AgentTaskCompletedEvent

### State Machines
- AgentAssignmentStateMachine

### Security Requirements
- agent access to assigned tasks only
- role restrictions for updates and assignments
- verification of task ownership before status change

---

## 12. Operations Module

Package:
- com.propertypilot.operations

### Entities
- OperationsQueue
- QueueItem
- EscalationRecord
- OperationalAlert

### Repositories
- QueueRepository
- EscalationRepository
- OperationalAlertRepository

### Services
- QueueService
- OperationsService
- EscalationService
- AlertService

### Controllers
- OperationsController
- QueueController
- EscalationController

### DTOs
- QueueItemResponse
- EscalationRequest
- OperationsDashboardResponse

### Validators
- QueueActionValidator
- EscalationValidator

### Mappers
- QueueMapper
- EscalationMapper

### Events
- QueueItemCreatedEvent
- EscalationRaisedEvent
- EscalationResolvedEvent

### State Machines
- OperationsQueueStateMachine
- EscalationStateMachine

### Security Requirements
- ops users can view only allowed queue and service activity
- escalation actions logged
- read-only access for low-permission roles

---

## 13. Admin Module

Package:
- com.propertypilot.admin

### Entities
- SystemConfiguration
- AdminUser
- AuditLog
- Role
- Permission
- FeatureFlag

### Repositories
- AuditLogRepository
- SystemConfigurationRepository
- FeatureFlagRepository

### Services
- AdminUserService
- RoleService
- ConfigurationService
- AuditService
- FeatureFlagService

### Controllers
- AdminController
- UserManagementController
- RoleController
- ConfigController

### DTOs
- AdminUserResponse
- RoleAssignmentRequest
- SystemConfigurationRequest
- AuditLogResponse

### Validators
- RoleAssignmentValidator
- ConfigurationValidator
- FeatureFlagValidator

### Mappers
- AdminUserMapper
- AuditMapper

### Events
- AdminActionLoggedEvent
- RoleUpdatedEvent
- ConfigurationChangedEvent

### State Machines
- FeatureFlagStateMachine
- SystemConfigurationStateMachine

### Security Requirements
- admin-level authorization only
- all admin actions logged
- config changes must include approval path if required
- emergency access controlled and auditable

---

## 14. Common Module

Package:
- com.propertypilot.common

### Purpose
- shared code, constants, policies, common DTOs, cross-cutting concerns

### Contents
- ApiResponse
- PagedResponse
- ApiErrorCodes
- DomainEvent
- Auditing
- GlobalExceptionHandler
- Validation utilities
- Date utilities
- pagination utilities
- security constants
- generic repository patterns

---

# Database Migration Roadmap

PropertyPilot should use Flyway for schema management. Every domain module should have a versioned migration associated with it.

## Migration Strategy

- Each migration is versioned and immutable
- Database changes must be backward compatible where possible
- Failed migration execution blocks the application bootstrap
- All application startup must validate migration status
- Use incremental SQL migration files with explicit naming conventions

## Recommended Naming Convention

```text
V<major>__<short_description>.sql
```

Examples:
- V1__create_auth_schema.sql
- V2__create_customer_schema.sql
- V3__create_property_schema.sql
- V4__create_service_schema.sql
- V5__create_subscription_schema.sql
- V6__create_payment_schema.sql
- V7__create_notification_schema.sql
- V8__create_complaint_schema.sql
- V9__create_agent_schema.sql
- V10__create_operations_schema.sql
- V11__create_admin_schema.sql
- V12__seed_reference_data.sql
- V13__seed_roles_and_permissions.sql
- V14__insert_admin_users.sql
- V15__create_outbox_tables.sql

## Reference Migration Mapping

### Auth
- users
- roles
- permissions
- refresh_tokens
- user_sessions

### Customer
- customers
- customer_profiles
- customer_preferences
- customer_addresses

### Property
- properties
- property_addresses
- property_verification
- property_documents
- property_media

### Service
- service_catalog
- service_requests
- service_history
- service_evidence
- assignments

### Subscription and Billing
- subscriptions
- plans
- billing_cycles
- renewal_records

### Payment
- payments
- invoices
- refunds
- payment_attempts

### Notification
- notifications
- notification_templates
- notification_preferences
- delivery_records

### Complaint
- complaints
- complaint_threads
- complaint_resolutions

### Agent
- agents
- agent_assignments
- agent_availability

### Operations
- queue_items
- escalation_records
- operational_alerts

### Admin
- system_configuration
- audit_logs
- feature_flags

### Outbox & Eventing
- outbox_events
- webhook_events
- async_job_queue

## Migration Validation
- Validate migrations in test and UAT before production
- Run dry-run or schema diff in CI
- Verify rollback path for each major migration set
- Ensure migration scripts are idempotent where necessary
- Ensure database constraints match the physical model

---

# API Implementation Roadmap

API implementation must align with the OpenAPI contract and domain model. Each controller should map to one or more domain-specific operations.

## API Group Mapping

### Auth
- POST /api/v1/auth/register
- POST /api/v1/auth/login
- POST /api/v1/auth/refresh
- POST /api/v1/auth/logout
- GET /api/v1/auth/me

### Customer
- GET /api/v1/customers/{id}
- PUT /api/v1/customers/{id}
- GET /api/v1/customers/{id}/properties
- POST /api/v1/customers/{id}/preferences

### Property
- GET /api/v1/properties
- POST /api/v1/properties
- GET /api/v1/properties/{id}
- PUT /api/v1/properties/{id}
- POST /api/v1/properties/{id}/verify

### Service
- GET /api/v1/services
- GET /api/v1/services/{id}
- POST /api/v1/service-requests
- GET /api/v1/service-requests/{id}
- PATCH /api/v1/service-requests/{id}/status
- GET /api/v1/service-requests/{id}/history

### Subscription
- GET /api/v1/subscriptions
- POST /api/v1/subscriptions
- PATCH /api/v1/subscriptions/{id}
- DELETE /api/v1/subscriptions/{id}

### Payment
- POST /api/v1/payments
- GET /api/v1/payments/{id}
- POST /api/v1/payments/{id}/refund
- GET /api/v1/invoices/{id}

### Report
- GET /api/v1/reports
- GET /api/v1/reports/{id}
- POST /api/v1/reports/export

### Notification
- GET /api/v1/notifications
- PATCH /api/v1/notifications/{id}/read
- PUT /api/v1/notifications/preferences

### Complaint
- POST /api/v1/complaints
- GET /api/v1/complaints/{id}
- PATCH /api/v1/complaints/{id}/status

### Agent
- GET /api/v1/agents/{id}/assignments
- PATCH /api/v1/assignments/{id}/status
- POST /api/v1/assignments/{id}/complete

### Operations
- GET /api/v1/operations/queue
- POST /api/v1/operations/assignments
- POST /api/v1/operations/escalations

### Admin
- GET /api/v1/admin/users
- POST /api/v1/admin/roles
- PUT /api/v1/admin/configurations/{id}
- GET /api/v1/admin/audit-logs

## API Standards
- versioning via /api/v1
- consistent error response model
- pagination and sorting support
- strict validation for request DTOs
- standardized HTTP status mapping
- clear distinction between 4xx and 5xx errors
- support for idempotency keys on payment and mutation endpoints

---

# Authentication & Authorization Design

## JWT Design

Use JWT access tokens and refresh tokens with standard claims.

Claims:
- sub: user id
- role: primary role
- permissions: list of granted permissions
- aud: audience
- iss: issuer
- exp: expiry
- iat: issued at

## Token Strategy
- access token short-lived, e.g., 15 minutes
- refresh token long-lived, e.g., 7 to 30 days
- rotate refresh tokens on use
- revoke tokens on logout and suspicious activity
- store refresh token metadata in database and Redis for validation

## Roles
- CUSTOMER
- AGENT
- OPERATIONS
- ADMIN
- SYSTEM

## Permissions
Organize permissions by domain:
- customer:read_own_profile, customer:update_own_profile, property:create, property:read_own
- agent:read_assignment, assignment:update_own, property:verify
- operations:queue:view, queue:assign, escalation:create
- admin:user:manage, role:manage, config:update, audit:read

## RBAC Design
- assign roles to users
- map roles to permissions
- enforce permission checks at controller/service level
- use method security annotations such as:
  - @PreAuthorize
  - @Secured
- enforce role granularity and domain ownership checks

## Security Filters
- JWT authentication filter
- exception handling filter
- rate limiting filter
- request logging filter
- CSRF disabled for stateless API
- secure cookie strategy only if applicable to web UI

---

# Event-Driven Components

## Outbox Pattern

Use outbox pattern for reliable event publishing:
- events written to outbox table in same transaction as business data
- outbox relay publishes to brokers or external systems asynchronously
- ensures no lost events during transaction failures

### Outbox Tables
- outbox_events
- webhook_events
- async_job_queue

## Event Types

### Notification Events
- UserRegisteredEvent
- PropertyCreatedEvent
- ServiceRequestedEvent
- ServiceCompletedEvent
- PaymentSucceededEvent
- ComplaintCreatedEvent

### Subscription Events
- SubscriptionCreatedEvent
- SubscriptionCancelledEvent
- RenewalTriggeredEvent
- ExpiredSubscriptionEvent

### Payment Events
- PaymentInitiatedEvent
- PaymentSucceededEvent
- PaymentFailedEvent
- RefundRequestedEvent
- RefundProcessedEvent

### Webhook Processing
- webhook events created from payment or external integration notifications
- signature validation
- retries with exponential backoff
- dead-letter handling for permanent failures

### Event Consumers
- notification dispatcher
- payment outcome processor
- subscription state updater
- report generator
- audit writer

---

# Caching Strategy

## Redis Usage
Redis should be used for:
- JWT blacklist / token invalidation metadata
- user session lookups
- short-lived API response cache
- service catalog caching
- hot report and dashboard metrics
- lock / deduplication keys

## Cache Keys
Use namespaced keys such as:
- user:profile:{userId}
- property:details:{propertyId}
- service:catalog:{categoryId}
- queue:operations:{date}
- report:summary:{userId}:{period}

## TTL Strategy
- user profile: 5-15 min
- service catalog: 15-60 min
- queue summaries: 1-5 min
- report metrics: 5-15 min
- token invalidation metadata: short TTL aligned to token expiry
- hot API responses: short TTL with cache-aside pattern

## Cache Invalidation
- update cache on create/update/delete events
- invalidate on domain mutation
- avoid stale data on payment and subscription transitions

---

# Error Handling Strategy

## Global Exception Handling

Use centralized exception handling with:
- ApiException base class
- validation exceptions
- not found exceptions
- unauthorized exceptions
- business rule exceptions
- internal server errors masked appropriately

## Error Codes
Define structured codes such as:
- AUTH_001 invalid credentials
- AUTH_002 token expired
- AUTH_003 refresh token invalid
- USER_001 user not found
- PROPERTY_001 property not found
- SERVICE_001 invalid status transition
- PAYMENT_001 payment failed
- SUBSCRIPTION_002 plan invalid
- COMPLAINT_001 complaint not found

## Validation Errors
- standard field-level validation messages
- consistent names and required constraints
- validation exceptions for required fields and domain rules
- request validation in DTO annotations and custom validators

## Error Response Structure
```json
{
  "code": "SERVICE_001",
  "message": "Invalid status transition for service request",
  "details": [
    "Current status: IN_PROGRESS",
    "Requested status: CANCELLED"
  ],
  "timestamp": "2026-08-31T12:00:00Z"
}
```

---

# Audit Strategy

## Audit Logs
Capture:
- user login and logout
- role assignments
- admin config changes
- payment actions
- subscription lifecycle updates
- complaint resolution changes
- service status changes
- ownership verification events
- sensitive data access attempts

## Change Tracking
- store before and after values for admin changes
- log actor id, entity type, action, timestamp, and reason
- include correlation id for request tracing

## User Activity
- record user activity at principal/service boundaries
- include endpoint metadata, request id, and actor context
- separate audit trail from app logs

## Audit Storage
- store structured audit entries in PostgreSQL
- include retention policies and access restrictions
- restrict audit read access to authorized roles

---

# Observability

## Logging
- structured JSON logging
- correlation IDs in each request
- log levels based on environment
- log sensitive operations with controlled access
- avoid logging raw tokens, payment data, or PII

## Metrics
Track:
- API latency
- request rate
- error rate
- DB query performance
- Redis hit/miss rate
- queue backlog
- payment processing time
- worker processing duration

## Tracing
- enable distributed tracing across controller, service, DB, Redis, and external integrations
- trace id propagation across async tasks
- include service version and environment in trace metadata

## Health Checks
- readiness endpoint
- liveness endpoint
- DB connectivity health
- Redis health
- external service health
- queue health

## Monitoring & Alerting
- error spike detection
- queue backlog alerts
- latency threshold alerts
- payment failure alerts
- login and token failure alerts
- DB resource saturation alerts

---

# CI/CD Requirements

## Build
- Maven build with Java 21
- dependency vulnerability checks
- static analysis and formatting validation
- unit test execution as build gate
- compile and package checks

## Test
- unit test suite
- integration test suite
- API contract validation
- security scans
- database migration validation
- regression suite pre-merge

## Deploy
- environment-specific deployment pipelines
- immutable build artifact promotion
- deployment approvals for QA, UAT, and production
- blue/green or rolling deployments for production
- health verification gate after deployment

## Rollback
- provide rollback to previous stable artifact
- rollback plan for DB schema and application version
- validate rollback path in CI/CD and cutover readiness
- maintain artifact retention and deployment metadata

---

# Security Controls

## OWASP Alignment
- protect against injection attacks
- validate input and payload shapes
- prevent broken access control
- sanitize outputs and logs
- protect against SSRF if external integrations are used
- enforce secure session and token management
- protect against mass assignment and insecure deserialization

## Input Validation
- Bean Validation for DTOs
- custom validators for business rules
- IP validation, phone validation, OTP validation, date transition validation
- reject invalid enums and malformed payloads

## Rate Limiting
- login rate limiting
- OTP resend rate limiting
- payment retry protection
- public API abuse protection
- operation-specific quotas for report generation

## Secrets Management
- externalize all secrets
- use environment variables or secret manager
- rotate tokens, certs, DB credentials regularly
- do not store secrets in source control

---

# Sprint-wise Backend Delivery Plan

## Sprint 1
Focus:
- foundation setup
- project skeleton
- security setup
- DB and migration baseline
- auth domain
- base common modules

Deliverables:
- Spring Boot project initialized
- PostgreSQL and Redis configured
- Flyway migration baseline
- JWT security and RBAC skeleton
- basic health, logging, and config
- user and role domain model

---

## Sprint 2
Focus:
- customer and property domain
- service catalog and service-request workflows
- controller and service scaffolding
- validation framework

Deliverables:
- customer onboarding APIs
- property create/read/update APIs
- service catalog and booking flow
- request state machine implementation
- migration and repository layer for core entities

---

## Sprint 3
Focus:
- agent workflow and operations queue
- payments and subscriptions
- notification foundations
- event publishing and outbox

Deliverables:
- assignment management APIs
- service workflow progression
- payment and invoice flows
- subscription lifecycle APIs
- notification templates and dispatch logic
- outbox integration

---

## Sprint 4
Focus:
- complaints, reports, and admin controls
- audit logging
- performance and security validation
- report generation APIs

Deliverables:
- complaint lifecycle APIs
- reporting and analytics APIs
- admin user and config management
- audit log implementation
- security scanning and hardening

---

## Sprint 5
Focus:
- stabilization, performance tuning, UAT support
- production readiness and deployment validation

Deliverables:
- end-to-end API validation
- regression suite
- production deployment pipeline operationalization
- rollback script validation
- observability tuning and alert readiness
- UAT support and defect closure

---

# Effort Estimates

## Developer Count
Recommended team shape:
- 1 backend architect
- 3 to 5 backend engineers
- 1 QA automation engineer
- 1 DevOps/Platform engineer
- 1 security reviewer or security-minded engineer
- optional 1 integration engineer for payment/notifications/external services

## Timeline
- Foundation and core modules: 4 to 6 weeks
- End-to-end domain implementation: 6 to 10 weeks
- Hardening, security, performance, and PROD readiness: 3 to 6 weeks

Total realistic MVP backend implementation window:
- 10 to 16 weeks depending on delivery maturity and team size

## Dependencies
- UI and screen finalized
- API contract agreement finalized
- DB design approved
- payment gateway integration spec available
- notification provider integration spec available
- security model approved by product and architecture
- environment provisioning completed

## Risks
- unclear ownership of integration contracts
- late changes in business rules
- insufficient validation of payment and notification flows
- DB migration complexity during production deployment
- excessive scope creep beyond MVP
- missing test automation for critical flows
- weak operational ownership for production readiness

---

# Implementation Recommendation

The backend is best implemented as:
- a modular Spring Boot 3 monolith with clearly separated packages and bounded contexts
- PostgreSQL-backed transactional domain model
- Redis-adjacent cache and short-lived session metadata
- Flyway-managed migrations
- event-driven integration using outbox and asynchronous workers
- security-first design with JWT and RBAC
- observability-first instrumentation from day one

This approach gives PropertyPilot a strong foundation for MVP delivery while also enabling later decomposition if product scale or team scaling requires service isolation.

---

# Exit Criteria for Backend Foundation Completion

The backend foundation is ready for MVP delivery when:
- all required modules are implemented and tested
- OpenAPI contract aligns with actual controllers
- Flyway migration pipeline is stable and validated
- auth and RBAC are tested
- payment, subscription, property, and service workflows pass end-to-end validation
- Redis and PostgreSQL are operational in all environments
- outbox-based async processing is validated
- monitoring, logs, and health checks are active
- production deployment and rollback are validated
- security scans and dependency checks are passing
- operations and admin workflows are validated in UAT

---

# Final Summary

This backend foundation plan provides a production-grade blueprint for building the PropertyPilot platform using Java 21, Spring Boot 3, PostgreSQL, Redis, JWT, Flyway, Maven, and Docker. It aligns the implementation with the product vision, the canonical data model, and the API contract while establishing robust security, auditing, observability, and deployment practices.

By structuring the backend around clear domain modules, enforcing a migration-driven database model, and designing for operational maturity from the start, the team can deliver a secure, testable, and scalable MVP foundation.
