# Canonical Data Dictionary

## Overview
The Canonical Data Dictionary serves as a comprehensive reference for the data elements utilized within the database. It provides definitions, descriptions, and relevant details for each data field, ensuring clarity and consistency across the application.

## Data Elements

| Field Name          | Data Type      | Description                                           | Constraints                     |
|---------------------|----------------|-------------------------------------------------------|---------------------------------|
| user_id             | INT            | Unique identifier for each user                       | PRIMARY KEY, NOT NULL           |
| username            | VARCHAR(255)   | The user's login name                                 | UNIQUE, NOT NULL                |
| email               | VARCHAR(255)   | The user's email address                              | UNIQUE, NOT NULL                |
| password_hash       | VARCHAR(255)   | Hashed password for user authentication               | NOT NULL                        |
| created_at          | TIMESTAMP      | Timestamp of when the user was created               | DEFAULT CURRENT_TIMESTAMP       |
| updated_at          | TIMESTAMP      | Timestamp of the last update to the user record      | DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP |
| subscription_id     | INT            | Unique identifier for each subscription               | PRIMARY KEY, NOT NULL           |
| subscription_type   | VARCHAR(100)   | Type of subscription (e.g., monthly, yearly)        | NOT NULL                        |
| start_date          | DATE           | Start date of the subscription                        | NOT NULL                        |
| end_date            | DATE           | End date of the subscription                          |                                 |
| payment_id          | INT            | Unique identifier for each payment                    | PRIMARY KEY, NOT NULL           |
| amount              | DECIMAL(10, 2) | Amount of the payment                                 | NOT NULL                        |
| payment_date        | TIMESTAMP      | Date and time when the payment was made              | DEFAULT CURRENT_TIMESTAMP       |
| notification_id     | INT            | Unique identifier for each notification               | PRIMARY KEY, NOT NULL           |
| notification_type   | VARCHAR(100)   | Type of notification (e.g., email, SMS)             | NOT NULL                        |
| message             | TEXT           | Content of the notification                           | NOT NULL                        |
| marketplace_id      | INT            | Unique identifier for each marketplace item          | PRIMARY KEY, NOT NULL           |
| item_name           | VARCHAR(255)   | Name of the marketplace item                          | NOT NULL                        |
| price               | DECIMAL(10, 2) | Price of the marketplace item                         | NOT NULL                        |
| complaint_id        | INT            | Unique identifier for each complaint                  | PRIMARY KEY, NOT NULL           |
| complaint_text      | TEXT           | Description of the complaint                          | NOT NULL                        |
| created_at          | TIMESTAMP      | Timestamp of when the complaint was created          | DEFAULT CURRENT_TIMESTAMP       |
| audit_id            | INT            | Unique identifier for each audit record               | PRIMARY KEY, NOT NULL           |
| action              | VARCHAR(100)   | Action performed (e.g., INSERT, UPDATE, DELETE)      | NOT NULL                        |
| action_timestamp    | TIMESTAMP      | Timestamp of when the action was performed            | DEFAULT CURRENT_TIMESTAMP       |

## Notes
- Ensure that all data types are consistent with the physical model outlined in `Database_Physical_Model.md`.
- Update this dictionary as new fields are added or existing fields are modified in the database schema.
- Maintain a version history for changes made to this document for tracking purposes.