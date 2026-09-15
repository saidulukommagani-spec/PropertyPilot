````markdown
# Backend Implementation Roadmap

Document Type: Backend Engineering Roadmap  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Engineering Architecture / Platform

---

# Objectives

The backend roadmap is designed to deliver the PropertyPilot MVP in a controlled, traceable sequence while minimizing implementation risk and architectural drift.

Primary objectives:
- Implement a secure, role-based backend foundation for customer, agent, operations, and admin flows
- Deliver the core MVP user journeys in a dependency-safe order
- Align backend implementation to the approved physical schema and API contract
- Support event-driven integration and operational visibility
- Ensure production-readiness criteria are met before release readiness
- Keep implementation anchored to the approved sprint backlog and traceability model

---

# Technology Stack

Recommended implementation stack:
- Language/runtime: .NET 8 / ASP.NET Core
- API style: REST with OpenAPI specification alignment
- Data persistence: PostgreSQL
- Cache/session: Redis
- Messaging/event bus: RabbitMQ or Azure Service Bus
- File storage: MinIO / Azure Blob Storage / S3-compatible storage
- Identity/security: JWT, OTP service, role-based access control
- Background jobs: Hangfire / hosted background workers
- Observability: OpenTelemetry, structured logs, Prometheus/Grafana
- Validation: FluentValidation / centralized request validation
- ORM: Entity Framework Core
- API documentation: OpenAPI 3.1 / Swagger
- Deployment model: containerized services via Docker + orchestrated environment
- CI/CD: Azure DevOps / GitHub Actions with quality gates

---

# Module Dependencies

## Core dependency model
- Authentication Module is foundational to all customer, agent, admin, and operations access
- Customer Module is foundational to property, subscription, and complaint flows
- Property Module enables booking, service requests, and verification workflows
- Service Module drives the core request lifecycle and operational queue
- Payment and Subscription Modules depend on customer and service states
- Agent Module depends on Service Module and Assignment workflows
- Notification Module is event-driven and listens to user lifecycle and request events
- Report and Analytics Modules depend on service, payment, customer, and operational data
- Admin and Operations Modules depend on authorization and auditing infrastructure
- Audit Module is cross-cutting and must be active for all sensitive changes

---

# Implementation Sequence

## Phase 1: Foundation and Core User Access
1. Database foundation
2. Authentication Module
3. Customer Module
4. Property Module
5. Core API contracts and validation

## Phase 2: Service Execution
1. Service Module
2. Agent Module
3. GPS Module
4. Evidence Module
5. Notification Module

## Phase 3: Commercial and Monitoring
1. Subscription Module
2. Payment Module
3. Report Module
4. Analytics Module

## Phase 4: Operational Governance
1. Operations Module
2. Admin Module
3. Audit Logs
4. System Configuration
5. RBAC hardening and release validation

---

# Phase 1

## 1. Authentication Module

### Purpose
Provide secure user identity, OTP verification, session creation, and access enforcement for customers, agents, operations users, and administrators.

### Tables
- users
- user_profiles
- user_sessions
- otp_verifications
- user_roles
- user_permissions
- audit_logs

### APIs
- POST /auth/customers/register
- POST /auth/customers/otp/send
- POST /auth/customers/otp/verify
- POST /auth/customers/login
- POST /auth/logout
- POST /auth/refresh
- GET /auth/me
- POST /auth/agents/login
- POST /auth/agents/otp/send
- POST /auth/agents/otp/verify
- POST /auth/admin/login

### Events
- UserRegistered
- OtpGenerated
- OtpVerified
- LoginSucceeded
- LoginFailed
- SessionExpired
- LogoutPerformed
- RoleAssigned

### Dependencies
- Database Foundation
- Notification Module for OTP delivery
- Customer Module for profile linking
- Agent Module for agent access mapping

### Security Requirements
- JWT with short-lived access tokens and refresh tokens
- OTP expiration and retry limits
- role-based restriction on admin and operations access
- rate limiting and lockout protection
- passwordless auth only; no plaintext storage of credentials
- full audit of auth events and failed attempts

### Testing Requirements
- unit tests for token generation and validation
- OTP lifecycle tests
- negative tests for invalid OTP, expiration, retry limits
- role tests for customer, agent, admin access
- integration tests for login and session refresh

### Definition of Done
- customer and agent auth flows pass end-to-end
- OTP sending and verification are complete
- session refresh and logout behave correctly
- RBAC is enforced at API layer
- audit logs capture auth events

### Critical Path Analysis
This is the critical path dependency for all other backend modules. No user-facing modules can safely proceed without the auth framework.

### Risks
- OTP spoofing or abuse
- token theft or session hijacking
- untracked account states

### Mitigations
- use short-lived tokens and refresh rotation
- enforce rate limiting and device/session tracking
- add hardened security policies and login auditing

### Implementation Timeline
- Week 1: user schema, auth service, OTP flow
- Week 2: token handling, refresh logic, admin and agent auth

---

## 2. Customer Module

### Purpose
Manage customer profile lifecycle, identity validation, preferences, privacy, and support-related interactions.

### Tables
- customers
- customer_profiles
- customer_preferences
- customer_complaints
- customer_privacy_requests
- customer_documents
- customer_audit_logs

### APIs
- POST /customers
- GET /customers/{customerId}
- PUT /customers/{customerId}
- GET /customers/{customerId}/profile
- PATCH /customers/{customerId}/preferences
- GET /customers/{customerId}/complaints
- POST /customers/{customerId}/complaints
- GET /customers/{customerId}/notifications

### Events
- CustomerCreated
- CustomerUpdated
- CustomerProfileCompleted
- ComplaintCreated
- ComplaintUpdated
- PrivacyRequestCreated

### Dependencies
- Authentication Module
- Notification Module
- Property Module
- Subscription Module

### Security Requirements
- customer can read/update only own profile
- support/admin roles require explicit authorization
- PII access is auditable
- export and privacy requests are restricted and logged

### Testing Requirements
- validation tests for customer creation
- profile update tests
- complaint creation and access tests
- access control tests for customer vs admin

### Definition of Done
- customer account and profile lifecycle is functional
- complaint and preference APIs are stable
- access restrictions are enforced
- customer data is audit-protected

### Critical Path Analysis
Customer data drives onboarding, service booking, property ownership, and support workflows.

### Risks
- duplicate customer registration
- poor privacy handling
- exposure of PII

### Mitigations
- unique constraint on phone/email
- consent tracking and privacy request processing
- strict route-based authorization and auditing

### Implementation Timeline
- Week 1: customer model and onboarding flow
- Week 2: profile/preferences/complaint flow

---

## 3. Property Module

### Purpose
Manage property registration, validation, ownership relationships, and GPS verification.

### Tables
- properties
- property_owners
- property_addresses
- property_verification
- property_documents
- property_status_history
- property_gps_checks

### APIs
- POST /properties
- GET /properties/{propertyId}
- PUT /properties/{propertyId}
- GET /customers/{customerId}/properties
- POST /properties/{propertyId}/verify/gps
- POST /properties/{propertyId}/verify/ownership
- PATCH /properties/{propertyId}/status
- GET /properties/search

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
- Service Module
- Notification Module
- External location verification service

### Security Requirements
- only owner and authorized support/admin roles can access property record
- GPS data is protected as sensitive location metadata
- ownership checks require elevated audit trail
- document uploads require secure access and retention policies

### Testing Requirements
- property create/update tests
- GPS validation success/failure tests
- ownership verification flow tests
- access control tests across owner and admin roles

### Definition of Done
- property registration and retrieval are complete
- GPS and ownership verification flows pass
- property status history is retained
- unauthorized property access is blocked

### Critical Path Analysis
This module is required before booking and service execution can proceed reliably.

### Risks
- invalid property ownership records
- duplicate property entries
- GPS and location accuracy failures

### Mitigations
- unique key strategy by owner + address
- validation of GPS and ownership state transitions
- strict ownership checks and admin review

### Implementation Timeline
- Week 1: property model and lifecycle
- Week 2: verification flows and status history

---

## 4. Database Foundation

### Purpose
Create the underlying schema, constraints, indexes, relationships, and migration tooling needed to support all modules.

### Tables
All foundational tables:
- users
- customers
- properties
- services
- service_requests
- payments
- subscriptions
- agents
- notifications
- audit_logs

### APIs
- migration runner
- schema versioning endpoints if exposed for internal ops
- admin health check or migration status endpoints

### Events
- SchemaMigrated
- MigrationFailed
- MigrationRollbackStarted
- MigrationRolledBack

### Dependencies
- Platform infrastructure
- CI/CD and migration tooling
- Environment provisioning

### Security Requirements
- database credentials stored in secure secret management
- migration access restricted to platform administrators
- no direct production mutation outside controlled deploy pipeline
- backup and rollback procedures documented and tested

### Testing Requirements
- migration dry-run and validation
- rollback verification
- index and FK validation
- data integrity tests
- load/performance checks for key tables

### Definition of Done
- schema is deployed and validated
- all key constraints and indexes exist
- migration rollback is tested
- no unresolved blocker tables remain in core MVP scope

### Critical Path Analysis
The database is the single most important dependency for all downstream engineering work.

### Risks
- schema drift
- incomplete constraints
- migration failure during release
- degraded query performance

### Mitigations
- migration sequencing with validation gates
- use least-privilege deployment architecture
- finalize schema backlog before sprint expansion
- build performance benchmarks for queue and reporting queries

### Implementation Timeline
- Week 1: foundational schema and key constraints
- Week 2: index stabilization and validation

---

# Phase 2

## 5. Service Module

### Purpose
Manage services and customer booking lifecycle, including creation, status transitions, and task execution.

### Tables
- services
- service_categories
- service_requests
- service_request_status_history
- service_request_assignments
- service_request_evidence
- booking_slots

### APIs
- GET /services
- GET /services/{serviceId}
- POST /service-requests
- GET /service-requests/{requestId}
- GET /customers/{customerId}/service-requests
- PATCH /service-requests/{requestId}/status
- GET /service-requests/{requestId}/history
- POST /service-requests/{requestId}/evidence

### Events
- ServiceRequested
- ServiceBookingCreated
- ServiceStatusUpdated
- AssignmentCreated
- EvidenceUploaded
- ServiceCompleted
- ServiceCancelled

### Dependencies
- Property Module
- Customer Module
- Agent Module
- Payment Module
- Notification Module

### Security Requirements
- customer access only to own requests
- agent access only to assigned requests
- support/admin access is role-scoped
- evidence upload requires authorization and file validation
- lifecycle status changes must be auditable

### Testing Requirements
- service catalog tests
- booking creation tests
- state transition tests
- invalid status transition tests
- access control and multi-role tests

### Definition of Done
- service booking and lifecycle are working
- status transitions are validated and recorded
- evidence and assignment linkage works
- customer visibility is enabled

### Critical Path Analysis
This module is the operational backbone of the product and the main business execution engine.

### Risks
- workflow gaps between backend and UI
- invalid status transitions
- inconsistent request visibility

### Mitigations
- enforce strict state machine validation
- publish clear event contract
- trace request updates with history table

### Implementation Timeline
- Week 1: service catalog and request model
- Week 2: lifecycle, status, evidence integration

---

## 6. Agent Module

### Purpose
Support field agents, assignment handling, availability tracking, and task execution updates.

### Tables
- agents
- agent_profiles
- agent_availability
- agent_visit_logs
- service_request_assignments
- assignment_history

### APIs
- POST /agents
- GET /agents/{agentId}
- PUT /agents/{agentId}
- GET /agents/available
- GET /agents/{agentId}/assignments
- PATCH /agents/{agentId}/availability
- POST /service-requests/{requestId}/assign
- PATCH /service-requests/{requestId}/assignments/{assignmentId}

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
- Notification Module
- Operations Module

### Security Requirements
- agent can only access assigned tasks and own profile
- assignment changes require audit logging
- sensitive location data must be protected
- support/admin overrides require explicit authorization

### Testing Requirements
- agent login and availability tests
- assignment and reassignment tests
- visit workflow and state transition tests
- role-based access validation

### Definition of Done
- agent dashboard and assignment flow work end-to-end
- availability is tracked and operationally useful
- reassignments are controlled and logged

### Critical Path Analysis
This module directly supports field execution and operations productivity.

### Risks
- assignment drift
- invalid queue state
- poor agent visibility

### Mitigations
- enforce assignment ownership and valid state machine
- keep assignment changes auditable
- validate agent availability before assignment

### Implementation Timeline
- Week 1: agent schema and assignment workflow
- Week 2: availability and execution integration

---

## 7. GPS Module

### Purpose
Provide GPS validation and geolocation context for property verification and service execution.

### Tables
- property_gps_checks
- service_request_gps_events
- gps_validation_logs

### APIs
- POST /properties/{propertyId}/verify/gps
- POST /service-requests/{requestId}/gps/validate
- GET /service-requests/{requestId}/gps

### Events
- GPSVerificationPassed
- GPSVerificationFailed
- LocationCaptured
- LocationValidationFailed

### Dependencies
- Property Module
- Service Module
- Agent Module

### Security Requirements
- geolocation data access must be limited to authorized roles
- GPS data should not be exposed broadly
- validation failure and success are logged
- all geolocation retrieval is time-limited and role-scoped

### Testing Requirements
- GPS validation success/failure
- property verification tests
- service request validation tests
- integration with field app location capture

### Definition of Done
- GPS acquisition and verification flow works for the required scenarios
- invalid and missing coordinates are handled
- data is captured and traceable

### Critical Path Analysis
GPS is a required validation gate for many service flows and is a key dependency for booking confidence.

### Risks
- poor accuracy leading to service failures
- invalid capture states
- inconsistent device/gateway data

### Mitigations
- validate coordinate ranges and accuracy thresholds
- provide retry and manual review fallbacks
- log failures for operational review

### Implementation Timeline
- Week 1: coordinate validation and storage
- Week 2: integration with booking and execution flows

---

## 8. Evidence Module

### Purpose
Store and retrieve customer/agent evidence attached to service requests and property verification activities.

### Tables
- service_request_evidence
- property_documents
- evidence_access_logs
- evidence_metadata

### APIs
- POST /service-requests/{requestId}/evidence
- GET /service-requests/{requestId}/evidence
- POST /properties/{propertyId}/documents
- GET /properties/{propertyId}/documents

### Events
- EvidenceUploaded
- EvidenceApproved
- EvidenceRejected
- EvidenceDeleted

### Dependencies
- Service Module
- Property Module
- Notification Module
- Storage backend

### Security Requirements
- file type and size checks
- secure object storage and signed URLs
- access is restricted to request owner, assigned agent, and authorized operations/admin
- evidence retention policy enforced

### Testing Requirements
- upload success and failure tests
- file type validation tests
- access control tests
- storage integration and cleanup tests

### Definition of Done
- upload and retrieval work for approved file types
- evidence is linked to the correct workflow
- storage is secure and logged
- invalid uploads are rejected gracefully

### Critical Path Analysis
Evidence is required for operational proof and dispute resolution.

### Risks
- malformed or oversized uploads
- unauthorized access
- storage sprawl or retention issues

### Mitigations
- validate uploads at API layer and storage layer
- use signed access and object-level ACLs
- enforce retention policy and cleanup jobs

### Implementation Timeline
- Week 1: storage integration and schema
- Week 2: upload API and retrieval governance

---

# Phase 3

## 9. Report Module

### Purpose
Provide all core operational report generation and report job handling for message, payment, tasks, and subscription views.

### Tables
- report_templates
- report_jobs
- report_exports
- report_metrics_cache

### APIs
- GET /reports/summary
- GET /reports/service-requests
- GET /reports/payments
- GET /reports/subscriptions
- GET /reports/agents
- GET /reports/customers
- POST /reports/export

### Events
- ReportGenerated
- ReportExportRequested
- ReportExportCompleted
- ReportExportFailed

### Dependencies
- Service Module
- Payment Module
- Subscription Module
- Agent Module
- Customer Module
- Notification Module

### Security Requirements
- role-based report visibility
- export access must be restricted
- sensitive fields should be masked depending on role
- report generation actions should be auditable

### Testing Requirements
- report generation tests
- filter validation
- export workflow tests
- role-based access tests

### Definition of Done
- core reports are generated and consistent with source data
- report jobs are tracked and exportable
- results are available to authorized users

### Critical Path Analysis
Reporting provides visibility for operations and release quality, and supports many governance workflows.

### Risks
- stale or inconsistent numbers
- slow performance on large datasets
- unauthorized export leakage

### Mitigations
- design read-optimized reporting queries
- use report cache and job scheduler
- enforce export restrictions and audit logs

### Implementation Timeline
- Week 1: summary and data aggregation
- Week 2: export, filters, and role restrictions

---

## 10. Subscription Module

### Purpose
Manage subscription plans, lifecycle state, renewal windows, and customer billing relationship.

### Tables
- subscription_plans
- subscriptions
- subscription_status_history
- billing_cycles
- subscription_events

### APIs
- GET /subscriptions/plans
- POST /subscriptions
- GET /subscriptions/{subscriptionId}
- PATCH /subscriptions/{subscriptionId}
- POST /subscriptions/{subscriptionId}/pause
- POST /subscriptions/{subscriptionId}/resume
- POST /subscriptions/{subscriptionId}/cancel
- GET /customers/{customerId}/subscriptions

### Events
- SubscriptionCreated
- SubscriptionActivated
- SubscriptionRenewed
- SubscriptionPaused
- SubscriptionResumed
- SubscriptionCancelled

### Dependencies
- Customer Module
- Payment Module
- Notification Module

### Security Requirements
- only customer or authorized admin/support roles can read subscription state
- lifecycle actions follow policy validation
- cancellations and pauses are logged and auditable
- renewal and billing rules require strict approval logic

### Testing Requirements
- plan fetch and selection tests
- lifecycle transitions tests
- invalid transition tests
- permissions and access tests

### Definition of Done
- plans and subscriptions are consistent and lifecycle-aware
- customer and support views are functional
- changes are audited and rescue-safe

### Critical Path Analysis
This module is essential for recurring billing and customer relationship continuity.

### Risks
- invalid lifecycle transitions
- duplicate or orphaned subscriptions
- incorrect billing cycles

### Mitigations
- policy-driven state machine checks
- validated subscription uniqueness per customer and plan
- audit trail for each lifecycle event

### Implementation Timeline
- Week 1: plan and subscription model
- Week 2: lifecycle actions and customer visibility

---

## 11. Notification Module

### Purpose
Send lifecycle updates to customers and internal operations via SMS, email, or message channel workflows.

### Tables
- notifications
- notification_templates
- notification_channels
- notification_preferences
- notification_delivery_attempts

### APIs
- POST /notifications/send
- GET /customers/{customerId}/notifications
- GET /notifications/{notificationId}
- PATCH /notifications/{notificationId}/status
- GET /notifications/preferences
- POST /notifications/preferences

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
- External provider integrations

### Security Requirements
- customer preferences and consent are enforced
- only approved templates may be used
- notification content must be sanitized
- channel routing and retry logging must be secure and auditable

### Testing Requirements
- template validation tests
- notification event triggers
- failure and retry tests
- customer preference tests

### Definition of Done
- notifications fire for key lifecycle events
- customer inbox and read state work
- alerts are secure and preference-aware

### Critical Path Analysis
Notifications support both customer communication and business signal flow, making them essential for successful product adoption.

### Risks
- spam or unwanted communication
- delivery failures
- privacy or consent violations

### Mitigations
- consent tracking and preference checks
- dedicated provider adapter with retry logic
- audit and suppression controls

### Implementation Timeline
- Week 1: preferences and templates
- Week 2: event-driven delivery and inbox flow

---

## 12. Payment Module

### Purpose
Process payments, track payment status, reconcile gateway responses, and maintain invoice and refund workflows.

### Tables
- payments
- payment_transactions
- payment_status_history
- invoices
- refunds
- payment_provider_accounts

### APIs
- POST /payments/initiate
- GET /payments/{paymentId}
- POST /payments/{paymentId}/confirm
- POST /payments/{paymentId}/refund
- GET /customers/{customerId}/payments
- GET /invoices/{invoiceId}
- POST /payments/reconcile

### Events
- PaymentInitiated
- PaymentSucceeded
- PaymentFailed
- PaymentExpired
- PaymentRefundRequested
- PaymentRefundProcessed
- InvoiceGenerated

### Dependencies
- Service Module
- Subscription Module
- Customer Module
- Notification Module
- External payment gateway

### Security Requirements
- provider secrets stored in secret manager
- payment operations require strong auth and audit controls
- idempotency keys enforced
- refund and capture actions restricted by policy

### Testing Requirements
- successful payment flow tests
- failed payment validation tests
- refund workflow tests
- gateway timeout and retry tests
- reconciliation tests

### Definition of Done
- payment initiation and confirmation work
- invoices and receipts are consistent
- refund flows are controlled and auditable

### Critical Path Analysis
Payment and transaction reliability are essential for service confirmation and subscription lifecycle.

### Risks
- duplicate or fraudulent charges
- payment gateway drift
- poor reconciliation

### Mitigations
- use idempotency keys and provider references
- restrict payment actions to allowed roles
- implement reconciliation jobs and alerting

### Implementation Timeline
- Week 1: payment model and gateway integration
- Week 2: refund and invoice support

---

# Phase 4

## 13. Operations Module

### Purpose
Provide operational oversight for queue routing, escalations, and service health monitoring.

### Tables
- operations_queues
- operations_alerts
- operations_queue_assignments
- operations_metrics

### APIs
- GET /operations/dashboard
- GET /operations/queue
- PATCH /operations/queue/{requestId}/route
- PATCH /operations/queue/{requestId}/escalate
- GET /operations/alerts

### Events
- QueueReviewed
- RequestEscalated
- AssignmentRouted
- AlertTriggered

### Dependencies
- Service Module
- Agent Module
- Report Module
- Audit Module

### Security Requirements
- access limited to operations/admin roles
- queue actions must be auditable
- escalation routes need validation and approval rules
- data visibility must match role scope

### Testing Requirements
- queue and escalation tests
- route validation tests
- unauthorized access tests
- dashboard loading tests

### Definition of Done
- queue management works for live service operations
- escalations are tracked and visible
- only valid roles can change queue state

### Critical Path Analysis
Operations oversight is required for field-level execution, backlog triage, and service continuity.

### Risks
- operation queue inconsistencies
- over-assignment of resources
- poor exception handling

### Mitigations
- use queue-state validation
- enforce alerts and assignment policies
- maintain audit logs for all route and escalation actions

### Implementation Timeline
- Week 1: queue model and dashboard
- Week 2: escalation logic and operational visibility

---

## 14. Admin Module

### Purpose
Provide administrative controls for user management, role assignment, platform policies, and secure system administration.

### Tables
- admin_users
- user_roles
- user_permissions
- admin_audit_events
- admin_settings

### APIs
- GET /admin/users
- POST /admin/users
- PATCH /admin/users/{userId}/role
- PATCH /admin/users/{userId}/status
- GET /admin/permissions
- POST /admin/roles
- PATCH /admin/roles/{roleId}

### Events
- UserRoleModified
- UserStatusChanged
- RoleCreated
- PermissionAssigned
- AdminActionPerformed

### Dependencies
- Authentication Module
- Audit Module
- System Configuration Module

### Security Requirements
- admin-only access
- role changes must be validated against authorization matrix
- sensitive changes must be logged
- least privilege enforcement mandatory

### Testing Requirements
- role assignment tests
- user status tests
- permissions validation
- admin access control tests

### Definition of Done
- admin features are stable and role-protected
- user and role management works
- security-sensitive actions are logged and validated

### Critical Path Analysis
Admin privileges influence the platform’s governance posture and must be tightly controlled.

### Risks
- privilege escalation
- broken role mappings
- lack of auditability

### Mitigations
- hard RBAC enforcement
- role matrix review
- require audit log on all admin changes

### Implementation Timeline
- Week 1: admin landing screens and API wiring
- Week 2: role matrix and permissions enforcement

---

## 15. Analytics Module

### Purpose
Aggregate operational and business metrics to drive dashboards and trend analysis.

### Tables
- analytics_kpis
- analytics_metrics
- analytics_views
- analytics_snapshots

### APIs
- GET /analytics/overview
- GET /analytics/service-trends
- GET /analytics/payment-trends
- GET /analytics/agent-performance
- GET /analytics/customer-usage

### Events
- AnalyticsSnapshotCreated
- AnalyticsMetricUpdated
- AnalyticsJobCompleted

### Dependencies
- Report Module
- Service Module
- Payment Module
- Subscription Module
- Agent Module

### Security Requirements
- access restricted to authorized leadership, ops, and admin roles
- no exposure of raw sensitive records through analytics
- snapshot generation is auditable

### Testing Requirements
- metric correctness tests
- trend validation tests
- access restriction tests
- performance tests for dashboard queries

### Definition of Done
- dashboard metrics are consistent and reliable
- aggregated data reflects source data accurately
- role-based permissions are enforced

### Critical Path Analysis
Analytics turns operational data into management insight and is required for decision-making and release readiness.

### Risks
- misleading metrics
- stale metrics
- poor performance

### Mitigations
- explicit metric definitions and source mapping
- query optimization and caching
- governance and dashboard validation before release

### Implementation Timeline
- Week 1: metric definitions and baseline aggregation
- Week 2: dashboard consumption and release validation

---

# Critical Path Analysis

Critical dependency chain:
1. Database Foundation
2. Authentication Module
3. Customer Module
4. Property Module
5. Service Module
6. Agent Module
7. GPS Module
8. Evidence Module
9. Notification Module
10. Payment and Subscription Modules
11. Report and Analytics Modules
12. Operations and Admin Modules
13. Audit and Configuration governance

Any failure in the early path delays all downstream work. The most critical items are:
- secure auth and RBAC
- database schema completeness
- core service lifecycle
- assignment and queue logic
- payment and subscription integrity
- admin and operations governance controls

---

# Risks and Mitigations

## Risk 1: Schema drift
Mitigation:
- finalize schema backlog and lock migration sequencing
- no broad implementation without valid migration gate
- require migration validation as part of every build

## Risk 2: Authentication and authorization gaps
Mitigation:
- enforce RBAC at API layer and route guard layer
- add test coverage for role mismatch and unauthorized access
- require strict audit logging for critical auth changes

## Risk 3: Payment and subscription inconsistencies
Mitigation:
- centralize payment state machine
- enforce idempotency and state transition validation
- require reconciliation and status reporting before production release

## Risk 4: Assignment and queue misalignment
Mitigation:
- implement assignment state machine and queue indexing
- validate agent availability and route restrictions
- test assignment flow under concurrent operations

## Risk 5: Reporting inaccuracy
Mitigation:
- use defined metric sources and mapping
- validate individual report queries to source transactions
- conduct reporting QA before signoff

## Risk 6: Security-sensitive admin controls
Mitigation:
- narrow admin routes and require MFA or enhanced auth if applicable
- enact audit log and approval prior to config changes
- restrict access by role plus environment

---

# Implementation Timeline

## Week 1-2: Foundation and Core User Experience
- database schema completion
- auth, customer, and property modules
- initial service request model
- basic validation and API contract alignment

## Week 3-4: Service Execution
- service and agent modules
- GPS and evidence modules
- notification events
- trial workflow end-to-end

## Week 5-6: Commercial and Monitoring
- payment and subscription modules
- reporting and analytics
- operational and customer status visibility

## Week 7-8: Governance and Release Readiness
- operations portal
- admin portal
- role management
- audit logs
- system configuration
- final security and production readiness testing

---

# Definition of Done for the Roadmap

The backend implementation roadmap is complete when:
- all required MVP modules are implemented in dependency order
- each module has defined tables, APIs, events, dependencies, security, and testing
- the implementation sequence aligns with production-readiness and release safety
- the team can execute Sprint 1, Sprint 2, and Sprint 3 backlog items without drift
- schema, API, and UI contracts remain aligned
- audit and security controls are active on all privileged flows
- production deployment approval criteria are supported by the completed backend stack

---

# Summary

This roadmap defines the backend implementation sequence for PropertyPilot in a way that is secure, scalable, traceable, and aligned to the MVP scope. It prioritizes foundational architecture, user access, service execution, and governance controls in a measured order to reduce implementation risk and increase the likelihood of successful release readiness.

The backend work should proceed in dependency order, with schema and auth completion treated as gating prerequisites for all downstream implementation.
```// filepath: c:\PropertyPilot\docs\Backend_Implementation_Roadmap.md
# Backend Implementation Roadmap

Document Type: Backend Engineering Roadmap  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Engineering Architecture / Platform

---

# Objectives

The backend roadmap is designed to deliver the PropertyPilot MVP in a controlled, traceable sequence while minimizing implementation risk and architectural drift.

Primary objectives:
- Implement a secure, role-based backend foundation for customer, agent, operations, and admin flows
- Deliver the core MVP user journeys in a dependency-safe order
- Align backend implementation to the approved physical schema and API contract
- Support event-driven integration and operational visibility
- Ensure production-readiness criteria are met before release readiness
- Keep implementation anchored to the approved sprint backlog and traceability model

---

# Technology Stack

Recommended implementation stack:
- Language/runtime: .NET 8 / ASP.NET Core
- API style: REST with OpenAPI specification alignment
- Data persistence: PostgreSQL
- Cache/session: Redis
- Messaging/event bus: RabbitMQ or Azure Service Bus
- File storage: MinIO / Azure Blob Storage / S3-compatible storage
- Identity/security: JWT, OTP service, role-based access control
- Background jobs: Hangfire / hosted background workers
- Observability: OpenTelemetry, structured logs, Prometheus/Grafana
- Validation: FluentValidation / centralized request validation
- ORM: Entity Framework Core
- API documentation: OpenAPI 3.1 / Swagger
- Deployment model: containerized services via Docker + orchestrated environment
- CI/CD: Azure DevOps / GitHub Actions with quality gates

---

# Module Dependencies

## Core dependency model
- Authentication Module is foundational to all customer, agent, admin, and operations access
- Customer Module is foundational to property, subscription, and complaint flows
- Property Module enables booking, service requests, and verification workflows
- Service Module drives the core request lifecycle and operational queue
- Payment and Subscription Modules depend on customer and service states
- Agent Module depends on Service Module and Assignment workflows
- Notification Module is event-driven and listens to user lifecycle and request events
- Report and Analytics Modules depend on service, payment, customer, and operational data
- Admin and Operations Modules depend on authorization and auditing infrastructure
- Audit Module is cross-cutting and must be active for all sensitive changes

---

# Implementation Sequence

## Phase 1: Foundation and Core User Access
1. Database foundation
2. Authentication Module
3. Customer Module
4. Property Module
5. Core API contracts and validation

## Phase 2: Service Execution
1. Service Module
2. Agent Module
3. GPS Module
4. Evidence Module
5. Notification Module

## Phase 3: Commercial and Monitoring
1. Subscription Module
2. Payment Module
3. Report Module
4. Analytics Module

## Phase 4: Operational Governance
1. Operations Module
2. Admin Module
3. Audit Logs
4. System Configuration
5. RBAC hardening and release validation

---

# Phase 1

## 1. Authentication Module

### Purpose
Provide secure user identity, OTP verification, session creation, and access enforcement for customers, agents, operations users, and administrators.

### Tables
- users
- user_profiles
- user_sessions
- otp_verifications
- user_roles
- user_permissions
- audit_logs

### APIs
- POST /auth/customers/register
- POST /auth/customers/otp/send
- POST /auth/customers/otp/verify
- POST /auth/customers/login
- POST /auth/logout
- POST /auth/refresh
- GET /auth/me
- POST /auth/agents/login
- POST /auth/agents/otp/send
- POST /auth/agents/otp/verify
- POST /auth/admin/login

### Events
- UserRegistered
- OtpGenerated
- OtpVerified
- LoginSucceeded
- LoginFailed
- SessionExpired
- LogoutPerformed
- RoleAssigned

### Dependencies
- Database Foundation
- Notification Module for OTP delivery
- Customer Module for profile linking
- Agent Module for agent access mapping

### Security Requirements
- JWT with short-lived access tokens and refresh tokens
- OTP expiration and retry limits
- role-based restriction on admin and operations access
- rate limiting and lockout protection
- passwordless auth only; no plaintext storage of credentials
- full audit of auth events and failed attempts

### Testing Requirements
- unit tests for token generation and validation
- OTP lifecycle tests
- negative tests for invalid OTP, expiration, retry limits
- role tests for customer, agent, admin access
- integration tests for login and session refresh

### Definition of Done
- customer and agent auth flows pass end-to-end
- OTP sending and verification are complete
- session refresh and logout behave correctly
- RBAC is enforced at API layer
- audit logs capture auth events

### Critical Path Analysis
This is the critical path dependency for all other backend modules. No user-facing modules can safely proceed without the auth framework.

### Risks
- OTP spoofing or abuse
- token theft or session hijacking
- untracked account states

### Mitigations
- use short-lived tokens and refresh rotation
- enforce rate limiting and device/session tracking
- add hardened security policies and login auditing

### Implementation Timeline
- Week 1: user schema, auth service, OTP flow
- Week 2: token handling, refresh logic, admin and agent auth

---

## 2. Customer Module

### Purpose
Manage customer profile lifecycle, identity validation, preferences, privacy, and support-related interactions.

### Tables
- customers
- customer_profiles
- customer_preferences
- customer_complaints
- customer_privacy_requests
- customer_documents
- customer_audit_logs

### APIs
- POST /customers
- GET /customers/{customerId}
- PUT /customers/{customerId}
- GET /customers/{customerId}/profile
- PATCH /customers/{customerId}/preferences
- GET /customers/{customerId}/complaints
- POST /customers/{customerId}/complaints
- GET /customers/{customerId}/notifications

### Events
- CustomerCreated
- CustomerUpdated
- CustomerProfileCompleted
- ComplaintCreated
- ComplaintUpdated
- PrivacyRequestCreated

### Dependencies
- Authentication Module
- Notification Module
- Property Module
- Subscription Module

### Security Requirements
- customer can read/update only own profile
- support/admin roles require explicit authorization
- PII access is auditable
- export and privacy requests are restricted and logged

### Testing Requirements
- validation tests for customer creation
- profile update tests
- complaint creation and access tests
- access control tests for customer vs admin

### Definition of Done
- customer account and profile lifecycle is functional
- complaint and preference APIs are stable
- access restrictions are enforced
- customer data is audit-protected

### Critical Path Analysis
Customer data drives onboarding, service booking, property ownership, and support workflows.

### Risks
- duplicate customer registration
- poor privacy handling
- exposure of PII

### Mitigations
- unique constraint on phone/email
- consent tracking and privacy request processing
- strict route-based authorization and auditing

### Implementation Timeline
- Week 1: customer model and onboarding flow
- Week 2: profile/preferences/complaint flow

---

## 3. Property Module

### Purpose
Manage property registration, validation, ownership relationships, and GPS verification.

### Tables
- properties
- property_owners
- property_addresses
- property_verification
- property_documents
- property_status_history
- property_gps_checks

### APIs
- POST /properties
- GET /properties/{propertyId}
- PUT /properties/{propertyId}
- GET /customers/{customerId}/properties
- POST /properties/{propertyId}/verify/gps
- POST /properties/{propertyId}/verify/ownership
- PATCH /properties/{propertyId}/status
- GET /properties/search

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
- Service Module
- Notification Module
- External location verification service

### Security Requirements
- only owner and authorized support/admin roles can access property record
- GPS data is protected as sensitive location metadata
- ownership checks require elevated audit trail
- document uploads require secure access and retention policies

### Testing Requirements
- property create/update tests
- GPS validation success/failure tests
- ownership verification flow tests
- access control tests across owner and admin roles

### Definition of Done
- property registration and retrieval are complete
- GPS and ownership verification flows pass
- property status history is retained
- unauthorized property access is blocked

### Critical Path Analysis
This module is required before booking and service execution can proceed reliably.

### Risks
- invalid property ownership records
- duplicate property entries
- GPS and location accuracy failures

### Mitigations
- unique key strategy by owner + address
- validation of GPS and ownership state transitions
- strict ownership checks and admin review

### Implementation Timeline
- Week 1: property model and lifecycle
- Week 2: verification flows and status history

---

## 4. Database Foundation

### Purpose
Create the underlying schema, constraints, indexes, relationships, and migration tooling needed to support all modules.

### Tables
All foundational tables:
- users
- customers
- properties
- services
- service_requests
- payments
- subscriptions
- agents
- notifications
- audit_logs

### APIs
- migration runner
- schema versioning endpoints if exposed for internal ops
- admin health check or migration status endpoints

### Events
- SchemaMigrated
- MigrationFailed
- MigrationRollbackStarted
- MigrationRolledBack

### Dependencies
- Platform infrastructure
- CI/CD and migration tooling
- Environment provisioning

### Security Requirements
- database credentials stored in secure secret management
- migration access restricted to platform administrators
- no direct production mutation outside controlled deploy pipeline
- backup and rollback procedures documented and tested

### Testing Requirements
- migration dry-run and validation
- rollback verification
- index and FK validation
- data integrity tests
- load/performance checks for key tables

### Definition of Done
- schema is deployed and validated
- all key constraints and indexes exist
- migration rollback is tested
- no unresolved blocker tables remain in core MVP scope

### Critical Path Analysis
The database is the single most important dependency for all downstream engineering work.

### Risks
- schema drift
- incomplete constraints
- migration failure during release
- degraded query performance

### Mitigations
- migration sequencing with validation gates
- use least-privilege deployment architecture
- finalize schema backlog before sprint expansion
- build performance benchmarks for queue and reporting queries

### Implementation Timeline
- Week 1: foundational schema and key constraints
- Week 2: index stabilization and validation

---

# Phase 2

## 5. Service Module

### Purpose
Manage services and customer booking lifecycle, including creation, status transitions, and task execution.

### Tables
- services
- service_categories
- service_requests
- service_request_status_history
- service_request_assignments
- service_request_evidence
- booking_slots

### APIs
- GET /services
- GET /services/{serviceId}
- POST /service-requests
- GET /service-requests/{requestId}
- GET /customers/{customerId}/service-requests
- PATCH /service-requests/{requestId}/status
- GET /service-requests/{requestId}/history
- POST /service-requests/{requestId}/evidence

### Events
- ServiceRequested
- ServiceBookingCreated
- ServiceStatusUpdated
- AssignmentCreated
- EvidenceUploaded
- ServiceCompleted
- ServiceCancelled

### Dependencies
- Property Module
- Customer Module
- Agent Module
- Payment Module
- Notification Module

### Security Requirements
- customer access only to own requests
- agent access only to assigned requests
- support/admin access is role-scoped
- evidence upload requires authorization and file validation
- lifecycle status changes must be auditable

### Testing Requirements
- service catalog tests
- booking creation tests
- state transition tests
- invalid status transition tests
- access control and multi-role tests

### Definition of Done
- service booking and lifecycle are working
- status transitions are validated and recorded
- evidence and assignment linkage works
- customer visibility is enabled

### Critical Path Analysis
This module is the operational backbone of the product and the main business execution engine.

### Risks
- workflow gaps between backend and UI
- invalid status transitions
- inconsistent request visibility

### Mitigations
- enforce strict state machine validation
- publish clear event contract
- trace request updates with history table

### Implementation Timeline
- Week 1: service catalog and request model
- Week 2: lifecycle, status, evidence integration

---

## 6. Agent Module

### Purpose
Support field agents, assignment handling, availability tracking, and task execution updates.

### Tables
- agents
- agent_profiles
- agent_availability
- agent_visit_logs
- service_request_assignments
- assignment_history

### APIs
- POST /agents
- GET /agents/{agentId}
- PUT /agents/{agentId}
- GET /agents/available
- GET /agents/{agentId}/assignments
- PATCH /agents/{agentId}/availability
- POST /service-requests/{requestId}/assign
- PATCH /service-requests/{requestId}/assignments/{assignmentId}

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
- Notification Module
- Operations Module

### Security Requirements
- agent can only access assigned tasks and own profile
- assignment changes require audit logging
- sensitive location data must be protected
- support/admin overrides require explicit authorization

### Testing Requirements
- agent login and availability tests
- assignment and reassignment tests
- visit workflow and state transition tests
- role-based access validation

### Definition of Done
- agent dashboard and assignment flow work end-to-end
- availability is tracked and operationally useful
- reassignments are controlled and logged

### Critical Path Analysis
This module directly supports field execution and operations productivity.

### Risks
- assignment drift
- invalid queue state
- poor agent visibility

### Mitigations
- enforce assignment ownership and valid state machine
- keep assignment changes auditable
- validate agent availability before assignment

### Implementation Timeline
- Week 1: agent schema and assignment workflow
- Week 2: availability and execution integration

---

## 7. GPS Module

### Purpose
Provide GPS validation and geolocation context for property verification and service execution.

### Tables
- property_gps_checks
- service_request_gps_events
- gps_validation_logs

### APIs
- POST /properties/{propertyId}/verify/gps
- POST /service-requests/{requestId}/gps/validate
- GET /service-requests/{requestId}/gps

### Events
- GPSVerificationPassed
- GPSVerificationFailed
- LocationCaptured
- LocationValidationFailed

### Dependencies
- Property Module
- Service Module
- Agent Module

### Security Requirements
- geolocation data access must be limited to authorized roles
- GPS data should not be exposed broadly
- validation failure and success are logged
- all geolocation retrieval is time-limited and role-scoped

### Testing Requirements
- GPS validation success/failure
- property verification tests
- service request validation tests
- integration with field app location capture

### Definition of Done
- GPS acquisition and verification flow works for the required scenarios
- invalid and missing coordinates are handled
- data is captured and traceable

### Critical Path Analysis
GPS is a required validation gate for many service flows and is a key dependency for booking confidence.

### Risks
- poor accuracy leading to service failures
- invalid capture states
- inconsistent device/gateway data

### Mitigations
- validate coordinate ranges and accuracy thresholds
- provide retry and manual review fallbacks
- log failures for operational review

### Implementation Timeline
- Week 1: coordinate validation and storage
- Week 2: integration with booking and execution flows

---

## 8. Evidence Module

### Purpose
Store and retrieve customer/agent evidence attached to service requests and property verification activities.

### Tables
- service_request_evidence
- property_documents
- evidence_access_logs
- evidence_metadata

### APIs
- POST /service-requests/{requestId}/evidence
- GET /service-requests/{requestId}/evidence
- POST /properties/{propertyId}/documents
- GET /properties/{propertyId}/documents

### Events
- EvidenceUploaded
- EvidenceApproved
- EvidenceRejected
- EvidenceDeleted

### Dependencies
- Service Module
- Property Module
- Notification Module
- Storage backend

### Security Requirements
- file type and size checks
- secure object storage and signed URLs
- access is restricted to request owner, assigned agent, and authorized operations/admin
- evidence retention policy enforced

### Testing Requirements
- upload success and failure tests
- file type validation tests
- access control tests
- storage integration and cleanup tests

### Definition of Done
- upload and retrieval work for approved file types
- evidence is linked to the correct workflow
- storage is secure and logged
- invalid uploads are rejected gracefully

### Critical Path Analysis
Evidence is required for operational proof and dispute resolution.

### Risks
- malformed or oversized uploads
- unauthorized access
- storage sprawl or retention issues

### Mitigations
- validate uploads at API layer and storage layer
- use signed access and object-level ACLs
- enforce retention policy and cleanup jobs

### Implementation Timeline
- Week 1: storage integration and schema
- Week 2: upload API and retrieval governance

---

# Phase 3

## 9. Report Module

### Purpose
Provide all core operational report generation and report job handling for message, payment, tasks, and subscription views.

### Tables
- report_templates
- report_jobs
- report_exports
- report_metrics_cache

### APIs
- GET /reports/summary
- GET /reports/service-requests
- GET /reports/payments
- GET /reports/subscriptions
- GET /reports/agents
- GET /reports/customers
- POST /reports/export

### Events
- ReportGenerated
- ReportExportRequested
- ReportExportCompleted
- ReportExportFailed

### Dependencies
- Service Module
- Payment Module
- Subscription Module
- Agent Module
- Customer Module
- Notification Module

### Security Requirements
- role-based report visibility
- export access must be restricted
- sensitive fields should be masked depending on role
- report generation actions should be auditable

### Testing Requirements
- report generation tests
- filter validation
- export workflow tests
- role-based access tests

### Definition of Done
- core reports are generated and consistent with source data
- report jobs are tracked and exportable
- results are available to authorized users

### Critical Path Analysis
Reporting provides visibility for operations and release quality, and supports many governance workflows.

### Risks
- stale or inconsistent numbers
- slow performance on large datasets
- unauthorized export leakage

### Mitigations
- design read-optimized reporting queries
- use report cache and job scheduler
- enforce export restrictions and audit logs

### Implementation Timeline
- Week 1: summary and data aggregation
- Week 2: export, filters, and role restrictions

---

## 10. Subscription Module

### Purpose
Manage subscription plans, lifecycle state, renewal windows, and customer billing relationship.

### Tables
- subscription_plans
- subscriptions
- subscription_status_history
- billing_cycles
- subscription_events

### APIs
- GET /subscriptions/plans
- POST /subscriptions
- GET /subscriptions/{subscriptionId}
- PATCH /subscriptions/{subscriptionId}
- POST /subscriptions/{subscriptionId}/pause
- POST /subscriptions/{subscriptionId}/resume
- POST /subscriptions/{subscriptionId}/cancel
- GET /customers/{customerId}/subscriptions

### Events
- SubscriptionCreated
- SubscriptionActivated
- SubscriptionRenewed
- SubscriptionPaused
- SubscriptionResumed
- SubscriptionCancelled

### Dependencies
- Customer Module
- Payment Module
- Notification Module

### Security Requirements
- only customer or authorized admin/support roles can read subscription state
- lifecycle actions follow policy validation
- cancellations and pauses are logged and auditable
- renewal and billing rules require strict approval logic

### Testing Requirements
- plan fetch and selection tests
- lifecycle transitions tests
- invalid transition tests
- permissions and access tests

### Definition of Done
- plans and subscriptions are consistent and lifecycle-aware
- customer and support views are functional
- changes are audited and rescue-safe

### Critical Path Analysis
This module is essential for recurring billing and customer relationship continuity.

### Risks
- invalid lifecycle transitions
- duplicate or orphaned subscriptions
- incorrect billing cycles

### Mitigations
- policy-driven state machine checks
- validated subscription uniqueness per customer and plan
- audit trail for each lifecycle event

### Implementation Timeline
- Week 1: plan and subscription model
- Week 2: lifecycle actions and customer visibility

---

## 11. Notification Module

### Purpose
Send lifecycle updates to customers and internal operations via SMS, email, or message channel workflows.

### Tables
- notifications
- notification_templates
- notification_channels
- notification_preferences
- notification_delivery_attempts

### APIs
- POST /notifications/send
- GET /customers/{customerId}/notifications
- GET /notifications/{notificationId}
- PATCH /notifications/{notificationId}/status
- GET /notifications/preferences
- POST /notifications/preferences

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
- External provider integrations

### Security Requirements
- customer preferences and consent are enforced
- only approved templates may be used
- notification content must be sanitized
- channel routing and retry logging must be secure and auditable

### Testing Requirements
- template validation tests
- notification event triggers
- failure and retry tests
- customer preference tests

### Definition of Done
- notifications fire for key lifecycle events
- customer inbox and read state work
- alerts are secure and preference-aware

### Critical Path Analysis
Notifications support both customer communication and business signal flow, making them essential for successful product adoption.

### Risks
- spam or unwanted communication
- delivery failures
- privacy or consent violations

### Mitigations
- consent tracking and preference checks
- dedicated provider adapter with retry logic
- audit and suppression controls

### Implementation Timeline
- Week 1: preferences and templates
- Week 2: event-driven delivery and inbox flow

---

## 12. Payment Module

### Purpose
Process payments, track payment status, reconcile gateway responses, and maintain invoice and refund workflows.

### Tables
- payments
- payment_transactions
- payment_status_history
- invoices
- refunds
- payment_provider_accounts

### APIs
- POST /payments/initiate
- GET /payments/{paymentId}
- POST /payments/{paymentId}/confirm
- POST /payments/{paymentId}/refund
- GET /customers/{customerId}/payments
- GET /invoices/{invoiceId}
- POST /payments/reconcile

### Events
- PaymentInitiated
- PaymentSucceeded
- PaymentFailed
- PaymentExpired
- PaymentRefundRequested
- PaymentRefundProcessed
- InvoiceGenerated

### Dependencies
- Service Module
- Subscription Module
- Customer Module
- Notification Module
- External payment gateway

### Security Requirements
- provider secrets stored in secret manager
- payment operations require strong auth and audit controls
- idempotency keys enforced
- refund and capture actions restricted by policy

### Testing Requirements
- successful payment flow tests
- failed payment validation tests
- refund workflow tests
- gateway timeout and retry tests
- reconciliation tests

### Definition of Done
- payment initiation and confirmation work
- invoices and receipts are consistent
- refund flows are controlled and auditable

### Critical Path Analysis
Payment and transaction reliability are essential for service confirmation and subscription lifecycle.

### Risks
- duplicate or fraudulent charges
- payment gateway drift
- poor reconciliation

### Mitigations
- use idempotency keys and provider references
- restrict payment actions to allowed roles
- implement reconciliation jobs and alerting

### Implementation Timeline
- Week 1: payment model and gateway integration
- Week 2: refund and invoice support

---

# Phase 4

## 13. Operations Module

### Purpose
Provide operational oversight for queue routing, escalations, and service health monitoring.

### Tables
- operations_queues
- operations_alerts
- operations_queue_assignments
- operations_metrics

### APIs
- GET /operations/dashboard
- GET /operations/queue
- PATCH /operations/queue/{requestId}/route
- PATCH /operations/queue/{requestId}/escalate
- GET /operations/alerts

### Events
- QueueReviewed
- RequestEscalated
- AssignmentRouted
- AlertTriggered

### Dependencies
- Service Module
- Agent Module
- Report Module
- Audit Module

### Security Requirements
- access limited to operations/admin roles
- queue actions must be auditable
- escalation routes need validation and approval rules
- data visibility must match role scope

### Testing Requirements
- queue and escalation tests
- route validation tests
- unauthorized access tests
- dashboard loading tests

### Definition of Done
- queue management works for live service operations
- escalations are tracked and visible
- only valid roles can change queue state

### Critical Path Analysis
Operations oversight is required for field-level execution, backlog triage, and service continuity.

### Risks
- operation queue inconsistencies
- over-assignment of resources
- poor exception handling

### Mitigations
- use queue-state validation
- enforce alerts and assignment policies
- maintain audit logs for all route and escalation actions

### Implementation Timeline
- Week 1: queue model and dashboard
- Week 2: escalation logic and operational visibility

---

## 14. Admin Module

### Purpose
Provide administrative controls for user management, role assignment, platform policies, and secure system administration.

### Tables
- admin_users
- user_roles
- user_permissions
- admin_audit_events
- admin_settings

### APIs
- GET /admin/users
- POST /admin/users
- PATCH /admin/users/{userId}/role
- PATCH /admin/users/{userId}/status
- GET /admin/permissions
- POST /admin/roles
- PATCH /admin/roles/{roleId}

### Events
- UserRoleModified
- UserStatusChanged
- RoleCreated
- PermissionAssigned
- AdminActionPerformed

### Dependencies
- Authentication Module
- Audit Module
- System Configuration Module

### Security Requirements
- admin-only access
- role changes must be validated against authorization matrix
- sensitive changes must be logged
- least privilege enforcement mandatory

### Testing Requirements
- role assignment tests
- user status tests
- permissions validation
- admin access control tests

### Definition of Done
- admin features are stable and role-protected
- user and role management works
- security-sensitive actions are logged and validated

### Critical Path Analysis
Admin privileges influence the platform’s governance posture and must be tightly controlled.

### Risks
- privilege escalation
- broken role mappings
- lack of auditability

### Mitigations
- hard RBAC enforcement
- role matrix review
- require audit log on all admin changes

### Implementation Timeline
- Week 1: admin landing screens and API wiring
- Week 2: role matrix and permissions enforcement

---

## 15. Analytics Module

### Purpose
Aggregate operational and business metrics to drive dashboards and trend analysis.

### Tables
- analytics_kpis
- analytics_metrics
- analytics_views
- analytics_snapshots

### APIs
- GET /analytics/overview
- GET /analytics/service-trends
- GET /analytics/payment-trends
- GET /analytics/agent-performance
- GET /analytics/customer-usage

### Events
- AnalyticsSnapshotCreated
- AnalyticsMetricUpdated
- AnalyticsJobCompleted

### Dependencies
- Report Module
- Service Module
- Payment Module
- Subscription Module
- Agent Module

### Security Requirements
- access restricted to authorized leadership, ops, and admin roles
- no exposure of raw sensitive records through analytics
- snapshot generation is auditable

### Testing Requirements
- metric correctness tests
- trend validation tests
- access restriction tests
- performance tests for dashboard queries

### Definition of Done
- dashboard metrics are consistent and reliable
- aggregated data reflects source data accurately
- role-based permissions are enforced

### Critical Path Analysis
Analytics turns operational data into management insight and is required for decision-making and release readiness.

### Risks
- misleading metrics
- stale metrics
- poor performance

### Mitigations
- explicit metric definitions and source mapping
- query optimization and caching
- governance and dashboard validation before release

### Implementation Timeline
- Week 1: metric definitions and baseline aggregation
- Week 2: dashboard consumption and release validation

---

# Critical Path Analysis

Critical dependency chain:
1. Database Foundation
2. Authentication Module
3. Customer Module
4. Property Module
5. Service Module
6. Agent Module
7. GPS Module
8. Evidence Module
9. Notification Module
10. Payment and Subscription Modules
11. Report and Analytics Modules
12. Operations and Admin Modules
13. Audit and Configuration governance

Any failure in the early path delays all downstream work. The most critical items are:
- secure auth and RBAC
- database schema completeness
- core service lifecycle
- assignment and queue logic
- payment and subscription integrity
- admin and operations governance controls

---

# Risks and Mitigations

## Risk 1: Schema drift
Mitigation:
- finalize schema backlog and lock migration sequencing
- no broad implementation without valid migration gate
- require migration validation as part of every build

## Risk 2: Authentication and authorization gaps
Mitigation:
- enforce RBAC at API layer and route guard layer
- add test coverage for role mismatch and unauthorized access
- require strict audit logging for critical auth changes

## Risk 3: Payment and subscription inconsistencies
Mitigation:
- centralize payment state machine
- enforce idempotency and state transition validation
- require reconciliation and status reporting before production release

## Risk 4: Assignment and queue misalignment
Mitigation:
- implement assignment state machine and queue indexing
- validate agent availability and route restrictions
- test assignment flow under concurrent operations

## Risk 5: Reporting inaccuracy
Mitigation:
- use defined metric sources and mapping
- validate individual report queries to source transactions
- conduct reporting QA before signoff

## Risk 6: Security-sensitive admin controls
Mitigation:
- narrow admin routes and require MFA or enhanced auth if applicable
- enact audit log and approval prior to config changes
- restrict access by role plus environment

---

# Implementation Timeline

## Week 1-2: Foundation and Core User Experience
- database schema completion
- auth, customer, and property modules
- initial service request model
- basic validation and API contract alignment

## Week 3-4: Service Execution
- service and agent modules
- GPS and evidence modules
- notification events
- trial workflow end-to-end

## Week 5-6: Commercial and Monitoring
- payment and subscription modules
- reporting and analytics
- operational and customer status visibility

## Week 7-8: Governance and Release Readiness
- operations portal
- admin portal
- role management
- audit logs
- system configuration
- final security and production readiness testing

---

# Definition of Done for the Roadmap

The backend implementation roadmap is complete when:
- all required MVP modules are implemented in dependency order
- each module has defined tables, APIs, events, dependencies, security, and testing
- the implementation sequence aligns with production-readiness and release safety
- the team can execute Sprint 1, Sprint 2, and Sprint 3 backlog items without drift
- schema, API, and UI contracts remain aligned
- audit and security controls are active on all privileged flows
- production deployment approval criteria are supported by the completed backend stack

---

# Summary

This roadmap defines the backend implementation sequence for PropertyPilot in a way that is secure, scalable, traceable, and aligned to the MVP scope. It prioritizes foundational architecture, user access, service execution, and governance controls in a measured order to reduce implementation risk and increase the likelihood of successful release readiness.

The backend work should proceed in dependency order, with schema and auth completion treated as gating prerequisites for all downstream implementation.
