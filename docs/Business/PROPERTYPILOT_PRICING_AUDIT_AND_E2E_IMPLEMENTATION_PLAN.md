# PropertyPilot Pricing Audit and End-to-End Implementation Plan

**Audit type:** Read-only source, schema, migration, test, and documentation review  
**Date:** 2026-10-10  
**Scope:** Pricing-related implementation and references across the PropertyPilot repository  
**Changes made during audit:** None

## 1. Executive summary

PropertyPilot already has a meaningful pricing foundation. The backend calculates and persists estimates; the calculation returns Standard price, PropertyPilot customer price, savings, and pricing breakdowns; customers can review and accept quotes; accepted quotes are bound to service requests; request pricing is protected from mutation; and qualifying request completion creates an invoice.

The customer journey is not yet end to end. The estimate endpoint only permits Admin and Operations, and the quote creation screen is an operations-facing workflow. The customer service request form asks for an already accepted quote and explains that Operations currently calculates it. Customers therefore cannot select a property and service, get a dynamic estimate, accept it, and proceed to booking themselves.

Administrator configuration exists in backend APIs and in the service catalogue’s base-price editor, but the admin UI does not expose the full pricing configuration. In addition, important documented components—such as location-based travel calculation, allowances, add-ons, and pricing simulation—are not all present in the active calculation path.

The recommended direction is to use the existing estimate/quote/request architecture, complete its customer flow, and extend its rule data model and admin UI. Preserve historical estimate snapshots and existing IDs. Do not activate catalogue rows with zero or unapproved prices.

## 2. Review scope and evidence

The repository was searched for pricing, estimates, quotes, price rules, discounts, coupons, and savings. The matching primary web application code, pricing-related migrations and entities, frontend quote/customer/admin screens, pricing tests, and product/pricing documents were inspected. No pricing implementation was found in the Flutter app or the auxiliary scaffold directories.

The review did not open unrelated dependency/build output. No tests were run because this was an audit, not a verification request. No files or database rows were changed during the audit.

### Live database limitation

Flyway migrations and seed files were inspected. A fresh live database query was not performed: `psql` was unavailable and the shell had no PostgreSQL/Spring datasource environment variables. An earlier local database inspection recorded in this conversation found 134 services and 134 eligibility rules; 133 newly seeded services were inactive and had a zero base price. This is a prior observed development snapshot, not a fresh confirmation of current database contents.

## 3. Existing implemented workflow

### 3.1 Estimate calculation

`POST /api/v1/pricing/calculate` is restricted to Admin and Operations in [PricingController.java](../../propertypilot-backend/src/main/java/com/propertypilot/web/controller/PricingController.java). The request carries customer, property, service, one-way distance, ETA, and optional coupon. The service checks that the property belongs to the requested customer and that the service is active and billable.

The calculation selects the latest active, effective `service_price_rules` record for the service, falling back to `services.base_price`. A positive configured base amount is required. The submitted `vendorCost` value is retained for compatibility but replaced with the server-selected base amount before calculation.

See [PricingEngineServiceImpl.java](../../propertypilot-backend/src/main/java/com/propertypilot/application/service/impl/PricingEngineServiceImpl.java) and [CreatePricingEstimateRequest.java](../../propertypilot-backend/src/main/java/com/propertypilot/application/dto/CreatePricingEstimateRequest.java).

### 3.2 Standard vs PropertyPilot price

The calculator evaluates the Standard and Customer profiles independently. Current profile parameters include travel rate, distance mode, agent allowance, overhead percentage, PropertyPilot margin percentage, and GST percentage. It builds a Standard breakdown and a Customer breakdown, then returns standard price, customer price, and savings.

Customer-side discounts are applied after the profile calculation:

1. Eligible subscription discount
2. Cluster demand discount
3. Flexible-ETA discount
4. Optional coupon discount

The discounts are applied sequentially against the remaining Customer subtotal. Tax is recalculated on the discounted subtotal. The estimate response carries Standard and Customer breakdowns, savings, discount details, and estimate line items.

See [PricingCalculatorServiceImpl.java](../../propertypilot-backend/src/main/java/com/propertypilot/application/service/impl/PricingCalculatorServiceImpl.java), [PricingRuleEngineServiceImpl.java](../../propertypilot-backend/src/main/java/com/propertypilot/application/service/impl/PricingRuleEngineServiceImpl.java), and [PricingEngineServiceImpl.java](../../propertypilot-backend/src/main/java/com/propertypilot/application/service/impl/PricingEngineServiceImpl.java).

### 3.3 Estimate persistence and quote decision

The pricing engine persists each calculation to `pricing_estimates`, with a 30-minute expiry. It stores customer, property, service, subtotal, discounts, tax, total, currency, status, and a calculation snapshot. The snapshot includes the selected price-rule ID, one-way distance, travel mode, base cost, Standard and Customer breakdowns, and applied discounts. Line items and discount applications are stored separately.

The current quote API uses this estimate as the customer quote; it is not a separate quote record for this flow. Customers can list their quotes and accept or reject them. Acceptance records the authenticated user and timestamp. Coupon capacity is reserved under a lock when the quote is accepted. An expiry scheduler handles estimates that expire without a decision.

See [QuoteController.java](../../propertypilot-backend/src/main/java/com/propertypilot/web/controller/QuoteController.java), [QuoteServiceImpl.java](../../propertypilot-backend/src/main/java/com/propertypilot/application/service/impl/QuoteServiceImpl.java), and [QuoteExpiryScheduler.java](../../propertypilot-backend/src/main/java/com/propertypilot/application/scheduler/QuoteExpiryScheduler.java).

### 3.4 Quote display

The quote detail UI displays quote total, line items, Standard price, and customer savings. The backend saves separate service, travel, agent allowance, overhead, margin, discount, and tax lines. However, the customer-facing quote response currently aggregates service and fee amounts into one service subtotal, then reports discounts and tax. Thus the customer sees the Standard price/savings comparison, but not the full itemization described in the product pricing documents.

See [QuotePages.tsx](../../propertypilot-frontend/src/features/quotes/pages/QuotePages.tsx) and `QuoteServiceImpl.customerLineItems`.

### 3.5 Request, fulfillment, and invoice

Customer request creation requires a quote ID. The backend locks the estimate and verifies that it is accepted, not expired at acceptance, belongs to the authenticated customer, references a consistent customer/property/service, and has not already created a request. It checks that the service is still active and rechecks current eligibility. It copies the accepted quote total to the service request.

Migration V032 adds a unique request-to-estimate link and a database trigger that prevents later mutation of the request amount or pricing-estimate reference. The existing assignment, optional visit, and evidence workflows remain downstream. Qualifying request completion calls billing to create an invoice from the completed service request.

See [ServiceRequestServiceImpl.java](../../propertypilot-backend/src/main/java/com/propertypilot/application/service/impl/ServiceRequestServiceImpl.java), [V032__link_service_requests_to_quotes.sql](../../propertypilot-backend/src/main/resources/db/migration/V032__link_service_requests_to_quotes.sql), and [BillingServiceImpl.java](../../propertypilot-backend/src/main/java/com/propertypilot/application/service/impl/BillingServiceImpl.java).

### 3.6 Current customer journey gap

The customer request form loads owned properties and eligible service types/services, then asks for an accepted quote. When no accepted quote exists, the UI explains that quote calculation is handled by PropertyPilot Operations. The customer can review and accept an Operations-created quote, but cannot start an estimate from the customer portal.

The customer form is [CustomerRequestCreatePage.tsx](../../propertypilot-frontend/src/features/customer-portal/pages/CustomerRequestCreatePage.tsx). The quote creation UI is [QuotePages.tsx](../../propertypilot-frontend/src/features/quotes/pages/QuotePages.tsx). The quote API client is [quoteApi.ts](../../propertypilot-frontend/src/features/quotes/services/quoteApi.ts).

## 4. Pricing-related database structures

| Structure | Current purpose and observations |
|---|---|
| `services` | Service identity, category, active flag, and fallback `base_price`. |
| `service_price_rules` | Service-specific base amount, currency, JSON rule definition, effective dates, and status. The current calculation selects a base amount from this table but does not evaluate the stored JSON definition. |
| `pricing_profiles` | Standard and Customer calculation profiles. |
| `pricing_profile_parameters` | Profile values used by the calculation. Values are stored as strings and interpreted by the pricing code. |
| `pricing_parameter_history` | Old/new parameter values, reason, actor, and timestamp. Current parameter-update code records a null actor. |
| `pricing_estimates` | Persisted estimate/quote, customer-property-service context, monetary totals, snapshot, status, expiry, and acceptance/rejection fields. |
| `pricing_quote_line_items` | Persisted service, fee, discount, and tax components. |
| `pricing_discount_applications` | Discount applications tied to an estimate. |
| `coupons` and `coupon_redemptions` | Coupon definitions, validity/limits, and redemption reservations/ledger. |
| `cluster_rules` | Service-type/code cluster discount conditions including radius and minimum request count. |
| `pricing_rule_approvals` | Schema exists for approvals, but no complete pricing-rule approval workflow/API was found. |
| `service_requests.pricing_estimate_id` | Accepted estimate linked to the request; unique and protected from amount/reference mutation. |

The main pricing/promotion schema is in [V014__create_pricing_and_promotions.sql](../../propertypilot-backend/src/main/resources/db/migration/V014__create_pricing_and_promotions.sql). Profile schema and parameter history are in [V025](../../propertypilot-backend/src/main/resources/db/migration/V025__create_pricing_profiles.sql), [V026](../../propertypilot-backend/src/main/resources/db/migration/V026__create_pricing_profile_parameters.sql), and [V027](../../propertypilot-backend/src/main/resources/db/migration/V027__create_pricing_parameter_history.sql). Quote lifecycle and request linkage are in [V031](../../propertypilot-backend/src/main/resources/db/migration/V031__formal_quote_lifecycle.sql) and [V032](../../propertypilot-backend/src/main/resources/db/migration/V032__link_service_requests_to_quotes.sql).

## 5. Admin configuration: what exists and what is missing

### Existing backend configuration

- Admin APIs can create/update and list service-specific pricing rules.
- Admin APIs can read/update profile parameters and activate/deactivate profiles.
- Admin APIs can create/update coupons and cluster rules.
- The Admin service catalogue can edit a service’s fallback base price and active status.

### Current UI limits

- The Pricing Rules directory is a generic list/detail view. It does not provide a pricing rule create/edit workflow.
- The frontend has no pricing profile parameter management UI.
- The frontend has no coupon or cluster-rule configuration UI.
- The service catalogue exposes base price, but not a complete property/location-scoped price-rule editor or pricing simulator.

Relevant files include [PricingRuleController.java](../../propertypilot-backend/src/main/java/com/propertypilot/web/controller/PricingRuleController.java), [PricingConfigurationController.java](../../propertypilot-backend/src/main/java/com/propertypilot/web/controller/PricingConfigurationController.java), [adminPortalApi.ts](../../propertypilot-frontend/src/features/admin-portal/adminPortalApi.ts), and [AdminDirectoryPage.tsx](../../propertypilot-frontend/src/features/admin-portal/pages/AdminDirectoryPage.tsx).

## 6. Confirmed gaps and risks

1. **Customer estimate access is missing.** The estimate API permits Admin and Operations, not Customer. This is the primary break in the intended customer flow.
2. **Property-aware rule selection is incomplete.** The estimator validates ownership and service active/billable status, but does not independently run service eligibility or coverage validation. Request creation later rechecks eligibility. `service_price_rules` is service-scoped; property type/location conditions are not first-class columns and stored `rule_definition` is not evaluated in the current price calculation.
3. **Distance is not computed by the pricing path.** The calculator uses a supplied one-way distance. The Pricing Engine documentation describes Maps-based route distance, but this implementation does not call a Maps API. Customer pricing must derive or validate distance and coverage on the server.
4. **Several documented components are absent from this formula.** The current calculator handles base/service amount, travel, agent allowance, overhead, margin, GST, subscription/cluster/ETA/coupon discounts. I did not find live calculation support for food allowance slabs, a separate platform fee, arbitrary add-ons, demand surge, or vendor quotations in this customer price path.
5. **Customer quote itemization is incomplete.** Saved components are collapsed into a service subtotal in the customer quote response, even though Standard price and savings are shown.
6. **Rule lifecycle mismatch.** The service allows status `INACTIVE`, while the `V014` database check constraint allows `DRAFT`, `ACTIVE`, and `RETIRED`. Attempting to save `INACTIVE` can fail at the database.
7. **Profile edits require reactivation.** Updating a profile parameter deactivates an active profile. The user must reactivate it before estimates can be calculated. The audit record currently supplies a null administrator ID.
8. **Rule audit/version snapshot needs improvement.** Price rules can be updated in place. Estimate snapshots retain the selected rule ID and calculated outputs, but do not appear to retain every pricing profile/rule version needed for full historical reproducibility.
9. **Overlapping structures need reconciliation.** V024 creates a separate `pricing_rules` table, whereas current pricing-rule Java code maps to `service_price_rules`. `PricingRuleEntity` and `ServicePriceRuleEntity` both map to `service_price_rules`. `quotations`/`QuotationEntity` are a distinct vendor quotation model, not the current customer quote model.
10. **Seed catalogue is not yet price-ready.** V055 intentionally adds new services inactive with base price zero. They should not be activated until a positive approved price and operational configuration are available.

The product intent is described in [Pricing_Engine.md](../Pricing_Engine.md), [Pricing_Strategy.md](../Pricing_Strategy.md), and [Service_Catalog.md](../02_Product/Service_Catalog.md). Those docs describe intended capabilities; they are not evidence that every capability is implemented.

## 7. Recommended end-to-end implementation plan

### Phase 1: Reconcile and canonicalize the current model

- Treat `pricing_estimates` as the customer estimate/quote record.
- Keep vendor quotations separate from customer pricing estimates.
- Audit `pricing_rules`, `service_price_rules`, duplicate entity mappings, repositories, and all migration history before schema changes.
- Preserve historical IDs and snapshots. Do not delete or rewrite pricing data as a cleanup shortcut.

### Phase 2: Define the data-driven pricing policy model

Provide administrator-configurable, effective-dated rules for:

- Service/service type and eligible property types.
- Coverage zone, cluster, locality, or other supported location scope.
- Base service charge, travel mode/rate, allowances, supported add-ons, platform/operations fees, margin, tax, and applicable discounts.
- Currency, priority, active status, and date range.

Use a constrained, typed rule model with explicit fields and precedence rather than arbitrary executable formulas in the database. Validate conflicting overlaps and define fallback behavior. One reusable service record should work across all eligible property types; different prices should be selected by rules, not duplicate service records.

### Phase 3: Complete the Admin pricing product

- Add list/create/edit/deactivate flows for price rules and pricing profiles.
- Add supported screens for coupons and cluster rules.
- Show impact scope, active dates, currency, and rule priority clearly.
- Add a preview/simulation with representative property, service, and location inputs before activation.
- Record the authenticated administrator, change reason, old/new values, and approval decision where required.
- Keep services inactive until positive approved pricing and required coverage/SLA/qualification setup are complete.

### Phase 4: Add the customer estimate API and user flow

- Customer chooses an owned property, then an eligible service type and service.
- Backend derives the customer from authentication and reads persisted property type/location.
- Backend validates service activity, property eligibility, coverage, price rule availability, and all required properties/qualifications.
- Backend computes or validates travel distance/ETA using trusted server inputs. Do not accept customer-supplied price, customer identity, property type, or unverified distance.
- Return a persisted estimate containing Standard price, PropertyPilot price, savings, line-by-line components, assumptions, expiry, and estimate ID.
- Recalculate when relevant selections/inputs change. Preserve each estimate as a historical snapshot.

### Phase 5: Connect acceptance to service booking

- Keep quote acceptance tied to the authenticated customer and the immutable estimate.
- Provide an explicit “Accept and request service” action that atomically accepts and creates the request if acceptance means booking. Otherwise show a clear subsequent booking action.
- Enforce expiry, ownership, property/service match, current eligibility, coupon limits, and idempotency on the backend.
- Ensure retries cannot create multiple requests from the same accepted estimate.

### Phase 6: Preserve fulfillment and billing

- Continue to use the existing assignment, optional visit, evidence, completion, invoice, and payment workflows.
- Invoice from the accepted estimate/request amount; do not recalculate a previously accepted price when current rules change.
- Keep invoice creation distinct from verified payment settlement.

### Phase 7: Verify in a safe rollout sequence

Add focused tests for rule precedence, effective dates, property-type/location eligibility, inactive/unavailable rules, client tampering, distance derivation, discounts and caps, estimate expiry, coupon concurrency, quote acceptance, idempotent request creation, and immutable request pricing. Add frontend tests for estimate/review/accept/book, empty catalogue, loading/errors, and clearing stale estimates when property/service changes.

Validate migrations and seed/configuration changes against a disposable development database first. Compare existing service/rule rows for duplicates and conflicts. Load only approved rates, activate services in controlled groups, and verify a complete customer estimate-to-invoice journey before wider activation. Do not run migrations against an unverified database.

## 8. Desired target journey

`Customer selects property → backend derives property type/location → eligible service type → eligible service → server calculates Standard and PropertyPilot prices → customer reviews itemized estimate and savings → customer accepts → service request is created once with immutable estimate link → assignment/visit/evidence workflow → invoice from accepted amount → payment settlement is verified separately.`

This design makes pricing dynamic from administrator-maintained configuration while keeping the pricing engine deterministic and secure. Admins can change rates, scopes, dates, discounts, and supported components through the product UI without editing service code; new calculation capabilities still require deliberate engineering and schema changes.

## 9. Audit verification note

Existing test files were inspected, including controller pricing tests, pricing-rule arithmetic tests, and subscription benefit coverage. No tests were executed for this read-only audit. No application code, migration, service data, or database state was modified.

## 10. Stage 0 implementation audit (2026-10-10)

This addendum verifies the audit against the current source and approved business documents before implementation. The current working tree contains extensive pre-existing changes, including customer property/catalogue/request work and migrations V053–V055. These are preserved. Database contents were not queried or modified during this implementation turn.

### Approved source documents and policy reconciliation

- `docs/Pricing_Strategy.md` says customer prices must come from effective-dated configured rules; estimates must be itemized and auditable; accepted quotes must not change when current rules change; and rule changes/simulation require governance.
- `docs/Pricing_Engine.md` describes base service, round-trip travel, food allowance, add-ons, platform fee, overhead, profit, discounts, and taxes. It gives example rates (₹4/km, 5% platform/overhead, 10% profit), and says route distance should come from Google Maps. It places platform fee before overhead and margin, and labels taxes future support.
- `docs/Business/PROPERTYPILOT_BUSINESS_BLUEPRINT_V1.md` states Base Cost + Travel + DA + Overhead + Margin + GST - Discounts, but does not fully define ordering or calculation bases.
- `docs/Pricing_vs_Payout_Principles.md` explicitly keeps customer pricing/revenue separate from agent payout/cost management.
- `docs/Business/PROPERTYPILOT_DOMAIN_MODEL_V1.md` places pricing and quotes in the Commercial domain and service pricing rules in the Service Catalog domain. The implementation map marks the connected commercial path as partial.

These documents do not agree on component support/order, tax treatment, or margin semantics. The user approved the following implementation guardrails on 2026-10-10: preserve current calculation behavior for now; keep sample rates as examples only, not active production defaults; and fail safely for travel-dependent customer estimates until a trusted server-side routing provider is available. No new business rates or tax policy are introduced by this implementation.

### Current source-verified architecture

1. `PricingController` exposes an Admin/Operations-only calculation endpoint. `CreatePricingEstimateRequest` accepts customer, property, service, one-way distance, optional ETA/coupon, and a legacy vendor-cost field. The engine overwrites vendor cost from the selected rule/service base, but still consumes the request distance.
2. `PricingEngineServiceImpl` checks property/customer match and active/billable service, selects the active effective rule with the latest `effectiveFrom`, falls back to `services.base_price`, and requires a positive price. It does not call the eligibility engine or route provider during quote calculation.
3. `PricingRuleEngineServiceImpl` calculates base + travel + fixed DA, overhead on base cost, margin on base plus overhead, then tax on subtotal. Round-trip mode doubles the submitted one-way distance. The customer discounts are applied afterward, sequentially as subscription, cluster, ETA, coupon, and Customer tax is recalculated after discounts. Standard and Customer profiles are calculated independently.
4. `QuoteServiceImpl.acceptQuote` locks and updates the estimate and reserves coupon capacity transactionally. `ServiceRequestServiceImpl.createRequest` separately locks the estimate, requires it to be accepted and unexpired, rechecks owner/property/service consistency, active service and eligibility, then creates the request for the accepted total. V032 adds a unique estimate link and an immutable amount/reference trigger. Thus duplicate request rows are prevented, but acceptance and booking are currently separate transactions/API calls.
5. Completion creates an invoice from the service request amount through the existing billing service. Vendor quotations and agent payouts remain separate workflows.

### Canonical model decision and migration boundary

- Continue with `pricing_estimates` as the customer estimate/quote lifecycle and `service_requests.pricing_estimate_id` as its booking relationship. Keep vendor `quotations` separate.
- The active pricing-rule repository/entity uses `service_price_rules`. Two Java entities map to that table. V024 separately creates `pricing_rules`; references and deployed data must be inventoried before any consolidation. Do not delete or rename either table in this work.
- Rule selection remains service-specific, latest effective start date among active applicable rows, with service base-price fallback, pending rule-status and conflict improvements. `rule_definition` is not evaluated by the current formula; this work will not claim generic JSON pricing support.
- Schema changes, if needed, will be additive, forward-only Flyway migrations. Existing migration files, service IDs, quote snapshots, and billing data are preserved. No migration is run against the user's persistent database. The current untracked V055 and earlier worktree modifications are not changed or replaced.

### Confirmed gaps, risks, and implementation files

- **Customer estimate:** currently no customer-authorized estimate API. Add an authenticated customer path that derives identity and property context from persistence. Retain the Operations/Admin workflow.
- **Eligibility/coverage:** apply the existing eligibility engine before pricing and make location/coverage requirements explicit; do not trust client property type or eligibility.
- **Travel:** no trusted Maps/router call is present. Do not use browser-supplied distance or silently assume zero travel. Travel-required quotes must fail with an actionable unavailable-route message and preserve Operations fallback until a provider is configured.
- **Configuration:** Admin APIs exist, but the UI lacks pricing-rule CRUD, profile configuration, coupon/cluster management, and simulation. Add UI only for fields whose calculation semantics are implemented.
- **Status/profile audit:** `PricingRuleServiceImpl` accepts `INACTIVE`; V014's `service_price_rules` check constraint lists `DRAFT`, `ACTIVE`, and `RETIRED`. Parameter edits record a null actor and deactivate the active profile. Resolve with tested lifecycle changes and an additive migration; never silently take live pricing offline.
- **Snapshots:** the pricing rule entity has optimistic versioning, but mutable rules/profile values are not fully copied into each estimate snapshot. Extend the persisted snapshot with the exact effective calculation configuration and calculation-policy version.
- **Request acceptance:** existing locks and unique constraint protect against duplicate requests, but API-level acceptance and booking are not atomic. Add one transactional acceptance-and-book operation; preserve existing quote and request authorization checks, coupon reservations, and price immutability.
- **Itemization:** line-item storage exists, but customer quote responses aggregate fee components. Make line items reconcile to Standard and PP totals without disclosing internal vendor costs or margin.

Expected implementation areas are the pricing request/response DTOs; pricing and quote controllers/services; pricing profile/rule services; eligibility/coverage integration; estimate, line-item, rule/profile entities and repositories; request creation orchestration; Admin pricing APIs/UI and client; customer estimate/quote UI/hooks/client; Flyway migrations; backend and frontend tests; this audit and the progress ledger. Exact file changes are gated on each stage's tests and compatibility review.

### Test and rollout gates

Reuse the existing `PricingControllerTest`, `PricingRuleControllerTest`, `PricingConfigurationControllerTest`, `PricingRuleEngineServiceImplTest`, `PricingEngineSubscriptionBenefitTest`, `QuoteServiceImplTest`, `CustomerRequestCreatePage.test.tsx`, and `QuotePages.test.tsx`. Add tests for customer ownership/eligibility, distance tampering and provider failure, missing configuration, component and line-item reconciliation, snapshot immutability, atomic/idempotent quote booking, coupon concurrency, and invoice amount preservation. No tests have yet been run for this implementation task.

Use a dedicated disposable database only if its isolation is explicitly available and verified. The development database snapshot described in the earlier ledger is historical evidence only; it is not a current-state verification. No active production prices will be seeded from the sample values in the pricing document.

### 2026-10-10 — Stage 1 implementation and verification

- Added a customer estimate endpoint that derives the authenticated customer, registered property, persisted service eligibility, and service from backend state. It ignores client-supplied customer identity and distance; the existing Admin/Operations calculation endpoint remains available.
- Customer estimates for services requiring an on-site visit fail safely because no trusted server-side route provider is configured. No zero-distance estimate is silently presented for travel-dependent work.
- Updated customer request creation to calculate and show Standard price, PropertyPilot price, and savings for the selected eligible service. Customer acceptance now invokes one transactional accept-and-create-request operation. Repeating an already-booked accepted estimate returns the existing request rather than creating a second one.
- Added Admin pricing-rule create/edit controls using existing APIs, exposing service, amount/currency, effective dates, and lifecycle status. Existing formula behavior is retained; arbitrary `rule_definition` JSON is not represented as executable pricing logic. Profile/discount parameter management and full eligibility/coverage authoring UI remain follow-up work.
- Added forward-only migration `V056__allow_inactive_pricing_rule_status.sql` to align the service price rule status constraint with the supported `INACTIVE` lifecycle. It has **not** been applied. No database migration or data write was performed in this implementation.
- Tests/builds: targeted backend pricing/quote tests passed (8 tests); full backend suite passed (**715 tests, 0 failures/errors/skips**, from 100 Surefire report suites); full frontend suite passed (**56 files, 170 tests**); frontend production build passed (1,283 modules); `git diff --check` passed with only Git line-ending notices.
- This verifies source-level tests and build only. No authenticated live browser/API journey, database migration, persisted pricing configuration, or routing-provider behavior was exercised. Existing sample rates remain examples and are not activated.

### 2026-10-10 — Customer estimate 400 diagnosis

- The customer estimate service explicitly returns HTTP 400 when neither an active effective service price rule nor the service base price is positive. That guard prevents a fabricated estimate; no fallback/sample price was added. The exact current database value for the selected service was not re-queried in this fix.
- The frontend previously rendered Axios's generic `Request failed with status code 400`, hiding the backend's actionable business message. Updated the estimate error display to extract the response message and explain the missing-price case in customer language.
- Added UI regression coverage for the 400 response. Focused request-page tests passed (**6 tests**) and frontend production build passed (1,283 modules). Database and authenticated live API were not queried or changed.

### 2026-10-10 — Optional ETA parameter defect

- The screenshot's exact 400 was caused by unconditional parsing of `ETA_DISCOUNT_PERCENT`. When the active Customer profile had the seeded/unconfigured text value, even an estimate with no ETA-discount request failed.
- Updated `PricingEngineServiceImpl` so optional ETA threshold/percentage settings are read only when an ETA discount was requested and the requested day count reaches the threshold. Normal estimates now proceed without depending on dormant ETA configuration; invalid requested ETA configuration remains a clear validation failure.
- Added regression coverage proving an estimate succeeds with unconfigured ETA values when ETA was not requested. Focused backend suite passed (**3 tests**).
- Local API (8080) and frontend (5173) were found running. Backend was not restarted: `application.yml` configures Flyway migration locations, and pending V056 would be applied at startup. The prior rollout constraint prohibits running an unapproved migration. Source fix is ready, but the currently running backend requires a restart before the browser receives it. No database changes were made.
