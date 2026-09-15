CREATE TABLE document_types
(
    document_type_id UUID PRIMARY KEY,
    property_category VARCHAR(50) NOT NULL,
    document_code VARCHAR(100) NOT NULL UNIQUE,
    document_name VARCHAR(255) NOT NULL,
    mandatory BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

INSERT INTO document_types
(
    document_type_id,
    property_category,
    document_code,
    document_name,
    mandatory
)
VALUES

/* =========================================================
   COMMON DOCUMENTS
   ========================================================= */

(gen_random_uuid(),'COMMON','SALE_DEED','Sale Deed',true),
(gen_random_uuid(),'COMMON','GIFT_DEED','Gift Deed',false),
(gen_random_uuid(),'COMMON','PARTITION_DEED','Partition Deed',false),
(gen_random_uuid(),'COMMON','RELEASE_DEED','Release Deed',false),
(gen_random_uuid(),'COMMON','SETTLEMENT_DEED','Settlement Deed',false),
(gen_random_uuid(),'COMMON','CONVEYANCE_DEED','Conveyance Deed',false),
(gen_random_uuid(),'COMMON','WILL_AND_PROBATE','Will and Probate',false),

(gen_random_uuid(),'COMMON','PROPERTY_TAX_RECEIPT','Property Tax Receipt',true),
(gen_random_uuid(),'COMMON','MUTATION_CERTIFICATE','Mutation Certificate',false),
(gen_random_uuid(),'COMMON','ENCUMBRANCE_CERTIFICATE','Encumbrance Certificate',true),

(gen_random_uuid(),'COMMON','OWNER_AADHAAR','Owner Aadhaar',false),
(gen_random_uuid(),'COMMON','OWNER_PAN','Owner PAN',false),

(gen_random_uuid(),'COMMON','ELECTRICITY_BILL','Electricity Bill',false),
(gen_random_uuid(),'COMMON','WATER_BILL','Water Bill',false),

(gen_random_uuid(),'COMMON','LINK_DOCUMENT','Link Document',false),
(gen_random_uuid(),'COMMON','LEGAL_OPINION','Legal Opinion',false),
(gen_random_uuid(),'COMMON','TITLE_SEARCH_REPORT','Title Search Report',false),
(gen_random_uuid(),'COMMON','COURT_CASE_DOCUMENT','Court Case Document',false),
(gen_random_uuid(),'COMMON','NOC','No Objection Certificate',false),

/* =========================================================
   PLOT DOCUMENTS
   ========================================================= */

(gen_random_uuid(),'PLOT','APPROVED_LAYOUT_PLAN','Approved Layout Plan',true),
(gen_random_uuid(),'PLOT','LP_NUMBER','Layout Permission Number',false),
(gen_random_uuid(),'PLOT','DTCP_APPROVAL','DTCP Approval',false),
(gen_random_uuid(),'PLOT','HMDA_APPROVAL','HMDA Approval',false),
(gen_random_uuid(),'PLOT','MUNICIPAL_APPROVAL','Municipal Approval',false),
(gen_random_uuid(),'PLOT','SURVEY_SKETCH','Survey Sketch',false),

(gen_random_uuid(),'PLOT','SURVEY_NUMBER_RECORD','Survey Number Record',true),
(gen_random_uuid(),'PLOT','SUBDIVISION_RECORD','Subdivision Record',false),
(gen_random_uuid(),'PLOT','FMB','Field Measurement Book',false),
(gen_random_uuid(),'PLOT','VILLAGE_MAP','Village Map',false),

/* =========================================================
   FLAT / APARTMENT DOCUMENTS
   ========================================================= */

(gen_random_uuid(),'FLAT','BUILDER_BUYER_AGREEMENT','Builder Buyer Agreement',true),
(gen_random_uuid(),'FLAT','CONSTRUCTION_AGREEMENT','Construction Agreement',false),
(gen_random_uuid(),'FLAT','ALLOTMENT_LETTER','Allotment Letter',false),
(gen_random_uuid(),'FLAT','POSSESSION_LETTER','Possession Letter',false),

(gen_random_uuid(),'FLAT','OCCUPANCY_CERTIFICATE','Occupancy Certificate',true),
(gen_random_uuid(),'FLAT','COMPLETION_CERTIFICATE','Completion Certificate',true),

(gen_random_uuid(),'FLAT','APARTMENT_ASSOCIATION_DOCUMENT','Apartment Association Document',false),
(gen_random_uuid(),'FLAT','MAINTENANCE_RECEIPT','Maintenance Receipt',false),
(gen_random_uuid(),'FLAT','PARKING_ALLOTMENT_LETTER','Parking Allotment Letter',false),

(gen_random_uuid(),'FLAT','BANK_NOC','Bank NOC',false),
(gen_random_uuid(),'FLAT','LOAN_CLOSURE_CERTIFICATE','Loan Closure Certificate',false),

/* =========================================================
   HOUSE DOCUMENTS
   ========================================================= */

(gen_random_uuid(),'HOUSE','BUILDING_PERMISSION','Building Permission',true),
(gen_random_uuid(),'HOUSE','APPROVED_BUILDING_PLAN','Approved Building Plan',true),
(gen_random_uuid(),'HOUSE','HOUSE_OCCUPANCY_CERTIFICATE','Occupancy Certificate',true),
(gen_random_uuid(),'HOUSE','PROPERTY_ASSESSMENT_COPY','Property Assessment Copy',false),
(gen_random_uuid(),'HOUSE','HOUSE_TAX_RECORD','House Tax Record',false),

(gen_random_uuid(),'HOUSE','ELECTRICITY_METER_OWNERSHIP','Electricity Meter Ownership',false),
(gen_random_uuid(),'HOUSE','WATER_CONNECTION_DOCUMENT','Water Connection Document',false),
(gen_random_uuid(),'HOUSE','BOREWELL_PERMISSION','Borewell Permission',false),

/* =========================================================
   COMMERCIAL DOCUMENTS
   ========================================================= */

(gen_random_uuid(),'COMMERCIAL','TRADE_LICENSE','Trade License',true),
(gen_random_uuid(),'COMMERCIAL','FIRE_NOC','Fire NOC',true),
(gen_random_uuid(),'COMMERCIAL','POLLUTION_NOC','Pollution NOC',false),
(gen_random_uuid(),'COMMERCIAL','LIFT_LICENSE','Lift License',false),
(gen_random_uuid(),'COMMERCIAL','STRUCTURAL_STABILITY_CERTIFICATE','Structural Stability Certificate',false),

(gen_random_uuid(),'COMMERCIAL','LEASE_AGREEMENT','Lease Agreement',false),
(gen_random_uuid(),'COMMERCIAL','TENANT_AGREEMENT','Tenant Agreement',false),
(gen_random_uuid(),'COMMERCIAL','RENTAL_RECORD','Rental Record',false),
(gen_random_uuid(),'COMMERCIAL','GST_REGISTRATION','GST Registration',false),

/* =========================================================
   AGRICULTURAL LAND DOCUMENTS
   ========================================================= */

(gen_random_uuid(),'AGRICULTURAL','PATTADAR_PASSBOOK','Pattadar Passbook',true),
(gen_random_uuid(),'AGRICULTURAL','TITLE_DEED','Title Deed',true),
(gen_random_uuid(),'AGRICULTURAL','PAHANI_ADANGAL','Pahani Adangal',true),
(gen_random_uuid(),'AGRICULTURAL','ROR_1B','ROR 1B',true),
(gen_random_uuid(),'AGRICULTURAL','DHARANI_RECORD','Dharani Record',true),
(gen_random_uuid(),'AGRICULTURAL','AGRI_SURVEY_SKETCH','Survey Sketch',false),

(gen_random_uuid(),'AGRICULTURAL','AGRI_BOREWELL_PERMISSION','Borewell Permission',false),
(gen_random_uuid(),'AGRICULTURAL','CROP_LOAN_DOCUMENT','Crop Loan Document',false),
(gen_random_uuid(),'AGRICULTURAL','WATER_RIGHTS_DOCUMENT','Water Rights Document',false),
(gen_random_uuid(),'AGRICULTURAL','LAND_CONVERSION_DOCUMENT','Land Conversion Document',false),

(gen_random_uuid(),'AGRICULTURAL','INHERITANCE_RECORD','Inheritance Record',false),
(gen_random_uuid(),'AGRICULTURAL','SUCCESSION_CERTIFICATE','Succession Certificate',false),
(gen_random_uuid(),'AGRICULTURAL','FAMILY_SETTLEMENT_DOCUMENT','Family Settlement Document',false);
