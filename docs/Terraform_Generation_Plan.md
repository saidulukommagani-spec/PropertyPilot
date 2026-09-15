````markdown
# Terraform Generation Plan

Document Type: Infrastructure as Code Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Platform Engineering / DevOps / Security / Cloud Architecture

---

# Purpose

This document translates the PropertyPilot infrastructure bootstrap requirements into a production-grade Terraform implementation plan.

The plan defines how to provision and govern:
- VPC and network isolation
- public and private subnets
- RDS PostgreSQL
- Redis ElastiCache
- S3 object storage
- IAM roles and policies
- Application Load Balancer
- CloudWatch logging and alarms
- Secrets Manager
- backup policy
- disaster recovery strategy

The target architecture assumes AWS as the primary cloud provider and Terraform as the source of truth for environment provisioning.

---

# Terraform Strategy

## Core Principles
- infrastructure as code for all environments
- environment parity across dev, staging, and production
- immutable infrastructure patterns for app workloads
- least privilege IAM
- encrypted storage and transit
- explicit tagging and cost controls
- drift detection via terraform plan and CI validation
- separation of state by environment

## Target Environments
- dev
- staging
- production

## State Management
- backend: S3 with DynamoDB locking
- state file path pattern:
  - terraform/state/propertypilot/dev/terraform.tfstate
  - terraform/state/propertypilot/staging/terraform.tfstate
  - terraform/state/propertypilot/prod/terraform.tfstate
- state encryption enabled
- remote state with versioning enabled

## Terraform Version
- Terraform >= 1.7.x
- provider versions pinned
- module versions pinned
- no implicit provider upgrades

---

# Terraform Repository Layout

```text
terraform/
  modules/
    networking/
    database/
    cache/
    storage/
    iam/
    alb/
    observability/
    secrets/
    backup/
    dr/
  env/
    dev/
      main.tf
      variables.tf
      outputs.tf
      terraform.tfvars
    staging/
      main.tf
      variables.tf
      outputs.tf
      terraform.tfvars
    prod/
      main.tf
      variables.tf
      outputs.tf
      terraform.tfvars
  scripts/
    validate.sh
    plan.sh
    apply.sh
    drift.sh
  providers.tf
  versions.tf
  backend.tf
  data.tf
  locals.tf
  variables.tf
  outputs.tf
```

---

# Global Configuration

## Common Variables
- region
- environment
- project_name
- owner
- cost_center
- app_name
- tags
- cidr_block
- azs
- domain_name
- kms_key_aliases
- backup_retention_days

## Shared Tags
- Project
- Environment
- Owner
- ManagedBy
- CostCenter
- Application
- Terraform
- BackupPolicy
- ComplianceScope

## Naming Convention
Use a consistent naming pattern:
- propertypilot-dev-vpc
- propertypilot-dev-public-subnet-1a
- propertypilot-dev-rds-postgres
- propertypilot-dev-redis-cache
- propertypilot-dev-alb

---

# VPC

## Purpose
Provide network isolation, segmentation, and internet access boundaries for the platform.

## Design
- one VPC per environment
- multi-AZ layout
- separate public and private subnets
- NAT gateway for private subnets
- dedicated DB subnets and app subnets
- flow logs enabled

## Terraform Resources
- aws_vpc
- aws_internet_gateway
- aws_nat_gateway
- aws_eip
- aws_route_table
- aws_route_table_association
- aws_flow_log

## VPC Layout
- Public subnets:
  - ALB
  - bastion / jump host if required
- Private app subnets:
  - Spring Boot backend
  - frontend app nodes if applicable
- Private database subnets:
  - PostgreSQL RDS
  - Redis
- Security groups:
  - alb-sg
  - backend-sg
  - db-sg
  - redis-sg
  - bastion-sg

## Required Inputs
- cidr
- availability_zones
- public_subnet_cidrs
- private_app_subnet_cidrs
- private_db_subnet_cidrs

## Outputs
- vpc_id
- public_subnet_ids
- private_app_subnet_ids
- private_db_subnet_ids
- nat_gateway_ids

---

# Subnets

## Subnet Types
- public
- private app
- private data/db
- optional private ingress/admin subnet

## Requirements
- minimum two AZs for production
- one AZ acceptable for dev if cost constrained, but still aligned to platform standards
- subnets must avoid overlap
- route tables and ACLs explicit and documented

## Terraform Resources
- aws_subnet
- aws_route_table
- aws_route_table_association
- aws_network_acl
- aws_default_security_group (if required to harden)

## Security Considerations
- private subnets deny direct inbound internet traffic
- public subnets only expose ALB and bastion endpoints
- DB subnets only reachable via backend SG

---

# RDS

## Purpose
Provide durable relational persistence for PropertyPilot primary transactional datasets.

## Database Type
- PostgreSQL
- engine version pinned
- multi-AZ enabled in staging and production
- storage encryption at rest enabled
- automated backups enabled
- performance insights enabled
- deletion protection enabled in production

## Terraform Resources
- aws_db_subnet_group
- aws_db_instance
- aws_db_parameter_group
- aws_db_option_group
- aws_kms_key
- aws_kms_alias

## Configuration
- instance_class
- allocated_storage
- max_allocated_storage
- backup_retention_period
- monitoring_interval
- maintenance_window
- backup_window
- publicly_accessible = false
- multi_az = true for non-dev
- storage_encrypted = true
- skip_final_snapshot = false in prod
- deletion_protection = true in prod

## Security
- security group restricted to backend and app services
- secrets stored in AWS Secrets Manager
- DB master credentials only through secret references

## Outputs
- rds_endpoint
- rds_identifier
- rds_port
- secret_arn

---

# Redis

## Purpose
Provide in-memory caching, session support, rate limiting, and transient data acceleration.

## Service
- Amazon ElastiCache for Redis
- cluster mode optional based on scale
- encryption in transit enabled
- backup retention in production
- subnet group mapped to private subnets

## Terraform Resources
- aws_elasticache_subnet_group
- aws_elasticache_replication_group
- aws_elasticache_parameter_group
- aws_security_group

## Configuration
- engine version pinned
- node type scaled by environment
- automatic_failover_enabled
- multi_az_enabled
- transit_encryption_enabled
- at_rest_encryption_enabled
- snapshot_retention_limit
- maintenance_window

## Security
- private-only accessibility
- backend security group allowed
- no public endpoint

---

# S3

## Purpose
Store uploaded files, reports, generated documents, static frontend artifacts, backups, and logs where relevant.

## Buckets
- app-static-assets
- user-upload-documents
- reports
- backups
- logs

## Terraform Resources
- aws_s3_bucket
- aws_s3_bucket_versioning
- aws_s3_bucket_server_side_encryption_configuration
- aws_s3_bucket_public_access_block
- aws_s3_bucket_lifecycle_configuration
- aws_s3_bucket_policy
- aws_iam_policy for bucket access

## Security Controls
- block public access
- bucket policy restricts access by role
- encryption with AWS-managed or customer-managed KMS
- object lock for legal/compliance retention where required
- versioning enabled
- lifecycle rules for old artifacts
- logging enabled to central bucket

## Outputs
- bucket_arns
- bucket_names

---

# IAM

## Purpose
Define least-privilege access for:
- backend service role
- frontend deployment role
- database access role
- ECS/EKS or EC2 VM role
- Lambda or event workers if used
- Terraform deployment user or GitHub OIDC role

## Terraform Resources
- aws_iam_role
- aws_iam_policy
- aws_iam_policy_attachment
- aws_iam_instance_profile
- aws_iam_openid_connect_provider
- aws_iam_user (only if not using OIDC)

## Roles to Create
- backend_app_role
- frontend_app_role
- rds_access_role
- s3_access_role
- secrets_reader_role
- cloudwatch_logs_role
- deploy_role
- ci_cd_role

## Policy Strategy
- least privilege
- explicit resource ARNs
- no wildcard on production-sensitive actions
- no admin role for app-level services
- use OIDC for GitHub Actions to avoid static AWS access keys

## Security Best Practices
- no permanent AWS access keys in CI
- use IAM role assumption via OIDC provider
- rotate credentials if long-lived access is unavoidable

---

# ALB

## Purpose
Provide ingress routing for frontend and backend services with TLS termination and path-based routing.

## Components
- application load balancer
- target groups
- listeners
- security groups
- health checks
- SSL certificate via ACM

## Terraform Resources
- aws_lb
- aws_lb_target_group
- aws_lb_listener
- aws_acm_certificate
- aws_acm_certificate_validation
- aws_security_group
- aws_lb_listener_rule

## Routing Pattern
- /api/* -> backend target group
- /* -> frontend target group
- optional /admin/* -> dedicated admin target group
- optional /agent/* -> dedicated agent route target group

## Health Checks
- backend health endpoint
- frontend static host health
- unhealthy targets automatically removed
- failure thresholds and response checks enabled

## TLS
- certificate managed by ACM
- redirect HTTP to HTTPS
- route53 alias records for DNS

---

# CloudWatch

## Purpose
Provide observability, log retention, alerting, and dashboards for platform health and performance.

## Terraform Resources
- aws_cloudwatch_log_group
- aws_cloudwatch_metric_alarm
- aws_cloudwatch_dashboard
- aws_cloudwatch_log_metric_filter
- aws_cloudwatch_event_rule
- aws_cloudwatch_event_target

## Monitoring Coverage
- ALB target response metrics
- RDS CPU, connections, storage, latency
- Redis CPU, memory, evictions
- backend error rate and latency
- frontend page load metrics if integrated
- application logs for auth, payments, property service, reports

## Alerting Rules
- CPU > 80% for 10 min
- DB connection saturation
- Redis memory pressure
- 5xx rate spike above threshold
- ALB unhealthy target count > 0
- backup failures
- deployment anomalies

## Log Retention
- dev: 7–30 days
- staging: 30–90 days
- prod: 90–365 days depending on compliance needs

---

# Secrets Manager

## Purpose
Securely store secrets for:
- database credentials
- JWT signing
- payment provider credentials
- SMTP credentials
- cloud storage keys
- external integration tokens

## Terraform Resources
- aws_secretsmanager_secret
- aws_secretsmanager_secret_version
- aws_kms_key
- aws_kms_alias

## Secret Categories
- database
- redis
- jwt
- payment
- email
- cloud object storage
- third-party integration secrets

## Access Policy
- only app roles and operational roles can access
- no direct secret exposure in Terraform state
- rotation supported where provider supports it
- production secrets protected and monitored

---

# Backup

## Purpose
Ensure recoverability for data and infrastructure.

## Backup Policy
### RDS
- automated daily backups
- retention window:
  - dev: 7 days
  - staging: 14 days
  - prod: 30–90 days
- PITR enabled
- instance snapshots before major changes

### Redis
- automatic backups in production
- snapshot retention according to environment

### S3
- versioning enabled
- lifecycle transition to IA/Glacier
- cross-region replication for prod if required

### App Config
- source-controlled Terraform and application config
- backup of deployment manifests and Helm or EC2 bootstrap scripts

## Terraform Resources
- aws_db_instance with backup retention
- aws_elasticache_replication_group with snapshot retention
- aws_s3_bucket_versioning
- aws_s3_bucket_lifecycle_configuration
- aws_backup_vault
- aws_backup_plan
- aws_backup_selection
- aws_backup_vault_lock_configuration

## Recovery Testing
- quarterly restore drill
- backup verification in staging before prod rollout

---

# Disaster Recovery (DR)

## Purpose
Ensure service continuity during regional or partial service failure.

## DR Model
- active-passive or warm standby across two regions
- production data replicated to secondary region
- route53 health checks directing traffic based on health
- RDS cross-region snapshot or read replica
- S3 cross-region replication
- Redis replication or warm standby if required

## Recommended DR Layout
- primary region: us-east-1
- secondary region: us-west-2 or another paired region
- RDS cross-region read replica or snapshot-based recovery
- S3 cross-region replication
- CloudWatch alarms trigger incident response
- Terraform code prepared to re-provision secondary environment

## Terraform Resources
- aws_iam_role for disaster recovery
- aws_s3_bucket_replication_configuration
- aws_db_instance (read replica or standby)
- aws_route53_health_check
- aws_route53_record
- aws_route53_zone
- aws_cloudwatch_metric_alarm

## RTO/RPO Targets
- RTO:
  - dev: 24h
  - staging: 8h
  - prod: 1–4h
- RPO:
  - prod: < 15 min for critical data
  - non-prod: 24h

## DR Runbook
- failover steps
- cutover validation
- rollback to primary after restoration
- incident ownership and communication plan

---

# Terraform Module Design

## 1. Networking Module
Responsibilities:
- VPC
- subnets
- route tables
- NAT
- Internet Gateway
- flow logs

## 2. Database Module
Responsibilities:
- RDS PostgreSQL
- DB subnet group
- parameter group
- KMS
- security groups

## 3. Cache Module
Responsibilities:
- ElastiCache Redis
- subnet group
- security group
- parameter group

## 4. Storage Module
Responsibilities:
- S3 buckets
- encryption
- bucket policies
- lifecycle rules
- replication

## 5. IAM Module
Responsibilities:
- roles
- policies
- OIDC integration
- policy attachments

## 6. ALB Module
Responsibilities:
- ALB
- target groups
- listeners
- certificates
- security groups

## 7. Observability Module
Responsibilities:
- CloudWatch logs
- dashboards
- alarms
- log groups
- metric filters

## 8. Secrets Module
Responsibilities:
- Secrets Manager
- secret rotation
- KMS references

## 9. Backup Module
Responsibilities:
- RDS and Redis backups
- S3 versioning and lifecycle
- AWS Backup definitions

## 10. DR Module
Responsibilities:
- regional replication
- route53 failover
- DR readiness checks
- cross-region configuration

---

# Terraform Input and Output Contract

## Inputs
- region
- environment
- app_name
- vpc_cidr
- subnet_cidrs
- db_instance_size
- db_storage
- redis_node_type
- domain_name
- certificate_arn
- allowed_cidr_blocks
- enable_backup
- enable_dr
- tags

## Outputs
- vpc_id
- subnet_ids
- alb_dns
- rds_endpoint
- redis_endpoint
- s3_bucket_names
- secrets_arns
- iam_role_arns
- cloudwatch_dashboard_name

---

# Security Controls

## Network Security
- restrict inbound traffic by SG
- use private subnets for data layers
- no public database or cache access
- enforce deny-by-default rules
- use NACLs only where needed

## Data Security
- encryption at rest for RDS, Redis, S3
- encryption in transit via TLS
- strict KMS key policy
- secret rotation enabled where possible

## Access Security
- no root-level cloud access
- role-based access for deployments
- audit changes to infrastructure using Terraform plan history and CloudTrail

## Compliance Considerations
- tag-based governance
- region restrictions
- retention policies
- access logging

---

# Deployment Order

## Phase 1: Foundation
1. VPC
2. Subnets
3. Security groups
4. Route tables
5. NAT and IGW
6. IAM baseline

## Phase 2: Data Services
1. KMS keys
2. Secrets Manager
3. RDS
4. Redis
5. S3
6. backup policies

## Phase 3: App Ingress
1. ACM certificate
2. ALB
3. target groups
4. listeners
5. DNS

## Phase 4: Observability
1. CloudWatch log groups
2. dashboards
3. alarms
4. log retention policy

## Phase 5: DR and Hardening
1. cross-region replication
2. failover config
3. backup validation
4. incident routing and testing

---

# CI/CD Integration for Terraform

## Workflow Requirements
- terraform fmt
- terraform validate
- terraform plan on PR
- terraform apply only on approved environment
- drift detection scheduled
- PR comments include plan summary
- no apply without approval in protected environment

## GitHub Actions Jobs
- terraform-format
- terraform-validate
- terraform-plan
- terraform-apply-dev
- terraform-apply-staging
- terraform-apply-prod
- terraform-drift-check

## Required Checks
- plan output reviewed
- no destroy without explicit approval
- production apply requires release approval
- plan artifact stored for audit

---

# Risks

Risk | Impact | Mitigation | Owner
---|---|---|---
Lack of subnet segmentation | lateral movement risk | isolate app, data, and public tiers | Cloud Architecture
Weak IAM policies | privilege escalation | least privilege and OIDC-based GitHub access | Security
Overly permissive public access | exposure of data services | restrict SGs and public endpoints | DevOps
Backup gaps | recoverability failure | automate snapshot and retention controls | Platform Engineering
No DR procedure | downtime after regional outage | replicate critical data and test failover | DevOps / Security
Terraform drift | hidden environment mismatch | scheduled drift detection and reconciliation | Platform Engineering
Improper secret handling | security breach | Secrets Manager only, no hardcoded credentials | Security
Uncontrolled cost growth | budget overrun | tagging, cost alerts, budget alarms | Platform Engineering

---

# Success Criteria

Terraform is ready for:
- dev provisioning
- staging provisioning
- production provisioning
- operational support
- disaster recovery validation

When all of the following are true:
- VPC and subnet topology are provisioned repeatably
- RDS and Redis are encrypted and private
- S3 is secure and versioned
- IAM follows least privilege
- ALB and TLS termination are working
- CloudWatch logs and alarms are active
- Secrets Manager is populated and restricted
- backup and restore procedures are tested
- DR architecture is documented and failover-tested

---

# Final Recommendation

PropertyPilot should implement Terraform in modular, environment-aware layers with clear ownership boundaries:
- networking first
- data services second
- ingress and security third
- observability and secrets fourth
- backups and DR last

This produces a cloud foundation that is secure, repeatable, and ready for production support while ensuring environment parity and robust incident recovery.
```// filepath: c:\PropertyPilot\docs\Terraform_Generation_Plan.md
# Terraform Generation Plan

Document Type: Infrastructure as Code Blueprint  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Platform Engineering / DevOps / Security / Cloud Architecture

---

# Purpose

This document translates the PropertyPilot infrastructure bootstrap requirements into a production-grade Terraform implementation plan.

The plan defines how to provision and govern:
- VPC and network isolation
- public and private subnets
- RDS PostgreSQL
- Redis ElastiCache
- S3 object storage
- IAM roles and policies
- Application Load Balancer
- CloudWatch logging and alarms
- Secrets Manager
- backup policy
- disaster recovery strategy

The target architecture assumes AWS as the primary cloud provider and Terraform as the source of truth for environment provisioning.

---

# Terraform Strategy

## Core Principles
- infrastructure as code for all environments
- environment parity across dev, staging, and production
- immutable infrastructure patterns for app workloads
- least privilege IAM
- encrypted storage and transit
- explicit tagging and cost controls
- drift detection via terraform plan and CI validation
- separation of state by environment

## Target Environments
- dev
- staging
- production

## State Management
- backend: S3 with DynamoDB locking
- state file path pattern:
  - terraform/state/propertypilot/dev/terraform.tfstate
  - terraform/state/propertypilot/staging/terraform.tfstate
  - terraform/state/propertypilot/prod/terraform.tfstate
- state encryption enabled
- remote state with versioning enabled

## Terraform Version
- Terraform >= 1.7.x
- provider versions pinned
- module versions pinned
- no implicit provider upgrades

---

# Terraform Repository Layout

```text
terraform/
  modules/
    networking/
    database/
    cache/
    storage/
    iam/
    alb/
    observability/
    secrets/
    backup/
    dr/
  env/
    dev/
      main.tf
      variables.tf
      outputs.tf
      terraform.tfvars
    staging/
      main.tf
      variables.tf
      outputs.tf
      terraform.tfvars
    prod/
      main.tf
      variables.tf
      outputs.tf
      terraform.tfvars
  scripts/
    validate.sh
    plan.sh
    apply.sh
    drift.sh
  providers.tf
  versions.tf
  backend.tf
  data.tf
  locals.tf
  variables.tf
  outputs.tf
```

---

# Global Configuration

## Common Variables
- region
- environment
- project_name
- owner
- cost_center
- app_name
- tags
- cidr_block
- azs
- domain_name
- kms_key_aliases
- backup_retention_days

## Shared Tags
- Project
- Environment
- Owner
- ManagedBy
- CostCenter
- Application
- Terraform
- BackupPolicy
- ComplianceScope

## Naming Convention
Use a consistent naming pattern:
- propertypilot-dev-vpc
- propertypilot-dev-public-subnet-1a
- propertypilot-dev-rds-postgres
- propertypilot-dev-redis-cache
- propertypilot-dev-alb

---

# VPC

## Purpose
Provide network isolation, segmentation, and internet access boundaries for the platform.

## Design
- one VPC per environment
- multi-AZ layout
- separate public and private subnets
- NAT gateway for private subnets
- dedicated DB subnets and app subnets
- flow logs enabled

## Terraform Resources
- aws_vpc
- aws_internet_gateway
- aws_nat_gateway
- aws_eip
- aws_route_table
- aws_route_table_association
- aws_flow_log

## VPC Layout
- Public subnets:
  - ALB
  - bastion / jump host if required
- Private app subnets:
  - Spring Boot backend
  - frontend app nodes if applicable
- Private database subnets:
  - PostgreSQL RDS
  - Redis
- Security groups:
  - alb-sg
  - backend-sg
  - db-sg
  - redis-sg
  - bastion-sg

## Required Inputs
- cidr
- availability_zones
- public_subnet_cidrs
- private_app_subnet_cidrs
- private_db_subnet_cidrs

## Outputs
- vpc_id
- public_subnet_ids
- private_app_subnet_ids
- private_db_subnet_ids
- nat_gateway_ids

---

# Subnets

## Subnet Types
- public
- private app
- private data/db
- optional private ingress/admin subnet

## Requirements
- minimum two AZs for production
- one AZ acceptable for dev if cost constrained, but still aligned to platform standards
- subnets must avoid overlap
- route tables and ACLs explicit and documented

## Terraform Resources
- aws_subnet
- aws_route_table
- aws_route_table_association
- aws_network_acl
- aws_default_security_group (if required to harden)

## Security Considerations
- private subnets deny direct inbound internet traffic
- public subnets only expose ALB and bastion endpoints
- DB subnets only reachable via backend SG

---

# RDS

## Purpose
Provide durable relational persistence for PropertyPilot primary transactional datasets.

## Database Type
- PostgreSQL
- engine version pinned
- multi-AZ enabled in staging and production
- storage encryption at rest enabled
- automated backups enabled
- performance insights enabled
- deletion protection enabled in production

## Terraform Resources
- aws_db_subnet_group
- aws_db_instance
- aws_db_parameter_group
- aws_db_option_group
- aws_kms_key
- aws_kms_alias

## Configuration
- instance_class
- allocated_storage
- max_allocated_storage
- backup_retention_period
- monitoring_interval
- maintenance_window
- backup_window
- publicly_accessible = false
- multi_az = true for non-dev
- storage_encrypted = true
- skip_final_snapshot = false in prod
- deletion_protection = true in prod

## Security
- security group restricted to backend and app services
- secrets stored in AWS Secrets Manager
- DB master credentials only through secret references

## Outputs
- rds_endpoint
- rds_identifier
- rds_port
- secret_arn

---

# Redis

## Purpose
Provide in-memory caching, session support, rate limiting, and transient data acceleration.

## Service
- Amazon ElastiCache for Redis
- cluster mode optional based on scale
- encryption in transit enabled
- backup retention in production
- subnet group mapped to private subnets

## Terraform Resources
- aws_elasticache_subnet_group
- aws_elasticache_replication_group
- aws_elasticache_parameter_group
- aws_security_group

## Configuration
- engine version pinned
- node type scaled by environment
- automatic_failover_enabled
- multi_az_enabled
- transit_encryption_enabled
- at_rest_encryption_enabled
- snapshot_retention_limit
- maintenance_window

## Security
- private-only accessibility
- backend security group allowed
- no public endpoint

---

# S3

## Purpose
Store uploaded files, reports, generated documents, static frontend artifacts, backups, and logs where relevant.

## Buckets
- app-static-assets
- user-upload-documents
- reports
- backups
- logs

## Terraform Resources
- aws_s3_bucket
- aws_s3_bucket_versioning
- aws_s3_bucket_server_side_encryption_configuration
- aws_s3_bucket_public_access_block
- aws_s3_bucket_lifecycle_configuration
- aws_s3_bucket_policy
- aws_iam_policy for bucket access

## Security Controls
- block public access
- bucket policy restricts access by role
- encryption with AWS-managed or customer-managed KMS
- object lock for legal/compliance retention where required
- versioning enabled
- lifecycle rules for old artifacts
- logging enabled to central bucket

## Outputs
- bucket_arns
- bucket_names

---

# IAM

## Purpose
Define least-privilege access for:
- backend service role
- frontend deployment role
- database access role
- ECS/EKS or EC2 VM role
- Lambda or event workers if used
- Terraform deployment user or GitHub OIDC role

## Terraform Resources
- aws_iam_role
- aws_iam_policy
- aws_iam_policy_attachment
- aws_iam_instance_profile
- aws_iam_openid_connect_provider
- aws_iam_user (only if not using OIDC)

## Roles to Create
- backend_app_role
- frontend_app_role
- rds_access_role
- s3_access_role
- secrets_reader_role
- cloudwatch_logs_role
- deploy_role
- ci_cd_role

## Policy Strategy
- least privilege
- explicit resource ARNs
- no wildcard on production-sensitive actions
- no admin role for app-level services
- use OIDC for GitHub Actions to avoid static AWS access keys

## Security Best Practices
- no permanent AWS access keys in CI
- use IAM role assumption via OIDC provider
- rotate credentials if long-lived access is unavoidable

---

# ALB

## Purpose
Provide ingress routing for frontend and backend services with TLS termination and path-based routing.

## Components
- application load balancer
- target groups
- listeners
- security groups
- health checks
- SSL certificate via ACM

## Terraform Resources
- aws_lb
- aws_lb_target_group
- aws_lb_listener
- aws_acm_certificate
- aws_acm_certificate_validation
- aws_security_group
- aws_lb_listener_rule

## Routing Pattern
- /api/* -> backend target group
- /* -> frontend target group
- optional /admin/* -> dedicated admin target group
- optional /agent/* -> dedicated agent route target group

## Health Checks
- backend health endpoint
- frontend static host health
- unhealthy targets automatically removed
- failure thresholds and response checks enabled

## TLS
- certificate managed by ACM
- redirect HTTP to HTTPS
- route53 alias records for DNS

---

# CloudWatch

## Purpose
Provide observability, log retention, alerting, and dashboards for platform health and performance.

## Terraform Resources
- aws_cloudwatch_log_group
- aws_cloudwatch_metric_alarm
- aws_cloudwatch_dashboard
- aws_cloudwatch_log_metric_filter
- aws_cloudwatch_event_rule
- aws_cloudwatch_event_target

## Monitoring Coverage
- ALB target response metrics
- RDS CPU, connections, storage, latency
- Redis CPU, memory, evictions
- backend error rate and latency
- frontend page load metrics if integrated
- application logs for auth, payments, property service, reports

## Alerting Rules
- CPU > 80% for 10 min
- DB connection saturation
- Redis memory pressure
- 5xx rate spike above threshold
- ALB unhealthy target count > 0
- backup failures
- deployment anomalies

## Log Retention
- dev: 7–30 days
- staging: 30–90 days
- prod: 90–365 days depending on compliance needs

---

# Secrets Manager

## Purpose
Securely store secrets for:
- database credentials
- JWT signing
- payment provider credentials
- SMTP credentials
- cloud storage keys
- external integration tokens

## Terraform Resources
- aws_secretsmanager_secret
- aws_secretsmanager_secret_version
- aws_kms_key
- aws_kms_alias

## Secret Categories
- database
- redis
- jwt
- payment
- email
- cloud object storage
- third-party integration secrets

## Access Policy
- only app roles and operational roles can access
- no direct secret exposure in Terraform state
- rotation supported where provider supports it
- production secrets protected and monitored

---

# Backup

## Purpose
Ensure recoverability for data and infrastructure.

## Backup Policy
### RDS
- automated daily backups
- retention window:
  - dev: 7 days
  - staging: 14 days
  - prod: 30–90 days
- PITR enabled
- instance snapshots before major changes

### Redis
- automatic backups in production
- snapshot retention according to environment

### S3
- versioning enabled
- lifecycle transition to IA/Glacier
- cross-region replication for prod if required

### App Config
- source-controlled Terraform and application config
- backup of deployment manifests and Helm or EC2 bootstrap scripts

## Terraform Resources
- aws_db_instance with backup retention
- aws_elasticache_replication_group with snapshot retention
- aws_s3_bucket_versioning
- aws_s3_bucket_lifecycle_configuration
- aws_backup_vault
- aws_backup_plan
- aws_backup_selection
- aws_backup_vault_lock_configuration

## Recovery Testing
- quarterly restore drill
- backup verification in staging before prod rollout

---

# Disaster Recovery (DR)

## Purpose
Ensure service continuity during regional or partial service failure.

## DR Model
- active-passive or warm standby across two regions
- production data replicated to secondary region
- route53 health checks directing traffic based on health
- RDS cross-region snapshot or read replica
- S3 cross-region replication
- Redis replication or warm standby if required

## Recommended DR Layout
- primary region: us-east-1
- secondary region: us-west-2 or another paired region
- RDS cross-region read replica or snapshot-based recovery
- S3 cross-region replication
- CloudWatch alarms trigger incident response
- Terraform code prepared to re-provision secondary environment

## Terraform Resources
- aws_iam_role for disaster recovery
- aws_s3_bucket_replication_configuration
- aws_db_instance (read replica or standby)
- aws_route53_health_check
- aws_route53_record
- aws_route53_zone
- aws_cloudwatch_metric_alarm

## RTO/RPO Targets
- RTO:
  - dev: 24h
  - staging: 8h
  - prod: 1–4h
- RPO:
  - prod: < 15 min for critical data
  - non-prod: 24h

## DR Runbook
- failover steps
- cutover validation
- rollback to primary after restoration
- incident ownership and communication plan

---

# Terraform Module Design

## 1. Networking Module
Responsibilities:
- VPC
- subnets
- route tables
- NAT
- Internet Gateway
- flow logs

## 2. Database Module
Responsibilities:
- RDS PostgreSQL
- DB subnet group
- parameter group
- KMS
- security groups

## 3. Cache Module
Responsibilities:
- ElastiCache Redis
- subnet group
- security group
- parameter group

## 4. Storage Module
Responsibilities:
- S3 buckets
- encryption
- bucket policies
- lifecycle rules
- replication

## 5. IAM Module
Responsibilities:
- roles
- policies
- OIDC integration
- policy attachments

## 6. ALB Module
Responsibilities:
- ALB
- target groups
- listeners
- certificates
- security groups

## 7. Observability Module
Responsibilities:
- CloudWatch logs
- dashboards
- alarms
- log groups
- metric filters

## 8. Secrets Module
Responsibilities:
- Secrets Manager
- secret rotation
- KMS references

## 9. Backup Module
Responsibilities:
- RDS and Redis backups
- S3 versioning and lifecycle
- AWS Backup definitions

## 10. DR Module
Responsibilities:
- regional replication
- route53 failover
- DR readiness checks
- cross-region configuration

---

# Terraform Input and Output Contract

## Inputs
- region
- environment
- app_name
- vpc_cidr
- subnet_cidrs
- db_instance_size
- db_storage
- redis_node_type
- domain_name
- certificate_arn
- allowed_cidr_blocks
- enable_backup
- enable_dr
- tags

## Outputs
- vpc_id
- subnet_ids
- alb_dns
- rds_endpoint
- redis_endpoint
- s3_bucket_names
- secrets_arns
- iam_role_arns
- cloudwatch_dashboard_name

---

# Security Controls

## Network Security
- restrict inbound traffic by SG
- use private subnets for data layers
- no public database or cache access
- enforce deny-by-default rules
- use NACLs only where needed

## Data Security
- encryption at rest for RDS, Redis, S3
- encryption in transit via TLS
- strict KMS key policy
- secret rotation enabled where possible

## Access Security
- no root-level cloud access
- role-based access for deployments
- audit changes to infrastructure using Terraform plan history and CloudTrail

## Compliance Considerations
- tag-based governance
- region restrictions
- retention policies
- access logging

---

# Deployment Order

## Phase 1: Foundation
1. VPC
2. Subnets
3. Security groups
4. Route tables
5. NAT and IGW
6. IAM baseline

## Phase 2: Data Services
1. KMS keys
2. Secrets Manager
3. RDS
4. Redis
5. S3
6. backup policies

## Phase 3: App Ingress
1. ACM certificate
2. ALB
3. target groups
4. listeners
5. DNS

## Phase 4: Observability
1. CloudWatch log groups
2. dashboards
3. alarms
4. log retention policy

## Phase 5: DR and Hardening
1. cross-region replication
2. failover config
3. backup validation
4. incident routing and testing

---

# CI/CD Integration for Terraform

## Workflow Requirements
- terraform fmt
- terraform validate
- terraform plan on PR
- terraform apply only on approved environment
- drift detection scheduled
- PR comments include plan summary
- no apply without approval in protected environment

## GitHub Actions Jobs
- terraform-format
- terraform-validate
- terraform-plan
- terraform-apply-dev
- terraform-apply-staging
- terraform-apply-prod
- terraform-drift-check

## Required Checks
- plan output reviewed
- no destroy without explicit approval
- production apply requires release approval
- plan artifact stored for audit

---

# Risks

Risk | Impact | Mitigation | Owner
---|---|---|---
Lack of subnet segmentation | lateral movement risk | isolate app, data, and public tiers | Cloud Architecture
Weak IAM policies | privilege escalation | least privilege and OIDC-based GitHub access | Security
Overly permissive public access | exposure of data services | restrict SGs and public endpoints | DevOps
Backup gaps | recoverability failure | automate snapshot and retention controls | Platform Engineering
No DR procedure | downtime after regional outage | replicate critical data and test failover | DevOps / Security
Terraform drift | hidden environment mismatch | scheduled drift detection and reconciliation | Platform Engineering
Improper secret handling | security breach | Secrets Manager only, no hardcoded credentials | Security
Uncontrolled cost growth | budget overrun | tagging, cost alerts, budget alarms | Platform Engineering

---

# Success Criteria

Terraform is ready for:
- dev provisioning
- staging provisioning
- production provisioning
- operational support
- disaster recovery validation

When all of the following are true:
- VPC and subnet topology are provisioned repeatably
- RDS and Redis are encrypted and private
- S3 is secure and versioned
- IAM follows least privilege
- ALB and TLS termination are working
- CloudWatch logs and alarms are active
- Secrets Manager is populated and restricted
- backup and restore procedures are tested
- DR architecture is documented and failover-tested

---

# Final Recommendation

PropertyPilot should implement Terraform in modular, environment-aware layers with clear ownership boundaries:
- networking first
- data services second
- ingress and security third
- observability and secrets fourth
- backups and DR last

This produces a cloud foundation that is secure, repeatable, and ready for production support while ensuring environment parity and robust incident recovery.
