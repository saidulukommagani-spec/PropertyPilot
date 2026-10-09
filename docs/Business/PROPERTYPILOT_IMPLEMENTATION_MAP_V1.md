# PROPERTYPILOT_IMPLEMENTATION_MAP_V1

**Review basis:** repository and business documents, static inspection only. Builds and tests were not run. Readiness scores are estimates based on the evidence listed here, not test results.

## 1. Executive Summary

PropertyPilot has substantial backend coverage and a broad frontend for customers, service operations, subscriptions, quotes, deals, invoices, and payments. The main issue is that coverage does not equal connected end-to-end workflows: several screens have no dedicated frontend, some route/API boundaries are inconsistent, and commercial and subscription flows have unresolved lifecycle gaps.

### Estimated readiness

| Area | Score | Basis |
|---|---:|---|
| Backend capability | 72% | Broad controller, service, persistence, and scheduler coverage; important lifecycle and schema gaps remain |
| Frontend capability | 63% | Many routes and feature pages exist, but several blueprint capabilities are absent or incomplete |
| Workflow integration | 54% | Service delivery is the most connected flow; lead/deal/revenue and renewal flows are incomplete |
| Business readiness | 55% | Primary service model is represented, but key revenue controls and handoffs are not established |
| Production readiness | **52%** | Static review only; database migration alignment and payment lifecycle are launch blockers |

The repository roadmap describes higher readiness, but this inspection found gaps that should be resolved before relying on those figures. See [PROPERTYPILOT_PRODUCTION_ROADMAP_V1.md](PROPERTYPILOT_PRODUCTION_ROADMAP_V1.md) and [PROPERTYPILOT_GAP_ANALYSIS_V4](PROPERTYPILOT_GAP_ANALYSIS_V4).

## 2. Business Understanding

The approved blueprints describe PropertyPilot as a **property-centric service platform**, not a marketplace. PropertyPilot owns customer relationships, pricing, service delivery, SLAs, and quality assurance. Agents and vendors are fulfillment resources.

The primary revenue engine is property services. Subscriptions provide benefits and entitlements; leads represent opportunities that may generate services or convert to deals; deals represent transaction opportunities. Evidence is intended to support service completion and customer trust.

These goals are documented in the [business blueprint](PROPERTYPILOT_BUSINESS_BLUEPRINT_V1.md), [operational workflow](PROPERTYPILOT_OPERATIONAL_WORKFLOW_V1.md), [domain model](PROPERTYPILOT_DOMAIN_MODEL_V1.md), and [entity relationship blueprint](PROPERTYPILOT_ENTITY_RELATIONSHIP_BLUEPRINT_V1).

## 3. Backend Module Inventory

| Module | Business purpose | Controllers and services | Persistence | Status |
|---|---|---|---|---|
| Authentication and users | Login, identity, roles, profile, session support | `AuthController`, `UserController`; `AuthServiceImpl`, `UserServiceImpl` | User, role, auth session and OTP entities/repositories | **PARTIAL** — JWT and frontend session persistence exist; session revocation/refresh behavior needs end-to-end validation |
| Customer | Profiles, addresses, verification, preferences | `AdminCustomerController`, customer-related controllers; `Customer*ServiceImpl` | Customer, address, verification, favorites, preferences, search history | **PARTIAL** — broad CRUD foundation; complete customer/property experience is not present |
| Property | Properties, ownership, geography, documents | `PropertyController`, `PropertyOwnerController`; `PropertyServiceImpl`, `PropertyOwnerServiceImpl` | Property, owner, location and document entities | **PARTIAL** — create/read/update/archive/restore APIs exist; not all lifecycle and Property 360 relationships are exposed |
| Service catalog and pricing | Service definitions, eligibility, estimates and rules | `ServiceCatalogController`, `PricingController`, pricing configuration/rule controllers; pricing services | Service, pricing estimate/rule/profile entities | **PARTIAL** — real pricing APIs exist; full configurable commercial path is not proven by static review |
| Service requests and assignment | Create and operate customer requests | `ServiceRequestController`, `StaffAssignmentController`; request and assignment services | Service request, assignment and status history entities | **PARTIAL** — create, read, update and workflow actions exist; no delete endpoint; controller paths and access rules are inconsistent |
| Visits | Schedule and execute field visits | `VisitController`, `MonitoringScheduleController`; visit and monitoring services | Visit, monitoring schedule/run entities | **PARTIAL** — create/read/start/complete/cancel/reschedule endpoints exist; operational screen and request completion linkage need verification |
| Evidence and validation | Upload, retrieve and validate evidence | `EvidenceController`, `GpsVerificationController`; evidence/GPS/completion services | Evidence, evidence type, validation history and GPS entities | **PARTIAL** — upload and validation APIs exist; public/read endpoint access depends on service-level authorization and needs review |
| Subscriptions and entitlements | Plans, purchases, consumption, renewals | Subscription plan, customer subscription, entitlement consumption, benefit, add-on and renewal controllers/services | Subscription, entitlement, consumption, renewal and lifecycle entities | **PARTIAL** — broad backend foundation; renewal activation and confirmed-payment linkage are not demonstrated as complete |
| Leads | Track and qualify opportunities | `LeadController`; `LeadServiceImpl`, `LeadActivityServiceImpl` | Lead and lead activity entities | **BACKEND_ONLY / PARTIAL** — APIs and persistence exist; no dedicated lead feature folder or user-facing lead journey was found |
| Quotes | Pricing estimate and customer acceptance/rejection | `QuoteController`; `QuoteServiceImpl` | Pricing estimate, quote line items and discount/redemption entities | **PARTIAL** — quote decision lifecycle exists; acceptance is not connected to deal creation in the inspected service |
| Deals | Property transaction opportunities, participants and status | `DealController`; `DealServiceImpl`, `DealBillingServiceImpl` | Deal, participant, milestone, activity and service-request link entities | **PARTIAL** — APIs and frontend pages exist; authorization is not consistently declared on controller actions, and the inspected migration inventory has no deal-table creation |
| Billing and invoices | Create and present financial obligations | `BillingController`; `BillingServiceImpl` | Invoice and contract entities | **PARTIAL** — invoice read/create support exists; full invoice lifecycle and required deal linkage are not established in the original migration/API model |
| Payments | Record and view invoice payments | `PaymentController`; `PaymentServiceImpl` | Payment and refund entities | **PARTIAL / BLOCKER** — payment endpoints exist, but settlement must be verified before a client action can be treated as collected funds |
| Reporting and monitoring | Operational/business reporting and scheduled monitoring | Report, analytics, revenue, operations and monitoring controllers/services; schedulers | Report, monitoring and alert entities | **PARTIAL** — backend reporting and monitoring exist, while the frontend has limited dedicated screens |

Relevant controllers include `ServiceRequestController`, `VisitController`, `EvidenceController`, `QuoteController`, `DealController`, and `BillingController` under `propertypilot-backend/src/main/java/com/propertypilot/web/controller/`.

## 4. Frontend Module Inventory

| Feature | Pages and API integration | Role access | Status |
|---|---|---|---|
| Authentication | Login and auth API; `AuthContext` persists access token in `sessionStorage`, then reloads the current user | Public login; protected application routes | **PARTIAL** — persistence is implemented; refresh, expiry and logout/revocation behavior need validation |
| Admin portal | Dashboard and generic admin directory/detail pages; admin API | Admin and Operations routing | **PARTIAL** — generic admin screens do not equal dedicated screens for every business workflow |
| Properties and customers | Property forms/details and customer portal pages; customer/property API clients | Customer portal; admin/operations pages | **PARTIAL** — key screens exist; full blueprint Property 360 remains incomplete |
| Service requests | List, form, dashboard, detail, assignment and timeline components; service-request API/hooks | Customer and Operations/Admin routes | **PARTIAL** — strongest workflow coverage; staff and operations request pages have parallel route families |
| Visits | List, dashboard, create/edit form, details, timeline, reschedule and evidence summary; visit API/service/hooks | Agent, Customer, Operations/Admin | **PARTIAL** — screens are present; workflow completion and role-specific API coverage require verification |
| Evidence | Dashboard, list, detail, upload, validation and visit summary; evidence API/service/hooks | Admin, Operations, Agent; customer viewer | **PARTIAL** — screens exist; no build/test verification was run |
| Agent operations | Dashboard, work queue, profile, operations agents and assignment console | Agent or Operations/Admin | **PARTIAL** — routes exist; complete performance metrics and execution journey need verification |
| Customer portal | Dashboard, properties, requests, visits, evidence, documents, subscriptions and invoices | Customer | **PARTIAL** — customer invoices exist, but dedicated payment history, quotes, deals, leads and several blueprint links are absent |
| Subscriptions | Plans, details, list, dashboard, checkout, usage, renewals and add-ons | Customer/Admin/Operations | **PARTIAL** — screens exist; payment-confirmed activation and renewal must be proven |
| Quotes | List, dashboard, details and create; API connects to quote/pricing endpoints | Customer/Admin/Operations | **PARTIAL** — acceptance/rejection is present; linked deal creation is not visible in the quote service |
| Deals | List, dashboard, create, edit and details; deal API | Admin route group | **PARTIAL** — UI exists; quote-to-deal linkage is not established and customer deal experience is absent |
| Invoices and payments | Invoice dashboard/list/create/detail/PDF; payment list/detail and invoice payment dialog | Primarily Admin for invoice routes; Customer can view/pay through customer invoice experience | **PARTIAL / BLOCKER** — no complete edit/lifecycle workflow; payment list requests payments separately for each invoice |
| Leads | No dedicated frontend lead feature or route found | None identified in the route map | **BACKEND_ONLY** |
| Monitoring and reports | Backend APIs exist; frontend has a report list and some report components, but no full monitoring/report route set was found | Incomplete | **PARTIAL / BACKEND_ONLY** |

No obvious “coming soon” or “under construction” page markers were found in the inspected feature pages. This does not prove every page is complete: some routes use generic admin pages, and no runtime UI/API audit was performed.

## 5. Route Map

Application routes are registered in `propertypilot-frontend/src/app/routes/AppRoutes.tsx`. Role groups are the guards surrounding each route group.

| URL | Page / feature | Role access |
|---|---|---|
| `/login` | Login | Public |
| `/` and `*` | Role-based home redirect | Authenticated application entry |
| `/staff` | Admin dashboard | Admin, Operations |
| `/staff/:resource`, `/staff/:resource/:id` | Generic admin directory/detail | Admin |
| `/staff/service-requests`, `/staff/service-requests/dashboard`, `/staff/service-requests/new`, `/staff/service-requests/:id`, `/staff/service-requests/:id/edit` | Service request operations | Admin |
| `/staff/visits/:visitId` | Visit details | Admin |
| `/staff/payments`, `/staff/payments/:paymentId` | Payment list/detail | Admin |
| `/operations/agents`, `/operations/assignments` | Agent performance and assignment console | Admin, Operations |
| `/service-requests`, `/service-requests/dashboard`, `/service-requests/new`, `/service-requests/:id`, `/service-requests/:id/edit` | Operations service requests | Admin, Operations |
| `/visits`, `/visits/dashboard`, `/visits/new`, `/visits/:visitId`, `/visits/:visitId/edit`, `/visits/:visitId/evidence` | Visit management and evidence summary | Admin, Operations |
| `/evidence`, `/evidence/dashboard`, `/evidence/validation` | Evidence operations | Admin, Operations |
| `/evidence/upload`, `/evidence/:evidenceId` | Upload and evidence details | Admin, Operations, Agent |
| `/agent/dashboard`, `/agent/work-queue`, `/agent/profile`, `/agent/visits`, `/agent/visit/:visitId`, `/agent/visits/:visitId`, `/agent/visits/:visitId/evidence` | Agent operations | Agent |
| `/customer/dashboard`, `/customer/properties`, `/customer/properties/new`, `/customer/properties/:id`, `/customer/properties/:id/edit` | Customer dashboard and properties | Customer |
| `/customer/service-requests`, `/customer/service-requests/new`, `/customer/service-requests/:id` | Customer requests | Customer |
| `/customer/visits`, `/customer/visits/:id`, `/customer/evidence`, `/customer/documents`, `/customer/subscriptions`, `/customer/invoices` | Customer self-service | Customer |
| `/quotes/dashboard`, `/quotes`, `/quotes/:id` | Quote list and decisions | Customer, Admin, Operations |
| `/quotes/new` | Quote creation | Admin |
| `/deals/dashboard`, `/deals`, `/deals/new`, `/deals/:id`, `/deals/:id/edit` | Deal management | Admin |
| `/invoices/dashboard`, `/invoices`, `/invoices/new`, `/invoices/:id`, `/invoices/:id/pdf` | Invoice management and print view | Admin |
| `/subscriptions/dashboard`, `/subscriptions/plans`, `/subscriptions/plans/:id`, `/subscriptions`, `/subscriptions/:id`, `/subscriptions/:id/usage`, `/subscriptions/renewals`, `/subscriptions/addons` | Subscription management | Customer, Admin, Operations |

There are no exact duplicate route paths in the inspected configuration. There are aliases, notably `/agent/visit/:visitId` and `/agent/visits/:visitId`; staff/operations request screens are registered under both `/staff/service-requests...` and `/service-requests...`. The `/staff/:resource...` catch-all means route precedence should remain covered by routing tests.

## 6. User Journey Map

- **Customer:** login → dashboard → properties → create/track service request → visits/evidence → invoices. This is represented by customer routes, but dedicated payment history and several blueprint areas (leads, deals, quotes) are not exposed as complete journeys.
- **Agent:** login → work queue/visits → visit details → evidence upload → visit completion. Routes and APIs exist. Verify agent ownership for every visit/evidence operation.
- **Operations:** login → request list → assignment → visits → evidence validation → request completion. This is the most represented operational journey; billing handoff after validated completion is not established as a visible end-to-end flow.
- **Admin:** dashboard → catalog/pricing/subscriptions/quotes/deals/invoices and reports. Many backend capabilities exist, but there is no complete route for every blueprint admin capability.

## 7. Screen → API → Controller → Service Mapping

| Screen or flow | Frontend API client | Backend endpoint/controller | Backend service | Finding |
|---|---|---|---|---|
| Property forms/details | `api/properties.ts`, property feature hooks | `/api/v1/properties` → `PropertyController` | `PropertyServiceImpl` | Connected for core property operations |
| Service request list/details | `api/serviceRequests.ts`, service-request feature service/hooks | `/api/service-requests...` → `ServiceRequestController` | `ServiceRequestServiceImpl` | Connected; APIs include assignment and status actions |
| Visit list/details | `api/visits.ts`, `VisitApi.ts`, `VisitService.ts` | `/api/v1/visits...` → `VisitController` | `VisitServiceImpl` | Connected; multiple frontend clients indicate duplicated API-layer conventions |
| Evidence upload/validation | Evidence feature service/hooks | `/api/evidence...` → `EvidenceController` | `EvidenceServiceImpl`, storage and completion validation services | Connected; authorization on read paths needs review |
| Quote screens | `features/quotes/services/quoteApi.ts` | `/api/v1/quotes...` → `QuoteController` | `QuoteServiceImpl` | Connected for quote operations; acceptance-to-deal handoff missing |
| Deal screens | `features/deals/services/dealApi.ts` | `/api/v1/deals...` → `DealController` | `DealServiceImpl` | Connected at API level; quote link and strict authorization are gaps |
| Invoice screens | `features/invoices/services/invoiceApi.ts` | `/api/v1/billing/invoices...` → `BillingController` | `BillingServiceImpl` | Read and payment operations exist; lifecycle actions are incomplete |
| Payment screens | `api/payments.ts` and invoice API | `/api/payments...` → `PaymentController` | `PaymentServiceImpl` | Connected, but payment list fans out one request per invoice |
| Subscription screens | `features/subscriptions/services/SubscriptionApi.ts` | Customer/subscription endpoints → subscription controllers | Subscription and renewal services | Connected at endpoint level; renewal settlement is the key business gap |
| Leads | No dedicated lead frontend API/page found | `/api/v1/leads...` → `LeadController` | `LeadServiceImpl`, `LeadActivityServiceImpl` | Backend-only |
| Reports/monitoring | Report API and report list; no complete monitoring UI | Report/monitoring controllers | Reporting and monitoring services | Partially exposed |

The payment-list fan-out behavior is visible in `propertypilot-frontend/src/api/payments.ts`. Quote acceptance is implemented in `QuoteServiceImpl`, but the method changes the estimate state and returns the quote; it does not show a deal creation handoff.

## 8. Blueprint Alignment Matrix

| Capability | Blueprint | Backend | Frontend | Integrated status |
|---|---|---|---|---|
| Customer | Yes | Yes | Yes | **Partial** |
| Property / Property 360 | Yes | Yes | Partial | **Partial** |
| Service catalog and pricing | Yes | Yes | Partial | **Partial** |
| Service request | Yes | Yes | Yes | **Partial-to-strong** |
| Assignment | Yes | Yes | Yes | **Partial** |
| Visit | Yes | Yes | Yes | **Partial** |
| Evidence and validation | Yes | Yes | Yes | **Partial** |
| Subscription purchase | Yes | Yes | Yes | **Partial** |
| Entitlement consumption | Yes | Yes | Partial | **Partial** |
| Subscription renewal | Yes | Yes | Yes | **Partial / Blocker** until tied to confirmed payment |
| Lead | Yes | Yes | No dedicated feature | **Backend-only** |
| Deal | Yes | Yes | Yes | **Partial** |
| Quote | Business flow varies across documents | Yes | Yes | **Partial** |
| Invoice | Yes | Yes | Yes | **Partial / Blocker** pending lifecycle controls |
| Payment | Yes | Yes | Yes | **Partial / Blocker** pending verified settlement |
| Monitoring | Yes | Yes | Limited | **Backend-only / Partial** |
| Reporting | Yes | Yes | Limited | **Partial** |

The business blueprint places service requests first in revenue priority. The entity blueprint includes service and deal revenue relationships, while the operational workflow emphasizes Service Request → Visit → Evidence → Invoice → Payment. The quote path differs across documents: one describes Quote → Invoice → Payment; the supplied implementation request describes Quote → Deal → Invoice → Payment. Treat these as separate business cases unless product owners confirm otherwise.

## 9. Workflow Coverage Analysis

### Flow A: Property → Service Request → Visit → Evidence → Invoice → Payment

**Partially supported.** Property, service request, visit, evidence, and invoice/payment APIs and screens exist. The backend has evidence validation and request completion services, but static inspection does not establish that every completion path requires approved evidence and reliably creates an invoice. Payment settlement is not sufficiently verified for launch.

### Flow B: Subscription → Entitlement → Consumption → Renewal

**Partially supported.** Subscription, entitlement, consumption, and renewal entities/services/controllers exist, with frontend subscription and renewal screens. Renewal activation needs a confirmed payment relationship. A payment ID alone is insufficient unless it is successful, settled, belongs to the customer, matches the renewal obligation, and has not already been used.

### Flow C: Lead → Service Request(s) → Deal

**Partially supported / disconnected.** Lead and activity APIs/services exist. No dedicated lead frontend feature was found. The blueprint allows a lead to generate services, deals, both, or neither; the inspected frontend does not present this as an end-to-end workflow.

### Flow D: Quote → Invoice → Payment

**Partially supported.** Quote, invoice, and payment pages/APIs exist. Quote acceptance is not visibly linked to deal creation in the inspected service. Invoice creation is not proven to require a deal source, and the invoice lifecycle is incomplete.

## 10. Gap Analysis

### Critical issues

1. **Payment settlement is not demonstrated as a verified provider event.** A client-facing payment request must not be treated as proof that funds settled. The current workspace also contains partial settlement edits described below; those edits are not a verified implementation.
2. **Database schema is not aligned with the current entity model.** The inspected Flyway history includes invoice and payment tables in V009 but no migration creating a `deals` table. `DealEntity` maps to `deals`, while billing/invoice relationships require schema alignment. Production uses `ddl-auto: validate`, making this a startup risk.
3. **Subscription renewal needs payment confirmation tied to the exact renewal obligation.** A payment ID alone is insufficient unless it is successful, settled, belongs to the customer, matches the renewal invoice/amount, and has not already been consumed.
4. **No complete invoice lifecycle is exposed.** The UI has invoice list/detail/create/print routes; the controller primarily exposes reads. DRAFT/SENT/PARTIALLY_PAID/PAID/OVERDUE/CANCELLED needs enforced transitions and auditability.

### High issues

5. **Quote acceptance does not create a linked deal.** The quote service accepts and stores the estimate status, but no linked deal creation appears in that path.
6. **Deal controller authorization is inconsistent.** Most `DealController` methods do not declare method-level `@PreAuthorize`; security then relies on broad authentication rules and service checks. Review each endpoint.
7. **No dedicated lead UI or route was found.** Lead backend functionality is not integrated into customer/operations journeys.
8. **Customer commercial visibility is incomplete.** Customer invoice pages exist, but no dedicated customer quote/deal/lead and payment-history journey was found.
9. **Invoice/payment reconciliation is absent from the inspected APIs.** There is a revenue dashboard, but no reconciliation endpoint/report to compare invoice balances with settled payment records.

### Medium issues

10. **Duplicate API-layer conventions exist.** For visits, both `visits.ts` and `VisitApi.ts`/`VisitService.ts` are present. This increases contract mismatch risk.
11. **Payment list performs N+1 HTTP requests.** It fetches invoices, then requests payments per invoice; this can degrade as volume grows.
12. **CRUD is not uniform.** Service requests have no delete endpoint; properties use archive/restore rather than delete; invoice edit/delete workflows are absent; payment cancellation/refund is not exposed as a full workflow.
13. **Monitoring and reporting are not fully exposed in the frontend.** Backend controllers/services exist, but frontend routes primarily cover reports list and operational dashboards.
14. **Role model terminology differs between blueprints and implementation.** Route guards use ADMIN/OPERATIONS/AGENT/CUSTOMER, while the screen blueprint also describes Super Admin and Operations User; confirm seeded roles and deployment permissions.

### Low issues

15. **Route aliases and catch-all routes increase navigation complexity.** There are no exact duplicate paths in the inspected route table, but staff/operations request pages have parallel paths and agent visit pages have aliases.
16. **The quote business flow differs between blueprint documents.** One describes Quote → Invoice → Payment; the implementation request describes Quote → Deal → Invoice → Payment. Make the distinction explicit in acceptance criteria.

## 11. Production Readiness Assessment

**Estimated production readiness: 52%.** The platform has broad functional coverage, but pilot launch requires validated financial state transitions, payment settlement verification, and a database schema that matches the deployed entity model. This is a static assessment; no build, integration suite, security test, migration validation, or UAT run was performed for this report.

### Workspace note: partial revenue-hardening edits

Before the audit-only request arrived, revenue-related files had already been edited in the shared workspace. Those edits are **incomplete and unverified**. They include partial payment settlement, invoice status, billing, and subscription renewal changes. No further changes were made after the audit-only instruction.

One concrete issue visible in those partial edits is a reference to `latestPlanVersion` before its declaration in `SubscriptionRenewalServiceImpl`. No migration or test verification was completed for those edits. Treat the current revenue-hardening working-tree changes as **not launch-ready** until reviewed and validated.

## 12. Recommended Next Development Tasks

1. **Stabilize the revenue-hardening working tree:** review or revert only the incomplete changes, then implement and test verified payment settlement, invoice transitions, and payment-backed renewal as one coherent change.
2. **Align schema and entities:** create and validate migrations for deal/invoice/payment relationships and lifecycle columns before relying on production `ddl-auto: validate`.
3. **Make quote acceptance atomic with deal creation:** enforce idempotency and a unique quote-to-deal relationship, then test repeat acceptance and rollback.
4. **Complete the service revenue handoff:** verify that completion requires approved evidence and define when a draft invoice is created and sent.
5. **Complete role and end-to-end verification:** test customer, agent, operations, and admin permissions against route guards and backend ownership checks; then run build, migration validation, and pilot UAT.
