# ADR-002: Authentication Strategy

**Status:** Accepted  
**Date:** 2026-08-31  
**Owners:** Security Architect, Product Owner

## Context

Cross-Cutting Requirements establishes mobile OTP verification as the MVP customer/agent authentication method. API Catalog documents password login, reset, and MFA routes without a matching approved MVP scope, creating inconsistent implementation expectations.

## Decision

For MVP customer and agent access, PropertyPilot shall use verified mobile OTP as the primary authentication method. OTPs are single-use, purpose-bound, time-bound, rate-limited, and stored only as salted hashes. Sessions are bound to verified identity, role, device/session context, and expiry.

Password/email authentication may be enabled only through a separately approved product and security decision. Privileged/admin authentication and MFA requirements are governed by Security Design and must not weaken customer/agent mobile verification.

## Consequences

- New customer/agent implementation shall not activate password login/reset merely because legacy API routes exist.
- API Catalog/OpenAPI must label non-MVP password routes as future or privileged-only until separately approved.
- OTP, session revocation, device risk, RBAC, rate limit, and audit controls are MVP security requirements.

## References

- [Cross-Cutting Requirements](../Cross_Cutting_Requirements.md)
- [Security Design](../03_Architecture/Security_Design.md)
- [KYC Operations Playbook](../KYC_Operations_Playbook.md)
