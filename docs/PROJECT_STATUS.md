# PropertyPilot Repository Status

Assessment date: 2026-09-01

## Executive assessment

The repository is documentation-rich but not implementation-ready. Its strongest assets are the requirements, architecture and planning documents, plus the draft SQL migration series. The apparent Spring Boot and React implementations are generated placeholders, not compilable code.

No existing project folder should currently be described as a working backend or web application.

## What was reviewed

- Root structure and Git working tree
- Documentation indexes, master plan, consolidation plan, and health reports
- Both Spring Boot candidate folders
- React/Vite candidate folder
- Flutter application
- Flyway migration project and SQL dialect signals
- Root manifests, configuration files, and ignore rules

## Findings

### 1. Repository consolidation is unfinished

The working tree shows a large migration from older documentation paths into numbered folders under `docs/`. The older tracked files appear deleted and reorganized copies are untracked. This may be intentional, but it should be reviewed and committed as one explicit documentation-consolidation change before application development is mixed into it.

Several now-empty legacy directories remain under `docs/`. They do not affect Git, but they should not receive new documents.

### 2. Backend candidates are placeholders

`propertypilot-backend/` has the preferred name and a conventional-looking layout, but its `pom.xml`, Java files, YAML files, tests, and SQL migration files contain repeated Markdown planning text rather than their declared formats.

`propertypilot-backend-1/` has a larger alternative tree, but its files contain directory-tree prose rather than Java/XML/YAML. It also places resources and tests under invalid nested paths such as `src/main/main/resources` and `src/main/test`.

Decision: reserve `propertypilot-backend/` as the canonical backend root. Do not merge code from `propertypilot-backend-1/`; there is no executable code to preserve. Reconcile unique domain names against the specifications, then archive or remove the alternative in a separate reviewed cleanup.

### 3. Web candidate is a placeholder

`propertypilot-frontend/` contains a useful proposed feature/file inventory, but the `.ts`, `.tsx`, configuration, and `package.json` files are not valid TypeScript, React, configuration, or JSON.

Decision: keep the folder name as canonical, but regenerate the application instead of repairing each placeholder file.

### 4. Mobile is only a starter

`mobile_app/` is a recognizable Flutter starter project with a valid-looking `pubspec.yaml` and one Dart source file. It does not yet represent PropertyPilot functionality.

### 5. Database work is the best implementation starting point

`my-database-migration-project/migrations/` contains actual SQL across V1-V8. However:

- V1 and V2 overlap in core table creation and must not both create the same objects.
- PostgreSQL `SERIAL` is used alongside MySQL-only `ON UPDATE CURRENT_TIMESTAMP` clauses.
- The SQL must be checked against the canonical data dictionary, MVP scope, foreign-key ordering, rollback expectations, and Flyway rules.
- The `package.json` depends on an old Node package named `flyway`; prefer the official Flyway CLI/container or Spring Boot Flyway integration.
- The SQL files under `propertypilot-backend/` are placeholders and must not be treated as migrations.

Decision: make a corrected migration series the first implementation deliverable, then place it in the canonical backend when that project is generated.

### 6. Documentation contains mechanical duplication

At least `docs/Implementation_Master_Plan.md` and `docs/Terraform_Generation_Plan.md` contain their full document twice. Several documents also contain mojibake characters such as `â€”`, indicating an encoding conversion problem.

These are documentation-quality issues, not reasons to delay the database/backend foundation. Clean them in a separate documentation pass so review history stays understandable.

## Canonical target layout

```text
PropertyPilot/
|-- README.md
|-- PROJECT_STATUS.md
|-- docs/
|   |-- 01_Business/
|   |-- 02_Product/
|   |-- 03_Architecture/
|   |-- 04_Data/
|   |-- 05_APIs/
|   |-- 06_UI_UX/
|   |-- 07_Planning/
|   |-- 08_Implementation/
|   `-- 09_Diagrams/
|-- propertypilot-backend/
|   |-- pom.xml
|   `-- src/
|       |-- main/java/
|       |-- main/resources/db/migration/
|       `-- test/java/
|-- propertypilot-frontend/
|-- mobile_app/
`-- infrastructure/
```

The temporary migration project may be removed only after its corrected migrations and database documentation have canonical homes. Empty skeleton directories and the alternate backend should be removed only in a separately reviewed cleanup commit.

## Recommended next work

Start Sprint 1 with a database-first, thin vertical slice:

1. Freeze the MVP entities needed for user/role, customer, property, service request, visit, evidence, and audit.
2. Rewrite V1 as a PostgreSQL Flyway baseline with extensions, tables, constraints, and indexes in dependency order. Later migrations should contain incremental changes only.
3. Validate migrations against a clean PostgreSQL instance and add an automated migration test.
4. Regenerate `propertypilot-backend/` as a minimal Spring Boot 3 / Java 21 Maven application with Web, Validation, Data JPA, Security, PostgreSQL, Flyway, Actuator, and Testcontainers.
5. Implement authentication plus customer/property/service-request endpoints with integration tests.
6. Only then scaffold React against the verified OpenAPI contract.

## Definition of ready for frontend work

- Clean backend build passes.
- Flyway migrates an empty PostgreSQL database successfully.
- Authentication and one core workflow pass integration tests.
- OpenAPI describes the implemented endpoints and error format.
- Local startup is documented and reproducible.
