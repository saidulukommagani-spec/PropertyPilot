# Architecture Summary

## High-level architecture

PropertyPilot follows a modular platform model with separate concerns for:

- Customer and lead workflows
- Property and inventory operations
- Service request lifecycle management
- Evidence collection and reporting
- Notifications and admin operations

## System layers

1. Presentation layer
   - Flutter mobile app
   - React admin portal
2. API layer
   - Backend microservices
   - OpenAPI-based contracts
3. Domain layer
   - Customer, property, service request, reports
4. Data layer
   - PostgreSQL / relational schema
   - Data governance and canonical models
5. Operational layer
   - monitoring, release, deployment, and support runbooks

## Main service domains

- auth-service
- customer-service
- property-service
- service-request-service
- report-service
- notification-service

## Enterprise principles

- Domain-driven design
- API-first integration
- Secure-by-default model
- Data ownership and governance
- Modular deployment and scalability
