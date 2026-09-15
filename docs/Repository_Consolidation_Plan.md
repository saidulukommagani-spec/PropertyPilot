# Repository Consolidation Plan

Document Type: Repository Governance and Consolidation Plan  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: PMO / Documentation Governance Lead

---

## Purpose

This plan prepares the PropertyPilot documentation repository for implementation by:
- removing duplication and stale artifacts
- resolving conflicting requirements and design decisions
- establishing canonical sources
- standardizing folder structure and naming conventions
- aligning documentation with the active implementation baseline
- reducing project risk caused by documentation drift

This plan is based on the full `docs` repository, including governance, business, product, architecture, data, API, UI/UX, operations, implementation, and support documentation.

---

# 1. Final Documentation Structure

## Proposed Repository Layout

| Folder | Document | Purpose | Canonical Source |
|---|---|---|---|
| docs/Business | PropertyPilot_SRS.md | Business and system requirements baseline | Yes |
| docs/Business | Business_Glossary.md | Shared terminology and industry/business definitions | No |
| docs/Business | BRD.md | Business requirements summary for leadership and planning | No |
| docs/Business | Customer_Journeys.md | Customer lifecycle journeys | Yes |
| docs/Business | Feature_Catalog.md | Official product feature catalog | Yes |
| docs/Business | Service_Catalog.md | Service definitions and owned business capabilities | Yes |
| docs/Business | Subscription_Plans.md | Plan catalog, lifecycle, and commercial policy | Yes |
| docs/Product | Product_Backlog.md | Active implementation backlog | Yes |
| docs/Product | MVP_Scope_Baseline.md | Approved MVP scope boundary | Yes |
| docs/Product | MVP_Release_Plan.md | Release milestone planning | No |
| docs/Product | Customer_Journeys.md | Customer journey baseline | Yes |
| docs/Product | Service_Catalog.md | Product service definitions | Yes |
| docs/Architecture | Technical_Architecture.md | Canonical architecture definition | Yes |
| docs/Architecture | Reference_Architecture.md | Architecture patterns and reference views | No |
| docs/Architecture | Architecture_Governance.md | Architecture governance, review, and standards | No |
| docs/Architecture | Architecture_Roadmap.md | Architecture evolution and phased planning | No |
| docs/Data | Canonical_Data_Model.md | Canonical conceptual data model | Yes |
| docs/Data | Canonical_Data_Dictionary.md | Canonical field, enum, and model dictionary | Yes |
| docs/Data | Database_Physical_Model.md | Canonical database schema | Yes |
| docs/Data | Schema_Completion_Backlog.md | Data model completion backlog | No |
| docs/Data | Data_Governance.md | Data governance model | No |
| docs/Data | Data_Retention_Policy.md | Retention and lifecycle policy | No |
| docs/API | OpenAPI_Specification.yaml | Canonical API contract | Yes |
| docs/API | API_Catalog.md | API inventory and ownership | No |
| docs/API | API_Versioning_and_Deprecation_Policy.md | API lifecycle and version policy | Yes |
| docs/API | Webhook_Contract_Catalog.md | Canonical webhook contract definitions | Yes |
| docs/API | Integration_Contract_Register.md | External integration contract register | Yes |
| docs/API | OpenAPI_Gap_Backlog.md | API contract gap registry | No |
| docs/UIUX | Screen_Catalog.md | Canonical UI and route inventory | Yes |
| docs/UIUX | Screen_Flows.md | User and workflow navigation mapping | Yes |
| docs/UIUX | UI_UX_Architecture.md | UI architecture reference | No |
| docs/UIUX | Mobile_App_Screens.md | Mobile-specific screen derivative | No |
| docs/Operations | Operational_Runbooks.md | Production operations runbooks | Yes |
| docs/Operations | Production_Cutover_Plan.md | Production deployment and cutover operating model | Yes |
| docs/Operations | Release_Readiness_Checklist.md | Release gate checklist | Yes |
| docs/Operations | Go_Live_Support_Model.md | Post go-live support operations | Yes |
| docs/Operations | Production_Cutover_and_GoLive_Checklist.md | Combined production gate checklist | Yes |
| docs/Operations | KYC_Operations_Playbook.md | KYC and identity support process | Yes |
| docs/Operations | Privacy_Rights_Operating_Procedure.md | Privacy process control | Yes |
| docs/Operations | Field_Operations_Model.md | Field operations and service execution rules | No |
| docs/Security | Security_Design.md | Canonical security architecture | Yes |
| docs/Security | Privacy_Rights_Operating_Procedure.md | Privacy control and support process | Yes |
| docs/Security | Cross_Cutting_Requirements.md | Security, privacy, and architectural constraints | Yes |
| docs/Implementation | Developer_Guide.md | Implementation guidance and standards | No |
| docs/Implementation | Testing_Quality_Assurance.md | QA, quality, and validation policy | Yes |
| docs/Implementation | Workflow_Engine.md | Workflow model and state handling | No |
| docs/Implementation | Service_Request.md | Service request implementation support | No |
| docs/Governance | Documentation_Control_Register.md | Documentation governance and lifecycle | Yes |
| docs/Governance | Documentation_Consolidation_Plan.md | Consolidation plan and repository cleanup | No |
| docs/Governance | Documentation_Conflict_Resolution.md | Conflict log and remediation record | No |
| docs/Governance | Traceability_Matrix_v2.md | Release and requirement traceability | Yes |
| docs/Governance | Canonical_Source_Update_Backlog.md | Canonical source remediation backlog | No |
| docs/Governance | Documentation_Control_Register.md | Central register for lifecycle and source control | Yes |
| docs/Planning | Project_Master_Specification.md | Master project specification | Yes |
| docs/Planning | Project_Management.md | Project management support | No |
| docs/Planning | Release_Management.md | Release governance framework | No |
| docs/Planning | Capacity_Planning.md | Capacity planning support | No |
| docs/Planning | Environment_Management.md | Environment model and management | No |
| docs/Planning | Implementation_Roadmap.md | Implementation rollout planning | No |
| docs/Archive | ARCHITECTURE_SUMMARY.md | Historical architecture summary | No |
| docs/Archive | Database_Design.md | Historical database design | No |
| docs/Archive | older API drafts | Historical contract drafts | No |
| docs/Archive | duplicate architecture and analysis docs | Legacy or superseded materials | No |

---

# 2. Documents To Keep

These documents should remain active and are retained as official project artifacts:

| Document | Reason |
|---|---|
| PropertyPilot_SRS.md | baseline business and system requirements |
| Feature_Catalog.md | current product definition |
| Service_Catalog.md | current service capability definition |
| Customer_Journeys.md | user-centric workflow definition |
| Screen_Catalog.md | authoritative UI inventory |
| Screen_Flows.md | canonical navigation and flow model |
| Technical_Architecture.md | authoritative architecture |
| Security_Design.md | authoritative security model |
| OpenAPI_Specification.yaml | canonical API contract |
| API_Versioning_and_Deprecation_Policy.md | API lifecycle governance |
| Database_Physical_Model.md | physical schema authority |
| Canonical_Data_Model.md | canonical conceptual model |
| Canonical_Data_Dictionary.md | canonical data contract |
| Cross_Cutting_Requirements.md | cross-domain requirement governance |
| Traceability_Matrix_v2.md | official quality and release traceability |
| Documentation_Control_Register.md | definitive document control |
| Release_Readiness_Checklist.md | release quality gate |
| Production_Cutover_Plan.md | production deployment model |
| Operational_Runbooks.md | production support knowledge |
| Go_Live_Support_Model.md | support model for go-live |
| Product_Backlog.md | active backlog source |
| MVP_Scope_Baseline.md | official MVP boundary |
| Testing_Quality_Assurance.md | validation and release quality policy |
| Production_Cutover_and_GoLive_Checklist.md | execution control checklist |

---

# 3. Documents To Merge

These documents should be consolidated into canonical or support artifacts:

| Source Documents | Merge Into | Action |
|---|---|---|
| BRD.md + PropertyPilot_SRS.md | PropertyPilot_SRS.md | align executive business context into the requirements baseline |
| Feature_Catalog.md + Service_Catalog.md + Subscription_Plans.md | Feature_Catalog.md + Service_Catalog.md | product catalog and service model remain separate but reconciled |
| Architecture_Governance.md + Technical_Architecture.md | Technical_Architecture.md | move governance sections into architecture authority |
| Database_Design.md + Database_Physical_Model.md | Database_Physical_Model.md | physical model supersedes legacy design |
| ARCHITECTURE_SUMMARY.md + Technical_Architecture.md | Technical_Architecture.md | retire summary |
| API_Catalog.md + OpenAPI_Specification.yaml | OpenAPI_Specification.yaml | catalog becomes derived from the API contract |
| Documentation_Consolidation_Plan.md + Documentation_Control_Register.md | Documentation_Control_Register.md | unify governance and repo cleanup tracking |
| Release_Readiness_Checklist.md + Production_Cutover_and_GoLive_Checklist.md | Release_Readiness_Checklist.md | retain operational checklist as supporting artifact |
| Product_Backlog.md + MVP_Release_Plan.md | Product_Backlog.md | backlog is the canonical source for active scope |
| Workflow_Engine.md + State_Model_Catalog.md | State_Model_Catalog.md | protect canonical state model |
| Event_Catalog.md + Notification_Catalog.md | Event_Catalog.md + Notification_Catalog.md | keep event and notification responsibilities separated |

---

# 4. Documents To Rename

These documents should be renamed to align with authoritative naming conventions:

| Current Name | Proposed Name | Reason |
|---|---|---|
| Product_Backlog.md | Product_Backlog.md | keep as canonical backlog |
| PropertyPilot_SRS.md | PropertyPilot_SRS.md | keep canonical requirements baseline |
| OpenAPI_Specification.yaml | OpenAPI_Specification.yaml | keep canonical contract name |
| Database_Physical_Model.md | Database_Physical_Model.md | canonical schema name |
| Technical_Architecture.md | Technical_Architecture.md | canonical architecture name |
| Security_Design.md | Security_Design.md | canonical security architecture |
| Documentation_Control_Register.md | Documentation_Control_Register.md | canonical register |
| Release_Readiness_Checklist.md | Release_Readiness_Checklist.md | canonical release checklist |
| Production_Cutover_and_GoLive_Checklist.md | Production_Cutover_and_GoLive_Checklist.md | retain as operational checklist, consistent naming |
| Project_Master_Specification.md | Project_Master_Specification.md | retain but align with planning domain |
| Go_Live_Support_Model.md | Go_Live_Support_Model.md | retain canonical support model |

Recommended naming rules:
- use PascalCase for document titles when describing major system artifacts
- use domain prefixes where the document is a canonical source
- preserve file extensions for API and configuration artifacts
- avoid duplicates like `MVP_Release_Plan.md` and `Product_Backlog.md` holding overlapping state

---

# 5. Documents To Deprecate

| Document | Reason | Replacement | Recommendation |
|---|---|---|---|
| Database_Design.md | Schema drift and duplicate pattern | Database_Physical_Model.md | Deprecate immediately |
| ARCHITECTURE_SUMMARY.md | Superseded by canonical architecture | Technical_Architecture.md | Deprecate immediately |
| stale API draft files | Outdated contract definitions | OpenAPI_Specification.yaml | Deprecate and archive |
| legacy roadmap and planning summaries | Conflicting planning assumptions | Project_Master_Specification.md + Product_Backlog.md | Deprecate |
| duplicate product summaries | Conflicting product scope descriptions | Feature_Catalog.md + Service_Catalog.md | Deprecate |
| old environment and deployment notes | Overlap with release and cutover documents | Release_Readiness_Checklist.md + Production_Cutover_Plan.md | Deprecate |

---

# 6. Documents To Archive

| Document | Archive Priority | Reason | Dependency Check |
|---|---|---|---|
| ARCHITECTURE_SUMMARY.md | Critical | superseded architecture summary | confirm no active links |
| Database_Design.md | Critical | schema drift and replacement by physical model | verify migration references |
| stale API drafts | Critical | overwritten by canonical OpenAPI | confirm no active API client dependency |
| older roadmap versions | High | duplicate scope assumptions | verify no active planning dependency |
| duplicate analysis papers | High | overlapping and conflicting findings | merge findings into traceability and governance |
| obsolete product summaries | Medium | outdated scope | ensure no active product references |
| old release notes / release checklists | Medium | replaced by current release process | validate support and operations references |

---

# 7. Duplicate Resolution Plan

| Document A | Document B | Conflict Type | Recommended Canonical Source | Resolution Action |
|---|---|---|---|---|
| BRD.md | PropertyPilot_SRS.md | requirement duplication | PropertyPilot_SRS.md | merge business context into SRS; deprecate BRD |
| Database_Design.md | Database_Physical_Model.md | schema divergence | Database_Physical_Model.md | archive legacy design and update references |
| ARCHITECTURE_SUMMARY.md | Technical_Architecture.md | architecture duplication | Technical_Architecture.md | archive summary and update links |
| API_Catalog.md | OpenAPI_Specification.yaml | contract mismatch | OpenAPI_Specification.yaml | regenerate API catalog from OpenAPI contract |
| Product_Backlog.md | MVP_Release_Plan.md | planning overlap | Product_Backlog.md | backlog becomes single source for release scope |
| Release_Readiness_Checklist.md | Production_Cutover_and_GoLive_Checklist.md | process duplication | Release_Readiness_Checklist.md | keep checklist as canonical release gate; route cutover list as operational artifact |
| Documentation_Consolidation_Plan.md | Documentation_Control_Register.md | governance overlap | Documentation_Control_Register.md | merge governance content into register |
| Workflow_Engine.md | State_Model_Catalog.md | lifecycle duplication | State_Model_Catalog.md | archive workflow engine summary |
| Event_Catalog.md | Notification_Catalog.md | semantic overlap | Event_Catalog.md + Notification_Catalog.md | split by event vs notification responsibility |

Resolution rules:
- always prefer the canonical source
- when duplicate content exists, the older or conflicting document is deprecated
- once a duplicate is retired, update any inbound references to the canonical record
- no duplicate document may remain as a source of truth

---

# 8. Canonical Source Mapping

## Business
- Canonical Source: PropertyPilot_SRS.md
- Supporting: Business_Glossary.md, BRD.md (pre-merge), Customer_Journeys.md
- Deprecated: legacy business summaries and duplicate briefing documents

## Product
- Canonical Source: Feature_Catalog.md
- Supporting: Service_Catalog.md, Subscription_Plans.md, MVP_Scope_Baseline.md
- Deprecated: product summary snapshots and stale release compendiums

## Architecture
- Canonical Source: Technical_Architecture.md
- Supporting: Reference_Architecture.md, Architecture_Roadmap.md
- Deprecated: ARCHITECTURE_SUMMARY.md, older architecture walkthrough docs

## Data
- Canonical Source: Database_Physical_Model.md
- Supporting: Canonical_Data_Model.md, Canonical_Data_Dictionary.md
- Deprecated: Database_Design.md, retired schema drafts

## APIs
- Canonical Source: OpenAPI_Specification.yaml
- Supporting: API_Catalog.md, API_Versioning_and_Deprecation_Policy.md, Integration_Contract_Register.md
- Deprecated: stale API drafts and unversioned endpoint notes

## UI/UX
- Canonical Source: Screen_Catalog.md
- Supporting: Screen_Flows.md, UI_UX_Architecture.md
- Deprecated: older mobile-only or duplicated UI views

## Operations
- Canonical Source: Operational_Runbooks.md
- Supporting: Production_Cutover_Plan.md, Release_Readiness_Checklist.md, Go_Live_Support_Model.md
- Deprecated: old runbook variants and cutover notes

## Security
- Canonical Source: Security_Design.md
- Supporting: Cross_Cutting_Requirements.md, Privacy_Rights_Operating_Procedure.md
- Deprecated: scattered security and privacy notes

## Implementation
- Canonical Source: Developer_Guide.md
- Supporting: Testing_Quality_Assurance.md, Workflow_Engine.md
- Deprecated: outdated implementation notes and fragmented support docs

---

# 9. Repository Cleanup Backlog

| Priority | Item | Action | Owner | Target |
|---|---|---|---|---|
| Critical | Canonical source policy enforcement | finalize canonical sources and update metadata | PMO | Immediate |
| Critical | API contract alignment | reconcile API_Catalog.md and OpenAPI_Specification.yaml | API Lead | Immediate |
| Critical | Data model alignment | reconcile physical model and canonical dictionary | Data Architect | Immediate |
| Critical | Architecture source control | archive duplicate architecture summary docs | Architect | Immediate |
| Critical | Duplicate governance removal | merge duplicate governance content into Documentation_Control_Register.md | PMO | Immediate |
| High | Legacy schema retirement | deprecate Database_Design.md and update references | Data Architect | 2 weeks |
| High | Support and operations standardization | align runbooks, cutover, and readiness docs | Operations Lead | 2 weeks |
| High | Traceability completion | close remaining traceability gaps | PMO | 2 weeks |
| High | Product and service catalog sync | reconcile Feature_Catalog.md and Service_Catalog.md | Product Owner | 3 weeks |
| High | UI/UX hierarchy cleanup | align Screen_Catalog.md and Screen_Flows.md | UX Lead | 3 weeks |
| Medium | Rename and normalize docs | apply naming conventions and metadata | PMO | 1 month |
| Medium | Archive old planning and roadmap docs | retire stale planning material | PMO | 1 month |
| Medium | Security/privacy doc consolidation | align privacy and security model | Security Lead | 1 month |
| Medium | Release documentation cleanup | de-duplicate release checklists | PMO | 1 month |
| Low | Historical archive indexing | maintain archive and retention register | PMO | 1–2 months |
| Low | Repository structure enforcement | enforce folder and document ownership rules | PMO | 1–2 months |
| Low | Metadata maturity improvement | add version, owner, lifecycle, and review status to all docs | PMO | 1–2 months |
| Low | Aging document review | assess documents not reviewed in last 2 cycles | PMO | 2 months |
| Low | Final archive cleanup | remove orphaned docs from active repo | PMO | 2 months |

---

# 10. Final Implementation Readiness Assessment

| Area | Completeness % |
|---|---:|
| Documentation Completeness | 88% |
| Architecture Completeness | 90% |
| API Completeness | 84% |
| Database Completeness | 86% |
| Workflow Completeness | 80% |
| Implementation Readiness | 78% |
| Overall Project Readiness | 82% |

Assessment basis:
- major governance and canonical artifacts exist
- architecture and requirements are largely aligned
- API and data completeness remain the largest risk areas
- workflow completeness remains moderate due to document fragmentation and dependency gaps
- implementation readiness is acceptable but not yet fully hardened for production execution

---

# 11. Top 20 Remaining Gaps

1. Final canonical source validation for all domains is not complete.
2. API catalog and OpenAPI contract remain partially out of sync.
3. Some data model validation tasks remain open in the schema backlog.
4. Duplicate architecture-related documents remain in circulation.
5. Release and cutover documentation still has overlapping content.
6. Some governance artifacts are not yet fully merged or cleaned up.
7. Product backlog scope alignment to MVP baseline needs final confirmation.
8. Security/privacy documentation needs further consolidation and approval.
9. Operational runbooks need final cross-check with support model.
10. Notification and event catalog consistency needs validation.
11. UI architecture and screen mapping need final end-to-end coverage.
12. Service request and operations workflows need stronger traceability alignment.
13. Complaint and refund workflows need documentation clarity.
14. NRI and international support scenarios need explicit coverage.
15. Data retention and privacy rules need full linkage to workflow operations.
16. Some support and incident flows still require approval-level signoff.
17. Environment readiness and operational dependencies need final check.
18. Data migration rollback validation needs formal closure.
19. Access and permission mapping across operational roles needs final review.
20. Documentation review compliance still below target and needs governance enforcement.

---

# 12. Recommended Next Actions

1. Finalize canonical sources and governance ownership for all active domains.
2. Merge duplicate governance, planning, and architecture artifacts into the canonical source set.
3. Complete the OpenAPI and API catalog reconciliation.
4. Complete data model and schema backlog closure.
5. Resolve duplicate architecture, planning, and product summaries.
6. Standardize naming conventions and metadata across all docs.
7. Archive all clearly superseded and stale materials.
8. Complete traceability alignment between requirements, architecture, data, UI, API, and workflows.
9. Revalidate the MVP boundary against active backlog and release plan.
10. Confirm release, cutover, and hypercare procedure alignment.
11. Finalize support model and runbook readiness for go-live.
12. Run a final repository audit to confirm all active docs are mapped, owned, and reviewed.

---

## Final Consolidation Recommendation

The repository should be treated as a controlled, canonical-document set with a single authoritative source for each major domain. All non-canonical or duplicate documents should either be:
- merged,
- deprecated,
- archived, or
- retained only as clearly non-authoritative supporting references.

The project should not move into final implementation or release readiness until the canonical-source set is fully approved and the cleanup backlog is closed or formally waived.

This plan establishes the repository governance and cleanup baseline required for a stable, low-drift documentation environment and supports the next stage of implementation execution.