# API Design

## Common Contract

The versioned API, pagination, errors, idempotency, authentication, authorization, and webhook rules are defined in [Cross_Cutting_Requirements.md](Cross_Cutting_Requirements.md). All endpoints in this document are relative to `/api/v1`.

## Authentication APIs

POST /api/auth/register

POST /api/auth/login

POST /api/auth/send-otp

POST /api/auth/verify-otp

---

## User APIs

GET /api/users

GET /api/users/{id}

PUT /api/users/{id}

DELETE /api/users/{id}

---

## Property APIs

GET /api/properties

POST /api/properties

GET /api/properties/{id}

PUT /api/properties/{id}

DELETE /api/properties/{id}

---

## Service APIs

GET /api/services

POST /api/services

PUT /api/services/{id}

DELETE /api/services/{id}

---

## Document APIs

POST /api/documents/upload

GET /api/documents/{id}

DELETE /api/documents/{id}

Accept Service

Reject Service

Upload Photos

Submit Report

Update Status
Manage Pricing

Manage Payouts

Manage ETA

Manage Clusters

Manage Coverage
Send Notification

Notification History
Generate Report

Download Report

AI Summary
