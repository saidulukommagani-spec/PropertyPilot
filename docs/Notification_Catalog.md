# PropertyPilot Notification Catalog

## Version

1.0

## Purpose

This catalog defines the notifications PropertyPilot sends to customers, agents, vendors, administrators, and NRI customers. Each notification maps a business trigger to approved channels, recipient role, template, and priority.

[Notification_Strategy.md](Notification_Strategy.md) governs delivery, preferences, queueing, throttling, retries, and tracking. [Cross_Cutting_Requirements.md](Cross_Cutting_Requirements.md) governs consent, transactional exceptions, and conflict resolution.

---

# Channel and Priority Standards

## Channels

- **SMS** - Time-sensitive transactional or critical notices when a verified mobile number is available.
- **Email** - Detailed, record-bearing, invoice, report, and administrative notices.
- **WhatsApp** - Approved template-based transactional updates for opted-in recipients.
- **Push** - Near-real-time mobile-app updates for opted-in, active devices.
- **In-App** - Persistent notification-centre entry; used alongside applicable external channels.

## Priorities

| Priority | Use |
|---|---|
| `LOW` | Informational updates and non-urgent reminders. |
| `MEDIUM` | Routine transactional updates requiring awareness. |
| `HIGH` | Time-sensitive action, SLA, payment, or service-impacting updates. |
| `CRITICAL` | Security, fraud, emergency-property, or material operational-risk alerts. |

---

# Customer Notifications

| Trigger | Channel | Recipient | Template Name | Priority |
|---|---|---|---|---|
| `CustomerRegistered` | SMS, Email, WhatsApp, Push, In-App | Registered customer | `customer_registration_welcome` | `MEDIUM` |
| OTP generated for login, registration, recovery, or KYC | SMS, WhatsApp | Customer | `customer_otp_verification` | `HIGH` |
| `CustomerKycVerified` with approved, rejected, expired, or rework outcome | Email, Push, In-App | Customer | `customer_kyc_status` | `HIGH` |
| `ServiceRequested` | Push, In-App, Email | Requesting customer | `customer_service_request_created` | `MEDIUM` |
| `PaymentCompleted` for service or subscription | Email, Push, In-App | Paying customer | `customer_payment_completed` | `HIGH` |
| `PaymentFailed` | Push, SMS, In-App | Paying customer | `customer_payment_failed` | `HIGH` |
| `ServiceAssigned` | Push, In-App | Requesting customer | `customer_service_assigned` | `MEDIUM` |
| `VisitStarted` | Push, In-App | Requesting customer | `customer_visit_started` | `MEDIUM` |
| Service delay, missed schedule, or SLA-impacting reschedule | SMS, Push, In-App | Affected customer | `customer_service_delay` | `HIGH` |
| `ReportGenerated` with approved/delivered report | Email, Push, In-App | Authorised customer/property owner | `customer_report_ready` | `MEDIUM` |
| Service lifecycle reaches `COMPLETED` | Email, Push, In-App | Requesting customer | `customer_service_completed` | `MEDIUM` |
| Service feedback becomes eligible | Push, In-App | Requesting customer | `customer_feedback_request` | `LOW` |
| `ServiceCancelled` or authorised cancellation outcome | Email, Push, In-App | Requesting customer | `customer_service_cancellation` | `HIGH` |
| `RefundProcessed` | Email, Push, In-App | Paying customer | `customer_refund_processed` | `HIGH` |
| `SubscriptionActivated` | Email, WhatsApp, Push, In-App | Subscriber | `customer_subscription_activated` | `HIGH` |
| Subscription renewal due, successful renewal, failure, pause, resume, expiry, or suspension | Email, SMS, WhatsApp, Push, In-App | Subscriber | `customer_subscription_lifecycle` | `HIGH` |
| `ComplaintCreated` or complaint status change | Email, Push, In-App | Complaint creator | `customer_complaint_status` | `MEDIUM` |
| `MarketplaceInquiryCreated` or authorised contact-disclosure outcome | Email, Push, In-App | Authorised marketplace participant | `customer_marketplace_interaction` | `MEDIUM` |

# Agent Notifications

| Trigger | Channel | Recipient | Template Name | Priority |
|---|---|---|---|---|
| `ServiceAssigned` to an agent | Push, SMS, In-App | Assigned agent | `agent_new_assignment` | `HIGH` |
| Assignment acceptance deadline approaching | Push, SMS, In-App | Assigned agent | `agent_assignment_reminder` | `HIGH` |
| Assignment is cancelled, reassigned, or escalated | Push, SMS, In-App | Affected agent | `agent_assignment_change` | `HIGH` |
| Emergency visit assignment | Push, SMS, In-App | Eligible or assigned agent | `agent_emergency_assignment` | `CRITICAL` |
| Visit start, evidence, or report submission is overdue | Push, SMS, In-App | Assigned agent | `agent_visit_or_report_reminder` | `HIGH` |
| Report or evidence is rejected and rework is required | Push, In-App | Submitting agent | `agent_report_rework_required` | `HIGH` |
| Agent payout is processed or held | Email, Push, In-App | Affected agent | `agent_payout_status` | `MEDIUM` |
| Agent performance, compliance, or training action is required | Email, Push, In-App | Affected agent | `agent_compliance_action` | `MEDIUM` |

# Vendor Notifications

| Trigger | Channel | Recipient | Template Name | Priority |
|---|---|---|---|---|
| `VendorRegistered` acknowledgement | Email, WhatsApp, In-App | Registering vendor | `vendor_registration_received` | `MEDIUM` |
| `VendorApproved` with approved, restricted, suspended, rejected, or rework outcome | Email, SMS, Push, In-App | Vendor administrator | `vendor_verification_status` | `HIGH` |
| Quotation request is available | Email, WhatsApp, Push, In-App | Eligible vendor | `vendor_quotation_request` | `HIGH` |
| Quotation selected, rejected, expired, or requires revision | Email, Push, In-App | Quoting vendor | `vendor_quotation_status` | `HIGH` |
| `VendorAssigned` | Email, SMS, Push, In-App | Assigned vendor | `vendor_job_assigned` | `HIGH` |
| Vendor job schedule, scope, SLA, or milestone changes | Email, WhatsApp, Push, In-App | Assigned vendor | `vendor_job_change` | `HIGH` |
| Vendor completion evidence requires review or rework | Email, Push, In-App | Assigned vendor | `vendor_job_review_status` | `HIGH` |
| Vendor invoice, payment, commission, or settlement status changes | Email, Push, In-App | Vendor finance contact | `vendor_financial_status` | `MEDIUM` |

# Admin Notifications

| Trigger | Channel | Recipient | Template Name | Priority |
|---|---|---|---|---|
| Customer KYC, vendor verification, or privileged approval queue requires action | Email, Push, In-App | Authorised administrator | `admin_approval_action_required` | `HIGH` |
| SLA warning, breach, or assignment failure | Email, SMS, Push, In-App | Operations or service administrator | `admin_sla_or_assignment_alert` | `HIGH` |
| High or critical complaint, fraud report, or dispute escalation | Email, SMS, Push, In-App | Authorised operations/security administrator | `admin_complaint_escalation` | `CRITICAL` |
| Payment reconciliation exception, chargeback, refund exception, or payout failure | Email, Push, In-App | Finance administrator | `admin_financial_exception` | `HIGH` |
| Pricing override, high-discount, or controlled configuration approval is required | Email, Push, In-App | Authorised administrator | `admin_configuration_approval` | `HIGH` |
| `SecurityViolationDetected` | Email, SMS, Push, In-App | Security administrator | `admin_security_incident` | `CRITICAL` |
| Event dead-letter, integration failure, or provider outage threshold is reached | Email, Push, In-App | Platform administrator | `admin_integration_failure` | `HIGH` |
| Daily operational, revenue, compliance, or capacity summary is ready | Email, In-App | Authorised administrator | `admin_daily_summary` | `LOW` |

# NRI Notifications

| Trigger | Channel | Recipient | Template Name | Priority |
|---|---|---|---|---|
| NRI registration or KYC outcome | Email, WhatsApp, Push, In-App | NRI customer | `nri_onboarding_status` | `HIGH` |
| NRI subscription activation, renewal, payment failure, expiry, or suspension | Email, SMS, WhatsApp, Push, In-App | NRI subscriber | `nri_subscription_lifecycle` | `HIGH` |
| Scheduled monitoring visit completed | WhatsApp, Push, In-App | NRI property owner | `nri_monitoring_visit_completed` | `MEDIUM` |
| `ReportGenerated` for a monitoring, video, or walkthrough report | Email, WhatsApp, Push, In-App | NRI property owner | `nri_report_ready` | `MEDIUM` |
| `MonitoringAlertCreated` | SMS, Email, WhatsApp, Push, In-App | NRI property owner and authorised relationship manager | `nri_property_alert` | `CRITICAL` |
| Emergency visit requested, assigned, started, completed, or requires approval | SMS, Email, WhatsApp, Push, In-App | NRI property owner and authorised relationship manager | `nri_emergency_response` | `CRITICAL` |
| Relationship-manager message, service-coordination update, or resolution confirmation | Email, WhatsApp, Push, In-App | NRI customer | `nri_relationship_manager_update` | `MEDIUM` |

---

# Delivery and Template Rules

1. Every notification shall originate from a documented business event or approved system trigger and shall include the triggering event/correlation identifier.
2. Templates shall be versioned, locale-aware, channel-approved, and free of OTP values, payment credentials, unmasked contact details, and signed media URLs.
3. Recipient preferences, consent, quiet hours, verified channel availability, and transactional/security exceptions shall be evaluated before delivery.
4. `CRITICAL` and `HIGH` notifications are prioritised in the delivery queue and follow escalation policy when delivery fails or remains unacknowledged where acknowledgement is required.
5. Delivery tracking shall retain `sent`, `delivered`, `read`, `failed`, and `expired` status with provider reference and failure reason.
6. Duplicate notifications for the same event, recipient, channel, and template version shall be suppressed unless an approved reminder or escalation rule requires another delivery.

# Related Documents

- Notification_Strategy.md
- Event_Catalog.md
- Cross_Cutting_Requirements.md
- Subscription_Management.md
- Vendor_Management.md
- Complaint_Dispute_Management.md
