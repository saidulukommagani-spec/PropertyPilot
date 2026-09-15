# Data Dictionary Template

## Version

1.0

---

# Purpose

This template standardizes the documentation of enterprise data elements across PropertyPilot.

The Data Dictionary provides:

- Data element definitions
- Data ownership
- Data classifications
- Data quality rules
- Data lineage references
- Integration consistency

---

# Data Dictionary Metadata

| Attribute | Value |
|------------|---------|
| Domain | |
| System | |
| Service | |
| Data Owner | |
| Data Steward | |
| Version | |
| Last Updated | |
| Status | Draft / Approved |

---

# Entity Information

## Entity Name

Example:

```text
Customer
```

---

## Business Definition

Describe the business meaning of the entity.

Example:

```text
Represents an individual or organization that engages with PropertyPilot.
```

---

## Domain

Example:

```text
Customer
```

---

## System of Record

Example:

```text
Customer Service
```

---

## Related Documents

```text
Customer_Data_Model.md

Canonical_Data_Model.md

Business_Glossary.md
```

---

# Entity Data Elements

| Attribute | Description |
|------------|-------------|
| Field Name | Physical attribute name |
| Business Name | Business-friendly name |
| Definition | Business definition |
| Data Type | Technical type |
| Length | Maximum size |
| Mandatory | Yes / No |
| Primary Key | Yes / No |
| Classification | Data sensitivity |
| Example | Sample value |
| Owner | Data owner |

---

# Example: Customer Entity

| Field Name | Business Name | Definition | Type | Length | Mandatory | PK | Classification | Example |
|------------|--------------|------------|------|---------|-----------|----|---------------|---------|
| customerId | Customer ID | Unique customer identifier | UUID | 36 | Yes | Yes | Internal | CUS-10001 |
| firstName | First Name | Customer first name | String | 100 | Yes | No | Confidential | John |
| lastName | Last Name | Customer last name | String | 100 | Yes | No | Confidential | Smith |
| email | Email Address | Customer email | String | 255 | Yes | No | Confidential | user@email.com |
| status | Customer Status | Customer lifecycle status | String | 20 | Yes | No | Internal | Active |

---

# Data Types Standard

## String

```text
VARCHAR
TEXT
```

---

## Numeric

```text
INTEGER

BIGINT

DECIMAL
```

---

## Date

```text
DATE

TIMESTAMP

DATETIME
```

---

## Boolean

```text
TRUE

FALSE
```

---

## Identifier

```text
UUID
```

---

# Data Classification

Reference:

```text
Security_Controls_Catalog.md
```

---

| Classification | Description |
|---------------|-------------|
| Public | Publicly available |
| Internal | Internal business data |
| Confidential | Sensitive business data |
| Restricted | Highly sensitive data |

---

# Example Classifications

| Data Element | Classification |
|--------------|----------------|
| Customer Name | Confidential |
| Customer Email | Confidential |
| Property Value | Restricted |
| Revenue Amount | Restricted |
| Property Type | Internal |

---

# Validation Rules

Document validation requirements.

Example:

| Field | Rule |
|---------|---------|
| Email | Valid email format |
| Customer ID | Unique |
| Revenue Amount | Greater than 0 |
| Status | Valid reference value |

---

# Reference Data Mapping

Reference:

```text
Reference_Data_Management.md
```

---

| Field | Reference Type |
|----------|----------------|
| Customer Status | CustomerStatus |
| Property Type | PropertyType |
| Revenue Type | RevenueType |

---

# Data Quality Rules

## Completeness

Example:

```text
Customer Email Required
```

---

## Accuracy

Example:

```text
Revenue Amount must match invoice total.
```

---

## Consistency

Example:

```text
Customer Status must match approved reference values.
```

---

## Uniqueness

Example:

```text
Customer ID must be unique.
```

---

# Source System Information

| Attribute | Value |
|------------|--------|
| Source System | |
| Source Service | |
| Source Database | |
| Source API | |
| Source Event | |

---

# Data Lineage

Reference:

```text
Data_Lineage.md
```

---

## Upstream Sources

```text
Source APIs

Source Events

Source Databases
```

---

## Downstream Consumers

```text
Reporting

Analytics

Data Warehouse

AI Models
```

---

# API Mapping

Reference:

```text
API_Catalog.md
```

---

| Field | API Property |
|----------|-------------|
| customerId | customerId |
| status | customerStatus |

---

# Event Mapping

Reference:

```text
Event_Catalog.md
```

---

| Field | Event Attribute |
|----------|---------------|
| customerId | customerId |
| status | customerStatus |

---

# Retention Information

Reference:

```text
Data_Retention_Policy.md
```

---

| Attribute | Value |
|------------|--------|
| Retention Period | |
| Archive Required | Yes / No |
| Deletion Method | |
| Legal Hold Applicable | Yes / No |

---

# Security Requirements

Reference:

```text
Security_Controls_Catalog.md
```

---

## Encryption Required

```text
Yes / No
```

---

## Masking Required

```text
Yes / No
```

---

## Access Control

```text
RBAC
ABAC
```

---

# Ownership

## Data Owner

Responsible for:

```text
Business Accountability

Data Quality

Governance Compliance
```

---

## Data Steward

Responsible for:

```text
Metadata

Definitions

Quality Monitoring
```

---

# Change History

| Version | Date | Author | Description |
|----------|------|---------|-------------|
| 1.0 | YYYY-MM-DD | | Initial Version |

---

# Approval

| Role | Name | Date |
|--------|--------|--------|
| Data Owner | | |
| Data Steward | | |
| Architect | | |

---

# Related Documents

Business_Glossary.md

Canonical_Data_Model.md

Data_Model_Standards.md

Reference_Data_Management.md

Master_Data_Management.md

Data_Lineage.md

Security_Controls_Catalog.md

Data_Governance.md