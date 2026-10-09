ALTER TABLE services
    ADD COLUMN IF NOT EXISTS requires_visit BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS visit_mandatory BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS max_visits_allowed INTEGER NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS requires_gps BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS gps_radius_km NUMERIC(8,3) NOT NULL DEFAULT 0.200,
    ADD COLUMN IF NOT EXISTS requires_before_photo BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS requires_after_photo BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS requires_signature BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS requires_document_upload BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS is_remote BOOLEAN NOT NULL DEFAULT FALSE,
    ADD COLUMN IF NOT EXISTS billable BOOLEAN NOT NULL DEFAULT TRUE;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = to_regclass('services')
          AND conname = 'ck_services_max_visits'
    ) THEN
        ALTER TABLE services
            ADD CONSTRAINT ck_services_max_visits CHECK (max_visits_allowed >= 0);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = to_regclass('services')
          AND conname = 'ck_services_remote_visit'
    ) THEN
        ALTER TABLE services
            ADD CONSTRAINT ck_services_remote_visit
            CHECK (NOT is_remote OR (NOT requires_visit AND NOT visit_mandatory AND NOT requires_gps));
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = to_regclass('services')
          AND conname = 'ck_services_mandatory_visit'
    ) THEN
        ALTER TABLE services
            ADD CONSTRAINT ck_services_mandatory_visit CHECK (NOT visit_mandatory OR requires_visit);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = to_regclass('services')
          AND conname = 'ck_services_gps_visit'
    ) THEN
        ALTER TABLE services
            ADD CONSTRAINT ck_services_gps_visit CHECK (NOT requires_gps OR requires_visit);
    END IF;

    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint
        WHERE conrelid = to_regclass('services')
          AND conname = 'ck_services_gps_radius'
    ) THEN
        ALTER TABLE services
            ADD CONSTRAINT ck_services_gps_radius CHECK (gps_radius_km > 0);
    END IF;
END $$;

COMMENT ON COLUMN services.requires_visit IS 'Whether this service requires at least one completed visit.';
COMMENT ON COLUMN services.visit_mandatory IS 'Legacy-compatible explicit mandatory visit policy.';
COMMENT ON COLUMN services.max_visits_allowed IS 'Maximum scheduled visits; zero means no configured limit.';
COMMENT ON COLUMN services.requires_gps IS 'Whether a validated GPS check is required to complete the service.';
COMMENT ON COLUMN services.gps_radius_km IS 'Maximum GPS distance from the property location allowed for this service.';
COMMENT ON COLUMN services.requires_before_photo IS 'Whether BEFORE_PHOTO evidence is required.';
COMMENT ON COLUMN services.requires_after_photo IS 'Whether AFTER_PHOTO evidence is required.';
COMMENT ON COLUMN services.requires_signature IS 'Whether SIGNATURE evidence is required.';
COMMENT ON COLUMN services.requires_document_upload IS 'Whether DOCUMENT evidence is required.';
COMMENT ON COLUMN services.is_remote IS 'Whether the service is delivered remotely and cannot require visits or GPS.';
COMMENT ON COLUMN services.billable IS 'Whether this catalog service is chargeable, subject to configured discounts.';
