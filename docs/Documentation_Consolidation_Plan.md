# Documentation Consolidation Plan

Document Type: Governance and Documentation Consolidation  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review

---

## 1. Documents to Archive

The following documents should be archived as historical or non-authoritative because they duplicate canonical content, are superseded by newer governance artifacts, or represent working drafts that should not remain active:

| Document | Reason | Action |
|---|---|---|
| Architecture_Gap_Analysis.md | overlaps with PropertyPilot_Gap_Analysis.md and Traceability_Matrix.md | Archive after merging into canonical gap/traceability sources |
| Duplicate Architecture Summary documents | repeated architecture content and conflicting summaries | Archive |
| Duplicate Database Design documents | multiple schema definitions conflict with Database_Physical_Model.md | Archive |
| Duplicate Service Catalogs | duplicate service definitions and overlapping catalog entries | Archive |
| Duplicate Project Master Specifications | multiple master spec claims and inconsistent baselines | Archive |
| Duplicate Gap Analysis documents | conflicting scope and status across reports | Archive |
| Duplicate subscription state documents | repeated lifecycle state definitions conflict with canonical model | Archive |
| Security design repeated sections | duplicates spread across multiple docs; must consolidate in Security_Design.md | Archive duplicated fragments |
| Legacy working notes and draft summaries | not part of approved source of truth set | Archive |
| Stale API drafts not represented in OpenAPI_Specification.yaml | not authoritative if superseded | Archive |

High-priority archive candidates:
- duplicate architecture summaries
- duplicate gap analyses
- duplicate schema drafts
- duplicate service catalog snapshots
- unreconciled internal working notes

---

## 2. Documents to Merge

The following documents should be merged into a canonical source or a primary governing document:

| Source Document(s) | Merge Target | Merge Purpose |
|---|---|---|
| PropertyPilot_Gap_Analysis.md + Architecture_Gap_Analysis.md | Traceability_Matrix.md + PropertyPilot_Gap_Analysis.md | unify open gaps, architecture gaps, and traceability |
| Duplicate architecture summaries | Technical_Architecture.md | one architecture source |
| Duplicate database design docs | Database_Physical_Model.md | one physical data source |
| Duplicate service catalogs | Canonical_Data_Model.md + OpenAPI_Specification.yaml | one service and data contract baseline |
| Duplicate security sections | Security_Design.md | one security source |
| Duplicate subscription lifecycle models | Canonical_Data_Model.md | one subscription lifecycle definition |
| Duplicate KYC workflow notes and status versions | KYC_Operations_Playbook.md | one operational KYC baseline |
| Duplicate privacy policy and request notes | Privacy_Rights_Operating_Procedure.md | one privacy rights operational standard |
| Duplicate release history and runbook fragments | Production_Cutover_and_GoLive_Checklist.md | one release and go-live baseline |
| Legacy API notes and miscellaneous endpoint drafts | OpenAPI_Specification.yaml + API_Catalog.md | one API contract baseline |

---

## 3. Canonical Source of Truth Matrix

| Domain | Canonical Source | Supporting Documents | Deprecated / Legacy |
|---|---|---|---|
| Business Requirements | PropertyPilot_SRS.md | Cross_Cutting_Requirements.md, Traceability_Matrix.md | duplicate requirement summaries |
| Product Requirements | PropertyPilot_SRS.md | Customer_Journeys.md, Screen_Catalog.md, Screen_Flows.md | informal product drafts |
| Architecture | Technical_Architecture.md | Security_Design.md, DevOps_Architecture.md, Operational_Runbooks.md | duplicated architecture summaries |
| Data Model | Canonical_Data_Model.md | Customer_Data_Model.md, Database_Physical_Model.md | duplicate data models |
| Database | Database_Physical_Model.md | Database_Architecture.md, schema migration notes | duplicate schema drafts |
| API Contract | OpenAPI_Specification.yaml | API_Catalog.md, API_Governance.md, API_Versioning_and_Deprecation_Policy.md | stale API drafts, unversioned specs |
| Security | Security_Design.md | Cross_Cutting_Requirements.md, Privacy_Rights_Operating_Procedure.md | repeated security fragments |
| Privacy | Privacy_Rights_Operating_Procedure.md | Security_Design.md, OpenAPI_Gap_Backlog.md | fragmented privacy notes |
| KYC | KYC_Operations_Playbook.md | Customer_Management.md, Customer_Journeys.md | duplicated onboarding notes |
| Subscriptions | Canonical_Data_Model.md | Subscription_Plans.md, Subscription APIs backlog | duplicate subscription lifecycle docs |
| Payments | Canonical_Data_Model.md | Payment gateway docs, refund and invoice requirements | stale payment contract notes |
| Notifications | Notification design docs / API contract sources | Notification API backlog, Webhook_Contract_Catalog.md | duplicate notification docs |
| Reporting | Canonical_Data_Model.md | report design notes, analytics specs | duplicate report definitions |
| Release & Operations | Production_Cutover_and_GoLive_Checklist.md | Release_Management.md, Environment_Management.md, DevOps_Architecture.md | duplicated release notes |
| Webhooks | OpenAPI_Specification.yaml | Webhook_Contract_Catalog.md, API_Catalog.md | stale webhook contract copies |

Canonical rules:
- OpenAPI is source of truth for API contract behavior.
- Canonical Data Model is source of truth for conceptual domain semantics.
- Database Physical Model is source of truth for physical schema.
- SRS is source of truth for product and requirements.
- Cross-Cutting Requirements govern shared rules.
- Architecture explains implementation decisions; it does not override requirements.

---

## 4. Conflicts Remaining

| ID | Area | Conflict | Status | Resolution Needed |
|---|---|---|---|---|
| CONF-001 | API Versioning | /api/v1, /v1, and unversioned API paths are mixed | Open | Finalize to /api/v1 for active production APIs |
| CONF-002 | Authentication | OTP-only MVP assumptions vs password and API auth flows | Open | Align to documented auth model and security design |
| CONF-003 | RTO/RPO | Recovery objectives differ across architecture and NFR docs | Open | Resolve by Cross_Cutting_Requirements.md precedence |
| CONF-004 | Subscription Lifecycle | state names differ across docs and flows | Open | Finalize canonical state model |
| CONF-005 | Service Request Status | inconsistent status definitions across screens/workflows | Open | Adopt canonical status model |
| CONF-006 | Ownership Model | property ownership model inconsistent | Open | Align with canonical data model and schema |
| CONF-007 | Pricing Logic | quote logic differs across pricing strategy and workflow docs | Open | Standardize in canonical pricing model |
| CONF-008 | Data Export / Privacy | privacy request flows exist but not consistently documented | Open | Consolidate in Privacy_Rights_Operating_Procedure.md |
| CONF-009 | KYC Status and Workflow | multiple KYC flows exist with partial overlap | Open | Standardize in KYC_Operations_Playbook.md |
| CONF-010 | Release Governance | multiple operational procedures vary in scope and detail | Open | Consolidate into Production_Cutover_and_GoLive_Checklist.md |
| CONF-011 | Documentation Ownership | no single owner assigned to many documents | Open | Assign owners in governance register |
| CONF-012 | Document Naming Consistency | numerous documents use overlapping or duplicate naming patterns | Open | Standardize naming and archive stale copies |

---

## 5. Required Updates to Existing Documents

| Document | Required Update |
|---|---|
| OpenAPI_Specification.yaml | Add missing APIs identified in OpenAPI_Gap_Backlog.md; remove duplicate/legacy route variants; enforce /api/v1 standard |
| API_Catalog.md | Mark canonical endpoints; classify deprecated endpoints; remove stale entries |
| PropertyPilot_SRS.md | Confirm requirements baseline and ensure it holds precedence over summaries |
| Technical_Architecture.md | Remove duplicate architecture summaries; ensure implementation alignment with source-of-truth rules |
| Security_Design.md | Consolidate repeated security sections and retain as unique canonical security document |
| Database_Physical_Model.md | Remove duplicate schema versions; ensure one physical schema source |
| Canonical_Data_Model.md | Confirm canonical data semantics for subscriptions, KYC, privacy, payment, notifications |
| Cross_Cutting_Requirements.md | Confirm governing precedence for security, compliance, resilience, and shared policies |
| Traceability_Matrix.md | Update to include API, workflow, screen, and database linkages for all active documents |
| PropertyPilot_Gap_Analysis.md | Merge duplicate findings into one authoritative gap report |
| Production_Cutover_and_GoLive_Checklist.md | Confirm governance and release process alignment with environment management and runbooks |
| KYC_Operations_Playbook.md | Align with actual KYC status model and API set |
| Privacy_Rights_Operating_Procedure.md | Align with privacy APIs, legal hold, and data retention rules |
| API_Versioning_and_Deprecation_Policy.md | Align version formats and deprecation lifecycle with OpenAPI governance |
| Documentation_Conflict_Resolution.md | Keep only final, approved decisions; remove unresolved draft notes |
| Documentation_Consolidation_Plan.md | Finalize after approval and archival actions are complete |

---

## 6. Final Documentation Roadmap

### Phase 1: Canonicalization
1. Confirm and lock source-of-truth documents:
   - PropertyPilot_SRS.md
   - Cross_Cutting_Requirements.md
   - Technical_Architecture.md
   - Canonical_Data_Model.md
   - Database_Physical_Model.md
   - OpenAPI_Specification.yaml
   - Security_Design.md
2. Archive duplicate/legacy working documents.
3. Merge redundant summaries and gap documents.
4. Assign document owners and review cadence.

### Phase 2: Contract and Data Alignment
1. Complete API contract alignment in OpenAPI_Specification.yaml.
2. Update API catalog and deprecation policy.
3. Standardize naming, path versioning, and lifecycle semantics.
4. Align database schema with canonical data model and physical model.

### Phase 3: Operational Procedure Finalization
1. Finalize KYC playbook.
2. Finalize privacy rights procedures.
3. Finalize production cutover and go-live checklist.
4. Ensure release governance and runbook alignment with operations.

### Phase 4: Audit and Quality Validation
1. Validate every active document against traceability matrix.
2. Confirm all major domains have a canonical source.
3. Ensure no active document contradicts the canonical baseline.
4. Review all unresolved conflicts and close them with formal governance approval.

### Phase 5: Governance and Maintenance
1. Publish final document ownership register.
2. Establish periodic reviews:
   - monthly for API and data
   - quarterly for business and architecture
   - on change for security, privacy, and release process
3. Require all future documentation updates to pass the source-of-truth check.

---

## Final Recommendation

The project should adopt a single-authoritative documentation model. Duplicate documents should not remain in active circulation. The recommended canonical baseline is:

- Requirements: PropertyPilot_SRS.md
- Cross-cutting rules: Cross_Cutting_Requirements.md
- Architecture: Technical_Architecture.md
- Conceptual data: Canonical_Data_Model.md
- Physical database: Database_Physical_Model.md
- API contracts: OpenAPI_Specification.yaml
- Security: Security_Design.md
- Governance and release: Production_Cutover_and_GoLive_Checklist.md

Once this baseline is enforced, the remaining conflicts must be resolved through formal governance and the affected documents updated before the next release milestone.