# PropertyPilot Traceability Matrix

## Purpose

This matrix traces the Feature Catalog, Service Catalog, Customer Journeys, Screen Catalog, API Catalog, Database Physical Model, and SRS. It identifies unresolved coverage gaps, duplicate requirements, and conflicting rules as of the current documentation baseline.

Status values:

- **Covered**: the capability has an identifiable trace through the relevant artifacts.
- **Partial**: one or more required delivery artifacts are absent, generic, or incomplete.
- **Conflict**: two artifacts define incompatible or stale requirements.

---

# Capability Traceability

| Capability | Feature Catalog | Service Catalog / Journeys | Screens | APIs | Data Model | SRS | Status / finding |
|---|---|---|---|---|---|---|---|
| Registration, OTP, KYC, profile | Customer Registration, Login, KYC, Profile | Registration and KYC journey | Customer KYC screen exists | Identity verification exists; no KYC review/status contract | `customer_verifications`, identity/auth tables added | FR-01 | **Partial** - no dedicated admin KYC review/rework screen or API. |
| Property registration and ownership | Registration, multiple/family ownership | Property registration and shared-ownership journeys | Property and co-owner screens | Property/owner APIs exist | `properties`, `property_owners` | FR-02, FR-03 | **Partial** - ownership table lacks the percentage, effective-date, and source-document fields required by cross-cutting ownership rules. |
| Service booking and fulfilment | Catalog, booking, assignment, tracking, completion | Booking, agent, vendor, and emergency journeys | Service, agent, vendor, operations screens | Request, assignment, visit, evidence, and report APIs | Service, assignment, visit, evidence, report tables | FR-05 to FR-08 | **Partial** - agent task list/detail APIs and screens have no `tasks` physical-model table. |
| Service cancellation and rating | Cancellation and rating | Cancellation and feedback journeys | Cancellation and rating screens | Cancellation and rating APIs | Status history and ratings tables | FR-05 | **Partial** - no dedicated cancellation record links reason, effective date, refund evaluation, and outcome to a service request. |
| Monitoring and NRI alerts | Monitoring, alerts, emergency visits, NRI services | Monitoring, alert-remediation, NRI emergency journeys | Monitoring, NRI alert-detail screens | Monitoring request, visit, alert, NRI APIs | Schedules, alerts, NRI assignment/message tables | FR-07, FR-20 | **Covered** at the catalog level; implementation migrations remain to be created. |
| Subscription lifecycle | Plans, renewal, upgrade/downgrade | Activation, renewal, plan-change journeys | Plans, subscription, renewal screens | Plan, create, renew, pause, resume APIs | Plans, subscriptions, renewals, entitlements, pause and lifecycle-event tables | FR-12 to FR-14 | **Partial** - no explicit upgrade, downgrade, auto-renewal-consent, grace-period, or suspension APIs/screens. |
| Pricing, discounts, and coupons | Cost calculator and dynamic pricing | Cost and upgrade journeys | Calculator and admin pricing screens | Estimate, comparison, pricing-rule, coupon APIs | Price rules, estimates, quote line items, discounts, coupons, approvals | FR-09, FR-10 | **Partial** - quote APIs are present only as bare endpoint inventory entries and lack canonical request/response contracts. |
| Payments, invoices, and refunds | Payments, invoices, receipts, refunds | Booking/payment and cancellation/refund journeys | Checkout, history, invoices, cancellation screens | Payment and refund APIs | Payments, invoices, refunds | FR-11 | **Partial** - refund status/approval and payment-provider webhook contracts are not defined in API Catalog. |
| Vendor quotation and delivery | Vendor registration, assignment, quote management | Vendor onboarding, quotation, execution journeys | Vendor jobs, invoices, payments, performance screens | Vendor, job, quotation APIs | Vendors, mappings, quotations, assignments | FR-16, FR-17 | **Partial** - vendor invoice/payment records are not explicitly linked to a vendor assignment in the physical model. |
| Marketplace and protected contact | Marketplace and lead-protection features | Buy/sell/rental and protected-interaction journeys | Marketplace and protected-interaction screens | Listings, inquiries, disclosure, commission APIs | Listings, inquiries, disclosures, commissions, favourites, history | FR-18, FR-19 | **Covered** at the catalog level. |
| Complaints, support, and knowledge | Service complaint/escalation support | Complaint journey | Support, complaint, FAQ screens | Complaint and knowledge-base APIs | Complaints/comments, knowledge-base tables | FR-21 | **Covered** at the catalog level. |
| Reports and review | Verification, monitoring, revenue, performance reports | Report delivery and review journeys | Report/evidence viewer and operations review screens | Report generation, review, evidence APIs | Reports and report-reviews tables | FR-22 | **Covered** at the catalog level. |

---

# Missing Features

| Missing feature requirement | Evidence | Required outcome |
|---|---|---|
| Subscription pause, cancellation, and auto-renewal controls are not named in Feature Catalog scope. | Subscription Plans and SRS require them; Feature Catalog lists renewal and upgrade/downgrade only. | Add feature-level scope and release priority for pause, cancellation, auto-renewal consent, retry/grace handling, and suspension. |
| Construction project lifecycle is not defined as a feature. | Service Catalog supports project execution; customer journeys include construction progression; no Feature Catalog entry governs approved quotation, milestones, change orders, acceptance, or warranty. | Add a project-execution feature with milestones, scope versioning, acceptance, and warranty. |
| Privacy/consent and customer-data rights are not represented as product features. | Customer preferences exist, but export, deletion, consent withdrawal, and legal hold have no Feature Catalog entry. | Add privacy-rights and consent-management feature requirements with priority and ownership. |

# Missing Screens

| Missing screen | Trace source | Required role/action |
|---|---|---|
| KYC Review and Exception Queue | KYC is a customer feature and journey; only customer KYC screen is catalogued. | Admin/operations review, approve, reject, request rework, and view expiry. |
| Subscription Change Management | SRS and Subscription Plans require pause, cancellation, auto-renewal, and plan-change controls; the catalog has plans, subscription, and renewal only. | Customer review and control of pause, cancellation, auto-renewal consent, downgrade effective date, and grace/suspension status. |
| Construction Project Milestones | Construction service journey requires progress monitoring; no project screen is catalogued. | Customer/vendor/operations milestone progress, change orders, evidence, acceptance, and warranty view. |
| Privacy and Consent Centre | Customer preferences are a Feature Catalog requirement; no dedicated screen is catalogued. | Customer communication preference, consent, export, deletion, and request-status controls. |

# Missing APIs

| Missing API capability | Trace source | Required resource operations |
|---|---|---|
| KYC administration | Feature Catalog, Customer Journey, SRS | Get KYC status; list review queue; approve/reject/request rework; record expiry/reverification. |
| Subscription change controls | Subscription Plans, Customer Journey, SRS | Upgrade, downgrade, configure auto-renewal consent, expose grace/`PAST_DUE`/suspension state, and retrieve lifecycle events. |
| Payment provider webhook and refund workflow | Payment/refund requirements in SRS and customer journeys | Signed provider webhook intake; refund status, approval, reversal, and reconciliation-exception operations. |
| Offline agent synchronisation | Agent journey and Screen Catalog offline-sync screen | Submit queued operations; retrieve sync state/conflicts; resolve/retry synchronisation. |
| Project execution lifecycle | Construction services and journeys | Create project from selected quotation; manage milestones, change orders, acceptance, warranty, and settlement status. |
| Privacy rights and consent | Feature gap and SRS privacy obligations | Export data; submit/status deletion request; manage consent and legal-hold-aware request outcome. |

# Missing Tables

| Missing table | Trace source | Minimum responsibility |
|---|---|---|
| `tasks` | Agent task screens and `/agent-tasks/{taskId}` APIs | Persist agent/vendor task identity, assignment, due date, status, and service-request relationship. |
| `service_request_cancellations` | Cancellation journey, screen, API, and SRS service-cancellation requirement | Persist cancellation reason, requester, effective time, policy decision, refund eligibility, and related refund outcome. |
| Vendor invoice/settlement linkage | Vendor job invoice and vendor-payment APIs; vendor portal screens | Add either a dedicated `vendor_invoices` table or explicit foreign keys from invoices/payments to `vendor_assignments` and quotations. |

# Duplicate Requirements

| Duplicate or overlapping requirement | Artifacts | Impact |
|---|---|---|
| Property resource is described both as a managed customer property and as a marketplace listing. | SRS/property requirements, Database Physical Model, API Catalog `POST /properties` labelled “Create Listing”. | The same route and table name risk conflating property management with marketplace publication. |
| Assignment is modelled three ways. | Physical model `vendor_assignments` and `service_assignments`; implementation migration `agent_assignments`. | Ownership, lifecycle, and reporting can diverge unless a single canonical assignment aggregate is selected. |
| Subscription lifecycle requirements occur in legacy and enterprise sections. | Subscription Management lifecycle/status sections and enterprise operating specification. | State vocabulary differs and should be retired in favour of the State Model Catalog. |
| API Catalog contains a detailed endpoint catalog plus appended endpoint inventories. | API Catalog main sections and “Appended Missing Endpoints”. | Bare routes can drift from detailed contracts and OpenAPI unless generated from one source. |

# Conflicting Rules

| Conflict | Artifacts | Required resolution |
|---|---|---|
| SRS says `Pricing_Strategy.md` is absent, but the file now exists and is referenced by Subscription Plans. | SRS source-of-truth/traceability sections; Pricing Strategy; Subscription Plans. | Update SRS source references and pricing acceptance criteria. |
| Initial quote distance source differs. | Pricing Engine uses agent and property coordinates; Cross-Cutting Requirements requires property/coverage coordinates and prohibits agent coordinates for initial quotes. | Apply Cross-Cutting Requirements as master and amend Pricing Engine. |
| Authentication scope differs. | Cross-Cutting Requirements makes mobile OTP the MVP customer/agent method; API Catalog exposes password login/reset and MFA as active APIs. | Mark non-OTP APIs as future/privileged-only or revise the master policy through approval. |
| API base path differs. | Cross-Cutting Requirements mandates `/api/v1`; API Catalog paths omit the prefix; OpenAPI uses a `/v1` server path. | Publish one canonical base URL and regenerate API/OpenAPI contracts. |
| Property persistence shape conflicts with property API semantics. | Database Physical Model uses customer-owned `properties`; API Catalog describes `POST /properties` as marketplace listing creation. | Separate managed-property and marketplace-listing contracts or explicitly map the listing aggregate. |
| Service lifecycle naming differs from implementation. | Cross-Cutting/State Model start at `NEW` and require `service_request_status_history`; SQL migration defaults to `created` and uses a different history name. | Align migration, physical model, API status enums, events, and state catalog. |
| Subscription status vocabulary differs. | Cross-Cutting/State Model use `PENDING` and `PAST_DUE`; legacy Subscription Management uses `DRAFT`, `PENDING_PAYMENT`, and `RENEWAL_DUE`. | Treat State Model Catalog as canonical and map or deprecate legacy statuses. |

# Recommended Remediation Order

1. Resolve the six conflicting rules and publish updated SRS/API/state references.
2. Add the missing subscription, KYC, offline-sync, project, payment-webhook, and privacy API contracts to OpenAPI and API Catalog.
3. Add the three missing persistence designs, then implement migrations aligned to the Database Physical Model.
4. Add the four missing screens and their navigation flows.
5. Add the missing Feature Catalog entries and priorities for subscription controls, project execution, and privacy rights.
6. Consolidate duplicate assignment, subscription lifecycle, and API-catalog representations under the relevant master sources.

# Source Documents

- Feature_Catalog.md
- Service_Catalog.md
- Customer_Journeys.md
- Screen_Catalog.md
- API_Catalog.md
- Database_Physical_Model.md
- PropertyPilot_SRS.md
