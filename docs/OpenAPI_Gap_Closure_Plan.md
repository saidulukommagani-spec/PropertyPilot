# OpenAPI Gap Closure Plan

## Purpose

This plan closes the difference between [API Catalog](05_APIs/API_Catalog.md) and the canonical [OpenAPI Specification](05_APIs/OpenAPI_Specification.yaml), using [Cross-Cutting Requirements](Cross_Cutting_Requirements.md) for API conventions and security rules.

## Assessment Result

- API Catalog operations detected: **107**
- OpenAPI operations detected: **6**
- Exact catalog operations currently represented in OpenAPI: `GET /service-requests`, `POST /service-requests`, and `GET /reports`
- Catalog operations missing from OpenAPI: **104**

The current OpenAPI also requires reconciliation for `/api/v1` base path, property/service-request field naming, common error responses, authentication schemes, pagination, idempotency, and callback/webhook references. This plan does not introduce new functionality; it specifies the missing contracts for existing catalogued operations.

## Common OpenAPI Requirements

Every operation below shall be published under `/api/v1`, use JSON payloads, UUID identifiers, UTC ISO-8601 timestamps, request ID/correlation support, and the approved security scheme.

| Area | Required contract standard |
|---|---|
| Security | Declare OAuth/JWT or approved bearer scheme; apply RBAC plus tenant/customer/property ownership scope. Privileged admin, finance, KYC, vendor, and analytics routes require role-specific scopes. |
| Pagination | List responses use documented filters, stable sorting, `page`, `page_size` (maximum 100), and standard collection response. |
| Idempotency | `POST`/mutation operations with financial, operational, assignment, notification, or lifecycle side effects accept `Idempotency-Key`. |
| Error envelope | `code`, safe `message`, `request_id`, optional `details`; never expose secret, PII, or another tenant’s data. |
| Standard errors | `400 VALIDATION_ERROR`, `401 UNAUTHENTICATED`, `403 FORBIDDEN`, `404 RESOURCE_NOT_FOUND`, `409 RESOURCE_STATE_CONFLICT`/`DUPLICATE_REQUEST`, `422 BUSINESS_RULE_VIOLATION`, `429 RATE_LIMITED`, `500 INTERNAL_ERROR`, `502 PROVIDER_ERROR`, `503 SERVICE_UNAVAILABLE`. |
| Response schemas | Every successful and error response declares schema, examples, status code, and sensitive-field/masking rule. |

## Missing API Contract Families

Each semicolon-delimited endpoint below is a distinct missing OpenAPI operation. It uses the family request/response/security/error contract specified on the same row; operation-specific IDs/path parameters and business validation are required in the final OpenAPI definition.

| Family / Missing Endpoint(s) | Request Schema | Response Schema | Security | Error Codes | Priority |
|---|---|---|---|---|---|
| **Authentication** — `POST /auth/otp/request` | `OtpRequest`: identifier, purpose, deliveryChannel; idempotency key where provider dispatch occurs. | `OtpChallengeAccepted`: challengeId, expiresAt, deliveryStatus. | Public rate-limited endpoint; device/IP/identifier throttling; no account enumeration. | `400`, `429`, `502`, `503`. | P0 |
| **Administration: partners/services/plans/pricing** — `GET /admin/partners`; `GET /admin/partners/{partnerId}/services`; `GET /admin/pricing-rules`; `GET /admin/services`; `GET /admin/subscription-plans`; `POST /admin/partners`; `POST /admin/pricing-simulations`; `POST /admin/services`; `POST /admin/subscription-plans`; `PUT /admin/partners/{partnerId}`; `PUT /admin/partners/{partnerId}/services`; `PUT /admin/pricing-rules`; `PUT /admin/services/{serviceId}`; `PUT /admin/subscription-plans/{planId}` | `PartnerCreate/Update`, `PartnerServiceMapping`, `ServiceCreate/Update`, `SubscriptionPlanCreate/Update`, `PricingRuleCreate/Update`, `PricingSimulationRequest`; list filters. | `Partner`, `PartnerServiceMapping`, `Service`, `SubscriptionPlan`, `PricingRule`, `PricingSimulationResult`, paged collections. | Admin RBAC; pricing/configuration approval scope; audit each mutation. | All standard; `409` active-version/conflict; `422` invalid effective range/eligibility. | P1 |
| **Agent and task operations** — `POST /agents`; `PUT /agents/{agentId}`; `GET /agents/{agentId}/reports`; `GET /agents/{agentId}/skills`; `PUT /agents/{agentId}/skills`; `GET /agents/{agentId}/tasks`; `GET /agent-tasks/{taskId}`; `PATCH /agent-tasks/{taskId}` | `AgentCreate/Update`, `AgentSkillSet`, `TaskStatusUpdate`; task list filters. | `Agent`, `AgentSkill`, `AgentTask`, `ReportSummary`, paged collections. | Field Operations/Admin; agent self-scope only for own tasks; assignment/role checks. | Standard; `409` stale task/state; `422` unavailable/unqualified agent. | P0 — task persistence/schema remains a dependency. |
| **Customer address, property, invoice, subscription views** — `GET /customers/{customerId}/addresses`; `POST /customers/{customerId}/addresses`; `PUT /customers/{customerId}/addresses/{addressId}`; `DELETE /customers/{customerId}/addresses/{addressId}`; `GET /customers/{customerId}/properties`; `GET /customers/{customerId}/invoices`; `GET /customers/{customerId}/subscriptions` | `CustomerAddressCreate/Update`; collection query parameters. | `CustomerAddress`, `PropertySummary`, `InvoiceSummary`, `SubscriptionSummary`, paged collections; `204` on delete. | Customer self, authorised co-owner/representative, support/admin with ownership checks. | Standard; `409` address/reference constraint; `422` ownership/address validation. | P0 |
| **Property documents, owners, verification** — `GET /properties/{propertyId}/documents`; `POST /properties/{propertyId}/documents`; `GET /properties/{propertyId}/documents/{documentId}`; `DELETE /properties/{propertyId}/documents/{documentId}`; `GET /properties/{propertyId}/owners`; `PATCH /properties/{propertyId}/owners/{ownerId}`; `POST /properties/{propertyId}/owners/invitations`; `POST /properties/{propertyId}/owners/{ownerId}/approval`; `GET /properties/{propertyId}/verifications` | `DocumentUploadInitiation/Metadata`, `PropertyOwnerUpdate`, `PropertyOwnerInvitation`, `OwnershipApproval`; filters. | Controlled `DocumentMetadata`/signed access reference, `PropertyOwner`, `Invitation`, `VerificationSummary`, collections. | Customer owner/representative, authorised operations/admin; document/evidence access checks. | Standard; `409` ownership transition/invite conflict; `422` authority/evidence/verification invalid. | P0 |
| **Property monitoring** — `POST /property-monitoring/requests`; `GET /property-monitoring/requests/{requestId}`; `GET /property-monitoring/visits`; `GET /property-monitoring/alerts`; `PATCH /property-monitoring/alerts/{alertId}` | `MonitoringRequestCreate`, `MonitoringAlertUpdate`; list filters. | `MonitoringRequest`, `MonitoringVisit`, `MonitoringAlert`, paged collections. | Customer/property owner, NRI relationship manager, Operations; property scope enforcement. | Standard; `409` alert/status transition; `422` inactive subscription/eligibility. | P1 |
| **Service catalogue, estimate, comparison** — `GET /services`; `GET /services/{serviceId}`; `POST /services/estimates`; `POST /services/comparisons` | Service filters; `PricingEstimateRequest`; `ServiceComparisonRequest`. | `Service`, `PricingEstimate`, `ServiceComparison`, paged collection. | Public/authenticated catalogue as configured; estimate requires relevant customer/property access. | Standard; `422` coverage/eligibility/price input; `429` calculator abuse control. | P0 |
| **Service request detail, status, cancellation, quotation, rating** — `GET /service-requests/{requestId}`; `PATCH /service-requests/{requestId}/status`; `POST /service-requests/{requestId}/cancel`; `GET /service-requests/{requestId}/quotations`; `POST /service-requests/{requestId}/quotations/{quotationId}/select`; `POST /service-requests/{requestId}/ratings` | `ServiceRequestStatusTransition`, `ServiceCancellationRequest`, `QuotationSelectionRequest`, `ServiceRatingCreate`; quotation filters. | `ServiceRequest`, `ServiceRequestStatusHistory`, `CancellationOutcome`, `Quotation`, `ServiceRating`, collection. | Requester/authorised owner, assigned Operations/vendor role, finance/admin for exceptions; state/ownership checks. | Standard; `409 SERVICE_STATE_CONFLICT`; `422` cancellation/refund/quotation/rating eligibility. | P0 |
| **Visit, evidence, GPS, report creation** — `POST /visits`; `GET /visits/{visitId}`; `PATCH /visits/{visitId}`; `GET /visits/{visitId}/evidence`; `POST /visits/{visitId}/evidence`; `POST /visits/{visitId}/gps-captures`; `POST /visits/{visitId}/reports` | `VisitCreate/Update`, `EvidenceUploadInitiation`, `GpsCaptureCreate`, `ReportGenerationRequest`. | `Visit`, `EvidenceMetadata`, `GpsCapture`, `ReportGenerationAccepted`, collection. | Assigned agent/vendor and Operations; device/session, assignment, property/visit state checks. | Standard; `409` visit state/duplicate completion; `422` GPS/evidence integrity/assignment invalid; `413` upload limit. | P0 |
| **Report evidence** — `GET /reports/{reportId}/evidence` | Path/query filters. | Controlled `EvidenceMetadata`/authorised retrieval reference collection. | Customer/property owner, assigned Operations/agent, authorised reviewer; report/evidence access policy. | `401`, `403`, `404`, `409` report not deliverable. | P1 |
| **Subscription controls and entitlement** — `POST /subscriptions/compare`; `GET /subscriptions/{subscriptionId}/entitlements`; `GET /subscriptions/{subscriptionId}/visits`; `POST /subscriptions/{subscriptionId}/renew`; `POST /subscriptions/{subscriptionId}/pause`; `POST /subscriptions/{subscriptionId}/resume` | `SubscriptionCompareRequest`, `RenewalRequest`, `SubscriptionPauseRequest`, `SubscriptionResumeRequest`; entitlement/visit filters. | `SubscriptionComparison`, `SubscriptionEntitlement`, `SubscriptionVisit`, `SubscriptionLifecycleOutcome`, collections. | Subscription owner/authorised property owner; Admin exception scope; payment/idempotency controls. | Standard; `409` canonical subscription state conflict; `422` plan/pause/entitlement/payment eligibility. | P0 |
| **Complaints** — `GET /complaints`; `GET /complaints/{complaintId}`; `POST /complaints`; `POST /complaints/{complaintId}/comments`; `PATCH /complaints/{complaintId}/status` | Complaint filters; `ComplaintCreate`, `ComplaintCommentCreate`, `ComplaintStatusTransition`. | `Complaint`, `ComplaintComment`, `ComplaintStatusHistory`, paged collection. | Customer/authorised representative, Operations, assigned resolver; complaint privacy scope. | Standard; `409` invalid status transition; `422` complaint context/authority invalid. | P1 — physical complaint fields are dependency. |
| **Notifications** — `GET /notification-templates`; `PUT /notification-templates/{templateId}`; `POST /notifications/whatsapp` | Template filters; `NotificationTemplateUpdate`; `WhatsAppNotificationRequest`. | `NotificationTemplate`, `NotificationDispatchAccepted`, paged collection. | Admin/template approval scope; customer notification dispatch requires consent/template/channel checks. | Standard; `409` template version conflict; `422` consent/template/channel invalid; `502` provider. | P1 |
| **NRI operations** — `GET /nri/dashboard`; `GET /nri/emergency-alerts`; `GET /nri/properties/{propertyId}/monitoring-summary`; `POST /nri/relationship-manager/messages` | Dashboard/alert filters; `NriRelationshipMessageCreate`. | `NriDashboard`, `EmergencyAlert`, `MonitoringSummary`, `RelationshipMessage`, paged collection. | NRI customer, authorised relationship manager, Operations; strict property/relationship scope. | Standard; `403` relationship unauthorised; `422` inactive relationship/property mismatch. | P1 |
| **Marketplace** — `GET /marketplace/listings`; `POST /marketplace/listings`; `PATCH /marketplace/listings/{listingId}`; `POST /marketplace/inquiries`; `GET /marketplace/leads`; `POST /marketplace/leads/{leadId}/contact-disclosure`; `GET /marketplace/commissions` | Listing filters; `MarketplaceListingCreate/Update`, `MarketplaceInquiryCreate`, `ContactDisclosureDecision`; commission filters. | `MarketplaceListing`, `MarketplaceInquiry`, `MarketplaceLead`, `ContactDisclosureOutcome`, `MarketplaceCommission`, collections. | Listing owner/authorised property owner, approved marketplace/vendor/admin; protected-contact and property/listing separation checks. | Standard; `409` listing/disclosure state; `422` publication/contact-disclosure/commission rule invalid. | P1 |
| **Vendor and vendor jobs** — `GET /vendors`; `PUT /vendors/{vendorId}`; `GET /vendors/{vendorId}/jobs`; `GET /vendors/{vendorId}/payments`; `GET /vendor-jobs/{jobId}`; `PATCH /vendor-jobs/{jobId}`; `POST /vendor-jobs/{jobId}/evidence`; `POST /vendor-jobs/{jobId}/invoices` | Vendor filters/update; `VendorJobUpdate`, `VendorEvidenceUploadInitiation`, `VendorInvoiceCreate`; job/payment filters. | `Vendor`, `VendorJob`, `VendorPaymentSummary`, `EvidenceMetadata`, `VendorInvoice`, collections. | Vendor self-scope, Operations, Finance/Admin; KYC/service mapping/coverage/capacity/assignment checks. | Standard; `409` job state; `422` vendor eligibility/evidence/invoice validation. | P0 — vendor/payment physical schema dependency. |
| **Quotation submission** — `POST /quotations` | `QuotationCreate`: requestId, vendorId, scopeVersion, BOQ/cost/tax/validity/schedule/warranty/evidence references. | `Quotation`: quotationId, version, status, submittedAt, validity/approval state. | Eligible verified vendor or authorised Operations; service-request and scope validation. | Standard; `409` withdrawn/expired/version conflict; `422` vendor/scope/commercial validation. | P1 |
| **Verification and GPS** — `GET /verifications/{verificationId}`; `POST /verifications/gps` | Verification lookup; `GpsVerificationRequest`. | `VerificationStatus`, `GpsVerificationOutcome`. | Subject self/authorised reviewer; field user/assignment scope for GPS verification. | Standard; `403` restricted verification; `422` expired/invalid location/evidence. | P0 — KYC administration family remains additional required contract. |
| **Analytics** — `GET /analytics/customers`; `GET /analytics/agents`; `GET /analytics/operations`; `GET /analytics/revenue` | Query/date range, filters, aggregation/granularity parameters. | `AnalyticsDataset`/metric series with metadata, pagination where row-oriented. | Analytics/Admin scopes; aggregation/masking, export controls, tenant isolation. | Standard; `403` data-access scope; `422` query range/granularity invalid; `429` query limit. | P2 |
| **Invoices** — `GET /invoices`; `GET /invoices/{invoiceId}` | Invoice filters. | `Invoice`, `InvoiceLineItem`, paged collection. | Invoice customer owner, authorised Finance/Operations; financial access/tenant checks. | Standard; `403` financial scope; `404`; `409` invoice state where applicable. | P0 — invoice physical schema dependency. |

## Additional Canonical Contract Gaps

The following are not represented as complete API Catalog endpoint rows or are explicitly identified by traceability, but must be included in the OpenAPI closure work to make catalogued functionality implementable:

- KYC review queue, approve/reject/rework, expiry/reverification, and fraud-escalation operations.
- Subscription upgrade/downgrade, auto-renewal consent, grace/`PAST_DUE`/suspension state, and lifecycle-history operations.
- Payment provider webhook, refund approval/reversal/status, and reconciliation-exception operations.
- Offline synchronisation submit/status/conflict/replay operations.
- Construction project, BOQ, milestone, change-order, inspection, acceptance, warranty, and dispute operations.
- Privacy export/correction/deletion/consent/legal-hold request/status operations.

## Closure Sequence

| Phase | Scope | Acceptance gate |
|---|---|---|
| 1 — Contract foundation | Fix `/api/v1`; define security schemes, common errors, pagination, idempotency, state conflict, standard schema components. | OpenAPI lint/validation and compatibility checks pass. |
| 2 — MVP P0 | Authentication, customer/property, service/visit/evidence/report, pricing, subscription, vendor, invoice/payment-facing operations. | Provider and consumer contract tests; physical schema/migration alignment. |
| 3 — MVP P1 | Monitoring, complaints, notifications, NRI, marketplace, quotation, KYC/Privacy/Construction contracts. | End-to-end traceability to screens, states, events, notifications, and test cases. |
| 4 — P2 | Analytics and non-MVP administrative/optimisation APIs. | Data classification, query limits, performance and operational review. |

## Ownership and Verification

| Control | Owner | Evidence |
|---|---|---|
| OpenAPI operation/schema authoring | API Lead / domain owner | Version-controlled OpenAPI PR with examples and change impact. |
| Business semantics | Product/service/finance/vendor owner | Approved request/response/state/acceptance criteria. |
| Security/privacy | Security/Privacy Lead | Scope, sensitive fields, authorisation, logging, rate-limit and abuse review. |
| Data alignment | Data Architect | Physical entity/field/migration and enum alignment. |
| Contract verification | QA/Engineering Lead | OpenAPI lint, provider tests, compatibility diff, consumer tests, negative/security tests. |

## Related Documents

- [API Catalog](05_APIs/API_Catalog.md)
- [OpenAPI Specification](05_APIs/OpenAPI_Specification.yaml)
- [Cross-Cutting Requirements](Cross_Cutting_Requirements.md)
- [API Versioning and Deprecation Policy](API_Versioning_and_Deprecation_Policy.md)
- [Traceability Matrix](Traceability_Matrix.md)
