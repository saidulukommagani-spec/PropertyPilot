# PropertyPilot Technology Radar

## Version

1.0

---

# Purpose

The Technology Radar provides guidance on technology adoption decisions across the PropertyPilot platform.

The radar helps teams:

- Standardize technology choices
- Reduce technology sprawl
- Accelerate architecture decisions
- Promote proven technologies
- Identify emerging technologies
- Manage technical risk

---

# Radar Categories

```text
Languages & Frameworks

Data & Analytics

Cloud & Infrastructure

Integration

Security

DevOps

Observability

AI / ML

Frontend

Architecture Patterns
```

---

# Technology Status Definitions

## Adopt

Recommended for production use.

Characteristics:

- Proven
- Supported
- Strategic
- Enterprise Approved

---

## Trial

Approved for controlled implementation.

Characteristics:

- Promising
- Limited Production Usage
- Architecture Approval Required

---

## Assess

Under evaluation.

Characteristics:

- Research Phase
- Proof of Concept
- Future Candidate

---

## Hold

Not recommended for new development.

Characteristics:

- Legacy
- High Risk
- Deprecated
- Migration Planned

---

# Languages & Frameworks

| Technology | Status |
|------------|---------|
| Java 21 | Adopt |
| Spring Boot | Adopt |
| .NET 8 | Adopt |
| Python | Adopt |
| TypeScript | Adopt |
| JavaScript | Adopt |
| Node.js | Trial |
| Go | Assess |
| PHP | Hold |
| Perl | Hold |

---

# Frontend Technologies

| Technology | Status |
|------------|---------|
| React | Adopt |
| Next.js | Adopt |
| Angular | Trial |
| Vue.js | Assess |
| jQuery | Hold |

---

# API Technologies

| Technology | Status |
|------------|---------|
| REST | Adopt |
| OpenAPI 3.1 | Adopt |
| GraphQL | Trial |
| gRPC | Assess |
| SOAP | Hold |

---

# Event & Messaging Technologies

| Technology | Status |
|------------|---------|
| Kafka | Adopt |
| RabbitMQ | Adopt |
| Azure Service Bus | Trial |
| AWS SNS/SQS | Trial |
| ActiveMQ | Hold |

---

# Data Technologies

| Technology | Status |
|------------|---------|
| PostgreSQL | Adopt |
| SQL Server | Adopt |
| MySQL | Trial |
| MongoDB | Trial |
| Cassandra | Assess |
| Oracle DB | Hold |

---

# Data Warehouse Technologies

| Technology | Status |
|------------|---------|
| Snowflake | Adopt |
| Azure Synapse | Trial |
| BigQuery | Trial |
| Redshift | Assess |

---

# Search Technologies

| Technology | Status |
|------------|---------|
| Elasticsearch | Adopt |
| OpenSearch | Trial |
| Solr | Hold |

---

# Caching Technologies

| Technology | Status |
|------------|---------|
| Redis | Adopt |
| Hazelcast | Trial |
| Memcached | Hold |

---

# Cloud Technologies

| Technology | Status |
|------------|---------|
| Azure | Adopt |
| AWS | Adopt |
| GCP | Trial |
| OpenStack | Hold |

---

# Container Technologies

| Technology | Status |
|------------|---------|
| Docker | Adopt |
| Kubernetes | Adopt |
| OpenShift | Trial |
| Docker Swarm | Hold |

---

# Infrastructure as Code

| Technology | Status |
|------------|---------|
| Terraform | Adopt |
| Bicep | Trial |
| CloudFormation | Trial |
| Chef | Hold |

---

# CI/CD Technologies

| Technology | Status |
|------------|---------|
| GitHub Actions | Adopt |
| Azure DevOps | Adopt |
| GitLab CI | Trial |
| Jenkins | Hold |

---

# Observability Technologies

| Technology | Status |
|------------|---------|
| OpenTelemetry | Adopt |
| Grafana | Adopt |
| Prometheus | Adopt |
| ELK Stack | Adopt |
| Datadog | Trial |
| Splunk | Trial |

---

# Security Technologies

| Technology | Status |
|------------|---------|
| OAuth2 | Adopt |
| OpenID Connect | Adopt |
| Keycloak | Trial |
| Microsoft Entra ID | Adopt |
| SAML | Trial |

---

# AI / ML Technologies

| Technology | Status |
|------------|---------|
| OpenAI APIs | Trial |
| Azure OpenAI | Adopt |
| MLflow | Trial |
| LangChain | Trial |
| TensorFlow | Assess |
| PyTorch | Assess |

---

# Architecture Patterns

| Pattern | Status |
|----------|---------|
| Microservices | Adopt |
| Event Driven Architecture | Adopt |
| Domain Driven Design | Adopt |
| API First | Adopt |
| CQRS | Trial |
| Event Sourcing | Assess |
| SOA | Hold |
| Monolithic Architecture | Hold |

---

# Technology Selection Criteria

Technology adoption decisions shall consider:

- Business Value
- Security
- Scalability
- Maintainability
- Community Support
- Vendor Support
- Total Cost of Ownership
- Operational Complexity

---

# Technology Governance

All new technologies require:

1. Architecture Review

2. Security Review

3. Risk Assessment

4. Proof of Concept

5. Approval from Architecture Board

---

# Hold Technologies Policy

Technologies in Hold status:

- Shall not be used for new projects
- May remain in existing solutions
- Should be considered for replacement

---

# Radar Review Cycle

The Technology Radar shall be reviewed:

```text
Quarterly
```

---

# Technology Ownership

| Area | Owner |
|--------|---------|
| Architecture | Enterprise Architecture Team |
| Security | Security Team |
| Infrastructure | Platform Engineering |
| Data | Data Architecture Team |
| AI/ML | AI Platform Team |

---

# Related Documents

Technology_Standards.md

Reference_Architecture.md

Architecture_Principles.md

Architecture_Governance.md

DevOps_Architecture.md

Platform_Engineering.md

Security_Controls_Catalog.md

AI_ML_Platform.md

Observability_Monitoring.md

Solution_Design_Process.md