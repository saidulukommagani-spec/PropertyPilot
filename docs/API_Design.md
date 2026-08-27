# API Design

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