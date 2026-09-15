# Database Physical Model

## Tables Overview

### Core Tables
- **Users**
  - `user_id`: INT, Primary Key
  - `username`: VARCHAR(50), Unique
  - `password_hash`: VARCHAR(255)
  - `email`: VARCHAR(100), Unique
  - `created_at`: TIMESTAMP
  - `updated_at`: TIMESTAMP

- **Products**
  - `product_id`: INT, Primary Key
  - `name`: VARCHAR(100)
  - `description`: TEXT
  - `price`: DECIMAL(10, 2)
  - `created_at`: TIMESTAMP
  - `updated_at`: TIMESTAMP

### Reference Tables
- **Categories**
  - `category_id`: INT, Primary Key
  - `category_name`: VARCHAR(100)
  - `created_at`: TIMESTAMP
  - `updated_at`: TIMESTAMP

### Subscription Tables
- **Subscriptions**
  - `subscription_id`: INT, Primary Key
  - `user_id`: INT, Foreign Key (Users)
  - `start_date`: DATE
  - `end_date`: DATE
  - `status`: VARCHAR(20)
  - `created_at`: TIMESTAMP
  - `updated_at`: TIMESTAMP

### Payment Tables
- **Payments**
  - `payment_id`: INT, Primary Key
  - `user_id`: INT, Foreign Key (Users)
  - `amount`: DECIMAL(10, 2)
  - `payment_date`: TIMESTAMP
  - `status`: VARCHAR(20)

### Notification Tables
- **Notifications**
  - `notification_id`: INT, Primary Key
  - `user_id`: INT, Foreign Key (Users)
  - `message`: TEXT
  - `is_read`: BOOLEAN
  - `created_at`: TIMESTAMP

### Marketplace Tables
- **Orders**
  - `order_id`: INT, Primary Key
  - `user_id`: INT, Foreign Key (Users)
  - `product_id`: INT, Foreign Key (Products)
  - `order_date`: TIMESTAMP
  - `status`: VARCHAR(20)

### Complaint Tables
- **Complaints**
  - `complaint_id`: INT, Primary Key
  - `user_id`: INT, Foreign Key (Users)
  - `order_id`: INT, Foreign Key (Orders)
  - `description`: TEXT
  - `status`: VARCHAR(20)
  - `created_at`: TIMESTAMP

### Audit Tables
- **Audit_Log**
  - `audit_id`: INT, Primary Key
  - `table_name`: VARCHAR(50)
  - `operation`: VARCHAR(10)
  - `changed_data`: JSON
  - `changed_at`: TIMESTAMP

## Indexes
- Create indexes on `username`, `email` in Users table for faster lookups.
- Create indexes on `product_id` in Orders table for efficient joins.

## Constraints
- Foreign key constraints on `user_id` in Subscriptions, Payments, Notifications, Orders, and Complaints referencing Users.
- Unique constraints on `username` and `email` in Users table.

## Partitioning
- Implement partitioning on the Orders table based on `order_date` to improve query performance for large datasets.

## Seed Data
- Insert initial data into Categories, Users, and Products tables to facilitate testing and development.

## Rollback Strategy
- Each migration should include a corresponding rollback script to revert changes if necessary, ensuring data integrity and consistency.