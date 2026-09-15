# Backend Recovery Assessment

**Scope:** `propertypilot-backend/`  
**Assessment date:** 2026-09-01  
**Mode:** assessment only; no backend files modified

## Executive Decision

The backend should be **rebuilt from a clean Spring Boot foundation**, not repaired file by file.

The folder has a plausible Spring Boot directory shape, but no executable Spring Boot project exists. Every one of the 102 files inspected contains a variation of the same generated “Backend Foundation Generation Plan” document rather than the format implied by its filename.

This is systematic generation failure, not isolated POM corruption:

| Evidence | Result |
| --- | --- |
| Total files inspected | 102 |
| Files containing generated backend-plan documentation | 102 |
| Valid Maven POM files | 0 |
| Valid Java files | 0 of 82 |
| Valid Java test files | 0 of 4 |
| Valid Spring YAML files | 0 of 3 profiles plus base configuration |
| Valid Flyway SQL migrations | 0 of 4 |
| Valid schema SQL files | 0 of 1 |
| Valid Maven wrapper artifacts | 0 |
| Valid Logback XML files | 0 |
| Valid Docker Compose files | 0 |

The directory and filenames can guide recovery planning, but none of their contents can be trusted as implementation.

## Assessment Answers

### Does a Spring Boot project exist?

**No.**

A directory tree named like a Spring Boot project exists, but the minimum executable project elements do not:

- No valid Maven project model
- No `@SpringBootApplication` class
- No Java package declarations
- No application entry point
- No dependency configuration
- No valid runtime configuration
- No executable tests

The folder is a generated project outline only.

### Is `pom.xml` corrupted?

**Yes, completely.**

`pom.xml` starts with `# Backend_Foundation_Generation_Plan.md` and contains Markdown sections. It has no XML declaration, `<project>` root, Maven coordinates, parent, properties, dependencies, build plugins, or profiles.

Maven cannot parse it. This is not repairable through a small edit; it must be replaced by a valid project model derived from approved dependencies and versions.

### Are Java source files valid?

**No. All 82 Java files are invalid.**

Evidence from complete Java inspection:

- 0 package declarations
- 0 imports
- 0 usable classes, interfaces, enums, or records
- 0 Spring annotations
- 0 executable methods
- Every file contains Markdown headings and backend-plan prose

Words such as “class,” “interface,” or “enum” occur only in English sentences. No Java implementation exists.

Detailed per-file evidence is recorded in `docs/08_Implementation/Backend_Codebase_Audit.md`.

### Is `application.yml` valid?

**No.**

The following files contain Markdown documentation rather than YAML:

- `src/main/resources/application.yml`
- `src/main/resources/application-dev.yml`
- `src/main/resources/application-prod.yml`

They define no Spring application name, datasource, JPA, Flyway, server, logging, management, security, or profile configuration.

### Does Flyway configuration exist?

**No executable Flyway configuration exists.**

There is no valid POM dependency and no valid `spring.flyway` configuration. The migration directory name follows Spring Boot conventions, but folder placement alone does not configure Flyway.

### Are migrations valid SQL?

**No. All four backend migrations are invalid SQL.**

| File | Actual state |
| --- | --- |
| `V1__init_schema.sql` | Markdown backend plan; zero schema statements |
| `V2__seed_roles.sql` | Markdown backend plan; zero inserts |
| `V3__seed_admin_user.sql` | Markdown backend plan; zero inserts |
| `V4__seed_reference_data.sql` | Markdown backend plan; zero inserts |

`src/main/resources/db/schema/schema.sql` is also Markdown, not SQL.

The database reconciliation report identifies usable external inputs and a proposed migration responsibility sequence: `docs/08_Implementation/Migration_Reconciliation_Report.md`.

## Complete Backend Inspection

### Root and build artifacts — 9 files

All contain generated documentation instead of their declared format:

- `.editorconfig`
- `.env.example`
- `.mvn/wrapper/maven-wrapper.properties`
- `Backend_Foundation_Generation_Plan.md`
- `docker-compose.yml`
- `mvnw`
- `mvnw.cmd`
- `pom.xml`
- `README.md`

The Markdown plan file is the only filename whose format roughly matches its content. Even it is one of many near-duplicate plan variants and should be consolidated with canonical documentation.

### Main Java sources — 78 files

Every main-source file contains generated documentation instead of Java.

Affected areas:

- Application entry point
- API controllers
- Request/response DTOs
- API mappers and validation
- Exceptions and global error handling
- Application services and use cases
- Pagination and response wrappers
- Spring/JPA/OpenAPI configuration
- Domain enums and models
- Domain repositories
- Redis/cache infrastructure
- Kafka/messaging infrastructure
- Persistence entities and JPA repositories
- JWT/Spring Security infrastructure
- Shared constants, utilities, and validation

No named type in these packages exists as code.

### Test Java sources — 4 files

All contain generated documentation instead of tests:

- `AuthControllerTest.java`
- `PropertyServiceTest.java`
- `ApiIntegrationTest.java`
- `JwtServiceTest.java`

There are no JUnit annotations, test methods, assertions, mocks, Spring test contexts, or Testcontainers.

### Runtime resources — 11 files

All contain generated documentation:

- Four application YAML/profile files
- Four Flyway-named SQL files
- One schema SQL file
- `logback-spring.xml`
- Two message property files

The profile count includes the base `application.yml` plus `application-dev.yml` and `application-prod.yml`; none is parseable as Spring configuration.

## Documentation Stored Instead of Executable Code

Every executable/configuration extension in the backend is affected:

| Intended format | Affected examples | Actual content |
| --- | --- | --- |
| Maven XML | `pom.xml` | Markdown plan |
| Java | all `.java` files | Markdown plans |
| YAML | application profiles, Compose | Markdown plans |
| SQL | migrations and schema | Markdown plans |
| XML | `logback-spring.xml` | Markdown plan |
| Properties | wrapper and message bundles | Markdown plans |
| Shell | `mvnw` | Markdown plan |
| Batch | `mvnw.cmd` | Markdown plan |
| EditorConfig | `.editorconfig` | Markdown plan |
| Environment example | `.env.example` | Markdown plan |
| README/Markdown | README and foundation plan | Generated planning prose; format is readable but content is not an executable setup guide |

## Likely Generation Failure

The evidence indicates an automated generation/export operation wrote a backend foundation plan into every requested output path.

Indicators:

- Every file begins with the same Markdown filename/header pattern.
- Each file contains similar Overview, Architecture, Technologies, Implementation Steps, and Conclusion sections.
- Content varies slightly between files, suggesting repeated AI generation rather than one exact filesystem copy.
- Filenames and folder paths are plausible, while content never matches the extension.
- Apparent Java type names, tests, migrations, profiles, and infrastructure were created as labels only.

No evidence suggests that valid source code was later corrupted in place. Git currently treats the backend as untracked, so the repository does not provide a recoverable committed version of this backend.

## Files to Keep

### Keep as paths or planning references

- Keep the canonical folder name `propertypilot-backend/`.
- Keep the conventional Maven roots `src/main/java`, `src/main/resources`, and `src/test/java` as directory targets.
- Keep `Backend_Foundation_Generation_Plan.md` temporarily as a planning input only, then consolidate useful unique decisions into the existing canonical backend planning documents.
- Keep filenames from the initial approved vertical slice only as an inventory to review; do not keep their contents.

### Keep unchanged as executable artifacts

**None.**

No build, code, test, configuration, migration, container, or resource file is valid enough to retain unchanged.

## Files to Archive

Archive one snapshot of the current folder before rebuilding if recovery traceability is desired. Within that snapshot, the following may have limited historical value:

- `Backend_Foundation_Generation_Plan.md`
- `README.md`
- The file/folder inventory showing originally intended modules

Do not archive each invalid Java/config file as though it were source history; a single tagged repository snapshot or compressed artifact is sufficient.

After approved requirements have been extracted, the generated plan variants should not remain in the active source tree.

## Files to Regenerate

These require clean, format-valid replacements rather than repairs:

### Project/build foundation

- `pom.xml`
- Maven wrapper scripts and wrapper properties
- `.editorconfig`
- `.env.example`
- `docker-compose.yml` if local containers are part of the first development workflow

### Application foundation

- `PropertyPilotApplication.java`
- Valid package structure
- Base runtime configuration
- Environment-specific configuration using safe external secrets
- Logging configuration
- Message bundles only if the application uses them

### Database foundation

- Flyway configuration
- Reconciled PostgreSQL migrations
- Migration integration tests

Do not regenerate `schema.sql` alongside Flyway unless it has a separately defined, non-executable documentation purpose. A second executable schema source would recreate ownership ambiguity.

### Initial business implementation

Generate only the approved first slice:

- Authentication and authorization
- User/role support
- Customer
- Property
- Service request
- Common error handling and necessary pagination
- Auditing required by that slice
- Unit and integration tests

Do not regenerate the entire placeholder inventory merely because filenames exist.

## Files to Repair

Very little can be repaired in place because the intended content is absent.

The repair category is limited to documentation work:

- Consolidate useful parts of `Backend_Foundation_Generation_Plan.md` into existing canonical plans.
- Rewrite the backend `README.md` after real build/run/test commands exist.
- Align `.env.example` descriptions with actual configuration after the valid application is established.
- Correct documentation references that currently imply the backend is implemented.

All executable files require replacement/regeneration, not repair.

## Repair Versus Rebuild Decision

### Repair option

Rejected.

Repair would require replacing essentially every byte in every executable file while also redesigning dependencies, packages, schema ownership, tests, and runtime configuration. Calling that “repair” would obscure the fact that no implementation is being preserved.

### Rebuild option

Approved.

Rebuild the backend in the same canonical `propertypilot-backend/` path using a clean, minimal Spring Boot project. Preserve requirements and decisions from canonical documents, not placeholder source files.

This does not require changing the intended technology direction. Spring Boot 3, Java 21, Maven, PostgreSQL, Flyway, validation, security, and integration testing remain compatible with existing plans. Redis, Kafka, payments, notifications, and advanced operational modules should be deferred until actual requirements justify them.

## Recovery Risks

| Risk | Impact | Control |
| --- | --- | --- |
| Mistaking filenames for completed implementation | False progress and unsafe delivery estimates | Count only compiling/tested behavior |
| Copying standalone migrations directly | Duplicate tables and PostgreSQL failures | Follow migration reconciliation report and clean-database tests |
| Recreating all placeholder packages | Premature complexity | Implement one vertical slice first |
| Editing applied Flyway versions | Checksum failures/environment divergence | Inspect all Flyway histories before replacement |
| Shipping seeded admin credentials | Security compromise | Bootstrap admin through controlled deployment/identity process |
| Adding Redis/Kafka immediately | Unnecessary runtime/operational burden | Add only for measured/approved use cases |
| Mixing cleanup with prior documentation reorganization | Unreviewable Git change | Separate commits/change sets |
| Missing local toolchain | Inability to verify recovery | Provide Java 21/Maven or a reproducible container/CI toolchain first |

## Exact Next Steps

Execute in this order:

1. **Preserve a recovery reference.** Record the current backend tree in a Git branch/tag or other reviewed snapshot; do not treat it as valid source.
2. **Confirm migration history externally.** Check every existing database for `flyway_schema_history` before replacing V1–V4.
3. **Approve canonical inputs.** Use the MVP scope, canonical data dictionary/state catalog, OpenAPI YAML, authentication ADR, backend audit, and migration reconciliation report.
4. **Freeze the first vertical slice.** Limit it to authentication/user-role, customer, property, and service request unless product ownership approves additions.
5. **Remove the generated backend contents in a dedicated recovery change.** Preserve only the canonical folder path and any explicitly approved planning reference.
6. **Create a clean minimal Spring Boot 3 / Java 21 Maven foundation.** Include only dependencies needed for Web, Validation, JPA, Security, PostgreSQL, Flyway, Actuator, testing, and the approved API documentation approach.
7. **Establish reproducible verification.** Ensure Maven wrapper/build, Java version, formatting, unit testing, integration testing, and CI all work before domain implementation.
8. **Reconcile and validate Flyway migrations.** Use the PropertyPilot documentation DDL as the main input, implement the approved V1 onward sequence, and prove clean PostgreSQL migration.
9. **Implement authentication and the core domain slice incrementally.** Add real code and tests feature by feature; keep OpenAPI aligned with behavior.
10. **Add a local runtime configuration.** Provide safe example environment settings and a reproducible PostgreSQL/application startup workflow.
11. **Run mandatory gates.** Clean build, unit tests, integration tests, migration test, security baseline, health endpoint, and OpenAPI validation must pass.
12. **Update existing documentation.** Replace readiness claims and backend README instructions with verified commands and evidence.
13. **Archive/remove the recovery snapshot from active development.** Retain it only according to repository cleanup policy.
14. **Schedule later capabilities separately.** Visits/evidence/reports next; billing, marketplace, Redis, Kafka, and advanced operations only when their release scope is approved.

## Recovery Exit Criteria

Backend recovery is complete only when:

- `pom.xml` parses and a clean Maven build succeeds.
- Maven wrapper works from a clean checkout.
- Java packages compile under Java 21.
- Application configuration loads without embedded secrets.
- Flyway migrates a clean PostgreSQL database exactly once.
- The application starts and Actuator health reports readiness.
- Authentication and one customer/property/service-request workflow pass integration tests.
- OpenAPI describes the implemented endpoints and error responses.
- CI reproduces the same build and tests.
- No generated planning prose remains under executable file extensions.

## Final Assessment

`propertypilot-backend/` is **0% implemented as executable backend code**. The structure communicates intent, but all 102 files were generated incorrectly. Rebuild from scratch within the existing canonical path, using approved repository specifications and reconciled migrations as inputs. Do not attempt incremental repair of the current POM, Java, configuration, or SQL files.
