
# PropertyPilot Gap Analysis

Document Type: Architecture & Product Gap Review  
Version: 1.0  
Date: 2026-08-31  
Prepared by: Chief Enterprise Architect  
Scope: Review of all currently available PropertyPilot documents in the project repository

---

## 1. Executive Summary

The PropertyPilot project has a strong foundation across product vision, architecture, feature catalog, service catalog, journeys, screens, compliance-oriented architecture, and database modeling. The overall solution direction is coherent and enterprise-ready in concept.

However, the current documentation set still contains significant product, process, operational, and governance gaps that would prevent a production-grade implementation without additional detail. The most important gaps are in:
- operational security and governance
- financial and legal controls
- onboarding and tenant lifecycle completeness
- vendor and field service operations
- support processes, escalation paths, and SLA handling
- detailed API and integration contracts
- reporting taxonomy and report ownership
- subscription lifecycle and billing edge cases
- disaster recovery and business continuity runbooks
- data retention and data privacy implementation details

The project is reasonably mature for an enterprise architecture concept stage, but not yet complete for production release planning or full engineering execution.

This analysis classifies gaps as:
- Critical: must be addressed before MVP or before entering production
- Important: should be resolved in MVP or Phase 1 to avoid operational risk
- Nice-to-Have: valuable for later phases or advanced scale

---

## 2. Basis of Review

The following project documents were reviewed:
- Product_Vision.md
- Feature_Catalog.md
- Service_Catalog.md
- Customer_Journeys.md
- Subscription_Plans.md
- Screen_Catalog.md
- Screen_Flows.md
- Database_Physical_Model.md
- Technical_Architecture.md
- SRS.md
- Application_Interaction_Map.md

Conclusion:
- The platform vision is strong and aligned across disciplines.
- The architecture is sufficiently rich for enterprise planning.
- The current docs are missing operational and implementation specificity needed for hands-on engineering and QA readiness.

---

## 3. Overall Risk Assessment

| Area | Maturity | Risk |
|---|---|---|
| Product vision | Strong | Low |
| Feature completeness | Moderate | Medium |
| Screen coverage | Moderate | Medium |
| Service and domain decomposition | Strong | Low |
| Database modeling | Strong | Low |
| Security design | Moderate | Medium |
| Audit/compliance controls | Incomplete | High |
| Subscription lifecycle | Incomplete | High |
| Operational processes | Incomplete | High |
| Reporting requirements | Moderate | Medium |
| API specification coverage | Incomplete | High |
| Vendor and partner workflows | Incomplete | Medium |
| Disaster recovery | Partial | Medium |
| Production readiness | Incomplete | High |

---

## 4. Gap Analysis by Category

## 4.1 Missing Business Capabilities

### Findings

| ID | Finding | Priority | Description | Recommendation |
|---|---|---|---|---|
| BC-01 | Tenant lifecycle governance is not fully defined | Critical | No formal lifecycle for applicant, resident, move-in, move-out, delinquency, collections, and exit management | Add complete tenant lifecycle and state transitions to MVP |
| BC-02 | Vendor management is not fully modeled | Important | Vendor onboarding, qualification, SLA tracking, and invoice approval are not fully defined | Add vendor management capability in Phase 1 or 2 |
| BC-03 | Lease enforcement and legal controls are under-specified | Critical | Lease rules, legal clauses, default handling, eviction workflow, and document generation are not detailed enough | Define legal and operational lease policy rules before engineering |
| BC-04 | Portfolio operations and multi-property governance are weakly defined | Important | Multi-property ownership, portfolio-level compliance, approvals, and role segregation are not fully specified | Add portfolio governance and cross-property controls |
| BC-05 | Field operations and technician management are incomplete | Important | Mobile field work, technician scheduling, skill matrix, and route optimization are not detailed | Add field workforce domain in MVP or Phase 2 |
| BC-06 | Resident support and issue escalation are not fully defined | Important | No formal escalation rules for tenant complaints, priority changes, or service failures | Add SLA and escalation matrix |
| BC-07 | Compliance control matrix is incomplete | Critical | There is architecture but no operational compliance framework across lease, document, payment, and privacy controls | Add compliance framework before production |
| BC-08 | Property listing and external market expansion are not covered | Nice-to-Have | Marketplace features are mentioned only as future state and not planned in detail | Add as Phase 3/4 product extension |

### MVP Recommendation
- Include core tenant lifecycle governance
- Include lease policy and legal enforcement logic
- Include structured vendor and service partner onboarding
- Include service escalation and SLAs
- Include portfolio and org governance controls

### Future Release Recommendation
- Marketplace modules
- advanced field operations optimization
- AI-driven lease and maintenance recommendations
- expanded compliance automation

---

## 4.2 Missing Screens

### Findings

| ID | Finding | Priority | Description | Recommendation |
|---|---|---|---|---|
| SC-01 | No dedicated onboarding and account activation screens | Critical | No clear sign-up, org onboarding, property setup, and resident activation flow | Add screens for admin and tenant onboarding |
| SC-02 | No screen for compliance or document expiry reviews | Important | Compliance dashboard is implied but not defined as UI | Add a compliance center with expiring doc matrix |
| SC-03 | No screen for payment disputes / collections escalation | Important | Delinquency flow exists in narrative but not in screen catalog | Add dispute, adjustment, and escalation screens |
| SC-04 | No screen for vendor management and approval workflow | Important | Vendors appear in architecture but not in UI documentation | Add vendor onboarding and assignment screens |
| SC-05 | No screen for report subscription management | Important | Scheduled reports are mentioned but no UI flow exists | Add report subscription and delivery management |
| SC-06 | No screen for audit trail management | Critical | Audit logs are mentioned but no user interface or review screen exists | Add audit review and search screens |
| SC-07 | No screen for service SLA tracking | Important | SLA and performance tracking is implied but not surfaced to operations staff | Add service operations cockpit |
| SC-08 | No tenant move-out / exit process screen | Important | Legal and operational exit workflows are not defined in screens | Add move-out, check-out, and security deposit workflow screens |

### MVP Recommendation
- Add onboarding, audit, dispute, and collections screens
- Add vendor assignment and compliance management screens
- Add schedule/report subscription screens

### Future Release Recommendation
- field technician route UI
- predictive maintenance and analytics dashboard
- marketplace listing screens
- AI assistant and workflow copilot screens

---

## 4.3 Missing APIs

### Findings

| ID | Finding | Priority | Description | Recommendation |
|---|---|---|---|---|
| API-01 | No formal OpenAPI or contract definitions | Critical | The platform has service and transaction models, but no concrete API specs | Standardize API definitions for all modules |
| API-02 | No payment reconciliation API contract | Critical | Payment callback and settlement logic is high-risk without explicit API behavior | Define idempotent reconciliation APIs and failure states |
| API-03 | No subscription lifecycle API design | Critical | Subscription plans, renewals, proration, and downgrades are under-specified | Define lifecycle, states, and billing hooks |
| API-04 | No document management API contract | Important | AWS S3 access, metadata, versioning, retention, and security are not fully specified | Define document metadata and signed-access flow |
| API-05 | No API for audit search / review operations | Critical | Audit trails are discussed, but no API contract exists for operational review | Define audit query and export endpoints |
| API-06 | No API for vendor management | Important | Vendor workflows are absent from API design | Define onboarding, qualification, assignment, invoice, and SLA APIs |
| API-07 | No API for maintenance escalation and SLA management | Important | Escalation logic and action triggers are not described as endpoints | Add maintenance SLAs and escalation APIs |
| API-08 | No API for report scheduling and export delivery | Important | Scheduled reports are mentioned but API definitions absent | Use API-first approach for report management |
| API-09 | No identity and consent API model | Critical | Consent management, privacy rights, and SSO integration are not clearly defined | Add consent, auth, and identity APIs |
| API-10 | No bulk import/export API definitions | Important | Data migration and batch onboarding are implied but not formalized | Define bulk import APIs and validations |

### MVP Recommendation
- Define all domain APIs before implementation starts
- Add API contracts for:
  - tenant lifecycle
  - payment reconciliation
  - subscriptions
  - audit management
  - vendor management
  - report schedule / export
  - consent and privacy

### Future Release Recommendation
- decision support APIs
- marketplace APIs
- service partner APIs
- advanced analytics APIs

---

## 4.4 Missing Database Entities

### Findings

| ID | Finding | Priority | Description | Recommendation |
|---|---|---|---|---|
| DB-01 | No consent and privacy consent entity | Critical | Privacy treatment, data consent, and opt-in/out are not modeled | Add Consent, ConsentHistory, and ConsentScope models |
| DB-02 | No lease legal document and clause entity | Important | Lease terms, legal clause repository, and template variants are not specified | Add LeaseTemplate, LeaseClause, and LegalDocument tables |
| DB-03 | No task or workflow state entity | Important | Workflow state tracking for approvals, escalations, and admin actions is not modeled | Add WorkflowTask and WorkflowState tables |
| DB-04 | No vendor entity with service qualifications | Important | Vendor capability and qualifications are not in the physical model | Add Vendor, VendorQualification, and VendorRate tables |
| DB-05 | No audit chain / immutable event log model | Critical | Audit records are referenced, but no immutable event store or tamper-resistant pattern is defined | Add immutable audit log and event journal tables |
| DB-06 | No payment dispute / adjustment entity | Important | Payment disputes, reversals, manual adjustments, and write-offs missing | Add Dispute, Adjustment, and WriteOff models |
| DB-07 | No escalation matrix entity | Important | SLA and failure escalation model absent | Add EscalationPolicy and EscalationHistory |
| DB-08 | No notification delivery result entity at scale | Important | Notification logs are discussed but no persistent status model for retries is defined | Add NotificationDelivery and NotificationRetry tables |
| DB-09 | No billing schedule or proration entity | Critical | Subscription and invoice proration details are not modeled | Add BillingSchedule, ProrationEntry, InvoiceAdjustment |
| DB-10 | No service level or SLA tracking entity | Important | SLAs are not represented in DB design | Add ServiceLevelAgreement and SLAEvent tables |

### MVP Recommendation
- Add consent/privacy model
- Add immutable audit model
- Add dispute/adjustment model
- Add vendor qualification model
- Add SLA and escalation model

### Future Release Recommendation
- contract clause library
- workflow orchestration model
- advanced analytics traces and event warehouse

---

## 4.5 Missing Workflows

### Findings

| ID | Finding | Priority | Description | Recommendation |
|---|---|---|---|---|
| WF-01 | Move-in / move-out workflows are incomplete | Critical | No complete workflow from application to residence handover to exit | Add lifecycle workflow |
| WF-02 | Collections escalation workflow missing | Critical | There is no debt escalation process with reminders, legal notices, and collections actions | Add collections workflow |
| WF-03 | Payment dispute workflow missing | Important | Disputes and manual corrections are not defined | Add dispute management flow |
| WF-04 | Renewal approval and lease policy exceptions missing | Important | Renewal exceptions and legal approvals are not described | Add approval matrix |
| WF-05 | Vendor onboarding and performance workflow missing | Important | Vendor approval, onboarding, and performance review are not explicit | Add vendor lifecycle workflow |
| WF-06 | Audit response workflow missing | Critical | No workflow for security incident or audit review response | Add investigation and remediation workflow |
| WF-07 | Support case lifecycle missing | Important | Support operations and cross-functional escalation are not detailed | Add support workflow |
| WF-08 | Report approval workflow missing | Nice-to-Have | Scheduled reports and exported deliveries are described but approval flow missing | Add report governance workflow |

### MVP Recommendation
- Complete move-in/move-out
- Complete collections escalation
- Complete dispute workflow
- Complete audit investigation workflow
- Complete lease renewal exception workflow

### Future Release Recommendation
- predictive maintenance orchestration
- AI workflow assistance
- advanced partner and marketplace workflows

---

## 4.6 Missing Subscription Scenarios

### Findings

| ID | Finding | Priority | Description | Recommendation |
|---|---|---|---|---|
| SUB-01 | Trial and free tier scenarios are undocumented | Important | Subscription plan docs mention plans but not trials, grace periods, or free pilots | Add plan trial logic |
| SUB-02 | Grace period and arrears handling are missing | Critical | No defined billing grace period, retry policy, or service suspension rules | Add policy matrix |
| SUB-03 | Plan downgrade and reactivation edge cases are missing | Important | No logic for downgrade during active billing, reactivation, or contract carryover | Define downgrade and reactivation rules |
| SUB-04 | Proration and invoice adjustments are not specified | Critical | No billing prorating rules across plan changes | Add proration engine and policy |
| SUB-05 | Multi-organization billing and shared account scenarios missing | Important | No enterprise account billing model described | Define parent-child org billing structure |
| SUB-06 | Plan usage limits and overage logic missing | Important | No description of inclusion caps and overage billing | Define usage metrics and enforcement |
| SUB-07 | Renewal failure and failed payment scenarios absent | Critical | No plan renewal retry or service restriction process | Add renewal failure handling |
| SUB-08 | Contract cancellation and data retention rules missing | Critical | No termination and offboarding policy defined | Add contract cancellation procedures and data cleanup rules |

### MVP Recommendation
- Define subscription lifecycle states and rules
- Add grace period, suspension, reactivation, and failed renewal logic
- Define proration and invoice adjustment rules
- Add billing and contract termination process

### Future Release Recommendation
- enterprise multi-entity subscriptions
- partner/reseller billing
- marketplace subscription bundles

---

## 4.7 Missing Operational Processes

### Findings

| ID | Finding | Priority | Description | Recommendation |
|---|---|---|---|---|
| OP-01 | No incident and outage management process | Critical | There is no incident runbook and service restoration process | Define SRE and incident management process |
| OP-02 | No support and ticket escalation model | Important | No support process for user-facing issues, vendor failures, or premium support | Define support tiers and escalation |
| OP-03 | No change management and release governance process | Critical | No production release controls or rollback governance is defined | Add change management and release freeze process |
| OP-04 | No capacity and performance tuning process | Important | No operational performance review plan or scale trigger process | Add capacity review and tuning process |
| OP-05 | No backup validation and restore rehearsal process | Critical | DR strategy exists but not proof of validation process | Add restore testing and backup verification |
| OP-06 | No access review process | Critical | No periodic review model for admin, privilege, or role approval | Add quarterly access review and attestation |
| OP-07 | No service desk workflow for tenants and operators | Important | No formal process for tenant issues routing within ops teams | Add service desk process model |

### MVP Recommendation
- Add incident management
- Add release governance
- Add backup validation
- Add access review
- Add support escalation flow

### Future Release Recommendation
- SRE maturity and predictive operations
- advanced AIOps and event correlation
- automated remediation playbooks

---

## 4.8 Missing Security Requirements

### Findings

| ID | Finding | Priority | Description | Recommendation |
|---|---|---|---|---|
| SEC-01 | Privacy and consent requirements are under-defined | Critical | No clear consent semantics and privacy controls are described | Add explicit privacy policy and consent model |
| SEC-02 | No data classification and protection matrix | Critical | No classification of data sensitivity by object or domain | Add classification matrix with encryption and retention rules |
| SEC-03 | No tenant isolation enforcement specification | Critical | Multi-tenant boundaries are described but not operationally enforced in detail | Define row-level security and org isolation design |
| SEC-04 | No formal threat model and abuse cases | Important | Security threats and abuse scenarios are not enumerated | Add threat model and abuse cases |
| SEC-05 | No secure file upload / validation process | Important | Document uploads are mentioned but no validation rules defined | Add malware scanning and file validation controls |
| SEC-06 | No session management and token revocation design | Important | Token lifecycle and logout semantics are not described | Define JWT/session lifecycle and refresh policy |
| SEC-07 | No external identity trust model for enterprise SSO | Important | Identity provider trust and federation design incomplete | Define SSO trust, scopes, and enterprise mapping |
| SEC-08 | No rate limiting and abuse prevention model | Important | Platform protection against abuse and brute force is not described | Add request throttling and bot protection design |
| SEC-09 | No incident response plan and breach notification workflow | Critical | No IR plan defined for security events or data exposure | Add incident response playbook |

### MVP Recommendation
- Add privacy and consent model
- Add tenant isolation design
- Add secure upload and malware scanning controls
- Add session and token lifecycle controls
- Add incident response workflow

### Future Release Recommendation
- deeper identity governance and adaptive MFA
- threat intelligence automation
- risk-based anomaly detection

---

## 4.9 Missing Audit Requirements

### Findings

| ID | Finding | Priority | Description | Recommendation |
|---|---|---|---|---|
| AUD-01 | No immutable audit model defined | Critical | Audit is discussed but fine-grained record retention and immutability are not specified | Add event journal and tamper-resistant logs |
| AUD-02 | No approval and exception audit trail for lease/legal actions | Critical | Lease approval exceptions and legal overrides are not captured | Add workflow audit events |
| AUD-03 | No financial approval workflow traceability | Critical | Manual adjustments and overrides are not fully audited | Add approval matrix and review controls |
| AUD-04 | No document change history model | Important | Versioning and document access history are incomplete | Add document version, access, and change log |
| AUD-05 | No audit retention and purge policy | Important | Retention period and legal hold process is not defined | Add audit retention schedule |
| AUD-06 | No audit review dashboard for security and ops | Important | No user interface for operational audit review | Add audit operations workspace |
| AUD-07 | No security event correlation model | Important | Alerts and incident investigation are not connected to audit logs | Add correlation and SIEM integration |

### MVP Recommendation
- Add immutable audit log model
- Add financial exception audit trail
- Add document version and access logging
- Add retention policy

### Future Release Recommendation
- integration with SIEM and governance tools
- policy enforcement automation
- advanced compliance automation

---

## 4.10 Missing Reporting Requirements

### Findings

| ID | Finding | Priority | Description | Recommendation |
|---|---|---|---|---|
| REP-01 | No report ownership and stewardship model | Important | It is not clear who owns each report and how it is maintained | Define report catalog and ownership |
| REP-02 | No executive KPI library | Important | KPI taxonomy is implied but not defined | Create KPI matrix by business role |
| REP-03 | No report data lineage model | Important | No traceability from report field to source data | Add lineage mapping |
| REP-04 | No privacy-sensitive reporting policy | Critical | Reports may include PII or sensitive financial data without clear rules | Add filtering and masking policy |
| REP-05 | No report distribution governance | Important | Scheduled delivery is described but not managed by governance model | Add subscription and approval process |
| REP-06 | No unavailable / delayed report handling model | Important | There is no service behavior for failed or stale report generation | Add SLA and fallback model |
| REP-07 | No long-term analytics strategy | Nice-to-Have | Current reporting covers operational reporting only | Define analytics roadmap for predictive and prescriptive reporting |

### MVP Recommendation
- Define report catalog and KPI ownership
- Add masking and privacy policy
- Add report failure handling and SLA
- Add report distribution governance

### Future Release Recommendation
- advanced analytics and forecasting
- predictive maintenance and revenue analytics
- AI-driven portfolio recommendations

---

## 5. Priority Summary: Critical Gaps

The following items should be treated as non-negotiable before MVP or production readiness:

1. Tenant lifecycle governance and legal enforcement
2. Payment reconciliation and financial controls
3. Subscription lifecycle and proration rules
4. Audit and immutable log model
5. Consent and privacy model
6. Multi-tenant data isolation enforcement
7. Backup validation and restore testing
8. Security incident response plan
9. API contract definition
10. Vendor management and assignment controls

---

## 6. MVP Recommendations

### MVP Scope Recommendation
PropertyPilot MVP should include the following business and technical additions before engineering begins:

- Complete tenant lifecycle:
  - applicant
  - active resident
  - lease renewal
  - delinquency
  - move-out
  - final settlement
- Complete lease management and legal contract rules
- Robust billing and payment lifecycle:
  - invoice creation
  - payment processing
  - reversal
  - dispute
  - collections escalation
- Subscription lifecycle:
  - plan selection
  - plan change
  - proration
  - failed renewal
  - suspension and reactivation
- Audit model:
  - immutable audit log
  - approval events
  - document access log
  - security event log
- Privacy and consent:
  - consent capture
  - consent revocation
  - privacy policy enforcement
- Security and governance:
  - role model
  - tenant isolation
  - security incident response
  - access review
- Operations:
  - support workflow
  - incident response
  - release governance
  - backup validation

### MVP Delivery Principle
The MVP should not be driven only by feature count. It should be driven by:
- production-grade data controls
- policy enforcement
- tenant safety and compliance
- financial integrity
- operational recoverability

---

## 7. Future Release Recommendations

### Phase 2
- Vendor marketplace and service partner management
- Field workforce schedule and technician optimization
- Advanced SLA management
- Expanded report catalog and analytics
- Advanced collections automation
- Compliance center with policy dashboards

### Phase 3
- AI-driven maintenance, occupancy, and renewal suggestions
- Portfolio forecasting and predictive analytics
- Expanded marketplace and lead routing
- External partner management ecosystem
- Enterprise SSO and enterprise GRC integration

### Phase 4
- Intelligent property operations assistant
- predictive cashflow optimization
- high-automation workflow orchestration
- cross-organization data exchange and standardization
- advanced market intelligence and property demand analytics

---

## 8. Final Conclusion

PropertyPilot is structurally strong and strategically well-founded, but it is not yet complete enough for production delivery without additional operational, security, legal, and billing details.

The highest-priority gaps are not about user experience alone — they are about governance, tenant safety, financial integrity, compliance, auditability, and operational readiness. These areas must be closed before a production launch or large-scale enterprise deployment.

The project is well-positioned to move forward, but it should do so using a disciplined release strategy:
- MVP = operationally safe, legally sound, financially controlled, and auditable
- Phase 2 = scale, automation, and analytics
- Phase 3/4 = marketplace and intelligent operations

---

## 9. Recommendation Summary

| Priority | Recommendation |
|---|---|
| Critical | Complete tenant lifecycle and legal enforcement |
| Critical | Finalize payment reconciliation, proration, and billing controls |
| Critical | Add immutable audit and security event model |
| Critical | Define privacy, consent, and multi-tenant security rules |
| Critical | Define subscription lifecycle and failure policy |
| Important | Add full operational process and support runbooks |
| Important | Expand vendor and field service domain |
| Important | Fill UI gaps for onboarding, audit, compliance, and reporting |
| Important | Add API contract definitions |
| Nice-to-Have | Marketplace, AI, and advanced analytics expansion |

---

## 10. Action Items for the Next Architecture Review

1. Define missing lifecycle states and transition matrices
2. Create IAM and tenant isolation policy
3. Define immutable audit log and data retention policy
4. Define API contract backlog by domain
5. Define subscription lifecycle and billing rules
6. Define support, incident, and release governance process
7. Define report catalog and KPI ownership
8. Define vendor onboarding and service SLA processes
9. Define consent/privacy model and retention rules
10. Update MVP backlog and roadmap based on the above gaps
