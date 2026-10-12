ALTER TABLE property_locations
    ADD COLUMN locality_name VARCHAR(120),
    ADD COLUMN city VARCHAR(120),
    ADD COLUMN district VARCHAR(120),
    ADD COLUMN state VARCHAR(120),
    ADD COLUMN postal_code VARCHAR(20),
    ADD COLUMN country_code CHAR(2) NOT NULL DEFAULT 'IN';

ALTER TABLE property_locations
    ADD CONSTRAINT ck_property_locations_country_code
        CHECK (country_code ~ '^[A-Z]{2}$'),
    ADD CONSTRAINT ck_property_locations_postal_code
        CHECK (postal_code IS NULL OR length(btrim(postal_code)) > 0);

COMMENT ON COLUMN property_locations.locality_name IS 'Free-text locality captured until the address is linked to the canonical locality master.';
COMMENT ON COLUMN property_locations.city IS 'City or town in the property service address.';
COMMENT ON COLUMN property_locations.district IS 'District in the property service address.';
COMMENT ON COLUMN property_locations.state IS 'State or province in the property service address.';
COMMENT ON COLUMN property_locations.postal_code IS 'Postal or PIN code in the property service address.';
COMMENT ON COLUMN property_locations.country_code IS 'ISO 3166-1 alpha-2 country code for the property service address.';
