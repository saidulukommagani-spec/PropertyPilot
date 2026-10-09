# PROPERTYPILOT_LEAD_WORKFLOW_V1

## 1. Executive Summary

PropertyPilot should use Leads to manage **customer intent and opportunity context** before, during, and after service delivery. A Lead is not a Deal and should not be forced into one.

A Lead may produce service revenue, deal revenue, both, or neither:

```text
Lead
 ├── Service Request(s) → Service revenue
 ├── Deal → Transaction / commission revenue
 ├── Service Request(s) → Deal
 └── Closed or Lost → No further revenue
```

The workflow should preserve three distinct records:

- **Lead:** Why the person or organization is engaging with PropertyPilot, and what opportunity is being pursued.
- **Service Request:** A specific service to be delivered for a property.
- **Deal:** A property transaction opportunity that has been qualified for buying, selling, or another supported transaction.

The customer relationship remains with PropertyPilot throughout. Agents and operations staff coordinate fulfillment; they do not own the customer relationship.

## 2. Lead Purpose

A **Lead** is a time-bound record of a potential customer need or commercial opportunity. It captures intent, qualification, follow-up, assessment, and links to service requests or a deal when those are created.

A Lead exists to:

- Capture and qualify an opportunity before it is ready to become a service request or deal.
- Coordinate follow-up across customer service, operations, and sales responsibilities.
- Associate multiple service requests with one broader customer objective.
- Measure which activities and channels generate service and transaction revenue.
- Retain a traceable history when the customer does not proceed.

| Record | Represents | Example |
|---|---|---|
| Customer | The person or organization and its relationship with PropertyPilot | “Asha Rao, property owner” |
| Property | A property and its verified or prospective details | “Residential property in Bengaluru” |
| Lead | A customer intention or opportunity | “Considering selling this property; needs document preparation” |
| Service Request | One deliverable service with scope, price, SLA, and fulfillment | “Obtain an Encumbrance Certificate” |
| Deal | A qualified transaction opportunity | “Sale of the Bengaluru property” |

One customer may have several Leads. A Lead may be associated with an existing Property or begin with prospective property details that still need confirmation.

## 3. Lead Categories

Keep **intent**, **source**, and **customer/property context** separate. This avoids mixing values such as “SELLER” and “REFERRAL,” which describe different things.

### Recommended intent categories

| Category | Use |
|---|---|
| `BUY_PROPERTY` | Customer is exploring a purchase |
| `SELL_PROPERTY` | Customer is exploring a sale |
| `SERVICE_ONLY` | Customer needs property services without a transaction objective |
| `INVESTMENT` | Customer is assessing investment or portfolio opportunities |
| `RENTAL_OR_TENANCY` | Customer is exploring rental or tenant-related help |
| `PROPERTY_CARE` | Customer needs ongoing maintenance, monitoring, or property management |
| `OTHER_PROPERTY_NEED` | A property need that does not fit the defined categories |

### Track separately

- **Source:** Existing customer, website enquiry, referral, inbound call, campaign, agent referral, admin-created, or other approved channel.
- **Party role:** Buyer, seller, investor, tenant, landlord, owner, or authorized representative. A person may hold more than one role.
- **Property context:** Existing property, prospective property, multiple properties, or not yet identified.
- **Priority and qualification:** Values used for routing and follow-up, not substitutes for intent.

`ADMIN_CREATED` should be a **source or creation channel**, not an intent category.

## 4. Lead Lifecycle

Recommended lifecycle:

```text
NEW
 ↓
CONTACTING
 ↓
CONNECTED
 ↓
ASSESSMENT
 ├── SERVICE_REQUIRED → SERVICE_IN_PROGRESS → REASSESSMENT
 ├── DEAL_READY → DEAL_CONVERTED
 ├── SERVICE_AND_DEAL
 ├── NURTURE
 ├── LOST
 └── CLOSED
```

| Stage | Meaning | Exit criteria |
|---|---|---|
| `NEW` | Lead is captured and awaiting triage | Owner, source, intent, and next action are set |
| `CONTACTING` | PropertyPilot is attempting to reach the prospect | Successful connection, nurture decision, or unreachable outcome |
| `CONNECTED` | Two-way contact has occurred and intent is confirmed | Assessment is scheduled or enough information exists to assess |
| `ASSESSMENT` | Need, property context, urgency, and eligibility are being understood | Required services and/or transaction readiness are documented |
| `SERVICE_REQUIRED` | One or more services are identified | Service requests are created, declined, or deferred |
| `SERVICE_IN_PROGRESS` | Linked service requests are being fulfilled | Requests complete, cancel, or pause |
| `REASSESSMENT` | Customer’s position is reviewed after services or new information | Continue service, qualify a Deal, nurture, close, or mark lost |
| `DEAL_READY` | Transaction intent and minimum qualification are confirmed | Deal is created or customer defers |
| `DEAL_CONVERTED` | A Deal has been created and linked | Lead remains available for related service follow-up or is closed according to policy |
| `NURTURE` | Customer is interested but not ready; follow-up is scheduled | Re-enter assessment or close/lost |
| `LOST` | Opportunity will not proceed, with a reason recorded | Terminal unless customer re-engages and policy allows reopening |
| `CLOSED` | No further action is required; outcome is recorded | Terminal |

**Important distinctions**

- `LOST` means the opportunity did not proceed, such as choosing another provider or abandoning the plan.
- `CLOSED` means no action remains, for example the need was met through services and there is no ongoing sales opportunity.
- A service-only Lead can close successfully without ever becoming a Deal.
- A Lead can move directly from assessment to deal-ready when the customer’s requirements are already satisfied.
- The lifecycle should allow separate service and deal tracks to progress in parallel when both apply.

## 5. Lead Actions Matrix

| Stage | Allowed actions |
|---|---|
| `NEW` | Validate details, identify source and intent, assign owner, make first contact, mark duplicate, close invalid record |
| `CONTACTING` | Log call/email/meeting, schedule follow-up, record outcome, retry within contact policy, move to nurture or lost |
| `CONNECTED` | Confirm customer and property context, capture needs and consent, schedule assessment, add activity |
| `ASSESSMENT` | Record requirements, attach or identify property, identify service catalog items, request supporting documents, assess transaction intent |
| `SERVICE_REQUIRED` | Create one or more Service Requests, request or present service quotes, set expectations, monitor acceptance |
| `SERVICE_IN_PROGRESS` | Review linked request status, coordinate blockers, add services when requested, update next action |
| `REASSESSMENT` | Review completed services and remaining needs, ask whether a transaction is still intended, return to service, deal-ready, nurture, closed, or lost |
| `DEAL_READY` | Verify buyer/seller intent and transaction context, confirm required parties/property, create linked Deal, retain Lead-to-Deal trace |
| `DEAL_CONVERTED` | View linked Deal, coordinate supporting services, record changes in intent, close or retain the Lead according to follow-up policy |
| `NURTURE` | Schedule dated follow-up, record reason and preferred contact method, return to assessment when due |
| Any nonterminal stage | Reassign owner with reason, record activity, update priority, merge duplicate under controlled policy |
| `LOST` / `CLOSED` | View history and outcome; reopening should be controlled and audited |

Actions should be enabled based on role and record state. Status changes should be validated against allowed business transitions rather than accepting arbitrary stage values.

## 6. Lead → Service Workflow

```text
Lead captured
  ↓
Contact and qualify need
  ↓
Confirm customer and property context
  ↓
Assess property need and urgency
  ↓
Identify service(s) from the service catalog
  ↓
Explain scope, price, and SLA
  ↓
Customer accepts a service
  ↓
Create linked Service Request(s)
  ↓
Assignment → Visit → Evidence → Validation → Completion
  ↓
Review remaining customer need
  ├── More services → Create additional Service Request(s)
  ├── Transaction intent remains → Assess for Deal
  ├── Future need → Nurture
  └── No further need → Close Lead
```

Operational rules:

1. A Lead may have zero, one, or many linked Service Requests.
2. Each Service Request represents a specific deliverable with its own price, SLA, status, assignment, visit, evidence, and completion record.
3. Do not make the Lead status a substitute for Service Request status. The Lead summarizes the opportunity; each request tracks fulfillment independently.
4. A Lead may still generate a Deal after its services complete, as in the seller-with-missing-EC scenario.
5. If the customer buys elsewhere or abandons a transaction after receiving services, close or mark the Deal opportunity lost while retaining revenue from completed services.
6. Reassess the Lead after significant events: assessment complete, quote accepted/rejected, service request completion, customer change of intent, or deal creation.

## 7. Lead → Deal Workflow

```text
Lead with transaction intent
  ↓
Confirm buyer/seller role and customer authority
  ↓
Confirm property or prospective property
  ↓
Record transaction objective and timing
  ↓
Identify prerequisites and unresolved services
  ↓
Determine readiness
  ├── Services needed → Create Service Request(s), reassess later
  ├── Customer not ready → Nurture
  └── Requirements sufficiently clear → Deal Ready
  ↓
Create Deal linked to Lead and relevant parties/property
  ↓
Track transaction stages in the Deal
```

### Conversion rules

- Create a Deal only when the customer expresses a genuine transaction objective and the minimum transaction context is known.
- A Deal is not required for a service-only Lead.
- A Lead can produce service revenue without a Deal.
- A Deal can be created directly when requirements are already satisfied; service prerequisites are not mandatory.
- Preserve the Lead ID on the Deal and show the linked Deal on the Lead.
- Conversion should be one controlled business action: create the Deal, link it to the Lead, record the conversion activity, and update the Lead’s conversion state together.
- Prevent duplicate active Deals for the same Lead and transaction objective unless an authorized user records why multiple opportunities are valid.
- Creating a Deal does not itself close linked Service Requests or create service revenue.

## 8. Revenue Opportunities

| Revenue stream | Example | Revenue recognition source |
|---|---|---|
| Documentation services | EC, certified copies, mutation, registration support | Accepted and fulfilled Service Request, invoiced under billing policy |
| Legal and verification services | Title/legal review, property verification, survey | Service Request fulfillment |
| Property care | Cleaning, maintenance, tenant management, security | One-time request or contracted service |
| Monitoring | Scheduled inspections, geo-tagging, reports | Monitoring/service contract or subscription entitlement policy |
| Construction and compliance | Inspection, construction monitoring, compliance support | Service Request or approved contract |
| Subscriptions | Annual property-care or monitoring plan | Confirmed plan purchase and subscription billing |
| Subscription consumption | Entitled visit or discounted service usage | Consumption recorded against subscription entitlement; do not double-count as full-price revenue |
| Deal revenue | Brokerage/advisory/success fee for a sale or purchase | Deal closure and applicable fee/commission policy |
| Combined service + deal | Seller needs EC and legal work before listing/sale | Service revenue from each request plus separately governed deal revenue |

**Revenue measurement principles**

- Separate **lead pipeline value**, **quoted value**, **invoiced value**, and **collected revenue**.
- Do not count a Lead’s estimated transaction value as revenue.
- Do not double-count a service included in a subscription and separately bill it without an explicit add-on or overage rule.
- Attribute revenue to the Lead source and campaign while preserving the underlying invoice and payment as financial records.
- Report service revenue and transaction revenue separately.

## 9. Screen Blueprint

| Screen | Purpose |
|---|---|
| Lead Dashboard | Show new leads, overdue follow-ups, stage distribution, source performance, service conversion, and deal conversion |
| Lead List | Search, filter, sort, assign, and prioritize leads; show intent, source, owner, stage, next action, and linked property |
| Lead Create | Capture intent, source, contact, property context, urgency, and consent; support an unverified/prospective property |
| Lead Detail / Workspace | Provide one operational record with customer/property context, stage, owner, next action, score, and linked records |
| Lead Activity Timeline | Show calls, emails, meetings, notes, assignment changes, stage changes, service creation, quote decisions, and conversion |
| Lead Assessment | Record goals, property needs, timing, budget where appropriate, and service/deal readiness |
| Lead Service Requests | List linked requests with their individual status, SLA, quote, assignment, visit, and completion |
| Lead Documents | Display documents relevant to assessment or transaction preparation, with access controls and document categories |
| Lead Deal Conversion | Verify conversion prerequisites, select/create the Deal context, confirm participants/property, and show the resulting linkage |
| Lead Reporting | Measure source quality, response times, stage aging, service revenue attribution, and deal conversion without conflating pipeline and revenue |

The Lead Detail should make the distinction between **opportunity status** and **linked request/deal status** clear to staff.

## 10. Role Responsibilities

| Stage / activity | Admin | Operations | Agent | Customer |
|---|---|---|---|---|
| Capture `NEW` Lead | Configure sources, create or correct records, govern duplicates | Triage and assign | Submit a referral or field-discovered opportunity if policy allows | Submit enquiry and provide contact/intent |
| Contact and qualify | Set policy and resolve escalations | Own timely contact, confirm intent, schedule assessment | Support field contact when assigned | Confirm needs, preferred contact, and consent |
| Assess property need | Define service eligibility and approval rules | Coordinate assessment and identify services | Collect on-site facts/evidence within assignment scope | Provide access, documents, and decisions |
| Create Service Requests | Govern pricing/service rules and exceptions | Ensure scope, price, SLA, and customer acceptance are clear | Perform assigned work; do not create unauthorized scope | Approve the service and schedule |
| Service in progress | Review exceptions and controls | Monitor SLA, blockers, quality, and reassessment | Execute visits and submit evidence | Provide access and feedback |
| Deal-ready / conversion | Approve exceptions and compliance controls | Verify readiness and coordinate Deal creation | Refer relevant opportunity; no unauthorized commercial commitment | Confirm transaction intent and parties |
| Nurture | Define retention and contact policy | Schedule and own follow-up | Provide field observations | Choose contact preference and timing |
| Lost / closed | Govern reason codes and reporting | Record outcome and close accurately | Provide context for field-sourced outcomes | May state that the need is resolved or will not proceed |

Customer access should expose only the customer’s own information, related service requests, and relevant Deal details. Agents should see only assigned work and permitted customer/property information.

## 11. KPI & Analytics

### Lead operations

- **First response time:** time from lead creation to first two-way contact.
- **Contact rate:** leads with successful contact ÷ leads requiring contact.
- **Assessment completion rate:** assessed leads ÷ connected leads.
- **Follow-up SLA compliance:** due follow-ups completed on time ÷ follow-ups due.
- **Stage aging:** median and percentile time spent in each stage.
- **Stale lead rate:** open leads with no future action or recent activity.

### Service conversion and revenue

- **Lead-to-service conversion:** leads that produce at least one accepted Service Request ÷ eligible leads.
- **Requests per service-converted lead:** useful for multi-need property opportunities.
- **Service quote acceptance:** accepted service quotes ÷ service quotes issued.
- **Lead-attributed service revenue:** invoiced/collected service revenue linked to Leads, reported separately.
- **Subscription attachment rate:** eligible leads that purchase a subscription ÷ eligible leads.

### Deal conversion

- **Lead-to-deal conversion:** qualified transaction leads that create a Deal ÷ qualified transaction leads.
- **Deal win rate:** completed Deals ÷ Deals closed or lost, with the denominator defined consistently.
- **Lead-to-deal time:** elapsed time from lead creation to Deal creation.
- **Deal revenue:** realized fees/commissions from completed Deals, separate from expected transaction value.

### Quality and productivity

- **Duplicate lead rate**
- **Lost-reason distribution**
- **Lead owner workload and follow-up compliance**
- **Assessment-to-service accuracy:** services identified at assessment versus services ultimately requested
- **Customer satisfaction after service completion**

## 12. Enterprise Best Practices

- **Separate intent, source, and party role.** Do not encode “referral” or “admin-created” as Lead types.
- **Use controlled vocabularies.** Maintain defined values for source, intent, priority, lost reason, and assessment outcome.
- **Keep stage transitions meaningful.** Require appropriate data and an activity/decision record when advancing or closing a Lead.
- **Make follow-up owned and dated.** Every open Lead should have an owner and next action, or a documented reason why it does not.
- **Track activity history.** Record who changed ownership, status, priority, qualification, or conversion and when.
- **Use scoring as prioritization, not as a decision.** Consider customer intent, urgency, property readiness, response engagement, and serviceability; keep scoring factors reviewable.
- **Define SLA by stage and source.** Measure first response and follow-up separately from service delivery SLAs.
- **Protect customer data.** Respect contact preferences, consent, retention rules, and role-based visibility.
- **Avoid pipeline inflation.** Distinguish estimated opportunity amounts from approved quotes, invoices, and collected payments.
- **Preserve relationship integrity.** Link Lead, Customer, Property, Service Requests, Deal, quotes, and revenue records instead of copying mutable details between them.
- **Support legitimate non-conversion.** Closing without a Deal is a valid outcome, not a process failure when service needs were met or the customer chose not to proceed.
- **Audit exceptions.** Reopening lost leads, merging duplicates, overriding qualification, and creating multiple Deals should leave an accountable history.

## 13. Final Recommended Workflow

```text
Capture Lead
  ↓
Identify intent, source, customer role, and property context
  ↓
Assign owner and next action
  ↓
Contact and assess
  ├── Need is service-only
  │     ↓
  │   Identify service(s) and explain price/SLA
  │     ↓
  │   Create one or more Service Requests
  │     ↓
  │   Fulfill, validate evidence, invoice under policy
  │     ↓
  │   Reassess customer need
  │     ├── More services → additional Service Request(s)
  │     ├── Transaction intent → qualify Deal
  │     ├── Future interest → nurture
  │     └── No further need → close
  │
  └── Transaction intent exists
        ↓
      Verify parties, property, timing, and requirements
        ├── Prerequisite services needed → create requests, then reassess
        ├── Not ready → nurture
        └── Ready → create linked Deal
                      ↓
                    Manage transaction to won/lost/closed
                      ↓
                    Recognize deal revenue only under deal policy
```

The central rule is: **a Lead coordinates intent; Service Requests deliver services; Deals represent transactions.** Preserve their separate lifecycles while linking them so PropertyPilot can serve customers whether or not a transaction ultimately occurs.
