# Schema Completion Backlog

## Overview
This document outlines the backlog of schema changes and enhancements that need to be completed for the database. Each entry includes a description of the task, its priority, and the current status.

## Backlog Items

| Task ID | Description                                      | Priority | Status       |
|---------|--------------------------------------------------|----------|--------------|
| SCB-001 | Add new column `last_updated` to `users` table  | High     | In Progress  |
| SCB-002 | Create `user_roles` reference table              | Medium   | Pending      |
| SCB-003 | Implement cascading delete on `orders` table     | Low      | Pending      |
| SCB-004 | Normalize `products` table                        | High     | Not Started  |
| SCB-005 | Add indexes on `email` column in `customers`     | Medium   | In Progress  |
| SCB-006 | Create `audit_log` table for tracking changes     | High     | Not Started  |
| SCB-007 | Update `payments` table to include `transaction_id` | Medium   | Pending      |
| SCB-008 | Review and optimize existing indexes              | Low      | Not Started  |
| SCB-009 | Add foreign key constraints to `orders` table    | High     | In Progress  |
| SCB-010 | Implement partitioning for `transactions` table   | Medium   | Pending      |

## Notes
- Priorities are categorized as High, Medium, or Low based on the impact on application functionality and performance.
- Status indicates the current progress of each task: Not Started, In Progress, or Pending.
- Regular reviews of this backlog will be conducted to adjust priorities and statuses as needed.