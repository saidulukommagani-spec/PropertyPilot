# PropertyPilot Business Glossary

## Version

1.0

---

# Purpose

The Business Glossary defines the standard business terminology used throughout PropertyPilot.

The glossary provides:

- Consistent business definitions
- Shared vocabulary
- Data governance alignment
- Reporting consistency
- API consistency
- Event consistency

---

# Glossary Categories

```text
Customer

Lead

Property

Partner

Vendor

Contract

Revenue

Finance

Security

Platform

Reporting
```

---

# Customer Domain

## Customer

### Definition

An individual or organization that engages with PropertyPilot for services, products, or transactions.

### Owner

Customer Domain

---

## Customer ID

### Definition

Unique identifier assigned to a customer.

### Example

```text
CUS-100001
```

---

## Customer Type

### Definition

Classification of customer.

### Examples

```text
Individual

Corporate

Government

Partner
```

---

## Customer Status

### Definition

Current lifecycle state of a customer.

### Examples

```text
Active

Inactive

Suspended

Prospect
```

---

# Lead Domain

## Lead

### Definition

A prospective customer who has expressed interest in PropertyPilot offerings.

### Owner

Lead Domain

---

## Lead Source

### Definition

Origin of the lead.

### Examples

```text
Website

Referral

Campaign

Partner

Walk-In
```

---

## Lead Score

### Definition

Numerical value representing lead quality and likelihood of conversion.

---

## Lead Qualification

### Definition

Process of evaluating whether a lead meets business criteria.

---

## Lead Conversion

### Definition

Process of transforming a qualified lead into a customer.

---

# Property Domain

## Property

### Definition

Real estate asset managed or transacted through PropertyPilot.

---

## Property ID

### Definition

Unique identifier assigned to a property.

---

## Property Type

### Definition

High-level classification of property.

### Examples

```text
Residential

Commercial

Industrial

Agricultural
```

---

## Property Category

### Definition

Specific classification within a property type.

### Examples

```text
Apartment

Villa

Office

Warehouse

Land
```

---

## Property Listing

### Definition

Published representation of a property available for viewing or transaction.

---

## Property Valuation

### Definition

Estimated market value of a property.

---

# Partner Domain

## Partner

### Definition

External organization or individual collaborating with PropertyPilot.

---

## Partner Type

### Definition

Classification of partner.

### Examples

```text
Broker

Agency

Consultant

Referral Partner
```

---

## Partner Commission

### Definition

Compensation paid to a partner for successful business outcomes.

---

# Vendor Domain

## Vendor

### Definition

External supplier providing products or services.

---

## Vendor Type

### Definition

Classification of vendor.

### Examples

```text
Construction

Maintenance

Legal

Financial

Marketing
```

---

## Vendor Contract

### Definition

Formal agreement between PropertyPilot and a vendor.

---

# Contract Domain

## Contract

### Definition

Legally binding agreement governing a business relationship.

---

## Contract Type

### Definition

Classification of contract.

### Examples

```text
Sale

Lease

Service

Maintenance
```

---

## Contract Status

### Definition

Current lifecycle state of a contract.

### Examples

```text
Draft

Submitted

Approved

Active

Expired

Terminated
```

---

## Contract Renewal

### Definition

Extension or continuation of an existing contract.

---

# Revenue Domain

## Revenue

### Definition

Income generated through PropertyPilot business activities.

---

## Revenue Type

### Definition

Classification of revenue.

### Examples

```text
Property Sale

Lease Revenue

Commission

Service Revenue
```

---

## Revenue Recognition

### Definition

Accounting process of recording earned revenue.

---

## Invoice

### Definition

Financial document requesting payment.

---

## Payment

### Definition

Transfer of funds received from a customer or partner.

---

# Finance Domain

## Cost Center

### Definition

Business unit responsible for expenses.

---

## Budget

### Definition

Planned allocation of financial resources.

---

## Forecast

### Definition

Projected financial performance based on assumptions and trends.

---

# Security Domain

## User

### Definition

Person or system granted access to PropertyPilot.

---

## Role

### Definition

Collection of permissions assigned to users.

---

## Permission

### Definition

Authorization to perform a specific action.

---

## Authentication

### Definition

Process of verifying identity.

---

## Authorization

### Definition

Process of verifying access rights.

---

## Multi-Factor Authentication (MFA)

### Definition

Authentication requiring multiple verification factors.

---

# Platform Domain

## API

### Definition

Application Programming Interface exposing business functionality.

---

## Event

### Definition

Business occurrence communicated between systems.

---

## Service

### Definition

Independent software component implementing business capabilities.

---

## Domain

### Definition

Business capability area owning data, APIs, services, and events.

---

## Tenant

### Definition

Logical customer boundary within a multi-tenant platform.

---

# Reporting Domain

## Report

### Definition

Structured presentation of business information.

---

## Dashboard

### Definition

Visual representation of key metrics and business insights.

---

## KPI

### Definition

Key Performance Indicator used to measure business performance.

---

## Data Warehouse

### Definition

Centralized analytical repository used for reporting and analytics.

---

## Metric

### Definition

Quantitative measure used for analysis and reporting.

---

# Data Governance Terms

## Master Data

### Definition

Core business entities shared across systems.

### Examples

```text
Customer

Property

Partner

Vendor
```

---

## Reference Data

### Definition

Standardized values used for classification and validation.

### Examples

```text
Country

Currency

Property Type

Contract Status
```

---

## Data Steward

### Definition

Person responsible for data quality and governance.

---

## Data Owner

### Definition

Business owner accountable for a data domain.

---

# AI / ML Terms

## Model

### Definition

Trained machine learning artifact used to generate predictions.

---

## Prediction

### Definition

Output generated by a model.

---

## Training Data

### Definition

Historical data used to train machine learning models.

---

## Explainability

### Definition

Ability to understand how a model produced a result.

---

# Governance Terms

## Architecture Decision Record (ADR)

### Definition

Document capturing a significant architecture decision.

---

## Non-Functional Requirement (NFR)

### Definition

Quality attribute defining system behavior.

### Examples

```text
Availability

Performance

Security

Scalability
```

---

## Service Level Objective (SLO)

### Definition

Target level of service performance.

---

## Recovery Time Objective (RTO)

### Definition

Maximum acceptable outage duration.

---

## Recovery Point Objective (RPO)

### Definition

Maximum acceptable data loss window.

---

# Governance Rules

1. All business terms shall be defined in this glossary.

2. APIs shall use glossary terminology.

3. Events shall use glossary terminology.

4. Reports shall use glossary terminology.

5. Data models shall use glossary terminology.

6. Duplicate business definitions are prohibited.

7. Changes require Data Governance approval.

---

# Related Documents

Reference_Data_Management.md

Master_Data_Management.md

Canonical_Data_Model.md

Data_Governance.md

API_Catalog.md

Event_Catalog.md

Enterprise_Reporting.md

Domain_Boundaries.md

Architecture_Principles.md