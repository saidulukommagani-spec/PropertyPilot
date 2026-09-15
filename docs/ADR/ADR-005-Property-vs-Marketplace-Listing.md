# ADR-005: Property Versus Marketplace Listing

**Status:** Accepted  
**Date:** 2026-08-31  
**Owners:** Product Owner, Data Architect, API Lead

## Context

The physical model treats `properties` as customer-managed property records. Some API Catalog material describes `POST /properties` as marketplace listing creation, conflating property ownership/operations with optional marketplace publication and risking unauthorised exposure of property/contact data.

## Decision

`properties` is the canonical managed-property aggregate used for customer ownership, service eligibility, inspections, monitoring, reports, and operational history. `marketplace_listings` is a separate optional aggregate representing an authorised buy/sell/rental publication linked to a property and customer.

Marketplace listing creation, publication, inquiry, contact disclosure, and commission use dedicated marketplace resources and permissions. A managed property does not become public merely by being created, and marketplace publication never grants ownership or operational authority.

## Consequences

- API/OpenAPI must use distinct managed-property and marketplace-listing routes and request/response models.
- Property ownership, exact coordinates, documents, and contact details remain protected under property/marketplace disclosure rules.
- Marketplace queries, listings, inquiries, disclosures, commissions, and privacy controls remain linked to—not merged with—the managed-property record.

## References

- [Database Physical Model](../04_Data/Database_Physical_Model.md)
- [Canonical Data Dictionary](../Canonical_Data_Dictionary.md)
- [Marketplace Management](../02_Product/Marketplace_Management.md)
- [Cross-Cutting Requirements](../Cross_Cutting_Requirements.md)
- [Traceability Matrix](../Traceability_Matrix.md)
