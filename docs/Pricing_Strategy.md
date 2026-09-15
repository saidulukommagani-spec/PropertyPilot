# PropertyPilot Pricing Strategy

## Purpose

This document defines the business principles and governance for PropertyPilot pricing. It complements the implementation rules in [Pricing_Engine.md](Pricing_Engine.md), the available services in [Service_Catalog.md](02_Product/Service_Catalog.md), and subscription entitlements in [Subscription_Plans.md](01_Business/Subscription_Plans.md).

It does not define or override individual prices. The Pricing Engine remains the authoritative source for calculated customer pricing.

---

# Pricing Objectives

- Provide transparent, explainable pricing before checkout.
- Price services sustainably across property types, locations, and execution models.
- Keep subscription plans focused on clear entitlements and recurring customer value.
- Support customer choice between one-time services, add-ons, and subscriptions.
- Allow approved commercial changes without application deployment.

---

# Pricing Principles

1. Final prices shall be derived by the Pricing Engine from configured rules and the specific booking context.
2. A customer shall see an itemised estimate or quote before payment, including applicable service, travel, allowance, add-on, subscription, discount, and platform components.
3. Service availability and eligibility must be validated before a quote can be used for checkout.
4. Subscription plan documents define benefits and visit entitlements; they do not override dynamic pricing inputs.
5. Prices, discount rules, and bundles must be traceable to configured, effective-dated rules.
6. Manual price changes, high-value discounts, and exceptional pricing require approval and audit logging.

---

# Pricing Model

PropertyPilot supports the following commercial models:

| Model | Application | Governing Source |
|---|---|---|
| One-time service pricing | Verification, inspection, coordination, and execution services | Pricing Engine service rules |
| Subscription pricing | Recurring monitoring and NRI plans | Subscription Plans and Pricing Engine |
| Add-on pricing | Premium evidence, priority, emergency, weekend, and similar optional services | Pricing Engine add-on rules |
| Vendor quotation pricing | Vendor-supported or project-execution services | Approved vendor quotation and Pricing Engine |
| Bundle pricing | Compatible catalog services offered together | Configured bundle rule and service eligibility |

---

# Customer Quote Policy

A customer quote shall be based on the selected service, property, location, distance, schedule, add-ons, applicable subscription entitlement, and validated discounts. The quote must state its validity and be recalculated when a pricing input or rule changes.

An estimate supplied by the anonymous calculator is informational. A checkout-eligible quote is produced only after the booking information and service eligibility have been validated.

---

# Subscription Commercial Policy

- Subscriptions are priced for the enrolled property and configured service frequency.
- Included visits and benefits are consumed against the active plan entitlement at booking.
- Non-included services, excess visits, and non-entitled add-ons are priced independently.
- Upgrades may use approved prorated pricing. Downgrades take effect at the next renewal cycle, as defined in Subscription Plans.
- Renewal pricing is recalculated under the active rules and presented before renewal confirmation.
- Plan prices and entitlement changes must be versioned with effective dates so an active paid term is not changed retrospectively.

---

# Discount and Promotion Policy

Discounts may be delivered through configured promotions, coupons, referrals, approved administrative concessions, and eligible subscription benefits. Each rule must define eligibility, scope, validity, usage limits, stackability, approval requirements, and audit data.

The checkout calculation shall identify every applied benefit separately and prevent duplicate application of the same benefit. Refunds remain limited to the net paid amount and the applicable subscription usage and refund policy.

---

# Bundle Strategy

Bundles may group compatible services from the Service Catalog while retaining transparent, auditable line items and all underlying deliverables. Supported bundle themes include verification, inspection and evidence, recurring monitoring, plot care and security, construction coordination, rental management, agriculture, and NRI property management.

A bundle is available only when its component services meet the relevant property-type, coverage, documentation, execution-model, and vendor or agent availability rules.

---

# Governance and Controls

Pricing operations shall use effective-dated configuration with a rule owner, priority, status, approval trail, and change reason. All price calculations, overrides, discounts, and selected vendor quotations must be auditable.

Pricing changes shall be tested through simulation before activation. Approved changes must not alter a previously accepted quote or an active paid subscription term unless the governing policy explicitly permits it.

---

# Related Documents

- [Pricing_Engine.md](Pricing_Engine.md)
- [Service_Catalog.md](02_Product/Service_Catalog.md)
- [Subscription_Plans.md](01_Business/Subscription_Plans.md)
- [Subscription_Management.md](Subscription_Management.md)
- [Vendor_Management.md](01_Business/Vendor_Management.md)
