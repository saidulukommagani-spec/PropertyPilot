# PropertyPilot Flyway Reconciliation Plan

**Scope:** PostgreSQL schema history through V033; Flyway 11.x. This is a read-only repository audit and proposed recovery plan. No migration, database, or `flyway_schema_history` changes were made.

## A. Current schema state

### V028 / `services`

The repository migration is named `V28__configure_launch_services.sql` (unpadded `28`; the request calls it V028). It expects to add 11 columns, five check constraints, and comments on all 11 columns.

| V028 object | Expected definition | Evidence from the supplied database inspection |
|---|---|---|
| `requires_visit` | `BOOLEAN NOT NULL DEFAULT FALSE` | Present |
| `visit_mandatory` | `BOOLEAN NOT NULL DEFAULT FALSE` | Present |
| `max_visits_allowed` | `INTEGER NOT NULL DEFAULT 0` | Present |
| `requires_gps` | `BOOLEAN NOT NULL DEFAULT FALSE` | Present |
| `gps_radius_km` | `NUMERIC(8,3) NOT NULL DEFAULT 0.200` | Not reported; existence and definition unverified |
| `requires_before_photo` | `BOOLEAN NOT NULL DEFAULT FALSE` | Present |
| `requires_after_photo` | `BOOLEAN NOT NULL DEFAULT FALSE` | Present |
| `requires_signature` | `BOOLEAN NOT NULL DEFAULT FALSE` | Present |
| `requires_document_upload` | `BOOLEAN NOT NULL DEFAULT FALSE` | Present |
| `is_remote` | `BOOLEAN NOT NULL DEFAULT FALSE` | Not reported; existence and definition unverified |
| `billable` | `BOOLEAN NOT NULL DEFAULT TRUE` | Not reported; existence and definition unverified |
| `ck_services_max_visits` | `max_visits_allowed >= 0` | Unverified |
| `ck_services_remote_visit` | Remote service cannot require visit, mandatory visit, or GPS | Unverified |
| `ck_services_mandatory_visit` | Mandatory visit implies visit required | Unverified |
| `ck_services_gps_visit` | GPS implies visit required | Unverified |
| `ck_services_gps_radius` | `gps_radius_km > 0` | Unverified |
| Column comments | Comments for each of the 11 columns | Unverified |

V012 creates the base `services` table without these 11 operational columns. A repository search finds no V001–V027 migration that adds them. Therefore, the eight reported columns came from outside the tracked V001–V027 migration chain (for example, manual SQL, another schema-management path, or a restored/drifted database). Their presence alone does not establish that their types, defaults, nullability, constraints, or comments match V028.

### V029–V033 objects

The supplied evidence contains no live catalog inventory for these migrations. The following states are therefore **unknown**, not assumed missing or present:

| Migration | Objects expected by the repository migration | Database state |
|---|---|---|
| V029 `align_evidence_catalog_schema` | `evidence_types`; audit trigger; evidence type/request/file/remarks/latitude/longitude columns; backfill; nullability and FKs/check; evidence indexes; removal of old `evidence_type`, old check and index; comments | Unknown |
| V030 `seed_launch_pricing_profiles` | `STANDARD` and `CUSTOMER` pricing profiles, plus eight parameter codes per profile (`TRAVEL_RATE`, `AGENT_DA`, `OVERHEAD_PERCENT`, `PROPERTYPILOT_MARGIN_PERCENT`, `GST_PERCENT`, `TRAVEL_DISTANCE_MODE`, `ETA_DISCOUNT_PERCENT`, `ETA_DISCOUNT_MIN_DAYS`) | Unknown; inserts are conflict-tolerant, but existing rows are not reconciled |
| V031 `formal_quote_lifecycle` | Revised `ck_estimate_status`; five acceptance/rejection columns on `pricing_estimates`; partial expiry index; comments | Unknown |
| V032 `link_service_requests_to_quotes` | `service_requests.pricing_estimate_id` and FK; unique partial index; immutable-price function and trigger; comment | Unknown |
| V033 `monitoring_run_foundation` | `monitoring_schedules.source_service_request_id`, nullability/constraint/FK changes and unique index; `monitoring_runs` table, constraints/indexes, immutable trigger/function, comments | Unknown |

### Required read-only database inventory

Run these against the affected database before attempting repair. They are reads only.

```sql
-- Full Flyway ledger; retain the output as incident evidence.
SELECT installed_rank, version, description, type, script, checksum,
       installed_on, success
FROM flyway_schema_history
ORDER BY installed_rank;

-- V028 service columns: definitions and comments.
SELECT c.column_name, c.data_type, c.udt_name, c.is_nullable,
       c.column_default,
       col_description(to_regclass(format('%I.%I', c.table_schema, c.table_name)), c.ordinal_position) AS comment
FROM information_schema.columns c
WHERE c.table_schema = current_schema()
  AND c.table_name = 'services'
  AND c.column_name = ANY (ARRAY[
    'requires_visit', 'visit_mandatory', 'max_visits_allowed', 'requires_gps',
    'gps_radius_km', 'requires_before_photo', 'requires_after_photo',
    'requires_signature', 'requires_document_upload', 'is_remote', 'billable'
  ])
ORDER BY c.ordinal_position;

-- V028 check definitions and validation state.
SELECT conname, pg_get_constraintdef(oid) AS definition, convalidated
FROM pg_constraint
WHERE conrelid = to_regclass('services')
  AND conname = ANY (ARRAY[
    'ck_services_max_visits', 'ck_services_remote_visit',
    'ck_services_mandatory_visit', 'ck_services_gps_visit',
    'ck_services_gps_radius'
  ])
ORDER BY conname;

-- V028 value preflight. Run after confirming all referenced columns exist.
SELECT count(*) FILTER (WHERE max_visits_allowed < 0) AS negative_visit_limits,
       count(*) FILTER (WHERE gps_radius_km <= 0) AS invalid_gps_radii,
       count(*) FILTER (WHERE is_remote AND (requires_visit OR visit_mandatory OR requires_gps)) AS invalid_remote_rules,
       count(*) FILTER (WHERE visit_mandatory AND NOT requires_visit) AS mandatory_without_visit,
       count(*) FILTER (WHERE requires_gps AND NOT requires_visit) AS gps_without_visit
FROM services;

-- Presence check for representative later-migration objects. This is not a
-- substitute for checking every column, constraint, index, trigger, and definition.
SELECT name, to_regclass(name) AS relation
FROM (VALUES
  ('evidence_types'), ('pricing_profiles'), ('pricing_profile_parameters'),
  ('pricing_estimates'), ('service_requests'), ('monitoring_schedules'), ('monitoring_runs')
) AS expected(name);

-- V030 seeded data: inspect actual values before relying on ON CONFLICT behavior.
SELECT p.profile_code, p.active_flag, x.parameter_code,
       x.parameter_value, x.active_flag AS parameter_active
FROM pricing_profiles p
LEFT JOIN pricing_profile_parameters x ON x.pricing_profile_id = p.pricing_profile_id
WHERE p.profile_code IN ('STANDARD', 'CUSTOMER')
ORDER BY p.profile_code, x.parameter_code;

-- Later-migration column inventory.
SELECT table_name, column_name, data_type, udt_name, is_nullable, column_default
FROM information_schema.columns
WHERE table_schema = current_schema()
  AND ( (table_name = 'evidence' AND column_name = ANY (ARRAY[
          'evidence_type_id', 'service_request_id', 'file_name', 'remarks', 'latitude', 'longitude', 'evidence_type'
        ]))
     OR (table_name = 'pricing_estimates' AND column_name = ANY (ARRAY[
          'accepted_at', 'accepted_by_user_id', 'rejected_at', 'rejected_by_user_id', 'rejection_reason'
        ]))
     OR (table_name = 'service_requests' AND column_name = 'pricing_estimate_id')
     OR (table_name = 'monitoring_schedules' AND column_name = ANY (ARRAY[
          'customer_subscription_id', 'source_service_request_id'
        ])) )
ORDER BY table_name, column_name;

-- Later trigger/function/index inventory. Compare definitions to the SQL files,
-- not just names, before allowing the corresponding version to run.
SELECT 'trigger' AS object_type, event_object_table AS table_name, trigger_name AS object_name,
       action_statement AS definition
FROM information_schema.triggers
WHERE trigger_schema = current_schema()
  AND trigger_name = ANY (ARRAY[
    'trg_evidence_types_audit', 'trg_service_request_price_immutable', 'trg_monitoring_runs_immutable'
  ])
UNION ALL
SELECT 'index', schemaname || '.' || tablename, indexname, indexdef
FROM pg_indexes
WHERE schemaname = current_schema()
  AND indexname = ANY (ARRAY[
    'idx_evidence_service_request', 'idx_evidence_type_id', 'idx_estimates_open_expiry',
    'uq_service_requests_quote', 'uq_monitoring_schedule_purchase_request',
    'idx_monitoring_runs_schedule_history', 'idx_monitoring_runs_request'
  ]);
```

If the history table is not in the active search path, qualify it with the schema configured for Flyway. The repository leaves `defaultSchema` unset, so confirm the deployment's effective schema/search path rather than assuming `public`.

## B. Migration history state

Per the reported ledger, V001–V027 succeeded and V028 is not recorded as a successful migration. The repository resolves this migration from `V28__configure_launch_services.sql`; V029 is physically named `V29__align_evidence_catalog_schema.sql`, while V030–V033 are zero-padded. The version ordering is 28, 29, 30, 31, 32, 33. Confirm the exact `version`, `script`, and `success` values using the history query above and `flyway info`; this report had no connection to the live database.

Expected source order:

1. V28 configure launch-service execution flags
2. V29 normalize evidence catalog
3. V030 seed launch pricing profiles
4. V031 formal quote lifecycle
5. V032 link service requests to accepted quotes
6. V033 add monitoring run foundation

Even though V029–V033 do not directly reference V028's new service columns in their SQL, they remain pending behind the unresolved version 28 migration under normal sequential Flyway execution. V030 depends on the V025/V026 pricing tables; V031 uses `pricing_estimates` and `users`; V032 uses `pricing_estimates` and `service_requests`; V033 uses `monitoring_schedules`, `service_requests`, `properties`, `customer_subscriptions`, and `users`. V029 uses `evidence`, `visits`, `service_requests`, `users`, and the existing audit trigger function.

## C. Root cause

`V28__configure_launch_services.sql` is not idempotent: its first statement uses `ADD COLUMN` without `IF NOT EXISTS`. PostgreSQL rejects that statement at `requires_visit` because the column already exists. Since none of V001–V027 creates that column, the live schema has drifted from the Flyway ledger through an out-of-band schema change or a different provisioning path.

With standard Flyway transaction handling on PostgreSQL, a failed versioned migration is rolled back as a unit. The repository has no custom `executeInTransaction=false` setting. Thus the failed V028 attempt is not, by itself, evidence that V028 partially applied. The supplied pre-existing columns are evidence of out-of-band state. The rest of V028 may still be partially present from a separate manual application; only catalog inspection can establish that.

Do not infer success from matching column names: `ADD COLUMN IF NOT EXISTS` does not reconcile an existing column's type, default, nullability, or data. Likewise, a same-named constraint may have a different definition or be `NOT VALID`.

## D. Recommended repair strategy — Option C: Rebuild V028 as idempotent

**Chosen strategy: Option C only.** Make the still-unapplied V028 migration safely re-runnable, then let Flyway execute and record it as successful. Do not insert/update history rows manually and do not create V034 to represent the V028 work while V028 remains pending. Flyway's `repair` removes failed migration entries; it does not mark V028 successful. Use `repair` only after the migration file has been corrected and rehearsed, and pass exactly the same migration locations used by `migrate`.

This recommendation assumes the history query confirms no successful V028 row. If V028 is actually recorded successful in the target, stop: never rewrite a successfully applied versioned migration; reconcile that different state with a new forward migration instead.

### Idempotent V028 SQL to use in the reviewed migration

This SQL is a proposed replacement body for V028, not an edit made by this audit. First inspect existing column/constraint definitions and repair any mismatches deliberately. The constraint guards use `to_regclass('services')`, matching the migration's unqualified table reference and current search path.

```sql
ALTER TABLE services
    ADD COLUMN IF NOT EXISTS requires_visit BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS visit_mandatory BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS max_visits_allowed INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS requires_gps BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS gps_radius_km NUMERIC(8,3) NOT NULL DEFAULT 0.200,
    ADD COLUMN IF NOT EXISTS requires_before_photo BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS requires_after_photo BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS requires_signature BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS requires_document_upload BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS is_remote BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS billable BOOLEAN NOT NULL DEFAULT TRUE;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('services') AND conname = 'ck_services_max_visits') THEN
        ALTER TABLE services ADD CONSTRAINT ck_services_max_visits CHECK (max_visits_allowed >= 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('services') AND conname = 'ck_services_remote_visit') THEN
        ALTER TABLE services ADD CONSTRAINT ck_services_remote_visit CHECK (NOT is_remote OR (NOT requires_visit AND NOT visit_mandatory AND NOT requires_gps));
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('services') AND conname = 'ck_services_mandatory_visit') THEN
        ALTER TABLE services ADD CONSTRAINT ck_services_mandatory_visit CHECK (NOT visit_mandatory OR requires_visit);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('services') AND conname = 'ck_services_gps_visit') THEN
        ALTER TABLE services ADD CONSTRAINT ck_services_gps_visit CHECK (NOT requires_gps OR requires_visit);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('services') AND conname = 'ck_services_gps_radius') THEN
        ALTER TABLE services ADD CONSTRAINT ck_services_gps_radius CHECK (gps_radius_km > 0);
    END IF;
END $$;

COMMENT ON COLUMN services.requires_visit IS 'Whether this service requires at least one completed visit.';
COMMENT ON COLUMN services.visit_mandatory IS 'Legacy-compatible explicit mandatory visit policy.';
COMMENT ON COLUMN services.max_visits_allowed IS 'Maximum scheduled visits; zero means no configured limit.';
COMMENT ON COLUMN services.requires_gps IS 'Whether a validated GPS check is required to complete the service.';
COMMENT ON COLUMN services.gps_radius_km IS 'Maximum GPS distance from the property location allowed for this service.';
COMMENT ON COLUMN services.requires_before_photo IS 'Whether BEFORE_PHOTO evidence is required.';
COMMENT ON COLUMN services.requires_after_photo IS 'Whether AFTER_PHOTO evidence is required.';
COMMENT ON COLUMN services.requires_signature IS 'Whether SIGNATURE evidence is required.';
COMMENT ON COLUMN services.requires_document_upload IS 'Whether DOCUMENT evidence is required.';
COMMENT ON COLUMN services.is_remote IS 'Whether the service is delivered remotely and cannot require visits or GPS.';
COMMENT ON COLUMN services.billable IS 'Whether this catalog service is chargeable, subject to configured discounts.';
```

The constraint guards only prevent duplicate-name errors; they intentionally do not silently replace a conflicting existing definition. Compare every pre-existing definition with the expected checks in V028. If any differ, stop and prepare a reviewed corrective SQL action before migration. Likewise, verify all existing rows pass the five checks using the value-preflight query.

### Exact Flyway commands

Run from `C:\PropertyPilot\propertypilot-backend` using the Flyway 11.x CLI, against the intended environment only. Set `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` in the shell/secret manager; do not put credentials in command history.

```powershell
$env:FLYWAY_URL = $env:DB_URL
$env:FLYWAY_USER = $env:DB_USERNAME
$env:FLYWAY_PASSWORD = $env:DB_PASSWORD
$flywayArgs = @('-locations=filesystem:src/main/resources/db/migration')

# Read-only checks first:
flyway @flywayArgs info
flyway @flywayArgs validate

# After backup/snapshot, reviewed V028 edit, and rehearsal on a clone:
flyway @flywayArgs repair
flyway @flywayArgs info

# Apply only through version 28; confirm Flyway reports V28 successful before continuing.
flyway @flywayArgs -target=28 migrate
flyway @flywayArgs info
flyway @flywayArgs validate
```

Then compare V029 objects against its migration before allowing it to execute. If those objects are not already present or conflicting, apply one version at a time and verify after each. Repeat the same `-target` pattern for versions 29, 30, 31, 32, and 33; finally run `flyway @flywayArgs migrate` and `flyway @flywayArgs validate`. Do not issue one unbounded `migrate` until preflight establishes that none of V029–V033's non-idempotent DDL will collide with out-of-band objects.

If the target's Flyway configuration has locations other than the repository default, replace `-locations` with the exact same value for **all** `info`, `validate`, `repair`, and `migrate` commands.

### Strategy comparison

- **Option A rejected:** Flyway `repair` does not mark a version as applied; manual history edits are prohibited and would hide schema drift.
- **Option B rejected:** a later V034 cannot run before pending V028 in normal order, and adding V034 does not clear the duplicate-column failure.
- **Option C selected:** idempotent V028 can add missing objects and preserve matching pre-existing objects; Flyway then records the migration through normal execution.
- **Option D not selected:** no need to baseline or rebuild the database based on the evidence currently supplied; those approaches are more disruptive and obscure the precise drift.

## Evidence limits and source references

No live connection, `psql` executable, or catalog export was available during this audit. Only the eight columns stated in the request are treated as confirmed live objects. Execute the read-only SQL above to complete environment-specific presence/definition checks before repair.

Repository sources: `V012__create_service_catalog_and_capabilities.sql`, `V28__configure_launch_services.sql`, and `V29__align_evidence_catalog_schema.sql` through `V033__monitoring_run_foundation.sql`; Flyway is configured at `classpath:db/migration`, and Hibernate uses `ddl-auto: validate`.

Flyway references: [Repair command behavior](https://documentation.red-gate.com/flyway/reference/commands/repair), [Flyway FAQ: rollback and failed-migration repair](https://documentation.red-gate.com/flyway/reference/usage/frequently-asked-questions), [Flyway schema history](https://documentation.red-gate.com/flyway/flyway-concepts/migrations/flyway-schema-history-table).
