CREATE TABLE countries (
    country_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), country_name VARCHAR(100) NOT NULL, country_code CHAR(2) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_countries_name UNIQUE (country_name), CONSTRAINT uq_countries_code UNIQUE (country_code),
    CONSTRAINT ck_countries_name CHECK (length(btrim(country_name)) > 0), CONSTRAINT ck_countries_code CHECK (country_code ~ '^[A-Z]{2}$'),
    CONSTRAINT ck_countries_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE states (
    state_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), country_id UUID NOT NULL REFERENCES countries(country_id) ON DELETE RESTRICT,
    state_name VARCHAR(120) NOT NULL, state_code VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_states_country_name UNIQUE (country_id, state_name), CONSTRAINT ck_states_name CHECK (length(btrim(state_name)) > 0),
    CONSTRAINT ck_states_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE districts (
    district_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), state_id UUID NOT NULL REFERENCES states(state_id) ON DELETE RESTRICT,
    district_name VARCHAR(120) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_districts_state_name UNIQUE (state_id, district_name), CONSTRAINT ck_districts_name CHECK (length(btrim(district_name)) > 0),
    CONSTRAINT ck_districts_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE localities (
    locality_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), district_id UUID NOT NULL REFERENCES districts(district_id) ON DELETE RESTRICT,
    locality_name VARCHAR(120) NOT NULL, pincode VARCHAR(20),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_localities_district_name UNIQUE (district_id, locality_name), CONSTRAINT ck_localities_name CHECK (length(btrim(locality_name)) > 0),
    CONSTRAINT ck_localities_pincode CHECK (pincode IS NULL OR pincode ~ '^[A-Za-z0-9 -]{3,20}$'),
    CONSTRAINT ck_localities_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE clusters (
    cluster_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), cluster_name VARCHAR(120) NOT NULL, cluster_type VARCHAR(50) NOT NULL,
    status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_clusters_name UNIQUE (cluster_name), CONSTRAINT ck_clusters_name CHECK (length(btrim(cluster_name)) > 0),
    CONSTRAINT ck_clusters_type CHECK (length(btrim(cluster_type)) > 0), CONSTRAINT ck_clusters_status CHECK (status IN ('ACTIVE','INACTIVE')),
    CONSTRAINT ck_clusters_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE coverage_zones (
    coverage_zone_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), cluster_id UUID NOT NULL REFERENCES clusters(cluster_id) ON DELETE RESTRICT,
    zone_name VARCHAR(120) NOT NULL, zone_type VARCHAR(50) NOT NULL, polygon_data JSONB, status VARCHAR(30) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT uq_coverage_zones_cluster_name UNIQUE (cluster_id, zone_name), CONSTRAINT ck_coverage_zones_name CHECK (length(btrim(zone_name)) > 0),
    CONSTRAINT ck_coverage_zones_type CHECK (length(btrim(zone_type)) > 0), CONSTRAINT ck_coverage_zones_polygon CHECK (polygon_data IS NULL OR jsonb_typeof(polygon_data) = 'object'),
    CONSTRAINT ck_coverage_zones_status CHECK (status IN ('ACTIVE','INACTIVE')), CONSTRAINT ck_coverage_zones_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE properties (
    property_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), customer_id UUID NOT NULL REFERENCES customers(customer_id) ON DELETE RESTRICT,
    title VARCHAR(255) NOT NULL, property_type VARCHAR(100) NOT NULL, listing_status VARCHAR(50) NOT NULL DEFAULT 'DRAFT',
    price NUMERIC(15,2), status VARCHAR(50) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_properties_title CHECK (length(btrim(title)) > 0), CONSTRAINT ck_properties_type CHECK (length(btrim(property_type)) > 0),
    CONSTRAINT ck_properties_listing_status CHECK (listing_status IN ('DRAFT','PUBLISHED','UNPUBLISHED','ARCHIVED')),
    CONSTRAINT ck_properties_status CHECK (status IN ('AVAILABLE','OCCUPIED','UNDER_MAINTENANCE','INACTIVE','ARCHIVED')),
    CONSTRAINT ck_properties_price CHECK (price IS NULL OR price >= 0), CONSTRAINT ck_properties_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE property_locations (
    property_location_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), property_id UUID NOT NULL UNIQUE REFERENCES properties(property_id) ON DELETE CASCADE,
    locality_id UUID REFERENCES localities(locality_id) ON DELETE SET NULL, coverage_zone_id UUID REFERENCES coverage_zones(coverage_zone_id) ON DELETE SET NULL,
    address_line_1 VARCHAR(255) NOT NULL, address_line_2 VARCHAR(255), latitude NUMERIC(9,6), longitude NUMERIC(9,6),
    location_accuracy_score NUMERIC(5,2), map_verification_status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_property_locations_address CHECK (length(btrim(address_line_1)) > 0),
    CONSTRAINT ck_property_locations_latitude CHECK (latitude IS NULL OR latitude BETWEEN -90 AND 90),
    CONSTRAINT ck_property_locations_longitude CHECK (longitude IS NULL OR longitude BETWEEN -180 AND 180),
    CONSTRAINT ck_property_locations_coordinates CHECK ((latitude IS NULL) = (longitude IS NULL)),
    CONSTRAINT ck_property_locations_accuracy CHECK (location_accuracy_score IS NULL OR location_accuracy_score BETWEEN 0 AND 100),
    CONSTRAINT ck_property_locations_status CHECK (map_verification_status IN ('PENDING','VERIFIED','REJECTED')),
    CONSTRAINT ck_property_locations_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE TABLE property_documents (
    document_id UUID PRIMARY KEY DEFAULT gen_random_uuid(), property_id UUID NOT NULL REFERENCES properties(property_id) ON DELETE CASCADE,
    document_type VARCHAR(80) NOT NULL, file_url TEXT NOT NULL, media_type VARCHAR(100), uploaded_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP, updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL, updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0,
    CONSTRAINT ck_property_documents_type CHECK (length(btrim(document_type)) > 0), CONSTRAINT ck_property_documents_url CHECK (length(btrim(file_url)) > 0),
    CONSTRAINT ck_property_documents_audit CHECK (updated_at >= created_at AND version >= 0)
);

CREATE INDEX idx_states_country ON states(country_id); CREATE INDEX idx_districts_state ON districts(state_id); CREATE INDEX idx_localities_district ON localities(district_id);
CREATE INDEX idx_localities_pincode ON localities(pincode) WHERE pincode IS NOT NULL; CREATE INDEX idx_clusters_status ON clusters(status);
CREATE INDEX idx_coverage_zones_cluster_status ON coverage_zones(cluster_id,status); CREATE INDEX idx_properties_customer_status ON properties(customer_id,status);
CREATE INDEX idx_properties_listing_status ON properties(listing_status); CREATE INDEX idx_property_locations_locality ON property_locations(locality_id) WHERE locality_id IS NOT NULL;
CREATE INDEX idx_property_locations_zone ON property_locations(coverage_zone_id) WHERE coverage_zone_id IS NOT NULL; CREATE INDEX idx_property_documents_property_type ON property_documents(property_id,document_type);

CREATE TRIGGER trg_countries_audit BEFORE UPDATE ON countries FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_states_audit BEFORE UPDATE ON states FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_districts_audit BEFORE UPDATE ON districts FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_localities_audit BEFORE UPDATE ON localities FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_clusters_audit BEFORE UPDATE ON clusters FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_coverage_zones_audit BEFORE UPDATE ON coverage_zones FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_properties_audit BEFORE UPDATE ON properties FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_property_locations_audit BEFORE UPDATE ON property_locations FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
CREATE TRIGGER trg_property_documents_audit BEFORE UPDATE ON property_documents FOR EACH ROW EXECUTE FUNCTION set_audit_columns();

COMMENT ON TABLE countries IS 'ISO-style countries used by the PropertyPilot geography hierarchy.';
COMMENT ON TABLE states IS 'States or first-level administrative areas within a country.';
COMMENT ON TABLE districts IS 'Districts within a state.';
COMMENT ON TABLE localities IS 'Serviceable localities within a district.';
COMMENT ON TABLE clusters IS 'Operational groupings used for coverage and workforce allocation.';
COMMENT ON TABLE coverage_zones IS 'Service coverage zones belonging to an operational cluster.';
COMMENT ON TABLE properties IS 'Customer-owned or managed properties registered with PropertyPilot.';
COMMENT ON TABLE property_locations IS 'Canonical physical and geospatial location of a property.';
COMMENT ON TABLE property_documents IS 'Metadata for documents stored outside PostgreSQL and associated with a property.';

COMMENT ON COLUMN countries.country_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN countries.country_name IS 'Unique country name.'; COMMENT ON COLUMN countries.country_code IS 'Uppercase ISO 3166-1 alpha-2 country code.';
COMMENT ON COLUMN states.state_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN states.country_id IS 'Parent country.'; COMMENT ON COLUMN states.state_name IS 'State name unique within the country.'; COMMENT ON COLUMN states.state_code IS 'Optional administrative code.';
COMMENT ON COLUMN districts.district_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN districts.state_id IS 'Parent state.'; COMMENT ON COLUMN districts.district_name IS 'District name unique within the state.';
COMMENT ON COLUMN localities.locality_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN localities.district_id IS 'Parent district.'; COMMENT ON COLUMN localities.locality_name IS 'Locality name unique within the district.'; COMMENT ON COLUMN localities.pincode IS 'Postal code when known.';
COMMENT ON COLUMN clusters.cluster_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN clusters.cluster_name IS 'Unique operational cluster name.'; COMMENT ON COLUMN clusters.cluster_type IS 'Business-defined cluster classification.'; COMMENT ON COLUMN clusters.status IS 'Cluster lifecycle state.';
COMMENT ON COLUMN coverage_zones.coverage_zone_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN coverage_zones.cluster_id IS 'Owning operational cluster.'; COMMENT ON COLUMN coverage_zones.zone_name IS 'Zone name unique within its cluster.'; COMMENT ON COLUMN coverage_zones.zone_type IS 'Business-defined coverage-zone type.'; COMMENT ON COLUMN coverage_zones.polygon_data IS 'GeoJSON-compatible boundary object.'; COMMENT ON COLUMN coverage_zones.status IS 'Coverage-zone lifecycle state.';
COMMENT ON COLUMN properties.property_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN properties.customer_id IS 'Customer that owns or manages the property.'; COMMENT ON COLUMN properties.title IS 'Human-readable property title.'; COMMENT ON COLUMN properties.property_type IS 'Business-defined property classification.'; COMMENT ON COLUMN properties.listing_status IS 'Publication lifecycle state.'; COMMENT ON COLUMN properties.price IS 'Optional property price or declared value.'; COMMENT ON COLUMN properties.status IS 'Current operational availability state.';
COMMENT ON COLUMN property_locations.property_location_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN property_locations.property_id IS 'Property having this one-to-one location.'; COMMENT ON COLUMN property_locations.locality_id IS 'Structured locality when known.'; COMMENT ON COLUMN property_locations.coverage_zone_id IS 'Resolved service coverage zone.'; COMMENT ON COLUMN property_locations.address_line_1 IS 'Primary address line.'; COMMENT ON COLUMN property_locations.address_line_2 IS 'Optional secondary address line.'; COMMENT ON COLUMN property_locations.latitude IS 'Latitude in decimal degrees.'; COMMENT ON COLUMN property_locations.longitude IS 'Longitude in decimal degrees.'; COMMENT ON COLUMN property_locations.location_accuracy_score IS 'Location confidence percentage.'; COMMENT ON COLUMN property_locations.map_verification_status IS 'Map verification lifecycle state.';
COMMENT ON COLUMN property_documents.document_id IS 'System-generated UUID primary key.'; COMMENT ON COLUMN property_documents.property_id IS 'Property owning the document.'; COMMENT ON COLUMN property_documents.document_type IS 'Business-defined document classification.'; COMMENT ON COLUMN property_documents.file_url IS 'Protected object-storage reference.'; COMMENT ON COLUMN property_documents.media_type IS 'Optional MIME media type.'; COMMENT ON COLUMN property_documents.uploaded_at IS 'Timestamp when the document was uploaded.';

COMMENT ON COLUMN countries.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN countries.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN countries.created_by IS 'Creating user.'; COMMENT ON COLUMN countries.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN countries.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN states.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN states.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN states.created_by IS 'Creating user.'; COMMENT ON COLUMN states.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN states.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN districts.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN districts.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN districts.created_by IS 'Creating user.'; COMMENT ON COLUMN districts.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN districts.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN localities.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN localities.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN localities.created_by IS 'Creating user.'; COMMENT ON COLUMN localities.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN localities.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN clusters.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN clusters.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN clusters.created_by IS 'Creating user.'; COMMENT ON COLUMN clusters.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN clusters.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN coverage_zones.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN coverage_zones.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN coverage_zones.created_by IS 'Creating user.'; COMMENT ON COLUMN coverage_zones.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN coverage_zones.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN properties.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN properties.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN properties.created_by IS 'Creating user.'; COMMENT ON COLUMN properties.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN properties.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN property_locations.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN property_locations.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN property_locations.created_by IS 'Creating user.'; COMMENT ON COLUMN property_locations.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN property_locations.version IS 'Optimistic-lock version.';
COMMENT ON COLUMN property_documents.created_at IS 'Creation timestamp.'; COMMENT ON COLUMN property_documents.updated_at IS 'Last-update timestamp.'; COMMENT ON COLUMN property_documents.created_by IS 'Creating user.'; COMMENT ON COLUMN property_documents.updated_by IS 'Last-updating user.'; COMMENT ON COLUMN property_documents.version IS 'Optimistic-lock version.';
