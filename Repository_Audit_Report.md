# Repository Audit Report

**Project:** PropertyPilot  
**Audit date:** 2026-09-01  
**Basis:** repository contents and Git working tree; no architectural redesign assumed

## Executive Summary

PropertyPilot is a planning repository, not a working product repository. The documentation set is broad, the standalone migration project contains real but conflicting SQL, and the Flutter directory is a standard starter application. The Spring Boot and React trees are not implementations: their code/configuration files contain Markdown plans or directory-tree text. No backend, web frontend, infrastructure-as-code, CI/CD pipeline, integrated API, or production test suite is executable.

The repository is also in the middle of an uncommitted documentation reorganization: Git reports 86 tracked deletions, 5 tracked modifications, and 110 untracked path groups. Cleanup must preserve reviewability by separating documentation consolidation from application generation.

Recommended recovery direction:

1. Keep the numbered `docs/` hierarchy as the documentation target.
2. Keep `propertypilot-backend/` and `propertypilot-frontend/` as canonical path names, but replace their placeholder contents through reviewed implementation work.
3. Consolidate real SQL into one verified Flyway history under the canonical backend.
4. Keep `infrastructure/` as the canonical IaC path, but defer implementation until a backend vertical slice works.
5. Keep `docs/05_APIs/OpenAPI_Specification.yaml` as the canonical API contract location, but do not label it complete: it contains only 4 paths and 6 operations.

## Audit Method and Evidence

The audit inspected all requested folders, file types, manifests, configuration, source/test signals, SQL object counts, OpenAPI operations, duplicate basenames, repeated document headings, content hashes, and Git state.

Key measurements:

| Area | Files | Evidence-based state |
| --- | ---: | --- |
| `docs/` | 244 | 237 Markdown, 6 SQL, 1 YAML; extensive but duplicated and conflicting |
| `propertypilot-backend/` | 102 | 82 `.java`; zero Java `package` declarations; placeholder prose |
| `propertypilot-backend-1/` | 138 | 120 `.java`; zero Java `package` declarations; directory-tree prose |
| `backend/` | 1 | README only; child service directories empty |
| `propertypilot-frontend/` | 166 | 156 TS/TSX; zero import declarations; invalid placeholder content |
| `mobile_app/` | 327 | Flutter platform scaffold, one Dart application file, starter test |
| `admin-portal/` | 1 | README only; child directories empty |
| `database/` | 1 | README only; `sql/` empty |
| `infrastructure/` | 1 | README only |
| `diagrams/` | 0 | Empty |
| `design/` | 1 | README only; `wireframes/` empty |

Toolchain observation: Flutter is available locally. Java, Maven, Node, npm, and Docker were not available on the audit PATH, so no real Java/Node/container build could be executed. This does not change the source assessment because the relevant manifests and source files are not valid in their declared formats.

## Current Repository Structure

```text
PropertyPilot/
|-- docs/                         # primary specifications; mid-consolidation
|   |-- 01_Business/              # 31 files
|   |-- 02_Product/               # 4 files
|   |-- 03_Architecture/          # 31 files
|   |-- 04_Data/                  # 13 files
|   |-- 05_APIs/                  # 9 files
|   |-- 06_UI_UX/                 # 2 files plus empty wireframes path
|   |-- 07_Planning/              # 9 files
|   |-- 08_Implementation/        # 10 files including 6 SQL scripts
|   |-- 09_Diagrams/              # 5 files
|   |-- ADR/                      # 6 ADR files
|   `-- legacy empty folders      # Architecture, Business, Data, etc.
|-- propertypilot-backend/        # conventional shape, invalid placeholder content
|-- propertypilot-backend-1/      # alternate invalid placeholder tree
|-- backend/                      # empty microservice skeleton
|-- propertypilot-frontend/       # broad proposed React tree, invalid content
|-- mobile_app/                   # Flutter starter scaffold
|-- admin-portal/                 # empty React skeleton
|-- database/                     # empty canonical-looking skeleton
|-- my-database-migration-project/# real draft SQL V1-V8 plus database docs
|-- infrastructure/               # empty IaC skeleton
|-- diagrams/                     # empty; docs/09_Diagrams contains artifacts
|-- design/                       # empty design skeleton
`-- assets/                       # empty shared-assets skeleton
```

## Folder-by-Folder Decision

| Folder | Purpose found | Decision | Execution action |
| --- | --- | --- | --- |
| `docs/` | Requirements, architecture, data, API, UX, planning, operations | **Keep + Merge** | Make numbered folders canonical; finish current tracked-to-numbered migration; remove internal duplication; move transitional root docs after mapping |
| `propertypilot-backend/` | Intended Spring Boot monolith/modular backend | **Keep path; replace placeholders** | Preserve the path and migration destination; rebuild contents as valid Maven/Java sources in Sprint 1 |
| `propertypilot-backend-1/` | Alternate generated backend inventory | **Archive, then delete** | Extract only unique module names/requirements; no executable source exists to merge; archive for one release, then remove |
| `backend/` | Proposed microservice split | **Delete** | Empty skeleton conflicts with the chosen modular-backend path; delete after confirming no external automation references it |
| `propertypilot-frontend/` | Intended React/Vite application | **Keep path; replace placeholders** | Retain proposed feature inventory as planning input; regenerate valid Vite/React files only after API baseline |
| `mobile_app/` | Cross-platform Flutter client | **Keep + Update** | Keep starter; rename package/metadata and implement only after core API workflow is stable |
| `admin-portal/` | Earlier standalone admin UI skeleton | **Merge, then delete** | Merge intended admin routes into `propertypilot-frontend/src/features/admin`; folder has no code to preserve |
| `database/` | Intended central schema location | **Delete or repurpose** | Prefer backend-owned Flyway migrations. If retained, restrict it to database tooling/docs; do not duplicate migration history |
| `my-database-migration-project/` | Draft Flyway V1-V8 and database notes | **Merge, then archive/delete** | Correct and migrate SQL/docs to canonical backend and numbered docs; archive until a clean migration test passes |
| `infrastructure/` | Intended Terraform/deployment root | **Keep + Update** | Canonical IaC location; replace README-only placeholder after application/runtime requirements are verified |
| `diagrams/` | Intended generated diagrams | **Merge, then delete** | Use `docs/09_Diagrams/` for versioned documentation diagrams; reserve a build output path only if generation tooling later requires it |
| `design/` | Intended design references | **Merge or Archive** | Move durable wireframes to `docs/06_UI_UX/`; archive raw design source only if it cannot live there |
| `assets/` | Intended shared assets | **Keep only if used** | Currently empty; retain when real shared assets exist, otherwise delete with empty skeleton cleanup |

## Duplicate Folder Analysis

### Backend duplication

Three backend concepts compete:

- `backend/`: an empty microservice directory plan.
- `propertypilot-backend/`: a modular/layered Spring Boot-looking tree filled with repeated Markdown.
- `propertypilot-backend-1/`: another Spring Boot-looking tree filled with directory listings and invalid `src/main/main/resources` / `src/main/test` nesting.

There is no codebase merge to perform because neither Spring tree contains executable Java. The only useful merge inputs are file/module names. Canonical path: `propertypilot-backend/`.

### Frontend duplication

Three client surfaces overlap:

- `propertypilot-frontend/`: proposed unified React feature tree.
- `admin-portal/`: empty admin-only skeleton.
- `mobile_app/`: real Flutter starter, intended for a distinct mobile runtime.

The admin portal should be a role-gated section of the canonical React app unless an existing requirement explicitly mandates independent deployment. The mobile app is not a duplicate runtime and should remain separate.

### Database duplication

Four locations claim migration/schema ownership:

| Location | SQL files | `CREATE TABLE` statements | Assessment |
| --- | ---: | ---: | --- |
| `docs/08_Implementation/database/` | 6 | 28 | Real DDL drafts; documentation-area executable artifacts |
| `my-database-migration-project/migrations/` | 8 | 27 | Real Flyway-named drafts; V1/V2 overlap and dialect errors exist |
| `propertypilot-backend/.../db/migration/` | 4 | 0 | Placeholder Markdown, not SQL |
| `propertypilot-backend-1/.../db/migration/` | 3 | 0 | Placeholder text, not SQL |

Canonical destination: `propertypilot-backend/src/main/resources/db/migration/`. Canonical input must be produced by reconciling the first two real SQL sets, not by copying either set unchanged.

### Documentation/diagram duplication

- Numbered `docs/01_*` through `docs/09_*` overlap with root-level `docs/*.md` and formerly tracked domain folders.
- `diagrams/` overlaps with `docs/09_Diagrams/`; the former is empty.
- `design/wireframes/` overlaps with `docs/06_UI_UX/wireframes/`; both contain no substantive assets.
- Empty legacy `docs/Architecture`, `Business`, `Data`, `Governance`, `Operations`, and `Security` paths remain after files were moved/deleted.

## Duplicate Document Analysis

### Exact filename duplication

- `docs/Project_Master_Specification.md`
- `docs/07_Planning/Project_Master_Specification.md`

Compare them, designate the numbered copy canonical, merge unique changes, then remove the root duplicate.

### Confirmed internal full-document duplication

Repeated top-level headings at consistent offsets show that the following documents contain a complete or near-complete second copy inside the same file:

- `Backend_Foundation_Generation_Plan.md`
- `Backend_Implementation_Roadmap.md`
- `Backend_Module_Implementation_Plan.md`
- `Business_Process_Catalog.md`
- `CICD_Implementation_Plan.md`
- `Database_Implementation_Plan.md`
- `Database_Migration_Backlog.md`
- `DevOps_Implementation_Roadmap.md`
- `Frontend_Implementation_Roadmap.md`
- `Frontend_Module_Implementation_Plan.md`
- `Frontend_Screen_Specifications.md`
- `Implementation_Master_Plan.md`
- `Implementation_Readiness_Report.md`
- `Infrastructure_Bootstrap_Plan.md`
- `MVP_Scope_Baseline.md`
- `OpenAPI_Completion_Backlog.md`
- `OpenAPI_Completion_Plan.md`
- `OpenAPI_Generation_Plan.md`
- `Project_Readiness_Scorecard.md`
- `QA_Test_Strategy.md`
- `Sprint_1_Implementation_Backlog.md`
- `Sprint_2_Implementation_Backlog.md`
- `Sprint_3_Implementation_Backlog.md`
- `Terraform_Generation_Plan.md`
- `UAT_Test_Plan.md`

`ETA_Management.md` and `Test_Data_Strategy.md` also have repeated major headings and require manual inspection before trimming.

Action: mechanically remove only verified repeated halves, then review links and line-level differences. Do not recreate these documents.

### Content overlap without identical filenames

The following families cover substantially overlapping decisions and must be consolidated by authority rather than concatenated:

- `Repository_Health_Report`, `Final_Repository_Health_Report`, `Implementation_Readiness_Report`, `Implementation_Readiness_Assessment`, `Project_Readiness_Scorecard`, `PropertyPilot_Gap_Analysis`, and this audit.
- `Implementation_Master_Plan`, three sprint backlogs, backend/frontend roadmaps, and module implementation plans.
- `API_Catalog`, domain `*_API.md` files, `OpenAPI_*` plans/backlogs, and `OpenAPI_Specification.yaml`.
- `Canonical_Data_Model`, `Canonical_Data_Dictionary`, domain `*_Data_Model.md` files, schema backlogs, physical SQL, and database implementation plans.
- `Infrastructure_Bootstrap_Plan`, `Terraform_Generation_Plan`, `DevOps_Implementation_Roadmap`, and `CICD_Implementation_Plan`.
- `Production_Cutover_Plan`, `Production_Cutover_and_GoLive_Checklist`, `Release_Readiness_Checklist`, and `Go_Live_Support_Model`.

## Duplicate Codebase Analysis

### Backend

`propertypilot-backend/` reports 82 Java files and four test-named files, but none has a Java package declaration. Its `pom.xml` is a Markdown backend plan. Configuration and migration files are also Markdown. Completion is therefore not proportional to file count.

`propertypilot-backend-1/` reports 120 Java files and eleven test-named files, but none has a Java package declaration. Its `pom.xml` and source files are directory trees. It provides no compilable code or tests.

Conclusion: duplicate generated inventories, not duplicate implementations.

### React frontend

`propertypilot-frontend/` reports 100 `.tsx` and 56 `.ts` files, but no import declarations were found. `package.json` is a single package-install command, and `App.tsx`/`main.tsx` contain directory listings. No React code or tests exist.

### Flutter mobile

`mobile_app/` contains a default Flutter counter-style starter, default package description, and starter widget test. This is valid scaffolding but implements no PropertyPilot domain behavior.

## Canonical Source Recommendations

### Source of Truth

The source of truth must be executable artifacts plus explicitly governed specifications. Status labels in prose do not override missing code or incomplete contracts.

### Documentation Source

**Canonical root:** `docs/` numbered hierarchy.  
**Index:** `docs/PROJECT_INDEX.md` and `docs/README.md`, updated after consolidation.  
**Governance:** ADRs for decisions; one document per topic; cross-link instead of copy.  
**Transitional:** root-level docs until moved/mapped.  
**Non-canonical:** duplicate internal halves, empty legacy domain folders, generated health reports that contradict executable evidence.

### Backend Source

**Canonical path:** `propertypilot-backend/`.  
**Current code source:** none; folder contents are placeholders.  
**Inputs:** MVP scope, canonical data model/dictionary, state model catalog, OpenAPI contract, security ADRs, and reconciled migrations.  
**Reject as source:** `propertypilot-backend-1/` and `backend/`.

### Frontend Source

**Canonical web path:** `propertypilot-frontend/`.  
**Canonical mobile path:** `mobile_app/`.  
**Admin ownership:** merge into `propertypilot-frontend/src/features/admin/` unless independent deployment is approved by ADR.  
**Current implementation source:** Flutter starter only; no domain UI exists.

### Database Source

**Canonical runtime location:** `propertypilot-backend/src/main/resources/db/migration/`.  
**Authoring inputs:** reconcile `docs/08_Implementation/database/` and `my-database-migration-project/migrations/`.  
**Rules:** PostgreSQL-only syntax, immutable versioned Flyway history after release, automated clean-database validation, schema/data dictionary synchronization.

### Infrastructure Source

**Canonical path:** `infrastructure/`.  
**Current implementation source:** none.  
**Planning inputs:** existing Terraform, bootstrap, DevOps, environment, security, and recovery documents after duplicate removal and cloud/provider decisions.

### API Source

**Canonical machine contract:** `docs/05_APIs/OpenAPI_Specification.yaml`.  
**Current coverage:** 4 paths, 6 operations, and basic schemas; incomplete for authentication, item operations, visits, evidence, notifications, subscriptions, payments, admin, operations, errors, security, pagination, and webhooks.  
**Human catalog role:** `docs/05_APIs/API_Catalog.md` is a backlog/reference, not a production-ready contract. Its `Status: Production Ready` label conflicts with both the YAML coverage and repository health report.  
**Domain API Markdown:** update/merge into the YAML and keep only when it adds business semantics not representable in OpenAPI.

## Cleanup Plan

### Files and folders to keep

- Root `README.md`, `.gitignore`, and this audit report.
- Numbered `docs/` structure and `docs/ADR/`.
- Unique requirements, state models, data dictionary, security requirements, traceability material, and operational procedures after consolidation.
- `propertypilot-backend/`, `propertypilot-frontend/`, `mobile_app/`, and `infrastructure/` path names.
- Real SQL in both draft SQL locations until reconciliation and validation are complete.
- `docs/05_APIs/OpenAPI_Specification.yaml` as the machine-contract location.

### Files and folders to archive

- `propertypilot-backend-1/` for one cleanup/recovery cycle, then delete.
- `my-database-migration-project/` after its corrected migrations/docs are merged and tests pass.
- Superseded readiness/health reports required for historical traceability.
- Raw design sources that cannot be represented in `docs/06_UI_UX/`.

### Files and folders to delete

Delete only after merge verification and in a dedicated cleanup change:

- `backend/` empty microservice skeleton.
- `admin-portal/` after its intended scope is represented in the canonical frontend.
- `diagrams/` empty duplicate.
- Empty legacy documentation folders.
- Placeholder source/config/test files in both generated Spring trees and the React tree when valid projects are generated.
- Internally duplicated second halves of confirmed documents.
- `database/` if backend-owned migrations are accepted as the rule.
- Archived alternate backend and migration-project folders after the retention checkpoint.

### Files to merge

- Both `Project_Master_Specification.md` copies.
- SQL from `docs/08_Implementation/database/` and `my-database-migration-project/migrations/`.
- API catalog/domain Markdown into OpenAPI plus a smaller human guide.
- Redundant readiness and repository-health reports into one maintained status assessment; retain older versions only as history.
- Admin portal scope into canonical web frontend planning.
- Diagram/design references into `docs/09_Diagrams/` and `docs/06_UI_UX/`.
- `PROJECT_STATUS.md` into this report or the maintained health report after this audit is accepted; avoid parallel status sources.

### Required moves

1. Finish moving legacy tracked docs into the corresponding numbered directories using Git-aware moves/verified mappings.
2. Move corrected Flyway migrations into `propertypilot-backend/src/main/resources/db/migration/`.
3. Move reusable migration-project data documentation to `docs/04_Data/` and implementation instructions to `docs/08_Implementation/`.
4. Move durable design/wireframe artifacts into `docs/06_UI_UX/`.
5. Move/version diagram sources and rendered artifacts under `docs/09_Diagrams/`.
6. Move root-level planning documents into `docs/07_Planning/` only after duplicate resolution and link updates.

### Required renames

- Rename Flutter package/display metadata from generic `mobile_app` / “A new Flutter project” to PropertyPilot identifiers when implementation begins.
- Rename migration project artifacts that still contain generic `my-*`, placeholder GitHub URL, and `Your Name` metadata before merge.
- Correct the apparent `propertyilot.com` typo in the API catalog after the actual domain is confirmed.
- Do not rename `propertypilot-backend/`, `propertypilot-frontend/`, `mobile_app/`, or `infrastructure/`; these are the recommended canonical paths.

## Documentation Health Assessment

### Overall health

Coverage is high; reliability is moderate-to-low. Document volume substantially overstates readiness because of repeated halves, overlapping authorities, unsupported “production ready” claims, mojibake/encoding damage, and executable artifacts that contradict the prose.

### Missing documents or missing authoritative content

- A short, approved canonical-source register mapping each topic to one maintained file.
- A verified physical schema specification matching the actual Flyway history.
- A complete validated OpenAPI contract with security, common errors, pagination, idempotency, and webhook definitions.
- A reproducible local-development/run guide for an executable stack.
- Dependency/version policy backed by actual manifests.
- Secrets/configuration inventory tied to runtime configuration.
- Test evidence and coverage report tied to real code.
- Data retention/deletion implementation mapping.
- Threat model and security verification evidence tied to implemented controls.
- Deployment/rollback runbook tied to actual infrastructure and CI/CD.

These gaps should be filled by updating the relevant existing governance, developer, API, data, security, QA, and runbook documents—not by creating parallel plans.

### Obsolete documents/artifacts

- Generated backend/frontend foundation files masquerading as code.
- Empty skeleton README plans once canonical applications exist.
- Multiple historical health/readiness reports presented as current.
- Node `flyway` package metadata with placeholder repository/author details.
- Terraform/CI/CD generation plans until reconciled with a selected runtime and actual code.

### Conflicting documents

- API Catalog says “Production Ready”; OpenAPI contains only 4 paths/6 operations and the final health report says implementation is not ready.
- Microservice folder plan conflicts with the modular backend folder and current absence of deployable services.
- Multiple data definitions and two real DDL series disagree on ownership and migration order.
- OpenAPI/catalog/domain API documents differ in breadth and authority.
- Infrastructure documents include alternatives and production controls despite no chosen/provider-backed implementation.
- Multiple readiness reports use different readiness language without a single evidence date/baseline.

### Low-quality generated documents

- All content in `propertypilot-backend/` except repository metadata should be presumed generated placeholder until individually proven otherwise.
- All content in `propertypilot-backend-1/` should be presumed generated placeholder.
- All TS/TSX/config content in `propertypilot-frontend/` should be presumed generated placeholder.
- The 25 confirmed internally duplicated planning documents require mechanical correction.
- Documents with mojibake (`â€”` and related sequences) require encoding cleanup.

### Documents to update rather than recreate

- `docs/PROJECT_INDEX.md` and `docs/README.md`: canonical navigation and ownership.
- `docs/MVP_Scope_Baseline.md`: remove duplicate half and approve MVP boundary.
- `docs/Technical_Architecture.md`: record selected deployable architecture after ADR alignment.
- `docs/Canonical_Data_Dictionary.md`: align with verified migrations.
- `docs/State_Model_Catalog.md`: align database constraints and API enums.
- `docs/05_APIs/OpenAPI_Specification.yaml`: expand and validate.
- `docs/05_APIs/API_Catalog.md`: downgrade status and reconcile with YAML.
- `docs/Implementation_Master_Plan.md`: remove duplicate half and update actual progress.
- `docs/Sprint_1_Implementation_Backlog.md`: turn into the executable recovery backlog.
- `docs/Developer_Guide.md` / `docs/08_Implementation/Developer_Guide.md`: consolidate and add real commands when builds exist.
- Existing QA, security, CI/CD, Terraform, release, and runbook documents: bind them to implemented evidence later.

## Implementation Status Assessment

Percentages measure usable, verified delivery toward the documented platform—not quantity of generated files.

| Area | Completion | Evidence |
| --- | ---: | --- |
| Planning | **70%** | Broad business, architecture, data, API, UX, roadmap, QA, security, and operations coverage; reduced by internal duplication, conflicts, missing authority, and unapproved implementation baseline |
| Backend | **0%** | 202 Java-looking files across two candidates but zero package declarations; invalid POMs/configuration; no compilable application or executable tests |
| Frontend | **2%** | Flutter starter runs conceptually as scaffold; React files and package manifest are invalid; no PropertyPilot screens/workflows implemented |
| Database | **20%** | Two real DDL sets (28 and 27 create-table statements) and V1-V8 draft history exist; conflicting baselines, PostgreSQL/MySQL syntax mix, no clean migration validation, and backend migration files are fake |
| Infrastructure | **0%** | README and planning only; no `.tf`, deployment manifests, environment implementation, or state/backend configuration found |
| Testing | **1%** | Flutter starter widget test exists; backend/web test-named files are placeholders; no database integration, API, security, performance, or end-to-end execution evidence |
| Production readiness | **0%** | No runnable integrated system, CI/CD, IaC, secrets implementation, monitoring, backup validation, security evidence, deployment, or rollback proof |

The percentages are deliberately conservative. “Partially specified” is credited under planning, not under implementation.

## Actual Progress Assessment

### Actually implemented

- A large documentation corpus and numbered target organization.
- Six ADR files and multiple governance/operations artifacts.
- One small OpenAPI YAML containing 4 paths and 6 operations.
- Two sets of real SQL drafts, including a V1-V8 Flyway-named series.
- A standard cross-platform Flutter starter with one source file and a starter widget test.
- Repository path skeletons and ignore/readme groundwork.

### Exists only as documentation or generated inventory

- Spring Boot architecture, controllers, services, repositories, entities, security, caching, messaging, and tests.
- React admin/customer/operations features, API clients, state management, routing, components, mocks, and tests.
- Terraform, cloud resources, CI/CD, observability, disaster recovery, backup, and production operations.
- Most API endpoints and workflows described by catalogs and plans.
- QA, UAT, security, performance, release, and go-live processes.

### Partially implemented

- Database: meaningful DDL exists, but it is conflicting and unvalidated.
- API: a minimal OpenAPI fragment exists, but it lacks most documented domains and common contract standards.
- Mobile: valid generic shell, no domain functionality.
- Documentation organization: target numbered folders populated, but Git migration and canonical mapping are incomplete.

### Production-ready

Nothing in the application stack is production-ready. Some individual planning documents may be useful inputs, but their status labels are not deployment evidence.

## Pending Work Breakdown

Effort uses focused engineering days and assumes one experienced owner unless stated otherwise.

### Critical

| Work item | Description | Dependencies | Recommended owner | Estimated effort |
| --- | --- | --- | --- | ---: |
| Protect current work | Review 86 deletions/110 untracked groups, map old-to-new docs, commit consolidation separately | None | Tech lead + documentation owner | 1–2 days |
| Approve canonical sources | Update existing indexes/control register with one owner/path per topic | Git consolidation | Architect + product owner | 1 day |
| Freeze MVP data scope | Reconcile MVP, data dictionary, state models, tenant/security needs | Canonical docs | Product, architect, DBA | 2–3 days |
| Rebuild migration baseline | Reconcile both real SQL sets; remove duplicate table creation; correct PostgreSQL syntax; order constraints/indexes/seeds | Frozen data scope | DBA/backend engineer | 4–7 days |
| Automate migration validation | Run Flyway against clean PostgreSQL and test upgrade behavior | Correct migration series; Docker/Testcontainers availability | Backend/DBA | 2–3 days |
| Generate valid backend foundation | Replace placeholder POM/code/config with minimal Spring Boot 3/Java 21 project | Schema direction, toolchain installation | Backend lead | 2–3 days |
| Implement security baseline | Auth model, password hashing, JWT/session decision, RBAC, security integration tests | Backend foundation, auth ADR, user/role schema | Backend + security | 4–6 days |
| Establish executable CI | Build, unit/integration tests, migration validation, lint, dependency/security scanning | Valid backend and Git workflow | DevOps + backend | 3–5 days |
| Expand canonical OpenAPI MVP | Add implemented auth/customer/property/service-request operations, security, errors, pagination; validate | MVP scope and backend behavior | API/backend owner | 3–5 days |
| Deliver first vertical slice | Auth → customer → property → service request, with integration tests | All above | Cross-functional team | 8–12 days |

### High

| Work item | Description | Dependencies | Recommended owner | Estimated effort |
| --- | --- | --- | --- | ---: |
| Remove internal doc duplication | Trim verified repeated halves in 25+ files, preserve unique deltas | Consolidation commit | Documentation owner | 2–4 days |
| Resolve conflicting status claims | Correct API/readiness labels against evidence | Canonical-source approval | Architect + documentation owner | 1 day |
| Rebuild React foundation | Replace placeholders with valid Vite/React/TS project and role-based shell | Stable OpenAPI vertical slice, Node toolchain | Frontend lead | 3–5 days |
| Implement web vertical slice | Login, customer, property, service request UI and API integration | React foundation, working backend | Frontend + backend | 8–12 days |
| Define test architecture | Real unit, repository, API, migration, contract, and E2E suites | Executable apps | QA lead + engineers | 3–4 days |
| Local stack | Reproducible PostgreSQL/backend/web startup with safe example config | Backend/web foundations, container runtime | DevOps | 2–4 days |
| Audit/event baseline | Persist audit events and enforce core state transitions | Schema/backend vertical slice | Backend + security | 3–5 days |
| Evidence/document storage | Select storage approach and implement secure upload metadata flow | Provider decision, property/service schema | Backend + DevOps | 4–7 days |
| Consolidate developer docs | Merge duplicate guides and add verified build/run/test commands | Executable stack | Tech lead | 1–2 days |
| Archive invalid projects | Preserve a tagged/archive snapshot, then remove alternate/empty skeletons | Successful canonical builds and review | Repository maintainer | 1 day |

### Medium

| Work item | Description | Dependencies | Recommended owner | Estimated effort |
| --- | --- | --- | --- | ---: |
| Visits/evidence/reports | Implement next MVP workflow and tests | First vertical slice | Backend/frontend/QA | 10–15 days |
| Notifications | Select provider abstraction, templates, delivery state, retries | Events/audit, provider decision | Backend | 5–8 days |
| Mobile foundation | Rename package, configure environments, auth shell, API client | Stable API and auth | Mobile engineer | 4–6 days |
| Mobile MVP workflow | Customer/property/request/visit experience | Mobile foundation, stable endpoints | Mobile + QA | 10–15 days |
| Infrastructure baseline | Implement minimal Terraform for selected environment; no premature DR complexity | Runtime sizing/provider/security decisions | DevOps/cloud engineer | 6–10 days |
| Observability | Structured logs, metrics, traces, health checks, alert baseline | Deployed application baseline | DevOps + backend | 4–7 days |
| Security verification | Threat model update, SAST/SCA, authz tests, secrets controls | Working app and CI | Security | 4–6 days |
| Data lifecycle | Retention, privacy rights, deletion/anonymization jobs | Stable schema/legal inputs | DBA + security/product | 4–7 days |
| Documentation encoding cleanup | Repair mojibake and broken links, standardize UTF-8 | Duplication cleanup | Documentation owner | 1–3 days |
| Traceability update | Map approved requirements → API → schema → tests | Stable vertical slices | BA/QA/architect | 2–4 days |

### Low

| Work item | Description | Dependencies | Recommended owner | Estimated effort |
| --- | --- | --- | --- | ---: |
| Empty folder cleanup | Remove unused assets/design/diagram skeletons | Archive/merge verification | Repository maintainer | 0.5 day |
| Advanced marketplace | Vendor, quotations, assignments, commissions | MVP acceptance | Product + delivery team | 15–25 days |
| Subscription/payments expansion | Billing, invoices, refunds, webhooks | Core MVP/security/provider contracts | Backend/frontend/QA | 15–25 days |
| Advanced analytics/search | Reporting warehouse/search capabilities | Production data and defined KPIs | Data team | 15–30 days |
| Multi-region DR | Implement only after RTO/RPO, scale, and budget validation | Production architecture and operations maturity | Platform team | 10–20 days |
| Documentation polish | Reduce redundant long-form plans, improve navigation and diagrams | Stable implementation | Documentation owner | 3–5 days |

## Immediate Next Steps

Execute these ten tasks in order:

1. **Freeze and review the current Git reorganization.** Produce an old-path → new-path mapping for all 86 tracked deletions and commit documentation consolidation alone; do not mix code generation into it.
2. **Update the existing documentation control/index artifacts.** Declare the numbered docs hierarchy, canonical MVP document, data dictionary/state catalog, OpenAPI YAML, implementation plan, and ADR authority.
3. **Mechanically remove verified duplicated halves** from `MVP_Scope_Baseline.md`, `Implementation_Master_Plan.md`, database/OpenAPI plans, and Sprint 1 backlog first; then correct their internal links.
4. **Approve the Sprint 1 MVP entity/state subset** for users/roles, customers, properties, service requests, visits, evidence, and audit; record conflicts in existing ADR/control documents.
5. **Reconcile the two real SQL sources** into a proposed PostgreSQL-only Flyway sequence; eliminate V1/V2 table overlap and MySQL `ON UPDATE CURRENT_TIMESTAMP` syntax.
6. **Validate the proposed migrations on a clean PostgreSQL instance** and add an automated clean-migrate test plus schema assertions. Do not replace the active migration folder until this passes.
7. **Replace `propertypilot-backend/` placeholders with a minimal valid Spring Boot 3/Java 21 Maven foundation** using Web, Validation, JPA, Security, PostgreSQL, Flyway, Actuator, and Testcontainers.
8. **Move the validated migration history into the backend** and prove that the application starts, Flyway succeeds, health checks pass, and tests run from a clean checkout.
9. **Implement and document one backend vertical slice**—authentication, customer, property, and service request—while expanding `docs/05_APIs/OpenAPI_Specification.yaml` to match actual behavior.
10. **Only after task 9 passes, rebuild `propertypilot-frontend/` as a valid React/Vite/TypeScript application** and implement login plus the same vertical slice; then archive/remove the invalid alternate and empty skeleton projects in a separate cleanup commit.

## Final Recovery Decision

Proceed with implementation, but treat the repository as a specification recovery and foundation build—not as an existing application that needs finishing. The immediate critical path is canonical documentation control → validated PostgreSQL migrations → valid backend foundation → one tested vertical slice → web client. Infrastructure breadth, marketplace, billing, and production hardening follow only after that path is executable.
