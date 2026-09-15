# Migration Reconciliation Report

**Audit date:** 2026-09-01  
**Target database:** PostgreSQL  
**Migration engine:** Flyway  
**Canonical runtime directory:** `propertypilot-backend/src/main/resources/db/migration/`

## Executive Result

The backend migration directory is now clean and contains no SQL files. This is the correct state until a reconciled sequence is approved.

The repository currently has two SQL sources:

- `docs/08_Implementation/database/`: six real, PropertyPilot-specific PostgreSQL DDL drafts containing 28 table definitions.
- `my-database-migration-project/migrations/`: eight Flyway-named drafts containing 27 table definitions, but the sequence is internally inconsistent, partially generic e-commerce SQL, and cannot run successfully from V1 through V8.

Decision:

1. Use `docs/08_Implementation/database/` as the primary schema input.
2. Use the standalone migration project only as a checklist for audit, index, subscription, notification, marketplace, and complaint concepts.
3. Do not copy the standalone V1–V8 sequence into the backend.
4. Publish the final reviewed sequence only under the canonical backend migration directory.

No SQL was generated or changed by this report.

## Current Inventory

### Documentation database scripts

| File | Real SQL | Tables created | Assessment |
| --- | --- | ---: | --- |
| `001_create_customers.sql` | Yes | 5 | Strong PropertyPilot identity/customer foundation |
| `002_create_properties.sql` | Yes | 9 | Strong geography/property foundation |
| `003_create_service_requests.sql` | Yes | 2 | Valid core workflow draft |
| `004_create_visits.sql` | Yes | 6 | Valid field-operations draft; scope requires confirmation |
| `005_create_evidence.sql` | Yes | 2 | Valid evidence/task draft with integrity gaps |
| `006_create_reports.sql` | Yes | 4 | Valid syntax; combines reports with later billing concepts |

These files are SQL, not generated documentation. They use PostgreSQL-specific UUID, JSONB, and TIMESTAMPTZ features. They are not ready for Flyway unchanged because their filenames do not follow Flyway’s default convention and their constraints/indexes/domain scope require reconciliation.

### Standalone migration project

| File | Real SQL | Assessment |
| --- | --- | --- |
| `V1__baseline_schema.sql` | Yes | Generic commerce schema; contains MySQL-only timestamp syntax |
| `V2__core_tables.sql` | Yes | Recreates four V1 tables and therefore fails after V1 |
| `V3__reference_and_subscription_tables.sql` | Yes | Creates a foreign key to `subscription_plans` before that table exists |
| `V4__payment_and_notification_tables.sql` | Yes | Creates tables but inserts unsafe operational sample rows |
| `V5__marketplace_and_complaint_tables.sql` | Yes | Generic/incomplete model; inserts unsafe sample rows |
| `V6__audit_and_indexes.sql` | Yes | Useful audit/index concept; incompatible integer identity model |
| `V7__constraints_and_seed_data.sql` | SQL template | References nonexistent placeholder table and column names |
| `V8__partitioning_and_rollback_support.sql` | SQL draft | Recreates `orders`; invalid partitioned primary-key design |

### Canonical backend migration directory

`propertypilot-backend/src/main/resources/db/migration/` currently contains **zero SQL files**.

The previously detected fake backend V1–V4 files were removed during backend recovery. They contained Markdown, were never real migrations, and no longer create direct version collisions in the working tree.

Before publishing a new V1, verify that no external database recorded the removed draft versions in `flyway_schema_history`.

## Real SQL Migrations

### Recommended primary inputs

All six scripts under `docs/08_Implementation/database/` are real SQL and are the closest match to the PropertyPilot domain:

- UUID users, roles, and customer profiles
- Leads
- Geography and coverage zones
- Properties, locations, and documents
- Service requests and status history
- Partners, vendors, agents, assignments, visits, and GPS verification
- Evidence and tasks
- Reports, contracts, invoices, and payments

They have coherent inter-file dependency ordering from `001` through `006`.

### Secondary inputs only

Standalone V3–V6 contain some real SQL concepts worth reviewing:

- Reference codes
- Subscription plans/features
- Notifications
- Complaints
- Audit records
- Additional indexes

Their actual table definitions should not be reused because they use incompatible identifiers, generic models, or incomplete relationships.

### Not acceptable as migrations

- Standalone V7 is an uninstantiated template, not deployable schema SQL.
- Standalone V8 is an example, not a viable forward migration.
- No backend migration currently exists.

## Duplicate Migrations

### Standalone V1 and V2

V1 and V2 both create:

- `users`
- `roles`
- `user_roles`
- `products`

V2 does not use `IF NOT EXISTS`, so execution fails with duplicate-relation errors immediately after V1.

### Cross-source identity tables

Both SQL sources define `users`, `roles`, and `user_roles`, but the schemas conflict:

| Concern | Standalone V1/V2 | Documentation `001` |
| --- | --- | --- |
| Identifier | `SERIAL id` | UUID domain-specific IDs |
| User identity | username/email | full name/email/mobile |
| Customer representation | none | separate `customers` table |
| Time type | `TIMESTAMP` | `TIMESTAMPTZ` |
| Role assignment key | composite pair | UUID key plus unique pair |

These are competing schemas, not additive migrations. Retain the UUID PropertyPilot design as the starting point.

### Payment duplication

- Standalone V4 creates singular `payment` linked to integer users.
- Documentation `006` creates plural `payments` linked to UUID customers, contracts, and invoices.

Only one payment aggregate may exist. The documentation model is closer to PropertyPilot, but it should be deferred until billing is in scope.

### Orders duplication

- Standalone V1 creates an ordinary `orders` table.
- Standalone V8 attempts to create a second, differently shaped partitioned `orders` table.

The duplication makes V8 impossible to apply after V1.

### Seed duplication and ambiguity

The repository has no approved canonical role/reference seed. Standalone V4, V5, and V7 instead insert sample payments, notifications, marketplace rows, and complaints. These are test fixtures, not production reference data.

## Invalid Migrations

### `V1__baseline_schema.sql`

Invalid for the PostgreSQL target because several columns use MySQL’s `ON UPDATE CURRENT_TIMESTAMP`. It also models products/orders rather than the core PropertyPilot workflow.

### `V2__core_tables.sql`

Invalid after V1 because it recreates four tables. It repeats the same MySQL-only timestamp syntax.

### `V3__reference_and_subscription_tables.sql`

Invalid in its written order because `subscriptions.plan_id` references `subscription_plans(id)` before `subscription_plans` is created.

### `V4__payment_and_notification_tables.sql`

DDL is broadly PostgreSQL-compatible, but the migration fails on a clean database when seed rows reference users 1 and 2 that were never inserted. Transactional sample data must not be in a production migration.

### `V5__marketplace_and_complaint_tables.sql`

The complaint `user_id` has no foreign key, the marketplace model is generic, and inserted complaints assume users exist. It does not match the documented vendor/quotation/assignment model.

### `V6__audit_and_indexes.sql`

SQL syntax is valid, but the foreign key expects integer `users.id`, which conflicts with the recommended UUID schema. The audit concept should be redesigned against canonical identities.

### `V7__constraints_and_seed_data.sql`

Completely non-executable. It alters and inserts into literal placeholders including:

- `core_table_name`
- `reference_table_name`
- `subscription_table_name`
- `payment_table_name`
- `notification_table_name`
- `marketplace_table_name`
- `complaint_table_name`
- `audit_table_name`

The assumed columns also do not match real tables.

### `V8__partitioning_and_rollback_support.sql`

Invalid after V1 because `orders` already exists. Its partitioned primary key excludes the partition key `order_date`, which PostgreSQL does not allow for a unique/primary constraint on a partitioned table. It only creates historical 2023/2024 partitions.

Its rollback comment is also unsafe: dropping the tables would delete data, contrary to the “no data loss” claim.

## PostgreSQL Incompatibilities

### Confirmed syntax incompatibility

`ON UPDATE CURRENT_TIMESTAMP` is not PostgreSQL column syntax.

Affected standalone tables:

- V1: users, roles, products
- V2: users, roles, products, categories

Choose either application-managed `updated_at` values or an approved PostgreSQL trigger strategy.

### Confirmed relational failures

- V2 creates relations already created by V1.
- V3 references a table before creation.
- V4 seeds nonexistent users.
- V7 references nonexistent tables and columns.
- V8 recreates `orders` and declares an invalid partitioned primary key.

### Valid PostgreSQL features in the documentation scripts

- `CREATE EXTENSION IF NOT EXISTS pgcrypto`
- `gen_random_uuid()`
- UUID foreign keys
- `TIMESTAMPTZ`
- `JSONB`
- PostgreSQL-compatible delete actions

### PropertyPilot schema issues requiring repair

The documentation SQL is closer to valid but still needs:

- Foreign-key indexes
- Common query indexes for status, customer, property, scheduling, geography, and assignment
- Check constraints or governed reference codes for state fields
- Coordinate and percentage range checks
- Monetary non-negative checks
- A consistent `updated_at` policy
- Consistent actor identity: `uploaded_by`, `changed_by`, and `generated_by` currently use strings rather than UUID references
- Resolution of polymorphic `tasks.assignee_type` and `assignee_id`
- Audit table design aligned to UUID identities
- Separation of MVP reports from later contracts/invoices/payments

## Flyway Numbering Conflicts

### Current working tree

There are no direct Flyway version collisions in the backend because its migration directory is empty.

### Candidate-source conflict

The standalone project already claims V1–V8. The documentation scripts claim numeric order `001`–`006` but do not match Flyway’s default `V<version>__<description>.sql` naming convention.

If both are copied and renamed without reconciliation, versions and schema responsibilities collide.

### Historical collision risk

Removed backend drafts previously claimed V1–V4. They were invalid Markdown, but an external system could theoretically have recorded attempts or manually repaired versions.

Required check before final numbering:

1. Inspect `flyway_schema_history` in every environment.
2. If no version has been applied anywhere, establish a new V1 sequence.
3. If a version was applied, do not edit its checksum; establish a controlled baseline or forward-only correction strategy.

### Docker initialization conflict

`my-database-migration-project/docker-compose.yml` mounts SQL under PostgreSQL’s `/docker-entrypoint-initdb.d`. That mechanism runs files only when initializing an empty volume and does not create Flyway history. It must not run alongside Spring Boot Flyway.

## Final Migration Sequence

This is the final recommended responsibility and dependency order. It is not generated SQL.

### V1 — PostgreSQL extensions, identity, roles, and customers

Include:

- Required extensions
- Users
- Roles
- User-role assignments
- Customers
- Leads only if approved for the first release
- Core unique constraints and indexes

Primary input: `001_create_customers.sql`.

### V2 — geography, coverage, properties, and documents

Include:

- Countries, states, districts, and localities
- Clusters and coverage zones
- Properties
- Property locations
- Property document metadata
- Coordinate/range constraints and relationship indexes

Primary input: `002_create_properties.sql`.

### V3 — service requests and status history

Include:

- Service requests
- Service-request status history
- Approved status constraints
- Customer/property/coverage/cluster/status/scheduling indexes

Primary input: `003_create_service_requests.sql`.

### V4 — agents, assignments, visits, and GPS verification

Include:

- Agents
- Agent assignments
- Visits
- GPS verification
- Partners and vendors only if approved for the first release

Primary input: the approved subset of `004_create_visits.sql`.

### V5 — evidence, tasks, and reports

Include:

- Evidence metadata
- Tasks if they are part of the core workflow
- Reports
- Actor-identity corrections
- Required access and query indexes

Primary inputs: `005_create_evidence.sql` and only the reports portion of `006_create_reports.sql`.

### V6 — audit records and remaining operational indexes

Include:

- Canonical audit records aligned to UUID users/agents
- Remaining foreign-key and operational indexes
- Approved database-managed timestamp mechanism, if selected

Conceptual input: standalone V6. Do not reuse its integer-key SQL.

### V7 — deterministic reference and role seed data

Include only:

- Approved roles
- Required state/reference codes
- Stable system-owned lookup data

Exclude:

- Admin credentials
- Sample users/customers
- Payments
- Notifications
- Marketplace rows
- Complaints
- Other transactional test data

### V8 — contracts, invoices, and payments, when release scope requires them

Include:

- Contracts
- Invoices
- Payments
- Billing constraints and indexes

Primary input: the billing portion of `006_create_reports.sql`.

If billing is not part of the current release, do not create an empty V8 or reserve its number. Assign the next version when billing implementation begins.

### Later versions

Add these only when their domain contracts are approved:

- Subscription plans, features, and subscriptions
- Notifications and delivery history
- Marketplace vendors, quotations, assignments, and commissions
- Complaints and escalations
- Data-retention and archival jobs
- Partitioning based on measured table volume and a rolling partition-management strategy

## Source Disposition

| Source | Final disposition |
| --- | --- |
| Documentation `001`–`005` | Primary reconciliation inputs |
| Documentation `006` | Split reports from later billing responsibilities |
| Standalone V1–V2 | Reject; duplicate, generic, and PostgreSQL-incompatible |
| Standalone V3–V5 | Retain requirements ideas only; reject SQL |
| Standalone V6 | Retain audit/index concepts only; rewrite against canonical UUID schema |
| Standalone V7 | Delete after report acceptance; uninstantiated template |
| Standalone V8 | Delete after report acceptance; conflicting and invalid example |
| Backend migration directory | Canonical destination; keep empty until reconciled SQL passes review |

## Validation Gates

The final migration set is acceptable only when:

- Exactly one migration exists per Flyway version.
- A clean supported PostgreSQL database migrates from empty to latest.
- A second startup performs no duplicate schema or seed operation.
- All referenced objects exist before their foreign keys are created.
- No MySQL-only or placeholder syntax remains.
- No generic product/order schema remains without explicit PropertyPilot ownership.
- IDs match the UUID API/data-model strategy.
- Status, monetary, coordinate, percentage, and temporal constraints are tested.
- Required foreign-key and query indexes are verified.
- Seeds contain deterministic system/reference data only.
- Schema objects align with the canonical data dictionary and OpenAPI contract.
- Integration tests validate both migrations and important constraints.
- Released versioned migrations are immutable.

## Final Recommendation

Build a new Flyway history from the PropertyPilot-specific documentation SQL, using the V1–V7 core sequence above and deferring billing to V8 or later. Do not import the standalone V1–V8 history. Keep the backend migration directory empty until the reconciled SQL is reviewed and successfully tested against a clean PostgreSQL instance.
