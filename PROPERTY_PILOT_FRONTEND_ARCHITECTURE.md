# PropertyPilot Frontend Architecture

**Status:** Implementation-ready frontend plan  
**Technology:** React, Vite, TypeScript, Material UI, React Router, Axios  
**Business authority:** PropertyPilot Business Truth  
**Architecture authority:** PropertyPilot Architecture Truth  
**Pilot sequencing:** PropertyPilot Execution Roadmap V1

## 1. Purpose and architecture decisions

The frontend is one web application with two role-specific portals:

- **Customer Portal:** lets customers manage their own customer profile and properties, purchase PropertyPilot services, review quotes, follow service delivery, and view authorized service records.
- **Admin / Operations Portal:** lets PropertyPilot staff manage customers and sales opportunities, configure services/pricing, quote and fulfill requests, assign internal agents, and monitor operations.
- **Agent experience:** for the pilot, use the internal responsive web application and agent role routes where backend APIs support them. Agents are fulfillment resources; customer screens must not expose vendor selection, vendor contact, or marketplace workflows.

These are different navigation and permission shells over shared frontend infrastructure, not separate deployments or separate business systems. PropertyPilot owns the customer relationship, service price, customer communication, and outcome. The UI must preserve that model.

The existing propertypilot-frontend directory is not a working React application: package.json and screen/route files contain setup or directory-outline text. Begin by establishing the runnable app shell and validating every API contract against backend DTOs. This document defines the target structure; it does not claim those screens already exist.

## 2. Admin Portal Architecture

Admin and operations users share the application shell but receive menus and actions from the role policy. Keep the administration shell distinct from customer navigation.

### Admin capabilities

- Customer search, onboarding support, customer profile and verification.
- Property lookup and ownership/support actions.
- Lead intake, qualification, assignment and activities.
- Deal tracking and service opportunity handoff.
- Service request queue, internal assignment and operational status.
- Pricing and quote preparation; service catalog and pricing configuration.
- Subscription administration and review; customer subscription sales remain hidden until payment and entitlement flows are approved.
- Visit schedule/status and evidence review, subject to secure file access.
- Monitoring schedule/run visibility. Schedule/run read views are available; pause/resume and alert workflows require backend completion.
- Agent roster and operational workload views.
- Admin settings for services, pricing and permitted reference/configuration data.

### Admin interaction rules

- Require explicit ADMIN or OPERATIONS authorization for each route and action; menu hiding is not authorization.
- Keep pricing inputs and internal costs in staff-only screens. Customer quote views expose only approved customer price, discounts/savings, tax, currency and quote lifecycle.
- Treat agent/vendor information as internal fulfillment data. Show only what an internal role needs.
- Make sensitive state changes confirmable and auditable: assignment/reassignment, quote issuance, cancellation, completion override, service configuration, and evidence disposition.
- Use server pagination/filter parameters for lists; do not load all customers, properties, leads, requests or audit rows into the browser.

## 3. Customer Portal Architecture

The customer portal is property-centered. The initial landing page summarizes properties, quotes, active service requests and recent outcomes. Each property detail page is the entry point to that property's service and document history.

### Customer capabilities

- View and update permitted customer profile data.
- Add and manage properties owned or authorized by the current customer.
- Browse active PropertyPilot services available for the property.
- Review quote line items, customer price, discounts, tax, expiry, then accept or reject.
- Create a request from an accepted quote and follow request/visit status.
- View invoices and payment history only after customer-facing backend APIs and payment provider flows are ready.
- View authorized documents, evidence, reports, Monitoring history, and Health results only when their API and storage workflows are complete.

Customer pages must obtain the acting customer from the authenticated session/backend relationship. Do not treat a customer ID in a URL, form, query string, or browser cache as proof of ownership.

## 4. Folder Structure

Use feature-first modules, separating app setup, shared UI, and domain screens. Keep current product vocabulary and reuse the existing backend modules.

    propertypilot-frontend/
      index.html
      package.json
      vite.config.ts
      tsconfig.json
      src/
        app/
          App.tsx
          main.tsx
          router/
            router.tsx
            routePaths.ts
            guards/
              RequireAuth.tsx
              RequireRole.tsx
          providers/
            AppProviders.tsx
            AuthProvider.tsx
            QueryProvider.tsx
          layouts/
            PublicLayout.tsx
            CustomerLayout.tsx
            StaffLayout.tsx
        api/
          httpClient.ts
          apiError.ts
          authApi.ts
          customersApi.ts
          propertiesApi.ts
          leadsApi.ts
          dealsApi.ts
          servicesApi.ts
          pricingApi.ts
          quotesApi.ts
          serviceRequestsApi.ts
          subscriptionsApi.ts
          visitsApi.ts
          evidenceApi.ts
          monitoringApi.ts
          agentsApi.ts
          adminApi.ts
        features/
          auth/
          dashboard/
          customers/
          properties/
          leads/
          deals/
          service-requests/
          quotes/
          subscriptions/
          visits/
          evidence/
          monitoring/
          agents/
          admin-settings/
        shared/
          components/
            DataTable/
            ConfirmDialog/
            EmptyState/
            ErrorState/
            LoadingState/
            StatusChip/
            MoneyDisplay/
            PropertySelector/
            PageHeader/
            FormField/
          hooks/
          utils/
          types/
          validation/
          theme/
        tests/
          setup.ts
          mocks/
          fixtures/

### Folder conventions

- A feature owns its pages, feature-specific components, hooks, schemas and API query functions.
- API modules map backend DTOs into typed frontend contracts; do not duplicate HTTP calls in page components.
- Shared components contain presentation and common interaction patterns, not business rules.
- Keep route path constants in one place. Do not build URLs from untrusted IDs without encoding/validation.
- Place role-independent customer-safe UI in shared components. Do not share a page if it requires subtly different authorization or financial disclosures.

## 5. Routing Structure

Use React Router nested routes with public, customer, and staff layouts. Route guards improve navigation and user feedback only; backend authorization remains authoritative.

| Route | Portal / role | Screen |
|---|---|---|
| /login | Public | Login |
| /app | CUSTOMER | Customer dashboard |
| /app/properties | CUSTOMER | Property list |
| /app/properties/:propertyId | CUSTOMER | Property detail |
| /app/properties/:propertyId/documents | CUSTOMER | Property documents (after secure storage contract) |
| /app/services | CUSTOMER | Service catalog |
| /app/quotes | CUSTOMER | My quotes |
| /app/quotes/:quoteId | CUSTOMER | Quote detail and accept/reject |
| /app/requests | CUSTOMER | My service requests |
| /app/requests/:requestId | CUSTOMER | Request timeline and outcome |
| /app/subscriptions | CUSTOMER | Subscription view; purchase disabled until payment readiness |
| /app/monitoring | CUSTOMER | Read-only schedule/run history in Phase 1 |
| /staff | ADMIN, OPERATIONS | Operations dashboard |
| /staff/customers | ADMIN, OPERATIONS | Customer list/detail |
| /staff/properties | ADMIN, OPERATIONS | Property lookup |
| /staff/leads | ADMIN, OPERATIONS, approved SALES role | Leads |
| /staff/deals | ADMIN, OPERATIONS, approved SALES role | Deals |
| /staff/requests | ADMIN, OPERATIONS | Service request queue |
| /staff/requests/:requestId | ADMIN, OPERATIONS, assigned AGENT for allowed actions | Request detail |
| /staff/quotes | ADMIN, OPERATIONS | Quote work queue |
| /staff/visits | ADMIN, OPERATIONS, AGENT | Visit schedule and work |
| /staff/evidence | ADMIN, OPERATIONS, assigned AGENT for upload | Evidence review/work |
| /staff/monitoring | ADMIN, OPERATIONS | Monitoring schedule/run operations |
| /staff/agents | ADMIN, OPERATIONS | Agent roster and assignment |
| /staff/settings/services | ADMIN | Service catalog configuration |
| /staff/settings/pricing | ADMIN, approved OPERATIONS | Pricing configuration |
| /staff/settings/users | ADMIN | User/role administration |

Backend role names and policy must be normalized before relying on this route map. In particular, SecurityConfig currently applies ADMIN-only protection to /api/admin/** while AdminOperationsController also declares OPERATIONS access. Align the server policy before integrating Operations users.

## 6. Authentication Flow

The current backend login contract is POST /api/v1/auth/login and returns a bearer token, token type, and expiration. The JWT subject is the login email; the frontend must not treat custom JWT claims as the customer or role authority.

1. App starts in an unauthenticated state.
2. Login form posts credentials to authApi.
3. On success, store the access token in in-memory AuthContext and load GET /api/v1/users/me.
4. Build role-aware navigation from the server-authorized identity/roles. If the response does not include roles, add or use an authorized role/profile endpoint; do not decode a role from untrusted client state.
5. On each Axios request, attach Authorization: Bearer <token>.
6. On 401, clear in-memory session and redirect to login with a neutral session-expired message.
7. On 403, keep the session and show an access-denied state; do not retry.
8. On logout, clear token, query cache, and user-specific in-memory state.

### Identity contract required for customer screens

The current UserResponse for /api/v1/users/me contains userId, name, email, mobile and status, but no customerId or roles. CustomerDashboardController requires a customerId path parameter. Before F2 customer dashboard integration, provide a safe authenticated “current customer context” contract (for example, enrich the /me response or add a /customers/me route) and expose effective roles for routing. The client must never infer customer ownership from an arbitrary customerId parameter.

The backend currently provides no refresh-token flow. For the pilot, use the access token in memory and expect a fresh login after a full reload. Do not persist bearer tokens in localStorage. If persistent sessions are later required, add a server-managed refresh/session mechanism using Secure, HttpOnly, SameSite cookies and CSRF-aware design.

## 7. API Client Design

Use one Axios instance and feature-level API modules. Configure VITE_API_BASE_URL per environment; never embed production hosts or secrets in the frontend build.

    httpClient
      ├── authApi
      ├── propertiesApi
      ├── customersApi
      ├── servicesApi / pricingApi / quotesApi
      ├── serviceRequestsApi / visitsApi / evidenceApi
      ├── leadsApi / dealsApi
      ├── subscriptionsApi / monitoringApi
      └── adminApi / agentsApi / reportsApi

### Axios instance behavior

- Set JSON content type for JSON requests; allow FormData to set its own boundary for future uploads.
- Attach in-memory bearer token through a request interceptor.
- Normalize errors in one response interceptor; do not swallow HTTP status or validation details.
- On 401, clear session once and navigate to login; prevent redirect loops on login endpoints.
- Do not automatically retry POST/PUT/PATCH/DELETE. Retry safe GET requests only where appropriate. Mutating requests require explicit idempotency where the backend supports it.
- Set request timeouts and use AbortController/cancellation for unmounted screens and replaced searches.
- Redact passwords, tokens, payment references and signed file URLs from logs/telemetry.
- Keep backend route prefixes exactly as implemented until the server API versioning is deliberately standardized. Current controllers mix /api/v1 and /api routes.

### Initial backend API map

| Feature | Current backend contract to wrap |
|---|---|
| Authentication / profile | POST /api/v1/auth/login; GET /api/v1/users/me |
| Properties | /api/v1/properties |
| Customer dashboard | /api/customer-dashboard/{customerId}; use only after current-customer context is available |
| Service catalog | /api/services; admin operations under /api/services/admin |
| Pricing | POST /api/v1/pricing/calculate (currently staff-restricted) |
| Quotes | /api/v1/quotes and /api/v1/quotes/{quoteId}/accept or /reject |
| Service requests | /api/service-requests |
| Leads / Deals | /api/v1/leads; /api/v1/deals |
| Visits | /api/v1/visits |
| Evidence / GPS | /api/evidence; /api/gps-verifications |
| Monitoring | /api/v1/monitoring/schedules and child /runs |
| Payments / Billing | /api/payments; /api/v1/billing |
| Admin operations / agents | /api/admin/operations; /api/admin/agents |

Use request and response DTOs from backend code as the contract. Do not expose persistence entities directly in frontend types.

## 8. State Management Approach

Use a small, explicit split between server state and local UI state:

- **Server state:** use TanStack Query (React Query) around Axios for fetch lifecycle, cache, pagination, invalidation and mutation status. The existing frontend setup notes mention react-query, but the actual package metadata must be replaced with valid project dependencies.
- **Authentication state:** AuthContext holds the current user, effective roles and in-memory access token. Do not duplicate this in Redux, browser storage, or query data.
- **Local UI state:** component state for dialogs, tabs, drafts and filters. Use URL search parameters for shareable list filters and pagination.
- **Forms:** controlled form state with TypeScript DTOs and client-side validation that mirrors, but never replaces, backend validation.
- **Cross-screen business data:** rely on server query cache. Do not create a second client-side source of truth for quote price, payment state, request status, ownership or agent assignment.

### Query and mutation conventions

- Use stable keys such as ['properties', propertyId], ['quotes', 'mine'], ['requests', filters], ['monitoring', scheduleId, 'runs'].
- Invalidate the narrowest affected query after a successful mutation; update cached status only from the server response.
- Clear all customer-specific query data on logout and when the authenticated identity changes.
- Paginate staff/customer lists at the API. Keep filter/sort state in the URL for reloadable list views.
- Model loading, empty, stale, error and forbidden states explicitly.
- Use optimistic updates only for reversible low-risk preferences. Do not optimistically mark a quote accepted, request completed, invoice paid, evidence approved, or subscription active.

## 9. Screen Hierarchy and Inventory

The screen list below covers the requested inventory. “Customer” means a customer-owned view; “Staff” means internal PropertyPilot operations. Some backend records exist without a safe or complete customer-facing workflow, so the readiness column indicates integration timing.

| Screen | Portal / roles | Hierarchy and key user tasks | Backend readiness |
|---|---|---|---|
| **Login** | Public | Email/password; validation; loading; invalid credentials; session expiry; support path. | API exists; start F1. |
| **Dashboard** | Customer; separate staff operations dashboard | Customer: properties, active requests, quotes needing decision, recent outcomes. Staff: due/stuck requests and workload. | Basic customer summary and operations APIs exist; current-customer ID contract and staff role mismatch need resolution. |
| **Customers** | Staff ADMIN/OPERATIONS | Search/list → customer detail → profile, verification and associated properties/requests. Creation/onboarding path should be staff-assisted and audited. | Partial APIs; server-side pagination and role/ownership contract first. |
| **Properties** | Customer; staff support view | Customer property list → property detail → ownership/location/documents/services/history. Staff can search for support. | CRUD and ownership checks exist; current-customer bootstrap and co-owner semantics need confirmation. |
| **Leads** | Staff SALES/ADMIN/OPERATIONS as authorized | Lead queue → lead detail/activity → assignment/status → approved customer/deal conversion action. | API exists for create/read/status/assignment; limit actions until conversion and access behavior are verified. |
| **Deals** | Staff SALES/ADMIN/OPERATIONS as authorized | Deal pipeline/list → deal detail/participants/activities → proposed service scope → approved request handoff. Never show as vendor-marketplace transaction. | API exists; keep internal and gate conversion/status actions. |
| **Service Requests** | Customer and staff; assigned agent actions | Customer: request list/detail timeline. Staff: queue/detail, SLA, property, quoted service, assignment, visits, evidence and completion. | Quote-backed creation and lifecycle APIs exist; role/resource HTTP tests required. |
| **Quotes** | Customer quote list/detail; staff preparation | Staff creates/prices quote. Customer sees immutable line items, final price/tax/expiry and accepts/rejects. Accepted quote then leads to request. | Customer decision APIs exist; calculation is staff-only and no runnable UI exists. |
| **Subscriptions** | Customer read view; staff administration | Plans and current subscription/benefits/usage; customer purchase/renew UI only when payment and entitlement flows are approved. | APIs/entities exist; keep purchase and auto-renew actions disabled for pilot. |
| **Visits** | Customer read timeline; staff/assigned agent execution | Visit list/detail, schedule, start/complete/cancel, request linkage and status timeline. | Visit APIs exist; validate assigned-agent access and no-visit/multi-visit service policy. |
| **Evidence** | Assigned agent upload; staff review; customer read | Evidence list by request/visit, upload/capture, metadata, review result and authorized preview/download. | Metadata APIs exist, but caller-provided URL is not a controlled file contract; no live upload integration until storage is secured. |
| **Monitoring** | Customer read; staff operations | Customer schedule/history → run → linked service request. Staff view due/overdue/exceptions. Later actions: pause/resume and alert review. | Phase 1 supports schedule/run reads and creation by staff; no pause/alert/outcome screens until backend exists. |
| **Agents** | Staff ADMIN/OPERATIONS; agent self/work queue if APIs allow | Agent roster/skills/availability → assigned work → assignment response and workload. Customers do not browse/select agents. | Dashboard/assignment APIs exist; agent-specific APIs and role tests need verification. |
| **Admin Settings** | ADMIN; limited approved OPERATIONS | Service catalog, pricing rules/profiles, allowed reference data and notification templates. Show effective state and audit actor/time. | Service/pricing configuration APIs exist; hide unfinished settings and reconcile role authorization before integration. |

### Navigation hierarchy

- **Customer:** Dashboard → Properties → Property detail → Services / Requests / Documents / Monitoring (when available); Quotes; Subscriptions (read-only until safe); Profile.
- **Staff:** Operations dashboard → Customers / Properties / Leads / Deals / Service Requests / Visits / Evidence / Monitoring / Agents; Admin Settings as role permits.
- **Agent:** Assigned work → Assignment detail → Visit execution → Evidence/GPS capture. Do not provide broad customer/property search.
- Keep global search and list filters scoped to the current user's authorized records. Do not create vendor directories or customer-vendor contact workflows.

## 10. Shared Components

Build and reuse these shared components before duplicating feature-specific variations:

- **App shell:** responsive navigation, portal identity, role-based menu, breadcrumbs, account/logout menu.
- **Page primitives:** PageHeader, LoadingState, EmptyState, ErrorState, NotFoundState, AccessDeniedState.
- **Data display:** paginated DataTable, StatusChip, MoneyDisplay (currency/tax-safe), DateTimeDisplay, PropertySummary, CustomerSummary.
- **Forms:** typed FormField wrappers, validation messages, Address/Property selector, SearchFilterBar.
- **Workflow controls:** ConfirmDialog, state transition button group, ActivityTimeline, AuditSummary, FileMetadataList.
- **Feedback:** Snackbar/toast for success; inline field errors and durable page-level errors.
- **Security-aware file UI:** AuthorizedDownloadButton/preview that uses a scoped backend operation; never render an arbitrary URL from a DTO as a trusted file.
- **Testing helpers:** authenticated test render, API fixtures, role fixtures, and route test utilities.

MUI theme tokens and common layout components should be centralized under shared/theme and app/layouts. Domain labels and state-to-color mapping should be centralized; status transitions must still be determined by backend responses.

## 11. Error Handling Strategy

### API error model

Create one frontend ApiError representation with HTTP status, stable error code when returned, user-safe message, field validation errors, request/correlation ID if available, and retryability. Adapt the existing backend error formats in one response interceptor; do not make every page parse Spring error bodies independently.

### User-facing behavior

- **400 / validation:** show field errors and preserve safe form entries.
- **401:** clear session and return to login; explain that the session expired.
- **403:** show access denied; do not retry or reveal the protected object.
- **404:** show not found; use generic messaging for sensitive identifiers.
- **409:** explain the conflict (for example, quote already converted) and refresh authoritative state.
- **429:** show retry-after guidance; do not loop.
- **5xx / network:** preserve page context, offer safe retry for reads, and show a support/reference ID where available.
- **Payment or workflow mutation timeout:** show “status is being confirmed” and refetch authoritative state; do not repeat payment or completion blindly.

Use route-level error boundaries for unexpected render failures. Telemetry must exclude credentials, tokens, property documents, evidence contents, payment instrument data, and signed URLs.

## 12. Frontend Security Considerations

1. The frontend is not a security boundary. Every protected read and mutation must be authorized by the backend.
2. Keep access tokens in memory; do not store bearer tokens or sensitive business data in localStorage. Clear auth and query cache on logout/401.
3. Do not decode client-provided route IDs as ownership evidence. Use current-user context and resource APIs; backend verifies each access.
4. Route guards and hidden buttons are usability controls only. The server must reject unauthorized role/action/resource combinations.
5. Render text as text; avoid unsafe HTML. Sanitize any deliberately supported rich text and apply a restrictive Content Security Policy at hosting.
6. Configure production CORS for the exact frontend origin. Use HTTPS only. Do not embed server secrets in Vite variables; VITE_* values are public build-time data.
7. Handle uploads through an authenticated backend-issued upload operation; validate file type and size client-side for usability and again server-side for security.
8. Never expose vendor contact or vendor selection as a customer feature. Keep internal cost, staff notes, and admin data out of customer DTOs.
9. Avoid storing sensitive state in analytics, browser logs, URLs, error reports, or persistent query caches.
10. Use dependency lockfiles, automated dependency review, and production build checks. Do not rely on development server protections in deployment.

## 13. Recommended Development Sequence

| Phase | Deliverables | Dependencies / exit criteria |
|---|---|---|
| **F0 — Runnable foundation** | Valid package manifest and lockfile; Vite/TypeScript build; MUI theme; React Router; app providers; lint/build/test commands. | First task. Empty application starts and production build succeeds. |
| **F1 — Authentication and layouts** | Login, in-memory auth context, /me bootstrap, customer/staff layouts, role guards, 401/403 handling. | Backend must provide effective roles and a current-customer context suitable for F2. |
| **F2 — Customer dashboard and properties** | Dashboard summary, property list/detail/create/edit, ownership-safe property history shell. | Property APIs verified through customer A/B security tests; current identity contract resolved. |
| **F3 — Service purchase core** | Service catalog, staff quote preparation, customer quote detail/accept/reject, accepted quote → request creation, request list/detail. | Pricing/quote/request DTOs frozen; idempotent request and correct customer price verified. |
| **F4 — Staff fulfillment** | Staff request queue, manual assignment, agent work view, visit schedule/detail and lifecycle. | Role/resource tests pass; selected service visit rules verified. |
| **F5 — Evidence and customer outcomes** | Secure evidence upload/list/preview, GPS status, document views, completion report. | Private storage, scoped file access and evidence review contract are implemented and tested. |
| **F6 — Admin operations** | Customer/lead/deal/agent screens; service and pricing settings; basic SLA/operations views. | Admin vs Operations policies and any Sales role are normalized; staff workflows verified. |
| **F7 — Payment and deferred service views** | Invoice, provider checkout/confirmation, payment history; Monitoring operational actions; Health/recommendation screens if included. | Payment provider/webhook verification and appropriate backend APIs exist. Keep inactive features hidden until then. |
| **F8 — Pilot readiness** | Responsive/accessibility pass, browser tests, security checks, production config, error telemetry, UAT and role-based training. | End-to-end pilot acceptance gates pass in production-like environment. |

### Definition of frontend-ready

A screen is ready to integrate when its API DTO and authorization contract are stable, loading/empty/error/forbidden states are specified, and a role/resource test covers at least one allowed and one denied case. A screen is ready to release when the real backend flow, file/payment boundaries where applicable, accessibility, and browser-level path have been verified.

## Source alignment

- Business authority: docs/Business/PROPERTY_PILOT_BUSINESS_TRUTH.md.
- Target domains, role/resource ownership, and service-first relationships: PROPERTY_PILOT_ARCHITECTURE_TRUTH.md.
- Critical path, parallel work and pilot scope: PROPERTY_PILOT_EXECUTION_ROADMAP_V1.md.
- Current API contracts referenced here: controllers and DTOs under propertypilot-backend/src/main/java/com/propertypilot/web/controller and application/dto.
