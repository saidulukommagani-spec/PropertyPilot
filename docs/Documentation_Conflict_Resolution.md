# Documentation Conflict Resolution

Document Type: Governance and Documentation Alignment  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review

---

## Purpose

This document resolves conflicts, overlaps, duplicate sources of truth, and contradictory requirements across the PropertyPilot documentation set.

The objective is to establish a single authoritative baseline for:
- requirements
- architecture
- APIs
- data contracts
- physical schema
- operational controls
- implementation execution

This document is intended to reduce ambiguity, avoid implementation drift, and ensure all workstreams align to the approved canonical documents.

---

## Scope

This document applies to all PropertyPilot project documentation, including but not limited to:
- SRS
- architecture documents
- API contracts
- data dictionary and schema files
- traceability matrix
- gap analysis reports
- operational requirements
- security and compliance documents

This resolution applies across:
- product requirements
- implementation design
- integration design
- database design
- API behavior
- observability and operations
- governance controls

---

## Conflict Resolution Principles

The following rules govern all documentation conflicts:

- One canonical source per subject
- OpenAPI is API source of truth
- Canonical Data Model is conceptual source of truth
- Database Physical Model is physical source of truth
- SRS is requirements source of truth
- Cross-Cutting Requirements govern shared rules
- Architecture documents explain implementation approach, not product requirement decisions
- Legacy or duplicate documents are non-authoritative unless explicitly marked as historical
- Any contradiction must be resolved in favor of the canonical source
- Final decisions must be recorded in this document and reflected in source documents

---

# Identified Conflicts

| ID | Area | Documents | Conflict | Recommended Resolution | Canonical Source |
|---|---|---|---|---|---|
| DOC-001 | API Versioning | API_Contract_Governance.md, OpenAPI_Specification.yaml, API_Catalog.md | API paths vary between /api/v1, /v1, and unversioned endpoints | Standardize all canonical routes to /api/v1 and require versioned URLs for all production endpoints. Unversioned endpoints are prohibited outside legacy compatibility windows. | OpenAPI_Specification.yaml |
| DOC-002 | Authentication | SRS, API_Contract_Governance.md, Security_Design.md | OTP-only MVP assumptions conflict with password/API authentication requirements | Use OTP for customer/mobile verification flows; standard JWT-based auth for API access; password-first flows only where explicit product requirement exists. | Security_Design.md |
| DOC-003 | RTO/RPO | Technical_Architecture.md, NFR_Requirements.md, Cross_Cutting_Requirements.md | Recovery objectives differ across architecture and cross-cutting docs | Use Cross-Cutting Requirements as governing fallback for RTO/RPO values; architecture must be aligned to these values or document a justified exception. | Cross_Cutting_Requirements.md |
| DOC-004 | Subscription Lifecycle | Subscription docs, API docs, domain documents | Subscription lifecycle states are inconsistent across product, API, and database definitions | Use a single subscription state model with canonical states: draft, active, paused, cancelled, expired. All downstream docs must map to this list. | Canonical_Data_Model.md |
| DOC-005 | Service Request Status | Service domain docs, workflow docs, UI docs | Service request statuses differ between workflows and system models | Standardize on canonical service request states: draft, open, assigned, in_progress, resolved, closed, cancelled, escalated. UI and API must map to the canonical list. | Canonical_Data_Model.md |
| DOC-006 | Property Ownership Model | Property docs, database schema, requirements | Ownership and property administration model is not consistently represented | Use a single ownership model based on property ownership type, tenant relationship, and legal entity references. Source-of-truth remains the canonical data model and physical database schema. | Canonical_Data_Model.md |
| DOC-007 | Pricing Engine Quote Logic | Pricing docs, API docs, domain docs | Quote logic varies between workflows, price definitions, and subscription behavior | Implement one canonical pricing and quotation policy. Product rules, API output, and data model must use the same quote schema and lifecycle semantics. | Canonical_Data_Model.md |
| DOC-008 | Duplicate Project Master Specs | multiple planning or summary docs | More than one document claims to be the master specification | Consolidate all master specification content into the canonical SRS and approved architecture set. Other spec files are treated as historical or supplemental only. | PropertyPilot_SRS.md |
| DOC-009 | Duplicate Service Catalogs | service catalog docs, domain design docs, API docs | Service definitions appear in multiple places with overlapping or contradictory names | Maintain one canonical service catalog in the canonical data model and API definitions; any duplicate catalog should be archived or merged. | Canonical_Data_Model.md |
| DOC-010 | Duplicate Database Design Documents | Database_Physical_Model.md, schema docs, separate design notes | Multiple schema definitions are treated as authoritative | Database_Physical_Model.md is the physical source of truth. Other schema drafts are superseded unless explicitly preserved as historical versions. | Database_Physical_Model.md |
| DOC-011 | Duplicate Gap Analysis Documents | PropertyPilot_Gap_Analysis.md, Architecture_Gap_Analysis.md, backlog docs | Gap analysis results and scope differ across documents | Use a single lineage of gap analysis and backlog tracking. Any summary docs must reconcile to the same issue list and baseline. | Traceability_Matrix.md |
| DOC-012 | Duplicate Architecture Summary Documents | Technical_Architecture.md and other architecture summary docs | Different architecture summaries imply divergent design decisions | The Technical_Architecture.md remains authoritative. Supplementary summaries are informational only and cannot override architecture decisions. | Technical_Architecture.md |
| DOC-013 | Subscription Management Duplicate State Models | subscription state docs, API docs, database models | The state model is duplicated across product and engineering docs | Adopt a single subscription state and transition model, with all documents deriving from the canonical model. | Canonical_Data_Model.md |
| DOC-014 | Security Design Repeated Sections | Security_Design.md, API_Contract_Governance.md, Webhook_Contract_Catalog.md | Security requirements appear repeatedly with partial mismatches | Security requirements should be consolidated in Security_Design.md; API and webhook docs should reference and not redefine core security policy. | Security_Design.md |

---

# Source of Truth Register

| Domain | Canonical Document | Supporting Documents | Deprecated Documents |
|---|---|---|---|
| Business | PropertyPilot_SRS.md | Cross_Cutting_Requirements.md, Traceability_Matrix.md | legacy requirement summaries, draft business notes |
| Product | PropertyPilot_SRS.md | Product backlog notes, UI specs, workflow docs | duplicate product requirement files |
| Architecture | Technical_Architecture.md | Security_Design.md, Integration_Contract_Register.md, Webhook_Contract_Catalog.md | architecture summaries and duplicated design notes |
| Data | Canonical_Data_Model.md | Canonical_Data_Dictionary.md, Database_Physical_Model.md | duplicate schema drafts, partial data models |
| API | OpenAPI_Specification.yaml | API_Contract_Governance.md, API_Catalog.md | unversioned API drafts, stale contract docs |
| Security | Security_Design.md | API_Contract_Governance.md, Webhook_Contract_Catalog.md, Integration_Contract_Register.md | repeated security fragments across multiple docs |
| Payments | Canonical_Data_Model.md | Payment domain design docs, API payments contract, payment integration docs | duplicate payment process specs |
| Subscriptions | Canonical_Data_Model.md | Subscription domain models, API docs, billing docs | duplicate subscription lifecycle docs |
| Notifications | Notification_Catalog.md | Webhook_Contract_Catalog.md, API docs, messaging design docs | duplicate communication design docs |
| Reporting | Canonical_Data_Model.md | Report domain docs, analytics design docs, dashboard specs | duplicated report definitions |

---

# Deprecation Plan

## Files to Archive
The following should be treated as historical or non-authoritative unless retained for evidentiary or migration reasons:
- duplicate requirement summaries
- draft architecture summaries
- redundant schema drafts
- legacy service catalog snapshots
- previous gap analysis versions
- duplicate status models not referenced by canonical documents

## Files to Rename
The following should be renamed to clearly indicate canonical status:
- any file representing a master spec but lacking canonical designation should be renamed to reflect authoritative status
- draft or working files should be renamed to clearly indicate they are working drafts, not canonical

## Files to Merge
The following should be merged into canonical documents:
- duplicated security language into Security_Design.md
- duplicated API rules into API_Contract_Governance.md and OpenAPI_Specification.yaml
- duplicated database design content into Database_Physical_Model.md
- duplicate gap analysis content into Traceability_Matrix.md and project gap analysis source
- multiple subscription state definitions into Canonical_Data_Model.md

---

# Document Ownership

| Canonical Document | Owner | Review Frequency | Approval Authority |
|---|---|---|---|
| PropertyPilot_SRS.md | Product Owner | Quarterly and on major scope change | Product Steering Committee |
| Technical_Architecture.md | Architecture Lead | Quarterly and on major design change | Architecture Review Board |
| OpenAPI_Specification.yaml | API Architecture Lead | Monthly and on contract change | API Governance Board |
| Canonical_Data_Model.md | Data Architecture Lead | Quarterly and on data change | Data Governance Council |
| Database_Physical_Model.md | Database Engineering Lead | Monthly and on schema change | Engineering Design Review |
| Security_Design.md | Security Lead | Quarterly and on security change | Security Review Board |
| API_Contract_Governance.md | API Governance Lead | Quarterly | API Governance Board |
| Webhook_Contract_Catalog.md | Integration Lead | Monthly | Architecture + Security Review |
| Integration_Contract_Register.md | Integration Lead | Monthly | Integration Governance Board |
| Canonical_Data_Dictionary.md | Data Governance Lead | Quarterly | Data Governance Council |

---

# Implementation Alignment Actions

The following actions are required to align implementation with the canonical sources:

- OpenAPI updates
  - Align all endpoint paths to canonical /api/v1 patterns
  - Ensure all production API behavior matches OpenAPI contract
  - Remove unversioned routes from production API definitions
  - Add explicit error handling and idempotency metadata

- Database updates
  - Align all schema names and state values to canonical model
  - Remove conflicting status values and duplicated tables
  - Ensure database constraints reflect canonical data definitions
  - Standardize naming conventions and retention metadata

- Migration updates
  - Prepare schema migration scripts to align physical model with canonical model
  - Backfill state transitions where conflicting state names exist
  - Preserve historical data while standardizing on canonical semantics

- Screen updates
  - Align UI field names and validation rules with canonical data dictionary
  - Standardize status labels and lifecycle state terminology
  - Update customer, service, subscription, and payment screens to inherit the canonical values

- Workflow updates
  - Update state transitions for support tickets, service requests, subscriptions, and payments
  - Ensure service actions map to the canonical lifecycle states
  - Align workflow triggers with canonical event and status definitions

---

# Governance Decisions

The following final decisions are adopted as policy:

1. OpenAPI_Specification.yaml is the API source of truth.
2. Canonical_Data_Model.md is the conceptual data source of truth.
3. Database_Physical_Model.md is the physical database source of truth.
4. PropertyPilot_SRS.md is the requirements source of truth.
5. Cross_Cutting_Requirements.md controls shared rules across architecture, security, resilience, and operations.
6. Duplicate or contradictory documents are non-authoritative unless explicitly designated as historical.
7. All future contract, schema, and workflow changes must be checked against the canonical documents before implementation.
8. Any conflict between architecture and requirements must be resolved in favor of the approved requirement baseline and the governing cross-cutting control, unless an exception is formally approved.
9. All API routes must use /api/v1 for active production endpoints.
10. Security and compliance requirements must be reflected in implementation and not redefined per project artifact.
11. Documentation changes must include a governance review and traceability check against the canonical registers.
# Related Documents

- Traceability_Matrix.md
- PropertyPilot_Gap_Analysis.md
- Architecture_Gap_Analysis.md
- Cross_Cutting_Requirements.md
- Technical_Architecture.md
- PropertyPilot_SRS.md

---

## Summary

This document resolves documentary conflict by establishing a single authoritative baseline. The purpose is not to eliminate useful design history, but to ensure that all active product, technical, and operational work is aligned to the same source of truth.

Implementation must follow the canonical sources and the governance decisions recorded here.