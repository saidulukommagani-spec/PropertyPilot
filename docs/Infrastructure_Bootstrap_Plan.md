````markdown
# Infrastructure Bootstrap Plan

Document Type: Infrastructure Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Platform Engineering / DevOps / Security / Architecture

---

# Purpose

This document defines the Infrastructure Bootstrap Plan for PropertyPilot. It provides a production-grade cloud foundation for the application stack using AWS as the preferred target platform and Azure as a viable alternative when enterprise or organizational constraints require it.

The plan covers:
- AWS and Azure design options
- environment strategy
- compute, database, cache, storage, and networking layers
- IAM, secrets management, and security posture
- CI/CD and deployment methodology
- observability and operational monitoring
- backup, disaster recovery, and resilience
- Terraform structure and reusable modules
- bootstrap roadmap across MVP, scale, and enterprise phases
- cost estimates and risk register

The final infrastructure is intended to support:
- React frontend
- Spring Boot backend
- PostgreSQL primary database
- Redis caching and temporary data acceleration
- object storage for evidence, documents, and reports
- Dockerized application deployment
- GitHub Actions driven CI/CD
- secure production operations with strong observability and auditability

---

# Infrastructure Principles

## Scalability
- design for horizontal growth in frontend, backend, and API layers
- separate stateless application workloads from persistent data services
- enable autoscaling for application tiers and queue-based workers
- isolate platform components by environment and workload type

## Security
- enforce least-privilege IAM and service roles
- manage secrets via managed secret stores
- use TLS everywhere
- encrypt data at rest and in transit
- protect public endpoints with WAF and DDoS controls
- restrict network ingress and admin access

## Cost Optimization
- keep compute right-sized for MVP and scale in measured steps
- use managed services where possible
- enable autoscaling and spot-based options where viable
- avoid overprovisioning in development and QA
- prefer S3-compatible object storage and managed Redis/Postgres services

## Availability
- use multi-AZ deployment patterns in production
- implement health checks, autoscaling, and load balancing
- maintain database backup and restore capability
- ensure RTO and RPO objectives are aligned to business continuity needs

## Observability
- centralize logs, metrics, and traces
- enforce health endpoints and dashboard coverage
- alarm on error budgets and saturation
- create runbooks and release checklists for operational response

---

# Environment Strategy

## Development
Purpose:
- developer experimentation and feature validation

Characteristics:
- lowest cost footprint
- local or ephemeral cloud environment
- reduced redundancy
- mocked or minimal external dependencies where possible

## QA
Purpose:
- validation of feature correctness and regression stability

Characteristics:
- production-like environment
- isolated infrastructure
- synthetic and masked datasets
- integration testing and contract validation

## UAT
Purpose:
- stakeholder validation before production release

Characteristics:
- near-production-like environment
- controlled access
- production-equivalent security and service configuration
- final sign-off readiness before live deployment

## Production
Purpose:
- live customer and operational workloads

Characteristics:
- highest availability and redundancy
- strict security controls
- backup, DR, monitoring, and alerting
- no direct developer access
- controlled change windows and full release governance

---

# AWS Architecture

## Preferred Cloud Pattern

The AWS target architecture should support a secure, scalable, multi-environment deployment pattern for the PropertyPilot platform.

## VPC
- one VPC per environment
- public subnets for load balancers and edge services
- private subnets for application workloads and databases
- isolated subnets for data and services requiring additional controls
- route table separation for public/private traffic

## Subnets
- public subnets:
  - ALB / API Gateway edge layer
  - NAT gateway or egress path
- private subnets:
  - frontend application runtime
  - backend application runtime
  - PostgreSQL RDS/Aurora
  - Redis ElastiCache
  - internal services and workers

## Security Groups
- ALB SG:
  - allow HTTPS 443 from internet
  - allow health checks
- App SG:
  - allow only from ALB and internal VPC
  - deny direct internet ingress
- DB SG:
  - allow only from app subnets and admin paths
- Redis SG:
  - allow only from app subnets
- Bastion / admin SG:
  - restricted access with MFA and IP allowlists

## Load Balancer
Use:
- Application Load Balancer for frontend and API workloads
- path-based or host-based routing
- TLS termination at ALB
- health checks for application targets

## Auto Scaling
- ECS or EKS services with target tracking autoscaling
- scale based on CPU, memory, request concurrency, and latency
- use min/max desired counts by environment
- maintain burst capacity for peak periods

## NAT Gateway
- provide outbound internet access for private subnets
- use cost-aware NAT strategy with single AZ for dev and multi-AZ for production

## Route Tables
- public subnet route through IGW
- private subnet route through NAT and private endpoints
- restrict inter-subnet access to required communication paths

## AWS Reference Services
- Frontend: CloudFront + S3 for static assets, optional ALB for SPA runtime if needed
- Backend: ECS Fargate or EKS
- Database: Amazon RDS for PostgreSQL
- Cache: ElastiCache for Redis
- Storage: Amazon S3
- Secrets: AWS Secrets Manager
- Logs: CloudWatch Logs
- Metrics: CloudWatch metrics and alarms
- Traces: X-Ray or OpenTelemetry to CloudWatch/AWS observability stack
- WAF: AWS WAF in front of ALB or CloudFront
- DNS: Route 53
- Certificates: ACM
- Container registry: ECR
- GitHub Actions: integration with AWS credentials via OIDC

---

# Azure Alternative

## Recommended Azure Pattern

If AWS is not feasible, the platform can run on Azure with equivalent control and resilience.

## Core Services
- VNet with public and private subnets
- Azure Load Balancer or Application Gateway
- Azure Container Apps or AKS
- Azure Database for PostgreSQL
- Azure Cache for Redis
- Azure Blob Storage
- Azure Key Vault
- Azure Monitor and Log Analytics
- Azure Front Door or CDN
- Azure WAF
- Azure Active Directory / Entra ID
- Azure Container Registry
- GitHub Actions with Azure login and RBAC

## Differences from AWS
- application services may use Azure Container Apps for simplicity in MVP
- AKS is preferred if more Kubernetes orchestration is required
- monitoring and log analytics are more integrated via Azure Monitor
- Azure Key Vault replaces Secrets Manager pattern
- Azure Front Door replaces certain edge protections and routing

---

# Compute Layer

## Application Servers

PropertyPilot workload types:
- frontend web app
- backend API application
- background workers or async tasks
- scheduled jobs
- queue consumers and notification processors

## Container Strategy
Use Docker-based packaging for all application workloads. Recommended:
- one container image for frontend
- one container image for backend
- one container image for background workers
- versioned immutable image tags
- environment-specific configurations injected at runtime

## ECS/EKS Recommendations

### Recommended for MVP
- ECS Fargate for simplicity, lower operational overhead, faster bootstrap
- ALB in front of service tasks
- easier security and cost control for smaller teams

### Recommended for scale
- EKS with managed node groups when the platform grows in team size or operational complexity
- better for multi-service orchestration and advanced workload patterns

### Decision guidance
- choose ECS Fargate for MVP and moderate complexity
- choose EKS when:
  - advanced Kubernetes operations are required
  - multi-service orchestration expands
  - cluster-level resource control and rollout strategies are needed

## Sizing

### Frontend
- small to medium instances / Fargate CPU units depending on traffic
- caching and CDN for static assets
- autoscale based on request rate and CPU

### Backend
- size for API and service workloads based on traffic and DB latency
- target moderate concurrency early, then scale horizontally
- maintain headroom for bursts and scheduled reporting activity

### Background Workers
- separate workers for:
  - notification dispatch
  - report generation
  - async notifications
  - webhook processing
  - event processing

## Scaling Rules
- scale out on sustained CPU, memory, or request latency
- use targeted autoscaling for backend and workers
- provision min replicas for production to ensure availability
- use cooldown and health checks to avoid oscillation

---

# Database Layer

## PostgreSQL

Use PostgreSQL as the primary transactional database for:
- customer data
- property records
- service requests
- subscriptions
- payments
- audit logs
- admin configuration
- reporting tables

## Primary Architecture
- RDS PostgreSQL or managed Azure PostgreSQL
- multi-AZ in production
- automatic backups enabled
- encryption at rest enabled
- parameter tuning for connection pooling and throughput

## Backup Strategy
- automated daily snapshots
- point-in-time recovery enabled in production
- transaction log retention aligned to RPO
- periodic restore testing

## Replication Strategy
- read replicas for analytics/reporting if needed
- hot standby or multi-AZ within region for production resilience
- cross-region replication for DR if business continuity requires it

## Disaster Recovery
- cross-region backup storage
- restore drills at least quarterly
- DB snapshot retention aligned with compliance and business requirements
- failover plan documented and validated

## Sizing
MVP sizing assumptions:
- moderate transaction volume
- single primary with backups and replica
- scale to larger compute as operations increase

Production sizing:
- ensure storage headroom for evidence, audit, and reporting
- monitor IOPS, connection counts, and table bloat

---

# Cache Layer

## Redis

Redis is required for:
- session and token validation metadata
- rate limiting
- short-lived caching for common read-heavy endpoints
- queue and work distribution metadata
- notification throttling
- dashboard metrics caching
- temporary deduplication keys

## Use Cases
- cache service catalog entries
- cache user profile summary data
- cache frequently accessed property lists or dashboard summaries
- store temporary API rate limit counters
- hold short TTL token invalidation data
- distribute short-lived operational data

## Sizing
- small to medium cache instance for MVP
- scale with cache hit rate and memory pressure
- separate cache clusters by workload if required

## TTL Strategy
- user profile: 5-15 minutes
- service catalog: 15-60 minutes
- queue summary: 1-5 minutes
- rate limiting counters: short-lived, seconds to minutes
- token invalidation metadata: aligned to token expiry and risk policy
- reports: 5-15 minutes

---

# Storage Layer

## Object Storage

Object storage is needed for:
- property evidence
- agent photo documentation
- uploaded files
- reports
- archived documents
- public/limited-access static content

## Storage Services
AWS:
- Amazon S3
Azure:
- Azure Blob Storage

## Evidence Storage
- securely store agent and customer evidence files
- enforce access controls and signed URLs
- supported file types limited by validation
- malware scanning recommended for uploaded content

## Report Storage
- store generated PDFs and CSV exports
- maintain retention rules
- allow secure retrieval for admin and operations

## Document Storage
- support support tickets, invoices, printed docs, and associated attachments
- add lifecycle rules for archival and retention

## Retention Rules
- evidence retention based on operational and compliance policy
- report export retention based on business requirements
- audit logs retained for required duration
- support deletion and purge workflow for personal data
- implement encryption and versioning

---

# Identity & Access Management

## IAM Roles

Use least-privilege IAM roles and service principals:
- app execution roles
- database access roles
- object storage access roles
- deployment roles
- monitoring access roles
- admin access via break-glass accounts only

## Least Privilege
- no shared admin credentials
- separate roles for dev, QA, UAT, production
- scoped permissions per service
- deny wildcard permissions and broad resource access

## Service Accounts
- dedicated service accounts for backend api
- dedicated accounts for report generation
- dedicated accounts for background jobs
- separate service accounts for external vendor integrations

## Secrets Management
AWS:
- Secrets Manager
Azure:
- Key Vault

Use:
- DB credentials
- API keys
- JWT signing secrets
- OAuth or external integration tokens
- payment provider credentials
- email/SMS provider secrets

---

# Networking

## DNS
- use Route 53 or Azure DNS
- environment-specific subdomains:
  - app.dev.propertypilot
  - qa.propertypilot
  - uat.propertypilot
  - app.propertypilot
- separate admin and customer endpoints if required

## TLS
- enforce HTTPS on all public and private endpoints where applicable
- use managed certificates
- redirect all HTTP to HTTPS
- certificate rotation managed automatically where possible

## Certificates
- public certs for frontend and API endpoints
- internal certs for back-end service-to-service communication if needed
- validation on expiry and renewal automation

## Firewall Rules
- deny all except required ingress
- restrict admin access and break-glass access
- maintain explicit egress control where required

## WAF
- protect public API and frontend edge
- rate limiting against abusive traffic
- custom rules for malicious payloads
- layer on DDoS protection

## DDoS Protection
- cloud provider edge protection
- ALB or CDN with DDoS shielding
- rate limiting and anomaly detection
- failover design for regionally critical services

---

# CI/CD Architecture

## GitHub Actions

Use GitHub Actions as the CI/CD pipeline engine.

## Build Pipeline
- checkout code
- install dependencies
- run lint
- run static analysis
- run unit tests
- run integration tests
- build container images
- scan vulnerabilities
- publish artifacts

## Test Pipeline
- run backend unit tests
- run frontend unit/component tests
- run e2e smoke tests
- database migration validation
- contract validation against OpenAPI
- security scan and dependency scan

## Deployment Pipeline
- deploy to dev on merge to main branch or feature branch where allowed
- deploy to QA/UAT on approved release branch
- production deployment via protected workflow and manual approval
- environment-specific secrets and variables injected at runtime

## Rollback Strategy
- redeploy previous image tag
- restore prior configuration if needed
- roll back DB migrations only if proven safe
- maintain clear approval path for critical rollback events
- validate health checks before re-enabling traffic

---

# Observability

## Centralized Logging
- aggregate app logs into centralized logging
- include correlation IDs and request IDs
- filter by environment and service
- store adequate retention windows for support and security investigation

## Metrics
Track:
- API response times
- request counts and error rate
- CPU and memory usage
- DB CPU, storage, connection pool
- Redis memory and hit rate
- queue depth / backlog
- upload latency
- payment processing duration
- admin and service operations KPIs

## Tracing
- distributed tracing for request flow from frontend to backend to DB
- trace async tasks and event processing
- track critical user journeys such as sign-in, booking, payment, escalation, and report generation

## Dashboards
- technical operational dashboard
- business operations dashboard
- incident response dashboard
- performance and capacity dashboard

## Alerts
- API 5xx spike
- DB saturation
- Redis memory exhaustion
- queue backlog growth
- deployment failure
- login/password attack events
- payment failure trends

## Health Checks
- liveness and readiness endpoints
- service dependency health checks
- DB and Redis health checks
- external provider health checks
- scheduled check for critical services

---

# Security Controls

## Encryption at Rest
- RDS encryption
- Redis encryption where supported
- S3/Blob storage encryption
- worker storage and ephemeral volumes encrypted
- logs and backups encrypted

## Encryption in Transit
- TLS for all public endpoints
- mTLS or internal TLS for backend-to-backend communication if required
- encrypted connection to DB and Redis
- encrypted artifact delivery and deployment channels

## Secrets Rotation
- rotate credentials at a defined cadence
- use managed secret stores and automation
- use short-lived credentials where possible
- maintain emergency rotation procedures

## Audit Logging
- admin actions
- config changes
- permission changes
- security-sensitive operations
- data access events
- system configuration changes

## Compliance Controls
- least privilege access
- structured data retention rules
- approval-based deployments
- break-glass account governance
- vulnerability scans and dependency audits

---

# Backup & Recovery

## Backup Frequency
- database: continuous or frequent snapshot schedule
- object storage: inherently versioned and durable
- configuration: backed up in Git and IaC state
- secrets and environment configuration: stored in secure service and versioned

## Retention
- production DB snapshots: aligned with RPO/RTO
- object storage versioning: enabled
- logs: retention based on operational/security policy
- support artifacts and reports: aligned with business retention requirements

## Restore Process
- restore DB from snapshot or PITR
- restore object storage artifacts by version
- redeploy application stack from infrastructure code
- recover service configuration from versioned state
- validate service health after recovery

## RTO
Recovery Time Objective:
- critical frontend: 1-4 hours
- critical backend: 2-6 hours
- DB restoration: 2-8 hours depending on scale
- full environment recovery: 6-24 hours depending on complexity

## RPO
Recovery Point Objective:
- critical database: near-zero for recent transactions within backup windows
- object storage: near-zero with versioning and replication
- config and secrets: near-zero via managed storage

---

# Cost Estimates

The numbers below are directional estimates for planning and should be refined during architecture validation.

## Development
Estimated monthly cost:
- compute and networking: $120 - $400
- database and cache: $80 - $250
- storage: $20 - $100
- monitoring and logging: $30 - $120
- secrets and IAM: $10 - $50

Total:
- approximately $260 - $920 per month

## QA
Estimated monthly cost:
- compute and networking: $250 - $700
- database and cache: $120 - $350
- storage and backup: $50 - $200
- monitoring: $50 - $150

Total:
- approximately $470 - $1,400 per month

## UAT
Estimated monthly cost:
- compute: $400 - $1,200
- database and cache: $200 - $600
- storage: $80 - $250
- observability: $80 - $200

Total:
- approximately $760 - $2,250 per month

## Production
Estimated monthly cost:
- compute: $1,000 - $4,000
- database + cache: $500 - $2,500
- object storage: $150 - $600
- networking and WAF: $200 - $800
- monitoring and logs: $200 - $700
- backup and DR: $200 - $800

Total:
- approximately $2,250 - $9,400 per month

These values depend heavily on:
- traffic
- data volume
- backup duration
- storage type
- region
- chosen managed services
- autoscaling rules

---

# Infrastructure Roadmap

## Phase 1: MVP
Objectives:
- bootstrap secure development and QA environments
- deploy backend and frontend with minimal complexity
- validate DB, Redis, storage, and monitors
- enable zero-downtime deployment workflows

Deliverables:
- VPC / network baseline
- app environment for dev and QA
- managed PostgreSQL and Redis
- object storage
- ALB and autoscaling
- WAF and TLS
- GitHub Actions pipelines
- health, logs, and dashboards

---

## Phase 2: Scale
Objectives:
- increase resilience and performance
- optimize cost and autoscaling
- improve production availability
- strengthen security and metrics coverage

Deliverables:
- multi-AZ production deployment
- database replicas and DR pattern
- improved autoscaling policies
- centralized observability
- more robust alerting and runbooks
- capacity planning and release governance maturity

---

## Phase 3: Enterprise
Objectives:
- multi-environment governance and broader scale
- higher security and controls
- more advanced resilience and compliance posture
- operational maturity and enterprise platform support

Deliverables:
- cross-region DR
- strict enterprise IAM and policy enforcement
- advanced networking and segmentation
- robust audit, retention, and compliance controls
- cost optimization and platform lifecycle maturity
- advanced automation and release safety controls

---

# Terraform Structure

Recommended Terraform folder structure:

```text
infrastructure/
├── README.md
├── versions.tf
├── providers.tf
├── backend.tf
├── variables.tf
├── outputs.tf
├── locals.tf
├── modules/
│   ├── network/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── security/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── compute/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── database/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── cache/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── storage/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── monitoring/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── iam/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── dns/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   └── cd/
│       ├── main.tf
│       ├── variables.tf
│       └── outputs.tf
├── environments/
│   ├── dev/
│   │   ├── main.tf
│   │   ├── terraform.tfvars
│   │   └── backend.hcl
│   ├── qa/
│   │   ├── main.tf
│   │   ├── terraform.tfvars
│   │   └── backend.hcl
│   ├── uat/
│   │   ├── main.tf
│   │   ├── terraform.tfvars
│   │   └── backend.hcl
│   └── prod/
│       ├── main.tf
│       ├── terraform.tfvars
│       └── backend.hcl
└── scripts/
    ├── bootstrap.sh
    ├── validate.sh
    └── drift-check.sh
```

## Terraform Modules
Reusable modules should include:
- network module
- compute module
- database module
- cache module
- object storage module
- monitoring module
- IAM module
- DNS and TLS module
- CI/CD and environment module

---

# Deployment Checklist

## Pre-Deployment
- verify environment variables and secrets
- ensure Terraform state is available and locked
- validate DB migration scripts
- inspect Redis and storage configuration
- confirm domain and certificate configuration
- validate application health checks
- confirm monitoring and dashboards exist
- verify runbook and rollback plan
- validate security group and firewall posture
- verify traffic routing and expected service endpoints

## Deployment
- deploy infrastructure with IaC
- deploy application containers or artifacts
- run migration scripts in the correct order
- confirm service health
- monitor logs and error rate
- verify post-deploy smoke tests
- validate external integrations and notification flows
- validate admin and customer access paths

## Post-Deployment
- confirm platform health and metrics
- validate alerting and observability
- verify release notes and environment configuration
- check object storage and evidence uploads
- confirm backup schedules and retention
- document any exceptions or production issues
- close out go/no-go checklist with owners

---

# Risks

Risk list with impact, mitigation, and owner:

## 1. Database performance bottleneck
Impact:
- slow queries and degraded user experience

Mitigation:
- use connection pooling, read replicas, query tuning, and planned indexing
Owner:
- Database Engineering

## 2. Cache storm or Redis memory pressure
Impact:
- application latency and degraded cache efficiency

Mitigation:
- use TTL controls, cache invalidation strategy, and scaling plan
Owner:
- Platform Engineering

## 3. Incomplete backup or restore validation
Impact:
- unrecoverable data loss or extended downtime

Mitigation:
- scheduled restore drills and validated backup retention
Owner:
- DevOps / DBA

## 4. Public API exposure without WAF protection
Impact:
- abuse, malicious traffic, and potential service disruption

Mitigation:
- WAF policy, rate limiting, and DDoS protection
Owner:
- Security / Platform

## 5. Secret leakage
Impact:
- security breach and external vendor compromise

Mitigation:
- use managed secret stores, rotation, least privilege, and protected pipelines
Owner:
- Security

## 6. Overprovisioning leading to unnecessary spend
Impact:
- inflated monthly cloud cost

Mitigation:
- autoscaling, rightsizing, and environment-specific cost review
Owner:
- Platform / Finance

## 7. Inconsistent environment configuration
Impact:
- deployment failures and fragile release process

Mitigation:
- IaC enforcement and environment parity checks
Owner:
- DevOps

## 8. Container image vulnerabilities
Impact:
- exploitable runtime defects

Mitigation:
- dependency scans, image vulnerability scanning, and fix SLAs
Owner:
- Security / DevOps

## 9. Unvalidated production cutover
Impact:
- service interruption and customer-facing issues

Mitigation:
- pre-prod rehearsal, smoke tests, and release sign-off
Owner:
- Engineering / Release Manager

## 10. External service outages
Impact:
- failed notifications, payments, or integrations

Mitigation:
- retry logic, fallback workflows, and circuit breakers
Owner:
- Architecture / Integration

---

# Final Recommendation

The preferred infrastructure design for PropertyPilot is AWS-based, with a secure, modular, environment-aware architecture that supports:
- React frontend hosting
- Spring Boot backend application runtime
- PostgreSQL transactional persistence
- Redis caching and rate-limited access
- object storage for media and documents
- Dockerized container deployments
- GitHub Actions CI/CD
- integrated monitoring, security, and backup operations

The architecture should be implemented using Terraform from the start to ensure environment parity, controlled reproduction, and safer deployment practices.

For production operations, the platform should prioritize:
- multi-AZ deployment
- managed services
- strong IAM controls
- real-time observability
- regular backup and restore testing
- formal release governance
- explicit incident response and rollback runbooks

This plan gives PropertyPilot a practical and scalable cloud foundation for MVP, growth, and long-term enterprise readiness.
```// filepath: c:\PropertyPilot\docs\Infrastructure_Bootstrap_Plan.md
# Infrastructure Bootstrap Plan

Document Type: Infrastructure Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Platform Engineering / DevOps / Security / Architecture

---

# Purpose

This document defines the Infrastructure Bootstrap Plan for PropertyPilot. It provides a production-grade cloud foundation for the application stack using AWS as the preferred target platform and Azure as a viable alternative when enterprise or organizational constraints require it.

The plan covers:
- AWS and Azure design options
- environment strategy
- compute, database, cache, storage, and networking layers
- IAM, secrets management, and security posture
- CI/CD and deployment methodology
- observability and operational monitoring
- backup, disaster recovery, and resilience
- Terraform structure and reusable modules
- bootstrap roadmap across MVP, scale, and enterprise phases
- cost estimates and risk register

The final infrastructure is intended to support:
- React frontend
- Spring Boot backend
- PostgreSQL primary database
- Redis caching and temporary data acceleration
- object storage for evidence, documents, and reports
- Dockerized application deployment
- GitHub Actions driven CI/CD
- secure production operations with strong observability and auditability

---

# Infrastructure Principles

## Scalability
- design for horizontal growth in frontend, backend, and API layers
- separate stateless application workloads from persistent data services
- enable autoscaling for application tiers and queue-based workers
- isolate platform components by environment and workload type

## Security
- enforce least-privilege IAM and service roles
- manage secrets via managed secret stores
- use TLS everywhere
- encrypt data at rest and in transit
- protect public endpoints with WAF and DDoS controls
- restrict network ingress and admin access

## Cost Optimization
- keep compute right-sized for MVP and scale in measured steps
- use managed services where possible
- enable autoscaling and spot-based options where viable
- avoid overprovisioning in development and QA
- prefer S3-compatible object storage and managed Redis/Postgres services

## Availability
- use multi-AZ deployment patterns in production
- implement health checks, autoscaling, and load balancing
- maintain database backup and restore capability
- ensure RTO and RPO objectives are aligned to business continuity needs

## Observability
- centralize logs, metrics, and traces
- enforce health endpoints and dashboard coverage
- alarm on error budgets and saturation
- create runbooks and release checklists for operational response

---

# Environment Strategy

## Development
Purpose:
- developer experimentation and feature validation

Characteristics:
- lowest cost footprint
- local or ephemeral cloud environment
- reduced redundancy
- mocked or minimal external dependencies where possible

## QA
Purpose:
- validation of feature correctness and regression stability

Characteristics:
- production-like environment
- isolated infrastructure
- synthetic and masked datasets
- integration testing and contract validation

## UAT
Purpose:
- stakeholder validation before production release

Characteristics:
- near-production-like environment
- controlled access
- production-equivalent security and service configuration
- final sign-off readiness before live deployment

## Production
Purpose:
- live customer and operational workloads

Characteristics:
- highest availability and redundancy
- strict security controls
- backup, DR, monitoring, and alerting
- no direct developer access
- controlled change windows and full release governance

---

# AWS Architecture

## Preferred Cloud Pattern

The AWS target architecture should support a secure, scalable, multi-environment deployment pattern for the PropertyPilot platform.

## VPC
- one VPC per environment
- public subnets for load balancers and edge services
- private subnets for application workloads and databases
- isolated subnets for data and services requiring additional controls
- route table separation for public/private traffic

## Subnets
- public subnets:
  - ALB / API Gateway edge layer
  - NAT gateway or egress path
- private subnets:
  - frontend application runtime
  - backend application runtime
  - PostgreSQL RDS/Aurora
  - Redis ElastiCache
  - internal services and workers

## Security Groups
- ALB SG:
  - allow HTTPS 443 from internet
  - allow health checks
- App SG:
  - allow only from ALB and internal VPC
  - deny direct internet ingress
- DB SG:
  - allow only from app subnets and admin paths
- Redis SG:
  - allow only from app subnets
- Bastion / admin SG:
  - restricted access with MFA and IP allowlists

## Load Balancer
Use:
- Application Load Balancer for frontend and API workloads
- path-based or host-based routing
- TLS termination at ALB
- health checks for application targets

## Auto Scaling
- ECS or EKS services with target tracking autoscaling
- scale based on CPU, memory, request concurrency, and latency
- use min/max desired counts by environment
- maintain burst capacity for peak periods

## NAT Gateway
- provide outbound internet access for private subnets
- use cost-aware NAT strategy with single AZ for dev and multi-AZ for production

## Route Tables
- public subnet route through IGW
- private subnet route through NAT and private endpoints
- restrict inter-subnet access to required communication paths

## AWS Reference Services
- Frontend: CloudFront + S3 for static assets, optional ALB for SPA runtime if needed
- Backend: ECS Fargate or EKS
- Database: Amazon RDS for PostgreSQL
- Cache: ElastiCache for Redis
- Storage: Amazon S3
- Secrets: AWS Secrets Manager
- Logs: CloudWatch Logs
- Metrics: CloudWatch metrics and alarms
- Traces: X-Ray or OpenTelemetry to CloudWatch/AWS observability stack
- WAF: AWS WAF in front of ALB or CloudFront
- DNS: Route 53
- Certificates: ACM
- Container registry: ECR
- GitHub Actions: integration with AWS credentials via OIDC

---

# Azure Alternative

## Recommended Azure Pattern

If AWS is not feasible, the platform can run on Azure with equivalent control and resilience.

## Core Services
- VNet with public and private subnets
- Azure Load Balancer or Application Gateway
- Azure Container Apps or AKS
- Azure Database for PostgreSQL
- Azure Cache for Redis
- Azure Blob Storage
- Azure Key Vault
- Azure Monitor and Log Analytics
- Azure Front Door or CDN
- Azure WAF
- Azure Active Directory / Entra ID
- Azure Container Registry
- GitHub Actions with Azure login and RBAC

## Differences from AWS
- application services may use Azure Container Apps for simplicity in MVP
- AKS is preferred if more Kubernetes orchestration is required
- monitoring and log analytics are more integrated via Azure Monitor
- Azure Key Vault replaces Secrets Manager pattern
- Azure Front Door replaces certain edge protections and routing

---

# Compute Layer

## Application Servers

PropertyPilot workload types:
- frontend web app
- backend API application
- background workers or async tasks
- scheduled jobs
- queue consumers and notification processors

## Container Strategy
Use Docker-based packaging for all application workloads. Recommended:
- one container image for frontend
- one container image for backend
- one container image for background workers
- versioned immutable image tags
- environment-specific configurations injected at runtime

## ECS/EKS Recommendations

### Recommended for MVP
- ECS Fargate for simplicity, lower operational overhead, faster bootstrap
- ALB in front of service tasks
- easier security and cost control for smaller teams

### Recommended for scale
- EKS with managed node groups when the platform grows in team size or operational complexity
- better for multi-service orchestration and advanced workload patterns

### Decision guidance
- choose ECS Fargate for MVP and moderate complexity
- choose EKS when:
  - advanced Kubernetes operations are required
  - multi-service orchestration expands
  - cluster-level resource control and rollout strategies are needed

## Sizing

### Frontend
- small to medium instances / Fargate CPU units depending on traffic
- caching and CDN for static assets
- autoscale based on request rate and CPU

### Backend
- size for API and service workloads based on traffic and DB latency
- target moderate concurrency early, then scale horizontally
- maintain headroom for bursts and scheduled reporting activity

### Background Workers
- separate workers for:
  - notification dispatch
  - report generation
  - async notifications
  - webhook processing
  - event processing

## Scaling Rules
- scale out on sustained CPU, memory, or request latency
- use targeted autoscaling for backend and workers
- provision min replicas for production to ensure availability
- use cooldown and health checks to avoid oscillation

---

# Database Layer

## PostgreSQL

Use PostgreSQL as the primary transactional database for:
- customer data
- property records
- service requests
- subscriptions
- payments
- audit logs
- admin configuration
- reporting tables

## Primary Architecture
- RDS PostgreSQL or managed Azure PostgreSQL
- multi-AZ in production
- automatic backups enabled
- encryption at rest enabled
- parameter tuning for connection pooling and throughput

## Backup Strategy
- automated daily snapshots
- point-in-time recovery enabled in production
- transaction log retention aligned to RPO
- periodic restore testing

## Replication Strategy
- read replicas for analytics/reporting if needed
- hot standby or multi-AZ within region for production resilience
- cross-region replication for DR if business continuity requires it

## Disaster Recovery
- cross-region backup storage
- restore drills at least quarterly
- DB snapshot retention aligned with compliance and business requirements
- failover plan documented and validated

## Sizing
MVP sizing assumptions:
- moderate transaction volume
- single primary with backups and replica
- scale to larger compute as operations increase

Production sizing:
- ensure storage headroom for evidence, audit, and reporting
- monitor IOPS, connection counts, and table bloat

---

# Cache Layer

## Redis

Redis is required for:
- session and token validation metadata
- rate limiting
- short-lived caching for common read-heavy endpoints
- queue and work distribution metadata
- notification throttling
- dashboard metrics caching
- temporary deduplication keys

## Use Cases
- cache service catalog entries
- cache user profile summary data
- cache frequently accessed property lists or dashboard summaries
- store temporary API rate limit counters
- hold short TTL token invalidation data
- distribute short-lived operational data

## Sizing
- small to medium cache instance for MVP
- scale with cache hit rate and memory pressure
- separate cache clusters by workload if required

## TTL Strategy
- user profile: 5-15 minutes
- service catalog: 15-60 minutes
- queue summary: 1-5 minutes
- rate limiting counters: short-lived, seconds to minutes
- token invalidation metadata: aligned to token expiry and risk policy
- reports: 5-15 minutes

---

# Storage Layer

## Object Storage

Object storage is needed for:
- property evidence
- agent photo documentation
- uploaded files
- reports
- archived documents
- public/limited-access static content

## Storage Services
AWS:
- Amazon S3
Azure:
- Azure Blob Storage

## Evidence Storage
- securely store agent and customer evidence files
- enforce access controls and signed URLs
- supported file types limited by validation
- malware scanning recommended for uploaded content

## Report Storage
- store generated PDFs and CSV exports
- maintain retention rules
- allow secure retrieval for admin and operations

## Document Storage
- support support tickets, invoices, printed docs, and associated attachments
- add lifecycle rules for archival and retention

## Retention Rules
- evidence retention based on operational and compliance policy
- report export retention based on business requirements
- audit logs retained for required duration
- support deletion and purge workflow for personal data
- implement encryption and versioning

---

# Identity & Access Management

## IAM Roles

Use least-privilege IAM roles and service principals:
- app execution roles
- database access roles
- object storage access roles
- deployment roles
- monitoring access roles
- admin access via break-glass accounts only

## Least Privilege
- no shared admin credentials
- separate roles for dev, QA, UAT, production
- scoped permissions per service
- deny wildcard permissions and broad resource access

## Service Accounts
- dedicated service accounts for backend api
- dedicated accounts for report generation
- dedicated accounts for background jobs
- separate service accounts for external vendor integrations

## Secrets Management
AWS:
- Secrets Manager
Azure:
- Key Vault

Use:
- DB credentials
- API keys
- JWT signing secrets
- OAuth or external integration tokens
- payment provider credentials
- email/SMS provider secrets

---

# Networking

## DNS
- use Route 53 or Azure DNS
- environment-specific subdomains:
  - app.dev.propertypilot
  - qa.propertypilot
  - uat.propertypilot
  - app.propertypilot
- separate admin and customer endpoints if required

## TLS
- enforce HTTPS on all public and private endpoints where applicable
- use managed certificates
- redirect all HTTP to HTTPS
- certificate rotation managed automatically where possible

## Certificates
- public certs for frontend and API endpoints
- internal certs for back-end service-to-service communication if needed
- validation on expiry and renewal automation

## Firewall Rules
- deny all except required ingress
- restrict admin access and break-glass access
- maintain explicit egress control where required

## WAF
- protect public API and frontend edge
- rate limiting against abusive traffic
- custom rules for malicious payloads
- layer on DDoS protection

## DDoS Protection
- cloud provider edge protection
- ALB or CDN with DDoS shielding
- rate limiting and anomaly detection
- failover design for regionally critical services

---

# CI/CD Architecture

## GitHub Actions

Use GitHub Actions as the CI/CD pipeline engine.

## Build Pipeline
- checkout code
- install dependencies
- run lint
- run static analysis
- run unit tests
- run integration tests
- build container images
- scan vulnerabilities
- publish artifacts

## Test Pipeline
- run backend unit tests
- run frontend unit/component tests
- run e2e smoke tests
- database migration validation
- contract validation against OpenAPI
- security scan and dependency scan

## Deployment Pipeline
- deploy to dev on merge to main branch or feature branch where allowed
- deploy to QA/UAT on approved release branch
- production deployment via protected workflow and manual approval
- environment-specific secrets and variables injected at runtime

## Rollback Strategy
- redeploy previous image tag
- restore prior configuration if needed
- roll back DB migrations only if proven safe
- maintain clear approval path for critical rollback events
- validate health checks before re-enabling traffic

---

# Observability

## Centralized Logging
- aggregate app logs into centralized logging
- include correlation IDs and request IDs
- filter by environment and service
- store adequate retention windows for support and security investigation

## Metrics
Track:
- API response times
- request counts and error rate
- CPU and memory usage
- DB CPU, storage, connection pool
- Redis memory and hit rate
- queue depth / backlog
- upload latency
- payment processing duration
- admin and service operations KPIs

## Tracing
- distributed tracing for request flow from frontend to backend to DB
- trace async tasks and event processing
- track critical user journeys such as sign-in, booking, payment, escalation, and report generation

## Dashboards
- technical operational dashboard
- business operations dashboard
- incident response dashboard
- performance and capacity dashboard

## Alerts
- API 5xx spike
- DB saturation
- Redis memory exhaustion
- queue backlog growth
- deployment failure
- login/password attack events
- payment failure trends

## Health Checks
- liveness and readiness endpoints
- service dependency health checks
- DB and Redis health checks
- external provider health checks
- scheduled check for critical services

---

# Security Controls

## Encryption at Rest
- RDS encryption
- Redis encryption where supported
- S3/Blob storage encryption
- worker storage and ephemeral volumes encrypted
- logs and backups encrypted

## Encryption in Transit
- TLS for all public endpoints
- mTLS or internal TLS for backend-to-backend communication if required
- encrypted connection to DB and Redis
- encrypted artifact delivery and deployment channels

## Secrets Rotation
- rotate credentials at a defined cadence
- use managed secret stores and automation
- use short-lived credentials where possible
- maintain emergency rotation procedures

## Audit Logging
- admin actions
- config changes
- permission changes
- security-sensitive operations
- data access events
- system configuration changes

## Compliance Controls
- least privilege access
- structured data retention rules
- approval-based deployments
- break-glass account governance
- vulnerability scans and dependency audits

---

# Backup & Recovery

## Backup Frequency
- database: continuous or frequent snapshot schedule
- object storage: inherently versioned and durable
- configuration: backed up in Git and IaC state
- secrets and environment configuration: stored in secure service and versioned

## Retention
- production DB snapshots: aligned with RPO/RTO
- object storage versioning: enabled
- logs: retention based on operational/security policy
- support artifacts and reports: aligned with business retention requirements

## Restore Process
- restore DB from snapshot or PITR
- restore object storage artifacts by version
- redeploy application stack from infrastructure code
- recover service configuration from versioned state
- validate service health after recovery

## RTO
Recovery Time Objective:
- critical frontend: 1-4 hours
- critical backend: 2-6 hours
- DB restoration: 2-8 hours depending on scale
- full environment recovery: 6-24 hours depending on complexity

## RPO
Recovery Point Objective:
- critical database: near-zero for recent transactions within backup windows
- object storage: near-zero with versioning and replication
- config and secrets: near-zero via managed storage

---

# Cost Estimates

The numbers below are directional estimates for planning and should be refined during architecture validation.

## Development
Estimated monthly cost:
- compute and networking: $120 - $400
- database and cache: $80 - $250
- storage: $20 - $100
- monitoring and logging: $30 - $120
- secrets and IAM: $10 - $50

Total:
- approximately $260 - $920 per month

## QA
Estimated monthly cost:
- compute and networking: $250 - $700
- database and cache: $120 - $350
- storage and backup: $50 - $200
- monitoring: $50 - $150

Total:
- approximately $470 - $1,400 per month

## UAT
Estimated monthly cost:
- compute: $400 - $1,200
- database and cache: $200 - $600
- storage: $80 - $250
- observability: $80 - $200

Total:
- approximately $760 - $2,250 per month

## Production
Estimated monthly cost:
- compute: $1,000 - $4,000
- database + cache: $500 - $2,500
- object storage: $150 - $600
- networking and WAF: $200 - $800
- monitoring and logs: $200 - $700
- backup and DR: $200 - $800

Total:
- approximately $2,250 - $9,400 per month

These values depend heavily on:
- traffic
- data volume
- backup duration
- storage type
- region
- chosen managed services
- autoscaling rules

---

# Infrastructure Roadmap

## Phase 1: MVP
Objectives:
- bootstrap secure development and QA environments
- deploy backend and frontend with minimal complexity
- validate DB, Redis, storage, and monitors
- enable zero-downtime deployment workflows

Deliverables:
- VPC / network baseline
- app environment for dev and QA
- managed PostgreSQL and Redis
- object storage
- ALB and autoscaling
- WAF and TLS
- GitHub Actions pipelines
- health, logs, and dashboards

---

## Phase 2: Scale
Objectives:
- increase resilience and performance
- optimize cost and autoscaling
- improve production availability
- strengthen security and metrics coverage

Deliverables:
- multi-AZ production deployment
- database replicas and DR pattern
- improved autoscaling policies
- centralized observability
- more robust alerting and runbooks
- capacity planning and release governance maturity

---

## Phase 3: Enterprise
Objectives:
- multi-environment governance and broader scale
- higher security and controls
- more advanced resilience and compliance posture
- operational maturity and enterprise platform support

Deliverables:
- cross-region DR
- strict enterprise IAM and policy enforcement
- advanced networking and segmentation
- robust audit, retention, and compliance controls
- cost optimization and platform lifecycle maturity
- advanced automation and release safety controls

---

# Terraform Structure

Recommended Terraform folder structure:

```text
infrastructure/
├── README.md
├── versions.tf
├── providers.tf
├── backend.tf
├── variables.tf
├── outputs.tf
├── locals.tf
├── modules/
│   ├── network/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── security/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── compute/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── database/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── cache/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── storage/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── monitoring/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── iam/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   ├── dns/
│   │   ├── main.tf
│   │   ├── variables.tf
│   │   └── outputs.tf
│   └── cd/
│       ├── main.tf
│       ├── variables.tf
│       └── outputs.tf
├── environments/
│   ├── dev/
│   │   ├── main.tf
│   │   ├── terraform.tfvars
│   │   └── backend.hcl
│   ├── qa/
│   │   ├── main.tf
│   │   ├── terraform.tfvars
│   │   └── backend.hcl
│   ├── uat/
│   │   ├── main.tf
│   │   ├── terraform.tfvars
│   │   └── backend.hcl
│   └── prod/
│       ├── main.tf
│       ├── terraform.tfvars
│       └── backend.hcl
└── scripts/
    ├── bootstrap.sh
    ├── validate.sh
    └── drift-check.sh
```

## Terraform Modules
Reusable modules should include:
- network module
- compute module
- database module
- cache module
- object storage module
- monitoring module
- IAM module
- DNS and TLS module
- CI/CD and environment module

---

# Deployment Checklist

## Pre-Deployment
- verify environment variables and secrets
- ensure Terraform state is available and locked
- validate DB migration scripts
- inspect Redis and storage configuration
- confirm domain and certificate configuration
- validate application health checks
- confirm monitoring and dashboards exist
- verify runbook and rollback plan
- validate security group and firewall posture
- verify traffic routing and expected service endpoints

## Deployment
- deploy infrastructure with IaC
- deploy application containers or artifacts
- run migration scripts in the correct order
- confirm service health
- monitor logs and error rate
- verify post-deploy smoke tests
- validate external integrations and notification flows
- validate admin and customer access paths

## Post-Deployment
- confirm platform health and metrics
- validate alerting and observability
- verify release notes and environment configuration
- check object storage and evidence uploads
- confirm backup schedules and retention
- document any exceptions or production issues
- close out go/no-go checklist with owners

---

# Risks

Risk list with impact, mitigation, and owner:

## 1. Database performance bottleneck
Impact:
- slow queries and degraded user experience

Mitigation:
- use connection pooling, read replicas, query tuning, and planned indexing
Owner:
- Database Engineering

## 2. Cache storm or Redis memory pressure
Impact:
- application latency and degraded cache efficiency

Mitigation:
- use TTL controls, cache invalidation strategy, and scaling plan
Owner:
- Platform Engineering

## 3. Incomplete backup or restore validation
Impact:
- unrecoverable data loss or extended downtime

Mitigation:
- scheduled restore drills and validated backup retention
Owner:
- DevOps / DBA

## 4. Public API exposure without WAF protection
Impact:
- abuse, malicious traffic, and potential service disruption

Mitigation:
- WAF policy, rate limiting, and DDoS protection
Owner:
- Security / Platform

## 5. Secret leakage
Impact:
- security breach and external vendor compromise

Mitigation:
- use managed secret stores, rotation, least privilege, and protected pipelines
Owner:
- Security

## 6. Overprovisioning leading to unnecessary spend
Impact:
- inflated monthly cloud cost

Mitigation:
- autoscaling, rightsizing, and environment-specific cost review
Owner:
- Platform / Finance

## 7. Inconsistent environment configuration
Impact:
- deployment failures and fragile release process

Mitigation:
- IaC enforcement and environment parity checks
Owner:
- DevOps

## 8. Container image vulnerabilities
Impact:
- exploitable runtime defects

Mitigation:
- dependency scans, image vulnerability scanning, and fix SLAs
Owner:
- Security / DevOps

## 9. Unvalidated production cutover
Impact:
- service interruption and customer-facing issues

Mitigation:
- pre-prod rehearsal, smoke tests, and release sign-off
Owner:
- Engineering / Release Manager

## 10. External service outages
Impact:
- failed notifications, payments, or integrations

Mitigation:
- retry logic, fallback workflows, and circuit breakers
Owner:
- Architecture / Integration

---

# Final Recommendation

The preferred infrastructure design for PropertyPilot is AWS-based, with a secure, modular, environment-aware architecture that supports:
- React frontend hosting
- Spring Boot backend application runtime
- PostgreSQL transactional persistence
- Redis caching and rate-limited access
- object storage for media and documents
- Dockerized container deployments
- GitHub Actions CI/CD
- integrated monitoring, security, and backup operations

The architecture should be implemented using Terraform from the start to ensure environment parity, controlled reproduction, and safer deployment practices.

For production operations, the platform should prioritize:
- multi-AZ deployment
- managed services
- strong IAM controls
- real-time observability
- regular backup and restore testing
- formal release governance
- explicit incident response and rollback runbooks

This plan gives PropertyPilot a practical and scalable cloud foundation for MVP, growth, and long-term enterprise readiness.
