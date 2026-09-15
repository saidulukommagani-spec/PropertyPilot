````markdown
# OpenAPI Generation Plan

Document Type: API Contract Generation Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: API Architecture / Backend Engineering / Frontend Engineering / QA

---

# Purpose

This document converts the current PropertyPilot OpenAPI artifacts into a complete generation-ready OpenAPI contract and delivery roadmap.

The objective is to:
- close specification gaps across all modules
- align endpoint generation to the screen catalog, business workflows, and database model
- define reusable request, response, and error schemas
- standardize pagination, filtering, sorting, validation, and authentication
- support backend generation, frontend integration, testing, and production deployment

This plan is based on:
- OpenAPI_Completion_Plan.md
- OpenAPI_Specification.yaml
- API_Catalog.md
- Database_Implementation_Plan.md
- Screen_Catalog.md
- Screen_Flows.md
- PropertyPilot_SRS.md
- Cross_Cutting_Requirements.md

---

# OpenAPI Generation Strategy

## Architectural Principles
- Contract-first delivery for all API modules
- single versioned OpenAPI spec root: /api/v1
- shared schema library and reusable components
- strict validation of required fields, enums, and error objects
- consistent response envelope across all endpoints
- explicit auth and RBAC requirements on every path
- webhook contracts treated as first-class API artifacts

## Generation Rules
- Every endpoint must define:
  - Path
  - Method
  - Request body or query params
  - Response model
  - Security requirement
  - Error model mapping
  - Pagination behavior where applicable
  - Validation rules
  - Webhook dependencies
- All enums must be declared centrally
- All IDs and statuses must be normalized against database names
- All domain models must be generated from the canonical data dictionary
- All APIs must support both producer and consumer requirements from client apps

---

# Core API Contract Standards

## Versioning
- Base path: /api/v1
- Major version changes require new path version
- Deprecation headers and lifecycle warnings required for existing clients

## Response Envelope
Standard success envelope:
- status
- data
- message
- timestamp
- requestId

Standard error envelope:
- code
- message
- details
- traceId
- timestamp
- errors[]

## Pagination
- page
- pageSize
- sortBy
- sortOrder
- totalItems
- totalPages
- hasNextPage

## Filters
- Search across supported fields
- eq, ne, in, like, gt, gte, lt, lte
- support both querystring parameters and filter object payloads

## Validation
- server-side validation on all request bodies and query params
- client-side validation mirrors server rules for UX
- required field lists must be explicit
- enum values must be enforced

## Security
- JWT bearer auth
- refresh token flow
- RBAC + permission checks
- optional scopes for admin and operations workflows
- audit on privileged actions

## Error Model
Common error types:
- ValidationError
- UnauthorizedError
- ForbiddenError
- NotFoundError
- ConflictError
- RateLimitError
- DependencyFailureError
- InternalServerError

---

# Shared Schema Library

Generate these shared components first:

- ApiResponse
- ErrorResponse
- ValidationError
- PaginationMeta
- PageResult
- EmptyResponse
- TimestampedEntity
- AuditMetadata
- GeoPoint
- FileMetadata
- IdResponse
- StatusResponse

Generate common enums:
- UserRole
- AccountStatus
- PropertyStatus
- VerificationStatus
- ServiceRequestStatus
- VisitStatus
- PaymentStatus
- SubscriptionStatus
- NotificationChannel
- NotificationStatus
- ComplaintStatus
- ReportStatus
- KYCStatus
- EscalationStatus

---

# Endpoint Generation Roadmap

## 1. Authentication APIs

### 1.1 POST /api/v1/auth/login
- Request:
  - LoginRequest { identifier, password, rememberMe, deviceId }
- Response:
  - 200 AuthTokenResponse { accessToken, refreshToken, expiresIn, tokenType, user }
- Authentication:
  - none
- Error Model:
  - 400 ValidationError
  - 401 UnauthorizedError
  - 429 RateLimitError
- Pagination:
  - n/a
- Validation:
  - identifier required
  - password required
  - deviceId optional but recommended
- Webhook dependencies:
  - none

### 1.2 POST /api/v1/auth/otp/request
- Request:
  - OtpRequest { channel, contact, purpose }
- Response:
  - 200 OtpIssuedResponse { requestId, expiresIn, channel }
- Authentication:
  - none
- Error Model:
  - 400 ValidationError
  - 429 RateLimitError
- Pagination:
  - n/a
- Validation:
  - channel enum required
  - contact required
  - purpose required
- Webhook dependencies:
  - notification provider webhook status

### 1.3 POST /api/v1/auth/otp/verify
- Request:
  - OtpVerifyRequest { requestId, otpCode, purpose }
- Response:
  - 200 AuthTokenResponse
- Authentication:
  - none
- Error Model:
  - 400 ValidationError
  - 401 UnauthorizedError
- Pagination:
  - n/a
- Validation:
  - otpCode length fixed
  - requestId required
- Webhook dependencies:
  - none

### 1.4 POST /api/v1/auth/token/refresh
- Request:
  - RefreshTokenRequest { refreshToken }
- Response:
  - 200 AuthTokenResponse
- Authentication:
  - none, but refresh token is credential
- Error Model:
  - 401 UnauthorizedError
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - refreshToken required
  - token rotation supported
- Webhook dependencies:
  - none

### 1.5 POST /api/v1/auth/logout
- Request:
  - LogoutRequest { refreshToken? }
- Response:
  - 200 EmptyResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - n/a
- Validation:
  - token must be valid
- Webhook dependencies:
  - none

### 1.6 GET /api/v1/auth/me
- Request:
  - none
- Response:
  - 200 AuthUserProfile
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - current user resolution only
- Webhook dependencies:
  - none

### 1.7 GET /api/v1/auth/roles
- Request:
  - none
- Response:
  - 200 RoleListResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - optional
- Validation:
  - role list restricted by auth context
- Webhook dependencies:
  - none

### 1.8 GET /api/v1/auth/roles/{roleId}/permissions
- Request:
  - none
- Response:
  - 200 PermissionListResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - optional
- Validation:
  - roleId required
- Webhook dependencies:
  - none

---

## 2. Customer APIs

### 2.1 POST /api/v1/customers
- Request:
  - CreateCustomerRequest { firstName, lastName, email, phone, password, address, consent }
- Response:
  - 201 CustomerResponse
- Authentication:
  - none for signup; admin/auth required for internal creation
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - unique email/phone
  - password policy
  - consent required where legal
- Webhook dependencies:
  - notification service for welcome email/SMS

### 2.2 GET /api/v1/customers/{customerId}
- Request:
  - pathParam customerId
- Response:
  - 200 CustomerResponse
- Authentication:
  - JWT required
  - customer owner or admin
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - customerId UUID
- Webhook dependencies:
  - none

### 2.3 PUT /api/v1/customers/{customerId}
- Request:
  - UpdateCustomerRequest
- Response:
  - 200 CustomerResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - field-level validation
- Webhook dependencies:
  - none

### 2.4 PATCH /api/v1/customers/{customerId}
- Request:
  - PartialCustomerUpdate
- Response:
  - 200 CustomerResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - partial update rules
- Webhook dependencies:
  - none

### 2.5 DELETE /api/v1/customers/{customerId}
- Request:
  - soft delete semantics
- Response:
  - 200 EmptyResponse or 204
- Authentication:
  - JWT required, admin or owner
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - soft delete only unless legal hold bypass
- Webhook dependencies:
  - none

### 2.6 GET /api/v1/customers/me/profile
- Request:
  - none
- Response:
  - 200 CustomerProfileResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - n/a
- Validation:
  - current user scope
- Webhook dependencies:
  - none

### 2.7 PUT /api/v1/customers/me/profile
- Request:
  - CustomerProfileUpdateRequest
- Response:
  - 200 CustomerProfileResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - phone/email update restrictions
- Webhook dependencies:
  - none

### 2.8 GET /api/v1/customers/me/addresses
- Request:
  - none
- Response:
  - 200 AddressListResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - supported
- Validation:
  - current user only
- Webhook dependencies:
  - none

### 2.9 POST /api/v1/customers/me/addresses
- Request:
  - CreateAddressRequest
- Response:
  - 201 AddressResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - required address fields and geo info
- Webhook dependencies:
  - none

### 2.10 PUT /api/v1/customers/me/addresses/{addressId}
- Request:
  - UpdateAddressRequest
- Response:
  - 200 AddressResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - addressId required
- Webhook dependencies:
  - none

### 2.11 GET /api/v1/customers/me/preferences
- Request:
  - none
- Response:
  - 200 CustomerPreferenceResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - n/a
- Validation:
  - current user scope
- Webhook dependencies:
  - none

### 2.12 PUT /api/v1/customers/me/preferences
- Request:
  - CustomerPreferenceUpdateRequest
- Response:
  - 200 CustomerPreferenceResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - preference key validation
- Webhook dependencies:
  - none

---

## 3. Property APIs

### 3.1 GET /api/v1/properties
- Request:
  - query parameters: page, pageSize, status, city, propertyType, search
- Response:
  - 200 PropertyPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - status enum, search length, page bounds
- Webhook dependencies:
  - none

### 3.2 POST /api/v1/properties
- Request:
  - CreatePropertyRequest
- Response:
  - 201 PropertyResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - required property details
  - ownership info check
- Webhook dependencies:
  - property-created event to notification / operations

### 3.3 GET /api/v1/properties/{propertyId}
- Request:
  - pathParam propertyId
- Response:
  - 200 PropertyResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - propertyId UUID
- Webhook dependencies:
  - none

### 3.4 PUT /api/v1/properties/{propertyId}
- Request:
  - UpdatePropertyRequest
- Response:
  - 200 PropertyResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - address/ownership constraints
- Webhook dependencies:
  - none

### 3.5 PATCH /api/v1/properties/{propertyId}
- Request:
  - PartialPropertyUpdate
- Response:
  - 200 PropertyResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - status transitions validated
- Webhook dependencies:
  - property status updates

### 3.6 DELETE /api/v1/properties/{propertyId}
- Request:
  - soft delete
- Response:
  - 200 EmptyResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - soft delete only unless escalation
- Webhook dependencies:
  - none

### 3.7 GET /api/v1/properties/{propertyId}/documents
- Request:
  - pathParam propertyId
- Response:
  - 200 PropertyDocumentListResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - yes
- Validation:
  - propertyId required
- Webhook dependencies:
  - evidence storage metadata

### 3.8 POST /api/v1/properties/{propertyId}/documents
- Request:
  - multipart/form-data with file metadata
- Response:
  - 201 PropertyDocumentResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 413 PayloadTooLargeError
- Pagination:
  - n/a
- Validation:
  - file type, size, checksum
- Webhook dependencies:
  - evidence storage event

### 3.9 DELETE /api/v1/properties/{propertyId}/documents/{documentId}
- Request:
  - pathParams propertyId, documentId
- Response:
  - 200 EmptyResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - ownership validation
- Webhook dependencies:
  - none

### 3.10 GET /api/v1/properties/{propertyId}/ownership
- Request:
  - pathParam propertyId
- Response:
  - 200 OwnershipResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - property ownership access rules
- Webhook dependencies:
  - none

### 3.11 POST /api/v1/properties/{propertyId}/ownership/verify
- Request:
  - OwnershipVerificationRequest
- Response:
  - 200 VerificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - proof and reviewer check
- Webhook dependencies:
  - operations review events

### 3.12 GET /api/v1/properties/{propertyId}/status-history
- Request:
  - pathParam propertyId
- Response:
  - 200 PropertyStatusHistoryResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - yes
- Validation:
  - property ownership checks
- Webhook dependencies:
  - none

---

## 4. Service Request APIs

### 4.1 GET /api/v1/service-catalog
- Request:
  - query params: category, city, status, search
- Response:
  - 200 ServiceCatalogPageResponse
- Authentication:
  - JWT required for customer and operations; public for browsing if desired
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - category values and active only
- Webhook dependencies:
  - none

### 4.2 GET /api/v1/service-catalog/{serviceId}
- Request:
  - pathParam serviceId
- Response:
  - 200 ServiceCatalogItemResponse
- Authentication:
  - JWT or public depending on product requirement
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - serviceId required
- Webhook dependencies:
  - none

### 4.3 POST /api/v1/service-requests
- Request:
  - CreateServiceRequestRequest
- Response:
  - 201 ServiceRequestResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - property ownership, date and service type validation
- Webhook dependencies:
  - assignment and notification event generation

### 4.4 GET /api/v1/service-requests/{requestId}
- Request:
  - pathParam requestId
- Response:
  - 200 ServiceRequestResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - requestId UUID
- Webhook dependencies:
  - none

### 4.5 GET /api/v1/service-requests/my
- Request:
  - query params: status, sort, page, pageSize
- Response:
  - 200 ServiceRequestPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - filter/enum restrictions
- Webhook dependencies:
  - none

### 4.6 PATCH /api/v1/service-requests/{requestId}/cancel
- Request:
  - CancelServiceRequestRequest
- Response:
  - 200 ServiceRequestResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - cancellation allowed only in valid states
- Webhook dependencies:
  - notification and assignment event update

### 4.7 PATCH /api/v1/service-requests/{requestId}/reschedule
- Request:
  - RescheduleRequest
- Response:
  - 200 ServiceRequestResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - date/time rules, assignment rules
- Webhook dependencies:
  - notification reschedule event

### 4.8 GET /api/v1/service-requests/{requestId}/history
- Request:
  - pathParam requestId
- Response:
  - 200 ServiceRequestHistoryResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - yes
- Validation:
  - requestId required
- Webhook dependencies:
  - none

### 4.9 GET /api/v1/service-requests/{requestId}/assignments
- Request:
  - pathParam requestId
- Response:
  - 200 AssignmentListResponse
- Authentication:
  - JWT required; operations/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - requestId required
- Webhook dependencies:
  - assignment event propagation

---

## 5. Visit APIs

### 5.1 POST /api/v1/visits
- Request:
  - CreateVisitRequest
- Response:
  - 201 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - valid date/time and assignment status
- Webhook dependencies:
  - assignment and notification events

### 5.2 GET /api/v1/visits/{visitId}
- Request:
  - pathParam visitId
- Response:
  - 200 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - visitId UUID
- Webhook dependencies:
  - none

### 5.3 GET /api/v1/visits
- Request:
  - query params: status, dateFrom, dateTo, agentId, customerId
- Response:
  - 200 VisitPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - status and date filters
- Webhook dependencies:
  - none

### 5.4 PATCH /api/v1/visits/{visitId}/schedule
- Request:
  - ScheduleVisitRequest
- Response:
  - 200 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - valid scheduling rules
- Webhook dependencies:
  - notification and assignment updates

### 5.5 PATCH /api/v1/visits/{visitId}/start
- Request:
  - StartVisitRequest
- Response:
  - 200 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - allowed status transitions
- Webhook dependencies:
  - visit state event

### 5.6 PATCH /api/v1/visits/{visitId}/complete
- Request:
  - CompleteVisitRequest
- Response:
  - 200 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - completion checklist
- Webhook dependencies:
  - report generation, service completion event

### 5.7 PATCH /api/v1/visits/{visitId}/cancel
- Request:
  - CancelVisitRequest
- Response:
  - 200 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - cancel allowed in valid states
- Webhook dependencies:
  - notification event

### 5.8 POST /api/v1/visits/{visitId}/gps/checkin
- Request:
  - GeoCheckinRequest { latitude, longitude, accuracy }
- Response:
  - 200 VisitGpsResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - coordinates valid, GPS accuracy thresholds
- Webhook dependencies:
  - none

### 5.9 POST /api/v1/visits/{visitId}/gps/checkout
- Request:
  - GeoCheckoutRequest
- Response:
  - 200 VisitGpsResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - matching visit state
- Webhook dependencies:
  - none

### 5.10 POST /api/v1/visits/{visitId}/evidence
- Request:
  - multipart/form-data
- Response:
  - 201 EvidenceResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 413 PayloadTooLargeError
- Pagination:
  - n/a
- Validation:
  - file size/type/metadata
- Webhook dependencies:
  - evidence uploaded event

### 5.11 GET /api/v1/visits/{visitId}/evidence
- Request:
  - pathParam visitId
- Response:
  - 200 EvidenceListResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - yes
- Validation:
  - visitId required
- Webhook dependencies:
  - none

---

## 6. Evidence APIs

### 6.1 GET /api/v1/evidence/{evidenceId}
- Request:
  - pathParam evidenceId
- Response:
  - 200 EvidenceResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - evidenceId required
- Webhook dependencies:
  - none

### 6.2 GET /api/v1/evidence/{evidenceId}/preview
- Request:
  - pathParam evidenceId
- Response:
  - 200 binary/media stream
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - media access rights
- Webhook dependencies:
  - none

### 6.3 GET /api/v1/evidence/{evidenceId}/download
- Request:
  - pathParam evidenceId
- Response:
  - 200 binary file
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - file access rights
- Webhook dependencies:
  - none

### 6.4 PATCH /api/v1/evidence/{evidenceId}/review
- Request:
  - EvidenceReviewRequest
- Response:
  - 200 EvidenceResponse
- Authentication:
  - JWT required, admin/ops role
- Error Model:
  - 400 ValidationError
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - status and reason required
- Webhook dependencies:
  - evidence reviewed event

---

## 7. Report APIs

### 7.1 POST /api/v1/reports
- Request:
  - CreateReportRequest
- Response:
  - 201 ReportResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - valid report type and scope
- Webhook dependencies:
  - report generated event when job completes

### 7.2 POST /api/v1/reports/generate
- Request:
  - ReportGenerationRequest
- Response:
  - 202 ReportJobResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - supported date ranges and filters
- Webhook dependencies:
  - async generation job completion callback

### 7.3 GET /api/v1/reports/{reportId}
- Request:
  - pathParam reportId
- Response:
  - 200 ReportResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - reportId required
- Webhook dependencies:
  - none

### 7.4 GET /api/v1/reports
- Request:
  - query params: status, type, page, pageSize
- Response:
  - 200 ReportPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - status enum validation
- Webhook dependencies:
  - none

### 7.5 GET /api/v1/reports/{reportId}/download
- Request:
  - pathParam reportId
- Response:
  - 200 binary/pdf
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - report ready or not
- Webhook dependencies:
  - none

### 7.6 GET /api/v1/reports/{reportId}/export
- Request:
  - pathParam reportId
- Response:
  - 200 ExportResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - export format enum
- Webhook dependencies:
  - none

---

## 8. Subscription APIs

### 8.1 GET /api/v1/subscriptions/plans
- Request:
  - query params: activeOnly, region
- Response:
  - 200 SubscriptionPlanPageResponse
- Authentication:
  - JWT required or public for plan visibility
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - valid region/plan filters
- Webhook dependencies:
  - none

### 8.2 GET /api/v1/subscriptions/plans/{planId}
- Request:
  - pathParam planId
- Response:
  - 200 SubscriptionPlanResponse
- Authentication:
  - JWT or public
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - planId required
- Webhook dependencies:
  - none

### 8.3 POST /api/v1/subscriptions/purchase
- Request:
  - PurchaseSubscriptionRequest
- Response:
  - 201 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - planId and payment method required
- Webhook dependencies:
  - payment webhook and subscription webhook

### 8.4 GET /api/v1/subscriptions/my
- Request:
  - query params: status, page, pageSize
- Response:
  - 200 SubscriptionPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - status enum validation
- Webhook dependencies:
  - none

### 8.5 POST /api/v1/subscriptions/{subscriptionId}/renew
- Request:
  - RenewSubscriptionRequest
- Response:
  - 200 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - status must allow renewal
- Webhook dependencies:
  - subscription renew webhook

### 8.6 POST /api/v1/subscriptions/{subscriptionId}/upgrade
- Request:
  - UpgradeSubscriptionRequest
- Response:
  - 200 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - upgrade path validation
- Webhook dependencies:
  - subscription upgrade webhook

### 8.7 POST /api/v1/subscriptions/{subscriptionId}/downgrade
- Request:
  - DowngradeSubscriptionRequest
- Response:
  - 200 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - downgrade allowed by policy
- Webhook dependencies:
  - subscription downgrade webhook

### 8.8 POST /api/v1/subscriptions/{subscriptionId}/pause
- Request:
  - PauseSubscriptionRequest
- Response:
  - 200 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - pause allowed in active state
- Webhook dependencies:
  - subscription webhook

### 8.9 POST /api/v1/subscriptions/{subscriptionId}/cancel
- Request:
  - CancelSubscriptionRequest
- Response:
  - 200 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - cancellation policy check
- Webhook dependencies:
  - subscription cancelled webhook

### 8.10 GET /api/v1/subscriptions/{subscriptionId}/grace-period
- Request:
  - pathParam subscriptionId
- Response:
  - 200 GracePeriodResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - subscription must be in grace state
- Webhook dependencies:
  - none

---

## 9. Payment APIs

### 9.1 POST /api/v1/payments/checkout
- Request:
  - CheckoutRequest
- Response:
  - 201 PaymentResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 402 PaymentRequiredError
- Pagination:
  - n/a
- Validation:
  - amount, currency, billing metadata, idempotencyKey
- Webhook dependencies:
  - payment provider and internal webhook processing

### 9.2 GET /api/v1/payments/{paymentId}
- Request:
  - pathParam paymentId
- Response:
  - 200 PaymentResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - paymentId required
- Webhook dependencies:
  - none

### 9.3 GET /api/v1/payments
- Request:
  - query params: status, customerId, fromDate, toDate, page
- Response:
  - 200 PaymentPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - status filter enum
- Webhook dependencies:
  - none

### 9.4 POST /api/v1/payments/{paymentId}/refund
- Request:
  - RefundRequest
- Response:
  - 200 RefundResponse
- Authentication:
  - JWT required, finance/admin role for some flows
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - amount <= total refundable
- Webhook dependencies:
  - payment refund webhook

### 9.5 GET /api/v1/payments/{paymentId}/refunds
- Request:
  - pathParam paymentId
- Response:
  - 200 RefundListResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - yes
- Validation:
  - paymentId required
- Webhook dependencies:
  - none

### 9.6 GET /api/v1/invoices/{invoiceId}
- Request:
  - pathParam invoiceId
- Response:
  - 200 InvoiceResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - invoiceId required
- Webhook dependencies:
  - none

### 9.7 GET /api/v1/invoices/my
- Request:
  - query params: status, page, pageSize
- Response:
  - 200 InvoicePageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - filter rules
- Webhook dependencies:
  - none

### 9.8 POST /api/v1/invoices/{invoiceId}/download
- Request:
  - pathParam invoiceId
- Response:
  - 200 binary/pdf
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - invoice ownership rights
- Webhook dependencies:
  - none

### 9.9 POST /api/v1/payments/webhooks/provider
- Request:
  - provider callback payload
- Response:
  - 200 WebhookAckResponse
- Authentication:
  - HMAC or signature validation
- Error Model:
  - 400 ValidationError
  - 401 UnauthorizedError
- Pagination:
  - n/a
- Validation:
  - signed payload verification
- Webhook dependencies:
  - provider webhooks and internal event processing

### 9.10 GET /api/v1/payments/reconciliation
- Request:
  - query params: dateFrom, dateTo, status
- Response:
  - 200 ReconciliationSummaryResponse
- Authentication:
  - JWT required, finance/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - date range restrictions
- Webhook dependencies:
  - provider callback events

### 9.11 POST /api/v1/payments/reconciliation/run
- Request:
  - ReconciliationRunRequest
- Response:
  - 202 ReconciliationJobResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - date range and provider validation
- Webhook dependencies:
  - internal reconciliation status update

---

## 10. Complaint APIs

### 10.1 POST /api/v1/complaints
- Request:
  - CreateComplaintRequest
- Response:
  - 201 ComplaintResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - subject, description, category required
- Webhook dependencies:
  - complaint created event

### 10.2 GET /api/v1/complaints/{complaintId}
- Request:
  - pathParam complaintId
- Response:
  - 200 ComplaintResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - complaintId required
- Webhook dependencies:
  - none

### 10.3 GET /api/v1/complaints/my
- Request:
  - query params: status, page, pageSize
- Response:
  - 200 ComplaintPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - status filter validation
- Webhook dependencies:
  - none

### 10.4 PATCH /api/v1/complaints/{complaintId}/resolve
- Request:
  - ResolveComplaintRequest
- Response:
  - 200 ComplaintResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - resolution reason required
- Webhook dependencies:
  - complaint resolved event

### 10.5 PATCH /api/v1/complaints/{complaintId}/escalate
- Request:
  - EscalateComplaintRequest
- Response:
  - 200 ComplaintResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - escalation reason required
- Webhook dependencies:
  - complaint escalated event

### 10.6 GET /api/v1/complaints
- Request:
  - query params: status, assignedTo, page, pageSize
- Response:
  - 200 ComplaintPageResponse
- Authentication:
  - JWT required, ops/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - status filter enum
- Webhook dependencies:
  - none

---

## 11. Notification APIs

### 11.1 GET /api/v1/notifications
- Request:
  - query params: status, channel, page, pageSize
- Response:
  - 200 NotificationPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - channel/status enums
- Webhook dependencies:
  - notification delivery webhook

### 11.2 PATCH /api/v1/notifications/{notificationId}/read
- Request:
  - none
- Response:
  - 200 NotificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - notification ownership
- Webhook dependencies:
  - none

### 11.3 GET /api/v1/notifications/{notificationId}/delivery-status
- Request:
  - pathParam notificationId
- Response:
  - 200 NotificationDeliveryStatusResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - notificationId required
- Webhook dependencies:
  - providers delivering to notification status endpoints

### 11.4 POST /api/v1/notifications/email
- Request:
  - SendEmailNotificationRequest
- Response:
  - 202 NotificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - recipient, template, subject required
- Webhook dependencies:
  - provider status callback

### 11.5 POST /api/v1/notifications/sms
- Request:
  - SendSmsNotificationRequest
- Response:
  - 202 NotificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - recipient required
- Webhook dependencies:
  - SMS provider status callback

### 11.6 POST /api/v1/notifications/whatsapp
- Request:
  - SendWhatsAppRequest
- Response:
  - 202 NotificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - recipient and template required
- Webhook dependencies:
  - WhatsApp webhook events

### 11.7 POST /api/v1/notifications/push
- Request:
  - SendPushRequest
- Response:
  - 202 NotificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - device token required
- Webhook dependencies:
  - push provider status

### 11.8 GET /api/v1/notifications/delivery-summary
- Request:
  - query params: channel, dateRange, status
- Response:
  - 200 DeliverySummaryResponse
- Authentication:
  - JWT required, admin/ops
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - channel/status filters
- Webhook dependencies:
  - none

---

## 12. Marketplace APIs

### 12.1 GET /api/v1/marketplace/vendors
- Request:
  - query params: city, category, search, page
- Response:
  - 200 VendorPageResponse
- Authentication:
  - JWT or public listing access
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - category and search constraints
- Webhook dependencies:
  - none

### 12.2 GET /api/v1/marketplace/vendors/{vendorId}
- Request:
  - pathParam vendorId
- Response:
  - 200 VendorResponse
- Authentication:
  - JWT or public
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - vendorId required
- Webhook dependencies:
  - none

### 12.3 POST /api/v1/marketplace/vendors
- Request:
  - CreateVendorRequest
- Response:
  - 201 VendorResponse
- Authentication:
  - JWT required, admin/partner
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - vendor metadata and legal fields
- Webhook dependencies:
  - vendor onboarding event

### 12.4 POST /api/v1/marketplace/quotations
- Request:
  - CreateQuotationRequest
- Response:
  - 201 QuotationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - requested service, budget, and dates required
- Webhook dependencies:
  - quotation created / vendor notified callback

### 12.5 GET /api/v1/marketplace/quotations/{quotationId}
- Request:
  - pathParam quotationId
- Response:
  - 200 QuotationResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - quotationId required
- Webhook dependencies:
  - none

### 12.6 GET /api/v1/marketplace/quotations/my
- Request:
  - query params: status, page, pageSize
- Response:
  - 200 QuotationPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - valid filter status
- Webhook dependencies:
  - none

### 12.7 POST /api/v1/marketplace/assignments
- Request:
  - CreateAssignmentRequest
- Response:
  - 201 MarketplaceAssignmentResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - selected vendor and request validation
- Webhook dependencies:
  - assignment and confirmation events

### 12.8 GET /api/v1/marketplace/assignments/{assignmentId}
- Request:
  - pathParam assignmentId
- Response:
  - 200 MarketplaceAssignmentResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - assignmentId required
- Webhook dependencies:
  - none

---

## 13. Admin APIs

### 13.1 GET /api/v1/admin/users
- Request:
  - query params: role, status, page, pageSize
- Response:
  - 200 AdminUserPageResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - role filter enum
- Webhook dependencies:
  - none

### 13.2 POST /api/v1/admin/users
- Request:
  - CreateAdminUserRequest
- Response:
  - 201 AdminUserResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - email, role, status
- Webhook dependencies:
  - user-created notification event

### 13.3 PATCH /api/v1/admin/users/{userId}
- Request:
  - UpdateAdminUserRequest
- Response:
  - 200 AdminUserResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - update policy
- Webhook dependencies:
  - none

### 13.4 GET /api/v1/admin/pricing/plans
- Request:
  - query params: location, activeOnly
- Response:
  - 200 PricingPlanPageResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - activeOnly boolean
- Webhook dependencies:
  - none

### 13.5 POST /api/v1/admin/pricing/plans
- Request:
  - CreatePricingPlanRequest
- Response:
  - 201 PricingPlanResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - amount, currency, validity window required
- Webhook dependencies:
  - none

### 13.6 PUT /api/v1/admin/pricing/plans/{planId}
- Request:
  - UpdatePricingPlanRequest
- Response:
  - 200 PricingPlanResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - valid planId and pricing constraints
- Webhook dependencies:
  - pricing update event

### 13.7 GET /api/v1/admin/config
- Request:
  - query params: category
- Response:
  - 200 ConfigurationPageResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - categories supported
- Webhook dependencies:
  - none

### 13.8 PUT /api/v1/admin/config/{key}
- Request:
  - ConfigUpdateRequest
- Response:
  - 200 ConfigValueResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - key existence and value type
- Webhook dependencies:
  - configuration update event

### 13.9 GET /api/v1/admin/audit
- Request:
  - query params: actor, entityType, action, page, pageSize
- Response:
  - 200 AuditLogPageResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - action and entity filters
- Webhook dependencies:
  - none

### 13.10 GET /api/v1/admin/analytics
- Request:
  - query params: metric, dateFrom, dateTo
- Response:
  - 200 AnalyticsSummaryResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - supported metrics
- Webhook dependencies:
  - none

---

## 14. Operations APIs

### 14.1 GET /api/v1/operations/dashboard
- Request:
  - query params: dateRange, status
- Response:
  - 200 OperationsDashboardResponse
- Authentication:
  - JWT required, operations/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - date range allowed
- Webhook dependencies:
  - none

### 14.2 GET /api/v1/operations/assignments
- Request:
  - query params: status, agentId, page, pageSize
- Response:
  - 200 AssignmentPageResponse
- Authentication:
  - JWT required, operations/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - status filters
- Webhook dependencies:
  - assignment events

### 14.3 POST /api/v1/operations/assignments
- Request:
  - CreateAssignmentRequest
- Response:
  - 201 AssignmentResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - agent and work item constraints
- Webhook dependencies:
  - assignment event

### 14.4 PATCH /api/v1/operations/assignments/{assignmentId}
- Request:
  - UpdateAssignmentRequest
- Response:
  - 200 AssignmentResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - reassignment rules
- Webhook dependencies:
  - notification to agent/customer

### 14.5 GET /api/v1/operations/escalations
- Request:
  - query params: status, severity, page
- Response:
  - 200 EscalationPageResponse
- Authentication:
  - JWT required, ops/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - severity enum
- Webhook dependencies:
  - escalation event

### 14.6 POST /api/v1/operations/escalations
- Request:
  - CreateEscalationRequest
- Response:
  - 201 EscalationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - reason and target required
- Webhook dependencies:
  - escalation created event

### 14.7 GET /api/v1/operations/workload
- Request:
  - query params: agentId, dateRange
- Response:
  - 200 WorkloadResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - date range checks
- Webhook dependencies:
  - none

---

## 15. KYC APIs

### 15.1 POST /api/v1/kyc/submissions
- Request:
  - KycSubmissionRequest
- Response:
  - 201 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - document type and required fields
- Webhook dependencies:
  - KYC submission event

### 15.2 GET /api/v1/kyc/submissions/{submissionId}
- Request:
  - pathParam submissionId
- Response:
  - 200 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - submissionId required
- Webhook dependencies:
  - none

### 15.3 GET /api/v1/kyc/review-queue
- Request:
  - query params: status, page, pageSize
- Response:
  - 200 KycReviewPageResponse
- Authentication:
  - JWT required, admin/ops
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - status enum
- Webhook dependencies:
  - none

### 15.4 POST /api/v1/kyc/submissions/{submissionId}/review
- Request:
  - KycReviewRequest
- Response:
  - 200 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - reviewer comments and action required
- Webhook dependencies:
  - review event

### 15.5 POST /api/v1/kyc/submissions/{submissionId}/approve
- Request:
  - ApproveKycRequest
- Response:
  - 200 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - only pending or in_review status
- Webhook dependencies:
  - approval event / notification

### 15.6 POST /api/v1/kyc/submissions/{submissionId}/reject
- Request:
  - RejectKycRequest
- Response:
  - 200 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - rejection reason required
- Webhook dependencies:
  - rejection notification

### 15.7 POST /api/v1/kyc/submissions/{submissionId}/reverify
- Request:
  - ReverifyKycRequest
- Response:
  - 200 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - reverify allowed status checks
- Webhook dependencies:
  - notification to user

---

# Webhook Contract Generation

Generate dedicated webhook schemas for:
- payment events
- subscription lifecycle events
- notification delivery callbacks
- marketplace vendor/quotation events
- external provider callbacks

Required payload fields:
- eventId
- eventType
- occurredAt
- resourceId
- status
- correlationId
- signature
- payload

Webhook security:
- HMAC verification
- timestamp validation
- replay protection
- idempotency support
- retry policy metadata

---

# OpenAPI Code Generation Requirements

## Required OpenAPI Components
Generate the following sections:
- info
- servers
- securitySchemes
- tags
- paths
- components.schemas
- components.parameters
- components.responses
- components.headers
- components.examples

## Required Tags
- Auth
- Customers
- Properties
- Services
- Visits
- Evidence
- Reports
- Subscriptions
- Payments
- Complaints
- Notifications
- Marketplace
- Admin
- Operations
- KYC

## Required Security
- bearerAuth
- apiKeyAuth, if required for provider or service callbacks
- internalServiceAuth for backend-to-backend calls

---

# Generation Sequence

## Phase 1: Shared Foundation
- base info and servers
- security schemes
- global response models
- pagination models
- validation error models
- enums
- audit metadata
- common params

## Phase 2: Customer and Core User Flows
- auth
- customer
- property
- service catalog
- service requests

## Phase 3: Operational Execution Flows
- visits
- evidence
- complaint
- notification
- marketplace

## Phase 4: Commercial and Compliance Flows
- subscriptions
- payments
- KYC
- invoices
- reports

## Phase 5: Admin and Platform Flows
- admin
- operations
- analytics
- configuration
- audit listing

## Phase 6: Webhooks and Validation
- provider callback contracts
- replay endpoints
- signature validation rules
- contract linting and generation verification

---

# Validation and Quality Gates

Before the OpenAPI contract is accepted:
- all paths must be mapped to screen flows
- all core database entities must have matching endpoints
- all required DTOs must be defined
- all webhooks must be explicitly listed
- all auth requirements must be declared
- all error models must be enforced
- all non-trivial list endpoints must support pagination
- all state-changing operations must support idempotency or duplicate protection where relevant

---

# Risks

Risk | Impact | Mitigation | Owner
---|---|---|---
Unmapped screens to APIs | hidden gaps in client delivery | require traceability matrix review | API Architecture
Schema drift from database | backend/frontend mismatch | contract-code generation gate | Backend Architecture
Missing webhook payload specs | integration failure | dedicated webhook schema review | Integration Engineering
Weak auth coverage | security issue | every endpoint must declare security requirement | Security
Inconsistent pagination | broken UI list flows | standardize across all list endpoints | Frontend Architecture
Overloaded contract | harder maintenance | modularize by domain with shared components | API Architecture
Incomplete validation | invalid input and noisy errors | enforce validation in schema and server tests | QA / Backend
Late API changes | rework overhead | freeze contract review milestones | Engineering Manager

---

# Success Criteria

The OpenAPI contract is ready for:
- backend generation
- frontend integration
- testing
- production deployment

Specifically:
- all primary screen flows are mapped to endpoints
- all major domain models are represented in schemas
- auth and RBAC are declared for every endpoint
- validation rules are complete and explicit
- pagination and filtering are standardized
- payment and subscription webhooks are complete
- admin and operational APIs are defined
- report and evidence endpoints are complete
- contract linting and generated SDK/client checks pass

---

# Final Recommendation

PropertyPilot should treat OpenAPI as the source of truth for backend and frontend implementation. The recommended generation flow is:

1. finalize shared components and enums
2. generate the customer and property domains
3. generate operational execution flows
4. generate billing and subscription contracts
5. generate admin and operations contracts
6. generate webhook contracts and validation rules
7. run linting, mock generation, and contract verification before merge

This ensures the final OpenAPI contract is complete, stable, and production-grade across all business domains.
```// filepath: c:\PropertyPilot\docs\OpenAPI_Generation_Plan.md
# OpenAPI Generation Plan

Document Type: API Contract Generation Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: API Architecture / Backend Engineering / Frontend Engineering / QA

---

# Purpose

This document converts the current PropertyPilot OpenAPI artifacts into a complete generation-ready OpenAPI contract and delivery roadmap.

The objective is to:
- close specification gaps across all modules
- align endpoint generation to the screen catalog, business workflows, and database model
- define reusable request, response, and error schemas
- standardize pagination, filtering, sorting, validation, and authentication
- support backend generation, frontend integration, testing, and production deployment

This plan is based on:
- OpenAPI_Completion_Plan.md
- OpenAPI_Specification.yaml
- API_Catalog.md
- Database_Implementation_Plan.md
- Screen_Catalog.md
- Screen_Flows.md
- PropertyPilot_SRS.md
- Cross_Cutting_Requirements.md

---

# OpenAPI Generation Strategy

## Architectural Principles
- Contract-first delivery for all API modules
- single versioned OpenAPI spec root: /api/v1
- shared schema library and reusable components
- strict validation of required fields, enums, and error objects
- consistent response envelope across all endpoints
- explicit auth and RBAC requirements on every path
- webhook contracts treated as first-class API artifacts

## Generation Rules
- Every endpoint must define:
  - Path
  - Method
  - Request body or query params
  - Response model
  - Security requirement
  - Error model mapping
  - Pagination behavior where applicable
  - Validation rules
  - Webhook dependencies
- All enums must be declared centrally
- All IDs and statuses must be normalized against database names
- All domain models must be generated from the canonical data dictionary
- All APIs must support both producer and consumer requirements from client apps

---

# Core API Contract Standards

## Versioning
- Base path: /api/v1
- Major version changes require new path version
- Deprecation headers and lifecycle warnings required for existing clients

## Response Envelope
Standard success envelope:
- status
- data
- message
- timestamp
- requestId

Standard error envelope:
- code
- message
- details
- traceId
- timestamp
- errors[]

## Pagination
- page
- pageSize
- sortBy
- sortOrder
- totalItems
- totalPages
- hasNextPage

## Filters
- Search across supported fields
- eq, ne, in, like, gt, gte, lt, lte
- support both querystring parameters and filter object payloads

## Validation
- server-side validation on all request bodies and query params
- client-side validation mirrors server rules for UX
- required field lists must be explicit
- enum values must be enforced

## Security
- JWT bearer auth
- refresh token flow
- RBAC + permission checks
- optional scopes for admin and operations workflows
- audit on privileged actions

## Error Model
Common error types:
- ValidationError
- UnauthorizedError
- ForbiddenError
- NotFoundError
- ConflictError
- RateLimitError
- DependencyFailureError
- InternalServerError

---

# Shared Schema Library

Generate these shared components first:

- ApiResponse
- ErrorResponse
- ValidationError
- PaginationMeta
- PageResult
- EmptyResponse
- TimestampedEntity
- AuditMetadata
- GeoPoint
- FileMetadata
- IdResponse
- StatusResponse

Generate common enums:
- UserRole
- AccountStatus
- PropertyStatus
- VerificationStatus
- ServiceRequestStatus
- VisitStatus
- PaymentStatus
- SubscriptionStatus
- NotificationChannel
- NotificationStatus
- ComplaintStatus
- ReportStatus
- KYCStatus
- EscalationStatus

---

# Endpoint Generation Roadmap

## 1. Authentication APIs

### 1.1 POST /api/v1/auth/login
- Request:
  - LoginRequest { identifier, password, rememberMe, deviceId }
- Response:
  - 200 AuthTokenResponse { accessToken, refreshToken, expiresIn, tokenType, user }
- Authentication:
  - none
- Error Model:
  - 400 ValidationError
  - 401 UnauthorizedError
  - 429 RateLimitError
- Pagination:
  - n/a
- Validation:
  - identifier required
  - password required
  - deviceId optional but recommended
- Webhook dependencies:
  - none

### 1.2 POST /api/v1/auth/otp/request
- Request:
  - OtpRequest { channel, contact, purpose }
- Response:
  - 200 OtpIssuedResponse { requestId, expiresIn, channel }
- Authentication:
  - none
- Error Model:
  - 400 ValidationError
  - 429 RateLimitError
- Pagination:
  - n/a
- Validation:
  - channel enum required
  - contact required
  - purpose required
- Webhook dependencies:
  - notification provider webhook status

### 1.3 POST /api/v1/auth/otp/verify
- Request:
  - OtpVerifyRequest { requestId, otpCode, purpose }
- Response:
  - 200 AuthTokenResponse
- Authentication:
  - none
- Error Model:
  - 400 ValidationError
  - 401 UnauthorizedError
- Pagination:
  - n/a
- Validation:
  - otpCode length fixed
  - requestId required
- Webhook dependencies:
  - none

### 1.4 POST /api/v1/auth/token/refresh
- Request:
  - RefreshTokenRequest { refreshToken }
- Response:
  - 200 AuthTokenResponse
- Authentication:
  - none, but refresh token is credential
- Error Model:
  - 401 UnauthorizedError
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - refreshToken required
  - token rotation supported
- Webhook dependencies:
  - none

### 1.5 POST /api/v1/auth/logout
- Request:
  - LogoutRequest { refreshToken? }
- Response:
  - 200 EmptyResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - n/a
- Validation:
  - token must be valid
- Webhook dependencies:
  - none

### 1.6 GET /api/v1/auth/me
- Request:
  - none
- Response:
  - 200 AuthUserProfile
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - current user resolution only
- Webhook dependencies:
  - none

### 1.7 GET /api/v1/auth/roles
- Request:
  - none
- Response:
  - 200 RoleListResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - optional
- Validation:
  - role list restricted by auth context
- Webhook dependencies:
  - none

### 1.8 GET /api/v1/auth/roles/{roleId}/permissions
- Request:
  - none
- Response:
  - 200 PermissionListResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - optional
- Validation:
  - roleId required
- Webhook dependencies:
  - none

---

## 2. Customer APIs

### 2.1 POST /api/v1/customers
- Request:
  - CreateCustomerRequest { firstName, lastName, email, phone, password, address, consent }
- Response:
  - 201 CustomerResponse
- Authentication:
  - none for signup; admin/auth required for internal creation
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - unique email/phone
  - password policy
  - consent required where legal
- Webhook dependencies:
  - notification service for welcome email/SMS

### 2.2 GET /api/v1/customers/{customerId}
- Request:
  - pathParam customerId
- Response:
  - 200 CustomerResponse
- Authentication:
  - JWT required
  - customer owner or admin
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - customerId UUID
- Webhook dependencies:
  - none

### 2.3 PUT /api/v1/customers/{customerId}
- Request:
  - UpdateCustomerRequest
- Response:
  - 200 CustomerResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - field-level validation
- Webhook dependencies:
  - none

### 2.4 PATCH /api/v1/customers/{customerId}
- Request:
  - PartialCustomerUpdate
- Response:
  - 200 CustomerResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - partial update rules
- Webhook dependencies:
  - none

### 2.5 DELETE /api/v1/customers/{customerId}
- Request:
  - soft delete semantics
- Response:
  - 200 EmptyResponse or 204
- Authentication:
  - JWT required, admin or owner
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - soft delete only unless legal hold bypass
- Webhook dependencies:
  - none

### 2.6 GET /api/v1/customers/me/profile
- Request:
  - none
- Response:
  - 200 CustomerProfileResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - n/a
- Validation:
  - current user scope
- Webhook dependencies:
  - none

### 2.7 PUT /api/v1/customers/me/profile
- Request:
  - CustomerProfileUpdateRequest
- Response:
  - 200 CustomerProfileResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - phone/email update restrictions
- Webhook dependencies:
  - none

### 2.8 GET /api/v1/customers/me/addresses
- Request:
  - none
- Response:
  - 200 AddressListResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - supported
- Validation:
  - current user only
- Webhook dependencies:
  - none

### 2.9 POST /api/v1/customers/me/addresses
- Request:
  - CreateAddressRequest
- Response:
  - 201 AddressResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - required address fields and geo info
- Webhook dependencies:
  - none

### 2.10 PUT /api/v1/customers/me/addresses/{addressId}
- Request:
  - UpdateAddressRequest
- Response:
  - 200 AddressResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - addressId required
- Webhook dependencies:
  - none

### 2.11 GET /api/v1/customers/me/preferences
- Request:
  - none
- Response:
  - 200 CustomerPreferenceResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - n/a
- Validation:
  - current user scope
- Webhook dependencies:
  - none

### 2.12 PUT /api/v1/customers/me/preferences
- Request:
  - CustomerPreferenceUpdateRequest
- Response:
  - 200 CustomerPreferenceResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - preference key validation
- Webhook dependencies:
  - none

---

## 3. Property APIs

### 3.1 GET /api/v1/properties
- Request:
  - query parameters: page, pageSize, status, city, propertyType, search
- Response:
  - 200 PropertyPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - status enum, search length, page bounds
- Webhook dependencies:
  - none

### 3.2 POST /api/v1/properties
- Request:
  - CreatePropertyRequest
- Response:
  - 201 PropertyResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - required property details
  - ownership info check
- Webhook dependencies:
  - property-created event to notification / operations

### 3.3 GET /api/v1/properties/{propertyId}
- Request:
  - pathParam propertyId
- Response:
  - 200 PropertyResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - propertyId UUID
- Webhook dependencies:
  - none

### 3.4 PUT /api/v1/properties/{propertyId}
- Request:
  - UpdatePropertyRequest
- Response:
  - 200 PropertyResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - address/ownership constraints
- Webhook dependencies:
  - none

### 3.5 PATCH /api/v1/properties/{propertyId}
- Request:
  - PartialPropertyUpdate
- Response:
  - 200 PropertyResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - status transitions validated
- Webhook dependencies:
  - property status updates

### 3.6 DELETE /api/v1/properties/{propertyId}
- Request:
  - soft delete
- Response:
  - 200 EmptyResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - soft delete only unless escalation
- Webhook dependencies:
  - none

### 3.7 GET /api/v1/properties/{propertyId}/documents
- Request:
  - pathParam propertyId
- Response:
  - 200 PropertyDocumentListResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - yes
- Validation:
  - propertyId required
- Webhook dependencies:
  - evidence storage metadata

### 3.8 POST /api/v1/properties/{propertyId}/documents
- Request:
  - multipart/form-data with file metadata
- Response:
  - 201 PropertyDocumentResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 413 PayloadTooLargeError
- Pagination:
  - n/a
- Validation:
  - file type, size, checksum
- Webhook dependencies:
  - evidence storage event

### 3.9 DELETE /api/v1/properties/{propertyId}/documents/{documentId}
- Request:
  - pathParams propertyId, documentId
- Response:
  - 200 EmptyResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - ownership validation
- Webhook dependencies:
  - none

### 3.10 GET /api/v1/properties/{propertyId}/ownership
- Request:
  - pathParam propertyId
- Response:
  - 200 OwnershipResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - property ownership access rules
- Webhook dependencies:
  - none

### 3.11 POST /api/v1/properties/{propertyId}/ownership/verify
- Request:
  - OwnershipVerificationRequest
- Response:
  - 200 VerificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - proof and reviewer check
- Webhook dependencies:
  - operations review events

### 3.12 GET /api/v1/properties/{propertyId}/status-history
- Request:
  - pathParam propertyId
- Response:
  - 200 PropertyStatusHistoryResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - yes
- Validation:
  - property ownership checks
- Webhook dependencies:
  - none

---

## 4. Service Request APIs

### 4.1 GET /api/v1/service-catalog
- Request:
  - query params: category, city, status, search
- Response:
  - 200 ServiceCatalogPageResponse
- Authentication:
  - JWT required for customer and operations; public for browsing if desired
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - category values and active only
- Webhook dependencies:
  - none

### 4.2 GET /api/v1/service-catalog/{serviceId}
- Request:
  - pathParam serviceId
- Response:
  - 200 ServiceCatalogItemResponse
- Authentication:
  - JWT or public depending on product requirement
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - serviceId required
- Webhook dependencies:
  - none

### 4.3 POST /api/v1/service-requests
- Request:
  - CreateServiceRequestRequest
- Response:
  - 201 ServiceRequestResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - property ownership, date and service type validation
- Webhook dependencies:
  - assignment and notification event generation

### 4.4 GET /api/v1/service-requests/{requestId}
- Request:
  - pathParam requestId
- Response:
  - 200 ServiceRequestResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - requestId UUID
- Webhook dependencies:
  - none

### 4.5 GET /api/v1/service-requests/my
- Request:
  - query params: status, sort, page, pageSize
- Response:
  - 200 ServiceRequestPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - filter/enum restrictions
- Webhook dependencies:
  - none

### 4.6 PATCH /api/v1/service-requests/{requestId}/cancel
- Request:
  - CancelServiceRequestRequest
- Response:
  - 200 ServiceRequestResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - cancellation allowed only in valid states
- Webhook dependencies:
  - notification and assignment event update

### 4.7 PATCH /api/v1/service-requests/{requestId}/reschedule
- Request:
  - RescheduleRequest
- Response:
  - 200 ServiceRequestResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - date/time rules, assignment rules
- Webhook dependencies:
  - notification reschedule event

### 4.8 GET /api/v1/service-requests/{requestId}/history
- Request:
  - pathParam requestId
- Response:
  - 200 ServiceRequestHistoryResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - yes
- Validation:
  - requestId required
- Webhook dependencies:
  - none

### 4.9 GET /api/v1/service-requests/{requestId}/assignments
- Request:
  - pathParam requestId
- Response:
  - 200 AssignmentListResponse
- Authentication:
  - JWT required; operations/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - requestId required
- Webhook dependencies:
  - assignment event propagation

---

## 5. Visit APIs

### 5.1 POST /api/v1/visits
- Request:
  - CreateVisitRequest
- Response:
  - 201 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - valid date/time and assignment status
- Webhook dependencies:
  - assignment and notification events

### 5.2 GET /api/v1/visits/{visitId}
- Request:
  - pathParam visitId
- Response:
  - 200 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - visitId UUID
- Webhook dependencies:
  - none

### 5.3 GET /api/v1/visits
- Request:
  - query params: status, dateFrom, dateTo, agentId, customerId
- Response:
  - 200 VisitPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - status and date filters
- Webhook dependencies:
  - none

### 5.4 PATCH /api/v1/visits/{visitId}/schedule
- Request:
  - ScheduleVisitRequest
- Response:
  - 200 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - valid scheduling rules
- Webhook dependencies:
  - notification and assignment updates

### 5.5 PATCH /api/v1/visits/{visitId}/start
- Request:
  - StartVisitRequest
- Response:
  - 200 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - allowed status transitions
- Webhook dependencies:
  - visit state event

### 5.6 PATCH /api/v1/visits/{visitId}/complete
- Request:
  - CompleteVisitRequest
- Response:
  - 200 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - completion checklist
- Webhook dependencies:
  - report generation, service completion event

### 5.7 PATCH /api/v1/visits/{visitId}/cancel
- Request:
  - CancelVisitRequest
- Response:
  - 200 VisitResponse
- Authentication:
  - JWT required
- Error Model:
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - cancel allowed in valid states
- Webhook dependencies:
  - notification event

### 5.8 POST /api/v1/visits/{visitId}/gps/checkin
- Request:
  - GeoCheckinRequest { latitude, longitude, accuracy }
- Response:
  - 200 VisitGpsResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - coordinates valid, GPS accuracy thresholds
- Webhook dependencies:
  - none

### 5.9 POST /api/v1/visits/{visitId}/gps/checkout
- Request:
  - GeoCheckoutRequest
- Response:
  - 200 VisitGpsResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - matching visit state
- Webhook dependencies:
  - none

### 5.10 POST /api/v1/visits/{visitId}/evidence
- Request:
  - multipart/form-data
- Response:
  - 201 EvidenceResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 413 PayloadTooLargeError
- Pagination:
  - n/a
- Validation:
  - file size/type/metadata
- Webhook dependencies:
  - evidence uploaded event

### 5.11 GET /api/v1/visits/{visitId}/evidence
- Request:
  - pathParam visitId
- Response:
  - 200 EvidenceListResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - yes
- Validation:
  - visitId required
- Webhook dependencies:
  - none

---

## 6. Evidence APIs

### 6.1 GET /api/v1/evidence/{evidenceId}
- Request:
  - pathParam evidenceId
- Response:
  - 200 EvidenceResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - evidenceId required
- Webhook dependencies:
  - none

### 6.2 GET /api/v1/evidence/{evidenceId}/preview
- Request:
  - pathParam evidenceId
- Response:
  - 200 binary/media stream
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - media access rights
- Webhook dependencies:
  - none

### 6.3 GET /api/v1/evidence/{evidenceId}/download
- Request:
  - pathParam evidenceId
- Response:
  - 200 binary file
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - file access rights
- Webhook dependencies:
  - none

### 6.4 PATCH /api/v1/evidence/{evidenceId}/review
- Request:
  - EvidenceReviewRequest
- Response:
  - 200 EvidenceResponse
- Authentication:
  - JWT required, admin/ops role
- Error Model:
  - 400 ValidationError
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - status and reason required
- Webhook dependencies:
  - evidence reviewed event

---

## 7. Report APIs

### 7.1 POST /api/v1/reports
- Request:
  - CreateReportRequest
- Response:
  - 201 ReportResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - valid report type and scope
- Webhook dependencies:
  - report generated event when job completes

### 7.2 POST /api/v1/reports/generate
- Request:
  - ReportGenerationRequest
- Response:
  - 202 ReportJobResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - supported date ranges and filters
- Webhook dependencies:
  - async generation job completion callback

### 7.3 GET /api/v1/reports/{reportId}
- Request:
  - pathParam reportId
- Response:
  - 200 ReportResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - reportId required
- Webhook dependencies:
  - none

### 7.4 GET /api/v1/reports
- Request:
  - query params: status, type, page, pageSize
- Response:
  - 200 ReportPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - status enum validation
- Webhook dependencies:
  - none

### 7.5 GET /api/v1/reports/{reportId}/download
- Request:
  - pathParam reportId
- Response:
  - 200 binary/pdf
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - report ready or not
- Webhook dependencies:
  - none

### 7.6 GET /api/v1/reports/{reportId}/export
- Request:
  - pathParam reportId
- Response:
  - 200 ExportResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - export format enum
- Webhook dependencies:
  - none

---

## 8. Subscription APIs

### 8.1 GET /api/v1/subscriptions/plans
- Request:
  - query params: activeOnly, region
- Response:
  - 200 SubscriptionPlanPageResponse
- Authentication:
  - JWT required or public for plan visibility
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - valid region/plan filters
- Webhook dependencies:
  - none

### 8.2 GET /api/v1/subscriptions/plans/{planId}
- Request:
  - pathParam planId
- Response:
  - 200 SubscriptionPlanResponse
- Authentication:
  - JWT or public
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - planId required
- Webhook dependencies:
  - none

### 8.3 POST /api/v1/subscriptions/purchase
- Request:
  - PurchaseSubscriptionRequest
- Response:
  - 201 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - planId and payment method required
- Webhook dependencies:
  - payment webhook and subscription webhook

### 8.4 GET /api/v1/subscriptions/my
- Request:
  - query params: status, page, pageSize
- Response:
  - 200 SubscriptionPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - status enum validation
- Webhook dependencies:
  - none

### 8.5 POST /api/v1/subscriptions/{subscriptionId}/renew
- Request:
  - RenewSubscriptionRequest
- Response:
  - 200 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - status must allow renewal
- Webhook dependencies:
  - subscription renew webhook

### 8.6 POST /api/v1/subscriptions/{subscriptionId}/upgrade
- Request:
  - UpgradeSubscriptionRequest
- Response:
  - 200 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - upgrade path validation
- Webhook dependencies:
  - subscription upgrade webhook

### 8.7 POST /api/v1/subscriptions/{subscriptionId}/downgrade
- Request:
  - DowngradeSubscriptionRequest
- Response:
  - 200 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - downgrade allowed by policy
- Webhook dependencies:
  - subscription downgrade webhook

### 8.8 POST /api/v1/subscriptions/{subscriptionId}/pause
- Request:
  - PauseSubscriptionRequest
- Response:
  - 200 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - pause allowed in active state
- Webhook dependencies:
  - subscription webhook

### 8.9 POST /api/v1/subscriptions/{subscriptionId}/cancel
- Request:
  - CancelSubscriptionRequest
- Response:
  - 200 SubscriptionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - cancellation policy check
- Webhook dependencies:
  - subscription cancelled webhook

### 8.10 GET /api/v1/subscriptions/{subscriptionId}/grace-period
- Request:
  - pathParam subscriptionId
- Response:
  - 200 GracePeriodResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - subscription must be in grace state
- Webhook dependencies:
  - none

---

## 9. Payment APIs

### 9.1 POST /api/v1/payments/checkout
- Request:
  - CheckoutRequest
- Response:
  - 201 PaymentResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
  - 402 PaymentRequiredError
- Pagination:
  - n/a
- Validation:
  - amount, currency, billing metadata, idempotencyKey
- Webhook dependencies:
  - payment provider and internal webhook processing

### 9.2 GET /api/v1/payments/{paymentId}
- Request:
  - pathParam paymentId
- Response:
  - 200 PaymentResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - paymentId required
- Webhook dependencies:
  - none

### 9.3 GET /api/v1/payments
- Request:
  - query params: status, customerId, fromDate, toDate, page
- Response:
  - 200 PaymentPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - status filter enum
- Webhook dependencies:
  - none

### 9.4 POST /api/v1/payments/{paymentId}/refund
- Request:
  - RefundRequest
- Response:
  - 200 RefundResponse
- Authentication:
  - JWT required, finance/admin role for some flows
- Error Model:
  - 400 ValidationError
  - 409 ConflictError
- Pagination:
  - n/a
- Validation:
  - amount <= total refundable
- Webhook dependencies:
  - payment refund webhook

### 9.5 GET /api/v1/payments/{paymentId}/refunds
- Request:
  - pathParam paymentId
- Response:
  - 200 RefundListResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - yes
- Validation:
  - paymentId required
- Webhook dependencies:
  - none

### 9.6 GET /api/v1/invoices/{invoiceId}
- Request:
  - pathParam invoiceId
- Response:
  - 200 InvoiceResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - invoiceId required
- Webhook dependencies:
  - none

### 9.7 GET /api/v1/invoices/my
- Request:
  - query params: status, page, pageSize
- Response:
  - 200 InvoicePageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - filter rules
- Webhook dependencies:
  - none

### 9.8 POST /api/v1/invoices/{invoiceId}/download
- Request:
  - pathParam invoiceId
- Response:
  - 200 binary/pdf
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - invoice ownership rights
- Webhook dependencies:
  - none

### 9.9 POST /api/v1/payments/webhooks/provider
- Request:
  - provider callback payload
- Response:
  - 200 WebhookAckResponse
- Authentication:
  - HMAC or signature validation
- Error Model:
  - 400 ValidationError
  - 401 UnauthorizedError
- Pagination:
  - n/a
- Validation:
  - signed payload verification
- Webhook dependencies:
  - provider webhooks and internal event processing

### 9.10 GET /api/v1/payments/reconciliation
- Request:
  - query params: dateFrom, dateTo, status
- Response:
  - 200 ReconciliationSummaryResponse
- Authentication:
  - JWT required, finance/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - date range restrictions
- Webhook dependencies:
  - provider callback events

### 9.11 POST /api/v1/payments/reconciliation/run
- Request:
  - ReconciliationRunRequest
- Response:
  - 202 ReconciliationJobResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - date range and provider validation
- Webhook dependencies:
  - internal reconciliation status update

---

## 10. Complaint APIs

### 10.1 POST /api/v1/complaints
- Request:
  - CreateComplaintRequest
- Response:
  - 201 ComplaintResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - subject, description, category required
- Webhook dependencies:
  - complaint created event

### 10.2 GET /api/v1/complaints/{complaintId}
- Request:
  - pathParam complaintId
- Response:
  - 200 ComplaintResponse
- Authentication:
  - JWT required
- Error Model:
  - 403 ForbiddenError
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - complaintId required
- Webhook dependencies:
  - none

### 10.3 GET /api/v1/complaints/my
- Request:
  - query params: status, page, pageSize
- Response:
  - 200 ComplaintPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - status filter validation
- Webhook dependencies:
  - none

### 10.4 PATCH /api/v1/complaints/{complaintId}/resolve
- Request:
  - ResolveComplaintRequest
- Response:
  - 200 ComplaintResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - resolution reason required
- Webhook dependencies:
  - complaint resolved event

### 10.5 PATCH /api/v1/complaints/{complaintId}/escalate
- Request:
  - EscalateComplaintRequest
- Response:
  - 200 ComplaintResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - escalation reason required
- Webhook dependencies:
  - complaint escalated event

### 10.6 GET /api/v1/complaints
- Request:
  - query params: status, assignedTo, page, pageSize
- Response:
  - 200 ComplaintPageResponse
- Authentication:
  - JWT required, ops/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - status filter enum
- Webhook dependencies:
  - none

---

## 11. Notification APIs

### 11.1 GET /api/v1/notifications
- Request:
  - query params: status, channel, page, pageSize
- Response:
  - 200 NotificationPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - channel/status enums
- Webhook dependencies:
  - notification delivery webhook

### 11.2 PATCH /api/v1/notifications/{notificationId}/read
- Request:
  - none
- Response:
  - 200 NotificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - notification ownership
- Webhook dependencies:
  - none

### 11.3 GET /api/v1/notifications/{notificationId}/delivery-status
- Request:
  - pathParam notificationId
- Response:
  - 200 NotificationDeliveryStatusResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - notificationId required
- Webhook dependencies:
  - providers delivering to notification status endpoints

### 11.4 POST /api/v1/notifications/email
- Request:
  - SendEmailNotificationRequest
- Response:
  - 202 NotificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - recipient, template, subject required
- Webhook dependencies:
  - provider status callback

### 11.5 POST /api/v1/notifications/sms
- Request:
  - SendSmsNotificationRequest
- Response:
  - 202 NotificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - recipient required
- Webhook dependencies:
  - SMS provider status callback

### 11.6 POST /api/v1/notifications/whatsapp
- Request:
  - SendWhatsAppRequest
- Response:
  - 202 NotificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - recipient and template required
- Webhook dependencies:
  - WhatsApp webhook events

### 11.7 POST /api/v1/notifications/push
- Request:
  - SendPushRequest
- Response:
  - 202 NotificationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - device token required
- Webhook dependencies:
  - push provider status

### 11.8 GET /api/v1/notifications/delivery-summary
- Request:
  - query params: channel, dateRange, status
- Response:
  - 200 DeliverySummaryResponse
- Authentication:
  - JWT required, admin/ops
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - channel/status filters
- Webhook dependencies:
  - none

---

## 12. Marketplace APIs

### 12.1 GET /api/v1/marketplace/vendors
- Request:
  - query params: city, category, search, page
- Response:
  - 200 VendorPageResponse
- Authentication:
  - JWT or public listing access
- Error Model:
  - 400 ValidationError
- Pagination:
  - yes
- Validation:
  - category and search constraints
- Webhook dependencies:
  - none

### 12.2 GET /api/v1/marketplace/vendors/{vendorId}
- Request:
  - pathParam vendorId
- Response:
  - 200 VendorResponse
- Authentication:
  - JWT or public
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - vendorId required
- Webhook dependencies:
  - none

### 12.3 POST /api/v1/marketplace/vendors
- Request:
  - CreateVendorRequest
- Response:
  - 201 VendorResponse
- Authentication:
  - JWT required, admin/partner
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - vendor metadata and legal fields
- Webhook dependencies:
  - vendor onboarding event

### 12.4 POST /api/v1/marketplace/quotations
- Request:
  - CreateQuotationRequest
- Response:
  - 201 QuotationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - requested service, budget, and dates required
- Webhook dependencies:
  - quotation created / vendor notified callback

### 12.5 GET /api/v1/marketplace/quotations/{quotationId}
- Request:
  - pathParam quotationId
- Response:
  - 200 QuotationResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - quotationId required
- Webhook dependencies:
  - none

### 12.6 GET /api/v1/marketplace/quotations/my
- Request:
  - query params: status, page, pageSize
- Response:
  - 200 QuotationPageResponse
- Authentication:
  - JWT required
- Error Model:
  - 401 UnauthorizedError
- Pagination:
  - yes
- Validation:
  - valid filter status
- Webhook dependencies:
  - none

### 12.7 POST /api/v1/marketplace/assignments
- Request:
  - CreateAssignmentRequest
- Response:
  - 201 MarketplaceAssignmentResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - selected vendor and request validation
- Webhook dependencies:
  - assignment and confirmation events

### 12.8 GET /api/v1/marketplace/assignments/{assignmentId}
- Request:
  - pathParam assignmentId
- Response:
  - 200 MarketplaceAssignmentResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - assignmentId required
- Webhook dependencies:
  - none

---

## 13. Admin APIs

### 13.1 GET /api/v1/admin/users
- Request:
  - query params: role, status, page, pageSize
- Response:
  - 200 AdminUserPageResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - role filter enum
- Webhook dependencies:
  - none

### 13.2 POST /api/v1/admin/users
- Request:
  - CreateAdminUserRequest
- Response:
  - 201 AdminUserResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - email, role, status
- Webhook dependencies:
  - user-created notification event

### 13.3 PATCH /api/v1/admin/users/{userId}
- Request:
  - UpdateAdminUserRequest
- Response:
  - 200 AdminUserResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - update policy
- Webhook dependencies:
  - none

### 13.4 GET /api/v1/admin/pricing/plans
- Request:
  - query params: location, activeOnly
- Response:
  - 200 PricingPlanPageResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - activeOnly boolean
- Webhook dependencies:
  - none

### 13.5 POST /api/v1/admin/pricing/plans
- Request:
  - CreatePricingPlanRequest
- Response:
  - 201 PricingPlanResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - amount, currency, validity window required
- Webhook dependencies:
  - none

### 13.6 PUT /api/v1/admin/pricing/plans/{planId}
- Request:
  - UpdatePricingPlanRequest
- Response:
  - 200 PricingPlanResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - valid planId and pricing constraints
- Webhook dependencies:
  - pricing update event

### 13.7 GET /api/v1/admin/config
- Request:
  - query params: category
- Response:
  - 200 ConfigurationPageResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - categories supported
- Webhook dependencies:
  - none

### 13.8 PUT /api/v1/admin/config/{key}
- Request:
  - ConfigUpdateRequest
- Response:
  - 200 ConfigValueResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - key existence and value type
- Webhook dependencies:
  - configuration update event

### 13.9 GET /api/v1/admin/audit
- Request:
  - query params: actor, entityType, action, page, pageSize
- Response:
  - 200 AuditLogPageResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - action and entity filters
- Webhook dependencies:
  - none

### 13.10 GET /api/v1/admin/analytics
- Request:
  - query params: metric, dateFrom, dateTo
- Response:
  - 200 AnalyticsSummaryResponse
- Authentication:
  - JWT required, admin
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - supported metrics
- Webhook dependencies:
  - none

---

## 14. Operations APIs

### 14.1 GET /api/v1/operations/dashboard
- Request:
  - query params: dateRange, status
- Response:
  - 200 OperationsDashboardResponse
- Authentication:
  - JWT required, operations/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - n/a
- Validation:
  - date range allowed
- Webhook dependencies:
  - none

### 14.2 GET /api/v1/operations/assignments
- Request:
  - query params: status, agentId, page, pageSize
- Response:
  - 200 AssignmentPageResponse
- Authentication:
  - JWT required, operations/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - status filters
- Webhook dependencies:
  - assignment events

### 14.3 POST /api/v1/operations/assignments
- Request:
  - CreateAssignmentRequest
- Response:
  - 201 AssignmentResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - agent and work item constraints
- Webhook dependencies:
  - assignment event

### 14.4 PATCH /api/v1/operations/assignments/{assignmentId}
- Request:
  - UpdateAssignmentRequest
- Response:
  - 200 AssignmentResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - reassignment rules
- Webhook dependencies:
  - notification to agent/customer

### 14.5 GET /api/v1/operations/escalations
- Request:
  - query params: status, severity, page
- Response:
  - 200 EscalationPageResponse
- Authentication:
  - JWT required, ops/admin
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - severity enum
- Webhook dependencies:
  - escalation event

### 14.6 POST /api/v1/operations/escalations
- Request:
  - CreateEscalationRequest
- Response:
  - 201 EscalationResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - reason and target required
- Webhook dependencies:
  - escalation created event

### 14.7 GET /api/v1/operations/workload
- Request:
  - query params: agentId, dateRange
- Response:
  - 200 WorkloadResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - date range checks
- Webhook dependencies:
  - none

---

## 15. KYC APIs

### 15.1 POST /api/v1/kyc/submissions
- Request:
  - KycSubmissionRequest
- Response:
  - 201 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - document type and required fields
- Webhook dependencies:
  - KYC submission event

### 15.2 GET /api/v1/kyc/submissions/{submissionId}
- Request:
  - pathParam submissionId
- Response:
  - 200 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 404 NotFoundError
- Pagination:
  - n/a
- Validation:
  - submissionId required
- Webhook dependencies:
  - none

### 15.3 GET /api/v1/kyc/review-queue
- Request:
  - query params: status, page, pageSize
- Response:
  - 200 KycReviewPageResponse
- Authentication:
  - JWT required, admin/ops
- Error Model:
  - 403 ForbiddenError
- Pagination:
  - yes
- Validation:
  - status enum
- Webhook dependencies:
  - none

### 15.4 POST /api/v1/kyc/submissions/{submissionId}/review
- Request:
  - KycReviewRequest
- Response:
  - 200 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - reviewer comments and action required
- Webhook dependencies:
  - review event

### 15.5 POST /api/v1/kyc/submissions/{submissionId}/approve
- Request:
  - ApproveKycRequest
- Response:
  - 200 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - only pending or in_review status
- Webhook dependencies:
  - approval event / notification

### 15.6 POST /api/v1/kyc/submissions/{submissionId}/reject
- Request:
  - RejectKycRequest
- Response:
  - 200 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - rejection reason required
- Webhook dependencies:
  - rejection notification

### 15.7 POST /api/v1/kyc/submissions/{submissionId}/reverify
- Request:
  - ReverifyKycRequest
- Response:
  - 200 KycSubmissionResponse
- Authentication:
  - JWT required
- Error Model:
  - 400 ValidationError
- Pagination:
  - n/a
- Validation:
  - reverify allowed status checks
- Webhook dependencies:
  - notification to user

---

# Webhook Contract Generation

Generate dedicated webhook schemas for:
- payment events
- subscription lifecycle events
- notification delivery callbacks
- marketplace vendor/quotation events
- external provider callbacks

Required payload fields:
- eventId
- eventType
- occurredAt
- resourceId
- status
- correlationId
- signature
- payload

Webhook security:
- HMAC verification
- timestamp validation
- replay protection
- idempotency support
- retry policy metadata

---

# OpenAPI Code Generation Requirements

## Required OpenAPI Components
Generate the following sections:
- info
- servers
- securitySchemes
- tags
- paths
- components.schemas
- components.parameters
- components.responses
- components.headers
- components.examples

## Required Tags
- Auth
- Customers
- Properties
- Services
- Visits
- Evidence
- Reports
- Subscriptions
- Payments
- Complaints
- Notifications
- Marketplace
- Admin
- Operations
- KYC

## Required Security
- bearerAuth
- apiKeyAuth, if required for provider or service callbacks
- internalServiceAuth for backend-to-backend calls

---

# Generation Sequence

## Phase 1: Shared Foundation
- base info and servers
- security schemes
- global response models
- pagination models
- validation error models
- enums
- audit metadata
- common params

## Phase 2: Customer and Core User Flows
- auth
- customer
- property
- service catalog
- service requests

## Phase 3: Operational Execution Flows
- visits
- evidence
- complaint
- notification
- marketplace

## Phase 4: Commercial and Compliance Flows
- subscriptions
- payments
- KYC
- invoices
- reports

## Phase 5: Admin and Platform Flows
- admin
- operations
- analytics
- configuration
- audit listing

## Phase 6: Webhooks and Validation
- provider callback contracts
- replay endpoints
- signature validation rules
- contract linting and generation verification

---

# Validation and Quality Gates

Before the OpenAPI contract is accepted:
- all paths must be mapped to screen flows
- all core database entities must have matching endpoints
- all required DTOs must be defined
- all webhooks must be explicitly listed
- all auth requirements must be declared
- all error models must be enforced
- all non-trivial list endpoints must support pagination
- all state-changing operations must support idempotency or duplicate protection where relevant

---

# Risks

Risk | Impact | Mitigation | Owner
---|---|---|---
Unmapped screens to APIs | hidden gaps in client delivery | require traceability matrix review | API Architecture
Schema drift from database | backend/frontend mismatch | contract-code generation gate | Backend Architecture
Missing webhook payload specs | integration failure | dedicated webhook schema review | Integration Engineering
Weak auth coverage | security issue | every endpoint must declare security requirement | Security
Inconsistent pagination | broken UI list flows | standardize across all list endpoints | Frontend Architecture
Overloaded contract | harder maintenance | modularize by domain with shared components | API Architecture
Incomplete validation | invalid input and noisy errors | enforce validation in schema and server tests | QA / Backend
Late API changes | rework overhead | freeze contract review milestones | Engineering Manager

---

# Success Criteria

The OpenAPI contract is ready for:
- backend generation
- frontend integration
- testing
- production deployment

Specifically:
- all primary screen flows are mapped to endpoints
- all major domain models are represented in schemas
- auth and RBAC are declared for every endpoint
- validation rules are complete and explicit
- pagination and filtering are standardized
- payment and subscription webhooks are complete
- admin and operational APIs are defined
- report and evidence endpoints are complete
- contract linting and generated SDK/client checks pass

---

# Final Recommendation

PropertyPilot should treat OpenAPI as the source of truth for backend and frontend implementation. The recommended generation flow is:

1. finalize shared components and enums
2. generate the customer and property domains
3. generate operational execution flows
4. generate billing and subscription contracts
5. generate admin and operations contracts
6. generate webhook contracts and validation rules
7. run linting, mock generation, and contract verification before merge

This ensures the final OpenAPI contract is complete, stable, and production-grade across all business domains.
