# PropertyPilot Service Level Agreement

## Version

1.0

## Status

MVP baseline

## Purpose

This document defines the operational clocks, ownership, escalation, and reporting rules used by service requests and recurring services. Module-specific SLAs may be stricter, but shall not weaken these rules without an approved configuration change.

## Clock Rules

- All timestamps are stored in UTC and displayed in the property's configured IANA time zone.
- MVP clocks use calendar time, including weekends and public holidays.
- A clock starts when the triggering event is persisted, not when a notification is sent.
- A clock pauses only in `PENDING_CUSTOMER`, `PENDING_PAYMENT`, or `ON_HOLD` with a recorded reason.
- A clock resumes when the blocking event is persisted.
- Terminal states stop all active clocks: `COMPLETED`, `CANCELLED`, and `FAILED`.
- Reassignment, rescheduling, and pause/resume events retain the original clock history.

## MVP Targets

| Clock | Start event | Target | Warning | Owner |
|---|---|---:|---:|---|
| Coverage validation | Request submitted | 15 minutes | 10 minutes | System |
| Payment confirmation | Payment initiated | 10 minutes | 5 minutes | Payments |
| Assignment | Payment completed | 4 hours | 2 hours | Operations |
| Agent acceptance | Assignment offered | 2 hours | 1 hour | Agent |
| Service start | Scheduled visit time | 30 minutes after appointment | At appointment time | Agent |
| Report submission | Visit completed | 24 hours | 18 hours | Agent |
| Quality review | Report submitted | 2 business days | 1 business day | Quality team |
| Customer response | Customer review requested | 3 calendar days | 1 day | Customer |
| Support first response | Complaint created | 4 hours | 2 hours | Support |

Targets are configuration data with effective dates. The service request stores the applied SLA version so later configuration changes cannot alter historical compliance.

## Breach and Escalation

At warning, notify the current owner and operations dashboard. At breach:

1. Record an immutable `sla_breached` event with clock, target, actual time, owner, and reason.
2. Notify the owner, cluster manager, and operations team.
3. Create or update an escalation case; duplicate alerts for the same clock are suppressed.
4. Recalculate the next escalation at 2x the target unless an administrator sets a shorter deadline.

Operations owns unresolved breaches. A breach may be closed only with a terminal outcome and a reason code such as `CUSTOMER_DELAY`, `AGENT_DELAY`, `SYSTEM_FAILURE`, `COVERAGE_LIMITATION`, or `EXTERNAL_DEPENDENCY`.

## Rescheduling and Recovery

- A customer may request rescheduling before agent arrival; the system records the old and new appointment and recalculates only appointment-dependent clocks.
- An agent may not silently reschedule. The request requires a reason and customer notification.
- Failed notifications do not stop an SLA clock.
- Provider outages, payment retries, and upload retries are recorded as dependency events; they do not erase elapsed time.
- Repeated breaches are counted per service request, agent, cluster, and SLA type for reporting and corrective action.

## Reporting

The platform shall report total, met, warned, breached, paused, and excluded clocks by service, cluster, agent, period, and reason. Reports shall include percentile duration (P50/P95), breach rate, time to recovery, and unresolved breach count.

## Audit Requirements

Every start, pause, resume, warning, breach, escalation, override, and closure shall record the actor or system source, timestamp, previous state, new state, reason, and applied SLA version.
