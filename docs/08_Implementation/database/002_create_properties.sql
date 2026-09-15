CREATE TABLE countries (
    country_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_name VARCHAR(100) NOT NULL UNIQUE,
    country_code CHAR(2) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE states (
    state_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    country_id UUID NOT NULL REFERENCES countries(country_id) ON DELETE CASCADE,
    state_name VARCHAR(120) NOT NULL,
    state_code VARCHAR(20),
    UNIQUE (country_id, state_name)
);

CREATE TABLE districts (
    district_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    state_id UUID NOT NULL REFERENCES states(state_id) ON DELETE CASCADE,
    district_name VARCHAR(120) NOT NULL,
    UNIQUE (state_id, district_name)
);

CREATE TABLE localities (
    locality_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    district_id UUID NOT NULL REFERENCES districts(district_id) ON DELETE CASCADE,
    locality_name VARCHAR(120) NOT NULL,
    pincode VARCHAR(20),
    UNIQUE (district_id, locality_name)
);

CREATE TABLE clusters (
    cluster_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cluster_name VARCHAR(120) NOT NULL UNIQUE,
    cluster_type VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'active',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE coverage_zones (
    coverage_zone_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cluster_id UUID NOT NULL REFERENCES clusters(cluster_id) ON DELETE CASCADE,
    zone_name VARCHAR(120) NOT NULL,
    zone_type VARCHAR(50) NOT NULL,
    polygon_data JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (cluster_id, zone_name)
);

CREATE TABLE properties (
    property_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    property_type VARCHAR(100) NOT NULL,
    listing_status VARCHAR(50) NOT NULL DEFAULT 'draft',
    price DECIMAL(15,2),
    status VARCHAR(50) NOT NULL DEFAULT 'available',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE property_locations (
    property_location_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL UNIQUE REFERENCES properties(property_id) ON DELETE CASCADE,
    locality_id UUID REFERENCES localities(locality_id) ON DELETE SET NULL,
    coverage_zone_id UUID REFERENCES coverage_zones(coverage_zone_id) ON DELETE SET NULL,
    address_line_1 VARCHAR(255),
    address_line_2 VARCHAR(255),
    latitude DECIMAL(9,6),
    longitude DECIMAL(9,6),
    location_accuracy_score DECIMAL(5,2),
    map_verification_status VARCHAR(30) NOT NULL DEFAULT 'pending',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE property_documents (
    document_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    property_id UUID NOT NULL REFERENCES properties(property_id) ON DELETE CASCADE,
    document_type VARCHAR(80) NOT NULL,
    file_url TEXT NOT NULL,
    uploaded_by VARCHAR(150),
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
