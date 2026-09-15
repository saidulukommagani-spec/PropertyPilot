# ADR-003: Service State Model

**Status:** Accepted  
**Date:** 2026-08-31  
**Owners:** Enterprise Architect, Service Operations Owner

## Context

The service lifecycle differs across the physical model, API material, state documents, and initial migration. In particular, the migration default `created` conflicts with the canonical `NEW` state and status-history naming.

## Decision

The State Model Catalog and Cross-Cutting Requirements are authoritative for service request lifecycle states and valid transitions. New service requests begin in `NEW`; payment, assignment, execution, review, completion, and cancellation transitions must use the canonical configured state model and create an immutable `service_request_status_history` record.

The legacy migration status `created` is not a valid canonical state. APIs, events, database constraints, test data, screens, and projections must use the canonical vocabulary.

## Consequences

- Existing migration/schema must be reconciled through forward-only controlled migration; no implicit status translation at runtime.
- A state transition requires authorisation, reason where policy requires it, correlation/audit data, and event publication only after durable commit.
- Consumers reject or safely ignore stale/invalid transitions and must be idempotent.

## References

- [State Model Catalog](../State_Model_Catalog.md)
- [Cross-Cutting Requirements](../Cross_Cutting_Requirements.md)
- [Database Physical Model](../04_Data/Database_Physical_Model.md)
- [Event Catalog](../Event_Catalog.md)
