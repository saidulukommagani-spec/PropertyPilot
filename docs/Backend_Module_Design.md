# Backend Module Design

Document Type: Backend Design Specification  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Engineering Architecture

---

## 1. Purpose

This document defines the backend module design for PropertyPilot. It translates the requirements, architecture, API contract, and data model into implementation-ready service boundaries, persistent structures, events, and security constraints.

The design covers modules required for the MVP and the initial production-supportable release:
- Customer Module
- Property Module
- Service Module
- Subscription Module
- Payment Module
- Agent Module
- Report Module
- Notification Module
- Authentication Module

This design is intended to be authoritative for backend implementation planning and service decomposition.

---

## 2. High-Level Backend Architecture

### Core Principles
- Each module owns its domain model and service logic.
- Business logic is separated from API transport and integration concerns.
- Persistence is model-driven and aligned with Database_Physical_Model.md.
- Event-driven communication is used for asynchronous actions and notifications.
- Security is enforced at the service and API layer.
- All modules support audit logging, traceability, and failure handling.

### Backend Components
- API Gateway / Edge Layer
- Authentication Service
- Customer Service
- Property Service
- Service Request Service
- Subscription Service
- Payment Service
- Agent Service
- Notification Service
- Reporting Service
- Audit / Event Bus
- Data Access Layer
- External Integrations Layer

### Cross-Cutting Concerns
- Authentication and authorization
- Request correlation IDs
- Observability and metrics
- Audit trail and compliance logging
- Retry, timeout, and idempotency handling
- Rate limiting and abuse prevention
- Feature-level configuration and tenant scoping

---

## 3. Shared Backend Standards

### Service Contracts
- All modules expose REST APIs per OpenAPI_Specification.yaml.
- Request/response contracts are versioned.
- All mutations are idempotent where required.
- All APIs validate authn/authz and business rules before persistence.

### Data Standards
- All tables use UUID or surrogate keys where required.
- Created/updated timestamps are mandatory.
- Audit fields are mandatory for customer-, payment-, and security-sensitive records.
- Soft delete is used for operational data retention; hard delete is restricted.
- External provider data is maintained separately from canonical domain records.

### Event Standards
- Event payloads include event_id, entity_id, tenant_id, actor_id, correlation_id, timestamp.
- Events are emitted for create/update/delete, assignment changes, payment status changes, notifications, and operational transitions.
- Event consumers are resilient and use retry logic.

### Security Standards
- All API calls must require authenticated identity.
- Role-based access control is enforced by module.
- Sensitive data is encrypted at rest and in transit.
- Audit logs capture who changed what and why.
- Payment data and KYC records are protected with stricter access and retention controls.

---

# 4. Module Design

## Module 1: Authentication Module

### Responsibilities
- User registration and login orchestration
- OTP generation and verification
- Session creation and token lifecycle management
- Role and permission resolution
- Passwordless login support for customer and agent flows
- Security event capture for login failures or suspicious behavior

### APIs
- POST /auth/customers/register
- POST /auth/customers/otp/send
- POST /auth/customers/otp/verify
- POST /auth/customers/login
- POST /auth/logout
- GET /auth/me
- POST /auth/refresh
- POST /auth/agents/login
- POST /auth/admin/login
- POST /auth/agents/otp/send
- POST /auth/agents/otp/verify

### Database Tables
- users
- user_profiles
- user_sessions
- otp_verifications
- user_roles
- user_permissions
- audit_logs

### Events
- UserRegistered
- LoginSucceeded
- LoginFailed
- OTPGenerated
- OTPVerified
- SessionExpired
- LogoutPerformed
- RoleAssigned

### Dependencies
- Customer Module for profile linking
- Agent Module for role mapping
- Notification Module for OTP delivery
- Security and auditing components

### Security Rules
- OTP must expire within a short validity window
- Login attempts are rate-limited
- Session tokens are short-lived and refreshable
- Sensitive credentials are never stored in plain text
- Admin and operations roles require elevated authorization
- Failed OTP attempts and suspicious login patterns are logged

---

## Module 2: Customer Module

### Responsibilities
- Customer profile lifecycle
- Account status and onboarding state
- Customer preference management
- Customer complaint intake and tracking
- Customer support linkage
- Customer-specific privacy and consent management

### APIs
- POST /customers
- GET /customers/{customerId}
- PUT /customers/{customerId}
- GET /customers/{customerId}/profile
- PATCH /customers/{customerId}/preferences
- POST /customers/{customerId}/complaints
- GET /customers/{customerId}/complaints
- GET /customers/{customerId}/notifications
- POST /customers/{customerId}/privacy/requests

### Database Tables
- customers
- customer_profiles
- customer_preferences
- customer_complaints
- customer_privacy_requests
- customer_documents
- customer_audit_logs

### Events
- CustomerCreated
- CustomerUpdated
- CustomerProfileCompleted
- ComplaintCreated
- ComplaintUpdated
- PrivacyRequestCreated
- CustomerDeactivated

### Dependencies
- Authentication Module
- Notification Module
- Support workflow components
- Property Module for ownership relations
- Payment Module for billing history
- Subscription Module for plan linkage

### Security Rules
- Only authenticated customer or authorized admin may read/update own profile
- Customer complaint data must be restricted to service/support staff roles
- Sensitive identity and KYC data require elevated access controls
- PII access is logged and audited
- Consent and privacy actions are immutable and traceable

---

## Module 3: Property Module

### Responsibilities
- Property registration and lifecycle management
- Ownership verification and relation to customer
- Property metadata, address, and location validation
- Property listing and retrieval
- GPS verification and validation state
- Property updates and status transitions

### APIs
- POST /properties
- GET /properties/{propertyId}
- PUT /properties/{propertyId}
- GET /customers/{customerId}/properties
- POST /properties/{propertyId}/verify/gps
- POST /properties/{propertyId}/verify/ownership
- PATCH /properties/{propertyId}/status
- GET /properties/search

### Database Tables
- properties
- property_owners
- property_verification
- property_documents
- property_addresses
- property_status_history
- property_gps_checks
- property_events

### Events
- PropertyRegistered
- PropertyUpdated
- PropertyVerified
- GPSVerificationPassed
- GPSVerificationFailed
- OwnershipVerified
- PropertyStatusChanged

### Dependencies
- Customer Module
- Authentication Module
- Service Module for property-linked service requests
- Notification Module to notify status changes
- External Maps service for verification

### Security Rules
- Property data is accessible to owner, assigned operations users, and authorized admin roles
- Ownership verification details require restricted access
- GPS-related data is treated as sensitive location metadata
- Update actions require validation and audit logging
- Unauthorized access to another customer’s property is denied

---

## Module 4: Service Module

### Responsibilities
- Service catalog management
- Service request creation and lifecycle
- Booking validation and scheduling
- Service status transitions
- Evidence upload and viewing
- Workflow support for customer and agent journeys

### APIs
- GET /services
- GET /services/{serviceId}
- POST /service-requests
- GET /service-requests/{requestId}
- PUT /service-requests/{requestId}
- PATCH /service-requests/{requestId}/status
- POST /service-requests/{requestId}/evidence
- GET /service-requests/{requestId}/evidence
- GET /customers/{customerId}/service-requests
- GET /agents/{agentId}/assignments

### Database Tables
- services
- service_categories
- service_requests
- service_request_status_history
- service_request_evidence
- service_request_assignments
- service_request_events
- booking_slots

### Events
- ServiceRequested
- ServiceBookingCreated
- ServiceStatusUpdated
- AssignmentCreated
- EvidenceUploaded
- ServiceCompleted
- ServiceCancelled
- ServiceEscalated

### Dependencies
- Customer Module
- Property Module
- Agent Module
- Notification Module
- Payment Module
- Storage Integration
- GPS verification upstream

### Security Rules
- Customers can only access their own service requests
- Agents can access assigned requests only
- Operations/admin users may access broader request sets as required
- Evidence files require access controls and audit trails
- Sensitive status transitions require validation and actor tracking

---

## Module 5: Subscription Module

### Responsibilities
- Plan definition and lifecycle
- Subscription creation and activation
- Renewal, pause, resume, and cancellation operations
- Pricing or plan linkage
- Billing state tracking
- Subscription status management

### APIs
- GET /subscriptions/plans
- POST /subscriptions
- GET /subscriptions/{subscriptionId}
- PATCH /subscriptions/{subscriptionId}
- POST /subscriptions/{subscriptionId}/cancel
- POST /subscriptions/{subscriptionId}/pause
- POST /subscriptions/{subscriptionId}/resume
- GET /customers/{customerId}/subscriptions

### Database Tables
- subscription_plans
- subscriptions
- subscription_status_history
- subscription_billing_cycles
- subscription_events

### Events
- SubscriptionCreated
- SubscriptionActivated
- SubscriptionRenewed
- SubscriptionPaused
- SubscriptionResumed
- SubscriptionCancelled
- BillingCycleGenerated

### Dependencies
- Customer Module
- Payment Module
- Notification Module
- Reporting Module

### Security Rules
- Subscription information is customer-specific and restricted
- Billing and renewal actions require stronger authorization
- Admin changes are logged and must be auditable
- Sensitive payment-linked state changes require validation and traceability

---

## Module 6: Payment Module

### Responsibilities
- Payment initiation and validation
- Invoice and receipt generation
- Payment status lifecycle
- Refund and reversal support
- Gateway integration and transaction reconciliation
- Payment failure handling and retry tracking

### APIs
- POST /payments/initiate
- GET /payments/{paymentId}
- POST /payments/{paymentId}/confirm
- POST /payments/{paymentId}/refund
- GET /customers/{customerId}/payments
- GET /invoices/{invoiceId}
- POST /payments/reconcile

### Database Tables
- payments
- payment_transactions
- payment_status_history
- invoices
- refunds
- payment_provider_accounts
- payment_reconciliation_records

### Events
- PaymentInitiated
- PaymentSucceeded
- PaymentFailed
- PaymentExpired
- PaymentRefundRequested
- PaymentRefundProcessed
- InvoiceGenerated
- ReconciliationCompleted

### Dependencies
- Subscription Module
- Service Module
- Customer Module
- Notification Module
- External Payment Gateway
- Reporting Module

### Security Rules
- Payment data is restricted to authorized operations and billing users
- Payment provider secrets remain externalized and never stored in application tables
- All payment state transitions are logged
- Payment confirmation requires provider validation and idempotency keys
- Refund operations require policy checks and authorization

---

## Module 7: Agent Module

### Responsibilities
- Agent account management
- Agent assignment queue
- Service assignment lifecycle
- Visit execution and task handling
- Agent availability and capacity tracking
- Field activity state tracking

### APIs
- POST /agents
- GET /agents/{agentId}
- PUT /agents/{agentId}
- GET /agents/available
- POST /service-requests/{requestId}/assign
- PATCH /service-requests/{requestId}/assignments/{assignmentId}
- GET /agents/{agentId}/assignments
- PATCH /agents/{agentId}/availability

### Database Tables
- agents
- agent_profiles
- agent_availability
- service_request_assignments
- assignment_history
- agent_visit_logs

### Events
- AgentCreated
- AgentAvailable
- AgentAssigned
- AssignmentUpdated
- VisitStarted
- VisitCompleted
- AgentUnavailable

### Dependencies
- Authentication Module
- Service Module
- Property Module
- Notification Module
- Reporting Module

### Security Rules
- Agent data is restricted to authorized operations and assigned service users
- Agents can only access assigned tasks and their own profile
- Assignment changes require audit logging and authorization
- Location data associated with visit execution is sensitive and access-controlled

---

## Module 8: Report Module

### Responsibilities
- Operational reporting
- KPI aggregation
- Service, payment, subscription, and activity summaries
- Support and admin dashboards
- Data export for management and monitoring

### APIs
- GET /reports/summary
- GET /reports/service-requests
- GET /reports/payments
- GET /reports/subscriptions
- GET /reports/agents
- GET /reports/customers
- POST /reports/export

### Database Tables
- report_templates
- report_jobs
- report_exports
- report_metrics_cache
- report_access_logs

### Events
- ReportGenerated
- ReportExportRequested
- ReportExportCompleted
- ReportExportFailed

### Dependencies
- Customer Module
- Service Module
- Payment Module
- Subscription Module
- Agent Module
- Notification Module

### Security Rules
- Reporting access is role-based and scoped to authorized users
- Exported reports must respect data access permissions
- Sensitive customer and payment data must be redacted or restricted as required
- Report generation must be auditable

---

## Module 9: Notification Module

### Responsibilities
- Send OTP, booking, assignment, payment, and summary notifications
- Support SMS, email, and WhatsApp channels
- Manage notification templates and preferences
- Track delivery status and retries
- Support event-driven communication

### APIs
- POST /notifications/send
- GET /notifications/{notificationId}
- GET /customers/{customerId}/notifications
- PATCH /notifications/{notificationId}/status
- POST /notifications/templates
- GET /notifications/preferences

### Database Tables
- notifications
- notification_templates
- notification_channels
- notification_status_history
- notification_preferences
- notification_delivery_attempts

### Events
- NotificationQueued
- NotificationDelivered
- NotificationFailed
- NotificationRetried
- NotificationTemplateUpdated

### Dependencies
- Authentication Module
- Customer Module
- Service Module
- Payment Module
- Agent Module
- External SMS, Email, and WhatsApp providers

### Security Rules
- Notification templates must be reviewed and approved before use in production
- Sensitive customer identifiers must be masked in logs and errors
- Notification channels must respect customer preferences and consent
- Retry and failure paths are tracked and auditable
- Do not send sensitive or confidential content through unapproved channels

---

# 5. Cross-Module Events

These events form the inter-module event backbone for the MVP:

- UserRegistered
- OTPGenerated
- OTPVerified
- LoginSucceeded
- PropertyRegistered
- PropertyVerified
- ServiceRequested
- ServiceBookingCreated
- ServiceStatusUpdated
- AssignmentCreated
- PaymentInitiated
- PaymentSucceeded
- PaymentFailed
- SubscriptionCreated
- SubscriptionActivated
- NotificationQueued
- NotificationDelivered
- ComplaintCreated
- ReportGenerated

These events should be emitted through a central event bus or message broker and consumed by modules requiring downstream processing.

---

# 6. Shared Data Model Expectations

## Common Tables
- audit_logs
- event_logs
- correlation_context
- tenant_context
- system_configuration
- user_activity_logs

## Common Fields
- id
- created_at
- updated_at
- created_by
- updated_by
- deleted_at
- tenant_id
- version
- status
- correlation_id

---

# 7. Security Model by Module

| Module | Security Controls |
|---|---|
| Authentication | OTP, session tokens, rate limiting, audit logs |
| Customer | PII access control, consent tracking, complaint confidentiality |
| Property | ownership validation, secure location metadata, restricted access |
| Service | role-based request access, evidence protection, status validation |
| Subscription | billing authorization, auditable plan changes |
| Payment | secure gateway handling, policy validation, idempotency |
| Agent | assignment authorization, visit access control, sensitive location handling |
| Report | role-based export controls, fixed report access rules |
| Notification | consent-based delivery, template validation, masked logs |

---

# 8. Dependency Map

| Module | Depends On |
|---|---|
| Authentication | Customer, Notification, Security, Session Infrastructure |
| Customer | Authentication, Notification, Property, Subscription |
| Property | Customer, Maps, Authentication |
| Service | Property, Customer, Agent, Payment, Storage |
| Subscription | Customer, Payment, Notification |
| Payment | Subscription, Service, Customer, Gateway |
| Agent | Authentication, Service, Notification |
| Report | Service, Customer, Payment, Subscription, Agent |
| Notification | Authentication, Customer, Payment, Service, Agent |

---

# 9. Service Interfaces Summary

| Module | Primary Interface |
|---|---|
| Authentication | Auth Service |
| Customer | Customer Service |
| Property | Property Service |
| Service | Service Request Service |
| Subscription | Subscription Service |
| Payment | Payment Service |
| Agent | Agent Service |
| Report | Reporting Service |
| Notification | Notification Service |

---

# 10. Implementation Notes

## Backend Principles
- Domain modules are autonomous but integrate through event-driven patterns and API contracts.
- All module APIs should support the same request correlation and error schema.
- Shared validation logic for status, role, and access should be centralized.
- Payment and KYC-related operations must remain stricter than standard customer-facing flows.

## Readiness Constraints
- This design assumes the approved MVP scope and database model remain stable.
- Any new module or expanded field not in the canonical model must be approved before implementation.
- Additional operational and support flows should be treated as extension work, not blocking MVP implementation.

---

## Summary

This backend module design defines the service-oriented structure required to support the PropertyPilot MVP. It aligns domain responsibilities, API contracts, persistent model usage, events, dependencies, and security controls across all core modules.

This design supports:
- customer onboarding and secure access
- property registration and verification
- service booking and lifecycle tracking
- payment and subscription flow
- agent assignment and operational execution
- notifications and reporting
- secure and auditable backend operations

The design is intended to be used as the implementation foundation for the next engineering iteration and should remain aligned with the canonical architecture, SRS, API contract, and physical database schema.