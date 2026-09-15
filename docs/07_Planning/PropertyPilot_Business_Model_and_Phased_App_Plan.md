# PropertyPilot Business Model and Phased App Development Plan

Document type: Business and product delivery blueprint  
Version: 1.0  
Date: 2026-09-05  
Status: Consolidated working baseline

## 1. Executive Summary

PropertyPilot is a managed property operations platform for people who own, buy, sell, rent, build, or maintain property from a distance. Its strongest initial customer need is trust: the customer wants reliable local execution, evidence, and visibility without having to be physically present.

The product promise is:

> Verify property, monitor property, protect property, and coordinate property services from one trusted platform.

PropertyPilot owns the customer experience and coordinates delivery through operations staff, field agents, specialist providers, vendors, and partners. It does not need to perform every service directly. The platform must make responsibility, evidence, price, SLA, payment, and service status visible at every step.

The recommended build sequence is:

1. Make one-time verification and monitoring services work end to end.
2. Add explainable pricing, bundles, payments, and recurring subscriptions.
3. Mature field operations, support, and agent economics.
4. Add vendors, NRI premium services, marketplaces, rental, construction, agriculture, and AI in controlled releases.

The repository contains a strong requirements set but the backend and web frontend are placeholders and the mobile app is only a starter shell. This plan is therefore an execution baseline, not a claim that these capabilities already exist.

## 2. Source Authority and Important Conflicts

The numbered `docs/` hierarchy is the target documentation structure. The following sources are the working authorities for this plan:

| Topic | Authority |
|---|---|
| Requirements | `docs/PropertyPilot_SRS.md` |
| Product priority | `docs/01_Business/Feature_Catalog.md` |
| Business vision | `docs/01_Business/Product_Vision.md` and `docs/Founder_Vision_and_Business_Principles.md` |
| Customer journeys | `docs/01_Business/Customer_Journeys.md` |
| Service definitions | `docs/02_Product/Service_Catalog.md` and `docs/Master_Service_Catalog.md` |
| Pricing calculation | `docs/Pricing_Engine.md` |
| Pricing governance | `docs/Pricing_Strategy.md` |
| Subscription entitlements | `docs/01_Business/Subscription_Plans.md` |
| Data model | `docs/04_Data/Canonical_Data_Model.md` and `docs/04_Data/Database_Physical_Model.md` |
| API behavior | `docs/05_APIs/OpenAPI_Specification.yaml` |
| Release boundary | `docs/MVP_Scope_Baseline.md` |
| Delivery roadmap | `docs/Implementation_Master_Plan.md` |

There is a material scope conflict:

- `MVP_Scope_Baseline.md` includes subscriptions, billing, refunds, complaints, and privacy operations.
- `MVP_Release_Plan.md` excludes subscriptions and several commercial capabilities from MVP.
- `Feature_Catalog.md` classifies subscriptions as P1, while one-time payments are P0.
- `Implementation_Master_Plan.md` places subscriptions and payments after the core operational MVP.

This plan recommends treating the first production release as the **Operational MVP** and subscriptions as the first monetization release. Product leadership should formally approve this decision before implementation starts. Until then, the approved scope baseline remains a requirements source, not an implementation order.

## 3. What the Business Is

### 3.1 Business category

PropertyPilot combines five businesses in one operating model:

1. **Property verification:** Check ownership, location, documents, approvals, boundaries, access, and risk before a customer acts.
2. **Property monitoring:** Visit and observe a property periodically, capture evidence, and report changes or issues.
3. **Property care and coordination:** Arrange maintenance, security, cleaning, agriculture, construction, and other local work.
4. **Premium remote ownership:** Give NRI and outstation owners reliable visibility, priority handling, and escalation support.
5. **Controlled marketplace:** Connect customers with verified vendors, buyers, sellers, tenants, and partners while protecting the customer relationship and commercial flow.

### 3.2 Customers

Primary segments are NRI property owners, outstation owners, plot and land owners, agriculture and farm owners, apartment and villa owners, guest-house owners, rental owners, commercial property owners, investors, and property buyers.

Future segments include builders, developers, banks, real-estate companies, and professional property-management firms.

### 3.3 Value delivered

Customers pay for confidence and execution, not merely software. The product must provide:

- verified local presence;
- GPS, timestamped photos, videos, observations, and documents;
- clear service status and accountable ownership;
- transparent, itemized quotes;
- reports that support decisions;
- alerts when a problem requires attention;
- coordination of trusted follow-up work.

## 4. Actors and Operating Model

| Actor | Responsibility |
|---|---|
| Customer or owner | Registers, adds property, requests services, pays, reviews evidence, and resolves issues |
| Buyer or seller | Uses verification and protected transaction workflows |
| Tenant | Uses future rental and inspection workflows |
| Field agent | Performs visits, captures evidence, and updates task status |
| Specialist | Performs legal, survey, drone, agriculture, or other skilled work |
| Vendor | Quotes and fulfils coordinated work |
| Operations user | Validates requests, quotes, assigns work, reviews evidence, and handles exceptions |
| Cluster manager | Owns regional coverage, capacity, and SLA performance |
| Admin | Manages users, catalog, pricing, permissions, configuration, and audit |
| Partner | Provides future legal, finance, insurance, brokerage, or referral capabilities |

The operating geography is intended to support State -> District -> Mandal -> Cluster -> Agent. Location drives coverage, assignment, travel pricing, ETA, workload balancing, and regional expansion.

## 5. Complete Core Business Flow

### 5.1 Customer lifecycle

`Discover -> estimate cost -> register -> verify identity -> add property -> add documents -> select service or plan -> receive quote -> pay -> track execution -> receive evidence and report -> rate or raise issue -> repeat or subscribe`

### 5.2 One-time field service flow

1. Customer selects a property and service.
2. The platform validates property type, service eligibility, coverage, required documents, and available execution model.
3. The Pricing Engine calculates an informational estimate or checkout-eligible quote.
4. Customer accepts the quote and pays.
5. Operations assigns an eligible agent using location, skill, availability, workload, and SLA.
6. Agent accepts the task, travels to the property, and checks in with GPS and timestamp.
7. Agent captures required photos, videos, documents, observations, and coordinates.
8. Agent checks out and submits the visit.
9. Operations or a reviewer validates evidence and requests correction when necessary.
10. The report engine produces the service report.
11. Customer receives the report and notifications.
12. The request is closed, rated, audited, and retained. Issues can create follow-up work.

### 5.3 Recurring monitoring flow

`Plan active -> visit entitlement created -> visit scheduled -> agent assigned -> evidence captured -> report delivered -> issue alert or closure -> next scheduled entitlement`

Unused visits expire according to the plan rules. Non-included work is quoted independently.

### 5.4 Vendor or project flow

`Need identified -> scope defined -> eligible vendors selected -> quotations received -> customer approval -> vendor assigned -> milestone evidence -> completion review -> payment and commission reconciliation`

### 5.5 Marketplace flow

`Property or requirement registered -> verification -> listing or protected request -> lead qualification -> visit or matching -> negotiation coordination -> transaction support -> commission or referral settlement`

Marketplace work must not be built before identity, lead protection, consent, audit, payments, dispute handling, and communication controls are defined.

## 6. Services Covered

### 6.1 Launch service family

These services are the best first commercial wedge because they are evidence-based, operationally bounded, and useful to owners and buyers:

- GPS and location verification
- ownership verification
- physical property inspection
- boundary verification
- road-access verification
- encroachment inspection
- plot or vacant-property monitoring
- property monitoring visit

### 6.2 Expansion service families

| Family | Examples | Recommended release |
|---|---|---|
| Verification | EC, registration, survey number, HMDA, DTCP, RERA, tax, legal, title, court case | Release 2-3 |
| Plot and land care | Cleaning, bush removal, fencing, gates, walls, CCTV, borewell, power, water | Release 3-4 |
| Residential | House, structural, leakage, plumbing, electrical, cleaning, pest control, renovation | Release 3-4 |
| Apartment | Builder, handover, snag, dues, tenant, and rental inspection | Release 3-4 |
| Agriculture | Passbook, Adangal, 1B, FMB, crop, soil, water, irrigation, farm road | Release 4 |
| Commercial | Trade license, fire safety, occupancy, facility, shop, office, and warehouse inspection | Release 4 |
| Rental | Tenant search and verification, agreement assistance, rent follow-up, move-in/out, property management | Release 5 |
| Construction | Civil, electrical, plumbing, interior, milestone monitoring, and vendor coordination | Release 5 |
| Security | Security visits, caretaker coordination, encroachment and illegal-activity monitoring, CCTV | Release 3-4 |
| NRI premium | Video walkthrough, monthly reporting, legal/document coordination, emergency checks, property care | Release 4 |
| Premium media | Drone, 360-degree tour, professional photography, same-day, weekend, and emergency service | Release 4-5 |
| AI and automation | Summaries, change detection, risk analysis, recommendations, crop analysis | Release 6, with human review |

The full catalog is intentionally broader than the first release. Each service must have an owner, property eligibility, coverage rule, skill/certification requirement, SLA, evidence checklist, pricing rule, deliverables, and audit history before activation.

## 7. Pricing, Packages, and Revenue

### 7.1 Pricing model

The Pricing Engine should calculate a quote using:

`service charge + travel and distance + food allowance + add-ons + platform fee + admin overhead + profit margin - discounts`

Inputs may include property type, size, location, distance, travel time, cluster, service frequency, agent/vendor cost, schedule, coverage, subscription entitlement, coupon, and market condition.

Pricing must be:

- itemized before payment;
- effective-dated and configurable through admin;
- validated against service eligibility;
- immutable for an accepted quote;
- idempotent at checkout;
- auditable for overrides, discounts, and approvals;
- separate from agent payout calculation.

The documented examples, such as ₹4/km travel, 5% platform fee, 5% overhead, and 10% margin, are configuration examples and must not be treated as approved commercial prices without a pricing decision.

### 7.2 One-time services and add-ons

The first revenue stream is one-time verification, inspection, monitoring, and coordination work. Documented add-on examples include:

| Add-on | Published example |
|---|---:|
| Emergency visit | ₹999-₹2,999 |
| Drone survey | ₹4,999+ |
| Boundary verification | ₹2,999+ |
| Legal verification | ₹4,999+ |

These values require approval and effective-dated configuration.

### 7.3 Subscription plans

Subscription documents define entitlements; the Pricing Engine determines the actual price for the property and context.

| Plan | Published example | Core entitlement |
|---|---:|---|
| Silver | ₹499/month or ₹4,999/year | One monitoring visit per month, GPS, photos, basic report |
| Gold | ₹999/month or ₹9,999/year | Two visits per month, GPS, photos, video, alerts, priority support |
| Platinum | ₹1,999/month or ₹19,999/year | Weekly monitoring, video, issue escalation, priority handling |
| NRI Elite | ₹24,999/year | Monthly monitoring, video walkthrough, GPS evidence, reports, priority support |
| NRI Concierge | ₹49,999/year | Bi-weekly monitoring, emergency visits, vendor coordination, property care, relationship manager |

Commercial rules include payment-confirmed activation, optional auto-renewal, manual renewal, upgrade with possible proration, downgrade at renewal, pause once per year for up to 60 days, usage-based refund eligibility, and no carry-forward of unused visits unless a future approved policy says otherwise.

### 7.4 Bundles

Documented bundle examples are:

| Bundle | Published example | Included value |
|---|---:|---|
| Property Purchase | ₹7,999 | Ownership, GPS, tax, and basic legal review |
| Property Due Diligence | ₹14,999 | Ownership, GPS, boundary, HMDA, DTCP, and legal verification |
| Legal Protection | ₹9,999 | Ownership, legal, court-case review, and risk assessment |
| Basic Protection | ₹5,999/year | Silver monitoring, quarterly GPS, and status updates |
| Property Guardian | ₹11,999/year | Gold monitoring, alerts, video evidence, and priority support |
| NRI Protection | ₹29,999/year | Ownership/GPS verification, NRI Elite, video, and emergency coverage |
| NRI Concierge | ₹59,999/year | Concierge plan, relationship manager, property care, vendors, and escalation |
| Agriculture Land | ₹9,999/year | Boundary/GPS verification, quarterly monitoring, and photos |
| Construction Monitoring | ₹24,999/project | Monthly visits, progress reports, photos, and videos |
| Construction Assurance | ₹49,999/project | Monitoring, milestones, issue escalation, and vendor coordination |
| Property Care | ₹14,999/year | Monitoring, maintenance coordination, vendors, and issue tracking |

Bundle prices are illustrative until approved in the Pricing Engine. Bundles must retain transparent component line items, eligibility rules, discount limits, and entitlement consumption.

### 7.5 Revenue streams

The intended hybrid model contains:

- one-time verification and inspection fees;
- recurring monitoring subscriptions;
- premium NRI services;
- property management and coordination fees;
- construction, maintenance, agriculture, and security margins;
- vendor listing, subscription, promotion, commission, and payout flows;
- buyer, seller, and rental lead revenue;
- marketplace success commissions;
- future featured listings, advertising, insurance, and loan referrals.

The documented target mix is 30% verification, 40% subscriptions, 15% NRI, 10% marketplace, and 5% property management. These are planning assumptions, not validated forecasts.

## 8. App and Platform Surfaces

### Customer mobile app

Onboarding, OTP login, profile, properties, documents, service catalog, cost estimate, quote, booking, payment, tracking, reports, notifications, complaints, subscriptions, and privacy requests.

### Agent mobile app

Login, assigned work, task details, navigation, GPS check-in/out, checklist, photos, videos, observations, evidence upload, status updates, and visit completion.

### Admin and operations web portal

Customer and property management, service catalog, eligibility, pricing rules, service queue, assignment board, evidence review, report review, payments, complaints, SLA, agents, vendors, configuration, audit, and analytics.

### Public web entry point

Landing and service discovery, guest cost calculator, package and plan comparison, lead capture, and registration handoff.

### Future portals

Vendor, partner, buyer/seller, and customer web portals may be added when the corresponding workflows are operationally governed.

## 9. Phase-Based Development Plan

### Phase 0: Decisions and canonicalization

Indicative duration: 1-2 weeks.  
Dependency: none.

Deliver:

- formal decision on Operational MVP versus subscription-in-MVP;
- one canonical service-request, visit, payment, subscription, and complaint state model;
- reconciled OpenAPI, data dictionary, physical model, migrations, and screen inventory;
- approved launch service list and launch geography;
- approved price policy and which published example prices are real;
- OTP provider, payment gateway, maps, messaging, object storage, and deployment decisions;
- ADRs for ownership, assignment, pricing, payout, and admin roles.

Exit gate: signed scope, contract, schema, commercial, and integration decisions.

### Phase 1: Technical foundation

Indicative duration: 3-4 weeks.  
Dependency: Phase 0.

Deliver:

- PostgreSQL schema and corrected Flyway history;
- Spring Boot 3 / Java 21 backend in `propertypilot-backend/`;
- API versioning, validation, error model, audit logging, health checks, and RBAC;
- OTP authentication and secure session/token handling;
- object storage and media access controls;
- React web shell in `propertypilot-frontend/`;
- Flutter customer/agent shell in `mobile_app/`;
- local development environment, CI checks, migrations test, and dev deployment;
- logs, metrics, traces, secrets, and environment configuration.

Exit gate: clean database migration, repeatable build, working authentication, API lint, and deployed development environment.

### Phase 2: Operational MVP vertical slice

Indicative duration: 8-10 weeks.  
Dependency: Phase 1.

Deliver:

- customer profile and KYC state;
- property registration, ownership relationship, photos, and documents;
- launch service catalog and eligibility checks;
- quote request for one-time services;
- service request lifecycle and cancellation/rescheduling rules;
- operations queue and agent assignment by cluster, skill, availability, and workload;
- agent visit scheduling, GPS check-in/out, evidence capture, and offline retry behavior;
- evidence review and report generation;
- customer tracking, notifications, and report download;
- basic admin dashboards, complaints, and audit history;
- one-time payment, invoice, receipt, and reconciliation flow if approved for MVP.

The first production proof should be: a customer registers, adds a property, orders GPS or ownership verification, pays, receives an assigned visit, the agent captures evidence, operations reviews it, and the customer receives a report.

Exit gate: this complete workflow passes integration, UAT, security, accessibility, and operational-readiness checks.

### Phase 3: Pricing, bundles, and recurring revenue

Indicative duration: 6-8 weeks.  
Dependency: Phase 2 and approved commercial rules.

Deliver:

- configurable Pricing Engine with quote snapshots and expiry;
- guest service-cost calculator;
- package comparison and savings display;
- bundles, add-ons, coupons, discounts, and approval controls;
- payment gateway callbacks with idempotency;
- invoices, refunds, reconciliation, and revenue reporting;
- Silver, Gold, Platinum, NRI Elite, and NRI Concierge entitlements;
- subscription activation, renewal, grace, pause, resume, upgrade, downgrade, cancellation, and usage consumption;
- independent agent payout calculation and finance export.

Exit gate: every customer charge is explainable, reproducible, auditable, and reconciled; subscription state transitions are tested.

### Phase 4: Operational maturity and NRI premium

Indicative duration: 6-8 weeks.  
Dependency: Phases 2 and 3.

Deliver:

- SLA timers, ETA rules, escalation, and exception queues;
- agent workload, attendance, performance, payout, and quality metrics;
- richer notifications across SMS, email, WhatsApp, push, and in-app channels;
- recurring monitoring scheduler and issue alerts;
- secure video walkthrough reports;
- NRI dashboard, emergency visit workflow, property-care coordination, and priority support;
- privacy requests, consent, retention, backup/restore, incident drills, and support runbooks;
- controlled beta operations and release evidence.

Exit gate: the team can operate a paid customer base with measurable SLA, support ownership, alerts, escalation, and recovery procedures.

### Phase 5: Vendor ecosystem and coordinated services

Indicative duration: 8-10 weeks.  
Dependency: stable pricing, payments, operations, and dispute controls.

Deliver:

- vendor onboarding, KYC, verification, coverage, skills, and catalog;
- quotation requests, comparison, approval, rejection, and expiry;
- vendor assignment, milestone updates, evidence, invoices, commissions, and payouts;
- protected customer-vendor communication and contact masking;
- launch categories for maintenance, security, cleaning, agriculture, or construction based on capacity;
- vendor quality, rating, dispute, and suspension workflows.

Exit gate: one vendor-delivered service can be quoted, approved, fulfilled, paid, reviewed, and reconciled end to end.

### Phase 6: Property transactions, rental, and construction

Indicative duration: 10-14 weeks per selected domain; do not build all domains in parallel without capacity.

Deliver the selected domain as a separate release:

- buy/sell marketplace with verification, protected leads, visits, negotiation coordination, and transaction support;
- rental listing, tenant verification, agreements, move-in/out inspection, rent follow-up, and maintenance;
- construction project, scope, vendor quotation, milestones, progress evidence, issue escalation, and completion;
- agriculture land workflows including crop, soil, water, and farm work coordination.

Exit gate: the selected domain has clear legal/compliance ownership, dispute handling, commissions, consent, audit, and support processes.

### Phase 7: Intelligence and ecosystem scale

Indicative duration: continuous after production data quality is proven.

Deliver:

- AI-generated service summaries and recommendations with human review;
- risk assessment, change detection, construction progress analysis, and crop monitoring;
- drone and sensor integrations;
- advanced analytics, forecasting, capacity planning, and customer segmentation;
- partner APIs for legal, lending, insurance, and referrals;
- multilingual support and additional geographic expansion.

Exit gate: each automation has measured accuracy, human override, explainability, privacy, retention, and rollback controls.

## 10. Delivery Workstreams

Every phase should run through these workstreams:

| Workstream | First responsibility |
|---|---|
| Product | Scope, journeys, acceptance criteria, pricing decisions, and release gates |
| Design | Mobile-first flows, evidence review, quote transparency, accessibility, and empty/error states |
| Backend | Domain services, APIs, state transitions, authorization, pricing, and audit |
| Web | Admin/operations workflows, catalog, queues, review, reporting, and configuration |
| Mobile | Customer and agent workflows, camera/GPS, offline retry, notifications, and permissions |
| Data | PostgreSQL, migrations, object storage, retention, reporting, and reconciliation |
| Operations | Coverage, staffing, assignment policy, review, support, SLA, and runbooks |
| QA/Security | Contract, unit, integration, UAT, security, performance, accessibility, and recovery testing |
| DevOps | CI/CD, environments, secrets, observability, backups, deployment, and rollback |

## 11. Release Gates and Success Measures

### Product and customer

- registration completion rate;
- property registration completion rate;
- quote-to-payment conversion;
- service request completion rate;
- report delivery within SLA;
- repeat purchase and subscription conversion;
- customer rating and complaint resolution time.

### Operations

- assignment time;
- visit completion rate;
- GPS/evidence completeness;
- first-pass report approval rate;
- SLA breach rate;
- agent utilization and travel cost;
- unresolved exception age.

### Commercial

- gross booking value;
- revenue by service, plan, bundle, and geography;
- gross margin after agent, travel, vendor, gateway, and platform costs;
- subscription activation, renewal, pause, cancellation, and churn;
- refund rate and payment reconciliation variance;
- customer acquisition cost and lifetime value when enough data exists.

### Engineering and trust

- API and app availability;
- failed payment and notification rates;
- evidence/report processing time;
- escaped defect rate;
- deployment frequency and rollback time;
- backup restore success;
- unauthorized access, privacy, and security incidents.

## 12. Immediate Next 30 Days

1. Approve the Operational MVP and subscription sequencing decision.
2. Select one launch geography and confirm service coverage.
3. Select the 3-4 launch services: GPS verification, ownership verification, physical inspection, and monitoring visit.
4. Approve actual launch prices or explicitly mark all prices as configurable placeholders.
5. Reconcile the canonical database schema and rewrite conflicting Flyway drafts as PostgreSQL migrations.
6. Generate a clean backend and implement OTP authentication plus the property/service-request vertical slice.
7. Define the launch evidence checklist and report templates before building the agent workflow.
8. Confirm field-agent staffing, travel reimbursement, review ownership, and customer support hours.
9. Freeze the OpenAPI contract for the first vertical slice.
10. Test the complete journey with internal users before adding more service categories.

## 13. Decisions Still Required

- Is subscription billing in the first production release or the next release?
- Which state, district, mandals, clusters, and services are covered at launch?
- Which published plan, bundle, add-on, and travel prices are approved?
- Who can approve discounts, manual quotes, refunds, and vendor quotations?
- Are field agents employees, contractors, vendors, or a mix, and what is the payout policy?
- Which legal, tax, KYC, privacy, and marketplace obligations apply to each service family?
- What evidence is mandatory for each launch service, and who reviews it?
- What is the customer support and escalation SLA?
- Which payment, OTP, messaging, maps, storage, and report providers will be used?
- Is the first client experience Flutter-only, or will a React customer web flow also launch?

## 14. Related Sources

- `docs/01_Business/Product_Vision.md`
- `docs/01_Business/Customer_Journeys.md`
- `docs/01_Business/Feature_Catalog.md`
- `docs/01_Business/Subscription_Plans.md`
- `docs/01_Business/Service_Bundles.md`
- `docs/01_Business/Revenue_Model.md`
- `docs/02_Product/Service_Catalog.md`
- `docs/Master_Service_Catalog.md`
- `docs/Pricing_Engine.md`
- `docs/Pricing_Strategy.md`
- `docs/MVP_Scope_Baseline.md`
- `docs/07_Planning/MVP_Release_Plan.md`
- `docs/Implementation_Master_Plan.md`
- `PROJECT_STATUS.md`