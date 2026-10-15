# PropertyPilot Security Remediation Plan

**Assessment date:** 2026-10-06  
**Scope:** Current backend source and configuration, with emphasis on pilot customer, agent, and operations access. This is a remediation plan, not a code change.

## Executive Summary

The backend has a meaningful security foundation: JWT signing and expiry validation, BCrypt password hashing, stateless Spring Security, role checks, and resource ownership helpers in SecurityService. Several services enforce customer/property/request/visit/evidence/invoice access. The gaps are inconsistent use of those controls across route families, broad authenticated-only matchers, no single tested authorization contract, a payment endpoint that marks client-submitted payments successful, and document/evidence metadata that trusts caller-provided file URLs.

**Pilot security posture: NOT READY for customer data or real payments.** Treat P0 work as a release gate. Keep payment, subscription renewal, and customer-visible evidence/file distribution disabled until the relevant P0 work is complete and tested.

## Scope and priorities

- **P0 — Critical:** Close before pilot users or real payments.
- **P1 — High:** Complete before a wider pilot; P1 items affecting the selected pilot service must also be release gates.
- **P2 — Medium:** Complete before expanding roles, traffic, or functionality.
- **P3 — Low:** Defense-in-depth and maintainability improvements after core controls are proven.

Effort is engineer-days, rounded planning estimates. Items can share test/setup work.

## Existing controls verified

- JwtService requires a non-empty JWT secret of at least 32 UTF-8 bytes and positive expiration; JJWT verifies signed claims and expiration when parsing.
- JwtAuthenticationFilter rejects invalid bearer tokens by clearing the security context; CustomUserDetailsService reloads active user and effective active roles.
- PasswordConfig uses BCryptPasswordEncoder.
- SecurityConfig is stateless, requires authentication by default, and exposes only selected auth/docs/health paths.
- SecurityService includes customer, property, service request, assigned-agent, visit, evidence, document, invoice, admin and operations checks.
- User registration creates only CUSTOMER role in UserServiceImpl; the public request does not accept an arbitrary role.
- PropertyServiceImpl derives new property ownership from the authenticated customer. Quote, service request, visit, evidence, document, payment, and several CRM services contain resource-specific checks.

These controls are valuable but are not applied uniformly at both route and service layers, and the current controller tests do not establish complete denial behavior for all role/resource combinations.

## Prioritized Remediation Plan

### P0 — Critical

| ID / area | Severity | Current issue and business impact | Exact files involved | Recommended fix | Effort |
|---|---|---|---|---|---:|
| P0.1 Authorization policy and route coverage | CRITICAL | Route protection is split between SecurityConfig, controller annotations, and service checks. Many controllers have no method-level role annotations and rely on service behavior; some URL patterns fall through to generic authenticated access. A missed service check can expose or mutate customer data. There is also a known mismatch: SecurityConfig restricts /api/admin/** to ADMIN while AdminOperationsController allows ADMIN or OPERATIONS, so intended operations access is denied at the filter. | propertypilot-backend/src/main/java/com/propertypilot/config/SecurityConfig.java; web/controller/*.java, especially AdminOperationsController.java, AgentDashboardController.java, PropertyController.java, LeadController.java, DealController.java, CustomerSubscriptionController.java, CustomerAddressController.java, CustomerPreferenceController.java, CustomerFavoriteController.java, CustomerSearchHistoryController.java, DocumentController.java, ServiceRequestController.java, VisitController.java, EvidenceController.java, PaymentController.java, BillingController.java | Create an endpoint-to-role matrix for actual controller paths. Align matcher prefixes and HTTP methods with controller annotations. Use explicit roles for reads/writes and retain service-level object checks. Resolve OPERATIONS policy intentionally. Reject unspecified route families by default. | 5–8 |
| P0.2 Customer/property/resource isolation | CRITICAL | SecurityService has sound ownership helpers, but security depends on every service method calling the right helper. ID-based reads and writes across properties, leads/deals, subscriptions, visits, documents, evidence, invoices/payments and reports can become IDORs if one path omits validation. Operations bypass is broad and must be role-appropriate. | security/SecurityService.java; application/service/impl/PropertyServiceImpl.java; LeadServiceImpl.java; DealServiceImpl.java; CustomerSubscriptionServiceImpl.java; CustomerSubscriptionAddOnServiceImpl.java; ServiceRequestServiceImpl.java; VisitServiceImpl.java; EvidenceServiceImpl.java; DocumentServiceImpl.java; BillingServiceImpl.java; PaymentServiceImpl.java; ReportServiceImpl.java; corresponding controllers | Centralize resource authorization at the application-service boundary. For each identifier-based action load the resource, authorize through its owning customer/property/request/assignment, then mutate. Do not accept customerId, agentId, assignedBy, amount, status, or actor identity as authority from the caller. Define whether co-owners are supported and apply that same ownership relation consistently. | 6–10 |
| P0.3 Payment settlement authority | CRITICAL | PaymentServiceImpl.recordPayment accepts payment/provider identifiers from the request, creates a SUCCESS payment, and marks the invoice PAID without a gateway confirmation. A customer can assert settlement and trigger paid-state behavior; forged, duplicate, or mismatched provider references are not verified. | application/service/impl/PaymentServiceImpl.java; web/controller/PaymentController.java; application/dto/CreatePaymentRequest.java; infrastructure/persistence/entity/PaymentEntity.java; InvoiceEntity.java; PaymentRepository.java; InvoiceRepository.java; db/migration/*payment*.sql (new additive migration if needed) | Replace record-as-success with server-created provider payment intent/order, pending state, signed webhook/callback verification, provider reference uniqueness, stable idempotency key, amount/currency/invoice matching, and server-only transition to SUCCESS/PAID. Add failure/refund/reconciliation handling. Do not trust provider, providerPaymentId, or success status from customer input. | 10–18 plus provider onboarding |
| P0.4 Admin/user privilege protection | CRITICAL | User endpoints include public POST creation plus list/read/update/delete routes. Service and URL rules currently protect admin operations and public create only creates CUSTOMER, which is good; this boundary must remain explicit and regression-tested because account/role administration is highly privileged. Admin/operations access also differs between service checks and route policies. | web/controller/UserController.java; application/service/impl/UserServiceImpl.java; application/dto/UserCreateRequest.java; config/SecurityConfig.java; security/SecurityService.java | Preserve self-registration as CUSTOMER-only; ensure no request DTO can bind roles/status/customer IDs. Keep list/read/update/delete behind ADMIN in both matcher and service. Add tests for anonymous create, customer self-update/delete attempts, and staff role escalation attempts. | 2–3 |
| P0.5 Evidence and document access | CRITICAL | Evidence creation accepts caller-provided fileUrl and checksum metadata. Document records/version APIs exist, but repository code does not establish a controlled upload/download service boundary. A guessed/public URL or URL substitution can disclose property records or allow untrusted content; service ownership on metadata reads alone does not secure file bytes. | web/controller/EvidenceController.java; application/service/impl/EvidenceServiceImpl.java; application/dto/CreateEvidenceRequest.java; infrastructure/persistence/entity/EvidenceEntity.java; web/controller/DocumentController.java; application/service/impl/DocumentServiceImpl.java; application/dto/DocumentCreateRequest.java; DocumentVersionCreateRequest.java; evidence/document entities and repositories | Put files in private object storage. Issue short-lived, resource-scoped upload/download grants from the server; verify ownership and assigned-agent permission before issuing them. Validate actual bytes, size, extension/MIME, checksum, malware policy, and retention. Store object key rather than trusting a URL; audit upload, download, archive and restore. | 8–14 |
| P0.6 Payment and invoice data ownership | CRITICAL | PaymentController allows CUSTOMER/ADMIN at the route and PaymentServiceImpl checks invoice ownership, which is positive. BillingController invoice list/detail routes are ADMIN-only despite customer-specific service checks, while other paths use broad authentication. Inconsistent access can either block customers or expose invoice data if a controller is broadened without service checks. | web/controller/PaymentController.java; BillingController.java; application/service/impl/BillingServiceImpl.java; PaymentServiceImpl.java; security/SecurityService.java | Make invoice/payment route policy explicit: customer routes derive current customer identity; staff routes require the intended finance/operations role; every invoice/payment lookup checks ownership in service. Avoid customerId path parameters as the authority for self-service views. | 2–4 |
| P0.7 Security regression tests | CRITICAL | Existing tests include controller/unit coverage, but no complete authorization matrix was demonstrated for customer A vs customer B, assigned vs unassigned agent, operations vs admin, and anonymous access across all sensitive APIs. Unit happy paths cannot prove the security boundary. | propertypilot-backend/src/test/java/com/propertypilot/web/controller/*Test.java; propertypilot-backend/src/test/java/com/propertypilot/security/* (add tests); pom.xml (Testcontainers dependencies already present) | Add Spring Security HTTP tests with real filters and PostgreSQL-backed ownership fixtures. Test allow and deny cases for each sensitive route/action and verify denied calls do not mutate state. Make this a required CI gate. | 6–10 |

### P1 — High

| ID / area | Severity | Current issue and business impact | Exact files involved | Recommended fix | Effort |
|---|---|---|---|---|---:|
| P1.1 JWT token lifecycle and key operations | HIGH | JWT validation checks signature and expiration, but tokens have no configured issuer/audience, jti/session binding, revocation/logout, or documented key rotation. A copied token stays valid until expiry; current default expiration is 30 minutes. User/role status is reloaded per request, which helps disable an account, but does not revoke a stolen active token immediately. | security/JwtService.java; JwtAuthenticationFilter.java; CustomUserDetailsService.java; application/service/impl/AuthServiceImpl.java; application/dto/auth/LoginResponse.java; infrastructure/persistence/entity/AuthSessionEntity.java; db/migration/V011__create_authentication_challenges_and_sessions.sql; application.yml; application-prod.yml | Add issuer and audience validation and key id/rotation strategy. Decide whether pilot requires session-backed revocation; if so issue jti and validate session state or maintain a denylist. Keep short access-token lifetime, rotate externally managed secrets without committing them, and test expired, malformed, wrong-key, wrong-issuer/audience and disabled-user tokens. | 4–7 |
| P1.2 Authentication abuse and account lifecycle | HIGH | Login and public account creation are available, but no rate limit/lockout or abuse control was found. Registration creates an active CUSTOMER account; email/mobile verification is a separate workflow and is not visibly enforced by login. This risks credential stuffing, spam accounts and unverified contact details. | web/controller/AuthController.java; AuthServiceImpl.java; UserController.java; UserServiceImpl.java; CustomerVerificationController.java; CustomerVerificationServiceImpl.java; AuthOtpChallengeEntity.java; AuthSessionEntity.java; application.yml | Add IP/account throttling and bounded failed-login handling with safe error messages; enforce verified contact or an approved staff-assisted onboarding policy before sensitive customer actions. Add password reset/session lifecycle only to the extent required by pilot operations. | 4–7 |
| P1.3 Agent authorization boundary | HIGH | SecurityService checks assigned-agent ownership for assignments, visits, requests and evidence. Several VisitController routes do not declare method-level roles, and generic authenticated route rules mean each path depends on deep service checks. AgentAssignmentServiceImpl.createAssignment is ADMIN-only even though Operations is an intended operations role. Missing one guard could let an agent act on another agent’s work. | security/SecurityService.java; web/controller/VisitController.java; ServiceRequestController.java; EvidenceController.java; GpsVerificationController.java; application/service/impl/AgentAssignmentServiceImpl.java; VisitServiceImpl.java; ServiceRequestServiceImpl.java; EvidenceServiceImpl.java; config/SecurityConfig.java | Require assigned-agent identity for accept/reject/start/complete/visit/GPS/evidence actions; require operations/admin for assign/reassign; consistently derive actor from JWT principal. Align operations role policy and add tests for agent A vs agent B, unassigned agent, and reassignment after revocation. | 3–5 |
| P1.4 API input trust and validation | HIGH | Some controllers lack @Valid or explicit role annotations; request DTO validation is inconsistent. Sensitive operations must not trust caller-supplied actor IDs, assignedBy, payment provider fields, file URLs, status transitions, or mutable pricing fields. | web/controller/*.java, especially ServiceRequestController.java, LeadController.java, DealController.java, VisitController.java, DocumentController.java; application/dto/*Request.java; ServiceRequestServiceImpl.java; LeadServiceImpl.java; DealServiceImpl.java | Audit write DTOs and apply bean validation plus service invariants. Remove server-owned fields from public request contracts; use authenticated user context for actor; enforce transition allowlists and immutable quote/request prices. | 4–7 |
| P1.5 API CORS, throttling and transport boundary | HIGH | No application CORS allowlist or request throttling configuration was found. Stateless bearer authentication makes CSRF disabling reasonable for header-token APIs, but browser origins still need an explicit allowlist; auth, OTP and payment endpoints need abuse protection. TLS enforcement is expected at the deployment ingress and must be verified. | config/SecurityConfig.java; config/* (add CORS/security policy configuration); web/controller/AuthController.java; PaymentController.java; application.yml; application-prod.yml; deployment/ingress configuration (identify in deployment repository) | Configure exact production frontend origins and allowed methods/headers; reject wildcard credentialed access. Rate-limit login, registration, verification, payment-intent and expensive report endpoints. Require HTTPS at ingress and set/document forwarded-header behavior. | 3–6 |
| P1.6 Error and security event handling | HIGH | JwtAuthenticationFilter catches all exceptions and logs only a debug rejection; there is no consistent audit/security event record for repeated invalid logins, denied high-risk actions, or payment signature failures. Operations may not notice active attacks or integration failure. | security/JwtAuthenticationFilter.java; application/service/impl/AuthServiceImpl.java; web/exception/*; audit entity/repository/service; logging and Actuator configuration | Keep client errors generic; add structured security events without tokens/passwords/PII; alert on repeated failures and webhook verification errors. Add correlation IDs and never log Authorization headers or sensitive file URLs. | 3–5 |

### P2 — Medium

| ID / area | Severity | Current issue and business impact | Exact files involved | Recommended fix | Effort |
|---|---|---|---|---|---:|
| P2.1 JWT subject and claim discipline | MEDIUM | Tokens use email as subject and AuthServiceImpl passes an empty custom claim map. This works with the current filter, which reloads the user by email, but email changes and future claim use need a stable identity contract. | security/JwtService.java; JwtAuthenticationFilter.java; AuthServiceImpl.java; CustomUserDetailsService.java; UserServiceImpl.java | Prefer immutable user UUID as subject and resolve the active account by UUID; do not trust role/customer/agent claims unless they are signed, short-lived and invalidated on role changes. Version the token contract and migrate safely. | 2–4 |
| P2.2 Sensitive information and API documentation | MEDIUM | Swagger/OpenAPI routes are public in SecurityConfig. API docs can reveal endpoint shape, request fields and operational functionality. No secrets were found in the reviewed application YAML; production JWT/database values are environment supplied. Public docs should still be an intentional choice. | config/SecurityConfig.java; config/OpenApiConfig.java; application-prod.yml; deployment environment/secret configuration | Disable Swagger UI in production or protect it with staff access. Use a secret manager, rotate keys, and verify CI/logs do not expose secret values. | 1–2 |
| P2.3 Security headers and deployment checks | MEDIUM | Spring Security defaults provide several headers, but there is no explicit documented policy for HSTS, CSP, frame/content-type behavior, TLS ingress, or trusted proxy configuration in the reviewed app configuration. | config/SecurityConfig.java; application-prod.yml; deployment/ingress manifests or compose files | Confirm effective headers in the deployed environment; configure strict transport and browser policies at the appropriate application/ingress boundary; restrict actuator exposure to health/info as currently configured. | 2–4 |
| P2.4 Ownership query efficiency and information leakage | MEDIUM | Some property list behavior fetches records then filters ownership in application memory; unauthorized IDs may return different 404/403 patterns across services. Large data sets increase cost and reveal resource existence. | application/service/impl/PropertyServiceImpl.java; PropertyRepository.java; SecurityService.java; API exception handlers | Scope list queries by authenticated owner in the database. Standardize not-found/forbidden behavior for sensitive identifiers and add pagination. | 2–4 |

### P3 — Low

| ID / area | Severity | Current issue and business impact | Exact files involved | Recommended fix | Effort |
|---|---|---|---|---|---:|
| P3.1 Security documentation and review cadence | LOW | Security rules are encoded across multiple layers and not represented by one maintained endpoint policy/test map. This raises regression and onboarding cost. | SecurityConfig.java; SecurityService.java; controller tests; docs/Architecture/*; CI configuration | Keep an endpoint-role-resource matrix beside API docs and require review when a route is added or changed. Add dependency and secret scanning to CI if not already supplied by the hosting pipeline. | 1–2 |

## Workstream Execution Order

### Gate 0 — Stop unsafe exposure

1. Disable or restrict customer payment recording and any live payment-success route until P0.3 is implemented.
2. Restrict production access to staff and test accounts while P0.1/P0.2 are in progress.
3. Confirm no production credentials are stored in repository, build logs, or test fixtures; use external secret storage.

### Gate 1 — Authorization and ownership

1. Build the controller endpoint/role matrix from actual mappings.
2. Fix matcher/controller/service disagreements and add explicit role requirements.
3. Verify owner and assigned-agent checks for every identifier-based operation.
4. Add HTTP security regression tests before exposing customer data.

### Gate 2 — Files and financial data

1. Secure evidence/document storage and scoped reads.
2. Integrate verified provider payment lifecycle and invoice ownership.
3. Confirm audit trail and idempotency across payment retries/refunds.

### Gate 3 — Authentication/API hardening

1. Add login/register/OTP rate limits and contact-verification policy.
2. Add JWT issuer/audience and session/key rotation decisions.
3. Add CORS allowlist, TLS ingress verification, security event logs and alerts.
4. Run security tests against PostgreSQL-backed integration fixtures and production-like configuration.

## Pilot Acceptance Checklist

- [ ] Anonymous access is limited to login and explicitly approved registration/docs/health endpoints.
- [ ] CUSTOMER A cannot list, read, update, archive, pay, or download CUSTOMER B resources using guessed UUIDs.
- [ ] AGENT A cannot see or act on AGENT B’s requests, visits, evidence, or assignments; access ends after reassignment.
- [ ] OPERATIONS and ADMIN have exactly the intended separate permissions; route filters and method/service rules agree.
- [ ] Customers cannot set roles, status, ownership, price, payment provider state, or actor identity through request bodies.
- [ ] Payment is marked successful only after provider verification; replayed webhooks/callbacks are idempotent.
- [ ] Evidence and documents are private, type/size checked, owner/assignment scoped, and access audited.
- [ ] Expired/invalid/revoked or disabled-user tokens are denied; secrets are external, strong, and rotatable.
- [ ] Login, registration, OTP and payment endpoints have abuse throttling; production CORS is an explicit origin allowlist.
- [ ] Security denial and payment verification failures are observable without logging credentials, tokens, or sensitive file links.
- [ ] PostgreSQL-backed integration tests exercise the real Spring Security filter chain and resource ownership rules.

## Code areas reviewed

- JWT/authentication: security/JwtService.java, JwtAuthenticationFilter.java, CustomUserDetailsService.java, config/SecurityConfig.java, config/PasswordConfig.java, AuthController.java, AuthServiceImpl.java, UserController.java, UserServiceImpl.java.
- Authorization/ownership: SecurityService.java and controllers/services for properties, customers, leads, deals, subscriptions, requests, assignments, visits, evidence, documents, billing, payments, reports and admin operations.
- Files: EvidenceController.java, EvidenceServiceImpl.java, DocumentController.java, DocumentServiceImpl.java and their request/entity mappings.
- Payments/API: PaymentController.java, PaymentServiceImpl.java, BillingController.java, BillingServiceImpl.java, application.yml and application-prod.yml.
- Tests/build: propertypilot-backend/src/test/java/com/propertypilot/web/controller/*Test.java, security tests (coverage to be added), and pom.xml dependencies.

