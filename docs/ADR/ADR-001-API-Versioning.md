# ADR-001: API Versioning

**Status:** Accepted  
**Date:** 2026-08-31  
**Owners:** API Lead, Enterprise Architect

## Context

Cross-Cutting Requirements mandates `/api/v1`, while the OpenAPI server uses `/v1` and API Catalog examples are inconsistently prefixed. This creates gateway, client, documentation, and compatibility risk.

## Decision

All external PropertyPilot APIs shall use path-based major versioning with `/api/v1` as the current base path:

```text
https://api.propertypilot.com/api/v1/{resource}
```

OpenAPI is the canonical machine-readable contract. API Catalog, gateway configuration, SDKs, examples, and webhook callback paths must be generated from or verified against the approved OpenAPI version. Breaking changes require a new major path; additive compatible changes remain within the active major version.

## Consequences

- The OpenAPI `servers` entry and all API Catalog paths require reconciliation to `/api/v1`.
- Existing unprefixed or `/v1` examples are legacy documentation and must not be used for new integration work.
- API Versioning and Deprecation Policy governs compatibility, notice, and retirement.

## References

- [Cross-Cutting Requirements](../Cross_Cutting_Requirements.md)
- [OpenAPI Specification](../05_APIs/OpenAPI_Specification.yaml)
- [API Versioning and Deprecation Policy](../API_Versioning_and_Deprecation_Policy.md)
