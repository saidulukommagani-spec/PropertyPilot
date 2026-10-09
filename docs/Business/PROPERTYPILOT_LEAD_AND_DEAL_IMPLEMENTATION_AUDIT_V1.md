# PROPERTYPILOT_LEAD_AND_DEAL_IMPLEMENTATION_AUDIT_V1

**Scope:** Static repository analysis only. No code was changed for this audit, and no build or tests were run.

**Workspace caveat:** The working tree contains incomplete revenue-hardening edits from before this audit request. Where those edits affect Lead or Deal relationships, they are called out separately; they do not count as verified, working integrations.

## 1. Executive Summary

Lead and Deal have a backend foundation, but the business workflow described in the blueprints is incomplete.

- **Lead:** Create, read, status update, and assignment APIs exist. Lead activities are partly recorded by the service, but the activity service has no controller endpoint. There is no Lead UI, route, or menu.
- **Deal:** Create, read, list, participant, status, finalize, cancel, complete, and commission-billing operations exist. The frontend has deal pages and navigation, but it does not convert a Lead into a Deal.
- **Lead → Deal:** **PARTIALLY_SUPPORTED**. Deal creation accepts an optional `leadId`, and the service associates the deal with that lead. It does not convert or update the lead as part of the operation.
- **Lead → Service Request:** **NOT_SUPPORTED**. No implemented lead-to-request operation or relationship was found.
- **Deal → Invoice:** **SUPPORTED for commission invoices on completed deals**, using the billing service. This is narrower than general deal billing.
- **Invoice → Payment:** The payment API exists, but the current workspace contains incomplete settlement edits. Do not treat settlement as verified or launch-ready based on this tree.

**Overall Lead/Deal readiness: PARTIAL.** Deal execution has more coverage than Lead lifecycle management. The missing Lead UI and disconnected Lead-to-service workflow prevent the business journey from matching the blueprint.

## 2. Lead Backend Inventory

Lead endpoints are defined in `propertypilot-backend/src/main/java/com/propertypilot/web/controller/LeadController.java`. The service is `LeadServiceImpl.java`.

| Endpoint | Request | Response | Business purpose |
|---|---|---|---|
| `POST /api/v1/leads` | `LeadCreateRequest` | `LeadResponse` (201) | Creates a lead for the current customer and records an internal “Lead Created” activity |
| `GET /api/v1/leads/{leadId}` | Path `leadId` | `LeadResponse` | Retrieves a lead, subject to service-level ownership validation |
| `PATCH /api/v1/leads/{leadId}/status?status=...` | `LeadStatus` query parameter | `LeadResponse` | Changes status; service restricts this action to admin |
| `GET /api/v1/leads/my` | None | `List<LeadResponse>` | Returns leads associated with the current customer |
| `PATCH /api/v1/leads/{leadId}/assign` | `LeadAssignRequest` | `LeadAssignmentResponse` | Assigns an agent; service restricts this action to admin |

### Lead DTOs

- `LeadCreateRequest`: `source`, `propertyType`, `location`, `budgetMin`, `budgetMax`, `notes`.
- `LeadResponse`: lead ID, source, property type/location, status, budget, notes, contact/conversion timestamps, assigned-agent ID/code.
- `LeadAssignRequest`: agent ID.
- `LeadAssignmentResponse`: lead ID, agent ID/code, status.
- `LeadActivityCreateRequest` and `LeadActivityResponse`: activity data exist in the backend, but there is no activity controller/API route.

### Repositories and entities

- Repositories: `LeadRepository`, `LeadJpaRepository`, `LeadActivityRepository`.
- Entities: `LeadEntity`, `LeadActivityEntity`.
- The two lead repositories overlap: both are JPA repositories for `LeadEntity`.
- The activity service is implemented, but its create/list methods are not exposed through a controller.

### Observed implementation gaps

- The controller does not use `@Valid` on lead creation, and the request DTO has no bean validation constraints.
- `LeadCreateRequest` includes property type and location, but `LeadServiceImpl.createLead` does not copy those fields onto the entity.
- Creation resolves the current customer, so an admin-created lead path is not evident.
- There is no list-all/search/filter endpoint in the Lead controller.
- Assignment records an activity, but no visible active-agent/skill validation is performed in the inspected method.

## 3. Lead Domain Model

The implemented `LeadEntity` has an optional customer association, optional assigned agent, source, status, budget, notes, property-type/location text, and contact/conversion timestamps. The migration for leads creates the customer foreign key and stores source/status/budget/notes; later migrations add lead activity and additional lead fields.

```text
Customer 1 ─── 0..N Lead
Agent    1 ─── 0..N assigned Leads

Lead
 ├── source
 ├── status
 ├── budget range
 ├── notes
 ├── propertyType/location (text fields)
 └── activities (LeadActivityEntity)
```

| Relationship | Finding |
|---|---|
| Customer | **Implemented** — nullable customer association on `LeadEntity` |
| Property | **Not implemented as an entity relationship** — property type and location are text fields; no `Property` foreign key |
| Service Request | **Not implemented** — no service-request association or lead-to-request service operation found |
| Deal | **Partial** — `DealEntity` has an optional lead association and `CreateDealRequest` accepts `leadId`; no reciprocal conversion operation |
| Quote | **Not implemented** — no quote reference on the baseline Lead model or Lead DTOs |
| Documents | **Not implemented** — no lead-document relationship found |

The current working tree has additional in-progress Deal/Quote fields from the interrupted revenue-hardening work. No migration or completed workflow accompanies those fields, so they should not be considered verified Lead integrations.

## 4. Lead Workflow

The actual `LeadStatus` values are:

`NEW`, `CONTACTED`, `QUALIFIED`, `CONVERTED`, `LOST`, `CLOSED`.

The service creates leads with `NEW`. It sets `contactedAt` when a status update changes a lead to `CONTACTED`, and `convertedAt` when changed to `CONVERTED`.

The API accepts a requested enum status directly. The inspected service does not enforce a transition graph or prevent transitions from terminal statuses. Therefore, the implementation does **not** establish a controlled workflow such as:

```text
NEW → CONTACTED → QUALIFIED → CONVERTED
                         └──→ LOST / CLOSED
```

That diagram is a possible business interpretation, not a transition rule enforced by the current code.

The operational blueprint includes additional stages such as `ASSESSMENT`, `SERVICE_REQUIRED`, and `READY_FOR_DEAL`; these are absent from the implementation enum.

## 5. Lead Integrations

| Integration | Classification | Evidence |
|---|---|---|
| Properties | **PARTIALLY_IMPLEMENTED** | Lead stores property type and location as text; no property entity relationship |
| Service Requests | **NOT_IMPLEMENTED** | No lead-to-request API/service or relationship found |
| Visits | **NOT_IMPLEMENTED** | No Lead/Visit association or workflow found |
| Evidence | **NOT_IMPLEMENTED** | No lead evidence relationship found |
| Quotes | **NOT_IMPLEMENTED** | No Lead/Quote linkage or operation found |
| Deals | **PARTIALLY_IMPLEMENTED** | Deal creation can accept and store a lead ID; it does not convert/update the lead atomically |
| Invoices | **NOT_IMPLEMENTED** | No direct lead invoice integration |
| Payments | **NOT_IMPLEMENTED** | No direct lead payment integration |

## 6. Deal Backend Inventory

Deal endpoints are defined in `propertypilot-backend/src/main/java/com/propertypilot/web/controller/DealController.java`; business logic is in `DealServiceImpl.java`.

| Endpoint | Request | Response | Business purpose |
|---|---|---|---|
| `POST /api/v1/deals` | `CreateDealRequest` | `DealResponse` | Creates a deal; service requires admin and loads property/seller; optional `leadId` associates a lead |
| `GET /api/v1/deals/{dealId}` | Path `dealId` | `DealResponse` | Retrieves a deal with service-level access validation |
| `GET /api/v1/deals` | None | `List<DealResponse>` | Lists all deals; service requires admin |
| `POST /api/v1/deals/{dealId}/participants` | `AddDealParticipantRequest` | `DealParticipantResponse` | Adds a customer participant |
| `GET /api/v1/deals/{dealId}/participants` | Path `dealId` | `List<DealParticipantResponse>` | Lists participants with service-level access validation |
| `POST /api/v1/deals/{dealId}/participants/{participantId}/select` | Path IDs | `DealParticipantResponse` | Selects a buyer participant and updates deal status |
| `PUT /api/v1/deals/{dealId}/status` | `UpdateDealStatusRequest` | `DealResponse` | Sets the requested deal status |
| `POST /api/v1/deals/{dealId}/finalize` | `FinalizeDealRequest` | `DealResponse` | Sets final amount/registration fields and marks the deal completed |
| `POST /api/v1/deals/{dealId}/cancel?reason=...` | `reason` query parameter | `DealResponse` | Cancels a deal |
| `PUT /api/v1/deals/{dealId}/complete` | `CompleteDealRequest` | `DealResponse` | Completes a deal and creates commission invoices |

### Deal DTOs

- `CreateDealRequest`: property ID, seller customer ID, optional lead ID, deal type, expected amount, commission percentages, remarks, source and exclusive-listing flag.
- `DealResponse`: deal/property/seller/lead IDs, type/status, expected/final amounts, commission figures, creation/closure timestamps and remarks.
- `UpdateDealStatusRequest`: deal status and remarks.
- `FinalizeDealRequest` / `CompleteDealRequest`: final amount and related close fields.
- `AddDealParticipantRequest` / `DealParticipantResponse`: participant data.

### Entities and repositories

- Entities: `DealEntity`, `DealParticipantEntity`, `DealActivityEntity`, `DealMilestoneEntity`, `DealServiceRequestLinkEntity`.
- Repositories found: `DealRepository`, `DealParticipantRepository`.
- No Deal Activity, Deal Milestone, or Deal–Service Request link repositories or API workflow were found. Those entities appear dormant in the implementation.
- No deal-table creation was found in the inspected Flyway migration files, despite the JPA deal entity mapping to `deals`. This is a database deployment concern, particularly with production schema validation enabled.

## 7. Deal Domain Model

The established Deal model associates a deal with a property, seller customer, optional lead, and participants. Selected buyer is stored as a UUID on the deal; participant rows are separate entities. Deal → Invoice billing is initiated by service logic at completion, using generic billing fields (`entityType`/`entityId`) in the established design.

```text
Customer (seller) ─── 1..N Deal
Property           ─── 1..N Deal
Lead               ─── 0..N Deal
Deal               ─── 1..N DealParticipant ─── Customer
Deal               ─── commission invoices (on completion)
Invoice            ─── 0..N Payments
```

| Relationship | Finding |
|---|---|
| Customer | **Implemented** — seller association and customer participants |
| Property | **Implemented** — deal creation requires a property |
| Lead | **Partial** — optional `leadId` accepted and linked when creating a deal |
| Quote | **Not implemented in the verified business flow** — quote acceptance does not create a linked deal; current worktree additions are unverified |
| Invoice | **Partially implemented** — completion creates commission invoices for a deal |
| Payment | **Indirect** — payment is associated with invoice; no direct Deal–Payment relationship/API |
| Documents | **Not implemented** — no deal-document association or document workflow found |

## 8. Deal Workflow

The implemented `DealStatus` enum contains:

`NEW`, `BUYER_SEARCH_IN_PROGRESS`, `BUYER_SHORTLISTED`, `OFFER_RECEIVED`, `NEGOTIATION_IN_PROGRESS`, `BUYER_SELECTED`, `SITE_VISIT_SCHEDULED`, `DOCUMENT_VERIFICATION`, `AGREEMENT_PENDING`, `REGISTRATION_SCHEDULED`, `COMPLETED`, `CANCELLED`.

Creation sets `NEW`. Buyer selection sets `BUYER_SELECTED`. Completion actions set `COMPLETED`; cancellation sets `CANCELLED`.

The status update service accepts an enum value and assigns it directly; no allowed-transition graph is enforced. The API therefore supports status changes, but the repository does not establish guarded transitions like `NEW → NEGOTIATION → AGREEMENT → COMPLETED`.

The frontend exposes the full enum in its edit/status UI. Its deal detail also states that full status-change audit history is not included in the deal API. The deal controller only annotates the completion endpoint with method-level admin authorization; the other methods rely on service/security checks.

## 9. Lead → Deal Analysis

**Classification: PARTIALLY_SUPPORTED**

Existing support:

- `CreateDealRequest` has an optional `leadId`.
- `DealServiceImpl.createDeal` loads the Lead when that ID is provided and assigns it to the Deal.
- Deal response includes `leadId`.
- Lead status can be changed to `CONVERTED`.

Missing from the conversion workflow:

- No `convertLeadToDeal` endpoint or service method.
- No single transaction that creates a deal and changes the lead to `CONVERTED`.
- No check that a lead is qualified or otherwise eligible for conversion.
- No idempotency protection against converting the same lead repeatedly.
- The frontend deal creation form does not expose lead selection or submit a `leadId`.
- There is no Lead UI to initiate or review conversion.

## 10. Service Revenue Integration

| Flow segment | Classification | Finding |
|---|---|---|
| Lead → Service Request | **NOT_SUPPORTED** | No lead-to-service-request API, relationship, or service workflow found |
| Service Request → Deal | **NOT_SUPPORTED** | A `DealServiceRequestLinkEntity` exists, but no repository/service/controller integration was found |
| Deal → Invoice | **SUPPORTED, narrowly** | Deal completion calculates commissions and calls the billing service to create commission invoices |
| Invoice → Payment | **PARTIALLY_SUPPORTED** | Payment APIs associate payments with invoices, but settlement verification is not established as complete in this workspace |

The Lead blueprint allows a lead to generate service requests, deals, both, or neither. The implementation only supports a direct optional lead reference during deal creation; it does not implement the “requirements fulfilled, then convert” service journey.

## 11. Frontend Coverage

| Capability | Pages | Routes/menu | Classification |
|---|---|---|---|
| Lead list, create, detail, activity, assignment, conversion | None found | No `/leads` route and no Lead sidebar item | **MISSING** |
| Deal list/dashboard/detail/create/edit | `features/deals/pages/DealPages.tsx` | `/deals...`; Deals sidebar item exists | **PARTIAL** |
| Create deal from Lead | No lead selection in the create form | No Lead entry point | **MISSING** |
| Deal participants | Participant display exists; API methods exist | Deal detail page | **PARTIAL** |
| Deal timeline/audit | Detail page shows creation/closure dates and notes; full history is not provided by API | Deal detail route | **PARTIAL** |
| Deal invoice creation | Invoice creation page finalizes eligible buyer-selected deals and directs to resulting invoice | `/invoices/new` | **PARTIAL** — it completes the deal as part of “invoice creation,” so the operation combines two business actions |

Deal routes are `/deals/dashboard`, `/deals`, `/deals/new`, `/deals/:id/edit`, and `/deals/:id`. The admin sidebar contains a Deals menu item. The Operations sidebar does not show Deals; customer navigation has no deal page. Deal fields such as expected close date and owner are displayed as empty/“not returned by API” in the UI.

## 12. Blueprint Alignment

### Lead Blueprint Alignment

| Blueprint capability | Implementation |
|---|---|
| Customer, new customer, referral, admin and website sources | `source` is a string; only current-customer creation is exposed through the inspected service path |
| Property context | Text property type/location, no property foreign key |
| Lead activities | Activity entity and service exist; no activity controller route |
| Qualification and assessment | Basic status enum only; no assessment/service-required/ready-for-deal states |
| Generate one or more service requests | Missing |
| Convert to deal | Partial optional lead link; no conversion workflow |
| Lead UI | Missing |

### Deal Blueprint Alignment

| Blueprint capability | Implementation |
|---|---|
| Buy/sell/transaction opportunity | Property transaction deal types and workflow are represented |
| Lead conversion or direct opportunity | Direct create exists; optional lead link exists, but conversion is not orchestrated |
| Buyer/seller/property | Seller and property associations exist; participants and buyer selection exist |
| Documents and negotiation history | No complete document workflow; status and remarks are not a full negotiation/audit timeline |
| Services linked to deal | Link entity exists but no operational repository/service/controller path found |
| Closure and commission revenue | Completion and commission invoicing exist |
| Deal UI and navigation | Admin deal screens and menu exist; customer/operations journeys are absent |

The business and operational blueprints are in `docs/Business/PROPERTYPILOT_BUSINESS_BLUEPRINT_V1.md` and `docs/Business/PROPERTYPILOT_OPERATIONAL_WORKFLOW_V1.md`; the domain baseline is `docs/Business/PROPERTYPILOT_DOMAIN_MODEL_V1.md`.

## 13. Gap Analysis

1. **Backend functionality not exposed in UI:** Lead create/status/assignment APIs and internal activity capture have no dedicated Lead screens. Lead activities cannot be retrieved or created through an API controller.
2. **Missing screens:** Lead list/search, Lead detail, activity timeline, qualification/assessment, service generation, and conversion screens.
3. **Missing workflow integrations:** Lead → Service Request; Lead → Deal atomic conversion; Deal ↔ Service Request link operations; deal document management.
4. **Missing navigation:** No Lead route or menu. Deals are not visible in customer navigation; Operations does not have a Deals menu.
5. **Missing business capabilities:** Guarded lead/deal status transitions, complete lead-source intake paths, property relationship on Lead, conversion idempotency, and full deal audit history.
6. **Persistence concern:** Deal entities exist, but the inspected Flyway migrations do not create deal tables. Some Deal auxiliary entities also lack repositories and API use.
7. **Revenue handoff concern:** Deal completion generates commission invoices, not a generalized invoice for all services associated with the Deal. The invoice UI couples deal completion with invoice generation.

## 14. Development Effort Estimate

Approximate effort assumes existing design system and API patterns, one full-stack engineer, and includes implementation plus focused tests. It excludes deployment/UAT coordination.

| Work item | Size | Approximate effort |
|---|---|---:|
| Lead UI completion: list/search, create, detail, activity, assignment and status | Large | 8–14 engineer-days |
| Deal UI completion: API-backed details, participant management, true audit history, missing field coverage | Medium | 5–9 engineer-days |
| Lead → Service Request integration | Large | 8–15 engineer-days |
| Lead → Deal conversion workflow | Medium | 5–9 engineer-days |
| Deal revenue workflow hardening, including invoice idempotency and payment handoff | Large | 8–14 engineer-days |

These are directional estimates; schema repair and existing test-suite condition may add effort.

## 15. Recommended Next Step

Complete a **Lead workflow vertical slice** first: expose the existing Lead APIs in the UI, add the missing activity API path, and establish a controlled transition into service requests. Then implement atomic, idempotent Lead → Deal conversion. Before expanding deal screens, resolve deal-table migration coverage and verify deal billing and payment flows against the deployed schema.
