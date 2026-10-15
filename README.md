# PropertyPilot

PropertyPilot is a property operations and service-management platform.

## Repository structure

- propertypilot-backend/ - Spring Boot backend, Java 21, PostgreSQL and Flyway.
- propertypilot-frontend/ - React frontend.
- docs/ - Product, architecture, operations, audit and delivery documentation.

## Backend tests

From propertypilot-backend, run:

    .\mvnw.cmd -o test

## Configuration and safety

- Use propertypilot-backend/.env.example as a local configuration template.
- Never commit .env, credentials, private keys or production data.
- Use an isolated verification database for database-backed tests.

## Project status

See docs/PROJECT_STATUS.md and docs/Business/ for implementation progress.
