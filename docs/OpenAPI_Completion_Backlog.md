````markdown
# OpenAPI Completion Backlog

Document Type: API Contract Completion Backlog  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: API Architecture / Engineering

---

## Purpose

This backlog identifies the remaining OpenAPI contract work required to bring the PropertyPilot API definition to implementation-ready status.

It compares:
- API_Catalog.md
- OpenAPI_Specification.yaml

The goal is to complete the contract for all MVP endpoints, payload models, error handling, webhooks, and security definitions before backend implementation scales beyond the initial sprint boundary.

---

# 1. Completion Summary

| Area | Status | Gap Level |
|---|---|---|
| Core auth flows | Partial | Critical |
| Customer APIs | Partial | High |
| Property APIs | Partial | Critical |
| Service request workflows | Partial | Critical |
| Payments and subscriptions | Partial | Critical |
| Agent assignment APIs | Partial | High |
| Notification APIs | Partial | High |
| Reporting APIs | Partial | Medium |
| Webhooks | Missing | High |
| Security definitions | Missing / Incomplete | Critical |
| Error model coverage | Partial | High |
| Request/response schemas | Partial | Critical |

---

# 2. Missing Endpoints

## Critical

| Priority | Area | Endpoint | Gap | Recommended Action |
|---|---|---|---|---|
| Critical | Authentication | POST /auth/customers/register | API catalog identifies onboarding flow but OpenAPI does not fully describe payload/response states | Add register request, response, and error models |
| Critical | Authentication | POST /auth/customers/otp/send | OTP delivery flow is required but not modeled fully | Add OTP request and response schemas |
| Critical | Authentication | POST /auth/customers/otp/verify | Verification flow is required for login and registration | Add validate OTP request/response model |
| Critical | Authentication | POST /auth/refresh | Token refresh flow is required for session continuity | Add refresh token request/response contract |
| Critical | Authentication | POST /auth/logout | Session termination flow is missing | Add logout request/response model |
| Critical | Customer | GET /customers/{customerId}/properties | Required for property portfolio view | Add route and response schema |
| Critical | Customer | PATCH /customers/{customerId}/preferences | Required for notification and profile preferences | Add route and schema |
| Critical | Property | POST /properties/{propertyId}/verify/gps | GPS verification is central to service booking validation | Add endpoint and validation schema |
| Critical | Property | POST /properties/{propertyId}/verify/ownership | Ownership verification required for property trust and validation | Add endpoint contract |
| Critical | Service | POST /service-requests | Required for booking creation | Ensure request model includes required fields and status states |
| Critical | Service | PATCH /service-requests/{requestId}/status | Required to support workflow transitions | Add transition status schema and validation rules |
| Critical | Service | POST /service-requests/{requestId}/evidence | Evidence upload is required for agent and support process | Add multipart upload contract |
| Critical | Service | GET /service-requests/{requestId}/history | Timeline and audit state are required | Add response schema for timeline events |
| Critical | Payment | POST /payments/initiate | Core payment flow is required; not sufficiently modeled | Add request, response, and provider reference schema |
| Critical | Payment | POST /payments/{paymentId}/confirm | Required for transaction finalization | Add confirm action and status model |
| Critical | Payment | POST /payments/{paymentId}/refund | Required for refund handling and support workflows | Add refund request and response models |
| Critical | Subscription | POST /subscriptions | Required for plan onboarding and billing lifecycle | Add create subscription contract |
| Critical | Subscription | PATCH /subscriptions/{subscriptionId} | Required for renew or change plan lifecycle | Add patch request model |
| Critical | Subscription | POST /subscriptions/{subscriptionId}/cancel | Required for cancellation lifecycle | Add cancellation contract |
| Critical | Subscription | POST /subscriptions/{subscriptionId}/pause | Required for lifecycle controls | Add pause contract |
| Critical | Subscription | POST /subscriptions/{subscriptionId}/resume | Required for lifecycle controls | Add resume contract |
| Critical | Agent | POST /service-requests/{requestId}/assign | Essential for task assignment | Add assignment payload and response |
| Critical | Agent | PATCH /service-requests/{requestId}/assignments/{assignmentId} | Required for reassignment and status changes | Add assignment update contract |
| Critical | Notifications | GET /customers/{customerId}/notifications | Required for notification center | Add list response schema |
| Critical | Notifications | PATCH /notifications/{notificationId}/status | Required for read/update state | Add notification status update schema |
| Critical | Reports | GET /reports/summary | Required for operational dashboard | Add metric summary schema |
| Critical | Reports | GET /reports/service-requests | Required for operations and support reporting | Add result schema |
| Critical | Reports | GET /reports/payments | Required for finance and operations view | Add schema |
| Critical | Reports | POST /reports/export | Required for file export | Add export request/response model |

## High

| Priority | Area | Endpoint | Gap | Recommended Action |
|---|---|---|---|---|
| High | Customer | POST /customers/{customerId}/complaints | Required for complaint workflow | Add complaint create contract |
| High | Customer | GET /customers/{customerId}/complaints | Required for complaint retrieval and support | Add response schema |
| High | Property | GET /properties/search | Needed for search-and-filter behavior | Add query parameter contract |
| High | Property | PATCH /properties/{propertyId}/status | Required for lifecycle transitions | Add status enum schema |
| High | Service | GET /services | Core catalog listing may exist but not fully modeled | Validate all query params and response arrays |
| High | Service | GET /services/{serviceId} | Catalog detail route needs consistent schema | Add response schema |
| High | Service | GET /customers/{customerId}/service-requests | Core request listing | Add response schema |
| High | Service | GET /agents/{agentId}/assignments | Needed for agent dashboard | Add assignment list schema |
| High | Subscription | GET /subscriptions/plans | Required for subscription selection | Add list schema |
| High | Subscription | GET /customers/{customerId}/subscriptions | Required for customer plan overview | Add limit and status schema |
| High | Payment | GET /customers/{customerId}/payments | Required for payment history | Add response schema |
| High | Payment | GET /invoices/{invoiceId} | Needed for receipt and invoice view | Add invoice response schema |
| High | Agent | GET /agents/available | Required for queue and assignment readiness | Add available agent response schema |
| High | Agent | GET /agents/{agentId}/assignments | Required for agent assignment dashboard | Add assignment list schema |
| High | Agent | PATCH /agents/{agentId}/availability | Required for toggle availability state | Add availability request schema |
| High | Reports | GET /reports/agents | Required for agent performance views | Add schema |
| High | Reports | GET /reports/customers | Required for customer usage reporting | Add schema |
| High | Notifications | POST /notifications/send | Required for internal send trigger contract | Add send request and response |
| High | Notifications | GET /notifications/preferences | Required for preference management | Add preference schema |

---

# 3. Missing Schemas

## Critical

| Schema | Purpose | Priority |
|---|---|---|
| CustomerRegistrationRequest | Registration payload | Critical |
| OtpSendRequest | OTP delivery payload | Critical |
| OtpVerifyRequest | OTP verification payload | Critical |
| AuthRefreshRequest | Refresh token contract | Critical |
| AuthSessionResponse | Session and token response | Critical |
| CustomerProfile | Customer profile model | Critical |
| CustomerPreferenceUpdateRequest | Notification and preference updates | Critical |
| PropertyCreateRequest | Property registration payload | Critical |
| PropertyVerifyGpsRequest | GPS verification payload | Critical |
| PropertyVerifyOwnershipRequest | Ownership verification payload | Critical |
| PropertyDetailResponse | Property response object | Critical |
| ServiceCatalogItem | Service definition | Critical |
| ServiceRequestCreateRequest | Booking creation payload | Critical |
| ServiceRequestUpdateRequest | Request update payload | Critical |
| ServiceRequestTimelineEntry | Request timeline and history event | Critical |
| PaymentInitiateRequest | Payment start payload | Critical |
| PaymentConfirmRequest | Payment confirmation payload | Critical |
| PaymentRefundRequest | Refund payload | Critical |
| PaymentResponse | Payment result model | Critical |
| SubscriptionCreateRequest | Subscription lifecycle creation | Critical |
| SubscriptionUpdateRequest | Plan lifecycle update | Critical |
| AssignmentCreateRequest | Assignment payload | Critical |
| AssignmentUpdateRequest | Assignment update payload | Critical |
| NotificationMessage | Notification payload | Critical |
| ReportSummaryResponse | Report metrics model | Critical |

## High

| Schema | Purpose | Priority |
|---|---|---|
| ComplaintCreateRequest | Complaint creation payload | High |
| ComplaintResponse | Complaint record model | High |
| ComplaintStatusHistory | Complaint lifecycle summary | High |
| ReportFilterQuery | Report filtering model | High |
| ExportRequest | Export job payload | High |
| AgentAvailabilityRequest | Availability state payload | High |
| EvidenceUploadRequest | File upload metadata contract | High |
| EvidenceResponse | Uploaded evidence metadata | High |

---

# 4. Missing Request Models

| Priority | Request Model | Missing Because | Notes |
|---|---|---|---|
| Critical | RegisterCustomerRequest | User onboarding is a core flow | Must include phone, consent flags, profile metadata |
| Critical | SendOtpRequest | Auth flow incomplete in OpenAPI | Must include channel and mobile/identifier |
| Critical | VerifyOtpRequest | Login and onboarding rely on OTP verification | Must contain OTP code and identifier |
| Critical | RefreshTokenRequest | Session security is incomplete | Must include refresh token |
| Critical | PropertyCreateRequest | Property onboarding flow missing contract | Must include address, owner, and verification fields |
| Critical | ServiceRequestCreateRequest | Booking flow missing contract | Must include property/service/time/location |
| Critical | PaymentInitiateRequest | Payment lifecycle not fully defined | Must include amount, booking or subscription reference |
| Critical | SubscriptionCreateRequest | Subscription lifecycle not standardized | Must include plan and billing metadata |
| Critical | AssignmentCreateRequest | Assignment workflow missing | Must include agentId and requestId |
| Critical | NotificationStatusUpdateRequest | Notification read/update flow missing | Must include status and actor info |
| High | ComplaintCreateRequest | Complaint handling not in contract | Must include service/property link and description |
| High | EvidenceUploadRequest | Multipart file upload missing contract | Must include metadata and requestId |
| High | ReportExportRequest | Export functionality missing schema | Must define format, filters, and output target |

---

# 5. Missing Response Models

| Priority | Response Model | Missing Because | Notes |
|---|---|---|---|
| Critical | AuthSessionResponse | Not fully defined | Must include token, expiry, status |
| Critical | CustomerResponse | Customer profile contract is incomplete | Must include personal and consent metadata |
| Critical | PropertyResponse | Property detail contract incomplete | Must include verification state and status |
| Critical | ServiceRequestResponse | Booking response is incomplete | Must include status, timeline, assignment, payment |
| Critical | PaymentResponse | Transactions are under-modeled | Must include payment state and provider reference |
| Critical | SubscriptionResponse | Lifecycle details incomplete | Must include plan, state, renewal info |
| Critical | AgentAssignmentResponse | Assignment flow under-defined | Must include assignment status and task metadata |
| Critical | NotificationResponse | Notification center not complete | Must include message, status, channel |
| High | ComplaintResponse | Complaint flow lacks response contract | Must include current status and resolution metadata |
| High | ReportSummaryResponse | Dashboard metrics under-specified | Must include count/value/time bucket models |
| High | InvoiceResponse | Billing record representation missing | Must include status, amount, and issued date |

---

# 6. Missing Error Models

| Priority | Error Model | Gap | Recommended Action |
|---|---|---|---|
| Critical | ValidationErrorResponse | Missing standard validation contract | Add 400 error schema and field validation details |
| Critical | UnauthorizedErrorResponse | Security requirements need explicit auth errors | Add 401 schema |
| Critical | ForbiddenErrorResponse | Authorization roles are not fully modeled | Add 403 schema |
| Critical | NotFoundErrorResponse | Resource missing states not fully described | Add 404 model |
| Critical | ConflictErrorResponse | Duplicate registration or status conflict is not modeled | Add 409 schema |
| Critical | PaymentGatewayErrorResponse | Gateway decline/retry patterns are not represented | Add 402 / 424 / 500 patterns as needed |
| High | RateLimitErrorResponse | OTP abuse protection and throttling require contract | Add 429 schema |
| High | UploadErrorResponse | Evidence upload errors need file validation mapping | Add 413 / 415 models |
| High | RetryableServiceErrorResponse | Integration failure handling needs spec | Add transient error contract |

---

# 7. Missing Webhooks

| Priority | Webhook | Purpose | Notes |
|---|---|---|---|
| Critical | serviceRequest.status.updated | Notify external systems when request status changes | Must include requestId, status, actor, updateTime |
| Critical | payment.status.updated | Notify downstream systems on payment transitions | Required for status reconciliation |
| Critical | property.verification.updated | Trigger on ownership/GPS verification changes | Needed for downstream support and onboarding |
| Critical | subscription.status.updated | Notify when plan lifecycle changes | Required for billing and customer support |
| High | assignment.created | Trigger when an agent is assigned to a request | Needed for agent queue updates |
| High | notification.delivered | Track delivery outcomes | Useful for operational monitoring |
| High | complaint.status.updated | Notify case progress and resolution updates | Needed for support operations |
| Medium | report.job.completed | Trigger when report generation finishes | Useful for dashboards and export flow |

---

# 8. Missing Security Definitions

## Required Security Schemes
| Priority | Security Definition | Purpose | Notes |
|---|---|---|---|
| Critical | bearerAuth | Standard API auth for customer, agent, admin flows | Use JWT or equivalent access token |
| Critical | refreshTokenAuth | Refresh flow for session continuity | Separate scheme or token parameter |
| Critical | otpAuth | Short-lived OTP verification flows | Only for auth-specific endpoints |
| High | apiKeyHeader | External integration or service-to-service auth | For provider and internal platform calls |
| High | mTLS or clientCertificate | High-trust internal flows | For service mesh or private integration use |
| Medium | basicAuth | Legacy fallback only if necessary | Not recommended for new flows |

## Missing Security Requirements by Area
- All authenticated routes must declare security requirements.
- Customer routes must require bearerAuth and appropriate role scope.
- Agent routes should require agent roles.
- Admin and operations routes must require admin or operations scopes.
- Payment and privacy-related routes require tighter role enforcement and consent checks.
- External provider routes and webhook validation must use secret-based or signed payload security.

---

# 9. Missing Response Headers / Standards

| Item | Gap | Recommended Action |
|---|---|---|
| X-Correlation-ID | Not consistently modeled | Add to all API responses and request headers |
| Retry-After | Missing on rate-limit and throttling responses | Add to 429 responses |
| requestId / traceId | Missing in payload conventions | Add to all responses |
| pagination metadata | Missing where list endpoints are paginated | Add page, pageSize, totalCount |
| content-type variants | Missing for file or multipart endpoints | Add explicit multipart/form-data contract |

---

# 10. Priority Backlog by Execution Order

## Phase 1: Critical Contract Completion
1. Authentication and session flows
2. Property verification endpoints
3. Service request lifecycle endpoints
4. Payment lifecycle and refund endpoints
5. Subscription lifecycle endpoints
6. Assignment endpoints
7. Security schemes and auth model

## Phase 2: High Priority Product and Support Contracts
1. Notification center and preferences
2. Complaint handling
3. Agent availability and assignment queue
4. Reporting summaries and export protocols
5. Invoice and payment history schemas

## Phase 3: Operational and Platform Completion
1. Webhooks
2. Event-driven contract modeling
3. Retry, timeout, and transient error models
4. Pagination and header standards
5. Final quality and consistency review

---

# 11. Recommended Backlog Items by Area

## Authentication
- add /auth/customers/register
- add /auth/customers/otp/send
- add /auth/customers/otp/verify
- add /auth/refresh
- add /auth/logout
- add security schemes
- add token/session model

## Customer
- add customer profile update contract
- add notification preference contract
- add complaint flow contract
- add customer payment and subscription history listing

## Property
- add property verification endpoints
- add GPS validation model
- add property search contract
- add property status transition model

## Service
- add service request creation, update, status, and evidence models
- add service request history model
- add service catalog detail and filter schemas

## Payment
- add payment initiation and confirm flows
- add refund and invoice models
- add payment error mapping

## Subscription
- add create/pause/resume/cancel contract
- add plan and status lifecycle schemas

## Agent
- add assignment, availability, and queue schemas
- add task detail and assignment update payloads

## Notifications
- add notification list and read/update contract
- define preferences and delivery status model

## Reporting
- add summary, queue, and export models
- add pagination and filter schema support

---

# 12. Final Recommendation

The OpenAPI contract is not yet implementation-complete. The current gap set is concentrated in:
- authentication and session flows
- property verification flows
- booking and service request lifecycle
- payment and subscription lifecycle
- agent assignment and operations workflows
- missing security definitions, error contracts, and webhooks

These items should be completed before broad backend implementation expands beyond the initial Sprint 1 scope. The priority should be to stabilize the contract around the MVP business flow, then close the remaining support and reporting models in subsequent iterations.

This backlog should be treated as the minimum required OpenAPI completion plan for implementation readiness.
```// filepath: c:\PropertyPilot\docs\OpenAPI_Completion_Backlog.md
# OpenAPI Completion Backlog

Document Type: API Contract Completion Backlog  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: API Architecture / Engineering

---

## Purpose

This backlog identifies the remaining OpenAPI contract work required to bring the PropertyPilot API definition to implementation-ready status.

It compares:
- API_Catalog.md
- OpenAPI_Specification.yaml

The goal is to complete the contract for all MVP endpoints, payload models, error handling, webhooks, and security definitions before backend implementation scales beyond the initial sprint boundary.

---

# 1. Completion Summary

| Area | Status | Gap Level |
|---|---|---|
| Core auth flows | Partial | Critical |
| Customer APIs | Partial | High |
| Property APIs | Partial | Critical |
| Service request workflows | Partial | Critical |
| Payments and subscriptions | Partial | Critical |
| Agent assignment APIs | Partial | High |
| Notification APIs | Partial | High |
| Reporting APIs | Partial | Medium |
| Webhooks | Missing | High |
| Security definitions | Missing / Incomplete | Critical |
| Error model coverage | Partial | High |
| Request/response schemas | Partial | Critical |

---

# 2. Missing Endpoints

## Critical

| Priority | Area | Endpoint | Gap | Recommended Action |
|---|---|---|---|---|
| Critical | Authentication | POST /auth/customers/register | API catalog identifies onboarding flow but OpenAPI does not fully describe payload/response states | Add register request, response, and error models |
| Critical | Authentication | POST /auth/customers/otp/send | OTP delivery flow is required but not modeled fully | Add OTP request and response schemas |
| Critical | Authentication | POST /auth/customers/otp/verify | Verification flow is required for login and registration | Add validate OTP request/response model |
| Critical | Authentication | POST /auth/refresh | Token refresh flow is required for session continuity | Add refresh token request/response contract |
| Critical | Authentication | POST /auth/logout | Session termination flow is missing | Add logout request/response model |
| Critical | Customer | GET /customers/{customerId}/properties | Required for property portfolio view | Add route and response schema |
| Critical | Customer | PATCH /customers/{customerId}/preferences | Required for notification and profile preferences | Add route and schema |
| Critical | Property | POST /properties/{propertyId}/verify/gps | GPS verification is central to service booking validation | Add endpoint and validation schema |
| Critical | Property | POST /properties/{propertyId}/verify/ownership | Ownership verification required for property trust and validation | Add endpoint contract |
| Critical | Service | POST /service-requests | Required for booking creation | Ensure request model includes required fields and status states |
| Critical | Service | PATCH /service-requests/{requestId}/status | Required to support workflow transitions | Add transition status schema and validation rules |
| Critical | Service | POST /service-requests/{requestId}/evidence | Evidence upload is required for agent and support process | Add multipart upload contract |
| Critical | Service | GET /service-requests/{requestId}/history | Timeline and audit state are required | Add response schema for timeline events |
| Critical | Payment | POST /payments/initiate | Core payment flow is required; not sufficiently modeled | Add request, response, and provider reference schema |
| Critical | Payment | POST /payments/{paymentId}/confirm | Required for transaction finalization | Add confirm action and status model |
| Critical | Payment | POST /payments/{paymentId}/refund | Required for refund handling and support workflows | Add refund request and response models |
| Critical | Subscription | POST /subscriptions | Required for plan onboarding and billing lifecycle | Add create subscription contract |
| Critical | Subscription | PATCH /subscriptions/{subscriptionId} | Required for renew or change plan lifecycle | Add patch request model |
| Critical | Subscription | POST /subscriptions/{subscriptionId}/cancel | Required for cancellation lifecycle | Add cancellation contract |
| Critical | Subscription | POST /subscriptions/{subscriptionId}/pause | Required for lifecycle controls | Add pause contract |
| Critical | Subscription | POST /subscriptions/{subscriptionId}/resume | Required for lifecycle controls | Add resume contract |
| Critical | Agent | POST /service-requests/{requestId}/assign | Essential for task assignment | Add assignment payload and response |
| Critical | Agent | PATCH /service-requests/{requestId}/assignments/{assignmentId} | Required for reassignment and status changes | Add assignment update contract |
| Critical | Notifications | GET /customers/{customerId}/notifications | Required for notification center | Add list response schema |
| Critical | Notifications | PATCH /notifications/{notificationId}/status | Required for read/update state | Add notification status update schema |
| Critical | Reports | GET /reports/summary | Required for operational dashboard | Add metric summary schema |
| Critical | Reports | GET /reports/service-requests | Required for operations and support reporting | Add result schema |
| Critical | Reports | GET /reports/payments | Required for finance and operations view | Add schema |
| Critical | Reports | POST /reports/export | Required for file export | Add export request/response model |

## High

| Priority | Area | Endpoint | Gap | Recommended Action |
|---|---|---|---|---|
| High | Customer | POST /customers/{customerId}/complaints | Required for complaint workflow | Add complaint create contract |
| High | Customer | GET /customers/{customerId}/complaints | Required for complaint retrieval and support | Add response schema |
| High | Property | GET /properties/search | Needed for search-and-filter behavior | Add query parameter contract |
| High | Property | PATCH /properties/{propertyId}/status | Required for lifecycle transitions | Add status enum schema |
| High | Service | GET /services | Core catalog listing may exist but not fully modeled | Validate all query params and response arrays |
| High | Service | GET /services/{serviceId} | Catalog detail route needs consistent schema | Add response schema |
| High | Service | GET /customers/{customerId}/service-requests | Core request listing | Add response schema |
| High | Service | GET /agents/{agentId}/assignments | Needed for agent dashboard | Add assignment list schema |
| High | Subscription | GET /subscriptions/plans | Required for subscription selection | Add list schema |
| High | Subscription | GET /customers/{customerId}/subscriptions | Required for customer plan overview | Add limit and status schema |
| High | Payment | GET /customers/{customerId}/payments | Required for payment history | Add response schema |
| High | Payment | GET /invoices/{invoiceId} | Needed for receipt and invoice view | Add invoice response schema |
| High | Agent | GET /agents/available | Required for queue and assignment readiness | Add available agent response schema |
| High | Agent | GET /agents/{agentId}/assignments | Required for agent assignment dashboard | Add assignment list schema |
| High | Agent | PATCH /agents/{agentId}/availability | Required for toggle availability state | Add availability request schema |
| High | Reports | GET /reports/agents | Required for agent performance views | Add schema |
| High | Reports | GET /reports/customers | Required for customer usage reporting | Add schema |
| High | Notifications | POST /notifications/send | Required for internal send trigger contract | Add send request and response |
| High | Notifications | GET /notifications/preferences | Required for preference management | Add preference schema |

---

# 3. Missing Schemas

## Critical

| Schema | Purpose | Priority |
|---|---|---|
| CustomerRegistrationRequest | Registration payload | Critical |
| OtpSendRequest | OTP delivery payload | Critical |
| OtpVerifyRequest | OTP verification payload | Critical |
| AuthRefreshRequest | Refresh token contract | Critical |
| AuthSessionResponse | Session and token response | Critical |
| CustomerProfile | Customer profile model | Critical |
| CustomerPreferenceUpdateRequest | Notification and preference updates | Critical |
| PropertyCreateRequest | Property registration payload | Critical |
| PropertyVerifyGpsRequest | GPS verification payload | Critical |
| PropertyVerifyOwnershipRequest | Ownership verification payload | Critical |
| PropertyDetailResponse | Property response object | Critical |
| ServiceCatalogItem | Service definition | Critical |
| ServiceRequestCreateRequest | Booking creation payload | Critical |
| ServiceRequestUpdateRequest | Request update payload | Critical |
| ServiceRequestTimelineEntry | Request timeline and history event | Critical |
| PaymentInitiateRequest | Payment start payload | Critical |
| PaymentConfirmRequest | Payment confirmation payload | Critical |
| PaymentRefundRequest | Refund payload | Critical |
| PaymentResponse | Payment result model | Critical |
| SubscriptionCreateRequest | Subscription lifecycle creation | Critical |
| SubscriptionUpdateRequest | Plan lifecycle update | Critical |
| AssignmentCreateRequest | Assignment payload | Critical |
| AssignmentUpdateRequest | Assignment update payload | Critical |
| NotificationMessage | Notification payload | Critical |
| ReportSummaryResponse | Report metrics model | Critical |

## High

| Schema | Purpose | Priority |
|---|---|---|
| ComplaintCreateRequest | Complaint creation payload | High |
| ComplaintResponse | Complaint record model | High |
| ComplaintStatusHistory | Complaint lifecycle summary | High |
| ReportFilterQuery | Report filtering model | High |
| ExportRequest | Export job payload | High |
| AgentAvailabilityRequest | Availability state payload | High |
| EvidenceUploadRequest | File upload metadata contract | High |
| EvidenceResponse | Uploaded evidence metadata | High |

---

# 4. Missing Request Models

| Priority | Request Model | Missing Because | Notes |
|---|---|---|---|
| Critical | RegisterCustomerRequest | User onboarding is a core flow | Must include phone, consent flags, profile metadata |
| Critical | SendOtpRequest | Auth flow incomplete in OpenAPI | Must include channel and mobile/identifier |
| Critical | VerifyOtpRequest | Login and onboarding rely on OTP verification | Must contain OTP code and identifier |
| Critical | RefreshTokenRequest | Session security is incomplete | Must include refresh token |
| Critical | PropertyCreateRequest | Property onboarding flow missing contract | Must include address, owner, and verification fields |
| Critical | ServiceRequestCreateRequest | Booking flow missing contract | Must include property/service/time/location |
| Critical | PaymentInitiateRequest | Payment lifecycle not fully defined | Must include amount, booking or subscription reference |
| Critical | SubscriptionCreateRequest | Subscription lifecycle not standardized | Must include plan and billing metadata |
| Critical | AssignmentCreateRequest | Assignment workflow missing | Must include agentId and requestId |
| Critical | NotificationStatusUpdateRequest | Notification read/update flow missing | Must include status and actor info |
| High | ComplaintCreateRequest | Complaint handling not in contract | Must include service/property link and description |
| High | EvidenceUploadRequest | Multipart file upload missing contract | Must include metadata and requestId |
| High | ReportExportRequest | Export functionality missing schema | Must define format, filters, and output target |

---

# 5. Missing Response Models

| Priority | Response Model | Missing Because | Notes |
|---|---|---|---|
| Critical | AuthSessionResponse | Not fully defined | Must include token, expiry, status |
| Critical | CustomerResponse | Customer profile contract is incomplete | Must include personal and consent metadata |
| Critical | PropertyResponse | Property detail contract incomplete | Must include verification state and status |
| Critical | ServiceRequestResponse | Booking response is incomplete | Must include status, timeline, assignment, payment |
| Critical | PaymentResponse | Transactions are under-modeled | Must include payment state and provider reference |
| Critical | SubscriptionResponse | Lifecycle details incomplete | Must include plan, state, renewal info |
| Critical | AgentAssignmentResponse | Assignment flow under-defined | Must include assignment status and task metadata |
| Critical | NotificationResponse | Notification center not complete | Must include message, status, channel |
| High | ComplaintResponse | Complaint flow lacks response contract | Must include current status and resolution metadata |
| High | ReportSummaryResponse | Dashboard metrics under-specified | Must include count/value/time bucket models |
| High | InvoiceResponse | Billing record representation missing | Must include status, amount, and issued date |

---

# 6. Missing Error Models

| Priority | Error Model | Gap | Recommended Action |
|---|---|---|---|
| Critical | ValidationErrorResponse | Missing standard validation contract | Add 400 error schema and field validation details |
| Critical | UnauthorizedErrorResponse | Security requirements need explicit auth errors | Add 401 schema |
| Critical | ForbiddenErrorResponse | Authorization roles are not fully modeled | Add 403 schema |
| Critical | NotFoundErrorResponse | Resource missing states not fully described | Add 404 model |
| Critical | ConflictErrorResponse | Duplicate registration or status conflict is not modeled | Add 409 schema |
| Critical | PaymentGatewayErrorResponse | Gateway decline/retry patterns are not represented | Add 402 / 424 / 500 patterns as needed |
| High | RateLimitErrorResponse | OTP abuse protection and throttling require contract | Add 429 schema |
| High | UploadErrorResponse | Evidence upload errors need file validation mapping | Add 413 / 415 models |
| High | RetryableServiceErrorResponse | Integration failure handling needs spec | Add transient error contract |

---

# 7. Missing Webhooks

| Priority | Webhook | Purpose | Notes |
|---|---|---|---|
| Critical | serviceRequest.status.updated | Notify external systems when request status changes | Must include requestId, status, actor, updateTime |
| Critical | payment.status.updated | Notify downstream systems on payment transitions | Required for status reconciliation |
| Critical | property.verification.updated | Trigger on ownership/GPS verification changes | Needed for downstream support and onboarding |
| Critical | subscription.status.updated | Notify when plan lifecycle changes | Required for billing and customer support |
| High | assignment.created | Trigger when an agent is assigned to a request | Needed for agent queue updates |
| High | notification.delivered | Track delivery outcomes | Useful for operational monitoring |
| High | complaint.status.updated | Notify case progress and resolution updates | Needed for support operations |
| Medium | report.job.completed | Trigger when report generation finishes | Useful for dashboards and export flow |

---

# 8. Missing Security Definitions

## Required Security Schemes
| Priority | Security Definition | Purpose | Notes |
|---|---|---|---|
| Critical | bearerAuth | Standard API auth for customer, agent, admin flows | Use JWT or equivalent access token |
| Critical | refreshTokenAuth | Refresh flow for session continuity | Separate scheme or token parameter |
| Critical | otpAuth | Short-lived OTP verification flows | Only for auth-specific endpoints |
| High | apiKeyHeader | External integration or service-to-service auth | For provider and internal platform calls |
| High | mTLS or clientCertificate | High-trust internal flows | For service mesh or private integration use |
| Medium | basicAuth | Legacy fallback only if necessary | Not recommended for new flows |

## Missing Security Requirements by Area
- All authenticated routes must declare security requirements.
- Customer routes must require bearerAuth and appropriate role scope.
- Agent routes should require agent roles.
- Admin and operations routes must require admin or operations scopes.
- Payment and privacy-related routes require tighter role enforcement and consent checks.
- External provider routes and webhook validation must use secret-based or signed payload security.

---

# 9. Missing Response Headers / Standards

| Item | Gap | Recommended Action |
|---|---|---|
| X-Correlation-ID | Not consistently modeled | Add to all API responses and request headers |
| Retry-After | Missing on rate-limit and throttling responses | Add to 429 responses |
| requestId / traceId | Missing in payload conventions | Add to all responses |
| pagination metadata | Missing where list endpoints are paginated | Add page, pageSize, totalCount |
| content-type variants | Missing for file or multipart endpoints | Add explicit multipart/form-data contract |

---

# 10. Priority Backlog by Execution Order

## Phase 1: Critical Contract Completion
1. Authentication and session flows
2. Property verification endpoints
3. Service request lifecycle endpoints
4. Payment lifecycle and refund endpoints
5. Subscription lifecycle endpoints
6. Assignment endpoints
7. Security schemes and auth model

## Phase 2: High Priority Product and Support Contracts
1. Notification center and preferences
2. Complaint handling
3. Agent availability and assignment queue
4. Reporting summaries and export protocols
5. Invoice and payment history schemas

## Phase 3: Operational and Platform Completion
1. Webhooks
2. Event-driven contract modeling
3. Retry, timeout, and transient error models
4. Pagination and header standards
5. Final quality and consistency review

---

# 11. Recommended Backlog Items by Area

## Authentication
- add /auth/customers/register
- add /auth/customers/otp/send
- add /auth/customers/otp/verify
- add /auth/refresh
- add /auth/logout
- add security schemes
- add token/session model

## Customer
- add customer profile update contract
- add notification preference contract
- add complaint flow contract
- add customer payment and subscription history listing

## Property
- add property verification endpoints
- add GPS validation model
- add property search contract
- add property status transition model

## Service
- add service request creation, update, status, and evidence models
- add service request history model
- add service catalog detail and filter schemas

## Payment
- add payment initiation and confirm flows
- add refund and invoice models
- add payment error mapping

## Subscription
- add create/pause/resume/cancel contract
- add plan and status lifecycle schemas

## Agent
- add assignment, availability, and queue schemas
- add task detail and assignment update payloads

## Notifications
- add notification list and read/update contract
- define preferences and delivery status model

## Reporting
- add summary, queue, and export models
- add pagination and filter schema support

---

# 12. Final Recommendation

The OpenAPI contract is not yet implementation-complete. The current gap set is concentrated in:
- authentication and session flows
- property verification flows
- booking and service request lifecycle
- payment and subscription lifecycle
- agent assignment and operations workflows
- missing security definitions, error contracts, and webhooks

These items should be completed before broad backend implementation expands beyond the initial Sprint 1 scope. The priority should be to stabilize the contract around the MVP business flow, then close the remaining support and reporting models in subsequent iterations.

This backlog should be treated as the minimum required OpenAPI completion plan for implementation readiness.
