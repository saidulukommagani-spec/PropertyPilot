# PropertyPilot V28 Reconciliation Result

**Date:** 2026-10-06  
**Scope:** Reconcile `V28__configure_launch_services.sql` against the reported PostgreSQL schema drift. V029–V033 were not applied or modified.

## Result

V28 was made idempotent and applied successfully by Flyway. Flyway advanced the `public` schema from version 027 to version 28 and reported that exactly one migration was applied. The normal Flyway execution recorded the successful migration; no manual edits were made to `flyway_schema_history`.

Execution was constrained to target version 28. V029–V033 remain pending and were not run.

## Change made

- Added `IF NOT EXISTS` to each of V28's 11 `services` column additions. The eight previously existing columns were skipped; the three reported missing columns (`gps_radius_km`, `is_remote`, and `billable`) were added.
- Wrapped each of the five check-constraint additions in a catalog guard, so an existing same-named constraint is preserved and not added twice.
- Kept the migration's column comments.
- Did not drop columns or constraints, rewrite existing data, create another migration, or modify V029–V033.

The full diff was presented before modification, and the user approved applying it.

## Validation and startup

The Flyway CLI executable was unavailable, so validation used the repository Maven wrapper and Flyway Maven Plugin 11.7.2.

A plain `validate` reported V029–V033 as pending, as expected because execution was intentionally stopped at V28. Validation with pending migrations ignored completed successfully:

```text
Successfully validated 33 migrations
BUILD SUCCESS
```

The dev application was started with Flyway target 28:

```powershell
.\mvnw.cmd "-Dspring-boot.run.profiles=dev" "-Dspring-boot.run.arguments=--spring.flyway.target=28" spring-boot:run
```

Flyway validated the migrations, migrated from 027 to 28, and logged:

```text
Successfully applied 1 migration to schema "public", now at version v28
```

Spring Boot then stopped during Hibernate schema validation with an existing schema mismatch unrelated to V28:

```text
invoices.currency_code: found bpchar (Types#CHAR), expected char(3) (Types#VARCHAR)
```

Thus V28 application succeeded, but the full dev application did not finish starting. No later migration was allowed to run.

## Validation caveat

Flyway 11.7.2 emitted a compatibility warning that PostgreSQL 18.6 is newer than its tested range (through PostgreSQL 17). The migration and validation nevertheless completed successfully in this environment.

## Next blocker

Investigate the `invoices.currency_code` Hibernate validation mismatch separately. V029–V033 are still pending; apply them only after a deliberate review of their schema effects and the environment's reconciliation state.
