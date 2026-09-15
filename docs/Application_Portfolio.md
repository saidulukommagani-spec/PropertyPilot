# PropertyPilot Application Portfolio

## Version

1.0

---

# Purpose

This document defines the enterprise application portfolio for PropertyPilot.

The objectives are to:

- Maintain application inventory
- Support technology governance
- Enable portfolio rationalization
- Identify modernization opportunities
- Support architecture planning
- Reduce technology risk

---

# Portfolio Principles

1. Every application shall have an owner.
2. Every application shall support a business capability.
3. Every application shall have a lifecycle status.
4. Every application shall have support ownership.
5. Every application shall comply with technology standards.
6. Duplicate applications should be minimized.
7. Applications shall be periodically reviewed.

---

# Portfolio Classification

Applications are categorized as:

```text
Business Applications

Core Domain Applications

Shared Services

Platform Applications

Data & Analytics Applications

Security Applications

DevOps Applications

External Applications
```

---

# Application Inventory

## Customer Service

### Description

Manages customer lifecycle and customer information.

### Business Capability

```text
Customer Management
```

### Domain

```text
Customer
```

### Owner

```text
Customer Domain Team
```

### Lifecycle Status

```text
Strategic
```

### Technology

```text
Java
Spring Boot
PostgreSQL
```

### Interfaces

```text
Customer API
Customer Events
```

---

## Lead Service

### Description

Manages lead acquisition, qualification, and conversion.

### Business Capability

```text
Lead Management
```

### Domain

```text
Lead
```

### Lifecycle Status

```text
Strategic
```

---

## Property Service

### Description

Manages property registration, listing, and lifecycle.

### Business Capability

```text
Property Management
```

### Domain

```text
Property
```

### Lifecycle Status

```text
Strategic
```

---

## Partner Service

### Description

Manages partner onboarding and operations.

### Domain

```text
Partner
```

### Lifecycle Status

```text
Strategic
```

---

## Vendor Service

### Description

Manages vendor onboarding and governance.

### Domain

```text
Vendor
```

### Lifecycle Status

```text
Strategic
```

---

## Contract Service

### Description

Manages contract lifecycle and compliance.

### Domain

```text
Contract
```

### Lifecycle Status

```text
Strategic
```

---

## Revenue Service

### Description

Manages billing, invoicing, payments, and revenue tracking.

### Domain

```text
Revenue
```

### Lifecycle Status

```text
Strategic
```

---

# Shared Platform Applications

## API Gateway

### Purpose

Central API management and governance.

### Owner

```text
Platform Team
```

### Lifecycle Status

```text
Strategic
```

---

## Identity Provider

### Purpose

Authentication and authorization.

### Owner

```text
Security Team
```

### Lifecycle Status

```text
Strategic
```

---

## Integration Hub

### Purpose

Enterprise integration platform.

### Owner

```text
Platform Team
```

### Lifecycle Status

```text
Strategic
```

---

## Enterprise Search

### Purpose

Cross-platform search capabilities.

### Owner

```text
Platform Team
```

### Lifecycle Status

```text
Strategic
```

---

# Data & Analytics Applications

## Data Lake

### Purpose

Raw enterprise data repository.

### Owner

```text
Data Team
```

---

## Data Warehouse

### Purpose

Enterprise reporting and analytics.

### Owner

```text
Data Team
```

---

## BI Platform

### Purpose

Dashboards and reporting.

### Examples

```text
Power BI
Tableau
```

---

## AI / ML Platform

### Purpose

Machine learning and AI services.

### Owner

```text
Data & AI Team
```

---

# Security Applications

## SIEM Platform

### Purpose

Security monitoring and threat detection.

---

## Vulnerability Management Platform

### Purpose

Vulnerability scanning and tracking.

---

## Secrets Management Platform

### Purpose

Secret storage and rotation.

---

# DevOps Applications

## Source Control Platform

### Examples

```text
GitHub Enterprise
```

---

## CI/CD Platform

### Examples

```text
GitHub Actions

Azure DevOps
```

---

## Artifact Repository

### Examples

```text
Nexus

Artifactory
```

---

# External Applications

## CRM

### Classification

```text
External
```

---

## Payment Gateway

### Classification

```text
External
```

---

## Email Provider

### Classification

```text
External
```

---

# Lifecycle Categories

| Status | Description |
|----------|-------------|
| Strategic | Long-term investment |
| Tolerate | Continue operating |
| Migrate | Replacement planned |
| Retire | Planned decommission |
| Emerging | New technology |

---

# Portfolio Assessment Criteria

Applications shall be assessed against:

```text
Business Value

Technical Health

Security

Operational Risk

Cost

Strategic Alignment
```

---

# Application Rationalization Matrix

| Business Value | Technical Health | Action |
|---------------|------------------|---------|
| High | High | Invest |
| High | Low | Modernize |
| Low | High | Contain |
| Low | Low | Retire |

---

# Technology Risk Assessment

Evaluate:

```text
Unsupported Technologies

Security Vulnerabilities

Vendor Dependency

Operational Risk

Scalability Constraints
```

---

# Application Dependency Mapping

Each application shall document:

```text
Upstream Systems

Downstream Systems

APIs

Events

Databases
```

---

# Portfolio Metrics

## Strategic Application Coverage

Target:

```text
90%
```

---

## Technology Standards Compliance

Target:

```text
95%
```

---

## Application Ownership Coverage

Target:

```text
100%
```

---

## Documentation Coverage

Target:

```text
100%
```

---

# Review Frequency

Application portfolio review shall occur:

```text
Quarterly
```

---

# Governance Rules

1. Every application must have an owner.
2. Every application must support a capability.
3. Every application must have a lifecycle status.
4. Technology standards compliance is mandatory.
5. Retired applications shall be removed from active portfolios.
6. Portfolio reviews shall occur quarterly.

---

# Related Documents

Enterprise_Capability_Model.md

Operating_Model.md

Reference_Architecture.md

Technology_Radar.md

Technology_Standards_Catalog.md

Architecture_KPIs.md

Service_Catalog.md

Domain_Boundaries.md

Architecture_Governance.md