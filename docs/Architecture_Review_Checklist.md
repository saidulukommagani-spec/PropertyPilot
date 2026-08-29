# Architecture Review Checklist

## Version

1.0

---

# Purpose

This checklist shall be used during architecture reviews to ensure compliance with:

- Architecture Principles
- Technology Standards
- Security Controls
- Data Governance
- API Governance
- NFR Requirements
- Operational Standards

---

# Review Information

| Field | Value |
|---------|---------|
| Project Name | |
| Solution Name | |
| Architect | |
| Reviewer | |
| Review Date | |
| Version | |
| Status | Pass / Conditional Pass / Fail |

---

# Review Outcome

| Result | Meaning |
|----------|----------|
| Pass | Approved |
| Conditional Pass | Approved with actions |
| Fail | Rework Required |

---

# Architecture Documentation Review

| Check | Status |
|---------|---------|
| Solution Architecture Document completed | |
| Architecture diagrams included | |
| Scope defined | |
| Stakeholders identified | |
| Assumptions documented | |
| Constraints documented | |
| Risks documented | |

---

# Architecture Principles Review

Reference:

```text
Architecture_Principles.md
```

| Check | Status |
|---------|---------|
| Business objectives supported | |
| API First principle followed | |
| Domain ownership respected | |
| Loose coupling achieved | |
| High cohesion achieved | |
| Event-driven opportunities evaluated | |
| Build for change principle followed | |

---

# Domain Review

Reference:

```text
Domain_Boundaries.md
```

| Check | Status |
|---------|---------|
| Domain ownership defined | |
| Service ownership defined | |
| Data ownership defined | |
| API ownership defined | |
| Event ownership defined | |
| Domain boundaries respected | |

---

# Service Design Review

| Check | Status |
|---------|---------|
| Service responsibilities clearly defined | |
| Single responsibility principle followed | |
| Service dependencies identified | |
| Service interactions documented | |
| Service boundaries clear | |

---

# API Review

Reference:

```text
API_Governance.md
API_Catalog.md
```

| Check | Status |
|---------|---------|
| API documented | |
| OpenAPI specification provided | |
| Versioning strategy defined | |
| Pagination supported | |
| Filtering supported | |
| Sorting supported | |
| Error handling defined | |
| Authentication defined | |
| Authorization defined | |

---

# Event Review

Reference:

```text
Event_Catalog.md
```

| Check | Status |
|---------|---------|
| Events documented | |
| Event ownership defined | |
| Event schema defined | |
| Event versioning defined | |
| Event consumers identified | |
| Event publishers identified | |
| Event payload reviewed | |

---

# Data Architecture Review

Reference:

```text
Canonical_Data_Model.md
```

| Check | Status |
|---------|---------|
| Data model documented | |
| Canonical model alignment verified | |
| Data ownership identified | |
| Master data identified | |
| Data quality rules defined | |
| Reference data identified | |
| Data retention requirements identified | |

---

# Database Review

| Check | Status |
|---------|---------|
| Database technology approved | |
| Data normalization reviewed | |
| Index strategy defined | |
| Backup strategy defined | |
| Archival strategy defined | |
| Data growth considered | |

---

# Integration Review

Reference:

```text
Integration_Hub.md
```

| Check | Status |
|---------|---------|
| Internal integrations documented | |
| External integrations documented | |
| Integration contracts defined | |
| Error handling defined | |
| Retry strategy defined | |
| Timeout strategy defined | |

---

# Security Review

Reference:

```text
Security_Controls_Catalog.md
```

| Check | Status |
|---------|---------|
| Authentication reviewed | |
| Authorization reviewed | |
| MFA requirements reviewed | |
| Sensitive data identified | |
| Encryption at rest defined | |
| Encryption in transit defined | |
| Tenant isolation verified | |
| Audit logging defined | |
| Security controls mapped | |

---

# Privacy Review

| Check | Status |
|---------|---------|
| PII identified | |
| Privacy requirements reviewed | |
| Data minimization applied | |
| Retention requirements reviewed | |
| Deletion requirements defined | |

---

# Non-Functional Requirements Review

Reference:

```text
Non_Functional_Requirements.md
```

| Check | Status |
|---------|---------|
| Availability targets defined | |
| Performance targets defined | |
| Scalability requirements defined | |
| Reliability requirements defined | |
| Security requirements defined | |
| Compliance requirements defined | |
| Observability requirements defined | |

---

# Cloud & Infrastructure Review

| Check | Status |
|---------|---------|
| Hosting model defined | |
| Infrastructure architecture documented | |
| Network architecture reviewed | |
| Infrastructure as Code used | |
| High availability considered | |
| Capacity planning completed | |

---

# Observability Review

Reference:

```text
Observability_Monitoring.md
```

| Check | Status |
|---------|---------|
| Metrics defined | |
| Logs defined | |
| Traces defined | |
| Health checks defined | |
| Dashboards identified | |
| Alerts defined | |

---

# Disaster Recovery Review

Reference:

```text
Disaster_Recovery_Business_Continuity.md
```

| Check | Status |
|---------|---------|
| RTO defined | |
| RPO defined | |
| Backup strategy defined | |
| Recovery process documented | |
| DR testing planned | |

---

# DevOps Review

Reference:

```text
DevOps_Architecture.md
```

| Check | Status |
|---------|---------|
| CI/CD defined | |
| Automated testing included | |
| Release strategy defined | |
| Rollback strategy defined | |
| Deployment automation included | |

---

# Technology Standards Review

Reference:

```text
Technology_Standards.md
Technology_Radar.md
```

| Check | Status |
|---------|---------|
| Approved technologies used | |
| Non-standard technologies justified | |
| Technology radar compliance verified | |
| Vendor lock-in assessed | |

---

# AI / ML Review (If Applicable)

Reference:

```text
AI_ML_Platform.md
```

| Check | Status |
|---------|---------|
| Model governance defined | |
| Explainability considered | |
| Bias reviewed | |
| Model monitoring defined | |
| Auditability ensured | |

---

# Risk Review

Reference:

```text
Risk_Management.md
```

| Check | Status |
|---------|---------|
| Risks identified | |
| Risk mitigation defined | |
| High risks reviewed | |
| Residual risks accepted | |

---

# Architecture Decision Records Review

Reference:

```text
Architecture_Decision_Records.md
```

| Check | Status |
|---------|---------|
| ADRs created where needed | |
| Significant decisions documented | |
| Alternatives considered | |
| Decisions approved | |

---

# Review Findings

## Finding 1

Description:

Severity:

Owner:

Target Date:

---

## Finding 2

Description:

Severity:

Owner:

Target Date:

---

# Action Items

| ID | Action | Owner | Due Date |
|------|---------|---------|---------|
| A-001 | | | |
| A-002 | | | |

---

# Approval

| Role | Name | Status |
|---------|---------|---------|
| Solution Architect | | |
| Security Architect | | |
| Data Architect | | |
| Enterprise Architect | | |
| Business Owner | | |

---

# Final Decision

```text
Approved

Approved With Conditions

Rejected
```

---

# Related Documents

Architecture_Principles.md

Architecture_Governance.md

Solution_Architecture_Template.md

Domain_Boundaries.md

Canonical_Data_Model.md

API_Catalog.md

Event_Catalog.md

Security_Controls_Catalog.md

Non_Functional_Requirements.md

Technology_Radar.md