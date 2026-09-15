# ADR-004: Subscription State Model

**Status:** Accepted  
**Date:** 2026-08-31  
**Owners:** Enterprise Architect, Subscription Owner

## Context

Subscription documentation contains legacy terms such as `DRAFT`, `PENDING_PAYMENT`, and `RENEWAL_DUE`, while Cross-Cutting Requirements and State Model Catalog define a different governed lifecycle. Without one model, billing, entitlement, scheduling, notifications, and support can diverge.

## Decision

The canonical subscription states are:

`PENDING`, `ACTIVE`, `PAUSED`, `PAST_DUE`, `SUSPENDED`, `EXPIRED`, and `CANCELLED`.

Only `ACTIVE` subscriptions generate new recurring service requests. Every state change preserves effective time, reason, actor, accepted plan version, billing anchor, entitlement impact, and lifecycle event. Renewal retry and schedule generation are idempotent.

Legacy terms are deprecated and may appear only in migration mapping/history until all consumers are reconciled.

## Consequences

- Subscription Management, API Catalog/OpenAPI, database schema/migrations, events, notifications, screens, and reports must adopt the canonical vocabulary.
- Upgrade, downgrade, pause, cancellation, auto-renewal consent, grace, and suspension controls require explicit API/screen/schema contracts before implementation.
- No service schedule may be generated for paused, past-due, suspended, expired, or cancelled subscription state.

## References

- [State Model Catalog](../State_Model_Catalog.md)
- [Cross-Cutting Requirements](../Cross_Cutting_Requirements.md)
- [Subscription Management](../Subscription_Management.md)
- [Subscription Plans](../01_Business/Subscription_Plans.md)
