# Documentation Index

> Status: consolidation in progress. The numbered folders are the target organization. Root-level documents remain transitional until their canonical destinations and duplicates are resolved. Do not add new files to the empty legacy folders (`Architecture`, `Business`, `Data`, `Governance`, `Operations`, or `Security`).

This folder contains the project documentation organized by domain and delivery stage.

## Structure

- 01_Business/ — business requirements, value proposition, and business model
- 02_Product/ — product catalog, marketplace, and product ownership docs
- 03_Architecture/ — architecture, principles, standards, and technical design
- 04_Data/ — data models, data ownership, and data governance
- 05_APIs/ — API contracts and OpenAPI artifacts
- 06_UI_UX/ — screens, flows, wireframes, and UX architecture
- 07_Planning/ — roadmap, backlog, and release planning
- 08_Implementation/ — database scripts and implementation artifacts
- 09_Diagrams/ — ER diagrams and visual documentation

## Working rules

- Add new documentation to the appropriate numbered folder.
- Maintain one canonical document per topic; use links instead of copied content.
- Record major architecture decisions in `ADR/`.
- Keep implementation plans in `07_Planning/` and executable artifacts outside `docs/`.
- Treat root-level documents as transitional until the consolidation change is reviewed.

See the repository-level [`PROJECT_STATUS.md`](../PROJECT_STATUS.md) for the implementation-readiness assessment and recommended starting point.
