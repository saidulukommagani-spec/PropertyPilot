# API Contract Governance

Document Type: API Governance Specification  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review

---

## 1. Purpose

This document defines the enterprise API governance standards for PropertyPilot. It establishes the contract, lifecycle, shape, and controls for all public and internal HTTP APIs used across the customer app, agent app, operations portal, admin portal, subscription, payment, notification, booking, reporting, and integration layers.

This specification applies to:
- REST APIs exposed to mobile and web clients
- internal microservice APIs
- webhook integrations
- partner integrations
- event-driven callback integrations
- admin and operational interfaces

The intent is to ensure:
- consistency
- security
- auditability
- maintainability
- compatibility
- operational predictability
- governance across all API consumers

---

## 2. API Design Principles

### 2.1 REST First
PropertyPilot APIs shall be designed as REST-based interfaces using HTTP semantics and JSON payloads unless there is a clear requirement for a different protocol.

Rules:
- use standard HTTP methods
- prefer stateless interactions
- model business resources as nouns
- avoid RPC-style endpoints when a resource pattern is possible

### 2.2 Resource Based URLs
All API resources shall be organized around domain entities and business nouns.

Examples:
- /api/v1/customers
- /api/v1/properties
- /api/v1/service-requests
- /api/v1/payments
- /api/v1/subscriptions
- /api/v1/reports

Rules:
- use plural nouns
- avoid verbs in URLs
- use nested resources only when relation is intentional
- prefer predictable hierarchical access patterns

### 2.3 Stateless APIs
Each API request shall contain all information required to process it.

Rules:
- no server-side session dependence for request processing
- client manages state where possible
- tokens, correlation IDs, and request metadata are passed explicitly
- server should not require prior stateful negotiation

### 2.4 Idempotent Operations
API actions that have financial or workflow consequences must be idempotent.

Required idempotency:
- payment creation
- subscription activation
- refund initiation
- webhook processing
- bulk updates
- retries after network loss

Rules:
- require an idempotency key for sensitive POST operations
- reject duplicate operations with same key when safe
- return stable result on repeated identical requests

### 2.5 Backward Compatibility
APIs shall be designed for safe evolution.

Rules:
- do not remove fields without deprecation
- add fields without breaking clients
- avoid changing meaning of existing fields
- preserve response field naming and data types within major versions
- communicate breaking changes through versioning and deprecation windows

---

## 3. API Versioning Standard

### 3.1 Canonical Base Path
All API routes shall use the following canonical base path:

/api/v1

Examples:
- /api/v1/customers
- /api/v1/properties
- /api/v1/service-requests
- /api/v1/payments

### 3.2 Version Lifecycle
Every API version has a lifecycle:
- Draft
- Active
- Deprecated
- Retired

Rules:
- major versions are required for breaking changes
- minor changes must remain backward compatible
- default behavior must follow the latest supported version
- version numbers shall be included in the path

### 3.3 Deprecation Policy
Deprecated APIs must be:
- clearly marked in documentation
- announced in release notes
- retained for a minimum of 6 months unless a regulatory or security mandate requires earlier retirement

### 3.4 Sunset Policy
Sunset rules:
- deprecated APIs must have a published sunset date
- consumer notifications must be issued before deprecation
- a minimum of 90 days notice is required before retirement for production APIs
- critical integrations may require longer notice based on risk or contract obligations

---

## 4. URL Standards

### 4.1 General Routing Rules
- all API endpoints start with /api/v1
- resource names are lower-case and kebab-case when needed
- nested resources represent parent-child relationships
- query parameters are for filtering, sorting, and pagination only

Examples:
- /api/v1/customers
- /api/v1/customers/{customerId}
- /api/v1/properties/{propertyId}/units
- /api/v1/service-requests/{serviceRequestId}/work-orders
- /api/v1/subscriptions/{subscriptionId}/invoices

### 4.2 Nested Resource Pattern
Use nesting only where a natural parent-child relationship exists.

Examples:
- /api/v1/properties/{propertyId}/units
- /api/v1/tenants/{tenantId}/invoices
- /api/v1/customers/{customerId}/notifications

Avoid:
- /api/v1/getCustomerPropertyList
- /api/v1/createServiceRequestAction
- /api/v1/processPaymentOperation

### 4.3 Query Parameters
Query parameters are reserved for:
- filtering
- sorting
- pagination
- expansion/selection
- partial retrieval

Examples:
- /api/v1/properties?page=1&size=20&sort=createdAt,desc
- /api/v1/service-requests?status=open&propertyId=123
- /api/v1/invoices?fromDate=2026-08-01&toDate=2026-08-31

---

## 5. HTTP Method Standards

### 5.1 GET
Purpose:
Retrieve a representation of a resource or collection.

Rules:
- must be safe and idempotent
- no side effects
- must support filtering and pagination

Examples:
- GET /api/v1/customers/{customerId}
- GET /api/v1/properties?status=active
- GET /api/v1/invoices?tenantId={tenantId}

### 5.2 POST
Purpose:
Create a new resource or initiate an action with a new entity.

Rules:
- creates new resource
- requires idempotency key for sensitive or financial operations
- response should include created entity or operation identifier
- HTTP 201 on success

Examples:
- POST /api/v1/customers
- POST /api/v1/service-requests
- POST /api/v1/payments

### 5.3 PUT
Purpose:
Replace an entire resource representation.

Rules:
- use when complete replacement is intended
- must be idempotent
- should be avoided when partial updates are intended

Examples:
- PUT /api/v1/properties/{propertyId}

### 5.4 PATCH
Purpose:
Apply partial update to a resource.

Rules:
- preferred for non-full updates
- must be idempotent when safe
- must validate fields and unknown properties
- should support JSON merge patch semantics when standard is chosen

Examples:
- PATCH /api/v1/customers/{customerId}
- PATCH /api/v1/service-requests/{serviceRequestId}

### 5.5 DELETE
Purpose:
Delete or deactivate a resource.

Rules:
- must be idempotent
- prefer logical deletion when data retention and auditability are required
- may return 204 No Content
- do not physically delete financial records unless explicitly approved

Examples:
- DELETE /api/v1/customers/{customerId}
- DELETE /api/v1/notifications/{notificationId}

---

## 6. Response Standards

### 6.1 Success Response Structure
Successful responses must use consistent JSON structure.

Example success:
```json
{
  "success": true,
  "data": {
    "id": "c_123",
    "status": "active"
  },
  "meta": {
    "requestId": "req_01J...",
    "timestamp": "2026-08-31T12:00:00Z"
  }
}
```

Rules:
- success responses include data and metadata
- payload wrapper keys must remain consistent
- not all APIs must use the same envelope if product standard allows, but consistency is required within the same domain and across major versions

### 6.2 Error Response Structure
Errors must be structured and actionable.

Example error:
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "The request contains invalid fields.",
    "details": [
      {
        "field": "email",
        "issue": "must be a valid email address"
      }
    ]
  },
  "meta": {
    "requestId": "req_01J...",
    "timestamp": "2026-08-31T12:00:00Z"
  }
}
```

Standard error codes:
- VALIDATION_ERROR
- AUTHENTICATION_REQUIRED
- FORBIDDEN
- NOT_FOUND
- CONFLICT
- RATE_LIMITED
- INTERNAL_SERVER_ERROR
- DEPENDENCY_FAILED
- IDEMPOTENCY_REPLAY
- UNAUTHORIZED

### 6.3 Validation Error Structure
Validation problems must be explicit at the field level.

Example:
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Input validation failed.",
    "details": [
      {
        "field": "amount",
        "issue": "must be greater than zero"
      },
      {
        "field": "status",
        "issue": "must be one of: draft, active, closed"
      }
    ]
  }
}
```

### 6.4 HTTP Status Standards
- 200 OK: successful read or update
- 201 Created: created resource
- 202 Accepted: accepted async process
- 204 No Content: successful deletion or no body response
- 400 Bad Request: malformed request
- 401 Unauthorized: missing or invalid credentials
- 403 Forbidden: insufficient role/permission
- 404 Not Found: resource absent
- 409 Conflict: resource conflict
- 422 Unprocessable Entity: business validation fail
- 429 Too Many Requests: rate limiting
- 500 Internal Server Error: unexpected server failure

---

## 7. Pagination Standards

### 7.1 Pagination Query Parameters
All list endpoints must support:
- page
- size
- sort

Examples:
- /api/v1/properties?page=1&size=25
- /api/v1/service-requests?page=2&size=50&sort=createdAt,desc

Rules:
- page defaults to 1
- size defaults to 20
- size maximum is defined by endpoint policy; default maximum 100
- page numbers must be 1-based

### 7.2 Response Metadata
List responses shall include metadata.

Example:
```json
{
  "success": true,
  "data": [
    { "id": "p_1" }
  ],
  "meta": {
    "page": 1,
    "size": 20,
    "total": 156,
    "totalPages": 8,
    "hasNext": true,
    "hasPrevious": false,
    "requestId": "req_01J..."
  }
}
```

### 7.3 Sorting
Sorting rules:
- use explicit sort parameter
- support only single or limited multi-field ordering
- field names must be canonical
- avoid unbounded or ambiguous sorting

Example:
- sort=createdAt,desc
- sort=status,asc,createdAt,desc

---

## 8. Filtering Standards

### 8.1 Filter Parameters
Filtering shall use query parameters for standard criteria.

Examples:
- status
- date ranges
- property filters
- tenant filters
- org filters
- user filters

Examples:
- /api/v1/service-requests?status=open&propertyId=prop_123
- /api/v1/invoices?fromDate=2026-08-01&toDate=2026-08-31
- /api/v1/payments?status=processed&customerId=customer_123

### 8.2 Date Range Standards
Date filters shall use ISO-8601 format.

Examples:
- fromDate=2026-08-01T00:00:00Z
- toDate=2026-08-31T23:59:59Z

### 8.3 Filter Validation
- invalid filter values must return a validation error
- filter names must be explicit and documented
- no free-form filtering without declared semantics
- case and null handling must be defined

---

## 9. Authentication Standards

### 9.1 JWT
JWT-based authentication is the standard for user-facing APIs.

Rules:
- JWT must be passed in Authorization header
- Bearer scheme is required
- token expiry is enforced
- refresh token is allowed where needed
- token claims must be limited to required identity and access data

### 9.2 Mobile OTP
Mobile OTP-based authentication may be used for customer and agent mobile verification flows.

Rules:
- OTP usage must be time-bounded
- attempt limits and lockout policies apply
- OTPs must be associated with a verified mobile number
- OTP flows must be audit logged

### 9.3 Role Based Access
Role-based access control is mandatory where user permissions vary.

Mandated roles:
- Customer
- Agent
- Operations
- Admin
- Vendor
- Partner

Rules:
- resource authorization is evaluated at the API layer
- service-level access checks must be enforced
- admin actions must require elevated permissions
- PII and payment APIs require additional controls

---

## 10. Authorization Standards

### 10.1 Customer
Customer role access:
- view own account and lease information
- submit service requests
- pay invoices
- view own billing and notifications
- access own documents and profile

### 10.2 Agent
Agent role access:
- assigned leads, properties, and lease workflows
- service request assignment and follow-up
- limited collections workflow access
- assigned tenant and property operations only

### 10.3 Operations
Operations role access:
- property, occupancy, and maintenance management
- work order and SLA review
- service-related analytics
- assigned property portfolio context only

### 10.4 Admin
Admin role access:
- user lifecycle and roles
- subscription management
- billing configuration
- audit review
- platform configuration
- global security settings

### 10.5 Vendor
Vendor role access:
- assigned work orders
- quote submission
- service fulfillment status updates
- cost reporting for approved jobs only

### 10.6 Partner
Partner role access:
- approved marketplace and integration data only
- limited read/write based on explicit partner contract

Rules:
- cross-tenant access is prohibited unless explicitly authorized
- each request must validate org and resource ownership
- authorization must be enforced even if resource IDs are known

---

## 11. Idempotency Standards

Idempotency is required for specific high-risk operations.

### 11.1 Required Operations
- payments
- subscription activation
- refunds
- webhooks
- expensive or side-effecting writes
- asynchronous processing status requests

### 11.2 Idempotency Key Requirements
Requests that create or trigger business-changing actions must include:
- X-Idempotency-Key header or request body field
- unique key generated per logical action
- expiration policy on stored idempotency state
- safe retries on duplicate key

### 11.3 Idempotency Responses
- same key on same operation returns same result
- duplicate calls must not create multiple financial records
- duplicates must be logged and correlated

### 11.4 Server Side Requirements
- idempotency store must persist for at least the maximum retry window
- key collision handling must reject ambiguous duplicate keys
- financial operations must ensure no double settlement

---

## 12. Webhook Standards

### 12.1 Inbound Webhooks
Inbound webhooks are used for:
- payment provider callbacks
- notification provider callbacks
- external system event delivery

Rules:
- verify signature or HMAC
- validate timestamp and replay window
- reject duplicates through idempotency keys
- use explicit event type validation
- store raw payload for replay and investigation

### 12.2 Outbound Webhooks
Outbound webhooks are used for:
- status updates to partner systems
- notification delivery status events
- subscription lifecycle events
- payment state events

Rules:
- event payloads must be versioned
- retry is automatic for transient failures
- event ordering should be preserved when required
- payloads must include correlation ID and event timestamp

### 12.3 Retry Policy
- retries are required for temporary failures
- exponential backoff is required
- maximum retries must be bounded
- permanent failures must trigger dead-letter handling

### 12.4 Dead Letter Handling
Failed webhook processing must be written to a dead-letter queue or equivalent archive.

Requirements:
- record original payload
- record failure reason
- permit manual replay
- retain evidence for audit investigation

### 12.5 Replay Support
Replay support is required for:
- payment status events
- subscription lifecycle events
- external integration callbacks

Rules:
- replayed events must be idempotent
- event IDs or unique event identifiers must be used
- replay is restricted to authorized integration consumers

---

## 13. Correlation IDs

### 13.1 Purpose
Every API request and downstream operation must carry a correlation ID to enable end-to-end tracing.

Required fields:
- X-Correlation-ID header
- requestId in response metadata
- same ID propagated to downstream services and events

### 13.2 Request Tracing
The correlation ID must be included in:
- API requests
- logs
- traces
- webhook payloads where appropriate
- asynchronous event metadata

### 13.3 Audit Tracing
Critical actions must include:
- correlation ID
- actor identity
- request source
- timestamp
- resource impacted
- previous and new state where applicable

---

## 14. Rate Limiting

Rate limiting is required to protect service quality and prevent abuse.

### 14.1 Customer APIs
- low to moderate rate limits
- protect billing and account endpoints with stricter limits
- limit repeated payments, OTP, and verification actions

### 14.2 Agent APIs
- moderate rate limits
- protect lead, lease, and property operations endpoints
- enforce concurrency limits for assignment and workflow actions

### 14.3 Admin APIs
- stronger audit logging and stricter rate limits
- protect sensitive configuration and user management endpoints

### 14.4 Rate Limit Response
When rate limit is hit:
- return HTTP 429
- include retry-after header
- include standard error metadata
- log the event

### 14.5 Distributed Rate Limits
Rate limiting must work across distributed service instances and not rely only on a single node.

---

## 15. Audit Requirements

All APIs that affect security, billing, identity, subscriptions, notifications, or service operations must produce audit evidence.

Required audit metadata:
- actor
- org
- tenant
- request path
- HTTP method
- source IP
- device or client metadata
- correlation ID
- prior and new state where meaningful
- timestamp

Critical actions requiring audit:
- login/logout
- role and permission changes
- payment initiation and settlement
- refund approval
- subscription activation, change, cancellation
- document upload or approval
- verification approval/rejection
- admin configuration changes
- webhook processing and replay
- security incident actions

---

## 16. Security Requirements

### 16.1 TLS and Transport Security
- all production APIs must use HTTPS/TLS
- certificates must be managed centrally
- TLS must be enforced for all transport between services

### 16.2 Authentication and Authorization
- all APIs require authentication unless explicitly documented otherwise
- authorization is required by role and resource ownership
- admin endpoints require stronger security controls

### 16.3 Input Validation
- validate payloads on entry
- reject unexpected fields by policy
- enforce schema validation
- sanitize data before write operations

### 16.4 Secret Handling
- secrets must be stored in managed secret stores
- no hard-coded credentials
- rotate keys according to policy
- use signed webhook validation and HMAC where appropriate

### 16.5 Data Protection
- PII must be encrypted at rest and in transit
- payment data must be handled through approved payment providers
- tokens and credentials must be short-lived and scoped

### 16.6 API Security Standards
- no API shall expose data beyond the current user’s scope
- object-level authorization is required
- service-to-service calls must use mTLS or equivalent mutual trust where required

---

## 17. OpenAPI Governance

### 17.1 Canonical Contract Source
OpenAPI is the canonical source of API contracts.

Rules:
- all APIs must have a valid OpenAPI definition
- changes to contract must go through API review
- OpenAPI definitions are versioned with the API version
- generated code and client SDKs must be derived from the OpenAPI spec

### 17.2 API Catalog is Inventory Only
The API catalog is inventory metadata only and is not the source of truth for contract semantics.

Rules:
- API catalog may list endpoints and ownership
- OpenAPI specification is authoritative
- catalog must not replace contract validation

### 17.3 Basic OpenAPI Governance Rules
- all endpoints require summary and description
- all request/response schemas must be explicit
- error responses must be declared
- security schemes must be documented
- examples are required for complex request or response payloads

---

## 18. API Review Process

All new or modified APIs require review before production release.

### 18.1 Review Checklist
The API review process must confirm:
- ownership and service responsibility
- versioning strategy
- request/response schemas
- backward compatibility status
- security and auth model
- rate limit policy
- error contract
- idempotency handling
- audit logging
- retention and privacy considerations
- webhook or integration impact

### 18.2 Required Reviewers
- product owner or domain owner
- API owner
- security reviewer
- architecture reviewer
- QA or integration reviewer
- compliance reviewer when sensitive data is involved

### 18.3 Approval Gate
An API must be approved before:
- it is exposed externally
- it is consumed by a production downstream system
- it is merged into a production release

---

## 19. API Lifecycle

### 19.1 Draft
A new API in design or validation.

Rules:
- subject to review and contract refinement
- must not be used in production without approval

### 19.2 Approved
API has passed review and is required for production use.

Rules:
- version is fixed
- contract is documented
- consumers may integrate
- observability and monitoring are enabled

### 19.3 Deprecated
The API is still supported but no longer recommended.

Rules:
- warnings are published
- consumers are notified
- sunset date is specified
- telemetry is monitored

### 19.4 Retired
The API is no longer available.

Rules:
- route is removed or redirected according to policy
- clients must use approved replacement API
- audit evidence of retirement is maintained

---

## 20. Related Documents

- API_Catalog.md
- OpenAPI_Specification.yaml
- Cross_Cutting_Requirements.md
- Technical_Architecture.md

---

## 21. Governance Summary

PropertyPilot’s API governance model requires:
- REST-first design
- versioned contracts
- safe, secure, auditable interfaces
- clear lifecycle management
- strong authorization and authentication controls
- explicit idempotency for financial workflows
- robust webhook handling and replay management
- OpenAPI as the source of truth
- enterprise-grade review and release discipline

This governance standard ensures that the PropertyPilot platform remains scalable, secure, compatible, and operationally reliable as the platform expands across multiple product domains and customer types.
