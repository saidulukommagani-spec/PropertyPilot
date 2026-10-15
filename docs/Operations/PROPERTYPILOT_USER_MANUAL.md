# PropertyPilot User Manual

**Audience:** Customers, field agents, Operations staff, and administrators
**Documentation status:** Current application guide, based on the routed screens and workflows in the repository
**Last reviewed:** 13 October 2026

This manual explains what each role can do in the current PropertyPilot application and how to use its main screens. Available actions depend on the role assigned to your account and on the records, service coverage, pricing, and workflow status configured in the system.

## Contents

1. [Getting started](#1-getting-started)
2. [Roles at a glance](#2-roles-at-a-glance)
3. [Customer portal](#3-customer-portal)
4. [Agent workspace](#4-agent-workspace)
5. [Operations workspace](#5-operations-workspace)
6. [Administrator workspace](#6-administrator-workspace)
7. [Shared workflows and terms](#7-shared-workflows-and-terms)
8. [Troubleshooting](#8-troubleshooting)

## 1. Getting started

### Register

1. Open the public sign-in page and choose **Register**.
2. Enter your full name, email address, mobile number, password, and password confirmation.
3. Use a password with at least eight characters and submit the form.
4. Sign in with the account credentials. Customer registration creates a customer account; agent, Operations, and administrator access is assigned through the appropriate internal account and access process.

### Sign in and navigate

1. Enter your email and password on **Login**.
2. PropertyPilot opens the workspace allowed by your account role.
3. Use the left navigation menu to move between pages. The header identifies the current workspace and provides the account menu; choose **Sign out** there when finished.
4. If a page offers search, filters, pagination, **Refresh**, or **Retry**, use those controls to find records or recover from a loading error.

The application supports the roles **CUSTOMER**, **AGENT**, **OPERATIONS**, and **ADMIN**. A user can have more than one role; in that case, some pages may show additional controls. Access is enforced by the application and backend, so a menu item alone does not grant access.

## 2. Roles at a glance

| Role | Main purpose | Typical workspace |
|---|---|---|
| Customer | Register properties, request services, review estimates, and track delivery | Customer portal |
| Agent | Work assigned requests and visits, update field progress, and submit evidence | Agent workspace |
| Operations | Triage leads, coordinate requests and agents, schedule field work, and review evidence | Operations workspace |
| Administrator | Configure the catalogue, pricing, plans, users, and oversee operational and financial records | Admin console |

## 3. Customer portal

### Dashboard — `/customer/dashboard`

**Purpose:** A summary of your properties, service requests, and account activity.

**How to use it:** Review service usage by service type and property activity. Choose **Request a service** to start a new request. Add a property first if your account has none.

### My Properties — `/customer/properties`

**Purpose:** Find and manage the properties registered to your account.

**How to use it:** Search by property details and filter by property status. Open a property card to see its details and activity. Choose **Add property** to register another property.

On a property's detail page, use **Property documents** to upload a file against that property or view a previously uploaded file. Document types are provided by the property-specific document checklist. Authorized administrators can view the same files from the property's **Documents** tab.

### Add or edit property — `/customer/properties/new`, `/customer/properties/:id/edit`

**Purpose:** Save the property information used for service eligibility, estimates, visits, and records.

**How to use it:** Enter a property name and select its property type. Add the survey number when available. Enter the property area in square feet or square yards; for agricultural land, enter whole acres and the remaining guntas (0–39). Enter the home/apartment built-up area when that is the useful measurement. Provide the property address: address line 1, optional address line 2, locality, city/town, district, state, and six-digit PIN code. Select the property status and optionally enter its value/price when requested. Save the form. When editing, confirm the saved information before leaving the page.

Use the closest matching property type and keep the address complete: coverage and travel-dependent service availability can depend on the property location. The exact types shown in the form are the application’s current list.

### Property details — `/customer/properties/:id`

**Purpose:** Review the property record, its address, service requests, visit history, documents, and evidence.

**How to use it:** Select **Edit property** to correct details. Select **Archive** when you no longer want to use the property for new work. Archived properties are retained in the account but cannot be used to start a service request. Choose **Request a service for this property** to begin a request for this property.

### Create a service request — `/customer/service-requests/new`

**Purpose:** Select eligible work for a registered property, review the current estimate, and submit a request.

**How to use it:**

1. Choose one of your active registered properties.
2. Choose an available **Service type**. The available types are based on the saved property type and current catalogue eligibility.
3. Choose the specific **Service**. If a service is unavailable, check the property details or contact Operations; the customer cannot override catalogue eligibility.
4. Complete any service-specific estimate inputs shown. For percentage-based services such as loan assistance, provide the requested loan amount and calculate the estimate.
5. Review the estimate, including the standard price, PropertyPilot price, and savings when provided. The estimate is based on the active administrator-configured pricing rules. It is specific to the selected service and request inputs.
6. Accept the estimate to continue. Change the property, service type, service, or pricing input and recalculate if needed; a prior estimate may no longer apply.
7. Choose a priority, optionally request a preferred service date, and describe the work. Submit the service request.

A preferred date is a request, not a confirmed appointment. Submitting the request does not itself confirm a field visit; Operations coordinates delivery under the service workflow.

### Service requests — `/customer/service-requests`

**Purpose:** Track requests and filter the list by search text or status.

**How to use it:** Search by service or property, choose a status filter, and open a request for its timeline, visit, and shared evidence. Choose **Request a service** to create another request.

### Service request details — `/customer/service-requests/:id`

**Purpose:** See the selected service, property, priority, submitted date, service target (when configured), progress timeline, visits, and shared evidence.

**How to use it:** Review the timeline for progress. Open a linked property or visit for more detail. Use the Evidence page to review evidence shared with you.

### Visits — `/customer/visits`, `/customer/visits/:id`

**Purpose:** View scheduled visits connected to your service work.

**How to use it:** Open a visit to review its schedule, status, service request, and any available visit information. A requested service date is not a confirmed visit; use the visit record for the current scheduled details.

### Evidence — `/customer/evidence`

**Purpose:** Review evidence made available to your customer account for service work.

**How to use it:** Search or filter available evidence, open a record to view its details or supported preview, and use the linked service request/visit context to understand the submission. Evidence availability and visibility depend on the request workflow and review state.

### Quotes and estimates — `/quotes`, `/quotes/dashboard`, `/quotes/:id`

**Purpose:** Review quote records available to your account and their status/details.

**How to use it:** Use the dashboard for a summary, the list to search/filter records, and a quote detail page for its line items, terms, and status. The estimate shown during a customer service request is generated from configured pricing; use the request screen’s **Accept estimate** action to proceed with that selection. A quote’s status and available actions depend on its workflow state.

### Subscriptions — `/customer/subscriptions`, `/subscriptions`

**Purpose:** Review current and past subscription records.

**How to use it:** Use the customer subscription page to review your own plans. The subscription list supports status filtering and record links. Open a subscription to review its term, renewal preference, renewal history, purchased add-ons, and usage.

### Subscription plans — `/subscriptions/plans`, `/subscriptions/plans/:id`

**Purpose:** Browse available plans and inspect their included services and terms.

**How to use it:** Open a plan to see details. If customer purchase is enabled, select an active plan price version, choose the start/end dates, set the renewal preference, and submit. A renewal still requires a separate payment verified by the payment provider.

### Subscription usage — `/subscriptions/:id/usage`

**Purpose:** See included entitlements, consumption, and remaining balance for a subscription.

**How to use it:** Open the subscription’s **View usage** action. Review the service allowance and recorded usage; contact support if consumption does not match your service history.

### Renewal center — `/subscriptions/renewals`

**Purpose:** Review renewal periods and renewal/payment progress.

**How to use it:** Check the current subscription and renewal history. The system may prepare a renewal invoice; payment settlement must be verified before the renewal is applied. Follow any administrator or provider action shown on the record.

### Add-ons — `/subscriptions/addons`

**Purpose:** Review purchased add-ons and add available extras to an eligible active subscription.

**How to use it:** Choose **Add an add-on**, select the subscription and available add-on, enter a quantity, and submit. If the list is empty, there is no eligible active subscription or no purchasable add-on configured.

### Invoices — `/customer/invoices`

**Purpose:** Review invoices associated with your customer account.

**How to use it:** Search by invoice details, filter by status, and open the invoice for its amount, dates, status, and available payment/document actions. Invoices are created through the billing workflow; estimate acceptance is not itself an invoice payment.

### Customer leads — `/customer/leads/dashboard`, `/customer/leads`, `/customer/leads/new`, `/customer/leads/:leadId`

**Purpose:** Submit and track a property or service enquiry that needs follow-up.

**How to use it:** The dashboard summarizes lead stages. Choose **Create lead** and provide how you heard about PropertyPilot, optional minimum/maximum budget, and notes about your enquiry. Review your lead list and open a lead for its recorded source, property type/location (when available), budget, notes, and status. Operations owns qualification and follow-up.

## 4. Agent workspace

### Agent dashboard — `/agent/dashboard`

**Purpose:** Review assigned field work and recent completion activity.

**How to use it:** Check the weekly completed-visit summary and productivity trend. Choose **Today’s work** to open your visits.

### My work queue — `/agent/work-queue`

**Purpose:** See assigned visits and service requests that do not yet have a visit.

**How to use it:** Filter the queue using the status tabs. Open a visit to review its schedule and work details. For an assigned request without a visit, choose **Open work**. When the request is in progress, use **Upload evidence** if the service requires or permits evidence submission.

### Assigned leads — `/agent/leads`, `/agent/leads/:leadId`

**Purpose:** Review leads made available to the agent and inspect their details.

**How to use it:** Search/filter the assigned lead list and open a lead to review its source, property type, location, budget, notes, and contact history when recorded. Lead assignment and stage control remain with the authorized operations/admin workflow.

### Assigned service request — `/agent/service-requests/:id`

**Purpose:** Review the request, property, service, status, timeline, and evidence before doing assigned work.

**How to use it:** Open from the work queue. Follow the request/visit instructions and record field progress through the visit workflow. Upload evidence from the request when allowed by its current status.

### My visits — `/agent/visits`, `/agent/visit/:visitId`, `/agent/visits/:visitId`

**Purpose:** Review and carry out scheduled field visits.

**How to use it:** Find your visit and open it for the visit schedule, status, service request, and execution details. Use the visit actions available for its current status to progress the work. Where supported, provide a reason when rescheduling or cancelling. Upload requested evidence from the visit page or its evidence action.

### Visit evidence — `/agent/visits/:visitId/evidence`

**Purpose:** Review evidence attached to a visit.

**How to use it:** Confirm the visit context and review previously submitted evidence. Submit new evidence from the visit workflow when requested.

### Agent profile — `/agent/profile`

**Purpose:** Review your operational profile, status, service area, availability, skills, and performance.

**How to use it:** Check that your area, availability, and skills are accurate. The profile page is a view of the agent record; contact an administrator/Operations user to correct managed profile fields if direct editing is not offered.

## 5. Operations workspace

Operations users coordinate delivery and customer follow-up. They do not automatically receive administrator-only catalogue, pricing, user-access, or finance configuration controls.

### Operations overview — `/staff`

**Purpose:** See operational workload and exception counts at a glance.

**How to use it:** Review the service request, assignment, active/completed/failed request, invoice, payment, and evidence cards. Choose a card action to open the related queue and work the outstanding items.

### Lead queue — `/operations/leads`, `/operations/leads/:leadId`

**Purpose:** Search, qualify, assign, and follow up on incoming customer leads.

**How to use it:** Search by customer/contact/source/property/location, filter by lead status, sort columns, and paginate through results. Open a lead to inspect the enquiry and contact history. Authorized staff can assign it to an agent and update its status through the permitted next stages. Closed stages cannot be reopened in the current flow.

### Agent directory and performance — `/operations/agents`, `/operations/agents` (performance view from navigation)

**Purpose:** Review agent availability, skills, assignments, and operational performance.

**How to use it:** Search/filter the agent directory and open an agent record or performance view. Use the data to coordinate assignments and identify workload or performance follow-up. Agent profile changes are subject to role permissions.

### Agent assignments — `/operations/assignments`

**Purpose:** Match eligible agents to accepted service requests and track assignment status.

**How to use it:** Review unassigned or pending work, inspect the request requirements and agent eligibility, and use the available assignment controls. Follow assignment acceptance/status until the request can proceed to delivery. Assignment availability depends on agent status, coverage, skills, and service requirements.

### Service request queue — `/service-requests`, `/service-requests/dashboard`

**Purpose:** Monitor request volume/status and manage the Operations service-delivery queue.

**How to use it:** Use the dashboard for status/priority summaries and the list to search or filter individual requests. Open a request for its timeline, assignment, visits, and evidence. Use the available status actions to move work through the supported workflow.

### Create/edit service request — `/service-requests/new`, `/service-requests/:id/edit`

**Purpose:** Create a request from an accepted quote or correct a request that is still eligible for editing.

**How to use it:** Select the customer’s eligible accepted quote, set priority, optionally record a preferred date, and describe the requested work. Save the request. Requests may become read-only for editing after assignment or later workflow progress.

### Service request details — `/service-requests/:id`

**Purpose:** Coordinate a request through assignment, visits, evidence, and status progression.

**How to use it:** Review the property, service, customer, quote, status, and timeline. Use **Assign** to select an eligible agent when needed, then use the permitted status actions. Create a visit after an accepted assignment when the service requires field work. Monitor evidence and request history from the same workspace.

### Visits — `/visits`, `/visits/dashboard`, `/visits/new`, `/visits/:visitId`, `/visits/:visitId/edit`

**Purpose:** Schedule and monitor field visits.

**How to use it:** Use the dashboard to review visits by status/service. Filter the list by status, agent, and date range. Choose **Create Visit**, select an accepted request and agent assignment, enter the date/time and notes, and submit. Open a visit to inspect its timeline and evidence. Use **Reschedule** or **Cancel visit** where permitted; supply a reason. Editing records the reschedule reason.

### Evidence operations — `/evidence`, `/evidence/dashboard`, `/evidence/validation`, `/evidence/:evidenceId`, `/visits/:visitId/evidence`

**Purpose:** Monitor field submissions and review evidence before it is accepted as complete.

**How to use it:** The evidence list/dashboard show submitted records and queue status. Open a record for its files, metadata, request/visit context, and audit history. In **Evidence validation**, inspect the submission and choose **Approve**, **Reject**, or **Request re-upload** with a review comment. Approval can satisfy service requirements and may advance or complete the associated request automatically.

### Quotes and estimates — `/quotes`, `/quotes/dashboard`, `/quotes/:id`

**Purpose:** Review quote status and coordinate customer estimate/approval work.

**How to use it:** Search/filter the list, use the dashboard for quote status totals, and open a quote for details. Follow its current status and supported actions. Customer estimate pricing is created from active pricing configuration; an Operations quote may need administrator review depending on the workflow.

### Subscriptions — `/subscriptions`, `/subscriptions/dashboard`, `/subscriptions/:id`, `/subscriptions/:id/usage`, `/subscriptions/renewals`, `/subscriptions/addons`, `/subscriptions/plans`, `/subscriptions/plans/:id`

**Purpose:** Review subscription records, plan usage, renewals, and add-ons.

**How to use it:** Filter subscriptions by status, open a record for its term, renewal preference, usage, renewal history, and add-ons. Use the renewal center to review pending renewals and payment reconciliation. Plan pages provide plan and entitlement details. Customer-facing purchase actions depend on role and configuration.

## 6. Administrator workspace

Administrators have operational access plus configuration and financial oversight. Use the least disruptive available action: deactivate or archive a record when historical requests may refer to it, rather than removing historical records.

### Admin console — `/staff`

**Purpose:** Access administrative workspaces and see operational health.

**How to use it:** Review overview cards and use the navigation sections for customers/assets, service delivery, quotes/finance, and configuration. The Overview screen is an entry point; use the appropriate directory or dashboard for detailed work.

### Admin directories — `/staff/:resource`

The generic directory screens provide search, supported status/type filters, pagination, record details, and role-dependent actions. Available directories include:

| Directory | Purpose and common use |
|---|---|
| Customers | Find customer accounts; inspect details, edit supported fields, or activate/deactivate an account. |
| Properties | Find property records; review ownership and address, edit supported details, or archive/restore. |
| Service requests | Find and monitor requests; open the request workspace for assignment and workflow actions. |
| Agents | Find agent accounts and inspect profile/operational availability. |
| Visits | Find scheduled field visits and open their details. |
| Evidence | Find evidence records and open details/review queues. |
| Quotes | Search estimates/quotes and inspect status and detail. |
| Deals | Review property transaction records and progress. |
| Invoices | Search/filter invoices and open billing details. |
| Payments | Review payment records and reconciliation status. |
| Subscriptions | Review customer subscription records and their status. |
| Services | Configure the service catalogue and service availability. |
| Pricing rules | Configure service price calculation rules. |
| Plans | Configure subscription plan records and versions. |
| Users | Review user accounts and manage supported access assignments. |

Choose **View** to inspect a record, **Edit** when available, and use the provided status action for activation, deactivation, archival, or restoration. Actions and fields vary by record type and current state.

### Record detail — `/staff/:resource/:id`

**Purpose:** Inspect a selected administrative record, including its linked customer/property and operational or financial details.

**How to use it:** Open a record from its directory. Review its fields and linked history, then use the supported edit or workflow action. Return to the directory when finished. Do not change identifiers on records referenced by historical requests.

### Customer and property editing — `/staff/customers/:id/edit`, `/staff/properties/:id/edit`

**Purpose:** Correct customer or property data under administrator authority.

**How to use it:** Verify the record identity before saving. Keep customer contact details and property location/type current because they affect account communication, service eligibility, and delivery. Use archive/deactivate actions for records that should no longer be used, retaining their history. To review uploaded property documents, open the customer's property record and select its **Documents** tab. This keeps each file alongside the property it describes.

### Service catalogue — `/staff/services`

**Purpose:** Maintain the services customers can request and the operational requirements for delivering them.

**How to use it:** Create or edit a service with a stable service code, customer-facing name, service type/category, base/fallback price where applicable, delivery/visit policy, frequency, property eligibility, and operational requirements (such as visit, GPS, photos, signature, or document needs). Keep a service inactive until its coverage, price, SLA, skills, and delivery process are configured. Activate only when it is ready to appear to eligible customers. Deactivate retired services to preserve historical request references.

The customer’s Service Type and Service dropdowns are driven by active catalogue records and their eligibility rules. Do not create duplicate service records merely to serve multiple property types; configure one service with the eligible types it supports.

### Pricing rules — `/staff/pricing`

**Purpose:** Configure administrator-controlled prices used to calculate customer estimates.

**How to use it:** Select the applicable service and create/update the price rule with its pricing model, rate, currency, effective dates, and active status. Use a fixed amount for fixed-price services. For a service priced as a percentage of an amount (for example, a loan service), configure a percentage rule and its minimum/maximum if needed; the customer estimate form will request the amount used in the calculation. Review the effective rule and test inputs through the estimate workflow before activating the rule for customer use.

The estimate may show standard price, PropertyPilot price, and customer savings when those values are configured and returned by the pricing system. Do not treat illustrative rates in product documents as production rates unless an administrator has explicitly configured and approved them. Travel-dependent prices require supported location/travel inputs; an estimate should not be represented as a confirmed final price if required inputs are missing.

### Subscription plans — `/staff/plans`

**Purpose:** Maintain plan catalogue and versions, including price and entitlements.

**How to use it:** Open a plan to review details and available versions. Create or update the plan and its active price/entitlement configuration using the supported admin controls. Verify the plan’s customer-facing description, included usage, dates, and price before making it available.

### User access — `/staff/users`

**Purpose:** Review application users and manage supported role/access assignments.

**How to use it:** Find the intended user, open **Manage access**, verify their identity, assign only the roles required for their job, and save. Role assignment affects both visible navigation and backend authorization. Sign out and back in if the updated role is not reflected in an existing session.

### Lead administration — `/staff/leads/:leadId`, `/operations/leads/:leadId`

**Purpose:** Administer lead assignment and stage progression.

**How to use it:** Review the lead, assign an owner, and update its status only to a permitted next stage. Closed leads cannot be reopened in the current workflow.

### Service request administration — `/staff/service-requests`, `/staff/service-requests/dashboard`, `/staff/service-requests/new`, `/staff/service-requests/:id`, `/staff/service-requests/:id/edit`

**Purpose:** Monitor, create, assign, and advance service requests.

**How to use it:** Follow the Operations steps above. Admins can access the same delivery screens and may have additional customer/property or configuration access. Ensure a request uses a valid accepted quote/estimate and an authorized property/service combination.

### Quotes — `/quotes/new` and quote pages

**Purpose:** Create and manage quote records under the administrator workflow.

**How to use it:** Create a quote using the customer, property, service, and pricing information required by the form. Review its status and details after saving. Customer requests use the quote/estimate that matches the selected property and service; do not attach an unrelated quote.

### Deals — `/deals`, `/deals/dashboard`, `/deals/new`, `/deals/:id`, `/deals/:id/edit`

**Purpose:** Track property sale, purchase, rental, or lease transactions.

**How to use it:** Use the dashboard/list to find deals. To create one, select a seller and that seller’s registered property, choose a deal type, enter the expected amount and seller/buyer commission percentages, and add notes. Open the saved deal to review participants, financials, and status. Edit the status only to a permitted next stage; completed or cancelled deals are closed.

Deals represent property transactions and are separate from service request quotes.

### Invoices — `/invoices`, `/invoices/dashboard`, `/invoices/:id`, `/invoices/:id/pdf`

**Purpose:** Review invoice totals, revenue, outstanding balances, status, and invoice documents.

**How to use it:** Use the dashboard to review totals and status counts. Search/filter the list and open an invoice for its details and supported workflow actions. Generate or view the PDF from the invoice record. The new-invoice route currently redirects to the invoice list; invoices are generated through the configured billable-service workflow.

### Payments — `/staff/payments`, `/staff/payments/:paymentId`

**Purpose:** Review payment records and resolve reconciliation exceptions.

**How to use it:** Find payments by status/details, open the payment record, and review its linked invoice/customer, provider reference, amount, and reconciliation status. Use only the actions shown for the current payment state; do not mark provider settlement manually unless the authorized reconciliation workflow supports it.

### Admin visits and evidence

Admins can use the same visit scheduling and evidence review screens described under Operations. These screens provide operational actions but do not replace the audit trail or the evidence validation process.

## 7. Shared workflows and terms

### Service request lifecycle

**Property → eligible Service Type → eligible Service → estimate/quote → accepted request → assignment → visit (when required) → evidence review → completion and billing (when applicable).**

The backend verifies customer ownership, property/service eligibility, service activation, location coverage, and request validity. Frontend dropdown filtering is a convenience; it is not an authorization override. Changing a property or service clears dependent selections and can invalidate a previously calculated estimate.

### Statuses and dates

- Status choices depend on the current state; only allowed transitions are available.
- A preferred service date is a customer preference. A scheduled visit record is the confirmed operational schedule.
- Service targets/SLA values appear only when configured for the service/request.
- Some actions are disabled after assignment, completion, cancellation, or another terminal state.

### Prices and estimates

- Customer estimates use active administrator-configured pricing rules and request-specific inputs.
- Standard price, PropertyPilot price, and savings are shown when available from the pricing response.
- Percentage-based prices require the relevant input amount. Review it carefully before calculating and accepting.
- An accepted estimate authorizes the service request flow; invoicing and payment follow the billing lifecycle and are separate steps.

### Property documents and service evidence are different

Property documents are attached to the registered property they describe. Open **My Properties**, choose a property, and use its **Property documents** card to upload a file or view an existing file. The available document types come from the property's document checklist. Authorized staff can open the property record and use its **Documents** tab to view uploaded files. Older records created before file uploads were enabled may show metadata without a file. Service evidence is captured as part of a service request or visit and has its own upload and review workflow; customers should not use service evidence as a substitute for permanent property documents.

### Evidence

Evidence can include files and metadata captured during service delivery. Reviewers may approve, reject, or request re-upload. Follow the evidence type and request/visit context, and include the review comment requested by the screen.

## 8. Troubleshooting

| What you see | What to do |
|---|---|
| No Service Types or Services are available | Confirm the property is active and its property type/address are correct. The administrator must have configured an active service, its type, eligible property types, and applicable coverage. |
| Estimate calculation fails | Check required inputs (including loan amount for a percentage rule), property/service selections, and whether the active pricing rule is valid for the current date. Retry. If it still fails, provide the service name and displayed error to Operations/admin. |
| “No accepted quote” or request cannot be submitted | Recalculate and accept the estimate for the current property and service. A quote for a different property/service does not qualify. If the estimate cannot be produced, ask an administrator to review pricing configuration. |
| A property address is missing or incorrect | Edit the property and save the full address, then reopen the property details. If it remains missing, report the property record to support/admin so the saved API record can be checked. |
| A visit or evidence action is unavailable | Check the request/visit status and whether the service requires field work or evidence. Ask Operations to review assignment and workflow status. |
| A page displays a loading or server error | Use **Retry** or **Refresh** once. If it persists, note the page, time, record identifier, and exact error; contact the system administrator. Do not repeatedly submit a create/payment action while its result is uncertain. |
| A menu/page is missing | The account may not have the required role. Ask an administrator to review access; then sign out and back in after a role update. |

## Scope and accuracy note

This guide describes the pages currently registered in the frontend route configuration and the behavior visible in the repository at the review date. It is not proof that a live database, payment provider, account, or deployment has been verified. Some actions are conditional on backend configuration, data availability, role permissions, and workflow status; labels may change in later releases.
