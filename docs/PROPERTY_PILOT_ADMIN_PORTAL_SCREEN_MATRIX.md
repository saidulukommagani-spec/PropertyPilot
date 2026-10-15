# PropertyPilot Admin Portal Screen Matrix

## Scope and API notation

This matrix covers the internal Admin / Operations Portal. It excludes customer pages and agent work screens, though an assigned agent may have narrowly scoped access to request or visit records when the backend permits it.

The API paths below are those documented in the frontend architecture and roadmap. Where a dedicated endpoint is not named there, the domain API is shown at its documented base path and the exact operation/DTO must be confirmed against the backend controller before integration. Role names are targets; the execution roadmap flags the `/api/admin/**` ADMIN-only versus controller-level OPERATIONS mismatch for resolution before live use.

**Priority:** P0 = required for the narrow paid pilot; P1 = useful for safe pilot operations or conditional pilot scope; P2 = defer until after the first cohort or backend readiness. **Pilot Required** is based on the recommended first-cohort scope in the roadmap, which defers subscriptions, Monitoring, and Property Health unless specifically approved.

## Screen matrix

| Screen Name | Purpose | User Roles | Backend APIs Required | Priority | Pilot Release Required (Y/N) |
|---|---|---|---|---|---|
| Staff Login and Session | Authenticate staff, load authorized identity/roles, and handle expiry/401/403. | ADMIN, OPERATIONS; SALES/AGENT only if approved | `POST /api/v1/auth/login`; `GET /api/v1/users/me` (must expose effective roles or a permitted role/profile contract) | P0 | Y |
| Operations Dashboard | Show due/stuck work, workload, SLA exceptions, and key operational alerts for daily triage. | ADMIN, OPERATIONS | Operations summary/dashboard APIs; `/api/admin/operations` (exact routes/DTOs to confirm) | P0 | Y |
| Customer Management | Search and view customer profile, verification/status, and linked properties/requests; support audited staff-assisted onboarding. | ADMIN, OPERATIONS | Customer APIs (exact base path/actions to verify); `/api/v1/users/me` for actor identity; property and request APIs for linked records | P0 | Y |
| Property Lookup | Find a property for support/operations and inspect its ownership and service history, subject to staff scope. | ADMIN, OPERATIONS | `/api/v1/properties`; customer/property access and history APIs (confirm exact routes and resource authorization) | P1 | N |
| Leads | Track prospects, sources, qualification, ownership/activity, and approved lead outcomes. | ADMIN, OPERATIONS; approved SALES role | `/api/v1/leads` | P1 | N |
| Deals | Track internal revenue opportunities and proposed service scope; hand off authorized wins to separate service requests. | ADMIN, OPERATIONS; approved SALES role | `/api/v1/deals`; `/api/v1/quotes`; `/api/service-requests` for approved handoff | P1 | N |
| Quote Work Queue and Quote Preparation | Prepare staff quotes with configured pricing, expiry, tax and customer-facing totals; track customer acceptance/rejection. | ADMIN, OPERATIONS | `/api/v1/pricing/calculate` (staff-restricted); `/api/v1/quotes`; customer decision routes `/api/v1/quotes/{quoteId}/accept` and `/reject` | P0 | Y |
| Service Request Queue | Triage accepted-quote work orders, view lifecycle/SLA, and move through only authorized operational actions. | ADMIN, OPERATIONS | `/api/service-requests`; related `/api/v1/quotes`; customer/property APIs as needed | P0 | Y |
| Assignment Board | Manually assign pilot work and inspect assignment/workload status. | ADMIN, OPERATIONS | `/api/admin/operations`; `/api/admin/agents`; service-request and visit APIs (`/api/service-requests`, `/api/v1/visits`) | P0 | Y |
| Visit Schedule and Detail | Schedule and review visits for services whose configured requirements require them; support no-visit services too. | ADMIN, OPERATIONS; assigned AGENT for permitted execution | `/api/v1/visits`; `/api/service-requests` | P0 | Y |
| Evidence Review and Completion | Review evidence metadata and completion readiness; expose file preview/download only through a secured storage contract. | ADMIN, OPERATIONS; assigned AGENT for upload actions | `/api/evidence`; `/api/gps-verifications`; `/api/service-requests`; controlled document/evidence storage API (required before live file access) | P0 | Y |
| Agents | Maintain internal fulfillment roster and review assignment capacity; never present agents/vendors as customer-selectable marketplace listings. | ADMIN, OPERATIONS | `/api/admin/agents`; `/api/admin/operations`; assignment APIs (exact routes to verify) | P0 | Y |
| Service Catalog Settings | Create and maintain versioned service definitions, eligibility, visit/evidence requirements and SLA policy. | ADMIN | `/api/services/admin`; `/api/services` for catalog read | P0 | Y |
| Pricing Settings | Configure/version pricing rules and review effective customer price behavior without exposing internal cost breakdown to customers. | ADMIN; approved OPERATIONS if policy permits | Pricing configuration/rule APIs (exact routes to verify); `POST /api/v1/pricing/calculate` | P0 | Y |
| User and Role Administration | Manage staff access and role assignment with audit and least privilege. | ADMIN | User/role administration API (exact route not named in frontend architecture; define/confirm before UI integration); `GET /api/v1/users/me` | P0 | Y |
| Payment and Refund Operations | Inspect payment/invoice state, reconcile provider settlement, and process authorized refunds with audit. | ADMIN; narrowly authorized OPERATIONS/Finance role if defined | `/api/payments`; `/api/v1/billing`; provider-confirmed settlement/refund operations (contracts gated on payment workstream E/B3) | P1 | Y |
| Monitoring Operations | Review schedule/run backlog and exceptions; later support pause/resume and alerts only when those APIs exist. | ADMIN, OPERATIONS | `/api/v1/monitoring/schedules` and child `/runs`; future pause/alert actions require additional APIs | P2 | N |
| Subscription Administration | Maintain plans, versions and entitlement policy; do not activate sales/renewals until payment and entitlement flows are verified. | ADMIN | Subscription plan/version/entitlement APIs (exact paths to verify); `/api/v1/billing` when payment-backed | P2 | N |
| Admin Audit and Reports | Review actor, time, changed values and operational/business reports for accountable administration. | ADMIN; limited OPERATIONS for scoped operational reports | Audit API and reports/dashboard APIs (exact paths to verify); domain APIs for report data | P1 | N |
| Coverage and Geo Configuration | Manage coverage zones, clusters, territories and service eligibility inputs when these are selected for the launch area. | ADMIN | Geo/coverage/cluster APIs (exact paths to verify); service catalog and pricing APIs | P1 | N |
| Notification Template Settings | Configure approved status templates and delivery channels; inspect delivery outcomes where supported. | ADMIN | Notification template/configuration and delivery APIs (exact paths to verify) | P1 | N |

## Recommended implementation order

1. **Staff Login and Session** — establish the runnable shell, in-memory auth, server-authorized identity/roles, and 401/403 handling. Resolve the role contract and ADMIN/OPERATIONS policy mismatch first.
2. **Operations Dashboard** — provide a home for triage and operational visibility; initially show only data with confirmed scope and stable APIs.
3. **Service Catalog Settings and Pricing Settings** — define the small pilot service set and approved configurable prices, eligibility, SLA, visit, and evidence rules before quoting.
4. **Customer Management and Quote Work Queue** — support staff-assisted customer context and quote creation/decision flow; enforce quote snapshot and authorization rules.
5. **Service Request Queue and Assignment Board** — deliver the accepted quote into exactly one request and enable manual assignment for pilot fulfillment.
6. **Agents and Visit Schedule and Detail** — validate roster/assignment access, and support visit-required and no-visit service paths.
7. **Evidence Review and Completion** — integrate only after secure scoped upload/download, evidence validation, and completion rules are verified.
8. **Payment and Refund Operations** — integrate provider-confirmed settlement, idempotency, invoice policy, refund and reconciliation flows before enabling paid customer checkout; this is a paid-pilot backend gate.
9. **User and Role Administration** — expose only after the role/resource policy is established, with audited changes and tests for denied access. A restricted back-office process can operate during initial pilot if this screen is not ready.
10. **Property Lookup, Leads, Deals, Audit/Reports, Coverage, Notification Templates** — add after core paid fulfillment or as a specific pilot operating need, each behind verified API/resource contracts.
11. **Monitoring Operations and Subscription Administration** — defer by default; bring forward only if the product owner includes those offers in cohort one and their lifecycle, payment/entitlement, support, and exception APIs are ready.

The paid pilot release gate also includes the roadmap's PostgreSQL end-to-end, security, deployment/rollback, and recovery proof. A screen being implemented does not by itself make its underlying workflow pilot-ready.
