# Database Design

## Users

| Field | Type |
|---------|---------|
| id | UUID |
| name | String |
| mobile | String |
| email | String |
| role | String |
| created_at | Timestamp |

---

## Properties

| Field | Type |
|---------|---------|
| id | UUID |
| owner_id | UUID |
| title | String |
| description | Text |
| location | String |
| property_type | String |
| created_at | Timestamp |

---

## Services

| Field | Type |
|---------|---------|
| id | UUID |
| property_id | UUID |
| service_type | String |
| status | String |
| assigned_agent | UUID |
| created_at | Timestamp |

---

## Documents

| Field | Type |
|---------|---------|
| id | UUID |
| property_id | UUID |
| document_name | String |
| document_url | String |
| uploaded_at | Timestamp |