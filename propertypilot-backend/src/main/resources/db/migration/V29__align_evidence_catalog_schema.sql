CREATE TABLE IF NOT EXISTS evidence_types (
    evidence_type_id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    category VARCHAR(80) NOT NULL DEFAULT 'SERVICE',
    description TEXT,
    active_flag BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    updated_by UUID REFERENCES users(user_id) ON DELETE SET NULL,
    version BIGINT NOT NULL DEFAULT 0
);

ALTER TABLE evidence_types
    ALTER COLUMN evidence_type_id SET DEFAULT gen_random_uuid();

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('evidence_types') AND conname = 'ck_evidence_types_code') THEN
        ALTER TABLE evidence_types ADD CONSTRAINT ck_evidence_types_code CHECK (length(btrim(code)) > 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('evidence_types') AND conname = 'ck_evidence_types_name') THEN
        ALTER TABLE evidence_types ADD CONSTRAINT ck_evidence_types_name CHECK (length(btrim(name)) > 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('evidence_types') AND conname = 'ck_evidence_types_category') THEN
        ALTER TABLE evidence_types ADD CONSTRAINT ck_evidence_types_category CHECK (length(btrim(category)) > 0);
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('evidence_types') AND conname = 'ck_evidence_types_audit') THEN
        ALTER TABLE evidence_types ADD CONSTRAINT ck_evidence_types_audit CHECK (updated_at >= created_at AND version >= 0);
    END IF;
    IF NOT EXISTS (
        SELECT 1 FROM pg_trigger
        WHERE tgrelid = to_regclass('evidence_types')
          AND tgname = 'trg_evidence_types_audit'
          AND NOT tgisinternal
    ) THEN
        CREATE TRIGGER trg_evidence_types_audit
            BEFORE UPDATE ON evidence_types
            FOR EACH ROW EXECUTE FUNCTION set_audit_columns();
    END IF;
END $$;

INSERT INTO evidence_types (code, name, category)
SELECT normalized.type_code,
       initcap(replace(lower(normalized.type_code), '_', ' ')),
       'SERVICE'
FROM (
    SELECT DISTINCT upper(btrim(evidence_type)) AS type_code
    FROM evidence
    WHERE evidence_type IS NOT NULL AND length(btrim(evidence_type)) > 0
) normalized
ON CONFLICT (code) DO NOTHING;

INSERT INTO evidence_types (code, name, category)
VALUES ('BEFORE_PHOTO', 'Before Photo', 'SERVICE'),
       ('AFTER_PHOTO', 'After Photo', 'SERVICE'),
       ('VIDEO', 'Video', 'SERVICE'),
       ('GPS', 'GPS Verification', 'SERVICE'),
       ('DOCUMENT', 'Document', 'SERVICE'),
       ('SIGNATURE', 'Signature', 'SERVICE')
ON CONFLICT (code) DO NOTHING;

ALTER TABLE evidence
    ADD COLUMN IF NOT EXISTS evidence_type_id UUID,
    ADD COLUMN IF NOT EXISTS service_request_id UUID,
    ADD COLUMN IF NOT EXISTS file_name VARCHAR(255),
    ADD COLUMN IF NOT EXISTS remarks TEXT,
    ADD COLUMN IF NOT EXISTS latitude NUMERIC(9,6),
    ADD COLUMN IF NOT EXISTS longitude NUMERIC(9,6);

UPDATE evidence e
SET evidence_type_id = t.evidence_type_id
FROM evidence_types t
WHERE e.evidence_type_id IS NULL
  AND t.code = upper(btrim(e.evidence_type));

UPDATE evidence e
SET service_request_id = v.service_request_id
FROM visits v
WHERE e.service_request_id IS NULL
  AND e.visit_id = v.visit_id;

ALTER TABLE evidence
    ALTER COLUMN evidence_type_id SET NOT NULL,
    ALTER COLUMN service_request_id SET NOT NULL,
    ALTER COLUMN visit_id DROP NOT NULL;

DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('evidence') AND conname = 'fk_evidence_type') THEN
        ALTER TABLE evidence ADD CONSTRAINT fk_evidence_type FOREIGN KEY (evidence_type_id)
            REFERENCES evidence_types(evidence_type_id) ON DELETE RESTRICT;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('evidence') AND conname = 'fk_evidence_service_request') THEN
        ALTER TABLE evidence ADD CONSTRAINT fk_evidence_service_request FOREIGN KEY (service_request_id)
            REFERENCES service_requests(service_request_id) ON DELETE CASCADE;
    END IF;
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conrelid = to_regclass('evidence') AND conname = 'ck_evidence_request_or_visit') THEN
        ALTER TABLE evidence ADD CONSTRAINT ck_evidence_request_or_visit
            CHECK (service_request_id IS NOT NULL OR visit_id IS NOT NULL);
    END IF;
END $$;

ALTER TABLE evidence DROP CONSTRAINT IF EXISTS ck_evidence_type;
DROP INDEX IF EXISTS idx_evidence_type;

CREATE INDEX IF NOT EXISTS idx_evidence_service_request
    ON evidence(service_request_id) WHERE service_request_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_evidence_type_id ON evidence(evidence_type_id);

ALTER TABLE evidence DROP COLUMN IF EXISTS evidence_type;

COMMENT ON TABLE evidence_types IS 'Reusable evidence classifications selected by service completion rules.';
COMMENT ON COLUMN evidence.service_request_id IS 'Service request documented by the evidence.';
