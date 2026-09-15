# API Deprecation and Versioning Policy

Document Type: API Lifecycle Governance  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review

---

## Purpose

This policy defines the lifecycle management, versioning standards, backward compatibility rules, deprecation process, and retirement governance for PropertyPilot APIs.

The purpose of this policy is to:
- maintain compatibility for API consumers
- reduce breaking change risk
- provide predictable release behavior
- ensure secure and auditable API evolution
- support migration planning and operational control

This policy applies to all internal, external, mobile, partner, vendor, and webhook APIs managed by PropertyPilot.

---

## Scope

This policy applies to:
- Internal APIs
- Public APIs
- Mobile APIs
- Partner APIs
- Vendor APIs
- Webhooks

The policy governs:
- API design
- endpoint versioning
- release and deprecation lifecycle
- compatibility decisions
- migration support
- retirement and cleanup
- governance approvals and documentation

---

## API Lifecycle

Every API must progress through the following lifecycle:

Draft
→ Development
→ Testing
→ Published
→ Deprecated
→ Retired

Definitions:
- Draft: proposed API or pre-approval version
- Development: in implementation, not yet available for consumer use
- Testing: validated internally or in staging
- Published: approved and available to consumers
- Deprecated: still functional but scheduled for retirement
- Retired: removed from service and no longer supported

No API may be treated as production-ready until it is published and approved under governance.

---

## Versioning Strategy

Standard:
- /api/v1
- /api/v2

Versioning rules:
- Major version for breaking changes
- Minor version for backward-compatible additions
- Patch version for fixes

Examples:
- /api/v1/users
- /api/v1.1/users
- /api/v1.1.1/users
- /api/v2/users

Versioning standards:
- Use a path-based version prefix for the public contract.
- Maintain a stable versioned API contract for production consumers.
- Do not silently alter request or response semantics in an existing major version.
- Minor and patch updates must preserve compatibility.
- New major versions must provide migration guidance and an explicit sunset timeline.

---

## Backward Compatibility Policy

### Allowed Changes
The following changes are allowed in a compatible version:
- addition of optional request fields
- addition of optional response fields
- addition of new endpoints
- new enum values if not required by clients
- expansion of field length or accepted values where safe and documented
- enhancement of error metadata without changing existing codes

### Disallowed Changes
The following are not allowed without a major version bump:
- removal of required request fields
- renaming fields or endpoints
- changing enum values or semantic meaning
- changing authentication or authorization requirements
- changing response shape for existing fields
- changing HTTP method semantics
- changing business validation logic that alters existing behavior unexpectedly

### Breaking Change Definition
A breaking change is any modification that:
- causes existing clients to fail without code changes
- removes or renames a field or endpoint
- alters required contracts or payload structures
- changes default behavior or business semantics in ways that impact integration
- requires a different authentication flow or token model
- changes success or error response design in incompatible ways

### Compatibility Matrix

| Change Type | Compatible in Same Version? | Requires New Major Version? |
|---|---|---|
| Add optional field | Yes | No |
| Add endpoint | Yes | No |
| Add enum value | Usually yes | No |
| Remove field | No | Yes |
| Rename field | No | Yes |
| Change required to optional | Yes, if non-breaking for clients | No |
| Change HTTP method | No | Yes |
| Change authentication scheme | No | Yes |
| Change response schema | No | Yes |
| Add error code | Yes | No |
| Change existing error semantics | No | Yes |

---

## API Naming Standards

### Resource Naming
- Use nouns, not verbs, for resources
- Use lowercase kebab-case or lower-case plural nouns consistently
- Prefer predictable collections and item paths
- Avoid overloaded endpoints with mixed business actions

Examples:
- /api/v1/customers
- /api/v1/properties/{propertyId}
- /api/v1/service-requests/{serviceRequestId}

### URI Standards
- use HTTPS only
- use path versioning
- use consistent plural nouns
- use resource IDs in path parameters
- use query parameters only for filtering, sorting, and pagination
- avoid embedding business logic in URI names

### HTTP Method Standards
- GET: read
- POST: create or action
- PATCH: partial update
- PUT: replace or full update
- DELETE: delete or revoke
- OPTIONS: preflight or capability checks where required

### Status Codes
Required use:
- 200 OK
- 201 Created
- 202 Accepted
- 204 No Content
- 400 Bad Request
- 401 Unauthorized
- 403 Forbidden
- 404 Not Found
- 409 Conflict
- 422 Unprocessable Entity
- 429 Too Many Requests
- 500 Internal Server Error
- 503 Service Unavailable

### Error Handling Standards
Every API must:
- return structured error response objects
- include error code, message, and trace ID
- classify errors as validation, authorization, not found, conflict, rate limit, or server
- avoid leaking sensitive implementation details
- support client retries only for retryable conditions

---

## Deprecation Process

### Trigger Conditions
An API is eligible for deprecation when:
- a new version has replaced the old contract
- usage falls below the adoption threshold
- the endpoint is no longer required by the product
- security or compatibility requirements require redesign
- a partner or internal consumer has been notified and migrated

### Approval Process
Deprecation requires:
- product owner approval
- architecture review
- API governance review
- security review when auth or risk changes are involved
- operations or support signoff if service-level commitments exist

### Communication Process
All deprecations must include:
- deprecation announcement
- migration date
- replacement endpoint or version
- expected behavior until retirement
- support contact and escalation path

### Migration Support
The API owner must provide:
- migration guide
- sample payloads
- timeline
- status of compatibility testing
- rollback or fallback guidance where applicable

### Retirement Process
Retirement occurs only after:
- migration window has passed
- deprecated endpoint usage is below threshold
- consumers have acknowledged or completed migration
- operational monitoring confirms low or no active usage
- governance approval is documented

---

## Deprecation Timeline

### Announcement
- Minimum 90 days for external APIs
- Minimum 30 days for internal APIs if low-risk
- Immediate notice for security or compliance-driven deprecations

### Migration Window
- The endpoint remains functional during the migration period.
- Error responses may include warnings for deprecation.
- Deprecation headers or metadata must be included where supported.

### Final Retirement
- final retirement occurs after all required migration windows and approvals
- the endpoint and related documentation are removed from active contract publication
- monitoring continues during a short post-retirement validation period

---

## Client Notification Strategy

### Email
Used for:
- external clients
- partner API consumers
- enterprise or regulated customer integrations

### Dashboard Notifications
Used for:
- internal platform consumers
- mobile app integration teams
- operational teams

### Release Notes
Required for:
- any version release
- any deprecation notice
- any change in behavior
- migration instructions

### Partner Notifications
Required for:
- vendor or partner APIs
- integration services with explicit contracts
- all externally hosted integrations

Partner notices must include:
- endpoint affected
- version impact
- migration instructions
- support contact
- deadline

---

## OpenAPI Governance

### Source of Truth
OpenAPI_Specification.yaml is the source of truth for API contract definition.

Rules:
- do not modify production contract in undocumented or ad hoc ways
- all API changes must be represented in OpenAPI before rollout
- release tags and versioned contracts must match implementation
- changes that are not in OpenAPI are considered non-compliant

### Review Process
OpenAPI review includes:
- architecture review
- schema validation
- security review
- data classification review
- backward compatibility review
- stakeholder signoff

### Publication Process
- published APIs must be surfaced in API_Catalog.md
- the catalog must reflect active version status and deprecation state
- stable versions must be clearly labelled
- deprecated routes must be marked as deprecated

### Approval Workflow
API publication requires:
- product approval
- technical design approval
- security approval
- governance signoff
- release readiness validation

---

## Webhook Versioning

### Version Strategy
Webhook endpoints must include explicit versioning where the consumer or provider is external and contracts may evolve.

Use:
- /api/v1/webhooks/payment
- /api/v1/webhooks/sms
- /api/v1/webhooks/whatsapp

### Backward Compatibility
Webhook payloads must maintain:
- backward-compatible field additions
- controlled, explicit event type contracts
- clear schema versioning
- replay-safe processing logic

### Retry Expectations
- retries are allowed for transient network or provider failures
- events must be idempotent and deduplicated
- retry loops must be bounded
- retryable failure classification must be explicit

### Migration Rules
- webhook consumers must support a migration window
- old signature or payload versions must be accepted during the migration period
- provider or consumer version changes must be communicated in advance
- no silent webhook schema breaking change is allowed

---

## Security Versioning Considerations

### Authentication Changes
Changing auth scheme requires:
- explicit product and security approval
- version bump or migration plan
- communication to all consumers
- dual support window if necessary

### Authorization Changes
Changes in access scope or role-based behavior require:
- contract review
- compatibility evaluation
- clear documentation
- migration note for impacted clients

### Token Lifecycle Changes
Changes to token lifetime, refresh policy, or claim structure require:
- governance approval
- client notification
- explicit migration support

---

## Documentation Requirements

Every API version and deprecation must be documented in:
- OpenAPI Updates
- API Catalog Updates
- Release Notes
- Migration Guides

Required documentation for each version:
- contract summary
- breaking changes
- new fields or endpoints
- deprecations
- migration steps
- support timeline
- retirement date

---

## Monitoring Requirements

### Usage Tracking
Track:
- total request count
- unique consumers
- active version usage
- failure rate
- latency
- error patterns

### Deprecated Endpoint Usage
Monitor:
- traffic to deprecated endpoints
- stale client versions
- failure and retry patterns
- remaining consumers

### Retirement Readiness
A deprecated API is ready for retirement when:
- active usage is below threshold
- all required consumers have migrated
- no critical dependencies remain
- governance approves retirement

---

## KPIs

Track the following:

- Version Adoption Rate
- Deprecated Endpoint Usage
- Migration Success Rate
- API Stability
- Mean Time to Resolve API Issues
- Consumer Breakage Rate
- Contract Validation Pass Rate

Targets:
- version adoption must trend upward toward the latest stable version
- deprecated endpoint usage must trend down
- migration success must be measured and reviewed monthly
- API stability must improve with release validation and contract governance

---

## Roles and Responsibilities

### Architecture Team
- define versioning standards
- approve major version changes
- align with enterprise patterns

### Engineering Team
- implement versioned contracts
- ensure compatibility and test coverage
- support migration

### Product Team
- approve scope and release windows
- define business impact and user impact
- support partner communication when required

### Operations Team
- monitor endpoint usage and deprecation signals
- validate readiness for retirement
- coordinate production rollback and migration support

### Partners
- comply with version deadlines
- migrate as required
- test against migration guidance

---

## Compliance Requirements

### Audit Requirements
All versioning and deprecation decisions must be recorded with:
- change description
- approval trail
- migration notice
- retirement record
- evidence of consumer communication

### Approval Requirements
All major changes require:
- architecture review
- security validation
- product approval
- governance signoff

### Retention Requirements
Version documentation and migration records must be retained for the required system governance period and archived when no longer active.

---

## Traceability

This policy must be traceable to:
- API → OpenAPI → Service → Screen → Workflow

Examples:
- customer onboarding flow → onboarding APIs → OpenAPI contract → service implementation
- payment workflow → payment endpoints → OpenAPI definitions → payment service → screen actions
- subscription lifecycle → plan and billing APIs → versioned contract → UI action and workflow

The contract and implementation must remain aligned through the full API lifecycle.

---

## Summary

API versioning and deprecation are governance controls required to protect compatibility, minimize customer disruption, and enable safe evolution of PropertyPilot services. All API changes must be reviewable, versioned, documented, and supported by a migration plan.

This policy must be enforced across all internal, external, partner, mobile, and webhook APIs. It is a core part of PropertyPilot’s architecture and operational governance.