# Documentation Control Register

Document Type: Governance Register  
Version: 1.0  
Date: 2026-08-31  
Owner: PMO / Documentation Governance Lead  
Review Frequency: Monthly  
Status: Active

---

# Governance Process

This register governs all documentation artifacts under the PropertyPilot `docs` repository.

Purpose:
- define source-of-truth ownership
- prevent conflicting requirements and design decisions
- standardize document lifecycle and versioning
- support traceability, release review, and production readiness
- ensure that canonical documents remain authoritative

Governance principles:
- Canonical documents are authoritative for implementation and approval decisions.
- Supporting documents must not override a canonical source.
- Legacy and archived documents remain visible only for historical reference.
- Every document must have an owner, status, review cadence, and lifecycle state.
- All updates require review and approval based on document impact and classification.

---

# Document Lifecycle

| Lifecycle State | Meaning | Typical Use |
|---|---|---|
| Draft | In development, not yet approved | New or evolving documents |
| Review | Under cross-functional validation | Pre-approval or policy review |
| Approved | Authoritative and in active use | Canonical and active supporting docs |
| Implemented | Published and actively used in execution | Operational and engineering use |
| Deprecated | Replaced or superseded but retained for reference | Migration or historical use |
| Archived | Read-only, no active use | Superseded or duplicate records |

Lifecycle rules:
- Draft and Review docs are not authoritative for release decisions.
- Approved and Implemented docs are valid for production governance.
- Deprecated docs require replacement mapping and retirement date.
- Archived docs cannot be used for new design or implementation decisions.

---

# Canonical Source Rules

## Canonical Document
A canonical document is the authoritative source for a domain or control. It is used for:
- implementation design
- release approval
- architectural and business decision-making
- traceability and operational governance

Examples:
- PropertyPilot_SRS.md
- OpenAPI_Specification.yaml
- Database_Physical_Model.md
- Technical_Architecture.md
- Security_Design.md
- Traceability_Matrix_v2.md
- Cross_Cutting_Requirements.md

## Supporting Document
A supporting document explains, implements, derives, or supplements a canonical source. It must align with the canonical source and may not redefine policy or architecture.

Examples:
- Feature_Catalog.md
- Service_Catalog.md
- API_Catalog.md
- Screen_Catalog.md
- Screen_Flows.md
- Integration_Contract_Register.md

## Reference Document
A reference document provides additional context, examples, or explanatory detail. It is useful but non-authoritative.

Examples:
- Business_Glossary.md
- BRD.md
- Architecture_Review_Checklist.md
- Data_Retention_Policy.md

## Deprecated Document
A deprecated document is superseded by a canonical or supporting document and is retained for migration or historical audit evidence.

Examples:
- Database_Design.md
- ARCHITECTURE_SUMMARY.md

## Archived Document
An archived document is no longer used and is stored as historical reference only. It must not be linked to active implementation.

Examples:
- stale roadmap versions
- duplicate architecture analyses
- superseded working drafts

## Conflict Resolution Rules
When a conflict exists:
1. Identify the canonical source for the domain.
2. Validate the conflict against the controlling requirement or contract.
3. Update the authoritative document first.
4. Align supporting and reference documents.
5. Mark outdated documents as Deprecated or Archived.
6. Record the resolution in the duplicate or conflict register.

If a conflict remains unresolved:
- no approval is granted
- implementation decisions default to the canonical document
- unresolved conflicts are escalated to governance owners

---

# Documentation Inventory

| Document Name | Document Category | Purpose | Owner | Status | Version | Canonical Source (Yes/No) | Review Frequency | Dependencies | Supersedes | Superseded By |
|---|---|---|---|---|---|---|---|---|---|---|
| PropertyPilot_SRS.md | Business | System requirements baseline | Product Owner | Approved | 1.0 | Yes | Quarterly | Feature_Catalog.md, Customer_Journeys.md, Cross_Cutting_Requirements.md | - | - |
| Feature_Catalog.md | Product | Product feature list | Product Owner | Approved | 1.0 | Yes | Quarterly | PropertyPilot_SRS.md, Service_Catalog.md | - | - |
| Service_Catalog.md | Product | Service catalog | Product Owner | Approved | 1.0 | Yes | Quarterly | Feature_Catalog.md, Customer_Journeys.md | - | - |
| Subscription_Plans.md | Product | Subscription and plan catalog | Product Owner | Approved | 1.0 | Yes | Quarterly | Subscription_Management.md, Pricing_Strategy.md | - | - |
| Pricing_Strategy.md | Product | Pricing and commercial policy | Product Owner | Approved | 1.0 | No | Quarterly | Subscription_Plans.md | - | - |
| Customer_Journeys.md | Product | Customer journey definitions | Product Owner | Approved | 1.0 | Yes | Quarterly | Screen_Catalog.md, Screen_Flows.md | - | - |
| Screen_Catalog.md | UI/UX | Screen inventory | UX Lead | Approved | 1.0 | Yes | Quarterly | Customer_Journeys.md, Screen_Flows.md | - | - |
| Screen_Flows.md | UI/UX | Screen flow and transitions | UX Lead | Approved | 1.0 | Yes | Quarterly | Screen_Catalog.md, Customer_Journeys.md | - | - |
| API_Catalog.md | API | API inventory and ownership | API Lead | Approved | 1.0 | No | Each API change | OpenAPI_Specification.yaml, Traceability_Matrix_v2.md | - | - |
| OpenAPI_Specification.yaml | API | Canonical API contract | API Lead | Approved | 1.0 | Yes | Each API change | API_Catalog.md, Integration_Contract_Register.md | - | - |
| API_Versioning_and_Deprecation_Policy.md | API | API versioning rules | API Lead | Approved | 1.0 | Yes | Quarterly | OpenAPI_Specification.yaml | - | - |
| Webhook_Contract_Catalog.md | API | Webhook contract catalog | Integration Architect | Approved | 1.0 | Yes | Each provider change | Integration_Contract_Register.md | - | - |
| Integration_Contract_Register.md | API | External contract register | Integration Architect | Approved | 1.0 | Yes | Quarterly | Webhook_Contract_Catalog.md, OpenAPI_Specification.yaml | - | - |
| Database_Physical_Model.md | Data | Canonical physical schema | Data Architect | Approved | 1.0 | Yes | Quarterly | Canonical_Data_Model.md, Canonical_Data_Dictionary.md | - | - |
| Canonical_Data_Model.md | Data | Canonical domain model | Data Architect | Approved | 1.0 | Yes | Quarterly | Database_Physical_Model.md | - | - |
| Canonical_Data_Dictionary.md | Data | Canonical field dictionary | Data Architect | Approved | 1.0 | Yes | Each migration | Database_Physical_Model.md | - | - |
| Data_Governance.md | Data | Data governance policy | Data Governance Lead | Approved | 1.0 | No | Quarterly | Canonical_Data_Model.md | - | - |
| Data_Retention_Policy.md | Data | Retention rules | Privacy Officer | Approved | 1.0 | No | Quarterly | Privacy_Rights_Operating_Procedure.md | - | - |
| Database_Design.md | Data | Legacy schema design | Data Architect | Deprecated | 0.9 | No | Quarterly | Database_Physical_Model.md | Database_Design.md | Database_Physical_Model.md |
| Technical_Architecture.md | Architecture | Canonical architecture | Architect | Approved | 1.0 | Yes | Quarterly | Security_Design.md, Cross_Cutting_Requirements.md | - | - |
| Reference_Architecture.md | Architecture | Reference architecture patterns | Architect | Approved | 1.0 | No | Quarterly | Technical_Architecture.md | - | - |
| Architecture_Governance.md | Architecture | Governance and review model | Enterprise Architect | Approved | 1.0 | No | Quarterly | Technical_Architecture.md | - | - |
| Architecture_Roadmap.md | Architecture | roadmap and evolution | Architect | Approved | 1.0 | No | Quarterly | Technical_Architecture.md | - | - |
| ARCHITECTURE_SUMMARY.md | Architecture | Legacy summary | Architect | Deprecated | 0.8 | No | Quarterly | Technical_Architecture.md | ARCHITECTURE_SUMMARY.md | Technical_Architecture.md |
| Security_Design.md | Security | Security architecture and controls | Security Lead | Approved | 1.0 | Yes | Quarterly | Cross_Cutting_Requirements.md, Technical_Architecture.md | - | - |
| Cross_Cutting_Requirements.md | Governance | Cross-cutting requirements | Enterprise Architect | Approved | 1.0 | Yes | Quarterly | PropertyPilot_SRS.md, Technical_Architecture.md | - | - |
| Traceability_Matrix_v2.md | Governance | Release traceability baseline | PMO | Approved | 2.0 | Yes | Quarterly | PropertyPilot_SRS.md, API_Catalog.md, Database_Physical_Model.md | Traceability_Matrix_v1 | - |
| Documentation_Control_Register.md | Governance | Documentation governance register | PMO | Approved | 1.0 | Yes | Monthly | Documentation_Consolidation_Plan.md, Documentation_Conflict_Resolution.md | - | - |
| Documentation_Consolidation_Plan.md | Governance | Consolidation plan | PMO | Approved | 1.0 | No | Quarterly | Documentation_Control_Register.md | - | - |
| Documentation_Conflict_Resolution.md | Governance | Conflict resolution log | PMO | Approved | 1.0 | No | Quarterly | Documentation_Control_Register.md | - | - |
| State_Model_Catalog.md | Governance | Canonical state models | Enterprise Architect | Approved | 1.0 | Yes | Quarterly | Workflow_Engine.md, Feature_Catalog.md | - | - |
| Event_Catalog.md | Governance | Event catalog | Integration Architect | Approved | 1.0 | Yes | Quarterly | Webhook_Contract_Catalog.md, Technical_Architecture.md | - | - |
| Notification_Catalog.md | Governance | Notification catalog | Customer Operations | Approved | 1.0 | Yes | Quarterly | Event_Catalog.md, Customer_Journeys.md | - | - |
| KYC_Operations_Playbook.md | Operations | KYC operations playbook | KYC Operations Lead | Approved | 1.0 | Yes | Quarterly | Privacy_Rights_Operating_Procedure.md, Security_Design.md | - | - |
| Privacy_Rights_Operating_Procedure.md | Operations | Privacy operating procedure | Privacy Officer | Approved | 1.0 | Yes | Quarterly | Security_Design.md, Data_Retention_Policy.md | - | - |
| Operational_Runbooks.md | Operations | Production runbooks | Operations Lead | Approved | 1.0 | Yes | Quarterly | Release_Readiness_Checklist.md, Production_Cutover_Plan.md | - | - |
| Production_Cutover_Plan.md | Operations | Production deploy and cutover plan | Operations Lead | Approved | 1.0 | Yes | Every release | Release_Readiness_Checklist.md, Technical_Architecture.md | - | - |
| Release_Readiness_Checklist.md | Operations | Release gating checklist | PMO | Approved | 1.0 | Yes | Every release | Tracability_Matrix_v2.md, Production_Cutover_Plan.md | - | - |
| Go_Live_Support_Model.md | Operations | Post go-live support model | Operations Lead | Approved | 1.0 | Yes | Every release | Release_Readiness_Checklist.md, Production_Cutover_Plan.md | - | - |
| Production_Cutover_and_GoLive_Checklist.md | Operations | Go-live control checklist | Operations Lead | Approved | 1.0 | Yes | Every release | Release_Readiness_Checklist.md, Production_Cutover_Plan.md | - | - |
| Business_Glossary.md | Business | Common definitions | Business Analyst | Approved | 1.0 | No | Quarterly | BRD.md, Customer_Journeys.md | - | - |
| BRD.md | Business | Business requirements document | Product Owner | Approved | 1.0 | No | Annual | PropertyPilot_SRS.md | - | - |
| Project_Master_Specification.md | Planning | Master project specification | PMO | Approved | 1.0 | Yes | Quarterly | Product_Backlog.md | - | - |
| Product_Backlog.md | Planning | Active product backlog | Product Owner | Approved | 1.0 | Yes | Every sprint | Feature_Catalog.md | - | - |
| MVP_Release_Plan.md | Planning | MVP milestone plan | PMO | Approved | 1.0 | No | Every release | Product_Backlog.md | - | - |
| Release_Management.md | Planning | Release governance process | PMO | Approved | 1.0 | No | Every release | Production_Cutover_Plan.md | - | - |
| Developer_Guide.md | Implementation | Engineering implementation guide | Engineering Lead | Approved | 1.0 | No | Quarterly | Technical_Architecture.md, OpenAPI_Specification.yaml | - | - |
| Testing_Quality_Assurance.md | Implementation | Quality assurance policy | QA Lead | Approved | 1.0 | Yes | Every release | PropertyPilot_SRS.md, OpenAPI_Specification.yaml | - | - |
| Schema_Completion_Backlog.md | Data | Data model completion backlog | Data Architect | Approved | 1.0 | No | Monthly | Database_Physical_Model.md, Canonical_Data_Dictionary.md | - | - |
| OpenAPI_Gap_Backlog.md | API | API contract backlog | API Lead | Approved | 1.0 | No | Monthly | OpenAPI_Specification.yaml, API_Catalog.md | - | - |
| Construction_Project_Lifecycle_Specification.md | Product | Construction lifecycle spec | Product Owner | Approved | 1.0 | No | Quarterly | Feature_Catalog.md, State_Model_Catalog.md | - | - |
| Field_Operations_Model.md | Operations | Field operations model | Operations Lead | Approved | 1.0 | No | Quarterly | Service_Catalog.md, Agent_Management.md | - | - |
| Subscription_Management.md | Product | Subscription operations model | Product Owner | Approved | 1.0 | No | Quarterly | Subscription_Plans.md | - | - |
| MVP_Scope_Baseline.md | Product | Official MVP boundary | Product Owner | Approved | 1.0 | Yes | Quarterly | PropertyPilot_SRS.md, Feature_Catalog.md | - | - |
| Documentation_Control_Register.md | Governance | This register | PMO | Approved | 1.0 | Yes | Monthly | All documents | - | - |

---

# Duplicate Document Register

| Document A | Document B | Conflict Type | Recommended Canonical Source | Resolution Action | Target Date |
|---|---|---|---|---|---|
| Architecture_Governance.md | Technical_Architecture.md | overlapping architecture guidance | Technical_Architecture.md | Archive or merge governance sections | 2026-09-30 |
| Database_Design.md | Database_Physical_Model.md | schema drift | Database_Physical_Model.md | Mark legacy and archive | 2026-09-15 |
| ARCHITECTURE_SUMMARY.md | Technical_Architecture.md | duplicated architecture descriptions | Technical_Architecture.md | Archive summary and update references | 2026-09-15 |
| API_Catalog.md | OpenAPI_Specification.yaml | contract drift | OpenAPI_Specification.yaml | synchronize catalog and add deprecation labels | 2026-09-30 |
| Documentation_Consolidation_Plan.md | Documentation_Control_Register.md | overlapping governance responsibilities | Documentation_Control_Register.md | merge active governance content | 2026-09-30 |
| Release_Readiness_Checklist.md | Production_Cutover_and_GoLive_Checklist.md | duplicated release gating coverage | Production_Cutover_and_GoLive_Checklist.md | keep as operational checklist; align with release checklist | 2026-09-15 |
| Product_Backlog.md | MVP_Release_Plan.md | milestone and scope overlap | Product_Backlog.md | align planning artifacts and remove stale dates | 2026-09-30 |
| State_Model_Catalog.md | Workflow_Engine.md | duplicate lifecycle states | State_Model_Catalog.md | archive workflow engine summary if redundant | 2026-10-15 |
| Notification_Catalog.md | Event_Catalog.md | overlapping event and notification semantics | Event_Catalog.md + Notification_Catalog.md | define clear separation of event vs notification responsibility | 2026-10-15 |

---

# Deprecated Document Register

| Document | Reason | Replacement | Retention Period |
|---|---|---|---|
| Database_Design.md | Schema drift and superseded physical model | Database_Physical_Model.md | 12 months |
| ARCHITECTURE_SUMMARY.md | Duplicate architecture summary | Technical_Architecture.md | 6 months |
| legacy roadmap drafts | Outdated planning assumptions | Architecture_Roadmap.md / Project_Master_Specification.md | 6 months |
| stale API drafts | Duplicate route set and version drift | OpenAPI_Specification.yaml | 12 months |
| older release checklist versions | Overlapping process ownership | Release_Readiness_Checklist.md | 6 months |

---

# Review Schedule

## Monthly
- Documentation_Control_Register.md
- Traceability_Matrix_v2.md
- OpenAPI_Gap_Backlog.md
- Schema_Completion_Backlog.md
- release and issue governance documents

## Quarterly
- architecture
- business requirements
- product and feature docs
- API and database docs
- security and privacy docs
- operational and support docs

## Annual
- strategic architecture and roadmap documents
- major governance frameworks
- policy and control documents
- long-term planning docs

## Trigger-Based Reviews
- API contract change
- schema migration
- production release
- security event
- privacy event
- major requirement change
- architecture decision change
- incident postmortem
- legal or regulatory impact

---

# Change Management Process

## Request
A change request can be initiated by:
- product owner
- engineering lead
- security lead
- operations lead
- business owner
- governance lead

Every request must include:
- document name
- section impacted
- reason for change
- business or technical impact
- proposed owner
- target date

## Review
Review includes:
- impact analysis
- dependency check
- canonical source confirmation
- conflict review
- approval path validation

## Approval
Approval requires:
- document owner approval
- domain owner review where applicable
- governance review for canonical and high-impact documents
- record of decision and date

## Publication
Approved changes must be:
- applied to the approved source
- reflected in versioning metadata
- published to the repository
- linked to release or review records where relevant

## Communication
The update must be communicated to:
- relevant stakeholders
- dependent document owners
- release leads when the change impacts production readiness or architecture

---

# Versioning Standards

## Major Version
Use for:
- structural change
- requirement or architecture re-baselining
- authoritative source replacement
- significant scope or governance reset

Example:
- 1.0 to 2.0

## Minor Version
Use for:
- material update
- new sections
- changed ownership or review cadence
- non-breaking content additions

Example:
- 1.1 to 1.2

## Patch Version
Use for:
- typo fixes
- minor formatting corrections
- clarifying edits not affecting meaning

Example:
- 1.0.1

## Document Naming Standards
- use clear, descriptive names
- avoid duplicate titles or ambiguous naming
- prefer domain-specific naming patterns
- include version only in formal controlled artifacts when needed
- use consistent casing and separators
- maintain a relationship between canonical name and supporting aliases

Examples:
- PropertyPilot_SRS.md
- OpenAPI_Specification.yaml
- Database_Physical_Model.md
- Release_Readiness_Checklist.md

---

# Ownership Matrix

| Domain | Business Owner | Product Owner | Architect | Engineering Lead | Operations Lead | Security Lead | Documentation Owner |
|---|---|---|---|---|---|---|---|
| Business | Primary | Primary | Support | Support | Support | Support | PMO |
| Product | Support | Primary | Support | Support | Support | Support | Product Owner |
| Architecture | Support | Support | Primary | Support | Support | Support | Architect |
| Data | Support | Support | Primary | Support | Support | Support | Data Architect |
| API | Support | Support | Support | Primary | Support | Support | API Lead |
| UI/UX | Support | Primary | Support | Support | Support | Support | UX Lead |
| Planning | Primary | Primary | Support | Support | Support | Support | PMO |
| Implementation | Support | Support | Support | Primary | Support | Support | Engineering Lead |
| Operations | Support | Support | Support | Support | Primary | Support | Operations Lead |
| Security | Support | Support | Support | Support | Support | Primary | Security Lead |
| Governance | Support | Support | Support | Support | Support | Support | PMO |

---

# Documentation Health Metrics

| Metric | Current Target | Current Status |
|---|---|---|
| Coverage % | 95% | 88% |
| Traceability % | 90% | 82% |
| Duplicate Count | 0 active duplicates | 6 identified |
| Conflict Count | 0 active unresolved conflicts | 5 unresolved / in remediation |
| Review Compliance % | 95% | 80% |

Health measurement approach:
- Coverage = % of documents with owner, lifecycle state, version, and review schedule
- Traceability = % of active documents linked to canonical source and dependency map
- Duplicate Count = active duplicate or overlapping documents
- Conflict Count = unresolved contradictions across canonical and supporting docs
- Review Compliance = % of required quarterly/monthly reviews completed on time

---

# Documentation Roadmap

## Phase 1 Cleanup
- classify all documents
- identify duplicates and conflicts
- assign canonical source owners
- archive outdated and superseded docs
- update inventory and ownership matrix

## Phase 2 Consolidation
- align supporting docs to canonical sources
- standardize naming, versioning, and lifecycle status
- complete traceability mappings
- finalize release governance and review cadence

## Phase 3 Governance Maturity
- automate review tracking
- integrate documentation control into release gates
- enforce canonical source policy in engineering and product governance
- maintain continuous documentation quality and auditability

---

# Final Canonical Source Map

| Major Area | Single Authoritative Source Document |
|---|---|
| Business | PropertyPilot_SRS.md |
| Product | Feature_Catalog.md |
| Architecture | Technical_Architecture.md |
| Data | Database_Physical_Model.md |
| API | OpenAPI_Specification.yaml |
| UI/UX | Screen_Catalog.md |
| Planning | Product_Backlog.md |
| Implementation | Developer_Guide.md |
| Operations | Operational_Runbooks.md |

Notes:
- Business and product decisions are anchored in the requirements and feature catalog.
- Architecture and data controls are anchored in technical architecture and physical schema.
- API contracts are anchored in the canonical OpenAPI document.
- UI/UX is anchored in screen catalog and flows.
- Operations is anchored in runbooks and cutover models.
- All supporting documents must reference and align with these single sources.

---

# Summary

This Documentation Control Register defines the governance baseline for all PropertyPilot documents under `docs`. It provides the structure needed to maintain source-of-truth integrity, reduce duplicate and conflicting artifacts, improve traceability, and ensure that documentation remains aligned with architecture, product, API, security, and operational reality.

The register should be treated as the authoritative control document for documentation ownership, lifecycle, review, versioning, and canonical-source assignment across the entire project.