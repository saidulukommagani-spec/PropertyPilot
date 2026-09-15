````markdown
# OpenAPI Completion Plan

Document Type: API Contract Completion Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: API Architecture / Backend Engineering / Product / QA

---

# Purpose

This document transforms the current PropertyPilot OpenAPI specification into a complete production-ready API contract that aligns with the screen catalog, user journeys, database model, business requirements, and cross-cutting platform constraints.

The objective is to:
- close schema and endpoint gaps
- align APIs with the canonical data model
- standardize request/response contracts
- define governance conventions for versioning, pagination, filtering, and error handling
- complete the API layer required for backend generation, frontend integration, testing, and production deployment

This plan covers the full business and operational surface of PropertyPilot, including customer, agent, operations, admin, marketplace, KYC, reporting, payments, subscriptions, notifications, and audit flows.

---

# Current State Assessment

## Existing APIs
The current OpenAPI document already indicates a base set of operational API domains, including likely coverage for:
- authentication and session flows
- customer profile and onboarding
- property lifecycle
- service request operations
- billing and subscription basics
- notification-related communications
- admin and operations actions
- complaint and support flows

These APIs provide a good platform foundation, but they are not yet complete enough for full production readiness across all modules.

## Missing APIs
The main missing APIs are in the following areas:
- property proof/verification workflows
- evidence upload and file metadata APIs
- visit scheduling, execution, and GPS tracking
- payment webhook processing and reconciliation
- marketplace vendor and quotations flow
- KYC submission review and approval flow
- report generation and export lifecycle
- notification delivery status and retries
- outbox/event-driven processing APIs
- cross-role operations and admin audit interactions
- privileged configuration and feature flag APIs

## Partially Defined APIs
The following areas likely exist but are not yet fully consistent:
- customer CRUD and profile endpoints
- property CRUD and ownership workflows
- service request create/update/history lifecycle
- complaint create/track/resolve flows
- notification status endpoints
- admin configuration APIs
- report generation routes
- payment and subscription state management

Likely issues:
- missing status transitions
- inconsistent error contracts
- incomplete pagination contracts
- missing model examples and field definitions
- weak object storage metadata contracts
- incomplete field-level validation metadata

## Duplicate APIs
Likely duplication risk between modules:
- user/profile retrieval endpoints across customer and admin routes
- service request endpoints across customer, operations, and agent with overlapping semantics
- notification endpoints for customer and admin actions
- payment invoice and refund endpoints may be fragmented across modules
- report and dashboard API routes may overlap with analytics endpoints

## Conflicting APIs
The main conflicts to resolve:
- different naming conventions for resource lifecycle endpoints
- inconsistent status naming across customer, agent, and admin flows
- differing ownership and authorization semantics for same resources
- inconsistency between database and API entity names
- diverse response envelope standards across endpoints
- variation in filter and sort semantics between modules

---

# API Governance Alignment

## Versioning Strategy
Use URL-based versioning for major contract changes:
- /api/v1/...
- future major version increments require explicit migration path

Rules:
- minor changes must remain backward compatible
- breaking changes must be introduced in a new version
- deprecate old endpoints with sunset period
- document API version in response headers when appropriate

## Base Path Standard
Consistent path conventions:
- /api/v1/auth
- /api/v1/customers
- /api/v1/properties
- /api/v1/services
- /api/v1/visits
- /api/v1/payments
- /api/v1/subscriptions
- /api/v1/complaints
- /api/v1/notifications
- /api/v1/marketplace
- /api/v1/admin
- /api/v1/operations
- /api/v1/kyc
- /api/v1/reports

Each resource group must have:
- consistent plural nouns
- CRUD style actions for core entities
- workflow-specific routes for state transitions

## Error Standards
All endpoints must use a standard error envelope:
- code
- message
- details
- traceId
- timestamp
- fieldErrors where applicable

Recommended error types:
- validation_error
- unauthorized
- forbidden
- not_found
- conflict
- rate_limited
- timeout
- dependency_failure
- internal_error

## Pagination Standards
Standardize query parameters:
- page
- pageSize
- sortBy
- sortOrder
- filterBy
- search

Response shape:
- items
- page
- pageSize
- totalItems
- totalPages
- hasNextPage

## Filtering Standards
Use consistent filter semantics:
- eq
- ne
- in
- contains
- gt
- gte
- lt
- lte
- between
- startsWith
- endsWith

Examples:
- /properties?status=ACTIVE&city=Hyderabad
- /service-requests?assignedTo=agent-123&status=IN_PROGRESS

## Sorting Standards
- sortBy is a field name from schema
- sortOrder is asc or desc
- default sort defined on each endpoint
- prohibit ad hoc sort over unsupported fields
- do not allow free-form sort expressions unless explicitly documented

## Idempotency Standards
Critical for:
- payment initiation
- subscription purchase
- complaint creation
- report generation
- refunds and cross-system callbacks

Rules:
- require Idempotency-Key header on safe non-idempotent operations
- store idempotency records for a configured retention window
- reject duplicate requests with same key and same payload

## Webhook Standards
All webhooks must:
- use HMAC signature verification
- include eventId, eventType, occurredAt, resourceId
- verify payload version
- support retry with exponential backoff
- expose a webhook replay endpoint where required

## Authentication Standards
Use JWT-based auth with:
- access token
- refresh token
- token expiry metadata
- role and permission claims
- refresh endpoint with rotation
- reject expired or invalid tokens consistently

---

# Domain API Completion Roadmap

## Authentication

### Login
Required APIs:
- POST /api/v1/auth/login
- POST /api/v1/auth/otp/request
- POST /api/v1/auth/otp/verify
- POST /api/v1/auth/token/refresh
- POST /api/v1/auth/logout
- GET /api/v1/auth/me
- GET /api/v1/auth/permissions

Required models:
- LoginRequest
- OtpRequest
- OtpVerifyRequest
- TokenRefreshRequest
- AuthUserProfile
- AuthTokenResponse

### OTP
- request OTP by mobile/email
- validate OTP
- support resend lock and expiry
- audit OTP attempts and failed validations

### Token Refresh
- rotate refresh tokens
- reject reused tokens
- return new access token with same principal metadata

### Logout
- invalidate server-side session or token state
- revoke refresh token
- remove client-side app state

### Role APIs
- GET /api/v1/auth/roles
- GET /api/v1/auth/roles/{roleId}/permissions

---

## Customer

### CRUD
- GET /api/v1/customers/{id}
- POST /api/v1/customers
- PUT /api/v1/customers/{id}
- PATCH /api/v1/customers/{id}
- DELETE /api/v1/customers/{id} (soft delete based on policy)

### Profile
- GET /api/v1/customers/me/profile
- PUT /api/v1/customers/me/profile
- PATCH /api/v1/customers/me/profile

### Addresses
- GET /api/v1/customers/me/addresses
- POST /api/v1/customers/me/addresses
- PUT /api/v1/customers/me/addresses/{id}
- DELETE /api/v1/customers/me/addresses/{id}

### Preferences
- GET /api/v1/customers/me/preferences
- PUT /api/v1/customers/me/preferences

---

## Property

### CRUD
- GET /api/v1/properties
- GET /api/v1/properties/{id}
- POST /api/v1/properties
- PUT /api/v1/properties/{id}
- PATCH /api/v1/properties/{id}
- DELETE /api/v1/properties/{id}

### Documents
- POST /api/v1/properties/{id}/documents
- GET /api/v1/properties/{id}/documents
- DELETE /api/v1/properties/{id}/documents/{documentId}

### Ownership
- GET /api/v1/properties/{id}/ownership
- POST /api/v1/properties/{id}/ownership/verify
- GET /api/v1/properties/{id}/ownership/history

### Monitoring
- GET /api/v1/properties/{id}/status-history
- PATCH /api/v1/properties/{id}/status
- GET /api/v1/properties/verification-queue

---

## Service Requests

### Create
- POST /api/v1/service-requests

### Track
- GET /api/v1/service-requests/{id}
- GET /api/v1/service-requests/{id}/history
- GET /api/v1/service-requests/my

### Cancel
- PATCH /api/v1/service-requests/{id}/cancel

### Reschedule
- PATCH /api/v1/service-requests/{id}/reschedule

### History
- GET /api/v1/service-requests/{id}/timeline
- GET /api/v1/service-requests/{id}/assignments

---

## Visits

### Scheduling
- POST /api/v1/visits
- GET /api/v1/visits/{id}
- GET /api/v1/visits
- PATCH /api/v1/visits/{id}/schedule

### Execution
- PATCH /api/v1/visits/{id}/start
- PATCH /api/v1/visits/{id}/complete
- PATCH /api/v1/visits/{id}/cancel

### Evidence
- POST /api/v1/visits/{id}/evidence
- GET /api/v1/visits/{id}/evidence
- DELETE /api/v1/visits/{id}/evidence/{evidenceId}

### GPS
- POST /api/v1/visits/{id}/gps/checkin
- POST /api/v1/visits/{id}/gps/checkout
- GET /api/v1/visits/{id}/gps

---

## Reports

### Generate
- POST /api/v1/reports
- POST /api/v1/reports/generate
- GET /api/v1/reports/{id}

### Download
- GET /api/v1/reports/{id}/download
- GET /api/v1/reports/{id}/export

### Evidence Viewer
- GET /api/v1/evidence/{id}
- GET /api/v1/evidence/{id}/preview
- GET /api/v1/evidence/{id}/download

---

## Subscription

### Plans
- GET /api/v1/subscriptions/plans
- GET /api/v1/subscriptions/plans/{planId}

### Purchase
- POST /api/v1/subscriptions/purchase
- GET /api/v1/subscriptions/my

### Renewal
- POST /api/v1/subscriptions/{id}/renew

### Upgrade
- POST /api/v1/subscriptions/{id}/upgrade

### Downgrade
- POST /api/v1/subscriptions/{id}/downgrade

### Pause
- POST /api/v1/subscriptions/{id}/pause

### Cancel
- POST /api/v1/subscriptions/{id}/cancel

### Grace Period
- GET /api/v1/subscriptions/{id}/grace-period
- POST /api/v1/subscriptions/{id}/grace-period/resolve

---

## Payments

### Checkout
- POST /api/v1/payments/checkout
- GET /api/v1/payments/{id}

### Webhook
- POST /api/v1/payments/webhooks/provider
- GET /api/v1/payments/webhooks/verify
- POST /api/v1/payments/webhooks/replay

### Refund
- POST /api/v1/payments/{id}/refund
- GET /api/v1/payments/{id}/refunds

### Invoice
- GET /api/v1/invoices/{id}
- GET /api/v1/invoices/my
- POST /api/v1/invoices/{id}/download

### Reconciliation
- GET /api/v1/payments/reconciliation
- POST /api/v1/payments/reconciliation/run
- GET /api/v1/payments/reconciliation/{id}

---

## Complaints

### Create
- POST /api/v1/complaints

### Track
- GET /api/v1/complaints/{id}
- GET /api/v1/complaints/my

### Resolve
- PATCH /api/v1/complaints/{id}/resolve

### Escalate
- PATCH /api/v1/complaints/{id}/escalate

---

## Notifications

### Email
- POST /api/v1/notifications/email
- GET /api/v1/notifications/email/status

### SMS
- POST /api/v1/notifications/sms
- GET /api/v1/notifications/sms/status

### WhatsApp
- POST /api/v1/notifications/whatsapp
- GET /api/v1/notifications/whatsapp/status

### Push
- POST /api/v1/notifications/push
- GET /api/v1/notifications/push/status

### Delivery Status
- GET /api/v1/notifications/{id}/delivery-status
- GET /api/v1/notifications/delivery-summary

---

## Marketplace

### Vendors
- GET /api/v1/marketplace/vendors
- GET /api/v1/marketplace/vendors/{id}
- POST /api/v1/marketplace/vendors

### Quotations
- POST /api/v1/marketplace/quotations
- GET /api/v1/marketplace/quotations/{id}
- GET /api/v1/marketplace/quotations/my

### Assignments
- POST /api/v1/marketplace/assignments
- GET /api/v1/marketplace/assignments/{id}

---

## Admin

### User Management
- GET /api/v1/admin/users
- GET /api/v1/admin/users/{id}
- POST /api/v1/admin/users
- PATCH /api/v1/admin/users/{id}
- DELETE /api/v1/admin/users/{id}

### Pricing
- GET /api/v1/admin/pricing/plans
- POST /api/v1/admin/pricing/plans
- PUT /api/v1/admin/pricing/plans/{id}

### Configuration
- GET /api/v1/admin/config
- PUT /api/v1/admin/config/{key}
- POST /api/v1/admin/feature-flags

### Audit
- GET /api/v1/admin/audit
- GET /api/v1/admin/audit/{id}

---

## Operations

### Assignment
- GET /api/v1/operations/assignments
- POST /api/v1/operations/assignments
- PATCH /api/v1/operations/assignments/{id}

### Monitoring
- GET /api/v1/operations/dashboard
- GET /api/v1/operations/queues
- GET /api/v1/operations/workload

### Escalation
- POST /api/v1/operations/escalations
- GET /api/v1/operations/escalations
- PATCH /api/v1/operations/escalations/{id}

---

## KYC

### Submission
- POST /api/v1/kyc/submissions
- GET /api/v1/kyc/submissions/{id}

### Review
- GET /api/v1/kyc/review-queue
- POST /api/v1/kyc/submissions/{id}/review

### Approval
- POST /api/v1/kyc/submissions/{id}/approve

### Rejection
- POST /api/v1/kyc/submissions/{id}/reject

### Reverification
- POST /api/v1/kyc/submissions/{id}/reverify

---

# Request/Response Model Backlog

## Missing DTOs
Need to define DTOs for:
- UserSession
- RefreshToken
- CustomerProfile
- PropertyOwnership
- ServiceRequestAssignment
- VisitExecution
- ComplaintThreadMessage
- PaymentGatewayResponse
- SubscriptionLifecycleEvent
- ReportJobStatus
- KYCVerificationSubmission
- NotificationDeliveryState
- MarketplaceAssignment
- AdminAuditEntry

## Missing Schemas
Missing schemas likely include:
- ErrorResponse
- PaginationResponse
- FilterCriteria
- SortCriteria
- FileUploadMetadata
- EvidenceMetadata
- GeoPoint
- PaymentStatus
- ServiceStatus
- ComplaintStatus
- NotificationChannel
- MarketplaceStatus
- KYCStatus
- ReportExportStatus

## Missing Enums
Need completion for:
- UserRole
- PropertyStatus
- PropertyVerificationStatus
- ServiceRequestStatus
- VisitStatus
- PaymentStatus
- SubscriptionStatus
- ComplaintStatus
- NotificationChannel
- NotificationStatus
- KYCStatus
- ReportStatus
- AssignmentStatus
- EscalationStatus

## Missing Error Models
Define standard error models:
- ApiError
- ValidationError
- FieldValidationError
- ResourceNotFoundError
- ConflictError
- ForbiddenError
- RateLimitError
- DependencyError
- TimeoutError

---

# Webhook Contract Backlog

## Payment Webhooks
- payment.created
- payment.updated
- payment.failed
- payment.refunded
- invoice.generated
- invoice.updated

Required fields:
- eventId
- eventType
- occurredAt
- provider
- status
- paymentId
- amount
- currency
- metadata

## Notification Webhooks
- notification.sent
- notification.delivered
- notification.failed
- notification.read
- notification.clicked

## Subscription Webhooks
- subscription.created
- subscription.renewed
- subscription.expired
- subscription.cancelled
- subscription.upgraded
- subscription.downgraded

## Marketplace Webhooks
- quotation.created
- quotation.accepted
- assignment.completed
- listing.published
- listing.archived

---

# Security Alignment

## JWT
- access token claims must include subject, role, tenant context, permissions
- token expiry must be validated centrally
- refresh tokens should rotate and be stored safely
- token validation middleware should enforce policies consistently

## RBAC
Define roles:
- customer
- agent
- operations
- admin
- nri
- system_service
- support_user

## Scopes
Define permission scopes for:
- customer read/write
- agent task operations
- operations assignment control
- admin configuration
- report download access
- notification management
- payment initiation and refund review

## Permissions
Explicit permission model required for:
- property read/write
- service request update
- visit evidence upload
- KYC review
- payment refund
- admin config change
- report generation

---

# API Traceability Matrix

The API traceability matrix should explicitly map:
- requirements to API endpoints
- features to endpoint groups
- screens to APIs
- database entities to API resources

Recommended traceability categories:
- requirement ID
- feature ID
- screen ID
- API path
- resource name
- schema name
- database entity
- authorization requirement
- test coverage status

Examples:
- SRS-Auth-001 -> POST /api/v1/auth/login
- SRS-Property-013 -> GET /api/v1/properties/{id}
- Screen-C06 -> GET /api/v1/customers/me/dashboard
- DB-Property-001 -> properties table
- DB-Payment-003 -> payments table

This matrix must be enforced in PR reviews and release validation.

---

# MVP vs Future APIs

## Phase 1
Core MVP APIs:
- auth and OTP
- customer profile and onboarding
- property create/read/update
- service request create/track
- visit scheduling and evidence
- basic payment flow and invoice retrieval
- complaint create/track
- notification send and read
- admin basic user and config management

## Phase 2
Expansion APIs:
- advanced marketplace workflows
- reporting automation
- KYC verification and review
- operations dashboard and escalation flow
- analytics and snapshot generation
- vendor management and quotations
- more granular audit APIs

## Phase 3
Enterprise APIs:
- cross-tenant access control
- complex reporting and analytics pipelines
- large-scale integration events
- event-driven orchestration
- advanced role and permissions model
- secret rotation and external ecosystem APIs

---

# API Completion Sequence

## Sprint 1
Focus:
- auth and session APIs
- customer profile and owner APIs
- base error model and shared schema
- common pagination/filter/sort conventions
- health and readiness endpoints

Deliverables:
- auth endpoints
- user/profile API set
- shared schema library
- API governance and validation rules

---

## Sprint 2
Focus:
- property and document APIs
- service request lifecycle
- ownership verification and status APIs

Deliverables:
- property CRUD and status flows
- ownership workflow APIs
- service request create/update/history
- document upload and management

---

## Sprint 3
Focus:
- visit, evidence, notification, and complaint APIs
- subscription basic lifecycle
- initial payment flow

Deliverables:
- visit management APIs
- evidence upload and preview APIs
- complaint flow
- notification delivery status
- subscription plans and purchase endpoints

---

## Sprint 4
Focus:
- advanced payments, reports, KYC, admin operations
- marketplace and agent workflows
- webhook contract completion

Deliverables:
- payment checkout/refund/webhook APIs
- report generation and export APIs
- KYC review and approval APIs
- admin user and config endpoints
- webhook integration set

---

## Sprint 5
Focus:
- QA hardening, traceability, performance, production readiness
- edge-case handling and contract maturity

Deliverables:
- full API contract validation
- final consistency review
- OpenAPI lint and generation checks
- production sign-off checklist

---

# Risks

Risk | Impact | Mitigation | Owner
---|---|---|---
Incomplete schema definitions | broken frontend/backend integration | enforce schema completion checklist before merge | API Architecture
Inconsistent naming conventions | duplicated or conflicting resources | enforce naming policy and contract linting | Backend Engineering
Weak pagination/filter conventions | fragmented client behavior | standardize at global API layer | API Architecture
Missing webhook validation | integration failure or security issues | define HMAC and retry rules | Platform / Integration
Inadequate idempotency handling | duplicate payments or actions | enforce Idempotency-Key on critical endpoints | Payment Engineering
Unauthorized access gaps | data leak and compliance risk | enforce RBAC and permission mapping in contracts | Security
Large response payloads | poor frontend performance | restrict fields and add pagination defaults | Architecture
Missing traceability link | untested or undocumented requirements | require API-to-requirement mapping in review | Product / QA
Unclear state transitions | broken workflow logic | define explicit enums and transition rules | Backend Domain Leads
Late API change request | sprint slippage | freeze API contract milestones with review gates | Engineering Manager

---

# Success Criteria

The OpenAPI contract is considered complete when it fully supports:
- backend generation
- frontend integration
- testing
- production deployment

Specifically:
- all major screens have corresponding APIs
- all core database entities have API routes
- all business workflows have explicit request/response models
- error and validation contracts are standardized
- authentication and authorization are enforced consistently
- pagination, filtering, and sorting patterns are uniform
- payment, subscription, and report APIs are contract-complete
- webhooks are defined and signed
- traceability matrix is complete and validated
- OpenAPI linting and generated client builds succeed
- test contracts are ready for backend and frontend integration

---

# Final Recommendation

The PropertyPilot OpenAPI specification should be treated as a production contract, not merely a developer convenience. The highest-value work is to complete API coverage for workflow-heavy domains, standardize cross-cutting governance, and align the contract with the database model and screen flows.

The recommended overall approach is:
- complete shared contract standards first
- implement core domain APIs in a stable sequence
- add workflow-specific endpoints next
- finish payments, reports, KYC, admin, and marketplace APIs as required for MVP
- validate all APIs against traceability, screen flows, and database entities
- enforce OpenAPI generation and contract tests as release gates

This produces a stable API layer suitable for backend generation, frontend consumption, and production deployment across the PropertyPilot platform.
```// filepath: c:\PropertyPilot\docs\OpenAPI_Completion_Plan.md
# OpenAPI Completion Plan

Document Type: API Contract Completion Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: API Architecture / Backend Engineering / Product / QA

---

# Purpose

This document transforms the current PropertyPilot OpenAPI specification into a complete production-ready API contract that aligns with the screen catalog, user journeys, database model, business requirements, and cross-cutting platform constraints.

The objective is to:
- close schema and endpoint gaps
- align APIs with the canonical data model
- standardize request/response contracts
- define governance conventions for versioning, pagination, filtering, and error handling
- complete the API layer required for backend generation, frontend integration, testing, and production deployment

This plan covers the full business and operational surface of PropertyPilot, including customer, agent, operations, admin, marketplace, KYC, reporting, payments, subscriptions, notifications, and audit flows.

---

# Current State Assessment

## Existing APIs
The current OpenAPI document already indicates a base set of operational API domains, including likely coverage for:
- authentication and session flows
- customer profile and onboarding
- property lifecycle
- service request operations
- billing and subscription basics
- notification-related communications
- admin and operations actions
- complaint and support flows

These APIs provide a good platform foundation, but they are not yet complete enough for full production readiness across all modules.

## Missing APIs
The main missing APIs are in the following areas:
- property proof/verification workflows
- evidence upload and file metadata APIs
- visit scheduling, execution, and GPS tracking
- payment webhook processing and reconciliation
- marketplace vendor and quotations flow
- KYC submission review and approval flow
- report generation and export lifecycle
- notification delivery status and retries
- outbox/event-driven processing APIs
- cross-role operations and admin audit interactions
- privileged configuration and feature flag APIs

## Partially Defined APIs
The following areas likely exist but are not yet fully consistent:
- customer CRUD and profile endpoints
- property CRUD and ownership workflows
- service request create/update/history lifecycle
- complaint create/track/resolve flows
- notification status endpoints
- admin configuration APIs
- report generation routes
- payment and subscription state management

Likely issues:
- missing status transitions
- inconsistent error contracts
- incomplete pagination contracts
- missing model examples and field definitions
- weak object storage metadata contracts
- incomplete field-level validation metadata

## Duplicate APIs
Likely duplication risk between modules:
- user/profile retrieval endpoints across customer and admin routes
- service request endpoints across customer, operations, and agent with overlapping semantics
- notification endpoints for customer and admin actions
- payment invoice and refund endpoints may be fragmented across modules
- report and dashboard API routes may overlap with analytics endpoints

## Conflicting APIs
The main conflicts to resolve:
- different naming conventions for resource lifecycle endpoints
- inconsistent status naming across customer, agent, and admin flows
- differing ownership and authorization semantics for same resources
- inconsistency between database and API entity names
- diverse response envelope standards across endpoints
- variation in filter and sort semantics between modules

---

# API Governance Alignment

## Versioning Strategy
Use URL-based versioning for major contract changes:
- /api/v1/...
- future major version increments require explicit migration path

Rules:
- minor changes must remain backward compatible
- breaking changes must be introduced in a new version
- deprecate old endpoints with sunset period
- document API version in response headers when appropriate

## Base Path Standard
Consistent path conventions:
- /api/v1/auth
- /api/v1/customers
- /api/v1/properties
- /api/v1/services
- /api/v1/visits
- /api/v1/payments
- /api/v1/subscriptions
- /api/v1/complaints
- /api/v1/notifications
- /api/v1/marketplace
- /api/v1/admin
- /api/v1/operations
- /api/v1/kyc
- /api/v1/reports

Each resource group must have:
- consistent plural nouns
- CRUD style actions for core entities
- workflow-specific routes for state transitions

## Error Standards
All endpoints must use a standard error envelope:
- code
- message
- details
- traceId
- timestamp
- fieldErrors where applicable

Recommended error types:
- validation_error
- unauthorized
- forbidden
- not_found
- conflict
- rate_limited
- timeout
- dependency_failure
- internal_error

## Pagination Standards
Standardize query parameters:
- page
- pageSize
- sortBy
- sortOrder
- filterBy
- search

Response shape:
- items
- page
- pageSize
- totalItems
- totalPages
- hasNextPage

## Filtering Standards
Use consistent filter semantics:
- eq
- ne
- in
- contains
- gt
- gte
- lt
- lte
- between
- startsWith
- endsWith

Examples:
- /properties?status=ACTIVE&city=Hyderabad
- /service-requests?assignedTo=agent-123&status=IN_PROGRESS

## Sorting Standards
- sortBy is a field name from schema
- sortOrder is asc or desc
- default sort defined on each endpoint
- prohibit ad hoc sort over unsupported fields
- do not allow free-form sort expressions unless explicitly documented

## Idempotency Standards
Critical for:
- payment initiation
- subscription purchase
- complaint creation
- report generation
- refunds and cross-system callbacks

Rules:
- require Idempotency-Key header on safe non-idempotent operations
- store idempotency records for a configured retention window
- reject duplicate requests with same key and same payload

## Webhook Standards
All webhooks must:
- use HMAC signature verification
- include eventId, eventType, occurredAt, resourceId
- verify payload version
- support retry with exponential backoff
- expose a webhook replay endpoint where required

## Authentication Standards
Use JWT-based auth with:
- access token
- refresh token
- token expiry metadata
- role and permission claims
- refresh endpoint with rotation
- reject expired or invalid tokens consistently

---

# Domain API Completion Roadmap

## Authentication

### Login
Required APIs:
- POST /api/v1/auth/login
- POST /api/v1/auth/otp/request
- POST /api/v1/auth/otp/verify
- POST /api/v1/auth/token/refresh
- POST /api/v1/auth/logout
- GET /api/v1/auth/me
- GET /api/v1/auth/permissions

Required models:
- LoginRequest
- OtpRequest
- OtpVerifyRequest
- TokenRefreshRequest
- AuthUserProfile
- AuthTokenResponse

### OTP
- request OTP by mobile/email
- validate OTP
- support resend lock and expiry
- audit OTP attempts and failed validations

### Token Refresh
- rotate refresh tokens
- reject reused tokens
- return new access token with same principal metadata

### Logout
- invalidate server-side session or token state
- revoke refresh token
- remove client-side app state

### Role APIs
- GET /api/v1/auth/roles
- GET /api/v1/auth/roles/{roleId}/permissions

---

## Customer

### CRUD
- GET /api/v1/customers/{id}
- POST /api/v1/customers
- PUT /api/v1/customers/{id}
- PATCH /api/v1/customers/{id}
- DELETE /api/v1/customers/{id} (soft delete based on policy)

### Profile
- GET /api/v1/customers/me/profile
- PUT /api/v1/customers/me/profile
- PATCH /api/v1/customers/me/profile

### Addresses
- GET /api/v1/customers/me/addresses
- POST /api/v1/customers/me/addresses
- PUT /api/v1/customers/me/addresses/{id}
- DELETE /api/v1/customers/me/addresses/{id}

### Preferences
- GET /api/v1/customers/me/preferences
- PUT /api/v1/customers/me/preferences

---

## Property

### CRUD
- GET /api/v1/properties
- GET /api/v1/properties/{id}
- POST /api/v1/properties
- PUT /api/v1/properties/{id}
- PATCH /api/v1/properties/{id}
- DELETE /api/v1/properties/{id}

### Documents
- POST /api/v1/properties/{id}/documents
- GET /api/v1/properties/{id}/documents
- DELETE /api/v1/properties/{id}/documents/{documentId}

### Ownership
- GET /api/v1/properties/{id}/ownership
- POST /api/v1/properties/{id}/ownership/verify
- GET /api/v1/properties/{id}/ownership/history

### Monitoring
- GET /api/v1/properties/{id}/status-history
- PATCH /api/v1/properties/{id}/status
- GET /api/v1/properties/verification-queue

---

## Service Requests

### Create
- POST /api/v1/service-requests

### Track
- GET /api/v1/service-requests/{id}
- GET /api/v1/service-requests/{id}/history
- GET /api/v1/service-requests/my

### Cancel
- PATCH /api/v1/service-requests/{id}/cancel

### Reschedule
- PATCH /api/v1/service-requests/{id}/reschedule

### History
- GET /api/v1/service-requests/{id}/timeline
- GET /api/v1/service-requests/{id}/assignments

---

## Visits

### Scheduling
- POST /api/v1/visits
- GET /api/v1/visits/{id}
- GET /api/v1/visits
- PATCH /api/v1/visits/{id}/schedule

### Execution
- PATCH /api/v1/visits/{id}/start
- PATCH /api/v1/visits/{id}/complete
- PATCH /api/v1/visits/{id}/cancel

### Evidence
- POST /api/v1/visits/{id}/evidence
- GET /api/v1/visits/{id}/evidence
- DELETE /api/v1/visits/{id}/evidence/{evidenceId}

### GPS
- POST /api/v1/visits/{id}/gps/checkin
- POST /api/v1/visits/{id}/gps/checkout
- GET /api/v1/visits/{id}/gps

---

## Reports

### Generate
- POST /api/v1/reports
- POST /api/v1/reports/generate
- GET /api/v1/reports/{id}

### Download
- GET /api/v1/reports/{id}/download
- GET /api/v1/reports/{id}/export

### Evidence Viewer
- GET /api/v1/evidence/{id}
- GET /api/v1/evidence/{id}/preview
- GET /api/v1/evidence/{id}/download

---

## Subscription

### Plans
- GET /api/v1/subscriptions/plans
- GET /api/v1/subscriptions/plans/{planId}

### Purchase
- POST /api/v1/subscriptions/purchase
- GET /api/v1/subscriptions/my

### Renewal
- POST /api/v1/subscriptions/{id}/renew

### Upgrade
- POST /api/v1/subscriptions/{id}/upgrade

### Downgrade
- POST /api/v1/subscriptions/{id}/downgrade

### Pause
- POST /api/v1/subscriptions/{id}/pause

### Cancel
- POST /api/v1/subscriptions/{id}/cancel

### Grace Period
- GET /api/v1/subscriptions/{id}/grace-period
- POST /api/v1/subscriptions/{id}/grace-period/resolve

---

## Payments

### Checkout
- POST /api/v1/payments/checkout
- GET /api/v1/payments/{id}

### Webhook
- POST /api/v1/payments/webhooks/provider
- GET /api/v1/payments/webhooks/verify
- POST /api/v1/payments/webhooks/replay

### Refund
- POST /api/v1/payments/{id}/refund
- GET /api/v1/payments/{id}/refunds

### Invoice
- GET /api/v1/invoices/{id}
- GET /api/v1/invoices/my
- POST /api/v1/invoices/{id}/download

### Reconciliation
- GET /api/v1/payments/reconciliation
- POST /api/v1/payments/reconciliation/run
- GET /api/v1/payments/reconciliation/{id}

---

## Complaints

### Create
- POST /api/v1/complaints

### Track
- GET /api/v1/complaints/{id}
- GET /api/v1/complaints/my

### Resolve
- PATCH /api/v1/complaints/{id}/resolve

### Escalate
- PATCH /api/v1/complaints/{id}/escalate

---

## Notifications

### Email
- POST /api/v1/notifications/email
- GET /api/v1/notifications/email/status

### SMS
- POST /api/v1/notifications/sms
- GET /api/v1/notifications/sms/status

### WhatsApp
- POST /api/v1/notifications/whatsapp
- GET /api/v1/notifications/whatsapp/status

### Push
- POST /api/v1/notifications/push
- GET /api/v1/notifications/push/status

### Delivery Status
- GET /api/v1/notifications/{id}/delivery-status
- GET /api/v1/notifications/delivery-summary

---

## Marketplace

### Vendors
- GET /api/v1/marketplace/vendors
- GET /api/v1/marketplace/vendors/{id}
- POST /api/v1/marketplace/vendors

### Quotations
- POST /api/v1/marketplace/quotations
- GET /api/v1/marketplace/quotations/{id}
- GET /api/v1/marketplace/quotations/my

### Assignments
- POST /api/v1/marketplace/assignments
- GET /api/v1/marketplace/assignments/{id}

---

## Admin

### User Management
- GET /api/v1/admin/users
- GET /api/v1/admin/users/{id}
- POST /api/v1/admin/users
- PATCH /api/v1/admin/users/{id}
- DELETE /api/v1/admin/users/{id}

### Pricing
- GET /api/v1/admin/pricing/plans
- POST /api/v1/admin/pricing/plans
- PUT /api/v1/admin/pricing/plans/{id}

### Configuration
- GET /api/v1/admin/config
- PUT /api/v1/admin/config/{key}
- POST /api/v1/admin/feature-flags

### Audit
- GET /api/v1/admin/audit
- GET /api/v1/admin/audit/{id}

---

## Operations

### Assignment
- GET /api/v1/operations/assignments
- POST /api/v1/operations/assignments
- PATCH /api/v1/operations/assignments/{id}

### Monitoring
- GET /api/v1/operations/dashboard
- GET /api/v1/operations/queues
- GET /api/v1/operations/workload

### Escalation
- POST /api/v1/operations/escalations
- GET /api/v1/operations/escalations
- PATCH /api/v1/operations/escalations/{id}

---

## KYC

### Submission
- POST /api/v1/kyc/submissions
- GET /api/v1/kyc/submissions/{id}

### Review
- GET /api/v1/kyc/review-queue
- POST /api/v1/kyc/submissions/{id}/review

### Approval
- POST /api/v1/kyc/submissions/{id}/approve

### Rejection
- POST /api/v1/kyc/submissions/{id}/reject

### Reverification
- POST /api/v1/kyc/submissions/{id}/reverify

---

# Request/Response Model Backlog

## Missing DTOs
Need to define DTOs for:
- UserSession
- RefreshToken
- CustomerProfile
- PropertyOwnership
- ServiceRequestAssignment
- VisitExecution
- ComplaintThreadMessage
- PaymentGatewayResponse
- SubscriptionLifecycleEvent
- ReportJobStatus
- KYCVerificationSubmission
- NotificationDeliveryState
- MarketplaceAssignment
- AdminAuditEntry

## Missing Schemas
Missing schemas likely include:
- ErrorResponse
- PaginationResponse
- FilterCriteria
- SortCriteria
- FileUploadMetadata
- EvidenceMetadata
- GeoPoint
- PaymentStatus
- ServiceStatus
- ComplaintStatus
- NotificationChannel
- MarketplaceStatus
- KYCStatus
- ReportExportStatus

## Missing Enums
Need completion for:
- UserRole
- PropertyStatus
- PropertyVerificationStatus
- ServiceRequestStatus
- VisitStatus
- PaymentStatus
- SubscriptionStatus
- ComplaintStatus
- NotificationChannel
- NotificationStatus
- KYCStatus
- ReportStatus
- AssignmentStatus
- EscalationStatus

## Missing Error Models
Define standard error models:
- ApiError
- ValidationError
- FieldValidationError
- ResourceNotFoundError
- ConflictError
- ForbiddenError
- RateLimitError
- DependencyError
- TimeoutError

---

# Webhook Contract Backlog

## Payment Webhooks
- payment.created
- payment.updated
- payment.failed
- payment.refunded
- invoice.generated
- invoice.updated

Required fields:
- eventId
- eventType
- occurredAt
- provider
- status
- paymentId
- amount
- currency
- metadata

## Notification Webhooks
- notification.sent
- notification.delivered
- notification.failed
- notification.read
- notification.clicked

## Subscription Webhooks
- subscription.created
- subscription.renewed
- subscription.expired
- subscription.cancelled
- subscription.upgraded
- subscription.downgraded

## Marketplace Webhooks
- quotation.created
- quotation.accepted
- assignment.completed
- listing.published
- listing.archived

---

# Security Alignment

## JWT
- access token claims must include subject, role, tenant context, permissions
- token expiry must be validated centrally
- refresh tokens should rotate and be stored safely
- token validation middleware should enforce policies consistently

## RBAC
Define roles:
- customer
- agent
- operations
- admin
- nri
- system_service
- support_user

## Scopes
Define permission scopes for:
- customer read/write
- agent task operations
- operations assignment control
- admin configuration
- report download access
- notification management
- payment initiation and refund review

## Permissions
Explicit permission model required for:
- property read/write
- service request update
- visit evidence upload
- KYC review
- payment refund
- admin config change
- report generation

---

# API Traceability Matrix

The API traceability matrix should explicitly map:
- requirements to API endpoints
- features to endpoint groups
- screens to APIs
- database entities to API resources

Recommended traceability categories:
- requirement ID
- feature ID
- screen ID
- API path
- resource name
- schema name
- database entity
- authorization requirement
- test coverage status

Examples:
- SRS-Auth-001 -> POST /api/v1/auth/login
- SRS-Property-013 -> GET /api/v1/properties/{id}
- Screen-C06 -> GET /api/v1/customers/me/dashboard
- DB-Property-001 -> properties table
- DB-Payment-003 -> payments table

This matrix must be enforced in PR reviews and release validation.

---

# MVP vs Future APIs

## Phase 1
Core MVP APIs:
- auth and OTP
- customer profile and onboarding
- property create/read/update
- service request create/track
- visit scheduling and evidence
- basic payment flow and invoice retrieval
- complaint create/track
- notification send and read
- admin basic user and config management

## Phase 2
Expansion APIs:
- advanced marketplace workflows
- reporting automation
- KYC verification and review
- operations dashboard and escalation flow
- analytics and snapshot generation
- vendor management and quotations
- more granular audit APIs

## Phase 3
Enterprise APIs:
- cross-tenant access control
- complex reporting and analytics pipelines
- large-scale integration events
- event-driven orchestration
- advanced role and permissions model
- secret rotation and external ecosystem APIs

---

# API Completion Sequence

## Sprint 1
Focus:
- auth and session APIs
- customer profile and owner APIs
- base error model and shared schema
- common pagination/filter/sort conventions
- health and readiness endpoints

Deliverables:
- auth endpoints
- user/profile API set
- shared schema library
- API governance and validation rules

---

## Sprint 2
Focus:
- property and document APIs
- service request lifecycle
- ownership verification and status APIs

Deliverables:
- property CRUD and status flows
- ownership workflow APIs
- service request create/update/history
- document upload and management

---

## Sprint 3
Focus:
- visit, evidence, notification, and complaint APIs
- subscription basic lifecycle
- initial payment flow

Deliverables:
- visit management APIs
- evidence upload and preview APIs
- complaint flow
- notification delivery status
- subscription plans and purchase endpoints

---

## Sprint 4
Focus:
- advanced payments, reports, KYC, admin operations
- marketplace and agent workflows
- webhook contract completion

Deliverables:
- payment checkout/refund/webhook APIs
- report generation and export APIs
- KYC review and approval APIs
- admin user and config endpoints
- webhook integration set

---

## Sprint 5
Focus:
- QA hardening, traceability, performance, production readiness
- edge-case handling and contract maturity

Deliverables:
- full API contract validation
- final consistency review
- OpenAPI lint and generation checks
- production sign-off checklist

---

# Risks

Risk | Impact | Mitigation | Owner
---|---|---|---
Incomplete schema definitions | broken frontend/backend integration | enforce schema completion checklist before merge | API Architecture
Inconsistent naming conventions | duplicated or conflicting resources | enforce naming policy and contract linting | Backend Engineering
Weak pagination/filter conventions | fragmented client behavior | standardize at global API layer | API Architecture
Missing webhook validation | integration failure or security issues | define HMAC and retry rules | Platform / Integration
Inadequate idempotency handling | duplicate payments or actions | enforce Idempotency-Key on critical endpoints | Payment Engineering
Unauthorized access gaps | data leak and compliance risk | enforce RBAC and permission mapping in contracts | Security
Large response payloads | poor frontend performance | restrict fields and add pagination defaults | Architecture
Missing traceability link | untested or undocumented requirements | require API-to-requirement mapping in review | Product / QA
Unclear state transitions | broken workflow logic | define explicit enums and transition rules | Backend Domain Leads
Late API change request | sprint slippage | freeze API contract milestones with review gates | Engineering Manager

---

# Success Criteria

The OpenAPI contract is considered complete when it fully supports:
- backend generation
- frontend integration
- testing
- production deployment

Specifically:
- all major screens have corresponding APIs
- all core database entities have API routes
- all business workflows have explicit request/response models
- error and validation contracts are standardized
- authentication and authorization are enforced consistently
- pagination, filtering, and sorting patterns are uniform
- payment, subscription, and report APIs are contract-complete
- webhooks are defined and signed
- traceability matrix is complete and validated
- OpenAPI linting and generated client builds succeed
- test contracts are ready for backend and frontend integration

---

# Final Recommendation

The PropertyPilot OpenAPI specification should be treated as a production contract, not merely a developer convenience. The highest-value work is to complete API coverage for workflow-heavy domains, standardize cross-cutting governance, and align the contract with the database model and screen flows.

The recommended overall approach is:
- complete shared contract standards first
- implement core domain APIs in a stable sequence
- add workflow-specific endpoints next
- finish payments, reports, KYC, admin, and marketplace APIs as required for MVP
- validate all APIs against traceability, screen flows, and database entities
- enforce OpenAPI generation and contract tests as release gates

This produces a stable API layer suitable for backend generation, frontend consumption, and production deployment across the PropertyPilot platform.
