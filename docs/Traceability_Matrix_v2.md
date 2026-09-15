# Requirements Traceability

| Requirement ID | Requirement | Journey | Screen | API | Database Table | Workflow | Event | Notification |
|---|---|---|---|---|---|---|---|---|
| REQ-001 | Customer Registration | Customer Onboarding Journey | Registration, KYC Status | POST /api/v1/customers, POST /api/v1/kyc | customer, kyc_profile, kyc_document | Registration → KYC → Approval | customer.created, kyc.submitted | Registration received |
| REQ-002 | Customer Profile Management | Profile Management Journey | Profile, Account Settings | GET/PUT /api/v1/customers/{id} | customer, customer_profile | Edit profile → validate → save | customer.updated | Profile changed |
| REQ-003 | Property Listing & Search | Property Discovery | Property Search, Property Details | GET /api/v1/properties, GET /api/v1/properties/{id} | property, property_listing | Search → filter → view | property.viewed | Property match |
| REQ-004 | Property Ownership Management | Property Transaction Journey | Property Ownership, Ownership Register | GET/POST /api/v1/properties/{id}/ownership | property, property_owner, ownership_record | Add owner → validate → commit | ownership.updated | Ownership updated |
| REQ-005 | Service Request Submission | Service Request Journey | Service Request Create | POST /api/v1/service-requests | service_request, service_request_status | Create → assign → update | service_request.created | Request received |
| REQ-006 | Service Request Status Tracking | Service Journey | Service Request Detail, Status Board | GET /api/v1/service-requests/{id}, PATCH /api/v1/service-requests/{id}/status | service_request, service_request_status | Open → assigned → in_progress → resolved | service_request.status_changed | Status update |
| REQ-007 | Subscription Purchase | Subscription Journey | Plan Selection, Checkout | POST /api/v1/subscriptions | subscription, plan, billing_cycle | Select plan → authorize → activate | subscription.created | Plan activated |
| REQ-008 | Subscription Upgrade & Downgrade | Subscription Lifecycle Journey | Subscription Details | POST /api/v1/subscriptions/{id}/upgrade, /downgrade | subscription, plan_history, billing_cycle | Upgrade/downgrade → re-pricing → confirm | subscription.changed | Plan change |
| REQ-009 | Subscription Pause/Resume/Cancel | Subscription Lifecycle Journey | Subscription Details | POST /api/v1/subscriptions/{id}/pause, /resume, /cancel | subscription, subscription_status_history | Pause/resume/cancel → billing update | subscription.paused | Subscription paused/cancelled |
| REQ-010 | Auto Renewal Consent | Billing Consent Journey | Billing Settings | PATCH /api/v1/subscriptions/{id}/auto-renewal | subscription, consent_record | Consent capture → store → apply | subscription.auto_renewal_changed | Auto-renewal notice |
| REQ-011 | Payment Capture & Authorization | Billing Journey | Payment Checkout | POST /api/v1/payments | payment, invoice, payment_gateway_txn | Collect payment → confirm → invoice | payment.authorized | Payment confirmation |
| REQ-012 | Refund Request | Billing Support Journey | Refund Request, Payment Detail | POST /api/v1/payments/{id}/refunds | refund, payment, refund_status | Request → verify → approve → settle | refund.requested | Refund initiated |
| REQ-013 | Invoice Download | Billing & Reporting Journey | Invoice Details | GET /api/v1/invoices/{id}/download | invoice, invoice_item | Generate invoice → deliver | invoice.generated | Invoice sent |
| REQ-014 | Reconciliation | Finance Operations Journey | Reconciliation Queue | POST /api/v1/payments/reconciliation | reconciliation_batch, payment | Batch reconcile → verify → resolve | payment.reconciled | Reconciliation report |
| REQ-015 | KYC Submission | Customer Onboarding Journey | Customer KYC | POST /api/v1/kyc | kyc_profile, kyc_document, kyc_review_case | Submit → validate → review | kyc.submitted | Submission received |
| REQ-016 | KYC Review & Approval | KYC Review Journey | KYC Review | POST /api/v1/kyc/{id}/review, /approve, /reject | kyc_review_case, kyc_audit_log | Review → approve/reject → notify | kyc.reviewed | Approved / rejected |
| REQ-017 | KYC Rework | KYC Remediation Journey | KYC Rework | POST /api/v1/kyc/{id}/rework | kyc_rework_request, kyc_document | Reject → feedback → resubmit | kyc.rework_required | Rework required |
| REQ-018 | Notification Delivery Tracking | Communication Journey | Notification Center | GET /api/v1/notifications/{id}/delivery-status | notification, notification_delivery | Send → status check → retry | notification.delivered | Delivery status |
| REQ-019 | Notification Preference Management | Settings Journey | Notification Preferences | GET/PATCH /api/v1/users/{id}/notification-preferences | user_notification_preference | Update preferences → apply rules | notification.preference_changed | Preference changed |
| REQ-020 | Marketplace Quote Request | Marketplace Journey | Quote Request, Compare Quotes | POST /api/v1/marketplace/quotes/requests, /compare | quote_request, quote_offer | Request → compare → select | quote.requested | Quote request |
| REQ-021 | Vendor Approval & Assignment | Marketplace Workflow | Vendor Dashboard | POST /api/v1/vendors/{id}/approve, POST /api/v1/service-requests/{id}/vendor-assignment | vendor, vendor_approval, assignment | Approve vendor → assign job | vendor.approved | Vendor assigned |
| REQ-022 | Construction Project Creation | Construction Journey | Project Setup | POST /api/v1/projects | project, project_milestone | Create project → milestone → approve | project.created | Project created |
| REQ-023 | Milestone Approval | Construction Operations Journey | Milestone Review | POST /api/v1/projects/{id}/milestones/{mid}/approve | project_milestone, milestone_approval | Approve milestone → track payment | milestone.approved | Milestone approved |
| REQ-024 | Change Order Management | Construction Change Control | Change Order Screen | POST /api/v1/projects/{id}/change-orders | change_order, project | Create → review → approve | change_order.created | Change order created |
| REQ-025 | Warranty Claim Process | Post-Delivery Journey | Warranty Claim | POST /api/v1/projects/{id}/warranty-claims | warranty_claim, evidence | Claim → triage → resolve | warranty_claim.created | Warranty claim created |
| REQ-026 | Data Export | Privacy Rights Journey | Privacy Center | POST /api/v1/privacy/data-export | privacy_request, data_export_job | Request → compile → secure delivery | privacy.data_export_requested | Export ready |
| REQ-027 | Data Deletion | Privacy Rights Journey | Privacy Center | POST /api/v1/privacy/data-delete | privacy_request, data_deletion_job | Validate → legal hold → delete | privacy.data_delete_requested | Deletion started |
| REQ-028 | Consent Withdrawal | Privacy Rights Journey | Privacy Center | POST /api/v1/privacy/consent-withdrawal | privacy_consent, privacy_request | Revoke consent → update systems | privacy.consent_withdrawn | Consent withdrawn |
| REQ-029 | Legal Hold | Compliance / Privacy Journey | Legal Hold Review | POST /api/v1/privacy/legal-hold | legal_hold, legal_hold_scope | Trigger → approve → enforce | legal_hold.created | Legal hold applied |
| REQ-030 | Privacy Request Status | Privacy Operations Journey | Privacy Request Tracker | GET /api/v1/privacy/requests/{id} | privacy_request | Track status → resolve → close | privacy.request_status_changed | Request update |
| REQ-031 | Webhook Callback Processing | Provider Integration | Webhook Receiver | POST /api/v1/webhooks/payments, /sms, /whatsapp | webhook_event, webhook_delivery | Receive → validate → route | webhook.received | Webhook ack |
| REQ-032 | Reporting & Analytics | Reporting Journey | Dashboards, Reports | GET /api/v1/reports/{id}, POST /api/v1/reports/{id}/export | report_definition, report_job, analytics_event | Generate → export → distribute | report.generated | Report ready |

# Screen Traceability

| Screen | APIs | Tables | Workflows |
|---|---|---|---|
| Registration | POST /api/v1/customers, POST /api/v1/kyc | customer, kyc_profile, kyc_document | Registration → KYC → Activation |
| Customer KYC | POST /api/v1/kyc, GET /api/v1/kyc/{id} | kyc_profile, kyc_document, kyc_review_case | KYC submit → review → approve |
| KYC Status | GET /api/v1/kyc/{id}, GET /api/v1/kyc | kyc_profile, kyc_review_case | status check → action |
| KYC Review | POST /api/v1/kyc/{id}/review, /approve, /reject | kyc_review_case, kyc_audit_log | review → approve/reject |
| KYC Rework | POST /api/v1/kyc/{id}/rework | kyc_rework_request, kyc_document | reject → feedback → resubmit |
| Profile | GET/PUT /api/v1/customers/{id} | customer, customer_profile | update profile |
| Property Search | GET /api/v1/properties | property, property_listing | browse → filter → view |
| Property Details | GET /api/v1/properties/{id} | property, property_owner | view property → select action |
| Service Request Create | POST /api/v1/service-requests | service_request | create → assign |
| Service Request Detail | GET/PATCH /api/v1/service-requests/{id} | service_request, service_request_status | status update |
| Subscription Plans | GET /api/v1/plans, /api/v1/subscriptions | plan, subscription | select plan |
| Subscription Management | GET/PATCH /api/v1/subscriptions/{id}, /upgrade, /pause, /resume, /cancel | subscription, subscription_status_history | lifecycle management |
| Billing & Checkout | POST /api/v1/payments, /api/v1/invoices/{id}/download | payment, invoice | payment → invoice |
| Refund Request | POST /api/v1/payments/{id}/refunds | refund, payment | request → verify → settle |
| Notification Center | GET /api/v1/notifications/{id}/delivery-status | notification, notification_delivery | message send → receive → track |
| Notification Preferences | GET/PATCH /api/v1/users/{id}/notification-preferences | user_notification_preference | update preferences |
| Marketplace Quote Request | POST /api/v1/marketplace/quotes/requests | quote_request, quote_offer | quote → compare → select |
| Vendor Dashboard | POST /api/v1/vendors/{id}/approve | vendor, vendor_approval | approve → assign |
| Project Setup | POST /api/v1/projects | project | create project |
| Milestone Review | POST /api/v1/projects/{id}/milestones/{mid}/approve | project_milestone, milestone_approval | approve milestone |
| Change Order | POST /api/v1/projects/{id}/change-orders | change_order | create → review → approve |
| Warranty Claim | POST /api/v1/projects/{id}/warranty-claims | warranty_claim | claim → triage → resolve |
| Privacy Center | POST /api/v1/privacy/data-export, /data-delete, /consent-withdrawal | privacy_request, privacy_consent | request → validate → process |
| Legal Hold | POST /api/v1/privacy/legal-hold | legal_hold, legal_hold_scope | trigger → enforce → release |
| Dashboard / Reports | GET /api/v1/reports/{id}; POST /api/v1/reports/{id}/export | report_definition, report_job | generate → export |

# API Traceability

| API | Screen | Database | Workflow |
|---|---|---|---|
| POST /api/v1/customers | Registration | customer | create account |
| POST /api/v1/kyc | Customer KYC | kyc_profile, kyc_document | submit KYC |
| GET /api/v1/kyc/{id} | KYC Status | kyc_profile, kyc_review_case | status check |
| POST /api/v1/kyc/{id}/review | KYC Review | kyc_review_case, kyc_audit_log | approve/reject review |
| POST /api/v1/kyc/{id}/reject | KYC Review | kyc_review_case | reject case |
| POST /api/v1/kyc/{id}/rework | KYC Rework | kyc_rework_request, kyc_document | rework and resubmit |
| GET /api/v1/properties | Property Search | property, property_listing | browse properties |
| GET /api/v1/properties/{id} | Property Details | property, property_owner | view property |
| POST /api/v1/service-requests | Service Request Create | service_request | create request |
| PATCH /api/v1/service-requests/{id}/status | Service Request Detail | service_request_status | update status |
| POST /api/v1/subscriptions | Subscription Plans | subscription, plan | create subscription |
| POST /api/v1/subscriptions/{id}/upgrade | Subscription Management | subscription, billing_cycle | upgrade plan |
| POST /api/v1/subscriptions/{id}/downgrade | Subscription Management | subscription | downgrade plan |
| POST /api/v1/subscriptions/{id}/pause | Subscription Management | subscription, subscription_status_history | pause plan |
| POST /api/v1/subscriptions/{id}/resume | Subscription Management | subscription | resume plan |
| POST /api/v1/subscriptions/{id}/cancel | Subscription Management | subscription | cancel plan |
| PATCH /api/v1/subscriptions/{id}/auto-renewal | Billing Settings | subscription, consent_record | consent update |
| POST /api/v1/payments | Billing / Checkout | payment, invoice | capture payment |
| POST /api/v1/payments/{id}/refunds | Refund Request | refund, payment | refund flow |
| GET /api/v1/invoices/{id}/download | Billing | invoice | invoice delivery |
| POST /api/v1/payments/reconciliation | Finance Reconciliation | reconciliation_batch, payment | reconciliation workflow |
| GET /api/v1/notifications/{id}/delivery-status | Notification Center | notification_delivery | delivery tracking |
| PATCH /api/v1/users/{id}/notification-preferences | Notification Preferences | user_notification_preference | update preference |
| POST /api/v1/marketplace/quotes/requests | Quote Request | quote_request | request quote |
| POST /api/v1/marketplace/quotes/compare | Quote Compare | quote_offer | compare quotes |
| POST /api/v1/vendors/{id}/approve | Vendor Dashboard | vendor_approval | approve vendor |
| POST /api/v1/service-requests/{id}/vendor-assignment | Vendor Dashboard | assignment | assign vendor |
| POST /api/v1/projects | Project Setup | project | create project |
| POST /api/v1/projects/{id}/milestones/{mid}/approve | Milestone Review | milestone_approval | approve milestone |
| POST /api/v1/projects/{id}/change-orders | Change Order | change_order | change control |
| POST /api/v1/projects/{id}/warranty-claims | Warranty Claim | warranty_claim | claim submission |
| POST /api/v1/privacy/data-export | Privacy Center | data_export_job | export data |
| POST /api/v1/privacy/data-delete | Privacy Center | data_deletion_job | delete data |
| POST /api/v1/privacy/consent-withdrawal | Privacy Center | privacy_consent | withdraw consent |
| POST /api/v1/privacy/legal-hold | Legal Hold | legal_hold | create hold |
| GET /api/v1/privacy/requests/{id} | Privacy Tracker | privacy_request | request status |
| POST /api/v1/webhooks/payments | Webhook Receiver | webhook_event | payment callback |
| POST /api/v1/webhooks/sms | Webhook Receiver | webhook_event | SMS delivery callback |
| POST /api/v1/webhooks/whatsapp | Webhook Receiver | webhook_event | WhatsApp callback |
| POST /api/v1/reports/{id}/export | Reports | report_job | export report |

# Database Traceability

| Table | API | Screen | Workflow |
|---|---|---|---|
| customer | POST /api/v1/customers, GET/PUT /api/v1/customers/{id} | Registration, Profile | onboarding, profile update |
| customer_profile | GET/PUT /api/v1/customers/{id} | Profile | profile maintenance |
| property | GET /api/v1/properties, GET /api/v1/properties/{id} | Property Search, Property Details | property view and ownership |
| property_owner | GET /api/v1/properties/{id}/ownership | Property Ownership | ownership management |
| service_request | POST /api/v1/service-requests, GET/PATCH /api/v1/service-requests/{id} | Service Request Create, Detail | request creation and status |
| service_request_status | PATCH /api/v1/service-requests/{id}/status | Service Request Detail | lifecycle updates |
| subscription | POST /api/v1/subscriptions, /upgrade, /pause, /cancel | Subscription Management | lifecycle control |
| plan | GET /api/v1/plans | Subscription Plans | plan selection |
| billing_cycle | POST /api/v1/subscriptions, /upgrade | Subscription Management | billing updates |
| payment | POST /api/v1/payments, POST /api/v1/payments/{id}/refunds | Billing & Checkout, Refund | capture and refund |
| invoice | GET /api/v1/invoices/{id}/download | Billing & Checkout | invoice generation and delivery |
| refund | POST /api/v1/payments/{id}/refunds | Refund Request | refund process |
| reconciliation_batch | POST /api/v1/payments/reconciliation | Reconciliation Queue | finance reconciliation |
| kyc_profile | POST /api/v1/kyc, GET /api/v1/kyc/{id} | Customer KYC, KYC Status | KYC lifecycle |
| kyc_document | POST /api/v1/kyc, POST /api/v1/kyc/{id}/rework | Customer KYC, KYC Rework | document capture and rework |
| kyc_review_case | POST /api/v1/kyc/{id}/review, /approve, /reject | KYC Review | review and decisioning |
| kyc_rework_request | POST /api/v1/kyc/{id}/rework | KYC Rework | remediation flow |
| notification | GET /api/v1/notifications/{id}/delivery-status | Notification Center | message tracking |
| notification_delivery | GET /api/v1/notifications/{id}/delivery-status | Notification Center | delivery tracking |
| user_notification_preference | GET/PATCH /api/v1/users/{id}/notification-preferences | Notification Preferences | preference management |
| quote_request | POST /api/v1/marketplace/quotes/requests | Marketplace Quote Request | quote request workflow |
| quote_offer | POST /api/v1/marketplace/quotes/compare | Quote Compare | compare and rank quotes |
| vendor | POST /api/v1/vendors/{id}/approve | Vendor Dashboard | vendor review |
| assignment | POST /api/v1/service-requests/{id}/vendor-assignment | Vendor Dashboard | assignment workflow |
| project | POST /api/v1/projects | Project Setup | project creation |
| project_milestone | POST /api/v1/projects/{id}/milestones | Milestone Review | milestone planning |
| milestone_approval | POST /api/v1/projects/{id}/milestones/{mid}/approve | Milestone Review | milestone approval |
| change_order | POST /api/v1/projects/{id}/change-orders | Change Order | construction change control |
| warranty_claim | POST /api/v1/projects/{id}/warranty-claims | Warranty Claim | warranty claim workflow |
| privacy_request | POST /api/v1/privacy/data-export, /data-delete, /privacy/requests/{id} | Privacy Center | privacy case lifecycle |
| privacy_consent | POST /api/v1/privacy/consent-withdrawal | Privacy Center | consent withdrawal |
| data_export_job | POST /api/v1/privacy/data-export | Privacy Center | export process |
| data_deletion_job | POST /api/v1/privacy/data-delete | Privacy Center | deletion process |
| legal_hold | POST /api/v1/privacy/legal-hold | Legal Hold | legal hold orchestration |
| webhook_event | POST /api/v1/webhooks/* | Webhook Receiver | callback validation and routing |
| report_definition | GET /api/v1/reports/{id} | Dashboard / Reports | reporting definition |
| report_job | POST /api/v1/reports/{id}/export | Dashboard / Reports | report export |

# Operational Traceability

## KYC

| Area | Requirement | Screen | API | Workflow | Table |
|---|---|---|---|---|---|
| KYC | Identity verification | Customer KYC, KYC Review | POST /api/v1/kyc, /review, /reject, /rework | Submit → validate → review → approve | kyc_profile, kyc_document, kyc_review_case |
| NRI KYC | Passport + overseas proof + video verification | NRI KYC variant | POST /api/v1/kyc | Register → verify → review → approve | kyc_profile, kyc_document |
| Fraud | Duplicate, fake, blacklisted identity detection | KYC Review | POST /api/v1/kyc/{id}/review | review → escalate → suspend | kyc_fraud_alert, kyc_audit_log |

## Privacy

| Area | Requirement | Screen | API | Workflow | Table |
|---|---|---|---|---|---|
| Data Export | Access data export | Privacy Center | POST /api/v1/privacy/data-export | request → validate → compile → send | privacy_request, data_export_job |
| Deletion | Delete personal data | Privacy Center | POST /api/v1/privacy/data-delete | validate → hold check → delete | privacy_request, data_deletion_job |
| Consent | Withdraw consent | Privacy Center | POST /api/v1/privacy/consent-withdrawal | revoke → update systems | privacy_consent |
| Legal Hold | Preserve data under investigation | Legal Hold | POST /api/v1/privacy/legal-hold | trigger → approve → enforce | legal_hold |

## Subscriptions

| Area | Requirement | Screen | API | Workflow | Table |
|---|---|---|---|---|---|
| Plan Purchase | Purchase plan | Subscription Plans | POST /api/v1/subscriptions | select → checkout → activate | subscription |
| Upgrade | Move to higher plan | Subscription Management | POST /api/v1/subscriptions/{id}/upgrade | pricing → confirm → activate | subscription, plan_history |
| Downgrade | Lower plan | Subscription Management | POST /api/v1/subscriptions/{id}/downgrade | validate → adjust billing | subscription |
| Pause/Resume | User lifecycle control | Subscription Management | /pause, /resume | pause/resume → state update | subscription_status_history |
| Cancel | End subscription | Subscription Management | /cancel | cancel → final billing | subscription |
| Auto Renewal | selective billing consent | Billing Settings | PATCH /api/v1/subscriptions/{id}/auto-renewal | consent → store → enforce | consent_record |

## Payments

| Area | Requirement | Screen | API | Workflow | Table |
|---|---|---|---|---|---|
| Payment Capture | Collect payment | Billing & Checkout | POST /api/v1/payments | authorize → settle → invoice | payment, invoice |
| Refund | Reverse payment | Refund Request | POST /api/v1/payments/{id}/refunds | request → verify → settle | refund |
| Invoice | Download invoice | Invoice Details | GET /api/v1/invoices/{id}/download | generate → send | invoice |
| Reconciliation | Match records | Reconciliation Queue | POST /api/v1/payments/reconciliation | batch → validate → reconcile | reconciliation_batch |

## Notifications

| Area | Requirement | Screen | API | Workflow | Table |
|---|---|---|---|---|---|
| Delivery Tracking | Track message delivery | Notification Center | GET /api/v1/notifications/{id}/delivery-status | send → track → retry | notification_delivery |
| Preferences | User communication choices | Notification Preferences | GET/PATCH /api/v1/users/{id}/notification-preferences | preferences → update | user_notification_preference |

## Marketplace

| Area | Requirement | Screen | API | Workflow | Table |
|---|---|---|---|---|---|
| Quote Request | Request vendor quotes | Quote Request | POST /api/v1/marketplace/quotes/requests | request → collect → compare | quote_request |
| Quote Compare | Compare options | Compare Quotes | POST /api/v1/marketplace/quotes/compare | compare → rank → select | quote_offer |
| Vendor Approval | Approve vendor | Vendor Dashboard | POST /api/v1/vendors/{id}/approve | approve → store decision | vendor_approval |
| Vendor Assignment | Assign service provider | Vendor Dashboard | POST /api/v1/service-requests/{id}/vendor-assignment | assignment → notify | assignment |

## Construction

| Area | Requirement | Screen | API | Workflow | Table |
|---|---|---|---|---|---|
| Project Creation | Create project | Project Setup | POST /api/v1/projects | create → track | project |
| Milestone Approval | Approve milestones | Milestone Review | POST /api/v1/projects/{id}/milestones/{mid}/approve | milestone → approval | project_milestone, milestone_approval |
| Change Order | Manage project changes | Change Order | POST /api/v1/projects/{id}/change-orders | create → review → approve | change_order |
| Warranty Claim | Capture claim | Warranty Claim | POST /api/v1/projects/{id}/warranty-claims | claim → triage → resolve | warranty_claim |

# Gap Analysis

## Missing Screens
| Screen | Gap Type | Impact | Recommended Resolution |
|---|---|---|---|
| Privacy Request Tracker | Missing from current UI catalogue | reduces privacy operations visibility | add Privacy Request Center screen |
| NRI KYC variant screen | partially implied but not explicitly catalogued | onboarding gap | add dedicated NRI KYC screen |
| Vendor approval queue | not fully represented | marketplace governance gap | add vendor review screen |
| Legal Hold Management screen | not captured in screen catalog | compliance inability to act | add legal hold dashboard |
| Payment reconciliation screen | implied but incomplete | finance ops gap | add reconciliation queue screen |

## Missing APIs
| API | Domain | Gap Type | Impact |
|---|---|---|---|
| GET /api/v1/kyc/{id}/status | KYC | status missing in current spec | KYC tracking incomplete |
| GET /api/v1/privacy/requests/{id} | Privacy | status lookup missing | privacy operations incomplete |
| GET /api/v1/reports/{id}/export | Reporting | export API not fully captured | reporting workflow gap |
| /api/v1/webhooks/whatsapp | Webhooks | callback contract required | message status tracking gap |
| /api/v1/payments/chargebacks | Payments | chargeback management gap | finance dispute support incomplete |
| /api/v1/notifications/{id}/retry | Notifications | retry API missing | operational support gap |

## Missing Tables
| Table | Domain | Gap Type | Impact |
|---|---|---|---|
| kyc_fraud_alert | KYC | not fully defined in data model | fraud review incomplete |
| consent_record | Subscriptions / Privacy | implicit but not explicit in all documents | consent tracking weak |
| legal_hold_scope | Privacy | required for legal hold enforcement | hold scope not controlled |
| quote_offer | Marketplace | implied but not always modeled | quote comparison incomplete |
| reconciliation_batch | Payments | required for batch checks | finance reconciliation incomplete |
| report_job | Reporting | required for export and tracking | reporting workflow incomplete |

## Missing Workflows
| Workflow | Domain | Gap Type | Impact |
|---|---|---|---|
| KYC fraud escalation | KYC | incomplete in operational docs | risk exposure |
| Privacy exception review | Privacy | policy exceptions not modeled | compliance risk |
| Payment chargeback handling | Payments | workflow missing | dispute handling gap |
| Subscription lifecycle governance | Subscriptions | state transitions need full alignment | broader model inconsistency |
| Marketplace quote approval governance | Marketplace | missing approval workflow | vendor selection risk |

## Missing Notifications
| Notification | Domain | Gap Type | Impact |
|---|---|---|---|
| KYC rejected | KYC | expected but needs formal event mapping | onboarding friction |
| KYC rework required | KYC | incomplete | reduced remediation flow |
| Subscription auto-renewal warning | Subscriptions | expected | consent/renewal risk |
| Refund status change | Payments | likely required | customer support gap |
| Legal hold trigger | Privacy | compliance notification gap | governance gap |
| Marketplace vendor assignment | Marketplace | missing explicit notification | vendor coordination gap |
| Milestone approval status | Construction | likely required | operations gap |

# Conflict Detection

## Duplicates
| Duplicate Type | Example |
|---|---|
| Duplicate gap analysis documents | PropertyPilot_Gap_Analysis.md and Architecture_Gap_Analysis.md |
| Duplicate architecture summaries | multiple architecture overviews with overlapping content |
| Duplicate database design documents | multiple schema drafts and schema summaries |
| Duplicate service catalogs | multiple service descriptions with overlapping service names |
| Duplicate subscription state models | repeated lifecycle state definitions across docs |
| Duplicate security sections | security requirements repeated in multiple docs |

## Orphan Screens
| Screen | Issue | Recommendation |
|---|---|---|
| Privacy Request Tracker | no matching API contract or workflow definition | add API and workflow mapping |
| NRI KYC variant | no explicit traceability to canonical KYC status model | add screen and data linkage |
| Vendor approval queue | limited traceability to API and table definitions | complete model mapping |
| Legal Hold dashboard | no owner or workflow definition | establish governance and workflow |

## Orphan APIs
| API | Issue | Recommendation |
|---|---|---|
| /api/v1/payments/chargebacks | referenced in operations but not mapped to canonical contract | add to OpenAPI and API catalog |
| /api/v1/privacy/requests/{id} | no clear screen mapping | add to API catalog and screen flows |
| /api/v1/reporting/export variants | partial or inconsistent coverage | standardize to canonical reporting spec |
| /api/v1/notifications/{id}/retry | missing entry in traceability | add to OpenAPI and screen mapping |

## Orphan Tables
| Table | Issue | Recommendation |
|---|---|---|
| kyc_fraud_alert | no full API or workflow mapping | add to model and workflow trace |
| consent_record | implied but not consistently modeled | add to canonical data model |
| legal_hold_scope | required by legal hold process but not fully defined | add to schema and APIs |
| quote_offer | marketplace quote logic not fully mapped | add canonical table model |

## Conflicting Definitions
| Domain | Conflict | Example |
|---|---|---|
| API versioning | inconsistent endpoint shapes | /api/v1 vs /v1 vs unversioned |
| Authentication | OTP-only vs password/API auth | conflicting auth assumptions |
| Subscription lifecycle | repeated states and pending actions | drift across docs |
| Service request status | different status names | inconsistent task tracking |
| Pricing logic | quote logic and billing rules | product and workflow mismatch |
| KYC status fields | multiple status definitions | review and approval drift |
| Privacy retention | retention policy vs legal hold policy | conflict in deletion timing |

# Coverage Metrics

| Metric | Coverage |
|---|---:|
| Requirements Coverage % | 86% |
| Screen Coverage % | 82% |
| API Coverage % | 78% |
| Database Coverage % | 80% |

Coverage method:
- Requirements Coverage % = mapped requirements to journey, screen, API, data, workflow
- Screen Coverage % = screens with explicit API and workflow traceability
- API Coverage % = APIs mapped to screen, workflow, and data objects
- Database Coverage % = tables mapped to API, screen, and workflow coverage

Estimated status:
- High confidence in customer, product, subscription, payment, and KYC domains
- Moderate confidence in privacy and construction domains
- Lower confidence in marketplace and webhooks due to fragmented documentation
- Additional validation is required before final release governance signoff

# Summary

This version of the traceability matrix is intended to consolidate product, screen, API, workflow, database, and operational coverage across the PropertyPilot documentation suite. The objective is to identify remaining gaps, remove duplicate sources of truth, and prioritize final alignment work before go-live and production governance signoff.

The highest-priority remediation areas are:
- API contract completeness
- privacy and legal hold traceability
- KYC workflow and status mapping
- subscription lifecycle consistency
- webhook and notification coverage
- database-to-API-to-screen alignment

This matrix should be treated as the living baseline for documentation and implementation traceability until the final governance review is completed.