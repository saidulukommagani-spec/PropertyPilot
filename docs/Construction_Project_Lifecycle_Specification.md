# Purpose

This specification defines the lifecycle for construction projects delivered through existing PropertyPilot construction services, vendor quotation management, and customer journeys. It covers compound/precast/RCC walls, guest/farm houses, watchman rooms, site offices, storage sheds, houses, villas, commercial buildings, and approved construction monitoring services listed in the Service Catalog.

It consolidates the project-execution controls already implied by Vendor Management, Customer Journeys, Service Catalog, Screen Flows, and SRS. Where project APIs, screens, or physical entities are not yet defined, this specification records the required implementation contract; it does not redefine unrelated marketplace or service functionality.

# Actors

| Actor | Responsibilities |
|---|---|
| Customer | Requests construction service, provides property/scope input, reviews quotations and change orders, approves milestones/completion where required, and receives reports/warranty information. |
| Vendor | Provides quotation/BOQ, accepts assignment, executes approved scope, submits progress, evidence, milestone completion, invoice, rework response, and warranty response. |
| Operations | Validates eligibility, coordinates quotation/assignment, monitors SLA/progress, arranges inspections, reviews evidence, manages change/dispute escalation, and recommends payment release. |
| Admin | Approves controlled exceptions, vendor restrictions, high-value selection/change/payment decisions, dispute outcome, and audit access under segregation-of-duties rules. |

# Project Lifecycle

| Phase | Entry condition | Activities | Exit condition |
|---|---|---|---|
| Lead | Construction enquiry/service request exists. | Capture property, service type, requirements, budget/timeline context, eligibility and coverage. | Eligible request is ready for quotation. |
| Quotation | Eligible vendors are invited or selected under vendor policy. | Vendor submits versioned quotation, BOQ, assumptions, exclusions, tax, schedule, milestones, payment terms, warranty, and supporting documents. | Valid quotation is available for comparison/selection. |
| Approval | Customer/authorised user selects an eligible, non-expired quotation. | Validate scope, price, vendor status/capacity, approvals, funding/payment prerequisites, and exceptions. | Selection is approved or rejected; approved quotation is immutable snapshot. |
| Contract | Approved selection and terms are accepted. | Create project/job, assignment, scope version, milestones, SLA, acceptance criteria, warranty, and payment controls. | Authorised vendor assignment/project is active. |
| Execution | Vendor accepts active assignment. | Perform approved work, submit milestone updates/evidence, manage approved rework and change orders. | All committed scope is complete or project is cancelled/disputed. |
| Inspection | Milestone or completion evidence is submitted. | Operations/customer performs quality, scope, safety, location/timestamp and acceptance checks. | Milestone/completion is accepted, rejected for rework, or escalated. |
| Completion | Final inspection passes and contractual scope is fulfilled. | Record completion, final acceptance, report, invoice/holdback/final settlement eligibility. | Project moves to warranty or closure. |
| Warranty | Completion acceptance is recorded. | Track warranty coverage, defects, repair requests, response SLA, and evidence. | Warranty expires or all obligations are resolved. |
| Closure | Financial, evidence, dispute, warranty, and retention conditions are satisfied. | Reconcile payments, archive record, collect rating where applicable, preserve audit/evidence. | Project is closed; no new execution change permitted. |

Terminal project outcomes are `CLOSED`, `CANCELLED`, or `TERMINATED`. A cancelled/terminated project retains quotation, assignment, evidence, financial, reason, and audit history.

# Scope Management

1. The approved quotation creates the baseline scope version with work description, property, specifications, exclusions, assumptions, quality/acceptance criteria, schedule, warranty, and commercial terms.
2. Only authorised customer/operations/admin roles may request scope change; vendor may propose but cannot self-approve scope, price, or timeline change.
3. The project must show baseline scope, current approved scope, open change orders, and relationship of each milestone/evidence item to its scope version.
4. Work outside approved scope is not eligible for milestone acceptance or payment except under approved emergency/exception authority.

# BOQ Management

The Bill of Quantities (BOQ) is a versioned quotation/project component. Each approved BOQ records item description, unit, quantity, unit rate, labour/material/equipment/travel cost, tax, subtotal, exclusions, assumption, supplier/vendor reference where applicable, and linked scope/milestone.

- BOQ versions are immutable after approval; a change order creates a new version/delta, not an overwrite.
- Totals must reconcile to the approved quotation and payment schedule.
- Operations validates commercial reasonableness; Finance/Admin approval is required for configured monetary thresholds.
- Customer-visible BOQ views must exclude internal vendor cost/risk data not authorised for disclosure.

# Milestone Management

| Control | Requirement |
|---|---|
| Definition | Each project has ordered, scoped milestones with planned date, completion criteria, evidence requirement, inspection requirement, payment/holdback relation, and owner. |
| Status | `PLANNED`, `IN_PROGRESS`, `SUBMITTED_FOR_REVIEW`, `ACCEPTED`, `REWORK_REQUIRED`, `BLOCKED`, `CANCELLED`. |
| Progress | Vendor submits progress against approved scope/milestone; Operations validates evidence and may request rework. |
| Acceptance | Accepted only after configured inspection/quality/customer approval; acceptance records actor, date, scope version, evidence, and exception. |
| Dependency | Later milestone/payment cannot be released if prior dependency is unaccepted unless authorised exception is recorded. |

# Change Orders

1. Change order may be raised for customer requirement, site condition, regulatory requirement, approved design/BOQ correction, or controlled emergency.
2. It identifies reason, affected scope/BOQ/milestones, cost/tax delta, schedule/warranty impact, vendor proposal, evidence, requester, and approval route.
3. Operations assesses impact; customer approval is required for customer price/scope/timeline impact. Admin/Finance approval applies at configured exception thresholds.
4. An approved change order creates new scope/BOQ/milestone versions and preserves prior versions. Rejected/withdrawn order does not alter the baseline.
5. No vendor payment or completion claim may include an unapproved change order.

# Payment Milestones

- Payment terms may include approved advance, accepted milestone payment, final settlement, approved reimbursement, quality holdback, and incentive, as already defined in Vendor Management.
- A payment milestone references approved quotation/BOQ/scope version, vendor assignment, milestone acceptance, invoice, tax/bank validation, amount/currency, holdback, approver, and provider/settlement outcome.
- Separation of duties applies: requester/vendor, evidence reviewer, payment recommender, and finance approver are distinct where configured.
- No payment is released merely because vendor marks a milestone complete. Accepted evidence, inspection, invoice, financial controls, and approval are required.
- Payment failure, refund, dispute, chargeback, or settlement exception follows the payment/refund governance and preserves project commercial state.

# Vendor Assignment

1. Assignment is created only for an active, verified vendor mapped to the construction service, eligible coverage, required capacity, accepted quotation, and approved project scope.
2. Assignment records vendor, project/service request, scope version, schedule, SLA, milestones, completion criteria, payment terms, warranty, and authorised contacts.
3. Exactly one canonical assignment aggregate must be selected before implementation; current `service_assignments`, `vendor_assignments`, and legacy migration representations must be reconciled.
4. Vendor can accept, progress, submit evidence, request rework/change, submit completion/invoice, or report blocker. Operations/Admin handles reassignment, suspension, exception, and termination.

# Site Inspections

1. Operations schedules inspection by milestone, risk, payment/acceptance gate, complaint, alert, or customer request.
2. Inspector verifies property/service/assignment context, approved scope version, work quality, evidence integrity, capture time, location accuracy, safety/compliance checklist, and open changes.
3. Inspection produces accepted finding, rework request, blocker, escalation, or completion recommendation with controlled evidence/report links.
4. Evidence follows checksum, malware, timestamp, device, optional GPS, access-control, and immutable-version rules. Inspection outcome does not overwrite vendor evidence.

# Completion Acceptance

1. Vendor submits final completion with scope version, milestone state, evidence, report, warranty information, invoice, and exceptions.
2. Operations validates all milestones, approved change orders, inspection findings, quality criteria, evidence, safety/compliance items, and customer acceptance requirement.
3. Customer may accept, request documented rework, or raise dispute within configured acceptance window. Silence must not equal acceptance unless contract/policy explicitly permits it.
4. Accepted completion records acceptance actor/time, report/version, warranty start/end, final settlement/holdback status, and closed scope baseline.

# Warranty Management

| Control | Requirement |
|---|---|
| Warranty record | Link project, vendor, approved scope/BOQ, coverage, exclusions, start/end, service SLA, claim procedure, and evidence. |
| Claim | Customer/Operations logs defect, property/project context, severity, evidence, and requested remedy. |
| Response | Vendor acknowledges, inspects, repairs, or disputes within configured warranty SLA. |
| Closure | Operations validates repair evidence and customer/authorised acceptance; preserve all claim history. |
| Expiry | System notifies relevant parties before expiry and prevents new claim after expiry except authorised legal/safety exception. |

# Dispute Management

1. Disputes may concern scope, quotation, BOQ, change order, progress, quality, milestone acceptance, invoice, payment, warranty, or misconduct.
2. A dispute freezes only affected payment/milestone/scope action; it must not silently halt unrelated approved work unless risk/safety/compliance requires.
3. Operations gathers immutable scope versions, quotations, approvals, inspection reports, evidence, communication, invoice/payment records, and audit history.
4. Authorised Operations/Admin/Finance resolves through accepted rework, revised agreement, partial approval, refund/holdback, reassignment, termination, or escalation. Resolution is audit logged and safely communicated.

# Notifications

| Trigger | Recipient | Priority | Notification alignment |
|---|---|---|---|
| Quotation requested/submitted/selected/rejected/expired | Customer, eligible vendor, Operations | HIGH | Vendor quotation/customer service templates. |
| Project/vendor assignment, schedule, scope, SLA change | Customer, assigned vendor, Operations | HIGH | `VendorAssigned`, assignment and job-change notifications. |
| Milestone submitted/accepted/rework/blocked | Customer, vendor, Operations | HIGH | Vendor job review/progress notifications. |
| Change order requires approval | Customer, Operations/Admin, vendor | HIGH | Approved controlled configuration/approval template. |
| Inspection/completion/warranty outcome | Customer, vendor, Operations | MEDIUM/HIGH | Report/completion/warranty template. |
| Payment, holdback, settlement, or dispute exception | Vendor finance contact, Finance/Admin, customer where applicable | HIGH | Vendor financial/customer payment exception templates. |
| Critical safety/fraud/major SLA breach | Security/Operations/Admin | CRITICAL | Security/operational incident escalation. |

All notification delivery honours consent, channel availability, approved templates, and safe disclosure rules. Notification events do not include protected evidence or unmasked third-party contacts.

# APIs

Existing Vendor Management/API Catalog patterns support vendor jobs and quotations at a catalog level. The following project-lifecycle operations are required to implement the existing construction journeys and must be specified in canonical OpenAPI before build:

| API capability | Required operations |
|---|---|
| Project | Create project from approved quotation; retrieve project lifecycle, scope, status, and closure. |
| BOQ/scope | Retrieve approved BOQ; submit/review/approve versioned scope or BOQ change. |
| Milestones | Create/retrieve/update progress; submit/accept/rework/block milestone. |
| Change orders | Create, price, approve/reject/withdraw, and retrieve change-order history. |
| Inspections | Schedule inspection; submit finding/evidence; retrieve inspection/report outcome. |
| Completion/warranty | Submit/accept/reject completion; create/manage warranty claim and repair closure. |
| Financial/dispute | Retrieve payment milestone/holdback status; raise/resolve project dispute under authorised workflow. |

# Screens

The Traceability Matrix identifies construction project screens as missing from the current Screen Catalog. The following screen contracts are required before implementation:

| Screen | User/action |
|---|---|
| Construction Project Dashboard | Customer/vendor/operations project status, schedule, current scope, milestones, alerts. |
| Quotation and BOQ Comparison | Customer/operations compare versioned approved quotations/BOQ with protected internal data controls. |
| Milestone Progress and Inspection | Vendor submits progress/evidence; operations/customer reviews acceptance/rework. |
| Change Order Approval | Customer/operations/admin evaluates scope, price, timeline, and warranty impact. |
| Completion and Warranty | Completion acceptance, reports, final settlement status, warranty claim/tracking. |
| Construction Dispute Workbench | Authorised operations/finance/admin evidence review and resolution. |

# Reports

| Report | Audience | Minimum content |
|---|---|---|
| Project status report | Customer, Operations | Lifecycle/status, schedule, milestone progress, current scope/BOQ version, open blockers/changes. |
| Inspection report | Customer, vendor, Operations | Inspection date, scope/milestone, findings, evidence references, rework/acceptance, reviewer. |
| Commercial report | Finance, Operations, authorised customer/vendor | Quotation/BOQ version, approved changes, payment milestones, invoice/holdback/settlement exception. |
| Warranty report | Customer, vendor, Operations | Coverage, claims, repair status, expiry, closure. |
| Vendor performance report | Operations/Admin | SLA, quality/rework, completion, dispute, capacity, payment/holdback indicators. |

# Metrics

Operations reviews project count/status, quotation turnaround and acceptance rate, scope/change-order frequency and value, milestone on-time/acceptance/rework rate, inspection finding severity, schedule/cost variance, vendor capacity/SLA/quality, payment/holdback/reconciliation status, completion acceptance time, warranty claim/repair rate, dispute volume/resolution time, customer satisfaction, and evidence/report completeness.

# Related Documents

- [Marketplace Management](02_Product/Marketplace_Management.md)
- [Vendor Management](01_Business/Vendor_Management.md)
- [Service Catalog](02_Product/Service_Catalog.md)
- [Customer Journeys](01_Business/Customer_Journeys.md)
- [Screen Flows](09_Diagrams/Screen_Flows.md)
- [PropertyPilot SRS](PropertyPilot_SRS.md)
- [Traceability Matrix](Traceability_Matrix.md)
