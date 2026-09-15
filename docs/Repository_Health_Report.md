# PropertyPilot Repository Health Report

## Audit Scope and Result

This is a read-only audit of the current `docs/` repository, containing **192 Markdown/OpenAPI artefacts** at assessment time. It incorporates the current documentation-control register, traceability matrix, readiness assessments, health report, data dictionary, KYC/privacy/webhook/integration/production-cutover documents, and the documented implementation baseline.

PropertyPilot has strong breadth of documentation and governance. It remains **not ready for broad implementation or external MVP release** because canonical API/schema contracts, executable platform assets, provider decisions, and verification evidence are incomplete or inconsistent.

## Readiness Summary

| Measure | Score | Assessment |
|---|---:|---|
| Documentation completeness | **82%** | Broad business, product, architecture, data, API, UX, planning, governance, and operational coverage. Score is reduced by unresolved traceability gaps, stale/duplicate sources, absent control records for newest documents, and missing implementation-grade specifications. |
| MVP readiness | **45%** | MVP scope is documented, but several P0 customer/service/payment/KYC/subscription/privacy paths lack complete contract, schema, screen, integration, and test evidence. |
| Architecture readiness | **65%** | Architectural principles and target domains are well documented. Final executable topology, ADRs, selected providers, event-platform choices, measurable SLOs, and production environment decisions are incomplete. |
| Development readiness | **46%** | A controlled foundation phase can begin after P0 reconciliation. Broad feature development should not begin because OpenAPI, physical schema/migrations, CI/CD/IaC, security enforcement, and test automation are not ready. |

## 1. Remaining Missing Documents

| Priority | Missing document / controlled artefact | Why it remains needed | Proposed owner |
|---|---|---|---|
| P0 | MVP Threat Model and Data-Flow Security Assessment | Security Design specifies controls but has no evidenced MVP threat model, attack paths, provider/data flows, mitigations, or risk acceptance. | Security Architect |
| P0 | Production SLO, Error Budget, and Alert Threshold Catalogue | Runbooks/monitoring architecture exist, but measurable availability, latency, recovery, queue, and business-service targets are not one approved control source. | Platform Operations Lead |
| P0 | Executable Environment and Deployment Configuration Register | Environment catalogue has incomplete/TBD ownership; no approved environment topology, secret/config mapping, promotion matrix, or production values record exists. | DevOps Lead |
| P0 | Physical Schema Completion Pack | Database Physical Model names subscription, payment, vendor, complaint, and notification entities without fields; a controlled schema/ERD/migration completion pack is required. | Data Architect |
| P0 | OpenAPI v1 Completion and Compatibility Baseline | The canonical OpenAPI file covers only a small subset of catalogued APIs and conflicts with v1 path/model naming. | API Lead |
| P1 | Construction Project Data Model and API/Screen Contract Set | Construction lifecycle specification now exists, but its feature, persistence, API, and screen contracts are not yet represented in canonical catalogues. | Product / Data / API / UX Leads |
| P1 | Privacy Operations Data Model and API Contract | Privacy procedure exists, but request, consent, restriction, legal-hold, export manifest, deletion outcome, and processor confirmation persistence/API contracts are not defined. | Privacy Officer / Data Architect |
| P1 | KYC Administration Data Model and API Contract | KYC playbook exists, but case history, reviewer/maker-checker, fraud, expiry, evidence, and queue API/schema contracts are incomplete. | KYC Operations / Data / API Leads |
| P1 | Third-Party Provider Decision Records | Integration register names all payment, messaging, maps, storage, analytics, and KYC providers as `TBD`; ADR/procurement/DPA/exit records are required. | Integration Architect / Procurement |
| P1 | Data Processing / Sub-processor Register | Privacy controls require documented processor purpose, data, location, DPA, retention, security, incident, and exit obligations. | Privacy Officer |
| P1 | Production Support and On-Call RACI | Cutover checklist defines escalation but not named service ownership, rota, contacts, handoff, and support coverage commitments. | Operations Lead |
| P2 | KPI Dictionary and Executive Metric Catalogue | Reporting/analytics documents exist, but a single approved business KPI definition/owner/lineage catalogue remains absent. | Analytics Owner |

## 2. Remaining Duplicate Documents and Content

| ID | Duplicate / overlap | Current authority | Required action |
|---|---|---|---|
| D-01 | `ARCHITECTURE_SUMMARY.md` overlaps Technical Architecture. | Technical_Architecture.md | Retain summary as non-normative overview only or archive after reference remediation. |
| D-02 | `Database_Design.md`, `Database_Design_v2.md`, and Database Physical Model overlap. | Database_Physical_Model.md | Deprecate legacy design; reconcile `v2` before it is used. |
| D-03 | Root and `07_Planning` Project Master Specifications overlap. | `07_Planning/Project_Master_Specification.md` | Merge unique history and archive root document. |
| D-04 | API Catalog detailed contracts and appended endpoint inventories overlap. | OpenAPI_Specification.yaml | Remove/reconcile appended inventories; generate derivative API catalog view from OpenAPI. |
| D-05 | Subscription lifecycle appears in legacy Subscription Management sections and State Model Catalog. | State_Model_Catalog.md | Mark/map legacy terms and remove them from new work. |
| D-06 | Assignment appears in legacy migration `agent_assignments`, `service_assignments`, and `vendor_assignments`. | Database Physical Model plus State Model Catalog | Approve one canonical assignment aggregate and forward migration. |
| D-07 | Business Process Catalog contains duplicate second catalog segment. | First complete segment of Business_Process_Catalog.md | Remove duplicate segment in controlled change. |
| D-08 | Final/Implementation readiness reports overlap in score/gap reporting. | Implementation_Readiness_Assessment.md for implementation gate; Repository_Health_Report.md for documentation audit | Establish distinct cadence/purpose and cross-link instead of duplicating score updates. |

## 3. Remaining Conflicting Requirements

| ID | Conflict | Canonical resolution required |
|---|---|---|
| C-01 | `/api/v1` in Cross-Cutting Requirements versus `/v1` in OpenAPI and unprefixed API Catalog routes. | Set `/api/v1` in OpenAPI/API Catalog/gateway/client conventions and publish compatibility migration. |
| C-02 | Mobile OTP MVP authentication versus API Catalog password login/reset/MFA availability. | Approve one MVP authentication scope; mark unsupported routes as future/privileged-only or amend master policy. |
| C-03 | Customer-managed property entity versus `POST /properties` described as marketplace listing creation. | Separate property and marketplace-listing resources/contracts. |
| C-04 | Service statuses in cross-cutting/state documents versus `created` status in migration. | Align state model, API enums, migrations, events, history names, and test data. |
| C-05 | Subscription `PENDING`/`PAST_DUE`/`SUSPENDED` versus legacy `DRAFT`/`PENDING_PAYMENT`/`RENEWAL_DUE`. | State Model Catalog is authoritative; publish mapping and migration. |
| C-06 | Initial quote distance source uses agent/property coordinates in pricing material versus property/coverage coordinates in Cross-Cutting Requirements. | Update pricing specification/implementation to the master rule. |
| C-07 | SRS historically states Pricing Strategy is absent although it now exists. | Update SRS sources and related acceptance criteria. |
| C-08 | Construction project specification defines lifecycle controls but Feature Catalog has no construction-project feature. | Add approved feature scope/priority and trace it to the project specification. |

## 4. Documents That Should Be Deprecated

| Document / content | Replacement | Condition |
|---|---|---|
| `ARCHITECTURE_SUMMARY.md` | Technical_Architecture.md | After inbound links are updated. |
| `Database_Design.md` | Database_Physical_Model.md | Immediate for new work; retain only history. |
| Root `Project_Master_Specification.md` | `07_Planning/Project_Master_Specification.md` | After unique content merge. |
| Appended endpoint inventories in API_Catalog.md | OpenAPI-generated/API Catalog contract sections | After unique entries are migrated to OpenAPI. |
| Legacy Subscription Management lifecycle sections | State_Model_Catalog.md | After status mapping is published. |
| Legacy SQL migration representation where it conflicts with Physical Model | Reconciled forward-only migration baseline | Before further schema development. |
| Duplicate second Business Process Catalog segment | First complete catalog segment | After controlled removal. |

## 5. Documents That Should Become Canonical Sources

| Document | Canonical scope to assign | Prerequisite |
|---|---|---|
| Construction_Project_Lifecycle_Specification.md | Construction project execution, milestone, BOQ, change, acceptance, warranty, and project dispute lifecycle. | Add Feature Catalog, data/API/screen traceability and register entry. |
| Production_Cutover_and_GoLive_Checklist.md | Production cutover/go-live release gate. | Add to Documentation Control Register; attach release evidence template. |
| KYC_Operations_Playbook.md | KYC operational workflow for all subject types. | Complete schema/API/screen contract and register review. |
| Privacy_Rights_Operating_Procedure.md | Privacy-rights operating workflow. | Complete privacy data/API implementation contract and register review. |
| Webhook_Contract_Catalog.md | Provider webhook security, retry, DLQ, and event-mapping contract. | OpenAPI callback and provider adapter implementation baseline. |
| Integration_Contract_Register.md | Third-party provider governance and approval record. | Populate approved provider decisions and maintain per-provider addenda. |
| Non_Functional_Requirements.md | Measurable NFR/acceptance-source authority. | Consolidate SLO, performance, resilience, accessibility, and capacity thresholds into it or an approved companion catalogue. |

## 6. Documentation Governance Findings

- The current Documentation Control Register does not yet list `Construction_Project_Lifecycle_Specification.md` or `Production_Cutover_and_GoLive_Checklist.md`; both need owner, status, review cadence, and canonical-scope decision.
- The register classifies some grouped documents together. High-risk documents should have individual rather than family-level entries once implementation begins: API contracts, security, physical data model, migrations, privacy/KYC, integration, cutover, DR, and test/quality controls.
- New operating documents have improved coverage but do not close traceability until their corresponding Feature Catalog, Screen Catalog/Flows, OpenAPI, physical model, migrations, and backlog records are updated.

## Prioritized Recommendations

| Order | Priority | Recommendation | Outcome |
|---:|---|---|---|
| 1 | P0 | Resolve C-01 through C-07 in canonical sources and create ADRs. | A coherent product/API/state/data implementation baseline. |
| 2 | P0 | Complete OpenAPI v1, standard errors/security/idempotency, and contract compatibility checks. | APIs can be implemented/tested without catalog drift. |
| 3 | P0 | Complete physical definitions/migrations for core missing entities; reconcile existing migrations. | Safe schema and data foundation. |
| 4 | P0 | Approve runtime, environment, provider, eventing, storage, identity, and observability decisions with executable DevOps baseline. | Reproducible development and release path. |
| 5 | P0 | Produce MVP threat model, SLO/error budget catalogue, and production support/on-call RACI. | Testable security/reliability operational controls. |
| 6 | P1 | Add project, KYC, privacy, subscription, cancellation, and task feature/API/screen/data traceability. | New operating specifications become implementable scope. |
| 7 | P1 | Select/approve provider integrations and complete DPA/security/exit/runbook/contract-test evidence. | External dependencies become launchable. |
| 8 | P1 | Update Documentation Control Register for new documents and individual high-risk contract entries. | Governance remains current and auditable. |
| 9 | P2 | Retire/merge duplicate and legacy artefacts; generate derivative views from canonical models. | Lower documentation drift and review burden. |

## Audit Decision

**Documentation:** mature enough to support a controlled foundation phase after canonical conflict resolution.  
**MVP:** no-go for external launch until P0 schema/API/platform/security/integration gaps are closed and evidenced.  
**Architecture:** conditionally ready for approved foundation work, not production deployment.  
**Development:** restrict work to a vertically sliced foundation with reconciled contracts, forward-only migrations, CI/CD, and security controls.

## Evidence Sources

- [Documentation Control Register](Documentation_Control_Register.md)
- [Traceability Matrix](Traceability_Matrix.md)
- [Final Repository Health Report](Final_Repository_Health_Report.md)
- [Implementation Readiness Assessment](Implementation_Readiness_Assessment.md)
- [Cross-Cutting Requirements](Cross_Cutting_Requirements.md)
- [Integration Contract Register](Integration_Contract_Register.md)
- [Production Cutover and Go-Live Checklist](Production_Cutover_and_GoLive_Checklist.md)
