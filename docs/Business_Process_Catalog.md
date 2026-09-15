# PropertyPilot - Complete Business Process Catalog

**Document Version:** 1.0  
**Date:** 2026-08-31  
**Status:** Production Ready  
**Standard:** BPMN 2.0 (Business Process Model and Notation)

---

## Table of Contents

1. [Customer Management Processes](#1-customer-management-processes)
2. [Property Management Processes](#2-property-management-processes)
3. [Verification Services Processes](#3-verification-services-processes)
4. [Monitoring Services Processes](#4-monitoring-services-processes)
5. [Agent Operations Processes](#5-agent-operations-processes)
6. [Vendor Operations Processes](#6-vendor-operations-processes)
7. [Payment Processing Processes](#7-payment-processing-processes)
8. [Subscription Management Processes](#8-subscription-management-processes)
9. [Complaint Management Processes](#9-complaint-management-processes)
10. [Report Management Processes](#10-report-management-processes)

---

## 1. CUSTOMER MANAGEMENT PROCESSES

### 1.1 Customer Registration and Onboarding

**Process ID:** PROC-CM-001  
**Process Name:** Customer Registration and Onboarding  
**Process Category:** Customer Management  
**Process Owner:** Customer Success Team  
**Version:** 1.0

**Business Description:**
New customer registration flow from initial signup through email verification and profile completion.

**Actors:**
- **Primary:** New Customer
- **Secondary:** System (Automated), Email Service, Compliance System
- **Support:** Customer Support Agent (if manual intervention needed)

**Inputs:**
- Email address
- Password
- First name
- Last name
- Phone number
- User type (customer/agent/vendor)
- Terms acceptance

**Outputs:**
- Active user account
- Verified email address
- User profile created
- Welcome email sent
- Account confirmation

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-CM-001 | Email uniqueness | Email must not exist in system |
| BR-CM-002 | Password complexity | Min 8 chars, 1 upper, 1 lower, 1 number, 1 special |
| BR-CM-003 | Phone validation | E.164 format, valid country code |
| BR-CM-004 | User type validation | Must be one of: customer, agent, vendor |
| BR-CM-005 | Terms requirement | Terms must be accepted before registration |
| BR-CM-006 | Email verification | Must verify email within 24 hours |
| BR-CM-007 | Rate limiting | Max 5 registration attempts per IP per hour |
| BR-CM-008 | Age verification | Minimum age 18 years (for some user types) |

**Process Flow (BPMN):**

```
START
  │
  ├─ [1] Receive Registration Request
  │   └─ Validate Input Data
  │       ├─ Check email format
  │       ├─ Validate password complexity
  │       ├─ Check phone format
  │       └─ Verify terms acceptance
  │
  ├─ [DECISION] Input Valid?
  │   ├─ NO → Return validation error → END
  │   └─ YES ↓
  │
  ├─ [2] Check Email Uniqueness
  │   └─ Query database for existing email
  │
  ├─ [DECISION] Email Exists?
  │   ├─ YES → Return conflict error → END
  │   └─ NO ↓
  │
  ├─ [3] Hash Password
  │   └─ Apply bcrypt algorithm (cost factor: 12)
  │
  ├─ [4] Create User Account
  │   ├─ Generate User ID (UUID)
  │   ├─ Store user record
  │   ├─ Encrypt sensitive data
  │   └─ Log account creation
  │
  ├─ [5] Generate Email Verification Token
  │   ├─ Create random token (32 bytes)
  │   ├─ Set expiration (24 hours)
  │   └─ Store token in cache
  │
  ├─ [6] Send Verification Email
  │   ├─ Queue email task
  │   └─ Include verification link with token
  │
  ├─ [ASYNC] Wait for Email Verification
  │   ├─ Success → [7]
  │   ├─ Timeout (24h) → Delete unverified account
  │   └─ Manual Verification → [8]
  │
  ├─ [7] Mark Email as Verified
  │   ├─ Update user record
  │   ├─ Set verification timestamp
  │   └─ Invalidate token
  │
  ├─ [8] Create Initial Profile
  │   ├─ Set default preferences
  │   ├─ Initialize notification settings
  │   └─ Setup account security settings
  │
  ├─ [9] Send Welcome Email
  │   ├─ Queue welcome email
  │   └─ Include onboarding guide link
  │
  ├─ [10] Return Success Response
  │   ├─ Provide user ID
  │   ├─ Confirm account status
  │   └─ Display next steps
  │
  └─ END
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Registration Completion | % of registrations completing all steps | ≥ 95% |
| Email Verification | % emails verified within 24 hours | ≥ 92% |
| Account Activation Time | Time from registration to active status | < 5 minutes |
| Error Rate | Failed registrations (technical errors) | < 1% |
| Duplicate Prevention | Duplicate accounts created | 0 |
| Password Quality | Weak passwords attempted | < 5% |
| Security Breaches | Compromised registrations | 0 |

**Validation Checkpoints:**

```
Checkpoint 1: Input Validation
├─ Email format valid ✓
├─ Password meets requirements ✓
├─ Phone number valid ✓
└─ Terms accepted ✓

Checkpoint 2: Database Checks
├─ Email unique ✓
├─ User ID generated ✓
└─ Record created ✓

Checkpoint 3: Email Delivery
├─ Verification email sent ✓
├─ Token generated ✓
└─ Expiration set ✓

Checkpoint 4: Verification
├─ Token valid ✓
├─ Not expired ✓
└─ Email verified ✓
```

**Error Handling:**

| Error Type | Error Code | Response | Recovery |
|-----------|-----------|----------|----------|
| Invalid Email | REG-001 | 400 Bad Request | Provide email format example |
| Weak Password | REG-002 | 422 Unprocessable | Show password requirements |
| Email Exists | REG-003 | 409 Conflict | Suggest login or password reset |
| Invalid Phone | REG-004 | 400 Bad Request | Provide E.164 format example |
| Terms Not Accepted | REG-005 | 400 Bad Request | Highlight terms checkbox |
| Email Delivery Failed | REG-006 | 202 Accepted | Retry with exponential backoff |
| Token Expired | REG-007 | 401 Unauthorized | Provide resend verification link |
| Rate Limited | REG-008 | 429 Too Many Requests | Show retry-after time |

**Performance SLA:**

| Metric | SLA | Priority |
|--------|-----|----------|
| Registration Processing | < 2 seconds | High |
| Email Delivery | < 5 minutes | High |
| Token Generation | < 100ms | Critical |
| Database Write | < 50ms | Critical |
| Total E2E Time | < 5 minutes | High |

---

### 1.2 Customer Profile Management

**Process ID:** PROC-CM-002  
**Process Name:** Customer Profile Management  
**Process Category:** Customer Management  
**Process Owner:** Customer Success Team  
**Version:** 1.0

**Business Description:**
Manage customer profile information including personal details, preferences, and saved searches.

**Actors:**
- **Primary:** Customer
- **Secondary:** System (Automated), Search Engine
- **Support:** Customer Support Agent

**Inputs:**
- Customer ID
- Profile data (name, phone, email, photo)
- Preferences (price range, locations, property types)
- Financing information
- Search preferences

**Outputs:**
- Updated customer profile
- Updated preferences applied
- Search engine refreshed
- Audit log entry
- Confirmation notification

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-CM-010 | Phone update | E.164 format required |
| BR-CM-011 | Email uniqueness | New email must not exist |
| BR-CM-012 | Profile photo size | Max 5MB, jpg/png format |
| BR-CM-013 | Price range validation | Min ≤ Max |
| BR-CM-014 | Location validation | Valid US/Canada locations |
| BR-CM-015 | Audit trail | All changes logged with timestamp |
| BR-CM-016 | Preference limits | Max 10 saved searches |
| BR-CM-017 | Change notification | User notified of account changes |

**Process Flow (BPMN):**

```
START
  │
  ├─ [1] Authenticate Customer
  │   ├─ Verify bearer token
  │   └─ Validate customer ID
  │
  ├─ [2] Retrieve Current Profile
  │   └─ Load from database/cache
  │
  ├─ [3] Receive Update Request
  │   ├─ Get update payload
  │   └─ Identify changed fields
  │
  ├─ [4] Validate Update Data
  │   ├─ Check field-level constraints
  │   ├─ Verify format requirements
  │   └─ Check business rule compliance
  │
  ├─ [DECISION] Validation Passed?
  │   ├─ NO → Return error details → END
  │   └─ YES ↓
  │
  ├─ [5] Check Special Validations
  │   ├─ If email changed:
  │   │   ├─ Verify email unique
  │   │   └─ Queue verification email
  │   ├─ If phone changed:
  │   │   └─ Queue verification SMS
  │   └─ If photo uploaded:
  │       ├─ Process image
  │       └─ Upload to CDN
  │
  ├─ [6] Create Audit Log Entry
  │   ├─ Record all changed fields
  │   ├─ Capture before/after values
  │   ├─ Timestamp change
  │   └─ Note request source (API/UI)
  │
  ├─ [7] Update Profile in Database
  │   ├─ Begin transaction
  │   ├─ Apply updates
  │   ├─ Update timestamp
  │   └─ Commit transaction
  │
  ├─ [8] Invalidate Cache
  │   └─ Remove profile from Redis/cache
  │
  ├─ [9] Update Search Index (if preferences changed)
  │   ├─ Refresh property recommendations
  │   ├─ Update favorite properties
  │   └─ Recalculate matching listings
  │
  ├─ [10] Send Notification
  │   ├─ Email: Profile updated confirmation
  │   ├─ Push: If on mobile
  │   └─ Include change summary
  │
  ├─ [11] Return Updated Profile
  │   └─ Include all current information
  │
  └─ END
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Update Completion | % updates processed successfully | ≥ 99% |
| Data Consistency | Database vs cache mismatch | 0 |
| Validation Accuracy | Incorrect rejections | < 0.1% |
| Performance | Update latency | < 1 second |
| Audit Trail Completeness | All changes logged | 100% |
| Notification Delivery | Profile update confirmations sent | ≥ 98% |

**Data Fields Tracked:**

```
Protected Fields (Require Verification):
├─ Email address
├─ Phone number
└─ Password

Preference Fields (No Verification):
├─ Price range
├─ Property types
├─ Locations
└─ Notification settings

Profile Fields (Optional):
├─ First name
├─ Last name
├─ Date of birth
├─ Profile photo
└─ Bio/About
```

---

### 1.3 Customer Search and Favorites Management

**Process ID:** PROC-CM-003  
**Process Name:** Customer Search and Favorites Management  
**Process Category:** Customer Management  
**Process Owner:** Product Team  
**Version:** 1.0

**Business Description:**
Handle customer property searches, save favorites, and manage saved search alerts.

**Actors:**
- **Primary:** Customer
- **Secondary:** Search Engine, Recommendation Engine, Notification Service
- **Support:** Analytics Team

**Inputs:**
- Search query (location, price, beds/baths, etc.)
- Filter parameters
- Pagination params
- Favorite property ID
- Search name (for saved searches)

**Outputs:**
- Search results (max 50 per page)
- Property recommendations
- Saved search created
- Favorite added/removed
- Search history recorded

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-CM-020 | Search result limit | Max 50 per page, default 20 |
| BR-CM-021 | Result cache TTL | Cache results for 5 minutes |
| BR-CM-022 | Favorite limit | Max 500 per customer |
| BR-CM-023 | Saved search limit | Max 20 per customer |
| BR-CM-024 | Duplicate favorite | Cannot add same property twice |
| BR-CM-025 | Price range validity | Min ≤ Max, reasonable ranges |
| BR-CM-026 | Search history retention | Keep 90 days of history |
| BR-CM-027 | Personalization | Apply customer preferences to results |

**Process Flow - Search (BPMN):**

```
START: Customer Search
  │
  ├─ [1] Receive Search Request
  │   ├─ Parse location
  │   ├─ Extract price range
  │   ├─ Get property types
  │   ├─ Get additional filters
  │   └─ Extract pagination params
  │
  ├─ [2] Validate Search Parameters
  │   ├─ Check location validity (geocode)
  │   ├─ Validate price range (min ≤ max)
  │   ├─ Verify property types
  │   ├─ Check pagination (limit ≤ 100)
  │   └─ Apply rate limiting
  │
  ├─ [DECISION] Valid Search?
  │   ├─ NO → Return validation error → END
  │   └─ YES ↓
  │
  ├─ [3] Check Search Cache
  │   ├─ Generate cache key from params
  │   ├─ Query Redis cache
  │   └─ Return if fresh (< 5 min old)
  │
  ├─ [4] Search Database
  │   ├─ Query properties matching criteria
  │   ├─ Apply geographic filters
  │   ├─ Apply price filters
  │   ├─ Apply property type filters
  │   ├─ Apply availability filters
  │   └─ Sort by relevance/price/date
  │
  ├─ [5] Apply Personalization
  │   ├─ Load customer preferences
  │   ├─ Boost preferred locations
  │   ├─ Highlight properties in saved searches
  │   ├─ Mark customer favorites
  │   └─ Add recommendation scores
  │
  ├─ [6] Paginate Results
  │   ├─ Calculate total count
  │   ├─ Apply offset/limit
  │   ├─ Get result subset
  │   └─ Include pagination info
  │
  ├─ [7] Enrich Results
  │   ├─ Load property details
  │   ├─ Add agent information
  │   ├─ Include image thumbnails
  │   ├─ Add market statistics
  │   └─ Include comparison data
  │
  ├─ [8] Cache Results
  │   ├─ Store in Redis (5 min TTL)
  │   └─ Compress for efficiency
  │
  ├─ [9] Log Search Event
  │   ├─ Record search query
  │   ├─ Timestamp search
  │   ├─ Note result count
  │   └─ Identify customer (if authenticated)
  │
  ├─ [10] Return Results
  │   ├─ Properties array
  │   ├─ Total count
  │   ├─ Pagination info
  │   └─ Search timestamp
  │
  └─ END
```

**Process Flow - Add Favorite (BPMN):**

```
START: Add Favorite
  │
  ├─ [1] Authenticate Customer
  │   └─ Verify bearer token
  │
  ├─ [2] Receive Favorite Request
  │   ├─ Get property ID
  │   └─ Timestamp request
  │
  ├─ [3] Validate Property
  │   ├─ Verify property exists
  │   └─ Check property is active
  │
  ├─ [4] Check Duplicate
  │   └─ Query favorites table
  │
  ├─ [DECISION] Already Favorited?
  │   ├─ YES → Return duplicate error → END
  │   └─ NO ↓
  │
  ├─ [5] Check Favorite Limit
  │   └─ Verify < 500 favorites
  │
  ├─ [DECISION] Limit Exceeded?
  │   ├─ YES → Return limit error → END
  │   └─ NO ↓
  │
  ├─ [6] Create Favorite Record
  │   ├─ Generate favorite ID
  │   ├─ Store in database
  │   ├─ Set timestamp
  │   └─ Log action
  │
  ├─ [7] Invalidate Cache
  │   └─ Clear customer's favorites from cache
  │
  ├─ [8] Update Search Index
  │   ├─ Add to customer's saved properties
  │   └─ Trigger recommendations refresh
  │
  ├─ [9] Return Success
  │   └─ Confirm favorite added
  │
  └─ END
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Search Performance | Query response time | < 1 second |
| Result Relevance | Customer satisfaction with results | ≥ 85% |
| Cache Hit Rate | Cache hits vs total searches | ≥ 80% |
| Favorite Accuracy | Correct property saved | 100% |
| Duplicate Prevention | Duplicate favorites | 0 |
| Search History Completeness | All searches logged | 100% |
| Personalization Effectiveness | Clicks on personalized results | ≥ 30% lift |

---

### 1.4 Customer Account Closure

**Process ID:** PROC-CM-004  
**Process Name:** Customer Account Closure  
**Process Category:** Customer Management  
**Process Owner:** Customer Success Team  
**Version:** 1.0

**Business Description:**
Graceful account closure with data retention, GDPR compliance, and communication.

**Actors:**
- **Primary:** Customer
- **Secondary:** System (Automated), Compliance Team, Finance
- **Support:** Customer Support Agent

**Inputs:**
- Customer ID
- Reason for closure
- Feedback (optional)
- Password confirmation
- Data retention preference

**Outputs:**
- Account marked for deletion
- 14-day grace period initiated
- Data archived
- Confirmation email sent
- Audit log entry

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-CM-030 | Grace period | 14 days before final deletion |
| BR-CM-031 | Active transaction check | Cannot delete with pending offers |
| BR-CM-032 | Password verification | Must confirm identity |
| BR-CM-033 | Data retention | Archive data for 3 years |
| BR-CM-034 | GDPR compliance | Honor right to be forgotten |
| BR-CM-035 | Cancellation notification | Notify within 24 hours |
| BR-CM-036 | Subscription handling | Auto-cancel active subscriptions |
| BR-CM-037 | Refund policy | Apply cancellation terms |

**Process Flow (BPMN):**

```
START: Customer Deletion Request
  │
  ├─ [1] Authenticate Customer
  │   ├─ Verify bearer token
  │   └─ Confirm password
  │
  ├─ [2] Retrieve Account Details
  │   ├─ Load customer record
  │   ├─ Check for active subscriptions
  │   ├─ Check for pending transactions
  │   └─ Get account balance
  │
  ├─ [3] Validate Closure Eligibility
  │   ├─ Check for active offers
  │   ├─ Check for pending deals
  │   ├─ Check for open disputes
  │   └─ Check payment status
  │
  ├─ [DECISION] Can Delete?
  │   ├─ NO → List blocking issues → END
  │   └─ YES ↓
  │
  ├─ [4] Process Refunds (if applicable)
  │   ├─ Calculate refund amount
  │   ├─ Initiate refund transaction
  │   ├─ Set refund status
  │   └─ Log refund
  │
  ├─ [5] Handle Active Subscriptions
  │   ├─ Retrieve subscriptions
  │   ├─ For each subscription:
  │   │   ├─ Cancel subscription
  │   │   ├─ Disable auto-renew
  │   │   └─ Pro-rate refund if applicable
  │   └─ Send subscription cancellation notices
  │
  ├─ [6] Create Deletion Request Record
  │   ├─ Generate request ID
  │   ├─ Set deletion status = "pending_grace_period"
  │   ├─ Set scheduled deletion date (14 days)
  │   ├─ Store reason and feedback
  │   └─ Record deletion request timestamp
  │
  ├─ [7] Archive Customer Data
  │   ├─ Export full customer profile
  │   ├─ Export transaction history
  │   ├─ Export communication logs
  │   ├─ Compress to archive file
  │   ├─ Encrypt archive
  │   ├─ Store in secure backup
  │   └─ Create retention record (3 years)
  │
  ├─ [8] Disable Account Access
  │   ├─ Invalidate all active sessions
  │   ├─ Revoke access tokens
  │   ├─ Invalidate refresh tokens
  │   └─ Set account status = "pending_deletion"
  │
  ├─ [9] Send Grace Period Notification
  │   ├─ Email: Account deletion scheduled
  │   ├─ Include cancellation deadline
  │   ├─ Provide cancellation link
  │   ├─ Offer alternative: suspend instead
  │   └─ Include data download link
  │
  ├─ [10] Create Audit Log
  │   ├─ Log deletion request
  │   ├─ Record reason
  │   ├─ Timestamp action
  │   └─ Note responsible party
  │
  ├─ [11] Return Confirmation
  │   ├─ Confirm deletion scheduled
  │   ├─ Show grace period end date
  │   ├─ Provide data export link
  │   └─ Offer support contact info
  │
  ├─ [ASYNC] Wait for Grace Period
  │   ├─ Schedule deletion job for 14 days
  │   ├─ On day 13: Send reminder email
  │   ├─ Customer can cancel deletion:
  │   │   ├─ Restore account access
  │   │   ├─ Reactivate subscriptions (optional)
  │   │   └─ Return to normal status
  │   └─ On day 14: Proceed to permanent deletion
  │
  ├─ [12] PERMANENT DELETION PROCESS (Day 14)
  │   ├─ Verify no active transactions
  │   ├─ Verify no refunds pending
  │   ├─ Generate final audit report
  │   ├─ Delete user from authentication system
  │   ├─ Delete profile data
  │   ├─ Delete transaction records (except tax compliance)
  │   ├─ Delete personal identifiable information
  │   ├─ Delete from all indices
  │   ├─ Clear related caches
  │   ├─ Archive deletion record
  │   └─ Log permanent deletion
  │
  └─ END
```

**Grace Period Management:**

```
Day 0: Deletion Requested
├─ Account disabled
├─ Confirmation email sent
└─ Grace period begins

Day 1-13: Grace Period Active
├─ Account inaccessible (can restore)
├─ Data retained
└─ Customer can cancel anytime

Day 13: Final Reminder
├─ Email: Deletion happening tomorrow
├─ Last chance to restore
└─ Provide restore link

Day 14: Permanent Deletion
├─ Execute deletion
├─ Confirm with final email
├─ Archive deletion proof
└─ Update audit trail
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Deletion Completion | % requests processed successfully | 100% |
| Grace Period Enforcement | Premature deletion prevented | 0 incidents |
| Data Archival | All data properly archived | 100% |
| GDPR Compliance | Deletion audit trail complete | 100% |
| Notification Delivery | Confirmation emails sent | ≥ 98% |
| Restoration Success | If customer restores within grace | 100% |

---

## 2. PROPERTY MANAGEMENT PROCESSES

### 2.1 Property Listing Creation

**Process ID:** PROC-PM-001  
**Process Name:** Property Listing Creation  
**Process Category:** Property Management  
**Process Owner:** Agent Operations Team  
**Version:** 1.0

**Business Description:**
Complete workflow for agents to create and publish property listings.

**Actors:**
- **Primary:** Real Estate Agent
- **Secondary:** System (Automated), MLS Integration, Syndication Service
- **Support:** Broker, Compliance

**Inputs:**
- Property address
- Property details (beds, baths, sqft, year built)
- Pricing information
- Description and features
- Photos and media
- Virtual tour URL
- Floor plan URL

**Outputs:**
- Active MLS listing
- Syndicated to portals
- Search index updated
- Listing created in system
- Confirmation sent

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-PM-001 | Address validation | Must geocode to valid address |
| BR-PM-002 | Agent authorization | Agent must have active license |
| BR-PM-003 | Property uniqueness | No duplicate listings same address |
| BR-PM-004 | Required fields | Address, price, beds/baths mandatory |
| BR-PM-005 | Image requirements | Min 1, max 50 images, min 800x600 px |
| BR-PM-006 | Price validation | Positive number, reasonable range |
| BR-PM-007 | MLS number assignment | Auto-generated within 1 hour |
| BR-PM-008 | Syndication timing | Syndicate within 2 hours |

**Process Flow (BPMN):**

```
START: Agent Creates Listing
  │
  ├─ [1] Agent Authentication
  │   ├─ Verify agent credentials
  │   ├─ Check agent is active
  │   └─ Verify license status
  │
  ├─ [2] Receive Listing Data
  │   ├─ Get property address
  │   ├─ Get property details
  │   ├─ Get pricing info
  │   ├─ Get description
  │   ├─ Get features
  │   └─ Receive media files
  │
  ├─ [3] Validate Listing Data
  │   ├─ Check address format
  │   ├─ Verify all required fields
  │   ├─ Validate price (positive, reasonable)
  │   ├─ Check bed/bath count (non-negative)
  │   ├─ Validate square footage
  │   ├─ Check year built (1800-2026)
  │   ├─ Validate description (min 50 chars)
  │   └─ Verify terms acceptance
  │
  ├─ [DECISION] Data Valid?
  │   ├─ NO → Return validation errors → END
  │   └─ YES ↓
  │
  ├─ [4] Geocode Address
  │   ├─ Call Google Maps API
  │   ├─ Get latitude/longitude
  │   ├─ Validate address components
  │   ├─ Normalize address format
  │   └─ Extract city/state/zip
  │
  ├─ [DECISION] Address Valid?
  │   ├─ NO → Return address error → END
  │   └─ YES ↓
  │
  ├─ [5] Check Property Uniqueness
  │   ├─ Search existing listings
  │   ├─ Query by coordinates (±0.001 miles)
  │   ├─ Query by full address
  │   └─ Check archived listings
  │
  ├─ [DECISION] Duplicate Found?
  │   ├─ YES → Warn agent, allow force create
  │   └─ NO ↓
  │
  ├─ [6] Process Images
  │   ├─ For each image:
  │   │   ├─ Validate format (jpg/png)
  │   │   ├─ Check file size (max 5MB)
  │   │   ├─ Verify dimensions (min 800x600)
  │   │   ├─ Compress image
  │   │   ├─ Generate thumbnail
  │   │   ├─ Upload to S3
  │   │   ├─ Get CDN URL
  │   │   └─ Store URL in database
  │   └─ Set image order
  │
  ├─ [7] Validate Media URLs
  │   ├─ If virtual tour provided:
  │   │   └─ Verify URL is accessible
  │   ├─ If floor plan provided:
  │   │   └─ Verify PDF is valid
  │   └─ Store media URLs
  │
  ├─ [8] Create Listing Record
  │   ├─ Generate Listing ID (PENDING-xxx)
  │   ├─ Begin transaction
  │   ├─ Insert into properties table
  │   ├─ Insert listing details
  │   ├─ Insert images
  │   ├─ Insert features
  │   ├─ Set status = "pending_mls"
  │   ├─ Set created timestamp
  │   ├─ Link to agent
  │   └─ Commit transaction
  │
  ├─ [9] Request MLS Number
  │   ├─ Queue MLS integration job
  │   ├─ Submit listing data to MLS
  │   ├─ Wait for MLS confirmation
  │   ├─ Receive MLS number
  │   ├─ Update listing record
  │   └─ Set status = "active"
  │
  ├─ [10] Update Search Index
  │   ├─ Index in Elasticsearch
  │   ├─ Add to search cache
  │   ├─ Trigger recommendation updates
  │   └─ Activate for customer searches
  │
  ├─ [11] Queue for Syndication
  │   ├─ Zillow export
  │   ├─ Trulia export
  │   ├─ Realtor.com export
  │   ├─ Facebook Marketplace export
  │   └─ Set syndication job schedule
  │
  ├─ [12] Create Audit Log
  │   ├─ Log listing creation
  │   ├─ Record all details
  │   ├─ Note agent ID
  │   ├─ Timestamp creation
  │   └─ Mark as compliant
  │
  ├─ [13] Send Confirmations
  │   ├─ Email to agent: Listing created
  │   ├─ Include MLS number
  │   ├─ Include listing URL
  │   ├─ Email to broker: New listing notification
  │   ├─ Push notification (if app)
  │   └─ Include next steps
  │
  ├─ [14] Return Success Response
  │   ├─ Provide property ID
  │   ├─ Provide MLS number
  │   ├─ Provide public listing URL
  │   ├─ Provide edit link
  │   └─ Show syndication status
  │
  └─ END
```

**Listing Status Lifecycle:**

```
PENDING_MLS
    ↓ (24h or MLS approved)
ACTIVE
    ├─ (Agent withdraws)
    │   ↓
    │ WITHDRAWN
    │   ↓ (Can reactivate within 90 days)
    │ REACTIVATED → ACTIVE
    │
    ├─ (Deal accepted)
    │   ↓
    │ PENDING_SALE
    │   ↓ (Deal closes)
    │   ↓
    │ SOLD
    │
    └─ (Expires after 180 days)
        ↓
        EXPIRED
            ↓ (Agent renews)
            ↓
            ACTIVE
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Listing Creation Time | From submission to active | < 2 hours |
| MLS Number Assignment | Received from MLS | Within 1 hour |
| Syndication Completion | Listed on all portals | Within 2 hours |
| Search Indexing | Searchable by customers | Within 5 minutes |
| Image Processing | All images processed | 100% |
| Validation Accuracy | Incorrect rejections | < 1% |
| Duplicate Prevention | Duplicate listings | 0 |
| Agent Satisfaction | Agents satisfied with process | ≥ 90% |

---

### 2.2 Property Listing Update

**Process ID:** PROC-PM-002  
**Process Name:** Property Listing Update  
**Process Category:** Property Management  
**Process Owner:** Agent Operations Team  
**Version:** 1.0

**Business Description:**
Allow agents to update listing information and media.

**Actors:**
- **Primary:** Real Estate Agent
- **Secondary:** System (Automated), Search Index, Syndication Service
- **Support:** Broker, Compliance

**Inputs:**
- Listing ID
- Updated fields (price, description, features, images)
- Update reason
- Update timestamp

**Outputs:**
- Updated listing
- Search index refreshed
- Syndication updated
- Price history recorded
- Change notification sent

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-PM-010 | Authorization check | Only agent can update own listing |
| BR-PM-011 | Listing status | Only active listings can be updated |
| BR-PM-012 | Price change tracking | All price changes logged |
| BR-PM-013 | Major change threshold | Price > 10% = major change |
| BR-PM-014 | Update audit trail | All changes timestamped |
| BR-PM-015 | Syndication sync | Updates sent to portals within 1 hour |
| BR-PM-016 | Image limit | Max 50 images per listing |
| BR-PM-017 | Description length | Min 50, max 10000 chars |

**Process Flow (BPMN):**

```
START: Agent Updates Listing
  │
  ├─ [1] Verify Authorization
  │   ├─ Check agent credentials
  │   ├─ Verify agent owns listing
  │   └─ Check listing is active
  │
  ├─ [2] Get Current Listing
  │   ├─ Load from database
  │   └─ Create backup copy
  │
  ├─ [3] Receive Update Payload
  │   ├─ Get changed fields
  │   ├─ Identify field types
  │   └─ Note update reason
  │
  ├─ [4] Validate Changes
  │   ├─ For each changed field:
  │   │   ├─ Check business rules
  │   │   ├─ Verify data format
  │   │   ├─ Validate constraints
  │   │   └─ Flag if major change
  │   └─ Aggregate validation errors
  │
  ├─ [DECISION] Valid Changes?
  │   ├─ NO → Return errors → END
  │   └─ YES ↓
  │
  ├─ [5] Handle Image Changes
  │   ├─ If images added:
  │   │   ├─ Validate format/size
  │   │   ├─ Upload to S3
  │   │   ├─ Generate thumbnails
  │   │   └─ Get CDN URLs
  │   ├─ If images removed:
  │   │   ├─ Delete from S3
  │   │   └─ Delete CDN cache
  │   └─ Update image order
  │
  ├─ [6] Create Audit Entry
  │   ├─ Record all changed fields
  │   ├─ Capture before/after values
  │   ├─ Timestamp change (UTC)
  │   ├─ Note agent ID
  │   ├─ Note update reason
  │   └─ Flag if major change
  │
  ├─ [DECISION] Major Price Change?
  │   ├─ YES → Trigger approval workflow
  │   └─ NO ↓
  │
  ├─ [7] Update Database
  │   ├─ Begin transaction
  │   ├─ Apply updates
  │   ├─ Store price history
  │   ├─ Update modified timestamp
  │   └─ Commit transaction
  │
  ├─ [8] Invalidate Cache
  │   ├─ Clear property cache
  │   ├─ Clear listing cache
  │   └─ Clear search results cache
  │
  ├─ [9] Update Search Index
  │   ├─ Re-index in Elasticsearch
  │   ├─ Update search facets
  │   ├─ Update price facets
  │   └─ Trigger relevance recalc
  │
  ├─ [10] Queue Syndication Update
  │   ├─ Generate syndication payload
  │   ├─ Queue for each portal:
  │   │   ├─ Zillow
  │   │   ├─ Trulia
  │   │   ├─ Realtor.com
  │   │   └─ Others
  │   └─ Set delivery timeout (1 hour)
  │
  ├─ [11] Notify Interested Parties
  │   ├─ Get saved searches matching listing
  │   ├─ Get customers who favorited
  │   ├─ Send price drop alert (if applicable)
  │   ├─ Send description update notification
  │   └─ Queue notifications
  │
  ├─ [12] Send Confirmation
  │   ├─ Email to agent: Update confirmed
  │   ├─ Include all updated fields
  │   └─ Show search status
  │
  ├─ [13] Return Updated Listing
  │   └─ Include current state
  │
  └─ END
```

**Price Change Handling:**

```
Price Change Detection:
├─ New Price vs Old Price
├─ Calculate percentage change
├─ If change > 10%:
│   ├─ Flag as major change
│   ├─ Log price reduction/increase
│   ├─ Track in price history
│   ├─ Create adjustment record
│   ├─ Notify saved search subscribers
│   └─ Update DOM (Days on Market) if reduced
└─ If change ≤ 10%:
    └─ Standard update (no special handling)
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Update Processing | Update completion rate | 99.5% |
| Index Refresh | Search index updated | < 5 minutes |
| Syndication Sync | Updated on external portals | < 1 hour |
| Audit Trail Completeness | All changes logged | 100% |
| Cache Invalidation | No stale results | 100% |
| Notification Delivery | Update notifications sent | ≥ 95% |

---

### 2.3 Property Delisting

**Process ID:** PROC-PM-003  
**Process Name:** Property Delisting  
**Process Category:** Property Management  
**Process Owner:** Agent Operations Team  
**Version:** 1.0

**Business Description:**
Handle listing removal (sold, withdrawn, expired).

**Actors:**
- **Primary:** Agent
- **Secondary:** System, MLS, Syndication Service
- **Support:** Broker

**Inputs:**
- Listing ID
- Status change (sold/withdrawn/expired)
- Sale price (if sold)
- Close date (if sold)
- Reason (if withdrawn)

**Outputs:**
- Listing deactivated
- MLS updated
- Syndication removed
- Search index updated
- Archive created
- Notifications sent

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-PM-020 | Authorization | Only agent can change listing status |
| BR-PM-021 | Sale data required | Sold status requires sale price/date |
| BR-PM-022 | MLS notification | Update MLS within 24 hours |
| BR-PM-023 | Syndication removal | Remove from portals within 4 hours |
| BR-PM-024 | Archive creation | Create listing archive |
| BR-PM-025 | History preservation | Keep listing history 7 years |
| BR-PM-026 | Customer notification | Notify interested customers |

**Process Flow (BPMN):**

```
START: Change Listing Status
  │
  ├─ [1] Authenticate Agent
  │   ├─ Verify credentials
  │   └─ Check owns listing
  │
  ├─ [2] Get Listing Details
  │   ├─ Load from database
  │   └─ Get current status
  │
  ├─ [3] Receive Status Change Request
  │   ├─ Get new status
  │   ├─ Get reason/details
  │   └─ Timestamp request
  │
  ├─ [DECISION] New Status = SOLD?
  │   ├─ YES → [4A] Process Sale
  │   ├─ NO (WITHDRAWN) → [4B] Process Withdrawal
  │   └─ NO (EXPIRED) → [4C] Process Expiration
  │
  ├─ [4A] PROCESS SALE:
  │   ├─ [A1] Validate Sale Data
  │   │   ├─ Verify sale price provided
  │   │   ├─ Verify close date provided
  │   │   ├─ Validate close date >= today
  │   │   └─ Check sale price > 0
  │   ├─ [DECISION] Valid Sale Data?
  │   │   ├─ NO → Return error → END
  │   │   └─ YES ↓
  │   ├─ [A2] Create Sale Record
  │   │   ├─ Record sale price
  │   │   ├─ Record close date
  │   │   ├─ Calculate days on market
  │   │   └─ Store sale date
  │   ├─ [A3] Generate Price History
  │   │   ├─ Track original price
  │   │   ├─ Track list price changes
  │   │   ├─ Calculate price reduction
  │   │   └─ Calculate DOM
  │   └─ Continue to [5]
  │
  ├─ [4B] PROCESS WITHDRAWAL:
  │   ├─ [B1] Get Withdrawal Reason
  │   │   └─ Optional: reason for market
  │   ├─ [B2] Store Withdrawal Details
  │   │   ├─ Record reason
  │   │   ├─ Set withdrawn date
  │   │   └─ Calculate time on market
  │   ├─ [B3] Enable Reactivation Option
  │   │   ├─ Mark as reactivatable
  │   │   ├─ Set reactivation deadline (90 days)
  │   │   └─ Provide reactivation instructions
  │   └─ Continue to [5]
  │
  ├─ [4C] PROCESS EXPIRATION:
  │   ├─ [C1] Calculate Listing Age
  │   │   ├─ Get original list date
  │   │   ├─ Calculate days elapsed
  │   │   ├─ Verify >= 180 days
  │   │   └─ Check auto-expiration enabled
  │   ├─ [DECISION] Valid Expiration?
  │   │   ├─ NO → Return error → END
  │   │   └─ YES ↓
  │   ├─ [C2] Create Expiration Record
  │   │   ├─ Record expiration date
  │   │   ├─ Note total DOM
  │   │   └─ Store expiration stats
  │   └─ Continue to [5]
  │
  ├─ [5] CREATE AUDIT LOG ENTRY
  │   ├─ Record status change
  │   ├─ Store reason/details
  │   ├─ Timestamp change
  │   ├─ Note agent ID
  │   └─ Log compliance check
  │
  ├─ [6] ARCHIVE LISTING DATA
  │   ├─ Create archive record
  │   ├─ Export all listing data
  │   ├─ Export transaction history
  │   ├─ Export agent interactions
  │   ├─ Compress archive file
  │   ├─ Encrypt and store
  │   └─ Set retention: 7 years
  │
  ├─ [7] UPDATE LISTING STATUS
  │   ├─ Begin transaction
  │   ├─ Update status field
  │   ├─ Store status timestamp
  │   ├─ Disable further editing
  │   └─ Commit transaction
  │
  ├─ [8] REMOVE FROM SEARCH
  │   ├─ Delete from Elasticsearch
  │   ├─ Clear from cache
  │   ├─ Remove from customer searches
  │   └─ Update saved search results
  │
  ├─ [9] NOTIFY MLS
  │   ├─ Queue MLS update job
  │   ├─ Submit status change
  │   ├─ Include status details
  │   ├─ Wait for confirmation
  │   └─ Store MLS acknowledgment
  │
  ├─ [10] QUEUE SYNDICATION REMOVAL
  │   ├─ Queue removal for:
  │   │   ├─ Zillow
  │   │   ├─ Trulia
  │   │   ├─ Realtor.com
  │   │   └─ Other portals
  │   └─ Set removal timeout: 4 hours
  │
  ├─ [11] NOTIFY STAKEHOLDERS
  │   ├─ Email to agent: Status updated
  │   ├─ Email to broker: Listing removed
  │   ├─ Find customers who:
  │   │   ├─ Favorited listing
  │   │   ├─ Viewed recently
  │   │   └─ Triggered by saved search
  │   ├─ If SOLD:
  │   │   └─ Send sale notification
  │   ├─ If WITHDRAWN:
  │   │   └─ Send "Listing Withdrawn" notice
  │   └─ If EXPIRED:
  │       └─ Send "Listing Expired" notice
  │
  ├─ [12] UPDATE AGENT STATS
  │   ├─ Calculate performance metrics
  │   ├─ Update sold count (if sold)
  │   ├─ Update average DOM
  │   ├─ Update average sale price
  │   └─ Store in agent profile
  │
  ├─ [13] RETURN CONFIRMATION
  │   ├─ Confirm status change
  │   ├─ Show new status
  │   ├─ Show details
  │   └─ Provide archive link
  │
  └─ END
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Status Update Completion | Successfully updated | 99.9% |
| MLS Notification | Notified within 24h | 100% |
| Syndication Removal | Removed from portals | 100% within 4h |
| Search Index Update | Removed from search | < 5 minutes |
| Archive Creation | Complete archive created | 100% |
| Notification Delivery | Stakeholders notified | ≥ 95% |

---

## 3. VERIFICATION SERVICES PROCESSES

### 3.1 Email Verification

**Process ID:** PROC-VS-001  
**Process Name:** Email Verification  
**Process Category:** Verification Services  
**Process Owner:** Security Team  
**Version:** 1.0

**Business Description:**
Verify email ownership through token-based confirmation.

**Actors:**
- **Primary:** User
- **Secondary:** Email Service, System
- **Support:** Support Team

**Inputs:**
- Email address
- Verification token (from email link)

**Outputs:**
- Email marked as verified
- User account updated
- Verification timestamp recorded
- Status notification sent

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-VS-001 | Token expiration | Tokens expire after 24 hours |
| BR-VS-002 | Single use tokens | Token invalidated after first use |
| BR-VS-003 | Max attempts | Max 5 verification attempts |
| BR-VS-004 | Token generation | Cryptographically secure (32 bytes) |
| BR-VS-005 | Resend limit | Max 3 resend requests per email |
| BR-VS-006 | Email format | Must be valid RFC 5322 format |
| BR-VS-007 | Rate limiting | 1 verification per minute per email |

**Process Flow (BPMN):**

```
START: Email Verification
  │
  ├─ INITIAL FLOW (Registration):
  │
  ├─ [1] User Completes Registration
  │   └─ Email captured in registration form
  │
  ├─ [2] Generate Verification Token
  │   ├─ Create secure random token (32 bytes)
  │   ├─ Encode to base64url
  │   ├─ Store in cache with:
  │   │   ├─ Email address
  │   │   ├─ User ID
  │   │   ├─ Expiration timestamp (24h)
  │   │   ├─ Used flag (initially false)
  │   │   └─ Attempt counter (0)
  │   └─ Set TTL = 24 hours
  │
  ├─ [3] Send Verification Email
  │   ├─ Compose HTML email template
  │   ├─ Include verification link:
  │   │   └─ https://propertyilot.com/verify?token={token}
  │   ├─ Add fallback: 6-digit code
  │   ├─ Queue email to service
  │   └─ Log email sent event
  │
  ├─ [4] User Clicks Verification Link
  │   ├─ Link redirects to:
  │   │   └─ /auth/verify-email?token={token}
  │   ├─ Browser sends GET request
  │   └─ Navigate to verification page
  │
  ├─ ============================================
  ├─ VERIFICATION FLOW:
  ├─ ============================================
  │
  ├─ [5] Receive Verification Token
  │   ├─ Extract token from query param
  │   ├─ Decode from base64url
  │   └─ Timestamp verification attempt
  │
  ├─ [6] Validate Token
  │   ├─ Check token exists in cache
  │   ├─ Check token not expired
  │   ├─ Check token not previously used
  │   └─ Increment attempt counter
  │
  ├─ [DECISION] Token Valid?
  │   ├─ NO → [7A] Handle Invalid Token
  │   └─ YES ↓
  │
  ├─ [7] Mark Email as Verified
  │   ├─ Load user record
  │   ├─ Update verification status
  │   ├─ Store verification timestamp
  │   ├─ Mark user as email_verified
  │   └─ Commit to database
  │
  ├─ [8] Invalidate Token
  │   ├─ Set used flag = true
  │   ├─ Delete from cache
  │   ├─ Create used token record
  │   └─ Store in audit log
  │
  ├─ [9] Update User Status
  │   ├─ If email_verified AND phone_verified:
  │   │   ├─ Set account_status = "active"
  │   │   └─ Grant full access
  │   └─ Clear verification-related restrictions
  │
  ├─ [10] Send Confirmation Email
  │   ├─ Subject: "Email Confirmed"
  │   ├─ Message: "Your email has been verified"
  │   ├─ Include next steps
  │   └─ Queue for delivery
  │
  ├─ [11] Return Success Page
  │   ├─ Display: "Email verified!"
  │   ├─ Show status: ✓ Email
  │   ├─ Show next steps
  │   ├─ Offer: Continue to app
  │   └─ Auto-redirect after 5 seconds
  │
  └─ END: Success
  │
  ├─ ============================================
  ├─ ERROR HANDLING:
  ├─ ============================================
  │
  ├─ [7A] Handle Invalid Token
  │   ├─ [DECISION] Why Invalid?
  │   │
  │   ├─ IF Token Expired:
  │   │   ├─ Offer: Request new verification email
  │   │   ├─ Button: "Resend Verification Email"
  │   │   ├─ Generate new token
  │   │   ├─ Send new email
  │   │   └─ Return: 401 Expired Token
  │   │
  │   ├─ IF Token Not Found:
  │   │   ├─ Check attempt count
  │   │   ├─ Log suspicious activity
  │   │   ├─ Return: 404 Not Found
  │   │   └─ Suggest: Re-register or contact support
  │   │
  │   ├─ IF Token Already Used:
  │   │   ├─ Check if same user
  │   │   ├─ If yes: Email already verified
  │   │   ├─ If no: Security alert
  │   │   ├─ Return: 409 Already Used
  │   │   └─ Require: Re-authentication
  │   │
  │   └─ IF Too Many Attempts:
  │       ├─ Increment attempt counter
  │       ├─ If attempts > 5:
  │       │   ├─ Invalidate token
  │       │   ├─ Require: User to request new link
  │       │   ├─ Send: Security notice to email
  │       │   └─ Return: 429 Too Many Attempts
  │       └─ Log failed verification
  │
  └─ END: Error Handling

RESEND VERIFICATION FLOW:

[R1] User Requests Resend
├─ Provide email address
└─ Click "Resend Verification Email"

[R2] Validate Resend Request
├─ Check email exists
├─ Check not already verified
├─ Check < 3 resend requests
├─ Check rate limit (1/minute)
└─ Increment resend counter

[R3] Generate New Token
├─ Create new secure token
├─ Store with new expiration
└─ Invalidate old token

[R4] Send New Email
├─ Send verification email
├─ Include new token
└─ Log resend event

[R5] Return Confirmation
├─ Message: "Email sent to..."
├─ Show: "Check your inbox"
└─ Offer: "Wait/Try again"
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Verification Completion | Emails verified within 24h | ≥ 92% |
| Token Security | Valid tokens only | 100% |
| False Rejections | Incorrectly rejected valid tokens | 0 |
| Email Delivery | Verification emails delivered | ≥ 98% |
| Performance | Verification processing time | < 500ms |
| Security | Compromised tokens | 0 |

---

### 3.2 Phone Number Verification (SMS)

**Process ID:** PROC-VS-002  
**Process Name:** Phone Number Verification  
**Process Category:** Verification Services  
**Process Owner:** Security Team  
**Version:** 1.0

**Business Description:**
Verify phone number ownership through SMS-based OTP.

**Actors:**
- **Primary:** User
- **Secondary:** SMS Service, System
- **Support:** Support Team

**Inputs:**
- Phone number (E.164 format)
- Country code
- OTP code (from SMS)

**Outputs:**
- Phone marked as verified
- User account updated
- Verification timestamp recorded
- Success notification

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-VS-010 | OTP length | 6 digits |
| BR-VS-011 | OTP expiration | Expires after 10 minutes |
| BR-VS-012 | OTP attempts | Max 5 verification attempts |
| BR-VS-013 | OTP uniqueness | Cannot reuse OTP |
| BR-VS-014 | SMS delivery | Via reputable SMS provider |
| BR-VS-015 | Rate limiting | 1 SMS per minute per number |
| BR-VS-016 | Resend limit | Max 3 SMS sends per verification |
| BR-VS-017 | Phone format | E.164 format required |

**Process Flow (BPMN):**

```
START: Phone Verification
  │
  ├─ [1] User Provides Phone Number
  │   ├─ Enter phone in E.164 format
  │   ├─ Select country (auto-detect from code)
  │   └─ Click "Send Verification Code"
  │
  ├─ [2] Validate Phone Number
  │   ├─ Check E.164 format
  │   ├─ Verify country code valid
  │   ├─ Validate number length
  │   └─ Check not already verified
  │
  ├─ [DECISION] Phone Valid?
  │   ├─ NO → Return error → END
  │   └─ YES ↓
  │
  ├─ [3] Check Rate Limiting
  │   ├─ Query recent SMS sends
  │   ├─ Check < 1 per minute
  │   ├─ Check < 3 total sends
  │   └─ Check not on blocklist
  │
  ├─ [DECISION] Rate Limit OK?
  │   ├─ NO → Return rate limit error → END
  │   └─ YES ↓
  │
  ├─ [4] Generate OTP Code
  │   ├─ Generate 6-digit random code
  │   ├─ Avoid predictable patterns
  │   ├─ Store in cache with:
  │   │   ├─ Phone number
  │   │   ├─ User ID
  │   │   ├─ Expiration (10 min)
  │   │   ├─ Attempt counter (0)
  │   │   └─ Used flag (false)
  │   └─ Set TTL = 10 minutes
  │
  ├─ [5] Send SMS
  │   ├─ Compose SMS message:
  │   │   └─ "Your PropertyPilot verification code: XXXXXX"
  │   ├─ Submit to SMS provider
  │   ├─ Receive delivery confirmation
  │   ├─ Log SMS sent event
  │   └─ Start delivery timeout (30 sec)
  │
  ├─ [DECISION] SMS Sent Successfully?
  │   ├─ NO → Retry logic → [5 Retry]
  │   └─ YES ↓
  │
  ├─ [6] Display Verification Page
  │   ├─ Show phone number (masked)
  │   ├─ Show OTP input field
  │   ├─ Show countdown timer (10 min)
  │   ├─ Show "Resend Code" link
  │   └─ Auto-focus OTP input
  │
  ├─ [7] User Enters OTP Code
  │   ├─ User types 6 digits
  │   ├─ Auto-submit when 6 digits entered
  │   └─ Timestamp OTP submission
  │
  ├─ [8] Validate OTP Code
  │   ├─ Retrieve stored OTP
  │   ├─ Check OTP not expired
  │   ├─ Check OTP not used
  │   ├─ Compare with user input
  │   ├─ Increment attempt counter
  │   └─ Check attempts < 5
  │
  ├─ [DECISION] OTP Valid?
  │   ├─ NO → [9A] Handle Invalid OTP
  │   └─ YES ↓
  │
  ├─ [9] Mark Phone as Verified
  │   ├─ Load user record
  │   ├─ Update phone_verified flag
  │   ├─ Store verification timestamp
  │   ├─ Store verified phone number
  │   └─ Commit to database
  │
  ├─ [10] Invalidate OTP
  │   ├─ Set used flag = true
  │   ├─ Delete from cache
  │   ├─ Create used OTP record
  │   └─ Log in audit trail
  │
  ├─ [11] Send Confirmation SMS
  │   ├─ Message: "Phone verified successfully"
  │   ├─ Include: Date/time verified
  │   └─ Queue for delivery
  │
  ├─ [12] Return Success
  │   ├─ Display: "Phone verified! ✓"
  │   ├─ Show status: ✓ Phone
  │   ├─ Auto-redirect after 3 seconds
  │   └─ Offer: Continue to app
  │
  └─ END: Success
  │
  ├─ ============================================
  ├─ ERROR HANDLING:
  ├─ ============================================
  │
  ├─ [9A] Handle Invalid OTP
  │   ├─ [DECISION] Why Invalid?
  │   │
  │   ├─ IF OTP Expired:
  │   │   ├─ Show error: "Code expired"
  │   │   ├─ Enable resend button
  │   │   ├─ Offer: Generate new code
  │   │   └─ Return: 401 Expired
  │   │
  │   ├─ IF OTP Incorrect:
  │   │   ├─ Show error: "Invalid code"
  │   │   ├─ Show attempts remaining
  │   │   ├─ If attempts < 2:
  │   │   │   ├─ Warn: "Limited attempts"
  │   │   │   └─ Offer: Resend code
  │   │   └─ Return: 403 Invalid
  │   │
  │   ├─ IF OTP Already Used:
  │   │   ├─ Check if same user
  │   │   ├─ If same: Show "Already verified"
  │   │   ├─ If different: Security alert
  │   │   └─ Return: 409 Already Used
  │   │
  │   └─ IF Too Many Attempts:
  │       ├─ Block further attempts
  │       ├─ Require new SMS
  │       ├─ Send security alert SMS
  │       ├─ Log suspicious activity
  │       └─ Return: 429 Too Many Attempts
  │
  ├─ [5 Retry] SMS Delivery Failure
  │   ├─ Retry up to 3 times
  │   ├─ Use exponential backoff
  │   ├─ If all fail:
  │   │   ├─ Show error: "Could not send SMS"
  │   │   ├─ Offer: Try again or use alternative
  │   │   └─ Return: 503 Service Error
  │   └─ Log failure for support
  │
  └─ END: Error Handling

RESEND OTP FLOW:

[R1] User Clicks "Resend Code"
├─ Check < 3 resend requests
├─ Check rate limit (1/minute)
└─ Increment resend counter

[R2] Generate New OTP
├─ Create new 6-digit code
├─ Store with new 10-min expiration
├─ Invalidate old OTP
└─ Log resend event

[R3] Send New SMS
├─ Submit to SMS provider
├─ Log delivery
└─ Return confirmation

[R4] Display Notification
├─ Message: "Code sent to..."
├─ Show: "Check your messages"
├─ Reset: Input field & timer
└─ Offer: "Wait or try again"
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| SMS Delivery Rate | OTP SMS delivered | ≥ 99% |
| Verification Success | Correct OTP accepted | 100% |
| OTP Security | Invalid OTPs rejected | 100% |
| False Rejections | Valid OTPs rejected | 0 |
| Performance | Verification processing | < 500ms |
| User Experience | Average time to verify | < 3 minutes |

---

### 3.3 Identity Verification (KYC)

**Process ID:** PROC-VS-003  
**Process Name:** Identity Verification (KYC)  
**Process Category:** Verification Services  
**Process Owner:** Compliance Team  
**Version:** 1.0

**Business Description:**
Know Your Customer (KYC) verification for regulatory compliance.

**Actors:**
- **Primary:** User
- **Secondary:** Third-party KYC Service, System, Compliance Team
- **Support:** Support Team

**Inputs:**
- Document type (driver's license, passport, national ID)
- Document number
- Name (from document)
- Date of birth
- Document expiration date
- Selfie (optional for enhanced verification)

**Outputs:**
- Verification status (verified/pending/failed)
- KYC record created
- Compliance record stored
- User notified
- Audit log entry

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-VS-020 | Age requirement | Minimum 18 years |
| BR-VS-021 | Document validity | Must be unexpired |
| BR-VS-022 | Document format | Valid government-issued ID only |
| BR-VS-023 | Name match | First/last name must match document |
| BR-VS-024 | Review period | Manual review within 2 business days |
| BR-VS-025 | Compliance retention | Keep KYC records 7 years |
| BR-VS-026 | Privacy | Encrypt PII in storage |
| BR-VS-027 | Retry limit | Max 3 KYC attempts per user |

**Process Flow (BPMN):**

```
START: KYC Verification
  │
  ├─ [1] User Initiates KYC
  │   ├─ Click "Verify Identity"
  │   ├─ Accept KYC disclosure
  │   └─ Select document type
  │
  ├─ [2] Select Document Type
  │   ├─ Options:
  │   │   ├─ Driver's License
  │   │   ├─ Passport
  │   │   ├─ National ID
  │   │   └─ State ID
  │   ├─ Show: Accepted countries
  │   └─ Continue to document input
  │
  ├─ [3] Enter Document Details
  │   ├─ Document number
  │   ├─ First name
  │   ├─ Last name
  │   ├─ Date of birth
  │   ├─ Expiration date
  │   └─ Issuing country
  │
  ├─ [4] Validate Input
  │   ├─ Check document number format
  │   ├─ Verify DOB format (YYYY-MM-DD)
  │   ├─ Check expiration in future
  │   ├─ Validate country code
  │   └─ Check name (50 chars max)
  │
  ├─ [DECISION] Input Valid?
  │   ├─ NO → Return validation errors → END
  │   └─ YES ↓
  │
  ├─ [5] Calculate Age
  │   ├─ Calculate age from DOB
  │   ├─ Check >= 18 years
  │   └─ Store age verification result
  │
  ├─ [DECISION] Age >= 18?
  │   ├─ NO → Return age error → END
  │   └─ YES ↓
  │
  ├─ [6] Optional: Selfie Collection
  │   ├─ Offer: "Take a selfie for enhanced verification"
  │   ├─ Access camera
  │   ├─ Capture selfie photo
  │   ├─ Compress image
  │   ├─ Upload to secure storage
  │   └─ Continue without (optional)
  │
  ├─ [7] Create KYC Request Record
  │   ├─ Generate request ID
  │   ├─ Store all provided data (encrypted)
  │   ├─ Set status = "pending_review"
  │   ├─ Timestamp submission
  │   ├─ Store attempt number
  │   └─ Link to user account
  │
  ├─ [8] Submit to Third-Party KYC Service
  │   ├─ Prepare API payload
  │   ├─ Sanitize PII
  │   ├─ Submit to verification provider
  │   ├─ Receive request ID from provider
  │   ├─ Store provider request ID
  │   └─ Log submission
  │
  ├─ [9] Display Pending Page
  │   ├─ Message: "Verification in progress"
  │   ├─ Explain: "Usually 2-3 business days"
  │   ├─ Offer: "Check status in profile"
  │   ├─ Email: Provide updates
  │   └─ Auto-refresh: Show updates
  │
  ├─ [ASYNC] Wait for Verification Result
  │   ├─ Provider returns result (async webhook)
  │   ├─ Statuses: verified, manual_review, failed
  │   └─ Continue to [10]
  │
  ├─ [10] PROCESS VERIFICATION RESULT
  │   ├─ [DECISION] Result Status?
  │   │
  │   ├─ IF VERIFIED:
  │   │   ├─ [10A] Mark as Verified
  │   │   ├─ Set status = "verified"
  │   │   ├─ Store verification timestamp
  │   │   ├─ Enable restricted features
  │   │   ├─ Update user permissions
  │   │   ├─ Send confirmation email
  │   │   └─ Continue to [13]
  │   │
  │   ├─ IF MANUAL_REVIEW:
  │   │   ├─ [10B] Queue for Manual Review
  │   │   ├─ Set status = "pending_manual_review"
  │   │   ├─ Assign to compliance reviewer
  │   │   ├─ Create review task
  │   │   ├─ Set deadline (2 business days)
  │   │   ├─ Email: Inform user
  │   │   └─ Reviewer will contact if needed
  │   │
  │   └─ IF FAILED:
  │       ├─ [10C] Handle Failed Verification
  │       ├─ Set status = "failed"
  │       ├─ Store failure reason
  │       ├─ Check attempt count
  │       ├─ If attempts < 3:
  │       │   ├─ Allow retry
  │       │   ├─ Email: Reason for failure
  │       │   ├─ Show: Retry instructions
  │       │   └─ Offer: Alternative verification
  │       ├─ If attempts >= 3:
  │       │   ├─ Block further attempts
  │       │   ├─ Escalate to support
  │       │   └─ Require manual intervention
  │       └─ Log failed verification
  │
  ├─ [11] MANUAL REVIEW PROCESS (if triggered)
  │   ├─ Compliance officer receives task
  │   ├─ Review submitted documents
  │   ├─ Check name/DOB match document
  │   ├─ Verify document authenticity
  │   ├─ Review selfie if provided
  │   ├─ Make approval decision
  │   ├─ Document review notes
  │   └─ Continue to [12]
  │
  ├─ [12] REVIEWER DECISION
  │   ├─ [DECISION] Approved?
  │   │
  │   ├─ YES → Mark as verified → [10A]
  │   └─ NO → Mark as rejected
  │       ├─ Store rejection reason
  │       ├─ Email user detailed reason
  │       ├─ Offer: Appeal process
  │       ├─ Log rejection
  │       └─ END
  │
  ├─ [13] SEND VERIFICATION CONFIRMATION
  │   ├─ Email: "Identity verified!"
  │   ├─ Include: Verification date
  │   ├─ Note: Features now available
  │   ├─ Push notification (if app)
  │   └─ Log confirmation sent
  │
  ├─ [14] UPDATE USER PERMISSIONS
  │   ├─ Grant: Make offers feature
  │   ├─ Grant: Schedule viewings
  │   ├─ Grant: Commission payouts
  │   ├─ Enable: Restricted features
  │   └─ Store permission timestamp
  │
  ├─ [15] CREATE COMPLIANCE RECORD
  │   ├─ Store verification details
  │   ├─ Encrypt sensitive data
  │   ├─ Set retention: 7 years
  │   ├─ Create audit log entry
  │   └─ Generate compliance report
  │
  ├─ [16] Return Success
  │   ├─ Display: "Identity verified! ✓"
  │   ├─ Show: Verification date
  │   ├─ Offer: Continue to app
  │   └─ Auto-redirect
  │
  └─ END
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Verification Accuracy | Correct verifications | ≥ 99.5% |
| Turnaround Time | Verified within 2 business days | ≥ 95% |
| Manual Review Rate | Cases requiring manual review | ≤ 5% |
| False Positives | Incorrectly rejected valid IDs | ≤ 0.5% |
| Compliance Completeness | Records retained 7 years | 100% |
| User Satisfaction | Acceptable verification process | ≥ 90% |

---

## 4. MONITORING SERVICES PROCESSES

### 4.1 System Health Monitoring

**Process ID:** PROC-MON-001  
**Process Name:** System Health Monitoring  
**Process Category:** Monitoring Services  
**Process Owner:** DevOps Team  
**Version:** 1.0

**Business Description:**
Continuous monitoring of system health and automated alerting.

**Actors:**
- **Primary:** System (Automated)
- **Secondary:** Monitoring Tools, Alert Service, On-call Team
- **Support:** DevOps Engineers

**Inputs:**
- Health check metrics
- Performance data
- Error logs
- Resource utilization

**Outputs:**
- Health status dashboard
- Alerts (if thresholds exceeded)
- Incident tickets
- Notifications sent
- Metrics reported

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-MON-001 | Health check interval | Every 30 seconds |
| BR-MON-002 | Service threshold | Unavailable > 1 min = alert |
| BR-MON-003 | Response time SLA | P95 < 500ms (API), < 2s (UI) |
| BR-MON-004 | Error rate threshold | > 0.1% = alert |
| BR-MON-005 | Alert escalation | Page after 5 min of critical state |
| BR-MON-006 | Metric retention | Keep 90 days raw, 1 year aggregated |
| BR-MON-007 | Dashboard update | Real-time (< 5 sec lag) |

**Process Flow (BPMN):**

```
START: Continuous Health Monitoring
  │
  ├─ [LOOP] Every 30 Seconds:
  │   │
  │   ├─ [1] Collect Metrics
  │   │   ├─ Query all service endpoints
  │   │   ├─ Measure response times
  │   │   ├─ Check service availability
  │   │   ├─ Query database connectivity
  │   │   ├─ Check cache layer (Redis)
  │   │   ├─ Check message queue
  │   │   ├─ Get CPU/Memory/Disk usage
  │   │   ├─ Get network metrics
  │   │   └─ Collect error counts
  │   │
  │   ├─ [2] Aggregate Metrics
  │   │   ├─ Calculate percentiles (P50, P95, P99)
  │   │   ├─ Compute moving averages
  │   │   ├─ Calculate error rates
  │   │   ├─ Determine overall status
  │   │   └─ Timestamp measurement
  │   │
  │   ├─ [3] Store Metrics
  │   │   ├─ Write to time-series DB
  │   │   ├─ Cache in Redis
  │   │   ├─ Archive old metrics
  │   │   └─ Compress historical data
  │   │
  │   ├─ [4] Evaluate Health Status
  │   │   ├─ Compare against thresholds
  │   │   ├─ Determine status:
  │   │   │   ├─ HEALTHY (all green)
  │   │   │   ├─ DEGRADED (some yellow)
  │   │   │   └─ DOWN (critical red)
  │   │   └─ Calculate uptime percentage
  │   │
  │   ├─ [5] Check Against Baseline
  │   │   ├─ Compare current vs previous
  │   │   ├─ Detect anomalies
  │   │   ├─ Check for trends
  │   │   └─ Flag unusual patterns
  │   │
  │   ├─ [6] UPDATE DASHBOARD
  │   │   ├─ Update status page
  │   │   ├─ Push metrics to WebSocket
  │   │   ├─ Update gauge widgets
  │   │   ├─ Update service indicators
  │   │   ├─ Show last update time
  │   │   └─ Refresh client displays
  │   │
  │   ├─ [DECISION] Status Changed?
  │   │   ├─ YES → Create health change event
  │   │   └─ NO → Continue monitoring
  │   │
  │   └─ END Loop
  │
  ├─ ============================================
  ├─ ALERT GENERATION:
  ├─ ============================================
  │
  ├─ [7] Check Alert Conditions
  │   ├─ For each alert rule:
  │   │   ├─ If response_time_P95 > 500ms:
  │   │   │   └─ Trigger: "Slow Response Time"
  │   │   ├─ If error_rate > 0.1%:
  │   │   │   └─ Trigger: "High Error Rate"
  │   │   ├─ If service_down > 1 min:
  │   │   │   └─ Trigger: "Service Unavailable"
  │   │   ├─ If cpu_usage > 85%:
  │   │   │   └─ Trigger: "High CPU Usage"
  │   │   ├─ If disk_usage > 90%:
  │   │   │   └─ Trigger: "Disk Space Low"
  │   │   ├─ If memory_usage > 85%:
  │   │   │   └─ Trigger: "High Memory Usage"
  │   │   ├─ If db_connections > threshold:
  │   │   │   └─ Trigger: "DB Connection Pool High"
  │   │   └─ If queue_depth > max:
  │   │       └─ Trigger: "Message Queue Backlog"
  │   │
  │   └─ Collect all triggered alerts
  │
  ├─ [8] Deduplicate Alerts
  │   ├─ Check if alert already active
  │   ├─ Merge if same condition
  │   ├─ Update alert timestamp
  │   ├─ Increment repeat count
  │   └─ Prevent alert fatigue
  │
  ├─ [9] Escalate Alerts
  │   ├─ [DECISION] Severity?
  │   │
  │   ├─ IF LOW:
  │   │   ├─ Log to monitoring system
  │   │   ├─ Show on dashboard
  │   │   └─ No immediate notification
  │   │
  │   ├─ IF MEDIUM:
  │   │   ├─ Send email to team
  │   │   ├─ Update dashboard
  │   │   ├─ Store in audit log
  │   │   └─ Escalate if not resolved (30 min)
  │   │
  │   ├─ IF HIGH:
  │   │   ├─ Create incident ticket
  │   │   ├─ Email + Slack alert
  │   │   ├─ Page on-call engineer (after 5 min)
  │   │   ├─ Update status page
  │   │   └─ Track for SLA
  │   │
  │   └─ IF CRITICAL:
  │       ├─ Immediate SMS alert
  │       ├─ Page on-call (now)
  │       ├─ Create P1 incident
  │       ├─ Update status page
  │       ├─ Notify customers
  │       ├─ Start incident management
  │       └─ Begin communication loop
  │
  ├─ [10] Create Alert Record
  │   ├─ Store alert details
  │   ├─ Include: Trigger time, condition, value
  │   ├─ Store: Severity, affected services
  │   ├─ Link to: Incident (if created)
  │   ├─ Set status: Open/Acknowledged/Resolved
  │   └─ Create audit entry
  │
  ├─ [11] Send Notifications
  │   ├─ Determine recipients by rule
  │   ├─ Send via multiple channels:
  │   │   ├─ Email (all)
  │   │   ├─ Slack (team)
  │   │   ├─ SMS (critical)
  │   │   ├─ PagerDuty (critical)
  │   │   └─ Push notification
  │   └─ Include: Alert details, action links
  │
  ├─ [12] Monitor Alert Lifecycle
  │   ├─ Alert resolved if:
  │   │   ├─ Condition returns to normal
  │   │   ├─ OR manually acknowledged
  │   │   └─ OR manually resolved
  │   ├─ Send resolution notification
  │   ├─ Include: Duration, impact
  │   ├─ Generate post-incident report
  │   └─ Create analytics record
  │
  └─ END
```

**Monitoring Dashboard:**

```
System Health Overview:
├─ Overall Status: HEALTHY / DEGRADED / DOWN
├─ Last Check: 2026-08-31 10:30:45 UTC
├─ Uptime: 99.98% (30 days)
│
├─ Service Status:
│   ├─ API Gateway: ✓ HEALTHY (avg 145ms)
│   ├─ Auth Service: ✓ HEALTHY (avg 95ms)
│   ├─ Customer API: ✓ HEALTHY (avg 220ms)
│   ├─ Property API: ✓ HEALTHY (avg 385ms)
│   ├─ Payment Service: ✓ HEALTHY (avg 1200ms)
│   ├─ Notification Service: ✓ HEALTHY (avg 150ms)
│   ├─ Database: ✓ HEALTHY (connections: 45/100)
│   ├─ Cache (Redis): ✓ HEALTHY (memory: 2.3GB/8GB)
│   └─ Message Queue: ✓ HEALTHY (depth: 234 msgs)
│
├─ Performance Metrics:
│   ├─ Requests/sec: 1,245
│   ├─ P50 Latency: 125ms
│   ├─ P95 Latency: 385ms
│   ├─ P99 Latency: 890ms
│   ├─ Error Rate: 0.045%
│   └─ Success Rate: 99.955%
│
├─ Resource Usage:
│   ├─ CPU: 42% (warning at 75%)
│   ├─ Memory: 58% (warning at 80%)
│   ├─ Disk: 35% (warning at 90%)
│   └─ Network: 380 Mbps (up), 125 Mbps (down)
│
└─ Recent Alerts:
    ├─ None active
    ├─ Last: Payment Gateway Slow (resolved 2h ago)
    └─ Today: 1 alert (resolved)
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Detection Latency | Time to detect issue | < 30 seconds |
| Alert Accuracy | True positives | ≥ 99% |
| False Positives | Invalid alerts | < 1% |
| MTTR | Mean time to resolution | < 15 minutes |
| System Availability | Uptime | ≥ 99.95% |
| Alert Response | On-call responds | < 5 minutes |

---

### 4.2 Performance Analytics and Reporting

**Process ID:** PROC-MON-002  
**Process Name:** Performance Analytics and Reporting  
**Process Category:** Monitoring Services  
**Process Owner:** Analytics Team  
**Version:** 1.0

**Business Description:**
Aggregate and report on system performance metrics and user analytics.

**Actors:**
- **Primary:** Analytics System (Automated)
- **Secondary:** Data Warehouse, Reporting Engine
- **Support:** Analytics Team, Management

**Inputs:**
- Event logs
- Transaction data
- User interactions
- API metrics
- Business metrics

**Outputs:**
- Daily/Weekly/Monthly reports
- Performance dashboards
- Executive summaries
- Trend analysis
- Recommendations

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-MON-010 | Report schedule | Daily (email), Weekly (dashboard), Monthly (exec) |
| BR-MON-011 | Data retention | Raw: 90 days, Aggregated: 3 years |
| BR-MON-012 | Privacy | PII removed from analytics |
| BR-MON-013 | Accuracy | Audit trail for all calculations |
| BR-MON-014 | Accessibility | Reports accessible to authorized users |
| BR-MON-015 | Timelines | Generated by 6 AM daily |

**Process Flow Summary:**

```
Daily Analytics Pipeline:

[1] Data Collection (overnight)
├─ Extract events from event log
├─ Pull transaction data
├─ Get user interaction events
├─ Aggregate API metrics
└─ Collect business metrics

[2] Data Processing
├─ Clean/normalize data
├─ Remove PII
├─ Calculate aggregations
├─ Compute moving averages
└─ Generate rollups

[3] Load to Data Warehouse
├─ Insert clean data
├─ Update dimensional tables
├─ Create aggregated views
└─ Index for queries

[4] Generate Reports
├─ Query warehouse
├─ Build report templates
├─ Calculate KPIs
├─ Create visualizations
└─ Generate PDF/Excel

[5] Send Reports
├─ Email to stakeholders
├─ Upload to dashboard
├─ Archive in S3
└─ Log delivery

[6] Archive Old Data
├─ Compress raw data > 90 days
├─ Move to long-term storage
└─ Update retention
```

---

## 5. AGENT OPERATIONS PROCESSES

### 5.1 Agent Registration and Onboarding

**Process ID:** PROC-AO-001  
**Process Name:** Agent Registration and Onboarding  
**Process Category:** Agent Operations  
**Process Owner:** Agent Operations Team  
**Version:** 1.0

**Business Description:**
Manage agent registration, license verification, and platform onboarding.

**Actors:**
- **Primary:** Real Estate Agent
- **Secondary:** System, License Verification Service, Compliance Team
- **Support:** Agent Operations Team

**Inputs:**
- Agent name
- License number
- State/country
- Brokerage affiliation
- Contact information
- Banking details (for commission payout)
- Experience information

**Outputs:**
- Active agent account
- Verified license
- Commission payout setup
- Welcome email with training
- Agent profile created

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-AO-001 | License requirement | Must have active real estate license |
| BR-AO-002 | License verification | Verify with state authority within 48h |
| BR-AO-003 | Brokerage linkage | Agent must be affiliated with brokerage |
| BR-AO-004 | Bank verification | ACH micro-deposits for payout verification |
| BR-AO-005 | Background check | Cleared before account activation |
| BR-AO-006 | Terms acceptance | Must accept platform terms & commission splits |
| BR-AO-007 | Training completion | Must complete onboarding training |

**Process Flow (BPMN):**

```
START: Agent Registration
  │
  ├─ [1] Initiate Registration
  │   ├─ Agent provides email
  │   ├─ Receives registration link
  │   └─ Completes initial registration form
  │
  ├─ [2] Provide Basic Information
  │   ├─ First name
  │   ├─ Last name
  │   ├─ Email
  │   ├─ Phone
  │   ├─ Password
  │   ├─ Brokerage search/select
  │   └─ Years of experience
  │
  ├─ [3] Verify Email
  │   ├─ Send verification email
  │   ├─ User clicks verification link
  │   ├─ Email marked as verified
  │   └─ Account status: Email Verified
  │
  ├─ [4] Provide License Information
  │   ├─ Select license type
  │   ├─ Select state/country
  │   ├─ Enter license number
  │   ├─ Enter license expiration date
  │   ├─ Verify date not expired
  │   └─ Continue
  │
  ├─ [5] Initiate License Verification
  │   ├─ Submit to state license authority
  │   ├─ Query verification database
  │   ├─ Wait for response (async)
  │   ├─ Can take up to 48 hours
  │   └─ Account status: License Pending Verification
  │
  ├─ [ASYNC] License Verification
  │   ├─ State authority returns result
  │   ├─ If verified:
  │   │   └─ Update license_verified = true
  │   ├─ If not found:
  │   │   ├─ Request manual review
  │   │   └─ Contact agent to re-verify
  │   └─ Continue to [6]
  │
  ├─ [6] DECISION: License Verified?
  │   ├─ NO → Request manual verification → [7]
  │   └─ YES ↓
  │
  ├─ [7] Provide Banking Information
  │   ├─ Bank name
  │   ├─ Account type (checking/savings)
  │   ├─ Routing number
  │   ├─ Account number
  │   ├─ Account holder name
  │   └─ Terms acceptance for ACH
  │
  ├─ [8] Verify Banking Information
  │   ├─ Initiate micro-deposits (2x < $1)
  │   ├─ Send to provided bank account
  │   ├─ Agent receives deposits in 1-2 business days
  │   ├─ Agent enters deposit amounts
  │   └─ Verify match
  │
  ├─ [DECISION] Banking Verified?
  │   ├─ NO → Request retry or alternative
  │   └─ YES ↓
  │
  ├─ [9] Request Background Check
  │   ├─ Submit to background check service
  │   ├─ Request: Criminal, legal, financial check
  │   ├─ Authorization from agent
  │   ├─ Consent for credit inquiry (optional)
  │   └─ Wait for results (3-5 business days)
  │
  ├─ [ASYNC] Background Check Results
  │   ├─ Service returns pass/fail/review
  │   ├─ If pass: Continue to [10]
  │   ├─ If review: Compliance review required
  │   └─ If fail: Reject application
  │
  ├─ [10] Accept Terms & Commission Structure
  │   ├─ Display: Platform terms & conditions
  │   ├─ Display: Commission split details
  │   ├─ Show: Fee structure
  │   ├─ Show: Payment terms
  │   ├─ Agent must accept (required)
  │   └─ Log acceptance with timestamp
  │
  ├─ [11] Assign Agent ID & Activate
  │   ├─ Generate unique agent ID
  │   ├─ Activate account
  │   ├─ Set status = "active"
  │   ├─ Timestamp activation
  │   ├─ Create agent profile
  │   ├─ Initialize performance stats
  │   └─ Setup commission tracking
  │
  ├─ [12] Assign Onboarding Training
  │   ├─ Enroll in training modules
  │   ├─ Send welcome email
  │   ├─ Provide training links
  │   ├─ Schedule: Platform walkthrough
  │   ├─ Provide: Help documentation
  │   ├─ Offer: Personalized training call
  │   └─ Set training deadline (14 days)
  │
  ├─ [13] Create Agent Dashboard
  │   ├─ Setup personal dashboard
  │   ├─ Initialize CRM
  │   ├─ Setup lead routing
  │   ├─ Configure notifications
  │   ├─ Enable API access (if applicable)
  │   └─ Provide: Access credentials
  │
  ├─ [14] Send Welcome Package
  │   ├─ Email: Welcome to PropertyPilot
  │   ├─ Include: Quick start guide
  │   ├─ Include: License certificate (digital)
  │   ├─ Include: Commission structure details
  │   ├─ Include: Support contact information
  │   ├─ Include: First listing guide
  │   └─ Offer: Onboarding call
  │
  ├─ [15] Initial Commission Setup
  │   ├─ Set commission rate (based on brokerage)
  │   ├─ Set payment frequency
  │   ├─ Enable: Commission tracking
  │   ├─ Setup: Tax document collection (W9/W8-BEN)
  │   ├─ Request: Immediate tax documents
  │   └─ Store: Banking info for payouts
  │
  ├─ [16] Return Success
  │   ├─ Display: "Welcome to PropertyPilot!"
  │   ├─ Show: Agent ID
  │   ├─ Show: Dashboard link
  │   ├─ Provide: Next steps
  │   └─ Auto-redirect to dashboard
  │
  └─ END
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Registration Completion | % agents complete registration | ≥ 98% |
| License Verification | Verified within 48 hours | ≥ 95% |
| Training Completion | % agents complete onboarding | ≥ 90% |
| Time to Active | Days from registration to active | < 7 days |
| Dropout Rate | Agents who abandon process | < 2% |
| Commission Accuracy | Correct calculations | 100% |

---

## 6. VENDOR OPERATIONS PROCESSES

### 6.1 Vendor Registration and Verification

**Process ID:** PROC-VO-001  
**Process Name:** Vendor Registration and Verification  
**Process Category:** Vendor Operations  
**Process Owner:** Vendor Management Team  
**Version:** 1.0

**Business Description:**
Register and verify vendors (inspectors, appraisers, title companies, lenders).

**Actors:**
- **Primary:** Vendor/Service Provider
- **Secondary:** Verification Service, Compliance Team, System
- **Support:** Vendor Management Team

**Inputs:**
- Vendor information (name, type, credentials)
- Business license
- Insurance documentation
- Banking information
- Service areas
- Certifications

**Outputs:**
- Verified vendor account
- Active vendor profile
- Commission payout setup
- Search listing activated
- Welcome communication

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-VO-001 | Business license required | Valid, current license needed |
| BR-VO-002 | Insurance requirement | Liability insurance > $1M |
| BR-VO-003 | Background check | Pass background verification |
| BR-VO-004 | Certification verification | Verify all claimed certifications |
| BR-VO-005 | Service area limits | Max 3 service areas per vendor |
| BR-VO-006 | Response time requirement | Respond to 80% of requests < 24 hours |
| BR-VO-007 | Ratings minimum | Maintain rating > 4.0 stars |

**Process Flow (BPMN):**

```
START: Vendor Registration
  │
  ├─ [1] Select Vendor Type
  │   ├─ Options:
  │   │   ├─ Home Inspector
  │   │   ├─ Appraiser
  │   │   ├─ Title Company
  │   │   ├─ Mortgage Lender
  │   │   ├─ Closing Attorney
  │   │   └─ Other Services
  │   └─ Continue with type-specific form
  │
  ├─ [2] Provide Business Information
  │   ├─ Business name
  │   ├─ Legal business structure
  │   ├─ Years in business
  │   ├─ Contact person
  │   ├─ Business phone
  │   ├─ Business email
  │   ├─ Website (optional)
  │   └─ Service description
  │
  ├─ [3] Verify Email
  │   ├─ Send verification email
  │   ├─ User clicks verification link
  │   └─ Email marked as verified
  │
  ├─ [4] Upload Business License
  │   ├─ Scan/upload business license
  │   ├─ Validate document format (PDF/image)
  │   ├─ Extract license number
  │   ├─ Extract expiration date
  │   ├─ Verify date not expired
  │   └─ Store in secure storage
  │
  ├─ [5] Upload Insurance Certificate
  │   ├─ Upload insurance certificate of liability
  │   ├─ Verify coverage > $1M
  │   ├─ Verify includes vendor type coverage
  │   ├─ Verify not expired
  │   └─ Store insurance info
  │
  ├─ [6] Define Service Areas
  │   ├─ Select up to 3 geographic areas
  │   ├─ Can be: City, County, State
  │   ├─ Specify service radius if needed
  │   ├─ Set availability status
  │   └─ Define preferred schedule
  │
  ├─ [7] Provide Banking Information
  │   ├─ Bank name
  │   ├─ Routing number
  │   ├─ Account number
  │   ├─ Account holder name
  │   └─ Verify with micro-deposits
  │
  ├─ [8] List Certifications (optional)
  │   ├─ Select applicable certifications
  │   ├─ Examples:
  │   │   ├─ NAHI (for inspectors)
  │   │   ├─ State appraiser license
  │   │   └─ Professional associations
  │   ├─ Upload certificate files
  │   ├─ Verify expiration dates
  │   └─ Store documentation
  │
  ├─ [9] Request Background Check
  │   ├─ Submit to background check service
  │   ├─ Criminal history check
  │   ├─ Legal history check
  │   ├─ Financial check (optional)
  │   └─ Wait for results
  │
  ├─ [ASYNC] Background Check Results
  │   ├─ If pass: Continue to [10]
  │   ├─ If review needed: Queue for compliance review
  │   └─ If fail: Reject and notify
  │
  ├─ [10] Compliance Review
  │   ├─ Compliance officer reviews:
  │   │   ├─ Business license validity
  │   │   ├─ Insurance coverage adequacy
  │   │   ├─ Background check results
  │   │   └─ Certifications
  │   ├─ Decision: Approve/Reject/Request Info
  │   └─ Timeline: 2-3 business days
  │
  ├─ [DECISION] Approved?
  │   ├─ NO → Send rejection reason → END
  │   └─ YES ↓
  │
  ├─ [11] Activate Vendor Account
  │   ├─ Generate vendor ID
  │   ├─ Set status = "active"
  │   ├─ Enable in search/marketplace
  │   ├─ Create vendor profile
  │   ├─ Initialize ratings/reviews (0)
  │   └─ Setup commission tracking
  │
  ├─ [12] Send Welcome Communications
  │   ├─ Email: Welcome to PropertyPilot Vendors
  │   ├─ Include: Vendor ID, credentials
  │   ├─ Explain: Commission structure
  │   ├─ Provide: Platform guide
  │   ├─ Include: API documentation
  │   └─ Offer: Training/support
  │
  ├─ [13] Request Tax Documentation
  │   ├─ For 1099 reporting: W9 form
  │   ├─ Or: W8-BEN (international)
  │   ├─ Request: Signed and notarized
  │   ├─ Store in secure system
  │   └─ Deadline: Before first payout
  │
  ├─ [14] Initialize Rating System
  │   ├─ Set baseline rating: No ratings yet
  │   ├─ Display: "New Vendor"
  │   ├─ Ratings will appear after first service
  │   └─ Enable: Review collection after completion
  │
  ├─ [15] Return Success
  │   ├─ Display: "Welcome!"
  │   ├─ Show: Vendor ID
  │   ├─ Show: Service area map
  │   ├─ Provide: Next steps
  │   └─ Auto-redirect to vendor dashboard
  │
  └─ END
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Registration Completion | % vendors complete registration | ≥ 95% |
| Verification Turnaround | Active within business days | ≤ 5 days |
| Compliance Accuracy | Compliant vendors | 100% |
| False Negatives | Rejected qualified vendors | ≤ 1% |
| Vendor Satisfaction | Satisfied with process | ≥ 85% |
| Retention | Active vendors after 1 year | ≥ 80% |

---

## 7. PAYMENT PROCESSING PROCESSES

### 7.1 Customer Payment Processing

**Process ID:** PROC-PP-001  
**Process Name:** Customer Payment Processing  
**Process Category:** Payment Processing  
**Process Owner:** Finance Team  
**Version:** 1.0

**Business Description:**
Handle customer payments for earnest money, subscription, and other charges.

**Actors:**
- **Primary:** Customer
- **Secondary:** Payment Gateway (Stripe/PayPal), Payment Processor, System
- **Support:** Finance Team

**Inputs:**
- Payment amount
- Payment method (card/ACH/PayPal)
- Customer information
- Payment purpose (earnest money, subscription, etc.)
- Billing details

**Outputs:**
- Payment processed
- Transaction record created
- Confirmation email
- Escrow account funded (if applicable)
- Receipt generated

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-PP-001 | PCI Compliance | Level 1 certified, TLS 1.2+ |
| BR-PP-002 | Amount validation | > $0, < reasonable max ($1M) |
| BR-PP-003 | Fraud detection | Real-time fraud checking |
| BR-PP-004 | 3D Secure | Required for cards when issuer mandates |
| BR-PP-005 | Retry logic | 3 retries for failed payments |
| BR-PP-006 | Idempotency | No duplicate charges (within 24h window) |
| BR-PP-007 | Audit trail | All transactions logged & encrypted |
| BR-PP-008 | Settlement | Funds settle within 2-3 business days |

**Process Flow (BPMN):**

```
START: Customer Payment
  │
  ├─ [1] Initiate Payment
  │   ├─ Customer clicks "Pay Now"
  │   ├─ Review payment details
  │   ├─ Confirm amount
  │   └─ Click "Proceed to Payment"
  │
  ├─ [2] Create Payment Intent
  │   ├─ Generate unique payment intent ID
  │   ├─ Record: Amount, currency, purpose
  │   ├─ Store: Customer ID, order ID
  │   ├─ Set: Expiration (15 minutes)
  │   ├─ Create: Client secret (for client-side)
  │   └─ Log: Payment intent creation
  │
  ├─ [3] Display Payment Form
  │   ├─ Show secure payment form
  │   ├─ Options:
  │   │   ├─ Credit/Debit card
  │   │   ├─ ACH bank transfer
  │   │   ├─ Apple Pay
  │   │   ├─ Google Pay
  │   │   └─ PayPal
  │   ├─ TLS: Secure connection
  │   └─ Tokenization: No PII in logs
  │
  ├─ [4] Customer Selects Payment Method
  │   ├─ Card: Enter card details
  │   │   ├─ Card number
  │   │   ├─ Expiration date
  │   │   ├─ CVV
  │   │   └─ Billing zip (optional)
  │   ├─ ACH: Bank account details
  │   │   ├─ Routing number
  │   │   ├─ Account number
  │   │   └─ Account holder name
  │   ├─ Apple Pay: One-click
  │   ├─ Google Pay: One-click
  │   └─ PayPal: Redirect to PayPal
  │
  ├─ [5] Tokenize Payment Method
  │   ├─ Send to payment processor
  │   ├─ Processor creates token
  │   ├─ Token returned to system
  │   ├─ Store token (encrypted) in database
  │   └─ Never store full card/ACH details
  │
  ├─ [6] Validate Payment Details
  │   ├─ Check: Amount > 0
  │   ├─ Check: Currency valid
  │   ├─ Check: Customer verified (if high amount)
  │   ├─ Check: Card not expired
  │   ├─ Check: CVV valid (if card)
  │   └─ Check: Payment method active
  │
  ├─ [DECISION] Valid?
  │   ├─ NO → Return validation error → END
  │   └─ YES ↓
  │
  ├─ [7] Fraud Detection Check
  │   ├─ Check amount vs customer profile
  │   ├─ Check: Unusual location
  │   ├─ Check: Velocity (multiple rapid charges)
  │   ├─ Use: Machine learning model
  │   ├─ Query: Fraud score service
  │   └─ Determine risk: Low/Medium/High
  │
  ├─ [DECISION] Risk Assessment?
  │   ├─ LOW → Process immediately
  │   ├─ MEDIUM → Require 3D Secure
  │   └─ HIGH → Block & require manual review
  │
  ├─ [8] Apply 3D Secure (if required)
  │   ├─ Redirect to card issuer
  │   ├─ Customer authenticates with issuer
  │   ├─ Issuer approves transaction
  │   ├─ Return to system with auth
  │   └─ Include: 3D Secure indicator
  │
  ├─ [9] Submit Payment to Processor
  │   ├─ Call payment gateway API
  │   ├─ Submit: Amount, token, metadata
  │   ├─ Include: Idempotency key
  │   ├─ Processor authorizes payment
  │   ├─ Receive: Authorization code
  │   ├─ Timeline: < 3 seconds
  │   └─ Store: Authorization details
  │
  ├─ [DECISION] Payment Authorized?
  │   ├─ NO → Check error → [10A] Retry Logic
  │   └─ YES ↓
  │
  ├─ [10] Create Transaction Record
  │   ├─ Generate transaction ID
  │   ├─ Store: Payment intent ID
  │   ├─ Store: Authorization code
  │   ├─ Store: Amount & currency
  │   ├─ Store: Status = "authorized"
  │   ├─ Store: Timestamp
  │   ├─ Store: Customer & order info
  │   ├─ Encrypt sensitive fields
  │   └─ Log in audit trail
  │
  ├─ [11] Capture Payment
  │   ├─ Call processor to capture
  │   ├─ Convert auth to charge
  │   ├─ Funds queued for settlement
  │   ├─ Update transaction status = "captured"
  │   ├─ Log capture event
  │   └─ Timeline: Usually immediate
  │
  ├─ [12] Handle Escrow (if earnest money)
  │   ├─ If payment purpose = "earnest_money":
  │   │   ├─ Create escrow account record
  │   │   ├─ Link to offer/transaction
  │   │   ├─ Mark as held in escrow
  │   │   ├─ Set release conditions
  │   │   └─ Notify parties
  │   └─ Continue to [13]
  │
  ├─ [13] Generate Receipt
  │   ├─ Create receipt PDF
  │   ├─ Include: Transaction details
  │   ├─ Include: Amount, date, method
  │   ├─ Include: Confirmation code
  │   ├─ Sign with certificate
  │   ├─ Store in S3 (encrypted)
  │   └─ Generate download link
  │
  ├─ [14] Send Confirmation Email
  │   ├─ Email to customer
  │   ├─ Subject: "Payment Confirmed"
  │   ├─ Include: Amount & date
  │   ├─ Include: Confirmation code
  │   ├─ Include: Receipt link (7-day expiration)
  │   ├─ Include: Contact for support
  │   └─ Queue for delivery
  │
  ├─ [15] Update Related Records
  │   ├─ If subscription payment:
  │   │   ├─ Mark subscription as paid
  │   │   ├─ Activate features
  │   │   ├─ Set renewal date
  │   │   └─ Enable auto-renew
  │   ├─ If offer payment:
  │   │   ├─ Mark earnest money received
  │   │   ├─ Update offer status
  │   │   ├─ Notify seller/agent
  │   │   └─ Move to next stage
  │   └─ Continue to [16]
  │
  ├─ [16] Return Success Response
  │   ├─ Display: "Payment successful!"
  │   ├─ Show: Confirmation number
  │   ├─ Show: Amount charged
  │   ├─ Provide: Receipt download link
  │   ├─ Offer: Next steps
  │   └─ Auto-redirect after 3 seconds
  │
  └─ END: Success
  │
  ├─ ============================================
  ├─ RETRY LOGIC (if authorization failed):
  ├─ ============================================
  │
  ├─ [10A] Analyze Failure
  │   ├─ Check error code from processor
  │   ├─ Error types:
  │   │   ├─ Insufficient funds
  │   │   ├─ Card expired
  │   │   ├─ CVV mismatch
  │   │   ├─ Fraud detected
  │   │   ├─ 3D Secure required
  │   │   └─ Temporary processor error
  │   └─ Determine if retryable
  │
  ├─ [DECISION] Retryable?
  │   ├─ NO → Return permanent error → END
  │   └─ YES ↓
  │
  ├─ [10B] Retry with Backoff
  │   ├─ Retry 1: Wait 5 seconds
  │   ├─ Retry 2: Wait 30 seconds (if failed)
  │   ├─ Retry 3: Wait 2 minutes (if failed)
  │   ├─ Each retry: Submit to processor again
  │   └─ If all fail: Return error to customer
  │
  ├─ [10C] Display Error to Customer
  │   ├─ Clear error message
  │   ├─ Suggested action:
  │   │   ├─ "Try a different card"
  │   │   ├─ "Check card details"
  │   │   ├─ "Contact bank"
  │   │   └─ "Use different payment method"
  │   ├─ Offer: Contact support
  │   ├─ Option: Try again
  │   └─ Return: 402 Payment Required
  │
  └─ END: Retry Exhausted
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Payment Success Rate | Successful payments | ≥ 98% |
| Fraud Detection Accuracy | Correct fraud detection | ≥ 99% |
| False Fraud Blocks | Legitimate blocked | ≤ 0.5% |
| Processing Time | Authorize to capture | < 3 seconds |
| PCI Compliance | Compliance violations | 0 |
| Customer Satisfaction | Easy payment experience | ≥ 90% |

---

### 7.2 Commission Payout Processing

**Process ID:** PROC-PP-002  
**Process Name:** Commission Payout Processing  
**Process Category:** Payment Processing  
**Process Owner:** Finance Team  
**Version:** 1.0

**Business Description:**
Calculate and process commission payments to agents.

**Actors:**
- **Primary:** Agent
- **Secondary:** System (Automated), Payment Processor, Finance Team
- **Support:** Finance, Accounting

**Inputs:**
- Closed transactions
- Commission calculations
- Agent banking info
- Tax withholdings
- Payout frequency

**Outputs:**
- Commission payments
- Payout statements
- Tax documents (W2/1099)
- Deposit confirmations
- Audit records

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-PP-010 | Commission calculation | Based on agreed split |
| BR-PP-011 | Minimum payout | $50 minimum per payout |
| BR-PP-012 | Payout frequency | Twice weekly (Mon/Thu) |
| BR-PP-013 | Timing requirement | After deal officially closes |
| BR-PP-014 | Tax withholding | Withhold based on W9/W8-BEN |
| BR-PP-015 | Chargeback handling | Recover from agent if transaction reversed |
| BR-PP-016 | Reconciliation | Match to bank deposits weekly |
| BR-PP-017 | Payment method | ACH transfers only (no checks) |

**Process Flow Summary:**

```
Commission Payout Pipeline (Twice Weekly):

[1] Identify Closed Deals
├─ Query closed transactions since last payout run
├─ Filter: Status = "closed"
├─ Filter: Commission calculated = true
├─ Filter: Payout not yet processed
└─ Group by agent

[2] Calculate Commissions
├─ For each transaction:
│   ├─ Get commission rate
│   ├─ Get gross sale price
│   ├─ Calculate: Gross * Rate
│   ├─ Subtract: Company cut
│   ├─ Get agent's net commission
│   ├─ Apply: Tax withholding
│   ├─ Get: Net payout amount
│   └─ Verify minimum ($50)
└─ Aggregate by agent

[3] Prepare Payout Batches
├─ Group payouts by bank/processor
├─ Create batch file (ACH format)
├─ Include: Routing, account, amount
├─ Calculate: Total batch amount
├─ Generate: Batch ID
└─ Create audit record

[4] Process ACH Transfers
├─ Submit batch to ACH processor
├─ Receive: Batch confirmation
├─ Confirm: All transfers queued
├─ Timeline: Funds settle 1-2 business days
└─ Log: Transfer details

[5] Send Payout Statements
├─ Email to each agent:
│   ├─ Statement of earnings
│   ├─ Commission details (per transaction)
│   ├─ Tax withholdings
│   ├─ Net payout
│   ├─ Deposit details
│   └─ Payout date
└─ Make available in agent dashboard

[6] Verify Deposits
├─ Match bank deposits to ACH submits
├─ Verify amounts
├─ Reconcile any discrepancies
├─ Flag chargebacks/reversals
└─ Create reconciliation report

[7] Generate Tax Documents
├─ Quarterly: Q1099 estimates
├─ Annually: 1099-NEC forms
├─ Include: Total commissions, withholdings
├─ File: With IRS
└─ Provide: Copy to agents
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Accuracy | Correct calculations | 99.95% |
| Timeliness | Deposits within 2 business days | 100% |
| Reconciliation | Bank match rate | 100% |
| Disputes | Commission disputes | < 0.5% |
| Tax Compliance | Correct withholdings | 100% |
| Agent Satisfaction | Satisfied with process | ≥ 95% |

---

## 8. SUBSCRIPTION MANAGEMENT PROCESSES

### 8.1 Subscription Creation and Activation

**Process ID:** PROC-SM-001  
**Process Name:** Subscription Creation and Activation  
**Process Category:** Subscription Management  
**Process Owner:** Billing Team  
**Version:** 1.0

**Business Description:**
Create new subscriptions and activate premium features.

**Actors:**
- **Primary:** Customer
- **Secondary:** Payment Gateway, Billing System, Feature Engine
- **Support:** Billing Team

**Inputs:**
- Plan ID
- Customer ID
- Payment method
- Billing period
- Auto-renew preference

**Outputs:**
- Active subscription
- Features enabled
- Confirmation email
- Billing record created
- Receipt generated

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-SM-001 | One active per customer | Only 1 active plan per customer |
| BR-SM-002 | Plan validation | Must be active/available |
| BR-SM-003 | Price capture | Capture price at subscription time |
| BR-SM-004 | Trial eligibility | First-time subscribers get trial |
| BR-SM-005 | Auto-renew default | Default enabled (customer can disable) |
| BR-SM-006 | Billing anchor | Bill on same day monthly |
| BR-SM-007 | Proration | Pro-rate if upgrading mid-cycle |
| BR-SM-008 | Feature activation | Features active immediately |

**Process Flow (BPMN):**

```
START: Subscribe to Plan
  │
  ├─ [1] Select Plan
  │   ├─ Browse available plans
  │   ├─ Compare features/pricing
  │   ├─ Click "Choose Plan"
  │   └─ Review plan details
  │
  ├─ [2] Check Eligibility
  │   ├─ Check: Customer not already subscribed
  │   ├─ Check: Subscription plan is available
  │   ├─ Check: Customer can trial (if applicable)
  │   ├─ Check: No active upgrade/downgrade
  │   └─// filepath: c:\PropertyPilot\docs\BUSINESS_PROCESS_CATALOG.md

# PropertyPilot - Complete Business Process Catalog

**Document Version:** 1.0  
**Date:** 2026-08-31  
**Status:** Production Ready  
**Standard:** BPMN 2.0 (Business Process Model and Notation)

---

## Table of Contents

1. [Customer Management Processes](#1-customer-management-processes)
2. [Property Management Processes](#2-property-management-processes)
3. [Verification Services Processes](#3-verification-services-processes)
4. [Monitoring Services Processes](#4-monitoring-services-processes)
5. [Agent Operations Processes](#5-agent-operations-processes)
6. [Vendor Operations Processes](#6-vendor-operations-processes)
7. [Payment Processing Processes](#7-payment-processing-processes)
8. [Subscription Management Processes](#8-subscription-management-processes)
9. [Complaint Management Processes](#9-complaint-management-processes)
10. [Report Management Processes](#10-report-management-processes)

---

## 1. CUSTOMER MANAGEMENT PROCESSES

### 1.1 Customer Registration and Onboarding

**Process ID:** PROC-CM-001  
**Process Name:** Customer Registration and Onboarding  
**Process Category:** Customer Management  
**Process Owner:** Customer Success Team  
**Version:** 1.0

**Business Description:**
New customer registration flow from initial signup through email verification and profile completion.

**Actors:**
- **Primary:** New Customer
- **Secondary:** System (Automated), Email Service, Compliance System
- **Support:** Customer Support Agent (if manual intervention needed)

**Inputs:**
- Email address
- Password
- First name
- Last name
- Phone number
- User type (customer/agent/vendor)
- Terms acceptance

**Outputs:**
- Active user account
- Verified email address
- User profile created
- Welcome email sent
- Account confirmation

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-CM-001 | Email uniqueness | Email must not exist in system |
| BR-CM-002 | Password complexity | Min 8 chars, 1 upper, 1 lower, 1 number, 1 special |
| BR-CM-003 | Phone validation | E.164 format, valid country code |
| BR-CM-004 | User type validation | Must be one of: customer, agent, vendor |
| BR-CM-005 | Terms requirement | Terms must be accepted before registration |
| BR-CM-006 | Email verification | Must verify email within 24 hours |
| BR-CM-007 | Rate limiting | Max 5 registration attempts per IP per hour |
| BR-CM-008 | Age verification | Minimum age 18 years (for some user types) |

**Process Flow (BPMN):**

```
START
  │
  ├─ [1] Receive Registration Request
  │   └─ Validate Input Data
  │       ├─ Check email format
  │       ├─ Validate password complexity
  │       ├─ Check phone format
  │       └─ Verify terms acceptance
  │
  ├─ [DECISION] Input Valid?
  │   ├─ NO → Return validation error → END
  │   └─ YES ↓
  │
  ├─ [2] Check Email Uniqueness
  │   └─ Query database for existing email
  │
  ├─ [DECISION] Email Exists?
  │   ├─ YES → Return conflict error → END
  │   └─ NO ↓
  │
  ├─ [3] Hash Password
  │   └─ Apply bcrypt algorithm (cost factor: 12)
  │
  ├─ [4] Create User Account
  │   ├─ Generate User ID (UUID)
  │   ├─ Store user record
  │   ├─ Encrypt sensitive data
  │   └─ Log account creation
  │
  ├─ [5] Generate Email Verification Token
  │   ├─ Create random token (32 bytes)
  │   ├─ Set expiration (24 hours)
  │   └─ Store token in cache
  │
  ├─ [6] Send Verification Email
  │   ├─ Queue email task
  │   └─ Include verification link with token
  │
  ├─ [ASYNC] Wait for Email Verification
  │   ├─ Success → [7]
  │   ├─ Timeout (24h) → Delete unverified account
  │   └─ Manual Verification → [8]
  │
  ├─ [7] Mark Email as Verified
  │   ├─ Update user record
  │   ├─ Set verification timestamp
  │   └─ Invalidate token
  │
  ├─ [8] Create Initial Profile
  │   ├─ Set default preferences
  │   ├─ Initialize notification settings
  │   └─ Setup account security settings
  │
  ├─ [9] Send Welcome Email
  │   ├─ Queue welcome email
  │   └─ Include onboarding guide link
  │
  ├─ [10] Return Success Response
  │   ├─ Provide user ID
  │   ├─ Confirm account status
  │   └─ Display next steps
  │
  └─ END
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Registration Completion | % of registrations completing all steps | ≥ 95% |
| Email Verification | % emails verified within 24 hours | ≥ 92% |
| Account Activation Time | Time from registration to active status | < 5 minutes |
| Error Rate | Failed registrations (technical errors) | < 1% |
| Duplicate Prevention | Duplicate accounts created | 0 |
| Password Quality | Weak passwords attempted | < 5% |
| Security Breaches | Compromised registrations | 0 |

**Validation Checkpoints:**

```
Checkpoint 1: Input Validation
├─ Email format valid ✓
├─ Password meets requirements ✓
├─ Phone number valid ✓
└─ Terms accepted ✓

Checkpoint 2: Database Checks
├─ Email unique ✓
├─ User ID generated ✓
└─ Record created ✓

Checkpoint 3: Email Delivery
├─ Verification email sent ✓
├─ Token generated ✓
└─ Expiration set ✓

Checkpoint 4: Verification
├─ Token valid ✓
├─ Not expired ✓
└─ Email verified ✓
```

**Error Handling:**

| Error Type | Error Code | Response | Recovery |
|-----------|-----------|----------|----------|
| Invalid Email | REG-001 | 400 Bad Request | Provide email format example |
| Weak Password | REG-002 | 422 Unprocessable | Show password requirements |
| Email Exists | REG-003 | 409 Conflict | Suggest login or password reset |
| Invalid Phone | REG-004 | 400 Bad Request | Provide E.164 format example |
| Terms Not Accepted | REG-005 | 400 Bad Request | Highlight terms checkbox |
| Email Delivery Failed | REG-006 | 202 Accepted | Retry with exponential backoff |
| Token Expired | REG-007 | 401 Unauthorized | Provide resend verification link |
| Rate Limited | REG-008 | 429 Too Many Requests | Show retry-after time |

**Performance SLA:**

| Metric | SLA | Priority |
|--------|-----|----------|
| Registration Processing | < 2 seconds | High |
| Email Delivery | < 5 minutes | High |
| Token Generation | < 100ms | Critical |
| Database Write | < 50ms | Critical |
| Total E2E Time | < 5 minutes | High |

---

### 1.2 Customer Profile Management

**Process ID:** PROC-CM-002  
**Process Name:** Customer Profile Management  
**Process Category:** Customer Management  
**Process Owner:** Customer Success Team  
**Version:** 1.0

**Business Description:**
Manage customer profile information including personal details, preferences, and saved searches.

**Actors:**
- **Primary:** Customer
- **Secondary:** System (Automated), Search Engine
- **Support:** Customer Support Agent

**Inputs:**
- Customer ID
- Profile data (name, phone, email, photo)
- Preferences (price range, locations, property types)
- Financing information
- Search preferences

**Outputs:**
- Updated customer profile
- Updated preferences applied
- Search engine refreshed
- Audit log entry
- Confirmation notification

**Business Rules:**

| Rule ID | Description | Constraint |
|---------|-------------|-----------|
| BR-CM-010 | Phone update | E.164 format required |
| BR-CM-011 | Email uniqueness | New email must not exist |
| BR-CM-012 | Profile photo size | Max 5MB, jpg/png format |
| BR-CM-013 | Price range validation | Min ≤ Max |
| BR-CM-014 | Location validation | Valid US/Canada locations |
| BR-CM-015 | Audit trail | All changes logged with timestamp |
| BR-CM-016 | Preference limits | Max 10 saved searches |
| BR-CM-017 | Change notification | User notified of account changes |

**Process Flow (BPMN):**

```
START
  │
  ├─ [1] Authenticate Customer
  │   ├─ Verify bearer token
  │   └─ Validate customer ID
  │
  ├─ [2] Retrieve Current Profile
  │   └─ Load from database/cache
  │
  ├─ [3] Receive Update Request
  │   ├─ Get update payload
  │   └─ Identify changed fields
  │
  ├─ [4] Validate Update Data
  │   ├─ Check field-level constraints
  │   ├─ Verify format requirements
  │   └─ Check business rule compliance
  │
  ├─ [DECISION] Validation Passed?
  │   ├─ NO → Return error details → END
  │   └─ YES ↓
  │
  ├─ [5] Check Special Validations
  │   ├─ If email changed:
  │   │   ├─ Verify email unique
  │   │   └─ Queue verification email
  │   ├─ If phone changed:
  │   │   └─ Queue verification SMS
  │   └─ If photo uploaded:
  │       ├─ Process image
  │       └─ Upload to CDN
  │
  ├─ [6] Create Audit Log Entry
  │   ├─ Record all changed fields
  │   ├─ Capture before/after values
  │   ├─ Timestamp change
  │   └─ Note request source (API/UI)
  │
  ├─ [7] Update Profile in Database
  │   ├─ Begin transaction
  │   ├─ Apply updates
  │   ├─ Update timestamp
  │   └─ Commit transaction
  │
  ├─ [8] Invalidate Cache
  │   └─ Remove profile from Redis/cache
  │
  ├─ [9] Update Search Index (if preferences changed)
  │   ├─ Refresh property recommendations
  │   ├─ Update favorite properties
  │   └─ Recalculate matching listings
  │
  ├─ [10] Send Notification
  │   ├─ Email: Profile updated confirmation
  │   ├─ Push: If on mobile
  │   └─ Include change summary
  │
  ├─ [11] Return Updated Profile
  │   └─ Include all current information
  │
  └─ END
```

**Success Criteria:**

| Criterion | Measurement | Target |
|-----------|-------------|--------|
| Update Completion | % updates processed successfully | ≥ 99% |
| Data Consistency | Database vs cache mismatch | 0 |
| Validation Accuracy | Incorrect rejections | < 0.1% |
| Performance | Update latency | < 1 second |
| Audit Trail Completeness | All changes logged | 100% |
| Notification Delivery | Profile update confirmations sent | ≥ 98% |

**Data Fields Tracked:**

```
Protected Fields (Require Verification):
├─ Email address
├─ Phone number
└─ Password

Preference Fields (No Verification):
├─ Price range
├─ Property types
├─ Locations
└─ Notification settings

Profile Fields (Optional):
├─ First name
├─ Last name
├─ Date of birth
├─ Profile photo
└─ Bio/About
```

---

### 1.3 Customer Search and Favorites Management

**Process ID:** PROC-CM-003  
**Process Name:** Customer Search and Favorites Management  
**Process Category:** Customer Management  
**Process Owner:** Product Team  
**Version:** 1.0
…