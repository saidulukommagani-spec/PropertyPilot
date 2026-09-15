# PropertyPilot - Complete API Catalog

**Document Version:** 1.0  
**Date:** 2026-08-31  
**Status:** Production Ready  
**API Version:** v1  
**Base URL:** `https://api.propertyilot.com/v1`

---

## Table of Contents

1. [Authentication APIs](#1-authentication-apis)
2. [Customer APIs](#2-customer-apis)
3. [Property APIs](#3-property-apis)
4. [Service APIs](#4-service-apis)
5. [Verification APIs](#5-verification-apis)
6. [Monitoring APIs](#6-monitoring-apis)
7. [Agent APIs](#7-agent-apis)
8. [Report APIs](#8-report-apis)
9. [Subscription APIs](#9-subscription-apis)
10. [Payment APIs](#10-payment-apis)
11. [Notification APIs](#11-notification-apis)
12. [Vendor APIs](#12-vendor-apis)
13. [Admin APIs](#13-admin-apis)

---

## 1. AUTHENTICATION APIs

### 1.1 User Registration

**API Name:** Register User  
**Method:** POST  
**Endpoint:** `/auth/register`  
**Authorization Required:** None

**Request:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1-555-0123",
  "userType": "customer|agent|vendor",
  "acceptTerms": true
}
```

**Response (201 Created):**
```json
{
  "userId": "usr_12345abcde",
  "email": "john.doe@example.com",
  "userType": "customer",
  "status": "active",
  "createdAt": "2026-08-31T10:30:00Z",
  "verificationStatus": "pending_email"
}
```

**Validation Rules:**
- Email: Valid RFC 5322 format, unique in system
- Password: Min 8 chars, 1 uppercase, 1 lowercase, 1 number, 1 special char
- Phone: E.164 format, valid country code
- User Type: Must be one of allowed types
- Terms: Must be true

**Error Codes:**
- `400` - Invalid input data
- `409` - Email already exists
- `422` - Validation failed (specific field errors)
- `500` - Server error

---

### 1.2 User Login

**API Name:** Login User  
**Method:** POST  
**Endpoint:** `/auth/login`  
**Authorization Required:** None

**Request:**
```json
{
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "deviceId": "device_abc123xyz",
  "deviceName": "iPhone 14 Pro",
  "rememberMe": true
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "ref_token_xyz789",
  "expiresIn": 3600,
  "user": {
    "userId": "usr_12345abcde",
    "email": "john.doe@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "userType": "customer",
    "profileStatus": "complete",
    "mfaEnabled": false
  }
}
```

**Validation Rules:**
- Email: Must exist in system
- Password: Must match stored hash
- Device ID: Unique identifier for device
- Max 5 concurrent sessions per user

**Error Codes:**
- `400` - Invalid credentials
- `401` - Unauthorized (bad password)
- `403` - Account locked after 5 failed attempts
- `404` - User not found
- `429` - Too many login attempts

---

### 1.3 Refresh Token

**API Name:** Refresh Access Token  
**Method:** POST  
**Endpoint:** `/auth/refresh`  
**Authorization Required:** None (uses Refresh Token)

**Request:**
```json
{
  "refreshToken": "ref_token_xyz789"
}
```

**Response (200 OK):**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "expiresIn": 3600
}
```

**Validation Rules:**
- Refresh Token: Must be valid and not expired
- Token age: Max 30 days
- Single use tokens: Mark as used after consumption

**Error Codes:**
- `400` - Invalid refresh token
- `401` - Token expired
- `403` - Token revoked

---

### 1.4 Logout

**API Name:** Logout User  
**Method:** POST  
**Endpoint:** `/auth/logout`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "deviceId": "device_abc123xyz",
  "allDevices": false
}
```

**Response (200 OK):**
```json
{
  "message": "Successfully logged out",
  "sessionsTerminated": 1
}
```

**Validation Rules:**
- Token: Must be valid and not expired
- Device ID: Optional (if provided, logout only that device)
- All Devices: If true, logout all sessions

**Error Codes:**
- `401` - Unauthorized
- `400` - Invalid request

---

### 1.5 Enable Two-Factor Authentication

**API Name:** Setup MFA  
**Method:** POST  
**Endpoint:** `/auth/mfa/setup`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "method": "totp|sms",
  "phoneNumber": "+1-555-0123"
}
```

**Response (200 OK):**
```json
{
  "secret": "JBSWY3DPEBLW64TMMQ======",
  "qrCode": "data:image/png;base64,iVBORw0KGgoAAAANS...",
  "backupCodes": [
    "12345-abcde",
    "67890-fghij"
  ],
  "mfaEnabled": false,
  "confirmedAt": null
}
```

**Validation Rules:**
- Method: TOTP or SMS only
- Phone: Valid E.164 format
- Backup codes: Generate 10 unique codes

**Error Codes:**
- `400` - Invalid method
- `401` - Unauthorized
- `409` - MFA already enabled

---

### 1.6 Verify MFA Code

**API Name:** Verify MFA  
**Method:** POST  
**Endpoint:** `/auth/mfa/verify`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "code": "123456"
}
```

**Response (200 OK):**
```json
{
  "mfaEnabled": true,
  "message": "MFA successfully enabled"
}
```

**Validation Rules:**
- Code: 6 digits, generated within last 30 seconds
- Backup Code: 11 characters with hyphen

**Error Codes:**
- `400` - Invalid code format
- `401` - Code expired
- `403` - Invalid code

---

### 1.7 Password Reset Request

**API Name:** Request Password Reset  
**Method:** POST  
**Endpoint:** `/auth/password/reset-request`  
**Authorization Required:** None

**Request:**
```json
{
  "email": "john.doe@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset link sent to email",
  "expiresIn": 3600
}
```

**Validation Rules:**
- Email: Must exist in system
- Rate limit: Max 3 requests per hour per email
- Link expiration: 1 hour

**Error Codes:**
- `404` - Email not found
- `429` - Too many reset requests
- `500` - Email sending failed

---

### 1.8 Reset Password with Token

**API Name:** Reset Password  
**Method:** POST  
**Endpoint:** `/auth/password/reset`  
**Authorization Required:** None

**Request:**
```json
{
  "token": "reset_token_abc123xyz",
  "newPassword": "NewSecurePass123!"
}
```

**Response (200 OK):**
```json
{
  "message": "Password successfully reset",
  "redirectUrl": "/login"
}
```

**Validation Rules:**
- Token: Must be valid and not expired
- Password: Min 8 chars, complexity requirements
- Invalidate all existing sessions after reset

**Error Codes:**
- `400` - Invalid token or password
- `401` - Token expired
- `403` - Token already used

---

## 2. CUSTOMER APIs

### 2.1 Create Customer Profile

**API Name:** Create Customer  
**Method:** POST  
**Endpoint:** `/customers`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```json
{
  "userId": "usr_12345abcde",
  "dateOfBirth": "1990-05-15",
  "profilePhoto": "https://cdn.propertyilot.com/photos/usr_12345abcde.jpg",
  "preferences": {
    "priceRange": {
      "min": 250000,
      "max": 750000
    },
    "propertyTypes": ["single_family", "condo"],
    "locations": ["New York", "Los Angeles"],
    "notifications": {
      "email": true,
      "sms": true,
      "push": true
    }
  },
  "financing": {
    "preApprovedAmount": 500000,
    "preApprovalExpiry": "2027-02-28"
  }
}
```

**Response (201 Created):**
```json
{
  "customerId": "cust_98765zyxwv",
  "userId": "usr_12345abcde",
  "profileStatus": "complete",
  "preferences": {
    "priceRange": {
      "min": 250000,
      "max": 750000
    },
    "propertyTypes": ["single_family", "condo"],
    "locations": ["New York", "Los Angeles"]
  },
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- User ID: Must exist and not have customer profile
- Date of Birth: Minimum age 18 years
- Price Range: Min <= Max
- Property Types: Valid predefined types
- Pre-approval: Expires in future

**Error Codes:**
- `400` - Invalid input data
- `409` - Profile already exists
- `401` - Unauthorized
- `422` - Validation failed

---

### 2.2 Get Customer Profile

**API Name:** Get Customer  
**Method:** GET  
**Endpoint:** `/customers/{customerId}`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /customers/cust_98765zyxwv
```

**Response (200 OK):**
```json
{
  "customerId": "cust_98765zyxwv",
  "userId": "usr_12345abcde",
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "phoneNumber": "+1-555-0123",
  "dateOfBirth": "1990-05-15",
  "profilePhoto": "https://cdn.propertyilot.com/photos/usr_12345abcde.jpg",
  "preferences": {
    "priceRange": {
      "min": 250000,
      "max": 750000
    },
    "propertyTypes": ["single_family", "condo"],
    "locations": ["New York", "Los Angeles"],
    "notifications": {
      "email": true,
      "sms": true,
      "push": true
    }
  },
  "financing": {
    "preApprovedAmount": 500000,
    "preApprovalExpiry": "2027-02-28"
  },
  "createdAt": "2026-08-31T10:30:00Z",
  "updatedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Customer ID: Must exist
- Authorization: User can only access own profile or admin override

**Error Codes:**
- `404` - Customer not found
- `401` - Unauthorized
- `403` - Forbidden

---

### 2.3 Update Customer Profile

**API Name:** Update Customer  
**Method:** PUT  
**Endpoint:** `/customers/{customerId}`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "phoneNumber": "+1-555-0124",
  "preferences": {
    "priceRange": {
      "min": 300000,
      "max": 800000
    },
    "locations": ["New York", "Los Angeles", "San Francisco"]
  }
}
```

**Response (200 OK):**
```json
{
  "customerId": "cust_98765zyxwv",
  "message": "Profile updated successfully",
  "updatedAt": "2026-08-31T11:45:00Z"
}
```

**Validation Rules:**
- All fields optional but validated if provided
- Phone: E.164 format
- Audit: Track all changes with timestamps

**Error Codes:**
- `400` - Invalid input
- `404` - Customer not found
- `401` - Unauthorized
- `409` - Conflict (e.g., duplicate email)

---

### 2.4 Delete Customer Account

**API Name:** Delete Customer  
**Method:** DELETE  
**Endpoint:** `/customers/{customerId}`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "password": "SecurePass123!",
  "reason": "optional_deletion_reason"
}
```

**Response (200 OK):**
```json
{
  "message": "Account scheduled for deletion",
  "deletionDate": "2026-09-14T00:00:00Z",
  "gracePeriod": 14
}
```

**Validation Rules:**
- Password: Must match user's password
- Grace Period: 14 days before actual deletion
- Active Transactions: Cannot delete with pending deals

**Error Codes:**
- `400` - Invalid password
- `403` - Cannot delete (active transactions)
- `404` - Customer not found

---

### 2.5 Get Customer Favorites

**API Name:** Get Favorite Properties  
**Method:** GET  
**Endpoint:** `/customers/{customerId}/favorites`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /customers/cust_98765zyxwv/favorites?limit=20&offset=0&sort=createdAt&order=desc
```

**Response (200 OK):**
```json
{
  "favorites": [
    {
      "favoriteId": "fav_abc123xyz",
      "propertyId": "prop_11111aaaaa",
      "address": "123 Main St, New York, NY 10001",
      "price": 525000,
      "bedrooms": 3,
      "bathrooms": 2.5,
      "thumbnail": "https://cdn.propertyilot.com/images/prop_11111aaaaa_thumb.jpg",
      "savedAt": "2026-08-30T15:20:00Z"
    }
  ],
  "total": 45,
  "limit": 20,
  "offset": 0
}
```

**Validation Rules:**
- Limit: Max 100, default 20
- Offset: Pagination support
- Sort: createdAt, address, price
- Order: asc, desc

**Error Codes:**
- `404` - Customer not found
- `400` - Invalid pagination params

---

### 2.6 Add Property to Favorites

**API Name:** Add Favorite  
**Method:** POST  
**Endpoint:** `/customers/{customerId}/favorites`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "propertyId": "prop_11111aaaaa"
}
```

**Response (201 Created):**
```json
{
  "favoriteId": "fav_abc123xyz",
  "propertyId": "prop_11111aaaaa",
  "customerId": "cust_98765zyxwv",
  "savedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Property ID: Must exist
- Duplicate: Cannot add same property twice
- Max Favorites: 500 per customer

**Error Codes:**
- `400` - Invalid property or duplicate
- `404` - Property or customer not found
- `409` - Already favorited

---

### 2.7 Remove Property from Favorites

**API Name:** Remove Favorite  
**Method:** DELETE  
**Endpoint:** `/customers/{customerId}/favorites/{propertyId}`  
**Authorization Required:** Bearer Token

**Request:**
```
DELETE /customers/cust_98765zyxwv/favorites/prop_11111aaaaa
```

**Response (204 No Content)**

**Error Codes:**
- `404` - Favorite not found
- `401` - Unauthorized

---

### 2.8 Get Customer Search History

**API Name:** Get Search History  
**Method:** GET  
**Endpoint:** `/customers/{customerId}/search-history`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /customers/cust_98765zyxwv/search-history?limit=50&days=30
```

**Response (200 OK):**
```json
{
  "searches": [
    {
      "searchId": "srch_xyz789abc",
      "query": {
        "location": "New York",
        "priceMin": 250000,
        "priceMax": 750000,
        "propertyTypes": ["single_family", "condo"]
      },
      "resultCount": 245,
      "searchedAt": "2026-08-31T09:15:00Z"
    }
  ],
  "total": 23
}
```

**Validation Rules:**
- Days: Max 90 days back
- Limit: Max 100 results

**Error Codes:**
- `404` - Customer not found

---

## 3. PROPERTY APIs

### 3.1 Search Properties

**API Name:** Search Properties  
**Method:** GET  
**Endpoint:** `/properties/search`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /properties/search?
  location=New%20York&
  priceMin=250000&
  priceMax=750000&
  bedrooms=3&
  bathrooms=2&
  propertyTypes=single_family,condo&
  sort=price&
  order=asc&
  limit=50&
  offset=0&
  lat=40.7128&
  lng=-74.0060&
  radius=5
```

**Response (200 OK):**
```json
{
  "properties": [
    {
      "propertyId": "prop_11111aaaaa",
      "address": "123 Main St, New York, NY 10001",
      "city": "New York",
      "state": "NY",
      "zipCode": "10001",
      "latitude": 40.7128,
      "longitude": -74.0060,
      "price": 525000,
      "propertyType": "single_family",
      "bedrooms": 3,
      "bathrooms": 2.5,
      "squareFeet": 2500,
      "yearBuilt": 1995,
      "lotSize": 0.25,
      "thumbnail": "https://cdn.propertyilot.com/images/prop_11111aaaaa_thumb.jpg",
      "listing": {
        "status": "active",
        "listedDate": "2026-08-15",
        "agentId": "agent_11111aaaaa",
        "agentName": "Jane Smith"
      }
    }
  ],
  "total": 245,
  "limit": 50,
  "offset": 0,
  "executedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Location: Geocode to lat/lng if text provided
- Price Range: Min <= Max
- Bedrooms/Bathrooms: Non-negative integers
- Radius: 0.5 to 50 miles (default 5)
- Limit: Max 100, default 50
- Result Cache: 5 minutes

**Error Codes:**
- `400` - Invalid search parameters
- `404` - No results found
- `429` - Rate limited

---

### 3.2 Get Property Details

**API Name:** Get Property  
**Method:** GET  
**Endpoint:** `/properties/{propertyId}`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /properties/prop_11111aaaaa
```

**Response (200 OK):**
```json
{
  "propertyId": "prop_11111aaaaa",
  "address": "123 Main St, New York, NY 10001",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "county": "New York",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "price": 525000,
  "propertyType": "single_family",
  "bedrooms": 3,
  "bathrooms": 2.5,
  "squareFeet": 2500,
  "yearBuilt": 1995,
  "lotSize": 0.25,
  "hoaFee": 150,
  "taxAmount": 8500,
  "description": "Beautiful home with updated kitchen and hardwood floors",
  "features": [
    "Swimming Pool",
    "Garage",
    "Deck",
    "Garden"
  ],
  "images": [
    {
      "url": "https://cdn.propertyilot.com/images/prop_11111aaaaa_1.jpg",
      "order": 1,
      "type": "exterior"
    },
    {
      "url": "https://cdn.propertyilot.com/images/prop_11111aaaaa_2.jpg",
      "order": 2,
      "type": "interior"
    }
  ],
  "virtualTour": "https://tours.propertyilot.com/prop_11111aaaaa",
  "floorPlan": "https://cdn.propertyilot.com/floorplans/prop_11111aaaaa.pdf",
  "listing": {
    "status": "active",
    "listedDate": "2026-08-15",
    "daysOnMarket": 16,
    "agentId": "agent_11111aaaaa",
    "agentName": "Jane Smith",
    "agentPhone": "+1-555-0100",
    "agentEmail": "jane.smith@brokerage.com",
    "brokerageId": "broker_11111aaaaa",
    "brokerageName": "Smith & Associates"
  },
  "openHouses": [
    {
      "date": "2026-09-02",
      "startTime": "10:00",
      "endTime": "12:00",
      "agentId": "agent_11111aaaaa"
    }
  ],
  "marketStats": {
    "averagePrice": 515000,
    "medianPrice": 520000,
    "pricePerSquareFoot": 210,
    "daysOnMarketAverage": 18,
    "priceReduction": 0
  },
  "isFavorite": true,
  "viewedAt": "2026-08-31T09:00:00Z",
  "createdAt": "2026-08-15T10:30:00Z",
  "updatedAt": "2026-08-31T08:00:00Z"
}
```

**Validation Rules:**
- Property ID: Must exist
- Cache: 1-minute TTL
- View Tracking: Log view if authenticated

**Error Codes:**
- `404` - Property not found
- `410` - Property delisted

---

### 3.3 Get Comparable Properties

**API Name:** Get Comparable Properties  
**Method:** GET  
**Endpoint:** `/properties/{propertyId}/comparables`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /properties/prop_11111aaaaa/comparables?limit=10&radius=1
```

**Response (200 OK):**
```json
{
  "subject": {
    "propertyId": "prop_11111aaaaa",
    "price": 525000,
    "squareFeet": 2500,
    "bedrooms": 3,
    "bathrooms": 2.5
  },
  "comparables": [
    {
      "propertyId": "prop_22222bbbbb",
      "address": "456 Oak Ave, New York, NY 10001",
      "distance": 0.3,
      "price": 520000,
      "squareFeet": 2480,
      "bedrooms": 3,
      "bathrooms": 2.5,
      "saleDate": "2026-08-20",
      "pricePerSquareFoot": 210
    }
  ],
  "analysis": {
    "averagePrice": 520000,
    "medianPrice": 522000,
    "priceRange": {
      "low": 510000,
      "high": 530000
    },
    "estimatedValue": 522000
  }
}
```

**Validation Rules:**
- Property ID: Must be residential
- Limit: Max 20, default 10
- Radius: 0.5 to 5 miles
- Recent Sales: Within last 6 months

**Error Codes:**
- `404` - Property not found
- `400` - Invalid parameters

---

### 3.4 Create Listing

**API Name:** Create Property Listing  
**Method:** POST  
**Endpoint:** `/properties`  
**Authorization Required:** Bearer Token (Agent)

**Request:**
```json
{
  "address": "123 Main St",
  "city": "New York",
  "state": "NY",
  "zipCode": "10001",
  "county": "New York",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "price": 525000,
  "propertyType": "single_family",
  "bedrooms": 3,
  "bathrooms": 2.5,
  "squareFeet": 2500,
  "yearBuilt": 1995,
  "lotSize": 0.25,
  "hoaFee": 150,
  "taxAmount": 8500,
  "description": "Beautiful home with updated kitchen and hardwood floors",
  "features": [
    "Swimming Pool",
    "Garage",
    "Deck",
    "Garden"
  ],
  "images": [
    {
      "url": "https://cdn.propertyilot.com/images/upload_1.jpg",
      "order": 1,
      "type": "exterior"
    }
  ],
  "virtualTour": "https://tours.propertyilot.com/new_tour_123",
  "floorPlan": "https://cdn.propertyilot.com/floorplans/new_plan_123.pdf"
}
```

**Response (201 Created):**
```json
{
  "propertyId": "prop_11111aaaaa",
  "address": "123 Main St, New York, NY 10001",
  "price": 525000,
  "status": "active",
  "listingStatus": "pending_mls",
  "mlsNumber": "pending",
  "listedDate": "2026-08-31T10:30:00Z",
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Address: Geocode validation required
- Price: Positive number
- Bedrooms/Bathrooms: Non-negative
- Images: Min 1, max 50 per listing
- Square Feet: Min 100
- Year Built: 1800-2026

**Error Codes:**
- `400` - Invalid data
- `401` - Unauthorized
- `403` - Agent permissions
- `422` - Validation failed

---

### 3.5 Update Listing

**API Name:** Update Property Listing  
**Method:** PUT  
**Endpoint:** `/properties/{propertyId}`  
**Authorization Required:** Bearer Token (Agent)

**Request:**
```json
{
  "price": 520000,
  "description": "Newly updated description",
  "features": ["Swimming Pool", "Garage", "Deck"]
}
```

**Response (200 OK):**
```json
{
  "propertyId": "prop_11111aaaaa",
  "message": "Property updated successfully",
  "updatedAt": "2026-08-31T11:45:00Z"
}
```

**Validation Rules:**
- Authorization: Agent must own listing
- Price History: Track all changes
- Audit: Log all modifications
- Update Index: Refresh search within 5 minutes

**Error Codes:**
- `400` - Invalid data
- `401` - Unauthorized
- `403` - Not listing agent
- `404` - Property not found

---

### 3.6 Change Listing Status

**API Name:** Update Listing Status  
**Method:** PATCH  
**Endpoint:** `/properties/{propertyId}/status`  
**Authorization Required:** Bearer Token (Agent)

**Request:**
```json
{
  "status": "active|withdrawn|sold|expired",
  "reason": "property_sold",
  "salePrice": 525000,
  "closeDate": "2026-09-15"
}
```

**Response (200 OK):**
```json
{
  "propertyId": "prop_11111aaaaa",
  "status": "sold",
  "salePrice": 525000,
  "closeDate": "2026-09-15",
  "updatedAt": "2026-08-31T12:00:00Z"
}
```

**Validation Rules:**
- Status transitions: active → (withdrawn|sold|expired)
- Sold: Requires sale price and close date
- Audit: Track status changes with timestamps

**Error Codes:**
- `400` - Invalid status transition
- `401` - Unauthorized
- `404` - Property not found

---

### 3.7 Get Property Views

**API Name:** Get Property Views Analytics  
**Method:** GET  
**Endpoint:** `/properties/{propertyId}/views`  
**Authorization Required:** Bearer Token (Agent)

**Request:**
```
GET /properties/prop_11111aaaaa/views?days=7&limit=50
```

**Response (200 OK):**
```json
{
  "propertyId": "prop_11111aaaaa",
  "totalViews": 523,
  "uniqueViewers": 412,
  "period": "7_days",
  "views": [
    {
      "viewId": "view_abc123xyz",
      "customerId": "cust_98765zyxwv",
      "viewedAt": "2026-08-31T10:30:00Z",
      "duration": 180,
      "source": "search|direct|recommendation"
    }
  ],
  "dailyBreakdown": [
    {
      "date": "2026-08-31",
      "views": 78,
      "uniqueViewers": 65
    }
  ]
}
```

**Validation Rules:**
- Days: Max 90
- Agent: Can only view own properties
- Privacy: No PII in views

**Error Codes:**
- `401` - Unauthorized
- `403` - Not listing agent
- `404` - Property not found

---

## 4. SERVICE APIs

### 4.1 Schedule Property Viewing

**API Name:** Schedule Viewing  
**Method:** POST  
**Endpoint:** `/services/viewings`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "propertyId": "prop_11111aaaaa",
  "customerId": "cust_98765zyxwv",
  "agentId": "agent_11111aaaaa",
  "preferredDateTime": "2026-09-02T14:00:00Z",
  "duration": 30,
  "notes": "Interested in seeing kitchen"
}
```

**Response (201 Created):**
```json
{
  "viewingId": "view_sched_abc123xyz",
  "propertyId": "prop_11111aaaaa",
  "customerId": "cust_98765zyxwv",
  "agentId": "agent_11111aaaaa",
  "scheduledDateTime": "2026-09-02T14:00:00Z",
  "duration": 30,
  "status": "confirmed",
  "confirmationCode": "VIEW-2026-ABC123",
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Property ID: Must exist and be active
- Customer ID: Must exist
- Agent ID: Must exist
- DateTime: Min 24 hours in future
- Duration: 15-120 minutes
- Conflict Check: Prevent double-booking

**Error Codes:**
- `400` - Invalid parameters
- `404` - Resource not found
- `409` - Time slot unavailable
- `422` - Validation failed

---

### 4.2 Cancel Viewing

**API Name:** Cancel Viewing  
**Method:** DELETE  
**Endpoint:** `/services/viewings/{viewingId}`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "reason": "customer_requested",
  "notes": "Found another property"
}
```

**Response (200 OK):**
```json
{
  "viewingId": "view_sched_abc123xyz",
  "status": "cancelled",
  "cancelledAt": "2026-08-31T10:45:00Z"
}
```

**Validation Rules:**
- Viewing ID: Must exist
- Status: Only cancel if not completed
- Notice: Min 24 hours notice
- Audit: Log cancellation reason

**Error Codes:**
- `400` - Cannot cancel (too close to appointment)
- `404` - Viewing not found
- `409` - Already completed

---

### 4.3 Submit Property Offer

**API Name:** Submit Offer  
**Method:** POST  
**Endpoint:** `/services/offers`  
**Authorization Required:** Bearer Token (Customer)

**Request:**
```json
{
  "propertyId": "prop_11111aaaaa",
  "customerId": "cust_98765zyxwv",
  "offerPrice": 520000,
  "earnestMoneyDeposit": 26000,
  "closingDate": "2026-10-15",
  "contingencies": {
    "inspection": true,
    "appraisal": true,
    "financing": true
  },
  "notes": "Subject to inspection"
}
```

**Response (201 Created):**
```json
{
  "offerId": "offer_abc123xyz",
  "propertyId": "prop_11111aaaaa",
  "customerId": "cust_98765zyxwv",
  "offerPrice": 520000,
  "earnestMoneyDeposit": 26000,
  "closingDate": "2026-10-15",
  "status": "submitted",
  "createdAt": "2026-08-31T10:30:00Z",
  "expiresAt": "2026-09-02T10:30:00Z"
}
```

**Validation Rules:**
- Offer Price: Min 50% of listing price
- Earnest Money: Typically 1-5% of offer
- Closing Date: 30-60 days in future
- Customer: Must have verified financing
- Duplicate Check: Max 1 active offer per customer per property

**Error Codes:**
- `400` - Invalid offer terms
- `401` - Unauthorized
- `403` - Insufficient financing
- `404` - Property not found
- `409` - Active offer exists

---

### 4.4 Accept/Reject Offer

**API Name:** Respond to Offer  
**Method:** PATCH  
**Endpoint:** `/services/offers/{offerId}`  
**Authorization Required:** Bearer Token (Agent/Seller)

**Request:**
```json
{
  "action": "accept|reject|counter",
  "counterOffer": {
    "price": 522000,
    "closingDate": "2026-10-10",
    "earnestMoneyDeposit": 26000
  }
}
```

**Response (200 OK):**
```json
{
  "offerId": "offer_abc123xyz",
  "status": "accepted|rejected|countered",
  "respondedAt": "2026-08-31T11:00:00Z",
  "nextSteps": [
    {
      "task": "Submit offer to seller",
      "deadline": "2026-09-02"
    }
  ]
}
```

**Validation Rules:**
- Authorization: Agent or seller only
- Offer Status: Only pending offers can be responded to
- Counter Terms: Must be valid if countered
- Expiration: Cannot respond after expiration

**Error Codes:**
- `400` - Invalid action
- `401` - Unauthorized
- `404` - Offer not found
- `409` - Offer expired

---

### 4.5 Get Offer History

**API Name:** Get Offers for Property  
**Method:** GET  
**Endpoint:** `/properties/{propertyId}/offers`  
**Authorization Required:** Bearer Token (Agent)

**Request:**
```
GET /properties/prop_11111aaaaa/offers?status=all&limit=50
```

**Response (200 OK):**
```json
{
  "propertyId": "prop_11111aaaaa",
  "offers": [
    {
      "offerId": "offer_abc123xyz",
      "customerId": "cust_98765zyxwv",
      "offerPrice": 520000,
      "earnestMoneyDeposit": 26000,
      "closingDate": "2026-10-15",
      "status": "accepted",
      "createdAt": "2026-08-31T10:30:00Z",
      "respondedAt": "2026-08-31T11:00:00Z"
    }
  ],
  "total": 3,
  "accepted": 1,
  "rejected": 2,
  "pending": 0
}
```

**Validation Rules:**
- Authorization: Listing agent only
- Status Filter: all, pending, accepted, rejected, countered

**Error Codes:**
- `401` - Unauthorized
- `404` - Property not found

---

## 5. VERIFICATION APIs

### 5.1 Verify Email Address

**API Name:** Verify Email  
**Method:** POST  
**Endpoint:** `/verification/email`  
**Authorization Required:** None

**Request:**
```json
{
  "token": "email_verification_token_xyz"
}
```

**Response (200 OK):**
```json
{
  "message": "Email verified successfully",
  "email": "john.doe@example.com",
  "verifiedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Token: Must be valid and not expired (24 hours)
- Single Use: Token expires after first use
- Email: Must exist in system

**Error Codes:**
- `400` - Invalid token
- `401` - Token expired
- `404` - Email not found

---

### 5.2 Verify Phone Number (SMS)

**API Name:** Verify Phone  
**Method:** POST  
**Endpoint:** `/verification/phone`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "code": "123456"
}
```

**Response (200 OK):**
```json
{
  "message": "Phone number verified",
  "phoneNumber": "+1-555-0123",
  "verifiedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Code: 6 digits sent via SMS
- Expiration: 10 minutes
- Attempts: Max 5 failed attempts
- Rate Limit: Max 3 verification requests per hour

**Error Codes:**
- `400` - Invalid code format
- `401` - Code expired
- `403` - Too many attempts
- `429` - Rate limited

---

### 5.3 Verify Identity (KYC)

**API Name:** Verify Identity  
**Method:** POST  
**Endpoint:** `/verification/identity`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "documentType": "drivers_license|passport|national_id",
  "documentNumber": "DL123456789",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1990-05-15",
  "expirationDate": "2027-05-15"
}
```

**Response (200 OK):**
```json
{
  "verificationId": "kyc_abc123xyz",
  "status": "verified|pending_review|failed",
  "createdAt": "2026-08-31T10:30:00Z",
  "expiresAt": "2027-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Document Type: Valid government-issued ID
- Document Number: Format validation
- Expiration: Must be valid (not expired)
- Name Match: First & last name match ID
- Age: Must be 18+

**Error Codes:**
- `400` - Invalid document
- `401` - Unauthorized
- `422` - Verification failed

---

### 5.4 Verify Financing

**API Name:** Verify Pre-Approval  
**Method:** POST  
**Endpoint:** `/verification/financing`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "lenderName": "First National Bank",
  "loanOfficerName": "Sarah Johnson",
  "preApprovedAmount": 500000,
  "preApprovalDate": "2026-08-15",
  "expirationDate": "2026-11-15",
  "documentUrl": "https://cdn.propertyilot.com/docs/preapproval_123.pdf"
}
```

**Response (201 Created):**
```json
{
  "verificationId": "fin_abc123xyz",
  "customerId": "cust_98765zyxwv",
  "preApprovedAmount": 500000,
  "status": "verified",
  "expiresAt": "2026-11-15",
  "verifiedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Amount: Positive number, reasonable range
- Dates: Approval date must be recent (within 90 days)
- Expiration: Must be in future
- Document: PDF validation

**Error Codes:**
- `400` - Invalid data
- `401` - Unauthorized
- `422` - Document validation failed

---

### 5.5 Verify Property Ownership

**API Name:** Verify Ownership  
**Method:** POST  
**Endpoint:** `/verification/property-ownership`  
**Authorization Required:** Bearer Token (Agent)

**Request:**
```json
{
  "propertyId": "prop_11111aaaaa",
  "ownerName": "John Doe",
  "documentType": "deed|tax_assessment|title_insurance",
  "documentUrl": "https://cdn.propertyilot.com/docs/deed_123.pdf"
}
```

**Response (201 Created):**
```json
{
  "verificationId": "own_abc123xyz",
  "propertyId": "prop_11111aaaaa",
  "status": "verified|pending_review",
  "verifiedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Owner Name: Must match document
- Document: Valid PDF, readable
- Title Search: Cross-reference public records (optional)
- Review Period: 2-3 business days

**Error Codes:**
- `400` - Invalid document
- `401` - Unauthorized
- `404` - Property not found
- `422` - Verification failed

---

## 6. MONITORING APIs

### 6.1 Get System Health

**API Name:** Get System Health  
**Method:** GET  
**Endpoint:** `/monitoring/health`  
**Authorization Required:** None

**Request:**
```
GET /monitoring/health
```

**Response (200 OK):**
```json
{
  "status": "healthy",
  "timestamp": "2026-08-31T10:30:00Z",
  "uptime": 99.98,
  "services": [
    {
      "name": "API Gateway",
      "status": "healthy",
      "responseTime": 50,
      "lastCheck": "2026-08-31T10:30:00Z"
    },
    {
      "name": "Database",
      "status": "healthy",
      "responseTime": 25,
      "lastCheck": "2026-08-31T10:30:00Z"
    },
    {
      "name": "Payment Gateway",
      "status": "healthy",
      "responseTime": 500,
      "lastCheck": "2026-08-31T10:30:00Z"
    }
  ]
}
```

**Validation Rules:**
- Cache: 30-second TTL
- Aggregated check: All critical services
- Health Status: healthy, degraded, down

**Error Codes:**
- `503` - Service unavailable

---

### 6.2 Get Performance Metrics

**API Name:** Get Metrics  
**Method:** GET  
**Endpoint:** `/monitoring/metrics`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```
GET /monitoring/metrics?period=1h&resolution=1m
```

**Response (200 OK):**
```json
{
  "period": "1_hour",
  "resolution": "1_minute",
  "metrics": {
    "requests": {
      "total": 125000,
      "successful": 124500,
      "failed": 500,
      "errorRate": 0.4
    },
    "latency": {
      "p50": 150,
      "p95": 450,
      "p99": 1200,
      "avg": 200
    },
    "throughput": {
      "requests_per_second": 34.7
    }
  }
}
```

**Validation Rules:**
- Period: 1h, 6h, 24h, 7d, 30d
- Resolution: 1m, 5m, 1h
- Authorization: Admin only

**Error Codes:**
- `401` - Unauthorized
- `400` - Invalid parameters

---

### 6.3 Get Service Status

**API Name:** Get Service Status Page  
**Method:** GET  
**Endpoint:** `/monitoring/status`  
**Authorization Required:** None

**Request:**
```
GET /monitoring/status
```

**Response (200 OK):**
```json
{
  "pageStatus": "all_systems_operational",
  "lastUpdated": "2026-08-31T10:30:00Z",
  "services": [
    {
      "name": "Customer Mobile App",
      "status": "operational",
      "uptime": 99.98
    },
    {
      "name": "Agent Mobile App",
      "status": "operational",
      "uptime": 99.99
    },
    {
      "name": "Payment Processing",
      "status": "operational",
      "uptime": 99.95
    },
    {
      "name": "Notification Service",
      "status": "operational",
      "uptime": 99.92
    }
  ],
  "incidents": []
}
```

**Validation Rules:**
- Public endpoint (no auth required)
- Cache: 5-minute TTL
- Real-time updates for critical issues

**Error Codes:**
- `503` - Service degraded (but endpoint still available)

---

### 6.4 Get Error Logs

**API Name:** Get Error Logs  
**Method:** GET  
**Endpoint:** `/monitoring/errors`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```
GET /monitoring/errors?limit=100&hours=24&service=payment
```

**Response (200 OK):**
```json
{
  "errors": [
    {
      "errorId": "err_abc123xyz",
      "timestamp": "2026-08-31T10:15:00Z",
      "service": "payment",
      "errorCode": "PAYMENT_TIMEOUT",
      "message": "Payment processing timeout",
      "severity": "high",
      "userId": "usr_12345abcde",
      "stackTrace": "..."
    }
  ],
  "total": 15,
  "limit": 100
}
```

**Validation Rules:**
- Hours: Max 90 days back
- Service Filter: Optional
- Limit: Max 1000
- PII: Encrypted in logs

**Error Codes:**
- `401` - Unauthorized
- `400` - Invalid parameters

---

## 7. AGENT APIs

### 7.1 Get Agent Profile

**API Name:** Get Agent  
**Method:** GET  
**Endpoint:** `/agents/{agentId}`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /agents/agent_11111aaaaa
```

**Response (200 OK):**
```json
{
  "agentId": "agent_11111aaaaa",
  "userId": "usr_11111aaaaa",
  "firstName": "Jane",
  "lastName": "Smith",
  "email": "jane.smith@brokerage.com",
  "phoneNumber": "+1-555-0100",
  "profilePhoto": "https://cdn.propertyilot.com/agents/agent_11111aaaaa.jpg",
  "brokerageId": "broker_11111aaaaa",
  "brokerageName": "Smith & Associates",
  "license": {
    "number": "AG123456",
    "state": "NY",
    "expirationDate": "2027-12-31",
    "verified": true
  },
  "specializations": [
    "residential",
    "luxury"
  ],
  "languages": [
    "English",
    "Spanish"
  ],
  "experience": {
    "yearsActive": 8,
    "transactionsCompleted": 156,
    "averagePrice": 525000
  },
  "ratings": {
    "averageRating": 4.8,
    "totalReviews": 42,
    "recommendationRate": 98
  },
  "availability": {
    "status": "available|unavailable",
    "responseTime": "< 2 hours"
  },
  "createdAt": "2018-03-15T10:30:00Z"
}
```

**Validation Rules:**
- Agent ID: Must exist
- License: Must be verified and not expired
- Public Profile: Available without authentication

**Error Codes:**
- `404` - Agent not found

---

### 7.2 Get Agent Listings

**API Name:** Get Agent Listings  
**Method:** GET  
**Endpoint:** `/agents/{agentId}/listings`  
**Authorization Required:** None

**Request:**
```
GET /agents/agent_11111aaaaa/listings?status=active&limit=50
```

**Response (200 OK):**
```json
{
  "agentId": "agent_11111aaaaa",
  "listings": [
    {
      "propertyId": "prop_11111aaaaa",
      "address": "123 Main St, New York, NY 10001",
      "price": 525000,
      "status": "active",
      "listedDate": "2026-08-15",
      "daysOnMarket": 16,
      "thumbnail": "https://cdn.propertyilot.com/images/prop_11111aaaaa_thumb.jpg"
    }
  ],
  "total": 12,
  "active": 10,
  "sold": 2
}
```

**Validation Rules:**
- Status Filter: active, sold, withdrawn, expired
- Limit: Max 100, default 50
- Public: Available without authentication

**Error Codes:**
- `404` - Agent not found

---

### 7.3 Update Agent Availability

**API Name:** Update Availability  
**Method:** PATCH  
**Endpoint:** `/agents/{agentId}/availability`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "status": "available|unavailable",
  "schedule": [
    {
      "dayOfWeek": "monday",
      "startTime": "09:00",
      "endTime": "17:00"
    },
    {
      "dayOfWeek": "saturday",
      "startTime": "10:00",
      "endTime": "14:00"
    }
  ],
  "unavailableDates": [
    {
      "date": "2026-09-05",
      "reason": "personal"
    }
  ]
}
```

**Response (200 OK):**
```json
{
  "agentId": "agent_11111aaaaa",
  "status": "available",
  "updatedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Schedule: Valid day/time format
- Concurrent Updates: Prevent conflicts
- Sync: Update across all platforms

**Error Codes:**
- `400` - Invalid schedule
- `401` - Unauthorized
- `404` - Agent not found

---

### 7.4 Get Agent Performance

**API Name:** Get Agent Performance  
**Method:** GET  
**Endpoint:** `/agents/{agentId}/performance`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```
GET /agents/agent_11111aaaaa/performance?period=30days
```

**Response (200 OK):**
```json
{
  "agentId": "agent_11111aaaaa",
  "period": "30_days",
  "metrics": {
    "listings": {
      "total": 12,
      "active": 10,
      "sold": 2,
      "average_days_on_market": 24
    },
    "leads": {
      "total": 45,
      "converted": 8,
      "conversion_rate": 17.8
    },
    "revenue": {
      "gross_commission": 18500,
      "net_commission": 14500
    },
    "customer_satisfaction": {
      "average_rating": 4.8,
      "reviews": 5,
      "nps": 85
    }
  }
}
```

**Validation Rules:**
- Authorization: Admin or agent viewing own data
- Period: 7d, 30d, 90d, 1y
- Metrics: Real-time calculation

**Error Codes:**
- `401` - Unauthorized
- `403` - Insufficient permissions
- `404` - Agent not found

---

## 8. REPORT APIs

### 8.1 Generate Report

**API Name:** Generate Report  
**Method:** POST  
**Endpoint:** `/reports/generate`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "reportType": "market_analysis|property_valuation|agent_performance|transaction_summary",
  "parameters": {
    "location": "New York, NY",
    "dateRange": {
      "startDate": "2026-08-01",
      "endDate": "2026-08-31"
    },
    "filters": {
      "propertyType": "single_family",
      "priceRange": {
        "min": 250000,
        "max": 750000
      }
    }
  },
  "format": "pdf|csv|excel|json",
  "deliveryEmail": "john.doe@example.com"
}
```

**Response (202 Accepted):**
```json
{
  "reportId": "rpt_abc123xyz",
  "reportType": "market_analysis",
  "status": "processing",
  "estimatedTime": 120,
  "createdAt": "2026-08-31T10:30:00Z",
  "checkStatusUrl": "/reports/rpt_abc123xyz"
}
```

**Validation Rules:**
- Report Type: Valid predefined types
- Date Range: Max 1 year
- Format: pdf, csv, excel, json
- Async Processing: Returns immediately
- Cache: 24 hours for identical reports

**Error Codes:**
- `400` - Invalid parameters
- `401` - Unauthorized
- `422` - Validation failed

---

### 8.2 Get Report Status

**API Name:** Get Report Status  
**Method:** GET  
**Endpoint:** `/reports/{reportId}`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /reports/rpt_abc123xyz
```

**Response (200 OK):**
```json
{
  "reportId": "rpt_abc123xyz",
  "status": "completed|processing|failed",
  "reportType": "market_analysis",
  "format": "pdf",
  "downloadUrl": "https://cdn.propertyilot.com/reports/rpt_abc123xyz.pdf",
  "expiresAt": "2026-09-07T10:30:00Z",
  "createdAt": "2026-08-31T10:30:00Z",
  "completedAt": "2026-08-31T11:00:00Z"
}
```

**Validation Rules:**
- Download Link: 7-day expiration
- Encryption: TLS for download

**Error Codes:**
- `404` - Report not found
- `401` - Unauthorized

---

### 8.3 Schedule Recurring Report

**API Name:** Schedule Report  
**Method:** POST  
**Endpoint:** `/reports/schedule`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```json
{
  "reportType": "agent_performance",
  "schedule": {
    "frequency": "daily|weekly|monthly",
    "dayOfWeek": "monday",
    "time": "09:00",
    "timezone": "America/New_York"
  },
  "recipients": [
    "manager@brokerage.com",
    "admin@propertyilot.com"
  ],
  "parameters": {
    "brokerageId": "broker_11111aaaaa"
  },
  "active": true
}
```

**Response (201 Created):**
```json
{
  "scheduleId": "sched_abc123xyz",
  "reportType": "agent_performance",
  "frequency": "weekly",
  "nextRun": "2026-09-07T09:00:00Z",
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Frequency: daily, weekly, monthly
- Recipients: Valid email addresses
- Next Run: Calculated based on schedule

**Error Codes:**
- `400` - Invalid schedule
- `401` - Unauthorized
- `422` - Validation failed

---

### 8.4 Export Data

**API Name:** Export Data  
**Method:** POST  
**Endpoint:** `/reports/export`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "dataType": "properties|customers|transactions|agents",
  "filters": {
    "dateRange": {
      "startDate": "2026-08-01",
      "endDate": "2026-08-31"
    }
  },
  "format": "csv|excel|json",
  "includeFields": [
    "id",
    "address",
    "price",
    "status"
  ]
}
```

**Response (202 Accepted):**
```json
{
  "exportId": "exp_abc123xyz",
  "status": "processing",
  "downloadUrl": "https://cdn.propertyilot.com/exports/exp_abc123xyz.csv",
  "expiresAt": "2026-09-07T10:30:00Z",
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Data Type: Valid exports only
- PII: Obfuscate sensitive data
- Encryption: Secure downloads
- Audit: Log all exports

**Error Codes:**
- `400` - Invalid parameters
- `401` - Unauthorized
- `403` - Data access denied

---

## 9. SUBSCRIPTION APIs

### 9.1 Get Available Plans

**API Name:** Get Subscription Plans  
**Method:** GET  
**Endpoint:** `/subscriptions/plans`  
**Authorization Required:** None

**Request:**
```
GET /subscriptions/plans?userType=customer&currency=USD
```

**Response (200 OK):**
```json
{
  "plans": [
    {
      "planId": "plan_free_tier",
      "name": "Free",
      "userType": "customer",
      "price": 0,
      "currency": "USD",
      "billing": "free",
      "features": {
        "propertySearches": "unlimited",
        "savedListings": 20,
        "scheduledViewings": 5,
        "offers": 1,
        "support": "community"
      },
      "description": "Get started with basic features"
    },
    {
      "planId": "plan_premium_monthly",
      "name": "Premium",
      "userType": "customer",
      "price": 9.99,
      "currency": "USD",
      "billing": "monthly",
      "features": {
        "propertySearches": "unlimited",
        "savedListings": "unlimited",
        "scheduledViewings": "unlimited",
        "offers": "unlimited",
        "support": "email_priority",
        "marketInsights": true,
        "priceAlerts": true
      },
      "description": "Everything you need to find your dream home"
    }
  ]
}
```

**Validation Rules:**
- User Type: customer, agent, vendor
- Currency: ISO 4217 codes
- Active Plans: Only return active/available plans

**Error Codes:**
- `400` - Invalid parameters

---

### 9.2 Create Subscription

**API Name:** Create Subscription  
**Method:** POST  
**Endpoint:** `/subscriptions`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "planId": "plan_premium_monthly",
  "customerId": "cust_98765zyxwv",
  "paymentMethodId": "pm_abc123xyz",
  "startDate": "2026-08-31",
  "autoRenew": true
}
```

**Response (201 Created):**
```json
{
  "subscriptionId": "sub_abc123xyz",
  "planId": "plan_premium_monthly",
  "customerId": "cust_98765zyxwv",
  "status": "active",
  "startDate": "2026-08-31",
  "renewalDate": "2026-09-30",
  "nextBillingDate": "2026-09-30",
  "autoRenew": true,
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Plan ID: Must exist and be active
- Payment Method: Valid and verified
- Customer: Must exist and not have active subscription
- Start Date: Today or future
- Duplicate Prevention: Check for existing active subscriptions

**Error Codes:**
- `400` - Invalid plan or payment method
- `401` - Unauthorized
- `409` - Subscription already exists
- `422` - Validation failed

---

### 9.3 Update Subscription

**API Name:** Update Subscription  
**Method:** PUT  
**Endpoint:** `/subscriptions/{subscriptionId}`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "planId": "plan_premium_annual",
  "autoRenew": false
}
```

**Response (200 OK):**
```json
{
  "subscriptionId": "sub_abc123xyz",
  "planId": "plan_premium_annual",
  "status": "active",
  "renewalDate": "2027-08-31",
  "autoRenew": false,
  "updatedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Authorization: User or admin only
- Plan Upgrade/Downgrade: Proration calculations
- Audit: Track plan changes

**Error Codes:**
- `400` - Invalid plan
- `401` - Unauthorized
- `404` - Subscription not found

---

### 9.4 Cancel Subscription

**API Name:** Cancel Subscription  
**Method:** DELETE  
**Endpoint:** `/subscriptions/{subscriptionId}`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "reason": "user_request",
  "feedback": "Not needed anymore"
}
```

**Response (200 OK):**
```json
{
  "subscriptionId": "sub_abc123xyz",
  "status": "cancelled",
  "cancellationDate": "2026-08-31T10:30:00Z",
  "refundAmount": 0,
  "refundReason": "no_refund_active_period"
}
```

**Validation Rules:**
- Authorization: User or admin
- Refund Policy: Based on terms
- Grace Period: No cancellation within first week

**Error Codes:**
- `400` - Cannot cancel during trial
- `401` - Unauthorized
- `404` - Subscription not found

---

### 9.5 Get Subscription Details

**API Name:** Get Subscription  
**Method:** GET  
**Endpoint:** `/subscriptions/{subscriptionId}`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /subscriptions/sub_abc123xyz
```

**Response (200 OK):**
```json
{
  "subscriptionId": "sub_abc123xyz",
  "customerId": "cust_98765zyxwv",
  "planId": "plan_premium_monthly",
  "planName": "Premium",
  "status": "active",
  "startDate": "2026-08-31",
  "renewalDate": "2026-09-30",
  "nextBillingDate": "2026-09-30",
  "autoRenew": true,
  "features": {
    "propertySearches": "unlimited",
    "savedListings": "unlimited",
    "support": "email_priority"
  },
  "billingHistory": [
    {
      "date": "2026-08-31",
      "amount": 9.99,
      "currency": "USD",
      "status": "paid",
      "invoiceUrl": "https://cdn.propertyilot.com/invoices/inv_123.pdf"
    }
  ]
}
```

**Validation Rules:**
- Authorization: User or admin
- History: Last 12 months

**Error Codes:**
- `401` - Unauthorized
- `404` - Subscription not found

---

## 10. PAYMENT APIs

### 10.1 Create Payment Intent

**API Name:** Create Payment  
**Method:** POST  
**Endpoint:** `/payments/intents`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "amount": 26000,
  "currency": "USD",
  "paymentType": "earnest_money|subscription|commission",
  "orderId": "offer_abc123xyz",
  "description": "Earnest money deposit for 123 Main St",
  "metadata": {
    "propertyId": "prop_11111aaaaa",
    "customerId": "cust_98765zyxwv"
  }
}
```

**Response (201 Created):**
```json
{
  "paymentIntentId": "pi_abc123xyz",
  "amount": 26000,
  "currency": "USD",
  "status": "requires_payment_method",
  "clientSecret": "pi_abc123xyz_secret_xyz789",
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Amount: > 0, reasonable maximum
- Currency: ISO 4217 code
- Order ID: Unique, not duplicate
- PCI Compliance: Never store full card details
- Idempotency: Use Idempotency-Key header

**Error Codes:**
- `400` - Invalid amount or currency
- `401` - Unauthorized
- `402` - Payment failed
- `422` - Validation failed

---

### 10.2 Process Payment

**API Name:** Confirm Payment  
**Method:** POST  
**Endpoint:** `/payments/{paymentIntentId}/confirm`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "paymentMethodId": "pm_abc123xyz",
  "saveCard": false
}
```

**Response (200 OK):**
```json
{
  "paymentIntentId": "pi_abc123xyz",
  "amount": 26000,
  "currency": "USD",
  "status": "succeeded",
  "transactionId": "txn_abc123xyz",
  "receiptUrl": "https://cdn.propertyilot.com/receipts/txn_abc123xyz.pdf",
  "processedAt": "2026-08-31T10:30:15Z"
}
```

**Validation Rules:**
- Payment Method: Valid and verified
- 3D Secure: When required by card issuer
- Fraud Check: Real-time validation
- PCI-DSS Compliance: Level 1 certified

**Error Codes:**
- `400` - Invalid payment method
- `402` - Payment declined
- `403` - Fraud detected
- `404` - Payment intent not found

---

### 10.3 Create Refund

**API Name:** Refund Payment  
**Method:** POST  
**Endpoint:** `/payments/{transactionId}/refunds`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```json
{
  "amount": 26000,
  "reason": "customer_request",
  "notes": "Offer cancelled by customer"
}
```

**Response (201 Created):**
```json
{
  "refundId": "ref_abc123xyz",
  "transactionId": "txn_abc123xyz",
  "amount": 26000,
  "currency": "USD",
  "status": "pending",
  "reason": "customer_request",
  "createdAt": "2026-08-31T10:30:00Z",
  "expectedDate": "2026-09-07"
}
```

**Validation Rules:**
- Amount: <= original transaction
- Reason: Valid refund reasons
- Timeline: Within 90 days
- Partial Refund: Allowed

**Error Codes:**
- `400` - Invalid amount
- `401` - Unauthorized
- `403` - Cannot refund
- `404` - Transaction not found

---

### 10.4 Get Payment History

**API Name:** Get Payments  
**Method:** GET  
**Endpoint:** `/payments`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /payments?limit=50&offset=0&status=succeeded&dateFrom=2026-08-01&dateTo=2026-08-31
```

**Response (200 OK):**
```json
{
  "payments": [
    {
      "paymentIntentId": "pi_abc123xyz",
      "transactionId": "txn_abc123xyz",
      "amount": 26000,
      "currency": "USD",
      "status": "succeeded",
      "orderId": "offer_abc123xyz",
      "description": "Earnest money deposit",
      "processedAt": "2026-08-31T10:30:00Z",
      "receiptUrl": "https://cdn.propertyilot.com/receipts/txn_abc123xyz.pdf"
    }
  ],
  "total": 5,
  "limit": 50,
  "offset": 0
}
```

**Validation Rules:**
- Status Filter: pending, succeeded, failed, refunded
- Date Range: Max 90 days
- Limit: Max 100, default 50
- PII: Masked payment details

**Error Codes:**
- `401` - Unauthorized
- `400` - Invalid parameters

---

### 10.5 Get Payment Receipt

**API Name:** Get Receipt  
**Method:** GET  
**Endpoint:** `/payments/{transactionId}/receipt`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /payments/txn_abc123xyz/receipt
```

**Response (200 OK - PDF)**
```
Binary PDF content
Content-Type: application/pdf
```

**Validation Rules:**
- Authorization: Original payer or admin
- Encryption: TLS for download
- Retention: 7 years

**Error Codes:**
- `401` - Unauthorized
- `404` - Receipt not found

---

## 11. NOTIFICATION APIs

### 11.1 Send Email Notification

**API Name:** Send Email  
**Method:** POST  
**Endpoint:** `/notifications/email`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "to": "john.doe@example.com",
  "templateId": "offer_confirmation",
  "variables": {
    "customerName": "John Doe",
    "propertyAddress": "123 Main St, New York, NY",
    "offerAmount": 520000
  },
  "priority": "high"
}
```

**Response (202 Accepted):**
```json
{
  "notificationId": "notif_abc123xyz",
  "type": "email",
  "status": "queued",
  "recipient": "john.doe@example.com",
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Email: Valid RFC 5322 format
- Template: Must exist
- Variables: Match template placeholders
- Rate Limit: Max 100 emails per hour per recipient
- Unsubscribe: Honor opt-out preferences

**Error Codes:**
- `400` - Invalid email or template
- `401` - Unauthorized
- `422` - Template validation failed

---

### 11.2 Send SMS Notification

**API Name:** Send SMS  
**Method:** POST  
**Endpoint:** `/notifications/sms`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "to": "+1-555-0123",
  "message": "Your viewing appointment for 123 Main St is confirmed for Sept 2 at 2:00 PM",
  "type": "appointment_reminder"
}
```

**Response (202 Accepted):**
```json
{
  "notificationId": "notif_xyz789abc",
  "type": "sms",
  "status": "queued",
  "recipient": "+1-555-0123",
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Phone: E.164 format
- Message: Max 160 characters (standard SMS)
- Type: Predefined notification types
- Opt-in: Customer must have opted in

**Error Codes:**
- `400` - Invalid phone or message
- `403` - Customer not opted in
- `429` - Rate limited

---

### 11.3 Send Push Notification

**API Name:** Send Push  
**Method:** POST  
**Endpoint:** `/notifications/push`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "deviceTokens": [
    "device_token_123xyz",
    "device_token_456abc"
  ],
  "title": "New Offer Received",
  "body": "You received an offer for 123 Main St",
  "deepLink": "propertyilot://offers/offer_abc123xyz",
  "badge": 1
}
```

**Response (202 Accepted):**
```json
{
  "notificationId": "notif_push_abc123xyz",
  "type": "push",
  "status": "queued",
  "recipients": 2,
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Device Tokens: Valid format, max 1000 per request
- Title: Max 65 characters
- Body: Max 240 characters
- Deep Link: Valid app URL scheme

**Error Codes:**
- `400` - Invalid tokens or content
- `401` - Unauthorized

---

### 11.4 Get Notification History

**API Name:** Get Notifications  
**Method:** GET  
**Endpoint:** `/notifications`  
**Authorization Required:** Bearer Token

**Request:**
```
GET /notifications?limit=50&offset=0&type=all&status=sent
```

**Response (200 OK):**
```json
{
  "notifications": [
    {
      "notificationId": "notif_abc123xyz",
      "type": "email",
      "recipient": "john.doe@example.com",
      "subject": "Offer Confirmation",
      "status": "delivered",
      "sentAt": "2026-08-31T10:30:00Z",
      "deliveredAt": "2026-08-31T10:30:05Z"
    }
  ],
  "total": 45,
  "limit": 50,
  "offset": 0
}
```

**Validation Rules:**
- Type Filter: email, sms, push, all
- Status: sent, delivered, failed, bounced
- Limit: Max 100

**Error Codes:**
- `401` - Unauthorized
- `400` - Invalid parameters

---

### 11.5 Manage Notification Preferences

**API Name:** Update Notification Preferences  
**Method:** PUT  
**Endpoint:** `/notifications/preferences`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "email": {
    "marketing": false,
    "transactional": true,
    "newsletters": false
  },
  "sms": {
    "alerts": true,
    "marketing": false
  },
  "push": {
    "enabled": true
  }
}
```

**Response (200 OK):**
```json
{
  "userId": "usr_12345abcde",
  "preferences": {
    "email": {
      "marketing": false,
      "transactional": true,
      "newsletters": false
    },
    "sms": {
      "alerts": true,
      "marketing": false
    },
    "push": {
      "enabled": true
    }
  },
  "updatedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- All fields optional
- Audit: Track preference changes
- Compliance: GDPR/CCPA compliant

**Error Codes:**
- `400` - Invalid preferences
- `401` - Unauthorized

---

## 12. VENDOR APIs

### 12.1 Register Vendor

**API Name:** Register Vendor  
**Method:** POST  
**Endpoint:** `/vendors/register`  
**Authorization Required:** None

**Request:**
```json
{
  "userId": "usr_12345abcde",
  "vendorName": "Smith Home Inspections",
  "vendorType": "inspector|appraiser|title_company|mortgage_lender",
  "businessLicense": "VEN123456",
  "serviceAreas": [
    "New York",
    "New Jersey",
    "Connecticut"
  ],
  "contactPerson": "Robert Smith",
  "phoneNumber": "+1-555-0200",
  "website": "https://www.smithinspections.com"
}
```

**Response (201 Created):**
```json
{
  "vendorId": "vendor_11111aaaaa",
  "userId": "usr_12345abcde",
  "vendorName": "Smith Home Inspections",
  "vendorType": "inspector",
  "status": "pending_verification",
  "verificationStatus": "pending_review",
  "createdAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Vendor Type: Valid predefined type
- Business License: Format validation
- Service Areas: Geographic validity
- Verification: 2-3 business days

**Error Codes:**
- `400` - Invalid data
- `409` - Vendor already registered
- `422` - Validation failed

---

### 12.2 Get Vendor Profile

**API Name:** Get Vendor  
**Method:** GET  
**Endpoint:** `/vendors/{vendorId}`  
**Authorization Required:** None

**Request:**
```
GET /vendors/vendor_11111aaaaa
```

**Response (200 OK):**
```json
{
  "vendorId": "vendor_11111aaaaa",
  "vendorName": "Smith Home Inspections",
  "vendorType": "inspector",
  "status": "active",
  "rating": 4.7,
  "reviews": 38,
  "responseTime": "< 1 hour",
  "serviceAreas": [
    "New York",
    "New Jersey",
    "Connecticut"
  ],
  "phoneNumber": "+1-555-0200",
  "website": "https://www.smithinspections.com",
  "certifications": [
    {
      "name": "New York Home Inspector Certification",
      "issuer": "NAHI",
      "expirationDate": "2027-12-31"
    }
  ],
  "averagePrice": 450,
  "availability": {
    "status": "available",
    "nextAvailable": "2026-09-02"
  }
}
```

**Validation Rules:**
- Public Profile: Available without authentication
- Rating: Only from verified transactions
- Certifications: Must be current

**Error Codes:**
- `404` - Vendor not found

---

### 12.3 Request Vendor Service

**API Name:** Request Service  
**Method:** POST  
**Endpoint:** `/vendors/{vendorId}/requests`  
**Authorization Required:** Bearer Token

**Request:**
```json
{
  "serviceType": "inspection|appraisal|title_search",
  "propertyId": "prop_11111aaaaa",
  "customerId": "cust_98765zyxwv",
  "preferredDate": "2026-09-02",
  "notes": "Need inspection before offer"
}
```

**Response (201 Created):**
```json
{
  "requestId": "req_abc123xyz",
  "vendorId": "vendor_11111aaaaa",
  "status": "pending_acceptance",
  "estimatedPrice": 450,
  "createdAt": "2026-08-31T10:30:00Z",
  "expiresAt": "2026-09-02T10:30:00Z"
}
```

**Validation Rules:**
- Service Type: Vendor must offer it
- Property: Must exist and be in service area
- Preferred Date: Min 24 hours in future
- Timeout: 48 hours for vendor response

**Error Codes:**
- `400` - Invalid request
- `401` - Unauthorized
- `404` - Vendor not found

---

### 12.4 Accept/Reject Service Request

**API Name:** Respond to Service Request  
**Method:** PATCH  
**Endpoint:** `/vendors/requests/{requestId}`  
**Authorization Required:** Bearer Token (Vendor)

**Request:**
```json
{
  "action": "accept|reject",
  "scheduledDate": "2026-09-02T10:00:00Z",
  "finalPrice": 450,
  "notes": "Confirmed for morning inspection"
}
```

**Response (200 OK):**
```json
{
  "requestId": "req_abc123xyz",
  "status": "accepted",
  "scheduledDate": "2026-09-02T10:00:00Z",
  "finalPrice": 450,
  "confirmationCode": "INSP-2026-ABC123",
  "respondedAt": "2026-08-31T10:45:00Z"
}
```

**Validation Rules:**
- Authorization: Vendor owner only
- Date: Must be available
- Price: Cannot exceed estimated
- Audit: Track all responses

**Error Codes:**
- `400` - Invalid action
- `401` - Unauthorized
- `404` - Request not found

---

### 12.5 Submit Service Report

**API Name:** Submit Report  
**Method:** POST  
**Endpoint:** `/vendors/requests/{requestId}/report`  
**Authorization Required:** Bearer Token (Vendor)

**Request:**
```json
{
  "reportTitle": "Home Inspection Report",
  "completionDate": "2026-09-02",
  "findings": "All systems functioning normally. Minor repairs needed: caulking bathroom tiles",
  "recommendedRepairs": [
    {
      "item": "Bathroom tile caulking",
      "severity": "minor",
      "estimatedCost": 200
    }
  ],
  "reportFile": "https://cdn.propertyilot.com/reports/vendor_11111aaaaa_req_abc123xyz.pdf"
}
```

**Response (201 Created):**
```json
{
  "reportId": "rpt_vendor_abc123xyz",
  "requestId": "req_abc123xyz",
  "status": "submitted",
  "downloadUrl": "https://cdn.propertyilot.com/reports/vendor_11111aaaaa_req_abc123xyz.pdf",
  "submittedAt": "2026-09-02T14:00:00Z"
}
```

**Validation Rules:**
- Request: Must be accepted
- PDF: Valid format and size
- Findings: Required field
- Audit: Maintain complete record

**Error Codes:**
- `400` - Invalid report
- `401` - Unauthorized
- `404` - Request not found

---

## 13. ADMIN APIs

### 13.1 Manage Users

**API Name:** Get All Users  
**Method:** GET  
**Endpoint:** `/admin/users`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```
GET /admin/users?userType=customer&status=active&limit=50&offset=0&search=john
```

**Response (200 OK):**
```json
{
  "users": [
    {
      "userId": "usr_12345abcde",
      "email": "john.doe@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "userType": "customer",
      "status": "active",
      "mfaEnabled": false,
      "lastLogin": "2026-08-31T09:15:00Z",
      "createdAt": "2026-01-15T10:30:00Z"
    }
  ],
  "total": 245,
  "limit": 50,
  "offset": 0
}
```

**Validation Rules:**
- User Type: customer, agent, vendor, admin
- Status: active, suspended, deleted
- Search: Searches email, name
- Limit: Max 100

**Error Codes:**
- `401` - Unauthorized
- `403` - Insufficient permissions

---

### 13.2 Suspend User Account

**API Name:** Suspend User  
**Method:** PATCH  
**Endpoint:** `/admin/users/{userId}/suspend`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```json
{
  "reason": "violation_of_terms",
  "duration": 30,
  "notes": "Repeated fraudulent activity"
}
```

**Response (200 OK):**
```json
{
  "userId": "usr_12345abcde",
  "status": "suspended",
  "suspensionUntil": "2026-09-30T10:30:00Z",
  "reason": "violation_of_terms",
  "suspendedAt": "2026-08-31T10:30:00Z"
}
```

**Validation Rules:**
- Reason: Valid predefined reasons
- Duration: 1-365 days
- Audit: Log all suspensions
- Notification: Notify user

**Error Codes:**
- `400` - Invalid duration
- `401` - Unauthorized
- `404` - User not found

---

### 13.3 Reset User Password (Admin)

**API Name:** Reset User Password  
**Method:** POST  
**Endpoint:** `/admin/users/{userId}/reset-password`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```json
{
  "email": "john.doe@example.com"
}
```

**Response (200 OK):**
```json
{
  "message": "Password reset email sent",
  "email": "john.doe@example.com",
  "expiresIn": 3600
}
```

**Validation Rules:**
- Email: Must match user's email
- Audit: Log all resets
- Notification: Send reset link to user

**Error Codes:**
- `401` - Unauthorized
- `404` - User not found

---

### 13.4 Configure System Settings

**API Name:** Update Settings  
**Method:** PUT  
**Endpoint:** `/admin/settings`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```json
{
  "commission_rates": {
    "standard": 6.0,
    "discount": 4.5
  },
  "earnest_money_range": {
    "min_percent": 1,
    "max_percent": 5
  },
  "notification_settings": {
    "email_template_footer": "Custom footer",
    "sms_max_per_hour": 100
  },
  "feature_flags": {
    "enable_virtual_tours": true,
    "enable_ai_valuation": false
  }
}
```

**Response (200 OK):**
```json
{
  "settings": {
    "commission_rates": {
      "standard": 6.0,
      "discount": 4.5
    },
    "earnest_money_range": {
      "min_percent": 1,
      "max_percent": 5
    }
  },
  "updatedAt": "2026-08-31T10:30:00Z",
  "appliedAt": "2026-08-31T10:35:00Z"
}
```

**Validation Rules:**
- Rates: Reasonable percentages
- Approval Workflow: For critical settings
- Audit: Track all changes
- Cache Invalidation: Refresh across services
- Delayed Application: 5-minute grace period

**Error Codes:**
- `400` - Invalid settings
- `401` - Unauthorized
- `403` - Setting locked

---

### 13.5 View Audit Logs

**API Name:** Get Audit Logs  
**Method:** GET  
**Endpoint:** `/admin/audit-logs`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```
GET /admin/audit-logs?limit=100&offset=0&action=user_delete&entity=users&userId=usr_12345abcde&dateFrom=2026-08-01&dateTo=2026-08-31
```

**Response (200 OK):**
```json
{
  "logs": [
    {
      "logId": "audit_abc123xyz",
      "timestamp": "2026-08-31T10:30:00Z",
      "action": "user_update",
      "entity": "users",
      "entityId": "usr_12345abcde",
      "adminId": "admin_11111aaaaa",
      "changes": {
        "status": {
          "before": "active",
          "after": "suspended"
        }
      },
      "ipAddress": "192.168.1.1",
      "userAgent": "Mozilla/5.0..."
    }
  ],
  "total": 245,
  "limit": 100,
  "offset": 0
}
```

**Validation Rules:**
- Action: Valid audit actions
- Date Range: Max 90 days
- Retention: 3 years
- Encryption: Secure storage

**Error Codes:**
- `401` - Unauthorized
- `400` - Invalid parameters

---

### 13.6 Generate Compliance Report

**API Name:** Generate Compliance Report  
**Method:** POST  
**Endpoint:** `/admin/compliance-reports`  
**Authorization Required:** Bearer Token (Admin)

**Request:**
```json
{
  "reportType": "gdpr_compliance|ccpa_compliance|sox_audit|annual_review",
  "dateRange": {
    "startDate": "2026-01-01",
    "endDate": "2026-08-31"
  },
  "format": "pdf|excel"
}
```

**Response (202 Accepted):**
```json
{
  "reportId": "comp_report_abc123xyz",
  "reportType": "gdpr_compliance",
  "status": "processing",
  "downloadUrl": "https://cdn.propertyilot.com/compliance/comp_report_abc123xyz.pdf",
  "expiresAt": "2026-09-07T10:30:00Z",
  "estimatedTime": 300
}
```

**Validation Rules:**
- Report Type: Predefined types
- Async Processing: Long-running task
- Encryption: Secure storage
- Audit: Log report generation

**Error Codes:**
- `400` - Invalid parameters
- `401` - Unauthorized
- `422` - Validation failed

---

## API AUTHENTICATION & AUTHORIZATION

### Authentication Methods

1. **OAuth 2.0 (Recommended)**
   - Authorization Code Flow for web/mobile apps
   - Token Endpoint: `POST /auth/token`
   - Refresh Token: 30-day expiration
   - Access Token: 1-hour expiration

2. **JWT Bearer Token**
   - Header: `Authorization: Bearer {token}`
   - Algorithm: HS256
   - Issued by: Auth service
   - Verification: Signature validation

3. **API Keys (Legacy/Vendor)**
   - Header: `X-API-Key: {key}`
   - Scope: Rate limited per API key
   - Revocable: Can disable anytime

### Authorization Model (RBAC)

**Roles:**
- `customer` - End users buying/selling properties
- `agent` - Licensed real estate agents
- `vendor` - Service providers (inspectors, appraisers, etc.)
- `ops_manager` - Operations team members
- `admin` - System administrators
- `super_admin` - Full system access

**Scopes:**
```
read:properties      - View property listings
write:properties     - Create/edit listings
read:customers       - View customer data
write:customers      - Edit customer profiles
read:transactions    - View deals and offers
write:transactions   - Create offers
read:payments        - View payment history
write:payments       - Process payments
admin:users          - Manage user accounts
admin:settings       - Configure system
```

---

## ERROR RESPONSE FORMAT

All APIs return errors in consistent format:

```json
{
  "error": {
    "code": "INVALID_INPUT",
    "message": "Email is required",
    "details": [
      {
        "field": "email",
        "message": "Email must be a valid RFC 5322 address"
      }
    ],
    "requestId": "req_xyz789abc",
    "timestamp": "2026-08-31T10:30:00Z"
  }
}
```

**Standard HTTP Status Codes:**
- `200` - OK
- `201` - Created
- `202` - Accepted (async)
- `204` - No Content
- `400` - Bad Request
- `401` - Unauthorized
- `403` - Forbidden
- `404` - Not Found
- `409` - Conflict
- `422` - Unprocessable Entity
- `429` - Too Many Requests
- `500` - Internal Server Error
- `503` - Service Unavailable

---

## RATE LIMITING

**Default Limits:**
- Unauthenticated: 60 requests/minute
- Authenticated: 1000 requests/minute
- Privileged (Admin): 10000 requests/minute

**Headers:**
```
X-RateLimit-Limit: 1000
X-RateLimit-Remaining: 999
X-RateLimit-Reset: 1693474200
```

---

## PAGINATION

All list endpoints support pagination:

```
GET /resources?limit=50&offset=0
GET /resources?limit=50&cursor=eyJvZmZzZXQiOjUwfQ==
```

**Response Structure:**
```json
{
  "items": [...],
  "total": 245,
  "limit": 50,
  "offset": 0,
  "cursor": "eyJvZmZzZXQiOjEwMH0=",
  "hasMore": true
}
```

---

## VERSIONING

API version in URL path:
- Current: `https://api.propertyilot.com/v1`
- Deprecation: 12-month notice before removal
- Header: `X-API-Version: 1.0.0`

---

**End of API Catalog Document**

Maintained by: API Platform Team  
Last Updated: 2026-08-31  
Document Version: 1.0
````
````

---

## APPENDED MISSING ENDPOINTS

| Method | Endpoint |
|---|---|
| POST | `/auth/otp/request` |
| GET | `/customers/{customerId}/properties` |
| GET | `/customers/{customerId}/addresses` |
| POST | `/customers/{customerId}/addresses` |
| PUT | `/customers/{customerId}/addresses/{addressId}` |
| DELETE | `/customers/{customerId}/addresses/{addressId}` |
| GET | `/properties/{propertyId}/documents` |
| POST | `/properties/{propertyId}/documents` |
| GET | `/properties/{propertyId}/documents/{documentId}` |
| DELETE | `/properties/{propertyId}/documents/{documentId}` |
| GET | `/properties/{propertyId}/owners` |
| POST | `/properties/{propertyId}/owners/invitations` |
| PATCH | `/properties/{propertyId}/owners/{ownerId}` |
| POST | `/properties/{propertyId}/owners/{ownerId}/approval` |
| GET | `/services` |
| GET | `/services/{serviceId}` |
| POST | `/services/estimates` |
| POST | `/services/comparisons` |
| POST | `/service-requests` |
| GET | `/service-requests` |
| GET | `/service-requests/{requestId}` |
| PATCH | `/service-requests/{requestId}/status` |
| POST | `/service-requests/{requestId}/cancel` |
| POST | `/service-requests/{requestId}/ratings` |
| POST | `/verifications/gps` |
| GET | `/verifications/{verificationId}` |
| GET | `/properties/{propertyId}/verifications` |
| POST | `/property-monitoring/requests` |
| GET | `/property-monitoring/requests/{requestId}` |
| GET | `/property-monitoring/visits` |
| GET | `/property-monitoring/alerts` |
| PATCH | `/property-monitoring/alerts/{alertId}` |
| GET | `/agents/{agentId}/tasks` |
| GET | `/agent-tasks/{taskId}` |
| PATCH | `/agent-tasks/{taskId}` |
| POST | `/visits` |
| GET | `/visits/{visitId}` |
| PATCH | `/visits/{visitId}` |
| POST | `/visits/{visitId}/gps-captures` |
| POST | `/visits/{visitId}/evidence` |
| GET | `/visits/{visitId}/evidence` |
| POST | `/visits/{visitId}/reports` |
| GET | `/agents/{agentId}/reports` |
| GET | `/reports` |
| GET | `/reports/{reportId}/evidence` |
| GET | `/customers/{customerId}/subscriptions` |
| POST | `/subscriptions/compare` |
| POST | `/subscriptions/{subscriptionId}/renew` |
| POST | `/subscriptions/{subscriptionId}/pause` |
| POST | `/subscriptions/{subscriptionId}/resume` |
| GET | `/subscriptions/{subscriptionId}/entitlements` |
| GET | `/subscriptions/{subscriptionId}/visits` |
| GET | `/invoices` |
| GET | `/invoices/{invoiceId}` |
| GET | `/customers/{customerId}/invoices` |
| GET | `/vendors` |
| PUT | `/vendors/{vendorId}` |
| GET | `/vendors/{vendorId}/jobs` |
| GET | `/vendor-jobs/{jobId}` |
| PATCH | `/vendor-jobs/{jobId}` |
| POST | `/vendor-jobs/{jobId}/evidence` |
| POST | `/vendor-jobs/{jobId}/invoices` |
| GET | `/vendors/{vendorId}/payments` |
| POST | `/quotations` |
| GET | `/service-requests/{requestId}/quotations` |
| POST | `/service-requests/{requestId}/quotations/{quotationId}/select` |
| POST | `/complaints` |
| GET | `/complaints` |
| GET | `/complaints/{complaintId}` |
| POST | `/complaints/{complaintId}/comments` |
| PATCH | `/complaints/{complaintId}/status` |
| POST | `/notifications/whatsapp` |
| GET | `/notification-templates` |
| PUT | `/notification-templates/{templateId}` |
| GET | `/nri/dashboard` |
| GET | `/nri/properties/{propertyId}/monitoring-summary` |
| POST | `/nri/relationship-manager/messages` |
| GET | `/nri/emergency-alerts` |
| POST | `/marketplace/inquiries` |
| GET | `/marketplace/leads` |
| GET | `/marketplace/listings` |
| POST | `/marketplace/listings` |
| PATCH | `/marketplace/listings/{listingId}` |
| POST | `/marketplace/leads/{leadId}/contact-disclosure` |
| GET | `/marketplace/commissions` |
| GET | `/admin/services` |
| POST | `/admin/services` |
| PUT | `/admin/services/{serviceId}` |
| GET | `/admin/subscription-plans` |
| POST | `/admin/subscription-plans` |
| PUT | `/admin/subscription-plans/{planId}` |
| GET | `/admin/pricing-rules` |
| PUT | `/admin/pricing-rules` |
| POST | `/admin/pricing-simulations` |
| GET | `/analytics/customers` |
| GET | `/analytics/operations` |
| GET | `/analytics/revenue` |
| GET | `/analytics/agents` |
| POST | `/agents` |
| PUT | `/agents/{agentId}` |
| GET | `/agents/{agentId}/skills` |
| PUT | `/agents/{agentId}/skills` |
| GET | `/admin/partners` |
| POST | `/admin/partners` |
| PUT | `/admin/partners/{partnerId}` |
| GET | `/admin/partners/{partnerId}/services` |
| PUT | `/admin/partners/{partnerId}/services` |

# Newly Added APIs

### Capture Lead

**Method:** `POST`  
**Endpoint:** `/leads`  
**Purpose:** Capture a service, subscription, marketplace, referral, or manual lead for qualification and follow-up.

**Request:**
```json
{
  "source": "website",
  "leadType": "service_inquiry",
  "name": "Asha Rao",
  "mobileNumber": "+919876543210",
  "email": "asha@example.com",
  "location": "Hyderabad",
  "propertyId": "prop_123",
  "interestedServiceId": "svc_monitoring",
  "priority": "high"
}
```

**Response:**
```json
{
  "leadId": "lead_123",
  "status": "new",
  "createdAt": "2026-08-31T10:30:00Z"
}
```

### Update Lead Status

**Method:** `PATCH`  
**Endpoint:** `/leads/{leadId}`  
**Purpose:** Update lead qualification, priority, status, or loss reason.

**Request:**
```json
{
  "status": "qualified",
  "priority": "high",
  "qualificationNotes": "Coverage and service eligibility confirmed"
}
```

**Response:**
```json
{
  "leadId": "lead_123",
  "status": "qualified",
  "updatedAt": "2026-08-31T10:45:00Z"
}
```

### Assign Lead

**Method:** `POST`  
**Endpoint:** `/leads/{leadId}/assignments`  
**Purpose:** Assign a lead to an eligible agent or sales owner.

**Request:**
```json
{
  "assigneeId": "agent_123",
  "assignmentReason": "coverage_and_service_match"
}
```

**Response:**
```json
{
  "leadId": "lead_123",
  "assigneeId": "agent_123",
  "status": "contacted",
  "assignedAt": "2026-08-31T10:50:00Z"
}
```

### Convert Lead

**Method:** `POST`  
**Endpoint:** `/leads/{leadId}/conversions`  
**Purpose:** Record conversion of a qualified lead to a service request, subscription, or marketplace outcome.

**Request:**
```json
{
  "conversionType": "service_request",
  "referenceId": "req_123"
}
```

**Response:**
```json
{
  "leadId": "lead_123",
  "status": "converted",
  "conversionType": "service_request",
  "referenceId": "req_123"
}
```

### Create Remediation Request from Monitoring Alert

**Method:** `POST`  
**Endpoint:** `/property-monitoring/alerts/{alertId}/service-requests`  
**Purpose:** Create a customer-approved remediation request that remains linked to the detected alert and its evidence.

**Request:**
```json
{
  "serviceId": "svc_boundary_repair",
  "customerApproval": true,
  "preferredDate": "2026-09-05"
}
```

**Response:**
```json
{
  "requestId": "req_456",
  "alertId": "alert_123",
  "status": "pending_payment"
}
```

### Assign Service Request

**Method:** `POST`  
**Endpoint:** `/service-requests/{requestId}/assignments`  
**Purpose:** Assign an eligible agent or vendor to a paid and ready service request.

**Request:**
```json
{
  "assigneeType": "agent",
  "assigneeId": "agent_123",
  "scheduledWindowStart": "2026-09-05T09:00:00Z",
  "scheduledWindowEnd": "2026-09-05T12:00:00Z"
}
```

**Response:**
```json
{
  "assignmentId": "assign_123",
  "requestId": "req_456",
  "status": "assigned"
}
```

### Review Submitted Report

**Method:** `POST`  
**Endpoint:** `/reports/{reportId}/reviews`  
**Purpose:** Record operations approval, rejection, or rework required for a submitted field report.

**Request:**
```json
{
  "decision": "approved",
  "reviewNotes": "Evidence and observations complete"
}
```

**Response:**
```json
{
  "reportId": "rpt_123",
  "status": "approved",
  "reviewedAt": "2026-08-31T11:00:00Z"
}
```

### Retrieve Knowledge-Base Articles

**Method:** `GET`  
**Endpoint:** `/knowledge-base/articles`  
**Purpose:** Retrieve support and FAQ articles for the customer support experience.

**Request:**
```text
GET /knowledge-base/articles?query=subscription&category=billing&limit=20
```

**Response:**
```json
{
  "items": [
    {
      "articleId": "kb_123",
      "title": "How subscription renewal works",
      "category": "billing"
    }
  ]
}
```

### Retrieve NRI Relationship-Manager Conversation

**Method:** `GET`  
**Endpoint:** `/nri/relationship-manager/messages`  
**Purpose:** Retrieve the authorised NRI customer and relationship-manager conversation history.

**Request:**
```text
GET /nri/relationship-manager/messages?propertyId=prop_123&limit=50
```

**Response:**
```json
{
  "messages": [
    {
      "messageId": "msg_123",
      "propertyId": "prop_123",
      "senderRole": "relationship_manager",
      "body": "Monitoring report is ready.",
      "sentAt": "2026-08-31T11:15:00Z"
    }
  ]
}
```

### Acknowledge NRI Emergency Alert

**Method:** `PATCH`  
**Endpoint:** `/nri/emergency-alerts/{alertId}`  
**Purpose:** Record an NRI customer's acknowledgement and requested next action for an emergency property alert.

**Request:**
```json
{
  "action": "request_emergency_visit",
  "acknowledged": true
}
```

**Response:**
```json
{
  "alertId": "alert_123",
  "status": "acknowledged",
  "followUpAction": "request_emergency_visit"
}
```

### Approve Vendor

**Method:** `POST`  
**Endpoint:** `/admin/vendors/{vendorId}/approval`  
**Purpose:** Approve, reject, restrict, suspend, or reactivate a vendor after controlled review.

**Request:**
```json
{
  "decision": "approved",
  "verificationLevel": "verified",
  "restrictions": []
}
```

**Response:**
```json
{
  "vendorId": "vendor_123",
  "status": "active",
  "verificationLevel": "verified",
  "effectiveAt": "2026-08-31T11:30:00Z"
}
```

### Create Coupon

**Method:** `POST`  
**Endpoint:** `/admin/coupons`  
**Purpose:** Create an administrator-managed coupon for eligible services or subscriptions.

**Request:**
```json
{
  "code": "MONITOR10",
  "discountType": "percentage",
  "discountValue": 10,
  "validFrom": "2026-09-01",
  "validTo": "2026-09-30"
}
```

**Response:**
```json
{
  "couponId": "coupon_123",
  "code": "MONITOR10",
  "status": "active"
}
```

### Update Coupon

**Method:** `PUT`  
**Endpoint:** `/admin/coupons/{couponId}`  
**Purpose:** Update or deactivate an administrator-managed coupon.

**Request:**
```json
{
  "status": "inactive",
  "validTo": "2026-09-15"
}
```

**Response:**
```json
{
  "couponId": "coupon_123",
  "status": "inactive",
  "updatedAt": "2026-08-31T11:45:00Z"
}
```
