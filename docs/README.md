# PropertyPilot

PropertyPilot is a planned property operations and service-management platform. The repository currently contains a substantial specification set, a basic Flutter shell, database migration drafts, and generated application placeholders.

> [!IMPORTANT]
> The Spring Boot and React folders are not runnable applications yet. Most files in them contain planning prose or directory-tree text despite having code extensions. See [`PROJECT_STATUS.md`](PROJECT_STATUS.md) before implementing anything.

## Repository map

| Path | Purpose | Current state |
| --- | --- | --- |
| `docs/` | Business, product, architecture, data, API, UX, and delivery specifications | Primary requirements source; consolidation is in progress |
| `my-database-migration-project/` | Draft Flyway migrations and database design notes | Contains real SQL; requires PostgreSQL validation and consolidation |
| `propertypilot-backend/` | Intended Spring Boot application | Placeholder content; reserve this name for the canonical backend |
| `propertypilot-frontend/` | Intended React/Vite web application | Placeholder content; reserve this name for the canonical web app |
| `mobile_app/` | Flutter mobile application | Valid Flutter starter shell only |
| `infrastructure/` | Future Terraform and deployment code | Placeholder README only |
| `backend/`, `database/`, `admin-portal/`, `design/` | Earlier empty skeleton paths | Non-canonical placeholders; do not add new work here |
| `propertypilot-backend-1/` | Alternative generated backend draft | Non-canonical and non-runnable |

## Canonical decisions

- Use PostgreSQL, Flyway, Spring Boot 3, and Java 21 for the backend foundation.
- Use `propertypilot-backend/` as the eventual backend root.
- Use `propertypilot-frontend/` as the web application root.
- Keep migrations in one location once Sprint 1 begins: `propertypilot-backend/src/main/resources/db/migration/`.
- Begin with [`docs/MVP_Scope_Baseline.md`](docs/MVP_Scope_Baseline.md), [`docs/Technical_Architecture.md`](docs/Technical_Architecture.md), and [`docs/Implementation_Master_Plan.md`](docs/Implementation_Master_Plan.md), subject to the conflicts in `PROJECT_STATUS.md`.

## Start here

1. Read [`PROJECT_STATUS.md`](PROJECT_STATUS.md).
2. Resolve and validate the canonical PostgreSQL schema.
3. Generate a fresh, minimal Spring Boot project in `propertypilot-backend/`.
4. Implement one vertical slice: authentication, customer, property, and service request.
5. Generate the React shell only after the first API contract and backend slice are executable.

Do not start Terraform or broad UI implementation yet. First establish one tested end-to-end business slice.
