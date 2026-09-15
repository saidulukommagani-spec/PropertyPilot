```markdown
<!--
PSEUDOCODE PLAN:
1. Identify the source inputs: SRS, MVP scope, schema, canonical data dictionary, QA strategy, existing UAT plan.
2. Build a UAT test plan that maps each user journey to scenario IDs, preconditions, steps, expected results, and pass/fail criteria.
3. Create a test data strategy that organizes datasets by domain, environment, privacy, masking, generation, and refresh lifecycle.
4. Keep the structure aligned to the project’s personas: customer, agent, operations, admin.
5. Include acceptance criteria, defect handling, security, performance, entry/exit gates, and reporting expectations.
6. Write both as production-grade markdown docs and place them in the docs folder.
7. Ensure the final output is a single markdown block for easy copy into docs files.
-->

# File: docs/test_UAT_Test_Plan.md

# PropertyPilot MVP UAT Test Plan

Document Type: UAT Test Cases  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: Product / QA / Business Validation  
Product: PropertyPilot MVP

---

# 1. Purpose

This document defines executable UAT scenarios for PropertyPilot MVP. It validates that the product satisfies approved business requirements, core customer journeys, role-based access, API-backed actions, and operational workflows in the UAT environment.

This plan is intended to be used by business users, QA, product, and technical stakeholders for test execution, defect capture, retest, and final business sign-off.

---

# 2. Scope

## In Scope
- Customer registration and login
- OTP verification
- Property management
- Service catalog, booking, and tracking
- Agent task management
- GPS capture and evidence upload
- Payment, subscription, and invoice actions
- Notification flows
- Complaint handling
- Operations queue and assignment
- Reports and analytics
- Admin user, role, pricing, and config management

## Out of Scope
- Future roadmap features
- Non-MVP custom workflows
- Full-scale production-scale performance certification

---

# 3. Test Approach

- Execute scenarios in UAT environment using production-like data
- Validate end-to-end business behavior against acceptance criteria
- Record evidence for each scenario
- Capture defects with severity and priority
- Retest all fixes before sign-off
- Use role-based accounts for each persona

---

# 4. Personas

## Customer
- Create account
- Login and confirm OTP
- Add property
- Book service
- Track service
- Pay and manage subscription
- Receive notifications
- Submit complaint

## Agent
- Login
- View assigned tasks
- Capture GPS
- Upload evidence and photos
- Complete visit

## Operations User
- View queue
- Assign and reassign tasks
- Review escalations
- Review reports

## Admin
- Manage users and roles
- Manage pricing
- Manage subscriptions
- Review analytics
- Manage system configuration

---

# 5. Test Execution Requirements

## Environment
- UAT environment online and stable
- All services available
- Test users and roles created
- Data seeded for valid and negative scenarios
- API endpoints reachable
- Monitoring and logs active

## Test Data
- Baseline customer accounts
- Agents with assignment states
- Properties with valid and invalid verification states
- Active and inactive services
- Payment and subscription scenarios
- Complaint records
- Reports with data sets

---

# 6. Entry Criteria

UAT execution may start when:
- QA and release candidate testing are complete
- critical defects are closed or accepted
- UAT environment is available and stable
- users and roles are provisioned
- seed data is loaded
- release notes and known issues are approved
- business testers are trained on scenarios

---

# 7. Exit Criteria

UAT is complete when:
- all planned scenarios are executed
- all high-priority and critical defects are closed or accepted
- no unresolved Sev 1 or P1 defects remain
- all required business journeys pass
- business approval is recorded
- final UAT report is signed off

---

# 8. Test Case Matrix

| ID | Area | Persona | Scenario | Priority | Status |
|---|---|---|---|---|---|
| UAT-01 | Auth | Customer | Register new user | P1 | Pending |
| UAT-02 | Auth | Customer | Login with valid OTP | P1 | Pending |
| UAT-03 | Auth | Customer | Login with invalid OTP | P1 | Pending |
| UAT-04 | Auth | Agent | Agent login | P1 | Pending |
| UAT-05 | Customer | Customer | Dashboard loads | P2 | Pending |
| UAT-06 | Property | Customer | Add valid property | P1 | Pending |
| UAT-07 | Property | Customer | Add invalid property | P1 | Pending |
| UAT-08 | Property | Customer | View property details | P2 | Pending |
| UAT-09 | Property | Customer | Ownership verification | P1 | Pending |
| UAT-10 | Service | Customer | Browse service catalog | P1 | Pending |
| UAT-11 | Service | Customer | Book a valid service | P1 | Pending |
| UAT-12 | Service | Customer | Book invalid service date | P1 | Pending |
| UAT-13 | Service | Customer | View tracking lifecycle | P1 | Pending |
| UAT-14 | Service | Agent | View assigned tasks | P1 | Pending |
| UAT-15 | Service | Agent | Start service task | P1 | Pending |
| UAT-16 | Service | Agent | Capture GPS | P1 | Pending |
| UAT-17 | Service | Agent | Upload evidence | P1 | Pending |
| UAT-18 | Service | Agent | Complete task | P1 | Pending |
| UAT-19 | Payment | Customer | Successful payment | P1 | Pending |
| UAT-20 | Payment | Customer | Failed payment | P1 | Pending |
| UAT-21 | Subscription | Customer | View subscription | P1 | Pending |
| UAT-22 | Subscription | Customer | Pause or cancel subscription | P1 | Pending |
| UAT-23 | Notification | Customer | Receive booking alert | P2 | Pending |
| UAT-24 | Notification | Customer | Receive payment alert | P2 | Pending |
| UAT-25 | Complaint | Customer | Submit complaint | P1 | Pending |
| UAT-26 | Complaint | Operations | Review complaint | P1 | Pending |
| UAT-27 | Operations | Operations | Review queue | P1 | Pending |
| UAT-28 | Operations | Operations | Assign task | P1 | Pending |
| UAT-29 | Operations | Operations | Reassign task | P1 | Pending |
| UAT-30 | Operations | Operations | Escalate request | P1 | Pending |
| UAT-31 | Reporting | Operations | View operational report | P2 | Pending |
| UAT-32 | Reporting | Admin | View analytics dashboard | P2 | Pending |
| UAT-33 | Admin | Admin | User management | P1 | Pending |
| UAT-34 | Admin | Admin | Role management | P1 | Pending |
| UAT-35 | Admin | Admin | Pricing management | P1 | Pending |
| UAT-36 | Admin | Admin | System config changes | P1 | Pending |
| UAT-37 | Admin | Admin | Audit log review | P1 | Pending |

---

# 9. UAT Test Cases

## UAT-01: Customer registration
Purpose:
Verify the customer can create a valid account.

Preconditions:
- No existing customer account for the tested mobile number
- UAT environment available

Steps:
1. Launch customer app
2. Select Register
3. Enter valid first name, last name, email, mobile number
4. Accept required consent
5. Tap Submit
6. Receive OTP
7. Enter OTP and verify

Expected Result:
- Registration succeeds
- OTP is generated and delivered
- OTP validation succeeds
- User is redirected to dashboard or onboarding flow
- Validation errors are shown for invalid inputs

Pass Criteria:
- customer account is created and authenticated

---

## UAT-02: Login with valid OTP
Purpose:
Confirm secure OTP flow for login.

Preconditions:
- Customer account exists

Steps:
1. Open login screen
2. Enter valid phone number
3. Request OTP
4. Enter correct OTP
5. Continue

Expected Result:
- OTP accepted
- Session created
- User lands on dashboard

Pass Criteria:
- login succeeds and session is active

---

## UAT-03: Login with invalid OTP
Purpose:
Verify invalid OTP is blocked.

Steps:
1. Request OTP
2. Enter incorrect OTP
3. Submit

Expected Result:
- Error message appears
- User remains on OTP screen
- Retry flow is available
- No session created

Pass Criteria:
- invalid OTP is rejected

---

## UAT-04: Agent login
Purpose:
Verify agent authentication flow.

Steps:
1. Open agent app
2. Login with valid credentials
3. Enter OTP if required
4. Access dashboard

Expected Result:
- agent session is authenticated
- role-specific dashboard displays tasks

Pass Criteria:
- agent login works and access is role-specific

---

## UAT-05: Customer dashboard loads
Purpose:
Validate the dashboard shows user-specific content.

Steps:
1. Login as customer
2. View dashboard

Expected Result:
- customer name and account summary display
- property count, requests, and notifications appear
- unauthorized content is not displayed

Pass Criteria:
- dashboard loads accurately for the user

---

## UAT-06: Add valid property
Purpose:
Verify customer can add a property.

Steps:
1. Login as customer
2. Navigate to Property List
3. Tap Add Property
4. Complete required property details and address
5. Save

Expected Result:
- property is created
- property appears in list
- status is set correctly

Pass Criteria:
- valid property saved successfully

---

## UAT-07: Add invalid property
Purpose:
Ensure invalid property data is blocked.

Steps:
1. Attempt to create property with missing address or invalid fields
2. Submit

Expected Result:
- form validation errors shown
- property is not created
- user remains on form

Pass Criteria:
- invalid data rejected

---

## UAT-08: View property details
Purpose:
Confirm property details page is accurate.

Steps:
1. Open property list
2. Select a property
3. Review details

Expected Result:
- correct address, metadata, and verification status displayed
- actions available based on state

Pass Criteria:
- property details are accurate and complete

---

## UAT-09: Ownership verification
Purpose:
Validate ownership verification flow.

Steps:
1. Create a property
2. Submit ownership verification
3. Review result

Expected Result:
- verification result shown
- valid ownership passes
- invalid ownership fails with guidance

Pass Criteria:
- ownership verification matches expected business rule

---

## UAT-10: Browse service catalog
Purpose:
Validate catalog and select service.

Steps:
1. Login as customer
2. Open service catalog
3. Filter or browse available services
4. Select a service

Expected Result:
- available services display
- category or filter works
- service details are visible

Pass Criteria:
- selection is usable and accurate

---

## UAT-11: Book valid service
Purpose:
Verify service booking lifecycle begins correctly.

Steps:
1. Select a service
2. Select property
3. Choose desired date/time
4. Submit booking
5. Review confirmation

Expected Result:
- service request created successfully
- confirmation screen shown
- request appears in tracking

Pass Criteria:
- booking is created with valid state

---

## UAT-12: Book invalid service date
Purpose:
Validate date/time rules.

Steps:
1. Attempt booking for invalid date or past date
2. Submit

Expected Result:
- validation error displayed
- booking not created

Pass Criteria:
- invalid date rejected

---

## UAT-13: View tracking lifecycle
Purpose:
Verify customer sees request status.

Steps:
1. Create booking
2. Open tracking page

Expected Result:
- request status visible
- timeline or history available
- state reflects current lifecycle

Pass Criteria:
- customer can view the status of their request

---

## UAT-14: Agent views assigned tasks
Purpose:
Validate assignment visibility.

Steps:
1. Login as assigned agent
2. Open task list

Expected Result:
- only assigned tasks are visible
- task details accessible
- status displayed correctly

Pass Criteria:
- agent sees only valid tasks

---

## UAT-15: Start service task
Purpose:
Verify agent can begin work.

Steps:
1. Open assigned task
2. Tap Start / Begin work

Expected Result:
- task status updates to in progress
- agent can continue task workflow

Pass Criteria:
- status change valid and visible

---

## UAT-16: Capture GPS
Purpose:
Validate GPS capture and validation behavior.

Steps:
1. Open GPS capture screen
2. Allow location access
3. Capture current location
4. Submit

Expected Result:
- coordinates captured
- valid coordinate flow accepted
- invalid coordinate flow rejected with clear message

Pass Criteria:
- GPS validation works according to policy

---

## UAT-17: Upload evidence
Purpose:
Validate file upload process.

Steps:
1. Open task detail
2. Select evidence upload
3. Add valid file
4. Submit

Expected Result:
- file is uploaded
- metadata stored
- evidence is associated with request

Pass Criteria:
- evidence successfully uploaded and visible

---

## UAT-18: Complete task
Purpose:
Verify work completion flow.

Steps:
1. Open task
2. Add required evidence or notes
3. Complete visit
4. Save

Expected Result:
- task marked complete
- customer and operations see updated status
- completion is recorded correctly

Pass Criteria:
- task completion is valid and visible

---

## UAT-19: Successful payment
Purpose:
Validate successful purchase flow.

Steps:
1. Start a payable action
2. Enter valid payment details
3. Confirm payment

Expected Result:
- payment processed successfully
- receipt or confirmation shown
- status updated

Pass Criteria:
- payment succeeds and is recorded

---

## UAT-20: Failed payment
Purpose:
Validate negative payment flow.

Steps:
1. Attempt payment with invalid or declined method
2. Submit

Expected Result:
- payment fails with clear message
- no duplicate processing
- user can retry or cancel

Pass Criteria:
- failed payment handled gracefully

---

## UAT-21: View subscriptions
Purpose:
Verify subscription status is visible.

Steps:
1. Login as customer
2. Open subscription page

Expected Result:
- current plan and status display correctly
- active/inactive states are visible

Pass Criteria:
- subscription details are accurate

---

## UAT-22: Pause or cancel subscription
Purpose:
Validate subscription lifecycle actions.

Steps:
1. Open subscription
2. Trigger pause or cancel action
3. Confirm

Expected Result:
- state change applied as business rules allow
- confirmation shown
- audit trail updated

Pass Criteria:
- valid lifecycle actions succeed

---

## UAT-23: Receive booking alert
Purpose:
Validate notification flow for booking lifecycle.

Steps:
1. Book a service
2. Wait for notification or trigger manually

Expected Result:
- notification reaches customer
- content matches event
- notification appears in notification center

Pass Criteria:
- correct alert is delivered

---

## UAT-24: Receive payment alert
Purpose:
Validate payment status notification.

Steps:
1. Complete or fail a payment
2. Review notifications

Expected Result:
- matching notification appears
- status message is accurate

Pass Criteria:
- user receives right payment status update

---

## UAT-25: Submit complaint
Purpose:
Verify complaint creation flow.

Steps:
1. Login as customer
2. Navigate to complaints
3. Create complaint with valid details
4. Submit

Expected Result:
- complaint created
- acknowledgment or status visible
- complaint appears to support team

Pass Criteria:
- complaint submission succeeds

---

## UAT-26: Review complaint
Purpose:
Validate operations review of customer complaints.

Steps:
1. Login as operations user
2. Open complaint queue
3. Review complaint details
4. Update status

Expected Result:
- complaint details accessible
- status can be changed according to workflow
- audit or trace recorded

Pass Criteria:
- complaint workflow is usable for support teams

---

## UAT-27: Review queue
Purpose:
Test operations queue and queue state.

Steps:
1. Login as operations user
2. Open operations dashboard
3. Review queue

Expected Result:
- active tasks visible
- filters and sorting work
- queue state is accurate

Pass Criteria:
- operations dashboard supports actual queue review

---

## UAT-28: Assign task
Purpose:
Verify task allocation.

Steps:
1. Open queue
2. Select a service request
3. Assign to available agent
4. Save

Expected Result:
- assignment succeeds
- task list updates for the assigned agent
- audit event recorded

Pass Criteria:
- valid assignment works and remains visible

---

## UAT-29: Reassign task
Purpose:
Validate reassignment workflow.

Steps:
1. Reassign an assigned task to another agent
2. Save

Expected Result:
- reassignment recorded correctly
- agent receives updated task
- customer/ops see updated action

Pass Criteria:
- reassignment follows policy and updates all surfaces

---

## UAT-30: Escalate request
Purpose:
Validate escalation logic.

Steps:
1. Open service request
2. Trigger escalation
3. Save

Expected Result:
- escalation flag or status applies
- relevant users are notified
- record remains traceable

Pass Criteria:
- escalation handled correctly and visibly

---

## UAT-31: View operational report
Purpose:
Verify report query and display logic.

Steps:
1. Login as operations user
2. Open reports
3. Select a report and filter range

Expected Result:
- correct metrics display
- filters show appropriate data
- export works if available

Pass Criteria:
- report is accurate and usable

---

## UAT-32: View analytics dashboard
Purpose:
Validate admin analytics.

Steps:
1. Login as admin
2. Open analytics dashboard
3. Review KPIs

Expected Result:
- metrics and charts render correctly
- trend data matches source data
- access is limited to authorized role

Pass Criteria:
- analytics dashboard is understandable and accurate

---

## UAT-33: User management
Purpose:
Validate admin user management.

Steps:
1. Login as admin
2. Open user list
3. Change user status or role
4. Save

Expected Result:
- update applied correctly
- only admin can change role
- audit log captured

Pass Criteria:
- user lifecycle changes are valid and secure

---

## UAT-34: Role management
Purpose:
Verify role and permission control.

Steps:
1. Login as admin
2. Open roles page
3. Create or update role
4. Assign permissions

Expected Result:
- valid role is created
- permissions are mapped correctly
- invalid role assignment rejected

Pass Criteria:
- access control works as designed

---

## UAT-35: Pricing management
Purpose:
Validate pricing changes.

Steps:
1. Login as admin
2. Update service plan pricing
3. Save

Expected Result:
- pricing update appears in relevant views
- historical price data preserved
- validation prevents invalid prices

Pass Criteria:
- pricing is updated correctly and consistently

---

## UAT-36: System config changes
Purpose:
Check system settings and validation.

Steps:
1. Login as admin
2. Update configuration value
3. Save

Expected Result:
- valid config updates apply
- invalid config blocked
- actions are logged

Pass Criteria:
- configuration management is safe and auditable

---

## UAT-37: Audit log review
Purpose:
Validate auditability of admin and system actions.

Steps:
1. Perform role or config modification
2. Open audit log page
3. Review latest entries

Expected Result:
- correct user, action, entity, and timestamp recorded
- logs are searchable and readable

Pass Criteria:
- audit trail is complete and usable

---

# 10. Negative and Boundary Scenarios

## Negative Cases
- invalid OTP
- duplicate phone number
- invalid property
- invalid payment
- expired subscription
- unauthorized access attempts
- invalid complaint payload
- invalid role mapping

## Boundary Cases
- minimum and maximum field lengths
- large file uploads
- maximum list length
- long notes and complaint text
- minimum and maximum price values
- expiry dates at boundary

---

# 11. Security and Access Validation

These scenarios must be included in UAT:
- Customer cannot access another customer’s property
- Agent cannot access tasks outside assignment
- Operations user cannot access admin-only config
- Admin cannot bypass role checks
- Session timeout redirects to login
- Payment data remains protected
- Audit logs capture privileged actions

---

# 12. Defect Handling

## Severity
- Sev 1: service outage, data loss, payment integrity, security compromise
- Sev 2: major user journey block
- Sev 3: moderate issue with workaround
- Sev 4: cosmetic or low-impact issue

## Defect Process
1. Tester logs defect with scenario ID and evidence
2. QA triages severity and priority
3. Developer fixes issue
4. Defect is retested
5. Closed only after validation

---

# 13. UAT Reporting

## Daily Report
Include:
- scenarios executed
- pass/fail counts
- defects logged
- defects open/closed
- blockers
- environment status

## Final UAT Report
Include:
- summary of all UAT results
- pass/fail percentage
- defect summary
- open risks
- final recommendation: accept / reject / conditional acceptance

---

# 14. Sign-Off Template

## Approvers
- Product Owner
- QA Lead
- Business Stakeholder
- Engineering Representative

## Sign-Off Statement
I confirm that the PropertyPilot MVP has been validated against the approved business requirements and UAT scenarios. The release is accepted / rejected / accepted with conditions.

---

# 15. Final Recommendation

The UAT release is recommended for production approval only if:
- all critical and high-priority scenarios pass
- no Sev 1 or P1 defects remain open
- core business journeys are validated by business users
- role-based security and audit logs are confirmed
- payment and subscription flows are accepted
- final sign-off is approved by product and QA

---

# File: docs/Test_Data_Strategy.md

# Test Data Strategy

Document Type: Test Data Management Strategy  
Version: 1.0  
Date: 2026-08-31  
Status: Draft for Review  
Owner: QA / Test Data Management / Product

---

# 1. Purpose

This document defines how PropertyPilot will generate, manage, protect, validate, and refresh test data across all test stages. The goal is to ensure the project has reliable, representative, privacy-safe, and traceable datasets for development, QA, UAT, performance validation, and release verification.

---

# 2. Objectives

- Provide realistic test data for all MVP domains
- Maintain privacy and security standards for personal and sensitive data
- Support functional, security, performance, and regression testing
- Reduce flaky test results caused by missing or inconsistent data
- Ensure repeatable test preparation across environments
- Keep test data aligned to the canonical data dictionary and database schema

---

# 3. Scope

## In Scope
- customer and profile data
- property and ownership data
- service catalog and service request data
- payment, subscription, and invoice data
- agent assignments and task execution data
- notifications, complaints, and reports
- audit, configuration, and admin governance data

## Out of Scope
- Production data used directly outside approved masking or anonymization flow
- Data outside the MVP scope
- Unapproved non-business test data or synthetic data for unrelated products

---

# 4. Assumptions

- The canonical data dictionary and physical data model are the approved source for structure and field semantics
- Test data must be environment-specific and role-specific
- All test data must be traceable to origin and test objective
- PII and sensitive data must be masked or anonymized before use in non-production
- Synthetic data is preferred where realistic production-like values are not needed

---

# 5. Test Data Principles

## Data Quality
- Data must be complete, valid, and realistic
- Data must match the field constraints defined by schema and business rules
- Invalid datasets must exist in dedicated negative test scenarios

## Data Privacy
- PII must be minimized and masked
- Personal identifiers must never be copied directly into lower environments without approval
- Consent, profile, and contact fields must be protected in all non-production environments

## Data Security
- Test data must be stored in secure, access-controlled locations
- Access must be role-restricted
- Sensitive data must not be included in logs, screenshots, or defect reports without masking

## Data Traceability
- Each dataset should have creator, purpose, and environment metadata
- Test data must be mapped to scenarios or requirements
- Data provenance must be documented for audits and support

## Data Reusability
- Data should be reusable across repeated scenarios where possible
- Add scenario-specific data variants rather than creating one-off records for each case
- Maintain reusable seed sets for common flows

---

# 6. Test Data Categories

## Customer Data
- customer profile
- phone number
- email
- consent flags
- status
- preferences

## Property Data
- address
- property type
- ownership details
- verification state
- document metadata

## Service Data
- service catalog entries
- service request records
- request status and timeline
- evidence links

## Subscription Data
- plan
- renewal cycle
- subscription status
- effective and expiry dates

## Payment Data
- payment amount
- status
- failure reason
- gateway transaction reference
- invoice state

## Agent Data
- agent profile
- availability
- assignments
- visit notes and completion

## Report Data
- summary metrics
- report filters
- export status
- schedule or generation metadata

## Notification Data
- notification type
- channel
- status
- content and preference state

## Complaint Data
- complaint subject
- complaint type
- status
- escalation and resolution state

---

# 7. Test Environment Data

## Development
Purpose:
- low-cost, feature-level validation

Data characteristics:
- synthetic data
- frequent refresh
- reduced PII
- broad availability for engineering validation

## QA
Purpose:
- regression and integration validation

Data characteristics:
- representative but masked data
- scenario-specific datasets
- role-based access coverage
- coverage for defects and edge conditions

## UAT
Purpose:
- business sign-off and acceptance validation

Data characteristics:
- production-like datasets
- final workflow validation
- customer, agent, operations, and admin role testing
- sign-off evidence captured

## Performance
Purpose:
- load, concurrency, and stress validation

Data characteristics:
- larger record counts
- generated peak and stress volumes
- representative distribution across domains

## Production-Like
Purpose:
- release validation and final readiness testing

Data characteristics:
- near-production dataset shape and distribution
- no direct production customer data unless approved and masked
- synthetic equivalent to expected live operating volume

---

# 8. Synthetic Data Strategy

## Customer Profiles
Generate:
- valid customers
- inactive customers
- duplicate contact attempts
- customers with different consent states
- customers without property records

## Property Types
Generate:
- residential property
- commercial property
- multi-unit property
- invalid address records
- duplicate ownership records
- properties with pending verification

## Service Requests
Generate:
- valid open request
- completed request
- cancelled request
- escalated request
- invalid status transition record
- request with evidence
- request without evidence

## Subscriptions
Generate:
- active subscription
- expired subscription
- paused subscription
- cancelled subscription
- pending renewal
- invalid state combination

## Payments
Generate:
- successful payment
- declined payment
- partially processed payment
- refunded payment
- duplicate payment attempt
- late or expired payment

## Agent Assignments
Generate:
- assigned task
- unassigned request
- agent unavailable
- reassigned task
- overdue task
- task with GPS and evidence mismatch

## Reports
Generate:
- daily totals
- weekly trends
- zero-data report
- report export request
- large dataset report
- filters with no data

---

# 9. Negative Test Data

## Invalid Mobile Numbers
- missing digits
- too short / too long
- invalid format
- non-numeric characters
- duplicate mobile use

## Invalid OTPs
- expired OTP
- incorrect length
- incorrect digits
- reused OTP
- OTP after attempt limit

## Invalid Property Records
- missing address
- malformed coordinates
- duplicate property record
- invalid property type
- missing owner reference

## Invalid Payments
- insufficient funds
- wrong currency
- expired card
- duplicate transaction
- invalid gateway response

## Expired Subscriptions
- expired plan
- cancelled plan still inactive
- invalid transitional state
- renewal beyond effective date

## Unauthorized Access
- customer access to other customer records
- agent access to unrelated tasks
- operations user access to admin screens
- admin access without correct role

## Duplicate Records
- duplicate account creation
- duplicate property creation
- duplicate complaint submission
- duplicate payment attempt

---

# 10. Boundary Test Data

## Minimum Values
- minimum short field length
- minimum price values
- zero-duration or zero-value values
- minimum notes length
- minimum date range

## Maximum Values
- maximum length values
- maximum file size
- maximum list size
- maximum number of notifications
- maximum number of records in report dataset

## Special Characters
- apostrophes
- slashes
- Unicode text
- HTML-like strings
- emoji
- symbols in names, addresses, and notes

## Large File Uploads
- near-limit file sizes
- unsupported file types
- image files with bad metadata
- corrupt files

## High Volume Records
- 10k+ service requests
- 1k+ active properties
- high-density notifications
- large queue assignments
- large analytics snapshots

---

# 11. Security Test Data

## Authentication Cases
- valid OTP
- expired OTP
- blocked login
- invalid session
- multiple failed attempts

## Authorization Cases
- customer accessing another customer record
- agent accessing other assigned tasks
- operations user viewing admin config
- admin user changing a role without approval path

## Privilege Escalation Cases
- role manipulation attempts
- token tampering
- modified admin route or payload
- unauthorized config changes

## Data Exposure Cases
- raw payment payload in UI
- PII visible in errors
- audit log not capturing privileged actions
- notification content exposing sensitive data

## Audit Validation Cases
- admin role change
- payment refund
- subscription cancellation
- config update
- complaint resolution

---

# 12. Performance Test Data

## Expected Volume
- baseline usage for one-day operations
- standard service request volume
- normal agent queue size
- expected payment volume

## Peak Volume
- peak booking hours
- concurrent customer registrations
- multiple customer requests in parallel
- high alert and report generation load

## Stress Volume
- 2x to 5x expected peak
- sudden queue spike
- large report generation run
- batch job backlog

## Growth Projections
- 3-month growth
- 6-month growth
- annual scaling expectations
- future region or additional property volumes

---

# 13. Data Masking Strategy

## PII Fields
- mobile number
- email
- name
- address
- national ID / customer identity data
- payment customer data
- complaint contact data

## Sensitive Fields
- tokens
- payment references
- gateway IDs
- system config secrets
- audit content
- agent location metadata when sensitive

## Masking Rules
- mask full mobile numbers in QA and UAT
- replace names with deterministic seeded aliases
- anonymize address while preserving format
- mask emails to maintain structure but remove real values
- hash or partially mask payment references
- keep business-critical patterns intact for validation

## Anonymization Rules
- data should be non-recoverable to real persons
- maintain uniqueness where needed for test logic
- preserve valid formatting for UI and business validation
- avoid exposing original customer relationship details in screenshots or logs

---

# 14. Test Data Refresh Strategy

## Frequency
- development: daily or as needed
- QA: per release candidate or daily
- UAT: before each cycle or execution window
- performance: before major run
- production-like: before release validation and readiness checks

## Ownership
- QA owns data catalog and refresh schedule
- Development owns schema-dependent and feature-specific seeds
- Business owners validate realistic scenario coverage
- Operations confirms operational workflow data

## Validation Process
- verify dataset completeness
- verify prerequisite state for scenario execution
- verify masking and restrictions
- validate role access to relevant data
- ensure data supports planned scenarios without cross-contamination

## Approval Process
- data refresh requires QA approval
- UAT release data requires product owner sign-off
- security review for sensitive or production-like data
- change log kept for each refresh cycle

---

# 15. Data Creation Process

## Manual Creation
- used for targeted, scenario-specific records
- best for negative and edge conditions
- often used by QA for business-authored scenarios

## Automated Generation
- used for large synthetic data volumes
- useful for stress, performance, and regression testing
- scripts should create deterministic and repeatable records

## Migration-Based Creation
- used when schema-backed values are required
- useful for seeded reference data such as service catalog and role permissions
- ensure versioned migration scripts are used

## API-Based Creation
- used for testing realistic end-to-end flows
- good for customer registration, booking, payment, and complaint scenarios
- verifies API contract and UI integration together

---

# 16. Test Data Ownership

## QA Team
- owns scenario data design
- maintains refresh cycles
- verifies dataset quality and coverage

## Development Team
- owns seed scripts for service catalog, roles, and application defaults
- supports data creation through APIs and migration scripts
- validates data mapping to schema and canonical model

## Business Team
- validates realistic business values
- approves business scenario coverage
- reviews if data represents real-world conditions

## Operations Team
- provides production-like operational data cases
- validates dashboards, queue, and incidents
- helps define operational scenarios for queue and escalation

---

# 17. Compliance Requirements

## Privacy
- no real customer PII in non-production unless approved and masked
- customer consent data must remain valid and consistent

## Retention
- test data retention policies must be aligned with project governance
- temporary datasets must be removed on schedule or after approval

## Deletion
- data cleanup scripts required for environment reset
- permanent delete or anonymization procedures documented
- no orphan records allowed after environment refresh

## Auditability
- data creation and refresh actions must be logged
- dataset approval records retained
- data variance and masking review retained for compliance evidence

---

# 18. Risks

| Risk | Impact | Mitigation | Owner |
|---|---|---|---|
| PII leakage in lower environments | Security breach and compliance risk | strict masking, restricted access, review process | QA + Security |
| Incomplete or invalid seed data | failed test execution | mandatory validation checklist before environment use | QA |
| Duplicate or inconsistent records | false positives/negatives in tests | unique keys, validation scripts, cleanup | Development |
| Heavy test data causing performance noise | misleading results | environment-specific volumes and scaling | QA + Performance |
| Missing business scenario coverage | incomplete UAT sign-off | scenario traceability to requirements | Product + QA |
| Stale data in UAT | incorrect acceptance decisions | refresh scheduling and signoff | QA + Product |

---

# 19. Deliverables

## Test Data Sets
- master customer dataset
- property dataset
- service request dataset
- payment and subscription dataset
- agent and assignment dataset
- complaint dataset
- report and analytics dataset

## Seed Scripts
- environment setup scripts
- reference data scripts
- role and permission scripts
- service catalog seed scripts

## Data Generation Scripts
- synthetic customer and property generation
- service lifecycle generation
- payment and subscription generation
- performance volume generation

## Validation Reports
- dataset validation summary
- masking validation summary
- environment readiness summary
- refresh certification report

## Coverage Reports
- requirement to data coverage mapping
- scenario coverage mapping
- role coverage report
- data completeness report

---

# 20. Test Data Validation Checklist

Before use in QA or UAT, verify:
- required fields are present
- data values match schema constraints
- masking is applied correctly
- permissions and role data reflect actual access model
- duplicate records are controlled
- negative scenarios are available
- performance scenarios are sized appropriately
- refreshes have approvals recorded

---

# 21. Final Summary

A strong test data strategy is essential for PropertyPilot’s QA, UAT, and performance success. It ensures that all workflows are validated with realistic, role-aware, secure, and repeatable datasets that reflect the system’s operational reality while protecting privacy and maintaining data integrity.

This strategy supports:
- reliable defect detection
- valid business acceptance decisions
- accurate performance outcomes
- secure and compliant handling of sensitive information
- predictable and repeatable testing across all environments

The result is a quality process grounded in accurate, controlled, and traceable data.
```